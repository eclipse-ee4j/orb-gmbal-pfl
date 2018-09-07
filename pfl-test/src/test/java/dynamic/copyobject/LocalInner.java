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
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LocalInner  {

    public Iterator iterator;

    public LocalInner(Object[] array) {
        iterator = walkThrough(array);
        iterator.next();
    }

    public static Iterator walkThrough(final Object[] objs) {
        class Iter implements Iterator {

            private int pos = 0;

            public boolean hasNext() {
                return (pos < objs.length);
            }

            public Object next() throws NoSuchElementException {
                if (pos >= objs.length) {
                    throw new NoSuchElementException();
                }
                return objs[pos++];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        return new Iter();
    }
}

class AnonymousInner {

    public Iterator iterator;

    public AnonymousInner(Object[] array) {
        iterator = walkThrough(array);
        iterator.next();
    }

    public static Iterator walkThrough(final Object[] objs) {
        return new Iterator() {
            private int pos = 0;

            public boolean hasNext() {
                return (pos < objs.length);
            }

            public Object next() throws NoSuchElementException {
                if (pos >= objs.length) {
                    throw new NoSuchElementException();
                }
                return objs[pos++];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

class Outer implements Serializable {

    private static int nextId;
    public Inner inner = new Inner();

    class Inner {
        final int id = nextId++;
    }
}

class ExtendedOuter extends Outer {

    class ExtendedInner extends Inner {
    }

    public ExtendedInner inner = new ExtendedInner();
}

class BankAccount {

    private int number;
    private int balance;
    public Permissions perm;

    public BankAccount(int number, int balance) {
        this.number = number;
        this.balance = balance;
        perm = new Permissions(true);
    }

    public static class Permissions {
        boolean canDeposit;

        public Permissions(boolean bool) {
            canDeposit = bool;
        }
    }
}
