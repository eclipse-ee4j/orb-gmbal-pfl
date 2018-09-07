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

class TestImpl3 implements Serializable {

    private transient NonSerializable3 nonser = null;

    public TestImpl3(NonSerializable3 nonser) {
        this.nonser = nonser;
    }

}

class NonSerializable3Base {

    String msg;

    public NonSerializable3Base(int i) {
        msg = "called int-arg cons";
    }

}

class NonSerializable3 extends NonSerializable3Base {

    String msg;

    public NonSerializable3(boolean bool) {
        super((bool ? 0 : 1));
        msg = "called bool-arg cons";
    }

}

