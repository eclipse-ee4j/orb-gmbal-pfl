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

/** Represents a generated AST, which can be used to generate code.
 *
 * XXX Should we add methods to this interface that are similar to the methods
 * on Wrapper like _generate?
 *
 * @author ken
 */
public interface ClassGenerator extends ClassInfo {
    String name() ;
}
