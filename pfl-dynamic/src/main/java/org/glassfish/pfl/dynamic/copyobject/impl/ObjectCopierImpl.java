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

import java.util.IdentityHashMap ;
import java.util.Map ;

import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopier ;
import org.glassfish.pfl.dynamic.copyobject.spi.Immutable ;
import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;


/** Class used to deep copy arbitrary data.  A single 
 * ObjectCopierImpl
 * instance will preserve all object aliasing across multiple calls
 * to copy.
 */
public class ObjectCopierImpl implements ObjectCopier {    
    // It is very important that ccf be static.  This means that
    // ccf is shared across all instances of the object copier,
    // so that any class is analyzed only once, instead of once per 
    // copier instance.  This is worth probably 20%+ in microbenchmark 
    // performance.
    private static PipelineClassCopierFactory ccf = 
	DefaultClassCopierFactories.getPipelineClassCopierFactory() ; 
   
    static {
	ccf.setSpecialClassCopierFactory(
	    new ClassCopierFactory() {
            @Override
		public ClassCopier getClassCopier( Class<?> cls )
		    throws ReflectiveCopyException 
		{
		    if (cls.isAnnotationPresent( Immutable.class )) {
                        return DefaultClassCopiers.getIdentityClassCopier();
                    } else {
                        return null;
                    }
		}
	    }
	) ;
    }

    private Map<Object,Object> oldToNew ;

    public ObjectCopierImpl()
    {
	if (DefaultClassCopierFactories.USE_FAST_CACHE) {
            oldToNew =
                new FastCache<Object,Object>(
                    new IdentityHashMap<Object,Object>());
        } else {
            oldToNew = new IdentityHashMap<Object,Object>();
        }
    }

    /** Return a deep copy of obj.  Aliasing is preserved within
     * obj and between objects passed in multiple calls to the
     * same instance of ReflectObjectCopierImpl.
     */
    @Override
    public Object copy( Object obj ) throws ReflectiveCopyException
    {
	if (obj == null) {
            return null;
        }

	Class<?> cls = obj.getClass() ;
	ClassCopier copier = ccf.getClassCopier( cls ) ;

	return copier.copy( oldToNew, obj ) ;
    }
}
