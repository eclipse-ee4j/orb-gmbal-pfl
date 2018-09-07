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

import org.glassfish.pfl.dynamic.codegen.spi.Expression;

public final class WhileStatement extends StatementBase {
    private BlockStatement body ;
    private Expression condition ;

    WhileStatement( Node parent, Expression condition ) {
	super( parent ) ;
	body = new BlockStatement( this ) ;
	this.condition = condition ;
    }

    public Expression condition() {
	return this.condition ;
    }
	
    public BlockStatement body() {
	return this.body ;
    }

    public void accept( Visitor visitor ) {
	visitor.visitWhileStatement( this ) ;
    }

}
