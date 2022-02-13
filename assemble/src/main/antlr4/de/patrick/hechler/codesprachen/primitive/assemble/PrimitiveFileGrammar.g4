/**
 * regex for grammar only:
 * '\s*returns' -> '' 
 * '\s*\[([^\[\]"]|("([^"\r\n]|\\")*"))*\]' -> '' 
 * '\s*{[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*"))*}))*}))*}' -> '' 
 */
 grammar PrimitiveFileGrammar;

 @parser::header {
import java.util.*;
import de.patrick.hechler.codesprachen.primitive.assemble.enums.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Param.*;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
}

 @parser::members {
	private int getAlign(boolean align, long pos) {
		if (align) {
			int mod = (int) (pos % 8);
			if (mod != 0) {
				return 8 - mod;
			}
		}
		return 0;
	}
 }

 parse [long startpos, boolean align, Map<String,Long> constants] returns
 [List<Command> commands, Map<String,Long> labels, long pos] @init {
 	$pos = startpos;
 	$labels = new HashMap<>();
 	$commands = new ArrayList<>();
 	$commands.add(new CompilerCommandCommand(align ? CompilerCommand.align : CompilerCommand.notAlign));
	constants.putIfAbsent("INT-ERRORS-UNKNOWN_COMMAND", (Long) 0L);
	constants.putIfAbsent("INT-ERRORS-ILLEGAL_INTERRUPT", (Long) 1L);
	constants.putIfAbsent("INT-ERRORS-ILLEGAL_MEMORY", (Long) 2L);
	constants.putIfAbsent("INT-ERRORS-ARITHMETIC_ERROR", (Long) 3L);
	constants.putIfAbsent("INT-EXIT", (Long) 4L);
	constants.putIfAbsent("INT-MEMORY-ALLOC", (Long) 5L);
	constants.putIfAbsent("INT-MEMORY-REALLOC", (Long) 6L);
	constants.putIfAbsent("INT-MEMORY-FREE", (Long) 7L);
	constants.putIfAbsent("INT-STREAMS-NEW_IN", (Long) 8L);
	constants.putIfAbsent("INT-STREAMS-NEW_OUT", (Long) 9L);
	constants.putIfAbsent("INT-STREAMS-NEW_APPEND", (Long) 10L);
	constants.putIfAbsent("INT_STREAMS-NEW_IN_OUT", (Long) 11L);
	constants.putIfAbsent("INT-STREAMS-NEW_APPEND_IN_OUT", (Long) 12L);
	constants.putIfAbsent("INT-STREAMS-WRITE", (Long) 13L);
	constants.putIfAbsent("INT-STREAMS-READ", (Long) 14L);
	constants.putIfAbsent("INT-STREAMS-CLOSE_STREAM", (Long) 15L);
	constants.putIfAbsent("INT-STREAMS-GET_POS", (Long) 16L);
	constants.putIfAbsent("INT-STREAMS-SET_POS", (Long) 17L);
	constants.putIfAbsent("INT-STREAMS-SET_POS_TO_END", (Long) 18L);
	constants.putIfAbsent("INT-STREAMS-REM", (Long) 19L);
	constants.putIfAbsent("INT-STREAMS-MK_DIR", (Long) 20L);
	constants.putIfAbsent("INT-STREAMS-REM_DIR", (Long) 21L);
	constants.putIfAbsent("INT-TIME-GET", (Long) 22L);
	constants.putIfAbsent("INT-TIME-WAIT", (Long) 23L);
	constants.putIfAbsent("INT-SOCKET-CLIENT-CREATE", (Long) 24L);
	constants.putIfAbsent("INT-SOCKET-CLIENT-CONNECT", (Long) 25L);
	constants.putIfAbsent("INT-SOCKET-SERVER-CREATE", (Long) 26L);
	constants.putIfAbsent("INT-SOCKET-SERVER-LISTEN", (Long) 27L);
	constants.putIfAbsent("INT-SOCKET-SERVER-ACCEPT", (Long) 28L);
	constants.putIfAbsent("INT-RANDOM", (Long) 29L);
	constants.putIfAbsent("INTERRUPT_COUNT", (Long) 30L);
	constants.putIfAbsent("MAX-VALUE", (Long) 0x7FFFFFFFFFFFFFFFL);
	constants.putIfAbsent("MIN-VALUE", (Long) (-0x8000000000000000L));
	constants.putIfAbsent("STD-IN", (Long) 0L);
	constants.putIfAbsent("STD-OUT", (Long) 1L);
	constants.putIfAbsent("STD-LOG", (Long) 2L);
	constants.putIfAbsent("FP-NAN", (Long) 0x7FFE000000000000L);
	constants.putIfAbsent("FP-MAX-VALUE", (Long) 0x7FEFFFFFFFFFFFFFL);
	constants.putIfAbsent("FP-MIN-VALUE", (Long) 0x0000000000000001L);
	constants.putIfAbsent("FP-POS-INFINITY", (Long) 0x7FF0000000000000L);
	constants.putIfAbsent("FP-NEG-INFINITY", (Long) (-0x7FF0000000000000L));
 }
 :
 	(
 		(
 			{$pos += getAlign(align, $pos);}

 			CONSTANT_POOL
 			{
				ConstsContext cc = Command.parseCP($CONSTANT_POOL.getText(), constants, $labels, $pos, align);
				align = cc.align;
				$pos += cc.pool.length();
				$commands.add(cc.pool);
			}

 		)
 		|
 		(
 			{$pos += getAlign(align, $pos);}

 			command [$pos, constants, $labels, align]
 			{
 				if ($command.c != null) {
 					$commands.add($command.c);
	 				$pos += $command.c.length();
 				}
 			}

 		)
 		|
 		(
 			CONSTANT
 			(
 				(
 					nummer [$pos, constants]
 					{constants.put($CONSTANT.getText().substring(1), $nummer.num);}

 				)
 				|
 				(
 					DEL
 					{constants.remove($CONSTANT.getText().substring(1));}

 				)
 			)
 		)
 		|
 		(
 			(
 				CD_ALIGN
 				{
					$commands.add(new CompilerCommandCommand(CompilerCommand.align));
					align = true;
				}

 			)
 			|
 			(
 				CD_NOT_ALIGN
 				{
					$commands.add(new CompilerCommandCommand(CompilerCommand.notAlign));
					align = false;
				}

 			)
 		)
 	)* EOF
 ;

 param [long pos, Map<String,Long> constants] returns [Param p]
 :
 	(
 		{ParamBuilder builder = new ParamBuilder();}

 		(
 			(
 				AX
 				{builder.art = ParamBuilder.A_AX;}

 			)
 			|
 			(
 				BX
 				{builder.art = ParamBuilder.A_BX;}

 			)
 			|
 			(
 				CX
 				{builder.art = ParamBuilder.A_CX;}

 			)
 			|
 			(
 				DX
 				{builder.art = ParamBuilder.A_DX;}

 			)
 			|
 			(
 				nummer [pos, constants]
 				{
 				builder.art = ParamBuilder.A_NUM;
 				builder.v1 = $nummer.num;
 			}

 			)
 		)
 		{$p = builder.build();}

 	)
 	|
 	(
 		ECK_KL_AUF
 		{
 			ParamBuilder build = new ParamBuilder();
 			build.art = ParamBuilder.B_REG;
 		}

 		(
 			(
 				(
 					(
 						AX
 						{build.art |= ParamBuilder.A_AX;}

 					)
 					|
 					(
 						BX
 						{build.art |= ParamBuilder.A_BX;}

 					)
 					|
 					(
 						CX
 						{build.art |= ParamBuilder.A_CX;}

 					)
 					|
 					(
 						DX
 						{build.art |= ParamBuilder.A_DX;}

 					)
 					|
 					(
 						nummer [pos,constants]
 						{
							build.art |= ParamBuilder.A_NUM;
							build.v1 = $nummer.num;
						}

 					)
 				)
 				(
 					PLUS
 					{build.art &= ~ParamBuilder.B_REG;}

 					(
 						(
 							AX
 							{build.art |= ParamBuilder.B_AX;}

 						)
 						|
 						(
 							BX
 							{build.art |= ParamBuilder.B_BX;}

 						)
 						|
 						(
 							CX
 							{build.art |= ParamBuilder.B_CX;}

 						)
 						|
 						(
 							DX
 							{build.art |= ParamBuilder.B_DX;}

 						)
 						|
 						(
 							nummer [pos,constants]
 							{
								build.art |= ParamBuilder.B_NUM;
								build.v2 = $nummer.num;
							}

 						)
 					)
 				)?
 			)
 		) ECK_KL_ZU
 		{$p = build.build();}

 	)
 	|
 	(
 		NAME
 		{$p = Param.createLabel($NAME.getText());}

 	)
 ;

 nummer [long pos, Map<String, Long> constants] returns [long num]
 :
 	(
 		DEC_FP_NUM
 		{$num = Double.doubleToRawLongBits(Double.parseDouble($DEC_FP_NUM.getText()));}

 	)
 	|
 	(
 		UNSIGNED_HEX_NUM
 		{$num = Long.parseUnsignedLong($UNSIGNED_HEX_NUM.getText().substring(5), 16);}

 	)
 	|
 	(
 		HEX_NUM
 		{$num = Long.parseLong($HEX_NUM.getText().substring(4), 16);}

 	)
 	|
 	(
 		DEC_NUM
 		{$num = Long.parseLong($DEC_NUM.getText(), 10);}

 	)
 	|
 	(
 		DEC_NUM0
 		{$num = Long.parseLong($DEC_NUM0.getText().substring(4), 10);}

 	)
 	|
 	(
 		OCT_NUM
 		{$num = Long.parseLong($OCT_NUM.getText().substring(4), 8);}

 	)
 	|
 	(
 		BIN_NUM
 		{$num = Long.parseLong($BIN_NUM.getText().substring(4), 2);}

 	)
 	|
 	(
 		NEG_HEX_NUM
 		{$num = Long.parseLong("-" + $NEG_HEX_NUM.getText().substring(5), 16);}

 	)
 	|
 	(
 		NEG_DEC_NUM
 		{$num = Long.parseLong("-" + $NEG_DEC_NUM.getText().substring(5), 10);}

 	)
 	|
 	(
 		NEG_DEC_NUM0
 		{$num = Long.parseLong($NEG_DEC_NUM0.getText(), 10);}

 	)
 	|
 	(
 		NEG_OCT_NUM
 		{$num = Long.parseLong("-" + $NEG_OCT_NUM.getText().substring(5), 8);}

 	)
 	|
 	(
 		NEG_BIN_NUM
 		{$num = Long.parseLong("-" + $NEG_BIN_NUM.getText().substring(5), 2);}

 	)
 	|
 	(
 		POS
 		{$num = pos;}

 	)
 	|
 	(
 		CONSTANT
 		{
 			Long zw = constants.get($CONSTANT.getText().substring(1));
 			if (zw == null) {
 				throw new RuntimeException("unknown constant: '" + $CONSTANT.getText() + "', known constants: '" + constants + "'");
 			}
 			$num = (long) zw;
 		}

 	)
 ;

 command
 [long pos, Map<String,Long> constants, Map<String,Long> labels, boolean align]
 returns [Command c] @init {Commands cmd = null;}
 :
 	(
 		(
 			(
 				(
 					MOV
 					{cmd = Commands.CMD_MOV;}

 				)
 				|
 				(
 					SWAP
 					{cmd = Commands.CMD_SWAP;}

 				)
 				|
 				(
 					ADD
 					{cmd = Commands.CMD_ADD;}

 				)
 				|
 				(
 					SUB
 					{cmd = Commands.CMD_SUB;}

 				)
 				|
 				(
 					ADDC
 					{cmd = Commands.CMD_ADDC;}

 				)
 				|
 				(
 					SUBC
 					{cmd = Commands.CMD_SUBC;}

 				)
 				|
 				(
 					ADDFP
 					{cmd = Commands.CMD_ADDFP;}

 				)
 				|
 				(
 					SUBFP
 					{cmd = Commands.CMD_SUBFP;}

 				)
 				|
 				(
 					MULFP
 					{cmd = Commands.CMD_MULFP;}

 				)
 				|
 				(
 					DIVFP
 					{cmd = Commands.CMD_DIVFP;}

 				)
 				|
 				(
 					MUL
 					{cmd = Commands.CMD_MUL;}

 				)
 				|
 				(
 					DIV
 					{cmd = Commands.CMD_DIV;}

 				)
 				|
 				(
 					AND
 					{cmd = Commands.CMD_AND;}

 				)
 				|
 				(
 					OR
 					{cmd = Commands.CMD_OR;}

 				)
 				|
 				(
 					XOR
 					{cmd = Commands.CMD_XOR;}

 				)
 				|
 				(
 					CMP
 					{cmd = Commands.CMD_CMP;}

 				)
 			) p1 = param [pos, constants] COMMA p2 = param [pos, constants]
 			{$c = new Command(cmd, $p1.p, $p2.p);}

 		)
 		|
 		(
 			(
 				(
 					RET
 					{cmd = Commands.CMD_RET;}

 				)
 				|
 				(
 					IRET
 					{cmd = Commands.CMD_IRET;}

 				)
 			)
 			{$c = new Command(cmd, null, null);}

 		)
 		|
 		(
 			(
 				(
 					INC
 					{cmd = Commands.CMD_INC;}

 				)
 				|
 				(
 					DEC
 					{cmd = Commands.CMD_DEC;}

 				)
 				|
 				(
 					NTFP
 					{cmd = Commands.CMD_NTFP;}

 				)
 				|
 				(
 					FPTN
 					{cmd = Commands.CMD_FPTN;}

 				)
 				|
 				(
 					GET_INTCNT
 					{cmd = Commands.CMD_GET_INTCNT;}

 				)
 				|
 				(
 					GET_INTS
 					{cmd = Commands.CMD_GET_INTS;}

 				)
 				|
 				(
 					GET_IP
 					{cmd = Commands.CMD_GET_IP;}

 				)
 				|
 				(
 					GET_SP
 					{cmd = Commands.CMD_GET_SP;}

 				)
 				|
 				(
 					INT
 					{cmd = Commands.CMD_INT;}

 				)
 				|
 				(
 					RASH
 					{cmd = Commands.CMD_RASH;}

 				)
 				|
 				(
 					RLSH
 					{cmd = Commands.CMD_RLSH;}

 				)
 				|
 				(
 					LSH
 					{cmd = Commands.CMD_LSH;}

 				)
 				|
 				(
 					NOT
 					{cmd = Commands.CMD_NOT;}

 				)
 				|
 				(
 					NEG
 					{cmd = Commands.CMD_NEG;}

 				)
 				|
 				(
 					PUSH
 					{cmd = Commands.CMD_PUSH;}

 				)
 				|
 				(
 					POP
 					{cmd = Commands.CMD_POP;}

 				)
 				|
 				(
 					JMP
 					{cmd = Commands.CMD_JMP;}

 				)
 				|
 				(
 					JMPEQ
 					{cmd = Commands.CMD_JMPEQ;}

 				)
 				|
 				(
 					JMPNE
 					{cmd = Commands.CMD_JMPNE;}

 				)
 				|
 				(
 					JMPGT
 					{cmd = Commands.CMD_JMPGT;}

 				)
 				|
 				(
 					JMPGE
 					{cmd = Commands.CMD_JMPGE;}

 				)
 				|
 				(
 					JMPLT
 					{cmd = Commands.CMD_JMPLT;}

 				)
 				|
 				(
 					JMPLE
 					{cmd = Commands.CMD_JMPLE;}

 				)
 				|
 				(
 					JMPCS
 					{cmd = Commands.CMD_JMPCS;}

 				)
 				|
 				(
 					JMPCC
 					{cmd = Commands.CMD_JMPCC;}

 				)
 				|
 				(
 					JMPZS
 					{cmd = Commands.CMD_JMPZS;}

 				)
 				|
 				(
 					JMPZC
 					{cmd = Commands.CMD_JMPZC;}

 				)
 				|
 				(
 					CALL
 					{cmd = Commands.CMD_CALL;}

 				)
 				|
 				(
 					SET_INTCNT
 					{cmd = Commands.CMD_SET_INTCNT;}

 				)
 				|
 				(
 					SET_INTS
 					{cmd = Commands.CMD_SET_INTS;}

 				)
 				|
 				(
 					SET_SP
 					{cmd = Commands.CMD_SET_SP;}

 				)
 				|
 				(
 					SET_IP
 					{cmd = Commands.CMD_SET_IP;}

 				)
 			) p1 = param [pos,constants]
 			{$c = new Command(cmd, $p1.p, null);}

 		)
 	)
 	|
 	(
 		LABEL_DECLARATION
 		{
 			labels.put($LABEL_DECLARATION.getText().substring(1), (Long) pos);
	 		$c = null;
	 	}

 	)
 ;

 SET_INTCNT
 :
 	'SET_INTCNT'
 ;

 SET_INTS
 :
 	'SET_INTS'
 ;

 SET_IP
 :
 	'SET_IP'
 ;

 SET_SP
 :
 	'SET_SP'
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

 GET_INTCNT
 :
 	'GET_INTCNT'
 ;

 GET_INTS
 :
 	'GET_INTS'
 ;

 GET_SP
 :
 	'GET_SP'
 ;

 GET_IP
 :
 	'GET_IP'
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

 AX
 :
 	'AX'
 ;

 BX
 :
 	'BX'
 ;

 CX
 :
 	'CX'
 ;

 DX
 :
 	'DX'
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

 POS
 :
 	'--POS--'
 ;

 NAME
 :
 	[a-zA-Z\-_] [a-zA-Z\-_0-9]*
 ;

 CONSTANT
 :
 	'#' [a-zA-Z\-_] [a-zA-Z\-_0-9]*
 ;

 LABEL_DECLARATION
 :
 	'@' [a-zA-Z\-_] [a-zA-Z\-_0-9]*
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

 CONSTANT_POOL
 :
 	':'
 	(
 		(
 			BLOCK_COMMENT
 		)
 		|
 		(
 			LINE_COMMENT
 		)
 		|
 		(
 			'\''
 			(
 				(
 					~[\r\n'\\]
 				)
 				|
 				(
 					'\\' .
 				)
 			)* '\''
 		)
 		|
 		(
 			'"'
 			(
 				(
 					~[\r\n"\\]
 				)
 				|
 				(
 					'\\' .
 				)
 			)* '"'
 		)
 		|
 		(
 			(
 				~( '>' )
 			)+
 		)
 	)* '>'
 ;

 LINE_COMMENT
 :
 	(
 		'|>'
 		(
 			~( [\r\n] )
 		)*
 	) -> skip
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
 	) -> skip
 ;

 WS
 :
 	[ \t\r\n]+ -> skip
 ;
