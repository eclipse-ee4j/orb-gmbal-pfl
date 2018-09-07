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

import org.glassfish.pfl.basic.testobjects.NonSerializableBaseClass;

import java.io.Serializable;

public class SerializableClass1 extends NonSerializableBaseClass implements Serializable {

    private double aDouble;


    public double getADouble() {
        return aDouble;
    }
}
