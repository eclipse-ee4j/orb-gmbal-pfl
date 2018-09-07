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

/** A TimerGroup is a collection of Controllables, which includes
 * Timers and TimerGroups.  The contents() method in a TimerGroup
 * returns an unmodifiable set.  The contents may only be updated
 * throught the add and remove methods.
 */
public interface TimerGroup extends Controllable {
    boolean add( Controllable con ) ;

    boolean remove( Controllable con ) ;
}
