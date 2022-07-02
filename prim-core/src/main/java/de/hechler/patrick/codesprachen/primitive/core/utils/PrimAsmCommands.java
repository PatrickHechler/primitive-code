package de.hechler.patrick.codesprachen.primitive.core.utils;

public class PrimAsmCommands {
	
	
	/**
	 * <code>MOV &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>copies the value of the second parameter to the first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>01 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int MOV = 0x01;
	/**
	 * <code>ADD &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>adds the values of both parameters and stores the sum in the first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ((p1 &gt; 0) &amp; (p2 &gt; 0) &amp; ((p1 + p2) &lt; 0)) | ((p1 &lt; 0) &amp; (p2 &lt; 0) &amp; ((p1 + p2) &gt; 0))</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 + p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>02 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int ADD = 0x02;
	/**
	 * <code>SUB &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>subtracts the second parameter from the first parameter and stores the result in the first
	 * parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ((p1 &gt; 0) &amp; (p2 &lt; 0) &amp; ((p1 - p2) &lt; 0)) | ((p1 &lt; 0) &amp; (p2 &gt; 0) &amp; ((p1 - p2) &gt; 0))</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 - p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>03 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int SUB = 0x03;
	/**
	 * <code>MUL &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>multiplies the first parameter with the second and stores the result in the first
	 * parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 * p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>04 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int MUL = 0x04;
	/**
	 * <code>DIV &lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>divides the first parameter with the second and stores the result in the first parameter and
	 * the reminder in the second parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 / p2</code></li>
	 * <li><code>p2 &lt;- p1 mod p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>05 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int DIV = 0x05;
	/**
	 * <code>AND &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>uses the logical AND operator with the first and the second parameter and stores the result
	 * in the first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 &amp; p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>06 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int AND = 0x06;
	/**
	 * <code>OR &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>uses the logical OR operator with the first and the second parameter and stores the result in
	 * the first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 | p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>07 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int OR = 0x07;
	/**
	 * <code>XOR &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>uses the logical OR operator with the first and the second parameter and stores the result in
	 * the first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 ^ p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>08 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int XOR = 0x08;
	/**
	 * <code>NOT &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>uses the logical NOT operator with every bit of the parameter and stores the result in the
	 * parameter</li>
	 * <li>this instruction works like <code>XOR p1, -1</code></li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- ~ p1</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>09 &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int NOT = 0x09;
	/**
	 * <code>NEG &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>uses the arithmetic negation operation with the parameter and stores the result in the
	 * parameter</li>
	 * <li>this instruction works like <code>MUL p1, -1</code></li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * </ul>
	 * <li><code>if p1 = UHEX-8000000000000000</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- 0 - p1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>0A &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int NEG = 0x0A;
	/**
	 * <code>LSH &lt;NO_CONST_PARAM&gt;, &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>shifts bits of the parameter logically left</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ((p1 &lt;&lt; p2) &gt;&gt;&gt; p2) = p1</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 &lt;&lt; p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>0B &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int LSH = 0x0B;
	/**
	 * <code>RLSH &lt;NO_CONST_PARAM&gt;, &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>shifts bits of the parameter logically right</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ((p1 &gt;&gt; p2) &lt;&lt; p2) = p1</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 &gt;&gt; 1</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>0C &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int RLSH = 0x0C;
	/**
	 * <code>RASH &lt;NO_CONST_PARAM&gt;, &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>shifts bits of the parameter arithmetic right</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ((p1 &gt;&gt;&gt; p2) &lt;&lt;&lt; p2) = p1</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 &gt;&gt;&gt; 2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>0D &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int RASH = 0x0D;
	/**
	 * <code>DEC &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>decrements the param by one</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if p1 = MIN_VALUE</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * </ul>
	 * <li><code>else if p1 = 1</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * <li><code>ZREO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 - 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>0E &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int DEC = 0x0E;
	/**
	 * <code>INC &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>increments the param by one</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if p1 = MAX_VALUE</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * </ul>
	 * <li><code>else if p1 = -1</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 + 1</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>0F &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int INC = 0x0F;
	
	/**
	 * <code>JMP &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>10 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMP = 0x10;
	/**
	 * <code>JMPEQ &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last compare
	 * operation compared two equal values</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if EQUAL</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>11 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPEQ = 0x11;
	/**
	 * <code>JMPNE &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last compare
	 * operation compared two different values</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if EQUAL</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>12 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPNE = 0x12;
	/**
	 * <code>JMPGT &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last compare
	 * result was greater</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if GREATHER</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>13 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPGT = 0x13;
	/**
	 * <code>JMPGE &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last compare
	 * result was not lower</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if GREATHER | EQUAL</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>14 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPGE = 0x14;
	/**
	 * <code>JMPLT &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last compare
	 * result was lower</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if LOWER</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>15 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPLT = 0x15;
	/**
	 * <code>JMPLE &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last compare
	 * result was not greater</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if LOWER | EQUAL</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>16 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPLE = 0x16;
	/**
	 * <code>JMPCS &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last carry
	 * flag is set</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if CARRY</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>17 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPCS = 0x17;
	/**
	 * <code>JMPCC &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last carry
	 * flag is cleared</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if CARRY</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>18 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPCC = 0x18;
	/**
	 * <code>JMPZS &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last zero flag
	 * is set</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ZERO</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>19 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPZS = 0x19;
	/**
	 * <code>JMPZC &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last zero flag
	 * is cleared</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ! ZERO</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>1A 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPZC = 0x1A;
	/**
	 * <code>JMPNAN &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last NaN flag
	 * is set</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if NAN</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>1B 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPNAN = 0x1B;
	/**
	 * <code>JMPAN &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last NaN flag
	 * is cleared</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ! NAN</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>1C 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPAN = 0x1C;
	/**
	 * <code>JMPAB &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last AllBits
	 * flag is set</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ALL_BITS</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>1D 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPAB = 0x1D;
	/**
	 * <code>JMPSB &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last SomeBits
	 * flag is set</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if SOME_BITS</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>1D 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPSB = 0x1E;
	/**
	 * <code>JMPSB &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the command after the label if the last NoneBits
	 * flag is set</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if NONE_BITS</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>note that all jumps and calls are relative, so it does not matter if the code was loaded to
	 * the memory address 0 or not</li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>1D 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int JMPNB = 0x1F;
	
	/**
	 * <code>CALL &lt;LABEL&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the label</li>
	 * <li>and pushes the current instruction pointer to the stack</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>[SP] &lt;- IP</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- IP + RELATIVE_LABEL</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>20 00 00 00 00 00 00 00</code></li>
	 * <li><code>&lt;RELATIVE_LABEL&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int CALL = 0x20;
	/**
	 * <code>CMP &lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>compares the two values and stores the result in the status register</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code></li>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul>
	 * </ul>
	 * <li><code>else if p1 &lt; p2</code></li>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>21 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int CMP = 0x21;
	/**
	 * <code>RET</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to the position which was secured in the stack</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>SP &lt;- SP - 8</code></li>
	 * <li><code>IP &lt;- [SP]</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>22 00 00 00 00 00 00 00</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int RET = 0x22;
	/**
	 * <code>INT &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>calls the interrupt specified by the parameter</li>
	 * <li>default interrupts may get called with a different routine</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>ZW &lt;- MEM-ALLOC{size=128}</code></li>
	 * <li><code>[ZW]       &lt;- IP</code></li>
	 * <li><code>[ZW + 8]   &lt;- SP</code></li>
	 * <li><code>[ZW + 16]  &lt;- STATUS</code></li>
	 * <li><code>[ZW + 24]  &lt;- INTCNT</code></li>
	 * <li><code>[ZW + 32]  &lt;- INTP</code></li>
	 * <li><code>[ZW + 40]  &lt;- FS_LOCK</code></li>
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
	 * <li><code>IP         &lt;- [INTP + (p1 * 8)]</code></li>
	 * </ul>
	 * <li>an interrupt can be overwritten:</li>
	 * <ul>
	 * <li>the interrupt-table is saved in the <code>INTP</code> register</li>
	 * <li>to overwrite the interrupt <code>N</code>, write to <code>(INTP + (N * 8))</code> the
	 * absolute position of the address</li>
	 * <li>example:</li>
	 * <ul>
	 * <li><code>PUSH X00</code> |&gt; only needed when the value of <code>X00</code> should not be
	 * overwritten</li>
	 * <li><code>LEA [INTP + OVERWRITE_INT_NUM_MULTIPLIED_WITH_8], RELATIVE_POS_FROM_GET_TO_INTERRUPT</code></li>
	 * <li><code>POP X00</code> |&gt; only needed when the value of <code>X00</code> should not be
	 * overwritten</li>
	 * </ul>
	 * </ul>
	 * <li>negative interrupts will always cause the illegal interrupt to be called instead</li>
	 * <li>when <code>INTCNT</code> is greater then the number of default interrupts and the called
	 * interrupt is not overwritten, the illegal interrupt will be called instead</li>
	 * <li>default interrupts:</li>
	 * <ul>
	 * <li><code>0</code>: illegal interrupt</li>
	 * <ul>
	 * <li><code>X00</code> contains the number of the illegal interrupt</li>
	 * <ul>
	 * <li>exits with <code>(128 + illegal_interrup_number)</code> (without calling the exit
	 * interrupt)</li>
	 * <li>if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the
	 * program exits with <code>128</code></li>
	 * </ul>
	 * </ul>
	 * <li><code>1</code>: unknown command</li>
	 * <ul>
	 * <li>exits with <code>7</code> (without calling the exit interrupt)</li>
	 * </ul>
	 * <li><code>2</code>: illegal memory</li>
	 * <ul>
	 * <li>exits with <code>6</code> (without calling the exit interrupt)</li>
	 * </ul>
	 * <li><code>3</code>: arithmetic error</li>
	 * <ul>
	 * <li>exits with <code>5</code> (without calling the exit interrupt)</li>
	 * </ul>
	 * <li><code>4</code>: exit</li>
	 * <ul>
	 * <li>use <code>X00</code> to specify the exit number of the progress</li>
	 * </ul>
	 * <li><code>5</code>: allocate a memory-block</li>
	 * <ul>
	 * <li><code>X00</code> saves the size of the block</li>
	 * <li>if the value of <code>X00</code> is <code>-1</code> after the call the memory-block could not
	 * be allocated</li>
	 * <li>if the value of <code>X00</code> is not <code>-1</code>, <code>X00</code> points to the first
	 * element of the allocated memory-block</li>
	 * </ul>
	 * <li><code>6</code>: reallocate a memory-block</li>
	 * <ul>
	 * <li><code>X00</code> points to the memory-block</li>
	 * <li><code>X01</code> is set to the new size of the memory-block</li>
	 * <li><code>X01</code> will be <code>-1</code> if the memory-block could not be reallocated, the
	 * old memory-block will remain valid and should be freed if it is not longer needed</li>
	 * <li><code>X01</code> will point to the new memory block, the old memory-block was automatically
	 * freed, so it should not be used, the new block should be freed if it is not longer needed</li>
	 * </ul>
	 * <li><code>7</code>: free a memory-block</li>
	 * <ul>
	 * <li><code>X00</code> points to the old memory-block</li>
	 * <li>after this the memory-block should not be used</li>
	 * </ul>
	 * <li><code>8</code>: open new stream</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer to the STRING, which refers to the file which should be
	 * read</li>
	 * <li><code>X01</code> specifies the open mode: (bitwise flags)</li>
	 * <ul>
	 * <li><code>UHEX-0000000000000001</code> : <code>OPEN_READ</code> open file for read access</li>
	 * <li><code>UHEX-0000000000000002</code> : <code>OPEN_WRITE</code> open file for write access</li>
	 * <li><code>UHEX-0000000000000004</code> : <code>OPEN_APPEND</code> open file for append access
	 * (implicit set of <code>OPEN_WRITE</code>)</li>
	 * <li><code>UHEX-0000000000000008</code> : <code>OPEN_CREATE</code> open file or create file (needs
	 * <code>OPEN_WRITE</code>, not compatible with <code>OPEN_NEW_FILE</code>)</li>
	 * <li><code>UHEX-0000000000000010</code> : <code>OPEN_NEW_FILE</code> fail if file already exists
	 * or create the file (needs <code>OPEN_WRITE</code>, not compatible with <code>OPEN_CREATE</code>
	 * and <code>OPEN_TRUNCATE</code>)</li>
	 * <li><code>UHEX-0000000000000020</code> : <code>OPEN_TRUNCATE</code> if the file already exists,
	 * remove its content (needs <code>OPEN_WRITE</code>, not compatible with
	 * <code>OPEN_NEW_FILE</code>)</li>
	 * <li>other flags will be ignored</li>
	 * <li>the operation will fail if it is not specified if the file should be opened for read, write
	 * and/or append</li>
	 * </ul>
	 * <li>opens a new stream to the specified file</li>
	 * <li>if successfully the STREAM-ID will be saved in the <code>X00</code> register</li>
	 * <li>if failed <code>X00</code> will contain <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed
	 * because the element is not of the correct type (file expected, but folder)</li>
	 * <ul>
	 * <li>if the element already exists, but is a folder and no file</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists, but <code>OPEN_CREATE</code> and <code>OPEN_NEW_FILE</code>
	 * are not set</li>
	 * </ul>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: operation
	 * failed because the element already existed</li>
	 * <ul>
	 * <li>if the element already exists, but <code>OPEN_NEW_FILE</code> is set</li>
	 * </ul>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed
	 * because there was not enough space in the file system</li>
	 * <ul>
	 * <li>if the system tried to create the new file, but there was not enough space for the new
	 * file-system-entry</li>
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: was denied because of
	 * read-only</li>
	 * <ul>
	 * <li>if the file is marked as read-only, but it was tried to open the file for read or append
	 * access</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the file is locked with <code>LOCK_NO_READ_ALLOWED</code> :
	 * <code>UHEX-0000000100000000</code> and it was tried to open the file for read access</li>
	 * <li>or if the file is locked with <code>LOCK_NO_WRITE_ALLOWED_LOCK</code> :
	 * <code>UHEX-0000000200000000</code> and it was tried to open the file for write/append access</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: STREAM-ID is invalid or
	 * <code>X01</code> contains an invalid open mode</li>
	 * <ul>
	 * <li>if the open mode was invalid</li>
	 * <ul>
	 * <li><code>OPEN_CREATE</code> or <code>OPEN_TRUNCATE</code> with <code>OPEN_NEW_FILE</code></li>
	 * <ul>
	 * <li>not <code>OPEN_READ</code> and not <code>OPEN_WRITE</code> and not
	 * <code>OPEN_APPEND</code></li>
	 * <li><code>OPEN_CREATE</code>, <code>OPEN_NEW_FILE</code> and/or <code>OPEN_TRUNCATE</code>
	 * without <code>OPEN_WRITE</code> (and without <code>OPEN_APPEND</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: operation failed
	 * because the system could not allocate enough memory</li>
	 * <ul>
	 * <li>the system tries to allocate some memory but was not able to allocate the needed memory</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>to close the stream call the free interrupt ( <code>7</code> :
	 * <code>INT_MEMORY_FREE</code>)</li>
	 * </ul>
	 * <li><code>9</code>: write</li>
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to write</li>
	 * <li><code>X02</code> points to the elements to write</li>
	 * <li><code>X01</code> will be set to <code>-1</code> if an error occurred</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed
	 * because there was not enough space in the file system</li>
	 * <ul>
	 * <li>if the system tried to allocate more space for either the file-system-entry of the open file
	 * or its content, but there was not enough space</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: was denied because of
	 * read-only</li>
	 * <ul>
	 * <li>if the file is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the file is locked with <code>LOCK_NO_WRITE_ALLOWED_LOCK</code> :
	 * <code>UHEX-0000000200000000</code></li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: STREAM-ID is invalid,
	 * the stream does not support write operations or <code>X01</code> is negative</li>
	 * <ul>
	 * <li>if the STREAM-ID is invalid (maybe because the corresponding file was deleted)</li>
	 * <li>or if a negative number of bytes should be written</li>
	 * <li>or if the stream does not support write operations</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>10</code>: read</li>
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to read</li>
	 * <li><code>X02</code> points to the elements to read</li>
	 * <li>after execution <code>X01</code> will contain the number of elements, which has been
	 * read</li>
	 * <li>if an error occurred <code>X01</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element was deleted</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the file is locked with <code>LOCK_NO_READ_ALLOWED</code> :
	 * <code>UHEX-0000000100000000</code></li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: STREAM-ID is invalid or
	 * <code>X01</code> is negative</li>
	 * <ul>
	 * <li>if the STREAM-ID is invalid (maybe because the corresponding file was deleted)</li>
	 * <li>or if a negative number of bytes should be written</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>if <code>X01</code> is <code>0</code> and was set before to a value greater <code>0</code>
	 * then the stream has reached its end</li>
	 * <li>reading less bytes than expected does not mead that the stream has reached it's end</li>
	 * </ul>
	 * <li><code>11</code>: get fs-file</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the file</li>
	 * <li><code>X00</code> will point to a fs-element of the file</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed
	 * because the element is not of the correct type (file expected, but folder)</li>
	 * <ul>
	 * <li>if the element exists, but is a folder and no file</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the one of the parents is locked with read forbidden ( <code>LOCK_NO_READ_ALLOWED</code> :
	 * <code>UHEX-0000000100000000</code>)</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory
	 * could be allocated</li>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>if the specified element is a link to a file, the target file of the link is returned instead
	 * of the actual link</li>
	 * </ul>
	 * <li><code>12</code>: get fs-folder</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the dictionary</li>
	 * <li><code>X00</code> will point to a fs-element of the folder</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed
	 * because the element is not of the correct type (folder expected, but file)</li>
	 * <ul>
	 * <li>if the element exists, but is a file and no folder</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory
	 * could be allocated</li>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>if the specified element is a link to a folder, the target folder of the link is returned
	 * instead of the actual link</li>
	 * </ul>
	 * <li><code>13</code>: get fs-link</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the link</li>
	 * <li><code>X00</code> will point to a fs-element of the link</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed
	 * because the element is not of the correct type (link expected, but file or folder)</li>
	 * <ul>
	 * <li>if the element exists, but is a file and no folder</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory
	 * could be allocated</li>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>14</code>: get fs-element</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the element</li>
	 * <li><code>X00</code> will point to the fs-element</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory
	 * could be allocated</li>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>if the specified element is a link the actual link is returned</li>
	 * </ul>
	 * <li><code>15</code>: duplicate fs-element (handle)</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X00</code> will point to a duplicate of the same element</li>
	 * <li>if the system could not allocate enough memory for the duplicate <code>X00</code> will be set
	 * to <code>-1</code></li>
	 * </ul>
	 * <li><code>16</code>: get parent</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the ID of the
	 * parent folder</li>
	 * <ul>
	 * <li>note that the only negative ID is <code>-2</code> (root folder)</li>
	 * <li>all other IDs are <code>0</code> or positive, but not all positive numbers are valid IDs</li>
	 * </ul>
	 * <li><code>[X00 + 8]</code> : <code>[X00 + FS_ELEMENT_OFFSET_LOCK]</code> will be set
	 * <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <li>on success <code>X01</code> will be set to <code>1</code></li>
	 * <li>on failure <code>X01</code> will be set to <code>0</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> is a
	 * fs-element of the root folder or contains itself an invalid ID</li>
	 * <ul>
	 * <li>if the given fs-element is the root folder</li>
	 * <li>or if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>17</code>: fs-element from ID</li>
	 * <ul>
	 * <li><code>X00</code> is set to the ID of the element</li>
	 * <li><code>X01</code> will be set to a fs-element of the element with the given id</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> is
	 * invalid ID</li>
	 * <ul>
	 * <li>if the given ID is invalid</li>
	 * <ul>
	 * <li>all negative IDs except of <code>-2</code> are invalid (the root folder has the ID
	 * <code>-2</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>18</code>: get create date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the create time of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>19</code>: get last mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the last modify time of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>20</code>: get last meta mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the last meta mod time of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>21</code>: set create date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the new create date</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> :
	 * <code>UHEX-0000000800000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>22</code>: set last mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the new last mod date</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> :
	 * <code>UHEX-0000000800000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>23</code>: set last meta mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the new last meta mod date</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> :
	 * <code>UHEX-0000000800000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>note: when changing all dates change this date at last, because it will bee automatically
	 * changed on meta changes like the change of the create or last mod date</li>
	 * </ul>
	 * <li><code>24</code>: get lock data</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X00</code> will be set to the lock data of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>25</code>: get lock date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the lock date of the element or <code>-1</code> if the
	 * element is not locked</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>26</code>: lock element</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> is set to the lock data of the new lock</li>
	 * <li><code>[X00 + 8]</code> : <code>[X00 + FS_ELEMENT_OFFSET_LOCK]</code> will be set to the new
	 * lock</li>
	 * <li>if the element is already exclusively locked the operation will fail</li>
	 * <li>if the element is locked with a shared lock and the lock data of the given lock is the same
	 * to the lock data of the current lock:</li>
	 * <ul>
	 * <li>a shared lock is flagged with <code>UHEX-4000000000000000</code> :
	 * <code>LOCK_SHARED_LOCK</code></li>
	 * <li>the new lock will not contain the shared lock counter</li>
	 * <li>the lock should be released like a exclusive lock, when it is no longer needed</li>
	 * <li>a shared lock does not give you any permissions, it just blocks operations for all (also for
	 * those with the lock)</li>
	 * </ul>
	 * <li>if the given lock is not flagged with <code>UHEX-8000000000000000</code> :
	 * <code>LOCK_LOCKED_LOCK</code>, it will be automatically be flagged with
	 * <code>UHEX-8000000000000000</code>:
	 * <code>LOCK_LOCKED_LOCK</code></li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is already locked</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or <code>X01</code> not only lock data bits</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the given lock does not only specify the lock data</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>27</code>: unlock element</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>[X00 + 8]</code> : <code>[X00 + FS_ELEMENT_OFFSET_LOCK]</code> will be set to
	 * <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <li>if the element is not locked with the given lock the operation will fail</li>
	 * <ul>
	 * <li>if the given lock is <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code>, the
	 * operation will always try to remove the lock of the element</li>
	 * </ul>
	 * <li>if the element is locked with a shared lock:</li>
	 * <ul>
	 * <li>if this is the last lock, the shared lock will be removed</li>
	 * <li>else the shared lock counter will be decremented</li>
	 * </ul>
	 * <li><code>X01</code> will be set to <code>1</code> on success</li>
	 * <li><code>X01</code> will be set to <code>0</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>28</code>: delete element</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the lock of the parent element or
	 * <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <li>deletes the element from the file system</li>
	 * <li>releases also the fs-element</li>
	 * <ul>
	 * <li>to release a fs-element (handle) normally just use the free interrupt ( <code>7</code> :
	 * <code>INT_MEMORY_FREE</code>)</li>
	 * </ul>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed
	 * bcause the there could not be allocated enough space</li>
	 * <ul>
	 * <li>the file system was not able to resize the file system entry to a smaller size</li>
	 * <ul>
	 * <li>the block intern table sometimes grow when a area is released</li>
	 * <ul>
	 * <li>if the block intern table can not grow this error occurres</li>
	 * </ul>
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied
	 * because of read-only</li>
	 * <ul>
	 * <li>if the element or its parent is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element or its parent is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>29</code>: move element</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> points to the new parent fs-element folder</li>
	 * <ul>
	 * <li>or <code>-1</code> if the parent folder should remain unchanged</li>
	 * </ul>
	 * <li><code>X02</code> points to the STRING name of the element</li>
	 * <ul>
	 * <li>or <code>-1</code> if the name should remain unchanged</li>
	 * </ul>
	 * <li><code>X03</code> contains the lock of the old parent folder or
	 * <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <ul>
	 * <li>this value is ignored if <code>X01</code> is set to <code>-1</code> (the parent folder is not
	 * set)</li>
	 * </ul>
	 * <li>moves the element to a new parent folder and sets its name</li>
	 * <ul>
	 * <li>note that both operations (set parent folder and set name) are optional</li>
	 * </ul>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed
	 * because the parent is not of the correct type (folder expected, but file)</li>
	 * <ul>
	 * <li>if the parent element exists, but is a file and no folder</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied
	 * because of read-only</li>
	 * <ul>
	 * <li>if the element, its (old) parent or its (not) new parent is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element, its (old) parent or its (not) new parent is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>30</code>: get element flags</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the flags of the element</li>
	 * <li><code>X01</code> will be set to <code>0</code> if an error occurred</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>note that links are also flagged as folder or file</li>
	 * <ul>
	 * <li>if the link target element is a file, the link is also flagged as file</li>
	 * <li>if the link target element is a folder, the link is also flagged as flder</li>
	 * <li>a link to a link is invalid</li>
	 * </ul>
	 * </ul>
	 * <li><code>31</code>: modify element flags</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the flags to add to the element</li>
	 * <ul>
	 * <li>only the 32 low bits are used and the 32 high bits are ignored</li>
	 * </ul>
	 * <li><code>X02</code> contains the flags to remove from the element</li>
	 * <ul>
	 * <li>only the 32 low bits are used and the 32 high bits are ignored</li>
	 * </ul>
	 * <li>note that the flags wich specify if the element is a folder, file or link are not allowed to
	 * be set/removed</li>
	 * <li>on error <code>X01</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or invalid flag modify</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <ul>
	 * <li>or if the flags to add or to remove contain the bits:</li>
	 * <ul>
	 * <li><code>HEX-00000001</code> : <code>FLAG_FOLDER</code></li>
	 * <ul>
	 * <li><code>HEX-00000002</code> <code>:</code>FLAG_FILE`</li>
	 * <ul>
	 * <li><code>HEX-00000004</code> : <code>FLAG_LINK</code></li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>bits out of the low 32-bit range will be ignored</li>
	 * </ul>
	 * <li><code>32</code>: get folder child element count</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> will be set to the child element count of the given folder</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>33</code>: get child element from index</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> contains the index of the child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the
	 * child element</li>
	 * <li><code>[X00 + 8]</code> : <code>[X00 + FS_ELEMENT_OFFSET_LOCK]</code> will be set to
	 * <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the index is out of range (negative or greater or equal to the child element count of
	 * the given folder)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>34</code>: get child element from name</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the
	 * child element</li>
	 * <li><code>[X00 + 8]</code> : <code>[X00 + FS_ELEMENT_OFFSET_LOCK]</code> will be set to
	 * <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: the folder does
	 * not contain a child with the given name</li>
	 * <ul>
	 * <li>if there is no child with the given name</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>35</code>: add folder</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the
	 * new child element folder</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder
	 * already contain a child with the given name</li>
	 * <ul>
	 * <li>if there is already child with the given name</li>
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was
	 * denied because read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>the folder will automatically be unflagged from the sorted flag ( <code>HEX-00000040</code> :
	 * <code>FLAG_FOLDER_SORTED</code>)</li>
	 * </ul>
	 * <li><code>36</code>: add file</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the
	 * new child element file</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder
	 * already contain a child with the given name</li>
	 * <ul>
	 * <li>if there is already child with the given name</li>
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was
	 * denied because read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>the folder will automatically be unflagged from the sorted flag ( <code>HEX-00000040</code> :
	 * <code>FLAG_FOLDER_SORTED</code>)</li>
	 * </ul>
	 * <li><code>37</code>: add link</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>X02</code> points to the fs-element of the target element</li>
	 * <ul>
	 * <li>the target element is not allowed to be a link</li>
	 * </ul>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the
	 * new child element link</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder
	 * already contain a child with the given name</li>
	 * <ul>
	 * <li>if there is already child with the given name</li>
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was
	 * denied because read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>the folder will automatically be unflagged from the sorted flag ( <code>HEX-00000040</code> :
	 * <code>FLAG_FOLDER_SORTED</code>)</li>
	 * </ul>
	 * <li><code>38</code>: file length</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> will be set to the length of the file in bytes</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>19</code>: file hash</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> points to a at least 32-byte large memory block (256-bits : 32-bytes)</li>
	 * <ul>
	 * <li>the memory block from <code>X01</code> will be filled with the SHA-256 hash code of the
	 * file</li>
	 * </ul>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>40</code>: file read</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the number of bytes to read</li>
	 * <li><code>X01</code> points to a memory block to which the file data should be filled</li>
	 * <li><code>X03</code> contains the offset from the file</li>
	 * <li><code>X02</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or the offset / read count is invalid</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the read count or file offset is negative</li>
	 * <li>or if the read count + file offset is larger than the file length</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>41</code>: file write</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the number of bytes to write</li>
	 * <li><code>X02</code> points to the memory block with the data to write</li>
	 * <li><code>X03</code> contains the offset from the file</li>
	 * <li><code>X02</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was
	 * denied because read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or the offset / read count is invalid</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the write count or file offset is negative</li>
	 * <li>or if the write count + file offset is larger than the file length</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>42</code>: file append</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the number of bytes to append</li>
	 * <li><code>X02</code> points to the memory block with the data to write</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed
	 * bcause the there could not be allocated enough space for the larger file</li>
	 * <ul>
	 * <li>the file system could either not allocate enough blocks for the new larger file</li>
	 * <li>or the file system could not allocate enough space for the larger file system entry of the
	 * file</li>
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was
	 * denied because read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or the offset / read count is invalid</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the write count or file offset is negative</li>
	 * <li>or if the write count + file offset is larger than the file length</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>43</code>: file truncate</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the new length of the file</li>
	 * <li>removes all data from the file which is behind the new length</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed
	 * bcause the there could not be allocated enough space</li>
	 * <ul>
	 * <li>the file system was not able to resize the file system entry to a smaller size</li>
	 * <ul>
	 * <li>the block intern table sometimes grow when a area is released</li>
	 * <ul>
	 * <li>if the block intern table can not grow this error occurres</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was
	 * denied because read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or the offset / read count is invalid</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the new length is larger than the current file length</li>
	 * <li>or if the new length is negative</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>44</code>: link get target</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element link</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the target
	 * ID</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no link</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or the offset / read count is invalid</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>45</code>: link set target</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element link</li>
	 * <li><code>X01</code> points to the new target element</li>
	 * <li>sets the target element of the link</li>
	 * <ul>
	 * <li>also flags the link with file or folder and removes the other flag (
	 * <code>HEX-00000001</code> : <code>FLAG_FOLDER</code> or <code>HEX-00000002</code> :
	 * <code>FLAG_FILE</code>)</li>
	 * </ul>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no link</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was
	 * denied because read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code>
	 * contains an invalid ID or the offset / read count is invalid</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>46</code>: lock file-system</li>
	 * <ul>
	 * <li><code>X00</code> contains the new lock data</li>
	 * <li>the lock is like a lock for elements, but it works for all elements</li>
	 * <li>if the file system is already exclusively locked the operation will fail</li>
	 * <li>if the file system is locked with a shared lock and the lock data of the given lock is the
	 * same to the lock data of the current lock:</li>
	 * <ul>
	 * <li>a shared lock is flagged with <code>UHEX-4000000000000000</code> :
	 * <code>LOCK_SHARED_LOCK</code></li>
	 * <li>the new lock will not contain the shared lock counter</li>
	 * <li>the lock should be released like a exclusive lock, when it is no longer needed</li>
	 * <li>a shared lock does not give you any permissions, it just blocks operations for all (also for
	 * those with the lock)</li>
	 * </ul>
	 * <li>if the given lock is not flagged with <code>UHEX-8000000000000000</code> :
	 * <code>LOCK_LOCKED_LOCK</code>, it will be automatically be flagged with
	 * <code>UHEX-8000000000000000</code>:
	 * <code>LOCK_LOCKED_LOCK</code></li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the file syste, is already locked</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> does
	 * not only contain lock data bits</li>
	 * <ul>
	 * <li>if the given lock does not only specify the lock data</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>the lock of the file system will be remembered in the <code>FS_LOCK</code> register</li>
	 * </ul>
	 * <li><code>47</code>: unlock file-system</li>
	 * <ul>
	 * <li>if the file system is not locked with the given lock the operation will fail</li>
	 * <ul>
	 * <li>if the <code>FS_LOCK</code> is <code>UHEX-0000000000000000</code> :
	 * <code>LOCK_NO_LOCK</code>, the operation will always try to remove the lock of the element</li>
	 * </ul>
	 * <li>if the file system is locked with a shared lock:</li>
	 * <ul>
	 * <li>if this is the last lock, the shared lock will be removed</li>
	 * <li>else the shared lock counter will be decremented</li>
	 * </ul>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the file system is locked with a different lock or not locked at all</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>48</code>: to get the time in milliseconds</li>
	 * <ul>
	 * <li><code>X00</code> will contain the time in milliseconds or <code>-1</code> if not
	 * available</li>
	 * </ul>
	 * <li><code>49</code>: to wait the given time in nanoseconds</li>
	 * <ul>
	 * <li><code>X00</code> contain the number of nanoseconds to wait (only values from <code>0</code>
	 * to <code>999999999</code> are allowed)</li>
	 * <li><code>X01</code> contain the number of seconds to wait</li>
	 * <li><code>X00</code> and <code>X01</code> will contain the remaining time (both <code>0</code> if
	 * it finished waiting)</li>
	 * <li><code>X02</code> will be <code>1</code> if the call was successfully and <code>0</code> if
	 * something went wrong</li>
	 * <ul>
	 * <li>if <code>X02</code> is <code>1</code> the remaining time will always be <code>0</code></li>
	 * <li>if <code>X02</code> is <code>0</code> the remaining time will be greater <code>0</code></li>
	 * </ul>
	 * <li><code>X00</code> will not be negative if the progress waited too long</li>
	 * </ul>
	 * <li><code>50</code>: random</li>
	 * <ul>
	 * <li><code>X00</code> will be filled with random bits</li>
	 * </ul>
	 * <li><code>51</code>: memory copy</li>
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function has undefined behavior if the two blocks overlap</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</li>
	 * </ul>
	 * <li><code>52</code>: memory move</li>
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function makes sure, that the original values of the source block are copied to the
	 * target block (even if the two block overlap)</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</li>
	 * </ul>
	 * <li><code>53</code>: memory byte set</li>
	 * <ul>
	 * <li>sets a memory block to the given byte-value</li>
	 * <li><code>X00</code> points to the block</li>
	 * <li><code>X01</code> the first byte contains the value to be written to each byte</li>
	 * <li><code>X02</code> contains the length in bytes</li>
	 * </ul>
	 * <li><code>54</code>: memory set</li>
	 * <ul>
	 * <li>sets a memory block to the given int64-value</li>
	 * <li><code>X00</code> points to the block</li>
	 * <li><code>X01</code> contains the value to be written to each element</li>
	 * <li><code>X02</code> contains the count of elements to be set</li>
	 * </ul>
	 * <li><code>55</code>: string length</li>
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00</code> will be set to the length of the string/ the (byte-)offset of the first byte
	 * from the <code>'\0'</code> character</li>
	 * </ul>
	 * <li><code>56</code>: string compare</li>
	 * <ul>
	 * <li><code>X00</code> points to the first STRING</li>
	 * <li><code>X01</code> points to the second STRING</li>
	 * <li><code>X00</code> will be set to zero if both are equal STRINGs, a value greater zero if the
	 * first is greater and below zero if the second is greater</li>
	 * <ul>
	 * <li>a STRING is greater if the first mismatching char has numeric greater value</li>
	 * </ul>
	 * </ul>
	 * <li><code>57</code>: number to string</li>
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert</li>
	 * <li><code>X01</code> is points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> contains the base of the number system</li>
	 * <ul>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></li>
	 * <li>other values lead to undefined behavior</li>
	 * </ul>
	 * <li><code>X03</code> is set to the length of the buffer</li>
	 * <ul>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</li>
	 * </ul>
	 * <li><code>X00</code> will be set to the length of the STRING</li>
	 * <li><code>X03</code> will be set to the new length of the buffer</li>
	 * <ul>
	 * <li>the new length will be the old length or if the old length is smaller than the length of the
	 * STRING (with <code>\0</code>) than the length of the STRING (with <code>\0</code>)</li>
	 * </ul>
	 * <li>on error <code>X01</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X02</code> is an
	 * invalid number system or an invalid buffer size</li>
	 * <ul>
	 * <li>if the given number system is smaller than <code>2</code> or larger than <code>36</code></li>
	 * <ul>
	 * <li>or if the buffer size is negative</li>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: operation failed
	 * because the system could not allocate enough memory</li>
	 * <ul>
	 * <li>the system tries to allocate some memory but was not able to allocate the needed memory</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>58</code>: floating point number to string</li>
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert</li>
	 * <li><code>X01</code> points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> is set to the current size of the buffer</li>
	 * <ul>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</li>
	 * </ul>
	 * <li>on error <code>X01</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X02</code> is an
	 * invalid number system</li>
	 * <ul>
	 * <li>if the buffer size is negative</li>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: operation failed
	 * because the system could not allocate enough memory</li>
	 * <ul>
	 * <li>the system tries to allocate some memory but was not able to allocate the needed memory</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>59</code>: string to number</li>
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X01</code> points to the base of the number system</li>
	 * <ul>
	 * <li>(for example <code>10</code> for the decimal system or <code>2</code> for the binary
	 * system)</li>
	 * </ul>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * <li>this function will ignore leading and following white-space characters</li>
	 * <li>on success <code>X01</code> will be set to <code>1</code></li>
	 * <li>on error <code>X01</code> will be set to <code>0</code></li>
	 * <ul>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</li>
	 * </ul>
	 * </ul>
	 * <li><code>60</code>: string to floating point number</li>
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * <li>if the STRING contains illegal characters or the base is not valid, the behavior is
	 * undefined</li>
	 * <li>this function will ignore leading and following white-space characters</li>
	 * <li>on success <code>X01</code> will be set to <code>1</code></li>
	 * <li>on error <code>X01</code> will be set to <code>0</code></li>
	 * <ul>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</li>
	 * </ul>
	 * </ul>
	 * <li><code>61</code>: format string</li>
	 * <ul>
	 * <li><code>X00</code> is set to the STRING input</li>
	 * <li><code>X01</code> contains the buffer for the STRING output</li>
	 * <li><code>X02</code> is the current size of the buffer in bytes</li>
	 * <li>the register <code>X03..XNN</code> are for the formatting arguments</li>
	 * <ul>
	 * <li>if there are mor arguments used then there are registers the behavior is undefined.</li>
	 * <ul>
	 * <li>that leads to a maximum of 248 arguments</li>
	 * </ul>
	 * </ul>
	 * <li><code>X00</code> will be set to the length of the output string</li>
	 * <li><code>X01</code> will be set to the output buffer</li>
	 * <li><code>X02</code> will be set to the new buffer size in bytes</li>
	 * <li>if an error occurred <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: operation failed
	 * because if invalid arguments</li>
	 * <ul>
	 * <li>if the last charactet of the input string is a <code>%</code> character</li>
	 * <ul>
	 * <li>or if there are invalid formatting characters</li>
	 * <ul>
	 * <li>a <code>%</code> is not followed by a <code>%</code>, <code>s</code>, <code>c</code>,
	 * <code>B</code>, <code>d</code>, <code>f</code>, <code>p</code>, <code>h</code>, <code>b</code> or
	 * <code>o</code> character</li>
	 * <li>or if there are too many arguments needed</li>
	 * </ul>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: operation failed
	 * because the system could not allocate enough memory</li>
	 * <ul>
	 * <li>if the buffer could not be resized</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>X02</code> will be set to the current size of the buffer</li>
	 * </ul>
	 * <li>formatting:</li>
	 * <ul>
	 * <li><code>%%</code>: to escape an <code>%</code> character (only one <code>%</code> will be in
	 * the formatted STRING)</li>
	 * <li><code>%s</code>: the next argument points to a STRING, which should be inserted here</li>
	 * <li><code>%c</code>: the next argument starts with a UTF-16 character, which should be inserted
	 * here</li>
	 * <ul>
	 * <li>note that UTF-16 characters contain always two bytes</li>
	 * </ul>
	 * <li><code>%B</code>: the next argument starts with a byte, which should be inserted here (without
	 * being converted to a valid STRING character)</li>
	 * <ul>
	 * <li>note that if the argument starts with a zero byte an <code>\0</code> character will be
	 * inserted in the middle of the output STRING</li>
	 * </ul>
	 * <li><code>%d</code>: the next argument contains a number, which should be converted to a STRING
	 * using the decimal number system and than be inserted here</li>
	 * <li><code>%f</code>: the next argument contains a floating point number, which should be
	 * converted to a STRING and than be inserted here</li>
	 * <li><code>%p</code>: the next argument contains a pointer, which should be converted to a
	 * STRING</li>
	 * <ul>
	 * <li>if not the pointer will be converted by placing a <code>"p-"</code> and then the unsigned
	 * pointer-number converted to a STRING using the hexadecimal number system</li>
	 * <li>if the pointer is <code>-1</code> it will be converted to the STRING
	 * <code>"p-inval"</code></li>
	 * </ul>
	 * <li><code>%h</code>: the next argument contains a number, which should be converted to a STRING
	 * using the hexadecimal number system and than be inserted here</li>
	 * <li><code>%b</code>: the next argument contains a number, which should be converted to a STRING
	 * using the binary number system and than be inserted here</li>
	 * <li><code>%o</code>: the next argument contains a number, which should be converted to a STRING
	 * using the octal number system and than be inserted here</li>
	 * </ul>
	 * </ul>
	 * <li><code>62</code>: STRING to U8-STRING</li>
	 * <ul>
	 * <li><code>X00</code> contains the STRING</li>
	 * <li><code>X01</code> points to a buffer for the U8-SRING</li>
	 * <li><code>X02</code> is set to the size of the size of the buffer</li>
	 * <li><code>X01</code> will point to the U8-STRING</li>
	 * <li><code>X02</code> will be set to the U8-STRING buffer size</li>
	 * <li><code>X03</code> will point to the <code>\0</code> character of the U8-STRING</li>
	 * <li>on error <code>X03</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: operation failed
	 * because if invalid arguments</li>
	 * <ul>
	 * <li>if the old buffer length is negative</li>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: operation failed
	 * because the system could not allocate enough memory</li>
	 * <ul>
	 * <li>if the buffer could not be resized</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li><code>63</code>: U8-STRING to STRING</li>
	 * <ul>
	 * <li><code>X00</code> contains the U8-STRING</li>
	 * <li><code>X01</code> points to a buffer for the SRING</li>
	 * <li><code>X02</code> is set to the size of the size of the buffer</li>
	 * <li><code>X01</code> will point to the STRING</li>
	 * <li><code>X02</code> will be set to the STRING buffer size</li>
	 * <li><code>X03</code> will point to the <code>\0</code> character of the STRING</li>
	 * </ul>
	 * <li><code>64</code>: load file</li>
	 * <ul>
	 * <li><code>X00</code> is set to the path (inclusive name) of the file</li>
	 * <li><code>X00</code> will point to the memory block, in which the file has been loaded</li>
	 * <li><code>X01</code> will be set to the length of the file (and the memory block)</li>
	 * <li>when an error occurred <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element
	 * is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed
	 * because the element does not exist</li>
	 * <ul>
	 * <li>if the given file does not exists</li>
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied
	 * because of lock</li>
	 * <ul>
	 * <li>if the file system is locked with a different lock or not locked at all</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error
	 * occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: operation failed
	 * because the system could not allocate enough memory</li>
	 * <ul>
	 * <li>if the buffer could not be resized</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>23 &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int INT = 0x23;
	/**
	 * <code>PUSH &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>pushes the parameter to the stack</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>[SP] &lt;- p1</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>24 &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int PUSH = 0x24;
	/**
	 * <code>POP &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>pops the highest value from the stack to the parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>SP &lt;- SP - 8</code></li>
	 * <li><code>p1 &lt;- [SP]</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>25 &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int POP = 0x25;
	/**
	 * <code>IRET</code>
	 * </p>
	 * <ul>
	 * <li>returns from an interrupt</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>ZW      &lt;- X09</code></li>
	 * <li><code>IP      &lt;- [X09]</code></li>
	 * <li><code>SP      &lt;- [X09 + 8]</code></li>
	 * <li><code>STATUS  &lt;- [X09 + 16]</code></li>
	 * <li><code>INTCNT  &lt;- [X09 + 24]</code></li>
	 * <li><code>INTP    &lt;- [X09 + 32]</code></li>
	 * <li><code>FS_LOCK &lt;- [X09 + 40]</code></li>
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
	 * <li><code>FREE ZW</code></li>
	 * <ul>
	 * <li>this does not use the free interrupt, but works like the default free interrupt (without
	 * calling the interrupt (what could cause an infinite recursion))</li>
	 * </ul>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>`23 00 00 00 00 00 00 00</li>
	 * </ul>
	 * </ul>
	 */
	public static final int IRET = 0x26;
	/**
	 * <code>SWAP &lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>swaps the value of the first and the second parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>ZW &lt;- p1</code></li>
	 * <li><code>p1 &lt;- p2</code></li>
	 * <li><code>p2 &lt;- ZW</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>27 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int SWAP = 0x27;
	/**
	 * <code>LEA &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the first parameter of the value of the second parameter plus the instruction
	 * pointer</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p2 + IP</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>28 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int LEA = 0x28;
	/**
	 * <code>MVAD &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt; , &lt;CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>copies the value of the second parameter plus the third parameter to the first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p2 + p3</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>29 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * <li><code>&lt;P3.NUM_NUM&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int MVAD = 0x29;
	/**
	 * <code>CALO &lt;PARAM&gt;, &lt;LABEL/CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>sets the instruction pointer to position of the label</li>
	 * <li>and pushes the current instruction pointer to the stack</li>
	 * <ul>
	 * <li><code>[SP] &lt;- IP</code></li>
	 * <li><code>SP &lt;- SP + 8</code></li>
	 * <li><code>IP &lt;- p1 + p2</code></li>
	 * <ul>
	 * <li>the call will not be made relative from this position, so the label remains relative to the
	 * start of the file it is declared in</li>
	 * </ul>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>2A &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>&lt;P2.NUM_NUM&gt;</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int CALO = 0x2A;
	/**
	 * <code>BCP &lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>compares the two values on bit level</li>
	 * <li>definition</li>
	 * <ul>
	 * <li><code>if (p1 &amp; p2) = 0</code></li>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 0</code></li>
	 * <ul>
	 * <li><code>SOME_BITS &lt;- 0</code></li>
	 * <li><code>NONE_BITS &lt;- 1</code></li>
	 * </ul>
	 * </ul>
	 * <li>`else if (p1 &amp; p2) = p2</li>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 1</code></li>
	 * <li><code>SOME_BITS &lt;- 1</code></li>
	 * <li><code>NONE_BITS &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ALL_BITS &lt;- 0</code></li>
	 * <li><code>SOME_BITS &lt;- 1</code></li>
	 * <li><code>NONE_BITS &lt;- 0</code></li>
	 * </ul>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>2B &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int BCP = 0x2B;
	/**
	 * <code>CMPFP &lt;PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>compares the two floating point values and stores the result in the status register</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if p1 &gt; p2</code></li>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul>
	 * </ul>
	 * <li><code>else if p1 &lt; p2</code></li>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else if p1 = NaN | p2 = NaN</code></li>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 1</code></li>
	 * <li><code>EQUAL &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NaN &lt;- 0</code></li>
	 * <li><code>EQUAL &lt;- 1</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>2C &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int CMPFP = 0x2C;
	/**
	 * <code>CHKFP &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>checks if the floating point param is a positive, negative infinity, NaN or normal value</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if p1 is positive-infinity</code></li>
	 * <ul>
	 * <li><code>GREATHER &lt;- 1</code></li>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * </ul>
	 * <li><code>else if p1 is negative-infinity</code></li>
	 * <ul>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>LOWER &lt;- 1</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else if p1 is NaN</code></li>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 1</code></li>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>LOWER &lt;- 0</code></li>
	 * <li><code>GREATHER &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>2D &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int CHKFP = 0x2D;
	
	/**
	 * <code>ADDC &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>adds the values of both parameters and the carry flag and stores the sum in the first
	 * parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if ((p1 &gt; 0) &amp; ((p2 + CARRY) &gt; 0) &amp; ((p1 + p2 + CARRY) &lt; 0)) | ((p1 &lt; 0) &amp; ((p2 + CARRY) &lt; 0) &amp; ((p1 + (p2 + CARRY)) &gt; 0))</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 + (p2 + CARRY)</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>30 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int ADDC = 0x30;
	/**
	 * <code>SUBC &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>subtracts the second parameter with the carry flag from the first parameter and stores the
	 * result in the first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>if (p1 &gt; 0) &amp; ((p2 + CARRY) &lt; 0) &amp; ((p1 - (p2 + CARRY)) &lt; 0)) | ((p1 &lt; 0) &amp; (p2 &gt; 0) &amp; ((p1 - (p2 + CARRY)) &gt; 0))</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>CARRY &lt;- 0</code></li>
	 * </ul>
	 * <li><code>p1 &lt;- p1 - (p2 + CARRY)</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>31 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int SUBC = 0x31;
	/**
	 * <code>ADDFP &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>adds the floating point values of both parameters and stores the floating point sum in the
	 * first parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 fp-add p2</code></li>
	 * <li><code>if p1 = 0.0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * </ul>
	 * <li><code>else if p1 = NaN</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * <li><code>NAN &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>32 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int ADDFP = 0x32;
	/**
	 * <code>SUBFP &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the
	 * first fp-parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 fp-sub p2</code></li>
	 * <li><code>if p1 = 0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>if p1 = NaN</code></li>
	 * <ul>
	 * <li><code>NAN &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>NAN &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>33 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int SUBFP = 0x33;
	/**
	 * <code>MULFP &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>multiplies the first fp parameter with the second fp and stores the fp result in the first
	 * parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 fp-mul p2</code></li>
	 * <li><code>if p1 = 0.0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>if p1 = NaN</code></li>
	 * <ul>
	 * <li><code>NAN &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>NAN &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>34 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int MULFP = 0x34;
	/**
	 * <code>DIVFP &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>divides the first fp-parameter with the second fp and stores the fp-result in the first
	 * fp-parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 fp-div p2</code></li>
	 * <li><code>if p1 = 0.0</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>ZERO &lt;- 0</code></li>
	 * </ul>
	 * <li><code>if p1 = NaN</code></li>
	 * <ul>
	 * <li><code>NAN &lt;- 1</code></li>
	 * </ul>
	 * <li><code>else</code></li>
	 * <ul>
	 * <li><code>NAN &lt;- 0</code></li>
	 * </ul>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>35 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int DIVFP = 0x35;
	/**
	 * <code>NTFP &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>converts the value of the number param to a floating point</li>
	 * <li>the value after the</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- as_fp(p1)</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>36 &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int NTFP = 0x36;
	/**
	 * <code>FPTN &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>converts the value of the floating point param to a number</li>
	 * <li>the value after the</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- as_num(p1)</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li><code>37 &lt;B-P1.TYPE&gt; 00 00 00 00 &lt;B-P1.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|00&gt;</code></li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int FPTN = 0x37;
	/**
	 * <code>UDIV &lt;NO_CONST_PARAM&gt; , &lt;NO_CONST_PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>like DIV, but uses the parameters as unsigned parameters</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;- p1 udiv p2</code></li>
	 * <li><code>p2 &lt;- p1 umod p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>39 &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int UDIV = 0x38;
	/**
	 * <code>MVB &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>copies the byte value of the second parameter to the first byte parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;-8-bit- p2</code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>3A &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int MVB = 0x3A;
	/**
	 * <code>MVW &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>copies the word value of the second parameter to the first word parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;-16-bit- p2 </code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>3B &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int MVW = 0x3B;
	/**
	 * <code>MVDW &lt;NO_CONST_PARAM&gt; , &lt;PARAM&gt;</code>
	 * </p>
	 * <ul>
	 * <li>copies the double-word value of the second parameter to the first double-word parameter</li>
	 * <li>definition:</li>
	 * <ul>
	 * <li><code>p1 &lt;-32-bit- p2 </code></li>
	 * <li><code>IP &lt;- IP + CMD_LEN</code></li>
	 * </ul>
	 * <li>binary:</li>
	 * <ul>
	 * <li>
	 * <code>3C &lt;B-P1.TYPE&gt; &lt;B-P2.TYPE&gt; 00 &lt;B-P2.OFF_REG|00&gt; &lt;B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt; &lt;B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00&gt;</code>
	 * </li>
	 * <li><code>[P1.NUM_NUM]</code></li>
	 * <li><code>[P1.OFF_NUM]</code></li>
	 * <li><code>[P2.NUM_NUM]</code></li>
	 * <li><code>[P2.OFF_NUM]</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final int MVDW = 0x3C;
	
}
