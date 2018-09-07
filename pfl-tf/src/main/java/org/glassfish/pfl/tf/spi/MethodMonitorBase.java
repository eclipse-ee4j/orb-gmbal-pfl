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

/**
 *
 * @author ken
 */
public abstract class MethodMonitorBase extends Named
    implements MethodMonitor {

    private final Class<?> cls;
    private final MethodMonitorFactory mmf;
    private final Collection<MethodMonitor> myContents;

    public static class MethodMonitorFactorySelfImpl extends
        MethodMonitorFactoryBase {
        private MethodMonitor mm ;

        public MethodMonitorFactorySelfImpl( String name ) {
            super( name ) ;
        }

        public void init( MethodMonitor mm ) {
            this.mm = mm ;
        }

        public MethodMonitor create(Class<?> cls) {
            return mm ;
        }
    }

    protected MethodMonitorBase(final String name, final Class<?> cls ) {
        this( name, cls, new MethodMonitorFactorySelfImpl( name )) ;
        ((MethodMonitorFactorySelfImpl)factory()).init(this);
    }

    protected MethodMonitorBase(final String name, final Class<?> cls,
        final MethodMonitorFactory mmf) {

        super("MethodMonitor", name);
        this.cls = cls;
        this.mmf = mmf;
        final Set<MethodMonitor> temp = new HashSet<MethodMonitor>();
        temp.add(this);
        this.myContents = Collections.unmodifiableSet(temp);
    }

    protected MethodMonitorBase(final String name, final Class<?> cls,
        final MethodMonitorFactory mmf, Set<MethodMonitor> contents) {

        super("MethodMonitor", name);
        this.cls = cls;
        this.mmf = mmf;
        this.myContents = Collections.unmodifiableSet(contents);
    }

    public final Class<?> myClass() {
        return cls;
    }

    public final MethodMonitorFactory factory() {
        return mmf;
    }

    public final Collection<MethodMonitor> contents() {
        return myContents;
    }
}
