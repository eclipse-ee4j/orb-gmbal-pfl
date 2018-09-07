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

import org.glassfish.pfl.dynamic.codegen.spi.ClassGenerator;

import org.glassfish.pfl.dynamic.codegen.ClassGeneratorFactory;

import static java.lang.reflect.Modifier.* ;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;

public class DefaultPackageTest_gen implements ClassGeneratorFactory {
    public String className() {
	return "DefaultPackageTest" ;
    }
    
    public ClassGenerator evaluate() {
	_clear() ;
	_package() ;
	_import( "org.glassfish.pfl.dynamic.codegen.lib.EchoInt" ) ;

	_class( PUBLIC, className(), _t("java.lang.Object"),
	    _t("EchoInt") ) ;
	    
	    _constructor( PUBLIC ) ;
	    _body() ;
		_expr(_super(_s(_void()))) ;
	    _end() ;

	    _method( PUBLIC, _int(), "echo" ) ;
		_arg( _int(), "arg" ) ;
	    _body() ;
		_return( _v("arg") ) ;
	    _end() ;
	_end() ;

	return _classGenerator() ;
    }
}
