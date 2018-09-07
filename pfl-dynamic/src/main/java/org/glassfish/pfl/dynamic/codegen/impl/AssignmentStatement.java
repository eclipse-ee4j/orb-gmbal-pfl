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

public final class AssignmentStatement extends StatementBase {
    private ExpressionInternal left ;
    private ExpressionInternal right ;

    AssignmentStatement( Node parent, ExpressionInternal left, ExpressionInternal right ) {
	super( parent ) ;
	this.left = left ;
	this.right = right ;
    }

    public ExpressionInternal right() {
	return this.right ;
    }
	
    public ExpressionInternal left() {
	return this.left ;
    }

    public void accept( Visitor visitor ) {
	visitor.visitAssignmentStatement( this ) ;
    }
}

