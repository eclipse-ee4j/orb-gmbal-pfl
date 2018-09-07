/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.spi ;

/** Provides an interface for a variety of means to copy an arbitrary 
 * object.  Any implementation of this interface must return an exact
 * copy of obj, preserving all aliasing across all objects reachable 
 * from obj.  ReflectiveCopyException must be thrown if the implementation
 * cannot copy obj for some reason.  Note that a trivial implementation
 * of this interface is possible (always return obj), but this is often
 * not the desired implementation.
 */
public interface ObjectCopier {
    /** copy the object.  Equivalent to copy( obj, false ).
     */
    Object copy( Object obj ) throws ReflectiveCopyException ;
}
