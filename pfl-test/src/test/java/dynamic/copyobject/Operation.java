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

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public abstract class Operation implements Serializable {

    private final transient String name;

    protected Operation(String name) {
        this.name = name;
    }

    public static final Operation PLUS = new Operation("+") {
        protected double eval(double x, double y) {
            return x + y;
        }
    };

    public static final Operation MINUS = new Operation("-") {
        protected double eval(double x, double y) {
            return x - y;
        }
    };

    protected abstract double eval(double x, double y);

    public String toString() {
        return name;
    }

    public final boolean equals(Object o) {
        return super.equals(o);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    private static int nextOrdinal = 0;
    private final int ordinal = nextOrdinal++;

    private static final Operation[] VALUES = { PLUS, MINUS };

    Object readResolve() throws ObjectStreamException {
        return VALUES[ordinal];
    }
}

abstract class ExtendedOperation extends Operation {

    private ExtendedOperation(String name) {
        super(name);
    }

    public static final Operation TIMES = new ExtendedOperation("*") {
        protected double eval(double x, double y) {
            return x * y;
        }
    };

    public static final Operation DIVIDE = new ExtendedOperation("/") {
        protected double eval(double x, double y) {
            return x / y;
        }
    };

    private static int nextOrdinal = 0;
    private final int ordinal = nextOrdinal++;
    private static final Operation[] VALUES = { TIMES, DIVIDE };

    public static final List LIST =
        Collections.unmodifiableList(Arrays.asList(VALUES));

    Object readResolve() throws ObjectStreamException {
        return VALUES[ordinal];
    }

}
