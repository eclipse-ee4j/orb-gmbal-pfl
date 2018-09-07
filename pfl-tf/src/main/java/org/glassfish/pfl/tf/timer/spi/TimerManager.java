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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.pfl.tf.spi.MethodMonitorRegistry;

/** Provides access to timer facilities.
 * This is intended to make it easy to set up timing,
 * either for performance tests, or for adaptive policy management.
 * Note that the constructor and the initialize method must be called
 * from the same thread in order to safely complete the initialization
 * of an instance of this class.  After that, multiple threads may
 * access this class for the factory(), points(), and controller() 
 * methods.
 */
public class TimerManager<T> {
    private final TimerFactory tf ;
    private final TimerEventController controller ;

    private volatile T tp ;
    private volatile boolean isInitialized = false ;

    private final Map<Class<?>,List<Timer>> classToTimers =
        new HashMap<Class<?>,List<Timer>>() ;

    /** Create a new TimerManager, with a TimerFactory registered under the given name
     * in the TimerFactoryBuilder, and a TimerEventController with the same name.
     */
    public TimerManager( ObjectRegistrationManager orm, String name ) {
	tf = TimerFactoryBuilder.make( orm, name, name ) ;
	controller = tf.makeController( name ) ;
    }

    public TimerManager( String name ) {
        this( null, name ) ;
    }

    /** Destroy this TimerManager by removing its TimerFactory from the
     * TimerFactoryBuilder.
     */
    public void destroy() {
	TimerFactoryBuilder.destroy( tf ) ;
    }

    // Implementation notes:
    // Performance is crucial here, especially for access to the controller.
    // In particular, enter/exit on a timer must NOT by default acquire any
    // global locks, as this would be likely to result in hot spots for
    // contention.
    //
    // The easiest way (ignoring volatile and AtomicXXX types) to achieve this
    // is simply to make controller() read only, since there is seldom a reason
    // to change the default controller.
    private void checkInitialized() {
	if (!isInitialized) {
            throw new IllegalStateException(
                "TimerManager is not initialized");
        }
    }

    // Originally, initialize was synchronized, and this forced the other methods
    // to be synchronized as well, particularly points().  The points() method gets
    // called very frequently, roughly whenever an instance of any major class
    // needed during a request dispatch cycle is called.  Synchronizing on the points()
    // method results in a very bad lock hot spot which severly hampers ORB throughput
    // (Scott measured 15% in GF v2 build 49).
    public void initialize( T tp ) {
	if (isInitialized) {
            throw new IllegalStateException(
                "TimerManager is already initialized");
        }

	this.tp = tp ;
	isInitialized = true ;
    }

    /** Get the timing point utility class of type T.
     */
    public T points() {
	// Must NOT be synchronized!
	checkInitialized() ;
	return tp ;
    }

    /** Get the TimerFactory.
     */
    public TimerFactory factory() {
	return tf ;
    }

    /** Return a TimerController.  
     * Returns null if called before initialize( T ).
     */
    public TimerEventController controller() {
	return controller ;
    }

    public synchronized List<Timer> getTimers( Class<?> cls ) {
        checkInitialized();

        List<Timer> result = classToTimers.get( cls ) ;
        if (result == null) {
            result = new ArrayList<Timer>() ;

            List<String> names = MethodMonitorRegistry.getTimerNames(cls) ;
            for (String name : names) {
                String tname = TimerFactoryBuilder.getTimerName(
                    cls.getSimpleName(), name) ;
                Timer timer = tf.timers().get( tname ) ;
                result.add( timer ) ;
            }

            classToTimers.put( cls, result ) ;
        }

        return result ;
    }
}
