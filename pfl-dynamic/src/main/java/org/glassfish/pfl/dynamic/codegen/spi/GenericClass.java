/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.spi;

import java.util.List ;
import java.util.ArrayList ;

import java.lang.reflect.Constructor ;

/** Class that allows any class to be instantiated via any accessible constructor.
 * Really a short hand to avoid writing a bunch of reflective code.
 */
public class GenericClass<T> {
    private Type implType ;
    private ClassInfo implClassInfo ;
    private Class<T> typeClass ;
    
    // Use the raw type of the constructor here, because
    // MethodInfo can only return a raw type for a constructor.
    // It is not possible to have MethodInfo return a 
    // Constructor<T> because T may not be known at compile time.
    private Constructor constructor ;
  
    /** Create a GenericClass of the given type by modifying classData
     * with the given interceptors.
     * XXX we may need a constructor that specifies the ClassLoader, etc.
     */
    public GenericClass( Class<T> type, InterceptorContext ic, byte[] classData ) {
	throw new IllegalArgumentException( "Not supported yet" ) ;
    }

    /** Create a generic of type T for the untyped class cls.
     * Generally cls is a class that has been generated and loaded, so
     * no compiled code can depend on the class directly. 
     * @throws IllegalArgumentException if cls is not a subclass of type. 
     */
    public GenericClass( Class<T> type, Class<?> cls ) {

	if (!type.isAssignableFrom( cls ))
	    throw new IllegalArgumentException( "Class " + cls.getName() +
		" is not a subclass of " + type.getName() ) ;

	implType = Type.type( cls ) ;
	implClassInfo = implType.classInfo() ;
	typeClass = type ;
    }

    private synchronized Constructor getConstructor( Object... args ) {
	if (constructor == null) {
	    List<Type> atypes = new ArrayList<Type>() ;
	    for (Object arg : args) {
		Type type = Type._null() ;
		if (arg != null) {
		    Class<?> cls = arg.getClass() ;
		    type = Type.type( cls ) ;
		}

		atypes.add( type ) ;
	    }

	    Signature sig = Signature.fromConstructorUsingTypes( implType, atypes ) ;
	    MethodInfo minfo = implClassInfo.findConstructorInfo( sig ) ;
	    constructor = minfo.getConstructor() ;
	}
	return constructor ;
    }
    
    private synchronized Constructor clearAndGetConstructor( Object... args ) {
	constructor = null ;
	return getConstructor( args ) ;
    }

    /** Create an instance of type T using the constructor that
     * matches the given arguments if possible.  The constructor
     * is cached, so an instance of GenericClass should always be
     * used for the same types of arguments.  If a call fails,
     * a check is made to see if a different constructor could 
     * be used.
     */
    public T create( Object... args ) {
	try {
	    try {
		return typeClass.cast( getConstructor().newInstance( args ) ) ;	
	    } catch (IllegalArgumentException argexc) {
		return typeClass.cast( clearAndGetConstructor( args ).newInstance( args ) ) ;
	    }
	} catch (Exception exc ) {
	    throw new IllegalArgumentException( 
		"Could not construct instance of class " 
		+ implType.name(), exc ) ;
	}
    }
}
