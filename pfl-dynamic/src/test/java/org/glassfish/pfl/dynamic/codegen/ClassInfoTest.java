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

import junit.framework.TestCase;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo;
import org.glassfish.pfl.dynamic.codegen.spi.FieldInfo;
import org.glassfish.pfl.dynamic.codegen.spi.Type;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.reflect.Modifier.*;

public class ClassInfoTest extends TestCase {
    public ClassInfoTest() {
	super() ;
    }

    public ClassInfoTest(String name ) {
	super( name ) ;
    }

    // Define a graph of test classes and interfaces

    public interface A {} 
    public interface B {}
    public interface C extends A {}
    public interface D extends C, B {}
    public static class C1 {
	private int x ;
	public int y ;
    }
    public static class C2 extends C1 implements C {
	private int z ;
    }
    public static class C3 extends C2 implements D {
	protected int w ;
	int t ;
    }
    public static class C4 extends C3 {
    }

    private static ClassInfo ObjectType = Type._Object().classInfo() ;
    private static ClassInfo AType = Type.type( A.class ).classInfo() ;
    private static ClassInfo BType = Type.type( B.class ).classInfo() ;
    private static ClassInfo CType = Type.type( C.class ).classInfo() ;
    private static ClassInfo DType = Type.type( D.class ).classInfo() ;
    private static ClassInfo C1Type = Type.type( C1.class ).classInfo() ;
    private static ClassInfo C2Type = Type.type( C2.class ).classInfo() ;
    private static ClassInfo C3Type = Type.type( C3.class ).classInfo() ;

    private void expectFieldInfo( FieldInfo finfo, ClassInfo cinfo, int modifiers,
	String name, Type type ) {

	assertTrue( finfo != null ) ;
	assertEquals( finfo.myClassInfo(), cinfo ) ;
	assertEquals( finfo.modifiers(), modifiers ) ;
	assertEquals( finfo.name(), name ) ;
	assertEquals( finfo.type(), type ) ;
    }

    public void testFindFieldInfo() {
	expectFieldInfo( C3Type.findFieldInfo( "t" ), C3Type, 0, 
	    "t", Type._int() ) ;
	expectFieldInfo( C3Type.findFieldInfo( "w" ), C3Type, PROTECTED, 
	    "w", Type._int() ) ;
	expectFieldInfo( C3Type.findFieldInfo( "z" ), C2Type, PRIVATE, 
	    "z", Type._int() ) ;
	expectFieldInfo( C3Type.findFieldInfo( "x" ), C1Type, PRIVATE, 
	    "x", Type._int() ) ;
	expectFieldInfo( C3Type.findFieldInfo( "y" ), C1Type, PUBLIC, 
	    "y", Type._int() ) ;
    }

    public void testFieldInfo() {
	Map<String,FieldInfo> infoMap = C3Type.fieldInfo() ;
	Set<String> names = infoMap.keySet() ;
	Set<String> expectedNames = new HashSet<String>() ;
	expectedNames.add( "t" ) ;
	expectedNames.add( "w" ) ;
	assertEquals( names, expectedNames ) ;
	FieldInfo finfo = infoMap.get( "t" ) ;
	expectFieldInfo( finfo, C3Type, 0, "t", Type._int() ) ;
    }

    public void testIsAccessibleInContext() {
	FieldInfo x_privateInC1 = C1Type.findFieldInfo( "x" ) ;
	FieldInfo y_public = C1Type.findFieldInfo( "y" ) ;
	FieldInfo z_privateInC2 = C2Type.findFieldInfo( "z" ) ;
	FieldInfo w_protectedInC3 = C3Type.findFieldInfo( "w" ) ;
	FieldInfo t_defaultInC3 = C3Type.findFieldInfo( "t" ) ;

	assertTrue( x_privateInC1.isAccessibleInContext( C1Type, C1Type ) ) ;
	assertTrue( x_privateInC1.isAccessibleInContext( C1Type, C2Type ) ) ;
	assertFalse( x_privateInC1.isAccessibleInContext( C2Type, C1Type ) ) ;
	assertFalse( x_privateInC1.isAccessibleInContext( C2Type, C2Type ) ) ;

	assertTrue( y_public.isAccessibleInContext( C1Type, C1Type ) ) ;
	assertTrue( y_public.isAccessibleInContext( C2Type, C2Type ) ) ;
	assertTrue( y_public.isAccessibleInContext( C3Type, C3Type ) ) ;

	assertFalse( z_privateInC2.isAccessibleInContext( C1Type, C1Type ) ) ;
	assertFalse( z_privateInC2.isAccessibleInContext( C1Type, C2Type ) ) ;
	assertTrue( z_privateInC2.isAccessibleInContext( C2Type, C1Type ) ) ;
	assertTrue( z_privateInC2.isAccessibleInContext( C2Type, C2Type ) ) ;

	assertTrue( w_protectedInC3.isAccessibleInContext( C3Type, C3Type ) ) ;
	assertTrue( t_defaultInC3.isAccessibleInContext( C3Type, C3Type ) ) ;
    }

    public void testClassName() {
	assertTrue( Type._String().classInfo().className().equals( "String" ) ) ;
    }

    public void testPkgName() {
	assertTrue( Type._String().classInfo().pkgName().equals( "java.lang" ) ) ;
    }

    public void testIsSubclass() {
	assertTrue( AType.isSubclass( AType ) ) ;
	assertTrue( AType.isSubclass( ObjectType ) ) ;

	assertTrue( AType.isSubclass( ObjectType ) ) ;
	assertFalse( ObjectType.isSubclass( AType ) ) ;

	assertFalse( BType.isSubclass( AType ) ) ;
	assertFalse( AType.isSubclass( BType ) ) ;

	assertTrue( CType.isSubclass( AType ) ) ;
	assertFalse( AType.isSubclass( CType ) ) ;

	assertTrue( DType.isSubclass( CType ) ) ;
	assertTrue( DType.isSubclass( BType ) ) ;
	assertTrue( DType.isSubclass( CType ) ) ;

	assertFalse( AType.isSubclass( DType ) ) ;
	assertFalse( BType.isSubclass( DType ) ) ;
	assertFalse( CType.isSubclass( DType ) ) ;

	assertTrue( C1Type.isSubclass( ObjectType ) ) ;
	assertFalse( ObjectType.isSubclass( C1Type ) ) ;

	assertTrue( C2Type.isSubclass( C1Type ) ) ;
	assertFalse( C1Type.isSubclass( C2Type ) ) ;

	assertTrue( C3Type.isSubclass( C2Type ) ) ;
	assertFalse( C2Type.isSubclass( C3Type ) ) ;

	assertTrue( C3Type.isSubclass( C1Type ) ) ;
	assertFalse( C1Type.isSubclass( C3Type ) ) ;

	assertTrue( C2Type.isSubclass( CType ) ) ;
	assertFalse( CType.isSubclass( C2Type ) ) ;

	assertTrue( C2Type.isSubclass( AType ) ) ;
	assertFalse( AType.isSubclass( C2Type ) ) ;

	assertTrue( C3Type.isSubclass( DType ) ) ;
	assertFalse( DType.isSubclass( C3Type ) ) ;

	assertTrue( C3Type.isSubclass( CType ) ) ;
	assertFalse( CType.isSubclass( C3Type ) ) ;

	assertTrue( C3Type.isSubclass( BType ) ) ;
	assertFalse( BType.isSubclass( C3Type ) ) ;

	assertTrue( C3Type.isSubclass( AType ) ) ;
	assertFalse( AType.isSubclass( C3Type ) ) ;
    }
}
