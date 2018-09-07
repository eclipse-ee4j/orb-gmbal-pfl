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

import java.io.Serializable;
import java.util.Comparator ;

public class NaturalComparator<T> implements Serializable, Comparator<T> {
    private static final long serialVersionUID = -6702229623606444679L;

    @SuppressWarnings("unchecked")
    @Override
    public int compare( T obj1, T obj2 )
    {
	return ((Comparable<T>)obj1).compareTo( obj2 ) ;
    }
}
