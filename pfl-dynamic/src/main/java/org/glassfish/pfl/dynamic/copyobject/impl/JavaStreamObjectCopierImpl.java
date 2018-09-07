/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl ;

import java.io.InputStream ;
import java.io.ByteArrayInputStream ;
import java.io.ByteArrayOutputStream ;
import java.io.ObjectInputStream ;
import java.io.ObjectOutputStream ;

import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopier ;

public class JavaStreamObjectCopierImpl implements ObjectCopier {
    @Override
    public Object copy(Object obj) {
	try {
	    ByteArrayOutputStream os = new ByteArrayOutputStream( 10000 ) ;
	    ObjectOutputStream oos = new ObjectOutputStream( os ) ;
	    oos.writeObject( obj ) ;

	    byte[] arr = os.toByteArray() ;
	    InputStream is = new ByteArrayInputStream( arr ) ;
	    ObjectInputStream ois = new ObjectInputStream( is ) ;

	    return ois.readObject();
	} catch (Exception exc) {
	    System.out.println( "Failed with exception:" + exc ) ;
	    return null ;
	}
    }
}
