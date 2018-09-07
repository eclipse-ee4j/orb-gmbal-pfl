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

public abstract class BinaryVoidFunctionBase<S,T> 
    extends FunctionBase 
    implements BinaryVoidFunction<S,T> {

    public BinaryVoidFunctionBase( final String name) {
	super( name) ;
    }

    public abstract void eval( S arg1, T arg2 ) ;

    @Override
    public void evaluate( S arg1, T arg2 ) {
        eval( arg1, arg2 ) ;
    }
}
