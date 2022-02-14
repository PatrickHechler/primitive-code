/**
 * Define a grammar called Hello
 */
grammar ConstantPoolGrammar;

@parser::header {
import java.util.*;
import java.math.*;
import java.nio.charset.*;
import de.patrick.hechler.codesprachen.primitive.assemble.enums.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.*;
import de.patrick.hechler.codesprachen.primitive.assemble.exceptions.AssembleError;
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
[Map<String,Long> constants, Map<String, Long> labels, long pos, boolean alignParam]
returns [ConstantPoolCommand pool, boolean align]
@init {
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
 	$pool = new ConstantPoolCommand();
 	$align = alignParam;
 }
:
	START
	(
		(
			{makeAlign($align, pos, $pool);}

			string [$pool]
		)
		|
		(
			{makeAlign($align, pos, $pool);}

			numconst [$pool]
		)
		|
		(
			CONSTANT
			(
				{boolean simpleAdd = true;}

				(
					(
						(
							constBerechnungDirekt [pos + $pool.length(), constants]
							{constants.put($CONSTANT.getText().substring(1), (Long) $constBerechnungDirekt.num);}

						)
						{simpleAdd = false;}

					)
					|
					(
						WRITE
					)
				)?
				{
				if (simpleAdd) {
					makeAlign($align, pos, $pool);
					Long szw = constants.get($CONSTANT.getText().substring(1));
					if (szw == null) {
						throw new AssembleError($CONSTANT.getLine(), $CONSTANT.getCharPositionInLine(),"unknown constant: '" + $CONSTANT.getText().substring(1) + "' (I know: '" + constants + "')");
					}
					long zw = (long) szw;
					$pool.addBytes(new byte[]{(byte) (zw >> 56), (byte) (zw >> 48), (byte) (zw >> 40), (byte) (zw >> 32), (byte) (zw >> 24), (byte) (zw >> 16), (byte) (zw >> 8), (byte) zw});
				}
			}

			)
			|
			(
				DEL
				{constants.remove($CONSTANT.getText().substring(1));}

			)
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
			IF constBerechnungDirekt [pos + $pool.length(), constants]
			{
				{
	 				boolean top = $constBerechnungDirekt.num != 0;
	 				stack.add(top);
	 				if (enabled && !top) {
	 					disabledSince = stack.size();
	 					enabled = false;
	 				}
 				}
 			}

		)
		|
		(
			ELSE_IF constBerechnungDirekt [pos + $pool.length(), constants]
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
			ERROR
			{
 				StringBuilder msg = null;
 				if (enabled) {
	 				msg = new StringBuilder("error at line: ").append($ERROR.getLine());
 				}
 			}

			(
				(
					(
						constBerechnungDirekt [$pos, constants]
						{
 							if (enabled) {
	 							msg.append(" error: ").append(_localctx.constBerechnungDirekt.getText()).append('=').append($constBerechnungDirekt.num);
 							}
 						}

					)
					|
					(
						ERROR_MESSAGE_START
						{
 							if (enabled) {
	 							msg.append('\n');
 							}
 						}

						(
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
													throw new AssembleError($STR_STR.getLine(), $STR_STR.getCharPositionInLine(),"illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
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
								constBerechnungDirekt [pos, constants]
								{
 									if (enabled) {
	 									msg.append($constBerechnungDirekt.num);
 									} 
 								}

							)
							|
							(
								ERROR_HEX constBerechnungDirekt [pos, constants]
								{
 									if (enabled) {
	 									msg.append(Long.toHexString($constBerechnungDirekt.num));
 									}
 								}

							)
						)* ERROR_MESSAGE_END
					)
				)?
			)
			{
				if (enabled) {
					throw new AssembleError($ERROR.getLine(), $ERROR.getCharPositionInLine(),msg.toString());
				}
			}

		)
		|
		(
			ANY
			{
				if (enabled) {
					throw new AssembleError($ANY.getLine(), $ANY.getCharPositionInLine(),"illegal character at line: " + $ANY.getLine() + ", pos-in-line: "+$ANY.getCharPositionInLine()+" char='" + $ANY.getText() + "'");
				} else {
					
				}
			}

		)
	)* ENDE EOF
;

string [ConstantPoolCommand pool]
@init {Charset cs = Charset.defaultCharset();}
:
	(
		(
			CHARS CHAR_STR
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
						default:
							throw new AssembleError($CHAR_STR.getLine(), $CHAR_STR.getCharPositionInLine(),"illegal escaped character: '" + strchars[si] + "' complete orig string='" + name + "'");
						}
					} else {
						chars[ci] = strchars[si];
					}
				}
	 			cs = Charset.forName(new String(chars, 0 ,ci));
	 		}

		)?
	) STR_STR
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
				default:
					throw new AssembleError($STR_STR.getLine(), $STR_STR.getCharPositionInLine(),"illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
				}
			} else {
				chars[ci] = strchars[si];
			}
		}
		byte[] bytes = new String(chars, 0, ci).getBytes(cs);
		pool.addBytes(bytes);
	}
	//		System.out.println("[J-LOG]: string='"+new String(bytes,cs)+"'");

	//		System.out.println("[J-LOG]: string='"+new String(bytes,StandardCharsets.UTF_16LE)+"'");

	//		for(int i = 0; i < bytes.length; i ++) {

	//			System.out.println("[J-LOG]: bytes[" + i + "]=" + (0xFF & (int) bytes[i]));
 //		}

;

constBerechnung [long pos, Map<String, Long> constants] returns [long num]
:
	c1 = constBerechnungInclusivoder [pos, constants]
	{$num = $c1.num;}

	(
		FRAGEZEICHEN c2 = constBerechnung [pos, constants] DOPPELPUNKT c3 =
		constBerechnungInclusivoder [pos, constants]
		{$num = ($num != 0L) ? $c2.num : $c3.num;}

	)?
;

constBerechnungInclusivoder [long pos, Map<String, Long> constants] returns
[long num]
:
	c1 = constBerechnungExclusivoder [pos, constants]
	{$num = $c1.num;}

	(
		INCLUSIVODER c2 = constBerechnungExclusivoder [pos, constants]
		{$num |= $c2.num;}

	)*
;

constBerechnungExclusivoder [long pos, Map<String, Long> constants] returns
[long num]
:
	c1 = constBerechnungUnd [pos, constants]
	{$num = $c1.num;}

	(
		EXCLUSIVPDER c2 = constBerechnungUnd [pos, constants]
		{$num ^= $c2.num;}

	)*
;

constBerechnungUnd [long pos, Map<String, Long> constants] returns [long num]
:
	c1 = constBerechnungGleichheit [pos, constants]
	{$num = $c1.num;}

	(
		UND c2 = constBerechnungGleichheit [pos, constants]
		{$num &= $c2.num;}

	)*
;

constBerechnungGleichheit [long pos, Map<String, Long> constants] returns
[long num]
:
	c1 = constBerechnungRelativeTests [pos, constants]
	{$num = $c1.num;}

	(
		{boolean gleich = false;}

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
		) c2 = constBerechnungRelativeTests [pos, constants]
		{
 			if (gleich) {
 				$num = ($num == $c1.num) ? 1L : 0L;
 			} else {
 				$num = ($num == $c1.num) ? 0L : 1L;
 			}
 		}

	)*
;

constBerechnungRelativeTests [long pos, Map<String, Long> constants] returns
[long num]
:
	c1 = constBerechnungSchub [pos, constants]
	{$num = $c1.num;}

	(
		{
		final int type_gr = 1, type_gr_gl = 2, type_kl_gl = 3, type_kl = 4;
		int type = -1;
	}

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
		) c2 = constBerechnungSchub [pos, constants]
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

constBerechnungSchub [long pos, Map<String, Long> constants] returns
[long num]
:
	c1 = constBerechnungStrich [pos, constants]
	{$num = $c1.num;}

	(
		{
		final int type_ls = 1, type_lrs = 2, type_ars = 3;
		int type = -1;
	}

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
		) c2 = constBerechnungStrich [pos, constants]
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

constBerechnungStrich [long pos, Map<String, Long> constants] returns
[long num]
:
	c1 = constBerechnungPunkt [pos, constants]
	{$num = $c1.num;}

	(
		{boolean add = false;}

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
		) c2 = constBerechnungPunkt [pos, constants]
		{
 			if (add) {
 				$num += $c2.num;
 			} else {
 				$num -= $c2.num;
 			}
 		}

	)*
;

constBerechnungPunkt [long pos, Map<String, Long> constants] returns
[long num]
:
	c1 = constBerechnungDirekt [pos, constants]
	{$num = $c1.num;}

	(
		{
			final int type_mal = 1, type_geteilt = 2, type_modulo = 3;
			int type = -1;
		}

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
		) c2 = constBerechnungDirekt [pos, constants]
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

constBerechnungDirekt [long pos, Map<String, Long> constants] returns
[long num]
:
	(
		numconst [null]
		{
 			if ($numconst.b) {
 				throw new AssembleError(_localctx.numconst.t.getLine(), _localctx.numconst.t.getCharPositionInLine(), "byte values are not allowed for conastants");
 			}
 			$num = $numconst.num;
 		}

	)
	|
	(
		POS
		{$num = pos;}

	)
	|
	(
		EXIST_CONSTANT
		{$num = constants.containsKey($EXIST_CONSTANT.getText().substring(2)) ? 1L : 0L;}

	)
	|
	(
		RND_KL_AUF constBerechnung [pos, constants] RND_KL_ZU
		{$num = $constBerechnung.num;}

	)
;

numconst [ConstantPoolCommand pool] returns [long num, boolean b]
@init {int radix;}
:
	(
		(
			BYTE
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
	{
			if (pool != null) {
				if ($b) {
					if (($num & 0xFFL) != $num) {
						throw new AssembleError($t.getLine(), $t.getCharPositionInLine(),"byte num not inside of byte bounds: 0..255 : 0x00..0xFF");
					}
			 		pool.addBytes(new byte[]{(byte)$num});
				}else {
			 		pool.addBytes(new byte[]{
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
			~'\''
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
			~'"'
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

RND_KL_AUF
:
	'('
;

RND_KL_ZU
:
	')'
;

PLUS
:
	'+'
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

ELSE
:
	'~ELSE'
;

ELSE_IF
:
	'~ELSE-IF'
;

ENDIF
:
	'~ENDIF'
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

POS
:
	'--POS--'
;

ANY_NUM
:
	[0-9a-fA-f]+
;

EXIST_CONSTANT
:
	'#~' [a-zA-Z\-_] [a-zA-Z\-_0-9]*
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
	)* ':>' -> skip
;

WS
:
	[ \t\r\n]+ -> skip
;

ANY
:
	.
;