/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi;

import org.glassfish.pfl.tf.spi.annotation.InfoMethod;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintStream;


/**
 * This tests that enums can be correctly deserialized when sent from the JDK ORB (no EnumDesc support)
 * to GlassFish, which supports EnumDesc.  We may also add a config flag to allow testing between two
 * GlassFish ORB instances.
 *
 * Basic test: have server run on JDK ORB (or GF with noEnumDesc configuration), and
 * then see if the client can correctly receive an echoed enum from the server.
 */
@Ignore("see README")
public class TfTest
{
    private static final boolean DEBUG = false ;

    private PrintStream out ;
    private PrintStream err ;

    public TfTest() throws Exception {
	this.out = System.out;
	this.err = System.err;
    }

    TestClass getTestClass(boolean isTraced ) {
        // if (isTraced) {
            // return new TestClassImpl_tf() ;
        // } else {
            return new TestClassImpl() ;
        // }
    }

    private void doSimpleTest( boolean isTraced ) {
        TestClass tc = getTestClass( isTraced ) ;
        Assert.assertEquals( tc.add( 10, 10 ), 20 ) ;
        Assert.assertEquals( tc.mult( 10, 10 ), 100 ) ;
    }

    @Test
    public void testSimple() {
        doSimpleTest( false ) ;
    }

    private MethodMonitorFactory tracingMonitorFactory =
        new MethodMonitorFactoryBase( "TestTracing" ) {
        @Override
        public MethodMonitor create(Class<?> cls) {
            return new MethodMonitorTracingImpl( cls ) ;
        }
    } ;

    @A @B
    public static class TestCombination {
        @A
        void single1( int arg1 ) {
            arg1++ ;
        }

        /*
        private static final MethodMonitor mm = new MethodMonitorTracingImpl(
            TestCombination.class ) ;

        void singl1_instr( int arg1 ) {
            final MethodMonitor __mm = mm  ;
            if (__mm != null) {
                __mm.enter( 1, arg1 )  ;
            }

            try {
            } finally {
                if (__mm != null) {
                    __mm.exit( 1 ) ;
                } 
            }
        }
        */

        @A
        int single2( int arg1 ) { return arg1 ; }

        @InfoMethod
        private void someInfo( int arg1 ) { }

        @A
        int single3( int arg1 ) { someInfo( arg1 ) ; return arg1 ; }

        @A
        int single4( int arg1 ) {
            throw new RuntimeException() ;
        }

        @A
        int call2( int arg1 ) { return call3( arg1 ) ; }

        @A
        int call3( int arg1 ) {
            if (arg1 == 0) {
                throw new RuntimeException() ;
            }

            return arg1 ;
        }
         
        @InfoMethod
        private void inSync() {
        }

        @A
        int call4( int arg1 ) {
            int result ;

            synchronized (this) {
                inSync() ;
                result = 2*arg1 ;
            }

            return result ;
        }

        @A
        void methodA() { methodB() ; }

        @B
        void methodB() { methodC() ; }

        @A
        void methodC() { }
    }

    private static final int SINGLE1 ;
    private static final int SINGLE2 ;
    private static final int SINGLE3 ;
    private static final int SOMEINFO ;
    private static final int SINGLE4 ;
    private static final int CALL2 ;
    private static final int CALL3 ;
    private static final int CALL4 ;
    private static final int METHODA ;
    private static final int METHODB ;
    private static final int METHODC ;
    private static final int INSYNC ;

    private static final MethodMonitor expected ;
    
    static {
        MethodMonitorBase.MethodMonitorFactorySelfImpl mmf = 
            new MethodMonitorBase.MethodMonitorFactorySelfImpl(
                "Tracing") ;
        MethodMonitor mm = new MethodMonitorTracingImpl(
            TestCombination.class ) ;
        mmf.init( mm ) ;
        expected = mm ;
    }

    private static final TestCombination tc ;

    static {
        Class<?> cls = TestCombination.class ;
        tc = new TestCombination() ;
        SINGLE1 = MethodMonitorRegistry.getMethodIdentifier( cls, "single1" ) ;
        SINGLE2 = MethodMonitorRegistry.getMethodIdentifier( cls, "single2" ) ;
        SINGLE3 = MethodMonitorRegistry.getMethodIdentifier( cls, "single3" ) ;
        SOMEINFO = MethodMonitorRegistry.getMethodIdentifier( cls, "someInfo" ) ;
        SINGLE4 = MethodMonitorRegistry.getMethodIdentifier( cls, "single4" ) ;
        CALL2 = MethodMonitorRegistry.getMethodIdentifier( cls, "call2" ) ;
        CALL3 = MethodMonitorRegistry.getMethodIdentifier( cls, "call3" ) ;
        CALL4 = MethodMonitorRegistry.getMethodIdentifier( cls, "call4" ) ;
        METHODA = MethodMonitorRegistry.getMethodIdentifier( cls, "methodA" ) ;
        METHODB = MethodMonitorRegistry.getMethodIdentifier( cls, "methodB" ) ;
        METHODC = MethodMonitorRegistry.getMethodIdentifier( cls, "methodC" ) ;
        INSYNC = MethodMonitorRegistry.getMethodIdentifier( cls, "inSync" ) ;
    }

    @Test
    public void singleMethodNoReturn() {
        final int arg = 42 ;

        expected.clear() ;
        expected.enter( SINGLE1, arg ) ;
        expected.exit( SINGLE1 ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        tc.single1( arg ) ;

        MethodMonitor actual = MethodMonitorRegistry.getMethodMonitorForClass(
            TestCombination.class, A.class ) ;

        Assert.assertEquals( actual, expected );
    }

    @Test
    public void singleMethodReturn() {
        final int arg = 42 ;

        expected.clear() ;
        expected.enter( SINGLE2, arg ) ;
        expected.exit( SINGLE2, arg ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        tc.single2( arg ) ;

        MethodMonitor actual = MethodMonitorRegistry.getMethodMonitorForClass(
            TestCombination.class, A.class ) ;

        Assert.assertEquals( actual, expected );
    }

    @Test
    public void singleMethodInfoCall() {
        final int arg = 42 ;

        expected.clear() ;
        expected.enter( SINGLE3, arg ) ;
        Object[] args = { arg } ;
        expected.info( args, SINGLE3, SOMEINFO ) ;
        expected.exit( SINGLE3, arg ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        tc.single3( arg ) ;

        MethodMonitor actual = MethodMonitorRegistry.getMethodMonitorForClass(
            TestCombination.class, A.class ) ;

        Assert.assertEquals( actual, expected );
    }

    @Test
    public void singleMethodThrowsException() {
        final int arg = 42 ;

        expected.clear() ;
        expected.enter( SINGLE4, arg ) ;
        expected.exception( SINGLE4, new RuntimeException() ) ;
        expected.exit( SINGLE4, 0 ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        try {
            tc.single4( arg ) ;
            Assert.fail( "Unexpected normal completion") ;
        } catch (RuntimeException exc) {
            MethodMonitor actual =
                MethodMonitorRegistry.getMethodMonitorForClass(
                    TestCombination.class, A.class ) ;

            Assert.assertEquals( actual, expected );
        } catch (Exception exc) {
            Assert.fail( "Unexpected exception " + exc ) ;
        }
    }

    @Test
    public void twoCalls() {
        final int arg = 42 ;

        expected.clear() ;
        expected.enter( CALL2, arg ) ;
        expected.enter( CALL3, arg ) ;
        expected.exit( CALL3, arg ) ;
        expected.exit( CALL2, arg ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        tc.call2( arg ) ;

        MethodMonitor actual = MethodMonitorRegistry.getMethodMonitorForClass(
            TestCombination.class, A.class ) ;

        Assert.assertEquals( actual, expected );
    }

    @Test
    public void twoCallsException() {
        final int arg = 0 ;

        expected.clear() ;
        expected.enter( CALL2, arg ) ;
        expected.enter( CALL3, arg ) ;
        expected.exception( CALL3, new RuntimeException() ) ;
        expected.exit( CALL3, arg ) ;
        expected.exit( CALL2, arg ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        try {
            tc.call2( arg ) ;
            Assert.fail( "Unexpected normal completion") ;
        } catch (RuntimeException exc) {
            MethodMonitor actual =
                MethodMonitorRegistry.getMethodMonitorForClass(
                    TestCombination.class, A.class ) ;

            Assert.assertEquals( actual, expected );
        } catch (Exception exc) {
            Assert.fail( "Unexpected exception " + exc ) ;
        }
    }

    @Test
    public void testSync() {
        int arg = 23 ;
        expected.clear() ;
        expected.enter( CALL4, arg ) ;
        expected.info( new Object[0], CALL4, INSYNC);
        // expected.info( null, CALL4, INSYNC);
        expected.exit( CALL4, 2*arg ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        tc.call4( arg ) ;

        MethodMonitor actual = MethodMonitorRegistry.getMethodMonitorForClass(
            TestCombination.class, A.class ) ;

        Assert.assertEquals( actual, expected );
    }

    @Test
    public void twoAnnotations() {
        expected.clear() ;
        expected.enter( METHODA ) ;
        expected.enter( METHODC ) ;
        expected.exit( METHODC ) ;
        expected.exit( METHODA ) ;

        MethodMonitorRegistry.register( A.class, tracingMonitorFactory ) ;

        tc.methodA() ;

        MethodMonitor actual = MethodMonitorRegistry.getMethodMonitorForClass(
            TestCombination.class, A.class ) ;

        Assert.assertEquals( actual, expected );

    }

    // Tests:
    // Two MM annotations, MM1 enabled, MM2 disabled
    // 7. Method (MM1) A calls (MM2) B calls (MM1) C

}
