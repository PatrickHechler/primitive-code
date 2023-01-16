package de.hechler.patrick.codesprachen.primitive.core.utils;

public class PrimAsmPreDefines {
	
	private PrimAsmPreDefines() {}
	
	/**
	 * illegal interrupt
	 * <ul>
	 * <li><code>X00</code> contains the number of the illegal interrupt</li>
	 * <li>exits with <code>(128 + illegal_interrup_number)</code> (without calling the exit interrupt)</li>
	 * <li>if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with
	 * <code>128</code></li>
	 * </ul>
	 */
	public static final long INT_ERRORS_ILLEGAL_INTERRUPT         = 0L;
	/**
	 * unknown command
	 * <ul>
	 * <li>exits with <code>7</code> (without calling the exit interrupt)</li>
	 * </ul>
	 */
	public static final long INT_ERRORS_UNKNOWN_COMMAND           = 1L;
	/**
	 * illegal memory
	 * <ul>
	 * <li>exits with <code>6</code> (without calling the exit interrupt)</li>
	 * </ul>
	 */
	public static final long INT_ERRORS_ILLEGAL_MEMORY            = 2L;
	/**
	 * arithmetic error
	 * <ul>
	 * <li>exits with <code>5</code> (without calling the exit interrupt)</li>
	 * </ul>
	 */
	public static final long INT_ERRORS_ARITHMETIC_ERROR          = 3L;
	/**
	 * exit
	 * <ul>
	 * <li>use <code>X00</code> to specify the exit number of the progress</li>
	 * </ul>
	 */
	public static final long INT_EXIT                             = 4L;
	/**
	 * allocate a memory-block
	 * <ul>
	 * <li><code>X00</code> saves the size of the block</li>
	 * <li>if the value of <code>X00` is `-1</code> after the call the memory-block could not be allocated</li>
	 * <li>if the value of <code>X00` is not `-1`, `X00</code> points to the first element of the allocated
	 * memory-block</li>
	 * </ul>
	 */
	public static final long INT_MEMORY_ALLOC                     = 5L;
	/**
	 * reallocate a memory-block
	 * <ul>
	 * <li><code>X00</code> points to the memory-block</li>
	 * <li><code>X01</code> is set to the new size of the memory-block</li>
	 * <li><code>X01` will be `-1</code> if the memory-block could not be reallocated, the old memory-block will remain
	 * valid and should be freed if it is not longer needed</li>
	 * <li><code>X01</code> will point to the new memory block, the old memory-block was automatically freed, so it
	 * should not be used, the new block should be freed if it is not longer needed</li>
	 * </ul>
	 */
	public static final long INT_MEMORY_REALLOC                   = 6L;
	/**
	 * free a memory-block
	 * <ul>
	 * <li><code>X00</code> points to the old memory-block</li>
	 * <li>after this the memory-block should not be used</li>
	 * </ul>
	 */
	public static final long INT_MEMORY_FREE                      = 7L;
	/**
	 * open new stream
	 * <ul>
	 * <li><code>X00</code> contains a pointer to the STRING, which refers to the file which should be read</li>
	 * <li><code>X01</code> specfies the open mode: (bitwise flags)</li>
	 * <li><code>OPEN_ONLY_CREATE</code></li>
	 * <li>fail if the file/pipe exist already</li>
	 * <li>when this flags is set either <code>OPEN_FILE` or `OPEN_PIPE</code> has to be set</li>
	 * <li><code>OPEN_ALSO_CREATE</code></li>
	 * <li>create the file/pipe if it does not exist, but do not fail if the file/pipe exist already (overwritten by
	 * PFS_SO_ONLY_CREATE)</li>
	 * <li><code>OPEN_FILE</code></li>
	 * <li>fail if the element is a pipe and if a create flag is set create a file if the element does not exist
	 * already</li>
	 * <li>this flag is not compatible with <code>OPEN_PIPE</code></li>
	 * <li><code>OPEN_PIPE</code></li>
	 * <li>fail if the element is a file and if a create flag is set create a pipe</li>
	 * <li>this flag is not compatible with <code>OPEN_FILE</code></li>
	 * <li><code>OPEN_READ</code></li>
	 * <li>open the stream for read access</li>
	 * <li><code>OPEN_WRITE</code></li>
	 * <li>open the stream for write access</li>
	 * <li><code>OPEN_APPEND</code></li>
	 * <li>open the stream for append access (before every write operation the position is set to the end of the
	 * file)</li>
	 * <li>implicitly also sets
	 * <code>OPEN_WRITE` (for pipes there is no diffrence in `OPEN_WRITE` and `OPEN_APPEND</code>)</li>
	 * <li><code>OPEN_FILE_TRUNCATE</code></li>
	 * <li>truncate the files content</li>
	 * <li>implicitly sets <code>OPEN_FILE</code></li>
	 * <li>nop when also <code>OPEN_ONLY_CREATE</code> is set</li>
	 * <li><code>OPEN_FILE_EOF</code></li>
	 * <li>set the position initially to the end of the file not the start</li>
	 * <li>ignored when opening a pipe</li>
	 * <li>other flags will be ignored</li>
	 * <li>the operation will fail if it is not spezified if the file should be opened for read, write and/or
	 * append</li>
	 * <li>opens a new stream to the specified file</li>
	 * <li>if successfully the STREAM-ID will be saved in the <code>X00</code> register</li>
	 * <li>if failed <code>X00` will contain `-1</code></li>
	 * <li>to close the stream use the stream close interrupt (<code>INT_STREAM_CLOSE</code>)</li>
	 * </ul>
	 */
	public static final long INT_OPEN_STREAM                      = 8L;
	/**
	 * write
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to write</li>
	 * <li><code>X02</code> points to the elements to write</li>
	 * <li><code>X01</code> will be set to the number of written bytes.</li>
	 * </ul>
	 */
	public static final long INT_STREAMS_WRITE                    = 9L;
	/**
	 * read
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> contains the number of elements to read</li>
	 * <li><code>X02</code> points to the elements to read</li>
	 * <li>after execution <code>X01</code> will contain the number of elements, which has been read.</li>
	 * <li>when the value is less than len either an error occured or end of file/pipe has reached (which is not
	 * considered an error)</li>
	 * </ul>
	 */
	public static final long INT_STREAMS_READ                     = 10L;
	/**
	 * stream close
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X00</code> will be set to 1 on success and 0 on error</li>
	 * </ul>
	 */
	public static final long INT_STREAMS_CLOSE                    = 11L;
	/**
	 * stream file get position
	 * <ul>
	 * <li><code>X00</code> contains the STREAM/FILE_STREAM-ID</li>
	 * <li><code>X01</code> will be set to the stream position or -1 on error</li>
	 * </ul>
	 */
	public static final long INT_STREAMS_FILE_GET_POS             = 12L;
	/**
	 * stream file set position
	 * <ul>
	 * <li><code>X00</code> contains the STREAM/FILE_STREAM-ID</li>
	 * <li><code>X01</code> contains the new position of the stream</li>
	 * <li><code>X01</code> will be set to 1 or 0 on error</li>
	 * <li>note that it is possible to set the stream position behind the end of the file.</li>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</li>
	 * </ul>
	 */
	public static final long INT_STREAMS_FILE_SET_POS             = 13L;
	/**
	 * stream file add position
	 * <ul>
	 * <li><code>X00</code> contains the STREAM/FILE_STREAM-ID</li>
	 * <li><code>X01</code> contains the value, which should be added to the position of the stream</li>
	 * <li><code>X01` is allowed to be negative, but the sum of the old position and `X01</code> is not allowed to be
	 * negative</li>
	 * <li><code>X01</code> will be set to the new position or -1 on error</li>
	 * <li>note that it is possible to set the stream position behind the end of the file.</li>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</li>
	 * </ul>
	 */
	public static final long INT_STREAMS_FILE_ADD_POS             = 14L;
	/**
	 * stream file seek eof
	 * <ul>
	 * <li><code>X00</code> contains the STREAM-ID</li>
	 * <li><code>X01</code> will be set to the new position of the stream or -1 on error</li>
	 * <li>sets the position of the stream to the end of the file (the file length)</li>
	 * </ul>
	 */
	public static final long INT_STREAMS_FILE_SEEK_EOF            = 15L;
	/**
	 * open element handle file
	 * <ul>
	 * <li><code>X00` points to the `STRING</code> which contains the path of the file to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM/FILE-ID or -1 on error</li>
	 * <li>this operation will fail if the element is no file</li>
	 * </ul>
	 */
	public static final long INT_OPEN_FILE                        = 16L;
	/**
	 * open element handle folder
	 * <ul>
	 * <li><code>X00` points to the `STRING</code> which contains the path of the folder to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM/FOLDER-ID or -1 on error</li>
	 * <li>this operation will fail if the element is no folder</li>
	 * </ul>
	 */
	public static final long INT_OPEN_FOLDER                      = 17L;
	/**
	 * open element handle pipe
	 * <ul>
	 * <li><code>X00` points to the `STRING</code> which contains the path of the pipe to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM/PIPE-ID or -1 on error</li>
	 * <li>this operation will fail if the element is no pipe</li>
	 * </ul>
	 */
	public static final long INT_OPEN_PIPE                        = 18L;
	/**
	 * open element handle (any)
	 * <ul>
	 * <li><code>X00` points to the `STRING</code> which contains the path of the element to be opened</li>
	 * <li><code>X00</code> will be set to the newly opened STREAM-ID or -1 on error</li>
	 * </ul>
	 */
	public static final long INT_OPEN_ELEMENT                     = 19L;
	/**
	 * element open parent handle
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X00</code> will be set to the newly opened ELEMENT/FOLDER-ID or -1 on error</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_OPEN_PARENT              = 20L;
	/**
	 * get create date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01` will be set to the create date or `-1</code> on error</li>
	 * <li>note that <code>-1` may be the create date of the element, so check `ERRNO</code> instead</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_GET_CREATE               = 21L;
	/**
	 * get last mod date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01` will be set to the last modified date or `-1</code> on error</li>
	 * <li>note that <code>-1` may be the last modified date of the element, so check `ERRNO</code> instead</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_GET_LAST_MOD             = 22L;
	/**
	 * set create date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X00</code> contains the new create date of the element</li>
	 * <li><code>X01` will be set to `1` or `0</code> on error</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_SET_CREATE               = 23L;
	/**
	 * set last modified date
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X00</code> contains the last modified date of the element</li>
	 * <li><code>X01` will be set to `1` or `0</code> on error</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_SET_LAST_MOD             = 24L;
	/**
	 * element delete
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li>note that this operation automatically closes the given ELEMENT-ID, the close interrupt should not be invoked
	 * after this interrupt returned</li>
	 * <li><code>X01` will be set to `1` or `0</code> on error</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_DELETE                   = 25L;
	/**
	 * element move
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01` points to a STRING which will be the new name or it is set to `-1</code> if the name should not be
	 * changed</li>
	 * <li><code>X02` contains the ELEMENT-ID of the new parent of `-1</code> if the new parent should not be
	 * changed</li>
	 * <li>when both <code>X01` and `X02` are set to `-1</code> this operation will do nothing</li>
	 * <li><code>X01` will be set to `1` or `0</code> on error</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_MOVE                     = 26L;
	/**
	 * element get name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> points the the a memory block, which should be used to store the name as a STRING</li>
	 * <li>when <code>X01` is set to `-1</code> a new memory block will be allocated</li>
	 * <li>on success <code>X01</code> will point to the name as STRING representation</li>
	 * <li>when the memory block is not large enugh, it will be resized</li>
	 * <li>note that when <code>X01</code> does not point to the start of the memory block the start of the memory block
	 * can still be moved during the reallocation</li>
	 * <li>on error <code>X01` will be set to `-1</code></li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_GET_NAME                 = 27L;
	/**
	 * element get flags
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01` will be set to the flags or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_GET_FLAGS                = 28L;
	/**
	 * element modify flags
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT-ID</li>
	 * <li><code>X01</code> contains the flags to be added</li>
	 * <li><code>X02</code> contains the flags to be removed</li>
	 * <li>note that only the low 32 bit will be used and the high 32 bit will be ignored</li>
	 * <li><code>X01` will be set to `1` or `0</code> on error</li>
	 * </ul>
	 */
	public static final long INT_ELEMENT_MODIFY_FLAGS             = 29L;
	/**
	 * element folder child count
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01` will be set to the number of child elements the folder has or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_CHILD_COUNT               = 30L;
	/**
	 * folder get child of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li><code>X01` will be set to a newly opened ELEMENT-ID for the child or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_OPEN_CHILD_OF_NAME        = 31L;
	/**
	 * folder get child folder of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li>this operation will fail if the child is no folder</li>
	 * <li><code>X01` will be set to a newly opened ELEMENT/FOLDER-ID for the child or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME = 32L;
	/**
	 * folder get child file of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li>this operation will fail if the child is no file</li>
	 * <li><code>X01` will be set to a newly opened ELEMENT/FILE-ID for the child or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_OPEN_CHILD_FILE_OF_NAME   = 33L;
	/**
	 * folder get child pipe of name
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li>this operation will fail if the child is no pipe</li>
	 * <li><code>X01` will be set to a newly opened ELEMENT/PIPE-ID for the child or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME   = 34L;
	/**
	 * folder add child folder
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X00</code> points to a STRING with the name of the child</li>
	 * <li><code>X01` will be set to a newly opened/created ELEMENT/FOLDER-ID for the child or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_CREATE_CHILD_FOLDER       = 35L;
	/**
	 * folder add child file
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>X01` will be set to a newly opened/created ELEMENT/FILE-ID for the child or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_CREATE_CHILD_FILE         = 36L;
	/**
	 * folder add child pipe
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01</code> points to the STRING name of the new child element</li>
	 * <li><code>X01` will be set to a newly opened/created ELEMENT/PIPE-ID for the child or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_CREATE_CHILD_PIPE         = 37L;
	/**
	 * open child iterator of folder
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01` is set to `0</code> if hidden files should be skipped and any other value if not</li>
	 * <li><code>X01` will be set to the FOLDER-ITER-ID or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FOLDER_OPEN_ITER                 = 38L;
	/**
	 * get the length of a file
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FILE-ID</li>
	 * <li><code>X01` will be set to the file length in bytes or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FILE_LENGTH                      = 39L;
	/**
	 * set the length of a file
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FILE-ID</li>
	 * <li><code>X01</code> is set to the new length of the file</li>
	 * <li>this interrupt will append zeros to the file when the new length is larger than the old length or remove all
	 * content after the new length</li>
	 * <li><code>X01` will be set `1` on success or `0</code> on error</li>
	 * </ul>
	 */
	public static final long INT_FILE_TRUNCATE                    = 40L;
	/**
	 * opens a stream from a file or pipe handle
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/FILE/PIPE-ID</li>
	 * <li>note that this interrupt works for both files and pipes, but will fail for folders</li>
	 * <li><code>X01</code> is set to the open flags</li>
	 * <li>note that the high 32-bit of the flags are ignored</li>
	 * <li><code>X01` will be set to the STREAM-ID or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_HANDLE_OPEN_STREAM               = 41L;
	/**
	 * get the length of a pipe
	 * <ul>
	 * <li><code>X00</code> contains the ELEMENT/PIPE-ID</li>
	 * <li><code>X01` will be set to the pipe length in bytes or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_PIPE_LENGTH                      = 42L;
	/**
	 * get the current system time
	 * <ul>
	 * <li><code>X00` will be set to `1` on success and `0</code> on error</li>
	 * <li><code>X01</code> will be set to the curent system time in seconds since the epoch</li>
	 * <li><code>X02</code> will be set to the additional curent system time in nanoseconds</li>
	 * </ul>
	 */
	public static final long INT_TIME_GET                         = 43L;
	/**
	 * get the system time resolution
	 * <ul>
	 * <li><code>X00` will be set to `1` on success and `0</code> on error</li>
	 * <li><code>X01</code> will be set to the resolution in seconds</li>
	 * <li><code>X02</code> will be set to the additional resolution in nanoseconds</li>
	 * </ul>
	 */
	public static final long INT_TIME_RES                         = 44L;
	/**
	 * to sleep the given time in nanoseconds
	 * <ul>
	 * <li><code>X00` contain the number of nanoseconds to wait (only values from `0` to `999999999</code> are
	 * allowed)</li>
	 * <li><code>X01` contain the number of seconds to wait (only values greather or equal to `0</code> are
	 * allowed)</li>
	 * <li><code>X00` and `X01` will contain the remaining time (both `0</code> if it finished waiting)</li>
	 * <li><code>X02` will be `1` if the call was successfully and `0</code> if something went wrong</li>
	 * <li><code>X00</code> will not be negative if the progress waited too long</li>
	 * </ul>
	 */
	public static final long INT_TIME_SLEEP                       = 45L;
	/**
	 * to wait the given time in nanoseconds
	 * <ul>
	 * <li><code>X00</code> contain the number of seconds since the epoch</li>
	 * <li><code>X01</code> contain the additional number of nanoseconds</li>
	 * <li>this interrupt will wait until the current system time is equal or after the given absolute time.</li>
	 * <li><code>X00` and `X01` will contain the remaining time (both `0</code> if it finished waiting)</li>
	 * <li><code>X02` will be `1` if the call was successfully and `0</code> if something went wrong</li>
	 * </ul>
	 */
	public static final long INT_TIME_WAIT                        = 46L;
	/**
	 * open a read stream which delivers random values
	 * <ul>
	 * <li><code>X00` will be set to the STREAM-ID or `-1</code> on error</li>
	 * <li>the stream will only support read operations</li>
	 * <li>not write/append or seek/setpos operations</li>
	 * </ul>
	 */
	public static final long INT_RND_OPEN                         = 47L;
	/**
	 * sets <code>X00</code> to a random number
	 * <ul>
	 * <li><code>X00` will be set to a random non negative number or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_RND_NUM                          = 48L;
	/**
	 * memory copy
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function has undefined behavior if the two blocks overlap</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</li>
	 * </ul>
	 */
	public static final long INT_MEM_CPY                          = 49L;
	/**
	 * memory move
	 * <ul>
	 * <li>copies a block of memory</li>
	 * <li>this function makes sure, that the original values of the source block are copied to the target block (even
	 * if the two block overlap)</li>
	 * <li><code>X00</code> points to the target memory block</li>
	 * <li><code>X01</code> points to the source memory block</li>
	 * <li><code>X02</code> has the length of bytes to bee copied</li>
	 * </ul>
	 */
	public static final long INT_MEM_MOV                          = 50L;
	/**
	 * memory byte set
	 * <ul>
	 * <li>sets a memory block to the given byte-value</li>
	 * <li><code>X00</code> points to the block</li>
	 * <li><code>X01</code> the first byte contains the value to be written to each byte</li>
	 * <li><code>X02</code> contains the length in bytes</li>
	 * </ul>
	 */
	public static final long INT_MEM_BSET                         = 51L;
	/**
	 * string length
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00` will be set to the length of the string/ the (byte-)offset of the first byte from the `'\0'</code>
	 * character</li>
	 * </ul>
	 */
	public static final long INT_STR_LEN                          = 52L;
	/**
	 * string compare
	 * <ul>
	 * <li><code>X00</code> points to the first STRING</li>
	 * <li><code>X01</code> points to the second STRING</li>
	 * <li><code>X00</code> will be set to zero if both are equal STRINGs, a value greather zero if the first is
	 * greather and below zero if the second is greather</li>
	 * <li>a STRING is greather if the first missmatching char has numeric greather value</li>
	 * </ul>
	 */
	public static final long INT_STR_CMP                          = 53L;
	/**
	 * number to string
	 * <ul>
	 * <li><code>X00</code> is set to the number to convert</li>
	 * <li><code>X01</code> is points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> contains the base of the number system</li>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></li>
	 * <li><code>X03</code> is set to the length of the buffer</li>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</li>
	 * <li><code>X00` will be set to the size of the STRING (without the `\0</code> terminating character)</li>
	 * <li><code>X01</code> will be set to the new buffer</li>
	 * <li><code>X03</code> will be set to the new size of the buffer</li>
	 * <li>the new length will be the old length or if the old length is smaller than the size of the STRING (with
	 * <code>\0`) than the size of the STRING (with `\0</code>)</li>
	 * <li>on error <code>X01` will be set to `-1</code></li>
	 * </ul>
	 */
	public static final long INT_STR_FROM_NUM                     = 54L;
	/**
	 * floating point number to string
	 * <ul>
	 * <li><code>X00</code> is set to the floating point number to convert</li>
	 * <li><code>X01</code> points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> is set to the current size of the buffer</li>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</li>
	 * <li><code>X00</code> will be set to the size of the STRING</li>
	 * <li><code>X01</code> will be set to the new buffer</li>
	 * <li><code>X02</code> will be set to the new size of the buffer</li>
	 * <li>the new length will be the old length or if the old length is smaller than the size of the STRING (with
	 * <code>\0`) than the size of the STRING (with `\0</code>)</li>
	 * <li>on error <code>X01` will be set to `-1</code></li>
	 * </ul>
	 */
	public static final long INT_STR_FROM_FPNUM                   = 55L;
	/**
	 * string to number
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X01</code> points to the base of the number system</li>
	 * <li>(for example <code>10` for the decimal system or `2</code> for the binary system)</li>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></li>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * <li>on success <code>X01` will be set to `1</code></li>
	 * <li>on error <code>X01` will be set to `0</code></li>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</li>
	 * <li>if
	 * <code>ERRNO` is set to out of range, the string value displayed a value outside of the 64-bit number range and `X00</code>
	 * will either be min or max value</li>
	 * </ul>
	 */
	public static final long INT_STR_TO_NUM                       = 56L;
	/**
	 * string to floating point number
	 * <ul>
	 * <li><code>X00</code> points to the STRING</li>
	 * <li><code>X00</code> will be set to the converted number</li>
	 * <li>on success <code>X01` will be set to `1</code></li>
	 * <li>on error <code>X01` will be set to `0</code></li>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</li>
	 * </ul>
	 */
	public static final long INT_STR_TO_FPNUM                     = 57L;
	/**
	 * STRING to U16-STRING
	 * <ul>
	 * <li><code>X00` points to the STRING (`UTF-8</code>)</li>
	 * <li><code>X01` points to the buffer to be filled with the to `UTF-16</code> converted string</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li><code>X00` points to the start of the unconverted sequenze (or behind the `\0</code> terminator)</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03` will be set to the number of converted characters or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_STR_TO_U16STR                    = 58L;
	/**
	 * STRING to U32-STRING
	 * <ul>
	 * <li><code>X00` points to the STRING (`UTF-8</code>)</li>
	 * <li><code>X01` points to the buffer to be filled with the to `UTF-32</code> converted string</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li><code>X00` points to the start of the unconverted sequenze (or behind the `\0</code> terminator)</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03` will be set to the number of converted characters or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_STR_TO_U32STR                    = 59L;
	/**
	 * U16-STRING to STRING
	 * <ul>
	 * <li><code>X00` points to the `UTF-16</code> STRING</li>
	 * <li><code>X01` points to the buffer to be filled with the converted STRING (`UTF-8</code>)</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li>
	 * <code>X00` points to the start of the unconverted sequenze (or behind the `\0` terminator (note that the `\0</code>
	 * char needs two bytes))</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03` will be set to the number of converted characters or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_STR_FROM_U16STR                  = 60L;
	/**
	 * U32-STRING to STRING
	 * <ul>
	 * <li><code>X00` points to the `UTF-32</code> STRING</li>
	 * <li><code>X01` points to the buffer to be filled with the converted STRING (`UTF-8</code>)</li>
	 * <li><code>X02</code> is set to the length of the buffer</li>
	 * <li>
	 * <code>X00` points to the start of the unconverted sequenze (or behind the `\0` terminator (note that the `\0</code>
	 * char needs four bytes))</li>
	 * <li><code>X01</code> points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03` will be set to the number of converted characters or `-1</code> on error</li>
	 * </ul>
	 */
	public static final long INT_STR_FROM_U32STR                  = 61L;
	/**
	 * format string
	 * <ul>
	 * <li><code>X00</code> is set to the STRING input</li>
	 * <li><code>X01</code> contains the buffer for the STRING output</li>
	 * <li><code>X02</code> is the size of the buffer in bytes</li>
	 * <li>the register <code>X03</code> points to the formatting arguments</li>
	 * <li><code>X00` will be set to the length of the output string (the offset of the `\0` character) or `-1</code> on
	 * error</li>
	 * <li>if <code>X00` is larger or equal to `X02`, only the first `X02</code> bytes will be written to the
	 * buffer</li>
	 * <li>formatting:</li>
	 * <li><code>%%`: to escape an `%` character (only one `%</code> will be in the formatted STRING)</li>
	 * <li><code>%s</code>: the next argument points to a STRING, which should be inserted here</li>
	 * <li><code>%c</code>: the next argument starts with a byte, which should be inserted here</li>
	 * <li><code>%n</code>: consumes two arguments: the first is the base (<code>2..36</code>) and the second is the
	 * number to convert with the given base</li>
	 * <li><code>%d</code>: the next argument contains a number, which should be converted to a STRING using the decimal
	 * number system and than be inserted here</li>
	 * <li><code>%f</code>: the next argument contains a floating point number, which should be converted to a STRING
	 * and than be inserted here</li>
	 * <li><code>%p</code>: the next argument contains a pointer, which should be converted to a STRING. if the pointer
	 * is <code>-1</code> the value will be "p-inval", otherwise "p-" together with the hex representation of the
	 * pointer</li>
	 * <li><code>%h</code>: the next argument contains a number, which should be converted to a STRING using the
	 * hexadecimal number system and than be inserted here</li>
	 * <li><code>%b</code>: the next argument contains a number, which should be converted to a STRING using the binary
	 * number system and than be inserted here</li>
	 * <li><code>%o</code>: the next argument contains a number, which should be converted to a STRING using the octal
	 * number system and than be inserted here</li>
	 * </ul>
	 */
	public static final long INT_STR_FORMAT                       = 62L;
	/**
	 * load a file
	 * <ul>
	 * <li><code>X00</code> is set to the path (inclusive name) of the file</li>
	 * <li><code>X00` will point to the memory block, in which the file has been loaded or `-1</code> on error</li>
	 * <li><code>X01</code> will be set to the length of the file (and the memory block)</li>
	 * </ul>
	 */
	public static final long INT_LOAD_FILE                        = 63L;
	/**
	 * load a library file
	 * <ul>
	 * <li>similar like the load file interrupt loads a file for the program.</li>
	 * <li>the difference is that this interrupt may remember which files has been loaded</li>
	 * <li>there are no guarantees, when the same memory block is reused and when a new memory block is created</li>
	 * <li>the other difference is that the file may only be unloaded with the unload lib interrupt (not with the free
	 * interrupt)</li>
	 * <li>the returned memory block also can not be resized</li>
	 * <li>if the interrupt is executed multiple times with the same file, it will return every time the same memory
	 * block.</li>
	 * <li>this interrupt does not recognize files loaded with the <code>64` (`INT_LOAD_FILE</code>) interrupt.</li>
	 * <li><code>X00</code> is set to the path (inclusive name) of the file</li>
	 * <li><code>X00</code> will point to the memory block, in which the file has been loaded</li>
	 * <li><code>X01</code> will be set to the length of the file (and the memory block)</li>
	 * <li><code>X02` will be set to `1` if the file has been loaded as result of this interrupt and `0</code> if the
	 * file was previously loaded</li>
	 * <li>when an error occurred <code>X00` will be set to `-1</code></li>
	 * </ul>
	 */
	public static final long INT_LOAD_LIB                         = 64L;
	/**
	 * unload a library file
	 * <ul>
	 * <li>unloads a library previously loaded with the load lib interrupt</li>
	 * <li>this interrupt will ensure that the given memory block will be freed and never again be returned from the
	 * load lib interrupt</li>
	 * <li><code>X00</code> points to the (start of the) memory block</li>
	 * </ul>
	 */
	public static final long INT_UNLOAD_LIB                       = 65L;
	/**
	 * the initial value of the <code>INTCNT</code> register this constant holds the number of default interrupts
	 */
	public static final long INTERRUPT_COUNT                      = 76L;
	/**
	 * NaN the floating point constant holding a nan value
	 */
	public static final long FP_NAN                               = 0x7FFE000000000000L;
	/**
	 * floating point max
	 */
	public static final long FP_MAX_VALUE                         = 0x7FEFFFFFFFFFFFFFL;
	/**
	 * floating point min
	 */
	public static final long FP_MIN_VALUE                         = 0x0000000000000001L;
	/**
	 * floating point constant for positive infinity
	 */
	public static final long FP_POS_INFINITY                      = 0x7FF0000000000000L;
	/**
	 * floating point constant for positive infinity
	 */
	public static final long FP_NEG_INFINITY                      = 0xFFF0000000000000L;
	/**
	 * the first memory address of the register memory block note that X00 is not the first register, but IP
	 */
	public static final long REGISTER_MEMORY_START                = 0x0000000000001000L;
	/**
	 * the first memory address of the XNN registers
	 */
	public static final long REGISTER_MEMORY_START_XNN            = 0x0000000000001028L;
	/**
	 * the address which refers to the last register of the PVM
	 */
	public static final long REGISTER_MEMORY_LAST_ADDRESS         = 0x00000000000017F8L;
	/**
	 * the lowest byte address which is above the PVM register memory block
	 */
	public static final long REGISTER_MEMORY_END_ADDRESS_SPACE    = 0x0000000000001800L;
	/**
	 * the maximum number value
	 */
	public static final long MAX_VALUE                            = 0x7FFFFFFFFFFFFFFFL;
	/**
	 * the minimum number value
	 */
	public static final long MIN_VALUE                            = -0x8000000000000000L;
	/**
	 * the STREAM-ID of the stdin pipe stream this stream is initially open for reading
	 */
	public static final long STD_IN                               = 0L;
	/**
	 * the STREAM-ID of the stdout pipe stream this stream is initially open for writing
	 */
	public static final long STD_OUT                              = 1L;
	/**
	 * the STREAM-ID of the stdlog pipe stream this stream is initially open for writing
	 */
	public static final long STD_LOG                              = 2L;
	/**
	 * if pfs_errno is not set/no error occurred
	 */
	public static final long ERR_NONE                             = 0L;
	/**
	 * if an operation failed because of an unknown/unspecified error
	 */
	public static final long ERR_UNKNOWN_ERROR                    = 1L;
	/**
	 * if the iterator has no next element
	 */
	public static final long ERR_NO_MORE_ELEMNETS                 = 2L;
	/**
	 * if an IO operation failed because the element is not of the correct type (file expected, but folder or reverse)
	 */
	public static final long ERR_ELEMENT_WRONG_TYPE               = 3L;
	/**
	 * if an IO operation failed because the element does not exist
	 */
	public static final long ERR_ELEMENT_NOT_EXIST                = 4L;
	/**
	 * if an IO operation failed because the element already existed
	 */
	public static final long ERR_ELEMENT_ALREADY_EXIST            = 5L;
	/**
	 * if an IO operation failed because there was not enough space in the file system
	 */
	public static final long ERR_OUT_OF_SPACE                     = 6L;
	/**
	 * if an unspecified IO error occurred
	 */
	public static final long ERR_IO_ERR                           = 7L;
	/**
	 * if there was at least one invalid argument
	 */
	public static final long ERR_ILLEGAL_ARG                      = 8L;
	/**
	 * if there was an invalid magic value
	 */
	public static final long ERR_ILLEGAL_MAGIC                    = 9L;
	/**
	 * if an IO operation failed because there was not enough space in the file system
	 */
	public static final long ERR_OUT_OF_MEMORY                    = 10L;
	/**
	 * if an IO operation failed because the root folder has some restrictions
	 */
	public static final long ERR_ROOT_FOLDER                      = 11L;
	/**
	 * if an folder can not be moved because the new child (maybe a deep/indirect child) is a child of the folder
	 */
	public static final long ERR_PARENT_IS_CHILD                  = 12L;
	/**
	 * if an element which is opened elsewhere is tried to be deleted
	 */
	public static final long ERR_ELEMENT_USED                     = 13L;
	/**
	 * if some value was outside of the allowed range
	 */
	public static final long ERR_OUT_OF_RANGE                     = 14L;
	/**
	 * the unmodifiable flags these flags can only be changed on creation
	 */
	public static final long UNMODIFIABLE_FLAGS                   = 0x000000FFL;
	/**
	 * the pipe flag this flag can only be changed on creation
	 */
	public static final long FLAG_FOLDER                          = 0x00000001L;
	/**
	 * the file flag this flag can only be changed on creation
	 */
	public static final long FLAG_FILE                            = 0x00000002L;
	/**
	 * the pipe flag this flag can only be changed on creation
	 */
	public static final long FLAG_PIPE                            = 0x00000004L;
	/**
	 * the executable flag
	 */
	public static final long FLAG_EXECUTABLE                      = 0x00000100L;
	/**
	 * the hidden flag
	 */
	public static final long FLAG_HIDDEN                          = 0x01000000L;
	/**
	 * create the file/pipe fail if the file/pipe exist already
	 */
	public static final long STREAM_ONLY_CREATE                   = 0x00000001L;
	/**
	 * create the file/pipe if it does not exist do not fail if the file/pipe exist already (overwritten by
	 * PFS_SO_ONLY_CREATE)
	 */
	public static final long STREAM_ALSO_CREATE                   = 0x00000002L;
	/**
	 * a file stream fail if the element is a pipe and if CREATE is set create a file
	 */
	public static final long STREAM_FILE                          = 0x00000004L;
	/**
	 * a pipe stream fail if the element is a file and if CREATE is set create a pipe
	 */
	public static final long STREAM_PIPE                          = 0x00000008L;
	/**
	 * open the stream for read access
	 */
	public static final long STREAM_READ                          = 0x00000100L;
	/**
	 * open the stream for write access
	 */
	public static final long STREAM_WRITE                         = 0x00000200L;
	/**
	 * open the stream for append access (before every write operation the position is set to the end of the file)
	 */
	public static final long STREAM_APPEND                        = 0x00000400L;
	/**
	 * truncate the files content
	 */
	public static final long STREAM_FILE_TRUNC                    = 0x00010000L;
	/**
	 * set the position initially to the end of the file not the start
	 */
	public static final long STREAM_FILE_EOF                      = 0x00020000L;
	
}
