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
 * Note that this is optional; an FSM implementation may directly
 * implement the FSM interface if desired.
 *
 * @author Ken Cavanaugh
 */
public class FSMImpl implements FSM
{
    private FSM parent ;
    private State state ;
    private StateEngine stateEngine ;

    public FSMImpl( StateEngine se, State initialState )
    {
	parent = null ;
	state = initialState ;
	stateEngine = se ;
	if (!(se.getStates( State.Kind.INITIAL ).contains( initialState ))) {
            throw new IllegalStateException("Error: State " + initialState +
                " is not an initial state");
        }
    }

    @Override
    public FSM getParent() {
	return parent ;
    }

    @Override
    public void setParent( FSM fsm ) {
	parent = fsm ;
    }
    
    @Override
    public StateEngine getStateEngine() {
	return stateEngine ;
    }

    /** Return the current state.
    */
    @Override
    public State getState() {
	return state ;
    }

    @Override
    public void setState( State nextState ) 
    {
	state = nextState ;
    }
}

// end of FSMImpl.java

