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
import java.util.Stack ;

import java.io.PrintStream ;
import org.glassfish.pfl.tf.timer.spi.LogEventHandler;
import org.glassfish.pfl.tf.timer.spi.NamedBase;
import org.glassfish.pfl.tf.timer.spi.TimerEvent;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;

// XXX This needs to be able to properly handle multiple reporting threads!
public class LogEventHandlerImpl extends NamedBase
    implements LogEventHandler {
    // Default number of entries in data
    private static final int DEFAULT_SIZE = 1000 ;

    // Default increment to number of entries in data
    private static final int DEFAULT_INCREMENT = 1000 ;

    // This is an array for speed.  All data is interleaved here:
    // data[2n] is the id, data[2n+1] is the timestamp for all n >= 0.
    // The array will be resized as needed.
    // id is actually 2*id for enter, 2*id+1 for exit.
    private long[] data ;

    private int size ;
    private int increment ;
    
    // Index of the next free slot in data 
    private int nextFree ;

    LogEventHandlerImpl( TimerFactory factory, String name ) {
	super( factory, name ) ;
	initData( DEFAULT_SIZE, DEFAULT_INCREMENT ) ;
    }

    public synchronized Iterator<TimerEvent> iterator() {
	return new LogEventHandlerIterator( factory(), data, nextFree ) ;
    }

    private void initData( int size, int increment ) {
        this.size = 2*size ;
        this.increment = 2*increment ;
	data = new long[ this.size ] ;
	nextFree = 0 ;
    }

    public void notify( TimerEvent event ) {
	final int id = 2*event.timer().id() + 
	    ((event.type() == TimerEvent.TimerEventType.ENTER) ? 0 : 1) ;
	log( id, event.time() ) ;
    }

    // XXX ignore old compensation idea; do we need it here?
    private synchronized void log( int id, long time ) {
        if (data.length - nextFree < 2) {
            // grow the array
	    int newSize = data.length + 2*increment ;
	    long[] newData = new long[ newSize ] ;
	    System.arraycopy( data, 0, newData, 0, data.length ) ;
	    data = newData ;
	}

        int index = nextFree ;
        nextFree += 2 ;
        
	data[ index ] = id ;
        data[ index + 1 ] = time ;
    }

    public synchronized void clear() {
	initData( size, increment ) ;
    }

    // Class used to maintain a variable-length indent.
    // Useful for displaying hierarchies.
    private static class Indent {
	private final int width ;
	private int level ;
	private String rep ;

	public Indent( final int width ) {
	    this.width = width ;
	    level = 0 ;
	    rep = "" ;
	}

	private void update() {
	    int size = level*width ;
	    char[] content = new char[size] ;
	    for (int ctr=0; ctr<size; ctr++) {
		content[ctr] = ' ' ;
	    }
	    rep = new String( content ) ;
	}

	public void in() {
	    level++ ;
	    update() ;
	}

	public void out() {
	    level-- ;
	    update() ;
	}

	public String toString() {
	    return rep ;
	}
    }

    private static final String ENTER_REP = ">> " ;
    private static final String EXIT_REP = "<< " ;

    public void display( PrintStream arg, String msg ) {
        arg.println( "Displaying contents of " + this + ": " + msg ) ;
	final Stack<TimerEvent> stack = new Stack<TimerEvent>() ;
	long startTime = -1 ;
	Indent indent = new Indent( ENTER_REP.length() ) ;
	for (TimerEvent te : this) {
	    if (startTime == -1) {
		startTime = te.time() ;
	    }

	    long relativeTime = (te.time() - startTime)/1000 ;

	    final boolean isEnter = te.type() == TimerEvent.TimerEventType.ENTER ;

	    if (isEnter) {
		arg.printf( "%8d: %s%s%s\n", relativeTime, indent, 
		    isEnter ? ENTER_REP : EXIT_REP,  te.timer().name() ) ;

		// Copy te, otherwise the iterator will overwrite it!
		stack.push( new TimerEvent(te) ) ;
		indent.in() ;
	    } else {
		TimerEvent enterEvent = stack.pop() ;
		indent.out() ;

		String duration = null ;
		if (enterEvent.timer().equals( te.timer() )) {
		    duration = Long.toString( (te.time()-enterEvent.time())/1000 ) ;
		} else {
		    duration = "BAD NESTED EVENT: ENTER was " + enterEvent.timer().name() ;
		}

		arg.printf( "%8d: %s%s%s[%s]\n", relativeTime, indent, 
		    isEnter ? ENTER_REP : EXIT_REP,  te.timer().name(), duration ) ;
	    }
	}
    }
}
