/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.impl;

// Test NamedBase

import org.glassfish.pfl.tf.timer.spi.TimerFactory;
import org.glassfish.pfl.tf.timer.spi.TimerFactoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ControllableBaseTest {
    private String name = "MyName" ;
    private int id = 26 ;
    private String description = "Another simple test" ;
    private TimerFactory factory ;
    private ControllableTest ct ;

    private static class ControllableTest extends ControllableBase {
	public ControllableTest( int id, String name, String description,
	    TimerFactory factory ) {

	    super( id, name, description, TimerFactoryImpl.class.cast( factory ) ) ;
	}
    }

    @Before
    public void setUp() {
	factory = TimerFactoryBuilder.make( "CTF", "No description" ) ;
	ct = new ControllableTest( id, name, description, factory ) ;
    }

    @After
    public void tearDown() {
	TimerFactoryBuilder.destroy( factory ) ;
    }

    @Test()
    public void testId() {
	Assert.assertEquals( id, ct.id() ) ;
    }

    @Test() 
    public void testDescription() {
	Assert.assertEquals( description, ct.description() ) ;
    }

    @Test()
    public void testEnable() {
	Assert.assertFalse( ct.isEnabled() ) ;
	ct.enable() ;
	Assert.assertTrue( ct.isEnabled() ) ;
	ct.disable() ;
	Assert.assertFalse( ct.isEnabled() ) ;
    }
}
