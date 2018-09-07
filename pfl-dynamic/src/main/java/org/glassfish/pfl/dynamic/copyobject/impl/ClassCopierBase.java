/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl ;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException;

import java.util.HashMap;
import java.util.Map;

/** A convenient base class for making ClassCopier types.
 * This takes care of checking oldToNew and updating oldToNew
 * when an actual copy is made.  All subclasses must override
 * createCopy, which allocates a new result.  In some simple
 * cases, this is all that is needed.  In the more complex
 * cases, doCopy must also be overridden to make the actual copy.
 */
public abstract class ClassCopierBase implements ClassCopier {
    private String name ;
    private boolean isReflective ;

    /** Pass a name here that can be used for toString, hashCode, and equals.
     * All different ClassCopier classes derived from this base should
     * have unique names.
     */
    protected ClassCopierBase( String name ) 
    {
	this( name, false ) ;
    }

    protected ClassCopierBase( String name, boolean isReflective )
    {
	this.name = name ;
	this.isReflective = isReflective ;
    }

    // Implement toString() and equals() as debugging and testing aids.
    // Implement hashCode() to satisfy the general contracts of equals and
    // hash.

    @Override
    public final String toString()
    {
	return "ClassCopier[" + name + "]" ;
    }

    @Override
    public final int hashCode()
    {
	return name.hashCode() ;
    }

    @Override
    public final boolean equals( Object obj )
    {
	if (this == obj) {
            return true;
        }

	if (!(obj instanceof ClassCopierBase)) {
            return false;
        }

	ClassCopierBase other = (ClassCopierBase)obj ;

	return name.equals( other.name ) && (isReflective == other.isReflective) ;
    }

    /** Make the actual copy of source, using oldToNew to preserve aliasing.
     * This first checks to see whether source has been previously copied.
     * If so, the value obtained from oldToNew is returned.  Otherwise,
     * <ol>
     * <li>createCopy( source ) is called to create a new copy of source.
     * <li>The new copy is placed in oldToNew with source as its key.
     * <li>doCopy is called to complete the copy.
     * </ol>
     *
     * This split into two phases isolates all subclasses from the need to
     * update oldToNew. It accommodates simple cases (arrays of primitives
     * for example) that only need to define createCopy, as well as more complex
     * case (general objects) that must first create the copy, update oldToNew,
     * and then do the copy, as otherwise self-references would cause 
     * infinite recursion.
     */
    @Override
    public final Object copy(Map<Object, Object> oldToNew, Object source) throws ReflectiveCopyException {
        Object result = oldToNew.get(source);
        if (result == null) {
            result = doSpecialCaseCopy(oldToNew, source);
        }
        if (result == null) {
            result = doByFieldCopy(oldToNew, source);
        }

        return result;
    }

    /**
     * Sometimes the internal representation of an object will be violated by our field-by-field copy. This method
     * handles those special cases. It suggests that there may be a basic problem with our code logic.
     * @param oldToNew a map of already copied objects and their corresponding copies.
     * @param source the object to copy.
     */
    private Object doSpecialCaseCopy(Map<Object, Object> oldToNew, Object source) {
        Object result = null;

        if (isEmptyHashMap(source))
            result = cloneEmptyHashMap();

        if (result != null)
            oldToNew.put(source, result);
        return result;
    }

    /**
     * As of JDK 1.7-40, the JDK relies on the HashMap#table field being identical to a constant. That will not be
     * true if we invoke #doByFieldCopy to copy it, so we need special processing.
     * @param source the object to copy.
     */
    private boolean isEmptyHashMap(Object source) {
        return source instanceof HashMap && ((HashMap) source).isEmpty();
    }

    private HashMap cloneEmptyHashMap() {
        return new HashMap();
    }

    private Object doByFieldCopy(Map<Object, Object> oldToNew, Object source) {
        Object result;
        try {
            result = createCopy(source);
            oldToNew.put(source, result);
            result = doCopy(oldToNew, source, result);
        } catch (StackOverflowError ex) {
            throw Exceptions.self.stackOverflow(source, ex);
        }
        return result;
    }

    @Override
    public boolean isReflectiveClassCopier()
    {
	return isReflective ;
    }

    /** Create a copy of source.  The copy may or may not be fully
     * initialized.  This method must always be overridden in a 
     * subclass.
     */
    protected abstract Object createCopy( 
	Object source ) throws ReflectiveCopyException ;

    /** Do the copying of data from source to result.
     * This just returns the result by default, but it may be overrideden 
     * in a subclass.  When this method completes, result must be fully
     * initialized.
     */
    protected Object doCopy( Map<Object,Object> oldToNew,
	Object source, Object result ) throws ReflectiveCopyException
    {
	return result ;
    }
}
