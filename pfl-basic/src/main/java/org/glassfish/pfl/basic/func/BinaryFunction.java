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

import java.util.function.BiFunction;

/**
 * @deprecated replaced in JDK8 by {@link BiFunction}
 */
@Deprecated
public interface BinaryFunction<S,T,R> extends BiFunction<S,T,R> {
    R evaluate( S arg1, T arg2 );
    
    @Override
    default R apply(S arg1, T arg2) {
        return evaluate(arg1, arg2);
    }
}
