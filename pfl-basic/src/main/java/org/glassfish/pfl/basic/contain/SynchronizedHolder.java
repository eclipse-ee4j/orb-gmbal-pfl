/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
public class SynchronizedHolder<T> 
{
    private T _content ;

    public SynchronizedHolder( T content ) 
    {
	this._content = content ;
    }

    public SynchronizedHolder()
    {
	this( null ) ;
    }

    public synchronized T content()
    {
	return _content ;
    }

    public synchronized void content( T content ) 
    {
	this._content = content ;
    }

    @Override
    public synchronized boolean equals( Object obj )
    {
	if (!(obj instanceof SynchronizedHolder)) {
            return false;
        }

	SynchronizedHolder other = SynchronizedHolder.class.cast( obj ) ;

        if (_content == null) {
            return other.content() == null ;
        } else  {
            return _content.equals( other.content() ) ;
        }
    }

    @Override
    public synchronized int hashCode()
    {
	return _content.hashCode() ;
    }

    @Override
    public synchronized String toString() 
    {
	return "SynchronizedHolder[" + _content + "]" ;
    }
}

