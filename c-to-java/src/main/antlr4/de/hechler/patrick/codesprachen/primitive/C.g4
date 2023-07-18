//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
 grammar C;

 @parser::header {
//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.

import java.util.*;
import java.math.*;
import java.nio.charset.*;
import de.hechler.patrick.codesprachen.primitive.ctj.*;
}

 tu [CTU ctu]
 :
 	typedef[ctu]
 	|
 	type[ctu]
 	|
 	variable[ctu]
 	|
 	function[ctu]
 ;

 typedef [CTU ctu]
 :
	 TYPEDEF
	 type[ctu]
	 SEMI
	 {ctu.typedef($type.ct);}
 ;

 type [CTU ctu] returns [CType ct]
 :
 	{List<String> typeName = new ArrayList<>();}
 	(
	 	NAME
	 	{typeName.add($NAME.getText());}
 	)*
 	{$ct = ctu.newType(typeName);}
	(
		fn = typeAddName [ctu, ct]
		{$ct = $fn.nct;}
		(
			ASSIGN e = expression[ctu]
			{$ct = $ct.assign();}
		)?
		(
			COMMA
			fn = typeAddName [ctu, ct]
			{$ct = $fn.nct;}
			(
				ASSIGN e = expression[ctu]
				{$ct = $ct.assign();}
			)?
		)*
 	)?
 ;

 typeAddName [CTU ctu, CType ct] returns [CType nct]
 :
	ROUND_OPEN ft = typeOptName [ctu] ROUND_CLOSE
	{CTypeName funcName = $ft.ctname;}
	ROUND_OPEN
	{List<CType> params = new ArrayList<>();} 
	(
		fp = type[ctu]
		{params.add($fp.ct);}
		(
			COMMA
			op = type[ctu]
			{params.add($op.ct);}
		)*
	)?
	ROUND_CLOSE
	{$ct = $ct.addFuncType(funcName, params);}
	|
	ton = typeOptName[ctu]
	{$ct = $ct.addName($ton.ctname);}
 ;

 typeWithName [CTU ctu] returns [CTypeName ctname]
 :
	{int pointerCnt = 0;}
	(
		STAR
		{pointerCnt++;}
	)*
	{$ctname = new CTypeName(pointerCnt);}
	(
		NAME
		{$ctname = $ctname.initName($NAME.getText());}
		|
		ROUND_OPEN
		inner = typeWithName [ctu]
		{$ctname = $ctname.initName($inner.ctname);}
		ROUND_CLOSE
	)
	(
		ARR_OPEN
		(
			e = expression [ctu]
		)?
		ARR_CLOSE
		{$ctname = $ctname.makeArray($e.val);}
	)*
 ;
 
 typeOptName [CTU ctu] returns [CTypeName ctname]
 :
 	twn = typeWithName [ctu]
 	{$ctname = $twn.ctname;}
	|
	{int pointerCnt = 0;}
	(
		STAR
		{pointerCnt++;}
	)+
	{$ctname = new CTypeName(pointerCnt);}
	(
		ARR_OPEN
		(
			e = expression [ctu]
		)?
		ARR_CLOSE
		{$ctname = $ctname.makeArray($e.val);}
	)*
	|
	{$ctname = new CTypeName(0);}
	(
		ARR_OPEN
		(
			e = expression [ctu]
		)?
		ARR_CLOSE
		{$ctname = $ctname.makeArray($e.val);}
	)+
 ;

 expression [CTU ctu] returns [CValue val]
 :
 	ce = conditionalExp[ctu]
 	{$val = $ce.val;}
 	|
 	ue = unaryExp[ctu]
 	{int assignType = 0;}
 	(
 		ASSIGN_MUL
 		{assignType = CValue.ASSIGN_MUL;}
		| ASSIGN_DIV
 		{assignType = CValue.ASSIGN_DIV;}
		| ASSIGN_MOD
 		{assignType = CValue.ASSIGN_MOD;}
		| ASSIGN_ADD
 		{assignType = CValue.ASSIGN_ADD;}
		| ASSIGN_SUB
 		{assignType = CValue.ASSIGN_SUB;}
		| ASSIGN_RSHIFT
 		{assignType = CValue.ASSIGN_RSHIFT;}
		| ASSIGN_LSHIFT
 		{assignType = CValue.ASSIGN_LSHIFT;}
		| ASSIGN_AND
 		{assignType = CValue.ASSIGN_AND;}
		| ASSIGN_XOR
 		{assignType = CValue.ASSIGN_XOR;}
		| ASSIGN_OR
 		{assignType = CValue.ASSIGN_OR;}
		| ASSIGN
 		{assignType = CValue.ASSIGN_ASSIGN;}
 	)
 	e = expression[ctu]
 	{$val = $ue.addAsign(assignType, $e.val);}
 	
 ;
 
 conditionalExp [CTU ctu] returns [CValue val]
 :
 	boolOrExp[ctu]
 	(
 		QUESTION
 		expression[ctu]
 		COLON
 		conditionalExpression[ctu]
 	)?
 ;
 
 boolOrExp [CTU ctu] returns [CValue val]
 :
 	ae = boolAndExp[ctu]
 	{$val = $ae.val;}
 	(
 		AND_AND
 		oe = boolAndExp[ctu]
 		{$val = $val.addBoolOr($oe.val);
 	)*
 ;
 
 boolAndExp [CTU ctu] returns [CValue val]
 :
 	ae = inclusiveOrExp[ctu]
 	{$val = $ae.val;}
 	(
 		AND_AND
 		oe = inclusiveOrExp[ctu]
 		{$val = $val.addBoolAnd($oe.val);
 	)*
 ;
  
 inclusiveOrExp [CTU ctu] returns [CValue val]
 :
 	ae = exclusiveOrExp[ctu]
 	{$val = $ae.val;}
 	(
 		OR
 		oe = exclusiveOrExp[ctu]
 		{$val = $val.addLogicOr($oe.val);
 	)*
 ;
  
 exclusiveOrExp [CTU ctu] returns [CValue val]
 :
 	ae = andExp[ctu]
 	{$val = $ae.val;}
 	(
 		XOR
 		oe = andExp[ctu]
 		{$val = $val.addLogicXor($oe.val);
 	)*
 ;
 
 andExp [CTU ctu] returns [CValue val]
 :
 	ee = equalityExp[ctu]
 	{$val = $ee.val;}
 	(
 		AND
 		oe = relationalExp[ctu]
 		{$val = $val.addLogicAnd($oe.val);
 	)*
 ;
 
 equalityExp [CTU ctu] returns [CValue val]
 :
 	fe = relationalExp[ctu]
 	{$val = $fe.val;}
 	(
 		{int eqType = 0;}
 		(
 			EQUAL
 			{eqType = CValue.EQUAL_EQUAL;}
 			| NOT_EQUAL
  			{eqType = CValue.EQUAL_NOT_EQ;}
 		)
 		oe = relationalExp[ctu]
 		{$val = $val.addEqual(eqType, $oe.val);
 	)*
 ;
 
 relationalExp [CTU ctu] returns [CValue val]
 :
 	fe = shiftExp[ctu]
 	{$val = $fe.val;}
 	(
 		{int relType = 0;}
 		(
 			GREATHER
 			{relType = CValue.REL_GREATHER;}
 			| LOWER
  			{relType = CValue.REL_LOWER;}
  			| GREATHER_EQUAL
  			{relType = CValue.REL_GREATER_EQUAL;}
  			| LOWER_EQUAL
  			{relType = CValue.REL_LOWER_EQUAL;}
 		)
 		oe = shiftExp[ctu]
 		{$val = $val.addRelational(relType, $oe.val);
 	)*
 ;
 
 shiftExp [CTU ctu] returns [CValue val]
 :
 	fe = additiveExp[ctu]
 	{$val = $fe.val;}
 	(
 		{int shftType = 0;}
 		(
 			LEFT_SHIFT
 			{shftType = CValue.SHFT_LEFT;}
 			| RIGHT_SHIFT
  			{shftType = CValue.SHFT_RIGTH;}
 		)
 		oe = additiveExp[ctu]
 		{$val = $val.addShift(shftType, $oe.val);
 	)*
 ;
 
 additiveExp [CTU ctu] returns [CValue val]
 :
 	fe = multiplicativeExp[ctu]
 	{$val = $fe.val;}
 	(
 		{int addType = 0;}
 		(
 			PLUS
 			{addType = CValue.ADD_PLUS;}
 			| MINUS
  			{addType = CValue.ADD_MINUS;}
 		)
 		oe = multiplicativeExp[ctu]
 		{$val = $val.addAdd(addType, $oe.val);
 	)*
 ;
 
 multiplicativeExp [CTU ctu] returns [CValue val]
 :
 	fe = castExp[ctu]
 	{$val = $fe.val;}
 	(
 		{int mulType = 0;}
 		(
 			STAR
 			{mulType = CValue.MUL_MUL;}
 			| DIV
  			{mulType = CValue.MUL_DIV;}
 			| MOD
  			{mulType = CValue.MUL_MOD;}
 		)
 		oe = castExp[ctu]
 		{$val = $val.addMul(mulType, $oe.val);}
 	)*
 ;
 
 castExp [CTU ctu] returns [CValue val]
 :
 	ue = unaryExp[ctu]
 	{$val = $ue.val;}
 	|
 	ROUND_OPEN ton = typeOptName[ctu] ROUND_CLOSE ce = castExp[ctu]
 	{$val = $ce.val.addCast($ton.ctname);}
 ;
 
 unaryExp [CTU ctu] returns [CValue val]
 :
 	{int unaryType0 = 0;}
 	(
		PLUS_PLUS
		{unaryType0 = CValue.UNARY0_PLUS_PLUS;}
		| MINUS_MINUS
		{unaryType0 = CValue.UNARY0_MINUS_MINUS;}
	)?
 	(
 		pe = postfixExp[ctu]
 		{$val = $pe.val;}
	 	|
	 	{int unaryType1 = 0;} 
	 	(
		 	AND
		 	{unaryType1 = CValue.UNARY1_AND;}
		 	| STAR
		 	{unaryType1 = CValue.UNARY1_DEREF;}
		 	| PLUS
		 	{unaryType1 = CValue.UNARY1_PLUS;}
		 	| MINUS
		 	{unaryType1 = CValue.UNARY1_MINUS;}
		 	| LOGIC_NOT
		 	{unaryType1 = CValue.UNARY1_LOGIC_NOT;}
		 	| BOOL_NOT
		 	{unaryType1 = CValue.UNARY1_BOOL_NOT;}
	 	)
	 	ce = castExp[ctu]
	 	{$val = $ce.addUnary(unaryType0, unaryType1);}
	 )
 ;
 
 postfixExp [CTU ctu] returns [CValue val]
 :
 	primaryExp[ctu]
 	(
 		DOT n = NAME
 		{$val = $val.derefName(false, $n.getText());}
 		| ARROW n = NAME
 		{$val = $val.derefName(true, $n.getText());}
 		| PLUS_PLUS
 		{$val = $val.plusPlus(false);}
 		| MINUS_MINUS
 		{$val = $val.minusMinus(false);}
 	)*
 ;
 
 primaryExp [CTU ctu] returns [CValue val]
 :
 	NUMBER
 	{$val = CValue.number($NUMBER.getText());}
 	|
 	{List<String> strings = new ArrayList<>();}
	(
	 	STRING
	 	{strings.add($STRING.getText());}
 	)+
 	{$val = CValue.string(strings);}
 	|
 	CHAR
 	{$val = CValue.char($CHAR.getText());}
 	|
 	NAME
 	{$val = ctu.nameValue($NAME.getText());}
 	|
 	ROUND_OPEN
 	expression [ctu]
 	{$val = $expression.val.withBraces();}
 	ROUND_CLOSE
 	| VA_ARG ROUND_OPEN ue = unaryExpression [ctu] COMMA n = NAME ROUND_CLOSE
 	{$val = ctu.vaArg($ue.val, $n.getText());}
 	| OFFSETOF ROUND_OPEN n0 = NAME (n1 = NAME)? COMMA n = NAME ROUND_CLOSE
 	{$val = ctu.offsetof($n0.getText(), $n1 != null ? $n1.getText() : null, $n.getText());}
 	// TODO _Generic
 ;
 
 variable [CTU ctu] returns [List<CVariable> cvars]
 :
 	twn = type[ctu] SEMI
 	{$cvars = CVariable.vars($twn.ctname);}
 ;

 function [CTU ctu] returns [CFunc cfunc]
 :
 	t = type[ctu] n = NAME ROUND_OPEN
 	{List<CType> params = new ArrayList<>();}
 	(
 		t = type[ctu]
 		(
 			COMMA t = type[ctu]
 		)*
 	)?
 	ROUND_CLOSE
 	(
 		SEMI
 		{$cfunc = null;}
 		|
 		{$cfunc = ctu.createFunc($t.ct, $n.getText(), params);}
 		block[ctu, $func.pool()]
 	)
 ;

 block [CTU ctu, CPool pool]
 :
 	{ctu.enterBlock(pool);}
 	BLK_OPEN
 	(
 		command [ctu, pool]
 	)*
 	BLK_CLOSE
 	{ctu.exitBlock(pool);}
 ;
 
 command [CTU ctu, CPool pool]
 :
	SEMI
	{pool.addSemi();}
	| e = expression[ctu]
	{pool.addExp($e.val);}
	| c = CONDITIONAL ROUND_OPEN e = expression[ctu] ROUND_CLOSE
	command [ctu, pool.conditional($c.getText(), $e.val)]
	| n = USE_DIRECTLY
	{pool.addSymbol($n.getText());}
	| FOR ROUND_OPEN (inite = expression[ctu] | initt = type[ctu])? SEMI e = expression[ctu]? SEMI
	{List<CValue>inc = new ArrayList<>();} 
	(
		i = expression[ctu]
		{inc.add($i.val);}
		(
			COMMA
			i = expression[ctu]
			{inc.add($i.val);}
		)*
	)? ROUND_CLOSE
	command [ctu, pool.forLoop($c.getText(), $inite != null ? $inite.val : $initt != null ? $initt.val : null, $e != null ? $e.val : null, inc)]
	| SWITCH ROUND_OPEN e = expression[ctu] ROUND_CLOSE BLK_OPEN
	{CSwtich s = pool.addSwitch($e.val);}
	(
		DEFAULT COLON
		{s.addDefault();}
		|
		CASE e = expression[ctu] COLON
		{s.addCase($e.val);}
		|
		command[ctu, s.pool()]
	)*
	BLK_CLOSE
 ;

 VA_ARG  : '__builtin_va_arg' ;
 OFFSETOF  : '__builtin_offsetof' ;
 SIZEOF  : 'sizeof' ;
 
 CONDITIONAL  : 'while' | 'if' ;
 USE_DIRECTLY  : 'else' | 'do' | 'break' | 'return' | 'continue' ;
 SWITCH  : 'switch' ;
 CASE  : 'case' ;
 default  : 'default' ;
 
 ROUND_OPEN  : '(' ;
 ROUND_CLOSE : ')' ;
 BLK_OPEN    : '{' ;
 BLK_CLOSE   : '{' ;
 ARR_OPEN    : '[' ;
 ARR_CLOSE   : ']' ;
 
 LEFT_SHIFT  : '<<' ;
 RIGHT_SHIFT : '>>' ;
 
 PLUS_PLUS : '++' ;
 MINUS_MINUS : '--' ;
 ARROW : '->' ;
 DOT : '.' ;
 
 OR_OR : '||' ;
 AND_AND : '&&' ;
 
 OR : '|' ;
 XOR : '^' ;
 AND : '&' ;
 LOGIC_NOT : '~' ;
 BOOL_NOT : '!' ;
 
 STAR : '*' ;
 DIV : '/' ;
 MOD : '%' ;
 PLUS : '+' ;
 MINUS : '-' ;

 ASSIGN_MUL    : '*=';
 ASSIGN_DIV    : '/=';
 ASSIGN_MOD    : '%=';
 ASSIGN_ADD    : '+=';
 ASSIGN_SUB    : '-=';
 ASSIGN_RSHIFT : '<<=';
 ASSIGN_LSHIFT : '>>=';
 ASSIGN_AND    : '&=';
 ASSIGN_XOR    : '^=';
 ASSIGN_OR     : '|=';
 ASSIGN        : '=';
 
 COMMA : ',' ;
 COLON : ':' ;
 SEMI  : ';' ;
 
 TYPEDEF : 'typedef' ;

 NUMBER
 : 
 	(
	 	[1-9] [0-9]*
	 	(
	 		'.' [0-9]*
	 	)?
	 	|
	 	'.' [0-9]+
	 	|
	 	'0' [0-7]*
	 	(
	 		'.' [0-7]*
	 	)?
	 	|
	 	'0x' [A-F0-9a-f]+
	 	(
	 		'.' [A-F0-9a-f]*
	 	)?
	 	|
	 	'0b' [01]+
	 	(
	 		'.' [01]*
	 	)?
	 )
 	[uUbBsSlLfFdD]*
 ;
 
 LINE_COMMENT
 :
 	(
 	'//'
 	| '#' // generated by the preprocessor
 	)
 	(
 		~( [\r\n] )
 	)* -> skip
 ;

 BLOCK_COMMENT
 :
 	'/*'
 	(
 		~'*'
 		|
 		(
 			'*' ~'/'
 		)
 	)* '*/' -> skip
 ;

 WS
 :
 	[ \t\r\n]+ -> skip
 ;
