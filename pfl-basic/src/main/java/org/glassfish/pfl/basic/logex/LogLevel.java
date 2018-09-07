/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.logex;

import java.util.logging.Level;

/** Enum corresponding to java.util.logging.Level that can be used in annotations.
 *
 * @author ken
 */
public enum LogLevel {
    CONFIG() {
        @Override
        public Level getLevel() { return Level.CONFIG ; }
    },

    FINE {
        @Override
        public Level getLevel() { return Level.FINE ; }
    },

    FINER {
        @Override
        public Level getLevel() { return Level.FINER ; }
    },

    FINEST {
        @Override
        public Level getLevel() { return Level.FINEST ; }
    },

    INFO {
        @Override
        public Level getLevel() { return Level.INFO ; }
    },

    SEVERE {
        @Override
        public Level getLevel() { return Level.SEVERE ; }
    },

    WARNING {
        public Level getLevel() { return Level.WARNING ; }
    } ;

    public abstract Level getLevel() ;
}
