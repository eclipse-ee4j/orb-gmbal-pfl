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

import java.io.IOException ;

import java.util.Map ;
import java.util.HashMap ;

/** Recognizes files according to patterns, and performs actions accordingly.
 */
public class Recognizer implements Scanner.Action {
    private final int verbose ;
    private final boolean dryRun ;
    private final Map<String,Scanner.Action> nameActions ;
    private final Map<String,Scanner.Action> suffixActions ;
    private Scanner.Action shellScriptAction ;
    private Scanner.Action defaultAction ;

    // Should only be constructed in ActionFactory.
    Recognizer( final int verbose, final boolean dryRun ) {
	this.verbose = verbose ;
	this.dryRun = dryRun ;

	nameActions = new HashMap<String,Scanner.Action>() ;
	suffixActions = new HashMap<String,Scanner.Action>() ;
	shellScriptAction = null ;
	defaultAction = new Scanner.Action() {
            @Override
	    public String toString() {
		return "Built-in Default Action" ;
	    }

            @Override
	    public boolean evaluate( FileWrapper fw ) {
		System.out.println( "No action defined for " + fw ) ;
		return false ;
	    }
	} ;
    }

    public void dump() {
	System.out.println( "Contents of Recognizer:" ) ;
	System.out.println( "verbose = " + verbose ) ;
	System.out.println( "dryRun = " + dryRun ) ;
	System.out.println( "Name actions:" ) ;
	for (Map.Entry<String,Scanner.Action> entry : nameActions.entrySet() ) {
	    System.out.println( "\tName = " + entry.getKey() 
		+ ", Action = " + entry.getValue() ) ;
	}

	System.out.println( "Suffix actions:" ) ;
	for (Map.Entry<String,Scanner.Action> entry : suffixActions.entrySet() ) {
	    System.out.println( "\tSuffix = " + entry.getKey() 
		+ ", Action = " + entry.getValue() ) ;
	}

	System.out.println( "Shell action:" + shellScriptAction ) ;

	System.out.println( "Default action:" + defaultAction ) ;
    }

    @Override
    public String toString() {
	return "Recognizer()" ;
    }

    public void addKnownName( final String name, final Scanner.Action action ) {
	nameActions.put( name, action ) ;
    }

    public void addKnownSuffix( final String suffix, final Scanner.Action action ) {
	suffixActions.put( suffix, action ) ;
    }

    /** If set, this defines the action taken for text files that start with the 
     * patter "#!", which is the standard for all *nix shell scripts.
     * If not set, such files are handled by the default action (if not otherwise
     * handled by name or suffix match.
     * @param action The action to perform on shell scripts.
     */
    public void setShellScriptAction( final Scanner.Action action ) {
	shellScriptAction = action ;
    }

    /** This defines the default action.  The standard default action prints
     * a message identifying the File that was not processed, and returns false.
     * This allows overriding the default action.
     * @param action The default action is nothing else matches.
     */
    public void setDefaultAction( final Scanner.Action action ) {
	if (action != null) {
            defaultAction = action;
        }
    }

    /** Apply the action that matches the classification of this file.
     * Returns the result of that action.
     * @param file The file to act upon.
     * @return result of matching action.
     */
    @Override
    public boolean evaluate( final FileWrapper file ) {
	final String name = file.getName() ;
	Scanner.Action action = nameActions.get( name ) ;

	if (action == null) {
	    // look for suffix action
	    final int dotIndex = name.lastIndexOf( '.' ) ;
	    if (dotIndex >= 0) {
		String suffix = name.substring( dotIndex + 1 ) ;
		action = suffixActions.get( suffix ) ;
	    }
	}

	if (action == null) {
	    try {
		// see if this is a shell script
		file.open( FileWrapper.OpenMode.READ ) ; 
		final String str = file.readLine() ;
		if ((str != null) && str.startsWith( "#!" )) {
		    action = shellScriptAction ;
		}
		file.close() ;
	    } catch (IOException exc) {
		// action is still null
		System.out.println( "Could not read file " + file + " to check for shell script" ) ;
	    }
	}

	if (action == null) {
            action = defaultAction;
        }

	if (verbose > 1) {
	    System.out.println( 
		"Recognizer: calling action " + action 
		+ " on file " + file ) ;
	}

	if (!dryRun) {
            return action.evaluate(file);
        }

	return true ;
    }
}

