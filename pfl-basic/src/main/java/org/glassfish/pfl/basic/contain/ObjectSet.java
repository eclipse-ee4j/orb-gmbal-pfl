/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glassfish.pfl.basic.contain;

import java.util.IdentityHashMap;
import java.util.Iterator;


/**
 *
 * @author ken
 */
public class ObjectSet {
    private IdentityHashMap map = new IdentityHashMap();
    private static Object VALUE = new Object() ;

    public boolean contains( Object obj ) {
        return map.get( obj ) == VALUE ;
    }

    public void add( Object obj ) {
        map.put( obj, VALUE ) ;
    }

    public void remove( Object obj ) {
        map.remove( obj ) ;
    }

    public Iterator iterator() {
        return map.keySet().iterator() ;
    }
}
