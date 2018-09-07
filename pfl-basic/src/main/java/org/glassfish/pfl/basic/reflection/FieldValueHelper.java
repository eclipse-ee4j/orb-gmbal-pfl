/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.reflection;

import java.lang.reflect.Field;

public class FieldValueHelper {
    private static final Bridge bridge = Bridge.get();

    /**
     * Returns the value of a field in an object.
     * @param obj the object holding the field
     * @param field the field whose value is to be returned.
     * @throws IllegalAccessException if the field cannot directly be accessed.
     */
    public static Object getFieldValue(Object obj, final Field field) throws IllegalAccessException {
        if (field.isAccessible())
            return field.get(obj);
        else
            return getPrivateFieldValue(obj, field);
    }

    private static Object getPrivateFieldValue(Object obj, final Field field) throws IllegalAccessException {
        Field privateField = bridge.toAccessibleField(field, FieldValueHelper.class);
        return (privateField != null) ? privateField.get(obj) : getInacessibleFieldValue(obj, field);
    }

    private static Object getInacessibleFieldValue(Object obj, Field field) {
        long offset = bridge.objectFieldOffset(field);

        if (!field.getType().isPrimitive())
            return bridge.getObject(obj, offset);
        else if (field.getType() == Integer.TYPE)
            return bridge.getInt(obj, offset);
        else if (field.getType() == Byte.TYPE)
            return bridge.getByte(obj, offset);
        else if (field.getType() == Long.TYPE)
            return bridge.getLong(obj, offset);
        else if (field.getType() == Float.TYPE)
            return bridge.getFloat(obj, offset);
        else if (field.getType() == Double.TYPE)
            return bridge.getDouble(obj, offset);
        else if (field.getType() == Short.TYPE)
            return bridge.getShort(obj, offset);
        else if (field.getType() == Character.TYPE)
            return bridge.getChar(obj, offset);
        else if (field.getType() == Boolean.TYPE)
            return bridge.getBoolean(obj, offset);
        else
            return null;
    }
}
