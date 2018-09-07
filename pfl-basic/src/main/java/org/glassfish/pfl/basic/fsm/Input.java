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

/**
 * This interface must be implemented by any class that is used as 
 * an input to a FSM.  The FSM only needs the identity of this 
 * object, so all that is really needs is the default equals implementation.
 * The toString() method should also be overridden to give a concise
 * description or name of the input.
 *
 * @author Ken Cavanaugh 
 */
public interface Input
{
    public class Base extends NameBase implements Input {
	public Base( String name ) { super( name ) ; } 
    }
}

// end of Input.java
