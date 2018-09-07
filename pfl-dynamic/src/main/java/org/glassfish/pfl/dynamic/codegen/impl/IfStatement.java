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

/**
 *
 * @author Ken Cavanaugh
 */
public final class IfStatement extends StatementBase {
    private Expression condition ;
    private BlockStatement truePart ;
    private BlockStatement falsePart ;

    IfStatement( Node parent, Expression expr ) {
	super( parent ) ;
	condition = expr ;
	truePart = new BlockStatement( this ) ;
	falsePart = new BlockStatement( this ) ;
    }

    public Expression condition() {
	return condition ;
    }
	
    public BlockStatement truePart() {
	return this.truePart ;
    }
    
    public BlockStatement falsePart() {
	return this.falsePart ;
    }

    public void accept( Visitor visitor ) {
	visitor.visitIfStatement( this ) ;
    }
}
