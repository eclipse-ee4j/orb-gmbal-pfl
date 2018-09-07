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

class TestImpl1 implements Serializable {

    private transient NonSerializable1 nonser = null;

    public TestImpl1() {
    }

    public TestImpl1(NonSerializable1 nonser) {
        this.nonser = nonser;
    }
}

class NonSerializable1 {

    String msg;

    public NonSerializable1() {
        msg = "called non-arg cons";
    }

    public NonSerializable1(String str) {
        msg = "called string-arg cons";
    }
}
