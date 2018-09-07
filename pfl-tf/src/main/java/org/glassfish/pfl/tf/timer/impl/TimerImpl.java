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

import java.util.concurrent.atomic.AtomicBoolean ;
import org.glassfish.pfl.tf.timer.spi.Timer;

/** This interface exists because a Controllable is not 
 * loggable: only a Timer is loggable (we do not want
 * TimerGroup to be loggable).
 *
 * @author  Ken Cavanaugh
 */
public class TimerImpl extends ControllableBase implements Timer {
    // Use an AtomicBoolean to avoid possible thread contention.
    // Is this a good tradeoff?  Most likely it will cost more
    // to access an AtomicBoolean than a Boolean in the most common cases.
    private AtomicBoolean isActivated ;

    TimerImpl( int id, TimerFactoryImpl factory, String name, String description) {
	super( id, name, description, factory ) ;
	isActivated = new AtomicBoolean( false ) ;
    }

    public final boolean isActivated() {
	return isActivated.get() ;
    }

    void setActivated( boolean flag ) {
	isActivated.set( flag ) ;
    }
}
