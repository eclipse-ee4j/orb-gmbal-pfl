/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen;

import org.glassfish.pfl.basic.func.NullaryFunction;
import org.glassfish.pfl.dynamic.codegen.spi.ClassGenerator;

/** Convenience interface that defines a factory for ClassGenerator instances.
 * It puts the class name of the generated class in a single place.
 * It must always be the case that evaluate().name().equals( className() ).
 */
public interface ClassGeneratorFactory extends NullaryFunction<ClassGenerator> {
    String className() ;
}
