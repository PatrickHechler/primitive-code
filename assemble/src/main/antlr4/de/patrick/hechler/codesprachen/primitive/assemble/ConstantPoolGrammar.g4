/**
 * Define a grammar called Hello
 */
 grammar ConstantPoolGrammar;

 @header {
import java.util.*;
import java.math.*;
import java.nio.charset.*;
import de.patrick.hechler.codesprachen.primitive.assemble.enums.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.*;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Command.ConstantPoolCommand;
}

 consts [Map<String,Long> constants, Map<String, Long> labels, long pos]
 returns [ConstantPoolCommand pool] @init {$pool = new ConstantPoolCommand();}
 :
 	START
 	(
 		(
 			(
 				string [$pool]
 			)
 			|
 			(
 				numconst [$pool]
 			)
 			|
 			(
 				CONSTANT
 				{boolean simpleAdd = true;}

 				(
 					(
 						HEX_NUM
 						{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($HEX_NUM.getText(), 16));}

 					)
 					|
 					(
 						DEC_NUM
 						{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($HEX_NUM.getText(), 10));}

 					)
 					|
 					(
 						OCT_NUM
 						{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($HEX_NUM.getText(), 8));}

 					)
 					|
 					(
 						BIN_NUM
 						{constants.put($CONSTANT.getText().substring(1), (Long) Long.parseLong($HEX_NUM.getText(), 2));}

 					)
 					|
 					(
 						POS
 						{constants.put($CONSTANT.getText().substring(1), (Long) (pos + $pool.length()));}

 					)
 					{simpleAdd = false;}

 				)?
 				{
 					if (simpleAdd) {
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
 				LABEL_DECLARATION
 				{labels.put($LABEL_DECLARATION.getText().substring(1), (Long) (pos + $pool.length()));}

 			)
 		)*
 	) ENDE EOF
 ;

 string [ConstantPoolCommand pool] @init {Charset cs = StandardCharsets.UTF_8;}
 :
 	(
 		CHARS CHAR_STR
 		{
 			String name = $CHAR_STR.getText();
			name = name.substring(1, name.length() - 1);
 			name = name.replaceAll("\\\\r", "\r");
 			name = name.replaceAll("\\\\n", "\n");
 			name = name.replaceAll("\\\\t", "\t");
 			name = name.replaceAll("\\\\0", "\0");
 			name = name.replaceAll("\\\\0", "\0");
 			name = name.replaceAll("\\\\(.)", "$1");
 			cs = Charset.forName(name);
 		}

 	)? STR_STR
 	{
		String str = $STR_STR.getText();
		str = str.substring(1, str.length() - 1);
		str = str.replaceAll("\\\\r", "\r");
		str = str.replaceAll("\\\\n", "\n");
		str = str.replaceAll("\\\\t", "\t");
		str = str.replaceAll("\\\\0", "\0");
		str = str.replaceAll("\\\\0", "\0");
		str = str.replaceAll("\\\\(.)", "$1");
		byte[] bytes = str.getBytes(cs);
		pool.addBytes(bytes);
	}

 ;

 numconst [ConstantPoolCommand pool] @init {int radix;}
 :
 	(
 		(
 			HEX_START
 			{radix = 16;}

 		)
 		|
 		(
 			DEC_START
 			{radix = 10;}

 		)
 		|
 		(
 			OCT_START
 			{radix = 8;}

 		)
 		|
 		(
 			BIN_START
 			{radix = 2;}

 		)
 	)
 	{
 		byte[] buffer = new byte[7];
 		int bufi = 0;
 	}
 	(
 		ANY_NUM
 		{
			BigInteger bi = new BigInteger($ANY_NUM.getText(), radix);
			byte[] bytes = bi.toByteArray();
			//the signum is always 1 (or 0, but never -1), because of the ANY_NUM Token (which does not permit a '-' or '+' befor the number)
			//it is possible, that the sign bit goes to the next byte (for example HEX: 80 -> HEX: 0080)
			int bits = bi.bitLength();
			int len = bits / 8 + (bits % 8 != 0 ? 1 : 0);
			if (len < bytes.length) {
				byte[] zw = new byte[len];
				if (bytes.length - len != 1) {
					throw new AssertionError("byte.len - len != 1: bytes.len=" + bytes.length + " len=" + len);
				}
				System.arraycopy(bytes, 1, zw, 0, len);
				bytes = zw;
			}
			if (bufi > 0) {
				byte[] zw = new byte[bufi + bytes.length];
				System.arraycopy(buffer, 0, zw, 0, bufi);
				System.arraycopy(bytes, 0, zw, bufi, bytes.length);
				bytes = zw;
			}
			bufi = bytes.length % 8;
			if (bufi > 0) {
				System.arraycopy(bytes, bytes.length - bufi, buffer, 0, bufi);
				bytes = Arrays.copyOf(bytes, bytes.length - bufi);
			}
			if (bytes.length > 0){
				pool.addBytes(bytes);
			}
		}

 	)+
 	{
 		if (bufi > 0) {
 			pool.addBytes(Arrays.copyOf(buffer, bufi));
 		}
 	}
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

 HEX_START
 :
 	'HEX:'
 ;

 DEC_START
 :
 	'DEC:'
 ;

 OCT_START
 :
 	'OCT:'
 ;

 BIN_START
 :
 	'BIN:'
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
 