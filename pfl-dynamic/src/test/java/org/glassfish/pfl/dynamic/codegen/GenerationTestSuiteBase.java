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

import junit.framework.TestCase;
import org.glassfish.pfl.dynamic.codegen.spi.ClassGenerator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.*;

/** Base class used to define code generator test suites.
 * A subclass of this class is created that contains all of the
 * test case code.  This first subclass must define two constructors
 * with the same arguments as in the constructors in GenerationTestSuiteBase.
 * This subclass also calls the get method to get generated Class objects.
 * Finally, subclasses of the first subclass are created, which have
 * names that end in "TestSuite", and have two constructors of the form:
 * xxxTestSuite() and xxxTestSuite( String ).  These second subclasses
 * must decide whether debugging support is needed, and whether to
 * use Java source code generation or direct bytecode generation to
 * create the class.
 */
public abstract class GenerationTestSuiteBase extends TestCase {
    private boolean generateByteCode ;
    private boolean debug ;
    private Inner inner = new Inner() ;
    private ClassLoader loader = new TestClassLoader( this.getClass().getClassLoader() ) ;

    public GenerationTestSuiteBase( boolean generateByteCode, boolean debug ) {
	super() ;
	this.generateByteCode = generateByteCode ;
	this.debug = debug ;
    }

    public GenerationTestSuiteBase( String name, boolean generateByteCode, 
	boolean debug ) {
	super( name ) ;
	this.generateByteCode = generateByteCode ;
	this.debug = debug ;
    }

    /** Utility method used to aid in formatting the display of an AST.
     */
    public static void displayAST( String msg, ClassGenerator cg ) {
	System.out.println( ) ;
	System.out.println( "=======================================================" ) ;
	System.out.println( msg ) ;
        _displayAST( cg, System.out ) ;
	System.out.println() ;
	System.out.println( "=======================================================" ) ;
    }

    /** Utility function used to enabled and disable debugging for 
     * bytecode generation.
     */
    public static Properties getByteCodeGenerationProperties( boolean debug ) {
	Properties options = new Properties() ;
	if (debug) {
	    options.setProperty( DUMP_CONSTANT_POOL, "true" ) ;
	    options.setProperty( DUMP_AFTER_SETUP_VISITOR, "true" ) ;
	    options.setProperty( TRACE_BYTE_CODE_GENERATION, "true" ) ;
	    options.setProperty( USE_ASM_VERIFIER, "true" ) ;

	    String baseName = "gen" + File.separatorChar + JavaCodeGenerator.getDirName()
		+ File.separatorChar + "bytecode" + File.separatorChar ;
	    options.setProperty( CLASS_GENERATION_DIRECTORY, 
		baseName + "classes" ) ;
	    options.setProperty( SOURCE_GENERATION_DIRECTORY,
		baseName + "debug_sources" ) ;
	}
	return options ;
    }

    /** Test ClassLoader used to provide scoping for classes, and to allow
     * generation of the same class multiple times in different ClassLoader
     * instances.
     */
    public static class TestClassLoader extends ClassLoader {
	public TestClassLoader( ClassLoader parent ) {
	    super( parent ) ;
	}

	protected Class defineClass( String name, byte[] cdata ) {
	    return super.defineClass( name, cdata, 0, cdata.length ) ;
	}
    }

    private Class<?> generateFromSource( ClassGeneratorFactory cgf ) {
	SimpleCodeGenerator gen = new JavaCodeGenerator( cgf ) ;

	return doGeneration( gen ) ;
    }

    private Class<?> generateFromByteCode( ClassGeneratorFactory cgf ) {
	Properties options = getByteCodeGenerationProperties( debug ) ;
	SimpleCodeGenerator gen = new ByteCodeGenerator( cgf, options ) ;

	return doGeneration( gen ) ;
    }

    private Class<?> doGeneration( SimpleCodeGenerator gen ) { 
	if (debug) {
	    ClassGenerator cg = _classGenerator() ;
	    displayAST( "Generated AST for class " + cg.name(), cg ) ;
	}

	try {
	    return gen.generate( loader ) ;
	} finally {
            if (debug)
                gen.reportTimes() ;
	}
    }

    // Inner encapsulates cbase, preventing direct access to this private
    // data member.  Note that the actual construction of cbase happens
    // in the derived class that overrides createInstance().
    private class Inner {
	private final Class<?> FAILURE = void.class ;
	private Map<String,Class<?>> instances = 
	    new HashMap<String,Class<?>>() ;

	public Class<?> get( String name ) {
	    Class<?> result = instances.get( name ) ;
	    if (result == FAILURE)
		throw new RuntimeException( "Could not create object for name " 
		    + name ) ;
	    else if (result == null) {
		try {
		    ClassGeneratorFactory cgf = ClassGeneratorFactoryRegistry.get( name ) ;
			    
		    if (generateByteCode)
			result = generateFromByteCode( cgf ) ;
		    else
			result = generateFromSource( cgf ) ;
		    instances.put( name, result ) ;
		} catch (RuntimeException exc) {
		    instances.put( name, FAILURE ) ;
		    throw exc ;
		}
	    }

	    return result ;
	}
    }

    /** Obtain an instance of the named class, if there is a
     * ClassGeneratorFactory available by that name.  Note that
     * each call to getClass in the same instance of this class
     * will return the same class object.  Note that multiple
     * classes from calls to getClass in the same instance of this
     * class will share the same ClassLoader.  Each instance of
     * this class uses a separate ClassLoader.
     */
    protected Class<?> getClass( String name ) {
	return inner.get( name ) ;
    }

    protected Object invoke( Object target, String methodName, Object... args ) {
	try {
	    // For this test, assume that all methods have
	    // unique names, and are obtained directly from
	    // the target's class.  Note that we are ignoring
	    // performance optimization here, since these methods
	    // are only invoked a few times in the tests.
	    Method meth = null ;
	    for (Method m : target.getClass().getDeclaredMethods()) {
		if (m.getName() == methodName) {
		    meth = m ;
		    break ;
		}
	    }

	    return meth.invoke( target, args ) ;
	} catch (Exception exc) {
	    throw new RuntimeException( exc ) ;
	}
    }

    protected Throwable expectException( Class<? extends Throwable> expectedException,
	Object target, String methodName, Object... args ) {

	try { 
	    invoke( target, methodName, args ) ;
	    fail( "Invoke completed normally, but exception " + expectedException ) ;
	    return null ; // not actually reachable
	} catch (RuntimeException exc) { // invoke only throws RuntimeException
	    Throwable cause = exc.getCause() ;
	    if (cause instanceof InvocationTargetException)
		cause = cause.getCause() ;

	    if (expectedException.isInstance( cause )) {
		return cause ;
	    } else {
		if (debug) 
		    cause.printStackTrace() ;

		fail( "Expected exception of class " + expectedException.getName() + 
		    " but got " + cause ) ;
		return null ; // not actually reachable
	    }
	}
    }
}
