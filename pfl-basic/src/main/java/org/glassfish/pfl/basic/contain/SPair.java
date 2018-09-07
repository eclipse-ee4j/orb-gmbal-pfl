/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain;

import java.io.Serializable;

/** Serializable version of Pair.
 *
 * @author ken_admin
 */
public class SPair<S extends Serializable,T extends Serializable>
    extends Pair<S,T> implements Serializable {
    private static final long serialVersionUID = -430443038952562565L;

    public SPair( S first, T second ) {
        super( first, second ) ;
    }
}
