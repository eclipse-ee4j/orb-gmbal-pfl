/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl ;

import org.glassfish.pfl.basic.contain.Pair;

/** Some utilities for dealing with Java identifiers.
 *
 * @Author Ken Cavanaugh
 */
public abstract class Identifier {
    private Identifier() {}

    /** Check that name is a valid Java identifier.  No packages
     * are permitted here.
     */
    public static boolean isValidIdentifier( String name ) {
	if ((name == null) || (name.length() == 0))
	    return false ;

	if (!Character.isJavaIdentifierStart( name.charAt(0) ))
	    return false ;

	for (int ctr=1; ctr<name.length(); ctr++) {
	    if (!Character.isJavaIdentifierPart( name.charAt(ctr) ))
		return false ;
	}

	return true ;
    }

    /** Check that name is a valid full qualified Java identifier.
     */
    public static boolean isValidFullIdentifier( String name ) {
	if ((name == null) || (name.length() == 0))
	    return false ;

	// String.split seems to ignore trailing separators
	if (name.charAt(name.length()-1) == '.')
	    return false ;

	String[] arr = name.split( "\\." ) ;
	for (String str : arr) {
	    if (!isValidIdentifier( str )) {
		return false ;
	    }
	}

	return true ;
    }

    /** Assuming that isValidFullIdentifier( pkg ) and 
     * isValidIdentifier( ident ), reurn a fully qualifed
     * name for the identifier in the package.
     */
    public static String makeFQN( String pkg, String ident ) {
	if ((pkg != null) && !pkg.equals( "" ))
	    return pkg + '.' + ident ;
	else
	    return ident ;
    }

    public static Pair<String,String> splitFQN( String fqn ) {
	int count = fqn.lastIndexOf( '.' ) ;
	String pkg = (count<0) ? "" : fqn.substring( 0, count ) ;
	String cls = fqn.substring( count+1 ) ;
	return new Pair<String,String>( pkg, cls ) ;
    }
}
