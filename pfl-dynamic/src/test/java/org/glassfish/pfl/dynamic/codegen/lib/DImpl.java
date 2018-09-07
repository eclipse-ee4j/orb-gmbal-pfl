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

public class DImpl implements D {
    public Object trinary( Object arg1, Object arg2, Object arg3 ) throws RemoteException 
    {
	return null ;
    }

    public Object binary( Integer arg1, Integer arg2 ) throws RemoteException 
    {
	return null ;
    }

    public Object unary( Object arg1 ) throws RemoteException 
    {
	return null ;
    }

    public String getName() throws RemoteException 
    {
	return "" ;
    }

    public void setName( String arg ) throws RemoteException 
    {
    }

    public boolean echo( boolean arg ) throws RemoteException 
    {
	return arg ;
    }

    public byte echo( byte arg ) throws RemoteException 
    {
	return arg ;
    }

    public char echo( char arg ) throws RemoteException 
    {
	return arg ;
    }

    public short echo( short arg ) throws RemoteException 
    {
	return arg ;
    }

    public int echo( int arg ) throws RemoteException 
    {
	return arg ;
    }

    public long echo( long arg ) throws RemoteException 
    {
	return arg ;
    }

    public float echo( float arg ) throws RemoteException 
    {
	 return arg ;
    }

    public double echo( double arg ) throws RemoteException 
    {
	 return arg ;
    }

    public Object echo( Object arg ) throws RemoteException 
    {
	return arg ;
    }

    public Object binary( Object arg1, Object arg2 ) throws RemoteException 
    {
	return null ;
    }
}

