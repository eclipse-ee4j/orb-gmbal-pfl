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

import java.util.Set ;
import java.util.HashSet ;
import java.util.Collections ;
import org.glassfish.pfl.tf.timer.spi.Controllable;
import org.glassfish.pfl.tf.timer.spi.TimerGroup;

/** A TimerGroup is a collection of Controllables, which includes
 * Timers and TimerGroups.
 */
public class TimerGroupImpl extends ControllableBase implements TimerGroup {
    private Set<ControllableBase> contents ;
    private Set<ControllableBase> roContents ;
    private long contentVersion ;

    private Set<ControllableBase> tcContents ;	// the transitive closure for this element
						// of the relation created by contents.
    private Set<ControllableBase> roTcContents ;

    private long tcContentVersion ;

    TimerGroupImpl( int id, TimerFactoryImpl factory, String name, String description ) {
	super( id, name, description, factory ) ;

	contents = new HashSet<ControllableBase>() ;
	roContents = Collections.unmodifiableSet( contents ) ;
	contentVersion = 0 ;

	tcContents = new HashSet<ControllableBase>() ;
	roTcContents = Collections.unmodifiableSet( tcContents ) ;
	tcContentVersion = -1 ;
    }

    public Set<ControllableBase> contents() {
	return roContents ;
    }

    public boolean add( Controllable con ) {
	synchronized (factory()) {
	    boolean result = contents.add( ControllableBase.class.cast( con ) ) ;
	    if (result)
		contentVersion++ ;
	    return result ;
	}
    }

    public boolean remove( Controllable con ) {
	synchronized (factory()) {
	    boolean result = contents.remove( ControllableBase.class.cast( con ) ) ;
	    if (result)
		contentVersion++ ;
	    return result ;
	}
    }

    // Get the transitive closure.  This is cached, and updated if the
    // contents have changed since the last access to tcContents.
    Set<ControllableBase> tcContents() {
	if (contentVersion != tcContentVersion) {
	    tcContents.clear() ;
	    transitiveClosure( tcContents ) ;
	}

	return roTcContents ;
    }
}

