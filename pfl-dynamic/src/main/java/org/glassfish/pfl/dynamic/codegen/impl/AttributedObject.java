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

import java.util.List ;

/** The interface that an Object must implement in order to support
 * Attributes.  The get and set methods are only for use by the
 * Attribute class.
 */
public interface AttributedObject {
    /** Internal method for dynamic attribute support.
     * Return the value of the attribute at index.  If
     * the attribute at index is not set, set it to the
     * default value and return the default.
     */
    Object get( int index ) ;

    /** Internal method for dynamic attribute support.
     * Set the attribute at index to obj.
     */
    void set( int index, Object obj ) ;

    /** Internal method for dynamic attribute support.
     * Return all attributes for this node (may be null).
     */
    List<Object> attributes() ;
}
