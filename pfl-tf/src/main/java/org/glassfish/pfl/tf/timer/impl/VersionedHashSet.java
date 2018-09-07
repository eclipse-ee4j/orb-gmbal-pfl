/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.impl ;

import java.util.Collection ;
import java.util.Iterator ;
import java.util.Set ;
import java.util.HashSet ;

/** This is an implementation of the Set interface that keeps track
 * of its version so that we can tell when it is modified.
 * Each time an element is added to or removed from the set, the
 * version is incremented.
 * This implementation is synchronized so that the version
 * is consistently updated whenever the set is modified.
 */
public class VersionedHashSet<E> extends HashSet<E> {
    private long version = 0 ;

    public synchronized long version() {
	return version ;
    }

    public VersionedHashSet() {
	super() ;
    }

    public VersionedHashSet( Collection<? extends E> c ) {
	super( c ) ;
    }

    public VersionedHashSet( int initialCapacity, float loadFactor ) {
	super( initialCapacity, loadFactor ) ;
    }

    public VersionedHashSet( int initialCapacity ) {
	super( initialCapacity ) ;
    }

    public synchronized boolean add( E e ) {
	boolean result = super.add( e ) ;
	if (result) 
	    version++ ;
	return result ;
    }

    public synchronized boolean remove( Object o ) {
	boolean result = super.remove( o ) ;
	if (result)
	    version++ ;
	return result ;
    }

    public Iterator<E> iterator() {
	final Iterator<E> state = super.iterator() ;

	return new Iterator<E>() {
	    public boolean hasNext() {
		return state.hasNext() ;
	    }

	    public E next() {
		return state.next() ;
	    }

	    public void remove() {
		synchronized (VersionedHashSet.this) {
		    state.remove() ;
		    version++ ;
		}
	    }
	} ;
    }
}

