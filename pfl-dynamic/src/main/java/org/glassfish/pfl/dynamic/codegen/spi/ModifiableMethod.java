/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.spi;

/** Modify an existing method.  Directly modifying the method body
 * is not supported here, but the same effect is easily achieved
 * by renaming the old method and creating a new method with the same
 * name as the old method that delegates to the old method.
 */
public interface ModifiableMethod extends MethodInfo {
    void setName( String name ) ;

    void setModifiers( int modifiers ) ;

    void delete() ;
}
