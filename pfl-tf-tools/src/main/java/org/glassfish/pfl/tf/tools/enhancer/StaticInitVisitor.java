/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 Payara Services Ltd.
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.tools.enhancer;

import org.glassfish.pfl.basic.contain.SynchronizedHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.pfl.tf.spi.EnhancedClassData;
import org.glassfish.pfl.tf.spi.MethodMonitorRegistry;
import org.glassfish.pfl.tf.spi.Util;
import org.objectweb.asm.Label;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;

/**
 *
 * @author ken
 */
public class StaticInitVisitor extends LocalVariablesSorter {
    private final Util util ;
    private final EnhancedClassData ecd ;

    public StaticInitVisitor( final int access, final String desc,
        final MethodVisitor mv, Util util, EnhancedClassData ecd ) {

        super( Opcodes.ASM7, access, desc, mv ) ;
        this.util = util ;
        this.ecd = ecd ;
        util.info( 2, "StaticInitVisitor created" ) ;
    }

    private LocalVariableNode defineLocal( MethodVisitor mv, String name, 
        Class<?> cls, Label start, Label end ) {

        Type type = Type.getType( cls ) ;
        int index = newLocal( type ) ;
        LabelNode snode = new LabelNode( start ) ;
        LabelNode enode = new LabelNode( end ) ;
        return new LocalVariableNode( name, type.getDescriptor(), null,
            snode, enode, index ) ;
    }

    private static final boolean ENABLED = false ;

    private void generateTraceMsg( MethodVisitor mv, String msg, int num ) {
        if (ENABLED && util.getDebug()) {
            final Label start = new Label() ;
            mv.visitLabel( start ) ;
            mv.visitLineNumber( num, start ) ;
            mv.visitFieldInsn( Opcodes.GETSTATIC, "java/lang/System", "out", 
                "Ljava/io/PrintStream;" ) ;
            mv.visitLdcInsn( msg );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "(Ljava/lang/String;)V", false);
        }
    }

    private static final boolean SHORT_FORM = true ;

    @Override
    public void visitCode() {
	if (SHORT_FORM) {
	    super.visitCode() ;
	    mv.visitLdcInsn( Type.getType( "L" + ecd.getClassName() + ";" ));
	    Type mmrType = Type.getType( MethodMonitorRegistry.class ) ;
	    String mdesc = "(Ljava/lang/Class;)V" ;
	    mv.visitMethodInsn( Opcodes.INVOKESTATIC,
		mmrType.getInternalName(), "registerClass", mdesc, false ) ;
	} else {
	    int line = 1 ;
	    util.info( 2, "StaticInitVisitor.visitCode" ) ;
	    super.visitCode() ;

	    Label start = new Label() ;
	    Label end = new Label() ;

	    mv.visitLabel( start ) ;

	    LocalVariableNode thisClass = defineLocal( mv, "thisClass",
		Class.class, start, end ) ;
	    LocalVariableNode mnameList = defineLocal( mv, "mnameList",
		List.class, start, end ) ;
	    LocalVariableNode holderMap = defineLocal( mv, "holderMap",
		Map.class, start, end ) ;

	    generateTraceMsg( mv, "initialize the holders", line++ ) ;
	    for (String str : ecd.getAnnotationToHolderName().values()) {
		generateTraceMsg( mv, "Generating to initialize holder " + str,
		    line++ ) ;
		util.info( 2, "Generating code to initialize holder " + str ) ;
		util.newWithSimpleConstructor( mv, SynchronizedHolder.class );
		mv.visitFieldInsn( Opcodes.PUTSTATIC,
		    ecd.getClassName(), str,
		    Type.getDescriptor(SynchronizedHolder.class ) ) ;
	    }

	    generateTraceMsg( mv, "Store the Class of this class", line++ );
	    mv.visitLdcInsn( Type.getType( "L" + ecd.getClassName() + ";" ));
	    mv.visitVarInsn( Opcodes.ASTORE, thisClass.index ) ;

	    generateTraceMsg( mv, "Create list of method names", line++ );
	    util.newWithSimpleConstructor( mv, ArrayList.class ) ;
	    mv.visitVarInsn( Opcodes.ASTORE, mnameList.index ) ;

	    for (String str : ecd.getMethodNames()) {
		util.info( 2, "Generating code to add " + str
                    + " to methodNames" ) ;
		mv.visitVarInsn( Opcodes.ALOAD, mnameList.index ) ;
		mv.visitLdcInsn( str );
		mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
		    "java/util/List", "add", "(Ljava/lang/Object;)Z", true );
		mv.visitInsn( Opcodes.POP ) ;
	    }

	    generateTraceMsg( mv,
		"create map from MM annotation class to Holder and init",
                line++ ) ;
	    util.newWithSimpleConstructor( mv, HashMap.class ) ;
	    mv.visitVarInsn( Opcodes.ASTORE, holderMap.index ) ;

	    for (Map.Entry<String,String> entry :
		ecd.getAnnotationToHolderName().entrySet()) {

		util.info( 2, "Generating code to put " + entry.getKey() + "=>"
		    + entry.getValue() + " into holderMap" ) ;

		mv.visitVarInsn( Opcodes.ALOAD, holderMap.index ) ;

		Type annoType = Type.getType( "L" + entry.getKey() + ";" ) ;
		mv.visitLdcInsn( annoType );

		mv.visitFieldInsn( Opcodes.GETSTATIC, ecd.getClassName(),
		    entry.getValue(),
		    Type.getDescriptor(SynchronizedHolder.class ) ) ;

		mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
		    "java/util/Map", "put",
		    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);

		mv.visitInsn( Opcodes.POP ) ;
	    }

	    generateTraceMsg( mv, "register with MethodMonitorRegistry",
                line++ ) ;
	    util.info( 2,
                "Generating code call MethodMonitorRegistry.registerClass" ) ;
	    mv.visitVarInsn( Opcodes.ALOAD, thisClass.index ) ;
	    mv.visitVarInsn( Opcodes.ALOAD, mnameList.index ) ;
	    mv.visitVarInsn( Opcodes.ALOAD, holderMap.index ) ;

	    Type mmrType = Type.getType( MethodMonitorRegistry.class ) ;
	    String mdesc =
                "(Ljava/lang/Class;Ljava/util/List;Ljava/util/Map;)V" ;
	    mv.visitMethodInsn( Opcodes.INVOKESTATIC,
		mmrType.getInternalName(), "registerClass", mdesc, false ) ;

	    mv.visitLabel( end ) ;

	    thisClass.accept( mv ) ;
	    mnameList.accept( mv ) ;
	    holderMap.accept( mv ) ;
	}
    }
}
