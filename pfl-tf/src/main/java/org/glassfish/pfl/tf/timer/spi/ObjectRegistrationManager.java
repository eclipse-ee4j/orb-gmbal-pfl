/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi;


/**  Interface used to provide the capability to manage timer service objects.
 *
 * @author ken_admin
 */
public interface ObjectRegistrationManager {
    ObjectRegistrationManager nullImpl
        = new ObjectRegistrationManagerNOPImpl() ;

    /** Register obj at the root of the management tree.
     *
     * @param obj Object to register
     */
    void manage( Named obj ) ;

    /** Register obj as an immediate child of parent in the management tree.
     *
     * @param parent Parent object (already registered)
     * @param obj Object to register
     */
    void manage( Named parent, Named obj ) ;

    /** Remove obj from the management tree.
     *
     * @param obj Object to be removed from the management tree.
     */
    void unmanage( Named obj ) ;

}
