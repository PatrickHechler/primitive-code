package de.hechler.patrick.codesprachen.primitive.core.utils;

public class PrimAsmCommands {
	
	private PrimAsmCommands() {}
	
	//GENERATED-CODE-START
	// this code-block is automatic generated, do not modify
	/**
	 * <h>MVB</h> <code>(01 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the byte value of the second parameter to the first byte parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;-8-bit- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int MVB     = 0x0100;
	/**
	 * <h>MVW</h> <code>(02 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the word value of the second parameter to the first word parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;-16-bit- p2 </code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int MVW     = 0x0200;
	/**
	 * <h>MVDW</h> <code>(03 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the double-word value of the second parameter to the first double-word parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;-32-bit- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int MVDW    = 0x0300;
	/**
	 * <h>MOV</h> <code>(04 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the value of the second parameter to the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int MOV     = 0x0400;
	/**
	 * <h>LEA</h> <code>(05 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * sets the first parameter of the value of the second parameter plus the instruction pointer<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p2 + IP</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int LEA     = 0x0500;
	/**
	 * <h>MVAD</h> <code>(06 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt; , &lt;CONST_PARAM&gt;</code>
	 * <p>
	 * copies the value of the second parameter plus the third parameter to the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p2 + p3</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int MVAD    = 0x0600;
	/**
	 * <h>SWAP</h> <code>(07 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * swaps the value of the first and the second parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>ZW &lt;- p1</code></li>
	 * <li><code>p1 &lt;- p2</code></li>
	 * <li><code>p2 &lt;- ZW</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int SWAP    = 0x0700;
	/**
	 * <h>OR</h> <code>(00 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * uses the logical OR operator with the first and the second parameter and stores the result in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 | p2) = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 | p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int OR      = 0x0001;
	/**
	 * <h>AND</h> <code>(01 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * uses the logical AND operator with the first and the second parameter and stores the result in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 & p2) = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 & p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int AND     = 0x0101;
	/**
	 * <h>XOR</h> <code>(02 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * uses the logical OR operator with the first and the second parameter and stores the result in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 ^ p2) = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 ^ p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int XOR     = 0x0201;
	/**
	 * <h>NOT</h> <code>(03 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * uses the logical NOT operator with every bit of the parameter and stores the result in the parameter<br>
	 * this instruction works like <code>XOR p1, -1</code> <br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = -1</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- ~ p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int NOT     = 0x0301;
	/**
	 * <h>LSH</h> <code>(04 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * shifts bits of the parameter logically left<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ((p1 &lt;&lt; p2) &gt;&gt; p2) = p1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 &lt;&lt; p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int LSH     = 0x0401;
	/**
	 * <h>RASH</h> <code>(05 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * shifts bits of the parameter arithmetic right<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ((p1 &gt;&gt; p2) &lt;&lt; p2) = p1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 &gt;&gt; 2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int RASH    = 0x0501;
	/**
	 * <h>RLSH</h> <code>(06 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * shifts bits of the parameter logically right<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ((p1 &gt;&gt;&gt; p2) &lt;&lt; p2) = p1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 &gt;&gt;&gt; 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int RLSH    = 0x0601;
	/**
	 * <h>ADD</h> <code>(10 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * adds the values of both parameters and stores the sum in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 + p2</code></li>
	 * <li><code>if ((p1 &lt; 0) & (p2 &gt; 0) & (p1 - p2 &gt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else if ((p1 &gt; 0) & (p2 &lt; 0) & (p1 - p2 &lt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 != 0</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int ADD     = 0x1001;
	/**
	 * <h>SUB</h> <code>(11 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * subtracts the second parameter from the first parameter and stores the result in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 - p2</code></li>
	 * <li><code>if ((p1 &lt; 0) & (p2 &lt; 0) & (p1 + p2 &gt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else if ((p1 &gt; 0) & (p2 &gt; 0) & (p1 + p2 &lt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 != 0</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int SUB     = 0x1101;
	/**
	 * <h>MUL</h> <code>(12 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * multiplies the first parameter with the second and stores the result in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 * p2</code></li>
	 * <li><code>if p1 = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int MUL     = 0x1201;
	/**
	 * <h>DIV</h> <code>(13 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * divides the first parameter with the second and stores the result in the first parameter and the reminder in the second parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 / p2</code></li>
	 * <li><code>p2 &lt;- p1 mod p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int DIV     = 0x1301;
	/**
	 * <h>NEG</h> <code>(14 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * uses the arithmetic negation operation with the parameter and stores the result in the parameter <br>
	 * this instruction works like <code>MUL p1, -1</code><br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = UHEX-8000000000000000</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- 0 - p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int NEG     = 0x1401;
	/**
	 * <h>ADDC</h> <code>(15 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * adds the values of both parameters and the OVERFLOW flag and stores the sum in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>ZW &lt;- p1 + (p2 + OVERFLOW)</code></li>
	 * <li><code>if ((p1 &gt; 0) & ((p2 + OVERFLOW) &gt; 0) & ((p1 + p2 + OVERFLOW) &lt; 0)) | ((p1 &lt; 0) & ((p2 + OVERFLOW) &lt; 0) & ((p1 + (p2 + OVERFLOW)) &gt; 0))</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- ZW</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int ADDC    = 0x1501;
	/**
	 * <h>SUBC</h> <code>(16 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * subtracts the second parameter with the OVERFLOW flag from the first parameter and stores the result in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>ZW &lt;- p1 - (p2 + OVERFLOW)</code></li>
	 * <li><code>if (p1 &gt; 0) & ((p2 + OVERFLOW) &lt; 0) & ((p1 - (p2 + OVERFLOW)) &lt; 0)) | ((p1 &lt; 0) & (p2 &gt; 0) & ((p1 - (p2 + OVERFLOW)) &gt; 0))</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- ZW</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int SUBC    = 0x1601;
	/**
	 * <h>INC</h> <code>(17 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * increments the param by one<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = MAX_VALUE</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 = -1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 + 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int INC     = 0x1701;
	/**
	 * <h>DEC</h> <code>(18 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * decrements the param by one<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = MIN_VALUE</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * <li><code>ZERO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 = 1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZREO &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 - 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int DEC     = 0x1801;
	/**
	 * <h>ADDFP</h> <code>(20 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * adds the floating point values of both parameters and stores the floating point sum in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-add p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int ADDFP   = 0x2001;
	/**
	 * <h>SUBFP</h> <code>(21 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the first fp-parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-sub p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int SUBFP   = 0x2101;
	/**
	 * <h>MULFP</h> <code>(22 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * multiplies the first fp parameter with the second fp and stores the fp result in the first parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-mul p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int MULFP   = 0x2201;
	/**
	 * <h>DIVFP</h> <code>(23 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * divides the first fp-parameter with the second fp and stores the fp-result in the first fp-parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-div p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int DIVFP   = 0x2301;
	/**
	 * <h>NEGFP</h> <code>(24 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * multiplies the fp parameter with -1.0<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-mul -1.0</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int NEGFP   = 0x2401;
	/**
	 * <h>UADD</h> <code>(30 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * like ADD, but uses the parameters as unsigned parameters<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 uadd p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int UADD    = 0x3001;
	/**
	 * <h>USUB</h> <code>(31 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * like SUB, but uses the parameters as unsigned parameters<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 usub p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int USUB    = 0x3101;
	/**
	 * <h>UMUL</h> <code>(32 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * like MUL, but uses the parameters as unsigned parameters<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 umul p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int UMUL    = 0x3201;
	/**
	 * <h>UDIV</h> <code>(33 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like DIV, but uses the parameters as unsigned parameters<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- oldp1 udiv oldp2</code></li>
	 * <li><code>p2 &lt;- oldp1 umod oldp2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int UDIV    = 0x3301;
	/**
	 * <h>BADD</h> <code>(40 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like ADD, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 big-add p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int BADD    = 0x4001;
	/**
	 * <h>BSUB</h> <code>(41 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like SUB, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 big-sub p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int BSUB    = 0x4101;
	/**
	 * <h>BMUL</h> <code>(42 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like MUL, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 big-mul p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int BMUL    = 0x4201;
	/**
	 * <h>BDIV</h> <code>(43 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like DIV, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- oldp1 big-div oldp2</code></li>
	 * <li><code>p2 &lt;- oldp1 big-mod oldp2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int BDIV    = 0x4301;
	/**
	 * <h>BNEG</h> <code>(44 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like NEG, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- big-neg p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int BNEG    = 0x4401;
	/**
	 * <h>FPTN</h> <code>(50 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * converts the value of the floating point param to a number<br>
	 * the value after the <br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 is no normal value</li>
	 * <li><code>p1 &lt;- as_num(p1)</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int FPTN    = 0x5001;
	/**
	 * <h>NTFP</h> <code>(51 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * converts the value of the number param to a floating point<br>
	 * the value after the <br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- as_fp(p1)</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int NTFP    = 0x5101;
	/**
	 * <h>CMP</h> <code>(00 02)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two values and stores the result in the status register<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int CMP     = 0x0002;
	/**
	 * <h>CMPL</h> <code>(01 02)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two values on logical/bit level<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 & p2) = p2</code>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 1</code></li>
	 * <li><code>SOME_BITS &lt;- 1</code></li>
	 * <li><code>NONE_BITS &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if (p1 & p2) != 0</code>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 0</code></li>
	 * <li><code>SOME_BITS &lt;- 1</code></li>
	 * <li><code>NONE_BITS &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 0</code></li>
	 * <li><code>SOME_BITS &lt;- 0</code></li>
	 * <li><code>NONE_BITS &lt;- 1</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int CMPL    = 0x0102;
	/**
	 * <h>CMPFP</h> <code>(02 02)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two floating point values and stores the result in the status register<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 is NaN | p2 is NaN</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int CMPFP   = 0x0202;
	/**
	 * <h>CHKFP</h> <code>(03 02)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * checks if the floating point param is a positive, negative infinity, NaN or normal value<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 is positive-infinity</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 is negative-infinity</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 is NaN</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int CHKFP   = 0x0302;
	/**
	 * <h>CMPU</h> <code>(04 02)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two unsigned values and stores the result in the status register<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int CMPU    = 0x0402;
	/**
	 * <h>CMPB</h> <code>(05 02)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * compares the two 128 bit values and stores the result in the status register<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></ul>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int CMPB    = 0x0502;
	/**
	 * <h>JMPERR</h> <code>(10 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the <code>ERRNO</code> register stores a value other than <code>0</code><br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ERRNO != 0</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPERR  = 0x1002;
	/**
	 * <h>JMPEQ</h> <code>(11 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare operation compared two equal values<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPEQ   = 0x1102;
	/**
	 * <h>JMPNE</h> <code>(12 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare operation compared two different values<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPNE   = 0x1202;
	/**
	 * <h>JMPGT</h> <code>(13 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was greater<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if GREATHER</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPGT   = 0x1302;
	/**
	 * <h>JMPGE</h> <code>(14 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was not lower<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if GREATHER | EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPGE   = 0x1402;
	/**
	 * <h>JMPLT</h> <code>(15 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was lower<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if LOWER</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPLT   = 0x1502;
	/**
	 * <h>JMPLE</h> <code>(16 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was not greater<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if LOWER | EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPLE   = 0x1602;
	/**
	 * <h>JMPCS</h> <code>(17 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is set<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if OVERFLOW</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPCS   = 0x1702;
	/**
	 * <h>JMPCC</h> <code>(18 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is cleared<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ! OVERFLOW</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPCC   = 0x1802;
	/**
	 * <h>JMPZS</h> <code>(19 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last zero flag is set<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ZERO</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPZS   = 0x1902;
	/**
	 * <h>JMPZC</h> <code>(1a 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last zero flag is cleared<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ! ZERO</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPZC   = 0x1a02;
	/**
	 * <h>JMPNAN</h> <code>(1b 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last NaN flag is set<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if NAN</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPNAN  = 0x1b02;
	/**
	 * <h>JMPAN</h> <code>(1c 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last NaN flag is cleared<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ! NAN</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPAN   = 0x1c02;
	/**
	 * <h>JMPAB</h> <code>(1d 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last AllBits flag is set<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ALL_BITS</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPAB   = 0x1d02;
	/**
	 * <h>JMPSB</h> <code>(1e 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last SomeBits flag is set<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if SOME_BITS</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPSB   = 0x1e02;
	/**
	 * <h>JMPNB</h> <code>(1f 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last NoneBits flag is set<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if NONE_BITS</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></ul>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int JMPNB   = 0x1f02;
	/**
	 * <h>JMP</h> <code>(20 02)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 */
	public static final int JMP     = 0x2002;
	/**
	 * <h>INT</h> <code>(30 02)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * calls the interrupt specified by the parameter<br>
	 * an interrupt can be overwritten:
	 * <ul>
	 * <li>the interrupt-table is saved in the <code>INTP</code> register</li>
	 * <li>to overwrite the interrupt <code>N</code>, write to <code>(INTP + (N * 8))</code> the absolute position of the address
	 * <ul>
	 * <li><code>|&gt; example to overwrite a interrupt</code></li>
	 * <li><code>LEA [INTP + OVERWRITE_INT_NUM_MULTIPLIED_WITH_8], RELATIVE_POS_FROM_GET_TO_INTERRUPT</code></ul>
	 * </ul></li>
	 * <li>on failure the default interrupts use the <code>ERRNO</code> register to store information about the error which caused the interrupt to fail</li>
	 * <li>negative interrupts will always cause the illegal interrup to be called instead</li>
	 * </ul>
	 * when <code>INTCNT</code> is greather then the number of default interrupts and the called interrupt is not overwritten, the illegal interrupt will be called instead<br>
	 * default interrupts:
	 * <ul>
	 * <li><code>0 : INT_ERRORS_ILLEGAL_INTERRUPT</code>: illegal interrupt
	 * <ul>
	 * <li><code>X00</code> contains the number of the illegal interrupt</li>
	 * <li>exits with <code>(128 + illegal_interrup_number)</code> (without calling the exit interrupt)</li>
	 * <li>if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with <code>128</code></ul>
	 * </ul></li>
	 * <li><code>1 : INT_ERRORS_UNKNOWN_COMMAND</code>: unknown command
	 * <ul>
	 * <li>exits with <code>7</code> (without calling the exit interrupt)</ul>
	 * </ul></li>
	 * <li><code>2 : INT_ERRORS_ILLEGAL_MEMORY</code>: illegal memory
	 * <ul>
	 * <li>exits with <code>6</code> (without calling the exit interrupt)</ul>
	 * </ul></li>
	 * <li><code>3 : INT_ERRORS_ARITHMETIC_ERROR</code>: arithmetic error
	 * <ul>
	 * <li>exits with <code>5</code> (without calling the exit interrupt)</ul>
	 * </ul></li>
	 * <li><code>4 : INT_EXIT</code>: exit
	 * <ul>
	 * <li>use <code>X00</code> to specify the exit number of the progress</ul>
	 * </ul></li>
	 * <li><code>5 : INT_MEMORY_ALLOC</code>: allocate a memory-block
	 * <ul>
	 * <li><code>X00</code> saves the size of the block</li>
	 * <li>if the value of <code>X00</code> is <code>-1</code> after the call the memory-block could not be allocated</li>
	 * <li>if the value of <code>X00</code> is not <code>-1</code>, <code>X00</code> points to the first element of the allocated memory-block</ul>
	 * </ul></li>
	 * <li><code>6 : INT_MEMORY_REALLOC</code>: reallocate a memory-block
	 * <ul>
	 * <li><code>X00</code> points to the memory-block</li>
	 * <li><code>X01</code> is set to the new size of the memory-block</li>
	 * <li><code>X01</code> will be <code>-1</code> if the memory-block could not be reallocated, the old memory-block will remain valid and should be freed if it is not longer needed</li>
	 * <li><code>X01</code> will point to the new memory block, the old memory-block was automatically freed, so it should not be used, the new block should be freed if it is not longer needed</ul>
	 * </ul></li>
	 * <li><code>7 : INT_MEMORY_FREE</code>: free a memory-block
	 * <ul>
	 * <li><code>X00</code> points to the old memory-block</li>
	 * <li>after this the memory-block should not be used</ul>
	 * </ul></li>
	 * <li><code>8 : INT_OPEN_STREAM</code>: open new stream
	 * <ul>
	 * <li><code>X00</code> contains a pointer to the STRING, which refers to the file which should be read</li>
	 * <li><code>X01</code> specfies the open mode: (bitwise flags)
	 * <ul>
	 * <li><code>OPEN_ONLY_CREATE</code>
	 * <ul>
	 * <li>fail if the file/pipe exist already</li>
	 * <li>when this flags is set either <code>OPEN_FILE</code> or <code>OPEN_PIPE</code> has to be set</ul>
	 * </ul></li>
	 * <li><code>OPEN_ALSO_CREATE</code>
	 * <ul>
	 * <li>create the file/pipe if it does not exist, but do not fail if the file/pipe exist already (overwritten by PFS_SO_ONLY_CREATE)</ul>
	 * </ul></li>
	 * <li><code>OPEN_FILE</code>
	 * <ul>
	 * <li>fail if the element is a pipe and if a create flag is set create a file if the element does not exist already</li>
	 * <li>this flag is not compatible with <code>OPEN_PIPE</code></ul>
	 * </ul></li>
	 * <li><code>OPEN_PIPE</code>
	 * <ul>
	 * <li>fail if the element is a file and if a create flag is set create a pipe</li>
	 * <li>this flag is not compatible with <code>OPEN_FILE</code></ul>
	 * </ul></li>
	 * <li><code>OPEN_READ</code>
	 * <ul>
	 * <li>open the stream for read access</ul>
	 * </ul></li>
	 * <li><code>OPEN_WRITE</code>
	 * <ul>
	 * <li>open the stream for write access</ul>
	 * </ul></li>
	 * <li><code>OPEN_APPEND</code>
	 * <ul>
	 * <li>open the stream for append access (before every write operation the position is set to the end of the file)</li>
	 * <li>implicitly also sets <code>OPEN_WRITE</code> (for pipes there is no diffrence in <code>OPEN_WRITE</code> and <code>OPEN_APPEND</code>)</ul>
	 * </ul></li>
	 * <li><code>OPEN_FILE_TRUNCATE</code>
	 * <ul>
	 * <li>truncate the files content</li>
	 * <li>implicitly sets <code>OPEN_FILE</code></li>
	 * <li>nop when also <code>OPEN_ONLY_CREATE</code> is set</ul>
	 * </ul></li>
	 * <li><code>OPEN_FILE_EOF</code>
	 * <ul>
	 * <li>set the position initially to the end of the file not the start</li>
	 * <li>ignored when opening a pipe</ul>
	 * </ul></li>
	 * <li>other flags will be ignored</li>
	 * <li>the operation will fail if it is not spezified if the file should be opened for read, write and/or append</ul>
	 * </ul></li>
	 * <li>opens a new stream to the specified file</li>
	 * <li>if successfully the STREAM-ID will be saved in the <code>X00</code> register</li>
	 * <li>if failed <code>X00</code> will contain <code>-1</code></li>
	 * <li>to close the stream use the stream close interrupt (<code>INT_STREAM_CLOSE</code>)</ul>
	 * </ul></li>
	 * <li><code>9 : INT_STREAMS_WRITE</code>: write
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to write</li>
	 * <li><code>X02</code> points to the elements to write</li>
	 * <li><code>X01</code> will be set to the number of written bytes.</ul>
	 * </ul></li>
	 * <li><code>10 : INT_STREAMS_READ</code>: read
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to read</li>
	 * <li><code>X02</code> points to the elements to read</li>
	 * <li>after execution <code>X01</code> will contain the number of elements, which has been read.
	 * <ul>
	 * <li>when the value is less than len either an error occured or end of file/pipe has reached (which is not considered an error)</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>11 : INT_STREAMS_CLOSE</code>: stream close
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X00</code> will be set to 1 on success and 0 on error</ul>
	 * </ul></li>
	 * <li><code>12 : INT_STREAMS_FILE_GET_POS</code>: stream file get position
	 * <ul>
	 * <li><code>X00</code> contains the STREAM/FILE_STREAM-ID</li>
	 * <li><code>X01</code> will be set to the stream position or -1 on error</ul>
	 * </ul></li>
	 * <li><code>13 : INT_STREAMS_FILE_SET_POS</code>: stream file set position
	 * <ul>
	 * <li><code>X00</code> contains the STREAM/FILE_STREAM-ID</li>
	 * <li><code>X01</code> contains the new position of the stream</li>
	 * <li><code>X01</code> will be set to 1 or 0 on error</li>
	 * <li>note that it is possible to set the stream position behind the end of the file.
	 * <ul>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>14 : INT_STREAMS_FILE_ADD_POS</code>: stream file add position
	 * <ul>
	 * <li><code>X00</code> contains the STREAM/FILE_STREAM-ID</li>
	 * <li><code>X01</code> contains the value, which should be added to the position of the stream
	 * <ul>
	 * <li><code>X01</code> is allowed to be negative, but the sum of the old position and <code>X01</code> is not allowed to be negative</ul>
	 * </ul></li>
	 * <li><code>X01</code> will be set to the new position or -1 on error</li>
	 * <li>note that it is possible to set the stream position behind the end of the file.
	 * <ul>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>15 : INT_STREAMS_FILE_SEEK_EOF</code>: stream file seek eof
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> will be set to the new position of the stream or -1 on error</li>
	 * <li>sets the position of the stream to the end of the file (the file length)</ul>
	 * </ul></li>
	 * <li><code>16 : INT_OPEN_FILE</code>: open element handle file
	 * <ul>
	 * <li><code>X00</code> points to the <code>STRING</code> which contains the path of the file to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM/FILE-ID or -1 on error</li>
	 * <li>this operation will fail if the element is no file</ul>
	 * </ul></li>
	 * <li><code>17 : INT_OPEN_FOLDER</code>: open element handle folder
	 * <ul>
	 * <li><code>X00</code> points to the <code>STRING</code> which contains the path of the folder to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM/FOLDER-ID or -1 on error</li>
	 * <li>this operation will fail if the element is no folder</ul>
	 * </ul></li>
	 * <li><code>18 : INT_OPEN_PIPE</code>: open element handle pipe
	 * <ul>
	 * <li><code>X00</code> points to the <code>STRING</code> which contains the path of the pipe to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM/PIPE-ID or -1 on error</li>
	 * <li>this operation will fail if the element is no pipe</ul>
	 * </ul></li>
	 * <li><code>19 : INT_OPEN_ELEMENT</code>: open element handle (any)
	 * <ul>
	 * <li><code>X00</code> points to the <code>STRING</code> which contains the path of the element to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM-ID or -1 on error</ul>
	 * </ul></li>
	 * <li><code>20 : INT_ELEMENT_OPEN_PARENT</code>: element open parent handle
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X00</code> will be set to the newly opened ELEMENT/FOLDER-ID or -1 on error</ul>
	 * </ul></li>
	 * <li><code>21 : INT_ELEMENT_GET_CREATE</code>: get create date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> will be set to the create date or <code>-1</code> on error
	 * <ul>
	 * <li>note that <code>-1</code> may be the create date of the element, so check <code>ERRNO</code> instead</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>22 : INT_ELEMENT_GET_LAST_MOD</code>: get last mod date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> will be set to the last modified date or <code>-1</code> on error
	 * <ul>
	 * <li>note that <code>-1</code> may be the last modified date of the element, so check <code>ERRNO</code> instead</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>23 : INT_ELEMENT_SET_CREATE</code>: set create date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X00</code> contains the new create date of the element</li>
	 * <li><code>X01</code> will be set to <code>1</code> or <code>0</code> on error</ul>
	 * </ul></li>
	 * <li><code>24 : INT_ELEMENT_SET_LAST_MOD</code>: set last modified date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X00</code> contains the last modified date of the element</li>
	 * <li><code>X01</code> will be set to <code>1</code> or <code>0</code> on error</ul>
	 * </ul></li>
	 * <li><code>25 : INT_ELEMENT_DELETE</code>: element delete
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li>note that this operation automatically closes the given ELEMENT-ID, the close interrupt should not be invoked after this interrupt returned</li>
	 * <li><code>X01</code> will be set to <code>1</code> or <code>0</code> on error</ul>
	 * </ul></li>
	 * <li><code>26 : INT_ELEMENT_MOVE</code>: element move
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> points to a STRING which will be the new name or it is set to <code>-1</code> if the name should not be changed</li>
	 * <li><code>X02</code> contains the ELEMENT-ID of the new parent of <code>-1</code> if the new parent should not be changed</li>
	 * <li>when both <code>X01</code> and <code>X02</code> are set to <code>-1</code> this operation will do nothing</li>
	 * <li><code>X01</code> will be set to <code>1</code> or <code>0</code> on error</ul>
	 * </ul></li>
	 * <li><code>27 : INT_ELEMENT_GET_NAME</code>: element get name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> points the the a memory block, which should be used to store the name as a STRING
	 * <ul>
	 * <li>when <code>X01</code> is set to <code>-1</code> a new memory block will be allocated</ul>
	 * </ul></li>
	 * <li>on success <code>X01</code> will point to the name as STRING representation
	 * <ul>
	 * <li>when the memory block is not large enough, it will be resized</li>
	 * <li>note that when <code>X01</code> does not point to the start of the memory block the start of the memory block can still be moved during the reallocation</ul>
	 * </ul></li>
	 * <li>on error <code>X01</code> will be set to <code>-1</code></ul>
	 * </ul></li>
	 * <li><code>28 : INT_ELEMENT_GET_FLAGS</code>: element get flags
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> will be set to the flags or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>29 : INT_ELEMENT_MODIFY_FLAGS</code>: element modify flags
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> contains the flags to be added</li>
	 * <li><code>X02</code> contains the flags to be removed</li>
	 * <li>note that only the low 32 bit will be used and the high 32 bit will be ignored</li>
	 * <li><code>X01</code> will be set to <code>1</code> or <code>0</code> on error</ul>
	 * </ul></li>
	 * <li><code>30 : INT_FOLDER_CHILD_COUNT</code>: element folder child count
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01</code> will be set to the number of child elements the folder has or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>31 : INT_FOLDER_OPEN_CHILD_OF_NAME</code>: folder get child of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li><code>X01</code> will be set to a newly opened ELEMENT-ID for the child or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>32 : INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME</code>: folder get child folder of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li>this operation will fail if the child is no folder</li>
	 * <li><code>X01</code> will be set to a newly opened ELEMENT/FOLDER-ID for the child or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>33 : INT_FOLDER_OPEN_CHILD_FILE_OF_NAME</code>: folder get child file of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li>this operation will fail if the child is no file</li>
	 * <li><code>X01</code> will be set to a newly opened ELEMENT/FILE-ID for the child or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>34 : INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME</code>: folder get child pipe of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li>this operation will fail if the child is no pipe</li>
	 * <li><code>X01</code> will be set to a newly opened ELEMENT/PIPE-ID for the child or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>35 : INT_FOLDER_CREATE_CHILD_FOLDER</code>: folder add child folder
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li><code>X01</code> will be set to a newly opened/created ELEMENT/FOLDER-ID for the child or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>36 : INT_FOLDER_CREATE_CHILD_FILE</code>: folder add child file
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>X01</code> will be set to a newly opened/created ELEMENT/FILE-ID for the child or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>37 : INT_FOLDER_CREATE_CHILD_PIPE</code>: folder add child pipe
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>X01</code> will be set to a newly opened/created ELEMENT/PIPE-ID for the child or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>38</code>: INT_FOLDER_OPEN_ITER`: open child iterator of folder
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01</code> is set to <code>0</code> if hidden files should be skipped and any other value if not</li>
	 * <li><code>X01</code> will be set to the FOLDER-ITER-ID or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>39 : INT_FILE_LENGTH</code>: get the length of a file
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FILE-ID</li>
	 * <li><code>X01</code> will be set to the file length in bytes or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>40 : INT_FILE_TRUNCATE</code>: set the length of a file
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FILE-ID</li>
	 * <li><code>X01</code> is set to the new length of the file</li>
	 * <li>this interrupt will append zeros to the file when the new length is larger than the old length or remove all content after the new length</li>
	 * <li><code>X01</code> will be set <code>1</code> on success or <code>0</code> on error</ul>
	 * </ul></li>
	 * <li><code>41 : INT_HANDLE_OPEN_STREAM</code>: opens a stream from a file or pipe handle
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FILE/PIPE-ID
	 * <ul>
	 * <li>note that this interrupt works for both files and pipes, but will fail for folders</ul>
	 * </ul></li>
	 * <li><code>X01</code> is set to the open flags
	 * <ul>
	 * <li>note that the high 32-bit of the flags are ignored</ul>
	 * </ul></li>
	 * <li><code>X01</code> will be set to the STREAM-ID or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>42 : INT_PIPE_LENGTH</code>: get the length of a pipe
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/PIPE-ID</li>
	 * <li><code>X01</code> will be set to the pipe length in bytes or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>43 : INT_TIME_GET</code>: get the current system time
	 * <ul>
	 * <li><code>X00</code> will be set to <code>1</code> on success and <code>0</code> on error</li>
	 * <li><code>X01</code> will be set to the curent system time in seconds since the epoch</li>
	 * <li><code>X02</code> will be set to the additional curent system time in nanoseconds</ul>
	 * </ul></li>
	 * <li><code>44 : INT_TIME_GET</code>: get the system time resolution
	 * <ul>
	 * <li><code>X00</code> will be set to <code>1</code> on success and <code>0</code> on error</li>
	 * <li><code>X01</code> will be set to the resolution in seconds</li>
	 * <li><code>X02</code> will be set to the additional resolution in nanoseconds</ul>
	 * </ul></li>
	 * <li><code>45 : INT_TIME_SLEEP</code>: to sleep the given time in nanoseconds
	 * <ul>
	 * <li><code>X00</code> contain the number of nanoseconds to wait (only values from <code>0</code> to <code>999999999</code> are allowed)</li>
	 * <li><code>X01</code> contain the number of seconds to wait (only values greather or equal to <code>0</code> are allowed)</li>
	 * <li><code>X00</code> and <code>X01</code> will contain the remaining time (both <code>0</code> if it finished waiting)</li>
	 * <li><code>X02</code> will be <code>1</code> if the call was successfully and <code>0</code> if something went wrong</li>
	 * <li><code>X00</code> will not be negative if the progress waited too long</ul>
	 * </ul></li>
	 * <li><code>46 : INT_TIME_WAIT</code>: to wait the given time in nanoseconds
	 * <ul>
	 * <li><code>X00</code> contain the number of seconds since the epoch</li>
	 * <li><code>X01</code> contain the additional number of nanoseconds</li>
	 * <li>this interrupt will wait until the current system time is equal or after the given absolute time.</li>
	 * <li><code>X00</code> and <code>X01</code> will contain the remaining time (both <code>0</code> if it finished waiting)</li>
	 * <li><code>X02</code> will be <code>1</code> if the call was successfully and <code>0</code> if something went wrong</ul>
	 * </ul></li>
	 * <li><code>47 : INT_RND_OPEN</code>: open a read stream which delivers random values
	 * <ul>
	 * <li><code>X00</code> will be set to the STREAM-ID or <code>-1</code> on error
	 * <ul>
	 * <li>the stream will only support read operations
	 * <ul>
	 * <li>not write/append or seek/setpos operations</ul>
	 * </ul></li>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>48 : INT_RND_NUM</code>: sets <code>X00</code> to a random number
	 * <ul>
	 * <li><code>X00</code> will be set to a random non negative number or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>49 : INT_MEM_CMP</code>: memory compare
	 * <ul>
	 * <li>compares two blocks of memory</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length in bytes of both memory blocks</li>
	 * <li>the <code>STATUS</code> register <code>LOWER</code> <code>GREATHER</code> and <code>EQUAL</code> flags will be set after this interrupt</ul>
	 * </ul></li>
	 * <li><code>49 : INT_MEM_CPY</code>: memory copy
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function has undefined behavior if the two blocks overlap</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</ul>
	 * </ul></li>
	 * <li><code>50 : INT_MEM_MOV</code>: memory move
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</ul>
	 * </ul></li>
	 * <li><code>51 : INT_MEM_BSET</code>: memory byte set
	 * <ul>
	 * <li>sets a memory block to the given byte-value</li>
	 * <li><code>X00</code> points to the block</li>
	 * <li><code>X01</code> the first byte contains the value to be written to each byte</li>
	 * <li><code>X02</code> contains the length in bytes</ul>
	 * </ul></li>
	 * <li><code>52 : INT_STR_LEN</code>: string length
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00</code> will be set to the length of the string/ the (byte-)offset of the first byte from the <code>'\0'</code> character</ul>
	 * </ul></li>
	 * <li><code>53 : INT_STR_CMP</code>: string compare
	 * <ul>
	 * <li><code>X00</code> points to the first STRING</li>
	 * <li><code>X01</code> points to the second STRING</li>
	 * <li>the <code>STATUS</code> register <code>LOWER</code> <code>GREATHER</code> and <code>EQUAL</code> flags will be set after this interrupt</ul>
	 * </ul></li>
	 * <li><code>54 : INT_STR_FROM_NUM</code>: number to string
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert</li>
	 * <li><code>X01</code> is points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> contains the base of the number system
	 * <ul>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></ul>
	 * </ul></li>
	 * <li><code>X03</code> is set to the length of the buffer
	 * <ul>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</ul>
	 * </ul></li>
	 * <li><code>X00</code> will be set to the size of the STRING (without the <code>\0</code> terminating character)</li>
	 * <li><code>X01</code> will be set to the new buffer</li>
	 * <li><code>X03</code> will be set to the new size of the buffer
	 * <ul>
	 * <li>the new length will be the old length or if the old length is smaller than the size of the STRING (with <code>\0</code>) than the size of the STRING (with <code>\0</code>)</ul>
	 * </ul></li>
	 * <li>on error <code>X01</code> will be set to <code>-1</code></ul>
	 * </ul></li>
	 * <li><code>55 : INT_STR_FROM_FPNUM</code>: floating point number to string
	 * <ul>
	 * <li><code>X00</code> is set to the floating point number to convert</li>
	 * <li><code>X01</code> points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> is set to the current size of the buffer
	 * <ul>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</ul>
	 * </ul></li>
	 * <li><code>X00</code> will be set to the size of the STRING</li>
	 * <li><code>X01</code> will be set to the new buffer</li>
	 * <li><code>X02</code> will be set to the new size of the buffer
	 * <ul>
	 * <li>the new length will be the old length or if the old length is smaller than the size of the STRING (with <code>\0</code>) than the size of the STRING (with <code>\0</code>)</ul>
	 * </ul></li>
	 * <li>on error <code>X01</code> will be set to <code>-1</code></ul>
	 * </ul></li>
	 * <li><code>56 : INT_STR_TO_NUM</code>: string to number
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X01</code> points to the base of the number system
	 * <ul>
	 * <li>(for example <code>10</code> for the decimal system or <code>2</code> for the binary system)</li>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></ul>
	 * </ul></li>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * <li>on success <code>X01</code> will be set to <code>1</code></li>
	 * <li>on error <code>X01</code> will be set to <code>0</code>
	 * <ul>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</li>
	 * <li>if <code>ERRNO</code> is set to out of range, the string value displayed a value outside of the 64-bit number range and <code>X00</code> will either be min or max value</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>57 : INT_STR_TO_FPNUM</code>: string to floating point number
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * <li>on success <code>X01</code> will be set to <code>1</code></li>
	 * <li>on error <code>X01</code> will be set to <code>0</code>
	 * <ul>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>58 : INT_STR_TO_U16STR</code>: STRING to U16-STRING
	 * <ul>
	 * <li><code>X00</code> points to the STRING (<code>UTF-8</code>)</li>
	 * <li><code>X01</code> points to the buffer to be filled with the to <code>UTF-16</code> converted string</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li><code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> will be set to the number of converted characters or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>59: INT_STR_TO_U32STR</code>: STRING to U32-STRING
	 * <ul>
	 * <li><code>X00</code> points to the STRING (<code>UTF-8</code>)</li>
	 * <li><code>X01</code> points to the buffer to be filled with the to <code>UTF-32</code> converted string</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li><code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> will be set to the number of converted characters or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>60 : INT_STR_FROM_U16STR</code>: U16-STRING to STRING
	 * <ul>
	 * <li><code>X00</code> points to the <code>UTF-16</code> STRING</li>
	 * <li><code>X01</code> points to the buffer to be filled with the converted STRING (<code>UTF-8</code>)</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li><code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator (note that the <code>\0</code> char needs two bytes))</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> will be set to the number of converted characters or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>61 : INT_STR_FROM_U32TR</code>: U32-STRING to STRING
	 * <ul>
	 * <li><code>X00</code> points to the <code>UTF-32</code> STRING</li>
	 * <li><code>X01</code> points to the buffer to be filled with the converted STRING (<code>UTF-8</code>)</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li><code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator (note that the <code>\0</code> char needs four bytes))</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> will be set to the number of converted characters or <code>-1</code> on error</ul>
	 * </ul></li>
	 * <li><code>62 : INT_STR_FORMAT</code>: format string
	 * <ul>
	 * <li><code>X00</code> is set to the STRING input</li>
	 * <li><code>X01</code> contains the buffer for the STRING output</li>
	 * <li><code>X02</code> is the size of the buffer in bytes</li>
	 * <li>the register <code>X03</code> points to the formatting arguments</li>
	 * <li><code>X00</code> will be set to the length of the output string (the offset of the <code>\0</code> character) or <code>-1</code> on error
	 * <ul>
	 * <li>if <code>X00</code> is larger or equal to <code>X02</code>, only the first <code>X02</code> bytes will be written to the buffer</ul>
	 * </ul></li>
	 * <li>formatting:
	 * <ul>
	 * <li><code>%%</code>: to escape an <code>%</code> character (only one <code>%</code> will be in the formatted STRING)</li>
	 * <li><code>%s</code>: the next argument points to a STRING, which should be inserted here</li>
	 * <li><code>%c</code>: the next argument starts with a byte, which should be inserted here
	 * <ul>
	 * <li>note that UTF-8 characters are not always represented by one byte, but there will always be only one byte used</ul>
	 * </ul></li>
	 * <li><code>%n</code>: consumes two arguments
	 * <ul>
	 * <li>1. the next argument contains a number in the range of <code>2..36</code>.
	 * <ul>
	 * <li>if the first argument is less than <code>2</code> or larger than <code>36</code> the interrupt will fail</ul>
	 * </ul></li>
	 * <li>2. which should be converted to a STRING using the number system with the basoe of the first argument and than be inserted here</ul>
	 * </ul></li>
	 * <li><code>%d</code>: the next argument contains a number, which should be converted to a STRING using the decimal number system and than be inserted here</li>
	 * <li><code>%f</code>: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here</li>
	 * <li><code>%p</code>: the next argument contains a pointer, which should be converted to a STRING
	 * <ul>
	 * <li>if the pointer is not <code>-1</code> the pointer will be converted by placing a <code>"p-"</code> and then the unsigned pointer-number converted to a STRING using the hexadecimal number system</li>
	 * <li>if the pointer is <code>-1</code> it will be converted to the STRING <code>"p-inval"</code></ul>
	 * </ul></li>
	 * <li><code>%h</code>: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here</li>
	 * <li><code>%b</code>: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here</li>
	 * <li><code>%o</code>: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here</ul>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>63 : INT_LOAD_FILE</code>: load a file
	 * <ul>
	 * <li><code>X00</code> is set to the path (inclusive name) of the file</li>
	 * <li><code>X00</code> will point to the memory block, in which the file has been loaded or <code>-1</code> on error</li>
	 * <li><code>X01</code> will be set to the length of the file (and the memory block)</ul>
	 * </ul></li>
	 * <li><code>64 : INT_LOAD_LIB</code>: load a library file
	 * <ul>
	 * <li>similar like the load file interrupt loads a file for the program.
	 * <ul>
	 * <li>the difference is that this interrupt may remember which files has been loaded
	 * <ul>
	 * <li>there are no guarantees, when the same memory block is reused and when a new memory block is created</ul>
	 * </ul></li>
	 * <li>the other difference is that the file may only be unloaded with the unload lib interrupt (not with the free interrupt)
	 * <ul>
	 * <li>the returned memory block also can not be resized</ul>
	 * </ul></li>
	 * <li>if the interrupt is executed multiple times with the same file, it will return every time the same memory block.</li>
	 * <li>this interrupt does not recognize files loaded with the <code>64</code> (<code>INT_LOAD_FILE</code>) interrupt.</ul>
	 * </ul></li>
	 * <li><code>X00</code> is set to the path (inclusive name) of the file</li>
	 * <li><code>X00</code> will point to the memory block, in which the file has been loaded</li>
	 * <li><code>X01</code> will be set to the length of the file (and the memory block)</li>
	 * <li><code>X02</code> will be set to <code>1</code> if the file has been loaded as result of this interrupt and <code>0</code> if the file was previously loaded</li>
	 * <li>when an error occurred <code>X00</code> will be set to <code>-1</code></ul>
	 * </ul></li>
	 * <li><code>65 : INT_UNLOAD_LIB</code>: unload a library file
	 * <ul>
	 * <li>unloads a library previously loaded with the load lib interrupt</li>
	 * <li>this interrupt will ensure that the given memory block will be freed and never again be returned from the load lib interrupt</li>
	 * <li><code>X00</code> points to the (start of the) memory block</li>
	 * </ul></li>
	 * </ul>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>IP         &lt;- IP + CMD_LEN</code></li>
	 * <li>note that default interrupts get called with a different routine</li>
	 * <li><code>ZW         &lt;- MEM-ALLOC{size=128}</code>
	 * <ul>
	 * <li>if the memory allocation fails, the program will terminate with 127</li>
	 * <li>the allocated memory block will not be resizable, but can be freed normally with the free interrupt or with the <code>IRET</code> command</ul>
	 * </ul></li>
	 * <li><code>[ZW]       &lt;- IP</code></li>
	 * <li><code>[ZW + 8]   &lt;- SP</code></li>
	 * <li><code>[ZW + 16]  &lt;- STATUS</code></li>
	 * <li><code>[ZW + 24]  &lt;- INTCNT</code></li>
	 * <li><code>[ZW + 32]  &lt;- INTP</code></li>
	 * <li><code>[ZW + 40]  &lt;- ERRNO</code></li>
	 * <li><code>[ZW + 48]  &lt;- X00</code></li>
	 * <li><code>[ZW + 56]  &lt;- X01</code></li>
	 * <li><code>[ZW + 64]  &lt;- X02</code></li>
	 * <li><code>[ZW + 72]  &lt;- X03</code></li>
	 * <li><code>[ZW + 80]  &lt;- X04</code></li>
	 * <li><code>[ZW + 88]  &lt;- X05</code></li>
	 * <li><code>[ZW + 96]  &lt;- X06</code></li>
	 * <li><code>[ZW + 104] &lt;- X07</code></li>
	 * <li><code>[ZW + 112] &lt;- X08</code></li>
	 * <li><code>[ZW + 120] &lt;- X09</code></li>
	 * <li><code>X09        &lt;- ZW</code></li>
	 * <li><code>IP         &lt;- [INTP + (p1 * 8)]</code>
	 * <ul>
	 * <li>if the address <code>INTP + (p1 * 8)</code> is invalid the pvm will execute the illegal memory interrupt
	 * <ul>
	 * <li>the pvm will terminate with 127 instead if the address <code>INTP + (INT_ERRORS_ILLEGAL_MEMORY * 8)</code> is also invalid</ul>
	 * </ul></li>
	 * <li>note that if the address <code>[INTP + (p1 * 8)]</code> the illegal memory interrupt will be executed.
	 * <ul>
	 * <li>note that if is the illegal memory interrupt entry is invalid (and not <code>-1</code>) a loop will occur
	 * <ul>
	 * <li>note that in this loop the program would allocate memory, until there is no longer enough memory</li>
	 * </ul></li>
	 * </ul></li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int INT     = 0x3002;
	/**
	 * <h>IRET</h> <code>(31 02)</code><br>
	 * <i>Parameter: none</i>
	 * <p>
	 * returns from an interrupt<br>
	 * if the address stored in <code>X09</code> was not retrieved from an <code>INT</code> execution, the PVM will call the illegal memory interrupt<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>ZW      &lt;- X09</code></li>
	 * <li><code>IP      &lt;- [X09]</code></li>
	 * <li><code>SP      &lt;- [X09 + 8]</code></li>
	 * <li><code>STATUS  &lt;- [X09 + 16]</code></li>
	 * <li><code>INTCNT  &lt;- [X09 + 24]</code></li>
	 * <li><code>INTP    &lt;- [X09 + 32]</code></li>
	 * <li><code>ERRNO   &lt;- [X09 + 40]</code></li>
	 * <li><code>X00     &lt;- [X09 + 48]</code></li>
	 * <li><code>X01     &lt;- [X09 + 56]</code></li>
	 * <li><code>X02     &lt;- [X09 + 64]</code></li>
	 * <li><code>X03     &lt;- [X09 + 72]</code></li>
	 * <li><code>X04     &lt;- [X09 + 80]</code></li>
	 * <li><code>X05     &lt;- [X09 + 88]</code></li>
	 * <li><code>X06     &lt;- [X09 + 98]</code></li>
	 * <li><code>X07     &lt;- [X09 + 104]</code></li>
	 * <li><code>X08     &lt;- [X09 + 112]</code></li>
	 * <li><code>X09     &lt;- [X09 + 120]</code></li>
	 * <li><code>FREE ZW</code>
	 * <ul>
	 * <li>this does not use the free interrupt, but works like the default free interrupt (without calling the interrupt (what could cause an infinite recursion))</li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int IRET    = 0x3102;
	/**
	 * <h>CALL</h> <code>(00 03)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the label<br>
	 * and pushes the current instruction pointer to the stack<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>[SP] &lt;- IP</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 */
	public static final int CALL    = 0x0003;
	/**
	 * <h>CALO</h> <code>(01 03)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;CONST_PARAM&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the label<br>
	 * and pushes the current instruction pointer to the stack<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>[SP] &lt;- IP</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- p1 + p2</code>
	 * <ul>
	 * <li>note that this call is not relative from the current position</li>
	 * </ul></li>
	 * </ul>
	 */
	public static final int CALO    = 0x0103;
	/**
	 * <h>RET</h> <code>(10 03)</code><br>
	 * <i>Parameter: none</i>
	 * <p>
	 * sets the instruction pointer to the position which was secured in the stack<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>IP &lt;- [SP + -8]</code></li>
	 * <li><code>SP &lt;- SP - 8</code></li>
	 * </ul>
	 */
	public static final int RET     = 0x1003;
	/**
	 * <h>PUSH</h> <code>(20 03)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * pushes the parameter to the stack<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>[SP] &lt;- p1</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int PUSH    = 0x2003;
	/**
	 * <h>POP</h> <code>(21 03)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * pops the highest value from the stack to the parameter<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- [SP - 8]</code></li>
	 * <li><code>SP &lt;- SP - 8</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int POP     = 0x2103;
	/**
	 * <h>PUSHBLK</h> <code>(22 03)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * pushes the memory block, which is refered by p1 and p2 large to the stack<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>note that p1 is not allowed to be negative</code></li>
	 * <li><code>MEMORY_COPY{TARGET=SP,SOURCE=p1,BYTE_COUNT=p2}</code></li>
	 * <li><code>SP &lt;- SP + p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int PUSHBLK = 0x2203;
	/**
	 * <h>POPBLK</h> <code>(22 03)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * pops the memory block, which will be saved to p1 and is p2 large from the stack<br>
	 * </p><p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>note that p2 is not allowed to be negative</code></li>
	 * <li><code>MEMORY_COPY{TARGET=p1,SOURCE=SP-p2,BYTE_COUNT=p2}</code></li>
	 * <li><code>SP &lt;- SP - p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 */
	public static final int POPBLK  = 0x2203;
	
	// here is the end of the automatic generated code-block
	// GENERATED-CODE-END
	
}
