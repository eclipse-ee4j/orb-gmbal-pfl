/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.test ;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.OutputStream ;
import org.glassfish.pfl.basic.contain.Pair;

/** Helper class for generating reports for tests that do not adapt well to 
 * Testng/JUnit.  For example, several tests re-run the same test method and
 * class many times with different parameters.  JUnit does not support this at
 * all.  Testng does, but it is too much work to adapt these tests.  Instead,
 * we can just bracket test case execution with start/(pass|fail) calls.
 */
public class JUnitReportHelper {
    private final boolean DEBUG = 
        Boolean.valueOf( System.getProperty( "corba.test.junit.helper.debug" ) ) ;

    private void msg( String str ) {
        if (DEBUG) {
            System.out.println("JUnitReportHelper: " + str);
        }
    }

    public static class Counts extends Pair<Integer,Integer> {
        Counts( int numPass, int numFail ) {
            super( numPass, numFail ) ;
        }

        public int numPass() {
            return first() ;
        }

        public int numFail() {
            return second() ;
        }
    }

    private JUnitReportWriter writer ;
    private String className ;
    private String fileName ;
    private JUnitReportWriter.TestDescription current ;
    private boolean testComplete ;

    /** Prepare to generate a JUnitReport in the file named
     * ${junit.report.dir}/${name}.xml.  junit.report.dir is obtained from
     * the environment variable which is passed to all CTF controllers.
     * @param cname The class name of the class for this test
     */
    public JUnitReportHelper( String cname ) {
        current = null ;
        testComplete = false ;
        String processName = System.getProperty( "corba.test.process.name" ) ;
        className = cname ;
        if (processName != null) {
            this.fileName = cname + "." + processName;
        } else {
            this.fileName = cname;
        }

        msg( "<init>: className = " 
            + this.className + " fileName = " + this.fileName ) ;

        String outdirName = System.getProperty( "junit.report.dir" ) ; 
        if (outdirName == null) {
            throw new RuntimeException("property junit.report.dir is not set");
        }

        File outdir = new File( outdirName ) ;
        if (!outdir.exists()) {
            throw new RuntimeException(outdir + " does not exist");
        }

        if (!outdir.isDirectory()) {
            throw new RuntimeException(outdir + " is not a directory");
        }

        OutputStream os = null ;

        try {
            File file = new File( outdir, this.fileName + ".xml" ) ;
            os = new FileOutputStream( file ) ;
        } catch (Exception exc) {
            throw new RuntimeException( exc ) ;
        }

        writer = new XMLJUnitReportWriter() ;
        writer.setOutput( os ) ;
        writer.startTestSuite( fileName, System.getProperties() ) ;
    }

    // current must be non-null, and the test must not have been completed.
    // Reporting the completion of a test multiple times results in multiple
    // entries in the report, and double-counting of test case results.
    private void checkCurrent() {
        if (current == null) {
            throw new RuntimeException("No current test set!");
        }

        if (testComplete) {
            System.out.println("Test " + current + " has already been completed");
        }
        /*
        if (testComplete) 
            throw new RuntimeException( "Test " + current
                + " has already been completed!" ) ;
         */

        testComplete = true ;
    }

    /** Start executing a test case with the given name.
     * All names MUST be unique for an instance of JUnitReportHelper.
     * @param name The name of the test case
     */
    public void start( String name ) {
        msg( "Starting test " + name ) ;
        if ((current != null) && !testComplete) {
            throw new RuntimeException("Trying to start test named " + name 
                + " before current test " + current + " has completed!");
        }

        testComplete = false ;

        current = new JUnitReportWriter.TestDescription( name, className ) ;
        writer.startTest( current ) ;
    }

    /** Report that the current test passed.
     */
    public void pass() {
        msg( "Test " + current + " passed" ) ;
        checkCurrent() ;

        writer.endTest( current ) ;
    }

    /** Report that the current test failed with an error message.
     */
    public void fail( String msg ) {
        fail( new AssertionError( msg ) ) ;
    }

    /** Report that the current test failed with the given exception
     * as cause.
     */
    public void fail( Throwable thr ) {
        msg( "Test " + current + " failed with exception " + thr ) ;
        checkCurrent() ;

        if (thr instanceof AssertionError) {
            writer.addFailure(current, thr);
        } else {
            writer.addError(current, thr);
        }

        writer.endTest( current ) ;
    }

    /** Report that the current test passed.
     */
    public void pass( long duration ) {
        msg( "Test " + current + " passed" ) ;
        checkCurrent() ;

        writer.endTest( current, duration ) ;
    }

    /** Report that the current test failed with an error message.
     */
    public void fail( String msg, long duration ) {
        fail( new AssertionError( msg ), duration ) ;
    }

    /** Report that the current test failed with the given exception
     * as cause.
     */
    public void fail( Throwable thr, long duration ) {
        msg( "Test " + current + " failed with exception " + thr ) ;
        checkCurrent() ;

        if (thr instanceof AssertionError) {
            writer.addFailure(current, thr);
        }
        else {
            writer.addError(current, thr);
        }

        writer.endTest( current, duration ) ;
    }

    private Counts counts = null ;

    /** Testing is complete.  Calls to start, pass, or fail after
     * this call will result in an IllegalStateException.
     * This method may be called multiple times, but only the first
     * call will write a report.
     */
    public Counts done() {
        msg( "Done called" ) ;
        boolean error = ((current != null) && !testComplete) ;

        if (error) {
            fail( "Test suite terminating without terminating test " + current ) ;
        }

        if (counts == null) {
            JUnitReportWriter.TestCounts tc = writer.endTestSuite() ;
            counts = new Counts( tc.pass(), tc.fail() + tc.error() ) ;
        }

        if (error) {
            throw new RuntimeException(
                "Trying to terminate test suite before current test "
                + current + " has completed!");
        }

        return counts ;
    }
}
