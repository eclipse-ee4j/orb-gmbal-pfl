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

import java.util.Map ;

import java.lang.reflect.Array ;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

/** A factory used for creating ClassCopier instances.
 * An instance of this factory can be created and customized to
 * handle special copying semantics for certain classes.
 * This maintains a cache of ClassCopiers, so that a ClassCopier is
 * never created more than once for a particular class.
 */
public class ClassCopierFactoryArrayImpl implements ClassCopierFactory {
    
    // A full ClassCopierFactory for all classes.
    private final ClassCopierFactory classCopierFactory ;

    public ClassCopierFactoryArrayImpl( ClassCopierFactory ccf ) 
    {
	classCopierFactory = ccf ;
    }

    // Copy an array of Objects.  This would also work
    // for an array of primitives, but its more efficient to clone
    // arrays of primitives.  This is not static due to the
    // need to reference classCopierFactory.
    private ClassCopier arrayClassCopier = 
	new ClassCopierBase( "array" ) {
        @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		int alen = Array.getLength( source ) ;
		Object result = Array.newInstance( 
		    source.getClass().getComponentType(), alen ) ;
		return result ;
	    }

            @Override
	    public Object doCopy( Map<Object,Object> oldToNew, Object source,
		Object result ) throws ReflectiveCopyException 
	    {
		int alen = Array.getLength( source ) ;
		for (int ctr=0; ctr<alen; ctr++) {
		    Object aobj = Array.get( source, ctr ) ;

		    if (aobj != null) {
			// Must look up the Copier for each element
			// to handle polymorphic arrays
			ClassCopier copier = classCopierFactory.getClassCopier( 
			    aobj.getClass() ) ;
			aobj = copier.copy( oldToNew, aobj ) ;
		    }

		    Array.set( result, ctr, aobj ) ;
		}

		return result ;
	    }
	} ;

    private static ClassCopier booleanArrayClassCopier =
	new ClassCopierBase( "boolean" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		boolean[] obj = (boolean[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier byteArrayClassCopier =
	new ClassCopierBase( "byte" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		byte[] obj = (byte[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier charArrayClassCopier =
	new ClassCopierBase( "char" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		char[] obj = (char[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier shortArrayClassCopier =
	new ClassCopierBase( "short" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		short[] obj = (short[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier intArrayClassCopier =
	new ClassCopierBase( "int" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		int[] obj = (int[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier longArrayClassCopier =
	new ClassCopierBase( "long" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		long[] obj = (long[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier floatArrayClassCopier =
	new ClassCopierBase( "float" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		float[] obj = (float[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier doubleArrayClassCopier =
	new ClassCopierBase( "double" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		double[] obj = (double[])source ;
		return obj.clone() ;
	    } 
	} ;

    @Override
    public ClassCopier getClassCopier( Class<?> cls )
    {
	Class<?> compType = cls.getComponentType() ;

	if (compType == null) {
            return null;
        }

	if (compType.isPrimitive()) {
	    // The primitives could be pre-registered in the cache, but
	    // I like having the handling of all arrays grouped together
	    // in the same place.  The result is basically lazy initialization
	    // of the ClassCopierFactoryCachingImpl instance.
	    if (compType == boolean.class) {
                return booleanArrayClassCopier;
            }
	    if (compType == byte.class) {
                return byteArrayClassCopier;
            }
	    if (compType == char.class) {
                return charArrayClassCopier;
            }
	    if (compType == short.class) {
                return shortArrayClassCopier;
            }
	    if (compType == int.class) {
                return intArrayClassCopier;
            }
	    if (compType == long.class) {
                return longArrayClassCopier;
            }
	    if (compType == float.class) {
                return floatArrayClassCopier;
            }
	    if (compType == double.class) {
                return doubleArrayClassCopier;
            }

	    return null ;
	} else {
	    return arrayClassCopier ; 
	}
    }
}
