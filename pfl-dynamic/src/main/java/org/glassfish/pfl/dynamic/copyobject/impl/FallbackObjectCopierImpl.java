/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl ;

import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopier ;
import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;
import org.glassfish.pfl.basic.contain.Pair;

/** Trys a first ObjectCopier.  If the first throws a ReflectiveCopyException,
 * falls back and tries a second ObjectCopier.
 */
public class FallbackObjectCopierImpl extends Pair<ObjectCopier,ObjectCopier>
    implements ObjectCopier {

    public FallbackObjectCopierImpl( ObjectCopier first, ObjectCopier second ) {
	super( first, second ) ;
    }

    @Override
    public Object copy( Object src ) throws ReflectiveCopyException {

	try {
	    return first().copy( src ) ;
	} catch (ReflectiveCopyException rce ) {
            Exceptions.self.failureInFallback( rce, src, src.getClass() ) ;
	    return second().copy( src ) ;
	}
    }
}
