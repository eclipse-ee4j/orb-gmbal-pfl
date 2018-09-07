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

import java.util.List ;

import org.glassfish.pfl.dynamic.codegen.impl.ExpressionInternal ;
import org.glassfish.pfl.dynamic.codegen.spi.Expression;
import org.glassfish.pfl.dynamic.codegen.spi.Type ;

public class FieldGenerator extends FieldInfoImpl implements Node {
    private Node nodeImpl ;
    
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
    // END of NodeBase delegation

    public FieldGenerator( ClassGeneratorImpl cinfo, int modifiers, Type type, String ident ) {
	super( cinfo, modifiers, type, ident ) ;
	nodeImpl = new NodeBase( cinfo ) ;
    }

    public Expression getExpression() {
	ClassGeneratorImpl cg = (ClassGeneratorImpl)parent() ;
	ExpressionFactory ef = new ExpressionFactory( cg ) ;
	if (Modifier.isStatic(modifiers())) {
	    return ef.fieldAccess( cg.thisType(), name() ) ;
	} else {
	    Expression target = ef._this() ;
	    return ef.fieldAccess( target, name() ) ;
	}
    }
    
    public void accept( Visitor visitor ) {
	visitor.visitFieldGenerator( this ) ;
    }
}

