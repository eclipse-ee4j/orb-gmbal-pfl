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

public interface NullaryPredicate {
    public boolean evaluate() ;

    public class Factory {
	private Factory() {}

	public static NullaryPredicate makeConstant( final boolean value ) {
	    return new NullaryPredicate() {
                @Override
		public boolean evaluate() {
		    return value ;
		}
	    } ;
	}

	public static NullaryPredicate makeFuture( 
	    final NullaryPredicate closure ) {
	    return new NullaryPredicate() {
		private boolean evaluated = false ;
		private boolean value ;

                @Override
		public synchronized boolean evaluate()
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
