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

/** This annotation is applied to an interface or abstract class that is used
 * to define methods for logging and/or constructing exceptions.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionWrapper {
    /** Return the prefix used in front of the numeric exception ID in the formatter
     * exception message.  For example, CORBA uses IIOP for this purpose.
     * @return The log messaged ID prefix
     */
    String idPrefix() ;

    /** Return the logger name to be used for all logged messages generated
     * from the class.  Default is the package in which the class is defined.
     * @return The logger name.
     */
    String loggerName() default "" ;

    /** Return the name of the ResourceBundle to use for I18N support for
     * exceptions in this class.
     *
     * @return The bundle name.
     */
    String resourceBundle() default "" ;
}
