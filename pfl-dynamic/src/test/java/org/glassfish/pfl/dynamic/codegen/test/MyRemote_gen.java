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
import org.glassfish.pfl.dynamic.codegen.spi.Type;

import static java.lang.reflect.Modifier.ABSTRACT;
import static java.lang.reflect.Modifier.PUBLIC;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._arg;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._classGenerator;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._clear;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._end;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._import;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._int;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._interface;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._method;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._package;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper._void;

public class MyRemote_gen implements ClassGeneratorFactory {
    @Override
    public String className() {
	return "MyRemote" ;
    }

    @Override
    public ClassGenerator evaluate() {
	_clear() ;
	_package( "dynamic.codegen.gen" ) ;
	Type EJBObject = _import( "jakarta.ejb.EJBObject" ) ;
	Type RemoteException = _import( "java.rmi.RemoteException" ) ;
	Type AppException = _import( "org.glassfish.pfl.dynamic.codegen.lib.AppException" ) ;

	_interface( PUBLIC, className(), EJBObject ) ;
	    _method( PUBLIC|ABSTRACT, _void(), "doSomething", RemoteException) ;
	    _end() ;

	    _method( PUBLIC|ABSTRACT, _int(), "doSomethingElse", 
		RemoteException, AppException ) ;
	    _end() ;

	    _method( PUBLIC|ABSTRACT, _int(), "echo", RemoteException ) ;
		_arg( _int(), "arg" ) ;
	    _end() ;
	_end() ;

	return _classGenerator() ;
   }
}
