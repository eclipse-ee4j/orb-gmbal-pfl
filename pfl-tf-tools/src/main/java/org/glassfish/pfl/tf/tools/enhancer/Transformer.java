/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.tools.enhancer;

import java.util.Set;
import java.util.Iterator;
import org.glassfish.pfl.tf.timer.spi.TimingInfoProcessor;
import org.glassfish.pfl.tf.timer.spi.TimerFactoryBuilder;
import org.glassfish.pfl.tf.spi.TimingPointType;
import org.glassfish.pfl.basic.func.UnaryFunction;
import org.glassfish.pfl.tf.spi.EnhancedClassData;
import org.glassfish.pfl.tf.spi.EnhancedClassDataASMImpl;
import org.glassfish.pfl.tf.spi.TraceEnhancementException;
import org.glassfish.pfl.tf.spi.Util;

import org.glassfish.pfl.objectweb.asm.ClassAdapter;
import org.glassfish.pfl.objectweb.asm.ClassReader;
import org.glassfish.pfl.objectweb.asm.ClassVisitor;
import org.glassfish.pfl.objectweb.asm.Opcodes;
import org.glassfish.pfl.objectweb.asm.tree.ClassNode;

/** ClassFile enhancer for the tracing facility.  This modifies the bytecode
 * for an applicable class, then returns the updated bytecode.
 * Makes extensive use of the ASM library.
 *
 * This is split into two parts.  The first part modifies the schema
 * of the class as follows:
 * <ul>
 * <li>Adds static fields as required for the SynchronizedHolder<MethodMonitor> 
 * instances.
 * <li>Modifies the static initializer to set up the new fields, and register
 * the class with the MethodMonitorRegistry.  This also constructs the list
 * of method names, which is needed by the second part.
 * <li>Re-writes all @InfoMethod methods to take two extra parameters at the
 * end of their argument lists.
 * <li>Re-writes all calls to @InfoMethod methods to supply the two extra 
 * parameters to all calls.
 * <li>Checks that @InfoMethod methods (which must be private) are only called
 * from MM annotated methods.
 * </ul>
 * <p>
 * The second part modifies the MM annotated methods as follows:
 * <li>Adds a preamble to set up some local variables, and to call 
 * the MethodMonitor.enter method when active.
 * <li>Adds a finally block at the end of the method that handles calling
 * MethodMonitor.exit whenever an exception is thrown or propagated from the
 * body of the method.
 * <li>Modifies all exit point in the method as follows:
 * <ul>
 * <li>If the exit point is a return, call MethodMonitor.exit before the return.
 * <li>If the exit point is a throw, call MethodMonitor.exception before the throw.
 * </ul>
 * </ul>
 * <p>
 * Note that the second part could be run in a ClassFileTransformer or ClassLoader
 * if desired, since this design enhances the class files in place for the first
 * part.
 *
 * @author ken
 */
public class Transformer implements UnaryFunction<byte[],byte[]> {
    private final Util util  ;
    private final EnhanceTool.ProcessingMode mode ;
    private final TimingInfoProcessor tip ;
    private final Set<String> annotationNames ;

    // Initialized in the evaluate method.
    private EnhancedClassData ecd = null ;

    Transformer(Util util, EnhanceTool.ProcessingMode mode,
        TimingInfoProcessor tip, Set<String> anames ) {

        this.util = util ;
        this.mode = mode ;
        this.tip = tip ;
        this.annotationNames = anames ;
    }

    private boolean hasAccess( int access, int flag ) {
        return (access & flag) == flag ;
    }

    private String getSuffix( String str ) {
        String result = str ;
        final int index = str.lastIndexOf('/') ;
        if (index >=0) {
            result = result.substring(index + 1);
        }
        return result ;
    }

    private void processTimers() {
        final Iterator<String> descriptions =
            ecd.getDescriptions().iterator() ;
        final Iterator<String> names =
            ecd.getTimingPointNames().iterator() ;
        final Iterator<TimingPointType> tpts =
            ecd.getTimingPointTypes().iterator() ;
        final Iterator<String> groups =
            ecd.getMethodMMAnnotationName().iterator() ;

        final Set<String> classAnnoNames =
            ecd.getAnnotationToHolderName().keySet() ;

        while (descriptions.hasNext()) {
            final String desc = descriptions.next() ;
            final String name = names.next() ;
            final TimingPointType tpt = tpts.next() ;
            final String group = groups.next() ;

            if (tpt != TimingPointType.NONE) {
                final String cname = getSuffix( ecd.getClassName() ) ;
                final String timerName = TimerFactoryBuilder.getTimerName( cname,
                    name) ;

                tip.addTimer( timerName, desc ) ;

                if (group == null) {
                    for (String str : classAnnoNames) {
                        tip.containedIn( timerName, getSuffix( str ) ) ;
                    }
                } else {
                    tip.containedIn( timerName, getSuffix( group ) ) ;
                }
            }
        }
    }

    public byte[] evaluate( final byte[] arg) {
        final ClassNode cn = new ClassNode() ;
        final ClassReader cr = new ClassReader( arg ) ;
        cr.accept( cn, ClassReader.SKIP_FRAMES ) ;

        // Ignore annotations and interfaces.
        if (util.hasAccess(cn.access, Opcodes.ACC_ANNOTATION) ||
            util.hasAccess(cn.access, Opcodes.ACC_INTERFACE)) {
            return null ;
        }

        try {
            ecd = new EnhancedClassDataASMImpl( util, annotationNames, cn ) ;

            // If this class is not annotated as a traced class, ignore it.
            if (!ecd.isTracedClass()) {
                return null ;
            }

            processTimers() ;

            byte[] phase1 = null ;
            if ((mode == EnhanceTool.ProcessingMode.UpdateSchemas) ||
               (mode == EnhanceTool.ProcessingMode.TraceEnhance)) {
                phase1 = util.transform( false, arg,
                    new UnaryFunction<ClassVisitor, ClassAdapter>() {
                        public ClassAdapter evaluate(ClassVisitor arg) {
                            return new ClassEnhancer( util, ecd, arg ) ;
                        }
                    }
                ) ;
            }

            // Only communication from part 1 to part2 is a byte[] and
            // the EnhancedClassData.  A runtime version can be regenerated
            // as above from the byte[] from the class file as it is presented
            // to a ClassFileTransformer.
            //     Implementation note: runtime would keep byte[] stored of
            //     original version whenever tracing is enabled, so that
            //     disabling tracing simply means using a ClassFileTransformer
            //     to get back to the original code.
            //
            // Then add tracing code (part 2).
            //     This is a pure visitor using the AdviceAdapter.
            //     It must NOT modify its input visitor (or you get an
            //     infinite series of calls to onMethodExit...)

            if (mode == EnhanceTool.ProcessingMode.TraceEnhance) {
                final byte[] phase2 = util.transform( util.getDebug(), phase1,
                    new UnaryFunction<ClassVisitor, ClassAdapter>() {

                    public ClassAdapter evaluate(ClassVisitor arg) {
                        return new ClassTracer( util, ecd, arg ) ;
                    }
                }) ;

                return phase2 ;
            } else {
                return phase1 ;
            }
        } catch (TraceEnhancementException exc) {
            if (util.getDebug()) {
                util.info( 1, "Could not enhance file: " + exc ) ;
            }

            return null ;
        }
    }
}
