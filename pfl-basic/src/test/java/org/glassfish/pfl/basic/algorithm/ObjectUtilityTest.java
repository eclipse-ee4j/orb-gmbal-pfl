/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.algorithm;

import org.glassfish.pfl.basic.testobjects.SerializableClass2;
import org.glassfish.pfl.basic.testobjects.TestObjects;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

public class ObjectUtilityTest {
    @Test
    public void whenObjectNotInJdk_displayFields() throws Exception {
        SerializableClass2 anObject = new SerializableClass2();
        String string = ObjectUtility.defaultObjectToString(anObject);

        assertThat(string, stringContainsInOrder(values("aNumber=", "java.lang.Integer", Integer.toString(TestObjects.INT_FIELD_VALUE))));
        assertThat(string, stringContainsInOrder(values("aDouble=", "java.lang.Double", "0.0")));
        assertThat(string, stringContainsInOrder(values("aLong=", "java.lang.Long", "0")));
    }

    private Iterable<String> values(String... strings) {
        return Arrays.asList(strings);
    }

    @Test
    public void whenObjectInJdk_displayFields() throws Exception {
        String string = ObjectUtility.defaultObjectToString(System.out);

        assertThat(string, stringContainsInOrder(values("autoFlush=", "true")));
        assertThat(string, stringContainsInOrder(values("formatter=", "null")));
        assertThat(string, stringContainsInOrder(values("maxBytesPerChar=", "java.lang.Float", "3.0")));
    }
}
