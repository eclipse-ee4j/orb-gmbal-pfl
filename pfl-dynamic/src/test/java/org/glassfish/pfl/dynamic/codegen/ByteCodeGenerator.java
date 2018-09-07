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

import java.util.Properties ;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;

public class ByteCodeGenerator extends CodeGeneratorBase {
    private Properties options ;
    private long bytecodeGenerationTime ;

    public ByteCodeGenerator( ClassGeneratorFactory cgf, 
	Properties options ) {
	super( cgf ) ;
	this.options = options ;
    }

    public Class generate( ClassLoader loader ) {
	long start = System.nanoTime() ;
	try {
	    return _generate( loader, null, options ) ;
	} finally {
	    bytecodeGenerationTime = (System.nanoTime() - start)/1000 ;
	}
    }

    public long bytecodeGenerationTime() {
	return bytecodeGenerationTime ;
    }
}
