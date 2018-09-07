/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi.annotation;

import java.lang.annotation.Target ;
import java.lang.annotation.Documented ;
import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;

/** Used to associate a description with part of a class.
 */
@Documented
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.TYPE } )
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    /** The description to be applied to the annotated element.
     * This value must not be empty.  It can either be the actual string that 
     * describes the annotated element, or a key into a resource bundle associated.
     * If there is no bundle value associated with the key, or no
     * resource bundle is specified, the value is used directly.
     */
    String value() ;

    /** Optional key to use in a resource bundle for this description. If present,
     * a tool will generate a resource bundle that contains key=value taken
     * from the description annotation.
     * <p>
     * If this key is not present, the default key is given by the class name, 
     * if this annotation appears on a class, or the class name.method name if 
     * this annotation appears on a method.  It is an error to use the default
     * value for more than one method of the same name, except for setters and getters.
     */
    String key() default "" ;
}
