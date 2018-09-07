/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi;

// Test NamedBase

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NamedBaseTest {
    private String name = "MyName" ;
    private TimerFactory factory ;
    private NamedTest nb1 ;
    private NamedTest nb2 ;

    private static class NamedTest extends NamedBase {
	public NamedTest( TimerFactory factory, String name ) {
	    super( factory, name ) ;
	}

	public void finish( TimerFactory factory ) {
	    setFactory( factory ) ;
	}
    }

    @Before
    public void setUp() {
	factory = TimerFactoryBuilder.make( "NTF", "No description" ) ;
	nb1 = new NamedTest( factory, name ) ;
	nb2 = new NamedTest( null, name ) ;
    }

    @After
    public void tearDown() {
	TimerFactoryBuilder.destroy( factory ) ;
    }

    @Test() 
    public void name1() {
	Assert.assertEquals( name, nb1.name() ) ;
    }

    @Test() 
    public void name2() {
	Assert.assertEquals( name, nb2.name() ) ;
    }

    @Test()
    public void factory1() {
	Assert.assertEquals( factory, nb1.factory() ) ;
    }

    @Test( expected=IllegalStateException.class)
    public void factory2() {
	TimerFactory tf = nb2.factory() ;
	nb2.finish( factory ) ;
	Assert.assertEquals( factory, nb2.factory() ) ;
	Assert.assertTrue( nb2.toString().contains( factory.name() + ":" +
	    name ) ) ;
    }

    @Test()
    public void equals() {
	Assert.assertEquals( nb1, nb2 ) ;
    }

    @Test()
    public void hashCode1() {
	Assert.assertEquals( nb1.hashCode(), name.hashCode() ) ;
    }

    @Test()
    public void hashCode2() {
	Assert.assertEquals( nb2.hashCode(), name.hashCode() ) ;
    }

    @Test()
    public void toString1() {
	Assert.assertTrue( nb1.toString().contains( factory.name() + ":" + 
	    name ) ) ;
    }

    @Test( expected=IllegalStateException.class)
    public void toString2() {
	String ts = nb2.toString() ;
    }

    @Test( expected=IllegalStateException.class)
    public void setFactory1() {
	nb1.finish( factory ) ;
    }
}
