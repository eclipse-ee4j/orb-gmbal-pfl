/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.basic.func.UnaryPredicate;

/** Given an AnnotatedElement, fetch all of the inherited annotations.
 * This include annotations on methods that may be
 * overridden.  Uses ClassAnalyzer to linearize the inheritance hierarchy.
 * This also includes any added annotations for Class and Method.
 * Constructor, Field, Parameter, and Package annotations are just translated
 * from the standard reflective calls.
 *
 * @author ken_admin
 */
public class AnnotationAnalyzer {
    private final Map<AnnotatedElement,Map<Class<?>,Annotation>> annotationCache =
        new WeakHashMap<AnnotatedElement,Map<Class<?>,Annotation>>() ;

    private final Map<AnnotatedElement, Map<Class<?>, Annotation>> addedAnnotations =
        new HashMap<AnnotatedElement,Map<Class<?>,Annotation>>();

    private synchronized <K,V> void putIfNotPresent( final Map<K,V> map,
        final K key, final V value ) {
        if (!map.containsKey( key )) {
            map.put( key, value ) ;
        }
    }

    /** Add an annotation to element, which must be a Class, Method,
     * or Constructor.
     *
     * @param element
     * @param annotation
     */
    public synchronized void addAnnotation( AnnotatedElement element,
        Annotation annotation ) {
        if (annotation == null) {
            throw new RuntimeException( "Cannot add null annotation to "
                + "annotated element " + element ) ;
        }

        Map<Class<?>, Annotation> map = addedAnnotations.get( element ) ;
        if (map == null) {
            map = new HashMap<Class<?>, Annotation>() ;
            addedAnnotations.put( element, map ) ;
        }

        Class<?> annotationType = annotation.annotationType() ;
        Annotation ann = map.get( annotationType ) ;
        if (ann != null) {
            throw new RuntimeException( "Duplicate annotatio "
                + annotation.getClass().getName()
                + " for element " + element ) ;
        }

        map.put( annotationType, annotation ) ;
    }

    /** Add all annotations on cls (including inherited annotations
     * and its methods (including overridden methods in super classes and
     * interfaces) to super (which must be a super class or interface of cls).
     *
     * @param cls
     */
    public synchronized void addInheritedAnnotations( final Class<?> cls,
        final Class<?> ancestor ) {
        if (!ancestor.isAssignableFrom(cls)) {
            throw new RuntimeException( "Ancestor " + ancestor
                + " is not assignment compatible with " + cls ) ;
        }

        // added class annotations
        final Map<Class<?>,Annotation> classAnnos =
            getAnnotations( cls, false ) ;
        addedAnnotations.put( ancestor, classAnnos ) ;

        // added method annotations

        final ClassAnalyzer clsCA = ClassAnalyzer.getClassAnalyzer(cls) ;

        final ClassAnalyzer ancestorCA =
            ClassAnalyzer.getClassAnalyzer( ancestor );

        // Just construct a list of all reachable classes from ancestor.
        final Set<Class<?>> ancestorClasses =
            new HashSet<Class<?>>() ;
        ancestorCA.findClasses( new UnaryPredicate<Class<?>>() {
            @Override
            public boolean evaluate( final Class<?> arg) {
                ancestorClasses.add(arg) ;
                return true ;
            }
        });

        // Construct a map from method type to annotation map for
        // all method types found in classes reachable from cls but not
        // from ancestor.
        final Map<Pair<String,List<Class<?>>>,Map<Class<?>,Annotation>> map =
            new HashMap<Pair<String,List<Class<?>>>,Map<Class<?>,Annotation>>() ;

        clsCA.findMethods( new UnaryPredicate<Method>() {
            @Override
            public boolean evaluate(Method arg) {
                // Only include annotations for methods declared in
                // classes not reachable from ancestor.
                if (!ancestorClasses.contains(arg.getDeclaringClass())) {
                    final Pair<String,List<Class<?>>> key = new
                        Pair<String,List<Class<?>>>( arg.getName(),
                            Arrays.asList( arg.getParameterTypes() ) ) ;

                    Map<Class<?>, Annotation> annos = map.computeIfAbsent(key, k -> new HashMap<>());

                    for (Annotation anno : arg.getDeclaredAnnotations()) {
                        putIfNotPresent(annos, anno.annotationType(), anno);
                    }
                }
                return true ;
            }
        }) ;

        // Store annotation maps from map into first method found from
        // ancestor.
        ancestorCA.findMethods( new UnaryPredicate<Method>() {
            @Override
            public boolean evaluate(Method arg) {
                final Pair<String,List<Class<?>>> key = new
                    Pair<String,List<Class<?>>>( arg.getName(),
                        Arrays.asList( arg.getParameterTypes() ) ) ;
                Map<Class<?>,Annotation> annos = map.get( key ) ;
                if (annos != null && !annos.isEmpty()) {
                    addedAnnotations.put( arg, annos ) ;
                    map.remove( key ) ;
                }
                return true ;
            }
        })  ;
    }

    /** Return a map of all annotations defined on cls and its super
     * classes and interfaces in ClassAnalyzer order.  Annotations nearer
     * the front of the list replace those later in the list of the same type.
     * @param cls Class to analyze.
     * @return Map from annotation class to annotation value.
     */
    public Map<Class<?>,Annotation> getAnnotations( final Class<?> cls ) {
        return getAnnotations( cls, true ) ;
    }

    private Map<Class<?>,Annotation> getAnnotations( final Class<?> cls,
        final boolean includeAddedAnnotations ) {
        final Map<Class<?>,Annotation> result = annotationCache.get( cls ) ;

        if (result == null) {
            final Map<Class<?>,Annotation> res =
                new HashMap<Class<?>,Annotation>() ;

            final ClassAnalyzer ca = ClassAnalyzer.getClassAnalyzer(cls) ;
            ca.findClasses( new UnaryPredicate<Class<?>>() {
                @Override
                public boolean evaluate(Class<?> arg) {
                    // First, put in declared annotations if not already present.
                    Annotation[] annots = arg.getDeclaredAnnotations() ;
                    for (Annotation anno : annots) {
                        putIfNotPresent( res, anno.annotationType(), anno ) ;
                    }

                    if (includeAddedAnnotations) {
                        // Then, put in added annotations if not already present.
                        final Map<Class<?>,Annotation> emap =
                            addedAnnotations.get( arg ) ;
                        if (emap != null) {
                            for (Map.Entry<Class<?>,Annotation> entry
                                : emap.entrySet()) {

                                putIfNotPresent( res, entry.getKey(),
                                    entry.getValue()) ;
                            }
                        }
                    }

                    return true ; // evaluate everything
                }
            }) ;

            annotationCache.put( cls, res ) ;
            return res ;
        }

        return result ;
    }

    /** Return a map of all annotations defined in method and its overriden
     * methods in the inheritance order of the ClassAnalyzer for the method's
     * defining class.
     * @param method The method to analyze
     * @return A map from annotation class to annotation
     */
    public Map<Class<?>,Annotation> getAnnotations( Method method ) {
        return getAnnotations( method, true ) ;
    }

    private Map<Class<?>,Annotation> getAnnotations( final Method method,
        final boolean includeAddedAnnotations ) {
        final Map<Class<?>,Annotation> result = annotationCache.get( method ) ;

        if (result == null) {
            final Class<?> cls = method.getDeclaringClass() ;
            final Map<Class<?>,Annotation> res =
                new HashMap<Class<?>,Annotation>() ;

            final String methodName = method.getName() ;
            final Class<?>[] methodParamTypes = method.getParameterTypes() ;

            final ClassAnalyzer ca = ClassAnalyzer.getClassAnalyzer(cls) ;
            ca.findClasses( new UnaryPredicate<Class<?>>() {
                @Override
                public boolean evaluate(Class<?> arg) {
                    Method overriddenMethod = null ;
                    try {
                        overriddenMethod = arg.getDeclaredMethod(
                            methodName, methodParamTypes) ;
                    } catch (Exception exc) {
                        // ignore this exception: just return on null.
                    }

                    if (overriddenMethod == null) {
                        // Method not defined in class arg, so skip it.
                        return true ;
                    }

                    if (includeAddedAnnotations) {
                        // First, put in added annotations if not already present.
                        final Map<Class<?>,Annotation> emap =
                            addedAnnotations.get( overriddenMethod ) ;
                        if (emap != null) {
                            for (Map.Entry<Class<?>,Annotation> entry
                                : emap.entrySet()) {

                                putIfNotPresent( res, entry.getKey(),
                                    entry.getValue()) ;
                            }
                        }
                    }

                    // Then, put in declared annotations if not already present.
                    final Annotation[] annots =
                        overriddenMethod.getDeclaredAnnotations() ;
                    for (Annotation anno : annots) {
                        putIfNotPresent( res, anno.annotationType(), anno ) ;
                    }

                    return true ; // evaluate everything
                }
            }) ;

            annotationCache.put( method, res ) ;
            return res ;
        }

        return result ;
    }

    private Map<Class<?>,Annotation> makeAnnoMap( Annotation[] annos ) {
        Map<Class<?>,Annotation> result =
            new HashMap<Class<?>,Annotation>() ;
        for (Annotation anno : annos ) {
            result.put( anno.annotationType(), anno ) ;
        }
        return result ;
    }

    /** Same as cons.getParameterAnnotations, with the result converted to a
     * list of maps.
     *
     * @param method A Java Method
     * @return A list of maps from annotation class to annotation value
     */
    public List<Map<Class<?>,Annotation>> getParameterAnnotations(
        Method method ) {
        final List<Map<Class<?>,Annotation>> result = 
            new ArrayList<Map<Class<?>,Annotation>>() ;
        Annotation[][] pannos = method.getParameterAnnotations() ;
        for (Annotation[] annos : pannos) {
            final Map<Class<?>,Annotation> element =
                makeAnnoMap( annos ) ;
            result.add( element ) ;
        }

        return result ;
    }

    /** Same as cons.getAnnotations, with the result converted to a map.
     *
     * @param cons A Java Constructor
     * @return A map from annotation class to annotation value
     */
    public Map<Class<?>,Annotation> getAnnotations( Constructor<?> cons ) {
        return makeAnnoMap( cons.getDeclaredAnnotations() ) ;
    }

    /** Same as cons.getParameterAnnotations, with the result converted to a
     * list of maps.
     *
     * @param cons A Java Constructor
     * @return A list of maps from annotation class to annotation value
     */
    public List<Map<Class<?>,Annotation>> getParameterAnnotations(
        Constructor<?> cons ) {
        final List<Map<Class<?>,Annotation>> result =
            new ArrayList<Map<Class<?>,Annotation>>() ;
        Annotation[][] pannos = cons.getParameterAnnotations() ;
        for (Annotation[] annos : pannos) {
            Map<Class<?>,Annotation> element =
                makeAnnoMap( annos ) ;
            result.add( element ) ;
        }

        return result ;
    }

    /** Same as fld.getAnnotations, with the result converted to a map.
     *
     * @param fld A Java Field
     * @return A map from annotation class to annotation value
     */
    public Map<Class<?>,Annotation> getAnnotations( Field fld ) {
        return makeAnnoMap( fld.getDeclaredAnnotations() ) ;
    }

    /** Same as pkg.getAnnotations, with the result converted to a map.
     *
     * @param pkg A Java Package
     * @return A map from annotation class to annotation value
     */
    public Map<Class<?>,Annotation> getAnnotations( Package pkg ) {
        return makeAnnoMap( pkg.getDeclaredAnnotations() ) ;
    }

    /** Return all annotations on the element, including any added annotations.
     * Really just a convenience wrapper for the other getAnnotations methods.
     * @param elem AnnotatedElement
     * @return A map from annotation class to annotation value.
     */
    public Map<Class<?>,Annotation> getAnnotations( AnnotatedElement elem ) {
        if (elem instanceof Class<?>) {
            return getAnnotations( (Class<?>)elem ) ;
        } else if (elem instanceof Method) {
            return getAnnotations( (Method)elem ) ;
        } else if (elem instanceof Constructor) {
            return getAnnotations( (Constructor)elem ) ;
        } else if (elem instanceof Field) {
            return getAnnotations( (Field)elem ) ;
        } else if (elem instanceof Package) {
            return getAnnotations( (Package)elem ) ;
        } else {
            return null ;
        }
    }

    public <A extends Annotation> A getAnnotation( AnnotatedElement elem,
	Class<A> cls ) {

	Annotation anno = getAnnotations( elem ).get( cls ) ;
	return cls.cast( anno ) ;
    }
}
