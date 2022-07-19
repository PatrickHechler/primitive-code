package de.hechler.patrick.codesprachen.primitive.core.utils;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
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
			try (Scanner sc = new Scanner(in, "UTF-8")) {
				StringBuilder build = new StringBuilder();
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.charAt(0) == '|') {
						build.append(line).append('\n');
					} else {
						int index = line.indexOf('=');
						String name = line.substring(0, index);
						String num = line.substring(index + 1);
						long val;
						if (num.matches("\\-?[0-9]+")) {
							val = Long.parseLong(num);
						} else if (num.startsWith("HEX-")) {
							num = num.substring(4);
							val = Long.parseLong(num, 16);
						} else if (num.startsWith("NHEX-")) {
							num = num.substring(4);
							val = Long.parseLong(num, 16);
						} else if (num.startsWith("UHEX-")) {
							num = num.substring(4);
							val = Long.parseUnsignedLong(num, 16);
						} else if (num.startsWith("BIN-")) {
							num = num.substring(4);
							val = Long.parseLong(num, 2);
						} else if (num.startsWith("NBIN-")) {
							num = num.substring(4);
							val = Long.parseLong(num, 2);
						} else if (num.startsWith("OCT-")) {
							num = num.substring(4);
							val = Long.parseLong(num, 8);
						} else if (num.startsWith("NOCT-")) {
							num = num.substring(4);
							val = Long.parseLong(num, 8);
						} else if (num.startsWith("DEC-")) {
							num = num.substring(4);
							val = Long.parseLong(num);
						} else if (num.startsWith("NDEC-")) {
							num = num.substring(4);
							val = Long.parseLong(num);
						} else {
							throw new InternalError();
						}
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
	 * <ul>
	 * <li><code>0</code>: illegal interrupt
	 * <ul>
	 * <li><code>X00</code> contains the number of the illegal interrupt
	 * <li>calls the exit interrupt with <code>(64 + illegal_interrup_number)</code>
	 * <li>if the forbidden interrupt is the exit input, the program exits with <code>(64 + 4) = 68</code>, but does not calls the exit interrupt to do so
	 * <li>if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with <code>63</code>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_ILLEGAL_INTERRUPT = 0L;
	/**
	 * <ul>
	 * <li><code>1</code>: unknown command<br>
	 * <ul>
	 * <li>calls the exit interrupt with <code>62</code>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_UNKNOWN_COMMAND   = 1L;
	/**
	 * <ul>
	 * <li><code>2</code>: illegal memory<br>
	 * <ul>
	 * <li>calls the exit interrupt with <code>61</code>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_ILLEGAL_MEMORY    = 2L;
	/**
	 * <ul>
	 * <li><code>3</code>: arithmetic error<br>
	 * <ul>
	 * <li>calls the exit interrupt with <code>60</code>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_ERRORS_ARITHMETIC_ERROR  = 3L;
	
	/**
	 * <ul>
	 * <li><code>11</code>: get fs-file
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the file
	 * <li><code>X00</code> will point to a fs-element of the file
	 * <li>on failure <code>X00</code> will be set to <code>-1</code>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (file expected, but folder)
	 * <li>if the element exists, but is a folder and no file
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist
	 * <li>if the element does not exists
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <li>if some IO error occurred
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated
	 * <li>the system could not allocate enough memory for the fs-element
	 * <li>if the specified element is a link to a file, the target file of the link is returned instead of the actual link
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_GET_FILE    = 11L;
	/**
	 * <ul>
	 * <li><code>12</code>: get fs-folder
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the dictionary
	 * <li><code>X00</code> will point to a fs-element of the folder
	 * <li>on failure <code>X00</code> will be set to <code>-1</code>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (folder expected, but file)
	 * <li>if the element exists, but is a file and no folder
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist
	 * <li>if the element does not exists
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <li>if some IO error occurred
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated
	 * <li>the system could not allocate enough memory for the fs-element
	 * <li>if the specified element is a link to a folder, the target folder of the link is returned instead of the actual link
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_GET_FOLDER  = 12L;
	/**
	 * <ul>
	 * <li><code>13</code>: get fs-link
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the link
	 * <li><code>X00</code> will point to a fs-element of the link
	 * <li>on failure <code>X00</code> will be set to <code>-1</code>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (link expected, but file or folder)
	 * <li>if the element exists, but is a file and no folder
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist
	 * <li>if the element does not exists
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <li>if some IO error occurred
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated
	 * <li>the system could not allocate enough memory for the fs-element
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_GET_LINK    = 13L;
	/**
	 * <ul>
	 * <li><code>14</code>: get fs-element
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the element
	 * <li><code>X00</code> will point to the fs-element
	 * <li>on failure <code>X00</code> will be set to <code>-1</code>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist
	 * <li>if the element does not exists
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <li>if some IO error occurred
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated
	 * <li>the system could not allocate enough memory for the fs-element
	 * <li>if the specified element is a link the actual link is returned
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_GET_ELEMENT = 14L;
	
	/**
	 * <ul>
	 * <li><code>18</code>: get create date
	 * <ul>
	 * <li><code>X00</code> points to the fs-element
	 * <li><code>X01</code> will be set to the create time of the element
	 * <li><code>X00</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_ELEMENT_GET_CREATE        = 18L;
	/**
	 * <ul>
	 * <li><code>19</code>: get last mod date
	 * <ul>
	 * <li><code>X00</code> points to the fs-element
	 * <li><code>X01</code> will be set to the last modify time of the element
	 * <li><code>X00</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_ELEMENT_GET_LAST_MOD      = 19L;
	/**
	 * <ul>
	 * <li><code>20</code>: get last meta mod date
	 * <ul>
	 * <li><code>X00</code> points to the fs-element
	 * <li><code>X01</code> will be set to the last meta mod time of the element
	 * <li><code>X00</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_ELEMENT_GET_LAST_META_MOD = 20L;
	/**
	 * <ul>
	 * <li><code>21</code>: set create date
	 * <ul>
	 * <li><code>X00</code> points to the fs-element
	 * <li><code>X01</code> contains the new create date
	 * <li><code>X00</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> : <code>UHEX-0000000800000000</code>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_ELEMENT_SET_CREATE        = 21L;
	/**
	 * <ul>
	 * <li><code>22</code>: set last mod date
	 * <ul>
	 * <li><code>X00</code> points to the fs-element
	 * <li><code>X01</code> contains the new last mod date
	 * <li><code>X00</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> : <code>UHEX-0000000800000000</code>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_ELEMENT_SET_LAST_MOD      = 22L;
	/**
	 * <ul>
	 * <li><code>23</code>: set last meta mod date
	 * <ul>
	 * <li><code>X00</code> points to the fs-element
	 * <li><code>X01</code> contains the new last meta mod date
	 * <li><code>X00</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> : <code>UHEX-0000000800000000</code>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>note: when changing all dates change this date at last, because it will bee automatically changed on meta changes like the change of the create or last mod date
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_ELEMENT_SET_LAST_META_MOD = 23L;
	/**
	 * <ul>
	 * <li><code>33</code>: get child element from index
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder
	 * <li><code>X01</code> contains the index of the child element
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the child element
	 * <li><code>X01</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no folder
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * <li>or if the index is out of range (negative or greater or equal to the child element count of the given folder)
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FOLDER_GET_CHILD_OF_INDEX = 33L;
	/**
	 * <ul>
	 * <li><code>34</code>: get child element from name
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder
	 * <li><code>X01</code> points to the STRING name of the child element
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the child element
	 * <li><code>X01</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no folder
	 * </ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: the folder does not contain a child with the given name
	 * <ul>
	 * <li>if there is no child with the given name
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FOLDER_GET_CHILD_OF_NAME  = 34L;
	/**
	 * <ul>
	 * <li><code>35</code>: add folder
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder
	 * <li><code>X01</code> points to the STRING name of the new child element
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the new child element folder
	 * <li><code>X01</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no folder
	 * </ul>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder already contain a child with the given name
	 * <ul>
	 * <li>if there is already child with the given name
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>the folder will automatically removed the sorted flag (<code>HEX-00000040</code> : <code>FLAG_FOLDER_SORTED</code>)
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FOLDER_ADD_FOLDER         = 35L;
	/**
	 * <ul>
	 * <li><code>36</code>: add file
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder
	 * <li><code>X01</code> points to the STRING name of the new child element
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the new child element file
	 * <li><code>X01</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no folder
	 * </ul>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder already contain a child with the given name
	 * <ul>
	 * <li>if there is already child with the given name
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>the folder will automatically remove the sorted flag (<code>HEX-00000040</code> : <code>FLAG_FOLDER_SORTED</code>)
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FOLDER_ADD_FILE           = 36L;
	/**
	 * <ul>
	 * <li><code>37</code>: add link
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder
	 * <li><code>X01</code> points to the STRING name of the new child element
	 * <li><code>X02</code> points to the fs-element of the target element
	 * <ul>
	 * <li>the target element is not allowed to be a link
	 * </ul>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the new child element link
	 * <li><code>X01</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no folder
	 * </ul>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder already contain a child with the given name
	 * <ul>
	 * <li>if there is already child with the given name
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>the folder will automatically remove the sorted flag (<code>HEX-00000040</code> : <code>FLAG_FOLDER_SORTED</code>)
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FOLDER_ADD_LINK           = 37L;
	
	/**
	 * <ul>
	 * <li><code>40</code>: file read
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file
	 * <li><code>X01</code> contains the offset from the file
	 * <li><code>X02</code> contains the number of bytes to read
	 * <li><code>X03</code> points to a memory block to which the file data should be filled
	 * <li><code>X02</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no file
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * <li>or if the read count or file offset is negative
	 * <li>or if the read count + file offset is larger than the file length
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FILE_READ       = 40L;
	/**
	 * <ul>
	 * <li><code>41</code>: file write
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file
	 * <li><code>X01</code> contains the offset from the file
	 * <li><code>X02</code> contains the number of bytes to write
	 * <li><code>X03</code> points to the memory block with the data to write
	 * <li><code>X02</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no file
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * <li>or if the write count or file offset is negative
	 * <li>or if the write count + file offset is larger than the file length
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FILE_WRITE      = 41L;
	/**
	 * <ul>
	 * <li><code>42</code>: file append
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file
	 * <li><code>X01</code> contains the number of bytes to append
	 * <li><code>X02</code> points to the memory block with the data to write
	 * <li><code>X01</code> will be set to <code>-1</code> on error
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type
	 * <ul>
	 * <li>if the given element is no file
	 * </ul>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed because the there could not be allocated enough space for the larger file
	 * <ul>
	 * <li>the file system could either not allocate enough blocks for the new larger file
	 * <li>or the file system could not allocate enough space for the larger file system entry of the file
	 * </ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only
	 * <ul>
	 * <li>if the element is marked as read-only
	 * </ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock
	 * <ul>
	 * <li>if the element is locked with a different lock
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred
	 * <ul>
	 * <li>if some IO error occurred
	 * </ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)
	 * <li>or if the write count or file offset is negative
	 * <li>or if the write count + file offset is larger than the file length
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FS_FILE_APPEND     = 42L;
	/**
	 * <ul>
	 * <li><code>57</code>: number to string
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert
	 * <li><code>X01</code> is points to the buffer to be filled with the number in a STRING format
	 * <li><code>X02</code> contains the base of the number system
	 * <ul>
	 * <li>the minimum base is <code>2</code>
	 * <li>the maximum base is <code>36</code>
	 * <li>other values lead to undefined behavior
	 * </ul>
	 * <li><code>X00</code> will be set to the length of the STRING
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_NUMBER_TO_STRING   = 57L;
	/**
	 * <ul>
	 * <li><code>58</code>: floating point number to string
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert
	 * <li><code>X02</code> contains the maximum amount of digits to be used to represent the floating point number
	 * <li><code>X01</code> is points to the buffer to be filled with the number in a STRING format
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_FPNUMBER_TO_STRING = 58L;
	/**
	 * <ul>
	 * <li><code>59</code>: string to number
	 * <ul>
	 * <li><code>X00</code> points to the STRING
	 * <li><code>X01</code> points to the base of the number system
	 * <ul>
	 * <li>(for example <code>10</code> for the decimal system or <code>2</code> for the binary system)
	 * </ul>
	 * <li><code>X00</code> will be set to the converted number
	 * <li><code>X01</code> will point to the end of the number-STRING
	 * <ul>
	 * <li>this might be the <code>\0</code> terminating character
	 * </ul>
	 * <li>if the STRING contains illegal characters or the base is not valid, the behavior is undefined
	 * <li>this function will ignore leading space characters
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_STRING_TO_NUMBER   = 59L;
	/**
	 * <ul>
	 * <li><code>60</code>: string to floating point number
	 * <ul>
	 * <li><code>X00</code> points to the STRING
	 * <li><code>X00</code> will be set to the converted number
	 * <li><code>X01</code> will point to the end of the number-STRING
	 * <ul>
	 * <li>this might be the <code>\0</code> terminating character
	 * </ul>
	 * <li>if the STRING contains illegal characters or the base is not valid, the behavior is undefined
	 * <li>this function will ignore leading space characters
	 * </ul>
	 * </ul>
	 */
	@PrimStartConstant
	public static final long INT_STRING_TO_FPNUMBER = 60L;
	/**
	 * <li><code>%1</code>: STRING to U8-STRING
	 * <li><code>%1</code> contains the STRING
	 * <li><code>%1</code> points to a buffer for the U8-SRING
	 * <li><code>%1</code> is set to the size of the size of the buffer
	 * <li><code>%1</code> will point to the U8-STRING
	 * <li><code>%1</code> will be set to the U8-STRING buffer size
	 * <li><code>%1</code> will point to the <code>%1</code> character of the U8-STRING
	 */
	@PrimStartConstant
	public static final long INT_STR_TO_U8STR       = 62L;
	/**
	 * <li><code>%1</code>: U8-STRING to STRING
	 * <li><code>%1</code> contains the U8-STRING
	 * <li><code>%1</code> points to a buffer for the SRING
	 * <li><code>%1</code> is set to the size of the size of the buffer
	 * <li><code>%1</code> will point to the STRING
	 * <li><code>%1</code> will be set to the STRING buffer size
	 * <li><code>%1</code> will point to the <code>%1</code> character of the STRING
	 */
	@PrimStartConstant
	public static final long INT_U8STR_TO_STR       = 63L;
	
	/** the total number of default interrupts */
	@PrimStartConstant
	public static final long INTERRUPT_COUNT = 66L;
	
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
	/** open option: open file for append access (implicit set of <code>OPEN_WRITE</code>) */
	@PrimStartConstant
	public static final long OPEN_APPEND                  = 4L;
	/** open option: open file or create file (needs <code>OPEN_WRITE</code>, not compatible with <code>OPEN_NEW_FILE</code>) */
	@PrimStartConstant
	public static final long OPEN_CREATE                  = 8L;
	/** open option: fail if file already exists or create the file (needs <code>OPEN_WRITE</code>, not compatible with <code>OPEN_CREATE</code>) */
	@PrimStartConstant
	public static final long OPEN_NEW_FILE                = 16L;
	/** open option: if the file already exists, remove its content (needs <code>OPEN_WRITE</code>) */
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
	
	
	
	public static final int PARAM_BASE  = 0x01;
	public static final int PARAM_A_NUM = 0x00;
	public static final int PARAM_A_SR  = 0x02;
	public static final int PARAM_NO_B  = 0x00;
	public static final int PARAM_B_REG = 0x04;
	public static final int PARAM_B_NUM = 0x08;
	public static final int PARAM_B_SR  = 0x0C;
	
	public static final int PARAM_ART_ANUM      = PARAM_BASE | PARAM_A_NUM | PARAM_NO_B;
	public static final int PARAM_ART_ASR       = PARAM_BASE | PARAM_A_SR | PARAM_NO_B;
	public static final int PARAM_ART_ANUM_BREG = PARAM_BASE | PARAM_A_NUM | PARAM_B_REG;
	public static final int PARAM_ART_ASR_BREG  = PARAM_BASE | PARAM_A_SR | PARAM_B_REG;
	public static final int PARAM_ART_ANUM_BNUM = PARAM_BASE | PARAM_A_NUM | PARAM_B_NUM;
	public static final int PARAM_ART_ASR_BNUM  = PARAM_BASE | PARAM_A_SR | PARAM_B_NUM;
	public static final int PARAM_ART_ANUM_BSR  = PARAM_BASE | PARAM_A_NUM | PARAM_B_SR;
	public static final int PARAM_ART_ASR_BSR   = PARAM_BASE | PARAM_A_SR | PARAM_B_SR;
	
}
