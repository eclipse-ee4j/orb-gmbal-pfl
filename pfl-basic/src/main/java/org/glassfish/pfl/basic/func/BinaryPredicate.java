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

import java.util.function.BiPredicate;

/**
 * @deprecated replaced in JDK8 by {@link BiPredicate}
 */
@Deprecated
public interface BinaryPredicate<S,T> extends BiPredicate<S, T>{
    boolean evaluate( S arg1, T arg2 ) ;
    
    @Override
    default boolean test(S arg1, T arg2) {
        return evaluate(arg1, arg2);
    }
}

