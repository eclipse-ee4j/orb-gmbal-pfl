/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm;

/**
 *
 * @author ken_admin
 */
public class DelayedObjectToString {
    private Object obj ;
    private ObjectUtility ou ;

    public DelayedObjectToString( Object obj, ObjectUtility ou ) {
	this.obj = obj ;
	this.ou = ou ;
    }

    @Override
    public String toString() {
	return ou.objectToString( obj ) ;
    }
}
