/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class MethodMonitorFactoryBase extends Named
    implements MethodMonitorFactory {

    private final Set<MethodMonitorFactory> myContents ;

    // Used in non-composite case.
    public MethodMonitorFactoryBase( String name ) {
        super( "Factory", name ) ;
        Set<MethodMonitorFactory> temp = new HashSet<MethodMonitorFactory>() ;
        temp.add( this ) ;
        myContents = Collections.unmodifiableSet(temp);
    }

    // Used in composite case.
    MethodMonitorFactoryBase( String name, Set<MethodMonitorFactory> content ) {
        super( "Factory", name ) ;
        myContents = Collections.unmodifiableSet(content);
    }

    public final Collection<MethodMonitorFactory> contents() {
        return myContents;
    }

}
