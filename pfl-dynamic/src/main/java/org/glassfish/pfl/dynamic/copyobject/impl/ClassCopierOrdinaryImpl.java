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
import org.glassfish.pfl.dynamic.copyobject.spi.Copy;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyInterceptor;
import org.glassfish.pfl.dynamic.copyobject.spi.LibraryClassLoader;
import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.WeakHashMap;

// General Object: use proper constructor, iterate over fields,
// get/set fields using Unsafe or reflection.  This also handles readResolve,
// but I don't think writeReplace is needed.
// XXX Add proper handling for custom marshalled classes.
public class ClassCopierOrdinaryImpl extends ClassCopierBase {

//******************************************************************************
// Static utilities 
//******************************************************************************

    /**
     * Bridge is used to access unsafe methods used to read and write
     * arbitrary data members in objects.
     * This is very fast, and completely ignores access
     * protections including final on fields.
     * NOTE WELL: Unsafe is capable of causing severe damage to the
     * VM, including causing the VM to dump core.  get and put calls
     * must only be made with offsets obtained from objectFieldOffset
     * calls.  Because of the dangerous nature of Unsafe, its use
     * must be carefully protected.
     */
    private static final Bridge BRIDGE_REF = AccessController.doPrivileged(
            new PrivilegedAction<Bridge>() {
                @Override
                public Bridge run() {
                    return Bridge.get();
                }
            }
    );


//******************************************************************************
// Method access utilities
//******************************************************************************

    private Object resolve(Object obj) {
        if (readResolveMethod != null) {
            try {
                return readResolveMethod.invoke(obj);
            } catch (Throwable t) {
                throw Exceptions.self.exceptionInReadResolve(obj, t);
            }
        } else {
            return obj;
        }
    }

//******************************************************************************
// Constructor access class
//******************************************************************************

    /**
     * Class used as a factory for the appropriate Serialization constructors.
     * Note that this cannot be exposed in another class (even package private),
     * because this would provide access to a constructor in some cases that
     * can never be used outside of special serialization or copy support.
     */
    private abstract static class ConstructorFactory {
        private ConstructorFactory() {
        }

        /**
         * Returns public no-arg constructor of given class, or null if none
         * found.  Access checks are disabled on the returned constructor
         * (if any), since the defining class may still be non-public.
         * <p>
         */
        private static Constructor<?> getExternalizableConstructor(Class<?> cl) {
            return BRIDGE_REF.newConstructorForExternalization(cl);
        }

        /**
         * Returns subclass-accessible no-arg constructor of first
         * non-serializable superclass, or null if none found.
         * Access checks are disabled on the
         * returned constructor (if any).
         * <p>
         */
        private static Constructor<?> getSerializableConstructor(Class<?> clazz) {
            return BRIDGE_REF.newConstructorForSerialization(clazz);
        }

        /**
         * Returns a constructor based on the first no-args constructor in
         * the super class chain.  This allows us to construct an instance
         * of any class at all, serializable or not.
         */
        private static Constructor<?> getDefaultConstructor(Class<?> clazz) {
            Constructor<?> constructor = null;
            Class<?> cl = clazz;
            while (cl != null) {
                try {
                    constructor = cl.getDeclaredConstructor();
                    break;
                } catch (NoSuchMethodException ex) {
                    cl = cl.getSuperclass();
                }
            }

            if (constructor == null) return null;

            return BRIDGE_REF.newConstructorForSerialization(clazz, constructor);
        }


        /**
         * Analyze the class to determine the correct constructor type.
         * Returns the appropriate constructor.
         */
        private static Constructor<?> makeConstructor(final Class<?> cls) {
            return AccessController.doPrivileged(
                    new PrivilegedAction<Constructor<?>>() {
                        @Override
                        public Constructor<?> run() {
                            Constructor constructor;

                            // We must check for Externalizable first, since Externalizable
                            // extends Serializable.
                            if (Externalizable.class.isAssignableFrom(cls)) {
                                constructor = getExternalizableConstructor(cls);
                            } else if (Serializable.class.isAssignableFrom(cls)) {
                                constructor = getSerializableConstructor(cls);
                            } else {
                                constructor = getDefaultConstructor(cls);
                            }

                            return constructor;
                        }
                    }
            );
        }
    }

//******************************************************************************
// ClassFieldCopiers and all their associated bits
//******************************************************************************

    // The first implementation of ClassFieldCopiers supported both reflective
    // and unsafe copiers.  However, the ORB now only runs on JDK implementations
    // that fully support the unsafe approach, so the old reflective code has been
    // removed.
    //
    // Unfortunately, we always need 9 field copier 
    // classes: Object plus the 8 primitive type wrappers.  Otherwise every 
    // getObject call would create a new primitive type wrapper, needlessly 
    // creating lots of garbage objects.
    //
    // Note that if a super class is copied in some other way, we must not attempt 
    // to build a ClassFieldCopier for the derived class.  We should just
    // throw a ReflectiveCopyException and fallback in that case.

    public interface ClassFieldCopier {
        /**
         * Copy all fields from src to dest, using
         * oldToNew as usual to preserve aliasing.  This copies
         * all fields declared in the class, as well as in the
         * super class.
         */
        void copy(Map<Object, Object> oldToNew,
                  Object src, Object dest) throws ReflectiveCopyException;
    }

    // Utilities for ClassFieldCopier instances.

    // Maps classes to ClassFieldCopier instances.
    private static Map<Class<?>, ClassFieldCopier> classToClassFieldCopier =
            DefaultClassCopierFactories.USE_FAST_CACHE ?
                    new FastCache<Class<?>, ClassFieldCopier>(new WeakHashMap<Class<?>, ClassFieldCopier>()) :
                    new WeakHashMap<Class<?>, ClassFieldCopier>();

    private static boolean useCodegenCopier() {
        return Boolean.getBoolean(
                "org.glassfish.dynamic.codegen.UseCodegenReflectiveCopyobject");
    }

    private static final Package CODEGEN_SPI =
            org.glassfish.pfl.dynamic.codegen.spi.Type.class.getPackage();
    private static final Package CODEGEN_IMPL =
            org.glassfish.pfl.dynamic.codegen.impl.Node.class.getPackage();

    private static ThreadLocal<Boolean> isCodegenCopierAllowed =
            new ThreadLocal<Boolean>() {
                @Override
                public Boolean initialValue() {
                    return Boolean.TRUE;
                }
            };

    // ONLY for use in spi.orbutil.codegen.Wrapper!
    public static void setCodegenCopierAllowed(boolean flag) {
        isCodegenCopierAllowed.set(flag);
    }

    private static synchronized ClassFieldCopier getClassFieldCopier(
            final Class<?> cls,
            final PipelineClassCopierFactory classCopierFactory)
            throws ReflectiveCopyException {
        ClassFieldCopier copier = classToClassFieldCopier.get(cls);

        if (copier == null) {
            try {
                copier = AccessController.doPrivileged(
                        new PrivilegedExceptionAction<ClassFieldCopier>() {
                            @Override
                            public ClassFieldCopier run()
                                    throws ReflectiveCopyException {
                                // Note that we can NOT generate a copier for
                                // classes used in codegen!  If we try, generating
                                // the copier uses codegen which calls the copier,
                                // which generates a copier using codegen, which...
                                // Just don't do this for anything that is in the
                                // codegen packages.
                                if (useCodegenCopier()
                                        && isCodegenCopierAllowed.get()) {
                                    return makeClassFieldCopierUnsafeCodegenImpl(
                                            cls, classCopierFactory);
                                } else {
                                    return new ClassFieldCopierUnsafeImpl(cls,
                                            classCopierFactory);
                                }
                            }
                        }
                );
            } catch (PrivilegedActionException exc) {
                throw (ReflectiveCopyException) exc.getException();
            }

            classToClassFieldCopier.put(cls, copier);
        }

        return copier;
    }

    // Get the superclass ClassFieldCopier, or return null if this is the end of the
    // chain.
    private static synchronized ClassFieldCopier getSuperCopier(
            PipelineClassCopierFactory ccf, Class<?> cls) throws ReflectiveCopyException {
        Class<?> superClass = cls.getSuperclass();
        ClassFieldCopier superCopier = null;
        if ((superClass != java.lang.Object.class) && (superClass != null)) {
            ClassCopier cachedCopier = ccf.lookupInCache(superClass);

            // If there is a cached ClassCopier, and it is not reflective, then cls
            // should not be copied by reflection.
            if ((cachedCopier != null)
                    && (!cachedCopier.isReflectiveClassCopier())) {
                throw Exceptions.self.noClassCopierForSuperclass(superClass);
            }

            // Return an error immediately, rather than waiting until the
            // superClass copier is invoked.
            if (!ccf.reflectivelyCopyable(superClass)) {
                throw new ReflectiveCopyException(
                        "Cannot create ClassFieldCopier for superclass " +
                                superClass.getName() +
                                ": This class cannot be copied.");
            }

            superCopier = getClassFieldCopier(superClass, ccf);
        }

        return superCopier;
    }

//******************************************************************************
//******************************************************************************

    /**
     * Use bridge to copy objects.  Now that we are on JDK 5, this has not
     * been known to fail on any VM (unlike on JDK 1.4.1).  Note that the
     * reflective copier should work now on JDK 5, due to the change in
     * suppressAccessChecks that supports writing to final fields.
     * This copier also supports @Copy annotations on fields.
     */
    private static class ClassFieldCopierUnsafeImpl implements ClassFieldCopier {
        private Class<?> myClass;

        // Note that fieldOffsets and fieldCopiers must always be the
        // same length.
        private long[] fieldOffsets;        // The field offsets of all
        // fields in this class, not
        // including inherited fields.
        private UnsafeFieldCopier[] fieldCopiers;   // The FieldCopier instances for
        // this class.

        // The ClassCopierFactory needed inside objectUnsafeFieldCopier.
        private PipelineClassCopierFactory classCopierFactory;

        // ClassFieldCopier for the immediate super class, if any.
        private ClassFieldCopier superCopier;

        // Note that most instances of UnsafeFieldCopier are stateless, in that
        // all needed info is passed in the copy call.  This means that
        // we only need instances of UnsafeFieldCopier for the primitive types
        // and Object.
        // Note: accessing to the static bridge is done through compiler synthesized
        // methods, which is noticeably slow given the number of times bridge is
        // accessed.  I think making bridge public or protected would avoid this,
        // but we can't expose an instance of Bridge, so we'll only pay for
        // access to bridge while constructing copiers, instead of while running them.
        private abstract static class UnsafeFieldCopier {
            protected Bridge bridge;

            public UnsafeFieldCopier(Bridge bridge) {
                this.bridge = bridge;
            }

            abstract void copy(Map<Object, Object> oldToNew, long offset,
                               Object src, Object dest)
                    throws ReflectiveCopyException;
        }

        // All of the XXXInitializer instances simply set the field to 0 or null
        private static UnsafeFieldCopier byteUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putByte(dest, offset, (byte) 0);
                    }

                    @Override
                    public String toString() {
                        return "byteUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier charUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putChar(dest, offset, (char) 0);
                    }

                    @Override
                    public String toString() {
                        return "charUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier shortUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putShort(dest, offset, (short) 0);
                    }

                    @Override
                    public String toString() {
                        return "shortUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier intUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putInt(dest, offset, 0);
                    }

                    @Override
                    public String toString() {
                        return "intUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier longUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putLong(dest, offset, 0);
                    }

                    @Override
                    public String toString() {
                        return "longUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier booleanUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putBoolean(dest, offset, false);
                    }

                    @Override
                    public String toString() {
                        return "booleanUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier floatUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putFloat(dest, offset, 0);
                    }

                    @Override
                    public String toString() {
                        return "floatUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier doubleUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putDouble(dest, offset, 0);
                    }

                    @Override
                    public String toString() {
                        return "doubleUnsafeFieldInitializer";
                    }
                };

        private UnsafeFieldCopier getPrimitiveFieldInitializer(Class<?> cls) {
            if (cls.equals(byte.class)) {
                return byteUnsafeFieldInitializer;
            } else if (cls.equals(char.class)) {
                return charUnsafeFieldInitializer;
            } else if (cls.equals(short.class)) {
                return shortUnsafeFieldInitializer;
            } else if (cls.equals(int.class)) {
                return intUnsafeFieldInitializer;
            } else if (cls.equals(long.class)) {
                return longUnsafeFieldInitializer;
            } else if (cls.equals(boolean.class)) {
                return booleanUnsafeFieldInitializer;
            } else if (cls.equals(float.class)) {
                return floatUnsafeFieldInitializer;
            } else if (cls.equals(double.class)) {
                return doubleUnsafeFieldInitializer;
            } else {
                // XXX use Exceptions
                throw new IllegalArgumentException(
                        "cls must be a primitive type");
            }
        }

        // The YYYUnsafeFieldCopier instances copy a field from source
        // to destination
        private static UnsafeFieldCopier byteUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        byte value = bridge.getByte(src, offset);
                        bridge.putByte(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "byteUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier charUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        char value = bridge.getChar(src, offset);
                        bridge.putChar(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "charUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier shortUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        short value = bridge.getShort(src, offset);
                        bridge.putShort(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "shortUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier intUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        int value = bridge.getInt(src, offset);
                        bridge.putInt(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "intUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier longUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        long value = bridge.getLong(src, offset);
                        bridge.putLong(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "longUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier booleanUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        boolean value = bridge.getBoolean(src, offset);
                        bridge.putBoolean(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "booleanUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier floatUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        float value = bridge.getFloat(src, offset);
                        bridge.putFloat(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "floatUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier doubleUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        double value = bridge.getDouble(src, offset);
                        bridge.putDouble(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "doubleUnsafeFieldCopier";
                    }
                };

        private UnsafeFieldCopier getPrimitiveFieldCopier(Class<?> cls) {
            if (cls.equals(byte.class)) {
                return byteUnsafeFieldCopier;
            }
            if (cls.equals(char.class)) {
                return charUnsafeFieldCopier;
            }
            if (cls.equals(short.class)) {
                return shortUnsafeFieldCopier;
            }
            if (cls.equals(int.class)) {
                return intUnsafeFieldCopier;
            }
            if (cls.equals(long.class)) {
                return longUnsafeFieldCopier;
            }
            if (cls.equals(boolean.class)) {
                return booleanUnsafeFieldCopier;
            }
            if (cls.equals(float.class)) {
                return floatUnsafeFieldCopier;
            }
            if (cls.equals(double.class)) {
                return doubleUnsafeFieldCopier;
            }
            // XXX use Exceptions
            throw new IllegalArgumentException("cls must be a primitive type");
        }

        // Various kinds of Object copiers
        //
        // The objectUnsafeFieldCopier is not stateless, as it requires access to the
        // ClassCopierFactory, so it cannot be static.
        private UnsafeFieldCopier objectUnsafeFieldCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map<Object, Object> oldToNew, long offset,
                                     Object src, Object dest) throws ReflectiveCopyException {
                        Object obj = bridge.getObject(src, offset);

                        Object result = null;

                        if (obj != null) {
                            // This lookup must be based on the actual type, not the
                            // declared type to allow for polymorphism.
                            ClassCopier copier = classCopierFactory.getClassCopier(
                                    obj.getClass());

                            result = copier.copy(oldToNew, obj);
                        }

                        bridge.putObject(dest, offset, result);
                    }

                    @Override
                    public String toString() {
                        return "objectUnsafeFieldCopier";
                    }
                };

        private static UnsafeFieldCopier objectUnsafeFieldInitializer =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putObject(dest, offset, null);
                    }

                    @Override
                    public String toString() {
                        return "objectUnsafeFieldInitializer";
                    }
                };

        private static UnsafeFieldCopier objectUnsafeFieldSourceCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putObject(dest, offset, src);
                    }

                    @Override
                    public String toString() {
                        return "objectUnsafeFieldSourceCopier";
                    }
                };

        private static UnsafeFieldCopier objectUnsafeFieldResultCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        bridge.putObject(dest, offset, src);
                    }

                    @Override
                    public String toString() {
                        return "objectUnsafeFieldResultCopier";
                    }
                };

        private static UnsafeFieldCopier objectUnsafeFieldIdentityCopier =
                new UnsafeFieldCopier(BRIDGE_REF) {

                    @Override
                    public void copy(Map oldToNew, long offset, Object src,
                                     Object dest) {
                        Object value = bridge.getObject(src, offset);
                        bridge.putObject(dest, offset, value);
                    }

                    @Override
                    public String toString() {
                        return "objectUnsafeFieldIdentityCopier";
                    }
                };

        private UnsafeFieldCopier getUnsafeFieldCopier(Field fld) {
            Class<?> defType = fld.getDeclaringClass();
            Class<?> fldType = fld.getType();
            Copy copyAnnotation = fld.getAnnotation(Copy.class);

            if (copyAnnotation == null) {
                if (fldType.isPrimitive()) {
                    return getPrimitiveFieldCopier(fldType);
                } else {
                    return objectUnsafeFieldCopier;
                }
            } else {
                switch (copyAnnotation.value()) {
                    case RECURSE:
                        if (fldType.isPrimitive()) {
                            return getPrimitiveFieldCopier(fldType);
                        } else {
                            return objectUnsafeFieldCopier;
                        }
                    case IDENTITY:
                        if (fldType.isPrimitive()) {
                            return getPrimitiveFieldCopier(fldType);
                        } else {
                            return objectUnsafeFieldIdentityCopier;
                        }
                    case NULL:
                        if (fldType.isPrimitive()) {
                            return getPrimitiveFieldInitializer(fldType);
                        } else {
                            return objectUnsafeFieldInitializer;
                        }
                    case SOURCE:
                        if (fldType.isAssignableFrom(defType)) {
                            return objectUnsafeFieldSourceCopier;
                        } else {
                            throw new IllegalArgumentException(
                                    "Cannot assign field to source object: "
                                            + "incompatible types\n"
                                            + "Field type is " + fldType
                                            + " Class type is " + defType);
                        }
                    case RESULT:
                        if (fldType.isAssignableFrom(defType)) {
                            return objectUnsafeFieldResultCopier;
                        } else {
                            throw new IllegalArgumentException(
                                    "Cannot assign field to result object: "
                                            + "incompatible types\n"
                                            + "Field type is " + fldType
                                            + " Class type is " + defType);
                        }
                    default:
                        throw new IllegalArgumentException(
                                "Unhandled case " + copyAnnotation.value()
                                        + " for field " + fld);
                }
            }
        }

        private boolean fieldIsCopyable(Field field) {
            int modifiers = field.getModifiers();
            boolean result = !Modifier.isStatic(modifiers);
            return result;
        }

        public ClassFieldCopierUnsafeImpl(Class<?> cls,
                                          PipelineClassCopierFactory ccf) throws ReflectiveCopyException {
            myClass = cls;
            classCopierFactory = ccf;
            superCopier = getSuperCopier(ccf, cls);

            // Count the number of non-static fields.  These are the
            // ones we must copy.
            Field[] fields = cls.getDeclaredFields();
            int numFields = 0;
            for (int ctr = 0; ctr < fields.length; ctr++) {
                if (fieldIsCopyable(fields[ctr])) {
                    numFields++;
                }
            }

            fieldOffsets = new long[numFields];
            fieldCopiers = new UnsafeFieldCopier[numFields];

            // Initialze offsets and field copiers for non-static
            // fields.
            int pos = 0;
            for (int ctr = 0; ctr < fields.length; ctr++) {
                Field fld = fields[ctr];
                if (fieldIsCopyable(fld)) {
                    fieldOffsets[pos] = BRIDGE_REF.objectFieldOffset(fld);
                    fieldCopiers[pos] = getUnsafeFieldCopier(fld);
                    pos++;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ClassFieldCopierUnsafeImpl[");
            sb.append(myClass.getName());
            for (int ctr = 0; ctr < fieldOffsets.length; ctr++) {
                sb.append("\n\t");
                sb.append(fieldOffsets[ctr]);
                sb.append(':');
                sb.append(fieldCopiers[ctr].toString());
            }
            sb.append("\n]");
            return sb.toString();
        }

        @Override
        public void copy(Map<Object, Object> oldToNew, Object source,
                         Object result) throws ReflectiveCopyException {
            if (superCopier != null) {
                ((ClassFieldCopierUnsafeImpl) superCopier).copy(
                        oldToNew, source, result);
            }

            for (int ctr = 0; ctr < fieldOffsets.length; ctr++) {
                fieldCopiers[ctr].copy(oldToNew, fieldOffsets[ctr],
                        source, result);
            }
        }
    }

//******************************************************************************
//******************************************************************************

    private static Map<Class<?>, Constructor<?>> classToConstructor =
            DefaultClassCopierFactories.USE_FAST_CACHE ?
                    new FastCache<Class<?>, Constructor<?>>(
                            new WeakHashMap<Class<?>, Constructor<?>>()) :
                    new WeakHashMap<Class<?>, Constructor<?>>();

    /**
     * Use bridge with code generated by codegen to copy objects.
     * This method must be invoked
     * from inside a doPrivileged call.
     */
    private static synchronized ClassFieldCopier
    makeClassFieldCopierUnsafeCodegenImpl(
            final Class<?> cls, final PipelineClassCopierFactory classCopierFactory)
            throws ReflectiveCopyException {
        Constructor<?> cons = classToConstructor.get(cls);
        if (cons == null) {
            final String className =
                    "org.glassfish.dynamic.codegen.impl.generated.copiers." +
                            cls.getName() + "Copier";
            final CodegenCopierGenerator generator =
                    new CodegenCopierGenerator(className, cls);
            final ProtectionDomain pd = cls.getProtectionDomain();
            final Class<?> copierClass = generator.create(pd,
                    LibraryClassLoader.getClassLoader());

            try {
                cons = copierClass.getDeclaredConstructor(
                        PipelineClassCopierFactory.class, ClassFieldCopier.class);
            } catch (Exception exc) {
                // XXX use Exceptions
                throw new ReflectiveCopyException(
                        "Could not access unsafe codegen copier constructor", exc);
            }

            classToConstructor.put(cls, cons);
        }

        ClassFieldCopier copier = null;

        try {
            ClassFieldCopier superCopier = getSuperCopier(classCopierFactory,
                    cls);
            Object[] args = new Object[]{classCopierFactory, superCopier};
            copier = (ClassFieldCopier) cons.newInstance(args);
        } catch (Exception exc) {
            // XXX use Exceptions
            throw new ReflectiveCopyException(
                    "Could not create unsafe codegen copier", exc);
        }

        return copier;
    }

//******************************************************************************
// private data of ClassCopierOrdinaryImpl
//******************************************************************************

    // Actually copies the declared fields in this class.
    private ClassFieldCopier classFieldCopier;

    // The appropriate constructor for this class, as defined by Java 
    // serialization, or the default constructor if the class is not serializable.
    private Constructor<?> constructor;

    // Null unless the class defines a readResolve() method.
    private MethodHandle readResolveMethod;

//******************************************************************************
// Implementation
//******************************************************************************

    public ClassCopierOrdinaryImpl(PipelineClassCopierFactory ccf,
                                   Class<?> cls) throws ReflectiveCopyException {
        super(cls.getName(), true);

        classFieldCopier = getClassFieldCopier(cls, ccf);
        constructor = ConstructorFactory.makeConstructor(cls);
        readResolveMethod = BRIDGE_REF.readResolveForSerialization(cls);

        // XXX handle custom marshalled objects.
    }

    @Override
    public Object createCopy(Object source) throws ReflectiveCopyException {
        try {
            return constructor.newInstance();
        } catch (Exception exc) {
            // XXX log this
            throw new ReflectiveCopyException(
                    "Failure in newInstance for constructor " + constructor, exc);
        }
    }

    // Copy all of the non-static fields from source to dest, starting with 
    // java.lang.Object.
    @Override
    public Object doCopy(Map<Object, Object> oldToNew, Object source,
                         Object result) throws ReflectiveCopyException {
        if (source instanceof CopyInterceptor) {
            // Note that result will also be an instance of CopyInterceptor in this case.
            ((CopyInterceptor) source).preCopy();
            classFieldCopier.copy(oldToNew, source, result);
            ((CopyInterceptor) result).postCopy();

            return resolve(result);
        } else {
            classFieldCopier.copy(oldToNew, source, result);

            return resolve(result);
        }
    }
}
