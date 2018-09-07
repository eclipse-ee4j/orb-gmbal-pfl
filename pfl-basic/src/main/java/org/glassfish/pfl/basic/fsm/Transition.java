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

/** This represents an action, guard, and next state for a transition.
 * Instances of this class may only be created by the StateEngine.
 */
public final class Transition {
    private static Guard trueGuard = new Guard.Base( "true" ) {
        @Override
	public Guard.Result evaluate( FSM fsm, Input in ) 
	{
	    return Guard.Result.ENABLED ;
	}
    } ;

    private Guard guard ;
    private Action action ;
    private State nextState ;

    Transition( Action action, State nextState )
    {
	this.guard = trueGuard ;
	this.action = action ;
	this.nextState = nextState ;
    }

    Transition( Guard guard, Action action, State nextState )
    {
	this.guard = guard ;
	this.action = action ;
	this.nextState = nextState ;
    }

    @Override
    public String toString() {
	return "Transition[action=" + action + " guard=" + guard +
	    " nextState=" + nextState + "]" ;
    }

    public Action getAction() { return action ; }
    public Guard getGuard() { return guard ; }
    public State getNextState() { return nextState ; }
}

