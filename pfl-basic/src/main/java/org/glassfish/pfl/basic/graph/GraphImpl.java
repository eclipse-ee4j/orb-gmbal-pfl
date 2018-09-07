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

import java.util.Collection ;
import java.util.AbstractSet ;
import java.util.ArrayList;
import java.util.Iterator ;
import java.util.Map ;
import java.util.HashMap ;
import java.util.Set ;
import java.util.HashSet ;

/**
 * Implementation of a simple graph.
 * @author ken
 * @param <T> The type of a Node in the graph, which must extend Node.
 */
public class GraphImpl<T extends Node<T>> extends AbstractSet<T> implements Graph<T>
{
    private Map<T,NodeData> nodeToData ;

    public GraphImpl()
    {
	nodeToData = new HashMap<T,NodeData>() ;
    }

    public GraphImpl( Collection<T> coll )
    {
	this() ;
	addAll( coll ) ;
    }
    
/***********************************************************************************/    
/************ AbstractSet implementation *******************************************/    
/***********************************************************************************/    

    // Required for AbstractSet
    @Override
    public boolean add( T obj ) // obj must be a Node
    {
	boolean found = nodeToData.keySet().contains( obj ) ;

	if (!found) {
	    NodeData nd = new NodeData() ;
	    nodeToData.put( obj, nd ) ;
	}

	return !found ;
    }

    // Required for AbstractSet
    public Iterator<T> iterator()
    {
	return nodeToData.keySet().iterator() ;
    }

    // Required for AbstractSet
    public int size()
    {
	return nodeToData.keySet().size() ;
    }

/***********************************************************************************/    

    public NodeData getNodeData( T node )
    {
	return nodeToData.get(node) ;
    }

    private void clearNodeData()
    {
	// Clear every node
        for (Map.Entry<T,NodeData> entry : nodeToData.entrySet() ) {
            entry.getValue().clear() ;
        }
    }

    interface NodeVisitor<T extends Node>
    {
	void visit( Graph<T> graph, T node, NodeData nd ) ;
    }

    // This visits every node in the graph exactly once.  A
    // visitor is allowed to modify the graph during the
    // traversal.
    void visitAll( NodeVisitor<T> nv )
    {
	boolean done = false ;

	// Repeat the traversal until every node has been visited.  Since
	// it takes one pass to determine whether or not each node has 
	// already been visited, this loop always runs at least once.
	do {
	    done = true ;

	    // Copy entries to array to avoid concurrent modification
	    // problem with iterator if the visitor is updating the graph.
	    Collection<Map.Entry<T,NodeData>> entries =
		new ArrayList<Map.Entry<T,NodeData>>( nodeToData.entrySet() ) ;

	    // Visit each node in the graph that has not already been visited.
	    // If any node is visited in this pass, we must run at least one more
	    // pass.
            for (Map.Entry<T,NodeData> current : entries) {
                T node = current.getKey() ;
                NodeData nd = current.getValue() ;

		if (!nd.isVisited()) {
		    nd.visited() ;
		    done = false ;

		    nv.visit( this, node, nd ) ;
		}
            }
	} while (!done) ;	
    }

    private void markNonRoots()
    {
	visitAll( 
	    new NodeVisitor<T>() {
		public void visit( Graph<T> graph, T node, NodeData nd ) {
                    for (T child : node.getChildren()) {
			// Make sure the child is in the graph so it can be
			// visited later if necessary.
			graph.add( child ) ;

			// Mark the child as a non-root, since a child is never a root.
			NodeData cnd = graph.getNodeData( child ) ;
			cnd.notRoot() ;
                    }
		}
	    } ) ;
    }

    private Set<T> collectRootSet()
    {
	final Set<T> result = new HashSet<T>() ;

        for (Map.Entry<T,NodeData> entry : nodeToData.entrySet()) {
	    T node = entry.getKey() ;
	    NodeData nd = entry.getValue() ;
	    if (nd.isRoot()) {
		result.add( node ) ;
            }
        }

	return result ;
    }

    public Set<T> getRoots()
    {
	clearNodeData() ;
	markNonRoots() ;
	return collectRootSet() ;
    }
}
