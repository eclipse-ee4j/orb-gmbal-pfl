/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.impl ;

import java.util.ArrayList ;
import java.util.Stack ;
import java.util.Map ;
import java.util.HashMap ;
import org.glassfish.pfl.tf.timer.spi.Controllable;
import org.glassfish.pfl.tf.timer.spi.NamedBase;
import org.glassfish.pfl.tf.timer.spi.Statistics;
import org.glassfish.pfl.tf.timer.spi.StatisticsAccumulator;
import org.glassfish.pfl.tf.timer.spi.StatsEventHandler;
import org.glassfish.pfl.tf.timer.spi.Timer;
import org.glassfish.pfl.tf.timer.spi.TimerEvent;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;

public abstract class StatsEventHandlerBase extends NamedBase
    implements StatsEventHandler {
    protected static final String UNITS = "nanoseconds" ;
    
    // indexed by Timer.id()
    protected ArrayList<StatisticsAccumulator> saList ; 
    
    protected StatsEventHandlerBase( TimerFactory factory, String name ) {
	super( factory, name ) ;

	// Note that this implies that no timers or timergroups are created
	// after the StatsEventHandler is created.  We should probably fix this.
	int size = factory.numberOfIds() ;
	saList = new ArrayList<StatisticsAccumulator>( size ) ;
	for (int ctr=0; ctr<size; ctr++) {
	    saList.add( new StatisticsAccumulator(UNITS) ) ;
	}
    } 

    public void clear() {
	for (StatisticsAccumulator sa : saList) 
	    sa.clearState() ;
    }

    // Override this as required to record a duraction for an enter/exit
    // pair.  Called from notify().
    protected abstract void recordDuration( int id, long duration ) ;

    protected final void notify( Stack<TimerEvent> teStack, TimerEvent event ) {
	Timer timer = event.timer() ;
	int id = timer.id() ;

	if (event.type() == TimerEvent.TimerEventType.ENTER) {
	    // push this event onto the Timer stack
	    teStack.push( event ) ;
	} else {
	    // pop off the ENTER event, record duration
	    if (teStack.empty()) {
		throw new IllegalStateException( 
		    "Unexpected empty stack for EXIT event on timer " + timer ) ;
	    } else {
		TimerEvent enter = teStack.pop() ;
		if (!timer.equals( enter.timer() ))
		    throw new IllegalStateException(
			"Expected timer " + timer + " but found timer "
			    + enter.timer() + " on the TimerEvent stack" ) ;

		long duration = event.time() - enter.time() ;

		// Remove the contribution of nested calls from
		// the time for all outer calls.
		for (TimerEvent ev : teStack) {
		    ev.incrementTime( duration ) ;
		}

		recordDuration( id, duration ) ;
	    }
	}
    }

    public Map<Timer,Statistics> stats() {
	Map<Timer,Statistics> result = new HashMap<Timer,Statistics>() ;
	for (int ctr=0; ctr<saList.size(); ctr++) {
	    Controllable con = factory().getControllable( ctr ) ;

	    // ignore IDs of TimerGroups	
	    if (con instanceof Timer) {
		Timer timer = Timer.class.cast( con ) ;
		StatisticsAccumulator sa = saList.get(ctr) ; 
		result.put( timer, sa.getStats() ) ;
	    }
	}

	return result ;
    }
}
