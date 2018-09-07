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

import java.util.Map ;
import java.util.HashMap ;
import java.util.LinkedHashMap ;
import java.util.Set ;
import java.util.HashSet ;
import java.util.Collections ;

import org.glassfish.pfl.tf.timer.spi.Controllable;
import org.glassfish.pfl.tf.timer.spi.LogEventHandler;
import org.glassfish.pfl.tf.timer.spi.NamedBase;
import org.glassfish.pfl.tf.timer.spi.ObjectRegistrationManager;
import org.glassfish.pfl.tf.timer.spi.StatsEventHandler;
import org.glassfish.pfl.tf.timer.spi.Timer;
import org.glassfish.pfl.tf.timer.spi.TimerEvent;
import org.glassfish.pfl.tf.timer.spi.TimerEventController;
import org.glassfish.pfl.tf.timer.spi.TimerEventControllerBase;
import org.glassfish.pfl.tf.timer.spi.TimerEventHandler;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;
import org.glassfish.pfl.tf.timer.spi.TimerGroup;

// TimerFactory is a TimerGroup containing all timers and timer groups 
// that it creates
// 
// Synchronization turns out to be a bit tricky in this module.  The contains
// graph can contain cycles, so locking in recursive calls through 
// TimerGroupImpl instances could result in deadlocks.  We also don't 
// want a single global lock for everything, because Timer.isActivated()
// calls must be quick, and avoid introducing a lot of contention.
//
// The current solution is to use two locks: a global lock on TimerFactoryImpl,
// which is used for all enable/disable/TimerGroup contents changes.
// A lock will also be used in Timer to control access to the
// activation state of a Timer.
public class TimerFactoryImpl extends TimerGroupImpl implements TimerFactory {
    private ObjectRegistrationManager orm ;

    // The string<->int dictionary for timer names
    private Map<Controllable,Integer> conToInt ;
    private Map<Integer,Controllable> intToCon;

    // Next available index in dictionary
    private int nextIndex ;

    private Map<String,TimerImpl> timers ;
    private Map<String,TimerImpl> roTimers ;
    private Map<String,TimerGroupImpl> timerGroups ;
    private Map<String,TimerGroupImpl> roTimerGroups ;
    private Map<String,TimerEventHandler> timerEventHandlers ;
    private Map<String,TimerEventControllerBase> timerEventControllers ;

    public TimerFactoryImpl( ObjectRegistrationManager orm, String name,
        String description ) {
	super( 0, null, name, description ) ;
        this.orm = orm ;
	setFactory( this ) ;
	add( this ) ; // The TimerFactory is a group containing all 
		      // Timers and TimerGroups it creates, so it
		      // should contain itself.
	conToInt = new HashMap<Controllable,Integer>() ;
	intToCon = new HashMap<Integer,Controllable>() ;
	nextIndex = 0 ;
	mapId( this ) ;

	timers = new LinkedHashMap<String,TimerImpl>() ;
	roTimers = Collections.unmodifiableMap( timers ) ;

	timerGroups = new LinkedHashMap<String,TimerGroupImpl>() ;
	timerGroups.put( name(), this ) ;
	roTimerGroups = Collections.unmodifiableMap( timerGroups ) ;

	timerEventHandlers = new HashMap<String,TimerEventHandler>() ;
	timerEventControllers = new HashMap<String,TimerEventControllerBase>() ;
        orm.manage( this ) ;
    }

    // con must be a Controllable with an id already set (which is
    // forced by the ControllableBase constructor).  Update both
    // con <-> int maps, and increment nextIndex.  Must be called
    // after any Controllable is constructed.
    private void mapId( Controllable con ) {
	conToInt.put( con, con.id() ) ;
	intToCon.put( con.id(), con ) ;
	nextIndex++ ;
    }
    
    // Check that the name and description are valid, and that
    // the name is not already in use for a Controllable.
    private void checkArgs( Set<String> inUse, String name, 
	String description ) {

	if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
	if (description == null) {
            throw new IllegalArgumentException("description must not be null");
        }
	if (inUse.contains(name)) {
            throw new IllegalArgumentException(name + " is already used.");
        }
    }

    @Override
    public synchronized int numberOfIds() {
	return nextIndex ;
    }

    @Override
    public synchronized Controllable getControllable( int id ) {
	if ((id >= 0) && (id < nextIndex)) {
            return intToCon.get(id);
        }

	throw new IllegalArgumentException( "Argument " + id 
	    + " must be between 0 and " + (nextIndex - 1)) ;
    }

    public static class TracingEventHandler 
	extends NamedBase 
	implements TimerEventHandler {

	public TracingEventHandler( TimerFactory factory, String name ) {
	    super( factory, name ) ;
	}

        @Override
	public void notify( TimerEvent event ) {
	    System.out.println( Thread.currentThread().getName() 
		+ " TRACE " + event ) ;
	}
    }

    @Override
    public synchronized TimerEventHandler makeTracingEventHandler( 
	String name ) {

	if (timerEventHandlers.keySet().contains( name )) {
            throw new IllegalArgumentException("Name " + name +
                " is already in use.");
        }

	TimerEventHandler result = new TracingEventHandler( factory(), name ) ;
        orm.manage( this, result ) ;
	timerEventHandlers.put( name, result ) ;
	return result ;
    }

    @Override
    public synchronized LogEventHandler makeLogEventHandler( String name ) {
	if (timerEventHandlers.keySet().contains( name )) {
            throw new IllegalArgumentException("Name " + name +
                " is already in use.");
        }

	LogEventHandler result = new LogEventHandlerImpl( factory(), name ) ;
        orm.manage( this, result ) ;
	timerEventHandlers.put( name, result ) ;
	return result ;
    }

    @Override
    public synchronized StatsEventHandler makeStatsEventHandler( String name ) {
	if (timerEventHandlers.keySet().contains( name )) {
            throw new IllegalArgumentException("Name " + name +
                " is already in use.");
        }

	StatsEventHandler result = new StatsEventHandlerImpl( factory(), 
	    name ) ;
        orm.manage( this, result ) ;
	timerEventHandlers.put( name, result ) ;
	return result ;
    }

    @Override
    public synchronized StatsEventHandler makeMultiThreadedStatsEventHandler( 
	String name ) {

	if (timerEventHandlers.keySet().contains( name )) {
            throw new IllegalArgumentException("Name " + name +
                " is already in use.");
        }

	StatsEventHandler result = new MultiThreadedStatsEventHandlerImpl( 
	    factory(), name ) ;
        orm.manage( this, result ) ;
	timerEventHandlers.put( name, result ) ;
	return result ;
    }

    @Override
    public synchronized void removeTimerEventHandler( 
	TimerEventHandler handler ) {

	timerEventHandlers.remove( handler.name() ) ;
        orm.unmanage( handler ) ;
    }

    @Override
    public synchronized Timer makeTimer( String name, String description ) {
	checkArgs( timers.keySet(), name, description ) ;

	TimerImpl result = new TimerImpl( nextIndex, this, name, description ) ;
        orm.manage( this, result ) ;
	mapId( result ) ;
	timers.put( name, result ) ;
	add( result ) ;  // Remember, a TimerFactory is a TimerGroup 
			 // containing all Controllables that it creates!

	return result ;
    }

    @Override
    public synchronized Map<String,TimerImpl> timers() {
	return roTimers ;
    }

    @Override
    public synchronized TimerGroup makeTimerGroup( String name, 
	String description ) {

	checkArgs( timerGroups.keySet(), name, description ) ;

	TimerGroupImpl result = new TimerGroupImpl( nextIndex, this, name, 
	    description ) ;
        orm.manage( this, result ) ;

	mapId( result ) ;
	timerGroups.put( result.name(), result ) ;
	add( result ) ;  // Remember, a TimerFactory is a TimerGroup 
			 // containing all
		         // Controllables that it creates!

	return result ;
    }

    @Override
    public synchronized Map<String,TimerGroupImpl> timerGroups() {
	return roTimerGroups ;
    }

    public void saveTimerEventController( TimerEventControllerBase tec ) {
	if (timerEventControllers.keySet().contains( tec.name() )) {
            throw new IllegalArgumentException("Name " + tec.name() +
                " is already in use.");
        }

	timerEventControllers.put( tec.name(), tec ) ;
    }

    @Override
    public synchronized TimerEventController makeController( String name ) {
	TimerEventController result = new TimerEventController( this, name ) ;
        orm.manage( this, result ) ;
	return result ;
    }

    @Override
    public synchronized void removeController( 
	TimerEventControllerBase controller ) {

	timerEventControllers.remove( controller.name() ) ;
        orm.unmanage( controller ) ;
    }

    @Override
    public synchronized Set<? extends Controllable> enabledSet() {
	Set<Controllable> result = new HashSet<Controllable>() ;

	for (Timer t : timers.values()) {
            if (t.isEnabled()) {
                result.add(t);
            }
        }

	for (TimerGroup tg : timerGroups.values()) {
            if (tg.isEnabled()) {
                result.add(tg);
            }
        }

	return result ;    
    }

    @Override
    public synchronized Set<Timer> activeSet() {
	Set<Timer> result = new HashSet<Timer>() ;

	for (Timer t : timers.values()) {
            if (t.isActivated()) {
                result.add(t);
            }
        }

	return result ;    
    }

    void updateActivation() {
	// First, set all timers to their enabled state.
	// Enabled timers are always activated, but disabled
	// timers may be enabled later because of enabled
	// timer groups.
	for (TimerImpl timer : timers.values()) {
	    timer.setActivated( timer.isEnabled() ) ;
	}
	
	// Now, iterate over the tcContents of the TimerGroups.
	// Activate each Timer contained in the transitive closure
	// of an enabled TimerGroup.
	for (TimerGroupImpl tg : timerGroups.values()) {
	    if (tg.isEnabled()) {
		Set<ControllableBase> tcc = tg.tcContents() ;
		for (ControllableBase c : tcc) {
		    if (c instanceof Timer) {
			TimerImpl ti = TimerImpl.class.cast( c ) ;
			ti.setActivated( true ) ;
		    }
		}
	    }
	}
    }

    @Override
    public synchronized boolean timerAlreadyExists( String name ) {
        return timers.keySet().contains( name ) ;
    }
}

