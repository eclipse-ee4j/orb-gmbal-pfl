/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain ;

import java.util.Map ;
import java.util.HashMap ;

/** A simple abstraction of a MultiSet, that is, a "set" that can contain
 * more than one copy of the same element.  I am implementing only the
 * bare minimum that is required for now.
 */
public class MultiSet<E> {
     private Map<E,Integer> contents = new HashMap<E,Integer>() ;

     public void add( E element ) {
	 Integer value = contents.get( element ) ;
	 if (value == null) {
	    value = 0 ;
	 }

	 value += 1 ;
	 contents.put( element, value ) ;
     }

     public void remove( E element ) {
	 Integer value = contents.get( element ) ;
	 if (value == null) {
	     return ;
	 }

	 value -= 1 ;

	 if (value == 0) {
	     contents.remove( element ) ;
	 } else {
	     contents.put( element, value ) ;
	 }
     }

     public boolean contains( E element ) {
	 Integer value = contents.get( element ) ;
	 if (value == null) {
	    value = 0 ;
	 }

	return value > 0 ;
     }

     /** Return the number of unique elements in this MultiSet.
      */
     public int size() {
	 return contents.keySet().size() ;
     }
     
     private static void shouldBeTrue( boolean val, String msg ) {
	 if (!val) 
	     System.out.println( msg ) ;
     }

     private static void shouldBeFalse( boolean val, String msg ) {
	 if (val) 
	     System.out.println( msg ) ;
     }

     public static void main( String[] args ) {
	MultiSet<String> mset = new MultiSet<String>() ;
	String s1 = "first" ;
	String s2 = "second" ;
	
	mset.add( s1 ) ;
	shouldBeTrue( mset.contains( s1 ), "mset does not contain s1 (1)" ) ;

	mset.add( s2 ) ;
	mset.add( s1 ) ;
	mset.remove( s1 ) ;
	shouldBeTrue( mset.contains( s1 ), "mset does not contain s1 (2)" ) ;
	mset.remove( s1 ) ;
	shouldBeFalse( mset.contains( s1 ), "mset still contains s1 (3)" ) ;
	shouldBeTrue( mset.contains( s2 ), "mset does not contain s2 (4)" ) ;
     }
}
