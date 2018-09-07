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

public final class BreakStatement extends StatementBase {
    BreakStatement( Node parent ) {
	super( parent ) ;
    }

    public void accept( Visitor visitor ) {
	visitor.visitBreakStatement( this ) ;
    }
}

