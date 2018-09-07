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

import java.util.Stack ;
import org.glassfish.pfl.tf.timer.spi.StatisticsAccumulator;
import org.glassfish.pfl.tf.timer.spi.TimerEvent;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;

// This is a single threaded version of the stats event handler.  It will not
// work correctly if multiple threads are generating timer events!
public class StatsEventHandlerImpl extends StatsEventHandlerBase {
    private Stack<TimerEvent> teStack ; 

    StatsEventHandlerImpl( TimerFactory factory, String name ) {
	super( factory, name ) ;
	teStack = new Stack<TimerEvent>() ;
    }

    public void clear() {
	super.clear() ;
	while (!teStack.empty())
	    teStack.pop() ;
    }

    protected void recordDuration( int id, long duration ) {
	StatisticsAccumulator acc = saList.get( id ) ;
	acc.sample( duration ) ;
    }

    public void notify( TimerEvent event ) {
	notify( teStack, event ) ;
    }
}
