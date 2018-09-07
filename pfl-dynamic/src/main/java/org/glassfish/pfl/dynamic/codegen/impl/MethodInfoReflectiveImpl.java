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

import org.glassfish.pfl.dynamic.copyobject.spi.Immutable ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo ;

/** Implementation of MethodInfo interface for actual Method.
 * Note that this internally caches the Method, and so all the
 * usual precautions for storing instances of this class in 
 * maps apply.
 */

@Immutable
public class MethodInfoReflectiveImpl extends MethodInfoBase {
    private Method method = null ;
    private Constructor constructor = null ;

    public MethodInfoReflectiveImpl( ClassInfo cinfo, Constructor constructor ) {
	super( cinfo, constructor.getModifiers()  ) ;

	this.constructor = constructor ;
	init( constructor.getExceptionTypes(), 
	    constructor.getParameterTypes() ) ;
    }

    public MethodInfoReflectiveImpl( ClassInfo cinfo, Method method ) {
	super( cinfo, method.getModifiers(), 
	    Type.type( method.getReturnType() ), method.getName() ) ;

	this.method = method ;
	init( method.getExceptionTypes(), 
	    method.getParameterTypes() ) ;
    }

    private void init( Class<?>[] exceptions, Class<?>[] arguments ) {
	ExpressionFactory ef = new ExpressionFactory( null ) ;

	for (Class<?> cls : exceptions) 
	    this.exceptions.add( Type.type(cls) ) ;

	// Note that we can't get the real parameter 
	// names through reflection, so we just make
	// up names in this case.  The names must not
	// affect hashCode or equals (see MethodInfoBase).
	int ctr = 0 ;
	for (Class<?> cls : arguments){
	    String name = "arg" + ctr++ ;
	    VariableInternal var = (VariableInternal)ef.variable(
                Type.type(cls), name ) ;
	    var.close() ;
	    this.arguments.add( var ) ;
	}
    }

    @Override
    public Method getMethod() {
	if (isConstructor())
	    throw new IllegalStateException( 
		"Cannot obtain a Method from a MethodInfo that represents a Constructor" ) ;
	return method ;
    }

    @Override
    public Constructor getConstructor() {
	if (!isConstructor())
	    throw new IllegalStateException( 
		"Cannot obtain a Constructor from a MethodInfo that represents a Method" ) ;
	return constructor ;
    }
}
