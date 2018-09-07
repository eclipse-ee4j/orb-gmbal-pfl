/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.spi ;

import java.lang.reflect.Method ;

import org.glassfish.pfl.dynamic.codegen.impl.MethodInfoReflectiveImpl ;

public class Utility {
    private Utility() {}

    public static MethodInfo getMethodInfo( Method method ) {
	Class cls = method.getDeclaringClass() ;
	Type clsType = Type.type( cls ) ;
	ClassInfo cinfo = clsType.classInfo() ;
	MethodInfo result = new MethodInfoReflectiveImpl( cinfo, method ) ;
	return result ;
    }
}
