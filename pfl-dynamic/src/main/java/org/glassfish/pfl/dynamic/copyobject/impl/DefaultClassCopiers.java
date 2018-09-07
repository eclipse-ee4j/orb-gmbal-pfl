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
import java.util.Iterator ;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

public abstract class DefaultClassCopiers
{
    private DefaultClassCopiers() {}

    private static ClassCopier identityClassCopier = 
	new ClassCopierBase( "identity" ) 
	{
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		return source ;
	    }
	} ;

    /** Return a ClassCopier that simple returns its argument as its
     *  result.
     */
    public static ClassCopier getIdentityClassCopier()
    {
	return identityClassCopier ;
    }

    private static ClassCopier errorClassCopier = 
	// Set isReflective true to get better error messages.
	new ClassCopierBase( "error", true ) 
	{
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		throw new ReflectiveCopyException( 
		    "Cannot copy class " + source.getClass() ) ;
	    }
	} ;

    /** Return a ClassCopier that always raises a ReflectiveCopyException
     * whenever its copy method is called.
     */
    public static ClassCopier getErrorClassCopier()
    {
	return errorClassCopier ;
    }

    /** Return a ClassCopier that is suitable for instances of the Map 
     * interface.  This should be limited to HashMap, Hashtable, 
     * IdentityHashMap, and TreeMap.
     */
    public static ClassCopier makeMapClassCopier( 
	final ClassCopierFactory ccf ) 
    {
	return new ClassCopierBase( "map" ) 
	{
            @Override
	    public Object createCopy( Object source ) 
		throws ReflectiveCopyException
	    {
		try {
		    return source.getClass().newInstance() ;
		} catch (Exception exc) {
		    throw new ReflectiveCopyException( 
			"MapCopier could not copy " + source.getClass(), 
			exc ) ;
		}
	    }

	    private Object myCopy( Map<Object,Object> oldToNew,
		Object obj ) throws ReflectiveCopyException
	    {
		if (obj == null) {
                    return null;
                }

		Class cls = obj.getClass() ;
		ClassCopier copier = ccf.getClassCopier( cls ) ;
		return copier.copy( oldToNew, obj ) ;
	    }

            @Override
	    public Object doCopy( Map<Object,Object> oldToNew,
		Object source, Object result ) throws ReflectiveCopyException
	    {
		Map sourceMap = (Map)source ;
		Map resultMap = (Map)result ;

		Iterator iter = sourceMap.entrySet().iterator() ;
		while (iter.hasNext()) {
		    Map.Entry entry = (Map.Entry)(iter.next()) ;

		    Object key = entry.getKey() ;
		    Object newKey = myCopy( oldToNew, key ) ;

		    Object value = entry.getValue() ;
		    Object newValue = myCopy( oldToNew, value ) ;

		    resultMap.put( newKey, newValue ) ;
		}

		return result ;
	    }
	} ;
    }
}
