/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.reflection;

import sun.reflect.ReflectionFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;

/**
 * This class provides the methods for fundamental JVM operations
 * needed in the ORB that are not part of the public Java API.  This includes:
 * <ul>
 * <li>throwException, which can throw undeclared checked exceptions.
 * This is needed to handle throwing arbitrary exceptions across a standardized OMG interface that (incorrectly) does not specify appropriate exceptions.</li>
 * <li>putXXX/getXXX methods that allow unchecked access to fields of objects.
 * This is used for setting uninitialzed non-static final fields (which is
 * impossible with reflection) and for speed.</li>
 * <li>objectFieldOffset to obtain the field offsets for use in the putXXX/getXXX methods</li>
 * <li>newConstructorForSerialization to get the special constructor required for a
 * Serializable class</li>
 * <li>latestUserDefinedLoader to get the latest user defined class loader from
 * the call stack as required by the RMI-IIOP specification (really from the
 * JDK 1.1 days)</li>
 * </ul>
 * The code that calls Bridge.get() must have the following Permissions:
 * <ul>
 * <li>RuntimePermission "reflectionFactoryAccess"</li>
 * <li>BridgePermission "getBridge"</li>
 * <li>ReflectPermission "suppressAccessChecks"</li>
 * </ul>
 * <p>
 * All of these permissions are required to obtain and correctly initialize
 * the instance of Bridge.  No security checks are performed on calls
 * made to Bridge instance methods, so access to the Bridge instance
 * must be protected.
 * <p>
 * This class is a singleton (per ClassLoader of course).  Access to the
 * instance is obtained through the Bridge.get() method.
 */
public final class Bridge extends BridgeBase {
    private static final Permission GET_BRIDGE_PERMISSION = new BridgePermission("getBridge");
    private static Bridge bridge = null;

    // latestUserDefinedLoader() is a private static method
    // in ObjectInputStream in JDK 1.3 through 1.5.
    // We use reflection in a doPrivileged block to get a
    // Method reference and make it accessible.
    private final Method latestUserDefinedLoaderMethod;

    // Since java.io.OptionalDataException's constructors are
    // package private, but we need to throw it in some special
    // cases, we try to do it by reflection.
    private final Constructor<OptionalDataException> optionalDataExceptionConstructor;

    private final ReflectionFactory reflectionFactory;

    private Method getLatestUserDefinedLoaderMethod() {
        return AccessController.doPrivileged(
                new PrivilegedAction<Method>() {
                    @SuppressWarnings("unchecked")
                    public Method run() {
                        Method result;

                        try {
                            Class io = ObjectInputStream.class;
                            result = io.getDeclaredMethod("latestUserDefinedLoader");
                            result.setAccessible(true);
                        } catch (NoSuchMethodException nsme) {
                            throw new Error("java.io.ObjectInputStream latestUserDefinedLoader " + nsme, nsme);
                        }

                        return result;
                    }
                }
        );
    }

    // Grab the OptionalDataException boolean ctor and make it accessible.
    @SuppressWarnings("unchecked")
    private Constructor<OptionalDataException> getOptDataExceptionCtor() {
        try {
            Constructor result = AccessController.doPrivileged(
                new PrivilegedExceptionAction<Constructor>() {
                    public Constructor run()
                        throws NoSuchMethodException, SecurityException {
                        Constructor constructor = OptionalDataException.class.getDeclaredConstructor(Boolean.TYPE);
                        constructor.setAccessible(true);
                        return constructor;
                    }
                }
            );

            if (result == null) {
                throw new Error("Unable to find OptionalDataException constructor");
            }

            return (Constructor<OptionalDataException>) result;

        } catch (Exception ex) {
            throw new Error("Unable to find OptionalDataException constructor");
        }
    }


    private Bridge() {
        latestUserDefinedLoaderMethod = getLatestUserDefinedLoaderMethod();
        reflectionFactory = ReflectionFactory.getReflectionFactory();
        optionalDataExceptionConstructor = getOptDataExceptionCtor();
    }

    /**
     * Fetch the Bridge singleton.  This requires the following
     * permissions:
     * <ul>
     * <li>RuntimePermission "reflectionFactoryAccess"</li>
     * <li>BridgePermission "getBridge"</li>
     * <li>ReflectPermission "suppressAccessChecks"</li>
     * </ul>
     *
     * @return The singleton instance of the Bridge class
     * @throws SecurityException if the caller does not have the
     *                           required permissions and the caller has a non-null security manager.
     */
    public static synchronized Bridge get() {
        SecurityManager sman = System.getSecurityManager();
        if (sman != null) {
            sman.checkPermission(GET_BRIDGE_PERMISSION);
        }

        if (bridge == null) {
            bridge = new Bridge();
        }

        return bridge;
    }

    @Override
    public final ClassLoader getLatestUserDefinedLoader() {
        try {
            return (ClassLoader) latestUserDefinedLoaderMethod.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException ite) {
            throw new Error(getClass().getName() + ".latestUserDefinedLoader: " + ite, ite);
        }
    }

    @Override
    public final <T> Constructor<T> newConstructorForExternalization(Class<T> cl) {
        try {
            Constructor<T> cons = cl.getDeclaredConstructor();
            cons.setAccessible(true);
            return isPublic(cons) ? cons : null;
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static boolean isPublic(Constructor<?> cons) {
        return (cons.getModifiers() & Modifier.PUBLIC) != 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Constructor<T> newConstructorForSerialization(Class<T> aClass, Constructor<?> cons) {
        Constructor newConstructor = reflectionFactory.newConstructorForSerialization(aClass, cons);
        newConstructor.setAccessible(true);
        return (Constructor<T>) newConstructor;
    }

    @Override
    public <T> Constructor<T> newConstructorForSerialization(Class<T> aClass) {
        Class<?> baseClass = getNearestNonSerializableBaseClass(aClass);
        if (baseClass == null) return null;

        try {
            Constructor<?> cons = baseClass.getDeclaredConstructor();
            if (isPrivate(cons) || !isAccessibleFromSubclass(cons, aClass, baseClass)) return null;

            return newConstructorForSerialization(aClass, cons);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static <T> Class<?> getNearestNonSerializableBaseClass(Class<T> clazz) {
        Class<?> baseClass = clazz;

        while (Serializable.class.isAssignableFrom(baseClass))
            if ((baseClass = baseClass.getSuperclass()) == null) return null;

        return baseClass;
    }

    private static boolean isAccessibleFromSubclass(Constructor<?> constructor, Class<?> clazz, Class<?> baseClass) {
        return isPublicOrProtected(constructor) || inSamePackage(clazz, baseClass);
    }

    private static boolean inSamePackage(Class<?> clazz, Class<?> baseClass) {
        return Objects.equals(clazz.getPackage(), baseClass.getPackage());
    }

    private static boolean isPublicOrProtected(Constructor<?> constructor) {
        return (constructor.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0;
    }

    private static boolean isPrivate(Constructor<?> cons) {
        return (cons.getModifiers() & Modifier.PRIVATE) != 0;
    }

    private static Method hasStaticInitializerMethod = null;

    @Override
    public boolean hasStaticInitializerForSerialization(Class<?> cl) {
        try {
            return (Boolean) getHasStaticInitializerMethod().invoke(null, cl);
        } catch (Exception ex) {
            throw new Error("Cannot invoke 'hasStaticInitializer' method on " + ObjectStreamClass.class.getName());
        }
    }

    private static Method getHasStaticInitializerMethod() throws NoSuchMethodException {
        if (hasStaticInitializerMethod == null) {
            hasStaticInitializerMethod = ObjectStreamClass.class.getDeclaredMethod("hasStaticInitializer", Class.class);
            hasStaticInitializerMethod.setAccessible(true);
        }

        return hasStaticInitializerMethod;
    }

    @Override
    public MethodHandle writeObjectForSerialization(Class<?> cl) {
        return toMethodHandle(getPrivateMethod(cl, "writeObject", Void.TYPE, ObjectOutputStream.class));
    }

    private static MethodHandle toMethodHandle(Method method) {
        try {
            if (method == null) return null;
            method.setAccessible(true);
            MethodHandle methodHandle = MethodHandles.lookup().unreflect(method);
            method.setAccessible(false);
            return methodHandle;
        } catch (SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    private static Method getPrivateMethod(Class<?> cl, String name, Class<?> returnType, Class<?>... argTypes ) {
        try {
            Method method = cl.getDeclaredMethod(name, argTypes);
            return ((method.getReturnType() == returnType) && isPrivate(method) && !isStatic(method)) ? method : null;
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    private static boolean isPrivate(Method method) {
        return Modifier.isPrivate(method.getModifiers());
    }

    @Override
    public MethodHandle readObjectForSerialization(Class<?> cl) {
        return toMethodHandle(getPrivateMethod(cl, "readObject", Void.TYPE, ObjectInputStream.class));
    }

    @Override
    public MethodHandle readResolveForSerialization(Class<?> cl) {
        return toMethodHandle(getInheritableMethod(cl, "readResolve", Object.class));
    }

    private static Method getInheritableMethod(Class<?> cl, String name, Class<?> returnType, Class<?>... argTypes ) {
        Method method = getMatchingMethod(cl, name, returnType, argTypes);

        return (method != null && isMethodInheritableBy(cl, method)) ? method : null;
    }

    private static Method getMatchingMethod(Class<?> cl, String name, Class<?> returnType, Class<?>[] argTypes) {
        Class<?> aClass = cl;
        while (aClass != null) {
            try {
                Method method = aClass.getDeclaredMethod(name, argTypes);
                return method.getReturnType() == returnType ? method : null;
            } catch (NoSuchMethodException ex) {
                aClass = aClass.getSuperclass();
            }
        }
        return null;
    }

    private static boolean isMethodInheritableBy(Class<?> callingClass, Method method) {
        Class<?> baseClass = method.getDeclaringClass();

        int mods = method.getModifiers();
        if ((mods & (Modifier.STATIC | Modifier.ABSTRACT)) != 0) {
            return false;
        } else if ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
            return true;
        } else if ((mods & Modifier.PRIVATE) != 0) {
            return (callingClass == baseClass);
        } else {
            return packageEquals(callingClass, baseClass);
        }
    }

    /**
     * Returns true if classes are defined in the same package, false
     * Copied from the Merlin java.io.ObjectStreamClass.
     */
    private static boolean packageEquals(Class<?> cl1, Class<?> cl2) {
        Package pkg1 = cl1.getPackage(), pkg2 = cl2.getPackage();
        return ((pkg1 == pkg2) || ((pkg1 != null) && (pkg1.equals(pkg2))));
    }

    @Override
    public MethodHandle writeReplaceForSerialization(Class<?> cl) {
        return toMethodHandle(getInheritableMethod(cl, "writeReplace", Object.class));
    }

    @Override
    public OptionalDataException newOptionalDataExceptionForSerialization(boolean endOfData) {
        try {
            return optionalDataExceptionConstructor.newInstance(endOfData);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new Error("Unable to create OptionalDataException");
        }
    }
}
