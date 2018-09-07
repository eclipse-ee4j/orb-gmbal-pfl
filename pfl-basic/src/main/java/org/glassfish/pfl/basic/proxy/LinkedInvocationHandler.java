/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.proxy ;

import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.Proxy ;

/** This interface is used for InvocationHandler types that are
 * linked to their Proxy.  This is useful when the InvocationHandler
 * needs access to data keyed by identity on the Proxy.
 */
public interface LinkedInvocationHandler extends InvocationHandler
{
    void setProxy( Proxy proxy ) ;

    Proxy getProxy() ;
}

