/**
 * Define a grammar called Hello
 */
 grammar ConstantPoolGrammar;

 @parser::header {
import java.util.*;
import java.math.*;
import java.nio.charset.*;
import de.hechler.patrick.codesprachen.primitive.assemble.enums.*;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.*;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
}

 @parser::members {
	
	private void makeAlign(boolean align, long pos, ConstantPoolCommand cpc) {
		if (align && cpc.length() > 0) {//on the start the compiler will align (if active)
			int mod = (int) ((pos + cpc.length()) % 8);
			if (mod != 0) {
				byte[] add = new byte[8 - mod];
				cpc.addBytes(add);
			}
		}
	}
	
}

 consts
 [Map<String,PrimitiveConstant> constants, Map<String, Long> labels, long pos, boolean alignParam, boolean bailError]
 returns
 [ConstantPoolCommand pool, boolean align, AssembleRuntimeException are] @init {
 	$pool = new ConstantPoolCommand();
 	$align = alignParam;
 }
 :
 	START
 	(
 		cpanything [pos, $pool, $align, constants, labels, bailError]
 		{
 			pos = $cpanything.pos;
 			$pool = $cpanything.pool;
 			$align = $cpanything.align;
 			constants = $cpanything.constants;
 			labels = $cpanything.labels;
 			if ($cpanything.are != null) {
 				if ($are != null) {
 					$are.addSuppressed($cpanything.are);
 				} else {
 					$are = $cpanything.are;
 				}
 			}
 		}

 	)* ENDE EOF
 ;

 cpanything
 [long pos_, ConstantPoolCommand pool_, boolean align_, Map<String, PrimitiveConstant> constants_, Map<String, Long> labels_, boolean be]
 returns
 [long pos, ConstantPoolCommand pool, boolean align, Map<String, PrimitiveConstant> constants, Map<String, Long> labels, AssembleRuntimeException are]
 @init {
 	$pos = pos_;
 	$pool = pool_;
 	$align = align_;
 	$constants = constants_;
 	$labels = labels_;
 }
 :
 	(
 		(
 			comment+
 		)
 		|
 		(
 			{makeAlign($align, $pos, $pool);}

 			string [$pool, be]
 			{$are = $string.are;}

 		)
 		|
 		(
 			{makeAlign($align, $pos, $pool);}

 			numconst [$pool, $constants, be]
 			{$are = $numconst.are;}

 		)
 		|
 		(
 			CD_ALIGN
 			{$align = true;}

 		)
 		|
 		(
 			CD_NOT_ALIGN
 			{$align = false;}

 		)
 		|
 		(
 			ERROR comment*
 			{StringBuilder msg = new StringBuilder("error at line: ").append($ERROR.getLine());}

 			(
 				(
 					(
 						numconst [null, $constants, be] comment*
 						{
 							msg.append(" error: ").append(_localctx.numconst.getText()).append('=').append($numconst.num);
 							$are = $numconst.are;
 						}

 					)
 					|
 					(
 						ERROR_MESSAGE_START comment*
 						{msg.append('\n');}

 						(
 							(
 								STR_STR comment*
 								{
									String str = $STR_STR.getText();
									str = str.substring(1, str.length() - 1);
									char[] chars = new char[str.length()];
									char[] strchars = str.toCharArray();
									int ci, si;
									for (ci = 0, si = 0; si < strchars.length; ci ++, si ++) {
										if (strchars[si] == '\\') {
											si ++;
											switch(strchars[si]) {
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
													throw new AssembleError($STR_STR.getLine(), $STR_STR.getCharPositionInLine() + si - 1, 2, $STR_STR.getStartIndex() + si, "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
												} else if ($are != null) {
													$are.addSuppressed(new AssembleRuntimeException($STR_STR.getLine(), $STR_STR.getCharPositionInLine() + si - 1, 2, $STR_STR.getStartIndex() + si, "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'"));
												} else {
													$are = new AssembleRuntimeException($STR_STR.getLine(), $STR_STR.getCharPositionInLine() + si - 1, 2, $STR_STR.getStartIndex() + si, "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
												}
											}
										} else {
											chars[ci] = strchars[si];
										}
									}
									msg.append(chars, 0, ci);
								}

 							)
 							|
 							(
 								numconst [null, $constants, be] comment*
 								{
 									msg.append($numconst.num);
 									$are = $numconst.are;
 								}

 							)
 							|
 							(
 								ERROR_HEX comment* numconst [null, $constants, be] comment*
 								{
 									msg.append(Long.toHexString($numconst.num));
									if ($are != null) {
										$are.addSuppressed($numconst.are);
									} else {
										$are = $numconst.are;
									}
 								}

 							)
 						)* ERROR_MESSAGE_END
 					)
 				)?
 			)
 			{
				if (be) {
					throw new AssembleError($ERROR.getLine(), $ERROR.getCharPositionInLine(), $ERROR.getStopIndex() - $ERROR.getStartIndex() + 1, $ERROR.getStartIndex(), msg.toString());
				} else if ($are != null) {
					$are.addSuppressed(new AssembleRuntimeException($ERROR.getLine(), $ERROR.getCharPositionInLine(), $ERROR.getStopIndex() - $ERROR.getStartIndex() + 1, $ERROR.getStartIndex(), msg.toString()));
				} else {
					$are = new AssembleRuntimeException($ERROR.getLine(), $ERROR.getCharPositionInLine(), $ERROR.getStopIndex() - $ERROR.getStartIndex() + 1, $ERROR.getStartIndex(), msg.toString());
				}
			}

 		)
 	)
 ;

 string [ConstantPoolCommand pool, boolean be] returns
 [AssembleRuntimeException are] @init {
 	StringBuilder build = new StringBuilder();
 	Charset cs = Charset.defaultCharset();
 }
 :
 	(
 		(
 			CHARS comment* CHAR_STR comment*
 			{
	 			String name = $CHAR_STR.getText();
				name = name.substring(1, name.length() - 1);
				char[] chars = new char[name.length()];
				char[] strchars = name.toCharArray();
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
						case '\'':
							chars[ci] = '\'';
							break;
						default:
							if (be) {
								throw new AssembleError($CHAR_STR.getLine(), $CHAR_STR.getCharPositionInLine() + si - 1, 2, $CHAR_STR.getStartIndex() + si - 1, "illegal escaped character: '" + strchars[si] + "' complete orig string='" + name + "'");
							} else if ($are != null) {
								$are.addSuppressed(new AssembleRuntimeException($CHAR_STR.getLine(), $CHAR_STR.getCharPositionInLine() + si - 1, 2, $CHAR_STR.getStartIndex() + si - 1, "illegal escaped character: '" + strchars[si] + "' complete orig string='" + name + "'"));
							} else {
								$are = new AssembleRuntimeException($CHAR_STR.getLine(), $CHAR_STR.getCharPositionInLine() + si - 1, 2, $CHAR_STR.getStartIndex() + si - 1, "illegal escaped character: '" + strchars[si] + "' complete orig string='" + name + "'");
							}
						}
					} else {
						chars[ci] = strchars[si];
					}
				}
				try {
		 			cs = Charset.forName(new String(chars, 0 ,ci));
				} catch (IllegalCharsetNameException | UnsupportedCharsetException icne) {
					if (be) {
						throw new AssembleError($CHAR_STR.getLine(), $CHAR_STR.getCharPositionInLine(), $CHAR_STR.getStopIndex() - $CHAR_STR.getStartIndex(), $CHAR_STR.getStartIndex(), icne.getClass().getSimpleName() + ": " + icne.getMessage(), icne);
					} else if ($are != null) {
						$are.addSuppressed(new AssembleRuntimeException($CHAR_STR.getLine(), $CHAR_STR.getCharPositionInLine(), $CHAR_STR.getStopIndex() - $CHAR_STR.getStartIndex(), $CHAR_STR.getStartIndex(), icne.getClass().getSimpleName() + ": " + icne.getMessage(), icne));
					} else {
						$are = new AssembleRuntimeException($CHAR_STR.getLine(), $CHAR_STR.getCharPositionInLine(), $CHAR_STR.getStopIndex() - $CHAR_STR.getStartIndex(), $CHAR_STR.getStartIndex(), icne.getClass().getSimpleName() + ": " + icne.getMessage(), icne);
					}
					//cs is by default set to the system default charset
				}
	 		}

 		)?
 	)
 	(
 		(
 			string_append [build, be]
 			{
 				if ($string_append.are != null) {
 					if ($are != null) {
 						$are.addSuppressed($string_append.are);
 					} else {
 						$are = $string_append.are;
 					}
 				}
 			}

 		)
 		|
 		(
 			MULTI_STR_START comment*
 			(
 				string_append [build, be] comment*
 				{
	 				if ($string_append.are != null) {
	 					if ($are != null) {
	 						$are.addSuppressed($string_append.are);
	 					} else {
	 						$are = $string_append.are;
	 					}
	 				}
	 			}

 			)* MULTI_STR_END
 		)
 	)
 	{
 		byte[] bytes = build.toString().getBytes(cs);
		pool.addBytes(bytes);
	}
//		System.out.println("[J-LOG]: string='"+new String(bytes,cs)+"'");

 	//		System.out.println("[J-LOG]: string='"+new String(bytes,StandardCharsets.UTF_16LE)+"'");

 	//		for(int i = 0; i < bytes.length; i ++) {

 	//			System.out.println("[J-LOG]: bytes[" + i + "]=" + (0xFF & (int) bytes[i]));
 	//		}

 ;

 string_append [StringBuilder build, boolean be] returns
 [AssembleRuntimeException are]
 :
 	STR_STR
 	{
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
				case '"':
					chars[ci] = '"';
					break;
				default:
					if (be) {
						throw new AssembleError($STR_STR.getLine(), $STR_STR.getCharPositionInLine() + si - 1, 2, $STR_STR.getStartIndex(), "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
					} else if ($are != null) {
						$are.addSuppressed(new AssembleRuntimeException($STR_STR.getLine(), $STR_STR.getCharPositionInLine() + si - 1, 2, $STR_STR.getStartIndex(), "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'"));
					} else {
						$are = new AssembleRuntimeException($STR_STR.getLine(), $STR_STR.getCharPositionInLine() + si - 1, 2, $STR_STR.getStartIndex(), "illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
					}
				}
			} else {
				chars[ci] = strchars[si];
			}
		}
		build.append(chars, 0, ci);
	}

 ;

 numconst [ConstantPoolCommand pool, Map<String, PrimitiveConstant> constants, boolean be]
 returns [long num, boolean b, AssembleRuntimeException are] @init {
	int radix;
	$b = false;
}
 :
 	(
 		(
 			(
 				BYTE comment*
 				{$b = true;}

 			)?
 			(
 				(
 					t = DEC_FP_NUM
 					{$num = Double.doubleToRawLongBits(Double.parseDouble($t.getText()));}

 				)
 				|
 				(
 					t = UNSIGNED_HEX_NUM
 					{$num = Long.parseUnsignedLong($t.getText().substring(5), 16);}

 				)
 				|
 				(
 					t = HEX_NUM
 					{$num = Long.parseLong($t.getText().substring(4), 16);}

 				)
 				|
 				(
 					t = DEC_NUM
 					{$num = Long.parseLong($t.getText(), 10);}

 				)
 				|
 				(
 					t = DEC_NUM0
 					{$num = Long.parseLong($t.getText().substring(4), 10);}

 				)
 				|
 				(
 					t = OCT_NUM
 					{$num = Long.parseLong($t.getText().substring(4), 8);}

 				)
 				|
 				(
 					t = BIN_NUM
 					{$num = Long.parseLong($t.getText().substring(4), 2);}

 				)
 				|
 				(
 					t = NEG_HEX_NUM
 					{$num = Long.parseLong($t.getText().substring(4), 16);}

 				)
 				|
 				(
 					t = NEG_DEC_NUM
 					{$num = Long.parseLong($t.getText().substring(4), 10);}

 				)
 				|
 				(
 					t = NEG_DEC_NUM0
 					{$num = Long.parseLong($t.getText(), 10);}

 				)
 				|
 				(
 					t = NEG_OCT_NUM
 					{$num = Long.parseLong($t.getText().substring(4), 8);}

 				)
 				|
 				(
 					t = NEG_BIN_NUM
 					{$num = Long.parseLong($t.getText().substring(4), 2);}

 				)
 			)
 		)
 		|
 		(
 			NAME
 			{
				PrimitiveConstant pc = constants.get($NAME.getText());
				if (pc == null) {
					if (be) {
						throw new AssembleError($NAME.getLine(), $NAME.getCharPositionInLine(), $NAME.getStopIndex() - $NAME.getStartIndex() + 1, $NAME.getStartIndex(), "unknown constant: " + $NAME.getText());
					} else {
						pc = new PrimitiveConstant(null, null, 0L, null, -1);
						$are = new AssembleRuntimeException($NAME.getLine(), $NAME.getCharPositionInLine(), $NAME.getStopIndex() - $NAME.getStartIndex() + 1, $NAME.getStartIndex(), "unknown constant: " + $NAME.getText());
					}
				}
				$num = pc.value;
			}

 		)
 	)
 	{
		if (pool != null) {
			if ($b) {
				if (($num & 0xFFL) != $num) {
					if (be) {
						throw new AssembleError($t.getLine(), $t.getCharPositionInLine(), $t.getStopIndex() - $t.getStartIndex() + 1, $t.getStartIndex(), "byte num not inside of byte bounds: 0..255 : 0x00..0xFF");
					} else if ($are != null) {
						$are.addSuppressed(new AssembleRuntimeException($t.getLine(), $t.getCharPositionInLine(), $t.getStopIndex() - $t.getStartIndex() + 1, $t.getStartIndex(), "byte num not inside of byte bounds: 0..255 : 0x00..0xFF"));
					} else {
						$are = new AssembleRuntimeException($t.getLine(), $t.getCharPositionInLine(), $t.getStopIndex() - $t.getStartIndex() + 1, $t.getStartIndex(), "byte num not inside of byte bounds: 0..255 : 0x00..0xFF");
					}
				}
		 		pool.addBytes(new byte[]{(byte)$num});
			} else {
		 		pool.addBytes(new byte[] {
			 		(byte) $num,
			 		(byte) ($num >> 8),
			 		(byte) ($num >> 16),
			 		(byte) ($num >> 24),
			 		(byte) ($num >> 32),
			 		(byte) ($num >> 40),
			 		(byte) ($num >> 48),
			 		(byte) ($num >> 56),
		 		});
			}
		}
 	}

 ;

 comment
 :
 	BLOCK_COMMENT
 	| LINE_COMMENT
 ;

 WRITE
 :
 	'WRITE'
 ;

 CHARS
 :
 	'CHARS'
 ;

 CHAR_STR
 :
 	'\''
 	(
 		(
 			~( '\'' | '\\' )
 		)
 		|
 		(
 			'\\' ~( '\r' | '\n' )
 		)
 	)* '\''
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

 START
 :
 	':'
 ;

 ENDE
 :
 	'>'
 ;

 BYTE
 :
 	'B-'
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

 ERROR
 :
 	'~ERROR'
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

 MULTI_STR_START
 :
 	'('
 ;

 MULTI_STR_END
 :
 	')'
 ;

 NAME
 :
 	[a-zA-Z_] [a-zA-Z_0-9]*
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

 ERROR_MESSAGE
 :
 	'{'
 	(
 		(
 			~( '}' | '\\' )
 		)
 		|
 		(
 			'\\' .
 		)
 	)* '}'
 ;

 LINE_COMMENT
 :
 	'|>'
 	(
 		~( [\r\n] )
 	)*
 ;

 BLOCK_COMMENT
 :
 	'|:'
 	(
 		~':'
 		|
 		(
 			':' ~'>'
 		)
 	)* ':>'
 ;

 WS
 :
 	[ \t\r\n]+ -> skip
 ;
