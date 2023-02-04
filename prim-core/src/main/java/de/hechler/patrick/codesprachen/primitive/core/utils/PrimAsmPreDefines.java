package de.hechler.patrick.codesprachen.primitive.core.utils;

/**
 * this class holds the predefined for the PVM assembler code as java constants
 * 
 * @author pat
 */
public class PrimAsmPreDefines {
	
	private PrimAsmPreDefines() {}
	
	//GENERATED-CODE-START
	// this code-block is automatic generated, do not modify
	/**
	 * <b>INT_ERRORS_ILLEGAL_INTERRUPT</b>: illegal interrupt<br>
	 * value: <code>0</code>
	 * <p>
	 * <code>X00</code> contains the number of the illegal interrupt<br>
	 * exits with <code>(128 + illegal_interrup_number)</code> (without calling the exit interrupt)<br>
	 * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with <code>128</code><br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called<br>
	 * the pvm may print an error message before terminating	 */
	public static final long INT_ERRORS_ILLEGAL_INTERRUPT = 0L;
	/**
	 * <b>INT_ERRORS_UNKNOWN_COMMAND</b>: unknown command<br>
	 * value: <code>1</code>
	 * <p>
	 * exits with <code>7</code> (without calling the exit interrupt)<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called<br>
	 * the pvm may print an error message before terminating	 */
	public static final long INT_ERRORS_UNKNOWN_COMMAND = 1L;
	/**
	 * <b>INT_ERRORS_ILLEGAL_MEMORY</b>: illegal memory<br>
	 * value: <code>2</code>
	 * <p>
	 * exits with <code>6</code> (without calling the exit interrupt)<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called<br>
	 * the pvm may print an error message before terminating	 */
	public static final long INT_ERRORS_ILLEGAL_MEMORY = 2L;
	/**
	 * <b>INT_ERRORS_ARITHMETIC_ERROR</b>: arithmetic error<br>
	 * value: <code>3</code>
	 * <p>
	 * exits with <code>5</code> (without calling the exit interrupt)<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called<br>
	 * the pvm may print an error message before terminating	 */
	public static final long INT_ERRORS_ARITHMETIC_ERROR = 3L;
	/**
	 * <b>INT_EXIT</b>: exit<br>
	 * value: <code>4</code>
	 * <p>
	 * use <code>X00</code> to specify the exit number of the progress<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_EXIT = 4L;
	/**
	 * <b>INT_MEMORY_ALLOC</b>: allocate a memory-block<br>
	 * value: <code>5</code>
	 * <p>
	 * <code>X00</code> saves the size of the block<br>
	 * if the value of <code>X00</code> is <code>-1</code> after the call the memory-block could not be allocated<br>
	 * if the value of <code>X00</code> is not <code>-1</code>, <code>X00</code> points to the first element of the allocated memory-block<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEMORY_ALLOC = 5L;
	/**
	 * <b>INT_MEMORY_REALLOC</b>: reallocate a memory-block<br>
	 * value: <code>6</code>
	 * <p>
	 * <code>X00</code> points to the memory-block<br>
	 * <code>X01</code> is set to the new size of the memory-block<br>
	 * <code>X01</code> will be <code>-1</code> if the memory-block could not be reallocated, the old memory-block will remain valid and should be freed if it is not longer needed<br>
	 * <code>X01</code> will point to the new memory block, the old memory-block was automatically freed, so it should not be used, the new block should be freed if it is not longer needed<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEMORY_REALLOC = 6L;
	/**
	 * <b>INT_MEMORY_FREE</b>: free a memory-block<br>
	 * value: <code>7</code>
	 * <p>
	 * <code>X00</code> points to the old memory-block<br>
	 * after this the memory-block should not be used<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEMORY_FREE = 7L;
	/**
	 * <b>INT_OPEN_STREAM</b>: open new stream<br>
	 * value: <code>8</code>
	 * <p>
	 * <code>X00</code> contains a pointer to the STRING, which refers to the file which should be read<br>
	 * <code>X01</code> specfies the open mode: (bitwise flags)
	 * <ul>
	 * <li><code>OPEN_ONLY_CREATE</code>
	 * <ul>
	 * <li>fail if the file/pipe exist already</li>
	 * <li>when this flags is set either <code>OPEN_FILE</code> or <code>OPEN_PIPE</code> has to be set</li>
	 * </ul></li>
	 * <li><code>OPEN_ALSO_CREATE</code>
	 * <ul>
	 * <li>create the file/pipe if it does not exist, but do not fail if the file/pipe exist already (overwritten by PFS_SO_ONLY_CREATE)</li>
	 * </ul></li>
	 * <li><code>OPEN_FILE</code>
	 * <ul>
	 * <li>fail if the element is a pipe and if a create flag is set create a file if the element does not exist already</li>
	 * <li>this flag is not compatible with <code>OPEN_PIPE</code></li>
	 * </ul></li>
	 * <li><code>OPEN_PIPE</code>
	 * <ul>
	 * <li>fail if the element is a file and if a create flag is set create a pipe</li>
	 * <li>this flag is not compatible with <code>OPEN_FILE</code></li>
	 * </ul></li>
	 * <li><code>OPEN_READ</code>
	 * <ul>
	 * <li>open the stream for read access</li>
	 * </ul></li>
	 * <li><code>OPEN_WRITE</code>
	 * <ul>
	 * <li>open the stream for write access</li>
	 * </ul></li>
	 * <li><code>OPEN_APPEND</code>
	 * <ul>
	 * <li>open the stream for append access (before every write operation the position is set to the end of the file)</li>
	 * <li>implicitly also sets <code>OPEN_WRITE</code> (for pipes there is no diffrence in <code>OPEN_WRITE</code> and <code>OPEN_APPEND</code>)</li>
	 * </ul></li>
	 * <li><code>OPEN_FILE_TRUNCATE</code>
	 * <ul>
	 * <li>truncate the files content</li>
	 * <li>implicitly sets <code>OPEN_FILE</code></li>
	 * <li>nop when also <code>OPEN_ONLY_CREATE</code> is set</li>
	 * </ul></li>
	 * <li><code>OPEN_FILE_EOF</code>
	 * <ul>
	 * <li>set the position initially to the end of the file not the start</li>
	 * <li>ignored when opening a pipe</li>
	 * </ul></li>
	 * <li>other flags will be ignored</li>
	 * <li>the operation will fail if it is not spezified if the file should be opened for read, write and/or append</li>
	 * </ul>
	 * opens a new stream to the specified file<br>
	 * if successfully the STREAM-ID will be saved in the <code>X00</code> register<br>
	 * if failed <code>X00</code> will contain <code>-1</code><br>
	 * to close the stream use the stream close interrupt (<code>INT_STREAM_CLOSE</code>)<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_STREAM = 8L;
	/**
	 * <b>INT_STREAMS_WRITE</b>: write<br>
	 * value: <code>9</code>
	 * <p>
	 * <code>X00</code> contains the STREAM-ID<br>
	 * <code>X01</code> contains the number of elements to write<br>
	 * <code>X02</code> points to the elements to write<br>
	 * <code>X01</code> will be set to the number of written bytes.<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAMS_WRITE = 9L;
	/**
	 * <b>INT_STREAMS_READ</b>: read<br>
	 * value: <code>10</code>
	 * <p>
	 * <code>X00</code> contains the STREAM-ID<br>
	 * <code>X01</code> contains the number of elements to read<br>
	 * <code>X02</code> points to the elements to read<br>
	 * after execution <code>X01</code> will contain the number of elements, which has been read.
	 * <ul>
	 * <li>when the value is less than len either an error occured or end of file/pipe has reached (which is not considered an error)</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAMS_READ = 10L;
	/**
	 * <b>INT_STREAMS_CLOSE</b>: stream close<br>
	 * value: <code>11</code>
	 * <p>
	 * <code>X00</code> contains the STREAM-ID<br>
	 * <code>X00</code> will be set to 1 on success and 0 on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAMS_CLOSE = 11L;
	/**
	 * <b>INT_STREAMS_FILE_GET_POS</b>: stream file get position<br>
	 * value: <code>12</code>
	 * <p>
	 * <code>X00</code> contains the STREAM/FILE_STREAM-ID<br>
	 * <code>X01</code> will be set to the stream position or -1 on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAMS_FILE_GET_POS = 12L;
	/**
	 * <b>INT_STREAMS_FILE_SET_POS</b>: stream file set position<br>
	 * value: <code>13</code>
	 * <p>
	 * <code>X00</code> contains the STREAM/FILE_STREAM-ID<br>
	 * <code>X01</code> contains the new position of the stream<br>
	 * <code>X01</code> will be set to 1 or 0 on error<br>
	 * note that it is possible to set the stream position behind the end of the file.
	 * <ul>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAMS_FILE_SET_POS = 13L;
	/**
	 * <b>INT_STREAMS_FILE_ADD_POS</b>: stream file add position<br>
	 * value: <code>14</code>
	 * <p>
	 * <code>X00</code> contains the STREAM/FILE_STREAM-ID<br>
	 * <code>X01</code> contains the value, which should be added to the position of the stream
	 * <ul>
	 * <li><code>X01</code> is allowed to be negative, but the sum of the old position and <code>X01</code> is not allowed to be negative</li>
	 * </ul>
	 * <code>X01</code> will be set to the new position or -1 on error<br>
	 * note that it is possible to set the stream position behind the end of the file.
	 * <ul>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAMS_FILE_ADD_POS = 14L;
	/**
	 * <b>INT_STREAMS_FILE_SEEK_EOF</b>: stream file seek eof<br>
	 * value: <code>15</code>
	 * <p>
	 * <code>X00</code> contains the STREAM-ID<br>
	 * <code>X01</code> will be set to the new position of the stream or -1 on error<br>
	 * sets the position of the stream to the end of the file (the file length)<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAMS_FILE_SEEK_EOF = 15L;
	/**
	 * <b>INT_OPEN_FILE</b>: open element handle file<br>
	 * value: <code>16</code>
	 * <p>
	 * <code>X00</code> points to the <code>STRING</code> which contains the path of the file to be opened<br>
	 * <code>X00</code> will be set to the newly opened STREAM/FILE-ID or -1 on error<br>
	 * this operation will fail if the element is no file<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_FILE = 16L;
	/**
	 * <b>INT_OPEN_FOLDER</b>: open element handle folder<br>
	 * value: <code>17</code>
	 * <p>
	 * <code>X00</code> points to the <code>STRING</code> which contains the path of the folder to be opened<br>
	 * <code>X00</code> will be set to the newly opened STREAM/FOLDER-ID or -1 on error<br>
	 * this operation will fail if the element is no folder<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_FOLDER = 17L;
	/**
	 * <b>INT_OPEN_PIPE</b>: open element handle pipe<br>
	 * value: <code>18</code>
	 * <p>
	 * <code>X00</code> points to the <code>STRING</code> which contains the path of the pipe to be opened<br>
	 * <code>X00</code> will be set to the newly opened STREAM/PIPE-ID or -1 on error<br>
	 * this operation will fail if the element is no pipe<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_PIPE = 18L;
	/**
	 * <b>INT_OPEN_ELEMENT</b>: open element handle (any)<br>
	 * value: <code>19</code>
	 * <p>
	 * <code>X00</code> points to the <code>STRING</code> which contains the path of the element to be opened<br>
	 * <code>X00</code> will be set to the newly opened STREAM-ID or -1 on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_ELEMENT = 19L;
	/**
	 * <b>INT_ELEMENT_OPEN_PARENT</b>: element open parent handle<br>
	 * value: <code>20</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X00</code> will be set to the newly opened ELEMENT/FOLDER-ID or -1 on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_OPEN_PARENT = 20L;
	/**
	 * <b>INT_ELEMENT_GET_CREATE</b>: get create date<br>
	 * value: <code>21</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X01</code> will be set to the create date or <code>-1</code> on error
	 * <ul>
	 * <li>note that <code>-1</code> may be the create date of the element, so check <code>ERRNO</code> instead</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_CREATE = 21L;
	/**
	 * <b>INT_ELEMENT_GET_LAST_MOD</b>: get last mod date<br>
	 * value: <code>22</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X01</code> will be set to the last modified date or <code>-1</code> on error
	 * <ul>
	 * <li>note that <code>-1</code> may be the last modified date of the element, so check <code>ERRNO</code> instead</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_LAST_MOD = 22L;
	/**
	 * <b>INT_ELEMENT_SET_CREATE</b>: set create date<br>
	 * value: <code>23</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X00</code> contains the new create date of the element<br>
	 * <code>X01</code> will be set to <code>1</code> or <code>0</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_SET_CREATE = 23L;
	/**
	 * <b>INT_ELEMENT_SET_LAST_MOD</b>: set last modified date<br>
	 * value: <code>24</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X00</code> contains the last modified date of the element<br>
	 * <code>X01</code> will be set to <code>1</code> or <code>0</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_SET_LAST_MOD = 24L;
	/**
	 * <b>INT_ELEMENT_DELETE</b>: element delete<br>
	 * value: <code>25</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * note that this operation automatically closes the given ELEMENT-ID, the close interrupt should not be invoked after this interrupt returned<br>
	 * <code>X01</code> will be set to <code>1</code> or <code>0</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_DELETE = 25L;
	/**
	 * <b>INT_ELEMENT_MOVE</b>: element move<br>
	 * value: <code>26</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X01</code> points to a STRING which will be the new name or it is set to <code>-1</code> if the name should not be changed<br>
	 * <code>X02</code> contains the ELEMENT-ID of the new parent of <code>-1</code> if the new parent should not be changed<br>
	 * when both <code>X01</code> and <code>X02</code> are set to <code>-1</code> this operation will do nothing<br>
	 * <code>X01</code> will be set to <code>1</code> or <code>0</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_MOVE = 26L;
	/**
	 * <b>INT_ELEMENT_GET_NAME</b>: element get name<br>
	 * value: <code>27</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X01</code> points the the a memory block, which should be used to store the name as a STRING
	 * <ul>
	 * <li>when <code>X01</code> is set to <code>-1</code> a new memory block will be allocated</li>
	 * </ul>
	 * on success <code>X01</code> will point to the name as STRING representation
	 * <ul>
	 * <li>when the memory block is not large enough, it will be resized</li>
	 * <li>note that when <code>X01</code> does not point to the start of the memory block the start of the memory block can still be moved during the reallocation</li>
	 * </ul>
	 * on error <code>X01</code> will be set to <code>-1</code><br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_NAME = 27L;
	/**
	 * <b>INT_ELEMENT_GET_FLAGS</b>: element get flags<br>
	 * value: <code>28</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X01</code> will be set to the flags or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_FLAGS = 28L;
	/**
	 * <b>INT_ELEMENT_MODIFY_FLAGS</b>: element modify flags<br>
	 * value: <code>29</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT-ID<br>
	 * <code>X01</code> contains the flags to be added<br>
	 * <code>X02</code> contains the flags to be removed<br>
	 * note that only the low 32 bit will be used and the high 32 bit will be ignored<br>
	 * <code>X01</code> will be set to <code>1</code> or <code>0</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_MODIFY_FLAGS = 29L;
	/**
	 * <b>INT_FOLDER_CHILD_COUNT</b>: element folder child count<br>
	 * value: <code>30</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X01</code> will be set to the number of child elements the folder has or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CHILD_COUNT = 30L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_OF_NAME</b>: folder get child of name<br>
	 * value: <code>31</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X00</code> points to a STRING with the name of the child<br>
	 * <code>X01</code> will be set to a newly opened ELEMENT-ID for the child or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_OF_NAME = 31L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME</b>: folder get child folder of name<br>
	 * value: <code>32</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X00</code> points to a STRING with the name of the child<br>
	 * this operation will fail if the child is no folder<br>
	 * <code>X01</code> will be set to a newly opened ELEMENT/FOLDER-ID for the child or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME = 32L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_FILE_OF_NAME</b>: folder get child file of name<br>
	 * value: <code>33</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X00</code> points to a STRING with the name of the child<br>
	 * this operation will fail if the child is no file<br>
	 * <code>X01</code> will be set to a newly opened ELEMENT/FILE-ID for the child or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_FILE_OF_NAME = 33L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME</b>: folder get child pipe of name<br>
	 * value: <code>34</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X00</code> points to a STRING with the name of the child<br>
	 * this operation will fail if the child is no pipe<br>
	 * <code>X01</code> will be set to a newly opened ELEMENT/PIPE-ID for the child or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME = 34L;
	/**
	 * <b>INT_FOLDER_CREATE_CHILD_FOLDER</b>: folder add child folder<br>
	 * value: <code>35</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X00</code> points to a STRING with the name of the child<br>
	 * <code>X01</code> will be set to a newly opened/created ELEMENT/FOLDER-ID for the child or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CREATE_CHILD_FOLDER = 35L;
	/**
	 * <b>INT_FOLDER_CREATE_CHILD_FILE</b>: folder add child file<br>
	 * value: <code>36</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X01</code> points to the STRING name of the new child element<br>
	 * <code>X01</code> will be set to a newly opened/created ELEMENT/FILE-ID for the child or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CREATE_CHILD_FILE = 36L;
	/**
	 * <b>INT_FOLDER_CREATE_CHILD_PIPE</b>: folder add child pipe<br>
	 * value: <code>37</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X01</code> points to the STRING name of the new child element<br>
	 * <code>X01</code> will be set to a newly opened/created ELEMENT/PIPE-ID for the child or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CREATE_CHILD_PIPE = 37L;
	/**
	 * <b>INT_FOLDER_OPEN_ITER</b>: open child iterator of folder<br>
	 * value: <code>38</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FOLDER-ID<br>
	 * <code>X01</code> is set to <code>0</code> if hidden files should be skipped and any other value if not<br>
	 * <code>X01</code> will be set to the FOLDER-ITER-ID or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_ITER = 38L;
	/**
	 * <b>INT_FILE_LENGTH</b>: get the length of a file<br>
	 * value: <code>39</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FILE-ID<br>
	 * <code>X01</code> will be set to the file length in bytes or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FILE_LENGTH = 39L;
	/**
	 * <b>INT_FILE_TRUNCATE</b>: set the length of a file<br>
	 * value: <code>40</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FILE-ID<br>
	 * <code>X01</code> is set to the new length of the file<br>
	 * this interrupt will append zeros to the file when the new length is larger than the old length or remove all content after the new length<br>
	 * <code>X01</code> will be set <code>1</code> on success or <code>0</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FILE_TRUNCATE = 40L;
	/**
	 * <b>INT_HANDLE_OPEN_STREAM</b>: opens a stream from a file or pipe handle<br>
	 * value: <code>41</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/FILE/PIPE-ID
	 * <ul>
	 * <li>note that this interrupt works for both files and pipes, but will fail for folders</li>
	 * </ul>
	 * <code>X01</code> is set to the open flags
	 * <ul>
	 * <li>note that the high 32-bit of the flags are ignored</li>
	 * </ul>
	 * <code>X01</code> will be set to the STREAM-ID or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_HANDLE_OPEN_STREAM = 41L;
	/**
	 * <b>INT_PIPE_LENGTH</b>: get the length of a pipe<br>
	 * value: <code>42</code>
	 * <p>
	 * <code>X00</code> contains the ELEMENT/PIPE-ID<br>
	 * <code>X01</code> will be set to the pipe length in bytes or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_PIPE_LENGTH = 42L;
	/**
	 * <b>INT_TIME_GET</b>: get the current system time<br>
	 * value: <code>43</code>
	 * <p>
	 * <code>X00</code> will be set to <code>1</code> on success and <code>0</code> on error<br>
	 * <code>X01</code> will be set to the curent system time in seconds since the epoch<br>
	 * <code>X02</code> will be set to the additional curent system time in nanoseconds<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_GET = 43L;
	/**
	 * <b>INT_TIME_RES</b>: get the system time resolution<br>
	 * value: <code>44</code>
	 * <p>
	 * <code>X00</code> will be set to <code>1</code> on success and <code>0</code> on error<br>
	 * <code>X01</code> will be set to the resolution in seconds<br>
	 * <code>X02</code> will be set to the additional resolution in nanoseconds<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_RES = 44L;
	/**
	 * <b>INT_TIME_SLEEP</b>: to sleep the given time in nanoseconds<br>
	 * value: <code>45</code>
	 * <p>
	 * <code>X00</code> contain the number of nanoseconds to wait (only values from <code>0</code> to <code>999999999</code> are allowed)<br>
	 * <code>X01</code> contain the number of seconds to wait (only values greather or equal to <code>0</code> are allowed)<br>
	 * <code>X00</code> and <code>X01</code> will contain the remaining time (both <code>0</code> if it finished waiting)<br>
	 * <code>X02</code> will be <code>1</code> if the call was successfully and <code>0</code> if something went wrong<br>
	 * <code>X00</code> will not be negative if the progress waited too long<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_SLEEP = 45L;
	/**
	 * <b>INT_TIME_WAIT</b>: to wait the given time in nanoseconds<br>
	 * value: <code>46</code>
	 * <p>
	 * <code>X00</code> contain the number of seconds since the epoch<br>
	 * <code>X01</code> contain the additional number of nanoseconds<br>
	 * this interrupt will wait until the current system time is equal or after the given absolute time.<br>
	 * <code>X00</code> and <code>X01</code> will contain the remaining time (both <code>0</code> if it finished waiting)<br>
	 * <code>X02</code> will be <code>1</code> if the call was successfully and <code>0</code> if something went wrong<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_WAIT = 46L;
	/**
	 * <b>INT_RND_OPEN</b>: open a read stream which delivers random values<br>
	 * value: <code>47</code>
	 * <p>
	 * <code>X00</code> will be set to the STREAM-ID or <code>-1</code> on error
	 * <ul>
	 * <li>the stream will only support read operations
	 * <ul>
	 * <li>not write/append or seek/setpos operations</li>
	 * </ul></li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_RND_OPEN = 47L;
	/**
	 * <b>INT_RND_NUM</b>: sets `X00` to a random number<br>
	 * value: <code>48</code>
	 * <p>
	 * <code>X00</code> will be set to a random non negative number or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_RND_NUM = 48L;
	/**
	 * <b>INT_MEM_CMP</b>: memory compare<br>
	 * value: <code>49</code>
	 * <p>
	 * compares two blocks of memory<br>
	 * <code>X00</code> points to the target memory block<br>
	 * <code>X01</code> points to the source memory block<br>
	 * <code>X02</code> has the length in bytes of both memory blocks<br>
	 * the <code>STATUS</code> register <code>LOWER</code> <code>GREATHER</code> and <code>EQUAL</code> flags will be set after this interrupt<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_CMP = 49L;
	/**
	 * <b>INT_MEM_CPY</b>: memory copy<br>
	 * value: <code>50</code>
	 * <p>
	 * copies a block of memory<br>
	 * this function has undefined behavior if the two blocks overlap<br>
	 * <code>X00</code> points to the target memory block<br>
	 * <code>X01</code> points to the source memory block<br>
	 * <code>X02</code> has the length of bytes to bee copied<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_CPY = 50L;
	/**
	 * <b>INT_MEM_MOV</b>: memory move<br>
	 * value: <code>51</code>
	 * <p>
	 * copies a block of memory<br>
	 * this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)<br>
	 * <code>X00</code> points to the target memory block<br>
	 * <code>X01</code> points to the source memory block<br>
	 * <code>X02</code> has the length of bytes to bee copied<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_MOV = 51L;
	/**
	 * <b>INT_MEM_BSET</b>: memory byte set<br>
	 * value: <code>52</code>
	 * <p>
	 * sets a memory block to the given byte-value<br>
	 * <code>X00</code> points to the block<br>
	 * <code>X01</code> the first byte contains the value to be written to each byte<br>
	 * <code>X02</code> contains the length in bytes<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_BSET = 52L;
	/**
	 * <b>INT_STR_LEN</b>: string length<br>
	 * value: <code>53</code>
	 * <p>
	 * <code>X00</code> points to the STRING<br>
	 * <code>X00</code> will be set to the length of the string/ the (byte-)offset of the first byte from the <code>'\0'</code> character<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_LEN = 53L;
	/**
	 * <b>INT_STR_CMP</b>: string compare<br>
	 * value: <code>54</code>
	 * <p>
	 * <code>X00</code> points to the first STRING<br>
	 * <code>X01</code> points to the second STRING<br>
	 * the <code>STATUS</code> register <code>LOWER</code> <code>GREATHER</code> and <code>EQUAL</code> flags will be set after this interrupt<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_CMP = 54L;
	/**
	 * <b>INT_STR_FROM_NUM</b>: number to string<br>
	 * value: <code>55</code>
	 * <p>
	 * <code>X00</code> is set to the number to convert<br>
	 * <code>X01</code> is points to the buffer to be filled with the number in a STRING format<br>
	 * <code>X02</code> contains the base of the number system
	 * <ul>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></li>
	 * </ul>
	 * <code>X03</code> is set to the length of the buffer
	 * <ul>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</li>
	 * </ul>
	 * <code>X00</code> will be set to the size of the STRING (without the <code>\0</code> terminating character)<br>
	 * <code>X01</code> will be set to the new buffer<br>
	 * <code>X03</code> will be set to the new size of the buffer
	 * <ul>
	 * <li>the new length will be the old length or if the old length is smaller than the size of the STRING (with <code>\0</code>) than the size of the STRING (with <code>\0</code>)</li>
	 * </ul>
	 * on error <code>X01</code> will be set to <code>-1</code><br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_NUM = 55L;
	/**
	 * <b>INT_STR_FROM_FPNUM</b>: floating point number to string<br>
	 * value: <code>56</code>
	 * <p>
	 * <code>X00</code> is set to the floating point number to convert<br>
	 * <code>X01</code> points to the buffer to be filled with the number in a STRING format<br>
	 * <code>X02</code> is set to the current size of the buffer
	 * <ul>
	 * <li><code>0</code> when the buffer should be allocated by this interrupt</li>
	 * </ul>
	 * <code>X00</code> will be set to the size of the STRING<br>
	 * <code>X01</code> will be set to the new buffer<br>
	 * <code>X02</code> will be set to the new size of the buffer
	 * <ul>
	 * <li>the new length will be the old length or if the old length is smaller than the size of the STRING (with <code>\0</code>) than the size of the STRING (with <code>\0</code>)</li>
	 * </ul>
	 * on error <code>X01</code> will be set to <code>-1</code><br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_FPNUM = 56L;
	/**
	 * <b>INT_STR_TO_NUM</b>: string to number<br>
	 * value: <code>57</code>
	 * <p>
	 * <code>X00</code> points to the STRING<br>
	 * <code>X01</code> points to the base of the number system
	 * <ul>
	 * <li>(for example <code>10</code> for the decimal system or <code>2</code> for the binary system)</li>
	 * <li>the minimum base is <code>2</code></li>
	 * <li>the maximum base is <code>36</code></li>
	 * </ul>
	 * <code>X00</code> will be set to the converted number<br>
	 * on success <code>X01</code> will be set to <code>1</code><br>
	 * on error <code>X01</code> will be set to <code>0</code>
	 * <ul>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</li>
	 * <li>if <code>ERRNO</code> is set to out of range, the string value displayed a value outside of the 64-bit number range and <code>X00</code> will either be min or max value</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_NUM = 57L;
	/**
	 * <b>INT_STR_TO_FPNUM</b>: string to floating point number<br>
	 * value: <code>58</code>
	 * <p>
	 * <code>X00</code> points to the STRING<br>
	 * <code>X00</code> will be set to the converted number<br>
	 * on success <code>X01</code> will be set to <code>1</code><br>
	 * on error <code>X01</code> will be set to <code>0</code>
	 * <ul>
	 * <li>the STRING contains illegal characters</li>
	 * <li>or the base is not valid</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_FPNUM = 58L;
	/**
	 * <b>INT_STR_TO_U16STR</b>: STRING to U16-STRING<br>
	 * value: <code>59</code>
	 * <p>
	 * <code>X00</code> points to the STRING (<code>UTF-8</code>)<br>
	 * <code>X01</code> points to the buffer to be filled with the to <code>UTF-16</code> converted string<br>
	 * <code>X02</code> is set to the length of the buffer<br>
	 * <code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)<br>
	 * <code>X01</code> points to the start of the unmodified space of the target buffer<br>
	 * <code>X02</code> will be set to unmodified space at the end of the buffer<br>
	 * <code>X03</code> will be set to the number of converted characters or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_U16STR = 59L;
	/**
	 * <b>INT_STR_TO_U32STR</b>: STRING to U32-STRING<br>
	 * value: <code>60</code>
	 * <p>
	 * <code>X00</code> points to the STRING (<code>UTF-8</code>)<br>
	 * <code>X01</code> points to the buffer to be filled with the to <code>UTF-32</code> converted string<br>
	 * <code>X02</code> is set to the length of the buffer<br>
	 * <code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)<br>
	 * <code>X01</code> points to the start of the unmodified space of the target buffer<br>
	 * <code>X02</code> will be set to unmodified space at the end of the buffer<br>
	 * <code>X03</code> will be set to the number of converted characters or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_U32STR = 60L;
	/**
	 * <b>INT_STR_FROM_U16STR</b>: U16-STRING to STRING<br>
	 * value: <code>61</code>
	 * <p>
	 * <code>X00</code> points to the <code>UTF-16</code> STRING<br>
	 * <code>X01</code> points to the buffer to be filled with the converted STRING (<code>UTF-8</code>)<br>
	 * <code>X02</code> is set to the length of the buffer<br>
	 * <code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator (note that the <code>\0</code> char needs two bytes))<br>
	 * <code>X01</code> points to the start of the unmodified space of the target buffer<br>
	 * <code>X02</code> will be set to unmodified space at the end of the buffer<br>
	 * <code>X03</code> will be set to the number of converted characters or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_U16STR = 61L;
	/**
	 * <b>INT_STR_FROM_U32STR</b>: U32-STRING to STRING<br>
	 * value: <code>62</code>
	 * <p>
	 * <code>X00</code> points to the <code>UTF-32</code> STRING<br>
	 * <code>X01</code> points to the buffer to be filled with the converted STRING (<code>UTF-8</code>)<br>
	 * <code>X02</code> is set to the length of the buffer<br>
	 * <code>X00</code> points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator (note that the <code>\0</code> char needs four bytes))<br>
	 * <code>X01</code> points to the start of the unmodified space of the target buffer<br>
	 * <code>X02</code> will be set to unmodified space at the end of the buffer<br>
	 * <code>X03</code> will be set to the number of converted characters or <code>-1</code> on error<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_U32STR = 62L;
	/**
	 * <b>INT_STR_FORMAT</b>: format string<br>
	 * value: <code>63</code>
	 * <p>
	 * <code>X00</code> is set to the STRING input<br>
	 * <code>X01</code> contains the buffer for the STRING output<br>
	 * <code>X02</code> is the size of the buffer in bytes<br>
	 * the register <code>X03</code> points to the formatting arguments<br>
	 * <code>X00</code> will be set to the length of the output string (the offset of the <code>\0</code> character) or <code>-1</code> on error
	 * <ul>
	 * <li>if <code>X00</code> is larger or equal to <code>X02</code>, only the first <code>X02</code> bytes will be written to the buffer</li>
	 * </ul>
	 * formatting:
	 * <ul>
	 * <li><code>%%</code>: to escape an <code>%</code> character (only one <code>%</code> will be in the formatted STRING)</li>
	 * <li><code>%s</code>: the next argument points to a STRING, which should be inserted here</li>
	 * <li><code>%c</code>: the next argument starts with a byte, which should be inserted here
	 * <ul>
	 * <li>note that UTF-8 characters are not always represented by one byte, but there will always be only one byte used</li>
	 * </ul></li>
	 * <li><code>%n</code>: consumes two arguments
	 * <ol>
	 * <li>the next argument contains a number in the range of <code>2..36</code>.
	 * <ul>
	 * <li>if the first argument is less than <code>2</code> or larger than <code>36</code> the interrupt will fail</li>
	 * </ul></li>
	 * <li>which should be converted to a STRING using the number system with the basoe of the first argument and than be inserted here</li>
	 * </ol></li>
	 * <li><code>%d</code>: the next argument contains a number, which should be converted to a STRING using the decimal number system and than be inserted here</li>
	 * <li><code>%f</code>: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here</li>
	 * <li><code>%p</code>: the next argument contains a pointer, which should be converted to a STRING
	 * <ul>
	 * <li>if the pointer is not <code>-1</code> the pointer will be converted by placing a <code>"p-"</code> and then the unsigned pointer-number converted to a STRING using the hexadecimal number system</li>
	 * <li>if the pointer is <code>-1</code> it will be converted to the STRING <code>"p-inval"</code></li>
	 * </ul></li>
	 * <li><code>%h</code>: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here</li>
	 * <li><code>%b</code>: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here</li>
	 * <li><code>%o</code>: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here</li>
	 * </ul>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FORMAT = 63L;
	/**
	 * <b>INT_LOAD_FILE</b>: load a file<br>
	 * value: <code>64</code>
	 * <p>
	 * <code>X00</code> is set to the path (inclusive name) of the file<br>
	 * <code>X00</code> will point to the memory block, in which the file has been loaded or <code>-1</code> on error<br>
	 * <code>X01</code> will be set to the length of the file (and the memory block)<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_LOAD_FILE = 64L;
	/**
	 * <b>INT_LOAD_LIB</b>: load a library file<br>
	 * value: <code>65</code>
	 * <p>
	 * similar like the load file interrupt loads a file for the program.
	 * <ul>
	 * <li>the difference is that this interrupt may remember which files has been loaded
	 * <ul>
	 * <li>there are no guarantees, when the same memory block is reused and when a new memory block is created</li>
	 * </ul></li>
	 * <li>the other difference is that the file may only be unloaded with the unload lib interrupt (not with the free interrupt)
	 * <ul>
	 * <li>the returned memory block also can not be resized</li>
	 * </ul></li>
	 * <li>if the interrupt is executed multiple times with the same file, it will return every time the same memory block.</li>
	 * <li>this interrupt does not recognize files loaded with the <code>64</code> (<code>INT_LOAD_FILE</code>) interrupt.</li>
	 * </ul>
	 * <code>X00</code> is set to the path (inclusive name) of the file<br>
	 * <code>X00</code> will point to the memory block, in which the file has been loaded<br>
	 * <code>X01</code> will be set to the length of the file (and the memory block)<br>
	 * <code>X02</code> will be set to <code>1</code> if the file has been loaded as result of this interrupt and <code>0</code> if the file was previously loaded<br>
	 * when an error occurred <code>X00</code> will be set to <code>-1</code><br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_LOAD_LIB = 65L;
	/**
	 * <b>INT_UNLOAD_LIB</b>: unload a library file<br>
	 * value: <code>66</code>
	 * <p>
	 * unloads a library previously loaded with the load lib interrupt<br>
	 * this interrupt will ensure that the given memory block will be freed and never again be returned from the load lib interrupt<br>
	 * <code>X00</code> points to the (start of the) memory block<br>
	 * the value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_UNLOAD_LIB = 66L;
	/**
	 * <b>INTERRUPT_COUNT</b>: the number of interrupts<br>
	 * value: <code>67</code>
	 * <p>
	 * the number of interrupts supported by default<br>
	 * the <code>INTCNT</code> register is initialed with this value	 */
	public static final long INTERRUPT_COUNT = 67L;
	/**
	 * <b>FP_NAN</b>: not a number<br>
	 * value: <code>0x7ffe000000000000</code>
	 * <p>
	 * this floating point constant holds a NaN value	 */
	public static final long FP_NAN = 0x7ffe000000000000L;
	/**
	 * <b>FP_MAX_VALUE</b>: floating point maximum finite<br>
	 * value: <code>0x7fefffffffffffff</code>
	 * <p>
	 * the maximum not infinite floating point value	 */
	public static final long FP_MAX_VALUE = 0x7fefffffffffffffL;
	/**
	 * <b>FP_MIN_VALUE</b>: floating point minimum finite<br>
	 * value: <code>0x0000000000000001</code>
	 * <p>
	 * the minimum not infinite floating point value	 */
	public static final long FP_MIN_VALUE = 0x0000000000000001L;
	/**
	 * <b>FP_POS_INFINITY</b>: floating point positive infinity<br>
	 * value: <code>0x7ff0000000000000</code>
	 * <p>
	 * the floating point constant for positive infinity	 */
	public static final long FP_POS_INFINITY = 0x7ff0000000000000L;
	/**
	 * <b>FP_NEG_INFINITY</b>: floating point negative infinity<br>
	 * value: <code>0xfff0000000000000</code>
	 * <p>
	 * the floating point constant for negative infinity	 */
	public static final long FP_NEG_INFINITY = 0xfff0000000000000L;
	/**
	 * <b>REGISTER_MEMORY_START</b>: register memory block address<br>
	 * value: <code>0x0000000000001000</code>
	 * <p>
	 * the start address of the register memory block	 */
	public static final long REGISTER_MEMORY_START = 0x0000000000001000L;
	/**
	 * <b>REGISTER_MEMORY_ADDR_IP</b>: address of `IP`<br>
	 * value: <code>0x0000000000001008</code>
	 * <p>
	 * the start address of the <code>IP</code> register<br>
	 * this constant has the same value as the <code>REGISTER_MEMORY_START</code> constant	 */
	public static final long REGISTER_MEMORY_ADDR_IP = 0x0000000000001008L;
	/**
	 * <b>REGISTER_MEMORY_ADDR_SP</b>: address of `SP`<br>
	 * value: <code>0x0000000000001008</code>
	 * <p>
	 * the start address of the <code>SP</code> register	 */
	public static final long REGISTER_MEMORY_ADDR_SP = 0x0000000000001008L;
	/**
	 * <b>REGISTER_MEMORY_ADDR_INTP</b>: address of `INTP`<br>
	 * value: <code>0x0000000000001010</code>
	 * <p>
	 * the start address of the <code>INTP</code> register	 */
	public static final long REGISTER_MEMORY_ADDR_INTP = 0x0000000000001010L;
	/**
	 * <b>REGISTER_MEMORY_ADDR_INTCNT</b>: address of `INTCNT`<br>
	 * value: <code>0x0000000000001018</code>
	 * 	 */
	public static final long REGISTER_MEMORY_ADDR_INTCNT = 0x0000000000001018L;
	/**
	 * <b>REGISTER_MEMORY_ADDR_STATUS</b>: address of `STATUS`<br>
	 * value: <code>0x0000000000001020</code>
	 * <p>
	 * the start address of the <code>STATUS</code> register	 */
	public static final long REGISTER_MEMORY_ADDR_STATUS = 0x0000000000001020L;
	/**
	 * <b>REGISTER_MEMORY_ADDR_ERRNO</b>: address of `ERRNO`<br>
	 * value: <code>0x0000000000001028</code>
	 * <p>
	 * the start address of the <code>ERRNO</code> register	 */
	public static final long REGISTER_MEMORY_ADDR_ERRNO = 0x0000000000001028L;
	/**
	 * <b>REGISTER_MEMORY_START_XNN</b>: address of `X00`<br>
	 * value: <code>0x0000000000001030</code>
	 * <p>
	 * the offset of the <code>XNN</code> registers<br>
	 * the address of a <code>XNN</code> register can be calculated by multiplying the register number and adding this constant	 */
	public static final long REGISTER_MEMORY_START_XNN = 0x0000000000001030L;
	/**
	 * <b>REGISTER_MEMORY_LAST_ADDRESS</b>: address of the last `XNN` register<br>
	 * value: <code>0x00000000000017f8</code>
	 * <p>
	 * this constant holds the last valid address of the registers	 */
	public static final long REGISTER_MEMORY_LAST_ADDRESS = 0x00000000000017f8L;
	/**
	 * <b>REGISTER_MEMORY_END_ADDRESS_SPACE</b>: the address after the last address<br>
	 * value: <code>0x0000000000001800</code>
	 * <p>
	 * this constant holds the lowest address, which is above the register memory block	 */
	public static final long REGISTER_MEMORY_END_ADDRESS_SPACE = 0x0000000000001800L;
	/**
	 * <b>MAX_VALUE</b>: the maximum number value<br>
	 * value: <code>0x7fffffffffffffff</code>
	 * <p>
	 * this constant holds the maximum number value	 */
	public static final long MAX_VALUE = 0x7fffffffffffffffL;
	/**
	 * <b>MIN_VALUE</b>: the minimum number value<br>
	 * value: <code>-0x8000000000000000</code>
	 * <p>
	 * this constant holds the minimum number value	 */
	public static final long MIN_VALUE = -0x8000000000000000L;
	/**
	 * <b>STD_IN</b>: the _ID_ of the _STDIN_ stream<br>
	 * value: <code>0</code>
	 * <p>
	 * this constant holds the <i>Stream-ID</i> of the <i>STDIN</i> stream<br>
	 * the stream is initially open for reading<br>
	 * write and seek operations on the <i>STDIN</i> stream will fail	 */
	public static final long STD_IN = 0L;
	/**
	 * <b>STD_OUT</b>: the _ID_ of the STDOUT stream<br>
	 * value: <code>1</code>
	 * <p>
	 * this constant holds the <i>Stream-ID</i> of the <i>STDOUT</i> stream<br>
	 * the stream is initially open for writing<br>
	 * read and seek operations on the <i>STDOUT</i> stream will fail	 */
	public static final long STD_OUT = 1L;
	/**
	 * <b>STD_LOG</b>: the _ID_ of the _STDLOG_ stream<br>
	 * value: <code>2</code>
	 * <p>
	 * this constant holds the <i>Stream-ID</i> of the <i>STDLOG</i> stream<br>
	 * the stream is initially open for writing<br>
	 * read and seek operations on the <i>STDLOG</i> stream will fail	 */
	public static final long STD_LOG = 2L;
	/**
	 * <b>ERR_NONE</b>: indicates no error<br>
	 * value: <code>0</code>
	 * <p>
	 * this constant has to hold the zero value<br>
	 * every non zero value in the <code>ERRNO</code> register indicates some error<br>
	 * after handling the error the <code>ERRNO</code> register should be set to this value	 */
	public static final long ERR_NONE = 0L;
	/**
	 * <b>ERR_UNKNOWN_ERROR</b>: indicates an unknown error<br>
	 * value: <code>1</code>
	 * <p>
	 * this error value is used when there occurred some unknown error<br>
	 * this error value is the least helpful value for error handling	 */
	public static final long ERR_UNKNOWN_ERROR = 1L;
	/**
	 * <b>ERR_NO_MORE_ELEMENTS</b>: indicates that there are no more elements<br>
	 * value: <code>2</code>
	 * <p>
	 * this error value is used when an iterator was used too often	 */
	public static final long ERR_NO_MORE_ELEMENTS = 2L;
	/**
	 * <b>ERR_ELEMENT_WRONG_TYPE</b>: indicates that the element has not the wanted/allowed type<br>
	 * value: <code>3</code>
	 * <p>
	 * this error value indicates that some operation was used, which is not supported by the given element<br>
	 * for example when an file is asked how many children it has	 */
	public static final long ERR_ELEMENT_WRONG_TYPE = 3L;
	/**
	 * <b>ERR_ELEMENT_NOT_EXIST</b>: indicates that the element does not exist<br>
	 * value: <code>4</code>
	 * <p>
	 * this error value indicates that some element does not exist	 */
	public static final long ERR_ELEMENT_NOT_EXIST = 4L;
	/**
	 * <b>ERR_ELEMENT_ALREADY_EXIST</b>: indicates that the element already exists<br>
	 * value: <code>5</code>
	 * <p>
	 * this error value indicates that an element should be created but it exists already	 */
	public static final long ERR_ELEMENT_ALREADY_EXIST = 5L;
	/**
	 * <b>ERR_OUT_OF_SPACE</b>: indicates that there is not enough space on the device<br>
	 * value: <code>6</code>
	 * <p>
	 * this error value indicates that the file system could not allocate the needed blocks	 */
	public static final long ERR_OUT_OF_SPACE = 6L;
	/**
	 * <b>ERR_IO_ERR</b>: indicates an IO error<br>
	 * value: <code>7</code>
	 * <p>
	 * this error value indicates an Input/Output error	 */
	public static final long ERR_IO_ERR = 7L;
	/**
	 * <b>ERR_ILLEGAL_ARG</b>: indicates an illegal argument<br>
	 * value: <code>8</code>
	 * <p>
	 * this error value indicates that some argument has an illegal value	 */
	public static final long ERR_ILLEGAL_ARG = 8L;
	/**
	 * <b>ERR_ILLEGAL_MAGIC</b>: indicates that some magic value is invalid<br>
	 * value: <code>9</code>
	 * <p>
	 * this error value indicates that a magic value was invalid	 */
	public static final long ERR_ILLEGAL_MAGIC = 9L;
	/**
	 * <b>ERR_OUT_OF_MEMORY</b>: indicates that the system is out of memory<br>
	 * value: <code>10</code>
	 * <p>
	 * this error value indicates that the system could not allocate the needed memory	 */
	public static final long ERR_OUT_OF_MEMORY = 10L;
	/**
	 * <b>ERR_ROOT_FOLDER</b>: indicates that the root folder does not support this operation<br>
	 * value: <code>11</code>
	 * <p>
	 * this error value indicates that the root folder restrictions does not allow the tried operation	 */
	public static final long ERR_ROOT_FOLDER = 11L;
	/**
	 * <b>ERR_PARENT_IS_CHILD</b>: indicates that the parent can't be made to it's own child<br>
	 * value: <code>12</code>
	 * <p>
	 * this error value indicates that it was tried to move a folder to one of it's (possibly indirect) children	 */
	public static final long ERR_PARENT_IS_CHILD = 12L;
	/**
	 * <b>ERR_ELEMENT_USED</b>: indicates the element is still used somewhere else<br>
	 * value: <code>13</code>
	 * <p>
	 * this error value indicates that an element has open multiple handles (more than the used handle)	 */
	public static final long ERR_ELEMENT_USED = 13L;
	/**
	 * <b>ERR_OUT_OF_RANGE</b>: indicates that some value was outside of the allowed range<br>
	 * value: <code>14</code>
	 * <p>
	 * this error value indicates that some value was outside of the allowed range	 */
	public static final long ERR_OUT_OF_RANGE = 14L;
	/**
	 * <b>ERR_FOLDER_NOT_EMPTY</b>: indicates that the operation was canceled, because only empty folders can be deleted<br>
	 * value: <code>15</code>
	 * <p>
	 * this error value indicates that the operation was canceled, because only empty folders can be deleted<br>
	 * this error will occur, when a non empty folder is tried to be deleted	 */
	public static final long ERR_FOLDER_NOT_EMPTY = 15L;
	/**
	 * <b>UNMODIFIABLE_FLAGS</b>: element flags that can not be modified<br>
	 * value: <code>0x000000ff</code>
	 * <p>
	 * these flags can not be modified after an element was created<br>
	 * these flags hold essential information for the file system (for example if an element is a folder)	 */
	public static final long UNMODIFIABLE_FLAGS = 0x000000ffL;
	/**
	 * <b>FLAG_FOLDER</b>: folder flag<br>
	 * value: <code>0x00000001</code>
	 * <p>
	 * this flag is used for all folders	 */
	public static final long FLAG_FOLDER = 0x00000001L;
	/**
	 * <b>FLAG_FILE</b>: file flag<br>
	 * value: <code>0x00000002</code>
	 * <p>
	 * this flag is used for all files	 */
	public static final long FLAG_FILE = 0x00000002L;
	/**
	 * <b>FLAG_PIPE</b>: pipe flag<br>
	 * value: <code>0x00000004</code>
	 * <p>
	 * this flag is used for all pipes	 */
	public static final long FLAG_PIPE = 0x00000004L;
	/**
	 * <b>FLAG_EXECUTABLE</b>: flag for executables<br>
	 * value: <code>0x00000100</code>
	 * <p>
	 * this flag is used to indicate, that a file can be executed	 */
	public static final long FLAG_EXECUTABLE = 0x00000100L;
	/**
	 * <b>FLAG_HIDDEN</b>: flag for hidden elements<br>
	 * value: <code>0x01000000</code>
	 * <p>
	 * this flag is used to indicate, that an element should be hidden	 */
	public static final long FLAG_HIDDEN = 0x01000000L;
	/**
	 * <b>STREAM_ONLY_CREATE</b>: create the element for the stream<br>
	 * value: <code>0x00000001</code>
	 * <p>
	 * used when a stream is opened, when the element should be created during the open operation<br>
	 * when used the open operation will fail, if the element already exists<br>
	 * when used the <code>STREAM_FILE</code> or <code>STREAM_PIPE</code> flag has to be set	 */
	public static final long STREAM_ONLY_CREATE = 0x00000001L;
	/**
	 * <b>STREAM_ALSO_CREATE</b>: possibly create the element for the stream<br>
	 * value: <code>0x00000002</code>
	 * <p>
	 * used when a stream is opened, when the element should be created during the open operation if it doesn't exists already<br>
	 * when used the <code>STREAM_FILE</code> or <code>STREAM_PIPE</code> flag has to be set	 */
	public static final long STREAM_ALSO_CREATE = 0x00000002L;
	/**
	 * <b>STREAM_FILE</b>: create a file stream<br>
	 * value: <code>0x00000004</code>
	 * <p>
	 * used when the stream should be used for a file, will fail if the existing element is a pipe<br>
	 * when used the <code>STREAM_PIPE</code> flag is not allowed	 */
	public static final long STREAM_FILE = 0x00000004L;
	/**
	 * <b>STREAM_PIPE</b>: create a pipe stream<br>
	 * value: <code>0x00000008</code>
	 * <p>
	 * used when the stream should be used for a pipe, will fail if the existing element is a file<br>
	 * when used the <code>STREAM_FILE</code> flag is not allowed	 */
	public static final long STREAM_PIPE = 0x00000008L;
	/**
	 * <b>STREAM_READ</b>: create a readable stream<br>
	 * value: <code>0x00000100</code>
	 * <p>
	 * used to open a stream, which support the use of the read operations	 */
	public static final long STREAM_READ = 0x00000100L;
	/**
	 * <b>STREAM_WRITE</b>: create a writable stream<br>
	 * value: <code>0x00000200</code>
	 * <p>
	 * used to open a stream, which support the use of the write operations	 */
	public static final long STREAM_WRITE = 0x00000200L;
	/**
	 * <b>STREAM_APPEND</b>: create a writable stream in append mode<br>
	 * value: <code>0x00000400</code>
	 * <p>
	 * used to open a stream, which support the use of the write operations<br>
	 * the given stream will seek the file/pipe end before every write operation<br>
	 * for pipes the <code>STREAM_WRITE</code> flag is equally to this flag	 */
	public static final long STREAM_APPEND = 0x00000400L;
	/**
	 * <b>STREAM_FILE_TRUNC</b>: truncate the file<br>
	 * value: <code>0x00010000</code>
	 * <p>
	 * truncate the files content during the open operation<br>
	 * this flag can be used only with file streams	 */
	public static final long STREAM_FILE_TRUNC = 0x00010000L;
	/**
	 * <b>STREAM_FILE_EOF</b>: start at end of file<br>
	 * value: <code>0x00020000</code>
	 * <p>
	 * when used the stream will not start at the start of the file, but its end<br>
	 * this flag can be used only with file streams	 */
	public static final long STREAM_FILE_EOF = 0x00020000L;
	
	// here is the end of the automatic generated code-block
	// GENERATED-CODE-END
	
}
