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

/** An interface that provides information common to all kinds of class
 * members.  This includes data members (represented by FieldInfo) and
 * methods and constructors (represented by MethodInfo).  This can be
 * used to describe both MethodGenerators that are used to generate code
 * and pre-existing Java classes.
 */
public interface MemberInfo {
    /** Return the ClassInfo of the class that contains this
     * member.
     */
    ClassInfo myClassInfo() ;

    /** Return the modifiers on this member
     */
    int modifiers() ;

    /** Return the name of this member.
     */
    String name() ;

    /** Returns true iff this member is accessible in the context
     * defined by definingClass (the class containing the 
     * reference to the member) and accessClass (the type of the
     * expression used to access this member).  This works as follows:
     * <ul>
     * <li>If modifiers() contains PUBLIC, the access is permitted.
     * <li>If modifiers() contains PRIVATE, the access is permitted
     * iff myClassInfo().name() is the same as definingClass.name().
     * <li>If modifiers() contains PROTECTED, the access is permitted as follows:
     * <ul>
     * <li>If myClassInfo().pkgName() is the same as definingClass.pkgName(),
     * the access is permitted.
     * <li>Otherwise, the access is permitted iff definingClass is a subclass of
     * myClassInfo(), and accessClass is a subclass of definingClass.
     * </ul>
     * <li>Otherwise, the access is permitted iff myClassInfo().pkgName is the
     * same as definingClass.pkgName().
     * </ul>
     * @param definingClass the ClassInfo of the class in which the access occurs.
     * @param accessClass the ClassInfo of the class used to access the member.
     */
    boolean isAccessibleInContext( ClassInfo definingClass,
	ClassInfo accessClass ) ;
}

