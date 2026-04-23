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

import static java.lang.reflect.Modifier.PUBLIC;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._arg;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._body;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._class;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._classGenerator;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._clear;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._constructor;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._end;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._expr;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._import;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._int;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._method;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._package;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._return;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._s;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._super;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._t;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._v;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._void;

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
