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

import java.util.Set ;
import java.util.List ;
import java.util.Map ;

/** An interface that provides information about classes.  This can be
 * used to describe both ClassGenerators that are used to generate code
 * and pre-existing Java classes.
 */
public interface ClassInfo {
    /** Return the modifiers on this class as specified in java.lang.reflect.Modifier.
     */
    public int modifiers() ;

    /** Return the Type of the class represented by this
     * ClassInfo.
     */
    public Type thisType() ;

    /** Return true iff this ClassInfo is an interface.
     */
    public boolean isInterface() ;

    /** Return the fully qualified class name for this
     * ClassInfo.
     */
    public String name() ;

    /** Return the fully qualified package name containing
     * the class represented by this ClassInfo.
     */
    public String pkgName() ;

    /** Return the class name of the class represented by this
     * ClassInfo relative to pkgName().
     */
    public String className() ;

    /** Return the Type of the supertype of this class.
     */
    public Type superType() ;

    /** Return the list of Types of interfaces implemented by this class.
     * May be empty, but never null.
     */
    public List<Type> impls() ;

    /** Return a map from field names to FieldInfo instances for
     * every field defined in this class (not including super types).
     */
    public Map<String,FieldInfo> fieldInfo() ;


    /** Find a field with the given name if one exists.
     * Searches this class and all super classes.
     */ 
    public FieldInfo findFieldInfo( String name ) ;

    /** Return methodInfo for all methods defined on this class.
     * This does not include inherited methods.  Here we return
     * a map from method name to the set of MethodInfo instances for
     * all methods with the same method name.  This form is useful
     * for handling method overload resolution.
     */
    public Map<String,Set<MethodInfo>> methodInfoByName() ;

    public Set<MethodInfo> constructorInfo() ;

    /** Find the method (if any) with the given name and Signature
     * in this ClassInfo, or in any superType of this ClassInfo.
     */
    public MethodInfo findMethodInfo( String name, Signature sig ) ;

    /** Find the MethodInfo (if any) for a Constructor with the given
     * Signature in this ClassInfo.
     */
    public MethodInfo findConstructorInfo( Signature sig ) ;

    /** Return true iff this is a subclass or subinterface of
     * info.
     */
    public boolean isSubclass( ClassInfo info ) ;
}
