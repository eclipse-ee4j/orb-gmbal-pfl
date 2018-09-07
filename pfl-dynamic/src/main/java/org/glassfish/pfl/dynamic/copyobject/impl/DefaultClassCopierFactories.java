/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl;

import java.lang.reflect.Method ;
import java.lang.reflect.Modifier ;

import java.util.Map ;

import java.security.PrivilegedAction;
import java.security.AccessController;

import org.glassfish.pfl.basic.concurrent.WeakHashMapSafeReadLock;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

public abstract class DefaultClassCopierFactories
{
    // Note that the FastCache is NOT a weak or soft
    // reference based cache, so it can hold onto 
    // references to classes indefinitely, pinning ClassLoader
    // instances in memory.  Until this is fixed, FastCache is
    // for testing only.
    public static final boolean USE_FAST_CACHE = false ;

    private DefaultClassCopierFactories() {}

    /** Create a ClassCopierFactory that handles arrays.  This 
     * ClassCopierFactory will return null on a get call if the 
     * class is not an array.
     */
    public static ClassCopierFactory makeArrayClassCopierFactory( 
	ClassCopierFactory ccf ) 
    {
	return new ClassCopierFactoryArrayImpl( ccf ) ;
    }

    private static final Class<?>[] SAFE_TO_COPY = new Class<?>[] {
	java.util.TimeZone.class,
	java.lang.Throwable.class,
	java.lang.reflect.Proxy.class
    } ;

    public static ClassCopierFactory makeOrdinaryClassCopierFactory( 
	final PipelineClassCopierFactory ccf )
    {
	return new ClassCopierFactory() {
            @Override
	    public ClassCopier getClassCopier( Class<?> cls ) 
                throws ReflectiveCopyException {

		if (notCopyable( cls )) {
		    return DefaultClassCopiers.getErrorClassCopier() ;
		} else {
		    return new ClassCopierOrdinaryImpl( ccf, cls ) ;
		}
	    }

	    // Returns true if class is (specially) known to be safe
	    // to copy, even if notCopyable(cls) is true.
	    private boolean safe( Class cls ) 
	    {
		for (Class klass : SAFE_TO_COPY) {
		    if (cls == klass) {
			return true ;
		    }
		}

		return false ;
	    }

	    // Scan the methods in cls and all its superclasses (except 
	    // Object!) to see if finalize is defined, or if there are
	    // any native methods.  Classes with such methods are not copyable,
	    // except for a few known classes that ARE safe to copy,
	    // and cause this method to return true.  If there are not 
	    // problematic methods, return false and allow the copy
	    // (at this level: references to non-copyable objects will
	    // cause ReflectiveCopyException to be thrown where ever they
	    // occur).
	    private boolean notCopyable( Class<?> cls ) {
		Class<?> current = cls ;
                Method[] methods ;
		while (current != Object.class) {
		    if (safe(current)) {
                        return false;
                    }
                    // Fix GLASSFISH-18310
                    if (System.getSecurityManager() == null) {
                        methods = current.getDeclaredMethods();
                    } else {
                        final Class<?> _current = current;
                        methods = (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
                            public Object run() {
                                return _current.getDeclaredMethods();
                            }
                        });
                    }
                    for (Method m : methods) {
                        if ((m.getName().equals("finalize"))
                                || Modifier.isNative(m.getModifiers())) {
                            return true;
                        }
                    }                  

		    current = current.getSuperclass() ;
		}

		return false ;
	    }
	} ;
    }

    public static CachingClassCopierFactory makeCachingClassCopierFactory( )
    {
	return new CachingClassCopierFactory() 
	{
	    private Map<Class<?>,ClassCopier> cache = USE_FAST_CACHE ?
		new FastCache<Class<?>,ClassCopier>(
                    new WeakHashMapSafeReadLock<Class<?>,ClassCopier>() ) :
		new WeakHashMapSafeReadLock<Class<?>,ClassCopier>() ;

            @Override
	    public void put( Class<?> cls, ClassCopier copier )
	    {
		cache.put( cls, copier ) ;
	    }

            @Override
	    public ClassCopier getClassCopier( Class<?> cls )
	    {
		return cache.get(cls) ;
	    }
	};
    }
    
    public static ClassCopierFactory getNullClassCopierFactory()
    {
	return new ClassCopierFactory()
	{
            @Override
	    public ClassCopier getClassCopier( Class cls ) 
	    {
		return null ;
	    }
	} ;
    }

    public static PipelineClassCopierFactory getPipelineClassCopierFactory()
    {
	return new ClassCopierFactoryPipelineImpl() ;
    }
}
