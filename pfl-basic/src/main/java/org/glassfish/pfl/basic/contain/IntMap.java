/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain ;

public interface IntMap<E> 
{
    /** If key {@literal >}= 0, return the value bound to key, or null if none.
     * Throws IllegalArgumentException if key {@literal <}0.
     */
    E get( int key ) ;

    /** If key {@literal >}= 0, bind value to the key.
     * Throws IllegalArgumentException if key {@literal <}0.
     */
    public void set( int key, E value ) ;
}

