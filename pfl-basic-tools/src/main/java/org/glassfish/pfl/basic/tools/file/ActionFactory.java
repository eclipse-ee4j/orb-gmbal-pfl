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

public class ActionFactory {
    private final int verbose ;
    private final boolean dryRun ;

    public ActionFactory() {
	this( 0, false ) ;
    }

    public ActionFactory( final int verbose ) {
	this( verbose, false ) ;
    }

    public ActionFactory( final int verbose, final boolean dryRun ) {
	this.verbose = verbose ;
	this.dryRun = dryRun ;
    }

    /** returns an action that returns true.  If verbose is true, the action
     * also displays the FileWrapper that was passed to it.
     * @return The skip action.
     */
   public Scanner.Action getSkipAction() {
	return new Scanner.Action() {
            @Override
	    public String toString() {
		return "SkipAction" ;
	    }

            @Override
	    public boolean evaluate( final FileWrapper fw ) {
		if (verbose > 1) {
                    System.out.println("SkipAction called on " + fw);
                }
		
		return true ;
	    }
	} ;
    }

    /** returns an action that returns false.  If verbose is true, the action
     * also displays the FileWrapper that was passed to it.
     * @return The stop action.
     */
   public Scanner.Action getStopAction() {
	return new Scanner.Action() {
            @Override
	    public String toString() {
		return "StopAction" ;
	    }

            @Override
	    public boolean evaluate( final FileWrapper fw ) {
		if (verbose > 1) {
                    System.out.println("StopAction called on " + fw);
                }

		return false ;
	    }
	} ;
    }

    public Recognizer getRecognizerAction() {
	return new Recognizer( verbose, dryRun ) ;
    }
}
