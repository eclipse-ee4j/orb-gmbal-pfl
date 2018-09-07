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

/** A version of a ClassCopierFactory that implements caching, and so
 * needs a way to update the cache.
 */
public interface CachingClassCopierFactory extends ClassCopierFactory
{
    /** Put copier into the cache for Class cls.  Thereafter, the get
     * method will return the assigned copier for Class cls.  There
     * is no way to remove an entry from the cache.  However, this 
     * cache must use weak keys (like WeakHashMap) to avoid pinning
     * ClassLoaders in memory.  Consequently entries in the cache
     * MAY silently disappear.
     */
    public void put( Class<?> cls, ClassCopier copier ) ;
}
