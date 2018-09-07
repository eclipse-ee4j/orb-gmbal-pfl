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

import java.util.List ;

/** Represents a node in the AST used to generate code.  All nodes support the
 * dynamic attribute facility.
 *
 * @author Ken Cavanaugh
 */
public interface Node extends AttributedObject {
    /** Return the Node that contains (and created) this Node.
    */
    Node parent() ;

    /** Return the unique ID of this node.  This starts at 1 and is incremented
     * for each new Node that is created.
     */
    int id() ;

    /** Set the parent to a new value.  Should only be called inside NodeBase.
     */
    void parent( Node node ) ;

    /** Return the first ancestor of this node of the given type, if any.
     * Throws IllegalArgumentException if not found.
     */
    <T extends Node> T getAncestor( Class<T> type ) ;

    /** Make a deep copy of this node.  If nn = n.copy(), then 
     * n.parent() == nn.parent(), which also means that the 
     * parent is NOT copied.
     */
    <T extends Node> T copy( Class<T> cls ) ;
    
    /** Copy setting a new parent in the result.
     */
    <T extends Node> T copy( Node newParent, Class<T> cls ) ;

    /** Accept the visitor and allow it to perform actions on this Node.
     */
    void accept( Visitor visitor ) ;
}
