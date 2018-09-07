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

public interface C {
    String getName() throws RemoteException ;
    void setName( String arg ) throws RemoteException ;
    boolean echo( boolean arg ) throws RemoteException ;
    byte echo( byte arg ) throws RemoteException ;
    char echo( char arg ) throws RemoteException ;
    short echo( short arg ) throws RemoteException ;
    int echo( int arg ) throws RemoteException ;
    long echo( long arg ) throws RemoteException ;
    float echo( float arg ) throws RemoteException ;
    double echo( double arg ) throws RemoteException ;
    Object echo( Object arg ) throws RemoteException ;
}
