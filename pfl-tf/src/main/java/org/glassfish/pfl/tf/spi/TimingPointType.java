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

/** Used to indicate what kind of TimingPoint a InfoMethod represents.
 * For convience, BOTH is the type of a Monitored Method, since the
 * enter and exit to the method give the corresponding timer events.
 */
public enum TimingPointType { 
    NONE, 
    BOTH,
    ENTER, 
    EXIT }
