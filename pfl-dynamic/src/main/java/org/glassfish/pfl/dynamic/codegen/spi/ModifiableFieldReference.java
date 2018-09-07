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

public interface ModifiableFieldReference extends FieldInfo {
    public enum ReferenceType { GET, SET } ;

    MethodInfo method() ;

    FieldInfo field() ;

    ReferenceType getReferenceType() ;

    /** Return an expression that can be used to access
     * the target object, if !Modifier.isStatic(field().modifiers()).
     * This variable refers to the target object available 
     * before the set or get of the field.  If the field is
     * static, this returns null.
     */
    Variable getTargetObject() ;

    /** Return an expression that can be used to access
     * the field value. 
     * If getReferenceType()==GET, this variable must be set
     * to the value returned by the reference.  
     * If getReferenceType()==SET, this variable contains the
     * value to be stored by the reference.
     */
    Variable getValue() ;

    /** After this call, the field reference will not be emitted.
     * Instead, any sequence of Wrapper calls valid in a method 
     * body may be used to generate replacement code for the
     * field reference.  As an example, the following code
     * would cause equivalent code to the original reference to be
     * emitted, in the case of a non-static field:
     * <pre>
     * Variable target = mf.getTargetObject() ;
     * Variable value = mf.getValue() ;
     * String name = field().name() ;
     *
     * // For getReferenceType() == GET:
     * _assign( value, _field( target, name ) ) ;
     *
     * // For getRerenceType() == SET:
     * _assign( _field( target, name ), value ) ;
     *
     * </pre>
     */
    void replace() ;

    /** Mark the end of the code generation to replace the field
     * reference.
     */
    void complete() ;
}
