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

/** Since each MethodMonitor generally needs a reference to the class it is
 * monitoring, we actually work with MethodMonitorFactory instances instead
 * of simply using MethodMonitor.
 *
 * @author ken
 */
public interface MethodMonitorFactory {
    /** Return an instance of a MethodMonitor suitable for use in the given
     * class cls, according to the currently registered MethodMonitorFactory 
     * instances in the MethodMonitorRegistry.
     * 
     * @param cls The class for which we need the MethodMonitor.
     * @return The MethodMonitor for cls.
     */
    MethodMonitor create( Class<?> cls ) ;

    /** Returns the contents of this method monitor factory.  If it is a composite
     * method monitor factory, all the component MethoMonitorFactory instances are 
     * returned.  If it is a single MethodMonitorFactory, it just returns itself.
     * It is required that the elements of contents are not composite method
     * monitors, i.e. for each mmf in contants(), mmf.contents.size() == 1.
     */
    Collection<MethodMonitorFactory> contents() ;

    /** The name of this mmf.  Given any two mmf a and b, a.equals( b ) iff
     * a.name().equals( b.name() ).
     * @return The name of this MethodMonitorFactory.
     */
    String name() ;
}
