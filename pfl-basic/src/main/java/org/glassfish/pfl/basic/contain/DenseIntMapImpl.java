/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain ;

import java.util.ArrayList ;

/** Utility for managing mappings from densely allocated integer
 * keys to arbitrary objects.  This should only be used for
 * keys in the range 0..max such that "most" of the key space is actually
 * used.
 */
public class DenseIntMapImpl<E> implements IntMap<E>
{
    private ArrayList<E> list = new ArrayList<E>() ;

    private void checkKey( int key ) 
    {
	if (key < 0)
	    throw new IllegalArgumentException( "Key must be >= 0." ) ;
    }

    /** If key >= 0, return the value bound to key, or null if none.
     * Throws IllegalArgumentException if key <0.
     */
    public E get( int key ) 
    {
	checkKey( key ) ;

	E result = null ;
	if (key < list.size())
	    result = list.get( key ) ;

	return result ;
    }

    /** If key >= 0, bind value to the key.
     * Throws IllegalArgumentException if key <0.
     */
    public void set( int key, E value ) 
    {
	checkKey( key ) ;
	extend( key ) ;
	list.set( key, value ) ;
    }

    private void extend( int index )
    {
	if (index >= list.size()) {
	    list.ensureCapacity( index + 1 ) ;
	    int max = list.size() ;
	    while (max++ <= index)
		list.add( null ) ;
	}
    }
}
