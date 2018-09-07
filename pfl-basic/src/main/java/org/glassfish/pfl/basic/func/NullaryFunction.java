/*
 * Copyright (c) 2004, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.func ;

public interface NullaryFunction<T> {
    public T evaluate() ;

    public class Factory {
	private Factory() {}

	public static <T> NullaryFunction<T> makeConstant( final T value ) {
	    return new NullaryFunction() {
                @Override
		public T evaluate() {
		    return value ;
		}
	    } ;
	}

	public static <T> NullaryFunction<T> makeFuture(
	    final NullaryFunction<T> closure ) {
	    return new NullaryFunction() {
		private boolean evaluated = false ;
		private T value ;

                @Override
		public synchronized T evaluate()
		{
		    if (!evaluated) {
			evaluated = true ;
			value = closure.evaluate() ;
		    }

		    return value ;
		}
	    } ;
	}
    }
}
