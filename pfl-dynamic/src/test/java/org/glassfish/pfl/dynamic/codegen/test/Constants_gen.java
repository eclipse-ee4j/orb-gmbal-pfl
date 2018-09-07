/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.test;

import org.glassfish.pfl.dynamic.codegen.ClassGeneratorFactory;
import org.glassfish.pfl.dynamic.codegen.spi.ClassGenerator;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo;
import org.glassfish.pfl.dynamic.codegen.spi.MethodInfo;
import org.glassfish.pfl.dynamic.codegen.spi.Type;

import java.util.Map;
import java.util.Set;

import static java.lang.reflect.Modifier.PUBLIC;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.*;

/** This class implements the Constants interface.  It parses the
 * name of each method declared in constants to determine what value
 * to return.  The purpose of this test is to check that the correct
 * code is being generated for constants.  This is worth checking because
 * the code generator emits the various ICONST/BIPUSH/SIPUSH/LDC bytecodes
 * as needed.  An incorrect choice of bytecode leads to bad results.
 */
public class Constants_gen implements ClassGeneratorFactory {
    private static final String RETURN = "return" ;
    private static final String RETURN_MINUS = "returnMinus" ;
    public String className() {
	return "ConstantsImpl" ;
    }

    public static int getValue( String methodName ) {
	boolean isNegative = false ;
	String numString = "" ;
	if (methodName.startsWith( RETURN_MINUS )) {
	    numString = methodName.substring( RETURN_MINUS.length() ) ;
	    isNegative = true ;
	} else if (methodName.startsWith( RETURN )) {
	    numString = methodName.substring( RETURN.length() ) ;
	} else {
	    throw new RuntimeException( 
		"Bad method methodName " + methodName + " in Constants" ) ;
	}

	int value = Integer.parseInt( numString ) ;
	if (isNegative)
	    value = -value ;

	return value ; 
    }

    private void makeTestMethods( Type interf ) {
	ClassInfo cinfo = interf.classInfo() ;

	Map<String,Set<MethodInfo>> minfoMap = cinfo.methodInfoByName() ;
	for (Set<MethodInfo> minfos : minfoMap.values()) {
	    for (MethodInfo minfo : minfos) {
		String name = minfo.name() ;
		int value = getValue( name ) ;

		_method( PUBLIC, _int(), name ) ; 
		_body() ;
		    _return( _const( value ) ) ;
		_end() ;
	    }
	}
    }

    public ClassGenerator evaluate() {
	_clear() ;
	_setClassLoader( Thread.currentThread().getContextClassLoader() ) ;
	_package( "dynamic.codegen.gen" ) ;
	Type Constants = _import( "org.glassfish.pfl.dynamic.codegen.lib.Constants" ) ;

	_class( PUBLIC, className(), _Object(), Constants ) ;
	    // Simple default constructor
	    _constructor( PUBLIC ) ;
	    _body() ;
		_expr(_super(_s(_void()))) ;
	    _end() ;

	    // generate all of the test methods
	    makeTestMethods( Constants ) ;
	_end() ; // of Const_gen class

	return _classGenerator() ;
   }
}
