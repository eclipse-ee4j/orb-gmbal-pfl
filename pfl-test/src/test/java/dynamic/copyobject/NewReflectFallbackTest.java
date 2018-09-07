/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package dynamic.copyobject  ;

import junit.framework.Test ;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyobjectDefaults;
import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopierFactory;

public class NewReflectFallbackTest extends Client
{
    public NewReflectFallbackTest( ) { }

    public NewReflectFallbackTest( String name ) { super( name ) ; }

    public boolean isTestExcluded() 
    {
	return false ;
    }

    public static void main( String[] args ) 
    { 
	Client root = new NewReflectFallbackTest() ;
	Client.doMain( args, root ) ; 
    }

    public static Test suite() {
	Client root = new NewReflectFallbackTest() ;
	return root.makeSuite() ;
    }

    public ObjectCopierFactory getCopierFactory()
    {
	ObjectCopierFactory reflect = 
	    CopyobjectDefaults.makeReflectObjectCopierFactory( ) ;
	ObjectCopierFactory stream = 
	    CopyobjectDefaults.makeJavaStreamObjectCopierFactory( ) ;
	ObjectCopierFactory result =
	    CopyobjectDefaults.makeFallbackObjectCopierFactory( 
		reflect, stream ) ;

	return result ;
    }

    public Client makeTest( String name ) 
    {
	return new NewReflectFallbackTest( name ) ;
    }
}
