/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen;

/**
 * The basic code generation test suite, which can be used for either source
 * or direct byte code generation.  Testing source code generation this way
 * is useful both for testing the source code generation (which is fairly simple),
 * and for testing the test cases.  Testing the direct bytecode generation
 * is where we expect to see the most problems, so debugging the tests themselves first is important.
 */
public abstract class FlowTestSuiteBase extends GenerationTestSuiteBase {
    private Class<?> flowClass;
    private ControlBase cb;

    private void init() {
        flowClass = getClass("Flow");
        try {
            Object obj = flowClass.newInstance();
            cb = ControlBase.class.cast(obj);
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public FlowTestSuiteBase(boolean generateBytecode, boolean debug) {
        super(generateBytecode, debug);
        init();
    }

    public FlowTestSuiteBase(String name, boolean generateBytecode, boolean debug) {
        super(name, generateBytecode, debug);
        init();
    }

    private void defineTest(Object... args) {
        cb.defineTest(args);
    }

    // All that we know statically about inner.get() is that it is
    // an instance of ControlBase.  However, the class actually has
    // many void no-args methods that we need to invoke reflectively
    // for the test cases.
    private void invoke(String name) {
        super.invoke(cb, name);
    }

    private void expectException(String methodName,
                                 Class<? extends RuntimeException> exClass) {

        super.expectException(exClass, cb, methodName);
    }

    // Each test case takes the same form:
    // 1. Call defineTest with an expected sequence of operations
    //    (see ControlBase for the encoding details).
    // 2. Invoke a particular test method.  If the test method does
    //    not follow the expected operation sequence, it will throw
    //    an exception, causing the test to fail.

    public void testSimpleIf1() {
        defineTest(1, 2, 4);
        invoke("simpleIf");
    }

    public void testSimpleIf2() {
        defineTest(ControlBase.moa(1, false), 3, 4);
        invoke("simpleIf");
    }

    // The complexIf tests can be described as all possible combinations
    // of T/F on the if conditionals, which correspond to trace calls
    // with the arguments 1 2 6 8 12 and 15.  Of course, if one conditional
    // prevents another conditional from executing, the value of the
    // conditional is a don't care state.  We can summarize this as follows:
    //
    // Tests: 1  2  6  8 12 15
    //	  T  T  T  T  -  -
    //	  T  T  T  F  -  -
    //	  T  T  F  -  -  -
    //	  T  F  T  T  -  -
    //	  T  F  T  F  -  -
    //	  T  F  F  -  -  -
    //	  F  -  -  -  T  -
    //	  F  -  -  -  F  T
    //	  F  -  -  -  F  F
    //

    public void testComplexIf1() {
        defineTest(1, 2, 3, 5, 6, 7, 8, 9, 18);
        invoke("complexIf");
    }

    public void testComplexIf2() {
        defineTest(1, 2, 3, 5, 6, 7, ControlBase.moa(8, false), 10, 18);
        invoke("complexIf");
    }

    public void testComplexIf3() {
        defineTest(1, 2, 3, 5, ControlBase.moa(6, false), 11, 18);
        invoke("complexIf");
    }

    public void testComplexIf4() {
        defineTest(1, ControlBase.moa(2, false), 4, 5, 6, 7, 8, 9, 18);
        invoke("complexIf");
    }

    public void testComplexIf5() {
        defineTest(1, ControlBase.moa(2, false), 4, 5, 6, 7, ControlBase.moa(8, false), 10, 18);
        invoke("complexIf");
    }

    public void testComplexIf6() {
        defineTest(1, ControlBase.moa(2, false), 4, 5, ControlBase.moa(6, false), 11, 18);
        invoke("complexIf");
    }

    public void testComplexIf7() {
        defineTest(ControlBase.moa(1, false), 12, 13, 18);
        invoke("complexIf");
    }

    public void testComplexIf8() {
        defineTest(ControlBase.moa(1, false), ControlBase.moa(12, false), 14, 15, 16, 18);
        invoke("complexIf");
    }

    public void testComplexIf9() {
        defineTest(ControlBase.moa(1, false), ControlBase.moa(12, false), 14, ControlBase.moa(15, false), 17, 18);
        invoke("complexIf");
    }

    public void testSimpleTryCatch1() {
        defineTest(1, 2, 3, 6);
        invoke("simpleTryCatch");
    }

    public void testSimpleTryCatch2() {
        defineTest(ControlBase.moa(1, FirstException.class), null);
        expectException("simpleTryCatch", FirstException.class);
    }

    public void testSimpleTryCatch3() {
        defineTest(1, ControlBase.moa(2, FirstException.class), 4, 5, 6);
        invoke("simpleTryCatch");
    }

    public void testSimpleTryCatch4() {
        defineTest(1, 2, ControlBase.moa(3, FirstException.class), 4, 5, 6);
        invoke("simpleTryCatch");
    }

    public void testSimpleTryCatch5() {
        defineTest(1, 2, ControlBase.moa(3, FirstException.class), 4,
                ControlBase.moa(5, SecondException.class));
        expectException("simpleTryCatch", SecondException.class);
    }

    public void testSimpleTryCatch6() {
        defineTest(1, ControlBase.moa(2, SecondException.class));
        expectException("simpleTryCatch", SecondException.class);
    }
}
