/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.glassfish.pfl.tf.spi.MethodMonitorBase;

/**
 *
 * @author ken
 */
public class MethodMonitorTracingImpl extends MethodMonitorBase
    implements Iterable<TraceNode>  {

    private static final String myName = "Tracing" ;

    @Override
    public Iterator<TraceNode> iterator() {
        return state.iterator() ;
    }

    public enum EntryType { ENTER, INFO, EXIT, EXIT_RESULT, EXCEPTION }

    private final List<TraceNode> state = new ArrayList<TraceNode>() ;

    public MethodMonitorTracingImpl( Class<?> cls ) {
        super( myName, cls,
            new MethodMonitorBase.MethodMonitorFactorySelfImpl(myName)) ;
        ((MethodMonitorBase.MethodMonitorFactorySelfImpl)factory()).init(
            this ) ;
    }

    @Override
    public void enter(int ident, Object... args) {
        state.add( new TraceNode( ident, args ) ) ;
    }

    @Override
    public void info(Object[] args, int callerIdent, int selfIdent) {
        state.add( new TraceNode( selfIdent, callerIdent, args ) ) ;
    }

    @Override
    public void exit(int ident) {
        state.add( new TraceNode( ident ) ) ;
    }

    @Override
    public void exit(int ident, Object result) {
        state.add( new TraceNode( ident, result ) ) ;
    }

    @Override
    public void exception(int ident, Throwable thr) {
        state.add( new TraceNode( ident, thr ) ) ;
    }

    @Override
    public void clear() {
        state.clear() ;
    }
}
