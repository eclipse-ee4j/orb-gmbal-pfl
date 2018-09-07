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

import java.util.Map ;
import java.util.LinkedHashMap ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;

import org.glassfish.pfl.dynamic.codegen.impl.StatementBase ;
import org.glassfish.pfl.basic.contain.Pair;

/**
 *
 * @author Ken Cavanaugh
 */
public final class TryStatement extends StatementBase {
    private BlockStatement bodyPart ;
    private BlockStatement finalPart ;
    private Map<Type,Pair<Variable,BlockStatement>> catches ;

    TryStatement( Node parent ) {
	super( parent ) ;
	bodyPart = new BlockStatement( this ) ;
	finalPart = new BlockStatement( this ) ;
	catches = new LinkedHashMap<Type,Pair<Variable,BlockStatement>>() ;
    }

    public BlockStatement bodyPart() {
	return this.bodyPart ;
    }
    
    public BlockStatement finalPart() {
	return this.finalPart ;
    }
    
    public Map<Type,Pair<Variable,BlockStatement>> catches() {
	return this.catches ;
    }

    /** Add a new Catch block to this try statement.  type must be
     * a non-primitive, non-array type, and may occur only once per
     * try statement.
     */
    public Pair<Variable,BlockStatement> addCatch( Type type, String ident ) {
	if (type.isPrimitive())
	    throw new IllegalArgumentException( "Primitive type " + type +
		" not allowed in catch block" ) ;

	if (type.isArray())
	    throw new IllegalArgumentException( "Array type " + type +
		" not allowed in catch block" ) ;

	if (catches.containsKey( type )) 
	    throw new IllegalArgumentException( "Type " + type + 
		" is already used as a catch block in this try statement" ) ;

	// XXX iterate over list and make sure that type is not a subclass of
	// any previous types in the list
	
	BlockStatement stmt = new BlockStatement( this ) ;
	Variable var = stmt.exprFactory().variable( type, ident ) ;
	Pair<Variable,BlockStatement> result = new Pair<Variable,BlockStatement>(
	    var, stmt ) ;
	catches.put( type, result ) ;
	return result ;
    }
	
    public void accept( Visitor visitor ) {
	visitor.visitTryStatement( this ) ;
    }
}
