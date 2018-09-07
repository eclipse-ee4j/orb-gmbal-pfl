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

import java.lang.reflect.Method;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.*;

public abstract class CodeGeneratorBase implements SimpleCodeGenerator {
    private String className;
    private long astTime;

    protected CodeGeneratorBase(ClassGeneratorFactory cgf) {
        _clear();

        long start = System.nanoTime();
        try {
            // Create the ClassGeneratorImpl as a side effect of this
            // call.  It is available through the Wrapper API.
            cgf.evaluate();
        } finally {
            astTime = (System.nanoTime() - start) / 1000;
        }

        className = _classGenerator().name();
    }

    @Override
    public String className() {
        return className;
    }

    public long astConstructionTime() {
        return astTime;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + className + "]";
    }

    public void reportTimes() {
        Class cls = this.getClass();
        Method[] methods = cls.getMethods();
        System.out.println("Timings for generator for class " + className);
        System.out.println("=======================================================");
        for (Method method : methods) {
            String name = method.getName();
            String suffix = "Time";
            if (name.endsWith(suffix)
                    && method.getParameterTypes().length == 0
                    && method.getReturnType().equals(long.class)) {
                try {
                    long time = (Long) method.invoke(this);
                    String title = name.substring(0, name.length() - suffix.length());
                    System.out.printf("%32s : %10d microseconds\n", name, time);
                } catch (Exception exc) {
                    System.out.println("Error in calling method " + name + ":" + exc);
                }
            }
        }
        System.out.println("=======================================================");
    }
}
