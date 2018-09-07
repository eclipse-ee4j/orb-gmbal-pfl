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

import org.glassfish.pfl.dynamic.codegen.test.Constants_gen;
import org.glassfish.pfl.dynamic.codegen.test.DefaultPackageTest_gen;
import org.glassfish.pfl.dynamic.codegen.test.Flow_gen;
import org.glassfish.pfl.dynamic.codegen.test.MyRemote__Adapter_Simplified_gen;
import org.glassfish.pfl.dynamic.codegen.test.MyRemote__Adapter_gen;
import org.glassfish.pfl.dynamic.codegen.test.MyRemote_gen;

import java.util.HashMap;
import java.util.Map;

// Import all of the code generators that implement ClassGeneratorFactory here.
// These are used in the tests.
// Every ClassGeneratorFactory must be imported here and registered below.

/** Registry that contains instances of all of the test class generators
 * used in this test.
 */
public abstract class ClassGeneratorFactoryRegistry {
    private ClassGeneratorFactoryRegistry() {}
    
    private static Map<String,ClassGeneratorFactory> map =
	new HashMap<String,ClassGeneratorFactory>() ;

    static {
	register( new MyRemote_gen() ) ;
	register( new MyRemote__Adapter_gen() ) ;
	register( new MyRemote__Adapter_Simplified_gen() ) ;
	register( new Flow_gen() ) ;
	register( new Constants_gen() ) ;
	register( new DefaultPackageTest_gen() ) ;
    }

    private static void register( ClassGeneratorFactory tcg ) {
	map.put( tcg.className(), tcg ) ;
    }

    public static ClassGeneratorFactory get( String name ) {
	return map.get( name ) ;
    }
}
