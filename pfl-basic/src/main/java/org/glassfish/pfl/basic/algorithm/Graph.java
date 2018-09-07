/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm ;

import java.util.List ;
import java.util.Collection ;
import java.util.Collections ;
import java.util.Set ;
import java.util.HashSet ;
import java.util.ArrayList ;
import org.glassfish.pfl.basic.func.UnaryFunction;

public class Graph<E> {
    /** A Finder finds the immediate successors of an element of the graph.
     */
    public interface Finder<E> extends UnaryFunction<E,List<E>> {} 

    private Set<E> roots ;
    private List<E> preorderTraversal = null ;
    private List<E> postorderTraversal = null ;

    private void traverse( final E node, final Set<E> visited, final Finder<E> finder ) {
	if (!visited.contains( node )) {
	    visited.add( node ) ;

	    preorderTraversal.add( node ) ;

	    for (E child : finder.evaluate(node)) {
		traverse( child, visited, finder ) ;
	    }

	    postorderTraversal.add( node ) ;
	}
    }

    private void init( final Collection<E> roots, final Finder<E> finder ) {
	this.roots = new HashSet<E>( roots ) ;
	this.roots = Collections.unmodifiableSet( this.roots ) ;
	this.preorderTraversal = new ArrayList<E>() ;
	this.postorderTraversal = new ArrayList<E>() ;
	final Set<E> visited = new HashSet<E>() ;
	for (E node : this.roots) {
	    traverse( node, visited, finder ) ;
	}
	this.preorderTraversal = Collections.unmodifiableList( this.preorderTraversal ) ;
	this.postorderTraversal = Collections.unmodifiableList( this.postorderTraversal ) ;
    }

    public Graph( final Collection<E> roots, final Finder<E> finder ) {
	init( roots, finder ) ;
    }

    public Graph( final E root, final Finder<E> finder )   {
	final Set<E> roots = new HashSet<E>() ;
	roots.add( root ) ;
	init( roots, finder ) ;
    }

    public Set<E> getRoots() {
	return roots ;
    }

    public List<E> getPreorderList() {
	return preorderTraversal ;
    }

    public List<E> getPostorderList() {
	return postorderTraversal ;
    }
}
