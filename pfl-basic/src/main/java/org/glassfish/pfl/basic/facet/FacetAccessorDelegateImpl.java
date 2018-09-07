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

/** Implementation of FacetAccessor that delegates to another FacetAccessor,
 * typically a FacetAccessorImpl.  The purpose of this call is to provide
 * a convenient template of methods that may be copied into a class that
 * implements FacetAccessor.  Typically such a class implements that
 * FacetAccessor interface and defines a data member initialized as:
 * 
 * FacetAccessor facetAccessorDelegate = new FacetAccessorImpl( this ) ;
 * 
 * and then simply copies the other methods directly.
 * 
 * This is all a workaround for the fact that Java does not
 * support dynamic inheritance, or more than one superclass.  
 * 
 * Because this is a template, I have commented out all of the code.
 * It is not used at runtime or compiletime.
 *
 * @author ken
 */
abstract class FacetAccessorDelegateImpl implements FacetAccessor {
    /*
    private FacetAccessor facetAccessorDelegate ;
    
    public FacetAccessorDelegateImpl( FacetAccessor fa ) {
        facetAccessorDelegate = fa ;
    }
    
    public <T> T facet(Class<T> cls ) {
        return facetAccessorDelegate.facet( cls ) ;
    }

    public <T> void addFacet(T obj) {
        facetAccessorDelegate.addFacet( obj ) ;
    }

    public void removeFacet( Class<?> cls ) {
        facetAccessorDelegate.removeFacet( cls ) ;
    }

    public Object invoke(Method method, Object... args) {
        return facetAccessorDelegate.invoke( method, args ) ;
    }

    public Object get( Field field ) {
        return facetAccessorDelegate.get( field ) ;
    }

    public Collection<Object> facets() {
        return facetAccessorDelegate.facets() ;
    }

    Object get( Field field ) {
        return facetAccessorDelegate.get( field ) ;
    }

    void set( Field field, Object value ) {
        facetAccessorDelegate.set( field, value ) ;
    }

    */
}
