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

import java.util.Iterator ;
import java.util.NoSuchElementException ;
import org.glassfish.pfl.tf.timer.spi.Controllable;
import org.glassfish.pfl.tf.timer.spi.Timer;
import org.glassfish.pfl.tf.timer.spi.TimerEvent;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;

public class LogEventHandlerIterator implements Iterator<TimerEvent> {
    private int current = 0 ;
    private TimerEvent entry = null ;

    private final TimerFactory factory ;
    private final long[] data ;
    private final int nextFree ;

    public LogEventHandlerIterator( TimerFactory factory, long[] data, 
	int nextFree ) {

	this.factory = factory ;
	this.data = data.clone() ;
	this.nextFree = nextFree ;
    }

    public void remove() {
	throw new UnsupportedOperationException() ;
    }

    public boolean hasNext() {
	return current < nextFree ;
    }

    public TimerEvent next() {
	if (hasNext()) {
	    long elem = data[current] ;

	    TimerEvent.TimerEventType etype = 
		((elem & 1) == 1) ? 
		    TimerEvent.TimerEventType.EXIT :
		    TimerEvent.TimerEventType.ENTER ;

	    int id = (int)(elem >> 1) ;

	    Controllable con = factory.getControllable( id ) ;
	    if (!(con instanceof Timer))
		throw new IllegalStateException( "Controllable id must be Timer" ) ;
	    Timer timer = Timer.class.cast( con ) ; 

	    if (entry == null)
		entry = new TimerEvent( timer, etype, data[current+1] ) ;
	    else 
		entry.update( timer, etype, data[current+1] ) ;

	    current += 2 ;
	    
	    return entry ;
	} else {
	    throw new NoSuchElementException() ;
	}
    }
}
