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

import java.lang.reflect.Method ;
import java.lang.reflect.Constructor ;

import java.util.List ;
import java.util.ArrayList ;

import org.glassfish.pfl.dynamic.codegen.spi.MethodInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.Signature ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;

public abstract class MethodInfoBase extends MemberInfoBase
    implements MethodInfo {

    protected Type rtype ;
    protected List<Type> exceptions ;
    protected List<Variable> arguments ;
    protected boolean isConstructor ;

    private Signature sig ;
    private boolean sigIsCached ;

    private int hashValue ;
    private boolean hashIsCached ;

    protected MethodInfoBase( ClassInfo cinfo, int modifiers ) {
	this( cinfo, modifiers, Type._void(), CodeGeneratorUtil.CONSTRUCTOR_METHOD_NAME ) ;
	this.isConstructor = true ;
    }

    protected MethodInfoBase( ClassInfo cinfo, int modifiers, Type rtype, String name ) {
	super( cinfo, modifiers, name ) ;
	this.rtype = rtype ;
	this.exceptions = new ArrayList<Type>() ;
	this.arguments = new ArrayList<Variable>() ;

	sig = null ;
	sigIsCached = false ;

	hashValue = 0 ;
	hashIsCached = false ;

	this.isConstructor = false ;
    }

    public boolean isConstructor() {
	return isConstructor ;
    }

    public Type returnType() {
	return rtype ;
    }

    public List<Type> exceptions() {
	return exceptions ;
    }

    public List<Variable> arguments() {
	return this.arguments ;
    }

    public synchronized Signature signature() {
	if (!sigIsCached) {
	    List<Type> argTypes = new ArrayList<Type>(arguments.size()) ;
	    for (Variable var : arguments)
		argTypes.add( ((VariableInternal)var).type() ) ;
	    sig = Signature.make( rtype, argTypes ) ;
	}

	return sig ;
    }

    public Method getMethod() {
	return null ;
    }

    public Constructor getConstructor() {
	return null ;
    }

    public boolean equals( Object obj ) {
	if (obj == this)
	    return true ;

	if (!(obj instanceof MethodInfo))
	    return false ;
    
	MethodInfo other = MethodInfo.class.cast( obj ) ;

	if (hashCode() != other.hashCode())
	    return false ;

	if (!super.equals( obj ))
	    return false ;

	if (!signature().equals( other.signature() ))
	    return false ;

	if (!exceptions().equals( other.exceptions() )) 
	    return false ;

	return true ;
    }

    public synchronized void clearHashCode() {
	hashIsCached = false ;
	hashValue = 0 ;
    }

    public synchronized int hashCode() {
	if (!hashIsCached) {
	    hashValue = super.hashCode() ;
	    hashValue ^= signature().hashCode() ;
	    hashValue ^= exceptions().hashCode() ;

	    hashIsCached = true ;
	}

	return hashValue ;
    }
}
