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

import java.util.Arrays;
import java.util.List;

public class TraceNode {
    private MethodMonitorTracingImpl.EntryType type;
    private int ident;          // selfIdent in INFO
    private int callerIdent;    // Only used in INFO for callerIdent
    private Object data;        // Object[] for ENTER, INFO
                                // Object for EXIT_RESULT
                                // Throwable for EXCEPTION

    private TraceNode( final MethodMonitorTracingImpl.EntryType type,
        final int ident, final int callerIdent, final Object data) {

        this.type = type;
        this.ident = ident;
        this.callerIdent = callerIdent;
        this.data = data;
    }

    public TraceNode(int ident, Object[] args) {
        this(MethodMonitorTracingImpl.EntryType.ENTER, ident, -1, args);
    }

    public TraceNode(int ident) {
        this(MethodMonitorTracingImpl.EntryType.EXIT, ident, -1, null);
    }

    public TraceNode(int ident, Object result) {
        this(MethodMonitorTracingImpl.EntryType.EXIT_RESULT, ident, -1, result);
    }

    public TraceNode(int ident, int callerIdent, Object[] args) {
        this(MethodMonitorTracingImpl.EntryType.INFO, ident, callerIdent, args);
    }

    public TraceNode(int ident, Throwable thr) {
        this(MethodMonitorTracingImpl.EntryType.EXCEPTION, ident, -1, thr);
    }

    public MethodMonitorTracingImpl.EntryType type() {
        return type ;
    }

    public int ident() {
        return ident ;
    }

    public int callerIdent() {
        return callerIdent ;
    }

    public Object data() {
        return data ;
    }

    @Override 
    public String toString() {
        final StringBuffer sb = new StringBuffer() ;
        sb.append( "TraceNode[" ) ;
        sb.append( type ) ;

        sb.append( " ident=" ) ;
        sb.append( ident ) ;

        if (callerIdent >= 0) {
            sb.append( " callerIdent=") ;
            sb.append( callerIdent ) ;
        }

        if (data != null) {
            sb.append( " data=" ) ;
            if (data instanceof Object[]) {
                Object[] arr = (Object[])data ;
                List<Object> list = Arrays.asList( arr ) ;
                sb.append( list ) ;
            } else {
                sb.append( data ) ;
            }
        }

        sb.append( ']' ) ;

        return sb.toString() ;
    }

    @Override
    public int hashCode() {
        int partRes = 53 * (37 * type.ordinal() + ident) + callerIdent;
        if (data == null) {
            return partRes;
        } else if (data instanceof Object[]) {
            int result = partRes;
            Object[] arr = (Object[]) data;
            for (int ctr = 0; ctr < arr.length; ctr++) {
                if (arr[ctr] == null) {
                    result = 37 * result;
                } else {
                    result = 37 * result + arr[ctr].hashCode();
                }
            }
            return result;
        } else {
            return partRes ^ data.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TraceNode)) {
            return false;
        }

        TraceNode other = (TraceNode) obj;
        final boolean partRes = type == other.type
            && ident == other.ident
            && callerIdent == other.callerIdent;
        if (partRes == false) {
            return partRes;
        }

        if ((data == null) || (other.data == null)) {
            return other.data == data ;
        }

        if (data instanceof Object[]) {
            if (!(other.data instanceof Object[])) {
                return false;
            }

            Object[] td = (Object[]) data;
            Object[] od = (Object[]) other.data;
            if (td.length != od.length) {
                return false;
            }

            for (int ctr = 0; ctr < td.length; ctr++) {
                if ((td[ctr] == null) && (od[ctr] != null)) {
                    return false;
                }
                if (!td[ctr].equals(od[ctr])) {
                    return false;
                }
            }

            return true;
        } else if (data instanceof Throwable) {
            return data.getClass().equals( other.data.getClass() ) ;
        } else {
            return data.equals(other.data);
        }
    }
}
