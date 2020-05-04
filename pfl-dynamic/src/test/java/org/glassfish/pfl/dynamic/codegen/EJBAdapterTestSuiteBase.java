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

import jakarta.ejb.EJBException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

/**
 * Base class for testing EJBAdapter code generation.
 * Setup:
 *	    myRemoteClass is the Class representing a remote interface
 *		that corresponds to the business interface MyBusinessIntf
 *	    myRemote__AdapterClass is the Class representing an adapter
 *		that implements MyBusinessIntf and delegates to myRemoteClass
 *
 * The tests:
 *	    Basically, inject test vector, invoke method, and check results.
 *	    However, the test vector is injected into an implementation of
 *	    myRemoteClass, which must be implemented using a Proxy.
 *	    We create an instance of the adapter which delegates to the Proxy.
 *	    So, the tests are written as follows:
 *	    1. Inject a test vector into the Proxy, which uses a ControlBase for
 *	       that purpose.
 *	    2. Create an instance of the adapter that delegates to the Proxy.
 *	    3. Invoke a method on the adapter, and verify that the correct result
 *	       is observed.
 *
 *	Details:
 *	    void doSomething():
 *		1. test (1): completes normally
 *		2. test (1, RemoteException): throws EJBException with the given exception
 *		   as cause.
 *	        3. test (1, RuntimeException): throws the RuntimeException
 *	    int doSomethingElse():
 *		1. test (1,1): expect result 1.
 *	    int echo( int ):
 *		2. test (1)
 *		   invoke with 1, proxy returns 1, verify result is 1.
 */
public abstract class EJBAdapterTestSuiteBase extends GenerationTestSuiteBase {
    private Class<?> myRemoteClass = getClass("MyRemote");
    private Constructor<?> adapterConstructor;
    private Object invokee; // object of type given by adapter class

    private static class MyRemoteProxyHandler implements InvocationHandler {
        private ControlBase cb;

        public MyRemoteProxyHandler(Object... args) {
            cb = new ControlBase();
            cb.defineTest(args);
        }

        public Object invoke(Object proxy, Method method,
                             Object[] args) throws Throwable {

            // Action depends on method sig:
            // void(): just call traceInt(1)
            // int(): return traceInt(1)
            // int(int): return traceInt(arg)
            Class<?> returnType = method.getReturnType();
            Class<?>[] argTypes = method.getParameterTypes();
            if (argTypes.length > 1)
                throw new IllegalStateException(
                        "Only methods with 0 or 1 parameters are supported in this test");

            int value = 1;
            if (argTypes.length == 1) {
                if (argTypes[0] != int.class)
                    throw new IllegalStateException(
                            "Argument type (if any) must be int");

                value = Integer.class.cast(args[0]);
            }

            int result = cb.traceInt(value);

            if (returnType == int.class)
                return result;
            else
                return null;
        }
    }

    private void defineTest(Object... args) {
        InvocationHandler handler = new MyRemoteProxyHandler(args);
        Class<?>[] interfaces = {myRemoteClass};
        Object proxy = Proxy.newProxyInstance(myRemoteClass.getClassLoader(),
                interfaces, handler);

        try {
            invokee = adapterConstructor.newInstance(proxy);
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    private Object invoke(String name, Object... args) {
        return super.invoke(invokee, name, args);
    }

    private Throwable expectException(String methodName,
                                      Class<? extends Throwable> exClass, Object... args) {
        return super.expectException(exClass, invokee, methodName, args);
    }

    public void testDoSomething1() {
        defineTest(ControlBase.moa(1, 1), null);
        invoke("doSomething");
    }

    public void testDoSomething2() {
        defineTest(ControlBase.moa(1, IllegalStateException.class), null);
        expectException("doSomething", RuntimeException.class);
    }

    public void testDoSomething3() {
        defineTest(ControlBase.moa(1, RemoteException.class), null);
        EJBException ejbex = EJBException.class.cast(
                expectException("doSomething", EJBException.class));
        Exception exc = ejbex.getCausedByException();
        assertEquals(RemoteException.class, exc.getClass());
    }

    public void testDoSomethingElse1() {
        defineTest(ControlBase.moa(1, 1), null);
        assertEquals(invoke("doSomethingElse"), 1);
    }

    public void testEcho() {
        defineTest(ControlBase.moa(42, 357), null);
        int result = Integer.class.cast(invoke("echo", 42));
        assertEquals(result, 357);
    }

    public EJBAdapterTestSuiteBase(String className, String name,
                                   boolean generateByteCode, boolean debug) {
        super(name, generateByteCode, debug);
        init(className);
    }

    private void init(String className) {
        try {
            final Class<?> myRemote__AdapterClass = getClass(className);
            adapterConstructor = myRemote__AdapterClass.getConstructor(
                    myRemoteClass);
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public EJBAdapterTestSuiteBase(String className,
                                   boolean generateByteCode, boolean debug) {
        super(generateByteCode, debug);
        init(className);
    }
}
