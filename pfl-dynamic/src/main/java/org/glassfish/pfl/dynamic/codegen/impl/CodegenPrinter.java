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

import java.io.PrintStream ;
import org.glassfish.pfl.basic.algorithm.Printer;

/** Extends the file utility Printer with line numbers that are
 * also optionally stored as Attributes in Nodes for annotating the AST.
 */
public class CodegenPrinter extends Printer {
    static Attribute<Integer> lineNumberAttribute = new Attribute<Integer>( 
	Integer.class, "lineNumber", -1 ) ;

    private int lineNumber ;

    public CodegenPrinter( PrintStream ps ) {
	this( ps, DEFAULT_INCREMENT, ' ' ) ;
    }

    public CodegenPrinter( PrintStream ps, int increment, char padChar ) {
	super( ps, increment, padChar ) ;
	this.lineNumber = 1 ;
    }

    public int lineNumber() {
	return lineNumber ;
    }

    @Override
    public CodegenPrinter p( String str ) {
	super.p( str ) ;
	return this ;
    }

    @Override
    public CodegenPrinter p( Object obj ) {
	super.p( obj ) ;
	return this ;
    }

    @Override
    public CodegenPrinter in() {
	super.in() ;
	return this ;
    }

    @Override
    public CodegenPrinter out() {
	super.out() ;
	return this ;
    }

    @Override
    public CodegenPrinter nl() {
	super.nl() ;
	return this ;
    }

    public CodegenPrinter nl( Node node ) {
	lineNumber++ ;
	if (node != null)
	    lineNumberAttribute.set( node, lineNumber ) ;
	super.nl() ;
	return this ;
    }
}

