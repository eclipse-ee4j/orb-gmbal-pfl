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

import java.util.Collections ;
import java.util.Set ;
import java.util.HashSet ;

import org.glassfish.pfl.tf.timer.impl.TimerFactoryImpl ;

/** Supports registration of TimerEventHandlers.  A subclass of this class
 * must also provide some mechanism to create and propagate TimerEvents,
 * which may be subclasses of TimerEvent if needed.  A subclass typically
 * provides methods to indicate when enter and exit.  If additional data
 * is stored in the event, customer enter/exit methods can pass the
 * extra data to the extended event.
 */
public abstract class TimerEventControllerBase extends NamedBase {
    // XXX We will need to explore the efficiency and synchronization
    // here.  Should we use read/write locks around handlers?
    // 
    private Set<TimerEventHandler> handlers ;
    private Set<TimerEventHandler> roHandlers ;

    public TimerEventControllerBase( TimerFactory factory, String name ) {
	super( factory, name ) ;
	handlers = new HashSet<TimerEventHandler>() ;
	roHandlers = Collections.unmodifiableSet( handlers ) ;
	TimerFactoryImpl tfi = TimerFactoryImpl.class.cast( factory ) ;
	tfi.saveTimerEventController( this ) ;
    }

    /** Register the handler to start receiving events from this
     * controller.
     */
    public void register( TimerEventHandler handler ) {
	handlers.add( handler ) ;
    }	

    /** Deregister the handler to stop receiving events from this
     * controller.
     */
    public void deregister( TimerEventHandler handler ) {
	handlers.remove( handler ) ;
    }

    /** Read-only image of the set of Handlers.
     */
    public Set<TimerEventHandler> handlers() {
	return roHandlers ;
    }

    /** Send the event to all registered handlers.
     */
    protected void propagate( TimerEvent ev ) {
	for (TimerEventHandler handler : handlers) {
	    handler.notify( ev ) ;
	}
    }
}

