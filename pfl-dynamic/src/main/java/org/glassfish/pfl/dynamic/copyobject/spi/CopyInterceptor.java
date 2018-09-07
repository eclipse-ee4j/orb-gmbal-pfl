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

/** Interface used to control copyobject behavior.
 * The preCopy method is invoked on the original object BEFORE that
 * copy is constructed, allowing the original to be prepared for
 * copying.  The postCopy method is invoked on the copy
 * AFTER the copy is completed, allowing the copy to be completed
 * as needed.
 */
public interface CopyInterceptor {
    void preCopy() ;

    void postCopy() ;
}
