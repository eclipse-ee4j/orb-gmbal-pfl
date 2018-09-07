/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import java.lang.reflect.Modifier ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.FieldInfo ;

public class FieldInfoImpl extends MemberInfoBase implements FieldInfo {    
    protected Type type ;

    public FieldInfoImpl( ClassInfo cinfo, int modifiers, Type type, 
	String ident ) {
    
	super( cinfo, modifiers, ident ) ;
	this.type = type ;
    }

    public Type type() {
	return type ;
    }

    public int hashCode() {
	return super.hashCode() ^ type.hashCode() ;
    }

    public boolean equals( Object obj ) {
	if (!(obj instanceof FieldInfo))
	    return false ;

	if (obj == this) 
	    return true ;

	FieldInfo other = FieldInfo.class.cast( obj ) ;

	return super.equals( obj ) &&
	    type.equals(other.type()) ;
    }

    public String toString() {
	return "FieldInfo[" + Modifier.toString( modifiers() ) + " " +
	    type.name() + " " + name() + "]" ;
    }
}
