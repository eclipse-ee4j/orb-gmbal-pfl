/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set ;
import java.util.HashSet ;
import java.lang.reflect.Method ;
import java.util.Arrays;
import java.util.Collection;
import org.glassfish.pfl.basic.tools.argparser.DefaultValue;
import org.glassfish.pfl.basic.tools.argparser.Help;
import org.glassfish.pfl.basic.tools.argparser.Separator;
import org.glassfish.pfl.basic.tools.argparser.ArgParser;

/** A VERY quick-and-dirty test framework.
 *
 * @author ken
 */
public class TestBase {
    private final List<Method> testMethods ;
    private final List<String> currentResults ;
    // private final List<String> currentNotes ;
    private final Arguments argvals ;
    private final Set<String> includes ;
    private final Set<String> excludes ;
    private final List<Method> preMethods ;
    private final List<Method> postMethods ;

    private String current ;
    private Set<String> pass = new HashSet<String>() ;
    private Set<String> fail = new HashSet<String>() ;
    private Set<String> skip = new HashSet<String>() ;
    private final Object testObject ;

    JUnitReportHelper reportHelper ;

    private interface Arguments {
        @DefaultValue( "false" )
        @Help( "Control debugging mode")
        boolean debug() ;

        @DefaultValue( "false" ) 
        @Help( "Displays the valid test case identifiers" ) 
        boolean cases() ;

        @DefaultValue( "" ) 
        @Help( "A list of test cases to include: includes everything if empty" ) 
        @Separator( "," )
        List<String> include() ;

        @DefaultValue( "" ) 
        @Help( "A list of test cases to excelude: include everything if empty" ) 
        @Separator( "," )
        List<String> exclude()  ;

        @DefaultValue( "true" )
        @Help( "If true, generate a JUnit report for the tests")
        boolean generateJunitReport() ;
    }

    private void execute( Collection<Method> methods )
        throws IllegalAccessException, IllegalArgumentException,
        InvocationTargetException {

        for (Method m : methods) {
            m.invoke( this ) ;
        }
    }

    public TestBase( String[] args ) {
        this( args, null ) ;
    }

    public TestBase(String[] args, Class<?> parserInterface) {
        this( args, parserInterface, null ) ;
    }

    public TestBase(String[] args, Class<?> parserInterface, Object testObject ) {
        reportHelper = new JUnitReportHelper( this.getClass().getName() );
        testMethods = new ArrayList<Method>() ;
        preMethods = new ArrayList<Method>() ;
        postMethods = new ArrayList<Method>() ;

        this.testObject = (testObject == null)
            ? this
            : testObject ;

        final Class<?> cls = (testObject == null)
            ? this.getClass()
            : testObject.getClass() ;

        for (Method m : cls.getMethods()) {
            if (m.getDeclaringClass().equals( TestBase.class )
                && !this.getClass().equals( TestBase.class )) {
                // Skip test methods defined on this class for self test
                // unless we are actually running the self test.
                continue ;
            }

            TestCase anno = m.getAnnotation( TestCase.class ) ;
            if (anno != null) {
                if (m.getParameterTypes().length == 0) {
                    if (m.getReturnType().equals( void.class )) {
                        testMethods.add( m ) ;
                    } else {
                        msg( "Method " + m + " is annotated @Test, "
                            + "but has a non-void return type").nl() ;
                    }
                } else {
                    msg( "Method " + m + " is annotated @Test, "
                        + "but has parameters").nl() ;
                }
            }

            Pre pre = m.getAnnotation( Pre.class ) ;
            if (pre != null) {
                preMethods.add( m ) ;
            }

            Post post = m.getAnnotation( Post.class ) ;
            if (post != null) {
                postMethods.add( m ) ;
            }
        }


        Class<?>[] interfaces = (parserInterface == null)
            ? new Class<?>[]{ Arguments.class } 
            : new Class<?>[]{ Arguments.class, parserInterface } ;

        ArgParser parser = new ArgParser( Arrays.asList(interfaces)) ;
        argvals = (Arguments)parser.parse( args ) ;
        if (argvals.debug()) {
            msg( "Arguments are:\n" + argvals ).nl() ;
        }

        if (argvals.include().isEmpty()) {
            includes = new HashSet<String>() ;
            for (Method m : testMethods) {
                includes.add( getTestId( m ) ) ;
            }
        } else {
            List<String> incs = argvals.include() ;
            includes = new HashSet<String>( incs ) ;
        }

        excludes = new HashSet<String>( argvals.exclude() ) ;

        if (argvals.cases()) {
            msg( "Valid test case identifiers are:" ).nl() ;
            for (Method m : testMethods) {
                msg( "    " + getTestId( m ) ).nl() ;
            }
        }
        
        currentResults = new ArrayList<String>() ;
        // currentNotes = new ArrayList<String>() ;
    }

    public <T> T getArguments( Class<T> cls ) {
        return cls.cast( argvals ) ;
    }

    private TestBase msg( String str ) {
        System.out.print( str ) ;
        return this ;
    }

    private TestBase nl() {
        System.out.println() ;
        return this ;
    }

    private String getTestId( Method m ) {
        TestCase anno = m.getAnnotation( TestCase.class ) ;
        if (!anno.value().equals("")) {
            return anno.value() ;
        }

        String mname = m.getName() ;
        if (mname.startsWith( "test" )) {
            return mname.substring( 4 ) ;
        } else {
            return mname ;
        }
    }

    private void display( String title, List<String> strs ) {
        if (!strs.isEmpty()) {
            msg( title + ":" ).nl() ;
            for (String str : strs ) {
                msg( "\t" + str ).nl() ;
            }
        }
    }

    private String getMessage( List<String> strs ) {
        StringBuilder sb = new StringBuilder() ;
        for (String str : strs ) {
            if (sb.length() != 0) {
                sb.append( '\n' ) ;
            }
            sb.append(str) ;
        }
        return sb.toString() ;
    }

    public int run() {
        for (Method m : testMethods) {
            currentResults.clear() ;
            // currentNotes.clear() ;

            current = getTestId( m ) ;
            if (includes.contains(current) && !excludes.contains(current)) {
                reportHelper.start( current ) ;
                msg( "Test " + current + ": " ).nl() ;
                msg( "    Notes:" ).nl() ;
                long start = System.currentTimeMillis() ;
                long duration = 0 ;
                try {
                    execute( preMethods ) ;
                    m.invoke( testObject ) ;
                } catch (Exception exc) {
                    fail( "Caught exception : " + exc )  ;
                    exc.printStackTrace();
                } finally {
                    try {
                        execute(postMethods);
                    } catch (Exception exc) {
                        fail( "Exception in post methods : " + exc ) ;
                        exc.printStackTrace();
                    }

                    duration = System.currentTimeMillis() - start ;
                }

                if (currentResults.isEmpty()) {
                    reportHelper.pass( duration ) ;
                    pass.add( current ) ;
                    msg( "Test " + current + " PASSED." ).nl() ;
                } else {
                    reportHelper.fail( getMessage( currentResults ),
                        duration ) ;

                    fail.add( current )  ;
                    msg( "Test " + current + " FAILED." ).nl() ;
                }

                reportHelper.done() ;
                // display( "    Notes", currentNotes ) ;
                display( "    Results", currentResults ) ;
            } else {
                msg( "Test " + current + " SKIPPED" ).nl() ;
                skip.add( current ) ;
            }
        }

        msg( "-------------------------------------------------").nl() ;
        msg( "Results:" ).nl() ;
        msg( "-------------------------------------------------").nl() ;

        msg( "\tFAILED:").nl() ; displaySet( fail ) ;
        msg( "\tSKIPPED:").nl() ; displaySet( skip ) ;
        msg( "\tPASSED:").nl() ; displaySet( pass ) ;

        nl() ;
        msg( pass.size() + " test(s) passed; "
            + fail.size() + " test(s) failed; "
            + skip.size() + " test(s) skipped." ).nl() ;
        msg( "-------------------------------------------------").nl() ;

        return fail.size() ;
    }

    private void displaySet( Set<String> set ) {
        for (String str : set ) {
            msg( "\t\t" ).msg( str ).nl() ;
        }
    }

    public void fail( String failMessage ) {
        check( false, failMessage ) ;
    }

    public void check( boolean result, String failMessage ) {
        if (!result) {
            currentResults.add( failMessage ) ;
        }
    }

    public void note( String msg ) {
        // currentNotes.add( msg ) ;
        msg( "\t" + msg ).nl() ;
    }

    @TestCase
    public void testSimple() {}

    @TestCase
    public void testGood( ) {
        note( "this is a good test" ) ;
        note( "A second note") ;
    }

    @TestCase( "Bad" )
    public void badTest() {
        note( "this is a bad test" ) ;
        fail( "this test failed once" ) ;
        fail( "this test failed twice" ) ;
    }

    @TestCase
    public void exception() {
        throw new RuntimeException( "This test throws an exception") ;
    }

    @TestCase
    public boolean badReturnType() {
        return true ;
    }

    @TestCase
    public void hasParameters( String name ) {
    }

    public static void main( String[] args ) {
        TestBase base = new TestBase( args ) ;
        base.run() ;
    }
}
