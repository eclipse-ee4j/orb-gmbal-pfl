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


import junit.framework.Test;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyobjectDefaults;
import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopierFactory;

public class JavaStreamTest extends Client
{
    // Mostly these fail because they are not Serializable.
    // I'm not sure what the UserException problem is, but org.omg.CORBA.UserException
    // is an abstract class (see Client.throwUserException).
    // testIdentityHashMap seems to be failing because the elements come out in a
    // different order?
    // testExternalizable fails because readExternal and writeExternal are not written
    // to write the data.
    // I have not explored the other failures, but this list should be reduced to
    // those tests that are correct for reflective copy and cannot work for stream
    // copy.
    private static final String[] EXCLUDE_LIST = new String[] {
	"testObject", "testTimedObject", "testObjects", "testComplexClassArray",
	"testComplexClassAliasedArray", "testComplexClassGraph",
	"testUserException",
	"testRemoteStub", "testCORBAObject", "testInnerClass",
	"testExtendedInnerClass", "testNestedClass", "testLocalInner",
	"testAnonymousLocalInner", "testDynamicProxy", "testIdentityHashMap",
	"testExternalizable", "testTransientNonSerializableField1", 
	"testTransientNonSerializableField2", "testTransientNonSerializableField3", 
	"testNonSerializableSuperClass", "testExternalizableNonStaticContext" 
    } ;

    public JavaStreamTest() { }

    public JavaStreamTest( String name ) { super( name ) ; }

    public static void main( String[] args ) 
    { 
  	// Create an instance of the test suite that is used only
	// to invoke the makeSuite() method.  No name is needed here.
	Client root = new JavaStreamTest() ;
	Client.doMain( args, root ) ; 
    }

    public static Test suite() {
	Client root = new JavaStreamTest() ;
	return root.makeSuite() ;
    }
    
    public ObjectCopierFactory getCopierFactory( )
    {
	return CopyobjectDefaults.makeJavaStreamObjectCopierFactory( ) ;
    }

    public boolean isTestExcluded()
    {
	String testName = getName() ;
	for (int ctr=0; ctr<EXCLUDE_LIST.length; ctr++) 
	    if (testName.equals( EXCLUDE_LIST[ctr]))
		return true ;

	return false ;
    }

    public Client makeTest( String name ) 
    {
	return new JavaStreamTest( name ) ;
    }
}
