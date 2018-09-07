/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import org.glassfish.pfl.objectweb.asm.Label ;

/** This trivial class exists only to give a slightly
 *  more readable toString method for ASM labels.
 *  The ASM version simply uses the identity hashcode,
 *  which is a bit hard to read.
 */
public class MyLabel extends Label {
    private static int next = 0 ;
    private int current = next++ ;
    private boolean emitted = false ;

    public boolean emitted() {
	return emitted ;
    }

    public void emitted( boolean flag ) {
	emitted = flag ;
    }

    @Override
    public String toString() {
	return "ML" + current ;
    }
}
