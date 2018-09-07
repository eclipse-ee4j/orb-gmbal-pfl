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

public class TestObjects {

    public static final int INT_FIELD_VALUE = (int) (Math.random() * Integer.MAX_VALUE);

    public static Class<? extends IntHolder> getNonPublicExternalizableClass() {
        return NonPublicExternalizedClass.class;
    }

    public static Class<? extends IntHolder> getNonPublicSerializableClass() {
        return SerializableClass2.class;
    }
}
