/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl ;

import java.util.Map ;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

/** Provides deep copying of one specific class.
 * An ObjectCopier (what Util.copyObject sees) uses some kind
 * of factory to find the ClassCopier for the Class of the object
 * in order to copy a particular object.
 */
public interface ClassCopier {
    /** Produce a deep copy of source, recursively copying all
     * of its constituents.  Aliasing is preserved through
     * oldToNew, so that no component of source is copied more than
     * once.  Throws ReflectiveCopyException if it cannot copy
     * source.  This may occur in some implementations, depending
     * on the mechanism used to copy the class.
     */
    Object copy( Map<Object,Object> oldToNew,
	Object source ) throws ReflectiveCopyException  ;

    /** We need to know whether this class copier operates via reflection
     * or not, as the reflective class copier must be able to tell 
     * when a super class is copied by an incompatible copier.
     */
    boolean isReflectiveClassCopier() ;
}
