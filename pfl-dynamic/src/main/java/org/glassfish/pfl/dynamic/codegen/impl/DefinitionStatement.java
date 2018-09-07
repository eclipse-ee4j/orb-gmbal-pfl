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

import org.glassfish.pfl.dynamic.codegen.spi.Expression ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;

public final class DefinitionStatement extends StatementBase {
    private Variable var ;
    private Expression expr ;

    DefinitionStatement( Node parent, Variable var, Expression expr ) {
	super( parent ) ;
	this.var = var ;
	this.expr = expr ;
    }

    public Variable var() {
	return this.var ;
    }

    public Expression expr() {
	return this.expr ;
    }

    @Override
    public void accept( Visitor visitor ) {
	visitor.visitDefinitionStatement( this ) ;
    }
}
