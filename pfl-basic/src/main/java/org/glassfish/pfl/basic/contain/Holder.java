/*
 * Copyright (c) 2004, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.contain ;

/** Type safe holder that can hold any non-primitive type.
 * Useful for out parameters and passing arguments that need
 * to be set later.
 */
public class Holder<T> 
{
    private transient T _content ;

    public Holder( final T content ) 
    {
	this._content = content ;
    }

    public Holder()
    {
	this( null ) ;
    }

    public T content()
    {
	return _content ;
    }

    public void content( final T content ) 
    {
	this._content = content ;
    }

    @Override
    public boolean equals( Object obj )
    {
	if (!(obj instanceof Holder)) {
            return false;
        }

	Holder other = Holder.class.cast( obj ) ;

	return _content.equals( other.content() ) ;
    }

    @Override
    public int hashCode()
    {
	return _content.hashCode() ;
    }

    @Override
    public String toString() 
    {
	return "Holder[" + _content + "]" ;
    }
}

