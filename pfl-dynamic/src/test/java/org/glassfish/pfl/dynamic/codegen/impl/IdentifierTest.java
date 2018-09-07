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

import junit.framework.TestCase;
import org.glassfish.pfl.basic.contain.Pair;

// Simple tests for Identifier
public class IdentifierTest extends TestCase {
    public IdentifierTest() {
        super();
    }

    public IdentifierTest(String name) {
        super(name);
    }

    public void testIdentifier00() {
        assertTrue(Identifier.isValidIdentifier("frobenius"));
    }

    public void testIdentifier01() {
        assertTrue(Identifier.isValidIdentifier("frob_123"));
    }

    public void testIdentifier02() {
        assertTrue(Identifier.isValidIdentifier("_12_frob_123"));
    }

    public void testIdentifier03() {
        assertFalse(Identifier.isValidIdentifier("2_frob_123"));
    }

    public void testIdentifier04() {
        assertTrue(Identifier.isValidFullIdentifier("frobenius"));
    }

    public void testIdentifier05() {
        assertTrue(Identifier.isValidFullIdentifier("frobenius.ert"));
    }

    public void testIdentifier06() {
        assertTrue(Identifier.isValidFullIdentifier("a.b.c.d.e.f"));
    }

    public void testIdentifier07() {
        assertFalse(Identifier.isValidFullIdentifier("2_frob_123"));
    }

    public void testIdentifier08() {
        assertFalse(Identifier.isValidFullIdentifier("a..b"));
    }

    public void testIdentifier09() {
        assertFalse(Identifier.isValidFullIdentifier("a.b."));
    }

    public void testIdentifier10() {
        assertFalse(Identifier.isValidFullIdentifier(".a.b"));
    }

    public void testIdentifier11() {
        assertEquals(Identifier.makeFQN("a.b", "c"), "a.b.c");
    }

    public void testIdentifier12() {
        assertEquals(Identifier.makeFQN("", "c"), "c");
    }

    public void testIdentifier13() {
        assertEquals(Identifier.makeFQN(null, "c"), "c");
    }

    public void testIdentifier14() {
        assertEquals(Identifier.splitFQN("a.b.c"),
                new Pair("a.b", "c"));
    }

    public void testIdentifier15() {
        assertEquals(Identifier.splitFQN("c"),
                new Pair("", "c"));
    }
}
