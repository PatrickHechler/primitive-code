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
							HEX_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($HEX_NUM.getText(), 16));}

						)
						|
						(
							DEC_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($DEC_NUM.getText(), 10));}

						)
						|
						(
							DEC_NUM0
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($DEC_NUM0.getText(), 10));}

						)
						|
						(
							OCT_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($OCT_NUM.getText(), 8));}

						)
						|
						(
							BIN_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($BIN_NUM.getText(), 2));}

						)
						|
						(
							NEG_HEX_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong("-" + $NEG_HEX_NUM.getText(), 16));}

						)
						|
						(
							NEG_DEC_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong("-" + $NEG_DEC_NUM.getText(), 10));}

						)
						|
						(
							NEG_DEC_NUM0
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong("-" + $NEG_DEC_NUM0.getText(), 10));}

						)
						|
						(
							NEG_OCT_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong("-" + $NEG_OCT_NUM.getText(), 8));}

						)
						|
						(
							NEG_BIN_NUM
							{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong("-" + $NEG_BIN_NUM.getText(), 2));}

						)
						|
						(
							POS
							{constants.put($CONSTANT.getText().substring(1), (Long) (pos + $pool.length()));}

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
						throw new RuntimeException("unknown constant: '" + $CONSTANT.getText().substring(1) + "' (I know: '" + constants + "')");
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
				for (int ci = 0, si = 0; si < strchars.length; ci ++, si ++) {
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
							throw new RuntimeException("illegal escaped character: '" + strchars[si] + "' complete orig string='" + name + "'");
						}
					} else {
						chars[ci] = strchars[si];
					}
				}
	 			cs = Charset.forName(new String(chars));
	 		}

		)?
	) STR_STR
	{
		String str = $STR_STR.getText();
		str = str.substring(1, str.length() - 1);
		char[] chars = new char[str.length()];
		char[] strchars = str.toCharArray();
		for (int ci = 0, si = 0; si < strchars.length; ci ++, si ++) {
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
					throw new RuntimeException("illegal escaped character: '" + strchars[si] + "' complete orig string='" + str + "'");
				}
			} else {
				chars[ci] = strchars[si];
			}
		}
		byte[] bytes = new String(chars).getBytes(cs);
		pool.addBytes(bytes);
//		System.out.println("[J-LOG]: string='"+new String(bytes,cs)+"'");
//		System.out.println("[J-LOG]: string='"+new String(bytes,StandardCharsets.UTF_16LE)+"'");
//		for(int i = 0; i < bytes.length; i ++) {
//			System.out.println("[J-LOG]: bytes[" + i + "]=" + (0xFF & (int) bytes[i]));
//		}
	}

;

numconst [ConstantPoolCommand pool] @init {int radix;}
:
	(
		{long num;}

		(
			(
				HEX_NUM
				{num = Long.parseLong($HEX_NUM.getText().substring(4), 16);}

			)
			|
			(
				DEC_NUM
				{num = Long.parseLong($DEC_NUM.getText(), 10);}

			)
			|
			(
				DEC_NUM0
				{num = Long.parseLong($DEC_NUM0.getText().substring(4), 10);}

			)
			|
			(
				OCT_NUM
				{num = Long.parseLong($OCT_NUM.getText().substring(4), 8);}

			)
			|
			(
				BIN_NUM
				{num = Long.parseLong($BIN_NUM.getText().substring(4), 2);}

			)
			|
			(
				NEG_HEX_NUM
				{num = Long.parseLong("-" + $NEG_HEX_NUM.getText().substring(5), 16);}

			)
			|
			(
				NEG_DEC_NUM
				{num = Long.parseLong("-" + $NEG_DEC_NUM.getText().substring(5), 10);}

			)
			|
			(
				NEG_DEC_NUM0
				{num = Long.parseLong($NEG_DEC_NUM0.getText(), 10);}

			)
			|
			(
				NEG_OCT_NUM
				{num = Long.parseLong("-" + $NEG_OCT_NUM.getText().substring(5), 8);}

			)
			|
			(
				NEG_BIN_NUM
				{num = Long.parseLong("-" + $NEG_BIN_NUM.getText().substring(5), 2);}

			)
		)
		{
	 		pool.addBytes(new byte[]{
	 		(byte) num,
	 		(byte) (num << 8),
	 		(byte) (num << 16),
	 		(byte) (num << 24),
	 		(byte) (num << 32),
	 		(byte) (num << 40),
	 		(byte) (num << 48),
	 		(byte) (num << 56),
	 		});
	 	}

	)+
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
			'\\' .
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
			'\\' .
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

ANY_NUM
:
	[0-9a-fA-f]+
;

CONSTANT
:
	'#' [a-zA-Z\-_]+
;

LABEL_DECLARATION
:
	'@' [a-zA-Z\-_]+
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
 