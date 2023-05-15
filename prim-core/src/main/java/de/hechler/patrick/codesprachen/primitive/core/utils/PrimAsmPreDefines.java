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

/**
 * this class holds the predefined for the PVM assembler code as java constants
 * 
 * @author pat
 */
public class PrimAsmPreDefines {
	
	private PrimAsmPreDefines() {}
	
	// GENERATED-CODE-START
	// this code-block is automatic generated, do not modify
	/**
	 * <b>INT_ERROR_ILLEGAL_INTERRUPT</b>: illegal interrupt<br>
	 * value: <code>0</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>intnum</code>: (<code>num</code>) the number of the illegal interrupt</li>
	 * </ul>
	 * exits with <code>(128 + illegal_interrup_number)</code> (without calling the exit interrupt)<br>
	 * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with <code>128</code><br>
	 * the pvm may print an error message before terminating<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ERROR_ILLEGAL_INTERRUPT = 0L;
	/**
	 * <b>INT_ERROR_UNKNOWN_COMMAND</b>: unknown command<br>
	 * value: <code>1</code>
	 * <p>
	 * exits with <code>7</code> (without calling the exit interrupt)<br>
	 * the pvm may print an error message before terminating<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ERROR_UNKNOWN_COMMAND = 1L;
	/**
	 * <b>INT_ERROR_ILLEGAL_MEMORY</b>: illegal memory<br>
	 * value: <code>2</code>
	 * <p>
	 * exits with <code>6</code> (without calling the exit interrupt)<br>
	 * the pvm may print an error message before terminating<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ERROR_ILLEGAL_MEMORY = 2L;
	/**
	 * <b>INT_ERROR_ARITHMETIC_ERROR</b>: arithmetic error<br>
	 * value: <code>3</code>
	 * <p>
	 * exits with <code>5</code> (without calling the exit interrupt)<br>
	 * the pvm may print an error message before terminating<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ERROR_ARITHMETIC_ERROR = 3L;
	/**
	 * <b>INT_EXIT</b>: exit<br>
	 * value: <code>4</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>exitnum</code>: (<code>num</code>) the exit number this progress will have</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_EXIT = 4L;
	/**
	 * <b>INT_MEMORY_ALLOC</b>: allocate a memory-block<br>
	 * value: <code>5</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>len</code>: (<code>unum</code>) the size of the block to be allocated</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>mem</code>: (<code>ubyte#</code>) the allocated memory block or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEMORY_ALLOC = 5L;
	/**
	 * <b>INT_MEMORY_REALLOC</b>: reallocate a memory-block<br>
	 * value: <code>6</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>oldMem</code>: (<code>ubyte#</code>) points to the old memory-block</li>
	 * <li><code>X01</code> <code>newLen</code>: (<code>unum</code>) the new size of the memory-block</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>newMem</code>: (<code>ubyte#</code>) points to the new memory-block or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEMORY_REALLOC = 6L;
	/**
	 * <b>INT_MEMORY_FREE</b>: free a memory-block<br>
	 * value: <code>7</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>mem</code>: (<code>ubyte#</code>) points to the old memory-block</li>
	 * </ul>
	 * after this the memory-block should not be used<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEMORY_FREE = 7L;
	/**
	 * <b>INT_STREAM_OPEN</b>: open new stream<br>
	 * value: <code>8</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>fileName</code>: (<code>char#</code>) the STRING, which refers to the file which should be read</li>
	 * <li><code>X01</code> <code>flags</code>: (<code>unum</code>) the open flags (see the <code>STREAM_*</code> constants)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ID of the opened STREAM or <code>-1</code> on error</li>
	 * </ul>
	 * opens a new stream to the specified file<br>
	 * to close the stream use the stream close interrupt (<code>INT_STREAM_CLOSE</code>)<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_OPEN = 8L;
	/**
	 * <b>INT_STREAM_WRITE</b>: write<br>
	 * value: <code>9</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the STREAM-ID</li>
	 * <li><code>X01</code> <code>len</code>: (<code>unum</code>) the number of bytes to write</li>
	 * <li><code>X02</code> <code>data</code>: (<code>ubyte#</code>) points to the params to write</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>wrote</code>: (<code>num</code>) will be set to the number of written bytes</li>
	 * </ul>
	 * if less bytes than len where written, an error occured (for example the disk could be full)<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_WRITE = 9L;
	/**
	 * <b>INT_STREAM_READ</b>: read<br>
	 * value: <code>10</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the STREAM-ID</li>
	 * <li><code>X01</code> <code>len</code>: (<code>unum</code>) the number of bytes to read</li>
	 * <li><code>X02</code> <code>data</code>: (<code>ubyte#</code>) points to the params to read</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>read</code>: (<code>unum</code>) the number of bytes which have been read</li>
	 * </ul>
	 * when less than len bytes where read either an error occured or end of file/pipe has reached
	 * <ul>
	 * <li>end of file/pipe is not considered an error (<code>ERRNO</code> will be unmodified)</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_READ = 10L;
	/**
	 * <b>INT_STREAM_CLOSE</b>: stream close<br>
	 * value: <code>11</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the STREAM-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>success</code>: (<code>num</code>) will be set to <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * note that even on error the STREAM-ID will be released by the system, so do NOT retry<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_CLOSE = 11L;
	/**
	 * <b>INT_STREAM_FILE_GET_POS</b>: stream file get position<br>
	 * value: <code>12</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FILE-)STREAM-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>pos</code>: (<code>num</code>) be set to the stream position or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_FILE_GET_POS = 12L;
	/**
	 * <b>INT_STREAM_FILE_SET_POS</b>: stream file set position<br>
	 * value: <code>13</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FILE-)STREAM-ID</li>
	 * <li><code>X01</code> <code>pos</code>: (<code>unum</code>) the new position of the stream</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * note that it is possible to set the stream position behind the end of the file.
	 * <ul>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_FILE_SET_POS = 13L;
	/**
	 * <b>INT_STREAM_FILE_ADD_POS</b>: stream file add position<br>
	 * value: <code>14</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FILE-)STREAM-ID</li>
	 * <li><code>X01</code> <code>add</code>: (<code>num</code>) the value to be added to the stream position</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>pos</code>: (<code>num</code>) the new position or <code>-1</code> on error</li>
	 * </ul>
	 * if add is lower than the negative value of the old position, this operation will fail (the new position will not be negative)<br>
	 * note that it is possible to set the stream position behind the end of the file.
	 * <ul>
	 * <li>when this is done, the next write (not append) operation will fill the hole with zeros</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_FILE_ADD_POS = 14L;
	/**
	 * <b>INT_STREAM_FILE_SEEK_EOF</b>: stream file seek eof<br>
	 * value: <code>15</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the STREAM-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>pos</code>: (<code>num</code>) the new position of the stream or <code>-1</code> on error</li>
	 * </ul>
	 * sets the position of the stream to the end of the file (the file length)<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STREAM_FILE_SEEK_EOF = 15L;
	/**
	 * <b>INT_OPEN_FILE</b>: open element handle file<br>
	 * value: <code>16</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>file</code>: (<code>char#</code>) points to the STRING which contains the path of the file to be opened</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the newly opened (FILE-)ELEMENT-ID or <code>-1</code> on error</li>
	 * </ul>
	 * this operation will fail if the element is no file<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_FILE = 16L;
	/**
	 * <b>INT_OPEN_FOLDER</b>: open element handle folder<br>
	 * value: <code>17</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>folder</code>: (<code>char#</code>) points to the STRING which contains the path of the folder to be opened</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the newly opened (FOLDER-)ELEMENT-ID or <code>-1</code> on error</li>
	 * </ul>
	 * this operation will fail if the element is no folder<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_FOLDER = 17L;
	/**
	 * <b>INT_OPEN_PIPE</b>: open element handle pipe<br>
	 * value: <code>18</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>pipe</code>: (<code>char#</code>) points to the <code>STRING</code> which contains the path of the pipe to be opened</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the newly opened (PIPE-)ELEMENT-ID or <code>-1</code> on error</li>
	 * </ul>
	 * this operation will fail if the element is no pipe<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_PIPE = 18L;
	/**
	 * <b>INT_OPEN_ELEMENT</b>: open element handle (any)<br>
	 * value: <code>19</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>element</code>: (<code>char#</code>) the STRING which contains the path of the element to be opened</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the newly opened ELEMENT-ID or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_OPEN_ELEMENT = 19L;
	/**
	 * <b>INT_ELEMENT_OPEN_PARENT</b>: element open parent handle<br>
	 * value: <code>20</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>parent_id</code>: (<code>num</code>) the newly opened (FOLDER-)ELEMENT-ID or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_OPEN_PARENT = 20L;
	/**
	 * <b>INT_ELEMENT_GET_CREATE</b>: get create date<br>
	 * value: <code>21</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>date</code>: (<code>num</code>) the create date or <code>-1</code> on error</li>
	 * </ul>
	 * note that <code>-1</code> may be the create date of the element, so check <code>ERRNO</code> instead<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_CREATE = 21L;
	/**
	 * <b>INT_ELEMENT_GET_LAST_MOD</b>: get last mod date<br>
	 * value: <code>22</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>date</code>: (<code>num</code>) will be set to the last modified date or <code>-1</code> on error</li>
	 * </ul>
	 * note that <code>-1</code> may be the last modified date of the element, so check <code>ERRNO</code> instead<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_LAST_MOD = 22L;
	/**
	 * <b>INT_ELEMENT_SET_CREATE</b>: set create date<br>
	 * value: <code>23</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * <li><code>X01</code> <code>date</code>: (<code>num</code>) the new create date of the element</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) <code>1</code> on success or <code>0</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_SET_CREATE = 23L;
	/**
	 * <b>INT_ELEMENT_SET_LAST_MOD</b>: set last modified date<br>
	 * value: <code>24</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * <li><code>X01</code> <code>date</code>: (<code>num</code>) the new last modified date of the element</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code>  <code>success</code>: (<code>num</code>) <code>1</code> on success or <code>0</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_SET_LAST_MOD = 24L;
	/**
	 * <b>INT_ELEMENT_DELETE</b>: element delete<br>
	 * value: <code>25</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) <code>1</code> on success or <code>0</code> on error</li>
	 * </ul>
	 * this operation automatically closes the given ELEMENT-ID, even when it fails<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_DELETE = 25L;
	/**
	 * <b>INT_ELEMENT_MOVE</b>: element move<br>
	 * value: <code>26</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * <li><code>X01</code> <code>newName</code>: (<code>char#</code>) points to a STRING which will be the new name or it is set to <code>-1</code> if the name should not be changed</li>
	 * <li><code>X02</code> <code>newParentId</code>: (<code>num</code>) contains the ELEMENT-ID of the new parent of <code>-1</code> if the new parent should not be changed</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) <code>1</code> on success or <code>0</code> on error</li>
	 * </ul>
	 * when both <code>newName</code> and <code>newParentId</code> are set to <code>-1</code> this operation will do nothing<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_MOVE = 26L;
	/**
	 * <b>INT_ELEMENT_GET_NAME</b>: element get name<br>
	 * value: <code>27</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * <li><code>X01</code> <code>buffer</code>: (<code>char#</code>) points the the a memory block, which should be used to store the name as a STRING</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to the name as STRING representation or <code>-1</code> on error</li>
	 * </ul>
	 * when <code>buffer</code> is set to <code>-1</code> a new memory block will be allocated
	 * <ul>
	 * <li>when the memory block is not large enough, it will be resized</li>
	 * <li>note that when <code>X01</code> does not point to the start of the memory block the resize will fail</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_NAME = 27L;
	/**
	 * <b>INT_ELEMENT_GET_FLAGS</b>: element get flags<br>
	 * value: <code>28</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>flags</code>: (<code>num</code>) will be set to the flags or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_GET_FLAGS = 28L;
	/**
	 * <b>INT_ELEMENT_MODIFY_FLAGS</b>: element modify flags<br>
	 * value: <code>29</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT-ID</li>
	 * <li><code>X01</code> <code>addFlags</code>: (<code>udword</code>) the flags to be added</li>
	 * <li><code>X02</code> <code>remFlags</code>: (<code>udword</code>) the flags to be removed</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) <code>1</code> on success or <code>0</code> on error</li>
	 * </ul>
	 * only the low 32 bit will be used for the flags and the high 32 bit will be ignored<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_ELEMENT_MODIFY_FLAGS = 29L;
	/**
	 * <b>INT_FOLDER_CHILD_COUNT</b>: element folder child count<br>
	 * value: <code>30</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childCount</code>: (<code>num</code>) the number of child params the folder has or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CHILD_COUNT = 30L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_OF_NAME</b>: folder get child of name<br>
	 * value: <code>31</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to a STRING with the name of the child</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childId</code>: (<code>num</code>) the newly opened ELEMENT-ID for the child or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_OF_NAME = 31L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME</b>: folder get child folder of name<br>
	 * value: <code>32</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the ELEMENT/FOLDER-ID</li>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to a STRING with the name of the child</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childId</code>: (<code>num</code>) the newly opened (FOLDER-)ELEMENT-ID for the child or <code>-1</code> on error</li>
	 * </ul>
	 * this operation will fail if the child is no folder<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME = 32L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_FILE_OF_NAME</b>: folder get child file of name<br>
	 * value: <code>33</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to a STRING with the name of the child</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childId</code>: (<code>num</code>) the newly opened (FILE-)ELEMENT-ID for the child or <code>-1</code> on error</li>
	 * </ul>
	 * this operation will fail if the child is no file<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_FILE_OF_NAME = 33L;
	/**
	 * <b>INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME</b>: folder get child pipe of name<br>
	 * value: <code>34</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to a STRING with the name of the child</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childId</code>: (<code>num</code>) the newly opened (PIPE-)ELEMENT-ID for the child or <code>-1</code> on error</li>
	 * </ul>
	 * this operation will fail if the child is no pipe<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME = 34L;
	/**
	 * <b>INT_FOLDER_CREATE_CHILD_FOLDER</b>: folder add child folder<br>
	 * value: <code>35</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to a STRING with the name of the child</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childId</code>: (<code>num</code>) the newly created/opened (FOLDER-)ELEMENT-ID for the child or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CREATE_CHILD_FOLDER = 35L;
	/**
	 * <b>INT_FOLDER_CREATE_CHILD_FILE</b>: folder add child file<br>
	 * value: <code>36</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to the STRING name of the new child element</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childId</code>: (<code>num</code>) will be set to a newly created/opened (FILE-)ELEMENT-ID for the child or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CREATE_CHILD_FILE = 36L;
	/**
	 * <b>INT_FOLDER_CREATE_CHILD_PIPE</b>: folder add child pipe<br>
	 * value: <code>37</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>name</code>: (<code>char#</code>) points to the STRING name of the new child element</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>childId</code>: (<code>num</code>) will be set to a newly created/opened (PIPE-)ELEMENT-ID for the child or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_CREATE_CHILD_PIPE = 37L;
	/**
	 * <b>INT_FOLDER_OPEN_ITER</b>: open child iterator of folder<br>
	 * value: <code>38</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FOLDER-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>showHidden</code>: (<code>num</code>) is set to <code>0</code> if hidden files should be skipped and any other value if not</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>iterId</code>: (<code>num</code>) will be set to the FOLDER-ITER-ID or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FOLDER_OPEN_ITER = 38L;
	/**
	 * <b>INT_FILE_LENGTH</b>: get the length of a file<br>
	 * value: <code>39</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FILE-)ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>len</code>: (<code>num</code>) the file length in bytes or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FILE_LENGTH = 39L;
	/**
	 * <b>INT_FILE_TRUNCATE</b>: set the length of a file<br>
	 * value: <code>40</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FILE-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>len</code>: (<code>num</code>) the new length of the file</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) will be set <code>1</code> on success or <code>0</code> on error</li>
	 * </ul>
	 * this will append zeros to the file when the new length is larger than the old length or remove all content after the new length<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_FILE_TRUNCATE = 40L;
	/**
	 * <b>INT_HANDLE_OPEN_STREAM</b>: opens a stream from a file or pipe handle<br>
	 * value: <code>41</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (FILE-/PIPE-)ELEMENT-ID</li>
	 * <li><code>X01</code> <code>flags</code>: (<code>udword</code>) the open flags (see the <code>STREAM_*</code> constants)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>streamId</code>: (<code>num</code>) the STREAM-ID or <code>-1</code> on error</li>
	 * </ul>
	 * note that this interrupt works for both files and pipes, but will fail for folders<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_HANDLE_OPEN_STREAM = 41L;
	/**
	 * <b>INT_PIPE_LENGTH</b>: get the length of a pipe<br>
	 * value: <code>42</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (PIPE-)ELEMENT-ID</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X01</code> <code>len</code>: (<code>num</code>) the pipe length in bytes or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_PIPE_LENGTH = 42L;
	/**
	 * <b>INT_TIME_GET</b>: get the current system time<br>
	 * value: <code>43</code>
	 * <p>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>secs</code>: (<code>num</code>) the curent system time in seconds since the epoch</li>
	 * <li><code>X01</code> <code>nanos</code>: (<code>num</code>) the current additional nanoseconds system time</li>
	 * <li><code>X02</code> <code>success</code>: (<code>num</code>) <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_GET = 43L;
	/**
	 * <b>INT_TIME_RES</b>: get the system time resolution<br>
	 * value: <code>44</code>
	 * 
	 * <ul>
	 * <li><code>X00</code> <code>secs</code>: (<code>num</code>) the resolution in seconds</li>
	 * <li><code>X01</code> <code>nanos</code>: (<code>num</code>) the additional nanoseconds resulution</li>
	 * <li><code>X02</code> <code>success</code>: (<code>num</code>) <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_RES = 44L;
	/**
	 * <b>INT_TIME_SLEEP</b>: to sleep the given time in nanoseconds<br>
	 * value: <code>45</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>secs</code>: (<code>num</code>) the number of seconds to wait (only values GREATER or equal to <code>0</code> are allowed)</li>
	 * <li><code>X01</code> <code>nanos</code>: (<code>num</code>) the number of nanoseconds to wait (only values from <code>0</code> to <code>999999999</code> are allowed)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>remainSecs</code>: (<code>num</code>) the remaining number of seconds to wait</li>
	 * <li><code>X01</code> <code>remainNanos</code>: (<code>num</code>) the remaining number of nanoseconds to wait</li>
	 * <li><code>X02</code> <code>success</code>: (<code>num</code>) <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * note that <code>X00</code> will not be negative if the progress waited too long<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_SLEEP = 45L;
	/**
	 * <b>INT_TIME_WAIT</b>: to wait the given time in nanoseconds<br>
	 * value: <code>46</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>secs</code>: (<code>num</code>) the number of seconds since the epoch (only values GREATER or equal to <code>0</code> are allowed)</li>
	 * <li><code>X01</code> <code>nanos</code>: (<code>num</code>) the additional number of nanoseconds (only values from <code>0</code> to <code>999999999</code> are allowed)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X02</code> <code>success</code>: (<code>num</code>) <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * this interrupt will wait until the current system time is equal or after the given absolute time.<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_TIME_WAIT = 46L;
	/**
	 * <b>INT_RND_OPEN</b>: open a read stream which delivers random values<br>
	 * value: <code>47</code>
	 * <p>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>id</code>: (<code>num</code>) the (PIPE-)STREAM-ID or <code>-1</code> on error</li>
	 * </ul>
	 * the stream will only support read operations
	 * <ul>
	 * <li>not write/append or seek/setpos operations</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_RND_OPEN = 47L;
	/**
	 * <b>INT_RND_NUM</b>: sets `X00` to a random number<br>
	 * value: <code>48</code>
	 * <p>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>rnd</code>: (<code>num</code>) a random non negative number or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_RND_NUM = 48L;
	/**
	 * <b>INT_MEM_CMP</b>: memory compare<br>
	 * value: <code>49</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>memA</code>: (<code>ubyte#</code>) points to the first memory block</li>
	 * <li><code>X01</code> <code>memB</code>: (<code>ubyte#</code>) points to the second memory block</li>
	 * <li><code>X02</code> <code>len</code>: (<code>num</code>) has the length in bytes of both memory blocks</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>STATUS</code> <code>cmpRes</code>: (<code>LOWER</code>, <code>GREATER</code>, <code>EQUAL</code>) if <code>memA</code> is lower/greater/equal than/to <code>memB</code></li>
	 * </ul>
	 * compares two blocks of memory<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_CMP = 49L;
	/**
	 * <b>INT_MEM_CPY</b>: memory copy<br>
	 * value: <code>50</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>dstMem</code>: (<code>ubyte#</code>) points to the target memory block</li>
	 * <li><code>X01</code> <code>srcMem</code>: (<code>ubyte#</code>) points to the source memory block</li>
	 * <li><code>X02</code> <code>len</code>: (<code>num</code>) has the length of bytes to be copied</li>
	 * </ul>
	 * copies a block of memory<br>
	 * this function has undefined behavior if the two blocks overlap<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_CPY = 50L;
	/**
	 * <b>INT_MEM_MOV</b>: memory move<br>
	 * value: <code>51</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>srcMem</code>: (<code>ubyte#</code>) points to the target memory block</li>
	 * <li><code>X01</code> <code>dstMem</code>: (<code>ubyte#</code>) points to the source memory block</li>
	 * <li><code>X02</code> <code>len</code>: (<code>num</code>) has the length of bytes to be copied</li>
	 * </ul>
	 * copies a block of memory<br>
	 * this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_MOV = 51L;
	/**
	 * <b>INT_MEM_BSET</b>: memory byte set<br>
	 * value: <code>52</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>mem</code>: (<code>ubyte#</code>) points to the block</li>
	 * <li><code>X01</code> <code>val</code>: (<code>ubyte</code>) the first byte contains the value to be written to each byte</li>
	 * <li><code>X02</code> <code>len</code>: (<code>num</code>) contains the length in bytes</li>
	 * </ul>
	 * sets a memory block to the given byte-value<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_MEM_BSET = 52L;
	/**
	 * <b>INT_STR_LEN</b>: string length<br>
	 * value: <code>53</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>str</code>: (<code>char#</code>) points to the STRING</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>len</code>: (<code>num</code>) the length of the string the (byte-)offset of the first byte from the <code>'\0'</code> character</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_LEN = 53L;
	/**
	 * <b>INT_STR_CMP</b>: string compare<br>
	 * value: <code>54</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>strA</code>: (<code>char#</code>) points to the first STRING</li>
	 * <li><code>X01</code> <code>strB</code>: (<code>char#</code>) points to the second STRING</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>STATUS</code> <code>cmpRes</code>: (<code>LOWER</code>, <code>GREATER</code>, <code>EQUAL</code>) if <code>memA</code> is lower/greater/equal than/to <code>memB</code></li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_CMP = 54L;
	/**
	 * <b>INT_STR_FROM_NUM</b>: number to string<br>
	 * value: <code>55</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>val</code>: (<code>num</code>) is set to the number to convert</li>
	 * <li><code>X01</code> <code>buf</code>: (<code>char#</code>) is points to the buffer to be filled with the number in a STRING representation</li>
	 * <li><code>X02</code> <code>base</code>: (<code>num</code>) contains the base of the number system (must be between <code>2</code> and <code>36</code> (both inclusive))</li>
	 * <li><code>X03</code> <code>bufLen</code>: (<code>unum</code>) is set to the length of the buffer or <code>0</code> if the buffer should be allocated</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>strLen</code>: (<code>num</code>) will be set to the size of the STRING (without the <code>\0</code> terminating character)</li>
	 * <li><code>X01</code> <code>str</code>: (<code>char#</code>) will be set to the new buffer</li>
	 * <li><code>X03</code> <code>newBufLen</code>: (<code>num</code>) will be set to the new size of the buffer or <code>-1</code> on error</li>
	 * </ul>
	 * the new length will be the old length or if the old length is smaller than the size of the STRING (with <code>\0</code>) than the size of the STRING (with <code>\0</code>)<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_NUM = 55L;
	/**
	 * <b>INT_STR_FROM_FPNUM</b>: floating point number to string<br>
	 * value: <code>56</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>val</code>: (<code>fpnum</code>) is set to the floating point number to convert</li>
	 * <li><code>X01</code> <code>buf</code>: (<code>char#</code>) points to the buffer to be filled with the number in a STRING format</li>
	 * <li><code>X02</code> <code>bufLen</code>: (<code>unum</code>) is set to the current length of the buffer or <code>0</code> when the buffer should be allocated</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>strLen</code>: (<code>num</code>) will be set to the size of the STRING (without the <code>\0</code> terminator)</li>
	 * <li><code>X01</code> <code>str</code>: (<code>char#</code>) will be set to the new buffer or <code>-1</code> on error</li>
	 * <li><code>X02</code> <code>bufLen</code>: (<code>num</code>) will be set to the new size of the buffer</li>
	 * </ul>
	 * the new length will be the old length or if the old length is smaller than the size of the STRING (with <code>\0</code>) than the size of the STRING (with <code>\0</code>)<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_FPNUM = 56L;
	/**
	 * <b>INT_STR_TO_NUM</b>: string to number<br>
	 * value: <code>57</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>str</code>: (<code>char#</code>) points to the STRING</li>
	 * <li><code>X01</code> <code>base</code>: (<code>num</code>) the base of the number system (between <code>2</code> and <code>36</code> (both inclusive))</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>val</code>: (<code>num</code>) will be set to the converted number</li>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * if the STRING represents a value out of the 64-bit number range <code>X00</code> will be min or max value and <code>ERRNO</code> will be set to <code>ERR_OUT_OF_RANGE</code><br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_NUM = 57L;
	/**
	 * <b>INT_STR_TO_FPNUM</b>: string to floating point number<br>
	 * value: <code>58</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>str</code>: (<code>char#</code>) points to the STRING</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>val</code>: (<code>fpnum</code>) the converted number</li>
	 * <li><code>X01</code> <code>success</code>: (<code>num</code>) <code>1</code> on success and <code>0</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_FPNUM = 58L;
	/**
	 * <b>INT_STR_TO_U16STR</b>: STRING to U16-STRING<br>
	 * value: <code>59</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>u8str</code>: (<code>char#</code>) points to the STRING (<code>UTF-8</code>)</li>
	 * <li><code>X01</code> <code>u16str</code>: (<code>uword#</code>) points to the buffer to be filled with the to <code>UTF-16</code> converted string</li>
	 * <li><code>X02</code> <code>bufLen</code>: (<code>num</code>) the length of the buffer (in bytes)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>u8strUnconcStart</code>: (<code>char#</code>) points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)</li>
	 * <li><code>X01</code> <code>u16strUnconvStart</code>: (<code>uword#</code>) points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> <code>remBufLen</code>: (<code>num</code>) will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> <code>remU8Len</code>: (<code>num</code>) will be set to the number of converted characters or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_U16STR = 59L;
	/**
	 * <b>INT_STR_TO_U32STR</b>: STRING to U32-STRING<br>
	 * value: <code>60</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>u8str</code>: (<code>char#</code>) points to the STRING (<code>UTF-8</code>)</li>
	 * <li><code>X01</code> <code>u32str</code>: (<code>udword#</code>) points to the buffer to be filled with the to <code>UTF-32</code> converted string</li>
	 * <li><code>X02</code> <code>bufLen</code>: (<code>num</code>) the length of the buffer (in bytes)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>u8strUnconcStart</code>: (<code>char#</code>) points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)</li>
	 * <li><code>X01</code> <code>u32strUnconvStart</code>: (<code>udword#</code>) points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> <code>remBufLen</code>: (<code>num</code>) will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> <code>remU8Len</code>: (<code>num</code>) will be set to the number of converted characters or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_TO_U32STR = 60L;
	/**
	 * <b>INT_STR_FROM_U16STR</b>: U16-STRING to STRING<br>
	 * value: <code>61</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>u16str</code>: (<code>uword#</code>) points to the <code>UTF-16</code> STRING</li>
	 * <li><code>X01</code> <code>u8str</code>: (<code>char#</code>) points to the buffer to be filled with the converted STRING (<code>UTF-8</code>)</li>
	 * <li><code>X02</code> <code>bufLen</code>: (<code>num</code>) the length of the buffer (in bytes)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>u16strUnconcStart</code>: (<code>uword#</code>) points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)</li>
	 * <li><code>X01</code> <code>u8strUnconvStart</code>: (<code>char#</code>) points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> <code>remBufLen</code>: (<code>num</code>) will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> <code>remU8Len</code>: (<code>num</code>) will be set to the number of converted characters or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_U16STR = 61L;
	/**
	 * <b>INT_STR_FROM_U32STR</b>: U32-STRING to STRING<br>
	 * value: <code>62</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>u32str</code>: (<code>uword#</code>) points to the <code>UTF-32</code> STRING</li>
	 * <li><code>X01</code> <code>u8str</code>: (<code>char#</code>) points to the buffer to be filled with the converted STRING (<code>UTF-8</code>)</li>
	 * <li><code>X02</code> <code>bufLen</code>: (<code>num</code>) the length of the buffer (in bytes)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>u32strUnconcStart</code>: (<code>uword#</code>) points to the start of the unconverted sequenze (or behind the <code>\0</code> terminator)</li>
	 * <li><code>X01</code> <code>u8strUnconvStart</code>: (<code>char#</code>) points to the start of the unmodified space of the target buffer</li>
	 * <li><code>X02</code> <code>remBufLen</code>: (<code>num</code>) will be set to unmodified space at the end of the buffer</li>
	 * <li><code>X03</code> <code>remU8Len</code>: (<code>num</code>) will be set to the number of converted characters or <code>-1</code> on error</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FROM_U32STR = 62L;
	/**
	 * <b>INT_STR_FORMAT</b>: format string<br>
	 * value: <code>63</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>frmtStr</code>: (<code>char#</code>) is set to the STRING input</li>
	 * <li><code>X01</code> <code>outStr</code>: (<code>char#</code>) contains the buffer for the STRING output</li>
	 * <li><code>X02</code> <code>bufLen</code>: (<code>num</code>) the length of the buffer</li>
	 * <li><code>X03</code> <code>args</code>: (<code>num#</code>) points to the formatting arguments (note that every argument will consume 64 bits)</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>outLen</code>: (<code>num</code>) will be set to the length of the output string (the offset of the <code>\0</code> character) or <code>-1</code> on error</li>
	 * </ul>
	 * if <code>outLne</code> is larger or equal to <code>bufLen</code>, only the first <code>bufLen</code> bytes will be written to the buffer<br>
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
	 * <li><code>%d</code>: the next argument contains a number, which should be converted to a STRING using the decimal number system (<code>10</code>) and than be inserted here</li>
	 * <li><code>%f</code>: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here</li>
	 * <li><code>%p</code>: the next argument contains a pointer, which should be converted to a STRING
	 * <ul>
	 * <li>if the pointer is not <code>-1</code> the pointer will be converted by placing a <code>"p-"</code> and then the unsigned pointer-number converted to a STRING using the hexadecimal number system (<code>16</code>)</li>
	 * <li>if the pointer is <code>-1</code> it will be converted to the STRING <code>"p-inval"</code></li>
	 * </ul></li>
	 * <li><code>%h</code>: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system (<code>16</code>) and than be inserted here</li>
	 * <li><code>%b</code>: the next argument contains a number, which should be converted to a STRING using the binary number system (<code>2</code>) and than be inserted here</li>
	 * <li><code>%o</code>: the next argument contains a number, which should be converted to a STRING using the octal number system (<code>8</code>) and than be inserted here</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_STR_FORMAT = 63L;
	/**
	 * <b>INT_LOAD_FILE</b>: load a file<br>
	 * value: <code>64</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>file</code>: (<code>char#</code>) is set to the path (inclusive name) of the file</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>data</code>: (<code>ubyte#</code>) points to the memory block, in which the file has been loaded or <code>-1</code> on error</li>
	 * <li><code>X01</code> <code>len</code>: (<code>num</code>) the length of the file (and the memory block)</li>
	 * </ul>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_LOAD_FILE = 64L;
	/**
	 * <b>INT_LOAD_LIB</b>: load a library file<br>
	 * value: <code>65</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>file</code>: (<code>char#</code>) is set to the path (inclusive name) of the file</li>
	 * </ul>
	 * result values:
	 * <ul>
	 * <li><code>X00</code> <code>data</code>: (<code>ubyte#</code>) points to the memory block, in which the file has been loaded</li>
	 * <li><code>X01</code> <code>len</code>: (<code>num</code>) the length of the file (and the memory block)</li>
	 * <li><code>X02</code> <code>loaded</code>: (<code>num</code>) <code>1</code> if the file has been loaded as result of this interrupt and <code>0</code> if the file was already loaded</li>
	 * </ul>
	 * similar like the load file interrupt loads a file for the program.
	 * <ul>
	 * <li>the difference is that this interrupt remembers which files has been loaded</li>
	 * <li>the other difference is that the file may only be unloaded with the unload lib interrupt (not with the free interrupt)
	 * <ul>
	 * <li>the returned memory block also can not be resized</li>
	 * </ul></li>
	 * <li>if the interrupt is executed multiple times with the same file, it will return every time the same memory block.</li>
	 * <li>this interrupt does not recognize files loaded with the <code>64</code> (<code>INT_LOAD_FILE</code>) interrupt.</li>
	 * </ul>
	 * when an error occurred <code>X01</code> will be set to <code>-1</code>, <code>X00</code> will be unmodified and <code>ERRNO</code> will be set to a non-zero value<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
	public static final long INT_LOAD_LIB = 65L;
	/**
	 * <b>INT_UNLOAD_LIB</b>: unload a library file<br>
	 * value: <code>66</code>
	 * <p>
	 * params:
	 * <ul>
	 * <li><code>X00</code> <code>data</code>: (<code>ubyte#</code>) points to the start of the memory block loaded with <code>INT_LOAD_LIB</code></li>
	 * </ul>
	 * unloads a library previously loaded with the load lib interrupt<br>
	 * this interrupt will ensure that the given memory block will be freed and not again be returned from the load lib interrupt<br>
	 * this value can be used by the <code>INT</code> command to indicate that this interrupt should be called	 */
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
	 * <b>ERR_NO_MORE_ELEMENTS</b>: indicates that there are no more params<br>
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
	 * <b>FLAG_HIDDEN</b>: flag for hidden params<br>
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
	/**
	 * <b>STATUS_LOWER</b>: indicates that the last compare of A and B resulted in `A` is lower than `B`<br>
	 * value: <code>0x0000000000000001</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_LOWER = 0x0000000000000001L;
	/**
	 * <b>STATUS_GREATER</b>: indicates that the last compare of A and B resulted in `A` is greater than `B`<br>
	 * value: <code>0x0000000000000002</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_GREATER = 0x0000000000000002L;
	/**
	 * <b>STATUS_EQUAL</b>: indicates that the last compare of A and B resulted in `A` is equal to `B`<br>
	 * value: <code>0x0000000000000004</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_EQUAL = 0x0000000000000004L;
	/**
	 * <b>STATUS_OVERFLOW</b>: indicates that an overflow was detected<br>
	 * value: <code>0x0000000000000008</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_OVERFLOW = 0x0000000000000008L;
	/**
	 * <b>STATUS_ZERO</b>: indicates that the result of the operation was zero<br>
	 * value: <code>0x0000000000000010</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_ZERO = 0x0000000000000010L;
	/**
	 * <b>STATUS_NAN</b>: indicates that the result of the operation was a NAN value<br>
	 * value: <code>0x0000000000000020</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_NAN = 0x0000000000000020L;
	/**
	 * <b>STATUS_ALL_BITS</b>: indicates that the last bit compare all bits were set<br>
	 * value: <code>0x0000000000000040</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_ALL_BITS = 0x0000000000000040L;
	/**
	 * <b>STATUS_SOME_BITS</b>: indicates that the last bit compare some bits were set<br>
	 * value: <code>0x0000000000000080</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_SOME_BITS = 0x0000000000000080L;
	/**
	 * <b>STATUS_NONE_BITS</b>: indicates that the last bit compare no bits were set<br>
	 * value: <code>0x0000000000000100</code>
	 * <p>
	 * this value is used for the <code>STATUS</code> register	 */
	public static final long STATUS_NONE_BITS = 0x0000000000000100L;
	
	// here is the end of the automatic generated code-block
	// GENERATED-CODE-END
	
}
