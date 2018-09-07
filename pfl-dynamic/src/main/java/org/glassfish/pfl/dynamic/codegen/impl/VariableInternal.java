/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import org.glassfish.pfl.dynamic.codegen.spi.Variable;

/**
 *
 * @author ken
 */
public interface VariableInternal extends ExpressionInternal, Variable {
    /** Returns true if this variable is still in scope.
     * Only variables still in scope may be referenced in
     * expressions.
     */
    boolean isAvailable() ;

    /** Mark the variable so that it is no longer in scope.
     */
    void close() ;
}
