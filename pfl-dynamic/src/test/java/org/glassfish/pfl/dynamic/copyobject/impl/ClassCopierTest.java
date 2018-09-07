/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.copyobject.impl;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ClassCopierTest {

    private PipelineClassCopierFactory factory = new ClassCopierFactoryPipelineImpl();
    private HashMap<Object, Object> map = new HashMap<Object, Object>();

    /**
     * Verify fix for https://java.net/jira/browse/GLASSFISH-20814
     */
    @Test
    public void afterCopyingHashMap_mayAddEntries() {
        HashMap<Object, Object> original = new HashMap<Object, Object>();
        ClassCopierOrdinaryImpl copier = new ClassCopierOrdinaryImpl(factory, HashMap.class);
        Object copy = copier.copy(map, original);

        toHashMap(copy).put("a", "b");
    }

    @SuppressWarnings("unchecked")
    private static HashMap<Object, Object> toHashMap(Object copy) {
        return (HashMap<Object, Object>) copy;
    }

    @Test
    public void afterCopyingHashMap_mapHasCachedResult() {
        HashMap<Object, Object> original = new HashMap<Object, Object>();
        ClassCopierOrdinaryImpl copier = new ClassCopierOrdinaryImpl(factory, HashMap.class);
        Object copy = copier.copy(map, original);

        assertEquals(copy, map.get(original));
    }

}
