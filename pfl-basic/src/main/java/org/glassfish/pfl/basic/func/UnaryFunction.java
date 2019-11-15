/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Services Ltd.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.func ;

import java.util.function.Function;

/**
 * Represents a functions that takes one argument and returns a result
 * @deprecated replaced in JDK8 by {@link java.util.function.Function}
 */
@Deprecated
public interface UnaryFunction<T,R> extends Function<T,R> {
    R evaluate( T arg ) ;
    
    @Override
    default R apply(T t) {
        return evaluate(t);
    }
}

