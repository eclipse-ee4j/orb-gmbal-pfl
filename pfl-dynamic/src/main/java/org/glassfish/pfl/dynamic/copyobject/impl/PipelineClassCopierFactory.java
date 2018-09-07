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

/** A factory used for creating ClassCopier instances.  
 */
public interface PipelineClassCopierFactory extends ClassCopierFactory 
{
    /** Look for cls only in the cache; do not create a ClassCopier
     * if there isn't one already in the cache.
     */
    public ClassCopier lookupInCache( Class<?> cls ) ;

    /** Mark this class as immutable, so that it is not copied at all.
     */
    public void registerImmutable( Class<?> cls ) ;

    /** Add a special ClassCopierFactory into the chain so that 
     * it handles some special cases.
     */
    public void setSpecialClassCopierFactory( ClassCopierFactory ccf ) ;

    /** Added this method so reflective copier could check if serializable
     * object contains a transient field of specific type.
     */
    public boolean reflectivelyCopyable( Class<?> cls ) ;
}
