/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation applied to a method in an abstract class or interface
 * to indicate that the method is an accessor for a property value.
 * The method must take no parameters and return a non-void result type.
 *
 * @author ken
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Delegate {
    /** Property identifier used to initialize property.
     * Default empty string indicates that the name should be derived from the
     * method name as follows:
     * <ul>
     * <li>If the method name is getName, the default id is name.
     * <li>if the method name is isName, and the return type is boolean or
     * Boolean, the default id is name.
     * <li>Otherwise, the method name is the default id.
     * </ul>
     * @return the property id.
     */
    String value() default "" ;
}
