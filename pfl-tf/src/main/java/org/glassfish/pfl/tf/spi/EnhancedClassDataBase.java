/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.spi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ken
 */
public abstract class EnhancedClassDataBase implements EnhancedClassData {
    protected Util util ;
    protected final Set<String> annotationNames ;

    protected EnhancedClassDataBase( Util util, Set<String> annotationNames ) {
        this.util = util ;
        this.annotationNames = annotationNames ;
    }

    protected String className ;

    protected final Set<String> annoNamesForClass =
        new HashSet<String>() ;

    // Map from MM annotation internal name to
    // SynchronizedHolder<MethodMonitor> field
    // name.  Use something like __$mm$__nnn that is unlikely to collide with
    // another field name that is already in use.
    protected final Map<String,String> annoToHolderName =
        new HashMap<String,String>() ;

    // List of tracing names of MM methods.  Index of method is identifier in
    // MethodMonitor calls.  Sorted to guarantee consistent order.
    protected final List<String> methodNames =
        new ArrayList<String>() ;

    // List of descriptions of methods.  This is in the same order as
    // methodNames.
    protected final List<String> methodDescriptions = 
        new ArrayList<String>() ;

    // List of timing point types of methods.  This is in the same order as
    // methodNames.  All names of monitored methods have type BOTH;
    // names of info methods may be ENTER, EXIT, or NONE.
    protected final List<TimingPointType> methodTPTs =
        new ArrayList<TimingPointType>() ;

    protected final List<String> methodTPNames =
        new ArrayList<String>() ;

    // List of annotations of methods.  This is in the same order as
    // methodNames.  If the method is a monitored method, this is the
    // annotation on the method; if the method is an info method, this is
    // null.
    protected final List<String> methodAnnoList = 
        new ArrayList<String>() ;

    // List of descriptors of @InfoMethod-annotated methods.
    // Needed for validating and transforming calls to such methods.
    protected final Set<String> infoMethodDescs =
        new HashSet<String>() ;

    protected final Set<String> mmMethodDescs =
        new HashSet<String>() ;

    // Map from method signature to internal name of its MM annotation.
    protected final Map<String,String> methodToAnno =
        new HashMap<String,String>() ;

    public String getClassName() {
        return className ;
    }

    public Map<String,String> getAnnotationToHolderName() {
        return annoToHolderName ;
    }

    public List<String> getMethodNames() {
        return methodNames ;
    }
    
    public int getMethodIndex( String methodName ) {
        if (methodName != null) {
            for (int ctr = 0; ctr < methodNames.size(); ctr++) {
                if (methodName.equals(methodNames.get(ctr))) {
                    return ctr ;
                }
            }
        }

        return -1 ;
    }
    
    public String getHolderName( String fullMethodDescriptor ) {
        String aname = methodToAnno.get( fullMethodDescriptor ) ;
        String result = annoToHolderName.get( aname ) ;
        return result ;
    }
    
    public MethodType classifyMethod( String fullMethodDescriptor ) {
        if (fullMethodDescriptor.equals( "<clinit>()V")) {
            return MethodType.STATIC_INITIALIZER ;
        } else if (infoMethodDescs.contains( fullMethodDescriptor )) {
            return MethodType.INFO_METHOD ;
        } else if (mmMethodDescs.contains( fullMethodDescriptor)) {
            return MethodType.MONITORED_METHOD ;
        } else {
            return MethodType.NORMAL_METHOD ;
        }
    }
    
    public boolean isTracedClass() {
        return !annoNamesForClass.isEmpty() ;
    }

    public void updateInfoDesc() {
        String[] descs = infoMethodDescs.toArray( 
            new String[infoMethodDescs.size() ] ) ;

        infoMethodDescs.clear() ;

        for (String desc : descs) {
            int index = desc.indexOf( '(' ) ;
            String name = desc.substring( 0, index )  ;
            String d = desc.substring( index ) ;
            String fd = util.augmentInfoMethodDescriptor(d) ;
            infoMethodDescs.add( name + fd ) ;
        }
    }

    /** List of descriptions of monitored methods and info methods.
     * If no description was given in the annotations, the value is "".
     *
     * @return List of descriptions in the same order as in
     * getMethodTracingNames.
     */
    public List<String> getDescriptions() {
        return methodDescriptions ;
    }

    /** List of timing point types of monitored methods and info methods.
     * The list contains BOTH for a monitored method.  An info method that
     * does not represent a timing point is represented by NONE.
     *
     * @return List of TimingPointTypes in the same order as in
     * getMethodTracingNames.
     */
    public List<TimingPointType> getTimingPointTypes() {
        return methodTPTs ;
    }

    public List<String> getTimingPointNames() {
        return methodTPNames ;
    }

    public List<String> getMethodMMAnnotationName() {
        return methodAnnoList ;
    }
}
