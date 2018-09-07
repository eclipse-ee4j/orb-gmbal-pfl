/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.spi;

public interface Variable extends Expression {
    /** Return the name of this variable.
     */
    String ident() ;

    /** Return the type of this variable.
     *
     * @return The variable type.
     */
    Type type() ;
}
