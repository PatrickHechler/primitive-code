/**
 * regex for grammar only:
 * '\s*returns' -> '' 
 * '\s*\[([^\[\]"]|("([^"\r\n]|\\")*"))*\]' -> '' 
 * '\s*{[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*"))*}))*}))*}' -> '' 
 */
grammar HighlithGrammar;

@parser::header {
import java.util.*;
import de.patrick.hechler.codesprachen.primitive.asmeditor.objects.*;
}

highlith [Map<String,Long> constants] returns [List<HL> hls] @init {
 	$hls = new ArrayList<>();
 	Map<String,Long> labels = new HashMap<>();
 	boolean enabled = true;
 	int disabledSince = -1;
 	boolean inConstPool = false;
 	int val = -1;
 }
:
	(
		{val = -1;}

		comment [$hls]*
		(
			(
				t = CONSTANT
				{
					val = -2;
	 				$hls.add(new HL($NAME.getStartIndex(), $NAME.getStopIndex(), HL.H_CONST_DECL));
				}

				comment [$hls]* constBerechnungDirekt [$hls, constants]
			)
			|
			(
				command [$hls, constants, labels]
				{val = -2;}

			)
			|
			( /*errors*/
				(
					t = IP
					| t = SP
					| t = STATUS
					| t = INTCNT
					| t = INTP
					| t = XNN
					| t = NAME
					| t = ECK_KL_AUF
					| t = ECK_KL_ZU
					| t = PLUS
					| t = FRAGEZEICHEN
					| t = DOPPELPUNKT
					| t = INCLUSIVODER
					| t = EXCLUSIVPDER
					| t = UND
					| t = GLEICH_GLEICH
					| t = UNGLEICH
					| t = GROESSER
					| t = GROESSER_GLEICH
					| t = KLEINER_GLEICH
					| t = KLEINER
					| t = LINKS_SCHUB
					| t = LOGISCHER_RECHTS_SCHUB
					| t = ARITMETISCHER_RECHTS_SCHUB
					| t = MINUS
					| t = MAL
					| t = GETEILT
					| t = MODULO
					| t = EXIST_CONSTANT
					| t = RND_KL_AUF
					| t = RND_KL_ZU
					| t = DEC_FP_NUM
					| t = UNSIGNED_HEX_NUM
					| t = HEX_NUM
					| t = DEC_NUM
					| t = DEC_NUM0
					| t = OCT_NUM
					| t = BIN_NUM
					| t = NEG_HEX_NUM
					| t = NEG_DEC_NUM
					| t = NEG_DEC_NUM0
					| t = NEG_OCT_NUM
					| t = NEG_BIN_NUM
					| t = POS
					| t = ANY
				)
				{val = HL.H_ERR;}

			)
		)
		{
 			if (!enabled) {
 				val = HL.H_DISABLED;
 			}
			if (val != -2) {
				$hls.add(new HL(val, $t.getStartIndex(), $t.getStopIndex()));
			}
 		}

	)* EOF
;

sr returns [HL hl, long srnum] @init {int val = -1;}
:
	(
		t = IP
		{val = HL.H_REG_IP;}

	)
	|
	(
		t = SP
		{val = HL.H_REG_SP;}

	)
	|
	(
		t = STATUS
		{val = HL.H_REG_STATUS;}

	)
	|
	(
		t = INTCNT
		{val = HL.H_REG_INTCNT;}

	)
	|
	(
		t = INTP
		{val = HL.H_REG_INTP;}

	)
	|
	(
		t = XNN
		{val = HL.H_REG_XNN;}

	)
	{$hl = new HL($t.getStartIndex(), $t.getStopIndex(), val);}

;

param [List<HL> hls, Map<String,Long> constants]
:
	(
		NAME
		{
			if (constants.containsKey($NAME.getText())) {
 				hls.add(new HL($NAME.getStartIndex(), $NAME.getStopIndex(), HL.H_PARAM_CONSTANT));
			} else {
 				hls.add(new HL($NAME.getStartIndex(), $NAME.getStopIndex(), HL.H_PARAM_LABEL));
			}
		}

	)
	|
	(
		(
			(
				sr
				{hls.add($sr.hl);}

			)
			|
			(
				nummerNoConstant
				{hls.add($nummerNoConstant.hl);}

			)
		)
	)
	|
	(
		t = ECK_KL_AUF
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_PARAM_BRACE));}

		(
			(
				(
					(
						sr
						{hls.add($sr.hl);}

					)
					|
					(
						nummer [hls, constants]
						{hls.add($nummer.hl);}

					)
				)
				(
					t = PLUS
					{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_PARAM_PLUS));}

					(
						(
							sr
							{hls.add($sr.hl);}

						)
						|
						(
							nummer [hls, constants]
							{hls.add($nummer.hl);}

						)
					)
				)?
			)
		) t = ECK_KL_ZU
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_PARAM_BRACE));}

	)
;

constBerechnung [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungInclusivoder [hls, constants]
	(
		comment [hls]* t = FRAGEZEICHEN
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnung [hls, constants] comment [hls]* t = DOPPELPUNKT
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnungInclusivoder [hls, constants]
	)?
;

constBerechnungInclusivoder [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungExclusivoder [hls, constants]
	(
		comment [hls]* t = INCLUSIVODER
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		comment [hls]* constBerechnungExclusivoder [hls, constants]
	)*
;

constBerechnungExclusivoder [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungUnd [hls, constants]
	(
		comment [hls]* t = EXCLUSIVPDER
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		comment [hls]* constBerechnungUnd [hls, constants]
	)*
;

constBerechnungUnd [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungGleichheit [hls, constants]
	(
		t = UND
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnungGleichheit [hls, constants]
	)*
;

constBerechnungGleichheit [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungRelativeTests [hls, constants]
	(
		(
			(
				t = GLEICH_GLEICH
				| t = UNGLEICH
			)
		)
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnungRelativeTests [hls, constants]
	)*
;

constBerechnungRelativeTests [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungSchub [hls, constants]
	(
		(
			(
				t = GROESSER
				| t = GROESSER_GLEICH
				| t = KLEINER_GLEICH
				| t = KLEINER
			)
		)
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnungSchub [hls, constants]
	)*
;

constBerechnungSchub [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungStrich [hls, constants]
	(
		(
			(
				t = LINKS_SCHUB
				| t = LOGISCHER_RECHTS_SCHUB
				| t = ARITMETISCHER_RECHTS_SCHUB
			)
		)
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnungStrich [hls, constants]
	)*
;

constBerechnungStrich [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungPunkt [hls, constants]
	(
		(
			(
				t = PLUS
				| t = MINUS
			)
		)
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnungPunkt [hls, constants]
	)*
;

constBerechnungPunkt [List<HL> hls, Map<String, Long> constants]
:
	constBerechnungDirekt [hls, constants]
	(
		(
			(
				t = MAL
				| t = GETEILT
				| t = MODULO
			)
		)
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_OP));}

		constBerechnungDirekt [hls, constants]
	)*
;

constBerechnungDirekt [List<HL> hls, Map<String, Long> constants]
:
	(
		nummer [hls, constants]
	)
	|
	(
		t = EXIST_CONSTANT
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_EXIST));}

	)
	|
	(
		t = RND_KL_AUF
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_RND));}

		comment [hls]* constBerechnung [hls, constants] comment [hls]* t = RND_KL_ZU
		{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_CONST_CALC_RND));}

	)
;

nummer [List<HL> hls, Map<String, Long> constants] returns [HL hl]
:
	(
		nummerNoConstant
		{$hl = $nummerNoConstant.hl;}

	)
	|
	(
		NAME
		{
 			if (constants.containsKey($NAME.getText())) {
 				$hl = new HL($NAME.getStartIndex(), $NAME.getStopIndex(), HL.H_ERR);
 			} else {
	 			$hl = new HL($NAME.getStartIndex(), $NAME.getStopIndex(), HL.H_NUMMER_CONSTANT);
 			}
 		}

	)
;

nummerNoConstant returns [HL hl]
:
	(
		(
			t = DEC_FP_NUM
			| t = UNSIGNED_HEX_NUM
			| t = HEX_NUM
			| t = DEC_NUM
			| t = DEC_NUM0
			| t = OCT_NUM
			| t = BIN_NUM
			| t = NEG_HEX_NUM
			| t = NEG_DEC_NUM
			| t = NEG_DEC_NUM0
			| t = NEG_OCT_NUM
			| t = NEG_BIN_NUM
		)
		{$hl = new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_NUMMBER);}

	)
	|
	(
		(
			t = POS
		)
		{$hl = new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_NUMMBER_POS);}

	)
;

command [List<HL> hls, Map<String,Long> constants, Map<String,Long> labels]
:
	(
		(
			(
				(
					t = MOV
					| t = LEA
					| t = SWAP
					| t = ADD
					| t = SUB
					| t = ADDC
					| t = SUBC
					| t = ADDFP
					| t = SUBFP
					| t = MULFP
					| t = DIVFP
					| t = MUL
					| t = DIV
					| t = AND
					| t = OR
					| t = XOR
					| t = CMP
					| t = RASH
					| t = RLSH
					| t = LSH
				)
			)
			{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_TWO_PARAM_CMD));}

			comment [hls]* param [hls, constants] t = COMMA
			{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_TWO_PARAM_SEP));}

			comment [hls]* param [hls, constants]
		)
		|
		(
			(
				(
					t = INC
					| t = DEC
					| t = NTFP
					| t = FPTN
					| t = INT
					| t = NOT
					| t = NEG
					| t = PUSH
					| t = POP
					| t = JMP
					| t = JMPEQ
					| t = JMPNE
					| t = JMPGT
					| t = JMPGE
					| t = JMPLT
					| t = JMPLE
					| t = JMPCS
					| t = JMPCC
					| t = JMPZS
					| t = JMPZC
					| t = CALL
				)
			)
			{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_ONE_PARAM_CMD));}

			comment [hls]* p1 = param [hls, constants]
		)
		|
		(
			(
				(
					t = RET
					| t = IRET
				)
			)
			{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_NO_PARAM_CMD));}

		)
	)
	|
	(
		t = LABEL_DECLARATION
		{
 			labels.put($t.getText().substring(1), -1L);
	 		hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_LABEL_DECL));
	 	}

	)
;

comment [List<HL> hls]
:
	(
		t = LINE_COMMENT
		| t = BLOCK_COMMENT
	)
	{hls.add(new HL($t.getStartIndex(), $t.getStopIndex(), HL.H_COMMENT));}

;

CALL
:
	'CALL'
;

JMPZC
:
	'JMPZC'
;

JMPZS
:
	'JMPZS'
;

JMPCC
:
	'JMPCC'
;

JMPCS
:
	'JMPCS'
;

JMPLE
:
	'JMPLE'
;

JMPLT
:
	'JMPLT'
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

POP
:
	'POP'
;

PUSH
:
	'PUSH'
;

NEG
:
	'NEG'
;

NOT
:
	'NOT'
;

LSH
:
	'LSH'
;

RLSH
:
	'RLSH'
;

RASH
:
	'RASH'
;

CMP
:
	'CMP'
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

DIV
:
	'DIV'
;

MUL
:
	'MUL'
;

SUBC
:
	'SUBC'
;

ADDC
:
	'ADDC'
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

LEA
:
	'LEA'
;

SWAP
:
	'SWAP'
;

RET
:
	'RET'
;

IRET
:
	'IRET'
;

INT
:
	'INT'
;

DEC
:
	'DEC'
;

INC
:
	'INC'
;

ADDFP
:
	'ADDFP'
;

SUBFP
:
	'SUBFP'
;

MULFP
:
	'MULFP'
;

DIVFP
:
	'DIVFP'
;

NTFP
:
	'NTFP'
;

FPTN
:
	'FPTN'
;

IP
:
	'IP'
;

SP
:
	'SP'
;

STATUS
:
	'STATUS'
;

INTCNT
:
	'INTCNT'
;

INTP
:
	'INTP'
;

XNN
:
	'X'
	(
		(
			[0-9A-E] [0-9A-F]
		)
		|
		(
			'F' [0-9A]
		)
	)
;

ECK_KL_AUF
:
	'['
;

ECK_KL_ZU
:
	']'
;

PLUS
:
	'+'
;

COMMA
:
	','
;

RND_KL_AUF
:
	'('
;

RND_KL_ZU
:
	')'
;

MINUS
:
	'-'
;

MAL
:
	'*'
;

GETEILT
:
	'/'
;

MODULO
:
	'%'
;

LINKS_SCHUB
:
	'<<'
;

LOGISCHER_RECHTS_SCHUB
:
	'>>'
;

ARITMETISCHER_RECHTS_SCHUB
:
	'>>>'
;

GROESSER
:
	'>'
;

GROESSER_GLEICH
:
	'>='
;

KLEINER_GLEICH
:
	'<='
;

KLEINER
:
	'<'
;

GLEICH_GLEICH
:
	'=='
;

UNGLEICH
:
	'!='
;

UND
:
	'&'
;

EXCLUSIVPDER
:
	'^'
;

INCLUSIVODER
:
	'|'
;

DOPPELPUNKT
:
	':'
;

FRAGEZEICHEN
:
	'?'
;

UNSIGNED_HEX_NUM
:
	'UHEX-' [0-9a-fA-F]+
;

NEG_HEX_NUM
:
	'NHEX-' [0-9a-fA-F]+
;

NEG_DEC_NUM
:
	'NDEC-' [0-9]+
;

NEG_DEC_NUM0
:
	'-' [0-9]+
;

NEG_OCT_NUM
:
	'NOCT-' [0-7]+
;

NEG_BIN_NUM
:
	'NBIN-' [01]+
;

HEX_NUM
:
	'HEX-' [0-9a-fA-F]+
;

DEC_NUM0
:
	'DEC-' [0-9]+
;

DEC_NUM
:
	[0-9]+
;

DEC_FP_NUM
:
	'-'? [0-9]* '.' [0-9]*
;

OCT_NUM
:
	'OCT-' [0-7]+
;

BIN_NUM
:
	'BIN-' [01]+
;

DEL
:
	'~DEL'
;

IF
:
	'~IF'
;

ELSE_IF
:
	'~ELSE-IF'
;

ELSE
:
	'~ELSE'
;

ENDIF
:
	'~ENDIF'
;

ERROR
:
	'~ERROR'
;

POS
:
	'--POS--'
;

EXIST_CONSTANT
:
	'#~' NAME
;

NAME
:
	[a-zA-Z_] [a-zA-Z_0-9]*
;

CP_CHAR_STRING_START
:
	'CHARS'
;

CP_CHAR_STRING
:
	'\''
	(
		~( '\'' | '\\' )
		|
		(
			'\\' .
		)
	) '\''
;

CP_STRING
:
	'"'
	(
		~( '"' | '\\' )
		|
		(
			'\\' .
		)
	) '"'
;

CP_END
:
	'>'
;

CONSTANT
:
	'#' NAME
;

LABEL_DECLARATION
:
	'@' NAME
;

CD_NOT_ALIGN
:
	'$NOT_ALIGN'
	| '$NOT-ALIGN'
	| '$not_align'
	| '$not-align'
;

CD_ALIGN
:
	'$ALIGN'
	| '$align'
;

ERROR_HEX
:
	[hH] ':'
;

ERROR_MESSAGE_START
:
	'{'
;

ERROR_MESSAGE_END
:
	'}'
;

STR_STR
:
	'"'
	(
		(
			~'"'
		)
		|
		(
			'\\' ~( '\r' | '\n' )
		)
	)* '"'
;

LINE_COMMENT
:
	(
		'|>'
		(
			~( [\r\n] )
		)*
	)
;

BLOCK_COMMENT
:
	(
		'|:'
		(
			(
				(
					~( ':' )
				)
				|
				(
					':'
					(
						~( '>' )
					)
				)
			)*
		) ':>'
	)
;

WS
:
	[ \t\r\n]+ -> skip
;

ANY
:
	.
;
