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

import org.glassfish.pfl.dynamic.codegen.spi.Expression;
import org.glassfish.pfl.dynamic.codegen.spi.Type;
import org.glassfish.pfl.dynamic.codegen.spi.ClassGenerator;
import org.glassfish.pfl.dynamic.codegen.ClassGeneratorFactory;
import static java.lang.reflect.Modifier.* ;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;

public class MyRemote__Adapter_Simplified_gen implements ClassGeneratorFactory {
    @Override
    public String className() {
	return "MyRemote__Adapter_Simplified" ;
    }
    
    public ClassGenerator evaluate() {
	_clear() ;
	_package( "dynamic.codegen.gen" ) ;
	Type Serializable = _import( "java.io.Serializable" ) ;
	Type EJBObject = _import( "javax.ejb.EJBObject" ) ;
	Type EJBException = _import( "javax.ejb.EJBException" ) ;
	Type RemoteException = _import( "java.rmi.RemoteException" ) ;
	Type AppException = _import( "org.glassfish.pfl.dynamic.codegen.lib.AppException" ) ;
	Type MyBusinessIntf = _import( "org.glassfish.pfl.dynamic.codegen.lib.MyBusinessIntf" ) ;
	Type EJBObjectBase = _import( "org.glassfish.pfl.dynamic.codegen.lib.EJBObjectBase" ) ;
	Type MyRemote = _import( "dynamic.codegen.gen.MyRemote" ) ;

	_class( PUBLIC, className(), EJBObjectBase,
	    EJBObject, MyBusinessIntf, Serializable) ; {
	    
	    Expression myRemote = _data( PRIVATE, MyRemote, "myRemote" ) ;

	    _constructor( PUBLIC ) ; {
		Expression arg = _arg( MyRemote, "arg" ) ;
	    _body() ;
		_expr(_super()) ;
		_assign(myRemote,arg) ;
	    _end() ; }

	    _method( PUBLIC, _void(), "doSomething" ) ; {
	    _body() ;
		_try() ;
		    _expr( _call( myRemote, "doSomething")) ;
		Expression re = _catch( RemoteException, "re" ) ;
		    _throw( _new( EJBException, re ) ) ;
		_end() ;
	    _end() ; }

	    _method( PUBLIC, _int(), "doSomethingElse", AppException) ; {
	    _body() ;
		_try() ;
		    _return( _call( myRemote, "doSomethingElse")) ;
		Expression re = _catch( RemoteException, "re" ) ;
		    Expression exc = _define( EJBException, "exc",
			_new( EJBException )) ;
		    _expr( _call( exc, "initCause", re )) ;
		    _throw( exc ) ;
		_end() ;
	    _end() ; }

	    _method( PUBLIC, _int(), "echo") ; {
		Expression arg = _arg( _int(), "arg" ) ;
	    _body() ;
		_try() ;
		    _return( _call( myRemote, "echo", arg)) ;
		Expression re = _catch( RemoteException, "re" ) ;
		    _throw( _new( EJBException, re ) ) ;
		_end() ;
	    _end() ; }
	_end() ; }

	return _classGenerator() ;
    }
}
