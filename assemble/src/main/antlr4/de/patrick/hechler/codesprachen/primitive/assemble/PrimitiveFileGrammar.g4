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
import de.patrick.hechler.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.patrick.hechler.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
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

 parse
 [long startpos, boolean align, Map<String,Long> constants, boolean bailError]
 returns [List<Command> commands, Map<String,Long> labels, long pos] @init {
 	$pos = startpos;
 	$labels = new HashMap<>();
 	$commands = new ArrayList<>();
 	$commands.add(new CompilerCommandCommand(align ? CompilerCommand.align : CompilerCommand.notAlign));
 	boolean enabled = true;
 	int disabledSince = -1;
 	/* 
 	 * use alll three states of the Boolean class (true, false, null)
 	 * true: enabled
 	 * false: disabled
 	 * null: was enabled, but is now disabled
 	 * 
 	 * null is used for elseif structures
 	 * example: 
 	 * 	(~IF 0 ~ELSE-IF 1 ~ELSE-IF 1 ~ELSE ~ENDIF)
 	 * 		then it is at first false (~IF 0)
 	 * 		then it becomes true (~ELSE-IF 1)
 	 * 		then if becomes null (~ELSE-IF 1), because it is true
 	 * 		then it stays null (~ELSE)
 	 * 		then the stack removes the frame (~ENDIF)
 	 */
 	List<Boolean> stack = new ArrayList();
 }
 :
 	(
 		anything
 		[enabled, disabledSince, stack, align, constants, $commands, $labels, $pos, bailError]
 		{
 			enabled = $anything.enabled_;
 			disabledSince = $anything.disabledSince_;
			stack = $anything.stack_;
			align = $anything.align_;
			constants = $anything.constants_;
			$commands = $anything.commands_;
			$labels = $anything.labels_;
			$pos = $anything.pos_;
 		}

 	)* EOF
 ;

 anything
 [boolean enabled, int disabledSince, List<Boolean> stack, boolean align, Map<String, Long> constants, List<Command> commands, Map<String, Long> labels, long pos, boolean be]
 returns
 [boolean enabled_, int disabledSince_, List<Boolean> stack_, boolean align_, Map<String, Long> constants_, List<Command> commands_, Map<String, Long> labels_, long pos_, ConstsContext cc]
 @init {
	$enabled_ = enabled;
	$disabledSince_ = disabledSince;
	$stack_ = stack;
	$align_ = align;
	$constants_ = new HashMap<>(constants);
	$commands_ = commands;
	$labels_ = labels;
	$pos_ = pos;
 }
 :
 	(
 		(
 			comment+
 		)
 		|
 		(
 			{pos += getAlign(align, pos);}

 			CONSTANT_POOL
 			{
				if (enabled) {
					$cc = Command.parseCP($CONSTANT_POOL.getText(), constants, labels, pos, align, $CONSTANT_POOL.getLine(), $CONSTANT_POOL.getCharPositionInLine(), be);
					align = $cc.align;
					pos += $cc.pool.length();
					commands.add($cc.pool);
				}
			}

 		)
 		|
 		(
 			{pos += getAlign(align, pos);}

 			command [pos, constants, labels, align, be]
 			{
	 				if (enabled && $command.c != null) {
	 					$commands.add($command.c);
		 				pos += $command.c.length();
	 				}
	 			}

 		)
 		|
 		(
 			CONSTANT comment*
 			(
 				(
 					constBerechnungDirekt [pos, constants, be]
 					{
	 						if (enabled) {
		 						constants.put($CONSTANT.getText().substring(1), $constBerechnungDirekt.num);
	 						}
	 					}

 				)
 				|
 				(
 					DEL
 					{
	 						if (enabled) {
		 						constants.remove($CONSTANT.getText().substring(1));
	 						}
	 					}

 				)
 			)
 		)
 		|
 		(
 			(
 				CD_ALIGN
 				{
	 					if (enabled) {
							commands.add(new CompilerCommandCommand(CompilerCommand.align));
							align = true;
	 					}
					}

 			)
 			|
 			(
 				CD_NOT_ALIGN
 				{
	 					if (enabled) {
							commands.add(new CompilerCommandCommand(CompilerCommand.notAlign));
							align = false;
	 					}
					}

 			)
 		)
 		|
 		(
 			(
 				IF comment* constBerechnungDirekt [pos, constants, be]
 				{
		 				boolean top = $constBerechnungDirekt.num != 0;
		 				stack.add(top);
		 				if (enabled && !top) {
		 					disabledSince = stack.size();
		 					enabled = false;
		 				}
		 			}

 			)
 			|
 			(
 				ELSE_IF comment* constBerechnungDirekt [pos, constants, be]
 				{
		 				if (enabled) {
		 					disabledSince = stack.size();
		 					enabled = false;
		 				}
		 				if (stack.get(stack.size()-1) != null) {
		 					if (stack.get(stack.size()-1)) {
		 						stack.set(stack.size()-1, null);
		 					} else if ($constBerechnungDirekt.num != 0L) {
		 						stack.set(stack.size()-1, true);
		 					}
		 				}
		 			}

 			)
 			|
 			(
 				ELSE
 				{
		 				if (enabled) {
		 					disabledSince = stack.size();
		 					enabled = false;
		 				}
						{
			 				Boolean top = stack.get(stack.size()-1);
			 				if (top != null && !top) {
			 					stack.set(stack.size()-1,true);
			 				}
		 				}
	 	 			}

 			)
 			|
 			(
 				ENDIF
 				{
						if (disabledSince == stack.size()) {
							enabled = true;
							disabledSince = -1;
						}
						stack.remove(stack.size()-1);
		 			}

 			)
 			|
 			(
 				ERROR comment*
 				{
						StringBuilder msg = null;
						if (enabled) {
							msg = new StringBuilder("error at line: ").append($ERROR.getLine());
						}
					}

 				(
 					(
 						(
 							constBerechnungDirekt [pos, constants, be]
 							{msg.append(" error: ").append(_localctx.constBerechnungDirekt.getText()).append('=').append($constBerechnungDirekt.num);}

 						)
 						|
 						(
 							ERROR_MESSAGE_START comment*
 							{
									if (enabled) {
										msg.append('\n');
									}
								}

 							(
 								comment*
 								(
 									STR_STR
 									{
											if (enabled) {
												String str = $STR_STR.getText();
												str = str.substring(1, str.length() - 1);
												char[] chars = new char[str.length()];
												char[] strchars = str.toCharArray();
												int ci, si;
												for (ci = 0, si = 0; si < strchars.length; ci ++, si ++) {
													if (strchars[si] == '\\') {
														si ++;
														switch(strchars[si]){
														case 'r':
															chars[ci] = '\r';
															break;
														case 'n':
															chars[ci] = '\n';
															break;
														case 't':
															chars[ci] = '\t';
															break;
														case '0':
															chars[ci] = '\0';
															break;
														case '\\':
															chars[ci] = '\\';
															break;
														default:
															if (be) {
																throw new AssembleError($STR_STR.getLine(), $STR_STR.getCharPositionInLine(), "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
															} else {
																throw new AssembleRuntimeException($STR_STR.getLine(), $STR_STR.getCharPositionInLine(), "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
															}
														}
													} else {
														chars[ci] = strchars[si];
													}
												}
												msg.append(chars, 0, ci);
											}
										}

 								)
 								|
 								(
 									constBerechnungDirekt [pos, constants, be]
 									{
											if (enabled) {
												msg.append($constBerechnungDirekt.num);
											}
										}

 								)
 								|
 								(
 									ERROR_HEX comment* constBerechnungDirekt [pos, constants, be]
 									{
										if (enabled) {
											msg.append(Long.toHexString($constBerechnungDirekt.num));
										}
									}

 								)
 							)* comment* ERROR_MESSAGE_END
 						)
 					)?
 				)
 				{
					if (enabled) {
						if (be) {
							throw new AssembleError($ERROR.getLine(), $ERROR.getCharPositionInLine(), msg.toString());
						} else {
							throw new AssembleRuntimeException($ERROR.getLine(), $ERROR.getCharPositionInLine(), msg.toString());
						}
					}
				}

 			)
 		)
 		|
 		(
 			ANY
 			{
					if (enabled) {
						if (be) {
							throw new AssembleError($ANY.getLine(), $ANY.getCharPositionInLine(),"illegal character at line: " + $ANY.getLine() + ", pos-in-line: "+$ANY.getCharPositionInLine()+" char='" + $ANY.getText() + "'");
						} else {
							throw new AssembleRuntimeException($ANY.getLine(), $ANY.getCharPositionInLine(),"illegal character at line: " + $ANY.getLine() + ", pos-in-line: "+$ANY.getCharPositionInLine()+" char='" + $ANY.getText() + "'");
						}
					}
				}

 		)
 	)
 ;

 sr returns [int srnum]
 :
 	(
 		IP
 		{$srnum = ParamBuilder.SR_IP;}

 	)
 	|
 	(
 		SP
 		{$srnum = ParamBuilder.SR_SP;}

 	)
 	|
 	(
 		STATUS
 		{$srnum = ParamBuilder.SR_STATUS;}

 	)
 	|
 	(
 		INTCNT
 		{$srnum = ParamBuilder.SR_INTCNT;}

 	)
 	|
 	(
 		INTP
 		{$srnum = ParamBuilder.SR_INTP;}

 	)
 	|
 	(
 		XNN
 		{$srnum= ParamBuilder.SR_X_ADD + Integer.parseInt($XNN.getText().substring(1), 16);}

 	)
 ;

 param [long pos, Map<String,Long> constants, boolean be] returns [Param p]
 :
 	(
 		NAME
 		{
			if (constants.containsKey($NAME.getText())) {
				ParamBuilder builder = new ParamBuilder();
 				builder.art = ParamBuilder.A_NUM;
 				builder.v1 = constants.get($NAME.getText());
 				$p = builder.build();
			} else {
				$p = Param.createLabel($NAME.getText());
			}
		}

 	)
 	|
 	(
 		{ParamBuilder builder = new ParamBuilder();}

 		(
 			(
 				sr
 				{
	 				builder.art = ParamBuilder.A_SR;
	 				builder.v1 = $sr.srnum;
	 			}

 			)
 			|
 			(
 				nummerNoConstant [pos]
 				{
	 				builder.art = ParamBuilder.A_NUM;
	 				builder.v1 = $nummerNoConstant.num;
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

 		comment*
 		(
 			(
 				(
 					(
 						sr
 						{
							build.art |= ParamBuilder.A_SR;
							build.v1 = $sr.srnum;
						}

 					)
 					|
 					(
 						nummer [pos,constants,be]
 						{
							build.art |= ParamBuilder.A_NUM;
							build.v1 = $nummer.num;
						}

 					)
 				)
 				(
 					comment* PLUS comment*
 					{build.art &= ~ParamBuilder.B_REG;}

 					(
 						(
 							sr
 							{
								build.art |= ParamBuilder.B_SR;
								build.v2 = $sr.srnum;
							}

 						)
 						|
 						(
 							nummer [pos,constants,be]
 							{
								build.art |= ParamBuilder.B_NUM;
								build.v2 = $nummer.num;
							}

 						)
 					)
 				)?
 			)
 		) comment* ECK_KL_ZU
 		{$p = build.build();}

 	)
 ;

 constBerechnung [long pos, Map<String, Long> constants, boolean be] returns
 [long num]
 :
 	c1 = constBerechnungInclusivoder [pos, constants, be]
 	{$num = $c1.num;}

 	(
 		comment* FRAGEZEICHEN comment* c2 = constBerechnung [pos, constants, be]
 		comment* DOPPELPUNKT comment* c3 = constBerechnungInclusivoder
 		[pos, constants, be]
 		{$num = ($num != 0L) ? $c2.num : $c3.num;}

 	)?
 ;

 constBerechnungInclusivoder
 [long pos, Map<String, Long> constants, boolean be] returns [long num]
 :
 	c1 = constBerechnungExclusivoder [pos, constants, be]
 	{$num = $c1.num;}

 	(
 		comment* INCLUSIVODER comment* c2 = constBerechnungExclusivoder
 		[pos, constants, be]
 		{$num |= $c2.num;}

 	)*
 ;

 constBerechnungExclusivoder
 [long pos, Map<String, Long> constants, boolean be] returns [long num]
 :
 	c1 = constBerechnungUnd [pos, constants, be]
 	{$num = $c1.num;}

 	(
 		comment* EXCLUSIVPDER comment* c2 = constBerechnungUnd [pos, constants, be]
 		{$num ^= $c2.num;}

 	)*
 ;

 constBerechnungUnd [long pos, Map<String, Long> constants, boolean be] returns
 [long num]
 :
 	c1 = constBerechnungGleichheit [pos, constants, be]
 	{$num = $c1.num;}

 	(
 		comment* UND comment* c2 = constBerechnungGleichheit [pos, constants, be]
 		{$num &= $c2.num;}

 	)*
 ;

 constBerechnungGleichheit [long pos, Map<String, Long> constants, boolean be]
 returns [long num]
 :
 	c1 = constBerechnungRelativeTests [pos, constants, be]
 	{$num = $c1.num;}

 	(
 		{boolean gleich = false;}

 		comment*
 		(
 			(
 				GLEICH_GLEICH
 				{gleich = true;}

 			)
 			|
 			(
 				UNGLEICH
 				{gleich = false;}

 			)
 		) comment* c2 = constBerechnungRelativeTests [pos, constants, be]
 		{
 			if (gleich) {
 				$num = ($num == $c1.num) ? 1L : 0L;
 			} else {
 				$num = ($num == $c1.num) ? 0L : 1L;
 			}
 		}

 	)*
 ;

 constBerechnungRelativeTests
 [long pos, Map<String, Long> constants, boolean be] returns [long num]
 :
 	c1 = constBerechnungSchub [pos, constants, be]
 	{$num = $c1.num;}

 	(
 	{
		final int type_gr = 1, type_gr_gl = 2, type_kl_gl = 3, type_kl = 4;
		int type = -1;
	}

 		comment*
 		(
 			(
 				GROESSER
 				{type = type_gr;}

 			)
 			|
 			(
 				GROESSER_GLEICH
 				{type = type_gr_gl;}

 			)
 			|
 			(
 				KLEINER_GLEICH
 				{type = type_kl_gl;}

 			)
 			|
 			(
 				KLEINER
 				{type = type_kl;}

 			)
 		) comment* c2 = constBerechnungSchub [pos, constants, be]
 		{
			switch(type) {
			case type_gr:
				$num = ($num > $c1.num) ? 1L : 0L;
				break;
			case type_gr_gl:
				$num = ($num >= $c1.num) ? 1L : 0L;
				break;
			case type_kl_gl:
				$num = ($num <= $c1.num) ? 1L : 0L;
				break;
			case type_kl:
				$num = ($num < $c1.num) ? 1L : 0L;
				break;
			default:
				throw new InternalError("unknown type=" + type);
			}
		}

 	)*
 ;

 constBerechnungSchub [long pos, Map<String, Long> constants, boolean be]
 returns [long num]
 :
 	c1 = constBerechnungStrich [pos, constants, be]
 	{$num = $c1.num;}

 	(
 	{
		final int type_ls = 1, type_lrs = 2, type_ars = 3;
		int type = -1;
	}

 		comment*
 		(
 			(
 				LINKS_SCHUB
 				{type = type_ls;}

 			)
 			|
 			(
 				LOGISCHER_RECHTS_SCHUB
 				{type = type_lrs;}

 			)
 			|
 			(
 				ARITMETISCHER_RECHTS_SCHUB
 				{type = type_ars;}

 			)
 		) comment* c2 = constBerechnungStrich [pos, constants, be]
 		{
 			switch(type) {
			case type_ls:
				$num <<= $c2.num;
				break;
			case type_lrs:
				$num >>= $c2.num;
				break;
			case type_ars:
				$num >>>= $c2.num;
				break;
			default:
				throw new InternalError("unknown type=" + type);
 			}
 		}

 	)*
 ;

 constBerechnungStrich [long pos, Map<String, Long> constants, boolean be]
 returns [long num]
 :
 	c1 = constBerechnungPunkt [pos, constants, be]
 	{$num = $c1.num;}

 	(
 		{boolean add = false;}

 		comment*
 		(
 			(
 				PLUS
 				{add = true;}

 			)
 			|
 			(
 				MINUS
 				{add = false;}

 			)
 		) comment* c2 = constBerechnungPunkt [pos, constants, be]
 		{
 			if (add) {
 				$num += $c2.num;
 			} else {
 				$num -= $c2.num;
 			}
 		}

 	)*
 ;

 constBerechnungPunkt [long pos, Map<String, Long> constants, boolean be]
 returns [long num]
 :
 	c1 = constBerechnungDirekt [pos, constants, be]
 	{$num = $c1.num;}

 	(
 	{
		final int type_mal = 1, type_geteilt = 2, type_modulo = 3;
		int type = -1;
	}

 		comment*
 		(
 			(
 				MAL
 				{type = type_mal;}

 			)
 			|
 			(
 				GETEILT
 				{type = type_geteilt;}

 			)
 			|
 			(
 				MODULO
 				{type = type_modulo;}

 			)
 		) comment* c2 = constBerechnungDirekt [pos, constants, be]
 		{
 			switch(type) {
			case type_mal:
				$num *= $c2.num; 
				break;
			case type_geteilt:
				$num /= $c2.num; 
				break;
			case type_modulo:
				$num %= $c2.num; 
				break;
			default:
				throw new InternalError("unknown type=" + type);
 			}
 		}

 	)*
 ;

 constBerechnungDirekt [long pos, Map<String, Long> constants, boolean be]
 returns [long num]
 :
 	(
 		nummer [pos, constants, be]
 		{$num = $nummer.num;}

 	)
 	|
 	(
 		EXIST_CONSTANT
 		{$num = constants.containsKey($EXIST_CONSTANT.getText().substring(2)) ? 1L : 0L;}

 	)
 	|
 	(
 		RND_KL_AUF comment* constBerechnung [pos, constants, be] comment* RND_KL_ZU
 		{$num = $constBerechnung.num;}

 	)
 ;

 nummer [long pos, Map<String, Long> constants, boolean be] returns [long num]
 :
 	(
 		nummerNoConstant [pos]
 		{$num = $nummerNoConstant.num;}

 	)
 	|
 	(
 		NAME
 		{
 			Long zw = constants.get($NAME.getText());
 			if (zw == null) {
 				if (be) {
	 				throw new AssembleError($NAME.getLine(), $NAME.getCharPositionInLine(), "unknown constant: '" + $NAME.getText() + "', known constants: '" + constants + "'");
 				} else {
	 				throw new AssembleRuntimeException($NAME.getLine(), $NAME.getCharPositionInLine(), "unknown constant: '" + $NAME.getText() + "', known constants: '" + constants + "'");
 				}
 			}
 			$num = (long) zw;
 		}

 	)
 ;

 nummerNoConstant [long pos] returns [long num]
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
 ;

 command
 [long pos, Map<String,Long> constants, Map<String,Long> labels, boolean align, boolean be]
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
 					LEA
 					{cmd = Commands.CMD_LEA;}

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
 			) comment* p1 = param [pos, constants, be] comment* COMMA comment* p2 =
 			param [pos, constants, be]
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
 					INT
 					{cmd = Commands.CMD_INT;}

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
 			) comment* p1 = param [pos, constants, be]
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

 comment
 :
 	LINE_COMMENT
 	| BLOCK_COMMENT
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
