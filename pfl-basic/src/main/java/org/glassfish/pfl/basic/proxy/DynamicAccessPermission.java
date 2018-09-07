/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.proxy;

import java.security.*;

/**
 * This class controls the use of dynamic proxies.
 * A DynamicAccessPermission contains a name (also referred to as a "target name") but
 * no actions list; you either have the named permission
 * or you don't.
 */

public final class DynamicAccessPermission extends BasicPermission {
    private static final long serialVersionUID = -8343910153355041693L;

    /**
     * Creates a new DynamicAccessPermission with the specified name.
     * @param name the name of the DynamicAccessPermission.
     */
    public DynamicAccessPermission(String name)
    {
	super(name);
    }

    /**
     * Creates a new DynamicAccessPermission object with the specified name.
     * The name is the symbolic name of the DynamicAccessPermission, and the
     * actions String is currently unused and should be null.
     *
     * @param name the name of the DynamicAccessPermission.
     * @param actions should be null.
     */
    public DynamicAccessPermission(String name, String actions)
    {
	super(name, actions);
    }
}
