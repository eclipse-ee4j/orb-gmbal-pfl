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

/** Gather statistics on the times reported to this TimerEventHandler.  It will keep 
 * events from different thread separated in order to get good results, but the
 * result of the stats call merges all of the results together.
 * <p>
 * The time is the duration between an enter and an exit call to a timer from
 * the same thread.  Recursive calls are matched, and the results accumulated
 * as in any other case.
 */
public interface StatsEventHandler extends TimerEventHandler {
    /** Return map that gives the accumulated statistics for each
     * TimerEvent that has been observed by this event handler since
     * the last call to clear (or since the creation of this handler,
     * if clear has not been called).
     */
    Map<Timer,Statistics> stats() ;

    /** Discard all accumulated statistics.
     */
    void clear() ;
}

