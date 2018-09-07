/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.spi ;

public class ReflectiveCopyException extends RuntimeException {
    private static final long serialVersionUID = 8451419413210965395L;

    public ReflectiveCopyException()
    {
	super() ;
    }

    public ReflectiveCopyException( String msg )
    {
	super( msg ) ;
    }

    public ReflectiveCopyException( String msg, Throwable t )
    {
	super( msg, t ) ;
    }
}
