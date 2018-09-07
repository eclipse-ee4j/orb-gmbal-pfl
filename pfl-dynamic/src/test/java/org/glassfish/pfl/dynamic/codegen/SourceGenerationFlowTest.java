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
 * Test code generation by generating source code and compiling it. This is mainly to validate the test itself,
 * but also provides additional testing of the source code generation process.
 */
public class SourceGenerationFlowTest extends FlowTestSuiteBase {
    public SourceGenerationFlowTest() {
        super(false, false);
    }
}
