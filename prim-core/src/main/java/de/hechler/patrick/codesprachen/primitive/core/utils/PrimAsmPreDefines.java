package de.hechler.patrick.codesprachen.primitive.core.utils;

public class PrimAsmPreDefines {
	
	/**
	 * <ul>
	 * <li><code>0</code>: illegal interrupt</li>
	 * <ul>
	 * <li><code>X00</code> contains the number of the illegal interrupt</li>
	 * <li>calls the exit interrupt with <code>(64 + illegal_interrup_number)</code></li>
	 * <li>if the forbidden interrupt is the exit input, the program exits with <code>(64 + 4) = 68</code>, but does not calls the exit interrupt to do so</li>
	 * <li>if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with <code>63</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_ERRORS_ILLEGAL_INTERRUPT     = 0L;
	/**
	 * <ul>
	 * <li><code>1</code>: unknown command</li>
	 * <ul>
	 * <li>calls the exit interrupt with <code>62</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_ERRORS_UNKNOWN_COMMAND       = 1L;
	/**
	 * <ul>
	 * <li><code>2</code>: illegal memory</li>
	 * <ul>
	 * <li>calls the exit interrupt with <code>61</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_ERRORS_ILLEGAL_MEMORY        = 2L;
	/**
	 * <ul>
	 * <li><code>3</code>: arithmetic error</li>
	 * <ul>
	 * <li>calls the exit interrupt with <code>60</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_ERRORS_ARITHMETIC_ERROR      = 3L;
	/**
	 * <ul>
	 * <li><code>4</code>: exit</li>
	 * <ul>
	 * <li>use <code>X00</code> to specify the exit number of the progress</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_EXIT                         = 4L;
	/**
	 * <ul>
	 * <li><code>5</code>: allocate a memory-block</li>
	 * <ul>
	 * <li><code>X00</code> saves the size of the block</li>
	 * <li>if the value of <code>X00</code> is <code>-1</code> after the call the memory-block could not be allocated</li>
	 * <li>if the value of <code>X00</code> is not <code>-1</code>, <code>X00</code> points to the first element of the allocated memory-block</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_MEMORY_ALLOC                 = 5L;
	/**
	 * <ul>
	 * <li><code>6</code>: reallocate a memory-block</li>
	 * <ul>
	 * <li><code>X00</code> points to the memory-block</li>
	 * <li><code>X01</code> is set to the new size of the memory-block</li>
	 * <li><code>X01</code> will be <code>-1</code> if the memory-block could not be reallocated, the old memory-block will remain valid and should be freed if it is not longer needed</li>
	 * <li><code>X01</code> will point to the new memory block, the old memory-block was automatically freed, so it should not be used, the new block should be freed if it is not longer needed</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_MEMORY_REALLOC               = 6L;
	/**
	 * <ul>
	 * <li><code>7</code>: free a memory-block</li>
	 * <ul>
	 * <li><code>X00</code> points to the old memory-block</li>
	 * <li>after this the memory-block should not be used</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_MEMORY_FREE                  = 7L;
	/**
	 * <ul>
	 * <li><code>8</code>: open new stream</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer to the STRING, which refers to the file which should be read</li>
	 * <li><code>X01</code> specifies the open mode: (bitwise flags)</li>
	 * <ul>
	 * <li><code>UHEX-0000000000000001</code> : <code>OPEN_READ</code> open file for read access</li>
	 * <li><code>UHEX-0000000000000002</code> : <code>OPEN_WRITE</code> open file for write access</li>
	 * <li><code>UHEX-0000000000000004</code> : <code>OPEN_APPEND</code> open file for append access (implicit set of <code>OPEN_WRITE</code>)</li>
	 * <li><code>UHEX-0000000000000008</code> : <code>OPEN_CREATE</code> open file or create file (needs <code>OPEN_WRITE</code>, not compatible with <code>OPEN_NEW_FILE</code>)</li>
	 * <li><code>UHEX-0000000000000010</code> : <code>OPEN_NEW_FILE</code> fail if file already exists or create the file (needs <code>OPEN_WRITE</code>, not compatible with
	 * <code>OPEN_CREATE</code>)</li>
	 * <li><code>UHEX-0000000000000020</code> : <code>OPEN_TRUNCATE</code> if the file already exists, remove its content (needs <code>OPEN_WRITE</code>)</li>
	 * <li>other flags will be ignored</li>
	 * <li>the operation will fail if it is not specified if the file should be opened for read, write and/or append</li>
	 * <li>opens a new stream to the specified file</li>
	 * </ul>
	 * <li>if successfully the STREAM-ID will be saved in the <code>X00</code> register</li>
	 * <li>if failed <code>X00</code> will contain <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (file expected, but folder)</li>
	 * <ul>
	 * <li>if the element already exists, but is a folder and no file</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * </ul>
	 * <ul>
	 * <li>if the element does not exists, but <code>OPEN_CREATE</code> and <code>OPEN_NEW_FILE</code> are not set</li>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: operation failed because the element already existed</li>
	 * </ul>
	 * <ul>
	 * <li>if the element already exists, but <code>OPEN_NEW_FILE</code> is set</li>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed because there was not enough space in the file system</li>
	 * </ul>
	 * <ul>
	 * <li>if the system tried to create the new file, but there was not enough space for the new file-system-entry</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: was denied because of read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the file is marked as read-only, but it was tried to open the file for read or append access</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the file is locked with <code>LOCK_NO_READ_ALLOWED</code> : <code>UHEX-0000000100000000</code> and it was tried to open the file for read access</li>
	 * <li>or if the file is locked with <code>LOCK_NO_WRITE_ALLOWED_LOCK</code> : <code>UHEX-0000000200000000</code> and it was tried to open the file for write/append access</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: STREAM-ID is invalid or <code>X01</code> contains an invalid open mode</li>
	 * </ul>
	 * <ul>
	 * <li>if the open mode was invalid</li>
	 * <ul>
	 * <li><code>OPEN_CREATE</code> with <code>OPEN_NEW_FILE</code></li>
	 * <li>not <code>OPEN_READ</code> and not <code>OPEN_WRITE</code> and not <code>OPEN_APPEND</code></li>
	 * <li><code>OPEN_CREATE</code>, <code>OPEN_NEW_FILE</code> and/or <code>OPEN_TRUNCATE</code> without <code>OPEN_WRITE</code> (and without <code>OPEN_APPEND</code>)</li>
	 * <li>to close the stream call the free interrupt (<code>7</code> : <code>INT_MEMORY_FREE</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STREAMS_NEW                  = 8L;
	/**
	 * <ul>
	 * <li><code>9</code>: write</li>
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to write</li>
	 * <li><code>X02</code> points to the elements to write</li>
	 * <li>after execution <code>X01</code> will contain the number of written elements or <code>-1</code> if an error occurred</li>
	 * <li>if an error occurred the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed because there was not enough space in the file system</li>
	 * <ul>
	 * <li>if the system tried to allocate more space for either the file-system-entry of the open file or its content, but there was not enough space</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: was denied because of read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the file is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the file is locked with <code>LOCK_NO_WRITE_ALLOWED_LOCK</code> : <code>UHEX-0000000200000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: STREAM-ID is invalid or <code>X01</code> is negative</li>
	 * </ul>
	 * <ul>
	 * <li>if the STREAM-ID is invalid (maybe because the corresponding file was deleted)</li>
	 * <li>or if a negative number of bytes should be written</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STREAMS_WRITE                = 9L;
	/**
	 * <ul>
	 * <li><code>10</code>: read</li>
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to read</li>
	 * <li><code>X02</code> points to the elements to read</li>
	 * <li>after execution <code>X01</code> will contain the number of elements, which has been read</li>
	 * <li>if an error occurred <code>X01</code> will be set to <code>-1</code> and the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (file expected, but folder)</li>
	 * <ul>
	 * <li>if the element was re-reated as folder (the re-created folder may has a different path and/or name)</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * </ul>
	 * <ul>
	 * <li>if the element was deleted</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the file is locked with <code>LOCK_NO_READ_ALLOWED</code> : <code>UHEX-0000000100000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: STREAM-ID is invalid or <code>X01</code> is negative</li>
	 * </ul>
	 * <ul>
	 * <li>if the STREAM-ID is invalid (maybe because the corresponding file was deleted)</li>
	 * <li>or if a negative number of bytes should be written</li>
	 * <li>if <code>X01</code> is <code>0</code> and was set before to a value greater <code>0</code> then the stream has reached its end</li>
	 * </ul>
	 * </ul>
	 * <li>reading less bytes than expected does not mead that the stream has reached it's end</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STREAMS_READ                 = 10L;
	/**
	 * <ul>
	 * <li><code>11</code>: get fs-file</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the file</li>
	 * <li><code>X00</code> will point to a fs-element of the file</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (file expected, but folder)</li>
	 * <ul>
	 * <li>if the element exists, but is a folder and no file</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * </ul>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated</li>
	 * </ul>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * <li>if the specified element is a link to a file, the target file of the link is returned instead of the actual link</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_GET_FILE                  = 11L;
	/**
	 * <ul>
	 * <li><code>12</code>: get fs-folder</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the dictionary</li>
	 * <li><code>X00</code> will point to a fs-element of the folder</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (folder expected, but file)</li>
	 * <ul>
	 * <li>if the element exists, but is a file and no folder</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * </ul>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated</li>
	 * </ul>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * <li>if the specified element is a link to a folder, the target folder of the link is returned instead of the actual link</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_GET_FOLDER                = 12L;
	/**
	 * <ul>
	 * <li><code>13</code>: get fs-link</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the link</li>
	 * <li><code>X00</code> will point to a fs-element of the link</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code> : <code>STATUS_ELEMENT_WRONG_TYPE</code>: operation failed because the element is not of the correct type (link expected, but file or folder)</li>
	 * <ul>
	 * <li>if the element exists, but is a file and no folder</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * </ul>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated</li>
	 * </ul>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_GET_LINK                  = 13L;
	/**
	 * <ul>
	 * <li><code>14</code>: get fs-element</li>
	 * <ul>
	 * <li><code>X00</code> contains a pointer of a STRING with the element</li>
	 * <li><code>X00</code> will point to the fs-element</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li><code>UHEX-4000000000000000</code> : <code>STATUS_OUT_OF_MEMORY</code>: not enough memory could be allocated</li>
	 * </ul>
	 * <ul>
	 * <li>the system could not allocate enough memory for the fs-element</li>
	 * <li>if the specified element is a link the actual link is returned</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_GET_ELEMENT               = 14L;
	/**
	 * <ul>
	 * <li><code>15</code>: duplicate fs-element (handle)</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X00</code> will point to a duplicate of the same element</li>
	 * <li>if the system could not allocate enough memory for the duplicate <code>X00</code> will be set to <code>-1</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_DUPLICATE_HANDLE          = 15L;
	/**
	 * <ul>
	 * <li><code>16</code>: get parent</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the ID of the parent folder</li> + note that the only negative ID is <code>-2</code> (root folder)
	 * </ul>
	 * </ul>
	 * </ul>
	 * <ul>
	 * <ul>
	 * <ul>
	 * <ul>
	 * <li>all other IDs are <code>0</code> or positive, but not all positive numbers are valid IDs</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * </ul>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> is a fs-element of the root folder or contains itself an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given fs-element is the root folder</li>
	 * <li>or if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_GET_PARENT        = 16L;
	/**
	 * <ul>
	 * <li><code>17</code>: fs-element from ID</li>
	 * <ul>
	 * <li><code>X00</code> is set to the ID of the element</li>
	 * <li><code>X00</code> will be set to a fs-element of the element with the given id</li>
	 * <li>on failure <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: operation failed because the element does not exist</li>
	 * <ul>
	 * <li>if the element does not exists</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> is invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID is invalid</li>
	 * <ul>
	 * <li>all negative IDs except of <code>-2</code> are invalid (the root folder has the ID <code>-2</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_FROM_ID           = 17L;
	/**
	 * <ul>
	 * <li><code>18</code>: get create date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the create time of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_GET_CREATE        = 18L;
	/**
	 * <ul>
	 * <li><code>19</code>: get last mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the last modify time of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_GET_LAST_MOD      = 19L;
	/**
	 * <ul>
	 * <li><code>20</code>: get last meta mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the last meta mod time of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_GET_LAST_META_MOD = 20L;
	/**
	 * <ul>
	 * <li><code>21</code>: set create date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the new create date</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> : <code>UHEX-0000000800000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_SET_CREATE        = 21L;
	/**
	 * <ul>
	 * <li><code>22</code>: set last mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the new last mod date</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> : <code>UHEX-0000000800000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_SET_LAST_MOD      = 22L;
	/**
	 * <ul>
	 * <li><code>23</code>: set last meta mod date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the new last meta mod date</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only</li>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with <code>LOCK_NO_META_CHANGE_ALLOWED_LOCK</code> : <code>UHEX-0000000800000000</code></li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>note: when changing all dates change this date at last, because it will bee automatically changed on meta changes like the change of the create or last mod date</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_SET_LAST_META_MOD = 23L;
	/**
	 * <ul>
	 * <li><code>24</code>: get lock data</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X00</code> will be set to the lock data of the element</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_GET_LOCK_DATA     = 24L;
	/**
	 * <ul>
	 * <li><code>25</code>: get lock date</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the lock date of the element or <code>-1</code> if the element is not locked</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_GET_LOCK_TIME     = 25L;
	/**
	 * <ul>
	 * <li><code>26</code>: lock element</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> is set to the lock data of the new lock</li>
	 * <li><code>[X00 + 8]</code> : <code>[X00 + FS_ELEMENT_OFFSET_LOCK]</code> will be set to the new lock</li>
	 * <li>if the element is already exclusively locked the operation will fail</li>
	 * <li>if the element is locked with a shared lock and the lock data of the given lock is the same to the lock data of the current lock:</li>
	 * <ul>
	 * <li>a shared lock is flagged with <code>UHEX-4000000000000000</code> : <code>LOCK_SHARED_LOCK</code></li>
	 * <li>the new lock will not contain the shared lock counter</li>
	 * <li>the lock should be released like a exclusive lock, when it is no longer needed</li>
	 * <li>a shared lock does not give you any permissions, it just blocks operations for all (also for those with the lock)</li>
	 * <li>if the given lock is not flagged with <code>UHEX-8000000000000000</code> : <code>LOCK_LOCKED_LOCK</code>, it will be automatically be flagged with <code>UHEX-8000000000000000</code>:
	 * <code>LOCK_LOCKED_LOCK</code></li>
	 * </ul>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * <ul>
	 * <li>if the element is already locked</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or <code>X01</code> not only lock data bits</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the given lock does not only specify the lock data</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_LOCK              = 26L;
	/**
	 * <ul>
	 * <li><code>27</code>: unlock element</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>[X00 + 8]</code> : <code>[X00 + FS_ELEMENT_OFFSET_LOCK]</code> will be set to <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <li>if the element is not locked with the given lock the operation will fail</li>
	 * <ul>
	 * <li>if the given lock is <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code>, the operation will always try to remove the lock of the element</li>
	 * <li>if the element is locked with a shared lock:</li>
	 * </ul>
	 * <ul>
	 * <li>if this is the last lock, the shared lock will be removed</li>
	 * <li>else the shared lock counter will be decremented</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * </ul>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_UNLOCK            = 27L;
	/**
	 * <ul>
	 * <li><code>28</code>: delete element</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the lock of the parent element or <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <li>deletes the element from the file system</li>
	 * <li>releases also the fs-element</li>
	 * <ul>
	 * <li>to release a fs-element (handle) normally just use the free interrupt (<code>7</code> : <code>INT_MEMORY_FREE</code>)</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * </ul>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed because the there could not be allocated enough space</li>
	 * <ul>
	 * <li>the file system was not able to resize the file system entry to a smaller size</li>
	 * <ul>
	 * <li>the block intern table sometimes grow when a area is released</li>
	 * <li>if the block intern table can not grow this error will occur</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only</li>
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>if the element or its parent is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element or its parent is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_DELETE            = 28L;
	/**
	 * <ul>
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
	 * <li><code>X03</code> contains the lock of the old parent folder or <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code></li>
	 * <ul>
	 * <li>this value is ignored if <code>X01</code> is set to <code>-1</code> (the parent folder is not set)</li>
	 * <li>moves the element to a new parent folder and sets its name</li>
	 * </ul>
	 * <ul>
	 * <li>note that both operations (set parent folder and set name) are optional</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * </ul>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_READ_ONLY</code>: operation was denied because of read-only</li>
	 * <ul>
	 * <li>if the element, its (old) parent or its (not) new parent is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element, its (old) parent or its (not) new parent is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_MOVE              = 29L;
	/**
	 * <ul>
	 * <li><code>30</code>: get element flags</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> will be set to the flags of the element</li>
	 * <li><code>X01</code> will be set to <code>0</code> if an error occurred</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>note that links are also flagged as folder or file</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>if the link target element is a file, the link is also flagged as file</li>
	 * <li>if the link target element is a folder, the link is also flagged as folder</li>
	 * <li>a link to a link is invalid</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_GET_FLAGS         = 30L;
	/**
	 * <ul>
	 * <li><code>31</code>: modify element flags</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element</li>
	 * <li><code>X01</code> contains the flags to add to the element</li>
	 * <li><code>X02</code> contains the flags to remove from the element</li>
	 * <li>note that the flags wich specify if the element is a folder, file or link are not allowed to be set/removed</li>
	 * <li>on error <code>X01</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or invalid flag modify</li>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the flags to add or to remove contain the bits:</li>
	 * <ul>
	 * <li><code>HEX-00000001</code> : <code>FLAG_FOLDER</code></li>
	 * <li><code>HEX-00000002</code><code>: </code>FLAG_FILE`</li>
	 * <li><code>HEX-00000004</code> : <code>FLAG_LINK</code></li>
	 * <li>bits out of the 32-bit range will be ignored</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>the 32-bit with less value are used (<code>UHEX-00000000FFFFFFFF</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_ELEMENT_MOD_FLAGS         = 31L;
	/**
	 * <ul>
	 * <li><code>31</code>: get folder child element count</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> will be set to the child element count of the given folder</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FOLDER_CHILD_COUNT        = 32L;
	/**
	 * <ul>
	 * <li><code>33</code>: get child element from index</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> contains the index of the child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the child element</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the index is out of range (negative or greater or equal to the child element count of the given folder)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FOLDER_GET_CHILD_OF_INDEX = 33L;
	/**
	 * <ul>
	 * <li><code>34</code>: get child element from name</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the child element</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0080000000000000</code> : <code>STATUS_ELEMENT_NOT_EXIST</code>: the folder does not contain a child with the given name</li>
	 * </ul>
	 * <ul>
	 * <li>if there is no child with the given name</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FOLDER_GET_CHILD_OF_NAME  = 34L;
	/**
	 * <ul>
	 * <li><code>35</code>: add folder</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the new child element folder</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder already contain a child with the given name</li>
	 * </ul>
	 * <ul>
	 * <li>if there is already child with the given name</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>the folder will automatically removed the sorted flag (<code>HEX-00000040</code> : <code>FLAG_FOLDER_SORTED</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FOLDER_ADD_FOLDER         = 35L;
	/**
	 * <ul>
	 * <li><code>36</code>: add file</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the new child element file</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder already contain a child with the given name</li>
	 * </ul>
	 * <ul>
	 * <li>if there is already child with the given name</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>the folder will automatically remove the sorted flag (<code>HEX-00000040</code> : <code>FLAG_FOLDER_SORTED</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FOLDER_ADD_FILE           = 36L;
	/**
	 * <ul>
	 * <li><code>37</code>: add link</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element folder</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>X02</code> points to the fs-element of the target element</li>
	 * <ul>
	 * <li>the target element is not allowed to be a link</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the id of the new child element link</li>
	 * </ul>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no folder</li>
	 * <li><code>UHEX-0100000000000000</code> : <code>STATUS_ELEMENT_ALREADY_EXIST</code>: the folder already contain a child with the given name</li>
	 * </ul>
	 * <ul>
	 * <li>if there is already child with the given name</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>the folder will automatically remove the sorted flag (<code>HEX-00000040</code> : <code>FLAG_FOLDER_SORTED</code>)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FOLDER_ADD_LINK           = 37L;
	/**
	 * <ul>
	 * <li><code>38</code>: file length</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> will be set to the length of the file in bytes</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FILE_LENGTH               = 38L;
	/**
	 * <ul>
	 * <li><code>39</code>: file hash</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> points to a at least 256-byte large memory block</li>
	 * <ul>
	 * <li>the memory block from <code>X01</code> will be filled with the SHA-256 hash code of the file</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * </ul>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FILE_HASH                 = 39L;
	/**
	 * <ul>
	 * <li><code>40</code>: file read</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the offset from the file</li>
	 * <li><code>X02</code> contains the number of bytes to read</li>
	 * <li><code>X03</code> points to a memory block to which the file data should be filled</li>
	 * <li><code>X02</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the read count or file offset is negative</li>
	 * <li>or if the read count + file offset is larger than the file length</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FILE_READ                 = 40L;
	/**
	 * <ul>
	 * <li><code>41</code>: file write</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the offset from the file</li>
	 * <li><code>X02</code> contains the number of bytes to write</li>
	 * <li><code>X03</code> points to the memory block with the data to write</li>
	 * <li><code>X02</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the write count or file offset is negative</li>
	 * <li>or if the write count + file offset is larger than the file length</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FILE_WRITE                = 41L;
	/**
	 * <ul>
	 * <li><code>42</code>: file append</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the number of bytes to append</li>
	 * <li><code>X02</code> points to the memory block with the data to write</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed because the there could not be allocated enough space for the larger file</li>
	 * </ul>
	 * <ul>
	 * <li>the file system could either not allocate enough blocks for the new larger file</li>
	 * <li>or the file system could not allocate enough space for the larger file system entry of the file</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the write count or file offset is negative</li>
	 * <li>or if the write count + file offset is larger than the file length</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FILE_APPEND               = 42L;
	/**
	 * <ul>
	 * <li><code>43</code>: file truncate</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element file</li>
	 * <li><code>X01</code> contains the new length of the file</li>
	 * <li>removes all data from the file which is behind the new length</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no file</li>
	 * <li><code>UHEX-0200000000000000</code> : <code>STATUS_OUT_OF_SPACE</code>: operation failed because the there could not be allocated enough space</li>
	 * </ul>
	 * <ul>
	 * <li>the file system was not able to resize the file system entry to a smaller size</li>
	 * <ul>
	 * <li>the block intern table sometimes grow when a area is released</li>
	 * <li>if the block intern table can not grow this error can occur</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only</li>
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * <li>or if the new length is larger than the current file length</li>
	 * <li>or if the new length is negative</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_FILE_TRUNCATE             = 43L;
	/**
	 * <ul>
	 * <li><code>44</code>: link get target</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element link</li>
	 * <li><code>[X00]</code> : <code>[X00 + FS_ELEMENT_OFFSET_ID]</code> will be set to the target ID</li>
	 * <li><code>X01</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no link</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_LINK_GET_TARGET           = 44L;
	/**
	 * <ul>
	 * <li><code>45</code>: link set target</li>
	 * <ul>
	 * <li><code>X00</code> points to the fs-element link</li>
	 * <li><code>X01</code> points to the new target element</li>
	 * <li>sets the target element of the link</li>
	 * <ul>
	 * <li>also flags the link with file or folder and removes the other flag (<code>HEX-00000001</code> : <code>FLAG_FOLDER</code> or <code>HEX-00000002</code> : <code>FLAG_FILE</code>)</li>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * </ul>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0040000000000000</code>: <code>STATUS_ELEMENT_WRONG_TYPE</code>: the given element is of the wrong type</li>
	 * <ul>
	 * <li>if the given element is no link</li>
	 * <li><code>UHEX-0400000000000000</code> : <code>STATUS_ELEMENT_READ_ONLY</code>: operation was denied because read-only</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is marked as read-only</li>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * </ul>
	 * <ul>
	 * <li>if the element is locked with a different lock</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> contains an invalid ID or the offset / read count is invalid</li>
	 * </ul>
	 * <ul>
	 * <li>if the given ID of the fs-element is invalid (because it was deleted)</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_LINK_SET_TARGET           = 45L;
	/**
	 * <ul>
	 * <li><code>46</code>: lock file-system</li>
	 * <ul>
	 * <li><code>X00</code> contains the new lock data</li>
	 * <li>the lock is like a lock for elements, but it works for all elements</li>
	 * <li>if the file system is already exclusively locked the operation will fail</li>
	 * <li>if the file system is locked with a shared lock and the lock data of the given lock is the same to the lock data of the current lock:</li>
	 * <ul>
	 * <li>a shared lock is flagged with <code>UHEX-4000000000000000</code> : <code>LOCK_SHARED_LOCK</code></li>
	 * <li>the new lock will not contain the shared lock counter</li>
	 * <li>the lock should be released like a exclusive lock, when it is no longer needed</li>
	 * <li>a shared lock does not give you any permissions, it just blocks operations for all (also for those with the lock)</li>
	 * <li>if the given lock is not flagged with <code>UHEX-8000000000000000</code> : <code>LOCK_LOCKED_LOCK</code>, it will be automatically be flagged with <code>UHEX-8000000000000000</code>:
	 * <code>LOCK_LOCKED_LOCK</code></li>
	 * </ul>
	 * <li><code>X00</code> will be set to <code>-1</code> on error</li>
	 * <ul>
	 * <li>the <code>STATUS</code> register will be flagged:</li>
	 * <ul>
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * <ul>
	 * <li>if the file system, is already locked</li>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified io error occurred</li>
	 * </ul>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * <li><code>UHEX-2000000000000000</code> : <code>STATUS_ILLEGAL_ARG</code>: <code>X00</code> does not only contain lock data bits</li>
	 * </ul>
	 * <ul>
	 * <li>if the given lock does not only specify the lock data</li>
	 * <li>the lock of the file system will be remembered in the <code>FS_LOCK</code> register</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_LOCK                      = 46L;
	/**
	 * <ul>
	 * <li><code>47</code>: unlock file-system</li>
	 * <ul>
	 * <li>if the file system is not locked with the given lock the operation will fail</li>
	 * <ul>
	 * <li>if the <code>FS_LOCK</code> is <code>UHEX-0000000000000000</code> : <code>LOCK_NO_LOCK</code>, the operation will always try to remove the lock of the element</li>
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
	 * <li><code>UHEX-0800000000000000</code> : <code>STATUS_ELEMENT_LOCKED</code>: operation was denied because of lock</li>
	 * <ul>
	 * <li>if the file system is locked with a different lock or not locked at all</li>
	 * </ul>
	 * <li><code>UHEX-1000000000000000</code> : <code>STATUS_IO_ERR</code>: an unspecified IO error occurred</li>
	 * <ul>
	 * <li>if some IO error occurred</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FS_UNLOCK                    = 47L;
	/**
	 * <ul>
	 * <li><code>48</code>: to get the time in milliseconds</li>
	 * <ul>
	 * <li><code>X00</code> will contain the time in milliseconds or <code>-1</code> if not available</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_TIME_GET                     = 48L;
	/**
	 * <ul>
	 * <li><code>49</code>: to wait the given time in nanoseconds</li>
	 * <ul>
	 * <li><code>X00</code> contain the number of nanoseconds to wait (only values from <code>0</code> to <code>999999999</code> are allowed)</li>
	 * <li><code>X01</code> contain the number of seconds to wait</li>
	 * <li><code>X00</code> and <code>X01</code> will contain the remaining time (both <code>0</code> if it finished waiting)</li>
	 * <li><code>X02</code> will be <code>1</code> if the call was successfully and <code>0</code> if something went wrong</li>
	 * <ul>
	 * <li>if <code>X02</code> is <code>1</code> the remaining time will always be <code>0</code></li>
	 * <li>if <code>X02</code> is <code>0</code> the remaining time will be greater <code>0</code></li>
	 * <li><code>X00</code> will not be negative if the progress waited too long</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_TIME_WAIT                    = 49L;
	/**
	 * <ul>
	 * <li><code>50</code>: random</li>
	 * <ul>
	 * <li><code>X00</code> will be filled with random bits</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_RANDOM                       = 50L;
	/**
	 * <ul>
	 * <li><code>51</code>: memory copy</li>
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function has undefined behavior if the two blocks overlap</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_MEMORY_COPY                  = 51L;
	/**
	 * <ul>
	 * <li><code>52</code>: memory move</li>
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_MEMORY_MOVE                  = 52L;
	/**
	 * <ul>
	 * <li><code>53</code>: memory byte set</li>
	 * <ul>
	 * <li>sets a memory block to the given byte-value</li>
	 * <li><code>X00</code> points to the block</li>
	 * <li><code>X01</code> the first byte contains the value to be written to each byte</li>
	 * <li><code>X02</code> contains the length in bytes</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_MEMORY_BSET                  = 53L;
	/**
	 * <ul>
	 * <li><code>54</code>: memory set</li>
	 * <ul>
	 * <li>sets a memory block to the given int64-value</li>
	 * <li><code>X00</code> points to the block</li>
	 * <li><code>X01</code> contains the value to be written to each element</li>
	 * <li><code>X02</code> contains the count of elements to be set</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_MEMORY_SET                   = 54L;
	/**
	 * <ul>
	 * <li><code>55</code>: string length</li>
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00</code> will be set to the length of the string/ the (byte-)offset of the <code>'\0'</code> character</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STRING_LENGTH                = 55L;
	/**
	 * <ul>
	 * <li><code>56</code>: string compare</li>
	 * <ul>
	 * <li><code>X00</code> points to the first STRING</li>
	 * <li><code>X01</code> points to the second STRING</li>
	 * <li><code>X00</code> will be set to zero if both are equal STRINGs, a value greater zero if the first is greater and below zero if the second is greater</li>
	 * <ul>
	 * <li>a STRING is greater if the first mismatching char has numeric greater value</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STRING_COMPARE               = 56L;
	/**
	 * <ul>
	 * <li><code>57</code>: number to string</li>
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert</li>
	 * <li><code>X01</code> is points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> contains the base of the number system</li>
	 * <li><code>X03</code> is set to the length of the buffer</li>
	 * <ul>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</li>
	 * </ul>
	 * <ul>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></li>
	 * <li>other values lead to undefined behavior</li>
	 * <li><code>X00</code> will be set to the size of the STRING</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_NUMBER_TO_STRING             = 57L;
	/**
	 * <ul>
	 * <li><code>58</code>: floating point number to string</li>
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert</li>
	 * <li><code>X02</code> contains the maximum amount of digits to be used to represent the floating point number</li>
	 * <li><code>X01</code> is points to the buffer to be filled with the number in a STRING format</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_FPNUMBER_TO_STRING           = 58L;
	/**
	 * <ul>
	 * <li><code>59</code>: string to number</li>
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X01</code> points to the base of the number system</li>
	 * <ul>
	 * <li>(for example <code>10</code> for the decimal system or <code>2</code> for the binary system)</li>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * </ul>
	 * <li><code>X01</code> will point to the end of the number-STRING</li>
	 * <ul>
	 * <li>this might be the <code>\0</code> terminating character</li>
	 * <li>if the STRING contains illegal characters or the base is not valid, the behavior is undefined</li>
	 * </ul>
	 * <li>this function will ignore leading space characters</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STRING_TO_NUMBER             = 59L;
	/**
	 * <ul>
	 * <li><code>60</code>: string to floating point number</li>
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * <li><code>X01</code> will point to the end of the number-STRING</li>
	 * <ul>
	 * <li>this might be the <code>\0</code> terminating character</li>
	 * <li>if the STRING contains illegal characters or the base is not valid, the behavior is undefined</li>
	 * </ul>
	 * <li>this function will ignore leading space characters</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STRING_TO_FPNUMBER           = 60L;
	/**
	 * <ul>
	 * <li><code>61</code>: format string</li>
	 * <ul>
	 * <li><code>X00</code> is set to the STRING input</li>
	 * <li><code>X01</code> contains the buffer for the STRING output</li>
	 * <li><code>X02</code> is the current size of the buffer in bytes</li>
	 * <li>the register <code>X03..XNN</code> are for the formatting parameters</li>
	 * <ul>
	 * <li>if there are mor parameters used then there are registers the behavior is undefined.</li>
	 * <ul>
	 * <li>that leads to a maximum of 248 parameters.</li>
	 * <li><code>X00</code> will be set to the length of the output string</li>
	 * </ul>
	 * </ul>
	 * <li><code>X01</code> will be set to the output buffer</li>
	 * <li><code>X02</code> will be set to the new buffer size in bytes</li>
	 * <li>if the buffer could not be resized, <code>X00</code> will be set to <code>-1</code></li>
	 * <ul>
	 * <li><code>X02</code> will be set to the current size of the buffer</li>
	 * <li>formatting:</li>
	 * </ul>
	 * <ul>
	 * <li>everything, which can not be formatted, will be delegated to the target buffer</li>
	 * <li><code>%s</code>: the next argument points to a STRING, which should be inserted here</li>
	 * <li><code>%c</code>: the next argument points to a character, which should be inserted here</li>
	 * <ul>
	 * <li>note that characters may contain more than one byte</li>
	 * <ul>
	 * <li><code>BIN-0.......</code> -&gt; one byte (equivalent to an ASCII character)</li>
	 * <li><code>BIN-10......</code> -&gt; invalid, treated as one byte</li>
	 * <li><code>BIN-110.....</code> -&gt; two bytes</li>
	 * <li><code>BIN-1110....</code> -&gt; three bytes</li>
	 * <li><code>BIN-11110...</code> -&gt; four bytes</li>
	 * <li><code>BIN-111110..</code> -&gt; invalid, treated as five byte</li>
	 * <li><code>BIN-1111110.</code> -&gt; invalid, treated as six byte</li>
	 * <li><code>BIN-11111110</code> -&gt; invalid, treated as seven byte</li>
	 * <li><code>BIN-11111111</code> -&gt; invalid, treated as eight byte</li>
	 * <li><code>%B</code>: the next argument points to a byte, which should be inserted here (without being converted to a STRING)</li>
	 * </ul>
	 * </ul>
	 * <li><code>%d</code>: the next argument contains a number, which should be converted to a STRING using the decimal number system and than be inserted here</li>
	 * <li><code>%f</code>: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here</li>
	 * <li><code>%p</code>: the next argument contains a pointer, which should be converted to a STRING</li>
	 * <ul>
	 * <li>if not the pointer will be converted by placing a <code>"p-"</code> and then the pointer-number converted to a STRING using the hexadecimal number system</li>
	 * <li>if the pointer is <code>-1</code> it will be converted to the STRING <code>"---"</code></li>
	 * <li><code>%h</code>: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here</li>
	 * </ul>
	 * <li><code>%b</code>: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here</li>
	 * <li><code>%o</code>: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STRING_FORMAT                = 61L;
	/**
	 * <ul>
	 * <li><code>62</code>: STRING to U8-STRING</li>
	 * <ul>
	 * <li><code>X00</code> contains the STRING</li>
	 * <li><code>X01</code> points to a buffer for the U8-SRING</li>
	 * <li><code>X02</code> is set to the size of the size of the buffer</li>
	 * <li><code>X01</code> will point to the U8-STRING</li>
	 * <li><code>X02</code> will be set to the U8-STRING buffer size</li>
	 * <li><code>X03</code> will point to the <code>\0</code> character of the U8-STRING</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_STR_TO_U8STR                 = 62L;
	/**
	 * <ul>
	 * <li><code>63</code>: U8-STRING to STRING</li>
	 * <ul>
	 * <li><code>X00</code> contains the U8-STRING</li>
	 * <li><code>X01</code> points to a buffer for the SRING</li>
	 * <li><code>X02</code> is set to the size of the size of the buffer</li>
	 * <li><code>X01</code> will point to the STRING</li>
	 * <li><code>X02</code> will be set to the STRING buffer size</li>
	 * <li><code>X03</code> will point to the <code>\0</code> character of the STRING</li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_U8STR_TO_STR                 = 63L;
	/**
	 * <ul>
	 * <li><code>64</code>: load file</li>
	 * <ul>
	 * <li><code>X00</code> is set to the path (inclusive name) of the file</li>
	 * <li><code>X00</code> will point to the memory block, in which the file has been loaded</li>
	 * <li><code>X01</code> will be set to the length of the file (and the memory block)</li>
	 * <li>when an error occurred <code>X00</code> will be set to <code>-1</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_LOAD_FILE                    = 64L;
	/**
	 * <ul>
	 * <li><code>65</code>: get file</li>
	 * <ul>
	 * <li>similar like <code>64</code> (<code>INT_LOAD_FILE</code>) this interrupt loads a file for the program.</li>
	 * <ul>
	 * <li>the only difference is that this interrupt remembers which files has been loaded</li>
	 * <li>if the interrupt is executed multiple times with the same file, it will return every time the same memory block.</li>
	 * <li>file changes after the file has already been loaded with this interrupt are ignored.</li>
	 * <ul>
	 * <li>only if the file moved or deleted the interrupt recognize the change.</li>
	 * <li>if the file gets moved and the new file path is used the interrupt recognizes the old file</li>
	 * <li>thus the same memory block is still returned if the file gets moved and the new path is used</li>
	 * <ul>
	 * <li>thus changes in the content of the file are never recognized</li>
	 * <li>this interrupt does not recognize files loaded with the <code>64</code> (<code>INT_LOAD_FILE</code>) interrupt.</li>
	 * </ul>
	 * </ul>
	 * <li><code>X00</code> is set to the path (inclusive name) of the file</li>
	 * </ul>
	 * <li><code>X00</code> will point to the memory block, in which the file has been loaded</li>
	 * <li><code>X01</code> will be set to the length of the file (and the memory block)</li>
	 * <li><code>X02</code> will be set to <code>1</code> if the file has been loaded as result of this interrupt and <code>0</code> if the file was previously loaded</li>
	 * <li>when an error occurred <code>X00</code> will be set to <code>-1</code></li>
	 * </ul>
	 * </ul>
	 */
	public static final long INT_GET_FILE                     = 65L;
	/**
	 * the total number of default interrupts
	 * </ul>
	 */
	public static final long INTERRUPT_COUNT                  = 66L;
	/**
	 * the maximum/greatest number
	 * </ul>
	 */
	public static final long MAX_VALUE                        = 0x7fffffffffffffffL;
	/**
	 * the minimum/lowest number
	 * </ul>
	 */
	public static final long MIN_VALUE                        = 0x8000000000000000L;
	/**
	 * the STREAM-ID of the default input stream
	 * </ul>
	 * <ul>
	 * this stream is initially open and does not support write operations
	 * </ul>
	 * <ul>
	 * this stream is no fs-stream (thus the position can not be set)
	 * </ul>
	 */
	public static final long STD_IN                           = 0L;
	/**
	 * the STREAM-ID of the default output stream
	 * </ul>
	 * <ul>
	 * this stream is initially open and does not support read operations
	 * </ul>
	 * <ul>
	 * this stream is no fs-stream (thus the position can not be set)
	 * </ul>
	 */
	public static final long STD_OUT                          = 1L;
	/**
	 * the STREAM-ID of the default log output stream
	 * </ul>
	 * <ul>
	 * this stream is initially open and does not support read operations
	 * </ul>
	 * <ul>
	 * this stream is no fs-stream (thus the position can not be set)
	 * </ul>
	 */
	public static final long STD_LOG                          = 2L;
	/**
	 * open option: open file for read access
	 * </ul>
	 */
	public static final long OPEN_READ                        = 0x0000000000000001L;
	/**
	 * open option: open file for write access
	 * </ul>
	 */
	public static final long OPEN_WRITE                       = 0x0000000000000002L;
	/**
	 * open option: open file for append access (implicit set of <code>OPEN_WRITE</code>)
	 * </ul>
	 */
	public static final long OPEN_APPEND                      = 0x0000000000000004L;
	/**
	 * open option: open file or create file (needs <code>OPEN_WRITE</code>, not compatible with <code>OPEN_NEW_FILE</code>)
	 * </ul>
	 */
	public static final long OPEN_CREATE                      = 0x0000000000000008L;
	/**
	 * open option: fail if file already exists or create the file (needs <code>OPEN_WRITE</code>, not compatible with <code>OPEN_CREATE</code>)
	 * </ul>
	 */
	public static final long OPEN_NEW_FILE                    = 0x0000000000000010L;
	/**
	 * open option: if the file already exists, remove its content (needs <code>OPEN_WRITE</code>)
	 * </ul>
	 */
	public static final long OPEN_TRUNCATE                    = 0x0000000000000020L;
	/**
	 * the offset of the fs-element file pointer of the stream
	 * </ul>
	 */
	public static final long FS_STREAM_OFFSET_FILE            = 0x0000000000000000L;
	/**
	 * the offset of the current position of the stream
	 * </ul>
	 */
	public static final long FS_STREAM_OFFSET_POS             = 0x0000000000000008L;
	/**
	 * the offset of the ID of a fs-element
	 * </ul>
	 */
	public static final long FS_ELEMENT_OFFSET_ID             = 0x0000000000000000L;
	/**
	 * the offset of the lock to be used of a fs-element
	 * </ul>
	 */
	public static final long FS_ELEMENT_OFFSET_LOCK           = 0x0000000000000008L;
	/**
	 * the lock flag to specify that no read operations are allowed
	 * </ul>
	 */
	public static final long LOCK_NO_READ_ALLOWED             = 0x0000000100000000L;
	/**
	 * the lock flag to specify that no write operations are allowed
	 * </ul>
	 */
	public static final long LOCK_NO_WRITE_ALLOWED_LOCK       = 0x0000000200000000L;
	/**
	 * the lock flag to specify that the element is not allowed to be deleted
	 * </ul>
	 */
	public static final long LOCK_NO_DELETE_ALLOWED_LOCK      = 0x0000000400000000L;
	/**
	 * the lock flag to specify that no direct metadata changes are allowed
	 * </ul>
	 * <ul>
	 * indirect metadata changes are still allowed (with write operations setting the last mod (and last meta mod) time)
	 * </ul>
	 */
	public static final long LOCK_NO_META_CHANGE_ALLOWED_LOCK = 0x0000000800000000L;
	/**
	 * the lock flag to specify that the lock is a shared lock
	 * </ul>
	 */
	public static final long LOCK_SHARED_LOCK                 = 0x4000000000000000L;
	/**
	 * the lock flag to specify that the lock is a lock
	 * </ul>
	 * <ul>
	 * this flag allows to have a exclusive lock without blocking any operations
	 * </ul>
	 */
	public static final long LOCK_LOCKED_LOCK                 = 0x8000000000000000L;
	/**
	 * this number is used to specify when no lock should be used
	 * </ul>
	 */
	public static final long LOCK_NO_LOCK                     = 0x0000000000000000L;
	/**
	 * the flag marks an element as folder
	 * </ul>
	 */
	public static final long FLAG_FOLDER                      = 0x00000001L;
	/**
	 * the flag marks an element as file
	 * </ul>
	 */
	public static final long FLAG_FILE                        = 0x00000002L;
	/**
	 * the flag marks an element as link
	 * </ul>
	 * <ul>
	 * note that links are also marked as folder or file
	 * </ul>
	 */
	public static final long FLAG_LINK                        = 0x00000004L;
	/**
	 * the flag marks an element as read only
	 * </ul>
	 */
	public static final long FLAG_READ_ONLY                   = 0x00000008L;
	/**
	 * the flag marks an element as executable
	 * </ul>
	 */
	public static final long FLAG_EXECUTABLE                  = 0x00000010L;
	/**
	 * the flag marks an element as hidden
	 * </ul>
	 */
	public static final long FLAG_HIDDEN                      = 0x00000020L;
	/**
	 * the flag marks an folder as sorted
	 * </ul>
	 * <ul>
	 * this flag does not has any real use yet
	 * </ul>
	 */
	public static final long FLAG_FOLDER_SORTED               = 0x00000040L;
	/**
	 * the flag marks an file as encrypted
	 * </ul>
	 * <ul>
	 * this flag does not has any real use for the file system
	 * </ul>
	 */
	public static final long FLAG_FILE_ENCRYPTED              = 0x00000080L;
	/**
	 * a floating-point-number to specify a Not-A-Number constant
	 * </ul>
	 */
	public static final long FP_NAN                           = 0x7ffe000000000000L;
	/**
	 * the maximum/greatest floating-point-number
	 * </ul>
	 */
	public static final long FP_MAX_VALUE                     = 0x7fefffffffffffffL;
	/**
	 * the minimum/lowest floating-point-number
	 * </ul>
	 */
	public static final long FP_MIN_VALUE                     = 0x0000000000000001L;
	/**
	 * the floating-point-number for positiv infinity
	 * </ul>
	 */
	public static final long FP_POS_INFINITY                  = 0x7ff0000000000000L;
	/**
	 * the floating-point-number for negative infinity
	 * </ul>
	 */
	public static final long FP_NEG_INFINITY                  = 0xfff0000000000000L;
	/**
	 * the status flag used when on the last compare (CMP A, B) A was lower than B
	 * </ul>
	 */
	public static final long STATUS_LOWER                     = 0x0000000000000001L;
	/**
	 * the status flag used when on the last compare (CMP A, B) A was greater than B
	 * </ul>
	 */
	public static final long STATUS_GREATHER                  = 0x0000000000000002L;
	/**
	 * the status flag used when on the last compare (CMP A, B) A was equal to B
	 * </ul>
	 */
	public static final long STATUS_EQUAL                     = 0x0000000000000004L;
	/**
	 * the status flag used when an overflow occurred
	 * </ul>
	 */
	public static final long STATUS_CARRY                     = 0x0000000000000008L;
	/**
	 * the status flag used when an mathematical (arithmetic or logic) operation had zero as result
	 * </ul>
	 */
	public static final long STATUS_ZERO                      = 0x0000000000000010L;
	/**
	 * the status flag used when an mathematical operation had a NaN as result
	 * </ul>
	 */
	public static final long STATUS_NAN                       = 0x0000000000000020L;
	/**
	 * the status flag used when on the last bit-compare (BCP A, B) A & B was B (and B != zero)
	 * </ul>
	 */
	public static final long STATUS_ALL_BITS                  = 0x0000000000000040L;
	/**
	 * the status flag used when on the last bit-compare (BCP A, B) A & B was not zero
	 * </ul>
	 */
	public static final long STATUS_SOME_BITS                 = 0x0000000000000080L;
	/**
	 * the status flag used when on the last bit-compare (BCP A, B) A & B was zero
	 * </ul>
	 */
	public static final long STATUS_NONE_BITS                 = 0x0000000000000100L;
	/**
	 * the status flag indicates a invalid fs-element type (file expected but folder was used or in reverse)
	 * </ul>
	 */
	public static final long STATUS_ELEMENT_WRONG_TYPE        = 0x0040000000000000L;
	/**
	 * the status flag indicates that a element was ought to exist but did not exist
	 * </ul>
	 */
	public static final long STATUS_ELEMENT_NOT_EXIST         = 0x0080000000000000L;
	/**
	 * the status flag indicates that a element was not ought to exist but did already exist
	 * </ul>
	 */
	public static final long STATUS_ELEMENT_ALREADY_EXIST     = 0x0100000000000000L;
	/**
	 * the status flag indicates that the file system could not reserve enough space for some reason
	 * </ul>
	 */
	public static final long STATUS_OUT_OF_SPACE              = 0x0200000000000000L;
	/**
	 * the status flag indicates that a element was marked as read only, but a write operation was tried
	 * </ul>
	 */
	public static final long STATUS_READ_ONLY                 = 0x0400000000000000L;
	/**
	 * the status flag indicates that a different lock was used or the lock of the element forbid the tried operation
	 * </ul>
	 */
	public static final long STATUS_ELEMENT_LOCKED            = 0x0800000000000000L;
	/**
	 * the status flag indicates that an undefined IO error occurred
	 * </ul>
	 */
	public static final long STATUS_IO_ERR                    = 0x1000000000000000L;
	/**
	 * the status flag indicates that an interrupt was misused
	 * </ul>
	 */
	public static final long STATUS_ILLEGAL_ARG               = 0x2000000000000000L;
	/**
	 * the status flag indicates the system could not reserve enough memory
	 * </ul>
	 */
	public static final long STATUS_OUT_OF_MEMORY             = 0x4000000000000000L;
	/**
	 * the status flag indicates that some unspecified error occurred
	 * </ul>
	 * <ul>
	 * this flag will not be generated by default interrupts
	 * </ul>
	 */
	public static final long STATUS_ERROR                     = 0x8000000000000000L;
	/**
	 * the start address of the registers
	 */
	public static final long REGISTER_MEMORY_START            = 0x0000000000001000L;
	/**
	 * the last address of the last registers (<code>XF9</code>)
	 */
	public static final long REGISTER_MEMORY_LAST_ADDRESS     = 0x00000000000018F8L;
	
}
