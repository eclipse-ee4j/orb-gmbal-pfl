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

class TestImpl2 implements Serializable {

    private transient NonSerializable2 nonser = null;
    private final transient String str;

    public TestImpl2(NonSerializable2 nonser, String str) {
        this.nonser = nonser;
        this.str = str;
    }
}

class NonSerializable2Base {

    protected String msg;

    public NonSerializable2Base() {
        msg = "called no-arg cons";
    }

    public NonSerializable2Base(int i) {
        msg = "called int-arg cons";
    }

}

class NonSerializable2 extends NonSerializable2Base {

    public NonSerializable2(boolean bool) {
        msg = "called bool-arg cons";
    }

}
