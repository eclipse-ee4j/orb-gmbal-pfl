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
import org.glassfish.pfl.dynamic.codegen.spi.Expression;
import org.glassfish.pfl.dynamic.codegen.ClassGeneratorFactory;
import static java.lang.reflect.Modifier.* ;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;

public class MyRemote__Adapter_gen implements ClassGeneratorFactory {
    @Override
    public String className() {
	return "MyRemote__Adapter" ;
    }
    
    private Expression makeNewEJBException( String varName ) {
	return _new( _t("EJBException"), _s(_void(),_t("Exception")), _v(varName)) ;
    }

    public ClassGenerator evaluate() {
	_clear() ;
	_package( "dynamic.codegen.gen" ) ;
	_import( "java.lang.Exception" ) ;
	_import( "java.lang.Throwable" ) ;
	_import( "java.io.Serializable" ) ;
	_import( "javax.ejb.EJBObject" ) ;
	_import( "javax.ejb.EJBException" ) ;
	_import( "java.rmi.RemoteException" ) ;
	_import( "org.glassfish.pfl.dynamic.codegen.lib.AppException" ) ;
	_import( "org.glassfish.pfl.dynamic.codegen.lib.MyBusinessIntf" ) ;
	_import( "org.glassfish.pfl.dynamic.codegen.lib.EJBObjectBase" ) ;
	_import( "dynamic.codegen.gen.MyRemote" ) ;

	_class( PUBLIC, className(), _t("EJBObjectBase"),
	    _t("EJBObject"), _t("MyBusinessIntf"), _t("Serializable")) ;
	    
	    _data( PRIVATE, _t("MyRemote"), "myRemote" ) ;

	    _constructor( PUBLIC ) ;
		_arg( _t("MyRemote"), "arg" ) ;
	    _body() ;
		_expr(_super(_s(_void()))) ;
		_assign(_v("myRemote"),_v("arg")) ;
	    _end() ;

	    _method( PUBLIC, _void(), "doSomething" ) ;
	    _body() ;
		_try() ;
		    _expr( _call( _v("myRemote"), "doSomething", _s(_void()))) ;
		_catch( _t("RemoteException"), "re" ) ;
		    _throw( makeNewEJBException( "re" ) ) ;
		_end() ;
	    _end() ;

	    _method( PUBLIC, _int(), "doSomethingElse", _t("AppException")) ;
	    _body() ;
		_try() ;
		    _return( _call( _v("myRemote"), "doSomethingElse", _s(_int()))) ;
		_catch( _t("RemoteException"), "re" ) ;
		    _define( _t("EJBException"), "exc", 
			_new( _t("EJBException"), _s(_void()))) ;
		    _expr( _call( _v("exc"), "initCause", 
			_s(_t("Throwable"),_t("Throwable")), _v("re"))) ;
		    _throw( _v("exc")) ;
		_end() ;
	    _end() ;

	    _method( PUBLIC, _int(), "echo") ;
		_arg( _int(), "arg" ) ;
	    _body() ;
		_try() ;
		    _return( _call( _v("myRemote"), "echo", _s(_int(),_int()), _v("arg"))) ;
		_catch( _t("RemoteException"), "re" ) ;
		    _throw( makeNewEJBException( "re" ) ) ;
		_end() ;
	    _end() ;
	_end() ;

	return _classGenerator() ;
    }
}
