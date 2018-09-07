/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen;

/** Provides interface for code generators that either 
 * generate Java source and compile it, or generate bytecode
 * directly.
 */
public interface SimpleCodeGenerator {
    /** Generate a class in the specified ClassLoader.
     */
    Class<?> generate( ClassLoader loader ) ;

    /** Returns the fully qualified class name of the generated class.
     */
    String className() ;

    /** Gather all timing information and report it.  This
     * method looks for methods that follow the pattern
     * <code>
     * long xxxTime()
     * </code>
     * invokes them, and displays the results.
     */
    void reportTimes() ;
}

