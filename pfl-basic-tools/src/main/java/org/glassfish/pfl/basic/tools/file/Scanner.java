/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.tools.file ;

import org.glassfish.pfl.basic.func.UnaryPredicate;
import java.io.File ;
import java.io.IOException ;

import java.util.List ;
import java.util.ArrayList ;

import java.util.StringTokenizer ;


import static java.util.Arrays.asList ;

/** Recursively scan directories to process files.
 */
public class Scanner {
    private final List<File> roots ;
    private final int verbose ;
    private List<String> patternsToSkip ;

    public Scanner( int verbose, final List<File> files ) {
	this.roots = files ;
	this.verbose = verbose ;
	patternsToSkip = new ArrayList<String>() ;
    }

    public Scanner( final int verbose, final File... files ) {
	this( verbose, asList( files ) ) ;
    }

    /** Add a pattern that defines a directory to skip.  We only need really simple
     * patterns: just a single name that must match a component of a directory name
     * exactly. 
     */
    public void addDirectoryToSkip( final String pattern ) {
	patternsToSkip.add( pattern ) ;
    }

    /** Action interface passed to scan method to act on files.
     * Terminates scan if it returns false.
     */
    public interface Action extends UnaryPredicate<FileWrapper> {}

    /** Scan all files reachable from roots.  Does a depth-first search. 
     * Ignores all directories (and their contents) that match an entry
     * in patternsToSkip.  Passes each file (not directories) to the action.
     * If action returns false, scan terminates.  The result of the scan is
     * the result of the last action call.
     */
    public boolean scan( final Action action ) throws IOException {
	boolean result = true ;
	for (File file : roots) {
	    // Skip non-existent roots
	    if (file.exists()) {
		if (file.isDirectory()) {
		    if (!skipDirectory(file)) {
			result = doScan( file, action ) ;
		    }
		} else  {
		    final FileWrapper fw = new FileWrapper( file ) ;
		    result = action.evaluate( fw ) ;
		}

		if (!result) {
                    break;
                }
	    }
	}

	return result ;
    }

    private boolean doScan( final File file, final Action action )  
	throws IOException {
		
	boolean result = true ;
	if (file.isDirectory()) {
	    if (!skipDirectory(file)) {
		for (File f : file.listFiles()) {
		    result = doScan( f, action ) ;
		    if (!result) {
                        break;
                    }
		}
	    }
	} else {
	    final FileWrapper fw = new FileWrapper( file ) ;
	    result = action.evaluate( fw ) ;
	}

	return result ;
    }

    private boolean skipDirectory( final File file ) {
	for (String pattern : patternsToSkip) {
	    String absPath = file.getAbsolutePath() ;
	    if (match( pattern, absPath)) {
		if (verbose > 1) {
                    System.out.println("Scanner: Skipping directory " + absPath + "(pattern " + pattern + ")");
                }
		return true ;
	    }
	}

	if (verbose > 1) {
            System.out.println("Scanner: Not skipping directory " + file);
        }
	return false ;
    }

    // This where we could support more complex pattern matches, if desired.
    private boolean match( final String pattern, final String fname ) {
	final String separator = File.separator ;

	// Don't use String.split here because its argument is a regular
        // expression, and some file separator characters could be confused
        // with regex meta-characters.

	final StringTokenizer st = new StringTokenizer( fname, separator ) ;
	while (st.hasMoreTokens()) {
	    final String token = st.nextToken() ;
	    if (pattern.equals( token )) {
		if (verbose > 1) {
                    System.out.println("fname " + fname
                        + " matched on pattern " + pattern);
                }
		return true ;
	    }
	}

	return false ;
    }
}
