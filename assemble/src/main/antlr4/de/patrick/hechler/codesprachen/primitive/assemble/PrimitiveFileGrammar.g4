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

 parse returns [List<Command> commands, Map<String,Long> labels] @init {
 	long pos = 0;
 	Map<String,Long> constants = new HashMap<>();
 	$labels = new HashMap<>();
 	$commands = new ArrayList<>();
 	Map<String,Long> myls = $labels;
 }
 :
 	(
 		(
 			command [pos, constants, $labels]
 			{
 				if ($command.c != null) {
 					$commands.add($command.c);
	 				pos += $command.c.length();
 				}
 			}

 		)
 		|
 		(
 			CONSTANT
 			(
 				nummer [pos, constants]
 				{constants.put($CONSTANT.getText().substring(1), $nummer.num);}

 			)
 			|
 			(
 				DEL
 				{constants.remove($CONSTANT.getText().substring(1));}

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
 		{$num = Long.parseLong($DEC_NUM.getText().substring(4), 10);}

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
 		POS
 		{$num = pos;}

 	)
 	|
 	(
 		CONSTANT
 		{
 			Long zw = constants.get($CONSTANT.getText());
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
 				INT
 				{cmd = Commands.CMD_INT;}

 			)
 			|
 			(
 				IRET
 				{cmd = Commands.CMD_IRET;}

 			)
 			|
 			(
 				RET
 				{cmd = Commands.CMD_RET;}

 			)

 		)
 		|
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

 			) p1 = param [pos, constants] COMMA p2 = param [pos, constants]
 		)
 		|
 		(
 			(
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

 			)
 			|
 			(
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
 					CALL
 					{cmd = Commands.CMD_CALL;}

 				)
 				|
 				(
 					CALLEQ
 					{cmd = Commands.CMD_CALLEQ;}

 				)
 				|
 				(
 					CALLNE
 					{cmd = Commands.CMD_CALLNE;}

 				)
 				|
 				(
 					CALLGT
 					{cmd = Commands.CMD_CALLGT;}

 				)
 				|
 				(
 					CALLGE
 					{cmd = Commands.CMD_CALLGE;}

 				)
 				|
 				(
 					CALLLO
 					{cmd = Commands.CMD_CALLLO;}

 				)
 				|
 				(
 					CALLLE
 					{cmd = Commands.CMD_CALLLE;}

 				)

 			)
 			|
 			(
 				(
 					SCALLLE
 					{cmd = Commands.CMD_SCALLLE;}

 				)
 				|
 				(
 					SCALLLO
 					{cmd = Commands.CMD_SCALLLO;}

 				)
 				|
 				(
 					SCALLGE
 					{cmd = Commands.CMD_SCALLGE;}

 				)
 				|
 				(
 					SCALLGT
 					{cmd = Commands.CMD_SCALLGT;}

 				)
 				|
 				(
 					SCALLNE
 					{cmd = Commands.CMD_SCALLNE;}

 				)
 				|
 				(
 					SCALLEQ
 					{cmd = Commands.CMD_SCALLEQ;}

 				)
 				|
 				(
 					SCALL
 					{cmd = Commands.CMD_SCALL;}

 				)
 				|
 				(
 					SJMPLE
 					{cmd = Commands.CMD_SJMPLE;}

 				)
 				|
 				(
 					SJMPLO
 					{cmd = Commands.CMD_SJMPLO;}

 				)
 				|
 				(
 					SJMPGE
 					{cmd = Commands.CMD_SJMPGE;}

 				)
 				|
 				(
 					SJMPGT
 					{cmd = Commands.CMD_SJMPGT;}

 				)
 				|
 				(
 					SJMPNE
 					{cmd = Commands.CMD_SJMPNE;}

 				)
 				|
 				(
 					SJMPEQ
 					{cmd = Commands.CMD_SJMPEQ;}

 				)
 				|
 				(
 					SJMP
 					{cmd = Commands.CMD_SJMP;}

 				)
 				|
 				(
 					SET_IP
 					{cmd = Commands.CMD_SET_IP;}

 				)

 			) p1 = param [pos,constants]
 		)
 		{$c = new Command(cmd, $p1.p, $p2.p);}

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

 CALLLE
 :
 	'CALLLE'
 ;

 SCALLLE
 :
 	'SCALLLE'
 ;

 CALLLO
 :
 	'CALLLO'
 ;

 SCALLLO
 :
 	'SCALLLO'
 ;

 CALLGE
 :
 	'CALLGE'
 ;

 SCALLGE
 :
 	'SCALLGE'
 ;

 CALLGT
 :
 	'CALLGT'
 ;

 SCALLGT
 :
 	'SCALLGT'
 ;

 CALLNE
 :
 	'CALLNE'
 ;

 SCALLNE
 :
 	'SCALLNE'
 ;

 CALLEQ
 :
 	'CALLEQ'
 ;

 SCALLEQ
 :
 	'SCALLEQ'
 ;

 CALL
 :
 	'CALL'
 ;

 SCALL
 :
 	'SCALL'
 ;

 JMPLE
 :
 	'JMPLE'
 ;

 SJMPLE
 :
 	'SJMPLE'
 ;

 JMPLO
 :
 	'JMPLO'
 ;

 SJMPLO
 :
 	'SJMPLO'
 ;

 JMPGE
 :
 	'JMPGE'
 ;

 SJMPGE
 :
 	'SJMPGE'
 ;

 JMPGT
 :
 	'JMPGT'
 ;

 SJMPGT
 :
 	'SJMPGT'
 ;

 JMPNE
 :
 	'JMPNE'
 ;

 SJMPNE
 :
 	'SJMPNE'
 ;

 JMPEQ
 :
 	'JMPEQ'
 ;

 SJMPEQ
 :
 	'SJMPEQ'
 ;

 JMP
 :
 	'JMP'
 ;

 SJMP
 :
 	'SJMP'
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

 IRET
 :
 	'IRET'
 ;

 INT
 :
 	'INT'
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

 HEX_NUM
 :
 	'HEX-' [0-9a-fA-F]+
 ;

 DEC_NUM
 :
 	'DEC-' [0-9]+
 ;

 OCT_NUM
 :
 	'OCT-' [0-7]+
 ;

 BIN_NUM
 :
 	'BIN-' [01]+
 ;

 NAME
 :
 	[a-zA-Z\-_]+
 ;

 DEL
 :
 	'~DEL'
 ;

 POS
 :
 	'--POS--'
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
 					~'\''
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
 					~'"'
 				)
 				|
 				(
 					'\\' .
 				)
 			)* '"'
 		)
 		|
 		(
 			~'>'
 		)
 	)* '>'
 ;

 LINE_COMMENT
 :
 	'|>'
 	(
 		~( [\r\n] )
 	)* -> skip
 ;

 BLOCK_COMMENT
 :
 	'|:'
 	(
 		~'|'
 		|
 		(
 			'|' ~'>'
 		)
 	)* '|>' -> skip
 ;

 WS
 :
 	[ \t\r\n]+ -> skip
 ;
