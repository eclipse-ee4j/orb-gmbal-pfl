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
import java.util.ArrayList ;
import java.lang.Math ;

import org.glassfish.pfl.dynamic.copyobject.spi.Copy ;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyInterceptor ;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyType ;

/** An implementation of the AttributedObject interface.  This implementation
 * supports lazy copying of AttributedObject instances.
 */
public class AttributedObjectBase implements AttributedObject, CopyInterceptor {
    // Copying AttributedObjectBase is complicated, because we want to make
    // sure that after the copy, source and dest have the same attributes,
    // but whenever an attribute is set either in the source or the dest, it
    // is not visible to the other node.  We do this by making both the
    // source and the dest point to the same delegate, which carries the 
    // attributes originally present in the source.  This happens in 3 parts:
    // 1. The preCopy method is invoked, which introduces a new delegate in the
    //    source object, which carries the sources old delegate and attributes.
    // 2. During the copy, the destination object's delegate points to the source object.
    // 3. After the copy, the destination object's delegate is moved to the source 
    //    object's delegate in the postCopy method.
    // At this point, the source and dest share the same delegate, which contains the
    // original attributes of the source object.  Any set calls on either the 
    // source or dest will allocate a new attributes objects, which will be used for
    // all attribute setting.

    // This method is invoked on the source before the copy.  If the 
    // source has attributes, this causes the source to delegate to a new
    // AttributedObjectBase object which has the attributes that the source
    // originally had.  The source ends up having a delegate but no attributes.
    // If it also had a delegate originally, the new delegate delegates to the old
    // delegate.
    public void preCopy() {
	if (attributes != null) {
	    AttributedObjectBase base = new AttributedObjectBase() ;
	    base.delegate = this.delegate ;
	    this.delegate = base ;
	    base.attributes = this.attributes ;
	    this.attributes = null ;
	}
    }

    // This method is invoked on the result of the copy after all fields have been
    // copied.  If the result has a delegate, the delegate points to the source,
    // but we really want to point to the source's delegate, thus avoiding any conflict
    // between the source and the result setting attribute values.
    public void postCopy() {
	if (delegate != null)
	    delegate = delegate.delegate ;
    }

    @Copy(CopyType.SOURCE) // delegate is set to the source object in the copy
    private AttributedObjectBase delegate = null ;

    @Copy(CopyType.NULL) // attributes is set to null in the copy.
    private ArrayList<Object> attributes = null ;
    
    // The current dynamic attribute implementation is chosen
    // for simplicity and maximum possible performance in a simple case, 
    // not robust performance.  It is intended
    // to handle O( 10-20 ) attributes on a few thousands of AttributedObjects.
    // Obviously we can end up with very sparsely
    // occupied ArrayLists as more attributes are added.
    // This implementation is probably adequate for the codegen
    // library's needs, but a more efficient representation
    // (like a compressed bitmap index into an array of the
    // number of attributes set) could be needed if there are
    // hundreds or thousands of attributes on thousands of nodes.
    private void ensure( int index ) {
	if (attributes == null) {
	    attributes = new ArrayList( index+1 ) ;
	} else {
	    attributes.ensureCapacity( index+1 ) ;
	}

	// Make sure that any new elements of attributes
	// are set to null, otherwise set/get will throw
	// an IndexOutOfBounds exception.
	for (int ctr=attributes.size(); ctr<=index; ctr++) {
	    attributes.add( null ) ;
	}
    }

    public final Object get( int index ) {
	ensure( index ) ;
	Object result = null ;
	if (attributes != null)
	    result = attributes.get(index) ;

	if ((result == null) && (delegate != null))
	    result = delegate.get( index ) ;

	return result ;
    }

    public final void set( int index, Object obj ) {
	ensure( index ) ;
	attributes.set( index, obj ) ;
    }

    public final List<Object> attributes() {
	List<Object> delAttrs = null ;
	if (delegate != null)
	    delAttrs = delegate.attributes() ;

	if (delAttrs == null) {
	    return attributes ;
	} else {
	    int len = Math.max( attributes.size(), delAttrs.size() ) ;
	    List<Object> result = new ArrayList<Object>( len ) ;
	    for (int ctr=0; ctr<len; ctr++)
		result.add( null ) ;

	    for (int ctr=0; ctr<len; ctr++) {
		Object attrElem = null ;
		if (ctr < attributes.size()) {
		    attrElem = attributes.get(ctr) ;
		}

		Object delAttrElem = null ;
		if (ctr < delAttrs.size()) {
		    delAttrElem = delAttrs.get(ctr) ;
		}

		if (attrElem != null) {
		    result.set( ctr, attrElem ) ;
		} else if (delAttrElem != null) {
		    result.set( ctr, delAttrElem ) ;
		} // else this entry should be null, and already is.
	    }

	    return result ;
	}
    }
}
