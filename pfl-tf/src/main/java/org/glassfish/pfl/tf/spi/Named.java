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

/**
 *
 * @author ken_admin
 */
public abstract class Named {
    private final String name ;
    private final String type ;

    // lazy init on first toString() call
    private boolean init = false ;
    private String toString = null ;
    private int hashCode = 0 ;

    protected Named( String type, String name ) {
        this.type = type ;
        this.name = name ;
    }

    private void init() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append( '[' ) ;
        sb.append(name);
        sb.append(']');
        toString = sb.toString();
        hashCode = toString.hashCode() ;
    }

    public final String name() {
        return name;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true ;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Named)) {
            return false;
        }

        Named other = (Named)obj;
        return name().equals(other.name());
    }

    @Override
    public synchronized final int hashCode() {
        if (!init) {
            init() ;
        }

        return hashCode ;
    }

    @Override
    public synchronized final String toString() {
        if (!init) {
            init() ;
        }

        return toString ;
    }

}
