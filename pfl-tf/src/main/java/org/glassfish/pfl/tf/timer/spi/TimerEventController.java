/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi ;

/** Creates timer events and sends them to all registered event
 * handlers.  Multiple controllers may be created from the
 * same TimerFactory.
 */
public class TimerEventController extends TimerEventControllerBase {

    public TimerEventController( TimerFactory factory, String name ) {
	super( factory, name ) ;
    }

    /** Generate a TimerEvent representing the entry to a 
     * particular timer.  This event is sent to all registered
     * TimerEventHandlers.  An event is only generated in
     * case timer.isActivated() is true.
     */
    public void enter( Timer timer ) {
	handle( timer, TimerEvent.TimerEventType.ENTER ) ;
    }

    /** Generate a TimerEvent representing the exit from a 
     * particular timer.  This event is sent to all registered
     * TimerEventHandlers.  An event is only generated in
     * case timer.isActivated() is true.
     */
    public void exit( Timer timer ) {
	handle( timer, TimerEvent.TimerEventType.EXIT ) ;
    }
   
    private void handle( Timer timer, TimerEvent.TimerEventType type ) {
	if (timer.isActivated()) {
	    TimerEvent te = new TimerEvent( timer, type ) ;
	    propagate( te ) ;
	}
    }
}
