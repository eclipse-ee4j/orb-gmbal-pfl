/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm ;

import java.util.Collections ;
import java.util.List ;
import java.util.ArrayList ;

import java.lang.reflect.Method ;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import org.glassfish.pfl.basic.func.UnaryPredicate;
    
/** Analyzes class inheritance hiearchy and provides methods for searching for
 * classes and methods.
 */
public class ClassAnalyzer {
    // General purpose class analyzer
    //
    // The basic problem is to determine for any class its linearized inheritance
    // sequence.  This is an old problem in OOP.  For my purpose, I want the following
    // to be true:
    //
    // Let C be a class, let C.super be C's superclass, and let C.inter be the list of
    // C's implemented interfaces (C may be an interface, abstract, or concrete class).
    // Define ILIST(C) to be a sequence that satisfies the following properties:
    //
    // 1. ILIST(C) starts with C.
    // 2. If X is in ILIST(C), then so is X.super and each element of X.inter.
    // 3. For any class X in ILIST(C):
    //    2a. X appears before X.super in ILIST(C)
    //    2b. X appears before any X.inter in ILIST(C)
    // 4. No class appears more than once in ILIST(C)
    //
    // Note that the order can change when new classes are analyzed, so each class must be 
    // analyzed independently
    //
    // We need to elaborate on this idea to handle several issues:
    //
    // 1. We start with needing to determine whether a particular class C is ManagedData (mapped
    //    to composite data, and used for attribute and operation values in an Open MBean) or
    //    ManagedObject (mapped to an MBean with an ObjectName).  We will require that the super
    //    class graph of any object contain at most one class annotated with @ManagedObject or
    //    @ManagedData (and not both).  This means that for any class C, there is a class MC
    //    (which may be C) which is the unique class that is a superclass of C and is annotated
    //    with either @ManagedData or @ManagedObject.
    // 2. The MC class may also contain InheritedAttribute and IncludeSubclass annotations.
    //    InheritedAttribute is handled by searching in the superclasses for getter and setters
    //    conforming to the InheritedAttribute id.  IncludeSubclass extends the set of classes
    //    to scan for @ManagedAttribute and @ManagedOperation by the union of MC's superclasses,
    //    and the superclasses of all classes specified by IncludeSubclass.
    // 3. What we require here is that ALL classes that share the same MC class translate to the
    //    SAME kind of MBean or CompositeData.
    private static final Graph.Finder<Class<?>> finder = new Graph.Finder<Class<?>>() {
        @Override
	public List<Class<?>> evaluate( Class<?> arg ) {
	    List<Class<?>> result = new ArrayList<Class<?>>() ;
	    Class<?> sclass = arg.getSuperclass() ;
	    if (sclass != null) {
		result.add( sclass ) ;
	    }
	    for (Class<?> cls : arg.getInterfaces() ) {
		result.add( cls ) ;
	    }
	    return result ;
	}
    } ;

    private static Map<Class<?>,ClassAnalyzer> caMap =
	new WeakHashMap<Class<?>,ClassAnalyzer>() ;

    public static synchronized ClassAnalyzer getClassAnalyzer( Class<?> cls ) {
        ClassAnalyzer result = caMap.get( cls ) ;
	if (result == null) {
	    result = new ClassAnalyzer(cls) ;
	    caMap.put( cls, result ) ;
	}

	return result ;
    }

    private List<Class<?>> classInheritance ;
    private String contents = null ;

    private ClassAnalyzer( Graph<Class<?>> gr ) {
	List<Class<?>> result = new ArrayList<Class<?>>( 
            gr.getPostorderList() ) ;
	Collections.reverse( result ) ;
        classInheritance = result ;
    }

    private ClassAnalyzer( final Class<?> cls ) {
	this( new Graph<Class<?>>( cls, finder ) ) ;
    }

    public List<Class<?>> findClasses( UnaryPredicate<Class<?>> pred ) {
	final List<Class<?>> result = new ArrayList<Class<?>>() ;
	for (Class<?> c : classInheritance) {
            if (pred.evaluate( c )) {
                result.add( c ) ;
            }
        }

        return result ;
    }

    private static List<Method> getDeclaredMethods( final Class<?> cls ) {
        SecurityManager sman = System.getSecurityManager() ;
        if (sman == null) {
            return Arrays.asList( cls.getDeclaredMethods() ) ;
        } else {
            return AccessController.doPrivileged(
                new PrivilegedAction<List<Method>>() {
                    @Override
                    public List<Method> run() {
                        return Arrays.asList( cls.getDeclaredMethods() ) ;
                    }
                }
            ) ;

        }
    }

    // Tested by testFindMethod
    // Tested by testGetAnnotatedMethods
    public List<Method> findMethods( UnaryPredicate<Method> pred ) {
	final List<Method> result = new ArrayList<Method>() ;
	for (Class<?> c : classInheritance) {
	    for (Method m : getDeclaredMethods( c )) {
                if (pred.evaluate( m )) {
                    result.add( m ) ;
                }
	    }
	}

	return result ;
    }
    
    @Override
    public synchronized String toString() {
        if (contents == null) {
            StringBuilder sb = new StringBuilder() ;

            boolean first = true ;
            sb.append( "ClassAnalyzer[" ) ;
            for (Class<?> cls : classInheritance) {
                if (first) {
                    first = false ;
                } else {
                    sb.append( " " ) ;
                }
                sb.append( cls.getSimpleName() ) ;
            }
            sb.append( "]" ) ;
            contents = sb.toString() ;
        }

        return contents ;
    }
}
