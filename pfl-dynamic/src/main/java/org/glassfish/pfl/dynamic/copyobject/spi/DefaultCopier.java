/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.spi ;


import org.glassfish.pfl.dynamic.copyobject.impl.Exceptions;
import org.glassfish.pfl.dynamic.copyobject.impl.ObjectCopierImpl ;
import org.glassfish.pfl.dynamic.copyobject.impl.JavaStreamObjectCopierImpl ;
import org.glassfish.pfl.dynamic.copyobject.impl.FallbackObjectCopierImpl ;

public class DefaultCopier {
    private DefaultCopier() {}

    public static Object copy( Object obj ) {
	ObjectCopier c1 = new ObjectCopierImpl() ;
	ObjectCopier c2 = new JavaStreamObjectCopierImpl() ;
	ObjectCopier copier = new FallbackObjectCopierImpl( c1, c2 ) ;

	try {
	    return copier.copy( obj ) ;
	} catch (ReflectiveCopyException exc) {
            throw Exceptions.self.couldNotCopy( obj, exc ) ;
	}
    }

    public static <T> T copy( T obj, Class<T> cls ) {
	return cls.cast( copy( obj ) ) ;
    }
}
