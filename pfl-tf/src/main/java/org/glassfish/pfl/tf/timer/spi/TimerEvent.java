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

/** Represents a particular observable event.  We are mostly interested
 * in measuring how long an operation takes.  An operation is typically
 * represented by a Timer, and then the timer is used to generate
 * a TimerEvent at the entry to and exit from the operation.
 * <p>
 * Note that this class can also be used as a base class, in case
 * there is a need to attach extra information to a TimerEvent.
 * <p> 
 * All access to this class is unsynchronized.  This class must be
 * used either as an immutable (no calls to update), or access
 * must be restricted to a single thread (as in an iterator).
 */
public class TimerEvent {
    private Timer timer ;
    private TimerEvent.TimerEventType etype ;
    private long time ;
    public String toString() {
	return "TimerEvent[" + etype + " " + timer.name() + "@" + time/1000 + "]" ;
    }

    public TimerEvent( TimerEvent te ) {
	this (te.timer(), te.type()) ;
	this.time = te.time() ;
    }

    /** Create a TimerEvent at the current time.
     */
    public TimerEvent( Timer timer,
	TimerEvent.TimerEventType etype ) {

	long time = System.nanoTime() ;
	internalSetData( timer, etype, time ) ;
    }

    /** Create a TimerEvent at the given time.
     */
    public TimerEvent( Timer timer,
	TimerEvent.TimerEventType etype, long time ) {

	internalSetData( timer, etype, time ) ;
    }

    /** Re-use the same TimerEvent instance with different
     * data.  Used to create flyweight instances for iteration
     * over a collection of TimerEvent instances.
     */
    public void update( Timer timer, 
	TimerEvent.TimerEventType etype, long time ) {

	internalSetData( timer, etype, time ) ;
    }

    private void internalSetData( Timer timer,
	TimerEvent.TimerEventType etype, long time ) {

	this.timer = timer ;
	this.etype = etype ;
	this.time = time ;
    }

    public void incrementTime( long update ) {
	time += update ;
    }

    /** The name of the Timer used to create this entry.
     */
    public Timer timer() {
	return timer ;
    }

    public enum TimerEventType { ENTER, EXIT }

    /** Type of event: ENTER for start of interval for a
     * Timer, EXIT for end of the interval.
     */
    public TimerEvent.TimerEventType type() {
	return etype ;
    }

    /** Time of event in nanoseconds since the TimerLog
     * was created or cleared.
     */
    public long time() {
	return time ;
    }
}
