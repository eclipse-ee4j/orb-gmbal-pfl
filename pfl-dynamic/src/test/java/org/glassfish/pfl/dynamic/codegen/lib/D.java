/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.lib;

import java.rmi.RemoteException ;

public interface D extends B, C {
    Object trinary( Object arg1, Object arg2, Object arg3 ) throws RemoteException ;
    Object binary( Integer arg1, Integer arg2 ) throws RemoteException ;
}
