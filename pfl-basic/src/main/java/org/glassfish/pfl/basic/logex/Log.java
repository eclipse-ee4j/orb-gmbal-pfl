/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.logex;

/**
 *
 * @author ken
 */
import java.lang.annotation.Documented ;
import java.lang.annotation.Target ;
import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;

/** This annotation is applied to a method to define the log and exception
 * details for the method.
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /** The logging Level (encoded as an enum) to use for the log record
     * generated from the annotated method.
     * 
     * @return The log level.
     */
    LogLevel level() default LogLevel.WARNING ;

    /** The exception ID to be used.  This is used to construct the message
     * ID in the log message.
     * @return The exception id (which must include the VMCID).
     */
    int id() default 0 ;
}
