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
import org.glassfish.pfl.dynamic.codegen.spi.Type ;

/**
 *
 * @author ken
 */
public interface ExpressionInternal extends Expression, Statement {
    // Return true iff this expression represents a variable,
    // data member, or array index operation that may appear
    // on the left side of an assignment.
    boolean isAssignable() ;

    // Return the type of this expression.
    Type type() ;
}
