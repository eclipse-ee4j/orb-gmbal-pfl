/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen;

import junit.framework.Test;
import junit.framework.TestCase;
import org.glassfish.pfl.basic.func.NullaryFunction;
import org.glassfish.pfl.dynamic.codegen.lib.Constants;
import org.glassfish.pfl.dynamic.codegen.lib.EchoInt;
import org.glassfish.pfl.dynamic.codegen.test.Constants_gen;
import org.glassfish.pfl.dynamic.codegen.impl.ASMSetupVisitor;
import org.glassfish.pfl.dynamic.codegen.impl.Attribute;
import org.glassfish.pfl.dynamic.codegen.impl.ClassGeneratorImpl;
import org.glassfish.pfl.dynamic.codegen.impl.CurrentClassLoader;
import org.glassfish.pfl.dynamic.codegen.impl.Node;
import org.glassfish.pfl.dynamic.codegen.impl.NodeBase;
import org.glassfish.pfl.dynamic.codegen.impl.TreeWalkerContext;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo;
import org.glassfish.pfl.dynamic.codegen.spi.GenericClass;
import org.glassfish.pfl.dynamic.codegen.spi.Type;
import org.glassfish.pfl.dynamic.copyobject.spi.DefaultCopier;
import org.glassfish.pfl.dynamic.TestCaseTools;
import org.junit.Ignore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.*;

/**
 * Test for the ASM-based codegen library.
 * Initial steps to test:
 * <OL>
 * <LI>Test the _DImpl_Tie_gen code
 * <LI>Test the EJB3 code
 * </OL>
 * There will be more tests later.  Here we need to test
 * two cases:
 * <OL>
 * <LI>Generate Java source code, compile it, load it.
 * <LI>Generate Bytecode directly, load it.
 * <LI>
 * These will be unified behind a simple CodeGenerator interface.
 * All tests will be written at the Wrapper level.
 * <p>
 * Other required tests:
 * <UL>
 * <LI>ClassInfo/MethodInfo for Classes
 * <LI>Type
 * <LI>Signature
 * <LI>Dynamic attributes
 * <LI>Method overload resolution
 * </UL>
 */
@Ignore("Some strange things are happening here, and it is far from clear that the code tested by this is even used by anyone")
public class ClientTest extends TestCase {
    private static final boolean DEBUG = false;

    // Make sure that ControlBase is loaded in the ClassLoader
    // that loaded Client, otherwise it could first be
    // loaded in a TestClassLoader (see GenerationTestSuiteBase)
    // rather than in the parent to which TestClassLoader delegates.
    private static final Object cb = new ControlBase();

    public ClientTest() {
    }

    public ClientTest(String name) {
        super(name);
    }

    private ClassLoader makeClassLoader() {
        ClassLoader cl = new GenerationTestSuiteBase.TestClassLoader(this.getClass().getClassLoader());
        return cl;
    }

    private ClassInfo getClassInfo(SimpleCodeGenerator cg) {
        ClassLoader cl = makeClassLoader();
        Class<?> cls = cg.generate(cl);
        if (DEBUG) cg.reportTimes();
        assertNotNull(cls);
        Type type = Type.type(cls);
        return type.classInfo();
    }

    public void testEJBRemoteInterface() {
        ClassGeneratorFactory myRemoteFactory =
                ClassGeneratorFactoryRegistry.get("MyRemote");

        JavaCodeGenerator jgen = new JavaCodeGenerator(myRemoteFactory);
        ClassInfo jci = getClassInfo(jgen);

        ByteCodeGenerator bgen = new ByteCodeGenerator(myRemoteFactory,
                GenerationTestSuiteBase.getByteCodeGenerationProperties(DEBUG));
        ClassInfo bci = getClassInfo(bgen);

        assertEquals(jci, bci);
    }

    public void testEJBAdapterSource() {
        JavaCodeGenerator gen = new JavaCodeGenerator(
                ClassGeneratorFactoryRegistry.get("MyRemote__Adapter"));
        Class<?> cls = gen.generate(makeClassLoader());
        if (DEBUG) gen.reportTimes();
        assertNotNull(cls);
    }

    public void testConstantGeneration() {
        ClassGeneratorFactory constantsFactory =
                ClassGeneratorFactoryRegistry.get("ConstantsImpl");

        constantsFactory.evaluate();

        GenericClass<Constants> genClass = null;

        try {
            genClass = _generate(Constants.class,
                    GenerationTestSuiteBase.getByteCodeGenerationProperties(DEBUG));
        } catch (Exception exc) {
            fail("Unexpected exception " + exc + " in _generate for Constants");
        }

        Constants constants = genClass.create();

        for (Method m : constants.getClass().getDeclaredMethods()) {
            String name = m.getName();
            int expectedValue = Constants_gen.getValue(name);

            try {
                int actualValue = (Integer) m.invoke(constants);
                assertEquals(expectedValue, actualValue);
            } catch (Exception exc) {
                fail("Unexpected exception " + exc + " in call to " + m);
            }
        }
    }

    public static class DynamicAttributeTestSuite extends TestCase {
        public DynamicAttributeTestSuite() {
            super();
        }

        public DynamicAttributeTestSuite(String name) {
            super(name);
        }

        // Declare some attributes of different types
        private static final Attribute<String> foo =
                new Attribute<String>(String.class, "foo", "");

        private static final Attribute<Integer> bar =
                new Attribute<Integer>(Integer.class, "bar", 1);

        private interface StringList extends List<String> {
        }

        private static class StringArrayList extends ArrayList<String>
                implements StringList {

            public StringArrayList() {
                super();
            }

            public StringArrayList(List<String> list) {
                super(list);
            }
        }

        private static final NullaryFunction<StringList> rgbl =
                new NullaryFunction<StringList>() {
                    public StringList evaluate() {
                        return new StringArrayList(Arrays.asList(
                                "red", "blue", "green"));
                    }
                };

        private static final Attribute<Integer> notUsed =
                new Attribute<Integer>(Integer.class, "notUsed", 0);

        private static final Attribute<StringList> baz =
                new Attribute<StringList>(StringList.class, "baz", rgbl);

        // Create a single node, set/get the attributes
        public void testSimpleNode() {
            Node node = new NodeBase(null);
            assertEquals(foo.get(node), "");
            assertEquals(bar.get(node), Integer.valueOf(1));
            assertEquals(baz.get(node), rgbl.evaluate());

            foo.set(node, "Raining");
            assertEquals(foo.get(node), "Raining");

            bar.set(node, 42);
            assertEquals(bar.get(node), new Integer(42));

            StringList tval = new StringArrayList(Arrays.asList(
                    "yellow", "orange"));
            baz.set(node, tval);
            assertEquals(baz.get(node), tval);

            Set<Attribute<?>> expectedAttrs =
                    new HashSet<Attribute<?>>();
            expectedAttrs.add(foo);
            expectedAttrs.add(bar);
            expectedAttrs.add(baz);

            assertEquals(expectedAttrs,
                    Attribute.getAttributes(node));

            // GenerationTestSuiteBase.displayNode( "Simple node", node ) ;
        }

        // Create a node with a delegate, set/get the attrs
        public void testNodeWithDelegate() {
            // Create node1, set some attributes
            NodeBase node1 = new NodeBase(null);
            foo.set(node1, "Raining");
            bar.set(node1, 42);

            // Copy node1 and then set some more attributes
            NodeBase node2 = DefaultCopier.copy(node1, NodeBase.class);
            bar.set(node2, 13);
            StringList tval = new StringArrayList(Arrays.asList(
                    "yellow", "orange"));
            baz.set(node2, tval);

            // make sure that we get the correct value for bar
            assertEquals(foo.get(node2), "Raining");
            assertEquals(bar.get(node2), Integer.valueOf(13));
            assertEquals(baz.get(node2), tval);

            // set bar on node1 to a different value
            bar.set(node1, 52);
            assertEquals(bar.get(node2), Integer.valueOf(13));

            // set bar on node2 to a different value
            bar.set(node2, 137);
            assertEquals(bar.get(node2), Integer.valueOf(137));
            assertEquals(bar.get(node1), Integer.valueOf(52));

            // GenerationTestSuiteBase.displayNode( "Delegating node", node2 ) ;
        }
    }

    public void testClassInfo() {
        // This works by first creating a simple class using the
        // framework and the java compiler, then getting ClassInfo
        // from the resulting class and comparing the ClassInfo
        // from the ClassGeneratorImpl with the ClassInfo from the
        // generated Class.
        ClassLoader cl = makeClassLoader();
        CurrentClassLoader.set(cl);
        JavaCodeGenerator gen = new JavaCodeGenerator(
                ClassGeneratorFactoryRegistry.get("MyRemote"));
        Class<?> cls = gen.generate(cl);
        if (DEBUG) gen.reportTimes();
        assertNotNull(cls);

        ClassInfo cinfo = _classGenerator();
        ClassInfo clinfo = null;

        try {
            clinfo = Type.type(cls).classInfo();
        } catch (Exception exc) {
            fail("Caught exception " + exc);
        }

        // While the last assertEquals( cinfo, clinfo ) implies
        // the other statements, leave this as is so that
        // an equals failure is easier to diagnose.
        assertEquals(cinfo.thisType(), clinfo.thisType());
        assertEquals(cinfo.isInterface(), clinfo.isInterface());
        assertEquals(cinfo.modifiers(), clinfo.modifiers());
        assertEquals(cinfo.name(), clinfo.name());
        assertEquals(cinfo.superType(), clinfo.superType());
        assertEquals(cinfo.impls(), clinfo.impls());
        assertEquals(cinfo.fieldInfo(), clinfo.fieldInfo());
        assertEquals(cinfo.methodInfoByName(), clinfo.methodInfoByName());
        assertEquals(cinfo.constructorInfo(), clinfo.constructorInfo());
        assertEquals(cinfo.thisType(), clinfo.thisType());
        assertEquals(cinfo, clinfo);
    }

    // 4. Validate ASMSetupVisitor before trying it out result
    //    on ASMByteCodeVisitor
    //
    public void testASMSetupVisitor() {
        _clear();
        ClassGeneratorFactory generator =
                ClassGeneratorFactoryRegistry.get("MyRemote");
        ClassLoader cl = makeClassLoader();
        CurrentClassLoader.set(cl);
        ClassGeneratorImpl cgen = (ClassGeneratorImpl) generator.evaluate();

        // GenerationTestSuiteBase.displayNode(
        //     "Dump of _DImpl_Tie AST:", cgen ) ;

        TreeWalkerContext twc = new TreeWalkerContext();
        ASMSetupVisitor visitor = new ASMSetupVisitor(twc);
        cgen.accept(visitor);

        // GenerationTestSuiteBase.displayNode(
        //     "Dump of _DImpl_Tie AST after ASMSetupVisitor:", cgen ) ;

        twc = new TreeWalkerContext();
        visitor = new ASMSetupVisitor(twc, ASMSetupVisitor.Mode.VERIFY);
        cgen.accept(visitor);
        List<ASMSetupVisitor.ErrorReport> errors = visitor.getVerificationErrors();
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder("failed:");
            for (ASMSetupVisitor.ErrorReport report : errors) {
                sb.append("\n  Error on ").append(report.node).append(":").append(report.msg);
            }
            fail(sb.toString());
        }
    }

    public static abstract class DefaultPackageTestSuiteBase extends GenerationTestSuiteBase {
        private Class<?> testClass;
        private EchoInt echo;

        private void init() {
            testClass = getClass("DefaultPackageTest");
            try {
                Object obj = testClass.newInstance();
                echo = EchoInt.class.cast(obj);
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
        }

        public DefaultPackageTestSuiteBase(boolean gbc, boolean debug) {
            super(gbc, debug);
            init();
        }

        public DefaultPackageTestSuiteBase(String name, boolean gbc, boolean debug) {
            super(name, gbc, debug);
            init();
        }

        public void testInvoke() {
            assertEquals(echo.echo(3), 3);
        }
    }

    // Test code generation by generating source code and compiling it.
    // This is mainly to validate the test itself, but also provides additional
    // testing of the source code generation process.
    public static class DefaultPackageTestSuite extends DefaultPackageTestSuiteBase {
        public DefaultPackageTestSuite(String name) {
            super(name, false, DEBUG);
        }

        public DefaultPackageTestSuite() {
            super(false, DEBUG);
        }
    }

    // The main test suite for code generation.  This tests all of the different
    // patterns of code generation.
    public static class BytecodeGenerationDefaultPackageTestSuite extends DefaultPackageTestSuiteBase {
        public BytecodeGenerationDefaultPackageTestSuite(String name) {
            super(name, true, DEBUG);
        }

        public BytecodeGenerationDefaultPackageTestSuite() {
            super(true, DEBUG);
        }
    }

    // Bytecode generator testing (and work list)
    //
    // There is a lot that needs testing here.  One test is simply
    // to make sure that the main test samples generate valid
    // bytecode.  Other detailed tests are needed as well.
    //
    // 1. Test that MyRemote_gen produces a valid interface.  This 
    //    is quite simple:
    //	  1. generate the AST, producing a ClassGeneratorImpl
    //	  2. generate bytecode from the AST
    //	  3. load the bytecode and get its ClassInfo
    //	  4. verify that the ClassGeneratorImpl and the ClassInfo are equal
    //	  DONE
    //
    // For specific features, we put one feature per method in a generated class.
    // The general test strategy then is:
    //	  1. generate the AST for the test class, generate bytecode, load it.
    //	  2. Invoke the methods in the generated class reflectively, validating
    //	     their behavior.
    // We need the following test methods:
    // 1. Simple expressions that return a value
    //	  1. Return a constant of each possile type
    //	  2. Return this
    //	  3. Return the result of a static method call
    //	  4. Return the result of a non-static method call (check cases here)
    //	  5. boolean testUnaryNot( boolean arg ) { return !arg ; }
    //	  6. boolean testBinaryXX( int arg1, int arg2 ) { return arg1 OPXX arg2 ; }
    //	     for each relational operator
    //	  7. boolean testEQU( int arg1, int arg2 ) (and similarly for Object)
    //	  8. boolean testNE similar to testEQU
    //	  9. boolean testAND( boolean arg1, boolean arg2 ) 
    //	     { return eval( 1, arg1 ) && eval( 2, arg2 ) ; }
    //	     (This tests that && is properly short circuited, that is,
    //	      that eval( 2, arg2 ) is only called if arg1 is false.
    //	      eval is a method in a base class that records the first arg
    //	      and echoes the second).
    // 2. Test cast expression, both successful and not successful.
    // 3. Test instanceof, both successful and not.
    // 4. Test various flavors of method calls and constructors
    //	  1. new Class( args ) 
    //	  2. new Class[size]
    //	  3. new Class[] { args }
    //	  4. super.method( args ) 
    //	  5. super( args ) in constructor
    //	  6. this( args ) in constructor
    //	  7. Class.method( args ) (static)
    //	  8. obj.method( args ) (virtual)
    //	  9. this.method( args ) (in current class to private method)
    // 5. obj.field in expression
    // 6. obj.field = expr
    // 7. Class.field in expression
    // 8. Class.field = expr
    // 9. arr[index] = expr
    // 10. arr[index] in expression
    // 11. if statement 
    //     DONE
    // 12. if/else statement 
    //	   DONE
    // 13. if {} else if {} else {} statement
    //	   DONE
    // 14. nested if statements
    //	   DONE
    // 15. while ... do {} 
    // 16. switch variants:
    //     1. no default, dense branches
    //     2. no default, sparse branches
    //     3. default, dense branches
    //     4. default, sparse branches
    //     Multiple branches including fall through cases
    // 17. try statements:
    //	   1. simple try {} catch () {}
    //	   DONE
    //	   2. try {} catch () {} catch () {} 
    //	   3. try {} finally {}
    //	   4. try {} catch () {} finally {}
    //	   DONE
    //	   5. try {} catch () {} catch () {} finally {}
    //	   6. nested try/catch/finally
    // 18. non-local control transfers using return and break, particularly with
    //     respect to handling finally blocks, and nested finally blocks
    //     (Need to add break to framework).
    // 19. Get MyRemote__Adapter working as soon as possible, before all of the 
    //     above tests are complete.
    //     - create an implementation of MyRemote using a dynamic proxy to
    //       implement the doSomething, doSomethingElse, and echo methods
    //     - There are several cases: return, return value, throw RemoteException,
    //       throw app exception for each method.
    //     DONE
    // 20. Use _DImpl_Tie test once most of the above is complete.
    // 21. Work on some sort of simple method overload resolution
    //     - Added method(s) on Type to determine if two types are related by
    //       method invocation conversion.
    //     - Implement Signature methods that compute signatures for calls.
    //     - Basic idea: only allow calls if there is exactly one method
    //       that has the same number of args, is accessible, and has all
    //       types related my method invocation conversion.  If multiple,
    //       user must supply signature.
    // 22. test static initializers (note: need to move this to a <clinit> method
    //     in the framework).  Need to support merging of multiple static 
    //     initializers (or maybe not).  Change _initializer to _static
    //     (generally make things look as much as possible like Java).
    // 
    // Packaging questions.
    // 1. Will we use codegen for new rmic?  (yes, I think)
    // 2. Will we use direct bytecode generation in rmic? (no or optional)
    // 3. Package framework except for bytecode visitor classes
    //    (ASM*, ByteCodeUtility, MyLabel, EmitterFactory) in the optional
    //    branch, rest moves into core?
    // 4. Need to refactor a bit for this.

    /**
     * Test code generation by generating source code and compiling it. This is mainly to validate the test itself,
     * but also provides additional testing of the source code generation process.
     * <p>
     * public static class EJBAdapterSourceTestSuite extends EJBAdapterTestSuiteBase {
     * public EJBAdapterSourceTestSuite() {
     * super("MyRemote__Adapter", false, false);
     * }
     * }
     * /
     **/

    // Test code generation by generating byte code directly.
    public static class EJBAdapterBytecodeTestSuite extends EJBAdapterTestSuiteBase {
        public EJBAdapterBytecodeTestSuite(String name) {
            super("MyRemote__Adapter", name, true, DEBUG);
        }

        public EJBAdapterBytecodeTestSuite() {
            super("MyRemote__Adapter", true, DEBUG);
        }
    }

    /**
     * Test code generation by generating source code and compiling it. This is mainly to validate the test itself,
     * but also provides additional testing of the source code generation process.
     * <p>
     * public static class EJBAdapterSimplifiedSourceTestSuite extends EJBAdapterTestSuiteBase {
     * public EJBAdapterSimplifiedSourceTestSuite(String name) {
     * super("MyRemote__Adapter_Simplified", name, false, DEBUG);
     * }
     * <p>
     * public EJBAdapterSimplifiedSourceTestSuite() {
     * super("MyRemote__Adapter_Simplified", false, DEBUG);
     * }
     * }
     * /
     **/

    // Test code generation by generating byte code directly.  
    public static class EJBAdapterSimplifiedBytecodeTestSuite extends EJBAdapterTestSuiteBase {
        public EJBAdapterSimplifiedBytecodeTestSuite(String name) {
            super("MyRemote__Adapter_Simplified", name, true, DEBUG);
        }

        public EJBAdapterSimplifiedBytecodeTestSuite() {
            super("MyRemote__Adapter_Simplified", true, DEBUG);
        }
    }

    public static Test suite() {
        return TestCaseTools.makeTestSuite(ClientTest.class);
    }
}
