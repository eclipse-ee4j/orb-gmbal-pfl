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

/**
 * A class which initializes some fields that will be ignored by deserialization.
 */
class NonSerializableBaseClass implements IntHolder {

    private int aNumber;

    NonSerializableBaseClass() {
        aNumber = TestObjects.INT_FIELD_VALUE;
    }

    @Override
    public int getAnInt() {
        return aNumber;
    }
}
