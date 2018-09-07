/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.tools.file ;

public class StringParser {
    private String data ;
    private int pos ;
    private char current ;

    public StringParser( String str ) {
        if (str.length() == 0) {
            throw new RuntimeException("Empty string not allowed");
        }

        this.data = str ;
        this.pos = 0 ;
        this.current = str.charAt( pos ) ;
    }

    private void setPos( int newPos ) {
        if (newPos < data.length() ) {
            pos = newPos ;
            current = data.charAt( newPos ) ;
        }
    }

    private boolean next() {
        if (data.length() > pos) {
            setPos( pos + 1 ) ;
            return true ;
        } else {
            return false ;
        }
    }

    /** skip everything until str is found.  Returns true if found, otherwise
     * false.
     * @param str String for which we are looking
     * @return whether or not str was found
     */
    public boolean skipToString( String str ) {
        int index = data.indexOf( str ) ;
        if (index >= 0) {
            setPos( index ) ;
            return true ;
        } else {
            return false ;
        }
    }

    /** skip over str, if str is at the current position.
     * @param str to skip (must be at current position)
     * @return whether or not str was at current position
     */
    public boolean skipString( String str ) {
        String cstr = data.substring( pos, pos+str.length() ) ;
        if (cstr.equals( str )) {
            setPos( pos+str.length() ) ;
            return true ;
        } else {
            return false ;
        }
    }

    /** Skip over whitespace.  Returns true if some whitespace skipped.
     * @return whether some whitespace was skipped.
     */
    public boolean skipWhitespace() {
        boolean hasSkipped = false ;
        while (Character.isWhitespace(current)) { 
            hasSkipped = true ;
            if (!next()) {
                break ;
            }
        }

        return hasSkipped ;
    }

    /** Return int matched at current position as a string.
     */
    public String parseInt() {
        int first = pos ;
        boolean atStart = true ;
        while ((current >= '0') && (current <= '9')) {
            atStart = false ;
            if (!next()) {
                break ;
            }
        }

        if (atStart) {
            return null ;
        } else {
            return data.substring( first, pos ) ;
        }
    }
}
