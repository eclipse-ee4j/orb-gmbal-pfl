/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;
import org.glassfish.pfl.dynamic.codegen.spi.ImportList;
import org.glassfish.pfl.dynamic.codegen.spi.Type;

/**
 * Class used to define classes and interfaces, and to generator source or
 * byte code from the resulting definitions.  This is the factory for the
 * codegen framework.
 */
public final class CodeGenerator {
  private CodeGenerator() {
  }

  /**
   * Define a ClassGeneratorImpl for a class.
   */
  public static ClassGeneratorImpl defineClass(int modifiers, String name,
                                               Type superType, List<Type> impls) {
    return new ClassGeneratorImpl(modifiers, name, superType, impls);
  }

  /**
   * Define a ClassGeneratorImpl for an interface.
   */
  public static ClassGeneratorImpl defineInterface(int modifiers, String name, List<Type> impls) {
    return new ClassGeneratorImpl(modifiers, name, impls);
  }

  /**
   * Convert the Java class or interface defined by ClassGeneratorImpl into an array
   * of bytecodes.
   */
  public static byte[] generateBytecode(ClassGeneratorImpl cg, ClassLoader cl, ImportList imports, Properties options, PrintStream debugOutput) {

    return ASMUtil.generate(cl, cg, imports, options, debugOutput);
  }

  /**
   * Write a source code representation of the class or interface defined by
   * cg to the PrintStream ps.
   */
  public static void generateSourceCode(PrintStream ps,
                                        ClassGeneratorImpl cg, ImportList imports,
                                        Properties options) throws IOException {

    ASMUtil.generateSourceCode(ps, cg, imports, options);
  }

  /**
   * Write a source code representation of the class or interface defined by
   * cg to a file in the SOURCE_GENERATION_DIRECTORY specified in options.
   */
  public static void generateSourceCode(String sdir,
                                        ClassGeneratorImpl cg, ImportList imports,
                                        Properties options) throws IOException {

    ASMUtil.generateSourceCode(sdir, cg, imports, options);
  }
} 
