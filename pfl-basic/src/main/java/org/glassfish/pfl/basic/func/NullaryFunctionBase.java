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

public abstract class NullaryFunctionBase<R> 
    extends FunctionBase
    implements NullaryFunction<R> {

    public NullaryFunctionBase( final String name) {
	super( name) ;
    }

    public abstract R eval( ) ;

    @Override
    public R evaluate() {
        return eval() ;
    }
}
