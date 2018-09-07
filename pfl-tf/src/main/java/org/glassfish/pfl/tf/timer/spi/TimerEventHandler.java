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

/** Handles timer events, represented by timer instances.
 * An enter event means that a measurement has begun for an event,
 * and an exit event signals the end of the last measurement that
 * was started.
 */
public interface TimerEventHandler extends Named {
    void notify( TimerEvent event ) ;
}
