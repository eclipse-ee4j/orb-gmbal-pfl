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

import java.io.*;

public class FooImpl implements Serializable {

    public BarImpl bar = null;
    public FooImpl() {
        bar = new BarImpl();
    }

    static class BarImpl implements Externalizable {

        private int value;

        public BarImpl() {
            this(-1);
        }

        public BarImpl(int value) {
            this.value = value;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            System.out.println("** externalizable.writeExternal **");
        }

        public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {
            System.out.println("** externalizable.readExternal **");
        }
    }
}
