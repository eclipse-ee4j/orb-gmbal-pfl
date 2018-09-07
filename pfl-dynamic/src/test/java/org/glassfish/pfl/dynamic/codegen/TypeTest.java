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
import org.glassfish.pfl.dynamic.codegen.spi.Type;

public class TypeTest extends TestCase {
    private static final boolean DEBUG = false ;

    public TypeTest() {
	super() ;
    }

    public TypeTest(String name ) {
	super( name ) ;
    }

    public void test_void() {
	Type t = Type._void() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 0 ) ;
	assertFalse( t.isNumber() ) ;
	assertEquals( t.signature(), "V" ) ;
    }

    public void test_null() {
	Type t = Type._null() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertFalse( t.isNumber() ) ;
	assertEquals( t.signature(), "N" ) ;
    }

    public void test_boolean() {
	Type t = Type._boolean() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertFalse( t.isNumber() ) ;
	assertEquals( t.signature(), "Z" ) ;
    }

    public void test_byte() {
	Type t = Type._byte() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertTrue( t.isNumber() ) ;
	assertEquals( t.signature(), "B" ) ;
    }

    public void test_char() {
	Type t = Type._char() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertTrue( t.isNumber() ) ;
	assertEquals( t.signature(), "C" ) ;
    }

    public void test_short() {
	Type t = Type._short() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertTrue( t.isNumber() ) ;
	assertEquals( t.signature(), "S" ) ;
    }

    public void test_int() {
	Type t = Type._int() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertTrue( t.isNumber() ) ;
	assertEquals( t.signature(), "I" ) ;
    }

    public void test_long() {
	Type t = Type._long() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 2 ) ;
	assertTrue( t.isNumber() ) ;
	assertEquals( t.signature(), "J" ) ;
    }

    public void test_float() {
	Type t = Type._float() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertTrue( t.isNumber() ) ;
	assertEquals( t.signature(), "F" ) ;
    }

    public void test_double() {
	Type t = Type._double() ;
	assertTrue( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 2 ) ;
	assertTrue( t.isNumber() ) ;
	assertEquals( t.signature(), "D" ) ;
    }

    public void test_int_array() {
	Type t = Type._array( Type._int() ) ;
	assertFalse( t.isPrimitive() ) ;
	assertTrue( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertFalse( t.isNumber() ) ;
	assertEquals( t.memberType(), Type._int() ) ;
	assertEquals( t.signature(), "[I" ) ;
    }

    public void test_string_class() {
	Type t = Type._String();
	assertFalse( t.isPrimitive() ) ;
	assertFalse( t.isArray() ) ;
	assertEquals( t.size(), 1 ) ;
	assertFalse( t.isNumber() ) ;
	assertEquals( t.signature(), "Ljava/lang/String;" ) ;
	assertEquals( t.getTypeClass(), java.lang.String.class ) ;
    }

    private static final Type[] TYPE_DATA = {
	Type._void(),
	Type._null(),
	Type._boolean(),
	Type._byte(),
	Type._char(),
	Type._short(),
	Type._int(),
	Type._long(),
	Type._float(),
	Type._double(),
	Type._Object()
    } ;

    // Expected result for X.hasPrimitiveNarrowingConversionFrom( Y )
    // (That is, there is a primitive narrowing conversion from Y to X):
    //
    boolean[][] PRIM_NARROW_CONVERSION_DATA = {
	//	      void   null   bool   byte   char   short  int    long   float  double Object
	/*void*/    { false, false, false, false, false, false, false, false, false, false, false },
	/*null*/    { false, false, false, false, false, false, false, false, false, false, false },
	/*boolean*/ { false, false, false, false, false, false, false, false, false, false, false },
	/*byte*/    { false, false, false, false, true,  false, false, false, false, false, false },
	/*char*/    { false, false, false, true,  false, true,  false, false, false, false, false },
	/*short*/   { false, false, false, true,  true,  false, false, false, false, false, false },
	/*int*/     { false, false, false, true,  true,  true,  false, false, false, false, false },
	/*long*/    { false, false, false, true,  true,  true,  true,  false, false, false, false },
	/*float*/   { false, false, false, true,  true,  true,  true,  true,  false, false, false },
	/*double*/  { false, false, false, true,  true,  true,  true,  true,  true,  false, false },
	/*object*/  { false, false, false, false, false, false, false, false, false, false, false }
    } ;

    public void testHasPrimitiveNarrowingConversion() {
	int errorCount = 0 ;

	for (int y=0; y<TYPE_DATA.length; y++)
	    for (int x=0; x<TYPE_DATA.length; x++) {
		boolean expected = PRIM_NARROW_CONVERSION_DATA[y][x] ;
		boolean result = TYPE_DATA[x].hasPrimitiveNarrowingConversionFrom( 
		    TYPE_DATA[y] ) ;
		if ( result != expected ) {
		    errorCount++ ;
		    System.out.println( "Error on " + TYPE_DATA[x].name() 
			+ ".hasPrimitiveNarrowingConversionFrom( " 
			+ TYPE_DATA[y].name() + " ): expected result was " 
			+ expected ) ;
		}
	    }

	assertTrue( errorCount == 0 ) ;
    }

    // Expected result for X.hasPrimitiveWideningConversionFrom( Y ):
    //
    boolean[][] PRIM_WIDEN_CONVERSION_DATA = {
	//	      void   null   bool   byte   char   short  int    long   float  double Object
	/*void*/    { false, false, false, false, false, false, false, false, false, false, false },
	/*null*/    { false, false, false, false, false, false, false, false, false, false, false },
	/*boolean*/ { false, false, false, false, false, false, false, false, false, false, false },
	/*byte*/    { false, false, false, false, false, true,  true,  true,  true,  true,  false },
	/*char*/    { false, false, false, false, false, false, true,  true,  true,  true,  false },
	/*short*/   { false, false, false, false, false, false, true,  true,  true,  true,  false },
	/*int*/     { false, false, false, false, false, false, false, true,  true,  true,  false },
	/*long*/    { false, false, false, false, false, false, false, false, true,  true,  false },
	/*float*/   { false, false, false, false, false, false, false, false, false, true,  false },
	/*double*/  { false, false, false, false, false, false, false, false, false, false, false },
	/*object*/  { false, false, false, false, false, false, false, false, false, false, false }
    } ;

    public void testHasPrimitiveWideningConversion() {
	int errorCount = 0 ;

	for (int y=0; y<TYPE_DATA.length; y++)
	    for (int x=0; x<TYPE_DATA.length; x++) {
		boolean expected = PRIM_WIDEN_CONVERSION_DATA[y][x] ;
		boolean result = TYPE_DATA[x].hasPrimitiveWideningConversionFrom( 
		    TYPE_DATA[y] ) ;
		if ( result != expected ) {
		    errorCount++ ;
		    System.out.println( "Error on " + TYPE_DATA[x].name() 
			+ ".hasPrimitiveWideningConversionFrom( " 
			+ TYPE_DATA[y].name() + " ): expected result was " 
			+ expected ) ;
		}
	    }

	assertTrue( errorCount == 0 ) ;
    }

    public interface ClassA {
	int foo() ;
    } 

    public interface ClassB extends ClassA {}

    public final class ClassD implements ClassA {
	public int foo() {
	    return 0 ;
	}
    }

    public class ClassC implements ClassB {
	public int foo() {
	    return 1 ;
	}
    }

    public interface ClassE {
	String foo() ;
    }

    private static final Type[] CLASS_TYPE_DATA = {
	Type._int(),
	Type._Object(),
	Type._null(),
	Type._Cloneable(),
	Type.type( ClassA.class ),
	Type.type( ClassB.class ),
	Type.type( ClassC.class ),
	Type.type( ClassD.class ),
	Type.type( ClassE.class ),
	Type._array( Type.type( ClassA.class ) ),
	Type._array( Type.type( ClassB.class ) ),
	Type._array( Type.type( ClassC.class ) ),
	Type._array( Type.type( ClassD.class ) ),
	Type._array( Type.type( ClassE.class ) ),
	Type._array( Type._int() )
    } ;

    // Expected results for X.hasReferenceNarrowingConversionFrom( Y ):
    // 
    boolean[][] REF_NARROW_CONVERSION_DATA = {
	//	      int    Object null   Clone  A	 B      C      D      E      A[]    B[]    C[]    D[]    E[]    int[]
	/*int*/	    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*Object*/  { false, true,  false, true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  },
	/*null*/    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*Clone*/   { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*A*/	    { false, false, false, false, false, true,  true,  true,  false, false, false, false, false, false, false },
	/*B*/	    { false, false, false, false, false, false, true,  false, false, false, false, false, false, false, false },
	/*C*/	    { false, false, false, true,  true,  true,  false, false, false, false, false, false, false, false, false },
	/*D*/	    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*E*/	    { false, false, false, false, false, false, true,  false, false, false, false, false, false, false, false },
	/*A[]*/	    { false, false, false, false, false, false, false, false, false, false, true,  true,  true,  false, false },
	/*B[]*/	    { false, false, false, false, false, false, false, false, false, false, false, true,  false, false, false },
	/*C[]*/	    { false, false, false, false, false, false, false, false, false, true,  true,  false, false, false, false },
	/*D[]*/	    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*E[]*/	    { false, false, false, false, false, false, false, false, false, false, false, true,  false, false, false },
	/*int[]*/   { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
    } ;

    public void testHasReferenceNarrowingConversion() {
	int errorCount = 0 ;

	for (int y=0; y<CLASS_TYPE_DATA.length; y++)
	    for (int x=0; x<CLASS_TYPE_DATA.length; x++) {
		boolean expected = REF_NARROW_CONVERSION_DATA[y][x] ;
		boolean result = CLASS_TYPE_DATA[x].hasReferenceNarrowingConversionFrom( 
		    CLASS_TYPE_DATA[y] ) ;
		if ( result != expected ) {
		    errorCount++ ;
                    if (DEBUG) 
                        System.out.println( "Error on " + CLASS_TYPE_DATA[x].name() 
                            + ".hasReferenceNarrowingConversionFrom( " 
                            + CLASS_TYPE_DATA[y].name() + " ): expected result was " 
                            + expected ) ;
		}
	    }

        if (errorCount >= 0)
            System.out.println( "REMINDER: need to work on testHashReferenceNarrowingConversion" ) ;

	// Fix this later
	// assertTrue( errorCount == 0 ) ;
    }

    // Expected results for X.hasReferenceWideningConversionFrom( Y ):
    // public interface ClassA {
    // int foo() ;
    // } 
    // 
    // public interface ClassB extends ClassA {}
    // 
    // public final class ClassD implements ClassA {}
    // 
    // public class ClassC implements ClassB {}
    // 
    // public interface ClassE {
    // String foo() ;
    // }
    // 
    boolean[][] REF_WIDENING_CONVERSION_DATA = {
	//	      int    Object null   Clone  A	 B      C      D      E      A[]    B[]    C[]    D[]    E[]    int[]
	/*int*/	    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*Object*/  { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*null*/    { false, true,  false, true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true  },
	/*Clone*/   { false, true,  false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*A*/	    { false, true,  false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*B*/	    { false, true,  false, false, true,  false, false, false, false, false, false, false, false, false, false },
	/*C*/	    { false, true,  false, false, true,  true,  false, false, false, false, false, false, false, false, false },
	/*D*/	    { false, true,  false, false, true,  false, false, false, false, false, false, false, false, false, false },
	/*E*/	    { false, true,  false, false, false, false, false, false, false, false, false, false, false, false, false },
	/*A[]*/	    { false, true,  false, true,  false, false, false, false, false, false, false, false, false, false, false },
	/*B[]*/	    { false, true,  false, true,  false, false, false, false, false, true,  false, false, false, false, false },
	/*C[]*/	    { false, true,  false, true,  false, false, false, false, false, true,  true,  false, false, false, false },
	/*D[]*/	    { false, true,  false, true,  false, false, false, false, false, true,  false, false, false, false, false },
	/*E[]*/	    { false, true,  false, true,  false, false, false, false, false, false, false, false, false, false, false },
	/*int[]*/   { false, true,  false, true,  false, false, false, false, false, false, false, false, false, false, false }
    } ;

    public void testHasReferenceWideningConversion() {
	int errorCount = 0 ;

	for (int y=0; y<CLASS_TYPE_DATA.length; y++)
	    for (int x=0; x<CLASS_TYPE_DATA.length; x++) {
		boolean expected = REF_WIDENING_CONVERSION_DATA[y][x] ;
		boolean result = CLASS_TYPE_DATA[x].hasReferenceWideningConversionFrom( 
		    CLASS_TYPE_DATA[y] ) ;
		if ( result != expected ) {
		    errorCount++ ;
                    if (DEBUG)
                        System.out.println( "Error on " + CLASS_TYPE_DATA[x].name() 
                            + ".hasReferenceWideningConversionFrom( " 
                            + CLASS_TYPE_DATA[y].name() + " ): expected result was " 
                            + expected ) ;
		}
	    }

        if (errorCount >= 0)
            System.out.println( "REMINDER: need to work on testHashReferenceWideningConversion" ) ;

	// Fix this later
	// assertTrue( errorCount == 0 ) ;
    }
}
