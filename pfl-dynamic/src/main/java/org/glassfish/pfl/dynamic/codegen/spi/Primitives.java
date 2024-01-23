/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.spi;

import org.glassfish.pfl.dynamic.codegen.impl.ExpressionInternal;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;

public class Primitives {
    private Primitives() {}

    // If expr.type() is not a primitive, return expr.
    // If expr.type() is a primitive, return an expression
    // that wraps expr in the appropriate primitive wrapper class.
    public static Expression wrap( Expression expr ) {
	Type etype = ExpressionInternal.class.cast(expr).type() ;
	Signature sig = _s(_void(),etype) ;

	if (etype.equals( _boolean() ) )
	    return _new( _t("java.lang.Boolean"), sig, expr ) ;
	else if (etype.equals( _byte() ) )
	    return _new( _t("java.lang.Byte"), sig, expr ) ;
	else if (etype.equals( _char() ) )
	    return _new( _t("java.lang.Character"), sig, expr ) ;
	else if (etype.equals( _short() ) )
	    return _new( _t("java.lang.Short"), sig, expr ) ;
	else if (etype.equals( _int() ) )
	    return _new( _t("java.lang.Integer"), sig, expr ) ;
	else if (etype.equals( _long() ) )
	    return _new( _t("java.lang.Long"), sig, expr ) ;
	else if (etype.equals( _float() ) )
	    return _new( _t("java.lang.Float"), sig, expr ) ;
	else if (etype.equals( _double() ) )
	    return _new( _t("java.lang.Double"), sig, expr ) ;
	else
	    return expr ;
    }

    public static Type getWrapperTypeForPrimitive( Type type ) {
	if (type.equals( _boolean() ) )
	    return _t("java.lang.Boolean");
	else if (type.equals( _byte() ) )
	    return _t("java.lang.Byte");
	else if (type.equals( _char() ) )
	    return _t("java.lang.Character");
	else if (type.equals( _short() ) )
	    return _t("java.lang.Short");
	else if (type.equals( _int() ) )
	    return _t("java.lang.Integer");
	else if (type.equals( _long() ) )
	    return _t("java.lang.Long");
	else if (type.equals( _float() ) )
	    return _t("java.lang.Float");
	else if (type.equals( _double() ) )
	    return _t("java.lang.Double");
	else
	    return type ;
    }

    // If expr.type() is a primitive wrapper, unwrap it.
    // If expr.type() is not a primitive wrapper,
    // return it.
    public static Expression unwrap( Expression expr ) {
	Type etype = ExpressionInternal.class.cast(expr).type() ;

	if (etype.equals( _t("java.lang.Boolean")))
	    return _call( expr, "booleanValue", _s(_boolean()) ) ;
	else if (etype.equals( _t("java.lang.Byte") ))
	    return _call( expr, "byteValue", _s(_byte()) ) ;
	else if (etype.equals( _t("java.lang.Character") ))
	    return _call( expr, "charValue", _s(_char()) ) ;
	else if (etype.equals( _t("java.lang.Short") ))
	    return _call( expr, "shortValue", _s(_short()) ) ;
	else if (etype.equals( _t("java.lang.Integer") ))
	    return _call( expr, "intValue", _s(_int()) ) ;
	else if (etype.equals( _t("java.lang.Long") ))
	    return _call( expr, "longValue", _s(_long()) ) ;
	else if (etype.equals( _t("java.lang.Float") ))
	    return _call( expr, "floatValue", _s(_float()) ) ;
	else if (etype.equals( _t("java.lang.Double") ))
	    return _call( expr, "doubleValue", _s(_double()) ) ;
	else return expr ;
    }

    public static Type getPrimitiveTypeForWrapper( Type type) {
	if (type.equals( _t("java.lang.Boolean")))
	    return _boolean() ;
	else if (type.equals( _t("java.lang.Byte") ))
	    return _byte() ;
	else if (type.equals( _t("java.lang.Character") ))
	    return _char() ;
	else if (type.equals( _t("java.lang.Short") ))
	    return _short() ;
	else if (type.equals( _t("java.lang.Integer") ))
	    return _int() ;
	else if (type.equals( _t("java.lang.Long") ))
	    return _long() ;
	else if (type.equals( _t("java.lang.Float") ))
	    return _float() ;
	else if (type.equals( _t("java.lang.Double") ))
	    return _double() ;
	else
	    return type ;
    }
}
