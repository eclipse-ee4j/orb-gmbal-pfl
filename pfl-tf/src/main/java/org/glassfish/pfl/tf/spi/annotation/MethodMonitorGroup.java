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

import java.lang.annotation.Annotation;
import java.lang.annotation.Target ;
import java.lang.annotation.Documented ;
import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;

/** Meta-annotation used to define annotations that define groups of related
 * classes whose methods should be traced.  MethodMonitorGroups may be nested,
 * and MethodMonitorFactory instances that apply to a group apply to all
 * subgroups as well (following the transitive closure of the subgroups).
 *
 * @author ken
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodMonitorGroup {
    /** List of MethodMonitorGroups that are subgrops of this one.
     * Because annotations don't support circularity, the type
     * can't be MethodMonitorGroup[], so we require that all classes
     * in subgroups be annotations which are annotated with
     * MethodMonitorGroup.
     *
     * @return List of MethodMonitorGroups that are subgroups of
     * this one.
     */
    Class<? extends Annotation>[] value() default {} ;

    String description() default "" ;
}
