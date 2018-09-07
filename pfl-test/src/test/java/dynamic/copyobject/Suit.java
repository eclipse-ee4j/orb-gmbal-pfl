/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package dynamic.copyobject ;

import java.io.Serializable;
import java.io.ObjectStreamException;

public class Suit implements Comparable, Serializable {

    private final String name;

    private Suit(String name) {
        this.name = name;
    }

    public int compareTo(Object o) {
        return ordinal - ((Suit) o).ordinal;
    }

    public String toString() {
        return name;
    }

    public static final Suit CLUBS = new Suit("clubs");
    public static final Suit DIAMONDS = new Suit("diamonds");
    public static final Suit HEARTS = new Suit("hearts");
    public static final Suit SPADES = new Suit("spades");

    private static int nextOrdinal = 0;
    private final int ordinal = nextOrdinal++;

    private static final Suit[] VALUES =
    { CLUBS, DIAMONDS, HEARTS, SPADES };

    private Object readResolve() throws ObjectStreamException {
        return VALUES[ordinal];
    }
}
