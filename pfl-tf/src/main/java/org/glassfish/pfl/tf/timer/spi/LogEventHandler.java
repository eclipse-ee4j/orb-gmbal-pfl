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

import java.io.PrintStream ;

/** A TimerEventHandler that stores all events that is receives.  It can
 * be used in the JDK 5 for loop.
 */
public interface LogEventHandler extends TimerEventHandler, Iterable<TimerEvent> {
    /** Discard the contents of the log.
     */
    void clear() ;

    /** Display the contents of this log in formatted form to the PrintStream.
     */
    void display( PrintStream arg, String msg ) ;
}
