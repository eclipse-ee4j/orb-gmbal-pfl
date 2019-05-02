/*
 * Copyright (c) 2003, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain ;

/** A utilitiy class representing a generic types Pair of elements.
 * Useful for simple data structures, returning multiple values, etc.
 * {@code Pair<Object,Object>} is similar to a cons cell.
 */
public class Pair<S,T> {
    protected S _first ;
    protected T _second ;

    public Pair( final S first, final T second ) {
	_first = first ;
	_second = second ;
    }

    public Pair( final S first ) {
	this( first, null ) ;
    }

    public Pair() {
	this( null ) ;
    }

    public S first() {
	return _first ;
    }

    public T second() {
	return _second ;
    }

    @Override
    public boolean equals( Object obj ) {
	if (obj == this) {
	    return true ;
        }

	if (!(obj instanceof Pair)) {
	    return false ;
        }

        @SuppressWarnings("unchecked")
	Pair<S,T> pair = (Pair<S,T>)obj ;

	if (first() == null ? 
	    pair.first() == null : first().equals( pair.first())) {
	    return (second() == null ? 
		pair.second() == null : second().equals( pair.second())) ;
	} else {
	    return false ;
	}
    }

    @Override
    public int hashCode() {
	int result = 0 ;
	if (_first != null) {
            result ^= _first.hashCode();
        }
	if (_second != null) {
            result ^= _second.hashCode();
        }

	return result ;
    }

    @Override
    public String toString() {
	return "Pair[" + _first + "," + _second + "]" ;
    }
}
