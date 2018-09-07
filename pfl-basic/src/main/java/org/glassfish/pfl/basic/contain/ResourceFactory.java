/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain;

import org.glassfish.pfl.basic.func.NullaryFunction;

/** A thread-safe, contention-free lazy resource factory.
 *
 * @param <T> Type created by resource factory.
 * @author ken
 */
public class ResourceFactory<T> {
    // Volatile double-checked locking.  Can't use resource holder for
    // a general-purpose library, because it requires a class per value.
    // Resource holder also doesn't work for non-singleton values.
    private volatile T value ;
    private final NullaryFunction<T> cons ;

    public ResourceFactory( NullaryFunction<T> cons ) {
        this.cons = cons ;
    }

    public T get() {
        if (value != null) {
            return value ;
        }

        synchronized (this) {
            if (value == null) {
                value = cons.evaluate() ;
            }

            return value ;
        }
    }
}
