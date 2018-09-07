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

/** Data about a node in a graph. 
 */
public class NodeData 
{
    private boolean visited ;
    private boolean root ;

    public NodeData()
    {
	clear() ;
    }

    public final void clear()
    {
	this.visited = false ;
	this.root = true ;
    }

    /** Return whether this node has been visited in a traversal.
     * Note that we only support a single traversal at a time.
     */
    boolean isVisited() 
    {
	return visited ;
    }

    void visited()
    {
	visited = true ;
    }

    /** Return whether this node is a root.
     */
    boolean isRoot() 
    {
	return root ;
    }

    void notRoot()
    {
	root = false ;
    }
}
