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
 * The main test suite for code generation.  This tests all of the different patterns of code generation.
 */
public class BytecodeGenerationFlowTest extends FlowTestSuiteBase {

    public BytecodeGenerationFlowTest() {
        super(true, false);
    }
}
