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

import org.glassfish.pfl.dynamic.copyobject.spi.LibraryClassLoader ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;

/** Class used to set and get the global class loader used
 * by the codegen library.  This is maintained in a ThreadLocal
 * to avoid concurrency problems.  All processing in the
 * codegen library takes place in the same thread in any case.
 */
public class CurrentClassLoader {
    private static ThreadLocal<ClassLoader> current = 
	new ThreadLocal() {
	    protected ClassLoader initialValue() {
		return LibraryClassLoader.getClassLoader() ;
	    }
	} ;

    public static ClassLoader get() {
	return current.get() ;
    }

    public static void set( ClassLoader cl ) {
	if (cl == null)
	    cl = LibraryClassLoader.getClassLoader() ;
	current.set( cl ) ;

	// This is essential for proper operation of codegen when multiple
	// ClassLoaders are in use.  The problem is that Type maintains a cache
	// the maps class names to Types.  The same class name may of course refer
	// to different classes in different ClassLoaders.  The Type interface
	// supports access to ClassInfo, which in the case of a Type for a loaded
	// class has a reflective implementation that includes all method and field
	// information from the class.  Thus we can have the situation where the class
	// name is mapped to a Type with ClassInfo from the wrong ClassLoader!
	// See bug 6562360 and GlassFish issue 3134 for the app server impact of getting
	// this wrong.
	Type.clearCaches() ;
    }
}
