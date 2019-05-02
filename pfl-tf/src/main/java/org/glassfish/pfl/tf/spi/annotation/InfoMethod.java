/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi.annotation;

import org.glassfish.pfl.tf.spi.TimingPointType;
import java.lang.annotation.Target ;
import java.lang.annotation.Documented ;
import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;

/** Used to indicate that a method is used for invoking MethodMonitor.info.
 * Note that the tracing name is the method name unless overridden by a
 * TracingName annotation (which is required if the method is overloaded).
 * Also note that either all overloaded methods of the same name are InfoMethods,
 * or none of them are.
 * The name of this method as a TimingPoint is the same as the tracing name.
 * The method must be private and have a void return type.
 * Any arguments are passed into the
 * MethodMonitor.info call in the instrumented code.
 *
 * @author ken
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InfoMethod {
    
    /**
     * Indicates whether this info method represents an ENTER, EXIT, or NONE (no timing point).
     */
    TimingPointType tpType() default TimingPointType.NONE ;
    
    /**
     * Gives the name of timing point to use for this info method.
     * Must not be "" if tpType is not NONE.
     */
    String tpName() default "" ;
}
