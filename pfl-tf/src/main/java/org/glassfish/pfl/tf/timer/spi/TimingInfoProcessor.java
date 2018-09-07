/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi ;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.pfl.dynamic.codegen.impl.Identifier;
import org.glassfish.pfl.basic.contain.Pair;

public class TimingInfoProcessor {
    private boolean done = false ;
    private String pkg ;
    private TimerFactory tf ;
    private Map<String,List<String>> contents ;
    private TimerGroup currentTimerGroup ;

    private void checkForValidIdentifier( String name ) {
	if (!Identifier.isValidIdentifier( name )) {
	    throw new IllegalArgumentException("name " + name
                + " is not a valid Java identifier");
	}
    }

    private void checkDone() {
	if (done) {
	    throw new IllegalStateException("past getResult: "
                + "no other methods may be called");
	}
    }

    public TimingInfoProcessor( String name, String pkg ) {
	this.done = false ;
	this.pkg = pkg ;
	checkForValidIdentifier( name ) ;
	if (!Identifier.isValidFullIdentifier( pkg )) {
	    throw new IllegalArgumentException(pkg
                + " is not a valid package name");
	}
	this.tf = TimerFactoryBuilder.make( name, name ) ;
	this.contents = new LinkedHashMap<String,List<String>>() ;
	this.currentTimerGroup = null ;
    }

    public void addTimer( String name, String desc ) {
	checkDone() ;
	checkForValidIdentifier( name ) ;
	if (!tf.timerAlreadyExists( name )) {
	    tf.makeTimer( name, desc ) ;
	}
	currentTimerGroup = null ;
    }

    public void addTimerGroup( String name, String desc ) {
	checkDone() ;
	checkForValidIdentifier( name ) ;
	currentTimerGroup = tf.makeTimerGroup( name, desc ) ;
    }

    private void addContained( String timerName, String timerGroupName ) {
	List<String> list = contents.get( timerGroupName ) ;
	if (list == null) {
	    list = new ArrayList<String>() ;
	    contents.put( timerGroupName, list ) ;
	}

	list.add( timerName ) ;
    }

    public void containedIn( String timerName, String timerGroupName ) {
	addContained( timerName, timerGroupName ) ;
    }

    public void contains( String name ) {
	checkDone() ;
	if (currentTimerGroup == null) {
	    throw new IllegalStateException(
		"contains must be called after an addTimerGroup call" ) ;
	} else {
	    String cname = currentTimerGroup.name() ;
	    addContained( name, cname ) ;
	}
    }

    private Controllable getControllable( String name ) {

	Controllable result = tf.timers().get( name ) ;
	if (result == null) {
	    result = tf.timerGroups().get(name);
	}
	if (result == null) {
	    throw new IllegalArgumentException(name +
		" is not a valid Timer or TimerGroup name");
	}
	return result ;
    }

    private void updateTimerFactoryContents() {
	//  Use the Map<String,List<String>> to fill in the TimerGroup
	//  containment relation
	for (String str : contents.keySet()) {
	    List<String> list = contents.get(str) ;
	    TimerGroup tg = tf.timerGroups().get( str ) ;
	    for (String content : list) {
		tg.add( getControllable( content ) ) ;
	    }
	}
    }

    public Pair<String,TimerFactory> getResult() {
	checkDone() ;
	done = true ;
	updateTimerFactoryContents() ;
	Pair<String,TimerFactory> result = 
	    new Pair<String,TimerFactory>( pkg, tf ) ;
	return result ;
    }
}
