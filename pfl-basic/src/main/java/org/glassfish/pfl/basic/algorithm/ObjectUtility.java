/*
 * Copyright (c) 2002, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Services Ltd.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm;

import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.basic.reflection.FieldValueHelper;

import javax.management.ObjectName;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * General object related utilities.
 */
public final class ObjectUtility {

    interface ObjectPrinter {
        void print(IdentityHashMap printed, ObjectWriter buff, Object obj);

        // Returns true if this printer should ALWAYS print, even if
        // the object has been visited before.
        boolean alwaysPrint();
    }

    private static class ClassMap {
        List<Pair<Class<?>, ObjectPrinter>> data;

        public ClassMap() {
            data = new ArrayList<>();
        }

        /**
         * Return the first element of the ClassMap that is assignable to cls.
         * The order is determined by the order in which the put method was
         * called.  Returns null if there is no match.
         */
        public ObjectPrinter get(Class cls) {
            for (Pair<Class<?>, ObjectPrinter> pair : data) {
                if (pair.first().isAssignableFrom(cls)) {
                    return pair.second();
                }
            }

            return null;
        }

        /**
         * Add obj to the map with key cls.  Note that order matters,
         * as the first match is returned.
         */
        public void put(Class cls, ObjectPrinter obj) {
            Pair<Class<?>, ObjectPrinter> pair = new Pair<Class<?>, ObjectPrinter>(cls, obj);
            data.add(pair);
        }
    }


    private ObjectPrinter generalObjectPrinter = new ObjectPrinter() {
        public void print(IdentityHashMap printed, ObjectWriter buff, java.lang.Object obj) {
            handleObject(printed, buff, obj);
        }

        public boolean alwaysPrint() {
            return false;
        }
    };

    private ObjectPrinter arrayPrinter = new ObjectPrinter() {
        public void print(IdentityHashMap printed, ObjectWriter buff, java.lang.Object obj) {
            handleArray(printed, buff, obj);
        }

        public boolean alwaysPrint() {
            return false;
        }
    };

    private static ObjectPrinter propertiesPrinter = new ObjectPrinter() {
        public void print(IdentityHashMap printed, ObjectWriter buff, java.lang.Object obj) {

            if (!(obj instanceof Properties)) {
                throw new Error();
            }

            Properties props = (Properties) obj;
            Enumeration keys = props.propertyNames();
            while (keys.hasMoreElements()) {
                String key = (String) (keys.nextElement());
                String value = props.getProperty(key);
                buff.startElement();
                buff.append(key);
                buff.append("=");
                buff.append(value);
                buff.endElement();
            }
        }

        public boolean alwaysPrint() {
            return true;
        }
    };

    private ObjectPrinter collectionPrinter = new ObjectPrinter() {
        public void print(IdentityHashMap printed, ObjectWriter buff, java.lang.Object obj) {
            if (!(obj instanceof Collection)) {
                throw new Error();
            }

            Collection coll = (Collection) obj;
            Iterator iter = coll.iterator();
            while (iter.hasNext()) {
                java.lang.Object element = iter.next();
                buff.startElement();
                objectToStringHelper(printed, buff, element);
                buff.endElement();
            }
        }

        public boolean alwaysPrint() {
            return false;
        }
    };

    private ObjectPrinter mapPrinter = new ObjectPrinter() {
        public void print(IdentityHashMap printed, ObjectWriter buff, java.lang.Object obj) {
            if (!(obj instanceof Map)) {
                throw new Error();
            }

            Map map = (Map) obj;
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Entry entry = (Entry) (iter.next());
                buff.startElement();
                objectToStringHelper(printed, buff, entry.getKey());
                buff.append("=>");
                objectToStringHelper(printed, buff, entry.getValue());
                buff.endElement();
            }
        }

        public boolean alwaysPrint() {
            return false;
        }
    };

    private static ObjectPrinter toStringPrinter = new ObjectPrinter() {
        public void print(IdentityHashMap printed, ObjectWriter buff,
                          java.lang.Object obj) {

            buff.append(obj.toString());
        }

        public boolean alwaysPrint() {
            return true;
        }
    };

    // Order matters here: subclasses must appear before superclasses!
    private final Object[][] CLASS_MAP_DATA = {
            {Integer.class, toStringPrinter},
            {BigInteger.class, toStringPrinter},
            {BigDecimal.class, toStringPrinter},
            {String.class, toStringPrinter},
            {StringBuffer.class, toStringPrinter},
            {StringBuilder.class, toStringPrinter},
            {Long.class, toStringPrinter},
            {Short.class, toStringPrinter},
            {Byte.class, toStringPrinter},
            {Character.class, toStringPrinter},
            {Float.class, toStringPrinter},
            {Double.class, toStringPrinter},
            {Boolean.class, toStringPrinter},
            {Date.class, toStringPrinter},
            {ObjectName.class, toStringPrinter},
            {CompositeData.class, toStringPrinter},
            {CompositeType.class, toStringPrinter},
            {TabularData.class, toStringPrinter},
            {TabularType.class, toStringPrinter},
            {ArrayType.class, toStringPrinter},
            {Class.class, toStringPrinter},
            {Method.class, toStringPrinter},
            {Thread.class, toStringPrinter},
            {AtomicInteger.class, toStringPrinter},
            {AtomicLong.class, toStringPrinter},
            {AtomicBoolean.class, toStringPrinter},
            {Properties.class, propertiesPrinter},
            {Collection.class, collectionPrinter},
            {Map.class, mapPrinter},
    };

    private ClassMap classMap;
    private boolean isIndenting;
    private int initialLevel;
    private int increment;

    private static ObjectUtility standard = new ObjectUtility(true, 0, 4);
    private static ObjectUtility compact = new ObjectUtility(false, 0, 4);

    public ObjectUtility(boolean isIndenting, int initialLevel, int increment) {
        this.isIndenting = isIndenting;
        this.initialLevel = initialLevel;
        this.increment = increment;
        this.classMap = new ClassMap();
        for (Object[] pair : CLASS_MAP_DATA) {
            Class<?> key = (Class<?>) pair[0];
            ObjectPrinter value = (ObjectPrinter) pair[1];
            classMap.put(key, value);
        }
    }

    public ObjectUtility useToString(Class cls) {
        classMap.put(cls, toStringPrinter);
        return this;
    }

    /**
     * A convenience method that gives the default behavior: use indenting
     * to display the object's structure and do not use built-in toString
     * methods.
     *
     * @param object Object to print.
     * @return the String representation of obj.
     */
    public static String defaultObjectToString(java.lang.Object object) {
        return standard.objectToString(object);
    }

    /**
     * A convenience method that gives the default behavior: do not use
     * indenting to display the object's structure.
     *
     * @param object Object to print.
     * @return the String representation of obj.
     */
    public static String compactObjectToString(java.lang.Object object) {
        return compact.objectToString(object);
    }

    /**
     * objectToString handles display of arbitrary objects.  It correctly
     * handles objects whose elements form an arbitrary graph.  It uses
     * reflection to display the contents of any kind of object.
     * An object's toString() method may optionally be used, but the default
     * is to ignore all toString() methods except for those defined for
     * primitive types, primitive type wrappers, and strings.
     */
    public String objectToString(java.lang.Object obj) {
        IdentityHashMap printed = new IdentityHashMap();
        ObjectWriter result = ObjectWriter.make(isIndenting, initialLevel, increment);
        objectToStringHelper(printed, result, obj);
        return result.toString();
    }

//===========================================================================
//  Implementation
//===========================================================================

    ObjectPrinter classify(Class cls) {
        if (cls.isEnum()) {
            return toStringPrinter;
        } else if (cls.isArray()) {
            return arrayPrinter;
        } else {
            ObjectPrinter result = classMap.get(cls);
            if (result == null) {
                return generalObjectPrinter;
            } else {
                return result;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void objectToStringHelper(IdentityHashMap printed,
                                      ObjectWriter result, java.lang.Object obj) {
        if (obj == null) {
            result.append("null");
            result.endElement();
        } else {
            Class cls = obj.getClass();
            ObjectPrinter opr = classify(cls);
            result.startObject(obj);
            try {
                if (opr.alwaysPrint()) {
                    opr.print(printed, result, obj);
                } else {
                    if (printed.keySet().contains(obj)) {
                        result.append("*VISITED*");
                    } else {
                        printed.put(obj, null);
                        opr.print(printed, result, obj);
                    }
                }
            } finally {
                result.endObject();
            }
        }
    }

    // Determine whether or not the package of class cls is 
    // accessible.  Handles arrays as well as normal classes.
    private void checkPackageAccess(Class cls) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            String cname = cls.getName().replace('/', '.');
            if (cname.startsWith("[")) {
                final int lastBracket = cname.lastIndexOf("[") + 2;
                if (lastBracket > 1 && lastBracket < cname.length()) {
                    cname = cname.substring(lastBracket);
                }

                final int lastDot = cname.lastIndexOf('.');
                if (lastDot != -1) {
                    final String pname = cname.substring(0, lastDot);
                    sm.checkPackageAccess(pname);
                }
            }
        }
    }

    private Field[] getDeclaredFields(Class cls) {
        checkPackageAccess(cls);
        return cls.getDeclaredFields();
    }

    @SuppressWarnings("unchecked")
    private void handleObject(IdentityHashMap printed, ObjectWriter result,
                              java.lang.Object obj) {
        Class cls = obj.getClass();

        try {
            SecurityManager security = System.getSecurityManager();
            List<Field> allFields = new ArrayList<Field>();
            Class current = cls;
            while (!current.equals(Object.class)) {
                Field[] fields;
                // If the security manager is not null, throw a security exception
                // if current is NOT accessible from the caller.
                if (security != null && !Modifier.isPublic(current.getModifiers())) {
                    fields = new Field[0];
                } else {
                    fields = getDeclaredFields(current);
                }

                allFields.addAll(Arrays.asList(fields));

                current = current.getSuperclass();
            }

            for (final Field fld : allFields) {
                int modifiers = fld.getModifiers();
                if (fld.isAnnotationPresent(DumpIgnore.class)) {
                    continue;
                }

                // Do not display field if it is static, since these fields
                // are always the same for every instances.  This could
                // be made configurable, but I don't think it is
                // useful to do so.
                if (!Modifier.isStatic(modifiers)) {
                    if (security != null) {
                        if (!Modifier.isPublic(modifiers)) {
                            continue;
                        }
                    }
                    result.startElement();
                    result.append(fld.getName());
                    result.append("=");
                    Object value = FieldValueHelper.getFieldValue(obj, fld);

                    if (fld.isAnnotationPresent(DumpToString.class)) {
                        toStringPrinter.print(printed, result, value);
                    } else {
                        objectToStringHelper(printed, result, value);
                    }

                    result.endElement();
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            // Just ignore the exception here
            result.append(obj.toString());
        }
    }

    private void handleArray(IdentityHashMap printed, ObjectWriter result,
                             java.lang.Object obj) {
        Class compClass = obj.getClass().getComponentType();
        if (compClass == boolean.class) {
            boolean[] arr = (boolean[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else if (compClass == byte.class) {
            byte[] arr = (byte[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else if (compClass == short.class) {
            short[] arr = (short[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else if (compClass == int.class) {
            int[] arr = (int[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else if (compClass == long.class) {
            long[] arr = (long[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else if (compClass == char.class) {
            char[] arr = (char[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else if (compClass == float.class) {
            float[] arr = (float[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else if (compClass == double.class) {
            double[] arr = (double[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                result.append(arr[ctr]);
                result.endElement();
            }
        } else { // array of object
            java.lang.Object[] arr = (java.lang.Object[]) obj;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                result.startElement();
                objectToStringHelper(printed, result, arr[ctr]);
                result.endElement();
            }
        }
    }
}
