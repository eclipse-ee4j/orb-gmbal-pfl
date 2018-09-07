/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain ;

import java.util.EmptyStackException ;

// We implement a Stack here instead of using java.util.Stack because
// java.util.Stack is thread-safe, negatively impacting performance.
// We use an ArrayList instead since it is not thread-safe.  
// RequestInfoStack is used quite frequently.
public class StackImpl<E> {
    // The stack for RequestInfo objects.  
    private Object[] data = new Object[3] ;
    private int top = -1 ;

    // Tests if this stack is empty.
    public final boolean empty() {
	return top == -1;
    }

    // Looks at the object at the top of this stack without removing it
    // from the stack.
    @SuppressWarnings("unchecked")
    public final E peek() {
	if (empty()) {
            throw new EmptyStackException();
        }

	return (E)data[ top ];
    }

    // Removes the object at the top of this stack and returns that 
    // object as the value of this function.
    @SuppressWarnings("unchecked")
    public final E pop() {
	Object obj = peek() ;
	data[top] = null ;
	top-- ;
	return (E)obj;
    }

    private void ensure() 
    {
	if (top == (data.length-1)) {
	    int newSize = 2*data.length ;
	    Object[] newData = new Object[ newSize ] ;
	    System.arraycopy( data, 0, newData, 0, data.length ) ;
	    data = newData ;
	}
    }

    // Pushes an item onto the top of the stack
    public final Object push( E item ) {
	ensure() ;
	top++ ;
	data[top] = item;
	return item;
    }
}
