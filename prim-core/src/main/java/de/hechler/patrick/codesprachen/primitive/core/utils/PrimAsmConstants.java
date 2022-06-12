package de.hechler.patrick.codesprachen.primitive.core.utils;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;

public class PrimAsmConstants {
	
	public static final Path START_CONSTANTS_PATH = Paths.get("[START_CONSTANTS]");
	
	public static final Map <String, PrimitiveConstant> START_CONSTANTS;
	
	static {
		Map <String, PrimitiveConstant> startConsts = new HashMap <>();
		int lineNum = 1;
		try (InputStream in = PrimAsmConstants.class.getResourceAsStream("/de/hechler/patrick/codesprachen/primitive/core/default-constants.psf")) {
			try (Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {
				StringBuilder build = new StringBuilder();
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.charAt(0) == '|') {
						build.append(line).append('\n');
					} else {
						int index = line.indexOf('=');
						String name = line.substring(0, index);
						String num = line.substring(index + 1);
						long val = Long.parseLong(num);
						PrimitiveConstant primConst = new PrimitiveConstant(name, build.toString(), val, START_CONSTANTS_PATH, lineNum);
						PrimitiveConstant old = startConsts.put(num, primConst);
						assert old == null;
					}
					lineNum ++ ;
				}
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
		START_CONSTANTS = Collections.unmodifiableMap(startConsts);
	}
	/**
	 * * `0`: illegal interrupt<br>
	 * * `X00` contains the number of the illegal interrupt<br>
	 * * calls the exit interrupt with `(64 + illegal_interrup_number)`<br>
	 * * if the forbidden interrupt is the exit input, the program exits with `(64 + 4) = 68`, but does not calls the exit interrupt to do so<br>
	 * * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `63`
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_ILLEGAL_INTERRUPT = 0L;
	/**
	 * * `1`: unknown command<br>
	 * * `X00` contains the illegal command<br>
	 * * calls the exit interrupt with `62`
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_UNKNOWN_COMMAND   = 1L;
	/**
	 * * `2`: illegal memory<br>
	 * * calls the exit interrupt with `61`
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_ILLEGAL_MEMORY    = 2L;
	/**
	 * * `3`: arithmetic error<br>
	 * * calls the exit interrupt with `60`
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_ARITHMETIC_ERROR  = 3L;
	
	/** the total number of default interrupts */
	public static final long INTERRUPT_COUNT = 65L;
	
	/**
	 * the offset of the fs-element file pointer of the stream this value is zero and can be ignored
	 */
	@PrimStartConstant
	public static final long FS_STREAM_OFFSET_FILE        = 0L;
	/**
	 * the offset of the current position of the stream
	 */
	@PrimStartConstant
	public static final long FS_STREAM_OFFSET_POS         = 8L;
	/**
	 * the offset of the ID of a fs-element
	 */
	@PrimStartConstant
	public static final long FS_ELEMENT_OFFSET_ID         = 0L;
	/**
	 * the offset of the lock to be used of a fs-element
	 */
	@PrimStartConstant
	public static final long FS_ELEMENT_OFFSET_LOCK       = 8L;
	/**
	 * the status flag used when on the last compare (CMP A, B) A was lower than B
	 */
	@PrimStartConstant
	public static final long STATUS_LOWER                 = 1L;
	/**
	 * the status flag used when on the last compare (CMP A, B) A was greater than B
	 */
	@PrimStartConstant
	public static final long STATUS_GREATHER              = 2L;
	/**
	 * the status flag used when on the last compare (CMP A, B) A was equal to B
	 */
	@PrimStartConstant
	public static final long STATUS_EQUAL                 = 4L;
	/**
	 * the status flag used when an overflow occurred
	 */
	@PrimStartConstant
	public static final long STATUS_CARRY                 = 8L;
	/**
	 * the status flag used when an mathematical (arithmetic or logic) operation had zero as result
	 */
	@PrimStartConstant
	public static final long STATUS_ZERO                  = 16L;
	/**
	 * the status flag used when an mathematical operation had a NaN as result
	 */
	@PrimStartConstant
	public static final long STATUS_NAN                   = 32L;
	/**
	 * the status flag used when on the last bit-compare (BCP A, B) A & B was B (and B != zero)
	 */
	@PrimStartConstant
	public static final long STATUS_ALL_BITS              = 64L;
	/**
	 * the status flag used when on the last bit-compare (BCP A, B) A & B was not zero
	 */
	@PrimStartConstant
	public static final long STATUS_SOME_BITS             = 128L;
	/**
	 * the status flag used when on the last bit-compare (BCP A, B) A & B was zero
	 */
	@PrimStartConstant
	public static final long STATUS_NONE_BITS             = 256L;
	/**
	 * the status flag indicates a invalid fs-element type (file expected but folder was used or in reverse)
	 */
	@PrimStartConstant
	public static final long STATUS_ELEMENT_WRONG_TYPE    = 18014398509481984L;
	/**
	 * the status flag indicates that a element was ought to exist but did not exist
	 */
	@PrimStartConstant
	public static final long STATUS_ELEMENT_NOT_EXIST     = 36028797018963968L;
	/**
	 * the status flag indicates that a element was not ought to exist but did already exist
	 */
	@PrimStartConstant
	public static final long STATUS_ELEMENT_ALREADY_EXIST = 72057594037927936L;
	/**
	 * the status flag indicates that the file system could not reserve enough space for some reason
	 */
	@PrimStartConstant
	public static final long STATUS_OUT_OF_SPACE          = 144115188075855872L;
	/**
	 * the status flag indicates that a element was marked as read only, but a write operation was tried
	 */
	@PrimStartConstant
	public static final long STATUS_READ_ONLY             = 288230376151711744L;
	/**
	 * the status flag indicates that a different lock was used or the lock of the element forbid the tried operation
	 */
	@PrimStartConstant
	public static final long STATUS_ELEMENT_LOCKED        = 576460752303423488L;
	/**
	 * the status flag indicates that an undefined IO error occurred
	 */
	@PrimStartConstant
	public static final long STATUS_IO_ERR                = 1152921504606846976L;
	/**
	 * the status flag indicates that an interrupt was misused
	 */
	@PrimStartConstant
	public static final long STATUS_ILLEGAL_ARG           = 2305843009213693952L;
	/**
	 * the status flag indicates the system could not reserve enough memory
	 */
	@PrimStartConstant
	public static final long STATUS_OUT_OF_MEMORY         = 4611686018427387904L;
	/**
	 * the status flag indicates that some unspecified error occurred this flag will not be generated by default interrupts
	 */
	@PrimStartConstant
	public static final long STATUS_ERROR                 = -9223372036854775808L;
	/** open option: open file for read access */
	@PrimStartConstant
	public static final long OPEN_READ                    = 1L;
	/** open option: open file for write access */
	@PrimStartConstant
	public static final long OPEN_WRITE                   = 2L;
	/** open option: open file for append access (implicit set of `OPEN_WRITE`) */
	@PrimStartConstant
	public static final long OPEN_APPEND                  = 4L;
	/** open option: open file or create file (needs `OPEN_WRITE`, not compatible with `OPEN_NEW_FILE`) */
	@PrimStartConstant
	public static final long OPEN_CREATE                  = 8L;
	/** open option: fail if file already exists or create the file (needs `OPEN_WRITE`, not compatible with `OPEN_CREATE`) */
	@PrimStartConstant
	public static final long OPEN_NEW_FILE                = 16L;
	/** open option: if the file already exists, remove its content (needs `OPEN_WRITE`) */
	@PrimStartConstant
	public static final long OPEN_TRUNCATE                = 32L;
	/**
	 * the STREAM-ID of the default input stream<br>
	 * this stream is initially open and does not support write operations<br>
	 * this stream is no fs-stream (thus the position can not be set)
	 */
	@PrimStartConstant
	public static final long STD_IN                       = 0L;
	/**
	 * the STREAM-ID of the default output stream<br>
	 * this stream is initially open and does not support read operations<br>
	 * this stream is no fs-stream (thus the position can not be set)
	 */
	@PrimStartConstant
	public static final long STD_OUT                      = 1L;
	/**
	 * the STREAM-ID of the default log output stream<br>
	 * this stream is initially open and does not support read operations<br>
	 * this stream is no fs-stream (thus the position can not be set)
	 */
	@PrimStartConstant
	public static final long STD_LOG                      = 2L;
	
	public static final long MAX_STD_STREAM = STD_LOG;
	
	/**
	 * only used for validation of the {@link PrimAsmConstants#START_CONSTANTS} and the fields wich are annotated.<br>
	 * a annotated field has to be of long type.
	 * <p>
	 * the field has to be in the {@link PrimAsmConstants#START_CONSTANTS} map (key is the {@link Field#getName()}).<br>
	 * the {@link PrimitiveConstant#name} also have to be equal to the field name.<br>
	 * the {@link PrimitiveConstant#value} also have to be equal to the fields value ({@link Field#getLong(Object)}).<br>
	 * the other values of the {@link PrimAsmConstants} are unspecified.
	 * 
	 * @author pat
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface PrimStartConstant {
		
	}
	
	static {
		for (Field field : PrimAsmConstants.class.getFields()) {
			PrimStartConstant c = field.getAnnotation(PrimStartConstant.class);
			if (c == null) continue;
			try {
				long val = field.getLong(null);
				PrimitiveConstant primConst = START_CONSTANTS.get(field.getName());
				if (primConst == null) {
					throw new AssertionError("validation error: primConst=null field: " + field.getName() + " (" + val + ")");
				}
				if (primConst.value != val) {
					throw new AssertionError("validation error: field: " + field.getName() + "=" + val + " primConst.val=" + primConst.value + " (comment):\n" + primConst.comment);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
			}
		}
	}
	
	public static final int IP      = 0;
	public static final int SP      = 1;
	public static final int STATUS  = 2;
	public static final int INTCNT  = 3;
	public static final int INTP    = 4;
	public static final int FS_LOCK = 5;
	public static final int X_ADD   = 6;
	
	
	
	private static final int PARAM_BASE  = 0x01;
	private static final int PARAM_A_NUM = 0x00;
	private static final int PARAM_A_SR  = 0x02;
	private static final int PARAM_NO_B  = 0x00;
	private static final int PARAM_B_REG = 0x04;
	private static final int PARAM_B_NUM = 0x08;
	private static final int PARAM_B_SR  = 0x0C;
	
	public static final int PARAM_ART_ANUM      = PARAM_BASE | PARAM_A_NUM | PARAM_NO_B;
	public static final int PARAM_ART_ASR       = PARAM_BASE | PARAM_A_SR | PARAM_NO_B;
	public static final int PARAM_ART_ANUM_BREG = PARAM_BASE | PARAM_A_NUM | PARAM_B_REG;
	public static final int PARAM_ART_ASR_BREG  = PARAM_BASE | PARAM_A_SR | PARAM_B_REG;
	public static final int PARAM_ART_ANUM_BNUM = PARAM_BASE | PARAM_A_NUM | PARAM_B_NUM;
	public static final int PARAM_ART_ASR_BNUM  = PARAM_BASE | PARAM_A_SR | PARAM_B_NUM;
	public static final int PARAM_ART_ANUM_BSR  = PARAM_BASE | PARAM_A_NUM | PARAM_B_SR;
	public static final int PARAM_ART_ASR_BSR   = PARAM_BASE | PARAM_A_SR | PARAM_B_SR;
	
}
