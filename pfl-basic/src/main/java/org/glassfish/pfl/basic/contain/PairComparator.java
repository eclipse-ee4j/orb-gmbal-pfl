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

import java.util.Comparator ;

public class PairComparator<S,T> implements Comparator<Pair<S,T>>
{
    private Comparator<? super S> sc ;
    private Comparator<? super T> tc ;

    public PairComparator( Comparator<? super S> sc, Comparator<? super T> tc )
    {
	if ((sc == null) || (tc == null)) {
            throw new IllegalArgumentException();
        }

	this.sc = sc ;
	this.tc = tc ;
    }

    @Override
    public int compare( Pair<S,T> o1, Pair<S,T> o2 )
    {
	int res = sc.compare( o1.first(), o2.first() ) ;
	if (res == 0) {
            return tc.compare(o1.second(), o2.second());
        } else {
            return res;
        }
    }

    @Override
    public boolean equals( Object obj ) 
    {
	if (!(obj instanceof PairComparator)) {
            return false;
        }

	if (obj == this) {
            return true;
        }

        @SuppressWarnings("unchecked")
	PairComparator<S,T> other = (PairComparator<S,T>)obj ;
	return other.sc.equals( sc ) && other.tc.equals( tc ) ;
    }

    @Override
    public int hashCode() {
	return sc.hashCode() ^ tc.hashCode() ;
    }

    @Override
    public String toString() {
	return "PairComparator[" + sc + "," + tc + "]" ;
    }
}
