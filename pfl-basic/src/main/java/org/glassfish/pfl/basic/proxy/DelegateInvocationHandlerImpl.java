/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.proxy ;

import java.lang.reflect.Method ;
import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.InvocationTargetException ;

public abstract class DelegateInvocationHandlerImpl 
{
    private DelegateInvocationHandlerImpl() {}

    public static InvocationHandler create( final Object delegate )
    {
	SecurityManager s = System.getSecurityManager();
	if (s != null) {
 	    s.checkPermission(new DynamicAccessPermission("access"));
 	}
	return new InvocationHandler() {
            @Override
	    public Object invoke( Object proxy, Method method, Object[] args )
		throws Throwable
	    {
		// This throws an IllegalArgument exception if the delegate
		// is not assignable from method.getDeclaring class.
		try {
		    return method.invoke( delegate, args ) ;
		} catch (InvocationTargetException ite) {
		    // Propagate the underlying exception as the
		    // result of the invocation
		    throw ite.getCause() ;
		}
	    }
	} ;
    }
}
