/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.impl.ExpressionInternal ;
import org.glassfish.pfl.dynamic.codegen.spi.Signature ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;

/**
 *
 * @author Ken Cavanaugh
 */
public interface Visitor {
    void visitClassGenerator( ClassGeneratorImpl arg ) ;

    void visitMethodGenerator( MethodGenerator arg ) ;

    void visitNode( Node arg ) ;

    void visitFieldGenerator( FieldGenerator arg ) ;
    
    void visitStatement( Statement arg ) ;

    void visitThrowStatement( ThrowStatement arg ) ;
    
    void visitAssignmentStatement( AssignmentStatement arg ) ;

    void visitDefinitionStatement( DefinitionStatement arg ) ;

    void visitBlockStatement( BlockStatement arg ) ;
    
    void visitCaseBranch( CaseBranch arg ) ;

    void visitIfStatement( IfStatement arg ) ;
    
    void visitBreakStatement( BreakStatement arg ) ;

    void visitReturnStatement( ReturnStatement arg ) ;
    
    void visitSwitchStatement( SwitchStatement arg ) ;
    
    void visitTryStatement( TryStatement arg ) ;
    
    void visitWhileStatement( WhileStatement arg ) ;
    
    void visitExpression( ExpressionInternal arg ) ;

    void visitVariable( Variable arg ) ;

    void visitConstantExpression( ExpressionFactory.ConstantExpression arg ) ;

    void visitVoidExpression( ExpressionFactory.VoidExpression arg ) ;

    void visitThisExpression( ExpressionFactory.ThisExpression arg ) ;

    void visitUnaryOperatorExpression( ExpressionFactory.UnaryOperatorExpression arg ) ;

    void visitBinaryOperatorExpression( ExpressionFactory.BinaryOperatorExpression arg ) ;

    void visitCastExpression( ExpressionFactory.CastExpression arg ) ;

    void visitInstofExpression( ExpressionFactory.InstofExpression arg ) ;

    void visitStaticCallExpression( ExpressionFactory.StaticCallExpression arg ) ;

    void visitNonStaticCallExpression( ExpressionFactory.NonStaticCallExpression arg ) ;

    void visitNewObjExpression( ExpressionFactory.NewObjExpression arg ) ;

    void visitNewArrExpression( ExpressionFactory.NewArrExpression arg ) ;

    void visitSuperCallExpression( ExpressionFactory.SuperCallExpression arg ) ;

    void visitSuperObjExpression( ExpressionFactory.SuperObjExpression arg ) ;

    void visitThisObjExpression( ExpressionFactory.ThisObjExpression arg ) ;

    void visitNonStaticFieldAccessExpression( 
	ExpressionFactory.NonStaticFieldAccessExpression arg ) ;

    void visitStaticFieldAccessExpression( ExpressionFactory.StaticFieldAccessExpression arg ) ;

    void visitArrayIndexExpression( ExpressionFactory.ArrayIndexExpression arg ) ;

    void visitArrayLengthExpression( ExpressionFactory.ArrayLengthExpression arg ) ;

    void visitIfExpression( ExpressionFactory.IfExpression arg ) ;
}
