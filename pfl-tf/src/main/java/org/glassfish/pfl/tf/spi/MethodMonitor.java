/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi;

import java.util.Collection;

/** Interface representing some action that takes place on entry and exit to
 * a method that is being traced.
 *
 * @author ken
 */
public interface MethodMonitor {
    /** The class for which this MethodMonitor is defined.
     * 
     * @return The class of this MethodMonitor.
     */
    Class<?> myClass() ;

    /** Invoked at the start of a method, before any actions in the method
     * take place.
     * 
     * @param ident The method identifier.
     * @param args The arguments passed into the method.
     */
    void enter( int ident, Object... args ) ;

    /** Invoked anywhere in the method after enter and before exit, to indicate
     * some useful tracing information in the method.
     * 
     * @param callerIdent The identifier of the method calling the InfoMethod.
     * @param selfIdent The identifier of the InfoMethod itself.
     * @param args Any information needed in the info call.
     */
    void info( Object[] args, int callerIdent, int selfIdent ) ;

    /** An exit from a method that has a void return type.  Called as the last
     * operation in the method.
     *
     * @param ident The method identifier.
     */
    void exit( int ident ) ;

    /** An exit from a method that has a non-void return type.  Called as the last
     * operation in the method.  result will be null if the method terminates
     * by throwing an exception.
     *
     * @param ident The method identifier.
     * @param result The method result.
     */
    void exit( int ident, Object result ) ;

    /** Called to report an exception that is thrown in the method.  If the
     * method throws and catches the exception, it will still be reported.
     *
     * @param ident The method identifier.
     * @param thr The exception that terminates the method.
     */
    void exception( int ident, Throwable thr ) ;

    /** Provided for MethodMonitor instances that maintain state.  Simply removes
     * the state and resets the MethodMonitor to its initial state.
     *
     */
    void clear() ;

    /** Returns the contents of this method monitor.  If it is a composite
     * method monitor, all the component MethoMonitor instances are 
     * returned.  If it is a single MethodMonitor, it just returns itself.
     * It is required that a composite method monitor only return MethodMonitor
     * instances which are not themselves composite.
     */
    Collection<MethodMonitor> contents() ;

    /** Factory used to create this MethodMonitor
     * Note: is is required that this.factory().create(myClass()).equals( this )
     * for any MethodMonitor.
     */
    MethodMonitorFactory factory() ;

    String name() ;
}
