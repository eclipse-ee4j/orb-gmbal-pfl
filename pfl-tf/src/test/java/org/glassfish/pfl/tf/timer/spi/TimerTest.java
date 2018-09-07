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

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

// import corba.framework.TimerUtils ;


public class TimerTest {
    // Test TimerFactoryBuilder
    @Test
    public void factoryBuilderCreate1() {
	String name = "TF1" ;
	String description = "First Test Factory" ;
	TimerFactory tf = TimerFactoryBuilder.make( name, description ) ;
	Assert.assertEquals( tf.name(), name ) ;
	Assert.assertEquals( tf.description(), description ) ;
	TimerFactoryBuilder.destroy( tf ) ;
    }

    @Test( expected=IllegalArgumentException.class )
    public void factoryBuilderCreate2() {
	String name = "TF1" ;
	String description = "First Test Factory" ;
	TimerFactory tf = TimerFactoryBuilder.make( name, description ) ;
	try {
	    tf = TimerFactoryBuilder.make( name, description ) ;
	} finally {
	    TimerFactoryBuilder.destroy( tf ) ;
	}
    }

    @Test()
    public void factoryBuilderCreate3() {
	String name = "TF1" ;
	String description = "First Test Factory" ;
	TimerFactory tf = TimerFactoryBuilder.make( name, description ) ;
	TimerFactoryBuilder.destroy( tf ) ;
	tf = TimerFactoryBuilder.make( name, description ) ;
	TimerFactoryBuilder.destroy( tf ) ;
    }

    private static void sleep( int time ) {
	try {
	    Thread.sleep( time ) ;
	} catch (Exception exc) {
	    // ignore it 
	}
    }

    private void recordCall(TimingPoints tp, Timer top,
                            TimerEventController controller, int transportDelay ) {
	
	controller.enter( top ) ;

	controller.enter( tp.ClientDelegateImpl__hasNextNext() );
	sleep( 1 ) ;
	controller.exit( tp.ClientDelegateImpl__hasNextNext() );

        controller.enter( tp.ClientRequestDispatcherImpl__connectionSetup() ) ;
	sleep( 4 ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__connectionSetup() ) ;

        controller.enter( tp.ClientRequestDispatcherImpl__clientEncoding() ) ;
	sleep( 100 ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__clientEncoding() ) ;

        controller.enter( tp.ClientRequestDispatcherImpl__clientTransportAndWait() ) ;
	sleep( transportDelay ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__clientTransportAndWait() ) ;

        controller.enter( tp.ClientRequestDispatcherImpl__clientDecoding() ) ;
	sleep( 40 ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__clientDecoding() ) ;

	controller.exit( top ) ;
    }

    Map<Timer,Statistics> makeData() {
	// Setup timing points and a top-level timer
        TimerManager<TimingPoints> tm = new TimerManager<TimingPoints>(
            ObjectRegistrationManager.nullImpl, "TestTimerManager" ) ;
        TimerFactory tf = tm.factory() ;
        TimingPoints tp = new TimingPoints(tf) ;
        tm.initialize(tp) ;
        TimerEventController controller = tm.controller() ;
        Timer top = tf.makeTimer( "top", "Encloses the entire operation" ) ;

        StatsEventHandler handler = tf.makeStatsEventHandler( "TestStats" ) ;
        controller.register( handler ) ;
        handler.clear() ;

        tp.Subcontract().enable() ;
        top.enable() ;

        // Simulate the actions of the ORB client transport
        recordCall( tp, top, controller, 25 ) ;
        recordCall( tp, top, controller, 31 ) ;
        recordCall( tp, top, controller, 27 ) ;
        recordCall( tp, top, controller, 42 ) ;
        recordCall( tp, top, controller, 19 ) ;
        recordCall( tp, top, controller, 21 ) ;
        recordCall( tp, top, controller, 23 ) ;
        recordCall( tp, top, controller, 25 ) ;
        recordCall( tp, top, controller, 34 ) ;
        recordCall( tp, top, controller, 33 ) ;
        recordCall( tp, top, controller, 31 ) ;
        recordCall( tp, top, controller, 28 ) ;
        recordCall( tp, top, controller, 27 ) ;
        recordCall( tp, top, controller, 29 ) ;
        recordCall( tp, top, controller, 30 ) ;
        recordCall( tp, top, controller, 31 ) ;
        recordCall( tp, top, controller, 28 ) ;

        return handler.stats() ;

    }

    @Test()
    public void generateStatsTable() {
	Map<Timer, Statistics> data = makeData() ;

	// TimerUtils.writeHtmlTable( data, "TimerTest.html", 
	    // "Client Test Timing Data" ) ;
    }
}
