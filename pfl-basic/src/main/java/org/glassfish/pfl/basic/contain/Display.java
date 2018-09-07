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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Supports a Display as typically found in Lexical scoping.
 * Upon entering a scope, a new environment is available for
 * binding keys to values.  Exiting a scope remove the top-most
 * environment.  Lookup searches from the top down to find the
 * value for the first matching key.
 *
 * @param <K> The type of the Key
 * @param <V> The type of the Value
 * @author ken
 */
public class Display<K,V> {
    private List<Map<K,V>> display = new ArrayList<Map<K,V>>() ;
    
    public void enterScope() {
        display.add( new HashMap<K,V>() ) ;
    }
    
    public void exitScope() {
        if (display.isEmpty()) {
            throw new IllegalStateException( "Display is empty" ) ;
        }
        
        display.remove( display.size() - 1 ) ;
    }
    
    public void bind( K key, V value ) {
        if (display.isEmpty()) {
            throw new IllegalStateException( "Display is empty" ) ;
        }
        
        display.get( display.size() - 1 ).put( key, value) ;
    }
    
    public void bind( Map<K,V> bindings ) {
        if (display.isEmpty()) {
            throw new IllegalStateException( "Display is empty" ) ;
        }
        
        display.get( display.size() - 1 ).putAll( bindings ) ;   
    }
    
    public V lookup( K key ) {
        V result = null ;
        for (int ctr=display.size()-1; ctr>=0; ctr-- ) {
            result = display.get( ctr ).get( key ) ;
            if (result != null) {
                break;
            }
        }
        
        return result ;
    }
}
