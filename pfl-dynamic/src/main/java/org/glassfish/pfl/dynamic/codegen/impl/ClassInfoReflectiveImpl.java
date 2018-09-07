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
import java.util.Map ;
import java.util.HashMap ;
import java.util.Set ;
import java.util.HashSet ;

import java.security.AccessController ;
import java.security.PrivilegedAction ;

import java.lang.reflect.Method ;
import java.lang.reflect.Constructor ;

import org.glassfish.pfl.dynamic.copyobject.spi.Immutable ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.FieldInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.MethodInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo ;

import org.glassfish.pfl.dynamic.codegen.impl.FieldInfoImpl ;

@Immutable
public class ClassInfoReflectiveImpl extends ClassInfoBase {
    private boolean DEBUG = false ;

    private void dprint( String msg ) {
	System.out.println( "ClassInfoReflectImpl: " + msg ) ;
    }

    public ClassInfoReflectiveImpl( final Type type ) {
	super( type.getTypeClass().getModifiers(), type ) ;

	if (DEBUG)
	    dprint( "Constructor for type " + type ) ;

	assert !type.isPrimitive() ;
	assert !type.isArray() ;

        AccessController.doPrivileged( 
            new PrivilegedAction<Object>() {
                public Object run() {
                    Class<?> cls = type.getTypeClass() ;

                    List<Type> impls = new ArrayList<Type>() ;
                    if (DEBUG) dprint( "Setting interfaces: " ) ;

                    for (Class<?> x : cls.getInterfaces()) {
                        if (DEBUG) dprint( "\t" + x.getName() ) ;

                        impls.add( Type.type(x) ) ;
                    }

                    if (cls.isInterface()) {
                        initializeInterface( impls ) ;
                    } else {
                        Type stype = null ;
                        if (cls.getSuperclass() != null)
                            stype = Type._class( 
                                cls.getSuperclass().getName() ) ;

                        initializeClass( type, stype, impls ) ;
                    }

                    if (DEBUG) dprint( "Setting fields:" ) ;
                    for (java.lang.reflect.Field x : cls.getDeclaredFields()) {
                        if (DEBUG) dprint( "\t" + x.getName() ) ;

                        FieldInfo var = new FieldInfoImpl( 
                            ClassInfoReflectiveImpl.this, x.getModifiers(),
                            Type.type( x.getType()), x.getName() ) ;
                        addFieldInfo( var ) ;
                    }
                    
                    if (DEBUG) dprint( "Setting methods:" ) ;
                    for (Method x : cls.getDeclaredMethods()) {
                        if (DEBUG) dprint( "\t" + x ) ;
                        addMethodInfo( new MethodInfoReflectiveImpl( 
                            ClassInfoReflectiveImpl.this, x )) ;
                    }

                    if (DEBUG) dprint( "Setting constructors:" ) ;
                    for (Constructor x : cls.getDeclaredConstructors()) {
                        if (DEBUG) dprint( "\t" + x ) ;

                        addConstructorInfo( new MethodInfoReflectiveImpl( 
                            ClassInfoReflectiveImpl.this, x )) ;
                    }

                    return null ;
                }
            }
        ) ;
    }
}
