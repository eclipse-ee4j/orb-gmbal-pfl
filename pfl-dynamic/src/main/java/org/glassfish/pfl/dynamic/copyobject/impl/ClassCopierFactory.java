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

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

/** A factory used for creating ClassCopier instances.  
 */
public interface ClassCopierFactory {
    /** Return the ClassCopier instance for a particular class.
     * The resulting ClassCopier may be used to copy an instance of type
     * cls. 
     * Note that it is an error to pass an interface for cls, as interfaces
     * have no state and no constructors, and hence cannot be copied.
     */
    ClassCopier getClassCopier( Class<?> cls ) throws ReflectiveCopyException ;
}
