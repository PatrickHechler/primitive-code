//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.primitive.core.utils;

public class PrimAsmCommands {
	
	private PrimAsmCommands() {}
	
	// GENERATED-CODE-START
	// this code-block is automatic generated, do not modify
	/**
	 * <b>EXTERN</b> <code>(00 00)</code><br>
	 * <i>Parameter: none</i>
	 * <p>
	 * executes an extern function identified by the current address of the instruction pointer (the <code>IP</code> register) and then returns<br>
	 * this command can be used to create libaries which operate outside of the virtual mashine<br>
	 * note that for this command to work, the PVMs extern functions must be initilized<br>
	 * note that this command is not supported by the assembler
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>func a = extern_func_from_addr(IP)</code></li>
	 * <li><code>if ther is a extern function at the IP address</code>
	 * <ul>
	 * <li><code>tmp &lt;-- IP</code></li>
	 * <li><code>execute the extern code</code></li>
	 * <li><code>if tmp = IP</code>
	 * <ul>
	 * <li><code>RET</code></li>
	 * </ul></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>act as if the comand was not known (see INT_ERROR_UNKNOWN_COMMAND)</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int EXTERN  = 0x0000;
	/**
	 * <b>MVB</b> <code>(00 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the byte value of the second parameter to the first byte parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;-8-bit- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int MVB     = 0x0001;
	/**
	 * <b>MVW</b> <code>(00 02)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the word value of the second parameter to the first word parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;-16-bit- p2 </code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int MVW     = 0x0002;
	/**
	 * <b>MVDW</b> <code>(00 03)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the double-word value of the second parameter to the first double-word parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;-32-bit- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int MVDW    = 0x0003;
	/**
	 * <b>MOV</b> <code>(00 04)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * copies the value of the second parameter to the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int MOV     = 0x0004;
	/**
	 * <b>LEA</b> <code>(00 05)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * sets the first parameter of the value of the second parameter plus the instruction pointer
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p2 + IP</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int LEA     = 0x0005;
	/**
	 * <b>MVAD</b> <code>(00 06)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt; , &lt;CONST_PARAM&gt;</code>
	 * <p>
	 * copies the value of the second parameter plus the third parameter to the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p2 + p3</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int MVAD    = 0x0006;
	/**
	 * <b>SWAP</b> <code>(00 07)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * swaps the value of the first and the second parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>ZW &lt;- p1</code></li>
	 * <li><code>p1 &lt;- p2</code></li>
	 * <li><code>p2 &lt;- ZW</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int SWAP    = 0x0007;
	/**
	 * <b>OR</b> <code>(01 00)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 | p2) = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 | p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int OR      = 0x0100;
	/**
	 * <b>AND</b> <code>(01 01)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * uses the logical AND operator with the first and the second parameter and stores the result in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 &amp; p2) = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 &amp; p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int AND     = 0x0101;
	/**
	 * <b>XOR</b> <code>(01 02)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 ^ p2) = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 ^ p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int XOR     = 0x0102;
	/**
	 * <b>NOT</b> <code>(01 03)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * uses the logical NOT operator with every bit of the parameter and stores the result in the parameter<br>
	 * this instruction works like <code>XOR p1, -1</code>
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = -1</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- ~ p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int NOT     = 0x0103;
	/**
	 * <b>LSH</b> <code>(01 04)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * shifts bits of the parameter logically left
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ((p1 &lt;&lt; p2) &gt;&gt; p2) = p1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 &lt;&lt; p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int LSH     = 0x0104;
	/**
	 * <b>RASH</b> <code>(01 05)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * shifts bits of the parameter arithmetic right
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ((p1 &gt;&gt; p2) &lt;&lt; p2) = p1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 &gt;&gt; 2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int RASH    = 0x0105;
	/**
	 * <b>RLSH</b> <code>(01 06)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * shifts bits of the parameter logically right
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ((p1 &gt;&gt;&gt; p2) &lt;&lt; p2) = p1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 &gt;&gt;&gt; 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int RLSH    = 0x0106;
	/**
	 * <b>ADD</b> <code>(01 10)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * adds the values of both parameters and stores the sum in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 + p2</code></li>
	 * <li><code>if ((p1 &lt; 0) &amp; (p2 &gt; 0) &amp; (p1 - p2 &gt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else if ((p1 &gt; 0) &amp; (p2 &lt; 0) &amp; (p1 - p2 &lt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else if p1 != 0</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int ADD     = 0x0110;
	/**
	 * <b>SUB</b> <code>(01 11)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * subtracts the second parameter from the first parameter and stores the result in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 - p2</code></li>
	 * <li><code>if ((p1 &lt; 0) &amp; (p2 &lt; 0) &amp; (p1 + p2 &gt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else if ((p1 &gt; 0) &amp; (p2 &gt; 0) &amp; (p1 + p2 &lt; 0))</code>
	 * <ul>
	 * <li><code>ZERO &lt;-  0</code></li>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else if p1 != 0</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int SUB     = 0x0111;
	/**
	 * <b>MUL</b> <code>(01 12)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * multiplies the first parameter with the second and stores the result in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 * p2</code></li>
	 * <li><code>if p1 = 0</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int MUL     = 0x0112;
	/**
	 * <b>DIV</b> <code>(01 13)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * divides the first parameter with the second and stores the result in the first parameter and the reminder in the second parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 / p2</code></li>
	 * <li><code>p2 &lt;- p1 mod p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int DIV     = 0x0113;
	/**
	 * <b>NEG</b> <code>(01 14)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * uses the arithmetic negation operation with the parameter and stores the result in the parameter<br>
	 * this instruction works like <code>MUL p1, -1</code>
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = UHEX-8000000000000000</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- 0 - p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int NEG     = 0x0114;
	/**
	 * <b>ADDC</b> <code>(01 15)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * adds the values of both parameters and the OVERFLOW flag and stores the sum in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>ZW &lt;- p1 + (p2 + OVERFLOW)</code></li>
	 * <li><code>if ((p1 &gt; 0) &amp; ((p2 + OVERFLOW) &gt; 0) &amp; ((p1 + p2 + OVERFLOW) &lt; 0)) | ((p1 &lt; 0) &amp; ((p2 + OVERFLOW) &lt; 0) &amp; ((p1 + (p2 + OVERFLOW)) &gt; 0))</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- ZW</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int ADDC    = 0x0115;
	/**
	 * <b>SUBC</b> <code>(01 16)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * subtracts the second parameter with the OVERFLOW flag from the first parameter and stores the result in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>ZW &lt;- p1 - (p2 + OVERFLOW)</code></li>
	 * <li><code>if (p1 &gt; 0) &amp; ((p2 + OVERFLOW) &lt; 0) &amp; ((p1 - (p2 + OVERFLOW)) &lt; 0)) | ((p1 &lt; 0) &amp; (p2 &gt; 0) &amp; ((p1 - (p2 + OVERFLOW)) &gt; 0))</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- ZW</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int SUBC    = 0x0116;
	/**
	 * <b>INC</b> <code>(01 17)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * increments the param by one
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = MAX_VALUE</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 = -1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 + 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int INC     = 0x0117;
	/**
	 * <b>DEC</b> <code>(01 18)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * decrements the param by one
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 = MIN_VALUE</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 1</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 = 1</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>OVERFLOW &lt;- 0</code></li>
	 * <li><code>ZREO &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>p1 &lt;- p1 - 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int DEC     = 0x0118;
	/**
	 * <b>ADDFP</b> <code>(01 20)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * adds the floating point values of both parameters and stores the floating point sum in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-add p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int ADDFP   = 0x0120;
	/**
	 * <b>SUBFP</b> <code>(01 21)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the first fp-parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-sub p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int SUBFP   = 0x0121;
	/**
	 * <b>MULFP</b> <code>(01 22)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * multiplies the first fp parameter with the second fp and stores the fp result in the first parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-mul p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int MULFP   = 0x0122;
	/**
	 * <b>DIVFP</b> <code>(01 23)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * divides the first fp-parameter with the second fp and stores the fp-result in the first fp-parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 or p2 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-div p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int DIVFP   = 0x0123;
	/**
	 * <b>NEGFP</b> <code>(01 24)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * multiplies the fp parameter with -1.0
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 is NAN</li>
	 * <li><code>p1 &lt;- p1 fp-mul -1.0</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int NEGFP   = 0x0124;
	/**
	 * <b>UADD</b> <code>(01 30)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * like ADD, but uses the parameters as unsigned parameters
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 uadd p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int UADD    = 0x0130;
	/**
	 * <b>USUB</b> <code>(01 31)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * like SUB, but uses the parameters as unsigned parameters
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 usub p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int USUB    = 0x0131;
	/**
	 * <b>UMUL</b> <code>(01 32)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * like MUL, but uses the parameters as unsigned parameters
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 umul p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int UMUL    = 0x0132;
	/**
	 * <b>UDIV</b> <code>(01 33)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like DIV, but uses the parameters as unsigned parameters
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- oldp1 udiv oldp2</code></li>
	 * <li><code>p2 &lt;- oldp1 umod oldp2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int UDIV    = 0x0133;
	/**
	 * <b>BADD</b> <code>(01 40)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like ADD, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>

	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 big-add p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int BADD    = 0x0140;
	/**
	 * <b>BSUB</b> <code>(01 41)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like SUB, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>

	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 big-sub p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int BSUB    = 0x0141;
	/**
	 * <b>BMUL</b> <code>(01 42)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like MUL, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>

	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- p1 big-mul p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int BMUL    = 0x0142;
	/**
	 * <b>BDIV</b> <code>(01 43)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like DIV, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>

	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- oldp1 big-div oldp2</code></li>
	 * <li><code>p2 &lt;- oldp1 big-mod oldp2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int BDIV    = 0x0143;
	/**
	 * <b>BNEG</b> <code>(01 44)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * like NEG, but uses the parameters as 128 bit value parameters
	 * <ul>
	 * <li>if registers are used the next register is also used</li>
	 * <li>the last register will cause the illegal memory interrupt</li>
	 * </ul>

	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- big-neg p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int BNEG    = 0x0144;
	/**
	 * <b>FPTN</b> <code>(01 50)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * converts the value of the floating point param to a number<br>
	 * the value after the
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li>note that the aritmetic error interrupt is executed instead if p1 is no normal value</li>
	 * <li><code>p1 &lt;- as_num(p1)</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int FPTN    = 0x0150;
	/**
	 * <b>NTFP</b> <code>(01 51)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * converts the value of the number param to a floating point<br>
	 * the value after the
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- as_fp(p1)</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int NTFP    = 0x0151;
	/**
	 * <b>CMP</b> <code>(02 00)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two values and stores the result in the status register
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int CMP     = 0x0200;
	/**
	 * <b>CMPL</b> <code>(02 01)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two values on logical/bit level
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if (p1 &amp; p2) = p2</code>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 1</code></li>
	 * <li><code>SOME_BITS &lt;- 1</code></li>
	 * <li><code>NONE_BITS &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if (p1 &amp; p2) != 0</code>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 0</code></li>
	 * <li><code>SOME_BITS &lt;- 1</code></li>
	 * <li><code>NONE_BITS &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 0</code></li>
	 * <li><code>SOME_BITS &lt;- 0</code></li>
	 * <li><code>NONE_BITS &lt;- 1</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int CMPL    = 0x0201;
	/**
	 * <b>CMPFP</b> <code>(02 02)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two floating point values and stores the result in the status register
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 is NaN | p2 is NaN</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int CMPFP   = 0x0202;
	/**
	 * <b>CHKFP</b> <code>(02 03)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * checks if the floating point param is a positive, negative infinity, NaN or normal value
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 is positive-infinity</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 is negative-infinity</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 is NaN</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int CHKFP   = 0x0203;
	/**
	 * <b>CMPU</b> <code>(02 04)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * compares the two unsigned values and stores the result in the status register
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int CMPU    = 0x0204;
	/**
	 * <b>CMPB</b> <code>(02 05)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * compares the two 128 bit values and stores the result in the status register
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 &lt; p2</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int CMPB    = 0x0205;
	/**
	 * <b>SGN</b> <code>(02 06)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * compares the value with <code>0</code> and stores the result in the status register<br>
	 * this command is like <code>CMP PARAM , 0</code>
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; 0</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 &lt; 0</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int SGN     = 0x0206;
	/**
	 * <b>SGNFP</b> <code>(02 07)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * compares the floating-point value with <code>0.0</code> and stores the result in the status register<br>
	 * this command is like <code>CMPFP PARAM , 0.0</code>
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if p1 &gt; 0.0</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 1</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 &lt; 0.0</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else if p1 is NaN</code>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>GREATER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int SGNFP   = 0x0207;
	/**
	 * <b>JMPERR</b> <code>(02 10)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the <code>ERRNO</code> register stores a value other than <code>0</code>
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ERRNO != 0</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPERR  = 0x0210;
	/**
	 * <b>JMPEQ</b> <code>(02 11)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare operation compared two equal values
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPEQ   = 0x0211;
	/**
	 * <b>JMPNE</b> <code>(02 12)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare operation compared two different values
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPNE   = 0x0212;
	/**
	 * <b>JMPGT</b> <code>(02 13)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was greater
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if GREATER</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPGT   = 0x0213;
	/**
	 * <b>JMPGE</b> <code>(02 14)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was not lower
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if GREATER | EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPGE   = 0x0214;
	/**
	 * <b>JMPLT</b> <code>(02 15)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was lower
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if LOWER</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPLT   = 0x0215;
	/**
	 * <b>JMPLE</b> <code>(02 16)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last compare result was not greater
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if LOWER | EQUAL</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPLE   = 0x0216;
	/**
	 * <b>JMPCS</b> <code>(02 17)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is set
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if OVERFLOW</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPCS   = 0x0217;
	/**
	 * <b>JMPCC</b> <code>(02 18)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is cleared
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ! OVERFLOW</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPCC   = 0x0218;
	/**
	 * <b>JMPZS</b> <code>(02 19)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last zero flag is set
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ZERO</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPZS   = 0x0219;
	/**
	 * <b>JMPZC</b> <code>(02 1a)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last zero flag is cleared
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ! ZERO</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPZC   = 0x021a;
	/**
	 * <b>JMPNAN</b> <code>(02 1b)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last NaN flag is set
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if NAN</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPNAN  = 0x021b;
	/**
	 * <b>JMPAN</b> <code>(02 1c)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last NaN flag is cleared
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ! NAN</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPAN   = 0x021c;
	/**
	 * <b>JMPAB</b> <code>(02 1d)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last AllBits flag is set
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if ALL_BITS</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPAB   = 0x021d;
	/**
	 * <b>JMPSB</b> <code>(02 1e)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last SomeBits flag is set
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if SOME_BITS</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPSB   = 0x021e;
	/**
	 * <b>JMPNB</b> <code>(02 1f)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label if the last NoneBits flag is set
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>if NONE_BITS</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul></li>
	 * <li><code>else</code>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul></li>
	 * </ul>

	 */
	public static final int JMPNB   = 0x021f;
	/**
	 * <b>JMP</b> <code>(02 20)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the command after the label
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>

	 */
	public static final int JMP     = 0x0220;
	/**
	 * <b>INT</b> <code>(02 30)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * calls the interrupt specified by the parameter<br>
	 * an interrupt can be overwritten:
	 * <ul>
	 * <li>the interrupt-table is saved in the <code>INTP</code> register</li>
	 * <li>to overwrite the interrupt <code>N</code>, write to <code>(INTP + (N * 8))</code> the absolute position of the address
	 * <ul>
	 * <li><code>|&gt; example to overwrite a interrupt</code></li>
	 * <li><code>LEA [INTP + OVERWRITE_INT_NUM_MULTIPLIED_WITH_8], RELATIVE_POS_FROM_GET_TO_INTERRUPT</code></li>
	 * </ul></li>
	 * <li>on failure the default interrupts use the <code>ERRNO</code> register to store information about the error which caused the interrupt to fail</li>
	 * </ul>
	 * negative interrupts will always cause the illegal interrupt to be called instead<br>
	 * when <code>INTCNT</code> is greater then the number of default interrupts and the called interrupt is not overwritten, the illegal interrupt will be called instead<br>
	 * for the list of default interrupts see the {@link PrimAsmPreDefines predefined constant} documentation
	 * <ul>
	 * <li>note that all predefined interrupts set the <code>ERRNO</code> register to a nonzero value on error</li>
	 * </ul>

	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>IP         &lt;- IP + CMD_LEN</code></li>
	 * <li>note that default interrupts get called with a different routine</li>
	 * <li><code>ZW         &lt;- MEM-ALLOC{size=128}</code>
	 * <ul>
	 * <li>if the memory allocation fails, the program will terminate with 127</li>
	 * <li>the allocated memory block will not be resizable, but can be freed normally with the free interrupt or with the <code>IRET</code> command</li>
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
	 * <li>the pvm will terminate with 127 instead if the address <code>INTP + (INT_ERROR_ILLEGAL_MEMORY * 8)</code> is also invalid</li>
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
	public static final int INT     = 0x0230;
	/**
	 * <b>IRET</b> <code>(02 31)</code><br>
	 * <i>Parameter: none</i>
	 * <p>
	 * returns from an interrupt<br>
	 * if the address stored in <code>X09</code> was not retrieved from an <code>INT</code> execution, the PVM will call the illegal memory interrupt
	 * <p>
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
	public static final int IRET    = 0x0231;
	/**
	 * <b>CALL</b> <code>(03 00)</code><br>
	 * Parameter: <code>&lt;LABEL&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the label<br>
	 * and pushes the current instruction pointer to the stack
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>[SP] &lt;- IP</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>

	 */
	public static final int CALL    = 0x0300;
	/**
	 * <b>CALO</b> <code>(03 01)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;CONST_PARAM&gt;</code>
	 * <p>
	 * sets the instruction pointer to position of the label<br>
	 * and pushes the current instruction pointer to the stack
	 * <p>
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
	public static final int CALO    = 0x0301;
	/**
	 * <b>RET</b> <code>(03 10)</code><br>
	 * <i>Parameter: none</i>
	 * <p>
	 * sets the instruction pointer to the position which was secured in the stack
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>IP &lt;- [SP + -8]</code></li>
	 * <li><code>SP &lt;- SP - 8</code></li>
	 * </ul>

	 */
	public static final int RET     = 0x0310;
	/**
	 * <b>PUSH</b> <code>(03 20)</code><br>
	 * Parameter: <code>&lt;PARAM&gt;</code>
	 * <p>
	 * pushes the parameter to the stack
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>[SP] &lt;- p1</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int PUSH    = 0x0320;
	/**
	 * <b>POP</b> <code>(03 21)</code><br>
	 * Parameter: <code>&lt;NO_CONST_PARAM&gt;</code>
	 * <p>
	 * pops the highest value from the stack to the parameter
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>p1 &lt;- [SP - 8]</code></li>
	 * <li><code>SP &lt;- SP - 8</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int POP     = 0x0321;
	/**
	 * <b>PUSHBLK</b> <code>(03 22)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * pushes the memory block, which is refered by p1 and p2 large to the stack
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>note that p1 is not allowed to be negative</code></li>
	 * <li><code>MEMORY_COPY{TARGET=SP,SOURCE=p1,BYTE_COUNT=p2}</code></li>
	 * <li><code>SP &lt;- SP + p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int PUSHBLK = 0x0322;
	/**
	 * <b>POPBLK</b> <code>(03 23)</code><br>
	 * Parameter: <code>&lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * <p>
	 * pops the memory block, which will be saved to p1 and is p2 large from the stack
	 * <p>
	 * <b>definition:</b>
	 * <ul>
	 * <li><code>note that p2 is not allowed to be negative</code></li>
	 * <li><code>MEMORY_COPY{TARGET=p1,SOURCE=SP-p2,BYTE_COUNT=p2}</code></li>
	 * <li><code>SP &lt;- SP - p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>

	 */
	public static final int POPBLK  = 0x0323;
	
	// here is the end of the automatic generated code-block
	// GENERATED-CODE-END
	
}
