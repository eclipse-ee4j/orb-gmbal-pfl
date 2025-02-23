/*
 * Copyright (c) 2007, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Services Ltd.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm ;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List ;
import java.util.Map ;
import java.util.Set;

import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.basic.func.BinaryFunction;
import org.glassfish.pfl.basic.func.UnaryFunction;
import org.glassfish.pfl.basic.func.UnaryPredicate;

public final class Algorithms {
    private Algorithms() {}
    
    /**
     * Converts an array of objects into a list
     * @param <T> type of objects
     * @param arg the array of objects
     * @return list of objects
     * @deprecated replaced by {@link Arrays#asList(java.lang.Object...)}
     */
    @Deprecated
    public static <T> List<T> list( T... arg ) {
        return Arrays.asList(arg) ;
    }

    public static <S,T> Pair<S,T> pair( S first, T second ) {
        return new Pair<S,T>( first, second ) ;
    }
    
    public static <K,V> Map<K,V> map( Pair<K,V>... pairs ) {
        Map<K,V> result = new HashMap<K,V>() ;
        for (Pair<K,V> pair : pairs ) {
            result.put( pair.first(), pair.second() ) ;
        }
        return result ;
    }
        
    public static <A,R> UnaryFunction<A,R> mapToFunction( final Map<A,R> map ) {
        return new UnaryFunction<A,R>() {
            @Override
            public R evaluate( A arg ) {
                return map.get( arg ) ;
            }
        } ;
    }

    public static <A,R> void map( final Collection<A> arg, 
        final Collection<R> result,
        final UnaryFunction<A,R> func ) {

        for (A a : arg) {
            final R newArg = func.evaluate( a ) ;
            if (newArg != null) {
                result.add( newArg ) ;
            }
        }
    }

    public static <K,A,R> Map<K,R> map( final Map<K,A> arg,
        final UnaryFunction<A,R> func ) {
        
        Map<K,R> result = new HashMap<K,R>() ;
        for (Map.Entry<K,A> entry : arg.entrySet()) {
            result.put( entry.getKey(), 
                func.evaluate( entry.getValue())) ;
        }
        
        return result ;
    }
    
    public static <A,R> List<R> map( final List<A> arg, 
        final UnaryFunction<A,R> func ) {

	final List<R> result = new ArrayList<R>() ;
	map( arg, result, func ) ;
	return result ;
    }

    public static <A> UnaryPredicate<A> and(
        final UnaryPredicate<A> arg1,
        final UnaryPredicate<A> arg2 ) {
    
        return new UnaryPredicate<A>() {
            @Override
            public boolean evaluate( A arg ) {
                return arg1.evaluate( arg ) && arg2.evaluate( arg ) ;
            }
        } ;
    }
    
    public static <A> UnaryPredicate<A> or(
        final UnaryPredicate<A> arg1,
        final UnaryPredicate<A> arg2 ) {

        return new UnaryPredicate<A>() {
            @Override
            public boolean evaluate( A arg ) {
                return arg1.evaluate( arg ) || arg2.evaluate( arg ) ;
            }
        } ;
    }
    
    public static <T> UnaryPredicate<T> FALSE( Class<T> cls
        ) {
        
        return new UnaryPredicate<T>() {
            @Override
            public boolean evaluate( T arg ) {
                return false ;
            }
        } ;
    } ;
    
    public static <T> UnaryPredicate<T> TRUE( Class<T> cls
        ) {
        
        return new UnaryPredicate<T>() {
            @Override
            public boolean evaluate( T arg ) {
                return true ;
            }
        } ;
    } ;
    
    public static <A> UnaryPredicate<A> not(
        final UnaryPredicate<A> arg1 ) {
        
        return new UnaryPredicate<A>() {
            @Override
            public boolean evaluate( A arg ) {
                return !arg1.evaluate( arg ) ;
            } 
        } ;
    }
        
    public static <A> void filter( final List<A> arg, final List<A> result,
	final UnaryPredicate<A> predicate ) {

	final UnaryFunction<A,A> filter = new UnaryFunction<A,A>() {
            @Override
	    public A evaluate( A arg ) { 
		return predicate.evaluate( arg ) ? arg : null ; } } ;

	map( arg, result, filter ) ;
    }

    public static <A> List<A> filter( List<A> arg, UnaryPredicate<A> predicate ) {
	List<A> result = new ArrayList<A>() ;
	filter( arg, result, predicate ) ;
	return result ;
    }

    public static <A> A find( List<A> arg, UnaryPredicate<A> predicate ) {
	for (A a : arg) {
	    if (predicate.evaluate( a )) {
		return a ;
	    }
	}

	return null ;
    }

    public static <A,R> R fold( List<A> list, R initial, BinaryFunction<R,A,R> func ) {
        R result = initial ;
        for (A elem : list) {
            result = func.evaluate( result, elem ) ;
        }
        return result ;
    }
    
    /** Flatten the results of applying map to list into a list of T.
     * 
     * @param <S> Type of elements of list.
     * @param <T> Type of elements of result.
     * @param list List of elements of type S.
     * @param map function mapping S to {@code List<T>}.
     * @return {@code List<T>} containing results of applying map to each element of list.
     */
    public static <S,T> List<T> flatten( final List<S> list,
        final UnaryFunction<S,List<T>> map ) {        
        
        return fold( list, new ArrayList<T>(), 
            new BinaryFunction<List<T>,S,List<T>>() {
            @Override
                public List<T> evaluate( List<T> arg1, S arg2 ) {
                    arg1.addAll( map.evaluate( arg2 ) ) ;
                    return arg1 ;
                }
        } ) ;     
    }

    /** Return the first element of the list, or invoke handleEmptyList if
     * list is empty.
     * @param <T> The type of the list element.
     * @param list The list 
     * @param handleEmptyList A runnable to call when the list is empty. Typically 
     * throws an exception.
     * @return The first element of the list, if any.
     */
    public static <T> T getFirst( Collection<T> list, Runnable handleEmptyList ) {
        for (T element : list) {
            return element ;
        }

        handleEmptyList.run();
        return null ;
    }

    /** Converts obj from an Array to a List, if obj is an array.
     * Otherwise just returns a List containing obj.
     */
    @SuppressWarnings({"unchecked"})
    public static List convertToList( Object arg ) {
        List result = new ArrayList() ;
        if (arg != null) {
            Class<?> cls = arg.getClass() ;
            if (cls.isArray()) {
                Class cclass = cls.getComponentType() ;
                if (cclass.equals( int.class )) {
                    for (int elem : (int[])arg) {
                        result.add( elem ) ;
                    }
                } else if (cclass.equals( byte.class )) {
                    for (byte elem : (byte[])arg) {
                        result.add( elem ) ;
                    }
                } else if (cclass.equals( boolean.class )) {
                    for (boolean elem : (boolean[])arg) {
                        result.add( elem ) ;
                    }
                } else if (cclass.equals( char.class )) {
                    for (char elem : (char[])arg) {
                        result.add( elem ) ;
                    }
                } else if (cclass.equals( short.class )) {
                    for (short elem : (short[])arg) {
                        result.add( elem ) ;
                    }
                } else if (cclass.equals( long.class )) {
                    for (long elem : (long[])arg) {
                        result.add( elem ) ;
                    }
                } else if (cclass.equals( float.class )) {
                    for (float elem : (float[])arg) {
                        result.add( elem ) ;
                    }
                } else if (cclass.equals( double.class )) {
                    for (double elem : (double[])arg) {
                        result.add( elem ) ;
                    }
                } else {
                    return Arrays.asList( (Object[])arg ) ;
                }
            } else {
                result.add( arg ) ;
                return result ;
            }
        }

        return result ;
    }

    /** Convert argument to String, either by toString, ot Arrays.toString.
     *
     * @param arg Object to convert.
     */
    public static String convertToString( Object arg ) {
        if (arg == null) {
            return "<NULL>";
        }

        Class<?> cls = arg.getClass() ;
        if (cls.isArray()) {
            Class<?> cclass = cls.getComponentType() ;
            if (cclass.equals( int.class )) {
                return Arrays.toString((int[]) arg);
            }
            if (cclass.equals( byte.class )) {
                return Arrays.toString((byte[]) arg);
            }
            if (cclass.equals( boolean.class )) {
                return Arrays.toString((boolean[]) arg);
            }
            if (cclass.equals( char.class )) {
                return Arrays.toString((char[]) arg);
            }
            if (cclass.equals( short.class )) {
                return Arrays.toString((short[]) arg);
            }
            if (cclass.equals( long.class )) {
                return Arrays.toString((long[]) arg);
            }
            if (cclass.equals( float.class )) {
                return Arrays.toString((float[]) arg);
            }
            if (cclass.equals( double.class )) {
                return Arrays.toString((double[]) arg);
            }
            return Arrays.toString( (Object[])arg ) ;
        } else {
            return arg.toString() ;
        }
    }

    private static List<Method> getDeclaredMethods( final Class<?> cls ) {
        SecurityManager sman = System.getSecurityManager() ;
        if (sman == null) {
            return Arrays.asList( cls.getDeclaredMethods() ) ;
        } else {
            return AccessController.doPrivileged(
                new PrivilegedAction<List<Method>>() {
                    @Override
                    public List<Method> run() {
                        return Arrays.asList( cls.getDeclaredMethods() ) ;
                    }
                }
            ) ;

        }
    }

    private static Set<String> annotationMethods ;
    static {
        annotationMethods = new HashSet<String>() ;
        for (Method m : getDeclaredMethods( Annotation.class )) {
            annotationMethods.add( m.getName()) ;
        }
    }

    /** Given an annotation, return a Map that maps each field (given by a
     * method name) to its value in the annotation.  If the value is an
     * annotation, that value is recursively converted into a Map in the
     * same way.
     *
     * @param ann The annotation to examine.
     * @param convertArraysToLists true if annotation values of array type
     * should be converted to an appropriate list.  This is often MUCH more
     * useful, but some contexts require arrays.
     * @return A map of annotation fields to their values.
     */
    public static Map<String, Object> getAnnotationValues(Annotation ann, boolean convertArraysToLists) {
        // We must ignore all of the methods defined in the java.lang.Annotation API.
        Map<String,Object> result = new HashMap<String,Object>() ;
        for (Method m : getDeclaredMethods( ann.getClass() )) {
            String name = m.getName() ;
            if (!annotationMethods.contains(name)) {
                Object value;
                try {
                    value = m.invoke(ann);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Error invoking method " + m + " on annotation " + ann, e);
                }
                if (value != null) {
                    Class<?> valueClass = value.getClass();
                    if (valueClass.isAnnotation()) {
                        value = getAnnotationValues((Annotation) value, convertArraysToLists);
                    } else if (convertArraysToLists && valueClass.isArray()) {
                        value = convertToList(value);
                    }
                }

                result.put( name, value ) ;
            }
        }

        return result ;
    }

    public static interface Action<T> {
        T run() throws Exception ;
    }

    private static <T> PrivilegedAction<T> makePrivilegedAction(
        final Action<T> act ) {

        return new PrivilegedAction<T>() {
            @Override
            public T run() {
                try {
                    return act.run() ;
                } catch (RuntimeException exc) {
                    throw exc ;
                } catch (Exception ex) {
                    throw new RuntimeException( ex ) ;
                }
            }
        } ;
    }

    public static <T> T doPrivileged( Action<T> func ) {
        SecurityManager sman = System.getSecurityManager() ;
        try {
            if (sman == null) {
                return func.run() ;
            } else {
                return AccessController.doPrivileged( makePrivilegedAction(
                    func ) ) ;
            }
        } catch (RuntimeException rex) {
            throw rex ;
        } catch (Exception ex) {
            throw new RuntimeException( ex ) ;
        }
    }
}
