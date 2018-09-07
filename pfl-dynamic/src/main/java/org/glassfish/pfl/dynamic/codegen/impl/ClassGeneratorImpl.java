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

import java.util.Set ;
import java.util.HashSet ;
import java.util.List ;
import java.util.ArrayList ;

import org.glassfish.pfl.dynamic.codegen.spi.ClassGenerator ;
import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.MethodInfo ;

import static java.lang.reflect.Modifier.* ;

/** Class used to generate a description of a class or interface.
 * An interface is an abstract class, all of whose methods are 
 * abstract.  Interfaces do not have a super class, an initializer,
 * or constructors.  Interfaces also do not have variables.
 * <p>
 * Note: the hashCode of a ClassGeneratorImpl changes whenever a
 * method, constructor, or field is added, so do not put
 * ClassGenerators into sets or maps unless they are fully
 * populated.
 */
public final class ClassGeneratorImpl extends ClassInfoBase 
    implements ClassGenerator, Node {

    private Node nodeImpl ;
    private BlockStatement initializer ;
    private List<MethodGenerator> methods ;
    private List<MethodGenerator> constructors ;
    private List<FieldGenerator> fields ;

    /** Construct a ClassGeneratorImpl representing an interface.
     */
    ClassGeneratorImpl( int modifiers, String name, List<Type> impls )  {
	// Note that all interfaces must have the ABSTRACT and INTERFACE 
	// modifiers.
	super( modifiers | ABSTRACT | INTERFACE, Type._class(name) ) ;

	nodeImpl = new NodeBase( null ) ; 
	initializeInterface( impls ) ;

	initializer = null ;
	methods = new ArrayList<MethodGenerator>() ;
	constructors = null ;
	fields = null ;
    }

    /** Construct a ClassGeneratorImpl representing a class.
     */
    ClassGeneratorImpl( int modifiers, String name, Type superType,
	List<Type> impls ) {
	super( modifiers, Type._class( name ) ) ;
	nodeImpl = new NodeBase( null ) ; 

	// We need the Type._class( name ) form of the class
	// type in order for Type._classGenerator to function
	// correctly.  Later we will need the _classGenerator form
	// to avoid attempts to load the class for the class that
	// has not yet been completely generated, so we override
	// the value of thisType here.
	initializeClass( Type._classGenerator( this ), superType, impls ) ;

	initializer = new BlockStatement( this ) ;
	methods = new ArrayList<MethodGenerator>() ;
	constructors = new ArrayList<MethodGenerator>() ;
	fields = new ArrayList<FieldGenerator>() ;
    }

    // All node methods are delegated to nodeImpl.
    public Node parent() {
	return nodeImpl.parent() ;
    }

    public int id() {
	return nodeImpl.id() ;
    }

    public void parent( Node node ) {
	nodeImpl.parent( node ) ;
    }

    public <T extends Node> T getAncestor( Class<T> type ) {
	return nodeImpl.getAncestor( type ) ;
    }

    public <T extends Node> T copy( Class<T> cls ) {
	return nodeImpl.copy( cls ) ;
    }

    public <T extends Node> T copy( Node newParent, Class<T> cls ) {
	return nodeImpl.copy( newParent, cls ) ;
    }
    
    public Object get( int index ) {
	return nodeImpl.get( index ) ;
    }

    public void set( int index, Object obj ) {
	nodeImpl.set( index, obj ) ;
    }

    public List<Object> attributes() {
	return nodeImpl.attributes() ;
    }
    // End of delegation

    public BlockStatement initializer() {
	if (isInterface())
	    throw new IllegalStateException( 
		"An Interface does not have an initializer" ) ;
	return initializer ;
    }

    public List<FieldGenerator> fields() {
	return fields ;
    }

    public List<MethodGenerator> methods() {
	return methods ;
    }

    public List<MethodGenerator> constructors() {
	if (isInterface())
	    throw new IllegalStateException( 
		"An Interface does not have constructors" ) ;
	return constructors ;
    }

    public Set<MethodInfo> constructorInfo() {
	return new HashSet<MethodInfo>( constructors ) ;
    }

    // Every method must be added to methodInfoByName (defined in ClassInfoBase)
    // AFTER it is completed.  This cannot be done here in startMethod, so
    // we do it in methodComplete.
    public MethodGenerator startMethod( int modifiers, Type rtype, String name, 
	List<Type> exceptions ) {
	if (isInterface() && !isAbstract(modifiers))
	    throw new IllegalArgumentException(
		"All methods in an interface must be abstract" ) ;

	MethodGenerator result = new MethodGenerator( this, modifiers, rtype, 
	    name, exceptions ) ;

	return result ;
    }

    // Since methods and constructors are handled largely the same way, we
    // have a startConstructor method that requires a call to methodComplete
    // after the constructor has been defined.
    public MethodGenerator startConstructor( int modifiers, 
	List<Type> exceptions ) {

	if (isInterface()) 
	    throw new IllegalStateException(
		"Interfaces may not define constructors" ) ;

	MethodGenerator result = new MethodGenerator( this, modifiers,
	    exceptions ) ;

	return result ;
    }

    public void methodComplete( MethodGenerator mg ) {
	mg.argsComplete() ;

	if (mg.isConstructor()) {
	    constructors.add( mg ) ;
	    addConstructorInfo( mg ) ;
	} else {
	    // Add method to the list of MethodGenerators maintained
	    // in the ClassGeneratorImpl API (not the same as
	    // methodInfoByName).
	    methods.add( mg ) ;

	    // Add method to methodInfoByName in ClassInfoBase
	    // after the method has been defined.
	    // This is required so that the hashCode value of 
	    // the MethodGenerator does not
	    // change after the MethodGenerator is added to the
	    // Set<MethodInfo> in methodInfoByName.
	    addMethodInfo( mg ) ;
	}
    }

    public FieldGenerator addField( int modifiers, Type type, String name ) {
	if (isInterface())
	    throw new IllegalStateException(
		"Interfaces may not contain data members" ) ;

	if (fieldInfo().keySet().contains( name ))
	    throw new IllegalArgumentException( "Fields for class " + name +
		" already contains field " + name ) ;

	FieldGenerator var = new FieldGenerator( this, modifiers, type, name ) ;

	fields.add( var ) ;
	addFieldInfo( var ) ;

	return var ;
    }

    public void accept( Visitor visitor ) {
	visitor.visitClassGenerator( this ) ;
    }
}
