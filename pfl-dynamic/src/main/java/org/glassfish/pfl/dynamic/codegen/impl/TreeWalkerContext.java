/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import java.util.List ;
import org.glassfish.pfl.basic.contain.MarkStack;

public class TreeWalkerContext {
    private MarkStack<Visitor> visitors = new MarkStack<Visitor>() ;

    public Visitor current() {
	return visitors.peek() ;
    }

    public void push( Visitor visitor ) {
	visitors.push( visitor ) ;
    }

    public Visitor pop() {
	return visitors.pop() ;
    }

    public void mark() {
	visitors.mark() ;
    }

    public List<Visitor> popMark() {
	return visitors.popMark() ;
    }
}
