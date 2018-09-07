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

import org.glassfish.pfl.dynamic.codegen.impl.StatementBase ;

import org.glassfish.pfl.dynamic.codegen.impl.ExpressionInternal ;

public final class ThrowStatement extends StatementBase {
    private ExpressionInternal expr ;

    ThrowStatement( Node parent, ExpressionInternal expr ) {
	super( parent ) ;
	this.expr = expr ;
    }

    public ThrowStatement( Node parent ) {
	this( parent, null ) ;
    }

    public ExpressionInternal expr() {
	return expr ;
    }
   
    public void accept( Visitor visitor ) {
	visitor.visitThrowStatement( this ) ;
    }
}

