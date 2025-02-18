/*
 * Copyright (c) 2024, 2025 Contributors to the Eclipse Foundation
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.lib;

import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBObject;
import jakarta.ejb.Handle;
import jakarta.ejb.RemoveException;

import java.rmi.RemoteException;

/**
 * This is a base class that provides trivial implementations
 * of the EJBObject methods so that the code generated by
 * the codegen framework can be compiled.
 */
public abstract class EJBObjectBase implements EJBObject {

    @Override
    public EJBHome getEJBHome() throws RemoteException {
        return null;
    }

    @Override
    public Object getPrimaryKey() throws RemoteException {
        return null;
    }

    @Override
    public void remove() throws RemoteException, RemoveException {
    }

    @Override
    public Handle getHandle() throws RemoteException {
        return null;
    }

    @Override
    public boolean isIdentical(EJBObject obj) throws RemoteException {
        return obj == this;
    }
}
