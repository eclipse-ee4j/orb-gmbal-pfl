/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package dynamic.copyobject ;

import java.io.File;
import java.io.FileInputStream;

public class TestClassLoader extends ClassLoader {
    public TestClassLoader( ClassLoader cl )
    {
	super( cl ) ;
    }

    public Class findClass(String name) throws ClassNotFoundException {

        try {
            String classfile = name.replace('.', '/') + ".class";

            String codebase =
                (String) System.getProperties().get("test.codebase");

            FileInputStream is = new FileInputStream(codebase + File.separator
                                                     + classfile);

            int available = is.available();
            byte[] b = new byte[available];
            is.read(b, 0, b.length);

            return defineClass(name, b, 0, b.length);

        } catch (Exception e) {
            ClassNotFoundException cnfe =
                new ClassNotFoundException("Failed to loadClass : " + name, e);
            throw cnfe;
        }
    }
}
