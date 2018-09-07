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

import java.util.List ;

import java.lang.reflect.Method ;
import java.lang.reflect.Constructor ;

/** An interface that provides information about methods.  This can be
 * used to describe both MethodGenerators that are used to generate code
 * and pre-existing Java classes.
 */
public interface MethodInfo extends MemberInfo {
    /** Returns true if this is a constructor, false if
     * method.
     */
    boolean isConstructor() ;
    /** Return the Type that is returned by this method.
     */
    Type returnType() ;

    /** Return a list of all Exception types that are declared as being
     * throwable from this method.
     */
    List<Type> exceptions() ;

    /** Return a list of arguments for this method.
     */
    List<Variable> arguments() ;
    
    /** Return the signature of this method.
     */
    Signature signature() ;

    /** Return the Method that is represented by this MethodInfo, or null
     * if no such Method instance exists (because this MethodInfo represents
     * a Method being generated, rather than a Method in a Class that is 
     * loaded into the VM). 
     * @throws IllegalStateException if isConstructor() is true. 
     */
    Method getMethod() ;

    /** Return the Constructor that is represented by this MethodInfo, or null
     * if no such Constructor instance exists (because this MethodInfo represents
     * a Constructor being generated, rather than a Constructor in a Class that is 
     * loaded into the VM). 
     * @throws IllegalStateException if isConstructor() is false. 
     */
    Constructor getConstructor() ;
}
