/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glassfish.pfl.test;

import java.io.OutputStream;

import java.util.Properties ;

import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.basic.contain.Triple;

/**
 * This Interface describes classes that format the results of a JUnit
 * testrun.
 *
 */
public interface JUnitReportWriter {
    public class TestDescription extends Pair<String,String> {
	public TestDescription( String name, String className ) {
            super( name, className ) ;
	}

	public String getName() {
	    return first() ;
	}

	public String getClassName() {
	    return second() ;
	}

        @Override
        public String toString() {
            return getClassName() + "." + getName() ;
        }
    }
    
    public class TestCounts extends Triple<Integer,Integer,Integer> {
        public TestCounts( int pass, int fail, int error ) {
            super( pass, fail, error ) ;
        }

        public int pass() {
            return first() ;
        }

        public int fail() {
            return second() ;
        }

        public int error() {
            return third() ;
        }
    }

    /**
     * The whole testsuite started.
     * @param suite the suite.
     */
    void startTestSuite(String name, Properties props ) ;

    /**
     * Sets the stream the formatter is supposed to write its results to.
     * @param out the output stream to use.
     */
    void setOutput(OutputStream out);

    /**
     * This is what the test has written to System.out
     * @param out the string to write.
     */
    void setSystemOutput(String out);

    /**
     * This is what the test has written to System.err
     * @param err the string to write.
     */
    void setSystemError(String err);

    /**
     * A test started.
     */
    void startTest(TestDescription test);

    /**
     * An error occurred.
     */
    void addError(TestDescription test, Throwable t);

    /**
     * A failure occurred.
     */
    void addFailure(TestDescription test, Throwable t);  

    /**
     * A test ended.
     */
    void endTest(TestDescription test); 

    /** 
     * A test ended.  Here we supply the duration, in case the duration is not
     * determined by the [ startTest, endTest ] interval.
     */
    void endTest(TestDescription test, long duration ); 

    /**
     * The whole testsuite ended.
     * @param suite the suite.
     */
    TestCounts endTestSuite() ;
}
