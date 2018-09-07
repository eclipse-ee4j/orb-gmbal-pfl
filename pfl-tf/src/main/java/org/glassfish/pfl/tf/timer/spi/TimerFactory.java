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

import java.util.Map ;
import java.util.Set ;

/** Factory class for all Timer-related objects.
 * TimerFactory is also a TimerGroup of all timers and timer groups that it creates.
 */
public interface TimerFactory extends TimerGroup {
    /** Returns the maximum id used by this TimerFactory for creating Controllables.
     * The value of con.id() for any Controllable created by this
     * TimerFactory always ranges from 0 inclusive to numberOfIds()
     * exclusive.
     */
    int numberOfIds() ;

    /** Returns the Controllable corresponding to id, for 
     * id in the range 0 (inclusive) to numberOfIds() (exclusive). 
     * @throws IndexOutOfBoundsException if id is not in range.
     */
    Controllable getControllable( int id ) ;

    /** Create a new LogEventHandler.  All LogEventHandler names
     * must be unique within the same TimerFactory.
     */
    LogEventHandler makeLogEventHandler( String name ) ;

    TimerEventHandler makeTracingEventHandler( String name ) ;

    /** Create a new StatsEventHandler.  A StatsEventHandler records 
     * running statistics for all enter/exit pairs until it is cleared,
     * at which point it starts over.  It will keep data separated for
     * each thread, combining information correctly from multiple threads.
     * All StatsEventHandler names
     * must be unique within the same TimerFactory.
     * This StatsEventHandler must be used from a single thread.
     */
    StatsEventHandler makeStatsEventHandler( String name ) ;

    /** Create a new StatsEventHandler.  A StatsEventHandler records 
     * running statistics for all enter/exit pairs until it is cleared,
     * at which point it starts over.  It will keep data separated for
     * each thread, combining information correctly from multiple threads.
     * All StatsEventHandler names
     * must be unique within the same TimerFactory.
     * This StatsEventHandler is multi-thread safe.
     */
    StatsEventHandler makeMultiThreadedStatsEventHandler( String name ) ;

    /** Remove the handler from this TimerFactory.  The handler
     * should not be used after this call.
     */
    void removeTimerEventHandler( TimerEventHandler handler ) ;

    /** Create a new Timer.  Note that Timers cannot be
     * destroyed, other than by garbage collecting the TimerFactory
     * that created them.
     */
    Timer makeTimer( String name, String description )  ;

    /** Returns a read-only map from Timer names to Timers.
     */
    Map<String,? extends Timer> timers() ;

    /** Create a new TimerGroup.  Note that TimerGroups cannot be
     * destroyed, other than by garbage collecting the TimerFactory
     * that created them.
     */
    TimerGroup makeTimerGroup( String name, String description ) ;

    /** Returns a read-only map from TimerGroup names to TimerGroups.
     */
    Map<String,? extends TimerGroup> timerGroups() ;

    /** Create a TimerController, which can create TimerEvents and
     * send them to registered TimerEventHandlers.
     */
    TimerEventController makeController( String name ) ;

    /** Remove the controller from this factory.  The controller 
     * should not be used after this call.
     */
    void removeController( TimerEventControllerBase controller ) ;

    /** Returns a read-only view of the set of enabled Controllables.
     * These have been explicitly enabled via a call to enable().
     */
    Set<? extends Controllable> enabledSet() ;

    /** Returns a read-only view of the set of Controllables that are 
     * currently active.  An enabled Timer is active.  All Controllables
     * contained in an active or enabled TimerGroup are active.
     */
    Set<Timer> activeSet() ;

    /** Return true iff a timer with the given name already exists.
     */
    boolean timerAlreadyExists( String name ) ;
}

