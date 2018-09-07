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

import java.util.Collections ;
import java.util.Map ;
import java.util.HashMap ;
import java.util.Set ;
import java.util.HashSet ;

/** Base class for all states in a StateEngine.  This must be used
* as the base class for all states in transitions added to a StateEngine.
*/
public class State extends NameBase {
    /** Kind of state.  A StateEngine must have at least one INITIAL state.
     * An FSM may only be created in an INITIAL state.
     * It may have 0 or more FINAL states. A FINAL state may only be the
     * target of a state transition.  If a state engine is used as a 
     * submachine, it must have at least one final state.
     * <P>
     * A REFERENCE state is handled specially.  It is used to call into
     * another state engine much as a normal subroutine call.  A 
     * REFERENCE state (like a FINAL state) may not have any transitions
     * that leave it.  The returnAction method on a REFERENCE state is 
     * responsible for setting the state directly.
     */
    public enum Kind { INITIAL, NORMAL, REFERENCE, FINAL }

    private Kind kind ;
    private Action defaultAction ;
    private State defaultNextState ;

    private Map<Input,Set<Transition>> inputMap ;

    // For each (k,v) in intputMap, there is a (k,iv) in 
    // inputMapRangeImage such that iv is an unmodifiable image of v.
    private Map<Input,Set<Transition>> inputMapRangeImage ;

    // unmodifiable image of inputMapRangeImage
    private Map<Input,Set<Transition>> inputMapImage ;

    public State( String name )  {
	this( name, Kind.NORMAL ) ;
    }

    public State( String name, Kind kind ) {
	this( null, name, kind ) ;
    }

    public State( Set<State> states, String name )  {
	this( states, name, Kind.NORMAL ) ;
    }

    public State( Set<State> states, String name, Kind kind ) 
    { 
	super( name ) ; 
	if (states != null) {
            states.add(this);
        }

	this.kind = kind ;
	defaultAction = null ;
	inputMap = new HashMap<Input,Set<Transition>>() ;
	inputMapRangeImage = new HashMap<Input,Set<Transition>>() ;
	inputMapImage = Collections.unmodifiableMap( inputMapRangeImage ) ;
    } 

    /** Return the Kind of this state.
     */
    public Kind getKind() {
	return kind ;
    }

    /** Method that defines action that occurs whenever this state is entered
     * from a different state.  preAction is not called on a self-transition.
     * If preAction returns a non-null result, the result becomes the current FSM.
     * <P>
     * Any exceptions except ThreadDeath thrown by this method are ignored.
     * This method can be overridden in a state implementation if needed.
     */
    public FSM preAction( FSM fsm ) {
	return null ;
    }

    /** If this state has Kind REFERENCE, and its preAction pushes a 
     * nested FSM onto the stack, the returnAction method is called after the
     * nested FSM reaches a final state.  The nested FSM is passed into
     * nestedFSM, and fsm is the new top of stack, which is the FSM
     * that was active when the preAction was called.  The result is
     * the new state that will be assumed after this REFERENCE's 
     * state postAction method is called.
     * <p>
     * If the returnAction method sets the state to a new state,
     * the postAction method is called as usuTransition.
     * <P>
     * Any exceptions except ThreadDeath thrown by this method are ignored.
     * This method can be overridden in a state implementation if needed.
     */
    public State returnAction( FSM fsm, FSM nestedFSM ) {
	return null ;
    }

    /** Method that defines action that occurs whenever this state is exited,
     * that is, when the state is changed from this state to a new state.
     * <P>
     * Any exceptions except ThreadDeath thrown by this method are ignored.
     * This method can be overridden in a state implementation if needed.
     */
    public void postAction( FSM fsm ) {
    }

    /** Return the default next state for this state.  This is the next
     * state if the input is not found in the action map.
     */
    public State getDefaultNextState() {
	return defaultNextState ;
    }

    /** Get the default transition action that is used if the default next
     * state is used.
     */
    public Action getDefaultAction() {
	return defaultAction ;
    }

    public Map<Input,Set<Transition>> getInputMap() {
	return inputMapImage ;
    }

    // These methods are only called from the StateEngine.
   
    void setDefaultNextState( State defaultNextState ) {
	this.defaultNextState = defaultNextState ;
    }

    void setDefaultAction( Action defaultAction ) {
	this.defaultAction = defaultAction ;
    }

    void addTransition( Input in, Transition ga ) {
	Set<Transition> gas = inputMap.get( in ) ;
	if (gas == null) {
	    gas = new HashSet<Transition>() ;
	    inputMap.put( in, gas ) ;

            Set<Transition> gasImage = Collections.unmodifiableSet(gas) ;
            inputMapRangeImage.put( in, gasImage ) ;
	}

	gas.add( ga ) ;
    }

    Set<Transition> getTransitions( Input in ) {
	return inputMap.get( in ) ;
    }
}
