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

public abstract class ControllableBase extends NamedBaseImpl
    implements Controllable {
    private static final Set<ControllableBase> emptyContent =
	Collections.unmodifiableSet( new HashSet<ControllableBase>() ) ;

    private int id ;
    private String description ;
    private boolean isEnabled ;

    protected ControllableBase( int id, String name, String description, 
	TimerFactoryImpl factory ) {
	super( factory, name ) ;
	this.id = id ;
	this.description = description ;
	isEnabled = false ;
    }

    @Override
    public int id() {
	return id ;
    }

    @Override
    public String description() {
	return description ;
    }

    // Allow setting this so that we can implement the XML parser
    // for generating timer files.
    void description( String description ) {
	this.description = description ;
    }

    @Override
    public final boolean isEnabled() {
	return isEnabled ;
    }

    // Override this in subclass if subclass can contain
    // other Controllables.
    // covariant return type: Set<ControllableBase> is a 
    // subtype of Set<? extends ControllableBase>
    @Override
    public Set<ControllableBase> contents() {
	return emptyContent ;
    }

    @Override
    public void enable() {
	synchronized( factory() ) {
	    if (!isEnabled()) {
		isEnabled = true ;
		factory().updateActivation() ;
	    }
	}
    }

    @Override
    public void disable() {
	synchronized( factory() ) {
	    if (isEnabled()) {
		isEnabled = false ;
		factory().updateActivation() ;
	    }
	}
    }

    // This is only called from TimerGroupImpl.tcContents, which is only 
    // called from TimerFactoryImpl.updateActiation, which in turn
    // is only called from enable and disable.  Therefore this does
    // not need any additional synchronization.
    void transitiveClosure( Set<ControllableBase> result ) {
	result.add( this ) ;
	for (ControllableBase c : contents() ) {
	    if (!result.contains(c)) {
		c.transitiveClosure( result ) ;
	    }
	}
    }
}

