/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.spi ;

import java.util.List ;
import org.glassfish.pfl.basic.contain.Pair;

public interface ImportList {
    /** Return a copy of this ImportList.
     */
    ImportList copy() ;

    /** Add a fully-qualified type name to the imports.
     * Returns the Type for the name.
     */
    public Type addImport( final String name ) ;

    public void addImport( final Type type ) ;

    /** Return whether or not this Type is in the imports.
     */
    public boolean contains( final String name ) ;

    public boolean contains( final Type type ) ;

    /** Lookup just the className, without package name.
     */
    public Type lookup( final String className ) ;

    /** Return a list of imports as (packageName,className) pairs.
     * The list is sorted lexicographically.
     */
    public List<Pair<String,String>> getInOrderList() ;
}
