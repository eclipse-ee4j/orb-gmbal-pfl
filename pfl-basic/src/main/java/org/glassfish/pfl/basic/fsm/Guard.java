/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.fsm;

import org.glassfish.pfl.basic.func.BinaryFunction;
import org.glassfish.pfl.basic.func.BinaryPredicate;

/**
 *
 * @author Ken Cavanaugh
 */
public interface Guard {
    enum Result { ENABLED, DISABLED, DEFERRED } ;

    /** Called by the state engine to determine whether a
    * transition is enabled, deferred, or disabled.
    * The result is interpreted as follows:
    * <ul>
    * <li>ENABLED if the transition is ready to proceed
    * <li>DISABLED if the transition is not ready to proceed
    * <li>DEFERED if the action associated with the transition
    * is to be deferred.  This means that the input will not be 
    * acted upon, but rather it will be saved for later execution.
    * Typically this is implemented using a CondVar wait, and the
    * blocked thread represents the deferred input.  The deferred
    * input is retried when the thread runs again.
    * </ul>
    *
    * @param fsm is the state machine causing this action.
    * @param in is the input that caused the transition.
    */
    public Result evaluate( FSM fsm, Input in ) ;

    public abstract class Base extends NameBase implements Guard {
	public static abstract class SimpleName {
	    private String name ;

	    public SimpleName( String name ) {
		this.name = name ;
	    }

            @Override
	    public String toString() {
		return name ;
	    }
	}

	public static abstract class Predicate extends SimpleName 
	    implements BinaryPredicate<FSM,Input> {

	    public Predicate( String name ) {
		super( name ) ;
	    }
	}

	public static abstract class IntFunc extends SimpleName 
	    implements BinaryFunction<FSM,Input,Integer> {

	    public IntFunc( String name ) {
		super( name ) ;
	    }
	}

	public static Guard makeGuard( final Predicate pred ) {
	    return new Guard.Base( pred.toString() ) {
                @Override
		public Guard.Result evaluate( FSM fsm, Input in ) {
		    return pred.evaluate( fsm, in ) ?
			Result.ENABLED : Result.DISABLED ;
		}
	    } ;
	}

	public static Predicate not( final Predicate pred ) {
	    return new Predicate( "!" + pred.toString() ) {
                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    return !pred.evaluate( fsm, in ) ;
		}
	    } ;
	}

	public static Predicate and( final Predicate arg1, final Predicate arg2 ) {
	    return new Predicate( "(" + arg1.toString() + "&&" + arg2.toString() + ")" ) {
                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    if (!arg1.evaluate( fsm, in )) {
                        return false;
                    } else {
                        return arg2.evaluate(fsm, in);
                    }
		}
	    } ;
	}

	public static Predicate or( final Predicate arg1, final Predicate arg2 ) {
	    return new Predicate( "(" + arg1.toString() + "||" + arg2.toString() + ")" ) {
                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    if (arg1.evaluate( fsm, in )) {
                        return true;
                    } else {
                        return arg2.evaluate(fsm, in);
                    }
		}
	    } ;
	}

	public static IntFunc constant( final int val ) {
	    return new IntFunc( "constant(" + val + ")" ) {
                @Override
		public Integer evaluate( final FSM fsm, final Input input ) {
		    return val ;
		}
	    } ;
	}

	/* This does not seem to be worthwhile
	public static IntFunc field( final Class cls, final String fieldName ) {
	    final Field fld = cls.getField( fieldName ) ;

	    return new IntFunc( cls + "." + fieldName ) {
		public boolean evaluate( final FSM fsm, final Input in ) {
		    // check that fsm is an instance of cls
		    return fld.getInt( fsm ) ;
		}
	    }
	}
	*/

	public static Predicate lt( final IntFunc arg1, final IntFunc arg2 ) {
	    return new Predicate( "(" + arg1.toString()
		+ "<" + arg2.toString() + ")" ) {

                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    return arg1.evaluate( fsm, in ) < arg2.evaluate( fsm, in ) ;
		}
	    } ;
	}

	public static Predicate le( final IntFunc arg1, final IntFunc arg2 ) {
	    return new Predicate( "(" + arg1.toString()
		+ "<=" + arg2.toString() + ")" ) {
		
                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    return arg1.evaluate( fsm, in ) <= arg2.evaluate( fsm, in ) ;
		}
	    } ;
	}

	public static Predicate gt( final IntFunc arg1, final IntFunc arg2 ) {
	    return new Predicate( "(" + arg1.toString()
		+ ">" + arg2.toString() + ")" ) {
		
                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    return arg1.evaluate( fsm, in ) > arg2.evaluate( fsm, in ) ;
		}
	    } ;
	}

	public static Predicate ge( final IntFunc arg1, final IntFunc arg2 ) {
	    return new Predicate( "(" + arg1.toString() 
		+ ">=" + arg2.toString() + ")" ) {

                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    return arg1.evaluate( fsm, in ) >= arg2.evaluate( fsm, in ) ;
		}
	    } ;
	}

	public static Predicate eq( final IntFunc arg1, final IntFunc arg2 ) {
	    return new Predicate( "(" + arg1.toString() 
		+ "==" + arg2.toString() + ")" ) {

                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    return arg1.evaluate( fsm, in ) == arg2.evaluate( fsm, in ) ;
		}
	    } ;
	}

	public static Predicate ne( final IntFunc arg1, final IntFunc arg2 ) {
	    return new Predicate( "(" + arg1.toString() 
		+ "!=" + arg2.toString() + ")") {

                @Override
		public boolean evaluate( final FSM fsm, final Input in ) {
		    return arg1.evaluate( fsm, in ) != arg2.evaluate( fsm, in ) ;
		}
	    } ;
	}

	public Base( String name ) { super( name ) ; } 
    }
}

// end of Action.java


