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

/** Indicates that a class has already been enhanced for tracing.
 * There are currently two stages, and a class may be enhanced to either
 * stage=1 or stage=2.  stage=1 means that all class-schema changes have
 * taken place, and the static initializer has been modified to register
 * with the tracing facility, but none of the traceable methods have been
 * modified.  stage=2 includes all stage 1 changes, plus all tracing code 
 * has been added.
 * <p>
 * The reason for 2 stages is that stage 1 must be done at build time, while
 * stage 2 can be done either at build time, or dynmically, for example in a 
 * ClassFileTransformer.  It is extremely helpful if EnhanceTool knows whether
 * a class has already been enhanced, so it can avoid making a mess by 
 * enhancing a class multiple times.  This is also necessary for incremental
 * enhancement when a project is recompiled: only those classes that have been
 * recompiled will be enhanced again.
 *
 * @author ken
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TFEnhanced {
    TraceEnhanceLevel stage() ;
}

