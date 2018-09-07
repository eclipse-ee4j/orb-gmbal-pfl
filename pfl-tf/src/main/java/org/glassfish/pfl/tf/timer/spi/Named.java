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

/** A simple interface used to provide access to the name and the
 * factory that created this instance.  All types that are
 * created by the TimerFactory implement this interface.
 */
public interface Named {
    /** Return the TimerFactory that created this Named.
     */
    TimerFactory factory() ;

    /** A short name for this Controllable.
     */
    String name() ;
}
