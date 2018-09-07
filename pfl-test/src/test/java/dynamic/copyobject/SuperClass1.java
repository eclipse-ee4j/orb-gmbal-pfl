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

public class SuperClass1 {
    int flag = 0;

    public SuperClass1() {
        flag = -1;
    }

    public SuperClass1(int flag) {
        this.flag = flag;
    }
}

class SubClass1 extends SuperClass1 implements Serializable {

    String str;

    public SubClass1(String str) {
        super(100);
        this.str = str;
    }
}

class SubClass2 implements Serializable {

    String str;

    public SubClass2(String str) {
        this.str = str;
    }
}


