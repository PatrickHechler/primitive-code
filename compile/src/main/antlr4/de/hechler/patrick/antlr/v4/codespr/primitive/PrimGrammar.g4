grammar PrimGrammar;

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
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.add, $add.num1, $add.num2);}

	)
	|
	(
		sub
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.sub, $sub.num1, $sub.num2);}

	)
	|
	(
		mul
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.mul, $mul.num1, $mul.num2);}

	)
	|
	(
		div
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegRegCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.div, $div.num1, $div.num2);}

	)
	|
	(
		neg
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.neg, $neg.num);}

	)
	|
	(
		and
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.and, $and.num1, $and.num2);}

	)
	|
	(
		or
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.or, $or.num1, $or.num2);}

	)
	|
	(
		xor
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.xor, $xor.num1, $xor.num2);}

	)
	|
	(
		not
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.not, $not.num);}

	)
	|
	(
		push
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.NumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.push, $push.num);}

	)
	|
	(
		pop
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.RegCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.pop, $pop.num);}

	)
	|
	(
		cmp
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.NumNumCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.cmp, $cmp.num1, $cmp.num2);}

	)
	|
	(
		jmp
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.jmp, $jmp.target);}

	)
	|
	(
		jmpeq
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.jmpeq, $jmpeq.target);}

	)
	|
	(
		jmpne
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.jmpne, $jmpne.target);}

	)
	|
	(
		jmpgt
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.jmpgt, $jmpgt.target);}

	)
	|
	(
		jmpge
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.jmpge, $jmpge.target);}

	)
	|
	(
		jmplo
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.jmplo, $jmplo.target);}

	)
	|
	(
		jmple
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.jmple, $jmple.target);}

	)
	|
	(
		call
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.call, $call.target);}

	)
	|
	(
		calleq
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.calleq, $calleq.target);}

	)
	|
	(
		callne
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.callne, $callne.target);}

	)
	|
	(
		callgt
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.callgt, $callgt.target);}

	)
	|
	(
		callge
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.callge, $callge.target);}

	)
	|
	(
		calllo
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.calllo, $calllo.target);}

	)
	|
	(
		callle
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.callle, $callle.target);}

	)
	|
	(
		label
		{$cmd = de.hechler.patrick.codesprachen.primitive.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.enums.Commands.label, $label.name);}
	)
;

label returns [java.lang.String name]
:
	LABEL_START NAME
	{$name = $NAME.getText();}

;

callle returns [java.lang.String target]
:
	CALLLE NAME
	{$target = $NAME.getText();}

;

calllo returns [java.lang.String target]
:
	CALLLO NAME
	{$target = $NAME.getText();}

;

callge returns [java.lang.String target]
:
	CALLGE NAME
	{$target = $NAME.getText();}

;

callgt returns [java.lang.String target]
:
	CALLGT NAME
	{$target = $NAME.getText();}

;

callne returns [java.lang.String target]
:
	CALLNE NAME
	{$target = $NAME.getText();}

;

calleq returns [java.lang.String target]
:
	CALLEQ NAME
	{$target = $NAME.getText();}

;

call returns [java.lang.String target]
:
	CALL NAME
	{$target = $NAME.getText();}

;

jmple returns [java.lang.String target]
:
	JMPLE NAME
	{$target = $NAME.getText();}

;

jmplo returns [java.lang.String target]
:
	JMPLO NAME
	{$target = $NAME.getText();}

;

jmpge returns [java.lang.String target]
:
	JMPGE NAME
	{$target = $NAME.getText();}

;

jmpgt returns [java.lang.String target]
:
	JMPGT NAME
	{$target = $NAME.getText();}

;

jmpne returns [java.lang.String target]
:
	JMPNE NAME
	{$target = $NAME.getText();}

;

jmpeq returns [java.lang.String target]
:
	JMPEQ NAME
	{$target = $NAME.getText();}

;

jmp returns [java.lang.String target]
:
	JMP NAME
	{$target = $NAME.getText();}

;

cmp returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	CMP
	(
		number [0]
		{$num1 = $number.num;}

	) COMMA
	(
		number [0]
		{$num2 = $number.num;}

	)
;

pop returns [de.hechler.patrick.codesprachen.primitive.objects.Num num]
:
	POP number [1]
	{$num = $number.num;}

;

push returns [de.hechler.patrick.codesprachen.primitive.objects.Num num]
:
	PUSH number [0]
	{$num = $number.num;}

;

not returns [de.hechler.patrick.codesprachen.primitive.objects.Num num]
:
	NOT number [1]
	{$num = $number.num;}

;

xor returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	XOR
	(
		number [1]
		{$num1 = $number.num;}

	) COMMA
	(
		number [0]
		{$num2 = $number.num;}

	)
;

or returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	OR
	(
		number [1]
		{$num1 = $number.num;}

	) COMMA
	(
		number [0]
		{$num2 = $number.num;}

	)
;

and returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	AND
	(
		number [1]
		{$num1 = $number.num;}

	) COMMA
	(
		number [0]
		{$num2 = $number.num;}

	)
;

neg returns [de.hechler.patrick.codesprachen.primitive.objects.Num num]
:
	NEG number [1]
	{$num = $number.num;}

;

div returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	DIV
	(
		number [1]
		{$num1 = $number.num;}

	) COMMA
	(
		number [1]
		{$num2 = $number.num;}

	)
;

mul returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	MUL
	(
		number [1]
		{$num1 = $number.num;}

	) COMMA
	(
		number [0]
		{$num2 = $number.num;}

	)
;

sub returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	SUB
	(
		number [1]
		{$num1 = $number.num;}

	) COMMA
	(
		number [0]
		{$num2 = $number.num;}

	)
;

add returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num1, de.hechler.patrick.codesprachen.primitive.objects.Num num2]
:
	ADD
	(
		number [1]
		{$num1 = $number.num;}

	) COMMA
	(
		number [0]
		{$num2 = $number.num;}

	)
;

number [int minDeep] returns
[de.hechler.patrick.codesprachen.primitive.objects.Num num]
:
	(
		(
			HEX_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.objects.params.DirectNumber(Long.parseLong($DEC_NUM.getText().substring(3), 16));}

		)
		|
		(
			DEC_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.objects.params.DirectNumber(Long.parseLong($DEC_NUM.getText().substring(3), 10));}

		)
		|
		(
			BIN_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.objects.params.DirectNumber(Long.parseLong($DEC_NUM.getText().substring(3), 2));}

		)
	)
	|
	(
		REG_START number [$minDeep - 1] REG_END
		{$num = de.hechler.patrick.codesprachen.primitive.objects.params.DeepNum.create($number.num.num, $number.num.numDeep);}

	)
;

COMMENT
:
	(
		':'
		(
			(
				'#' .
			)
			| ~( '>' )
		)
	) -> skip
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

LABEL_START
:
	'#'
;

NAME
:
	[a-zA-Z_\-öäüßÖÄÜẞ0-9]+
;

WS
:
	[ \t\r\n] -> skip
;

