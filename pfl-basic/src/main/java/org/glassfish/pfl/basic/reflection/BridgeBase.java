/*
 * Copyright (c) 2018, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.reflection;

import java.io.OptionalDataException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import sun.misc.Unsafe;

public abstract class BridgeBase {
    /**
     * This constant differs from all results that will ever be returned from
     * {@link #objectFieldOffset}.
     */
    public static final long INVALID_FIELD_OFFSET = -1;

    private final Unsafe unsafe = AccessController.doPrivileged(
                    new PrivilegedAction<Unsafe>() {
                        public Unsafe run() {
                            try {
                                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                                field.setAccessible(true);
                                return (Unsafe) field.get(null);
                            } catch (NoSuchFieldException | IllegalAccessException exc) {
                                throw new Error("Could not access Unsafe", exc);
                            }
                        }
                    }
            );

    /**
     * Fetches a field element within the given
     * object <code>o</code> at the given offset.
     * The result is undefined unless the offset was obtained from
     * {@link #objectFieldOffset} on the {@link java.lang.reflect.Field}
     * of some Java field and the object referred to by <code>o</code>
     * is of a class compatible with that field's class.
     *
     * @param o      Java heap object in which the field from which the offset
     *               was obtained resides
     * @param offset indication of where the field resides in a Java heap
     *               object
     * @return the value fetched from the indicated Java field
     * @throws RuntimeException No defined exceptions are thrown, not even
     *                          {@link NullPointerException}
     */
    public final int getInt(Object o, long offset) {
        return unsafe.getInt(o, offset);
    }

    /**
     * Stores a value into a given Java field.
     * <p>
     * The first two parameters are interpreted exactly as with
     * {@link #getInt(Object, long)} to refer to a specific
     * Java field.  The given value is stored into that field.
     * <p>
     * The field must be of the same type as the method
     * parameter <code>x</code>.
     *
     * @param o      Java heap object in which the field resides, if any, else
     *               null
     * @param offset indication of where the field resides in a Java heap
     *               object.
     * @param x      the value to store into the indicated Java field
     * @throws RuntimeException No defined exceptions are thrown, not even
     *                          {@link NullPointerException}
     */
    public final void putInt(Object o, long offset, int x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putInt(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    @SuppressWarnings("unchecked")
    public final <T> T getObject(Object o, long offset) {
        return (T) unsafe.getObject(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putObject(Object o, long offset, Object x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putObject(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    public final boolean getBoolean(Object o, long offset) {
        return unsafe.getBoolean(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putBoolean(Object o, long offset, boolean x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putBoolean(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    public final byte getByte(Object o, long offset) {
        return unsafe.getByte(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putByte(Object o, long offset, byte x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putByte(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    public final short getShort(Object o, long offset) {
        return unsafe.getShort(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putShort(Object o, long offset, short x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putShort(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    public final char getChar(Object o, long offset) {
        return unsafe.getChar(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putChar(Object o, long offset, char x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putChar(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    public final long getLong(Object o, long offset) {
        return unsafe.getLong(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putLong(Object o, long offset, long x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putLong(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    public final float getFloat(Object o, long offset) {
        return unsafe.getFloat(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putFloat(Object o, long offset, float x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putFloat(o, offset, x);
    }

    /**
     * @see #getInt(Object, long)
     */
    public final double getDouble(Object o, long offset) {
        return unsafe.getDouble(o, offset);
    }

    /**
     * @see #putInt(Object, long, int)
     */
    public final void putDouble(Object o, long offset, double x) {
        if (offset != INVALID_FIELD_OFFSET)
            unsafe.putDouble(o, offset, x);
    }

    /**
     * Returns the offset of a non-static field, which can be passed into the set... or get... methods.
     * @see #getInt(Object, long)
     */
    public final long objectFieldOffset(Field f) {
        return unsafe.objectFieldOffset(f);
    }

    public final long staticFieldOffset(Field f) {
        return unsafe.staticFieldOffset(f);
    }

    /**
     * Throw the exception.
     * The exception may be an undeclared checked exception.
     */
    public final void throwException(Throwable ee) {
        unsafe.throwException(ee);
    }

    /**
     * Defines a class is a specified classloader.
     * @param className the name of the class
     * @param classBytes the byte code for the class
     * @param classLoader the classloader in which it is to be defined
     * @param protectionDomain the domain in which the class should be defined
     *
     * @deprecated will not work in Java 11 or later. Use {@link #defineClass(Class, String, byte[])} instead
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public Class<?> defineClass(String className, byte[] classBytes, ClassLoader classLoader, ProtectionDomain protectionDomain) {
        return unsafe.defineClass(className, classBytes, 0, classBytes.length, classLoader, null);
    }

    /**
     * Defines a new class from bytecode. The class will be defined in the classloader and package associated with a
     * specified 'anchor class'.
     *
     * @param anchorClass the class from which the package and classloader of the new class are to be taken.
     * @param className the name of the class to define
     * @param classBytes the bytes used to define the class
     * @return a new instantiable class, in the package and classloader of the anchor class.
     */
    public Class<?> defineClass(Class<?> anchorClass, String className, byte[] classBytes) {
        return defineClass(className, classBytes, anchorClass.getClassLoader(), null);
    }

    /**
     * Ensure that the class has been initialized.
     * @param cl the class to ensure is initialized
     */
    public void ensureClassInitialized(Class<?> cl) {
        unsafe.ensureClassInitialized(cl);
    }

    /**
     * Obtain the latest user defined ClassLoader from the call stack.
     * This is required by the RMI-IIOP specification.
     */
    public abstract ClassLoader getLatestUserDefinedLoader();

    /**
     * Return a constructor that can be used to create an instance of the class for externalization.
     * @param cl the class
     */
    public abstract <T> Constructor<?> newConstructorForExternalization(Class<T> cl);

    /**
     * Return a no-arg constructor for the specified class which invokes the specified constructor.
     *
     * @param aClass the class for which a constructor should be returned.
     * @param cons the default constructor on which to model the new constructor.
     */
    public abstract <T> Constructor<T> newConstructorForSerialization(Class<T> aClass, Constructor<?> cons);

    /**
     * Return a no-arg constructor for the specified class, based on the default constructor
     * for its nearest non-serializable base class.
     * @param aClass the class for which a constructor should be returned.
     */
    public abstract <T> Constructor<T> newConstructorForSerialization(Class<T> aClass);

    /**
     * Returns true if the given class defines a static initializer method,
     * false otherwise.
     */
    public abstract boolean hasStaticInitializerForSerialization(Class<?> cl);

    /**
     * Returns a method handle to allow invocation of the specified class's writeObject method.
     * @param cl the class containing the method
     */
    public abstract MethodHandle writeObjectForSerialization(Class<?> cl) throws NoSuchMethodException, IllegalAccessException;

    /**
     * Returns a method handle to allow invocation of the specified class's readObject method.
     * @param cl the class containing the method
     */
    public abstract MethodHandle readObjectForSerialization(Class<?> cl) throws NoSuchMethodException, IllegalAccessException;

    /**
     * Returns a method handle to allow invocation of the specified class's readResolve method.
     * @param cl the class containing the method
     */
    public abstract MethodHandle readResolveForSerialization(Class<?> cl);

    /**
     * Returns a method handle to allow invocation of the specified class's writeReplace method.
     * @param cl the class containing the method
     */
    public abstract MethodHandle writeReplaceForSerialization(Class<?> cl);

    /**
     * Return a new OptionalDataException instance.
     * @return a new OptionalDataException instance
     */
    public abstract OptionalDataException newOptionalDataExceptionForSerialization(boolean bool);

    /**
     * Returns the specified field after calling setAccessible, if it is accessible from the specified class.
     * @param field the field to make accessible
     * @param callingClass the class which wants to access it.
     * @return the original field, rendered accessible, or null.
     */
    public Field toAccessibleField(Field field, Class callingClass) {
        field.setAccessible(true);
        return field;
    }

    /**
     * Returns the specified method after calling setAccessible, if it is accessible from the specified class.
     * @param method the method to make accessible
     * @param callingClass the class which wants to access it.
     * @return the original method, rendered accessible, or null.
     */
    public Method toAccessibleMethod(Method method, Class callingClass) {
        method.setAccessible(true);
        return method;
    }
}
