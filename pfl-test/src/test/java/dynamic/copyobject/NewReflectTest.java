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

public class NewReflectTest extends Client
{
    private static final String[] EXCLUDE_LIST = new String[] {
    } ;

    private static final String[] EXPECTED_EXCEPTION_LIST = new String[] {
	// "testLinkedHashMap",
	// "testLinkedHashSet",    // temp. until we fix the recursive copy of linked structures
	// "testCustomMap",
	"testTransientThread",
	"testTransientThreadGroup",
	"testTransientProcessBuilder" } ;

    public NewReflectTest( ) 
    { 
    }

    public NewReflectTest( String name ) { super( name ) ; }

    public static void main( String[] args ) 
    { 
	Client root = new NewReflectTest() ;
	Client.doMain( args, root ) ; 
    }

    public static Test suite() {
	Client root = new NewReflectTest() ;
	return root.makeSuite() ;
    }

    public boolean isTestExcluded()
    {
	return findInArray( getName(), EXCLUDE_LIST ) ;
    }

    public boolean shouldThrowReflectiveCopyException()
    {
	return findInArray( getName(), EXPECTED_EXCEPTION_LIST ) ;
    }

    public ObjectCopierFactory getCopierFactory( )
    {
	return CopyobjectDefaults.makeReflectObjectCopierFactory( ) ;
    }

    public Client makeTest( String name ) 
    {
	return new NewReflectTest( name ) ;
    }
}
