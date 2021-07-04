grammar PrimGrammar;

number [int minDeep] returns [int deep, long value]
:
	(
		(
			HEX_NUM
			{$value = Long.parseLong($HEX_NUM.getText().substring(3), 16);}

		)
		|
		(
			DEC_NUM
			{$value = Long.parseLong($DEC_NUM.getText().substring(3), 10);}

		)
		|
		(
			BIN_NUM
			{$value = Long.parseLong($BIN_NUM.getText().substring(3), 2);}

		)
		{$deep = 0;}

	)
	|
	(
		REG_START number [$minDeep - 1] REG_END
		{
			$deep = $number.deep + 1;
			$value = $number.value;
		}

	)
	{if ($minDeep > $deep || $deep > 255) {
		throw new java.util.InputMismatchException("deep is out of supported range: min=" + $minDeep + " max=255 deep=" + $deep);
	}}

;

datei returns
[java.util.List<de.hechler.patrick.codesprachen.primitive.objects.commands.Command> cmds]
:
	{$cmds = new java.util.ArrayList<>();}

	(
		command
		{$cmds.add($command.cmd);}

	)+
;

command returns
[de.hechler.patrick.codesprachen.primitive.objects.commands.Command cmd]
:
	(
		add
	)
	|
	(
		sub
	)
	|
	(
		mul
	)
	|
	(
		div
	)
	|
	(
		neg
	)
	|
	(
		and
	)
	|
	(
		or
	)
	|
	(
		xor
	)
	|
	(
		not
	)
	|
	(
		push
	)
	|
	(
		pop
	)
	|
	(
		cmp
	)
	|
	(
		jmp
	)
	|
	(
		jmpeq
	)
	|
	(
		jmpne
	)
	|
	(
		jmpgt
	)
	|
	(
		jmpge
	)
	|
	(
		jmplo
	)
	|
	(
		jmple
	)
	|
	(
		call
	)
	|
	(
		calleq
	)
	|
	(
		callne
	)
	|
	(
		callgt
	)
	|
	(
		callge
	)
	|
	(
		calllo
	)
	|
	(
		callle
	)
;

callle
:
	CALLLE NAME
;

calllo
:
	CALLLO NAME
;

callge
:
	CALLGE NAME
;

callgt
:
	CALLGT NAME
;

callne
:
	CALLNE NAME
;

calleq
:
	CALLEQ NAME
;

call
:
	CALL NAME
;

jmple
:
	JMPLE NAME
;

jmplo
:
	JMPLO NAME
;

jmpge
:
	JMPGE NAME
;

jmpgt
:
	JMPGT NAME
;

jmpne
:
	JMPNE NAME
;

jmpeq
:
	JMPEQ NAME
;

jmp
:
	JMP NAME
;

cmp
:
	CMP number [0] COMMA number [0]
;

pop
:
	POP number [1]
;

push
:
	PUSH number [0]
;

not
:
	NOT number [1]
;

xor
:
	XOR number [1] COMMA number [0]
;

or
:
	OR number [1] COMMA number [0]
;

and
:
	AND number [1] COMMA number [0]
;

neg
:
	NEG number [1]
;

div
:
	DIV number [1] COMMA number [1]
;

mul
:
	MUL number [1] COMMA number [1]
;

sub
:
	SUB number [1] COMMA number [0]
;

add 
:
	ADD number [1] COMMA number [0]
;

STRING
:
	'\''
	(
		(
			'\\' .
		)
		| ~( '\'' )
	)* '\''
;

CALLLE
:
	'CALLLE'
;

CALLLO
:
	'CALLLO'
;

CALLGE
:
	'CALLGE'
;

CALLGT
:
	'CALLGT'
;

CALLNE
:
	'CALLNE'
;

CALLEQ
:
	'CALLEQ'
;

CALL
:
	'CALL'
;

JMPLE
:
	'JMPLE'
;

JMPLO
:
	'JMPLO'
;

JMPGE
:
	'JMPGE'
;

JMPGT
:
	'JMPGT'
;

JMPNE
:
	'JMPNE'
;

JMPEQ
:
	'JMPEQ'
;

JMP
:
	'JMP'
;

CMP
:
	'CMP'
;

POP
:
	'POP'
;

PUSH
:
	'PUSH'
;

NOT
:
	'NOT'
;

XOR
:
	'XOR'
;

OR
:
	'OR'
;

AND
:
	'AND'
;

NEG
:
	'NEG'
;

DIV
:
	'DIV'
;

MUL
:
	'MUL'
;

SUB
:
	'SUB'
;

ADD
:
	'ADD'
;

HEX_NUM
:
	'HEX' [0-9a-fA-F]+
;

DEC_NUM
:
	'DEC' [0-9]+
;

BIN_NUM
:
	'BIN' [01]+
;

REG_START
:
	'['
;

REG_END
:
	']'
;

COMMA
:
	','
;

NAME
:
	[a-zA-Z_\-öäüßÖÄÜẞ] [a-zA-Z_\-öäüßÖÄÜẞ0-9]*
;

WS
:
	[ \t\r\n] -> skip
;
