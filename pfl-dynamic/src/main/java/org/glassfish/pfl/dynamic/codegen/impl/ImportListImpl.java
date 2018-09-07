/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl ;

import java.util.Collections ;
import java.util.List ;
import java.util.ArrayList ;
import java.util.Map ;
import java.util.HashMap ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.ImportList ;
import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.basic.func.UnaryVoidFunction;

public class ImportListImpl implements ImportList {
    private Map<String,Type> imports ;
    private Node root ;
    private List<Pair<String,String>> sortedImports ;

    public ImportListImpl() {
	imports = new HashMap<String,Type>() ;

	clearRoot() ;
    }

    public ImportList copy() {
	ImportListImpl result = new ImportListImpl() ;
	result.imports = new HashMap<String,Type>( imports ) ;
	return result ;
    }

    private void clearRoot() {
	root = null ;
	sortedImports = null ;
    }

    /** Add a fully-qualified type name to the imports.
     * Returns the Type for the name.
     */
    public Type addImport( final String name ) {
	final Type result = Type._class( name ) ;
	addImport( result ) ;
	return result ;
    }

    public void addImport( final Type type ) {
	final String key = type.className() ;
	if (!imports.entrySet().contains( key ) ) {
	    imports.put( key, type ) ;
	    clearRoot() ;
	}
    }

    /** Return whether or not this Type is in the imports.
     */
    public boolean contains( final String name ) {
	final Type type = Type._class( name ) ;
	return contains( type ) ;
    }

    public boolean contains( final Type type ) {
	final String key = type.className() ;
	final Type importType = imports.get( key ) ;
	if (importType == null)
	    return false ;

	return importType.equals( type ) ;
    }

    /** Lookup just the className, without package name.
     */
    public Type lookup( final String className ) {
	return imports.get( className ) ;
    }

    // A node is a node in a tree.  Each node contains either references to
    // other nodes (children()) or a reference to a Type (type()).
    // Every node has a name
    //
    // The name of a node is either the package name (for a list node)
    // or the class name (for a type node).
    private static abstract class Node implements Comparable<Node>{
	private final String name ;

	private Node( final String name ) {
	    this.name = name ;
	}

	public final String name() {
	    return name ;
	}

	public final int compareTo( final Node node ) {
	    return name.compareTo( node.name() ) ;
	}

	// Only one of type or children returns non-null
	public Type type() {
	    return null ;
	}

	public List<Node> children() {
	    return null ;
	}

	public void sort() {
	}

	public Node find( final String name ) {
	    return null ;
	}

	public void add( final Node node ) {
	}

	public void depthFirst( final UnaryVoidFunction<Node> fn ) {
	    fn.evaluate( this ) ;
	}

	public static Node makeTypeNode( final String name, final Type type ) {
	    return new Node( name ) {
		public Type type() {
		    return type ;
		}
	    } ;
	}

	public static Node makeListNode( final String name ) {
	    return new Node( name ) {
		final List<Node> children = new ArrayList<Node>() ;

		public List<Node> children() {
		    return children ;
		}

		public void sort() {
		    Collections.sort( children ) ;
		    for (Node node : children) {
			node.sort() ;
		    }
		}

		public Node find( String name ) {
		    for (Node n : children) {
			if (n.name().equals( name )) {
			    return n ;
			}
		    }

		    return null ;
		}

		public void add( final Node node ) {
		    children.add( node ) ;
		}

		public void depthFirst( final UnaryVoidFunction<Node> fn ) {
		    for (Node node : children) {
			node.depthFirst( fn ) ;
		    }
		}
	    } ;
	}
    }

    private void insertType( final Type type ) {
	final String packageName = type.packageName() ;
	final String[] packages = packageName.split( "[.]" ) ;
	final String className = type.className() ;

	// current is the List node onto which type is added.
	Node current = root ;
	for (String pkg : packages) {
	    if (current.children() == null)
		return ;

	    Node next = current.find( pkg ) ;
	    if (next == null) {
		next = Node.makeListNode( pkg ) ;
		current.add( next ) ;
	    }

	    current = next ;
	}

	// Current is the correct package, now add the type, if not
	// already present.
	Node classNode = current.find( className ) ;
	if (classNode == null) {
	    classNode = Node.makeTypeNode( className, type ) ;
	    current.add( classNode ) ;
	}
    }

    private void updateRoot() {
	if (root != null)
	    return ;

	root = Node.makeListNode( "" ) ;

	for (Type type : imports.values()) {
	    insertType( type ) ;
	}

	root.sort() ;
    }

    /** Return a list of imports as (packageName,className) pairs.
     * The list is sorted lexicographically.
     */
    public List<Pair<String,String>> getInOrderList() {
	if (sortedImports != null)
	    return sortedImports ;

	updateRoot() ;

	sortedImports = new ArrayList<Pair<String,String>>() ;

	UnaryVoidFunction<Node> fn = new UnaryVoidFunction<Node>() {
	    public void evaluate( Node node ) {
		Type type = node.type() ;
		if (type == null)
		    return ;

		Pair<String,String> pair =
		    new Pair<String,String>( type.packageName(), 
			type.className() ) ;

		sortedImports.add( pair ) ;
	    }
	} ;

	root.depthFirst( fn ) ;

	return sortedImports ;
    }
}
