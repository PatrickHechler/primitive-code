grammar PrimGrammar;

datei returns
[java.util.List<de.hechler.patrick.codesprachen.primitive.compile.objects.commands.Command> cmds]
:
	{$cmds = new java.util.ArrayList<>();}

	(
		command NEW_LINE+
		{$cmds.add($command.cmd);}

	)+
;

command returns
[de.hechler.patrick.codesprachen.primitive.compile.objects.commands.Command cmd]
:
	(
		mov
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.mov, $mov.p1, $mov.p2);}
	)
	|
	(
		add
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.add, $add.p1, $add.p2);}
	)
	|
	(
		sub
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.sub, $sub.p1, $sub.p2);}
	)
	|
	(
		mul
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.mul, $mul.p1, $mul.p2);}
	)
	|
	(
		div
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.mul, $div.p1, $div.p2);}
	)
	|
	(
		neg
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.neg, $neg.num);}
	)
	|
	(
		and
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.and, $and.p1, $and.p2);}
	)
	|
	(
		or
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.or, $or.p1, $or.p2);}
	)
	|
	(
		xor
	)
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.xor, $xor.p1, $xor.p2);}
	|
	(
		not
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.not, $not.num);}
	)
	|
	(
		cmp
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegNumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.cmp, $cmp.p1, $cmp.p2);}
	)
	|
	(
		jmp
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.jmp, $jmp.lname);}
	)
	|
	(
		jmpeq
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.jmpeq, $jmpeq.lname);}
	)
	|
	(
		jmpne
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.jmpne, $jmpne.lname);}
	)
	|
	(
		jmpgt
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.jmpgt, $jmpgt.lname);}
	)
	|
	(
		jmpge
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.jmpge, $jmpge.lname);}
	)
	|
	(
		jmplo
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.jmplo, $jmplo.lname);}
	)
	|
	(
		jmple
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.jmple, $jmple.lname);}
	)
	|
	(
		push
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.NumCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.push, $push.num);}
	)
	|
	(
		pop
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.RegCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.pop, $pop.num);}
	)
	|
	(
		call
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.call, $call.lname);}
	)
	|
	(
		calleq
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.calleq, $calleq.lname);}
	)
	|
	(
		callne
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.callne, $callne.lname);}
	)
	|
	(
		callgt
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.callgt, $callgt.lname);}
	)
	|
	(
		callge
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.callge, $callge.lname);}
	)
	|
	(
		calllo
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.calllo, $calllo.lname);}
	)
	|
	(
		callle
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.callle, $callle.lname);}
	)
	|
	(
		ret
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.Command.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.ret);}
	)
	|
	(
		label
		{$cmd = de.hechler.patrick.codesprachen.primitive.compile.objects.commands.StrCommand.create(de.hechler.patrick.codesprachen.primitive.compile.enums.Commands.label, $label.labelName);}

	)
;

mov returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	MOV
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

add returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	ADD
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

sub returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	SUB
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

mul returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	MUL
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

div returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	DIV
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

neg returns
[de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num num]
:
	NEG nummer [1]
	{$num = $nummer.num;}

;

and returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	AND
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

or returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	OR
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

xor returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	XOR
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

not returns
[de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num num]
:
	NOT nummer [1]
	{$num = $nummer.num;}

;

cmp returns [de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p1,
de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num p2]
:
	CMP
	(
		nummer [1]
		{$p1 = $nummer.num;}

	) PARAM_SEPARATOR
	(
		nummer [0]
		{$p2 = $nummer.num;}

	)
;

jmp returns [String lname]
:
	JMP NAME
	{$lname = $NAME.getText();}

;

jmpeq returns [String lname]
:
	JMPEQ NAME
	{$lname = $NAME.getText();}

;

jmpne returns [String lname]
:
	JMPNE NAME
	{$lname = $NAME.getText();}

;

jmpgt returns [String lname]
:
	JMPGT NAME
	{$lname = $NAME.getText();}

;

jmpge returns [String lname]
:
	JMPGE NAME
	{$lname = $NAME.getText();}

;

jmplo returns [String lname]
:
	JMPLO NAME
	{$lname = $NAME.getText();}

;

jmple returns [String lname]
:
	JMPLE NAME
	{$lname = $NAME.getText();}

;

push returns
[de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num num]
:
	PUSH nummer [0]
	{$num = $nummer.num;}

;

pop returns
[de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num num]
:
	POP nummer [0]
	{$num = $nummer.num;}

;

call returns [String lname]
:
	CALL NAME
	{$lname = $NAME.getText();}

;

calleq returns [String lname]
:
	CALLEQ NAME
	{$lname = $NAME.getText();}

;

callne returns [String lname]
:
	CALLNE NAME
	{$lname = $NAME.getText();}

;

callgt returns [String lname]
:
	CALLGT NAME
	{$lname = $NAME.getText();}

;

callge returns [String lname]
:
	CALLGE NAME
	{$lname = $NAME.getText();}

;

calllo returns [String lname]
:
	CALLLO NAME
	{$lname = $NAME.getText();}

;

callle returns [String lname]
:
	CALLLE NAME
	{$lname = $NAME.getText();}

;

ret
:
	RET
;

label returns [String labelName]
:
	LABEL_START NAME
	{$labelName = $NAME.getText();}

;

nummer [int minDeep] returns
[de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num num]
:
	(
		HEX_SYMBOL
		(
			HEX_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.compile.objects.num.DirectNum(Long.parseLong($HEX_NUM.getText(), 10));}

		)
		|
		(
			DEC_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.compile.objects.num.DirectNum(Long.parseLong($DEC_NUM.getText(), 10));}

		)
		|
		(
			BIN_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.compile.objects.num.DirectNum(Long.parseLong($BIN_NUM.getText(), 10));}

		)
	)
	|
	(
		DEC_SYMBOL
		(
			DEC_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.compile.objects.num.DirectNum(Long.parseLong($DEC_NUM.getText(), 10));}

		)
		|
		(
			BIN_NUM
			{$num = new de.hechler.patrick.codesprachen.primitive.compile.objects.num.DirectNum(Long.parseLong($BIN_NUM.getText(), 10));}

		)
	)
	|
	(
		BIN_SYMBOL BIN_NUM
		{$num = new de.hechler.patrick.codesprachen.primitive.compile.objects.num.DirectNum(Long.parseLong($BIN_NUM.getText(), 2));}

	)
	|
	(
		REG_START nummer [minDeep - 1] REG_END
		{$num = new de.hechler.patrick.codesprachen.primitive.compile.objects.num.DeepNum($nummer.num);}

	)
	{
		if ($num.deep < minDeep || $num.deep - 0xFF > minDeep) {
			throw new RuntimeException("deep out of range: min= " + minDeep + " max=" + (minDeep + 0xFF) + " deep=" + $num.deep);
		}
	}

;

RET
:
	'RET'
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

POP
:
	'POP'
;

PUSH
:
	'PUSH'
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

MOV
:
	'MOV'
;

PARAM_SEPARATOR
:
	','
;

LABEL_START
:
	'#'
;

HEX_SYMBOL
:
	'HEX'
;

DEC_SYMBOL
:
	'DEC'
;

BIN_SYMBOL
:
	'BIN'
;

BIN_NUM
:
	[01]+
;

DEC_NUM
:
	[0-9]+
;

HEX_NUM
:
	[0-9a-fA-F]+
;

REG_START
:
	'['
;

REG_END
:
	']'
;

NAME
:
	[a-zA-Z_\-öäüßÖÄÜẞ0-9]+
;

NEW_LINE
:
	[\r\n]+
;

BLOCK_COMMENT
:
	(
		'|:'
		(
			~( '>' )
		)* '>'
	) -> skip
;

LINE_COMMENT
:
	(
		'|>>'
		(
			~( [\r\n] )
		)
	) -> skip
;

WS
:
	[ \t] -> skip
;


