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
import org.glassfish.pfl.dynamic.codegen.spi.Expression;
import org.glassfish.pfl.dynamic.codegen.spi.Signature;
import org.glassfish.pfl.dynamic.codegen.spi.Type;

import static java.lang.reflect.Modifier.PUBLIC;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._Class;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._Object;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._body;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._boolean;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._call;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._catch;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._class;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._classGenerator;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._clear;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._const;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._constructor;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._else;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._end;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._expr;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._finally;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._if;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._import;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._int;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._method;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._package;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._s;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._super;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._t;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._this;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._try;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._v;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._void;

public class Flow_gen implements ClassGeneratorFactory {
    private static final Signature traceSignature = 
	_s( _boolean(), _int() ) ;
    private static final Signature expectClassSignature = 
	_s( _void(), _Object(), _Class() ) ;
    private static final Signature expectIntSignature = 
	_s( _void(), _int(), _int() ) ;

    public String className() {
	return "Flow" ;
    }

    private void startTestMethod( String name ) {
	_method( PUBLIC, _void(), name ) ;
	// method has no arguments
	_body() ;
    }

    private Expression traceCall( int arg ) {
	return _call( _this(), "trace", traceSignature, _const(arg)) ;
    }

    private Expression expectClassCall( Expression obj, Type classType ) {
	return _call( _this(), "expect", expectClassSignature, obj,
	    _const(classType) ) ;
    }
	    
    private void simpleIfMethod() {
	startTestMethod( "simpleIf" ) ;
	    _if( traceCall(1) ) ;
		_expr(traceCall(2)) ;
	    _else() ;
		_expr(traceCall(3)) ;
	    _end() ;
	    _expr(traceCall(4)) ;
	_end() ;
    }

    private void complexIfMethod() {
	startTestMethod( "complexIf" ) ;
	    _if( traceCall(1) ) ;
		_if( traceCall(2) ) ;
		    _expr(traceCall(3)) ;
		_else() ;
		    _expr(traceCall(4)) ;
		_end() ;
		_expr(traceCall(5)) ;
		_if( traceCall(6) ) ;
		    _expr(traceCall(7)) ;
		    _if( traceCall(8) ) ;
			_expr(traceCall(9)) ;
		    _else() ;
			_expr(traceCall(10)) ;
		    _end() ;
		_else() ;
		    _expr(traceCall(11)) ;
		_end() ;
	    _else() ;
		_if( traceCall(12) ) ;
		    _expr(traceCall(13)) ;
		_else() ;
		    _expr(traceCall(14)) ;
		    _if( traceCall(15) ) ;
			_expr(traceCall(16)) ;
		    _else() ;
			_expr(traceCall(17)) ;
		    _end() ;
		_end() ;
	    _end() ;
	    _expr(traceCall(18));
	_end() ;
    }

    private void simpleTryCatchMethod() {
	startTestMethod( "simpleTryCatch" ) ;
	    _expr(traceCall(1)) ;
	    _try() ;
		_expr(traceCall(2)) ;
		_expr(traceCall(3)) ;
	    _catch( _t("FirstException"), "exc" ) ;
		_expr(expectClassCall( _v("exc"), _t("FirstException"))) ;
		_expr(traceCall(4)) ;
		_expr(traceCall(5)) ;
	    _end() ;
	    _expr(traceCall(6)) ;
	_end() ;
    }

    private void simpleTryCatchFinallyMethod() {
	startTestMethod( "simpleTryCatchFinally" ) ;
	    _expr(traceCall(1)) ;
	    _try() ;
		_expr(traceCall(2)) ;
		_expr(traceCall(3)) ;
	    _catch( _t("FirstException"), "exc" ) ;
		_expr(expectClassCall( _v("exc"), _t("FirstException"))) ;
		_expr(traceCall(4)) ;
		_expr(traceCall(5)) ;
	    _finally() ;
		_expr(traceCall(6)) ;
		_expr(traceCall(7)) ;
	    _end() ;
	    _expr(traceCall(8)) ;
	_end() ;
    }

    public ClassGenerator evaluate() {
	_clear() ;
	_package( "dynamic.codegen.gen" ) ;
	_import( "org.glassfish.pfl.dynamic.codegen.ControlBase" ) ;
	_import( "org.glassfish.pfl.dynamic.codegen.BaseException" ) ;
	_import( "org.glassfish.pfl.dynamic.codegen.FirstException" ) ;
	_import( "org.glassfish.pfl.dynamic.codegen.SecondException" ) ;

	_class( PUBLIC, className(), _t("ControlBase") ) ;
	    // Simple default constructor
	    _constructor( PUBLIC ) ;
	    _body() ;
		_expr(_super(_s(_void()))) ;
	    _end() ;

	    // generate all of the test methods
	    simpleIfMethod() ;
	    complexIfMethod() ;
	    simpleTryCatchMethod() ;
	    simpleTryCatchFinallyMethod() ;
	_end() ; // of Flow_gen class

	return _classGenerator() ;
   }
}
