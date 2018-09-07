/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.graph ;

import java.util.Set ;

public interface Graph<T extends Node> extends Set<T> // Set<Node>
{
    NodeData getNodeData( T node ) ;

    Set<T> getRoots() ;
}
