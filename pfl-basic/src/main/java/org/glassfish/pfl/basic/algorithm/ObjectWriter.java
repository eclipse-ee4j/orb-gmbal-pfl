/*
 * Copyright (c) 2002, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm ;

import java.util.Arrays ;

public abstract class ObjectWriter {
    public static ObjectWriter make( boolean isIndenting, 
	int initialLevel, int increment )
    {
	if (isIndenting) {
            return new IndentingObjectWriter(initialLevel, increment);
        } else {
            return new SimpleObjectWriter();
        }
    }

    public abstract void startObject( String str ) ;
    
    public abstract void startObject( Object obj ) ;

    public abstract void startElement() ;

    public abstract void endElement() ;

    public abstract void endObject() ;

    @Override
    public String toString() { return result.toString() ; }

    public void append( boolean arg ) { result.append( arg ) ; } 

    public void append( char arg ) { result.append( arg ) ; } 

    public void append( short arg ) { result.append( arg ) ; } 

    public void append( int arg ) { result.append( arg ) ; } 

    public void append( long arg ) { result.append( arg ) ; } 

    public void append( float arg ) { result.append( arg ) ; } 

    public void append( double arg ) { result.append( arg ) ; } 

    public void append( String arg ) { result.append( arg ) ; } 
    
    public void append( Object arg ) { result.append( arg.toString() ) ; } 

//=================================================================================================
// Implementation
//=================================================================================================

    protected StringBuffer result ;

    protected ObjectWriter()
    {
	result = new StringBuffer() ;
    }

    protected void appendObjectHeader( Object obj ) 
    {
	result.append( obj.getClass().getName() ) ;
	result.append( "<" ) ;
	result.append( System.identityHashCode( obj ) ) ;
	result.append( ">" ) ;
	Class compClass = obj.getClass().getComponentType() ;

	if (compClass != null) {
	    result.append( "[" ) ;
	    if (compClass == boolean.class) {
		boolean[] arr = (boolean[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else if (compClass == byte.class) {
		byte[] arr = (byte[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else if (compClass == short.class) {
		short[] arr = (short[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else if (compClass == int.class) {
		int[] arr = (int[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else if (compClass == long.class) {
		long[] arr = (long[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else if (compClass == char.class) {
		char[] arr = (char[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else if (compClass == float.class) {
		float[] arr = (float[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else if (compClass == double.class) {
		double[] arr = (double[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    } else { // array of object
		java.lang.Object[] arr = (java.lang.Object[])obj ;
		result.append( arr.length ) ;
		result.append( "]" ) ;
	    }
	}

	result.append( "(" ) ;
    }

    /** Expected patterns:
    * startObject endObject( str )
    *	header( elem )\n
    * startObject ( startElement append* endElement ) * endObject
    *	header(\n
    *	    append*\n *
    *	)\n
    */
    private static class IndentingObjectWriter extends ObjectWriter {
	private int level ;
	private int increment ;

	public IndentingObjectWriter( int initialLevel, int increment )
	{
	    this.level = initialLevel ;
	    this.increment = increment ;
	    startLine() ;
	}

	private void startLine() 
	{
	    char[] fill = new char[ level * increment ] ;
	    Arrays.fill( fill, ' ' ) ;
	    result.append( fill ) ;
	}

        public void startObject( String str ) {
            append( str ) ;
            append( "(" ) ;
            level++ ;
        }
        
	public void startObject( java.lang.Object obj ) 
	{
	    appendObjectHeader( obj ) ;
	    level++ ;
	}

	public void startElement() 
	{
	    result.append( "\n" ) ;
	    startLine() ;
	}

	public void endElement() 
	{
	}

	public void endObject( ) 
	{
	    level-- ;
            result.append( ")" ) ;
	    result.append( "\n" ) ;
	    startLine() ;
	}
    }
    
    private static class SimpleObjectWriter extends ObjectWriter {
        public void startObject( String str  ) {
            append( str ) ;
            append( "(" ) ;
        }
        
	public void startObject( java.lang.Object obj ) 
	{
	    appendObjectHeader( obj ) ;
	    result.append( " " ) ;
	}

	public void startElement() 
	{
	    result.append( " " ) ;
	}

	public void endElement() 
	{
	}

	public void endObject() 
	{
	    result.append( ")" ) ;
	}
    }
}
