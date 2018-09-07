/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl;

import org.glassfish.pfl.basic.reflection.Bridge;
import org.glassfish.pfl.basic.reflection.BridgePermission;
import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException;

import java.security.Permission;
import java.util.Map;


/**
 * Base class for generated class copiers.  Note that this class
 * makes use of the unsafe copier through the Bridge class.
 * Because of this, CodegenCopierBase could potentially be used
 * to bypass security restrictions.  Consequently, this class must be
 * referenced inside a doPrivileged block, and a derived class
 * must have the permissions needed for the
 * Bridge class.
 */
public abstract class CodegenCopierBase implements ClassCopierOrdinaryImpl.ClassFieldCopier {
    private static final Bridge bridge = Bridge.get();
    private static final Permission getBridgePermission = new BridgePermission("getBridge");

    private PipelineClassCopierFactory factory;

    public CodegenCopierBase(PipelineClassCopierFactory factory) {
        SecurityManager sman = System.getSecurityManager();
        if (sman != null) {
            sman.checkPermission(getBridgePermission);
        }

        this.factory = factory;
    }

    final protected void copyObject(Map<Object, Object> oldToNew,
                                    long offset, Object src, Object dest) throws ReflectiveCopyException {
        Object obj = bridge.getObject(src, offset);

        Object result = null;

        if (obj != null) {
            // This lookup must be based on the actual type, not the
            // declared type to allow for polymorphism.
            ClassCopier copier = factory.getClassCopier(obj.getClass());
            result = copier.copy(oldToNew, obj);
        }

        bridge.putObject(dest, offset, result);
    }

    final protected void copyByte(long offset, Object src, Object dest) {
        bridge.putByte(dest, offset, bridge.getByte(src, offset));
    }

    final protected void copyChar(long offset, Object src, Object dest) {
        bridge.putChar(dest, offset, bridge.getChar(src, offset));
    }

    final protected void copyShort(long offset, Object src, Object dest) {
        bridge.putShort(dest, offset, bridge.getShort(src, offset));
    }

    final protected void copyInt(long offset, Object src, Object dest) {
        bridge.putInt(dest, offset, bridge.getInt(src, offset));
    }

    final protected void copyLong(long offset, Object src, Object dest) {
        bridge.putLong(dest, offset, bridge.getLong(src, offset));
    }

    final protected void copyFloat(long offset, Object src, Object dest) {
        bridge.putFloat(dest, offset, bridge.getFloat(src, offset));
    }

    final protected void copyDouble(long offset, Object src, Object dest) {
        bridge.putDouble(dest, offset, bridge.getDouble(src, offset));
    }

    final protected void copyBoolean(long offset, Object src, Object dest) {
        bridge.putBoolean(dest, offset, bridge.getBoolean(src, offset));
    }
};

