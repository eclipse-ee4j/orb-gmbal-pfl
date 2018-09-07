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

import java.util.Map ;
import java.util.HashMap ;
import java.util.Collection ;
import java.util.List ;
import java.util.ArrayList ;

import org.glassfish.pfl.tf.timer.impl.TimerFactoryImpl ;

/** TimerFactoryBuilder creates independent
 * instances of the TimerFactory interface.
 * Guarantees that all TimerFactory instances have unique names.
 */
public class TimerFactoryBuilder {
    private static Map<String,TimerFactory> fmap = 
	new HashMap<String,TimerFactory>() ;

    /** Construct the standard name for a Timer derived from a method
     * in the tracing facility.
     * @param cname The name of the monitored clas
     * @param name The name of a monitored method or info method
     * @return The timer name
     */
    public static String getTimerName( final String cname, final String name ) {
        return cname + "__" + name ;
    }


    public synchronized static TimerFactory make( 
        String name, String description ) {

        return make( ObjectRegistrationManager.nullImpl, name, description ) ;
    }

    /** Create a new TimerFactory.  No two TimerFactory instances
     * can have the same name.
     */
    public synchronized static TimerFactory make( ObjectRegistrationManager orm,
        String name, String description ) {

	if (fmap.get( name ) != null)
	    throw new IllegalArgumentException(
		"There is currently a TimerFactory named " + name ) ;

	TimerFactory result = new TimerFactoryImpl( orm, name, description ) ;
	fmap.put( name, result ) ;
	return result ;
    }

    /** Remove a TimerFactory so that it may be collected.
     */
    public synchronized static void destroy( TimerFactory factory ) {
	fmap.remove( factory.name() ) ;
    }

    /** Return a list of the TimerFactory instances in this TimerFactoryBuilder.
     * The list represents the state of the instances at the time this method is 
     * called; any susbsequent make/destroy calls do NOT affect this list.
     */
    public synchronized static List<TimerFactory> contents() {
	Collection<TimerFactory> coll = fmap.values() ;
	ArrayList<TimerFactory> list = new ArrayList( coll ) ;
	return list ;
    }
}
