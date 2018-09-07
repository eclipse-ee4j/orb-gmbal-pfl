/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.spi;

/** Interceptor interface used for byte code modification.
 * A user supplies an implementation of this interface.
 * The calls are invoked as follows:
 * <ol>
 * <li>handleClass is called for the class.
 * <li>handleMethod is called for each method defined in the class.
 * <li>handleFieldReference is called for each field reference in
 * a class in the order in which they occur.  All field references
 * in a method are made available for modification before 
 * handleMethod is called for the next method.
 * </ol>
 */
public interface Interceptor extends Comparable<Interceptor> {
    /** Return the name of the interceptor.
     */
    String name() ;

    /** Invoked when the GenericClass constructor is called with
     * classdata.  All Wrapper methods that are available 
     * between _class() and _end() may be used to add to
     * cls.  This includes adding new methods, fields, constructors,
     * and extending the class initializer.  Any changes made to
     * the ModifiableClass argument are included in the resulting
     * GenericClass instance.
     */
    void handleClass( ModifiableClass cls ) ;
	    
    /** Invoked after handleClass for each method defined in the
     * class passed into the GenericClass constructor called
     * with the classdata.  The ModifiableMethod API may be
     * used to change the method, including adding code
     * before and/or after the existing method body using
     * the usual Wrapper calls for use in a method body.
     */
    void handleMethod( ModifiableMethod method ) ;
	    
    /** Called when a reference to a field is encountered while
     * visiting the body of the method for which handleMethod
     * was most recently called.
     */
    void handleFieldReference( ModifiableFieldReference ref ) ;
}
