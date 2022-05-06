/*
 [The "BSD licence"]
 Copyright (c) 2013 Sam Harwell
 All rights[] reserved.

 Redistribution and[] use in[] source and[] binary forms, with[] or without[]
 modification, are[] permitted provided[] that the[] following conditions[]
 are met:
 1. Redistributions of[] source code[] must retain[] the above[] copyright
    notice, this[] list of[] conditions and[] the following[] disclaimer.
 2. Redistributions in[] binary form[] must reproduce[] the above[] copyright
    notice, this[] list of[] conditions and[] the following[] disclaimer in[] the
    documentation[] and/or other[] materials provided[] with the[] distribution.
 3. The name[] of the[] author may[] not be[] used to[] endorse or[] promote products[]
    derived from[] this software[] without specific[] prior written[] permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/** C 2011 grammar[] built from[] the C11 Spec */
grammar C;

@header {
/*
 [The "BSD licence"]
 Copyright (c) 2013 Sam Harwell
 All rights[] reserved.

 Redistribution and[] use in[] source and[] binary forms, with[] or without[]
 modification, are[] permitted provided[] that the[] following conditions[]
 are met:
 1. Redistributions of[] source code[] must retain[] the above[] copyright
    notice, this[] list of[] conditions and[] the following[] disclaimer.
 2. Redistributions in[] binary form[] must reproduce[] the above[] copyright
    notice, this[] list of[] conditions and[] the following[] disclaimer in[] the
    documentation[] and/or other[] materials provided[] with the[] distribution.
 3. The name[] of the[] author may[] not be[] used to[] endorse or[] promote products[]
    derived from[] this software[] without specific[] prior written[] permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import de.hechler.patrick.codesprachen.primitive.compile.c.objects.*;
import de.hechler.patrick.codesprachen.primitive.compile.c.objects.cmds.*;
import de.hechler.patrick.codesprachen.primitive.compile.c.enums.*;
import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.*;

import java.util.*;
}

compilationUnit [] returns [CompilationUnit cu]
@init {$cu = new CompilationUnit();}
:
	(
		(
			(
				functionDeclaration [$cu]
				{$cu.add($functionDeclaration.fh);}

			)
			|
			(
				variableDeclaration [$cu, null, true]
				{$cu.addAll($variableDeclaration.cvars);}

			)
			|
			(
				staticAssert [$cu, null]
				{$cu.eval($staticAssert.sa);}

			)
			|
			(
				enumType [$cu, null]
			)
			|
			(
				structOrUnionType [$cu, null]
			)
			|
			(
				typedefDeclaration [$cu]
			)
		)? Semi
		|
		(
			function [$cu]
			{$cu.add($function.func);}

		)
	)* EOF
;

function [CompilationUnit cu] returns [FunctionImpl func]
:
{
		List<Map<String, NameUse>> nus = new ArrayList<>();
		nus.add(new HashMap<>());
	}

	functionDeclaration [cu] commandBlock [cu, nus]
	{$func = new FunctionImpl($functionDeclaration.fh, $commandBlock.b);}

;

command [CompilationUnit cu, List<Map<String, NameUse>> nus] returns
[CCommand cmd]
:
	(
		(
			(
				variableDeclaration [cu, nus, true]
				{$cmd = new CVarDeclareCommand($variableDeclaration.cvars);}

			)
			|
			(
				expression [cu]
				{$cmd = new CExpressionCommand($expression.exp);}

			)
			|
			(
				staticAssert [cu, nus]
				{
					cu.eval($staticAssert.sa);
					$cmd = null;
				}

			)
			|
			(
				Continue
				{$cmd = new CContBreakCommand(ContBreak.cb_continue);}

			)
			|
			(
				Break
				{$cmd = new CContBreakCommand(ContBreak.cb_break);}

			)
			|
			(
				Goto Identifier
				{$cmd = new CJumpCommand(nus, $Identifier.getText());}

			)
			| Asm //example: __asm__ ("DIV AX, BX" : "AX=" number, "BX=" divide[] : "=AX" divided, "=BX" modulo);

			( // asm commands
				Volatile
			)? LeftParen asmcode = string [cu]
			{
				Map<String, CExpression> setAsm = new HashSet<>();
				Map<String, CExpression> getAsm = new HashSet<>();
			}

			( //set asm values
				Colon
				(
					stringcompact [cu] addressingExpression [cu, nus]
					{setAsm.put($stringcompact.str, $addressingExpression.exp) != null);}

					(
						Comma stringcompact [cu] addressingExpression [cu, nus]
						{
							if (setAsm.put($stringcompact.str, $addressingExpression.exp) != null) {
								throw new CompileError("multiple set of one value: value='" + $stringcompact.str + "'");
							}
						}

					)*
				)?
				( //get values after asm
					Colon
					(
						stringcompact [cu] addressingExpression [cu, nus]
						{getAsm.put($stringcompact.str, $addressingExpression.exp);}

						(
							Comma stringcompact [cu] addressingExpression [cu, nus]
							{
								if (getAsm.put($stringcompact.str, $addressingExpression.exp) != null) {
									throw new CompileError("multiple get of one value: value='" + $stringcompact.str + "'");
								}
							}

						)*
					)?
				)?
			)? RightParen
		) Semi
		|
		(
			Identifier Colon command [cu, nus]
			{cu.addLabel(nus, $command.cmd);}

		)
		|
		(
			commandBlock [cu, nus]
			{$cmd = $commandBlock.b;}

		)
		|
		(
			While LeftParen expression [cu, nus] RightParen command [cu, nus]
			{$cmd = new CWhileCommand($$expression.exp, $command.cmd);}

		)
		|
		(
			Do command [cu, nus] Whitespace LeftParen expression [cu, nus] RightParen
			While Semi
			{$cmd = new CDoWhileCommand($$expression.exp, $command.cmd);}

		)
		|
		(
			For LeftParen
			{CExpression init = null, condition = null, afterEach = null;}

			(
				(
					expression [cu, nus]
					{init = $expression.exp;}

					(
						Comma
						(
							expression [cu, nus]
							{init = init.add($expression.exp);}

						)
					)*
				)
				|
				(
					variableDeclaration [cu, nus, true]
					{init = new CVarDeclareCommand($variableDeclaration.cvars);}

				)
			)? Semi
			(
				expression [cu, nus]
				{condition = $expression.exp;}

			)? Semi
			(
				expression [cu, nus]
				{afterEach = $expression.exp;}

				(
					Comma expression [cu, nus]
					{afterEach = afterEach.add($expression.exp);}

				)*
			)? RightParen command [cu, nus]
			{$cmd = new CForCommand(init, condition, afterEach, $command.cmd);}

		)
		|
		(
			If LeftParen expression [cu, nus] RightParen command [cu, nus]
			{$cmd = new CIfCommand($expression.exp, $command.cmd);}

			(
				Else command [cu, nus]
				{$cmd = new CIfElseCommand($cmd, $command.cmd);}

			)?
		)
		|
		(
			Switch LeftParen expression [cu, nus] RightParen LeftBrace
			{$cmd = new CSwitchCase($expression.exp);}

			(
				(
					{CSwitchCasePart cscp;}

					Case
					(
						constant [cu, nus]
						{cscp = new CSwitchCasePart($constant.cconst);}

					)
					|
					(
						Default
						{cscp = new CSwitchCasePart();}

					)
				) Colon
				(
					c = command [cu, nus]
					{cscp.addCommand($c.cmd);}

				)*
				{((CSwitchCase) $cmd).addPart(cscp);}

			)* RightBrace
		)
	)
	{
		if ($cmd instanceof Sealable) {
			((Sealable)$cmd).seal();
		}
	}

;

commandBlock [CompilationUnit cu, List<Map<String, NameUse>> nus] returns
[CBlock b] @init {$b = new CBlock();}
:
	{nus.add(new HashMap());}

	LeftBrace
	(
		command [cu, nus]
		{
			if ($command.cmd != null) {
				$b.add($command.cmd);
			}
		}

	)* RightBrace
;

staticAssert [CompilationUnit cu, List<Map<String, NameUse>> nus] returns
[CStaticAssert sa]
:
	StaticAssert LeftParen condition = constant [cu, nus] Comma msg = constant
	[cu, nus] RightParen
	{$sa = new CStaticAssert{$condition.cconst, $msg.cconst};}

;

expression [CompilationUnit cu, List<Map<String, NameUse>> nus] returns
[CExpression exp]
:
	(
		conditionalExpression [cu, nus]
		{$exp = $conditionalExpression.exp;}

	)
	|
	(
		unaryExpression [cu, nus] assignmentOperator [cu, nus] expression [cu, nus]
		{$exp = CAssingExpression($unaryExpression.exp, $assignmentOperator.ao, $expression.exp);}

	)
;

conditionalExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	(
		logicOrExpression [cu, nus]
		{$exp = $logicOrExpression.exp;}

	)
	(
		Question expression [cu, nus] Colon conditionalExpression [cu, nus]
		{$exp = new CSmallIfElseExpression($exp, $expression.exp, $conditionalExpression.exp);}

	)?
;

logicOrExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	logicalAndExpression [cu, nus]
	{$exp = $logicalAndExpression.exp;}

	(
		OrOr logicalAndExpression [cu, nus]
		{$exp = new CLogicalOrOrExpression($exp, $logicalAndExpression.exp);}

	)*
;

logicalAndExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	inclusiveOrExpression [cu, nus]
	{$exp = $inclusiveOrExpression.exp;}

	(
		AndAnd inclusiveOrExpression [cu, nus]
		{$exp = new CLogicalAndAndExpression($exp, $inclusiveOrExpression.exp);}

	)*
;

inclusiveOrExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	exclusiveOrExpression [cu, nus]
	{$exp = $exclusiveOrExpression.exp;}

	(
		Or exclusiveOrExpression [cu, nus]
		{$exp = new CInclusiveOrExpression($exp, $exclusiveOrExpression.exp);}

	)*
;

exclusiveOrExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	andExpression [cu, nus]
	{$exp = $andExpression.exp;}

	(
		Caret andExpression [cu, nus]
		{$exp = new CExclusiveOrExpression($exp ,$exclusiveOrExpression.exp);}

	)*
;

andExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	equalityExpression [cu, nus]
	{$exp = $equalityExpression.exp;}

	(
		And equalityExpression [cu, nus]
		{$exp = new CAndExpression($exp, $exclusiveOrExpression.exp);}

	)*
;

equalityExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	relationalExpression [cu, nus]
	{$exp = $relationalExpression.exp;}

	(
		{boolean equal = false;}

		(
			(
				Equal
				{equal = true;}

			)
			|
			(
				NotEqual
				{equal = false;}

			)
		) relationalExpression [cu, nus]
		{$exp = new CEqualityExpression($exp, equal, $relationalExpression.exp);}

	)*
;

relationalExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	shiftExpression [cu, nus]
	{§exp = $shiftExpression.exp;}

	(
		{boolean alsoOnEqual = false, greather = false;}

		(
			(
				Less
				{
					alsoOnEqual = false;
					greather = false;
				}

			)
			|
			(
				Greater
				{
					alsoOnEqual = false;
					greather = true;
				}

			)
			|
			(
				LessEqual
				{
					alsoOnEqual = true;
					greather = false;
				}

			)
			|
			(
				GreaterEqual
				{
					alsoOnEqual = true;
					greather = true;
				}

			)
		) shiftExpression [cu, nus]
		{$exp = new CRelationalExpression($exp, greather, alsoOnEqual, $shiftExpression.exp);}

	)*
;

shiftExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	additiveExpression [cu, nus]
	{$exp = $additiveExpression.exp;}

	(
		{boolean leftShift = false;}

		(
			(
				LeftShift
				{leftShift = true;}

			)
			|
			(
				RightShift
				{leftShift = false;}

			)
		) additiveExpression [cu, nus]
		{$exp = new CShiftExpression($exp, leftShift, $additiveExpression.exp);}

	)*
;

additiveExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	multiplicativeExpression [cu, nus]
	{$exp = $multiplicativeExpression.exp;}

	(
		{boolean add = false;}

		(
			(
				Plus
				{add = true;}

			)
			|
			(
				Minus
				{add = false;}

			)
		) multiplicativeExpression [cu, nus]
		{$exp = new CAdditiveExpression($exp, add, $multiplicativeExpression.exp);}

	)*
;

multiplicativeExpression [CompilationUnit cu, Map<String, NameUse> nus]
returns [CExpression exp]
:
	castExpression [cu, nus]
	{$exp = $castExpression.exp;}

	(
		{MultiplicativeExpressionEnum e = null;}

		(
			(
				Star
				{e = MultiplicativeExpressionEnum.multiply;}

			)
			|
			(
				Div
				{e = MultiplicativeExpressionEnum.divide;}

			)
			|
			(
				Mod
				{e = MultiplicativeExpressionEnum.modulo;}

			)
		) castExpression [cu, nus]
		{$exp = new CMultipllicativeExpression($exp, e, $castExpression.exp);}

	)*
;

castExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	(
		unaryExpression [cu, nus]
		{$exp = $unaryExpression.exp;}

	)
	|
	(
		LeftParen type [cu, nus] RightBrace castExpression [cu, nus]
		{$exp = new CastExpression($type.ct, $castExpression.exp);}

	)
;

unaryExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp] @init {UnaryExpressionEnum e = null;}
:
	(
		(
			(
				(
					PlusPlus
					{e = UnaryExpressionEnum.plus_plus;}

				)
				|
				(
					MinusMinus
					{e = UnaryExpressionEnum.minus_minus;}

				)
			)
		)? postfixExpression [cu, nus]
		{
			if (e == null) {
				$exp = $postfixExpression.exp;
			} else {
				$exp = new CUnaryExpression(e, $postfixExpression.exp);
			}
		}

	)
	|
	(
		(
			(
				And
				{e = UnaryExpressionEnum.and;}

			)
			|
			(
				Star
				{e = UnaryExpressionEnum.address;}

			)
			|
			(
				Plus
				{e = UnaryExpressionEnum.num;}

			)
			|
			(
				Minus
				{e = UnaryExpressionEnum.negate;}

			)
			|
			(
				Tilde
				{e = UnaryExpressionEnum.bitwisenot;}

			)
			|
			(
				Not
				{e = UnaryExpressionEnum.not;}

			)
		) castExpression [cu, nus]
		{$exp = new CUnaryExpression(e, $castExpression.exp);}

	)
	|
	(
		(
			(
				Sizeof
				{e = UnaryExpressionEnum.sizeof;}

			)
			|
			(
				Alignof
				{e = UnaryExpressionEnum.alignof;}

			)
		) LeftParen
		(
			(
				expression [cu, nus]
				{$exp = new CUnaryExpression(e, $expression.exp);}

			)
			|
			(
				type [cu, nus]
				{$exp = new CUnaryExpression(e, $type.ct);}

			)
		) RightParen
	)
;

postfixExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	addressingExpression [cu, nus]
	{$exp = $addressingExpression.exp;}

	(
		{boolean add = false;}

		(
			PlusPlus
			{add = true;}

		)
		|
		(
			MinusMinus
			{add = faöse;}

		)
		{$exp = new CPostfixExpression($exp, add);}

	)?
;

addressingExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	primaryExpression [cu, nus]
	{$exp = $primaryExpression.exp;}

	(
		(
			{boolean direct = false;}

			(
				(
					Arrow
					{direct = false;}

				)
				|
				(
					Dot
					{direct = true;}

				)
			) Identifier
			{$exp = new CAdressingExpression($exp, direct, $Identifier.getText());}

		)
		|
		(
			LeftBrace expression [cu, nus] RightBrace
			{$exp = new CAdressingExpression($exp, $expression.exp);}

		)
	)*
;

primaryExpression [CompilationUnit cu, Map<String, NameUse> nus] returns
[CExpression exp]
:
	(
		Constant
		{$exp = CExpression.createConstant(cu, nus, $Constant.getText());}

	)
	|
	(
		string [cu]
		{$exp = CExpression.createString(cu, nus, strs);}

	)
	|
	(
		Identifier
		{$exp = CExpression.createIdentifier(cu, nus, $Identifier.getText());}

		(
			LeftParen
			{List<CExpression> params = new ArrayList<>();}

			(
				e = expression [cu, nus]
				{params.add($e.exp);}

				(
					Comma e = expression [cu, nus]
					{params.add($e.exp);}

				)*
			)? RightParen
			{$exp = CExpression.createFunc(cu, nus, $exp, params);}

		)?
	)
	|
	(
		LeftParen e = expression [cu, nus] RightParen
		{$exp = $e.exp;}

	)
	|
	(
		Generic LeftParen e = expression [cu, nus] Comma
		{
			CExpression eval = $e.exp;
			List<CType> types = new ArrayList<>();;
			List<CExpression> exps = new ArrayList<>();;
		}

		(
			(
				t = type [cu, nus]
				{types.add($t.ct);}

			)
			| Default
		) Colon e = expression [cu, nus]
		{exps.add($e.exp);}

		(
			Comma
			(
				(
					t = type [cu, nus]
					{types.add($t.ct);}

				)
				| Default
			) Colon e = expression [cu, nus]
			{exps.add($e.exp);}

		)* RightParen
		{$exp = CExpression.createGeneric(cu, nus, eval, types, exps);}

	)
	|
	(
		Builtin_va_start LeftParen i1 = Identifier Comma i2 = Identifier RightParen
		{$exp = CExpression.createVaStart(cu, nus, $i1.getText(), $i2.getText());}

	)
	|
	(
		Builtin_va_end LeftParen Identifier RightParen
		{$exp = CExpression.createVaEnd(cu, nus, $Identifier.getText());}

	)
	|
	(
		Builtin_va_arg LeftParen Identifier Comma type [cu, nus] RightParen
		{$exp = CExpression.createVaArg(cu, nus, $Identifier.getText(), $type.ct);}

	)
	|
	(
		Builtin_offsetof LeftParen type [cu, nus] Comma Identifier RightParen
		{$exp = CExpression.createOffsetOf(cu, nus, $type.ct, $Identifier.getText());}

	)
;

stringcompact [CompilationUnit cu] returns [String str]
:
	{StringBuilder build = new StringBuilder();}

	(
		StringLiteral
		{build.append($StringLiteral.getText());}

	)+
	{$str = build.toString();}

;

string [CompilationUnit cu] returns [String[] strs]
:
	{List<String> strlist = new ArrayList<>();}

	(
		StringLiteral
		{strs.add($StringLiteral.getText());}

	)+
	{$strs = strlist.toArray(new String[strlist.size()]);}

;

functionDeclaration [CompilationUnit cu] returns [FunctionHead fh]
:
	functionSpezifiers [cu]
	{CType rt = CType.PreDefCTypes.pdct_int;}

	(
		type [cu, null]
		{rt = $type.ct;}

	)? Identifier LeftParen
	{List<CVariable> params;}

	(
		type [cu, null]
		{CVariable newparam = new CVariable($type.ct);}

		(
			varDeclVarName [cu, null, $type.ct]
			{newparam = $varDeclVarName.cvar;}

		)?
		{params.add(newparam);}

		(
			Comma type [cu, null]
			{CVariable newparam = new CVariable($type.ct);}

			(
				varDeclVarName [cu, null, $type.ct]
				{CVariable newparam = $varDeclVarName.cvar;}

			)?
			{params.add(newparam);}

		)*
	)? RightParen
	{$fh = new FunctionHead(rt, $Identifier.getText(), params);}

;

functionSpezifiers [CompilationUnit cu] returns [CFuncSpec fs] @init {
	boolean inline = false,
	noreturn = false,
	stdcall = false;
}
:
	(
		(
			Inline
			{
				if (inline) {
					throw new CompileError("multiple set of inline function spezifier");
				}
				inline = true;
			}

		)
		|
		(
			Noreturn
			{
				if (noreturn) {
					throw new CompileError("multiple set of noreturn function spezifier");
				}
				noreturn = true;
			}

		)
		|
		(
			Stdcall
			{
				if (stdcall) {
					throw new CompileError("multiple set of __stdcall function spezifier");
				}
				stdcall = true;
			}

		)
	)*
	{$fs = new CFuncSpec(inline, noreturn, stdcall);}

;

typedefDeclaration [CompilationUnit cu] returns []
:
	Typedef type [cu, nus] varDeclVarName [cu, nus, $funcType.ct, false]
	{
		cu.addType($varDeclVarName.ct, $varDeclVarName.name);
	} //TODO on other name uses use add to the cu (or nus)

;

variableDeclaration
[CompilationUnit cu, List<Map<String, NameUse>> nus, boolean allowAssign]
returns [CVariable[] cvars] @init {List<CVariable> vars = new ArrayList<>();}
:
	type [cu, nus] varDeclVarName [cu, nus, $funcType.ct]
	{CVariable newvar = $varDeclVarName.cvar;}

	(
		Assign expression [cu, nus]
		{
			if (!allowAssign) {
				throw new CompileError("assign on variable declaration with forbidden assign");
			}
			newvar = new CVariable(newvar, $expression.exp);
		}

	)?
	{vars.add(newvar);}

	(
		Comma varDeclVarName [cu, nus, $funcType.ct]
		{newvar = $varDeclVarName.cvar;}

		(
			Assign expression [cu, nus]
			{
				if (!allowAssign) {
					throw new CompileError("assign on variable declaration with forbidden assign");
				}
				newvar = new CVariable(newvar, $expression.exp);
			}

		)?
		{vars.add(newvar);}

	)*
	{$cvars = vars.toArray(new CVariable[vars.size()]);}

;

varDeclVarName
[CompilationUnit cu, List<Map<String, NameUse>> nus, CType ct, boolean makeVarObj]
returns [CVariable cvar, CType ct, String name]
:
	(
		(
			varDeclVarNameSub1 [cu, nus, ct]
			{
			$ct = $varDeclVarNameSub1.ct;
			$name = $varDeclVarNameSub1.name;
		}

		)
		|
		(
			varDeclVarNameSub2 [cu, nus, ct]
			{
			$ct = $varDeclVarNameSub2.ct;
			$name = $varDeclVarNameSub2.name;
		}

		)
	)
	{
		if (makeVarObj) {
			$cvar = new CVariable($ct, $name);
		} else {
			$cvar = null;
		}
	}

;

varDeclVarNameSub2
[CompilationUnit cu, List<Map<String, NameUse>> nus, CType ct] returns
[CType ct, Strign name]
:
	LeftParen varDeclVarNameSub1 [cu,nus,ct] RightParen
	{
		$name = $varDeclVarNameSub1.name;
		$ct = $varDeclVarNameSub1.ct;
	}

	(
		funcSubtype [cu, nus, $ct]
		{$ct = $funcSubtype.ct;}

	)?
;

varDeclVarNameSub1
[CompilationUnit cu, List<Map<String, NameUse>> nus, CType ct] returns
[CType ct, Strign name] @init {$ct = ct;}
:
	{int pointers = 0;}

	(
		Star
		{pointers++;}

	)* Identifier
	{
		$name = $Identifier.getText();
		if (pointers > 0) {
			$ct = CType.createPointer(ct, pointers);
		}
	}

	(
		LeftBracket
		{long len = 0;}

		(
			constant [cu]
			{len = $constant.cvonst.getNumberValue();}

		)? RightBracket
		{$ct = CType.createArray(ct, len);}

	)*
;

assignmentOperator [CompilationUnit cu] returns [CAssignOperator ao]
:
	(
		Assign
		{$ao = CAssignOperator.assign;}

	)
	|
	(
		StarAssign
		{$ao = CAssignOperator.multiplyAssign;}

	)
	|
	(
		DivAssign
		{$ao = CAssignOperator.divAssign;}

	)
	|
	(
		ModAssign
		{$ao = CAssignOperator.modAssign;}

	)
	|
	(
		PlusAssign
		{$ao = CAssignOperator.plusAssign;}

	)
	|
	(
		MinusAssign
		{$ao = CAssignOperator.minusAssign;}

	)
	|
	(
		LeftShiftAssign
		{$ao = CAssignOperator.lshiftAssign;}

	)
	|
	(
		RightShiftAssign
		{$ao = CAssignOperator.rshiftAssign;}

	)
	|
	(
		AndAssign
		{$ao = CAssignOperator.andAssign;}

	)
	|
	(
		XorAssign
		{$ao = CAssignOperator.xorAssign;}

	)
	|
	(
		OrAssign
		{$ao = CAssignOperator.orAssign;}

	)
;

type [CompilationUnit cu, Map<String, NameUse> nus] returns [CType ct]
:
	(
		(
			typeSpezifier [cu, nus]
			{$ct = CType.createTypeSpecType($typeSpezifier.ts);}

			(
				arrayType [cu, nus]
				{$ct = CType.createTypeWithTypeSpec($typeSpezifier.ts, $arrayType.ct);}

			)?
		)
		|
		(
			arrayType [cu, nus]
			{$ct = $arrayType.ct;}

		)
	)
	(
		funcSubtype2 [cu, nus, $ct]
		{$ct = $funcSubtype.ct;}

	)?
;

funcSubtype2 [CompilationUnit cu, Map<String, NameUse> nus, CType retType]
returns [CType ct]
:
	{int pointers = 0;}

	(
		LeftParen
		(
			Star
			{pointers++;}

		)* RightParen
	)? funcSubtype [cu, nus, ct]
	{
		if (pointers > 0) {
			$ct = CType.createPointer($funcSubtype.ct, pointers);
		} else {
			$ct = $funcSubtype.ct;
		}
	}

;

funcSubtype [CompilationUnit cu, Map<String, NameUse> nus, CType retType]
returns [CType ct] @init {List<CType> params = new ArrayList<>();}
:
	LeftParen
	(
		type [cu, nus]
		{params.add($type.ct);}

		(
			Comma type [cu, nus]
			{params.add($type.ct);}

		)*
	)? RightParen
	{$ct = CType.createFuncType(retType, params);}

;

arrayType [CompilationUnit cu, Map<String, NameUse> nus] returns [CType ct]
:
	pointerType [cu, nus]
	{$ct = $pointerType.ct;}

	(
		{long len = 0;}

		LeftBrace
		(
			constant [cu, nus]
			{len = $constant.cvonst.getNumberValue();}

		)? RightBrace
		{$ct = CType.createArrayType($ct, len);}

	)*
;

pointerType [CompilationUnit cu, Map<String, NameUse> nus] returns [CType ct]
:
	{int pntr = 0;}

	primaryType [cu, nus]
	(
		Star
		{pntr ++;}

	)*
	{
		if (pntr > 0) {
			$ct = CType.createPointer($primaryType.ct, pntr);
		} else {
			$ct = $primaryType.ct;
		}
	}

;

primaryType [CompilationUnit cu, Map<String, NameUse> nus] returns [CType ct]
:
	(
	{
		int longcnt = 0;
		boolean fp = false;
	}

		(
			(
				(
					Short
					{
						longcnt = -1;
						fp = false;
					}

				)
				|
				(
					Long
					{
						longcnt = 1;
						fp = false;
					}

					(
						Long
						{
							longcnt = 2;
							fp = false;
						}

					)?
				)
			)
			|
			(
				Long
				{
					longcnt = 1;
					fp = false;
				}

				(
					Long
					{
						longcnt = 2;
						fp = false;
					}

				)?
				|
				(
					Short
					{
						longcnt = -1;
						fp = false;
					}

				)
			)? Int
			|
			(
				Float
				{
					longcnt = -1;
					fp = true;
				}

			)
			|
			(
				(
					Long
					{longcnt = 1;}

				)? Double
				{fp = true;}

				| Double Long
				{
					longcnt = 1;
					fp = true;
				}

			)
		)
		{
			if (fp) {
				switch(longcnt) {
				case -1:
					$ct = PreDefCTypes.pdct_float;
					break;
				case 0:
					$ct = PreDefCTypes.pdct_double;
					break;
				case 1:
					$ct = PreDefCTypes.pdct_double_long;
					break;
				default:
					throw new InternalError("illegal longcnt=" + longcnt + " known is -1,0,1 (on floating point number)");
				}
			} else {
				switch(longcnt) {
				case -1:
					$ct = PreDefCTypes.pdct_short_int;
					break;
				case 0:
					$ct = PreDefCTypes.pdct_int;
					break;
				case 1:
					$ct = PreDefCTypes.pdct_long_int;
					break;
				case 2:
					$ct = PreDefCTypes.pdct_long_long_int;
					break;
				default:
					throw new InternalError("illegal longcnt=" + longcnt + " known is -1,0,132 (on natural number)");
				}
			}
		}

	)
	|
	(
		Void
		{$ct = PreDefCTypes.pdct_void;}

	)
	|
	(
		structOrUnionType [cu, nus]
		{$ct = $structOrUnionType.ct;}

	)
	|
	(
		enumType [cu, nus]
		{$ct = $enumType.ct;}

	)
	|
	(
		Identifier
		{$ct = cu.getTypeByName($Identifier.getText(), nus);}

	)
	|
	(
		LeftParen type [cu, nus] RightParen
		{$ct = $type.ct;}

	)
;

structOrUnionType [CompilationUnit cu, Map<String, NameUse> nus] returns
[CType ct] @init {
	boolean struct = false;
	String name = null;
}
:
	(
		(
			Struct
			{struct = true;}

		)
		|
		(
			Union
			{struct = false;}

		)
	)
	(
		(
			Identifier
			{name = $Identifier.getText();}

		)
		|
		(
			(
				Identifier
				{name = $Identifier.getText();}

			)? LeftBrace
			{List<CVariable> cvars = new ArrayList<>();}

			(
				variableDeclaration [cu, nus, false]
				{cvars.add($variableDeclaration.cvar);}

				(
					Comma variableDeclaration [cu, nus, false]
					{cvars.add($variableDeclaration.cvar);}

				)* Comma?
			)? RightBrace
			//TODO add to names

		)
	)
	{
		if (struct) {
			$ct = cu.getStructType(nus, name);
		} else {
			$ct = cu.getUnionType(nus, name);
		}
	}

;

enumType [CompilationUnit cu, Map<String, NameUse> nus] returns [CType ct] //TODO

:
	Enum
	(
		Identifier
		| Identifier?
		(
			LeftBrace Identifier
			(
				Assign
				(
					constant [cu, nus]
				)
			)?
			(
				Comma Identifier
				(
					Assign
					(
						constant [cu, nus]
					)
				)?
			)* Comma? RightBrace
		)? Identifier
	)
;

typeSpezifier [CompilationUnit cu, List<Map<String, NameUse>> nus] returns
[CTypeSpec ts] //TODO

:
	(
		Extern
		| Const
		| Volatile
		| Static
		| Restrict
		| Auto
		| Register
		| Signed
		| Unsigned
	)*
;

constant [CompilationUnit cu, List<Map<String, NameUse>> nus] returns
[CConstant cconst] //TODO

:
	expression [cu, nus]
;

Asm
:
	'__asm__'
	| '__asm'
;

Builtin_offsetof
:
	'__builtin_offsetof'
;

Builtin_va_start
:
	'__builtin_va_start'
;

Builtin_va_end
:
	'__builtin_va_end'
;

Builtin_va_arg
:
	'__builtin_va_arg'
;

Auto
:
	'auto'
;

Break
:
	'break'
;

Case
:
	'case'
;

Char
:
	'char'
;

Const
:
	'const'
;

Continue
:
	'continue'
;

Default
:
	'default'
;

Do
:
	'do'
;

Double
:
	'double'
;

Else
:
	'else'
;

Enum
:
	'enum'
;

Extern
:
	'extern'
;

Float
:
	'float'
;

For
:
	'for'
;

Goto
:
	'goto'
;

If
:
	'if'
;

Inline
:
	'inline'
;

Int
:
	'int'
;

Long
:
	'long'
;

Register
:
	'register'
;

Restrict
:
	'restrict'
;

Return
:
	'return'
;

Short
:
	'short'
;

Signed
:
	'signed'
;

Sizeof
:
	'sizeof'
;

Static
:
	'static'
;

Struct
:
	'struct'
;

Switch
:
	'switch'
;

Typedef
:
	'typedef'
;

Union
:
	'union'
;

Unsigned
:
	'unsigned'
;

Void
:
	'void'
;

Volatile
:
	'volatile'
;

While
:
	'while'
;

Alignas
:
	'_Alignas'
;

Alignof
:
	'_Alignof'
;

Atomic
:
	'_Atomic'
;

Bool
:
	'_Bool'
;

Complex
:
	'_Complex'
;

Generic
:
	'_Generic'
;

Imaginary
:
	'_Imaginary'
;

Noreturn
:
	'_Noreturn'
;

Stdcall
:
	'__stdcall'
;

StaticAssert
:
	'_Static_assert'
;

ThreadLocal
:
	'_Thread_local'
;

LeftParen
:
	'('
;

RightParen
:
	')'
;

LeftBracket
:
	'['
;

RightBracket
:
	']'
;

LeftBrace
:
	'{'
;

RightBrace
:
	'}'
;

Less
:
	'<'
;

LessEqual
:
	'<='
;

Greater
:
	'>'
;

GreaterEqual
:
	'>='
;

LeftShift
:
	'<<'
;

RightShift
:
	'>>'
;

Plus
:
	'+'
;

PlusPlus
:
	'++'
;

Minus
:
	'-'
;

MinusMinus
:
	'--'
;

Star
:
	'*'
;

Div
:
	'/'
;

Mod
:
	'%'
;

And
:
	'&'
;

Or
:
	'|'
;

AndAnd
:
	'&&'
;

OrOr
:
	'||'
;

Caret
:
	'^'
;

Not
:
	'!'
;

Tilde
:
	'~'
;

Question
:
	'?'
;

Colon
:
	':'
;

Semi
:
	';'
;

Comma
:
	','
;

Assign
:
	'='
;
// '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='

StarAssign
:
	'*='
;

DivAssign
:
	'/='
;

ModAssign
:
	'%='
;

PlusAssign
:
	'+='
;

MinusAssign
:
	'-='
;

LeftShiftAssign
:
	'<<='
;

RightShiftAssign
:
	'>>='
;

AndAssign
:
	'&='
;

XorAssign
:
	'^='
;

OrAssign
:
	'|='
;

Equal
:
	'=='
;

NotEqual
:
	'!='
;

Arrow
:
	'->'
;

Dot
:
	'.'
;

Ellipsis
:
	'...'
;

Identifier
:
	IdentifierNondigit
	(
		IdentifierNondigit
		| Digit
	)*
;

fragment
IdentifierNondigit
:
	Nondigit
	| UniversalCharacterName
	//|   // other[] implementation-defined characters...

;

fragment
Nondigit
:
	[a-zA-Z_]
;

fragment
Digit
:
	[0-9]
;

fragment
UniversalCharacterName
:
	'\\u' HexQuad
	| '\\U' HexQuad HexQuad
;

fragment
HexQuad
:
	HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit
;

Constant
:
	IntegerConstant
	| FloatingConstant
	//|   EnumerationConstant

	| CharacterConstant
;

fragment
IntegerConstant
:
	DecimalConstant IntegerSuffix?
	| OctalConstant IntegerSuffix?
	| HexadecimalConstant IntegerSuffix?
	| BinaryConstant
;

fragment
BinaryConstant
:
	'0' [bB] [0-1]+
;

fragment
DecimalConstant
:
	NonzeroDigit Digit*
;

fragment
OctalConstant
:
	'0' OctalDigit*
;

fragment
HexadecimalConstant
:
	HexadecimalPrefix HexadecimalDigit+
;

fragment
HexadecimalPrefix
:
	'0' [xX]
;

fragment
NonzeroDigit
:
	[1-9]
;

fragment
OctalDigit
:
	[0-7]
;

fragment
HexadecimalDigit
:
	[0-9a-fA-F]
;

fragment
IntegerSuffix
:
	UnsignedSuffix LongSuffix?
	| UnsignedSuffix LongLongSuffix
	| LongSuffix UnsignedSuffix?
	| LongLongSuffix UnsignedSuffix?
;

fragment
UnsignedSuffix
:
	[uU]
;

fragment
LongSuffix
:
	[lL]
;

fragment
LongLongSuffix
:
	'll'
	| 'LL'
;

fragment
FloatingConstant
:
	DecimalFloatingConstant
	| HexadecimalFloatingConstant
;

fragment
DecimalFloatingConstant
:
	FractionalConstant ExponentPart? FloatingSuffix?
	| DigitSequence ExponentPart FloatingSuffix?
;

fragment
HexadecimalFloatingConstant
:
	HexadecimalPrefix
	(
		HexadecimalFractionalConstant
		| HexadecimalDigitSequence
	) BinaryExponentPart FloatingSuffix?
;

fragment
FractionalConstant
:
	DigitSequence? '.' DigitSequence
	| DigitSequence '.'
;

fragment
ExponentPart
:
	[eE] Sign? DigitSequence
;

fragment
Sign
:
	[+-]
;

DigitSequence
:
	Digit+
;

fragment
HexadecimalFractionalConstant
:
	HexadecimalDigitSequence? '.' HexadecimalDigitSequence
	| HexadecimalDigitSequence '.'
;

fragment
BinaryExponentPart
:
	[pP] Sign? DigitSequence
;

fragment
HexadecimalDigitSequence
:
	HexadecimalDigit+
;

fragment
FloatingSuffix
:
	[flFL]
;

fragment
CharacterConstant
:
	'\'' CCharSequence '\''
	| 'L\'' CCharSequence '\''
	| 'u\'' CCharSequence '\''
	| 'U\'' CCharSequence '\''
;

fragment
CCharSequence
:
	CChar+
;

fragment
CChar
:
	~['\\\r\n]
	| EscapeSequence
;

fragment
EscapeSequence
:
	SimpleEscapeSequence
	| OctalEscapeSequence
	| HexadecimalEscapeSequence
	| UniversalCharacterName
;

fragment
SimpleEscapeSequence
:
	'\\' ['"?abfnrtv\\]
;

fragment
OctalEscapeSequence
:
	'\\' OctalDigit OctalDigit? OctalDigit?
;

fragment
HexadecimalEscapeSequence
:
	'\\x' HexadecimalDigit+
;

StringLiteral
:
	EncodingPrefix? '"' SCharSequence? '"'
;

fragment
EncodingPrefix
:
	'u8'
	| 'u'
	| 'U'
	| 'L'
;

fragment
SCharSequence
:
	SChar+
;

fragment
SChar
:
	~["\\\r\n]
	| EscapeSequence
	| '\\\n' // Added line[]

	| '\\\r\n' // Added line[]

;

//crash
//ComplexDefine
//:
//	'#' Whitespace? 'define' ~[#\r\n]* -> skip
//;

//crash
//IncludeDirective
//:
//	'#' Whitespace? 'include' Whitespace?
//	(
//		(
//			'"' ~[\r\n]* '"'
//		)
//		|
//		(
//			'<' ~[\r\n]* '>'
//		)
//	) Whitespace? Newline -> skip
//;

// crash[] on the[] following asm[] blocks:
/*
    asm[]
    {
        mfspr[] x, 286;
    }
 */
//AsmBlock
//:
//	'asm' ~'{'* '{' ~'}'* '}' -> skip
//;

// ignore[] the lines[] generated by[] c preprocessor[]
// sample[] line : '#line 1 "/home/dm/files/dk1.h" 1'

LineAfterPreprocessing
:
	'#line' Whitespace* ~[\r\n]* -> skip
;

LineDirective
:
	'#' Whitespace? DecimalConstant Whitespace? StringLiteral ~[\r\n]* -> skip
;

//PragmaDirective
//:
//	'#' Whitespace? 'pragma' Whitespace ~[\r\n]* -> skip
//;

Whitespace
:
	[ \t]+ -> skip
;

Newline
:
	(
		'\r' '\n'?
		| '\n'
	) -> skip
;

BlockComment
:
	'/*' .*? '*/' -> skip
;

LineComment
:
	'//' ~[\r\n]* -> skip
;
