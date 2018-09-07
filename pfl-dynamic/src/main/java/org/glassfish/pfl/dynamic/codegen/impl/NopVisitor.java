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
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;


/** This is a visitor that does nothing.  Useful occasionally
 * for supressing actions in a certain context.
 */
public class NopVisitor extends TreeWalker {
    public NopVisitor( TreeWalkerContext context ) {
	super( context ) ;
	context.push( this ) ;
    }

    // Node
    @Override
    public boolean preNode( Node arg ) {
	return false ;
    }

    @Override
    public void postNode( Node arg ) {
    }

    // ClassGeneratorImpl
    @Override
    public boolean preClassGenerator( ClassGeneratorImpl arg ) {
	return preNode( arg ) ;
    }

    @Override
    public boolean classGeneratorBeforeFields( ClassGeneratorImpl arg ) {
	return false ;
    }

    @Override
    public void classGeneratorBeforeInitializer( ClassGeneratorImpl arg ) {
    }

    @Override
    public void classGeneratorBeforeMethod( ClassGeneratorImpl arg ) {
    }

    @Override
    public void classGeneratorBeforeConstructor( ClassGeneratorImpl arg ) {
    }

    @Override
    public void postClassGenerator( ClassGeneratorImpl arg ) {
	postNode( arg ) ;
    }
    
    // MethodGenerator
    @Override
    public boolean preMethodGenerator( MethodGenerator arg ) {
	return preNode( arg ) ;
    }

    @Override
    public boolean methodGeneratorBeforeArguments( MethodGenerator arg ) {
	return false ;
    }

    @Override
    public void postMethodGenerator( MethodGenerator arg ) {
	postNode( arg ) ;
    }

    // Statement
    @Override
    public boolean preStatement( Statement arg ) {
	return preNode( arg ) ;
    }

    @Override
    public void postStatement( Statement arg ) {
	postNode( arg ) ;
    }

    // ThrowStatement
    @Override
    public boolean preThrowStatement( ThrowStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void postThrowStatement( ThrowStatement arg ) {
	postStatement( arg ) ;
    }

    // AssignmentStatement
    @Override
    public boolean preAssignmentStatement( AssignmentStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void assignmentStatementBeforeLeftSide( AssignmentStatement arg ) {
    }

    @Override
    public void postAssignmentStatement( AssignmentStatement arg ) {
	postStatement( arg ) ;
    }

    // BlockStatement
    @Override
    public boolean preBlockStatement( BlockStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void blockStatementBeforeBodyStatement( BlockStatement arg, Statement stmt ) {
    }

    @Override
    public void postBlockStatement( BlockStatement arg ) {
	postStatement( arg ) ;
    }

    // CaseBranch
    @Override
    public boolean preCaseBranch( CaseBranch arg ) {
	return preBlockStatement( arg ) ;
    }

    @Override
    public void caseBranchBeforeBodyStatement( CaseBranch arg ) {
    }

    @Override
    public void postCaseBranch( CaseBranch arg ) {
	postBlockStatement( arg ) ;
    }

    // DefinitionStatement
    @Override
    public boolean preDefinitionStatement( DefinitionStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public boolean definitionStatementBeforeExpr( DefinitionStatement arg ) {
	return false ;
    }

    @Override
    public void postDefinitionStatement( DefinitionStatement arg ) {
	postStatement( arg ) ;
    }

    // IfStatement
    @Override
    public boolean preIfStatement( IfStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void ifStatementBeforeTruePart( IfStatement arg ) {
    }

    @Override
    public boolean ifStatementBeforeFalsePart( IfStatement arg ) {
	return false ;
    }

    @Override
    public void postIfStatement( IfStatement arg ) {
	postStatement( arg ) ;
    }

    // BreakStatement
    @Override
    public boolean preBreakStatement( BreakStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void postBreakStatement( BreakStatement arg ) {
	postStatement( arg ) ;
    }

    // ReturnStatement
    @Override
    public boolean preReturnStatement( ReturnStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void postReturnStatement( ReturnStatement arg ) {
	postStatement( arg ) ;
    }

    // SwitchStatement
    @Override
    public boolean preSwitchStatement( SwitchStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public boolean switchStatementBeforeCaseBranches( SwitchStatement arg ) {
	return false ;
    }

    @Override
    public boolean switchStatementBeforeDefault( SwitchStatement arg ) {
	return false ;
    }

    @Override
    public void postSwitchStatement( SwitchStatement arg ) {
	postStatement( arg ) ;
    }

    // TryStatement
    @Override
    public boolean preTryStatement( TryStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void tryStatementBeforeBlock( TryStatement arg,
	Type type, Variable var, BlockStatement block ) {
    }

    @Override
    public boolean tryStatementBeforeFinalPart( TryStatement arg ) {
	return false ;
    }

    @Override
    public void postTryStatement( TryStatement arg ) {
	postStatement( arg ) ;
    }

    // WhileStatement
    @Override
    public boolean preWhileStatement( WhileStatement arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void whileStatementBeforeBody( WhileStatement arg ) {
    }

    @Override
    public void postWhileStatement( WhileStatement arg ) {
	postStatement( arg ) ;
    }

    // ExpressionInternal
    @Override
    public boolean preExpression( ExpressionInternal arg ) {
	return preStatement( arg ) ;
    }

    @Override
    public void postExpression( ExpressionInternal arg ) {
	postStatement( arg ) ;
    }

    // Variable
    @Override
    public boolean preVariable( Variable arg ) {
	return preExpression( (VariableInternal)arg ) ;
    }

    @Override
    public void postVariable( Variable arg ) {
	postExpression( (VariableInternal)arg ) ;
    }

    // ConstantExpression
    @Override
    public boolean preConstantExpression( ExpressionFactory.ConstantExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postConstantExpression( ExpressionFactory.ConstantExpression arg ) {
	postExpression( arg ) ;
    }

    // VoidExpression
    @Override
    public boolean preVoidExpression( ExpressionFactory.VoidExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postVoidExpression( ExpressionFactory.VoidExpression arg ) {
	postExpression( arg ) ;
    }

    // ThisExpression
    @Override
    public boolean preThisExpression( ExpressionFactory.ThisExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postThisExpression( ExpressionFactory.ThisExpression arg ) {
	postExpression( arg ) ;
    }

    // UnaryOperatorExpression
    @Override
    public boolean preUnaryOperatorExpression( ExpressionFactory.UnaryOperatorExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postUnaryOperatorExpression( ExpressionFactory.UnaryOperatorExpression arg ) {
	postExpression( arg ) ;
    }

    // BinaryOperatorExpression
    @Override
    public boolean preBinaryOperatorExpression( ExpressionFactory.BinaryOperatorExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void binaryOperatorExpressionBeforeRight( 
	ExpressionFactory.BinaryOperatorExpression arg ) {
    }

    @Override
    public void postBinaryOperatorExpression( ExpressionFactory.BinaryOperatorExpression arg ) {
	postExpression( arg ) ;
    }

    // CastExpression
    @Override
    public boolean preCastExpression( ExpressionFactory.CastExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postCastExpression( ExpressionFactory.CastExpression arg ) {
	postExpression( arg ) ;
    }

    // InstofExpression
    @Override
    public boolean preInstofExpression( ExpressionFactory.InstofExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postInstofExpression( ExpressionFactory.InstofExpression arg ) {
	postExpression( arg ) ;
    }

    // StaticCallExpression
    @Override
    public boolean preStaticCallExpression( ExpressionFactory.StaticCallExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void staticCallExpressionBeforeArg( ExpressionFactory.StaticCallExpression arg ) {
    }

    @Override
    public void postStaticCallExpression( ExpressionFactory.StaticCallExpression arg ) {
	postExpression( arg ) ;
    }

    // NonStaticCallExpression
    @Override
    public boolean preNonStaticCallExpression( ExpressionFactory.NonStaticCallExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void nonStaticCallExpressionBeforeArg( ExpressionFactory.NonStaticCallExpression arg ) {
    }

    @Override
    public void postNonStaticCallExpression( ExpressionFactory.NonStaticCallExpression arg ) {
	postExpression( arg ) ;
    }

    // NewObjExpression
    @Override
    public boolean preNewObjExpression( ExpressionFactory.NewObjExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void newObjExpressionBeforeArg( ExpressionFactory.NewObjExpression arg ) {
    }

    @Override
    public void postNewObjExpression( ExpressionFactory.NewObjExpression arg ) {
	postExpression( arg ) ;
    }

    // NewArrExpression
    @Override
    public boolean preNewArrExpression( ExpressionFactory.NewArrExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void newArrExpressionAfterSize( ExpressionFactory.NewArrExpression arg ) {
    }

    @Override
    public void newArrExpressionBeforeExpression( ExpressionFactory.NewArrExpression arg ) {
    }

    @Override
    public void newArrExpressionAfterExpression( ExpressionFactory.NewArrExpression arg ) {
    }

    @Override
    public void postNewArrExpression( ExpressionFactory.NewArrExpression arg ) {
	postExpression( arg ) ;
    }

    // SuperCallExpression
    @Override
    public boolean preSuperCallExpression( ExpressionFactory.SuperCallExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void superCallExpressionBeforeArg( ExpressionFactory.SuperCallExpression arg ) {
    }

    @Override
    public void postSuperCallExpression( ExpressionFactory.SuperCallExpression arg ) {
	postExpression( arg ) ;
    }

    // SuperObjExpression
    @Override
    public boolean preSuperObjExpression( ExpressionFactory.SuperObjExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void superObjExpressionBeforeArg( ExpressionFactory.SuperObjExpression arg ) {
    }

    @Override
    public void postSuperObjExpression( ExpressionFactory.SuperObjExpression arg ) {
	postExpression( arg ) ;
    }

    // ThisObjExpression
    @Override
    public boolean preThisObjExpression( ExpressionFactory.ThisObjExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postThisObjExpression( ExpressionFactory.ThisObjExpression arg ) {
	postExpression( arg ) ;
    }

    @Override
    public void thisObjExpressionBeforeArg( ExpressionFactory.ThisObjExpression arg ) {
    }

    // NonStaticFieldAccessExpression
    @Override
    public boolean preNonStaticFieldAccessExpression( ExpressionFactory.NonStaticFieldAccessExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postNonStaticFieldAccessExpression( ExpressionFactory.NonStaticFieldAccessExpression arg ) {
	postExpression( arg ) ;
    }

    // StaticFieldAccessExpression
    @Override
    public boolean preStaticFieldAccessExpression( ExpressionFactory.StaticFieldAccessExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postStaticFieldAccessExpression( ExpressionFactory.StaticFieldAccessExpression arg ) {
	postExpression( arg ) ;
    }

    // ArrayIndexExpression
    @Override
    public boolean preArrayIndexExpression( ExpressionFactory.ArrayIndexExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void arrayIndexExpressionBeforeExpr( ExpressionFactory.ArrayIndexExpression arg ) {
    }

    @Override
    public void postArrayIndexExpression( ExpressionFactory.ArrayIndexExpression arg ) {
	postExpression( arg ) ;
    }

    // ArrayLengthExpression
    @Override
    public boolean preArrayLengthExpression( ExpressionFactory.ArrayLengthExpression arg ) {
	return preExpression( arg ) ;
    }

    @Override
    public void postArrayLengthExpression( ExpressionFactory.ArrayLengthExpression arg ) {
	postExpression( arg ) ;
    }
}
