/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm;

import org.glassfish.pfl.basic.contain.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ken Cavanaugh
 */
public class AnnotationAnalyzerTest {

    @Documented
    @Target({ElementType.METHOD,ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnoA{
        int value() default 0 ;
    }

    @Documented
    @Target({ElementType.METHOD,ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnoB{
        int value() default 0 ;
    }

    @Documented
    @Target({ElementType.METHOD,ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnoC{
        int value() default 0 ;
    }

    @Documented
    @Target({ElementType.METHOD,ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnoD{
        int value() default 0 ;
    }

    private static class AD {
        private Pair<Class<? extends Annotation>,Integer> data ;

        public static Set<AD> toSet( Map<Class<?>,Annotation> map ) {
            Set<AD> res = new HashSet<AD>() ;
            for (Annotation anno : map.values())  {
                AD ad = new AD( anno ) ;
                res.add( ad ) ;
            }
            return res ;
        }

        public static Set<AD> toSet( AD... ads ) {
            Set<AD> res = new HashSet<AD>() ;
            for (AD ad : ads) {
                res.add( ad ) ;
            }
            return res ;
        }

        AD( Class<? extends Annotation> anno, int value ) {
            data = new Pair<Class<? extends Annotation>,Integer>( anno, value ) ;
        }

        AD( Annotation anno ) {
            final Class<? extends Annotation> annoClass = anno.annotationType() ;
            int value = -1 ;
            if (annoClass.equals( AnnoA.class )) {
                value = ((AnnoA)anno).value() ;
            } else if (annoClass.equals( AnnoB.class )) {
                value = ((AnnoB)anno).value() ;
            } else if (annoClass.equals( AnnoC.class )) {
                value = ((AnnoC)anno).value() ;
            } else if (annoClass.equals( AnnoD.class )) {
                value = ((AnnoD)anno).value() ;
            }

            data = new Pair<Class<? extends Annotation>,Integer>( annoClass,
                value ) ;
        }

        @Override
        public String toString() {
            Class<? extends Annotation> annoClass = data.first() ;
            int value = data.second() ;
            return annoClass.getSimpleName() + "(" + value + ")" ;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (this.data != null ? this.data.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj == this) {
                return true ;
            }

            if (!(obj instanceof AD)) {
                return false ;
            }

            AD other = (AD)obj ;

            return data.equals( other.data ) ;
        }
    }

    @AnnoA(0)
    interface A {
        @AnnoA(0)
        void m1() ;

        @AnnoA(1)
        @AnnoB(1)
        void m2() ;
    }

    @AnnoA(1)
    @AnnoB(0)
    interface B extends A {
        @Override
        @AnnoA(2)
        void m1() ;

        @AnnoC(0)
        @AnnoA(3)
        void m3() ;
    }

    @AnnoB(2)
    interface C extends A {
        @AnnoA(4)
        @AnnoB(2)
        void m1( int arg ) ;

        @AnnoC(1)
        @AnnoA(5)
        void m4() ;
    }

    @AnnoA(2)
    @AnnoC(0)
    interface D extends B, C {
        @Override
        @AnnoA(6)
        void m2() ;

        @Override
        @AnnoB(3)
        void m4() ;
    }

    @AnnoA(3)
    @AnnoC(1)
    private interface MetaC extends C {
        @Override
        @AnnoA(7)
        @AnnoD(0)
        void m1() ;

        @Override
        @AnnoB(4)
        @AnnoD(1)
        void m1( int arg ) ;

        @Override
        @AnnoD(2)
        void m2() ;
    }

    @AnnoB(3)
    private interface MetaD extends D, MetaC {
        @Override
        @AnnoA(8)
        void m3() ;
    }

    @AnnoD(0)
    private interface DummyD {
        @AnnoD(0)
        void m2() ;
    }

    public AnnotationAnalyzerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addAnnotation method, of class AnnotationAnalyzer.
     */
    /* Not needed for now.
    @Test
    public void testAddAnnotation() {
        System.out.println("addAnnotation");
        AnnotatedElement element = null;
        Annotation annotation = null;
        AnnotationAnalyzer instance = new AnnotationAnalyzer();
        instance.addAnnotation(element, annotation);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
     */

    /**
     * Test of addInheritedAnnotations method, of class AnnotationAnalyzer.
     */
    @Test
    public void testAddInheritedAnnotations() throws NoSuchMethodException {
        AnnotationAnalyzer instance = new AnnotationAnalyzer();
        instance.addInheritedAnnotations(MetaD.class, D.class) ;

        // Test the class annotations
        Class<?> cls = D.class ;
        Set<AD> expResult = AD.toSet(
            new AD( AnnoA.class, 2 ), new AD( AnnoB.class, 3 ),
            new AD( AnnoC.class, 0 ));
        Set<AD> result = AD.toSet( instance.getAnnotations(cls) ) ;
        assertEquals(expResult, result);

        // Test the method annotations
        Method m1 = D.class.getMethod( "m1" ) ;
        Method m1_int = D.class.getMethod( "m1", int.class ) ;
        Method m2 = D.class.getMethod( "m2" ) ;
        Method m3 = D.class.getMethod( "m3" ) ;
        Method m4 = D.class.getMethod( "m4" ) ;

        List<Method> methods = Arrays.asList( m1, m1_int, m2, m3, m4 ) ;
        List<Set<AD>> expResults = Arrays.asList(
            AD.toSet( new AD( AnnoA.class, 7 ), new AD( AnnoD.class, 0 ) ),
            AD.toSet( new AD( AnnoB.class, 4 ), new AD( AnnoA.class, 4 ),
                      new AD( AnnoD.class, 1 ) ),
            AD.toSet( new AD( AnnoB.class, 1 ), new AD( AnnoA.class, 6 ),
                      new AD( AnnoD.class, 2 ) ),
            AD.toSet( new AD( AnnoC.class, 0 ), new AD( AnnoA.class, 8 ) ),
            AD.toSet( new AD( AnnoB.class, 3 ), new AD( AnnoC.class, 1 ),
                      new AD( AnnoA.class, 5 ) )
        ) ;

        int ctr = 0 ;
        verifyAnnotations( instance, methods, expResults, ctr );
    }

    private void verifyAnnotations( AnnotationAnalyzer instance, List<Method> methods, List<Set<AD>> expResults, int ctr )
    {
        for (Method m : methods ) {
            Set<AD> mresult = AD.toSet( instance.getAnnotations(m ) ) ;
            Set<AD> expADS = expResults.get( ctr++ ) ;
            assertEquals( expADS, mresult ) ;
        }
    }

    /**
     * Test of getAnnotations method, of class AnnotationAnalyzer.
     */
    @Test
    public void testGetAnnotations_Class() {
        Class<?> cls = D.class ;
        AnnotationAnalyzer instance = new AnnotationAnalyzer();
        Set<AD> expResult = AD.toSet(
            new AD( AnnoA.class, 2 ), new AD( AnnoB.class, 2 ),
            new AD( AnnoC.class, 0 ));
        Set<AD> result = AD.toSet( instance.getAnnotations(cls) ) ;
        assertEquals(expResult, result);
    }

    private String shortName( Method m ) {
        StringBuilder sb = new StringBuilder() ;
        sb.append( m.getDeclaringClass().getSimpleName() ) ;
        sb.append( '.' ) ;
        sb.append( m.getName() ) ;
        sb.append( '(' ) ;

        boolean first = true ;
        for (Class<?> cls : m.getParameterTypes() ) {
            if (first) {
                first = false ;
            } else {
                sb.append( ',' ) ;
            }
            sb.append( cls.getSimpleName() ) ;
        }

        sb.append( ')' ) ;
        return sb.toString() ;
    }

    /**
     * Test of getAnnotations method, of class AnnotationAnalyzer.
     */
    @Test
    public void testGetAnnotations_Method() throws NoSuchMethodException {
        AnnotationAnalyzer instance = new AnnotationAnalyzer();
        Method m1 = D.class.getMethod( "m1" ) ;
        Method m1_int = D.class.getMethod( "m1", int.class ) ;
        Method m2 = D.class.getMethod( "m2" ) ;
        Method m3 = D.class.getMethod( "m3" ) ;
        Method m4 = D.class.getMethod( "m4" ) ;

        List<Method> methods = Arrays.asList( m1, m1_int, m2, m3, m4 ) ;
        List<Set<AD>> expResult = Arrays.asList(
            AD.toSet( new AD( AnnoA.class, 2 ) ),
            AD.toSet( new AD( AnnoB.class, 2 ), new AD( AnnoA.class, 4 ) ),
            AD.toSet( new AD( AnnoB.class, 1 ), new AD( AnnoA.class, 6 ) ),
            AD.toSet( new AD( AnnoC.class, 0 ), new AD( AnnoA.class, 3 ) ),
            AD.toSet( new AD( AnnoB.class, 3 ), new AD( AnnoC.class, 1 ),
                      new AD( AnnoA.class, 5 ) )
        ) ;

        int ctr = 0 ;
        verifyAnnotations( instance, methods, expResult, ctr );
    }
}
