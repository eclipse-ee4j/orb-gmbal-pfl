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
import org.glassfish.pfl.tf.timer.spi.StatisticsAccumulator;
import org.glassfish.pfl.tf.timer.spi.Timer;
import org.glassfish.pfl.tf.timer.spi.TimerEvent;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;

public class MultiThreadedStatsEventHandlerImpl extends StatsEventHandlerBase {
    private Object saListLock ;
    
    // ArrayList indexed by Timer.id 
    private ThreadLocal<ArrayList<Stack<TimerEvent>>> tlsteList ; 

    MultiThreadedStatsEventHandlerImpl( TimerFactory factory, String name ) {
	super( factory, name ) ;
	final int size = factory.numberOfIds() ;

	saListLock = new Object() ;
	tlsteList = new ThreadLocal<ArrayList<Stack<TimerEvent>>>() {
	    public ArrayList<Stack<TimerEvent>> initialValue() {
		ArrayList<Stack<TimerEvent>> result = new ArrayList<Stack<TimerEvent>>( size ) ;
		for (int ctr=0; ctr<size; ctr++) {
		    result.add( new Stack<TimerEvent>() ) ;
		}
		return result ;
	    }
	} ;
    }

    private Stack<TimerEvent> getSteElement( int id ) {
	ArrayList<Stack<TimerEvent>> ste = tlsteList.get() ;
	ste.ensureCapacity( id + 1 ) ;
	for (int ctr=ste.size(); ctr<=id; ctr++)
	    ste.add( new Stack<TimerEvent>() ) ;
	return ste.get( id ) ;
    }

    public void clear() {
	synchronized (saListLock) {
	    super.clear() ;
	}
    }

    protected void recordDuration( int id, long duration ) {
	synchronized (saListLock) {
	    StatisticsAccumulator acc = saList.get( id ) ;
	    acc.sample( duration ) ;
	}
    }

    public void notify( TimerEvent event ) {
	Timer timer = event.timer() ;
	int id = timer.id() ;
	notify( getSteElement( id ), event ) ;
    }
}

