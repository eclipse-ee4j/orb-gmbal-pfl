/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.spi ;

/** Enum used with Copy annotation on fields to indicate
 * special handling by the object copier.  In what follows,
 * the source is the object being copied, and the result 
 * is the resulting copy.  field is the name of the annotated
 * field.
 */
public enum CopyType {
    /** Standard behavior: apply standard copier to the value
     * of the field.  This is the same thing that happens
     * if the field is not annotated.
     */
    RECURSE,

    /** Set result.field = source.field without copying
     */
    IDENTITY,

    /** Set result.field = null or 0 according to type.
     */
    NULL,

    /** Set result.field = source.  Requires that the declared
     * type of field is assignment compatible with the type of
     * the object; that is, field.class.isAssignableFrom( this.class ).
     */
    SOURCE,

    /** Set result.field = result.  Requires that the declared
     * type of field is assignment compatible with the type of
     * the object; that is, field.class.isAssignableFrom( this.class ).
     */
    RESULT
}
