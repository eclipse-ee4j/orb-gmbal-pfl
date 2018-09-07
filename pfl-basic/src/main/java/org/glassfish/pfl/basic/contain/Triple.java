/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain ;

/** Represents a Triple of values.  Used to return multiple values
 * and other similar uses.
 */
public class Triple<S,T,U> {
    private final Pair<S,Pair<T,U>> delegate ;

    public Triple( final S first, final T second, final U third ) {
        delegate = new Pair<S,Pair<T,U>>( first,
            new Pair<T,U>( second, third ) ) ;
    }

    public S first() {
        return delegate.first() ;
    }

    public T second() {
        return delegate.second().first() ;
    }

    public U third() {
        return delegate.second().second() ;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode() ;
    }

    @Override
    public boolean equals( Object obj ) {
        if (!(obj instanceof Triple)) {
            return false ;
        }

        Triple other = (Triple)obj ;

        return delegate.equals( other.delegate ) ;
    }
}
