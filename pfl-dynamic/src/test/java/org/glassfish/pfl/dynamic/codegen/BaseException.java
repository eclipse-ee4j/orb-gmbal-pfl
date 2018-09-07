/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen;

public class BaseException extends RuntimeException {
    public BaseException( String message, Throwable cause ) {
	super( message, cause ) ;
    }

    public BaseException( String message ) {
	this( message, null ) ;
    }

    public BaseException( Throwable cause ) {
	this( null, cause ) ;
    }

    public BaseException() {
	this( null, null ) ;
    }
}
