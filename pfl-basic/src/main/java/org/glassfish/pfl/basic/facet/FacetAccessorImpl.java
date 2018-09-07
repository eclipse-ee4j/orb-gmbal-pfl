/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.facet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.pfl.basic.algorithm.ClassAnalyzer;
import org.glassfish.pfl.basic.contain.Holder;
import org.glassfish.pfl.basic.func.UnaryPredicate;

/** 
 *
 * @author ken
 */
public class FacetAccessorImpl implements FacetAccessor {

    private Object delegate ;
    private Map<Class<?>,Object> facetMap =
        new HashMap<Class<?>,Object>() ;

    public FacetAccessorImpl( Object delegate ) {
        this.delegate = delegate ;
    }
    
    @Override
    public <T> T facet(Class<T> cls ) {
        Object result = null ;
        
        if (cls.isInstance(delegate)) {
            result = delegate ;
        } else {
            result = facetMap.get( cls ) ;
        }

        if (result == null) {
            return null ;
        } else {
            return cls.cast( result ) ;
        }
    }
    
    @Override
    public Collection<Object> facets() {
        Collection<Object> result = new ArrayList<Object>() ;
        result.addAll( facetMap.values() ) ;
        result.add( this ) ;
        return result ;
    }
    
    @Override
    public <T> void addFacet( final T obj) {
        if (obj.getClass().isInstance(delegate)) {
            throw new IllegalArgumentException( 
                "Cannot add facet of supertype of this object" ) ;
        }
                
        ClassAnalyzer ca = ClassAnalyzer.getClassAnalyzer( obj.getClass() ) ;
        ca.findClasses( 
            new UnaryPredicate<Class<?>>() {
                @Override
                public boolean evaluate(Class arg) {
                    facetMap.put( arg, obj ) ;
                    return false ;
                } } ) ;
    }

    @Override
    public Object invoke(final Method method, final Object... args) {
        final Object target = facet( method.getDeclaringClass() ) ;
        if (target == null) {
            throw new IllegalArgumentException(
                "No facet available for method " + method ) ;
        }

        try {
            final ClassAnalyzer ca =
                ClassAnalyzer.getClassAnalyzer( target.getClass() ) ;
            final String mname = method.getName() ;
            final Class<?>[] mparams = method.getParameterTypes() ;
            final Holder<Method> mholder = new Holder<Method>() ;

            ca.findClasses( 
                new UnaryPredicate<Class<?>>() {
                    @Override
                    public boolean evaluate(Class<?> arg) {
                        try {
                            if (mholder.content() == null) {
                                Method m = arg.getDeclaredMethod(mname, mparams);
                                mholder.content(m);
                                return true;
                            }
                        } catch (Exception ex) {
                            // ignore
                        }

                        return false ;
                    }
                } 
            ) ;

            if (System.getSecurityManager() == null) {
                mholder.content().setAccessible(true);
            } else {
                AccessController.doPrivileged( new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        mholder.content().setAccessible(true);
                        return null ;
                    }
                }) ;
            }

            final Object result = mholder.content().invoke(target, args);
            return result ;
        } catch (SecurityException ex) {
            throw new IllegalArgumentException(
                "Exception on invocation", ex ) ;
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(
                "Exception on invocation", ex ) ;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Exception on invocation", ex ) ;
        } catch (InvocationTargetException ex) {
            throw new IllegalArgumentException(
                "Exception on invocation", ex ) ;
        }
    }

    @Override
    public Object get(Field field ) {
        Object result = null ;

        Object target = facet( field.getDeclaringClass() ) ;

        try {
            result = field.get(target);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Exception on field get", ex ) ;
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(
                "Exception on field get", ex ) ;
        }

        return result ;
    }

    @Override
    public void set(Field field, Object value ) {
        Object target = facet( field.getDeclaringClass() ) ;

        try {
            field.set(target, value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                "Exception on field get", ex ) ;
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(
                "Exception on field get", ex ) ;
        }
    }

    @Override
    public void removeFacet( Class<?> cls ) {
        if (cls.isInstance(delegate)) {
            throw new IllegalArgumentException( 
                "Cannot add facet of supertype of this object" ) ;
        }
        
        ClassAnalyzer ca = ClassAnalyzer.getClassAnalyzer( cls ) ;
        ca.findClasses( 
            new UnaryPredicate<Class<?>>() {
                @Override
                public boolean evaluate(Class arg) {
                    facetMap.remove( arg ) ;
                    return false ;
                } } ) ;
    }

}
