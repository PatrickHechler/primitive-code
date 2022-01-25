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
import de.hechler.patrick.codesprachen.primitive.compile.c.enums.*;
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
				variableDeclaration [$cu]
				{$cu.addAll($variableDeclaration.cvar);}

			)
			|
			(
				staticAssert [$cu]
				{$cu.eval($staticAssert.sa);}

			)
		)? Semi
		|
		(
			function [$cu]
			{$cu.add($function.func);}

		)
	)* EOF
;

function [CompilationUnit cu] returns [FunctionImpl func] //TODO

:
	functionDeclaration [cu] LeftBrace
	(
		command [cu]
	)* RightBrace
;

command [CompilationUnit cu] returns []
:
	(
		variableDeclaration [cu]
		|
		(
			Identifier assignmentOperator [cu]
		)? expression [cu]
		| staticAssert [cu]
		| Continue
		| Break
		| Goto Identifier
		| Asm //example: __asm__ ("DIV AX, BX" : "AX=" number, "BX=" divide[] : "=AX" divided, "=BX" modulo);

		( // asm[] commands
			Volatile
		)? LeftParen StringLiteral+
		( //set asm[] values
			Colon
			(
				StringLiteral+ variable [cu]
				(
					Comma StringLiteral+ variable [cu]
				)*
			)?
			( //get values[] after asm[]
				Colon
				(
					StringLiteral+ variable [cu]
					(
						Comma StringLiteral+ variable [cu]
					)*
				)?
			)?
		)? RightParen
	) Semi
	| Identifier Colon command [cu]
	| LeftBrace command [cu]* RightBrace
	| While LeftParen expression [cu] RightParen command [cu]
	| Do command [cu] Whitespace LeftParen expression [cu] RightParen Semi
	| For LeftParen
	(
		expression [cu]
		(
			Comma
			(
				expression [cu]
			)
		)*
		| variableDeclaration [cu]
	)? Semi expression [cu]? Semi
	(
		expression [cu]
		(
			Comma expression [cu]
		)*
	)? RightParen command [cu]
	| If LeftParen expression [cu] RightParen command [cu]
	(
		Else command [cu]
	)?
	| Switch LeftParen expression [cu] RightParen LeftBrace
	(
		(
			Case
			(
				constant [cu]
			)
			| Default
		) Colon command [cu]*
	)* RightBrace
;

staticAssert [CompilationUnit cu] returns [CStaticAssert sa]
:
	StaticAssert LeftParen expression [cu] Comma expression [cu] RightParen
;

expression [CompilationUnit cu] returns []
:
	conditionalExpression [cu]
	| unaryExpression [cu] assignmentOperator [cu] expression [cu]
;

conditionalExpression [CompilationUnit cu] returns []
:
	logicOrExpression [cu]
	(
		Question expression [cu] Colon conditionalExpression [cu]
	)?
;

logicOrExpression [CompilationUnit cu] returns []
:
	logicalAndExpression [cu]
	(
		OrOr logicalAndExpression [cu]
	)*
;

logicalAndExpression [CompilationUnit cu] returns []
:
	inclusiveOrExpression [cu]
	(
		AndAnd inclusiveOrExpression [cu]
	)*
;

inclusiveOrExpression [CompilationUnit cu] returns []
:
	exclusiveOrExpression [cu]
	(
		Or exclusiveOrExpression [cu]
	)*
;

exclusiveOrExpression [CompilationUnit cu] returns []
:
	andExpression [cu]
	(
		Caret andExpression [cu]
	)*
;

andExpression [CompilationUnit cu] returns []
:
	equalityExpression [cu]
	(
		(
			And
		) equalityExpression [cu]
	)*
;

equalityExpression [CompilationUnit cu] returns []
:
	relationalExpression [cu]
	(
		(
			Equal
			| NotEqual
		) relationalExpression [cu]
	)*
;

relationalExpression [CompilationUnit cu] returns []
:
	shiftExpression [cu]
	(
		(
			Less
			| Greater
			| LessEqual
			| GreaterEqual
		) shiftExpression [cu]
	)*
;

shiftExpression [CompilationUnit cu] returns []
:
	additiveExpression [cu]
	(
		(
			LeftShift
			| RightShift
		) additiveExpression [cu]
	)*
;

additiveExpression [CompilationUnit cu] returns []
:
	multiplicativeExpression [cu]
	(
		(
			Plus
			| Minus
		) multiplicativeExpression [cu]
	)*
;

multiplicativeExpression [CompilationUnit cu] returns []
:
	castExpression [cu]
	(
		(
			'*'
			| '/'
			| '%'
		) castExpression [cu]
	)*
;

castExpression [CompilationUnit cu] returns []
:
	LeftParen type [cu] RightBrace castExpression [cu]
	| unaryExpression [cu]
;

unaryExpression [CompilationUnit cu] returns []
:
	(
		PlusPlus
		| MinusMinus
	)? postfixExpression [cu]
	|
	(
		And
		| Star
		| Plus
		| Minus
		| Tilde
		| Not
	) castExpression [cu]
	|
	(
		Sizeof
		| Alignof
	) LeftParen type [cu] RightParen
;

postfixExpression [CompilationUnit cu] returns []
:
	primaryExpression [cu]
	(
		PlusPlus
		| MinusMinus
	)?
;

primaryExpression [CompilationUnit cu] returns []
:
	Constant
	| StringLiteral
	| variable [cu]
	(
		LeftParen
		(
			expression [cu]
			(
				Comma expression [cu]
			)*
		)? RightParen
	)?
	| LeftParen expression [cu] RightParen
	| Generic LeftParen expression [cu] Comma
	(
		type [cu]
		| Default
	) Colon expression [cu]
	(
		Comma
		(
			type [cu]
			| Default
		) Colon expression [cu]
	)* RightParen
	| Builtin_va_start LeftParen Identifier Comma Identifier RightParen
	| Builtin_va_end LeftParen Identifier RightParen
	| Builtin_va_arg LeftParen Identifier Comma type [cu] RightParen
	| Builtin_offsetof LeftParen type [cu] Comma unaryExpression [cu] RightParen
;

variable [CompilationUnit cu] returns []
:
	(
		Identifier
		| Star+ variable [cu]
		| LeftParen
		(
			LeftParen type [cu] RightParen
		)? expression [cu] RightParen
	)
	(
		(
			Dot
			| Arrow
		) Identifier
		| LeftBracket primaryExpression [cu] RightBracket
	)*
;

functionDeclaration [CompilationUnit cu] returns [FunctionHead fh] //TODO

:
	type [cu]? Identifier LeftParen
	(
		type [cu]
		(
			Comma type [cu]
		)*
	)? RightParen functionSpezifiers [cu]
;

functionSpezifiers [CompilationUnit cu] returns []
:
	(
		Inline
		| Noreturn
	)*
;

typedefDeclaration [CompilationUnit cu] returns []
:
	Typedef type [cu]
;

variableDeclaration [CompilationUnit cu] returns [CVariable[] cvar]
:
	type [cu]
	(
		Assign expression [cu]
	)?
	(
		Comma Star* Identifier
		(
			Assign expression [cu]
		)?
		(
			LeftBracket
			(
				constant [cu]
			)? RightBracket
		)*
	)*
;

assignmentOperator [CompilationUnit cu] returns []
:
	Assign
	| StarAssign
	| DivAssign
	| ModAssign
	| PlusAssign
	| MinusAssign
	| LeftShiftAssign
	| RightShiftAssign
	| AndAssign
	| XorAssign
	| OrAssign
;

type [CompilationUnit cu] returns []
:
	typeSpezifier [cu] arrayType [cu]?
	| arrayType [cu]
;

arrayType [CompilationUnit cu] returns []
:
	pointerType [cu]
	(
		LeftBrace
		(
			constant [cu]
		)? RightBrace
	)*
;

pointerType [CompilationUnit cu] returns []
:
	funcType [cu] Star* Identifier?
;

funcType [CompilationUnit cu] returns []
:
	primaryType [cu]
	(
		(
			LeftParen
			(
				type [cu]
				(
					Comma type [cu]
				)*
			)? RightParen
		)?
	)?
;

primaryType [CompilationUnit cu] returns []
:
	(
		Long Long?
		| Short
	)? Int
	| Float
	|
	(
		Long? Double
		| Double Long
	)
	| Void
	| structOrUnionType [cu]
	| enumType [cu]
	| Identifier
	| LeftParen type [cu] RightBrace
;

structOrUnionType [CompilationUnit cu] returns []
:
	(
		Struct
		| Union
	)
	(
		Identifier
		|
		(
			Identifier? LeftBrace type [cu]
			(
				Comma type [cu]
			)* Comma? RightBrace
		)
	)
;

enumType [CompilationUnit cu] returns []
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
					constant [cu]
				)
			)?
			(
				Comma Identifier
				(
					Assign
					(
						constant [cu]
					)
				)?
			)* Comma? RightBrace
		)? Identifier
	)
;

typeSpezifier [CompilationUnit cu] returns []
:
	(
		Extern
		| Const
		| Volatile
		| Static
		(
			| Auto
			| Register
		)
	)+
;

constant [CompilationUnit cu] returns []
:
	expression [cu]
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
