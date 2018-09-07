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
import java.lang.reflect.Method;
import java.util.Collection;

/** Interface to access facets of an object.  A facet is
 * an instance of a particular class.  It may be implemented
 * in a variety of ways, including inheritance, delegation,
 * or dynamic construction on demand.
 *
 * @author ken
 */
public interface FacetAccessor {
    /** Access the Facet of Class T from the object.
     * 
     * @param <T> The Type (as a Class) of the Facet.
     * @param cls The class of the facet.
     * @return Instance of cls for this facet.  Null if no such
     * facet is available.
     */
    <T> T facet( Class<T> cls ) ;
    
    /** Add a facet to the object.  The type T must not already
     * be available as a facet.
     * @param <T>
     * @param obj
     */
    <T> void addFacet( T obj ) ;
    
    /** Remove the facet (if any) of the given type.
     * 
     * @param cls The class of the facet to remove.
     */
    void removeFacet( Class<?> cls ) ;
    
    /** Return a list of all facets on this object.
     *
     * @return Collection of all facets.
     */
    Collection<Object> facets() ;
    
    /** Invoke method on the appropriate facet of this 
     * object, that is, on the facet corresponding to 
     * method.getDeclaringClass.
     * @param method The method to invoke.
     * @param args Arguments to the method.
     * @return restult of the invoke call.
     */
    Object invoke( Method method, Object... args ) ;

    /** Fetch the value of the field from whichever facet contains the field.
     * Read-only because that's all that the intended application needs.
     *
     * @param field The field to access
     * @param debug True if debugging trace output is desired
     * @return The value of the field
     */
    Object get( Field field ) ;

    void set( Field field, Object value ) ;
}
