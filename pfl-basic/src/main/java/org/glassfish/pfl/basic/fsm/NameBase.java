/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.fsm ;

import java.util.StringTokenizer ;

public class NameBase {
    private String name ;
    private String toStringName ;

    // Return just the name of the class, not the full qualified name.
    private String getClassName() 
    {
	String fqn = this.getClass().getName() ;
	StringTokenizer st = new StringTokenizer( fqn, "." ) ;
	String token = st.nextToken() ;
	while (st.hasMoreTokens()) {
            token = st.nextToken();
        }
	return token ;
    }

    private String getPreferredClassName()
    {
	if (this instanceof Action) {
            return "Action";
        }
	if (this instanceof State) {
            return "State";
        }
	if (this instanceof Guard) {
            return "Guard";
        }
	if (this instanceof Input) {
            return "Input";
        }
	return getClassName() ;
    }

    public NameBase( String name ) 
    { 
	this.name = name ;
	toStringName = getPreferredClassName() + "[" + name + "]" ;
    }

    public String getName() 
    {
	return name ;
    }

    @Override
    public String toString() {
	return toStringName ;
    }
} 

