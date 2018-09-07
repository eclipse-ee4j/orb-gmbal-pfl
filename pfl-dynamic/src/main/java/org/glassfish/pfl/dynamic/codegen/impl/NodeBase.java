/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import org.glassfish.pfl.dynamic.codegen.impl.Node ;

import org.glassfish.pfl.dynamic.copyobject.spi.DefaultCopier ;
import org.glassfish.pfl.dynamic.copyobject.spi.Copy ;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyType ;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyInterceptor ;

/** Base class for implementing various kinds of Nodes in the AST.
 * This mainly supports dynamic attributes.  It also supports
 * dynamic delegation to another node.  Dynamic attributes not
 * found in the current node will automatically be searched for
 * in the delegate.
 *
 * @author Ken Cavanaugh
 */
public class NodeBase extends AttributedObjectBase implements Node, CopyInterceptor {
    // Copying of tree nodes deserves some discussion here.
    // The basic issue is that we must always make sure that the
    // AST is really a tree, and not a DAG.  This is necessary
    // because each Node in the AST has dynamic attributes, and we
    // cannot allow the situation where a Visitor traversing the
    // tree would attempt to set conflicting dynamic attribute values
    // when visiting a Node from different paths.
    //
    // To avoid this problem, we make sure that every Node that is 
    // added to the AST as an ordinary java field is first copied
    // before it is added to the tree.  Being a lazy programmer,
    // I do not want to write clone methods or copy constructors on
    // every different subclass of Node in the AST representation.
    // Since we already have a reflective copier in the ORB, I want to
    // re-use that.  But it does not quite do the right thing.  In particular,
    // the default copier would traverse the parent of each Node, which means
    // that effectively every time we copy a node, we copy the entire AST,
    // leading to extreme memory consumption (perhaps quadratic or worse in AST size).
    //
    // To avoid this, we need some way of controlling exactly how AST nodes are 
    // copied.  The CopyInterceptor interface and Copy annotation provide a lot
    // of flexibility for the object copier.  Here we just need to make sure that the
    // parent is NOT traversed, so that the copy and the original just share
    // the same parent.
    @Copy(CopyType.IDENTITY) // parent is set to the same reference in the source and the copy
    private Node parent ;

    private static int nextId = 0 ;
    private int myId ;

    public void preCopy() {
    }

    public void postCopy() {
	// Get a new ID for the new node
	myId = getNewId() ;
    }

    private synchronized static int getNewId() {
	return nextId++ ;
    }

    public final <T extends Node> T getAncestor( Class<T> type ) {
    	Node current = this ;
	while (current != null && current.getClass() != type)
	    current = current.parent() ;

	if (current == null)
	    return null ;

	return type.cast( current ) ;
    }

    public NodeBase( Node parent ) {
        this.parent = parent ;
	myId = getNewId() ;
    }
    
    public int id() {
	return myId ;
    }

    public final Node parent() {
        return parent ;
    }

    public final void parent( Node node ) {
	this.parent = node ;
    }

    // May be overridden in subclass to control copy behavior.
    public <T extends Node> T copy( Class<T> cls ) {
	return cls.cast( DefaultCopier.copy(this) ) ;
    }

    // May be overridden in subclass to control copy behavior.
    public <T extends Node> T copy( Node newParent, Class<T> cls ) {
	T result = cls.cast( DefaultCopier.copy(this) ) ;
	result.parent( newParent ) ;
	return result ;
    }

    public String toString() {
	String cname = this.getClass().getName() ;
	final int lastDot = cname.lastIndexOf( '.' ) ;
	cname = cname.substring( lastDot+1 ) ;
	return cname + "@" + myId ;
    }

    // Usually overridden in subclass
    public void accept( Visitor visitor ) {
	visitor.visitNode( this ) ;
    }
}
