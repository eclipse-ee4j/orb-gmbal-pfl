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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapImpl implements InvocationHandler {
    Object object;

    public MapImpl(Object obj) {
        this.object = obj;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {

        return method.invoke(object, args);
    }
}
