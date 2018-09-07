/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.fsm ;

/**
 * This is the main class that represents an instance of a state machine
 * using a state engine.  It may be used as a base class, in which case
 * the guards and actions have access to the derived class.
 *
 * @author Ken Cavanaugh
 */
public class Runner {
    private boolean debug ;
    private FSM current ;

    /** Create a new Runner with fsm on top of the stack.
     */
    public Runner( FSM fsm ) {
	this( fsm, false ) ;
    }

    public Runner( FSM fsm, boolean debug ) {
	current = fsm ;
	this.debug = debug ;
    }

    /** Return the top fsm on the stack.
     */
    public FSM peek() {
	return current ;
    }

    /** Push a new fsm onto the stack.
     */
    public void push( FSM fsm ) {
	fsm.setParent( current ) ;
	current = fsm ;
    }

    public FSM pop() {
	FSM result = current ;
	current = current.getParent() ;
	return result ;
    }

    /** Return true if the stack is empty, which means that the runner is
     * finished.
     */
    public boolean done() {
	return current == null ;
    }

    /** Perform the transition for the given input in the current state.  
     * This proceeds as follows:
    * <p>Let S be the current state of the FSM.  
    * If there are guarded actions for S with input in, evaluate their guards 
    * successively until all have been evaluted, or one returns a 
    * non-DISABLED Result. 
    * <ol>
    * <li>If a DEFERED result is returned, retry the input
    * <li>If a ENABLED result is returned, the action for the guarded action 
    * is the current action
    * <li>Otherwise there is no enabled action.  If S has a default action 
    * and next state, use them; otherwise use the state engine default action 
    * (the next state is always the current state).
    * </ol>
    * After the action is available, the transition proceeds as follows:
    * <ol>
    * <li>If the next state is not the current state, execute the current state 
    * postAction method.
    * <li>Execute the action.
    * <li>If the next state is not the current state, execute the next state 
    * preAction method.
    * <li>Set the current state to the next state.
    * </ol>
    */
    public void doIt( Input in ) {
	current.getStateEngine().doIt( this, in, debug ) ;
    }
}
