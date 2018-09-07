/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.testobjects;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

class NonPublicExternalizedClass implements IntHolder, Externalizable {

    private int anInt;

    public NonPublicExternalizedClass() {
        this.anInt = TestObjects.INT_FIELD_VALUE;
    }

    @Override
    public int getAnInt() {
        return anInt;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(anInt);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        anInt = in.readInt();
    }
}
