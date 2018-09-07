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

/** Simple marker interface for an Expression.  Expressions can be manipulated
 * only through the Wrapper interface.
 *
 * XXX could consider extending this for convenience, e.g.
 * exp1.or( exp2 ) instead of _or( exp1, exp2 ).
 *
 * @author ken
 */
public interface Expression {
}
