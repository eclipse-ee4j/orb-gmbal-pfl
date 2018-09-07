/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.fsm;

/** An Action may be associated with a transition between to 
 * states.  The transition action doIt method is called
 * before the transition takes place.
 *
 * @author Ken Cavanaugh
 */
public interface Action {
    /** Called by the state engine to perform an action
    * before a state transition takes place.  The FSM is 
    * passed so that the Action may examine the state of
    * the FSM.   Note that an
    * action should complete in a timely manner.  If the state machine
    * is used for concurrency control with multiple threads, the
    * action must not allow multiple threads to run simultaneously
    * in the state machine, as the state could be corrupted.
    * Any exception thrown by the Action for the transition
    * will be propagated to doIt.  
    * @param fsm is the state machine causing this action.
    * @param in is the input that caused the transition.
    */
    public void doIt( FSM fsm, Input in ) ;

    public abstract class Base extends NameBase implements Action {
	public static Action compose( final Action arg1, final Action arg2 ) {
	    return new Base( 
		"compose(" + arg1.toString() + "," + arg2.toString() + ")" ) {

                @Override
		public void doIt( final FSM fsm, final Input in ) {
		    arg1.doIt( fsm, in ) ;
		    arg2.doIt( fsm, in ) ;
		}
	    } ;
	}

	public Base( String name ) { super( name ) ; } 
    }
}

// end of Action.java
