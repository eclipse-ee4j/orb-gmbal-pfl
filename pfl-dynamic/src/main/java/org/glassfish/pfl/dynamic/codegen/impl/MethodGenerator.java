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
import java.util.Properties ;

import java.io.PrintStream ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;

import org.glassfish.pfl.dynamic.codegen.impl.NodeBase ;

public final class MethodGenerator extends MethodInfoBase implements Node {
    private Node nodeImpl ;
    private boolean isConstructor ;
    private boolean argsComplete ;
    private BlockStatement body ;

    public BlockStatement body() {
	return this.body ;
    }

    /** Construct a MethodGenerator that represents a constructor.
     */
    MethodGenerator( ClassGeneratorImpl parent, int modifiers,
	List<Type> exceptions ) {
	super( parent, modifiers ) ;
	nodeImpl = new NodeBase( parent ) ;

	this.isConstructor = true ;
	this.argsComplete = false ;
	this.exceptions.addAll( exceptions ) ;
	body = new BlockStatement( this ) ;
    }

    /** Construct a MethodGenerator that represents a method.
     */
    MethodGenerator( ClassGeneratorImpl parent, int modifiers, Type rtype, String name,
	List<Type> exceptions ) {
	super( parent, modifiers, rtype, name ) ;
	nodeImpl = new NodeBase( parent ) ;

	this.isConstructor = false ;
	this.exceptions.addAll( exceptions ) ;
	body = new BlockStatement( this ) ;
    }
    
    // All Node methods are delegated to nodeImpl.
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

    public Variable addArgument( Type type, String ident ) {
	if (argsComplete)
	    throw new IllegalStateException( 
		"An attempt was made to add an argument after argsComplete was called" ) ;

	Variable var = body.exprFactory().variable( type, ident ) ;

	synchronized (this) {
	    clearHashCode() ;
	    arguments.add( var ) ;
	}

	return var ;
    }

    public boolean isConstructor() {
	return this.isConstructor ;
    }

    public void argsComplete() {
	argsComplete = true ;
    }

    public int hashCode() {
	if (!argsComplete) 
	    throw new IllegalStateException(
		"Trying to call hashCode before argsComplete." ) ;

	return super.hashCode() ;
    }

    public void accept( Visitor visitor ) {
	visitor.visitMethodGenerator( this ) ;
    }
}
