/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import java.util.Set ;
import java.util.HashSet ;
import java.util.List ;
import java.util.ArrayList ;
import org.glassfish.pfl.basic.func.NullaryFunction;

/** Class used to define dynamic attributes on AttributedObject instances.
 * Note that T cannot be a generic type, due to problems with
 * {@code Class<T>} when T is a generic.  To work around this problem,
 * simply create an interface that extends the generic type
 * (you are programming to interfaces, right?).
 */
public class Attribute<T> {
    private static List<Attribute<?>> attributes =
	new ArrayList<Attribute<?>>() ;

    private static synchronized int next( Attribute<?> attr ) {
	for (int ctr=0; ctr<attributes.size(); ctr++) {
            if (attr.name().equals(attributes.get(ctr).name())) {
                return ctr;
            }
        }

	int result = attributes.size() ;
	attributes.add( attr ) ;
	return result ;
    }

    public static int numberOfAttributes() {
	return attributes.size() ;
    }

    public static Attribute<?> get( int index ) {
	if ((index >= 0) && (index < attributes.size()))
	    return attributes.get( index ) ;
	else
	    throw new IllegalArgumentException() ;
    }

    public static Set<Attribute<?>> getAttributes( AttributedObject node ) {
	List<Object> attrs = node.attributes() ;
	Set<Attribute<?>> result = new HashSet<Attribute<?>>() ;

	if (attrs == null)
	    return result ;

	for (int ctr=0; ctr<attrs.size(); ctr++) {
	    Object value = attrs.get(ctr) ;
	    if (value != null) {
		result.add( attributes.get(ctr) ) ;
	    }
	}

	return result ;
    }

    private String name ;
    private NullaryFunction<T> initializer ;
    private T defaultValue ;
    private Class<T> cls ;
    private int attributeIndex ;

    public String toString() {
	return "Attribute[" + name + ":" + cls.getName() + ":" + 
	    attributeIndex + "]" ;
    }

    public Attribute( Class<T> cls, String name, T defaultValue ) {
	this.cls = cls ;
	this.name = name ;
	this.initializer = null ;
	this.defaultValue = defaultValue ;

	attributeIndex = next( this ) ;
    }

    public Attribute( Class<T> cls, String name, 
	NullaryFunction<T> initializer ) {
	this.cls = cls ;
	this.name = name ;
	this.initializer = initializer ;
	this.defaultValue = null ;

	attributeIndex = next( this ) ;
    }

    public T get( AttributedObject node ) {
	T result = cls.cast( node.get( attributeIndex ) ) ;
	if (result == null) {
	    if (initializer != null)
		result = initializer.evaluate() ;
	    else
		result = defaultValue ;

	    node.set( attributeIndex, result ) ;
	}
	return result ;
    }

    public void set( AttributedObject node, T arg ) {
	node.set( attributeIndex, arg ) ;
    }

    public boolean isSet( AttributedObject node ) {
	return node.get( attributeIndex ) != null ;
    }

    public String name() {
	return name ;
    }

    public int index() {
	return attributeIndex ;
    }

    public Class<?> type() {
	return cls ;
    }
}
