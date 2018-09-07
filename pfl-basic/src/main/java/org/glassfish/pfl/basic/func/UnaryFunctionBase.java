/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.func ;

public abstract class UnaryFunctionBase<S,R> 
    extends FunctionBase 
    implements UnaryFunction<S,R> {

    public UnaryFunctionBase( final String name) {
        super( name) ;
    }

    public abstract R eval( S arg1 ) ;

    @Override
    public R evaluate( S arg1 ) {
        return eval( arg1 ) ;
    }
}
