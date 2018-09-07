/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.impl ;

import org.glassfish.pfl.tf.timer.spi.NamedBase;

public class NamedBaseImpl extends NamedBase {
    public NamedBaseImpl( TimerFactoryImpl factory, String name ) {
	super( factory, name ) ;
    }

    @Override
    public TimerFactoryImpl factory() {
	return TimerFactoryImpl.class.cast( super.factory() ) ;
    }
}
