/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi;

public class TimingPoints
{
    private final Timer ClientDelegateImpl__hasNextNext ;
    private final Timer ClientRequestDispatcherImpl__clientDecoding ;
    private final Timer ClientRequestDispatcherImpl__clientEncoding ;
    private final Timer ClientRequestDispatcherImpl__clientTransportAndWait ;
    private final Timer ClientRequestDispatcherImpl__connectionSetup ;
    private final TimerGroup TimingPoints ;
    private final TimerGroup IsLocal ;
    private final TimerGroup Subcontract ;

    
    public TimingPoints(TimerFactory tf) {
        this.ClientDelegateImpl__hasNextNext = 
	    tf.makeTimer("ClientDelegateImpl__hasNextNext", 
	    "Timer for method enter_hasNextNext in class ClientDelegateImpl") ;
        this.ClientRequestDispatcherImpl__clientDecoding = 
	    tf.makeTimer("ClientRequestDispatcherImpl__clientDecoding", 
	    "Timer for method enter_clientDecoding in class ClientRequestDispatcherImpl") ;
        this.ClientRequestDispatcherImpl__clientEncoding = 
	    tf.makeTimer("ClientRequestDispatcherImpl__clientEncoding", 
	    "Timer for method enter_clientEncoding in class ClientRequestDispatcherImpl") ;
        this.ClientRequestDispatcherImpl__clientTransportAndWait = 
	    tf.makeTimer("ClientRequestDispatcherImpl__clientTransportAndWait", 
	    "Timer for method enter_clientTransportAndWait in class " 
		+ "ClientRequestDispatcherImpl") ;
        this.ClientRequestDispatcherImpl__connectionSetup = 
	    tf.makeTimer("ClientRequestDispatcherImpl__connectionSetup", 
	    "Timer for method enter_connectionSetup in class ClientRequestDispatcherImpl") ;

        this.TimingPoints = tf.makeTimerGroup("TimingPoints", "TimingPoints") ;
        this.IsLocal = tf.makeTimerGroup("IsLocal", "TimerGroup for Annotation IsLocal") ;
        this.Subcontract = tf.makeTimerGroup("Subcontract", "TimerGroup for Annotation Subcontract") ;

	this.TimingPoints.add(this.ClientRequestDispatcherImpl__clientDecoding) ;
	this.TimingPoints.add(this.ClientRequestDispatcherImpl__clientEncoding) ;
	this.TimingPoints.add(this.ClientRequestDispatcherImpl__clientTransportAndWait) ;
	this.TimingPoints.add(this.ClientRequestDispatcherImpl__connectionSetup) ;
	this.TimingPoints.add(this.ClientDelegateImpl__hasNextNext) ;
	this.IsLocal.add(this.ClientDelegateImpl__hasNextNext) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__connectionSetup) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__clientDecoding) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__clientTransportAndWait) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__clientEncoding) ;
    }

    public final Timer ClientDelegateImpl__hasNextNext() {
	return this.ClientDelegateImpl__hasNextNext ;
    }

    public final Timer ClientRequestDispatcherImpl__clientDecoding() {
	return this.ClientRequestDispatcherImpl__clientDecoding ;
    }
	
    public final Timer ClientRequestDispatcherImpl__clientEncoding() {
	return this.ClientRequestDispatcherImpl__clientEncoding ;
    }

    public final Timer ClientRequestDispatcherImpl__clientTransportAndWait() {
	return this.ClientRequestDispatcherImpl__clientTransportAndWait ;
    }
	
    public final Timer ClientRequestDispatcherImpl__connectionSetup() {
	return this.ClientRequestDispatcherImpl__connectionSetup ;
    }
	
    public final TimerGroup IsLocal() {
        return this.IsLocal ;
    }
    
    public final TimerGroup Subcontract() {
        return this.Subcontract ;
    }
    
    public final TimerGroup TimingPoints() {
        return this.TimingPoints ;
    }
}
