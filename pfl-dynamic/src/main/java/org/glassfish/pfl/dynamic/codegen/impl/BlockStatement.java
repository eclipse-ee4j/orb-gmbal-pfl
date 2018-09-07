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

import org.glassfish.pfl.dynamic.codegen.spi.Expression;
import java.util.List ;
import java.util.ArrayList ;
import java.util.Map ;
import java.util.HashMap ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;


/** Main factory for creating statements.  Represents a block
 * of statements which also defines a scope for local variable
 * declarations.
 *
 * @author Ken Cavanaugh
 */
public class BlockStatement extends StatementBase {
    private List<Statement> body ;
    private ExpressionFactory efactory ;

    // All definitions are present in body.  This
    // map is used to make sure that an identifier
    // is only defined once within a scope.
    private Map<String,DefinitionStatement> definitions ;

    BlockStatement( Node parent ) {
        super( parent ) ;
        body = new ArrayList<Statement>() ;
	efactory = new ExpressionFactory( this ) ;
	definitions = new HashMap<String,DefinitionStatement>() ;
    }

    /** Look up the ident to see if it has an associated Variable in
     * this block.
     */
    public Variable getVar( String ident ) {
	Variable result = null ;
	DefinitionStatement ds = definitions.get( ident ) ;
	if (ds != null)
	    result = ds.var() ;
	return result ;
    }

    /** Return true iff this BlockStatement contains no local variables
     * or statements.
     */
    public boolean isEmpty() {
	return body.isEmpty() ;
    }

    /** Return the list of Statements in this BlockStatement.
     */
    public List<Statement> body() {
	return body ;
    }

    /** Return the ExpressionFactory that must be used to create
     * any expressions occuring either in expressions added to
     * the body, or in other statements immediately contained 
     * in this BlockStatement.
     */
    public ExpressionFactory exprFactory() {
	return efactory ;
    }
    
    /** Add a break statement to this BlockStatement.
     */
    public void addBreak() {
	body.add( new BreakStatement( this )) ;
    }

    /** Add an empty return to this BlockStatement.  The enclosing 
     * MethodGenerator must have a void return type.
     */
    public void addReturn() {
	// XXX check that the return type of this method is void 
	body.add( new ReturnStatement( this )) ;
    } 

    /** Add a return with an expression to this BlockStatement.
     * The enclosing MethodGenerator must have a return type that
     * is assignment compatible with the type of expr.
     */
    public void addReturn( Expression expr ) {
	// XXX check that expr.type() matches the return type of this method
        body.add( new ReturnStatement( this, 
            ((ExpressionInternal)expr).copy(this, ExpressionInternal.class) )) ;
    }
    
    public IfStatement addIf( Expression cond ) {
	IfStatement result = new IfStatement( this, 
            ((ExpressionInternal)cond).copy(this, ExpressionInternal.class) ) ;
	body.add( result ) ;
	return result ;
    }
    
    public TryStatement addTry() {
        TryStatement result = new TryStatement( this ) ;
        body.add( result ) ;
        return result ;
    }
    
    public void addThrow( Expression expr ) {
        body.add( new ThrowStatement( this, 
            ((ExpressionInternal)expr).copy(this, ExpressionInternal.class) )) ;
    }
   
    private void checkSwitchExpressionType( Type type ) {
	if ((type.size() != 1) || type.equals( Type._boolean()))
	    throw new IllegalArgumentException( 
		"A switch expression must have type char, byte, short, or int" ) ;
    }

    public SwitchStatement addSwitch( Expression value ) {
	checkSwitchExpressionType( ((ExpressionInternal)value).type() ) ;
        SwitchStatement result = new SwitchStatement( this, 
            ((ExpressionInternal)value).copy(this, ExpressionInternal.class) ) ;
        body.add( result ) ;
        return result ;
    }
    
    public WhileStatement addWhile( Expression expr ) {
        WhileStatement result = new WhileStatement( this, 
            ((ExpressionInternal)expr).copy(this, ExpressionInternal.class) ) ;
        body.add( result ) ;
        return result ;
    }
    
    public void addExpression( Expression expr ) {
        body.add( ((ExpressionInternal)expr).copy(this,
            ExpressionInternal.class) ) ;
    }
    
    public void addAssign( Expression left, Expression right ) {
        body.add(new AssignmentStatement( this, 
	    ((ExpressionInternal)left).copy(this, ExpressionInternal.class),
            ((ExpressionInternal)right).copy(this, ExpressionInternal.class))) ;
    }
    
    public Expression addDefinition( Type type, String ident,
        Expression value ) {

	if (definitions.containsKey( ident ))
	    throw new IllegalArgumentException( 
		"This scope already contains a variable named " + ident ) ;
	Variable var = efactory.variable( type, ident ) ;
	DefinitionStatement ds = new DefinitionStatement( this, var, 
	    ((ExpressionInternal)value).copy(this, ExpressionInternal.class) ) ;
	body.add( ds ) ;
	definitions.put( ident, ds ) ;
	return var ;
    }
    
    @Override
    public void accept( Visitor visitor ) {
	visitor.visitBlockStatement( this ) ;
    }
}
