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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import org.glassfish.pfl.basic.contain.SynchronizedHolder ;
import org.glassfish.pfl.tf.spi.EnhancedClassData;
import org.glassfish.pfl.tf.spi.Util;
import org.glassfish.pfl.tf.spi.annotation.TraceEnhanceLevel;

public class ClassEnhancer extends TFEnhanceAdapter {
    private final Util util ;
    private final EnhancedClassData ecd ;
    private boolean hasStaticInitializer = false ;

    public ClassEnhancer( Util util, EnhancedClassData ecd,
        ClassVisitor cv ) {

        super( cv, TraceEnhanceLevel.NONE, TraceEnhanceLevel.PHASE1, ecd ) ;
        this.util = util ;
        this.ecd = ecd ;
    }

    private void info( int level, String msg ) {
        util.info( level, "ClassEnhancer: " + msg ) ;
    }

    @Override
    public void visitEnd() {
        info( 2, "visitEnd") ;
        // Add the additional fields
        final String desc = Type.getDescriptor(
            SynchronizedHolder.class ) ;

        final int acc = Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC ;

        // Signature is actually L../SynchronizedHolder;<L.../MethodMonitor;>
        // where the ... are replaced with appropriate packages.  Not
        // that we actually need a signature here.
        final String sig = null ;

        for (String fname : ecd.getAnnotationToHolderName().values()) {
            info( 2, "Adding field " + fname + " of type " + desc ) ;
            cv.visitField( acc, fname, desc, sig, null ) ;
        }

        if (!hasStaticInitializer) {
            info( 2, "creating static init") ;
            int siacc = Opcodes.ACC_STATIC + Opcodes.ACC_PRIVATE ;
            MethodVisitor mv = cv.visitMethod( siacc, "<clinit>", "()V",
                null, null ) ;
            if (util.getDebug()) {
                mv = new SimpleMethodTracer(mv, util) ;
            }
            MethodVisitor ma = new StaticInitVisitor( siacc, "()V", mv,
                util, ecd ) ;

            ma.visitCode() ;
            ma.visitInsn( Opcodes.RETURN ) ; // Only if creating a <clinit>!

            ma.visitMaxs( 0, 0 ) ;
            ma.visitEnd() ;
        }

        super.visitEnd() ;

        ecd.updateInfoDesc() ;
    }

    public class InfoMethodRewriter extends GeneratorAdapter {
        private int access ;
        private String name ;
        private String desc ;

        // Note that desc is the descriptor of the unaugmented method:
        // no MethodMonitor or Object at the end of the args.
        public InfoMethodRewriter( MethodVisitor mv,
            int acc, String name, String desc ) {

            super( Opcodes.ASM7, mv, acc, name, desc ) ;
            this.access = acc ;
            this.name = name ;
            this.desc = desc ;
        }

        // add MethodMonitor and Object parameters to end of params
        // generate body
        @Override
        public void visitCode() {
            super.visitCode() ;
            info( 2, "InfoMethodRewriter: visitCode " + name + desc ) ;

            final boolean isStatic = util.hasAccess( access, 
                Opcodes.ACC_STATIC ) ;
            final Type[] argTypes = Type.getArgumentTypes( desc ) ;
            int argSize = isStatic ? 0 : 1 ;
            for (Type type : argTypes) {
                argSize += type.getSize() ;
            }

            info( 2, "InfoMethodRewriter: initial arg size " + argSize ) ;

            int mmIndex = argSize ;
            int cidIndex = argSize + 1 ;

            Label jumpLabel = new Label() ;
            mv.visitVarInsn( Opcodes.ALOAD, mmIndex ) ;
            mv.visitJumpInsn( Opcodes.IFNULL, jumpLabel) ;

            // mm.info( <args>, callerId, selfId )
            mv.visitVarInsn( Opcodes.ALOAD, mmIndex ) ;

            util.wrapArgs( mv, access, desc ) ;

            mv.visitVarInsn( Opcodes.ILOAD, cidIndex ) ;

            util.emitIntConstant( mv, ecd.getMethodIndex( name )) ;

            mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                EnhancedClassData.MM_NAME, "info",
                "([Ljava/lang/Object;II)V", true) ;

            mv.visitLabel( jumpLabel ) ;

            // Method should already have a RETURN at the end, so just let
            // the default behavior copy it.
            // mv.visitInsn( Opcodes.RETURN ) ;
        }
    }

    public class InfoMethodCallRewriter extends GeneratorAdapter {
        public InfoMethodCallRewriter( MethodVisitor mv,
            int acc, String name, String desc ) {

            super( Opcodes.ASM7, mv, acc, name, desc ) ;
        }

        @Override
        public void visitMethodInsn( int opcode, String owner,
            String name, String desc, boolean isInterface ) {
            info( 2, "InfoMethodCallRewriter: visitMethodInsn: " + owner
                + "." + name + desc ) ;

            // If opcode is INVOKESPECIAL, owner is this class, and name/desc
            // are in the infoMethodDescs set, update the desc for the call
            // and add the extra parameters to the end of the call.
            String fullDesc = util.getFullMethodDescriptor( name, desc ) ;
            if ((opcode == Opcodes.INVOKESPECIAL)
                && (owner.equals( ecd.getClassName() )
                && (ecd.classifyMethod(fullDesc)
                    == EnhancedClassData.MethodType.INFO_METHOD))) {

                info( 2, "InfoMethodCallRewriter: visitMethodInsn: "
                    + "rewriting method call" ) ;

                // For the re-write, just pass nulls.  These instructions
                // will be replaced when tracing is enabled.
                mv.visitInsn( Opcodes.ACONST_NULL ) ;
                mv.visitInsn( Opcodes.ICONST_0 ) ;

                // For the tracing case
                // mv.visitVarInsn( Opcodes.ALOAD, __mm.index ) ;
                // mv.visitVarInsn( Opcodes.ALOAD, __ident.index ) ;

                String newDesc = util.augmentInfoMethodDescriptor(desc) ;

                mv.visitMethodInsn(opcode, owner, name, newDesc, isInterface );
            } else {
                mv.visitMethodInsn(opcode, owner, name, desc, isInterface );
            }
        }
    }

    public class NormalMethodChecker extends GeneratorAdapter {
	private final String mname ;
        public NormalMethodChecker( MethodVisitor mv,
            int acc, String name, String desc ) {

            super( Opcodes.ASM7, mv, acc, name, desc ) ;

	    mname = util.getFullMethodDescriptor(name, desc ) ;
        }

        @Override
        public void visitMethodInsn( int opcode, String owner,
            String name, String desc, boolean isInterface ) {
            info( 2, "NormalMethodChecker: visitMethodInsn: " + owner
                + "." + name + desc ) ;

            // If opcode is INVOKESPECIAL, owner is this class, and name/desc
            // are in the infoMethodDescs set, update the desc for the call
            // and add the extra parameters to the end of the call.
            String fullDesc = util.getFullMethodDescriptor( name, desc ) ;
            if ((opcode == Opcodes.INVOKESPECIAL)
                && (owner.equals( ecd.getClassName() )
                && (ecd.classifyMethod(fullDesc)
                    == EnhancedClassData.MethodType.INFO_METHOD))) {

                util.error( "Method " + mname
                    + " in class " + ecd.getClassName() + " makes an"
                    + " illegal call to an @InfoMethod method" ) ;
            }

            mv.visitMethodInsn( opcode, owner, name, desc, isInterface ) ;
        }
    }

    @Override
    public MethodVisitor visitMethod( final int access, final String name,
        final String desc, final String sig, final String[] exceptions ) {
        info( 2, "visitMethod " + name + desc ) ;

        // Enhance the class first (this changes the "schema" of the class).
        // - Enhance the static initializer so that the class will be properly
        //   registered with the tracing facility.
        // - Modify all of the @InfoMethod methods with extra arguments
        // - Modify all calls to @InfoMethod methods to add the extra arguments
        //   or to flag an error if NOT called from an MM method.

        String fullDesc = util.getFullMethodDescriptor(name, desc) ;
        EnhancedClassData.MethodType mtype =
            ecd.classifyMethod(fullDesc) ;

        MethodVisitor mv ;

        switch (mtype) {
            case STATIC_INITIALIZER :
                mv = super.visitMethod( access, name, desc,
                    sig, exceptions ) ;
                if (util.getDebug()) {
                    mv = new SimpleMethodTracer(mv,util) ;
                }
                hasStaticInitializer = true ;
                return new StaticInitVisitor( access, desc, mv, util,
                    ecd ) ;

            case INFO_METHOD :
                String newDesc = util.augmentInfoMethodDescriptor( desc ) ;
                mv = super.visitMethod( access, name, newDesc,
                    sig, exceptions ) ;
                if (util.getDebug()) {
                    mv = new SimpleMethodTracer(mv,util) ;
                }
                return new InfoMethodRewriter( mv, access, name, desc ) ;

            case MONITORED_METHOD :
                mv = super.visitMethod( access, name, desc,
                    sig, exceptions ) ;
                if (util.getDebug()) {
                    mv = new SimpleMethodTracer(mv,util) ;
                }
                return new InfoMethodCallRewriter( mv, access, name, desc ) ;

            case NORMAL_METHOD :
                mv = super.visitMethod( access, name, desc,
                    sig, exceptions ) ;
                if (util.getDebug()) {
                    mv = new SimpleMethodTracer(mv,util) ;
                }
                return new NormalMethodChecker( mv, access, name, desc) ;
        }

        return null ;
    }
} // end of ClassEnhancer
