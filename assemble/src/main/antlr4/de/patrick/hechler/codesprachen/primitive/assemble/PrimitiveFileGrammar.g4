/**
 * regex for grammar only:
 * '\s*returns' -> '' 
 * '\s*\[([^\[\]"]|("([^"\r\n]|\\")*"))*\]' -> '' 
 * '\s*{[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*"))*}))*}))*}' -> '' 
 */
 grammar PrimitiveFileGrammar;

 @header {
import java.util.*;
import de.patrick.hechler.codesprachen.primitive.assemble.enums.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Param.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Command.ConstantPoolCommand;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
}

 parse [long startpos] returns
 [List<Command> commands, Map<String,Long> labels, long pos] @init {
 	$pos = startpos;  
 	Map<String,Long> constants = new HashMap<>();
 	$labels = new HashMap<>();
 	$commands = new ArrayList<>();
	constants.put("INT-MEMORY", (Long) 0L);
	constants.put("INT-ERRORS", (Long) 1L);
	constants.put("INT-STREAMS", (Long) 2L);
	constants.put("INT-TIME", (Long) 3L);
	constants.put("INT-MEMORY-ALLOC", (Long) 0L);
	constants.put("INT-MEMORY-REALLOC", (Long) 1L);
	constants.put("INT-MEMORY-FREE", (Long) 2L);
	constants.put("INT-ERRORS-EXIT", (Long) 0L);
	constants.put("INT-ERRORS-UNKNOWN_COMMAND", (Long) 1L);
	constants.put("INT-STREAMS-GET_OUT", (Long) 0L);
	constants.put("INT-STREAMS-GET_LOG", (Long) 1L);
	constants.put("INT-STREAMS-GET_IN", (Long) 2L);
	constants.put("INT-STREAMS-NEW_IN", (Long) 3L);
	constants.put("INT-STREAMS-NEW_OUT", (Long) 4L);
	constants.put("INT-STREAMS-WRITE", (Long) 5L);
	constants.put("INT-STREAMS-READ", (Long) 6L);
	constants.put("INT-STREAMS-REM", (Long) 7L);
	constants.put("INT-STREAMS-MK_DIR", (Long) 8L);
	constants.put("INT-STREAMS-REM_DIR", (Long) 9L);
	constants.put("INT-STREAMS-CLOSE_STREAM", (Long) 10L);
	constants.put("INT-STREAMS-GET_POS", (Long) 11L);
	constants.put("INT-STREAMS-SET_POS", (Long) 12L);
	constants.put("INT-STREAMS-SET_POS_TO_END", (Long) 13L);
	constants.put("INT-TIME-GET", (Long) 0L);
	constants.put("INT-TIME-WAIT", (Long) 1L);
	constants.put("MAX-VALUE", (Long) 0x7FFFFFFFFFFFFFFFL);
	constants.put("MIN-VALUE", (Long) (-0x8000000000000000L));
 }
 :
 	(
 		(
 			command [$pos, constants, $labels]
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
								build.v1 = $nummer.num;
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

 command [long pos, Map<String,Long> constants, Map<String,Long> labels]
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
 				RET
 				{cmd = Commands.CMD_RET;}

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
 					JMPLO
 					{cmd = Commands.CMD_JMPLO;}

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
 					CALL
 					{cmd = Commands.CMD_CALL;}

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
 	|
 	(
 		CONSTANT_POOL
 		{$c = Command.parseCP($CONSTANT_POOL.getText(), constants, labels, pos);}

 	)
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

 RET
 :
 	'RET'
 ;

 INT
 :
 	'INT'
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
 	[a-zA-Z\-_]+
 ;

 CONSTANT
 :
 	'#' [a-zA-Z\-_]+
 ;

 LABEL_DECLARATION
 :
 	'@' [a-zA-Z\-_]+
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
 				~'>'
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
 					~( '|' )
 				)
 				|
 				(
 					'|'
 					(
 						~( '>' )
 					)
 				)
 			)*
 		) '|>'
 	) -> skip
 ;

 WS
 :
 	[ \t\r\n]+ -> skip
 ;
