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

/** Exception used when an attempt is made to enhance a class file to a level that
 * is not appropriate for the class.
 *
 * @author ken
 */
public class TraceEnhancementException extends RuntimeException {

    /**
     * Creates a new instance of <code>TraceEnhancementException</code> without detail message.
     */
    public TraceEnhancementException() {
    }


    /**
     * Constructs an instance of <code>TraceEnhancementException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TraceEnhancementException(String msg) {
        super(msg);
    }
}
