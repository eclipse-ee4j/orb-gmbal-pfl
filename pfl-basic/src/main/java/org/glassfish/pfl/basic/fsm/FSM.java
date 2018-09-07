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
 * An FSM is used to represent an instance of a finite state machine
 * which has a transition function represented by an instance of
 * StateEngine.  An instance of an FSM may be created either by calling
 * StateEngine.makeFSM( startState ) on a state engine, or by extending FSMImpl and
 * using a constructor.  Using FSMImpl as a base class is convenient if
 * additional state is associated with the FSM beyond that encoded 
 * by the current state.  This is especially convenient if an action
 * needs some additional information.  For example, counters are best
 * handled by special actions rather than encoding a bounded counter
 * in a state machine.  It is also possible to create a class that
 * implements the FSM interface by delegating to an FSM instance
 * created by StateEngine.makeFSM.
 *
 * @author Ken Cavanaugh
 */
public interface FSM
{
    /** Return the state engine used to create this FSM.
     */
    public StateEngine getStateEngine() ;

    /** Get the parent state machine.
     */
    public FSM getParent() ;

    /** Set the parent state machine.
     */
    public void setParent( FSM fsm ) ;

    /** Set the current state of this FSM.  May not be called
     * inside a transition action, or from a State method.
     * Only here for use by the StateEngine.
     */
    public void setState( State state ) ;

    /** Get the current state of this FSM.
    */
    public State getState() ;
}

// end of FSM.java
