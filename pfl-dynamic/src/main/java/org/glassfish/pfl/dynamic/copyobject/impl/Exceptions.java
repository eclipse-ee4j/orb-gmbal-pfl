/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl;

import org.glassfish.pfl.basic.logex.Chain;
import org.glassfish.pfl.basic.logex.ExceptionWrapper;
import org.glassfish.pfl.basic.logex.Log;
import org.glassfish.pfl.basic.logex.LogLevel;
import org.glassfish.pfl.basic.logex.Message;
import org.glassfish.pfl.basic.logex.WrapperGenerator;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

/** Exception wrapper class.  The logex WrapperGenerator uses this interface
 * to generate an implementation which returns the appropriate exception, and
 * generates a log report when the method is called.  This is used for all
 * implementation classes in this package.
 *
 * The exception IDs are allocated in blocks of EXCEPTIONS_PER_CLASS, which is
 * a lot more than is needed, but we have 32 bits for IDs, and multiples of
 * a suitably chosen EXCEPTIONS_PER_CLASS (like 100 here) are easy to read in
 * error messages.
 *
 * @author ken
 */
@ExceptionWrapper( idPrefix="OBJCOPY" )
public interface Exceptions {
    static final Exceptions self = WrapperGenerator.makeWrapper(
        Exceptions.class) ;

    // Allow 100 exceptions per class
    static final int EXCEPTIONS_PER_CLASS = 100 ;

// FallbackCopierImpl
    static final int FB_START = 1 ;

    @Message( "Object copy failed on copy of {0} which has type {1}" )
    @Log( id = FB_START + 0, level=LogLevel.FINE )
    void failureInFallback(
        @Chain ReflectiveCopyException exc, Object obj, Class<?> cls );

// ClassCopierBase
    static final int CCB_START = FB_START + EXCEPTIONS_PER_CLASS ;

    @Message( "Stack overflow while copying {0}" )
    @Log( id = CCB_START + 0, level=LogLevel.WARNING )
    ReflectiveCopyException stackOverflow(Object source,
        @Chain StackOverflowError ex);

// DefaultCopier
    static final int DC_START = CCB_START + EXCEPTIONS_PER_CLASS ;

    @Message( "Could not copy {0}" )
    @Log( id = DC_START + 0, level=LogLevel.WARNING )
    ReflectiveCopyException couldNotCopy(Object obj, ReflectiveCopyException exc);

// ClassCopierFactoryPipelineImpl
    static final int CCFPI_START = DC_START + EXCEPTIONS_PER_CLASS ;

    @Log( id = CCFPI_START + 0, level=LogLevel.WARNING )
    @Message( "Cannot copy interface (attempt was for {0})")
    ReflectiveCopyException cannotCopyInterface( Class<?> cls ) ;

    @Log( id = CCFPI_START + 1, level=LogLevel.WARNING )
    @Message( "Could not find ClassCopier for {0}")
    IllegalStateException couldNotFindClassCopier( Class<?> cls ) ;

    @Log( id = CCFPI_START + 2, level=LogLevel.WARNING )
    @Message( "Could not copy class {0}")
    ReflectiveCopyException cannotCopyClass( Class<?> cls ) ;

// ClassCopierOrdinaryImpl
    static final int CCOI_START = CCFPI_START + EXCEPTIONS_PER_CLASS ;

    @Message( "Exception in readResolve() for {0}")
    @Log( id = CCOI_START + 0, level=LogLevel.WARNING )
    RuntimeException exceptionInReadResolve(Object obj, @Chain Throwable t);

    @Message( "Cannot create ClassFieldCopier for superclass {0} "
        + ": This class already has a ClassCopier" )
    @Log( id = CCOI_START + 0, level=LogLevel.WARNING )
    ReflectiveCopyException noClassCopierForSuperclass( Class<?> superClass ) ;
}
