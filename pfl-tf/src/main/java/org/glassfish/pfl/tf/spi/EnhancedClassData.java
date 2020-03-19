/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 Payara Services Ltd.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi ;

import java.util.List;
import java.util.Map;

import org.glassfish.pfl.basic.contain.SynchronizedHolder;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;
import org.glassfish.pfl.tf.spi.annotation.Description;
import org.objectweb.asm.Type;

/**
 *
 * @author ken
 */
public interface EnhancedClassData {
    Type OBJECT_TYPE = Type.getType( Object.class ) ;
    String OBJECT_NAME = OBJECT_TYPE.getInternalName() ;

    Type SH_TYPE = Type.getType( SynchronizedHolder.class ) ;
    String SH_NAME = SH_TYPE.getInternalName() ;

    Type MM_TYPE = Type.getType( MethodMonitor.class ) ;
    String MM_NAME = MM_TYPE.getInternalName() ;

    String INFO_METHOD_NAME = Type.getInternalName( InfoMethod.class ) ;

    String DESCRIPTION_NAME = Type.getInternalName( Description.class ) ;

    /** Return the internal name of the class.
     * @return The class name.
     */
    String getClassName() ;

    /** Returns true iff this class is monitored.
     *
     * @return true iff this class has one or more MM annotations.
     */
    boolean isTracedClass() ;

    /** Map from MM annotation name to the name of the holder 
     * field that contains the SynchronizedHolder for the
     * corresponding MethodMonitor.  The domain of this map is the set of
     * MM annotations on this class.
     *
     * @return Map from MM annotations defined on this class to the names of
     * the holder fields.
     */
    Map<String,String> getAnnotationToHolderName() ;

    public enum MethodType {
        STATIC_INITIALIZER,
        INFO_METHOD,
        NORMAL_METHOD,
        MONITORED_METHOD
    }

    /** Classify the method.
     * @param fullMethodDescriptor The full method descriptor of the method.
     * @return The kind of the corresponding method.
     */
    MethodType classifyMethod( String fullMethodDescriptor ) ;

    /** Name of the holder fields corresponding to a particular
     * method.  Note that the full descriptor (name + arg/return
     * descriptor) is used to unambiguously identify the method in the class.
     *
     * @param fullMethodDescriptor The full method descriptor of the method.
     * @return The name of the holder field used for this method.
     */
    String getHolderName( String fullMethodDescriptor ) ;

    /** List of method names for all MM methods and info methods 
     * in the class.  Order is significant, as the index of the
     * method in the list is the ordinal used to represent it.
     * This list is in sorted order.
     *
     * @return List of all method tracing names in sorted order.
     */
    List<String> getMethodNames() ;

    /** List of timing point names corresponding to method names.
     * For monitored methods, this is just the method name.
     * For info methods whose tpType is not NONE, this is specified
     * in tpName.
     * @return List of timing point names, in the same order as in 
     * getMethodTracingNames.
     */
    List<String> getTimingPointNames() ;

    /** List of descriptions of monitored methods and info methods.
     * If no description was given in the annotations, the value is "".
     * 
     * @return List of descriptions in the same order as in 
     * getMethodTracingNames.
     */
    List<String> getDescriptions() ;

    /** List of timing point types of monitored methods and info methods.
     * The list contains BOTH for a monitored method.  An info method that
     * does not represent a timing point is represented by NONE.
     * 
     * @return List of TimingPointTypes in the same order as in
     * getMethodTracingNames.
     */
    List<TimingPointType> getTimingPointTypes() ;

    /** List of annotation names for each info method and monitored method.
     * It is interpreted as follows:
     * <ul>
     * <li>If the entry in the list is not null, it is the only annotation
     * applicable to this method.  This is the case for monitored methods.
     * <li>If the entry in the list is null, all annotations on the
     * enclosing class apply to this method.  This is the case for an
     * InfoMethod, which can be called from any monitored method regardless of
     * the annotation on the monitored method.
     * </ul>
     * @return List of annotation names for methods.
     */
    List<String> getMethodMMAnnotationName() ;

    /** Index of method name in the list of method names.
     *
     * @param methodName The method name as defined for tracing.
     * @return the method index
     */
    int getMethodIndex( String methodName ) ;

    /** Enhance all of the descriptors for infoMethods.
     */
    void updateInfoDesc() ;
}
