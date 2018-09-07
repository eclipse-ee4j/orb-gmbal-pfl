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

import java.util.Set ;

/** Represents a named object that can be enabled or disabled.
 * It may also contain other Controllable instances.
 */
public interface Controllable extends Named {
    /** A longer description giving some details of the meaning of this
     * Controllable.
     */
    String description() ;

    /** A small id for this controllable.  Each controllable created from
     * the same TimerFactory will have a unique ID.  All ids will be small
     * integers starting at 0 (so indexing tables by timer ID is supported).
     */
    int id() ;

    /** Return an unmodifiable set of the contents of this Controllable.
     * May always be empty for some subclasses of Controllable.
     */
    Set<? extends Controllable> contents() ;

    /** Enable this controllable.  All Timers that are either enabled, or
     * reachable via contents() from an enabled Controllable are activated,
     * and will cause TimerEvents to be generated when passed to the
     * TimerEventController enter and exit methods.
     */ 
    void enable() ;

    /** Disable this controllable.
     */
    void disable() ;

    /** Return true if enable() was called, otherwise false if enable() was never
     * called, or disable() was last called.
     */
    boolean isEnabled() ;
}
