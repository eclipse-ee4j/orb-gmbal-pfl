/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * StatementBase.java
 *
 * Created on January 19, 2005, 11:59 PM
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import org.glassfish.pfl.dynamic.codegen.impl.Statement ;
import org.glassfish.pfl.dynamic.codegen.impl.Node ;

/**
 *
 * @author ken
 */
public abstract class StatementBase extends NodeBase implements Statement {
    public StatementBase( Node parent ) {
	super( parent ) ;
    }
}
