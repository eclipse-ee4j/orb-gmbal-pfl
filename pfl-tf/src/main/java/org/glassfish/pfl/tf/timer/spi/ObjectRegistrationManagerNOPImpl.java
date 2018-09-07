/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi;

/**
 *
 * @author Ken Cavanaugh
 */
public class ObjectRegistrationManagerNOPImpl 
    implements ObjectRegistrationManager {

    @Override
    public void manage(Named obj) {
    }

    @Override
    public void manage(Named parent, Named obj) {
    }

    @Override
    public void unmanage(Named obj) {
    }
}
