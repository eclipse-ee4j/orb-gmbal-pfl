/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl;

import java.util.Set ;
import java.util.Map ;
import java.util.AbstractMap ;

/** A cache intended to help speed up access to a Map.
 * The idea is that some maps have a few values that are retrieved
 * more frequently than others.  So, we create a fixed size array
 * that holds keys and values, and do a very fast hash on the key's
 * identityHashCode.  The cache is backed by a map, which can be
 * an IdentityHashMap, or any other map (such as a WeakHashMap)
 * where the keys satisfy k1.equals(k2) implies k1 == k2.
 * Note that all put operations MUST go through this class, 
 * because calling put on the underlying map can result in
 * the cache returning incorrect results for get.
 */
public class FastCache<K,V> extends AbstractMap<K,V> {
    public static final int TABLE_SIZE = 256 ; // must be a power of 2

    private Map<K,V> map ;
    private Object[] keys = new Object[256] ;
    private Object[] values = new Object[256] ;

    private long cacheCount = 0 ;
    private long totalCount = 0 ;

    public long getCacheCount() {
	return cacheCount ;
    }

    public long getTotalCount() {
	return totalCount ;
    }

    public FastCache( Map<K,V> map ) {
	this.map = map ;
    }

    @Override
    public Set<Map.Entry<K,V>> entrySet() {
	return map.entrySet() ;
    }

    private int hash( Object key ) {
	// int hv = key.hashCode() ;
	int hv = System.identityHashCode( key ) ;
	return hv & (TABLE_SIZE-1) ;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get( Object key ) {
	totalCount++ ;
	int slot = hash( key ) ;
        @SuppressWarnings("unchecked")
	K ckey = (K)keys[slot] ;
	if (ckey == key ) {
	    cacheCount++ ;
	    return (V)values[slot] ;
	} else {
	    V result = map.get( key ) ;
	    keys[slot] = key ;
	    values[slot] = result ;
	    return result ;
	}
    }

    /** Put the key and value in the cache and the underlying
     * map.  This writes through to the map, rather than
     * first storing a value in the cache which is only
     * written as required, because that makes it easier
     * to preserve the correct behavior of the map.
     */
    @Override
    public V put( K key, V value ) {
	int slot = hash( key ) ;
	keys[slot] = key ;
	values[slot] = value ;
	return map.put( key, value ) ;
    }
}
