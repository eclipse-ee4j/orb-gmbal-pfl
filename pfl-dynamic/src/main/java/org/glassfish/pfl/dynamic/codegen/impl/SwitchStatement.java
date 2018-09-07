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

import org.glassfish.pfl.dynamic.codegen.impl.ExpressionInternal ;

import org.glassfish.pfl.dynamic.codegen.impl.StatementBase ;

/**
 *
 * @author Ken Cavanaugh
 */
public final class SwitchStatement extends StatementBase {
    // Note that this map must maintain insertion order!
    private Map<Integer,CaseBranch> cases ;
    private BlockStatement defaultCase ;
    private ExpressionInternal expr ;

    public Map<Integer,CaseBranch> cases() {
	return cases ;
    }

    public BlockStatement defaultCase() {
        return defaultCase ;
    }
    
    public ExpressionInternal expr() {
	return expr ;
    }

    SwitchStatement( Node parent, ExpressionInternal expr ) {
        super( parent ) ;
        this.expr = expr ;
        cases = new LinkedHashMap<Integer,CaseBranch>() ;
        defaultCase = new BlockStatement( this ) ;
    }
    
    public CaseBranch addCase( int value ) {
	if (cases.containsKey( value ))
	    throw new IllegalArgumentException( "Switch already contains case " +
		value ) ;

        CaseBranch stmt = new CaseBranch( this, value ) ;
        cases.put( value, stmt ) ;
        return stmt ;
    }
    
    public void accept( Visitor visitor ) {
        visitor.visitSwitchStatement( this ) ;
    }
}
