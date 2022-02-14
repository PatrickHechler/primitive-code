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
returns [ConstantPoolCommand pool, boolean align] @init {
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

			numconst [$pool, constants]
		)
		|
		(
			CONSTANT
			(
				{boolean simpleAdd = true;}

				(
					(
						(
							(
								numconst [null, constants]
								{constants.put($CONSTANT.getText().substring(1), (Long) $numconst.num);}

							)
							|
							(
								CONSTANT_
								{
									Long l = constants.get($CONSTANT_.getText().substring(2));
									if (l == null) {
										throw new AssembleError($CONSTANT_.getLine(), $CONSTANT_.getCharPositionInLine(), "unknown constant: " + $CONSTANT_.getText().substring(2));
									}
									constants.put($CONSTANT.getText().substring(1), (Long) $numconst.num);
								}

							)
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
			ERROR
			{StringBuilder msg = new StringBuilder("error at line: ").append($ERROR.getLine());}

			(
				(
					(
						numconst [null, constants]
						{msg.append(" error: ").append(_localctx.numconst.getText()).append('=').append($numconst.num);}

					)
					|
					(
						ERROR_MESSAGE_START
						{msg.append('\n');}

						(
							(
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
											default:
												throw new AssembleError($STR_STR.getLine(), $STR_STR.getCharPositionInLine(),"illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
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
								numconst [null, constants]
								{msg.append($numconst.num);}

							)
							|
							(
								ERROR_HEX numconst [null, constants]
								{msg.append(Long.toHexString($numconst.num));}

							)
						)* ERROR_MESSAGE_END
					)
				)?
			)
			{
				if (true) {//just for the compiler (antlr puts code after that and the compiler throws an error because of unreachable code)
					throw new AssembleError($ERROR.getLine(), $ERROR.getCharPositionInLine(), msg.toString());
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

numconst [ConstantPoolCommand pool, Map<String, Long> constants] returns
[long num, boolean b] @init {
	int radix;
	$b = false;
}
:
	(
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
		|
		(
			CONSTANT_
			{
				Long l = constants.get($CONSTANT_.getText().substring(2));
				if (l == null) {
					throw new AssembleError($CONSTANT_.getLine(), $CONSTANT_.getCharPositionInLine(), "unknown constant: " + $CONSTANT_.getText().substring(2));
				}
				$num = l;
			}
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

PLUS
:
	'+'
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

ANY_NUM
:
	[0-9a-fA-f]+
;

EXIST_CONSTANT
:
	'#~' [a-zA-Z\-_] [a-zA-Z\-_0-9]*
;

CONSTANT_
:
	'#' CONSTANT
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
