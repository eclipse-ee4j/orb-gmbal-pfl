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

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

class TestExternalizableImpl implements Externalizable {

    private static String key;
    private int value;

    public TestExternalizableImpl() {
        this("default", -1);
    }

    public TestExternalizableImpl(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        System.out.println("** TestExternalizable.writeExternal **");
    }

    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {
        System.out.println("** TestExternalizable.readExternal **");
    }
}
