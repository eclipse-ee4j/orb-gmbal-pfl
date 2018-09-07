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

import junit.extensions.RepeatedTest ;
import junit.framework.TestResult ;
import junit.framework.Test ;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TimedTest extends RepeatedTest 
{
    // Duration in nanoseconds
    private long duration ;

    public TimedTest( Test test, int reps )
    {
	super( test, reps ) ;
    }

    // Adding this STUPID test case because it's the only
    // way I found for STUPID NetBeans to run the class (which it
    // shouldn't) without causing a "no test cases found" error.
    public static class InnerTest extends TestCase {
        public InnerTest() {
            super( "testNOP" ) ;
        }

        public void testNOP() {
            // do nothing
        }
    }

    public static TestSuite suite() {
        Test test = new InnerTest() ;
        TestSuite ts = new TestSuite() ;
        ts.addTest( test ) ;
        return ts ;
    }

    public void run( TestResult result )
    {
	long startTime = System.nanoTime() ;
	long stopTime = 0 ;
	try {
	    super.run( result ) ;
	} finally {
	    stopTime = System.nanoTime() ;
	}
	duration = stopTime - startTime ;
    }

    public long getDuration() 
    {
	return duration ;
    }
}


