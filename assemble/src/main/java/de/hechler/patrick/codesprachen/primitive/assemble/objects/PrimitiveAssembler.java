package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiConsumer;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.assemble.enums.PrimitiveFileTypes;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;

public class PrimitiveAssembler {
	
	public static final Map <String, PrimitiveConstant> START_CONSTANTS;
	
	private static final byte[] INERPRETER_START = "#!/bin/pvm        --pmc\n".getBytes(StandardCharsets.US_ASCII);
	
	public static final Path START_CONSTANTS_PATH = Paths.get("[START_CONSTANTS]");
	
	static { // @formatter:off
		Map<String, PrimitiveConstant> startConstants = new LinkedHashMap<>();
		startConstants.put("INT_ERRORS_ILLEGAL_INTERRUPT", new PrimitiveConstant("INT_ERRORS_ILLEGAL_INTERRUPT",
				  "|:  * `0`: illegal interrupt\n"
				+ "|         * `X00` contains the number of the illegal interrupt\n"
				+ "|         * calls the exit interrupt with `(64 + illegal_interrup_number)`\n"
				+ "|         * if the forbidden interrupt is the exit input, the program exits with `(64 + 4) = 68`, but does not calls the exit interrupt to do so\n"
				+ "|         * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `63`\n"
				+ "|:>", 0, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_ERRORS_UNKNOWN_COMMAND", new PrimitiveConstant("INT_ERRORS_UNKNOWN_COMMAND",
				  "|:  * `1`: unknown command\n"
				+ "|         * `X00` contains the illegal command\n"
				+ "|         * calls the exit interrupt with `62`\n"
				+ "|:>", 1, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_ERRORS_ILLEGAL_MEMORY", new PrimitiveConstant("INT_ERRORS_ILLEGAL_MEMORY",
				  "|:  * `2`: illegal memory\n"
				+ "|         * calls the exit interrupt with `61`\n"
				+ "|:>", 2, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_ERRORS_ARITHMETIC_ERROR", new PrimitiveConstant("INT_ERRORS_ARITHMETIC_ERROR",
				  "|:  * `3`: arithmetic error\n"
				+ "|         * calls the exit interrupt with `60`\n"
				+ "|:>", 3, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_EXIT", new PrimitiveConstant("INT_EXIT",
				  "|:  * `4`: exit\n"
				+ "|         * use `X00` to specify the exit number of the progress\n"
				+ "|:>", 4, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_MEMORY_ALLOC", new PrimitiveConstant("INT_MEMORY_ALLOC",
				  "|:  * `5`: allocate a memory-block\n"
				+ "|         * `X00` saves the size of the block\n"
				+ "|         * if the value of `X00` is `-1` after the call the memory-block could not be allocated\n"
				+ "|         * if the value of `X00` is not `-1`, `X00` points to the first element of the allocated memory-block\n"
				+ "|:>", 5, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_MEMORY_REALLOC", new PrimitiveConstant("INT_MEMORY_REALLOC",
				  "|:  * `6`: reallocate a memory-block\n"
				+ "|         * `X00` points to the memory-block\n"
				+ "|         * `X01` saves the new size of the memory-block\n"
				+ "|         * if the value of `X01` is `-1` after the call the memory-block could not be reallocated, the old memory-block will remain valid and may be used and should be freed if it is not longer needed\n"
				+ "|         * if the value of `X01` is not `-1`, `X01` points to the first element of the allocated memory-block and the old memory-block was automatically freed, so it should not be used\n"
				+ "|:>", 6, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_MEMORY_FREE", new PrimitiveConstant("INT_MEMORY_FREE",
				  "|:  * `7`: free a memory-block\n"
				+ "|         * `X00` points to the old memory-block\n"
				+ "|         * after this the memory-block should not be used\n"
				+ "|:>", 7, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_NEW_IN", new PrimitiveConstant("INT_STREAMS_NEW_IN",
				  "|:  * `8`: open new in stream\n"
				+ "|         * `X00` contains a pointer to the STRING, which refers to the file which should be read\n"
				+ "|         * opens a new in stream to the specified file\n"
				+ "|         * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n"
				+ "|         * output operations are not supported on the new stream\n"
				+ "|:>", 8, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_NEW_OUT", new PrimitiveConstant("INT_STREAMS_NEW_OUT",
				  "|:  * `9`: open new out stream\n"
				+ "|         * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "|         * opens a new out stream to the specified file\n"
				+ "|         * if the file exist already it's contend will be overwritten\n"
				+ "|         * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n"
				+ "|         * input operations are not supported on the new stream\n"
				+ "|:>", 9, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_NEW_APPEND", new PrimitiveConstant("INT_STREAMS_NEW_APPEND",
				  "|:  * `10`: open new out, append stream\n"
				+ "|         * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "|         * opens a new out stream to the specified file\n"
				+ "|         * if the file exist already it's contend will be overwritten\n"
				+ "|         * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n"
				+ "|:>", 10, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_NEW_IN_OUT", new PrimitiveConstant("INT_STREAMS_NEW_IN_OUT",
				  "|:  * `11`: open new in/out stream\n"
				+ "|         * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "|         * opens a new out stream to the specified file\n"
				+ "|         * if the file exist already it's contend will be overwritten\n"
				+ "|         * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n"
				+ "|:>", 11, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_NEW_APPEND_IN_OUT", new PrimitiveConstant("INT_STREAMS_NEW_APPEND_IN_OUT",
				  "|:  * `12`: open new in/out, append stream\n"
				+ "|         * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "|         * opens a new out stream to the specified file\n"
				+ "|         * if the file exist already it's contend will be overwritten\n"
				+ "|         * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n"
				+ "|:>", 12, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_WRITE", new PrimitiveConstant("INT_STREAMS_WRITE",
				  "|:  * `13`: write\n"
				+ "|         * `X00` contains the STREAM-ID\n"
				+ "|         * `X01` contains the number of elements to write\n"
				+ "|         * `X02` points to the elements to write\n"
				+ "|         * after execution `X01` will contain the number of written elements or `-1` if an error occurred\n"
				+ "|:>", 13, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_READ", new PrimitiveConstant("INT_STREAMS_READ",
				  "|:  * `14`: read\n"
				+ "|         * `X00` contains the STREAM-ID\n"
				+ "|         * `X01` contains the number of elements to read\n"
				+ "|         * `X02` points to the elements to read\n"
				+ "|         * after execution `X01` will contain the number of elements, which has been read or `-1` if an error occurred.\n"
				+ "|         * if `X01` is `0` the end of the stream has reached\n"
				+ "|         * reading less bytes than expected does not mead that the stream has reached it's end\n"
				+ "|:>", 14, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_SYNC_STREAM", new PrimitiveConstant("INT_STREAMS_SYNC_STREAM",
				  "|:  * `15`: sync stream\n"
				+ "|         * `X00` contains the STREAM-ID\n"
				+ "|         * if `X00` is set to `-1`, it will be tried to syncronize everything\n"
				+ "|         * if the synchronization was successfully `X00` will be set to `1`, if not `0`\n"
				+ "|:>", 15, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_CLOSE_STREAM", new PrimitiveConstant("INT_STREAMS_CLOSE_STREAM",
				  "|:  * `16`: close stream\n"
				+ "|         * `X00` contains the STREAM-ID\n"
				+ "|         * if the stream was closed successfully `X00` will contain `1`, if not `0`\n"
				+ "|:>", 16, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_GET_POS", new PrimitiveConstant("INT_STREAMS_GET_POS",
				  "|:  * `17`: get stream pos\n"
				+ "|         * `X00` contains the STREAM-ID\n"
				+ "|         * `X01` will contain the position of the stream or `-1` if something went wrong.\n"
				+ "|:>", 17, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_SET_POS", new PrimitiveConstant("INT_STREAMS_SET_POS",
				  "|:  * `18`: set stream pos\n"
				+ "|         * `X00` contains the STREAM-ID\n"
				+ "|         * `X01` contains the position to be set.\n"
				+ "|         * if the stream-ID is the ID of a default stream the behavior is undefined.\n"
				+ "|         * `X01` will contain the new stream position.\n"
				+ "|:>", 18, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_SET_POS_TO_END", new PrimitiveConstant("INT_STREAMS_SET_POS_TO_END",
				  "|:  * `19`: set stream to end\n"
				+ "|         * `X00` contains the STREAM-ID\n"
				+ "|         * this will set the stream position to the end\n"
				+ "|         * `X01` will the new file pos or `-1` if something went wrong\n"
				+ "|:>", 19, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_REM", new PrimitiveConstant("INT_STREAMS_REM",
				  "|:  * `20`: remove file\n"
				+ "|         * `X00` contains a pointer of a STRING with the file\n"
				+ "|         * if the file was successfully removed `X00` will contain `1`, if not `0`\n"
				+ "|:>", 20, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_MK_DIR", new PrimitiveConstant("INT_STREAMS_MK_DIR",
				  "|:  * `21`: make dictionary\n"
				+ "|         * `X00` contains a pointer of a STRING with the dictionary\n"
				+ "|         * if the dictionary was successfully created `X00` will contain `1`, if not `0`\n"
				+ "|:>", 21, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STREAMS_REM_DIR", new PrimitiveConstant("INT_STREAMS_REM_DIR",
				  "|:  * `22`: remove dictionary\n"
				+ "|         * `X00` contains a pointer of a STRING with the dictionary\n"
				+ "|         * if the dictionary was successfully removed `X00` will contain `1`, if not `0`\n"
				+ "|         * if the dictionary is not empty this call will fail (and set `X00` to `0`)\n"
				+ "|:>", 22, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_TIME_GET", new PrimitiveConstant("INT_TIME_GET",
				  "|:  * `23`: to get the time in milliseconds\n"
				+ "|         * `X00` will contain the time in milliseconds or `-1` if not available\n"
				+ "|:>", 23, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_TIME_WAIT", new PrimitiveConstant("INT_TIME_WAIT",
				  "|:  * `24`: to wait the given time in nanoseconds\n"
				+ "|         * `X00` contain the number of nanoseconds to wait (only values from `0` to `999999999` are allowed)\n"
				+ "|         * `X01` contain the number of seconds to wait\n"
				+ "|         * `X00` and `X01` will contain the remaining time (`0` if it finished waiting)\n"
				+ "|         * `X02` will be `1` if the call was successfully and `0` if something went wrong\n"
				+ "|             * if `X02` is `1` the remaining time will always be `0`\n"
				+ "|             * if `X02` is `0` the remaining time will be greater `0`\n"
				+ "|         * `X00` will not be negative if the progress waited too long\n"
				+ "|:>", 24, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_SOCKET_CLIENT_CREATE", new PrimitiveConstant("INT_SOCKET_CLIENT_CREATE",
				  "|:  * `25`: socket client create\n"
				+ "|         * makes a new client socket\n"
				+ "|         * `X00` will be set to the SOCKET-ID or `-1` if the operation failed\n"
				+ "|:>", 25, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_SOCKET_CLIENT_CONNECT", new PrimitiveConstant("INT_SOCKET_CLIENT_CONNECT",
				  "|:  * `26`: socket client connect\n"
				+ "|         * `X00` points to the SOCKET-ID\n"
				+ "|         * `X01` points to a STRING, which names the host\n"
				+ "|         * `X02` contains the port\n"
				+ "|             * the port will be the normal number with the normal byte order\n"
				+ "|         * connects an client socket to the host on the port\n"
				+ "|         * `X01` will be set to the `1` on success and `0` on a fail\n"
				+ "|         * on success, the SOCKET-ID, can be used as a STREAM-ID\n"
				+ "|:>", 26, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_SOCKET_SERVER_CREATE", new PrimitiveConstant("INT_SOCKET_SERVER_CREATE",
				  "|:  * `27`: socket server create\n"
				+ "|         * `X00` contains the port\n"
				+ "|             * the port will be the normal number with the normal byte order\n"
				+ "|         * makes a new server socket\n"
				+ "|         * `X00` will be set to the SOCKET-ID or `-1` when the operation fails\n"
				+ "|:>", 27, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_SOCKET_SERVER_LISTEN", new PrimitiveConstant("INT_SOCKET_SERVER_LISTEN",
				  "|:  * `28`: socket server listens\n"
				+ "|         * `X00` contains the SOCKET-ID\n"
				+ "|         * `X01` contains the MAX_QUEUE length\n"
				+ "|         * let a server socket listen\n"
				+ "|         * `X01` will be set to `1` or `0` when the operation fails\n"
				+ "|:>", 28, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_SOCKET_SERVER_ACCEPT", new PrimitiveConstant("INT_SOCKET_SERVER_ACCEPT",
				  "|:  * `29`: socket server accept\n"
				+ "|         * `X00` contains the SOCKET-ID\n"
				+ "|         * let a server socket accept a client\n"
				+ "|         * this operation will block, until a client connects\n"
				+ "|         * `X01` will be set a new SOCKET-ID, which can be used as STREAM-ID, or `-1`\n"
				+ "|:>", 29, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_RANDOM", new PrimitiveConstant("INT_RANDOM",
				  "|:  * `30`: random\n"
				+ "|         * `X00` will be filled with random bits\n"
				+ "|:>", 30, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_MEMORY_COPY", new PrimitiveConstant("INT_MEMORY_COPY",
				  "|:  * `31`: memory copy\n"
				+ "|         * copies a block of memory\n"
				+ "|         * this function has undefined behavior if the two blocks overlap\n"
				+ "|         * `X00` points to the target memory block\n"
				+ "|         * `X01` points to the source memory block\n"
				+ "|         * `X02` has the length of bytes to bee copied\n"
				+ "|:>", 31, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_MEMORY_MOVE", new PrimitiveConstant("INT_MEMORY_MOVE",
				  "|:  * `32`: memory move\n"
				+ "|         * copies a block of memory\n"
				+ "|         * this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)\n"
				+ "|         * `X00` points to the target memory block\n"
				+ "|         * `X01` points to the source memory block\n"
				+ "|         * `X02` has the length of bytes to bee copied\n"
				+ "|:>", 32, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_MEMORY_BSET", new PrimitiveConstant("INT_MEMORY_BSET",
				  "|:  * `33`: memory byte set\n"
				+ "|         * sets a memory block to the given byte-value\n"
				+ "|         * `X00` points to the block\n"
				+ "|         * `X01` the first byte contains the value to be written to each byte\n"
				+ "|         * `X02` contains the length in bytes\n"
				+ "|:>", 33, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_MEMORY_SET", new PrimitiveConstant("INT_MEMORY_SET",
				  "|:  * `34`: memory set\n"
				+ "|         * sets a memory block to the given int64-value\n"
				+ "|         * `X00` points to the block\n"
				+ "|         * `X01` contains the value to be written to each element\n"
				+ "|         * `X02` contains the count of elements to be set\n"
				+ "|:>", 34, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STRING_LENGTH", new PrimitiveConstant("INT_STRING_LENGTH",
				  "|:  * `35`: string length\n"
				+ "|         * `X00` points to the STRING\n"
				+ "|         * `X00` will be set to the length of the string/ the (byte-)offset of the `'\\0'` character\n"
				+ "|:>", 35, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STRING_TO_NUMBER", new PrimitiveConstant("INT_STRING_TO_NUMBER",
				  "|:  * `36`: string to number\n"
				+ "|         * `X00` points to the STRING\n"
				+ "|         * `X01` points to the base of the number system\n"
				+ "|             * (for example `10` for the decimal system or `2` for the binary system)\n"
				+ "|         * `X00` will be set to the converted number\n"
				+ "|         * `X01` will point to the end of the number-STRING\n"
				+ "|             * this might be the `\\0'` terminating character\n"
				+ "|         * if the STRING contains illegal characters or the base is not valid, the behavior is undefined\n"
				+ "|         * this function will ignore leading space characters\n"
				+ "|:>", 36, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STRING_TO_FPNUMBER", new PrimitiveConstant("INT_STRING_TO_FPNUMBER",
				  "|:  * `37`: string to floating point number\n"
				+ "|         * `X00` points to the STRING\n"
				+ "|         * `X00` will be set to the converted number\n"
				+ "|         * `X01` will point to the end of the number-STRING\n"
				+ "|             * this might be the `\\0'` terminating character\n"
				+ "|         * if the STRING contains illegal characters or the base is not valid, the behavior is undefined\n"
				+ "|         * this function will ignore leading space characters\n"
				+ "|:>", 37, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_NUMBER_TO_STRING", new PrimitiveConstant("INT_NUMBER_TO_STRING",
				  "|:  * `38`: number to string\n"
				+ "|         * `X00` is set to the number to convert\n"
				+ "|         * `X01` is points to the buffer to be filled with the number in a STRING format\n"
				+ "|         * `X02` contains the base of the number system\n"
				+ "|             * the minimum base is `2`\n"
				+ "|             * the maximum base is `36`\n"
				+ "|             * other values lead to undefined behavior\n"
				+ "|         * `X00` will be set to the length of the STRING\n"
				+ "|:>", 38, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_FPNUMBER_TO_STRING", new PrimitiveConstant("INT_FPNUMBER_TO_STRING",
				  "|:  * `39`: floating point number to string\n"
				+ "|         * `X00` is set to the number to convert\n"
				+ "|         * `X02` contains the maximum amount of digits to be used to represent the floating point number\n"
				+ "|         * `X01` is points to the buffer to be filled with the number in a STRING format\n"
				+ "|:>", 39, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_STRING_FORMAT", 
				  new  PrimitiveConstant("INT_STRING_FORMAT",
				  "|:  * `40`: format string\n"
				+ "|         * `X00` is set to the STRING input\n"
				+ "|         * `X01` contains the buffer for the STRING output\n"
				+ "|             * if `X01` is set to `-1`, `X01` will be allocated to a memory block\n"
				+ "|                 * the allocated memory block will be exact large enough to contain the formatted STRING\n"
				+ "|                 * if there could not be allocated enough memory, `X01` will be set to `-1`\n"
				+ "|         * `X00` will be set to the length of the output string\n"
				+ "|         * the register `X02..XNN` are for the formatting parameters\n"
				+ "|             * if there are mor parameters used then there are registers the behavior is undefined.\n"
				+ "|                 * that leads to a maximum of 249 parameters.\n"
				+ "|         * formatting:\n"
				+ "|             * everything, which can not be formatted, will be delegated to the target buffer\n"
				+ "|             * `%s`: the next argument points to a STRING, which should be inserted here\n"
				+ "|             * `%c`: the next argument points to a character, which should be inserted here\n"
				+ "|                 * note that characters may contain more than one byte\n"
				+ "|                     * `BIN-0.......` -> one byte (equivalent to an ASCII character)\n"
				+ "|                     * `BIN-10......` -> invalid, treated as one byte\n"
				+ "|                     * `BIN-110.....` -> two bytes\n"
				+ "|                     * `BIN-1110....` -> three bytes\n"
				+ "|                     * `BIN-11110...` -> four bytes\n"
				+ "|                     * `BIN-111110..` -> invalid, treated as five byte\n"
				+ "|                     * `BIN-1111110.` -> invalid, treated as six byte\n"
				+ "|                     * `BIN-11111110` -> invalid, treated as seven byte\n"
				+ "|                     * `BIN-11111111` -> invalid, treated as eight byte\n"
				+ "|             * `%B`: the next argument points to a byte, which should be inserted here (without being converted to a STRING)\n"
				+ "|             * `%d`: the next argument contains a number, which should be converted to a STRING using the decimal number system and than be inserted here\n"
				+ "|             * `%f`: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here\n"
				+ "|             * `%p`: the next argument contains a pointer, which should be converted to a STRING\n"
				+ "|                 * if not the pointer will be converted by placing a `\"p-\"` and then the pointer-number converted to a STRING using the hexadecimal number system\n"
				+ "|                 * if the pointer is `-1` it will be converted to the STRING `\"---\"`\n"
				+ "|             * `%h`: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here\n"
				+ "|             * `%b`: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here\n"
				+ "|             * `%o`: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here\n"
				+ "|:>", 40, START_CONSTANTS_PATH, -1));
		startConstants.put("INT_LOAD_FILE", new PrimitiveConstant("INT_LOAD_FILE",
				  "|:  * `41`: load file\n"
				+ "|         * `X00` is set to the path (inclusive name) of the file\n"
				+ "|         * `X00` will point to the memory block, in which the file has been loaded\n"
				+ "|         * `X01` will be set to the length of the file (and the memory block)\n"
				+ "|         * when an error occured `X00` will be set to `-1`\n"
				+ "|:>", 41, START_CONSTANTS_PATH, -1));
		startConstants.put("INTERRUPT_COUNT", new PrimitiveConstant("INTERRUPT_COUNT", "|> the number of interrupts activated and implemented by default", 42L, START_CONSTANTS_PATH, -1));
		startConstants.put("MAX_VALUE", new PrimitiveConstant("MAX_VALUE", "|> the maximal value (2^63-1)", 0x7FFFFFFFFFFFFFFFL, START_CONSTANTS_PATH, -1));
		startConstants.put("MIN_VALUE", new PrimitiveConstant("MIN_VALUE", "|> the minimal value (-2^63)", -0x8000000000000000L, START_CONSTANTS_PATH, -1));
		startConstants.put("STD_IN", new PrimitiveConstant("STD_IN", "|> the std-in STREAM-ID", 0L, START_CONSTANTS_PATH, -1));
		startConstants.put("STD_OUT", new PrimitiveConstant("STD_OUT", "|> the std-out STREAM-ID", 1L, START_CONSTANTS_PATH, -1));
		startConstants.put("STD_LOG", new PrimitiveConstant("STD_LOG", "|> the std-log STREAM-ID", 2L, START_CONSTANTS_PATH, -1));
		startConstants.put("FP_NAN", new PrimitiveConstant("FP_NAN", "|> a NaN constant", 0x7FFE000000000000L, START_CONSTANTS_PATH, -1));
		startConstants.put("FP_MAX_VALUE", new PrimitiveConstant("FP_MAX_VALUE", "|> the maximal floating point number", 0x7FEFFFFFFFFFFFFFL, START_CONSTANTS_PATH, -1));
		startConstants.put("FP_MIN_VALUE", new PrimitiveConstant("FP_MIN_VALUE", "|> the minimal floating point number", 0x0000000000000001L, START_CONSTANTS_PATH, -1));
		startConstants.put("FP_POS_INFINITY", new PrimitiveConstant("FP_POS_INFINITY", "|> a floating point containing a positiv infinity value", 0x7FF0000000000000L, START_CONSTANTS_PATH, -1));
		startConstants.put("FP_NEG_INFINITY", new PrimitiveConstant("FP_NEG_INFINITY", "|> a floating point containing a negativ infinity value", 0xFFF0000000000000L, START_CONSTANTS_PATH, -1));
		START_CONSTANTS = Collections.unmodifiableMap(startConstants);
	} // @formatter:on
	
	private final OutputStream out;
	private final PrintStream exportOut;
	private final boolean supressWarn;
	private final boolean defaultAlign;
	private final boolean interpreterStart;
	private final Path[] lookups;
	
	public PrimitiveAssembler(OutputStream out) {
		this(out, false);
	}
	
	public PrimitiveAssembler(OutputStream out, boolean supressWarnings) {
		this(out, supressWarnings, true);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, File lookup, boolean supressWarnings) {
		this(out, exportOut, lookup.toPath(), supressWarnings);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path lookup, boolean supressWarnings) {
		this(out, exportOut, new Path[] {lookup }, supressWarnings);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path[] lookup, boolean supressWarnings) {
		this(out, exportOut, lookup, supressWarnings, true);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, boolean supressWarnings) {
		this(out, exportOut, supressWarnings, true);
	}
	
	public PrimitiveAssembler(OutputStream out, boolean supressWarnings, boolean defaultAlign) {
		this(out, supressWarnings, defaultAlign, true);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, File lookup, boolean supressWarnings, boolean defaultAlign) {
		this(out, exportOut, lookup.toPath(), supressWarnings, defaultAlign);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path lookup, boolean supressWarnings, boolean defaultAlign) {
		this(out, exportOut, new Path[] {lookup }, supressWarnings, defaultAlign);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path[] lookups, boolean supressWarnings, boolean defaultAlign) {
		this(out, exportOut, lookups, supressWarnings, defaultAlign, true);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, boolean supressWarnings, boolean defaultAlign) {
		this(out, exportOut, supressWarnings, defaultAlign, true);
	}
	
	public PrimitiveAssembler(OutputStream out, boolean supressWarnings, boolean defaultAlign, boolean interpreterStart) {
		this(out, null, supressWarnings, defaultAlign, interpreterStart);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, boolean supressWarnings, boolean defaultAlign, boolean interpreterStart) {
		this(out, exportOut, Paths.get("./"), supressWarnings, defaultAlign, interpreterStart);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, File lookup, boolean supressWarnings, boolean defaultAlign, boolean interpreterStart) {
		this(out, exportOut, lookup.toPath(), supressWarnings, defaultAlign, interpreterStart);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path lookup, boolean supressWarnings, boolean defaultAlign, boolean interpreterStart) {
		this(out, exportOut, new Path[] {lookup }, supressWarnings, defaultAlign, interpreterStart);
	}
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path[] lookup, boolean supressWarnings, boolean defaultAlign, boolean interpreterStart) {
		this.out = out;
		this.exportOut = exportOut;
		this.supressWarn = supressWarnings;
		this.defaultAlign = defaultAlign;
		this.interpreterStart = interpreterStart;
		this.lookups = lookup == null ? new Path[] {Paths.get("./") } : lookup;
	}
	
	public ParseContext preassemble(Path path) throws IOException, AssembleError {
		return preassemble(path, Files.newInputStream(path));
	}
	
	public ParseContext preassemble(Path path, InputStream in) throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in));
	}
	
	public ParseContext preassemble(Path path, InputStream in, Charset cs) throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in, cs));
	}
	
	public ParseContext preassemble(Path path, Reader in) throws IOException, AssembleError {
		return preassemble(path, in, new HashMap <>(START_CONSTANTS));
	}
	
	public ParseContext preassemble(Path path, InputStream in, Map <String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, InputStream in, Charset cs, Map <String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in, cs), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, Reader in, Map <String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, new ANTLRInputStream(in), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin) throws IOException, AssembleError {
		return preassemble(path, antlrin, new HashMap <>(START_CONSTANTS));
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, antlrin, new HashMap <>(predefinedConstants), true);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants, boolean bailError) throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, bailError ? new BailErrorStrategy() : null, bailError);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError)
			throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, null);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
			ANTLRErrorListener errorListener) throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, (line, charPos) -> {});
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
			ANTLRErrorListener errorListener, BiConsumer <Integer, Integer> enterConstPool) throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, "[THIS]");
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
			ANTLRErrorListener errorListener, BiConsumer <Integer, Integer> enterConstPool, String thisFile) throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, thisFile, new HashMap <>());
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
			ANTLRErrorListener errorListener, BiConsumer <Integer, Integer> enterConstPool, String thisFile, Map <String, List <Map <String, Long>>> readFiles)
			throws IOException, AssembleError {
		PrimitiveFileGrammarLexer lexer = new PrimitiveFileGrammarLexer(antlrin);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PrimitiveFileGrammarParser parser = new PrimitiveFileGrammarParser(tokens);
		if (errorHandler != null) {
			parser.setErrorHandler(errorHandler);
		}
		if (errorListener != null) {
			parser.addErrorListener(errorListener);
		}
		try {
			return parser.parse(path, 0L, defaultAlign, predefinedConstants, bailError, errorHandler, errorListener, enterConstPool, this, antlrin, thisFile, readFiles);
		} catch (ParseCancellationException e) {
			Throwable cause = e.getCause();
			if (cause == null) {
				throw e;
			}
			if (cause instanceof AssembleError) {
				assert false;// this should never happen
				AssembleError ae = (AssembleError) cause;
				handle(ae);
			} else if (cause instanceof AssembleRuntimeException) {
				assert false;// this should never happen, since this should not
								// be thrown
				AssembleRuntimeException ae = (AssembleRuntimeException) cause;
				handle(ae);
			} else if (cause instanceof NoViableAltException) {
				NoViableAltException nvae = (NoViableAltException) cause;
				handle(nvae);
			} else if (cause instanceof InputMismatchException) {
				InputMismatchException ime = (InputMismatchException) cause;
				handle(ime);
			} else {
				handleUnknwon(e);
			}
		} catch (AssembleRuntimeException ae) {
			assert false;// this should never happen, since this should not be
							// thrown
			handle(ae);
		} catch (AssembleError ae) {
			handle(ae);
		} catch (Throwable t) {
			handleUnknwon(t);
		}
		throw new InternalError("handle returned");
	}
	
	/*
	 * the handle methods will never return normally
	 * 
	 * they either throw an error or call System.exit(1)
	 */
	private void handleUnknwon(Throwable t) {
		if (t instanceof Error) {
			throw (Error) t;
		}
		throw new InternalError("unknwon error: " + t, t);
	}
	
	// private void fullPrint(Throwable t, String ident, String identAdd) {
	// String nextIdent = ident + identAdd;
	// System.err.println(t.getClass().getName());
	// System.err.println(ident + "message: " + t.getMessage());
	// if (t.getMessage() != t.getLocalizedMessage()) {
	// System.err.println(ident + "localized-message: " + t.getMessage());
	// }
	// System.err.println(ident + "stack-tract:");
	// for (StackTraceElement ste : t.getStackTrace()) {
	// System.err.println(nextIdent + ste);
	// }
	// for (Throwable s : t.getSuppressed()) {
	// System.err.print(ident + "suppressed: ");
	// fullPrint(s, nextIdent, identAdd);
	// }
	// Throwable cause = t.getCause();
	// if (cause != null) {
	// System.err.print(ident + "cause: ");
	// fullPrint(cause, nextIdent, identAdd);
	// }
	// }
	
	private void handle(InputMismatchException ime) {
		IntervalSet ets = ime.getExpectedTokens();
		Token ot = ime.getOffendingToken();
		handleIllegalInput(ime, ot, ets);
	}
	
	private void handle(NoViableAltException nvae) {
		IntervalSet ets = nvae.getExpectedTokens();
		Token ot = nvae.getOffendingToken();
		handleIllegalInput(nvae, ot, ets);
	}
	
	private void handleIllegalInput(Throwable t, Token ot, IntervalSet ets) throws AssembleError {
		// if (exitOnError) {
		// System.err.println("error: " + t);
		// System.err.println("at line: " + ot.getLine() + ':' + ot.getCharPositionInLine());
		// System.err.println("illegal input: " + ot.getText());
		// System.err.println(" token: " + tokenToString(ot.getType(),
		// PrimitiveFileGrammarLexer.ruleNames));
		// System.err.println("expected: ");
		// for (int i = 0; i < ets.size(); i ++ ) {
		// System.err.println(" " + tokenToString(ets.get(i), PrimitiveFileGrammarLexer.ruleNames));
		// }
		// System.err.flush();
		// System.exit(1);
		// } else {
		StringBuilder build = new StringBuilder("error: ").append(t).append("at line ").append(ot.getLine()).append(':').append(ot.getCharPositionInLine()).append(" token.text='")
				.append(ot.getText());
		build.append("' token.id=").append(tokenToString(ot.getType(), PrimitiveFileGrammarLexer.ruleNames)).append('\n').append("expected: ");
		for (int i = 0; i < ets.size(); i ++ ) {
			if (i > 0) {
				build.append(", ");
			}
			build.append(' ').append(tokenToString(ets.get(i), PrimitiveFileGrammarLexer.ruleNames));
		}
		throw new AssembleError(ot.getLine(), ot.getCharPositionInLine(), ot.getStopIndex() - ot.getStartIndex() + 1, ot.getStartIndex(), build.toString());
		// }
	}
	
	private String tokenToString(int tok, String[] names) {
		String token;
		if (tok > 0) {
			token = "<" + names[tok] + '>';
		} else if (tok == PrimitiveFileGrammarLexer.EOF) {
			token = "<EOF>";
		} else {
			token = "<UNKNOWN=" + tok + ">";
		}
		return token;
	}
	
	// private void handle(AssembleError ae) {
	// Throwable cause = ae.getCause();
	// try {
	// if (cause == null) {
	// throw new InternalError("cause is null", ae);
	// }
	// if ( ! (cause instanceof ParseCancellationException)) {
	// throw new InternalError("unknown cause class: " +
	// cause.getClass().getName(),
	// ae);
	// }
	// ParseCancellationException pce = (ParseCancellationException) cause;
	// cause = pce.getCause();
	// if (cause instanceof NoViableAltException) {
	// NoViableAltException nvae = (NoViableAltException) cause;
	// Token ot = nvae.getOffendingToken();
	// int line = ot.getLine();
	// int posInLine = ot.getCharPositionInLine();
	// if (line == 0) {
	// posInLine += ae.posInLine;
	// }
	// line += ae.line;
	// if (exitOnError) {
	// System.err.println("at " + line + ":" + posInLine + " was illegal input:
	// " +
	// ot.getText());
	// System.err.flush();
	// System.exit(1);
	// } else {
	// throw new Error("at line: " + line + " at char: " + posInLine + " was
	// illegal
	// input: " +
	// ot.getText(), nvae);
	// }
	// } else if (cause instanceof AssembleException) {
	// handle(ae.line, ae.posInLine, (AssembleException) cause);
	// } else {
	// throw new InternalError("unknown cause cause class: " +
	// cause.getClass().getName(), cause);
	// }
	// } catch (Throwable t) {
	// if (exitOnError) {
	// t.printStackTrace(System.err);
	// System.err.flush();
	// System.exit(1);
	// }
	// throw t;
	// }
	// }
	//
	private void handle(AssembleRuntimeException ae) {
		handle(ae.line, ae.posInLine, ae.length, ae.charPos, ae.getMessage(), ae.getStackTrace());
	}
	
	private void handle(AssembleError ae) {
		handle(ae.line, ae.posInLine, ae.length, ae.charPos, ae.getMessage(), ae.getStackTrace());
	}
	
	private void handle(int line, int posInLine, int len, int charPos, String msg, StackTraceElement[] stack) {
		StringBuilder build = new StringBuilder();
		build.append("an error occured at line: ").append(line).append(':').append(posInLine).append(" length=").append(len).append('\n');
		build.append(msg).append('\n');
		build.append("stack:").append('\n');
		for (StackTraceElement ste : stack) {
			build.append("  at ").append(ste).append('\n');
		}
//		if (exitOnError) {
//			System.err.print(build);
//			System.err.flush();
//			System.exit(1);
//		} else {
			throw new AssembleError(line, posInLine, len, charPos, build.toString());
//		}
	}
	
	public void assemble(Path path) throws IOException, AssembleError {
		assemble(preassemble(path));
	}
	
	public void assemble(Path path, InputStream in) throws IOException, AssembleError {
		assemble(preassemble(path, in));
	}
	
	public void assemble(Path path, InputStream in, Charset cs) throws IOException, AssembleError {
		assemble(preassemble(path, in, cs));
	}
	
	public void assemble(Path path, Reader in) throws IOException, AssembleError {
		assemble(preassemble(path, in));
	}
	
	public void assemble(Path path, ANTLRInputStream antlrin) throws IOException, AssembleError {
		assemble(preassemble(path, antlrin));
	}
	
	public void assemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		assemble(preassemble(path, antlrin, predefinedConstants));
	}
	
	public void assemble(PrimitiveFileGrammarParser.ParseContext parsed) throws IOException {
		assemble(parsed.commands, parsed.labels);
		export(parsed.exports);
	}
	
	public void export(Map <String, PrimitiveConstant> exports) {
		if (this.exportOut == null) {
			return;
		}
		export(exports, this.exportOut);
	}
	
	public static void export(Map <String, PrimitiveConstant> exports, PrintStream out) {
		exports.forEach((symbol, pc) -> {
			assert symbol.equals(pc.name);
			if (pc.comment != null) {
				for (String line : pc.comment.split("\r\n?|\n")) {
					if ( !line.matches("\\s*\\|.*")) {
						line = "|" + line;
					}
					line = line.trim();
					out.print(line + '\n');
				}
			}
			out.print(symbol + '=' + Long.toHexString(pc.value).toUpperCase() + '\n');
		});
	}
	
	public static void readSymbols(String prefix, Map <String, PrimitiveConstant> addSymbols, Scanner sc, Path path) {
		StringBuilder comment = new StringBuilder();
		int lineNumber = 1;
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.charAt(0) == '|') {
				comment.append(line);
				continue;
			}
			final String regex = "^#?([a-zA-Z_0-9]+)\\s*[=]\\s*([0-9a-fA-F]+)$";
			if ( !line.matches(regex)) {
				throw new RuntimeException("line does not match regex: line='" + line + "', regex='" + regex + "'");
			}
			String constName = line.replaceFirst(regex, "$1");
			long val = Long.parseUnsignedLong(line.replaceFirst(regex, "$2"), 16);
			PrimitiveConstant value;
			if (comment.length() == 0) {
				value = new PrimitiveConstant(constName, null, val, path, lineNumber);
			} else {
				value = new PrimitiveConstant(constName, comment.toString(), val, path, lineNumber);
				comment = new StringBuilder();
			}
			if (prefix == null) {
				addSymbols.put(constName, value);
			} else {
				addSymbols.put(prefix + constName, value);
			}
			lineNumber ++ ;
		}
	}
	
	public AssembleRuntimeException readSymbols(String readFile, Boolean isSource, String prefix, Map <String, PrimitiveConstant> startConsts,
			Map <String, PrimitiveConstant> addSymbols, ANTLRInputStream antlrin, boolean be, Token tok, String thisFile, Map <String, List <Map <String, Long>>> readFiles)
			throws IllegalArgumentException, IOException, RuntimeException {
		readFiles = new HashMap <>(readFiles);
		byte[] bytes = null;
		if (readFile.equals("[THIS]")) {
			assert isSource == null || isSource;
			isSource = true;
			bytes = antlrin.getText(new Interval(0, Integer.MAX_VALUE)).getBytes(StandardCharsets.UTF_8);
			readFile = thisFile;
		}
		boolean isPrimSourceCode;
		if (isSource != null) {
			isPrimSourceCode = isSource;
		} else {
			PrimitiveFileTypes type = PrimitiveFileTypes.getTypeFromName(readFile, PrimitiveFileTypes.primitiveMashineCode);
			switch (type) {
			case primitiveSourceCode:
				isPrimSourceCode = true;
				break;
			case primitiveSymbolFile:
				isPrimSourceCode = false;
				break;
			default:
				throw new IllegalArgumentException("Source/Symbol not set, but readFile is not *.psc and not *.psf! readFile='" + readFile + "'");
			}
		}
		try {
			final String rf = readFile;
			readFiles.compute(readFile, (String key, List <Map <String, Long>> oldValue) -> {
				List <Map <String, Long>> newValue;
				if (oldValue == null) {
					newValue = Arrays.asList(convertPrimConstMapToLongMap(startConsts));
				} else {
					for (Map <String, Long> startConstants : oldValue) {
						if (startConsts.equals(startConstants)) {
							if (be) {
								throw new AssembleError(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - tok.getStartIndex() + 1, tok.getStartIndex(),
										"loop detected! started again with the same start consts: in file='" + thisFile + "' read symbols of file='" + rf + "' constants: "
												+ startConsts);
							} else {
								throw new AssembleRuntimeException(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - tok.getStartIndex() + 1, tok.getStartIndex(),
										"loop detected! started again with the same start consts: in file='" + thisFile + "' read symbols of file='" + rf + "' constants: "
												+ startConsts);
							}
						}
					}
					newValue = new ArrayList <>(oldValue);
					newValue.add(convertPrimConstMapToLongMap(startConsts));
				}
				return newValue;
			});
		} catch (AssembleRuntimeException are) {
			return are;
		}
		Path path = Paths.get(readFile);
		if ( !path.isAbsolute()) {
			for (int i = 0; i < this.lookups.length; i ++ ) {
				Path p = Paths.get(this.lookups[i].toString(), readFile);
				if (Files.exists(p)) {
					path = p;
					break;
				}
			}
		}
		try (InputStream input = bytes != null ? new ByteArrayInputStream(bytes) : Files.newInputStream(path)) {
			InputStream in = input;
			if (isPrimSourceCode) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ParseContext pc = preassemble(path, new ANTLRInputStream(new InputStreamReader(in, StandardCharsets.UTF_8)), startConsts, be ? new BailErrorStrategy() : null, be,
						null, (line, charPos) -> {}, readFile, readFiles);
				export(pc.exports, new PrintStream(baos, true, "UTF-8"));
				in = new ByteArrayInputStream(baos.toByteArray());
			}
			try (Scanner sc = new Scanner(in, "UTF-8")) {
				readSymbols(prefix, addSymbols, sc, path);
			}
		}
		return null;
	}
	
	private Map <String, Long> convertPrimConstMapToLongMap(Map <String, PrimitiveConstant> startConsts) {
		Map <String, Long> nv = new HashMap <>();
		startConsts.forEach((n, pc) -> nv.put(n, pc.value));
		return nv;
	}
	
	public void assemble(List <Command> cmds, Map <String, Long> labels) throws IOException {
		if (this.interpreterStart) {
			this.out.write(INERPRETER_START);
		}
		long pos = 0;
		boolean align = defaultAlign;
		Command last = null, cmd;
		for (int i = 0; i < cmds.size(); i ++ ) {
			cmd = cmds.get(i);
			if (align && last != null && last.alignable()) {
				int mod = (int) (pos % 8);
				if (mod != 0) {
					int add = 8 - mod;
					byte[] bytes = new byte[add];
					out.write(bytes, 0, bytes.length);
					pos += add;
				}
			}
			if (cmd.getClass() == Command.class) {
				last = cmd;// only on command and constant-pool (not on
							// directives)
				byte[] bytes = new byte[8];
				bytes[0] = (byte) cmd.cmd.num;
				switch (cmd.cmd) {
				case CMD_MVAD:
					if (cmd.p3.art != Param.ART_ANUM) {
						throw new IllegalStateException("offset must be a constant! (cmd: CALO)");
					}
					writeTwoParam(cmd, bytes);
					convertLong(bytes, cmd.p3.num);
					out.write(bytes, 0, bytes.length);
					break;
				case CMD_CALO:
					if (cmd.p2.art != Param.ART_ANUM) {
						throw new IllegalStateException("offset must be a constant! (cmd: CALO)");
					}
					writeOneParam(cmd, bytes);
					convertLong(bytes, cmd.p2.num);
					out.write(bytes, 0, bytes.length);
					break;
				case CMD_RET:
				case CMD_IRET:
					break;// nothing more to write
				case CMD_NEG:
				case CMD_NOT:
				case CMD_PUSH:
				case CMD_DEC:
				case CMD_INC:
				case CMD_FPTN:
				case CMD_NTFP:
				case CMD_ISNAN:
				case CMD_ISINF:
					if (cmd.p1.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed!");
					}
				case CMD_INT:
				case CMD_POP:
					writeOneParam(cmd, bytes);
					break;
				case CMD_DIV:
				case CMD_SWAP:
					if (cmd.p2.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed on any param!");
					}
				case CMD_RASH:
				case CMD_RLSH:
				case CMD_LSH:
				case CMD_LEA:
				case CMD_MOV:
				case CMD_ADD:
				case CMD_ADDC:
				case CMD_ADDFP:
				case CMD_SUB:
				case CMD_SUBC:
				case CMD_SUBFP:
				case CMD_MUL:
				case CMD_MULFP:
				case CMD_DIVFP:
				case CMD_AND:
				case CMD_OR:
				case CMD_XOR:
					if (cmd.p1.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed on the first param!");
					}
				case CMD_CMP: {
					assert cmd.p1 != null;
					assert cmd.p2 != null;
					writeTwoParam(cmd, bytes);
					break;
				}
				case CMD_CALL:
				case CMD_JMP:
				case CMD_JMPCS:
				case CMD_JMPCC:
				case CMD_JMPEQ:
				case CMD_JMPGE:
				case CMD_JMPGT:
				case CMD_JMPLE:
				case CMD_JMPLT:
				case CMD_JMPNE:
				case CMD_JMPZS:
				case CMD_JMPZC: {
					assert cmd.p1 != null : "I need a first param!";
					assert cmd.p2 == null : "my command can not have a second param";
					out.write(bytes, 0, bytes.length);
					long relativeDest;
					if (cmd.p1.art == Param.ART_LABEL) {
						assert cmd.p1.num == 0;
						assert cmd.p1.off == 0;
						final long absoluteDest = Objects.requireNonNull(labels.get(cmd.p1.label),
								"can't find the used label '" + cmd.p1.label + "', I know the labels '" + labels + "'");
						assert absoluteDest >= 0;
						relativeDest = absoluteDest - pos;
						assert pos + relativeDest == absoluteDest;
					} else if (cmd.p1.art == Param.ART_ANUM) {
						assert cmd.p1.label == null;
						assert cmd.p1.off == 0;
						if ( !supressWarn) {
							System.err.println("[WARN]: it is not recomended to use jump/call operation with a number instead of a label as param!");
						}
						relativeDest = cmd.p1.num;
					} else {
						throw new IllegalStateException("illegal param art: " + Param.artToString(cmd.p1.art));
					}
					convertLong(bytes, relativeDest);
					break;
				}
				default:
					throw new IllegalStateException("unknown command enum: " + cmd.cmd.name());
				}
				out.write(bytes, 0, bytes.length);
			} else if (cmd instanceof CompilerCommandCommand) {
				CompilerCommandCommand cdc = (CompilerCommandCommand) cmd;
				assert 0 == cdc.length();
				switch (cdc.directive) {
				case align:
					align = true;
					break;
				case notAlign:
					align = false;
					break;
				default:
					throw new InternalError("unknown directive: " + cdc.directive.name());
				}
			} else if (cmd instanceof ConstantPoolCommand) {
				last = cmd;
				ConstantPoolCommand cpc = (ConstantPoolCommand) cmd;
				cpc.write(out);
			} else {
				throw new InternalError("inknown command class: " + cmd.getClass().getName());
			}
			pos += cmd.length();
		}
		out.flush();
	}
	
	private void writeOneParam(Command cmd, byte[] bytes) throws IOException {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p2 == null : "I can't have a second Param!";
		assert cmd.p1.label == null : "I dom't need a label in my params!";
		bytes[1] = (byte) cmd.p1.art;
		long num = cmd.p1.num, off = cmd.p1.off;
		int art = cmd.p1.art;
		switch (art) {
		case Param.ART_ANUM:
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ANUM_BNUM:
			if ( !supressWarn) {
				System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
			}
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, off);
			break;
		case Param.ART_ANUM_BREG:
			if ( !supressWarn) {
				System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
			}
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ANUM_BSR:
			Param.checkSR(off);
			bytes[7] = (byte) off;
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ASR:
			Param.checkSR(num);
			bytes[7] = (byte) num;
			break;
		case Param.ART_ASR_BNUM:
			Param.checkSR(num);
			bytes[7] = (byte) num;
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ASR_BREG:
			Param.checkSR(num);
			bytes[7] = (byte) num;
			break;
		case Param.ART_ASR_BSR:
			Param.checkSR(num);
			Param.checkSR(off);
			bytes[6] = (byte) off;
			bytes[7] = (byte) num;
			break;
		default:
			throw new InternalError("unknown art: " + art);
		}
	}
	
	private static void convertLong(byte[] bytes, long num) {
		bytes[0] = (byte) num;
		bytes[1] = (byte) (num >> 8);
		bytes[2] = (byte) (num >> 16);
		bytes[3] = (byte) (num >> 24);
		bytes[4] = (byte) (num >> 32);
		bytes[5] = (byte) (num >> 40);
		bytes[6] = (byte) (num >> 48);
		bytes[7] = (byte) (num >> 56);
	}
	
	private void writeTwoParam(Command cmd, byte[] bytes) throws IOException {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p2 != null : "I need a second Param!";
		assert cmd.p1.label == null : "I don't need a label in my params!";
		assert cmd.p2.label == null : "I don't need a label in my params!";
		final long p1num = cmd.p1.num, p1off = cmd.p1.off, p2num = cmd.p2.num, p2off = cmd.p2.off;
		final int p1art = cmd.p1.art, p2art = cmd.p2.art;
		int index = 7;
		bytes[1] = (byte) p1art;
		bytes[2] = (byte) p2art;
		{
			switch (p1art) {
			case Param.ART_ANUM:
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				break;
			case Param.ART_ANUM_BSR:
				Param.checkSR(p1off);
				bytes[index -- ] = (byte) p1off;
				break;
			case Param.ART_ASR:
				Param.checkSR(p1num);
				bytes[index -- ] = (byte) p1num;
				break;
			case Param.ART_ASR_BNUM:
				Param.checkSR(p1num);
				bytes[index -- ] = (byte) p1num;
				break;
			case Param.ART_ASR_BREG:
				Param.checkSR(p1num);
				bytes[index -- ] = (byte) p1num;
				break;
			case Param.ART_ASR_BSR:
				Param.checkSR(p1num);
				Param.checkSR(p1off);
				bytes[index -- ] = (byte) p1num;
				bytes[index -- ] = (byte) p1off;
				break;
			default:
				throw new InternalError("unknown art: " + p1art);
			}
			switch (p2art) {
			case Param.ART_ANUM:
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				break;
			case Param.ART_ANUM_BSR:
				Param.checkSR(p2off);
				bytes[index -- ] = (byte) p2off;
				break;
			case Param.ART_ASR:
				Param.checkSR(p2num);
				bytes[index -- ] = (byte) p2num;
				break;
			case Param.ART_ASR_BNUM:
				Param.checkSR(p2num);
				bytes[index -- ] = (byte) p2num;
				break;
			case Param.ART_ASR_BREG:
				Param.checkSR(p2num);
				bytes[index -- ] = (byte) p2num;
				break;
			case Param.ART_ASR_BSR:
				Param.checkSR(p2num);
				Param.checkSR(p2off);
				bytes[index -- ] = (byte) p2num;
				bytes[index -- ] = (byte) p2off;
				break;
			default:
				throw new InternalError("unknown art: " + p2art);
			}
		}
		{
			switch (p1art) {
			case Param.ART_ANUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1off);
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ANUM_BSR:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ASR:
				break;
			case Param.ART_ASR_BNUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1off);
				break;
			case Param.ART_ASR_BREG:
				break;
			case Param.ART_ASR_BSR:
				break;
			default:
				throw new InternalError("unknown art: " + p1art);
			}
			switch (p2art) {
			case Param.ART_ANUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2off);
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ANUM_BSR:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ASR:
				break;
			case Param.ART_ASR_BNUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2off);
				break;
			case Param.ART_ASR_BREG:
				break;
			case Param.ART_ASR_BSR:
				break;
			default:
				throw new InternalError("unknown art: " + p2art);
			}
		}
	}
	
}
