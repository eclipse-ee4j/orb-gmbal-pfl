/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

module org.glassfish.pfl.dynamic {

    requires java.logging;
    // Required just by tests
    requires static java.rmi;

    requires org.glassfish.pfl.basic;
    requires org.objectweb.asm;
    requires org.objectweb.asm.util;

    exports org.glassfish.pfl.dynamic.codegen.impl;
    exports org.glassfish.pfl.dynamic.codegen.spi;
    exports org.glassfish.pfl.dynamic.copyobject.impl;
    exports org.glassfish.pfl.dynamic.copyobject.spi;
    exports org.glassfish.pfl.dynamic.generator;
}
