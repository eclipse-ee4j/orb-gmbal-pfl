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

import java.util.List ;
import java.util.ArrayList ;

import java.lang.reflect.Modifier ;

import org.glassfish.pfl.dynamic.codegen.spi.Signature ;
import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;
import org.glassfish.pfl.dynamic.codegen.spi.MemberInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo ;

public class MemberInfoBase implements MemberInfo {
    private ClassInfo myClassInfo ;
    private int modifiers ;
    private String name ;

    public MemberInfoBase( ClassInfo myClassInfo, int modifiers,
	String name ) {

	this.myClassInfo = myClassInfo ;
	this.modifiers = modifiers ;
	this.name = name ;
    }

    public ClassInfo myClassInfo() {
	return this.myClassInfo ;
    }

    public int modifiers() {
	return this.modifiers ;
    }

    public String name() {
	return this.name ;
    }

    public boolean isAccessibleInContext( ClassInfo definingClass,
	ClassInfo accessClass ) {

	if (Modifier.isPublic( modifiers )) {
	    return true ;
	}

	if (Modifier.isPrivate( modifiers)) {
	    return myClassInfo.name().equals( definingClass.name() ) ;
	}

	if (Modifier.isProtected( modifiers)) {
	    if (myClassInfo.pkgName().equals( definingClass.pkgName())) {
		return true ;
	    } else {
		return definingClass.isSubclass( myClassInfo ) &&
		    accessClass.isSubclass( definingClass ) ;
	    }
	}

	// check default access
	return myClassInfo.pkgName().equals( definingClass.pkgName() ) ;
    }

    public int hashCode() {
	return name.hashCode() ^ modifiers ;
    }

    public boolean equals( Object obj ) {
	if (!(obj instanceof MemberInfo))
	    return false ;

	if (obj == this) 
	    return true ;

	MemberInfo other = MemberInfo.class.cast( obj ) ;

	return name.equals(other.name()) &&
	    modifiers == other.modifiers() ; 
    }

    public String toString() {
	return this.getClass().getName() + "[" + Modifier.toString( modifiers ) 
	    + name + "]" ;
    }
}

