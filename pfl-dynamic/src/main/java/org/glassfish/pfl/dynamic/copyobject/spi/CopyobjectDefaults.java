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

import org.glassfish.pfl.dynamic.copyobject.impl.FallbackObjectCopierImpl;
import org.glassfish.pfl.dynamic.copyobject.impl.JavaStreamObjectCopierImpl;
import org.glassfish.pfl.dynamic.copyobject.impl.ObjectCopierImpl;

public abstract class CopyobjectDefaults
{
    private CopyobjectDefaults() { }

    private static final ObjectCopier javaStream =
        new JavaStreamObjectCopierImpl() ;

    public static ObjectCopierFactory makeJavaStreamObjectCopierFactory( ) 
    {
	return new ObjectCopierFactory() {
	    public ObjectCopier make( )
	    {
		return javaStream ;
	    }
	} ;
    }

    private static final ObjectCopier referenceObjectCopier = new ObjectCopier() {
        @Override
        public Object copy(Object obj) throws ReflectiveCopyException {
            return obj ;
        }
    };

    private static ObjectCopierFactory referenceObjectCopierFactory = 
	new ObjectCopierFactory() {
	    public ObjectCopier make() 
	    {
		return referenceObjectCopier ;
	    }
	} ;

    /** Obtain the reference object "copier".  This does no copies: it just
     * returns whatever is passed to it.
     */
    public static ObjectCopierFactory getReferenceObjectCopierFactory()
    {
	return referenceObjectCopierFactory ;
    }

    /** Create a fallback copier factory from the two ObjectCopierFactory
     * arguments.  This copier makes an ObjectCopierFactory that creates
     * instances of a fallback copier that first tries an ObjectCopier
     * created from f1, then tries one created from f2, if the first
     * throws a ReflectiveCopyException.
     */
    public static ObjectCopierFactory makeFallbackObjectCopierFactory( 
	final ObjectCopierFactory f1, final ObjectCopierFactory f2 )
    {
	return new ObjectCopierFactory() {
            @Override
	    public ObjectCopier make() 
	    {
		ObjectCopier c1 = f1.make() ;
		ObjectCopier c2 = f2.make() ;
		return new FallbackObjectCopierImpl( c1, c2 ) ;
	    }
	} ;
    }

    /** Obtain the new reflective copier factory.  This is 3-4 times faster than the stream
     * copier, and about 10% faster than the old reflective copier.  It should
     * normally be used with a fallback copier, as there are some classes that simply
     * cannot be copied reflectively.
     */
    public static ObjectCopierFactory makeReflectObjectCopierFactory( ) 
    {
	return new ObjectCopierFactory() {
            @Override
	    public ObjectCopier make( )
	    {
		return new ObjectCopierImpl( ) ;
	    }
	} ;
    }
}
