/**
 * regex for grammar only:
 * '\s*returns' -> '' 
 * '\s*\[([^\[\]"]|("([^"\r\n]|\\")*"))*\]' -> '' 
 * '\s*{[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*")|({[^({}]|("([^"\r\n]|\\")*"))*}))*}))*}' -> '' 
 */
 grammar PrimitiveFileGrammar;

 @parser::header {
import java.util.function.*;
import java.util.*;
import java.io.*;
import de.hechler.patrick.codesprachen.primitive.assemble.enums.*;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.*;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.Param.*;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
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
	private AssembleRuntimeException appendString(StringBuilder msg, Token tok, boolean be, AssembleRuntimeException are) {
		String str = tok.getText();
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
						throw new AssembleError(tok.getLine(), tok.getCharPositionInLine() + si - 1, 2, tok.getStartIndex(), "illegal escaped character: char='" + strchars[si] + "'");
					} else if (are != null) {
						are.addSuppressed(new AssembleRuntimeException(tok.getLine(), tok.getCharPositionInLine() + si - 1, 2, tok.getStartIndex(), "illegal escaped character: char='" + strchars[si] + "'"));
					} else {
						are = new AssembleRuntimeException(tok.getLine(), tok.getCharPositionInLine() + si - 1, 2, tok.getStartIndex(), "illegal escaped character: char='" + strchars[si] + "'");
					}
				}
			} else {
				chars[ci] = strchars[si];
			}
		}
		msg.append(chars, 0, ci);
		return are;
	}
 }

 parse
 [long startpos, boolean align, Map<String,Long> constants, boolean bailError, ANTLRErrorStrategy errorHandler, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> ecp, PrimitiveAssembler asm, ANTLRInputStream antlrin]
 returns
 [List<Command> commands, Map<String,Long> labels, long pos, AssembleRuntimeException are, boolean enabled, Map<String, Long> exports]
 @init {
 	$pos = startpos;
 	$labels = new HashMap<>();
 	$commands = new ArrayList<>();
 	$commands.add(new CompilerCommandCommand(align ? CompilerCommand.align : CompilerCommand.notAlign));
 	$enabled = true;
 	$exports = new HashMap<>();
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
 		[$enabled, disabledSince, stack, align, constants, $commands, $labels, $pos, bailError, errorHandler, errorListener, ecp, $exports, asm, antlrin]
 		{
 			$enabled = $anything.enabled;
 			disabledSince = $anything.disabledSince;
			stack = $anything.stack;
			align = $anything.align;
			constants = $anything.constants;
			$commands = $anything.commands;
			$labels = $anything.labels;
			$pos = $anything.pos;
			$exports = $anything.exports;
			if ($anything.are != null) {
				if ($are != null) {
					$are.addSuppressed($anything.are);
				} else {
					$are = $anything.are;
				}
			}
 		}

 	)* EOF
 ;

 anything
 [boolean enabled_, int disabledSince_, List<Boolean> stack_, boolean align_, Map<String, Long> constants_, List<Command> commands_, Map<String, Long> labels_, long pos_, boolean be, ANTLRErrorStrategy errorHandler, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> ecp, Map<String, Long> exports_, PrimitiveAssembler asm, ANTLRInputStream antlrin]
 returns
 [boolean enabled, int disabledSince, List<Boolean> stack, boolean align, Map<String, Long> constants, List<Command> commands, Map<String, Long> labels, long pos, Object zusatz, AssembleRuntimeException are, Map<String, Long> exports]
 @init {
	$enabled = enabled_;
	$disabledSince = disabledSince_;
	$stack = stack_;
	$align = align_;
	$constants = new HashMap<>(constants_);
	$commands = commands_;
	$labels = labels_;
	$pos = pos_;
	$zusatz = null;
	$exports = new HashMap<>(exports_);
 }
 :
 	(
 		(
 			comment+
 		)
 		|
 		(
 			{$pos += getAlign($align, $pos);}

 			CONSTANT_POOL
 			{
				if ($enabled) {
					ecp.accept($CONSTANT_POOL.getLine(), $CONSTANT_POOL.getStartIndex());
					ConstsContext cc = Command.parseCP($CONSTANT_POOL.getText(), $constants, $labels, $pos, $align, $CONSTANT_POOL.getLine(), $CONSTANT_POOL.getCharPositionInLine(), $CONSTANT_POOL.getStartIndex(), be, errorHandler, errorListener);
					$align = cc.align;
					$pos += cc.pool.length();
					$commands.add(cc.pool);
					$zusatz = cc;
					$are = Command.getConvertedCP_ARE(cc.are, $CONSTANT_POOL.getLine(), $CONSTANT_POOL.getCharPositionInLine(), $CONSTANT_POOL.getStartIndex());
				}
			}

 		)
 		|
 		(
 			{$pos += getAlign($align, $pos);}

 			command [$pos, $constants, $labels, $align, be & $enabled]
 			{
 				$zusatz = $command.c;
 				if ($enabled && $command.c != null) {
 					$commands.add($command.c);
 					try {
		 				$pos += $command.c.length();
 					} catch (Exception npe) {
 						if (be) {
 							throw new AssembleError(_localctx.command.getStart().getLine(), _localctx.command.getStart().getCharPositionInLine(), 1, _localctx.command.getStart().getStartIndex(), npe.getMessage(), npe);
 						} else {
 							$are = new AssembleRuntimeException(_localctx.command.getStart().getLine(), _localctx.command.getStart().getCharPositionInLine(), 1, _localctx.command.getStart().getStartIndex(), npe.getMessage(), npe);
 						}
 					}
 				}
 			}

 		)
 		|
 		(
 		{
				String constName = null;
				boolean export = false;
			}

 			(
 				(
 					CONSTANT
 					{
						constName = $CONSTANT.getText().substring(1);
						export = false;
					}

 				)
 				|
 				(
 					EXPORT_CONSTANT
 					{
						constName = $EXPORT_CONSTANT.getText().substring(5);
						export = true;
					}

 				)
 			) comment*
 			(
 				(
 					constBerechnungDirekt [$pos, $constants, be & $enabled]
 					{
 						if ($enabled) {
		 					$are = $constBerechnungDirekt.are;
	 						$constants.put(constName, $constBerechnungDirekt.num);
	 						if (export) {
		 						$exports.put(constName, $constBerechnungDirekt.num);
	 						}
 						}
 					}

 				)
 				|
 				(
 					DEL
 					{
 						if ($enabled) {
	 						$constants.remove(constName);
	 						if (export) {
		 						$exports.remove(constName);
	 						}
 						}
 					}

 				)
 			)
 		)
 		|
 		(
 			READ_SYM STR_STR
 			{
				String readFile = null;
				Boolean isSource = null;
				String prefix = null;
				boolean useMyConsts = false;
				Map<String,Long> addConsts = new HashMap<>();
				if ($enabled) {
					StringBuilder file = new StringBuilder();
					$are = appendString(file, $STR_STR, be, $are);
					readFile = file.toString();
				}
			}

 			(
 				(
 					CONSTANT
 					{
 						if (prefix == null) {
 							prefix = $CONSTANT.getText().substring(1);
 						} else if ($enabled) {
							if (be) {
								throw new AssembleError($CONSTANT.getLine(), $CONSTANT.getCharPositionInLine(), $CONSTANT.getStopIndex() - $CONSTANT.getStartIndex() + 1, $CONSTANT.getStartIndex(), "prefix already set prefix=" + prefix + " new=" + $CONSTANT.getText().substring(1));
							} else if ($are != null) {
								$are.addSuppressed(new AssembleRuntimeException($CONSTANT.getLine(), $CONSTANT.getCharPositionInLine(), $CONSTANT.getStopIndex() - $CONSTANT.getStartIndex() + 1, $CONSTANT.getStartIndex(), "prefix already set prefix=" + prefix + " new=" + $CONSTANT.getText().substring(1)));
							} else {
								$are = new AssembleRuntimeException($CONSTANT.getLine(), $CONSTANT.getCharPositionInLine(), $CONSTANT.getStopIndex() - $CONSTANT.getStartIndex() + 1, $CONSTANT.getStartIndex(), "prefix already set prefix=" + prefix + " new=" + $CONSTANT.getText().substring(1));
							}
 						}
 					}

 				)
 				|
 				(
 					MY_CONSTS
 					{
 						if (!useMyConsts) {
 							useMyConsts = true;
 						} else if ($enabled) {
							if (be) {
								throw new AssembleError($CONSTANT.getLine(), $CONSTANT.getCharPositionInLine(), $CONSTANT.getStopIndex() - $CONSTANT.getStartIndex() + 1, $CONSTANT.getStartIndex(), "myconsts already set");
							} else if ($are != null) {
								$are.addSuppressed(new AssembleRuntimeException($CONSTANT.getLine(), $CONSTANT.getCharPositionInLine(), $CONSTANT.getStopIndex() - $CONSTANT.getStartIndex() + 1, $CONSTANT.getStartIndex(), "myconsts already set"));
							} else {
								$are = new AssembleRuntimeException($CONSTANT.getLine(), $CONSTANT.getCharPositionInLine(), $CONSTANT.getStopIndex() - $CONSTANT.getStartIndex() + 1, $CONSTANT.getStartIndex(), "myconsts already set");
							}
 						}
 					}

 				)
 				|
 				(
 					ADD_CONSTANT constBerechnung [$pos, $constants, $be]
 					{
 						addConsts.put($ADD_CONSTANT.getText().substring(5), $constBerechnung.num);
 						if ($constBerechnung.are != null)  {
 							if ($are != null) {
 								$are.addSuppressed($constBerechnung.are);
 							} else {
 								$are = $constBerechnung.are;
 							}
 						}
 					}

 				)
 				|
 				(
 					{boolean iss = false;}

 					(
 						(
 							sourceOrSymbol = SOURCE
 							{iss = true;}
 						)
 						|
 						(
 							sourceOrSymbol = SYMBOL
 							{iss = false;}
 						)
 					)
 					{
 						if (isSource == null) {
 							isSource = iss;
 						} else if ($enabled) {
							if (be) {
								throw new AssembleError($sourceOrSymbol.getLine(), $sourceOrSymbol.getCharPositionInLine(), $sourceOrSymbol.getStopIndex() - $sourceOrSymbol.getStartIndex() + 1, $sourceOrSymbol.getStartIndex(), "Source/Symbol already set old=" + (isSource ? "Source" : "Symbol") + " new="+ (iss ? "Source" : "Symbol"));
							} else if ($are != null) {
								$are.addSuppressed(new AssembleRuntimeException($sourceOrSymbol.getLine(), $sourceOrSymbol.getCharPositionInLine(), $sourceOrSymbol.getStopIndex() - $sourceOrSymbol.getStartIndex() + 1, $sourceOrSymbol.getStartIndex(), "Source/Symbol already set old=" + (isSource ? "Source" : "Symbol") + " new="+ (iss ? "Source" : "Symbol")));
							} else {
								$are = new AssembleRuntimeException($sourceOrSymbol.getLine(), $sourceOrSymbol.getCharPositionInLine(), $sourceOrSymbol.getStopIndex() - $sourceOrSymbol.getStartIndex() + 1, $sourceOrSymbol.getStartIndex(), "Source/Symbol already set old=" + (isSource ? "Source" : "Symbol") + " new="+ (iss ? "Source" : "Symbol"));
							}
 						}
 					}
 				)
 			)* GROESSER
 			{
 				if ($enabled) {
	 				addConsts.putAll(useMyConsts ? $constants : PrimitiveAssembler.START_CONSTANTS);
	 				try {
		 				AssembleRuntimeException sare = asm.readSymbols(readFile, isSource, prefix, addConsts, $constants, antlrin, be && $enabled, $READ_SYM);
		 				if (sare != null) {
							if ($are != null) {
								$are.addSuppressed(sare);
			 				} else {
								$are = sare;
			 				}
		 				}
	 				} catch(Exception iae) {
						if (be) {
							throw new AssembleError($READ_SYM.getLine(), $READ_SYM.getCharPositionInLine(), $GROESSER.getStopIndex() - $READ_SYM.getStartIndex() + 1, $READ_SYM.getStartIndex(), iae.getMessage(), iae);
						} else if ($are != null) {
							$are.addSuppressed(new AssembleRuntimeException($READ_SYM.getLine(), $READ_SYM.getCharPositionInLine(), $GROESSER.getStopIndex() - $READ_SYM.getStartIndex() + 1, $READ_SYM.getStartIndex(), iae.getMessage(), iae));
						} else {
							$are = new AssembleRuntimeException($READ_SYM.getLine(), $READ_SYM.getCharPositionInLine(), $GROESSER.getStopIndex() - $READ_SYM.getStartIndex() + 1, $READ_SYM.getStartIndex(), iae.getMessage(), iae);
						}
	 				}
 				}
 			}
 		)
 		|
 		(
 			CD_ALIGN
 			{
				if ($enabled) {
					$commands.add(new CompilerCommandCommand(CompilerCommand.align));
					$align = true;
				}
			}

 		)
 		|
 		(
 			CD_NOT_ALIGN
 			{
				if ($enabled) {
					$commands.add(new CompilerCommandCommand(CompilerCommand.notAlign));
					$align = false;
				}
			}

 		)
 		|
 		(
 			IF comment* constBerechnungDirekt [$pos, $constants, be & $enabled]
 			{
 				boolean top = $constBerechnungDirekt.num != 0;
 				$stack.add(top);
 				if ($enabled) {
	 				$are = $constBerechnungDirekt.are;
	 				if (!top) {
	 					$disabledSince = $stack.size();
	 					$enabled = false;
	 				}
 				}
 			}

 		)
 		|
 		(
 			ELSE_IF comment* constBerechnungDirekt [$pos, $constants, be & $enabled]
 			{
 				if ($stack.isEmpty()) {
 					assert $enabled;
					if (be) {
						throw new AssembleError($ENDIF.getLine(), $ENDIF.getCharPositionInLine(), $ENDIF.getStopIndex() - $ENDIF.getStartIndex(), $ENDIF.getStartIndex(), "endif without previos if");
					} else {
						$are = new AssembleRuntimeException($ENDIF.getLine(), $ENDIF.getCharPositionInLine(), $ENDIF.getStopIndex() - $ENDIF.getStartIndex(), $ENDIF.getStartIndex(), "endif without previos if");
					}
 				} else {
	 				if ($enabled) {
	 					$disabledSince = $stack.size();
	 					$enabled = false;
	 				}
	 				if ($stack.get($stack.size()-1) != null) {
	 					if ($stack.get($stack.size()-1)) {
	 						$stack.set($stack.size()-1, null);
	 					} else if ($constBerechnungDirekt.num != 0L) {
	 						$stack.set($stack.size()-1, true);
	 					}
	 				}
	 				if ($enabled || $disabledSince == $stack.size()) {
		 				$are = $constBerechnungDirekt.are;
	 				}
 				}
 			}

 		)
 		|
 		(
 			ELSE
 			{
 				if ($stack.isEmpty()) {
 					assert $enabled;
					if (be) {
						throw new AssembleError($ENDIF.getLine(), $ENDIF.getCharPositionInLine(), $ENDIF.getStopIndex() - $ENDIF.getStartIndex(), $ENDIF.getStartIndex(), "else without previos if");
					} else {
						$are = new AssembleRuntimeException($ENDIF.getLine(), $ENDIF.getCharPositionInLine(), $ENDIF.getStopIndex() - $ENDIF.getStartIndex(), $ENDIF.getStartIndex(), "else without previos if");
					}
 				} else {
	 				if ($enabled) {
	 					$disabledSince = $stack.size();
	 					$enabled = false;
	 				}
	 				Boolean top = $stack.get($stack.size()-1);
	 				if (top != null && !top) {
	 					$stack.set($stack.size()-1,true);
	 					if ($disabledSince == $stack.size()) {
		 					$enabled = true;
	 					}
	 				}
 				}
 			}

 		)
 		|
 		(
 			ENDIF
 			{
 				if ($stack.isEmpty()) {
 					assert $enabled;
					if (be) {
						throw new AssembleError($ENDIF.getLine(), $ENDIF.getCharPositionInLine(), $ENDIF.getStopIndex() - $ENDIF.getStartIndex(), $ENDIF.getStartIndex(), "endif without previos if");
					} else {
						$are = new AssembleRuntimeException($ENDIF.getLine(), $ENDIF.getCharPositionInLine(), $ENDIF.getStopIndex() - $ENDIF.getStartIndex(), $ENDIF.getStartIndex(), "endif without previos if");
					}
 				} else {
					if ($disabledSince == $stack.size()) {
						$enabled = true;
						$disabledSince = -1;
					}
					$stack.remove($stack.size()-1);
 				}
 			}

 		)
 		|
 		(
 			ERROR comment*
 			{StringBuilder msg = new StringBuilder();}

 			(
 				(
 					(
 						constBerechnungDirekt [$pos, $constants, be & $enabled]
 						{
							msg.append(" error: ").append(_localctx.constBerechnungDirekt.getText()).append('=').append($constBerechnungDirekt.num);
							$are = $constBerechnungDirekt.are;
						}

 					)
 					|
 					(
 						ERROR_MESSAGE_START comment*
 						(
 							comment*
 							(
 								STR_STR
 								{
									if ($enabled) {
										$are = appendString(msg, $STR_STR, be, $are);
									}
								}

 							)
 							|
 							(
 								constBerechnungDirekt [$pos, $constants, be & $enabled]
 								{
									msg.append($constBerechnungDirekt.num);
									if ($constBerechnungDirekt.are != null) {
										if ($are != null) {
											$are.addSuppressed($constBerechnungDirekt.are);
										} else {
											$are = $constBerechnungDirekt.are;
										}
									}
								}

 							)
 							|
 							(
 								ERROR_HEX comment* constBerechnungDirekt
 								[$pos, $constants, be & $enabled]
 								{
									if ($constBerechnungDirekt.are != null) {
										if ($are != null) {
											$are.addSuppressed($constBerechnungDirekt.are);
										} else {
											$are = $constBerechnungDirekt.are;
										}
									}
									msg.append(Long.toHexString($constBerechnungDirekt.num));
								}

 							)
 						)* comment* ERROR_MESSAGE_END
 					)
 				)?
 			)
 			{
				$zusatz = msg.toString();
				if ($enabled) {
					if (be) {
						throw new AssembleError($ERROR.getLine(), $ERROR.getCharPositionInLine(), $ERROR.getStopIndex() - $ERROR.getStartIndex() + 1, $ERROR.getStartIndex(), (String) $zusatz);
					} else if ($are != null) {
						$are.addSuppressed(new AssembleRuntimeException($ERROR.getLine(), $ERROR.getCharPositionInLine(), $ERROR.getStopIndex() - $ERROR.getStartIndex() + 1, $ERROR.getStartIndex(), (String) $zusatz));
					} else {
						$are = new AssembleRuntimeException($ERROR.getLine(), $ERROR.getCharPositionInLine(), $ERROR.getStopIndex() - $ERROR.getStartIndex() + 1, $ERROR.getStartIndex(), (String) $zusatz);
					}
				}
			}

 		)
 		|
 		(
 			ANY
 			{
				if ($enabled) {
					if (be) {
						throw new AssembleError($ANY.getLine(), $ANY.getCharPositionInLine(), 1, $ANY.getStartIndex(), "illegal character: char='" + $ANY.getText() + "'");
					} else if ($are != null) {
						$are.addSuppressed(new AssembleRuntimeException($ANY.getLine(), $ANY.getCharPositionInLine(), 1, $ANY.getStartIndex(), "illegal character: char='" + $ANY.getText() + "'"));
					} else {
						$are = new AssembleRuntimeException($ANY.getLine(), $ANY.getCharPositionInLine(), 1, $ANY.getStartIndex(), "illegal character: char='" + $ANY.getText() + "'");
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

 param [long pos, Map<String,Long> constants, boolean be] returns
 [Param p, AssembleRuntimeException are]
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
							$are = $nummer.are;
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
								if ($nummer.are != null) {
									if ($are != null) {
										$are.addSuppressed($nummer.are);
									} else {
										$are = $nummer.are;
									}
								}
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
 [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungInclusivoder [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

 	(
 		comment* FRAGEZEICHEN comment* c2 = constBerechnung [pos, constants, be]
 		comment* DOPPELPUNKT comment* c3 = constBerechnungInclusivoder
 		[pos, constants, be]
 		{
 			$num = ($num != 0L) ? $c2.num : $c3.num;
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)?
 ;

 constBerechnungInclusivoder
 [long pos, Map<String, Long> constants, boolean be] returns
 [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungExclusivoder [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

 	(
 		comment* INCLUSIVODER comment* c2 = constBerechnungExclusivoder
 		[pos, constants, be]
 		{
 			$num |= $c2.num;
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)*
 ;

 constBerechnungExclusivoder
 [long pos, Map<String, Long> constants, boolean be] returns
 [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungUnd [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

 	(
 		comment* EXCLUSIVPDER comment* c2 = constBerechnungUnd [pos, constants, be]
 		{
 			$num ^= $c2.num;
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)*
 ;

 constBerechnungUnd [long pos, Map<String, Long> constants, boolean be]
 returns [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungGleichheit [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

 	(
 		comment* UND comment* c2 = constBerechnungGleichheit [pos, constants, be]
 		{
 			$num &= $c2.num;
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)*
 ;

 constBerechnungGleichheit [long pos, Map<String, Long> constants, boolean be]
 returns [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungRelativeTests [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

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
 				$num = ($num == $c2.num) ? 1L : 0L;
 			} else {
 				$num = ($num == $c2.num) ? 0L : 1L;
 			}
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)*
 ;

 constBerechnungRelativeTests
 [long pos, Map<String, Long> constants, boolean be] returns
 [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungSchub [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

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
				$num = ($num > $c2.num) ? 1L : 0L;
				break;
			case type_gr_gl:
				$num = ($num >= $c2.num) ? 1L : 0L;
				break;
			case type_kl_gl:
				$num = ($num <= $c2.num) ? 1L : 0L;
				break;
			case type_kl:
				$num = ($num < $c2.num) ? 1L : 0L;
				break;
			default:
				throw new InternalError("unknown type=" + type);
			}
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
		}

 	)*
 ;

 constBerechnungSchub [long pos, Map<String, Long> constants, boolean be]
 returns [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungStrich [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

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
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)*
 ;

 constBerechnungStrich [long pos, Map<String, Long> constants, boolean be]
 returns [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungPunkt [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

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
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)*
 ;

 constBerechnungPunkt [long pos, Map<String, Long> constants, boolean be]
 returns [long num, AssembleRuntimeException are]
 :
 	c1 = constBerechnungDirekt [pos, constants, be]
 	{
 		$num = $c1.num;
 		$are = $c1.are;
 	}

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
 			if ($c2.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($c2.are);
 				} else {
 					$are = $c2.are;
 				}
 			}
 		}

 	)*
 ;

 constBerechnungDirekt [long pos, Map<String, Long> constants, boolean be]
 returns [long num, AssembleRuntimeException are]
 :
 	(
 		nummer [pos, constants, be]
 		{
 			$num = $nummer.num;
 			$are = $nummer.are;
 		}

 	)
 	|
 	(
 		EXIST_CONSTANT
 		{$num = constants.containsKey($EXIST_CONSTANT.getText().substring(2)) ? 1L : 0L;}

 	)
 	|
 	(
 		RND_KL_AUF comment* constBerechnung [pos, constants, be] comment* RND_KL_ZU
 		{
 			$num = $constBerechnung.num;
 			$are = $constBerechnung.are;
 		}

 	)
 ;

 nummer [long pos, Map<String, Long> constants, boolean be] returns
 [long num, AssembleRuntimeException are]
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
	 				throw new AssembleError($NAME.getLine(), $NAME.getCharPositionInLine(), $NAME.getStopIndex() - $NAME.getStartIndex() + 1, $NAME.getStartIndex(), "unknown constant: '" + $NAME.getText() + "', known constants: '" + constants + "'");
 				} else {
	 				zw = 0L;
	 				$are = new AssembleRuntimeException($NAME.getLine(), $NAME.getCharPositionInLine(), $NAME.getStopIndex() - $NAME.getStartIndex() + 1, $NAME.getStartIndex(), "unknown constant: '" + $NAME.getText() + "', known constants: '" + constants + "'");
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
 				MVAD
 				{cmd = Commands.CMD_MVAD;}

 			) comment* p1 = param [pos, constants, be] comment* COMMA comment* p2 =
 			param [pos, constants, be] comment* p3 = param [pos, constants, be]
 			{$c = new Command(cmd, $p1.p, $p2.p, $p3.p);}

 		)
 		|
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
 				|
 				(
 					CALO
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
 					ISNAN
 					{cmd = Commands.CMD_ISNAN;}

 				)
 				|
 				(
 					ISINF
 					{cmd = Commands.CMD_ISINF;}

 				)
 				|
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

 CALO
 :
 	'CALO'
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

 MVAD
 :
 	'MVAD'
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

 ISNAN
 :
 	'ISNAN'
 ;

 ISINF
 :
 	'ISINF'
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
 	'-'? [0-9]+ '.' [0-9]*
 	| [0-9]* '.' [0-9]+
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

 MY_CONSTS
 :
 	'--MY_CONSTS--'
 ;

 SOURCE
 :
 	'--SOURCE--'
 ;

 SYMBOL
 :
 	'--SYMBOL--'
 ;

 ADD_CONSTANT
 :
 	'#ADD~' NAME
 ;

 READ_SYM
 :
 	'~READ_SYM'
 ;

 EXPORT_CONSTANT
 :
 	'#EXP~' NAME
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
 			~( '>' )
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
 			~( '"' | '\\' )
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
