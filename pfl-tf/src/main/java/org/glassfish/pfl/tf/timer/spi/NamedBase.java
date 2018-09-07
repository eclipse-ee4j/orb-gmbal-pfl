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

// A very simple class that represents something with a name,
// which is hashed on that name.
public abstract class NamedBase implements Named {
    private TimerFactory factory ;
    private String name ;
    private String toStringName ;

    protected NamedBase( TimerFactory factory, String name ) {
	this.name = name ;
	if (factory != null) 
	    setFactory( factory ) ;
    }

    protected void setFactory( TimerFactory factory ) {
	if (this.factory == null) {
	    this.factory = factory ;
	    final String className = this.getClass().getName() ;
	    final int lastDot = className.lastIndexOf( '.' ) ;
	    this.toStringName = className.substring( lastDot + 1 ) 
		+ "[" + factory.name() + ":" + name + "]";
	} else {
	    throw new IllegalStateException( "NamedBase.factory can only be set once!" ) ;
	}
    }

    private void checkFactorySet() {
	if (factory == null)
	    throw new IllegalStateException( "NamedBase.factory is not set!" ) ;
    }

    public TimerFactory factory() {
	checkFactorySet() ;
	return factory ;
    }

    public String name() {
	return name ;
    }

    public boolean equals( Object obj ) {
	if (obj == this)
	    return true ;

	if (!(obj instanceof Named)) 
	    return false ;

	Named other = Named.class.cast( obj ) ;
	return other.name().equals( name ) && 
            other.getClass().equals( this.getClass() ) ;
    }

    public int hashCode() {
	return name.hashCode() ;
    }

    public String toString() {
	checkFactorySet() ;
	return toStringName ;
    }
}
