package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTextInterval;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTokenInfo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.RuleNode;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungDirektContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungGleichheitContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungPunktContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungRelativeTestsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungSchubContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungStrichContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.NummerNoConstantContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParamContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.SrContext;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.Param;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.TokenInfo;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

public class PscHoverProvider implements ITextHover {

	private static final Map<String, String> DEFAULT_CONSTANT_HELPS;

	static { //@formatter:off
		Map<String, String> defConstHelps = new HashMap<>();
		defConstHelps.put("INT_ERRORS_ILLEGAL_INTERRUPT", 
				  "* `0`: illegal interrupt\n"
				+ "    * `X00` contains the number of the illegal interrupt\n"
				+ "    * calls the exit interrupt with `(64 + illegal_interrup_number)`\n"
				+ "    * if the forbidden interrupt is the exit input, the program exits with `(64 + 4) = 68`, but does not calls the exit interrupt to do so\n"
				+ "    * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `63`\n");
		defConstHelps.put("INT_ERRORS_UNKNOWN_COMMAND", 
				  "* `1`: unknown command\n"
				+ "    * `X00` contains the illegal command\n"
				+ "    * calls the exit interrupt with `62`\n");
		defConstHelps.put("INT_ERRORS_ILLEGAL_MEMORY", 
				  "* `2`: illegal memory\n"
				+ "    * calls the exit interrupt with `61`\n");
		defConstHelps.put("INT_ERRORS_ARITHMETIC_ERROR", 
				  "* `3`: arithmetic error\n"
				+ "    * calls the exit interrupt with `60`\n");
		defConstHelps.put("INT_EXIT", 
				  "* `4`: exit\n"
				+ "    * use `X00` to specify the exit number of the progress\n");
		defConstHelps.put("INT_MEMORY_ALLOC", 
				  "* `5`: allocate a memory-block\n"
				+ "    * `X00` saves the size of the block\n"
				+ "    * if the value of `X00` is `-1` after the call the memory-block could not be allocated\n"
				+ "    * if the value of `X00` is not `-1`, `X00` points to the first element of the allocated memory-block\n");
		defConstHelps.put("INT_MEMORY_REALLOC", 
				  "* `6`: reallocate a memory-block\n"
				+ "    * `X00` points to the memory-block\n"
				+ "    * `X01` saves the new size of the memory-block\n"
				+ "    * if the value of `X01` is `-1` after the call the memory-block could not be reallocated, the old memory-block will remain valid and may be used and should be freed if it is not longer needed\n"
				+ "    * if the value of `X01` is not `-1`, `X01` points to the first element of the allocated memory-block and the old memory-block was automatically freed, so it should not be used\n");
		defConstHelps.put("INT_MEMORY_FREE", 
				  "* `7`: free a memory-block\n"
				+ "    * `X00` points to the old memory-block\n"
				+ "    * after this the memory-block should not be used\n");
		defConstHelps.put("INT_STREAMS_NEW_IN", 
				  "* `8`: open new in stream\n"
				+ "    * `X00` contains a pointer to the STRING, which refers to the file which should be read\n"
				+ "    * opens a new in stream to the specified file\n"
				+ "    * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n"
				+ "    * output operations are not supported on the new stream\n");
		defConstHelps.put("INT_STREAMS_NEW_OUT", 
				  "* `9`: open new out stream\n"
				+ "    * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "    * opens a new out stream to the specified file\n"
				+ "    * if the file exist already it's contend will be overwritten\n"
				+ "    * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n"
				+ "    * input operations are not supported on the new stream\n");
		defConstHelps.put("INT_STREAMS_NEW_APPEND", 
				  "* `10`: open new out, append stream\n"
				+ "    * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "    * opens a new out stream to the specified file\n"
				+ "    * if the file exist already it's contend will be overwritten\n"
				+ "    * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n");
		defConstHelps.put("INT_STREAMS_NEW_IN_OUT", 
				  "* `11`: open new in/out stream\n"
				+ "    * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "    * opens a new out stream to the specified file\n"
				+ "    * if the file exist already it's contend will be overwritten\n"
				+ "    * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n");
		defConstHelps.put("INT_STREAMS_NEW_APPEND_IN_OUT", 
				  "* `12`: open new in/out, append stream\n"
				+ "    * `X00` contains a pointer to the STRING, which refers to the file which should be created\n"
				+ "    * opens a new out stream to the specified file\n"
				+ "    * if the file exist already it's contend will be overwritten\n"
				+ "    * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`\n");
		defConstHelps.put("INT_STREAMS_WRITE", 
				  "* `13`: write\n"
				+ "    * `X00` contains the STREAM-ID\n"
				+ "    * `X01` contains the number of elements to write\n"
				+ "    * `X02` points to the elements to write\n"
				+ "    * after execution `X01` will contain the number of written elements or `-1` if an error occurred\n");
		defConstHelps.put("INT_STREAMS_READ", 
				  "* `14`: read\n"
				+ "    * `X00` contains the STREAM-ID\n"
				+ "    * `X01` contains the number of elements to read\n"
				+ "    * `X02` points to the elements to read\n"
				+ "    * after execution `X01` will contain the number of elements, which has been read or `-1` if an error occurred.\n"
				+ "    * if `X01` is `0` the end of the stream has reached\n"
				+ "    * reading less bytes than expected does not mead that the stream has reached it's end\n");
		defConstHelps.put("INT_STREAMS_SYNC_STREAM", 
				  "* `15`: sync stream\n"
				+ "    * `X00` contains the STREAM-ID\n"
				+ "    * if `X00` is set to `-1`, it will be tried to syncronize everything\n"
				+ "    * if the synchronization was successfully `X00` will be set to `1`, if not `0`\n");
		defConstHelps.put("INT_STREAMS_CLOSE_STREAM", 
				  "* `16`: close stream\n"
				+ "    * `X00` contains the STREAM-ID\n"
				+ "    * if the stream was closed successfully `X00` will contain `1`, if not `0`\n");
		defConstHelps.put("INT_STREAMS_GET_POS", 
				  "* `17`: get stream pos\n"
				+ "    * `X00` contains the STREAM-ID\n"
				+ "    * `X01` will contain the position of the stream or `-1` if something went wrong.\n");
		defConstHelps.put("INT_STREAMS_SET_POS", 
				  "* `18`: set stream pos\n"
				+ "    * `X00` contains the STREAM-ID\n"
				+ "    * `X01` contains the position to be set.\n"
				+ "    * if the stream-ID is the ID of a default stream the behavior is undefined.\n"
				+ "    * `X01` will contain the new stream position.\n");
		defConstHelps.put("INT_STREAMS_SET_POS_TO_END", 
				  "* `19`: set stream to end\n"
				+ "    * `X00` contains the STREAM-ID\n"
				+ "    * this will set the stream position to the end\n"
				+ "    * `X01` will the new file pos or `-1` if something went wrong\n");
		defConstHelps.put("INT_STREAMS_REM", 
				  "* `20`: remove file\n"
				+ "    * `X00` contains a pointer of a STRING with the file\n"
				+ "    * if the file was successfully removed `X00` will contain `1`, if not `0`\n");
		defConstHelps.put("INT_STREAMS_MK_DIR", 
				  "* `21`: make dictionary\n"
				+ "    * `X00` contains a pointer of a STRING with the dictionary\n"
				+ "    * if the dictionary was successfully created `X00` will contain `1`, if not `0`\n");
		defConstHelps.put("INT_STREAMS_REM_DIR", 
				  "* `22`: remove dictionary\n"
				+ "    * `X00` contains a pointer of a STRING with the dictionary\n"
				+ "    * if the dictionary was successfully removed `X00` will contain `1`, if not `0`\n"
				+ "    * if the dictionary is not empty this call will fail (and set `X00` to `0`)\n");
		defConstHelps.put("INT_TIME_GET", 
				  "* `23`: to get the time in milliseconds\n"
				+ "    * `X00` will contain the time in milliseconds or `-1` if not available\n");
		defConstHelps.put("INT_TIME_WAIT", 
				  "* `24`: to wait the given time in nanoseconds\n"
				+ "    * `X00` contain the number of nanoseconds to wait (only values from `0` to `999999999` are allowed)\n"
				+ "    * `X01` contain the number of seconds to wait\n"
				+ "    * `X00` and `X01` will contain the remaining time (`0` if it finished waiting)\n"
				+ "    * `X02` will be `1` if the call was successfully and `0` if something went wrong\n"
				+ "        * if `X02` is `1` the remaining time will always be `0`\n"
				+ "        * if `X02` is `0` the remaining time will be greater `0`\n"
				+ "    * `X00` will not be negative if the progress waited too long\n");
		defConstHelps.put("INT_SOCKET_CLIENT_CREATE", 
				  "* `25`: socket client create\n"
				+ "    * makes a new client socket\n"
				+ "    * `X00` will be set to the SOCKET-ID or `-1` if the operation failed\n");
		defConstHelps.put("INT_SOCKET_CLIENT_CONNECT", 
				  "* `26`: socket client connect\n"
				+ "    * `X00` points to the SOCKET-ID\n"
				+ "    * `X01` points to a STRING, which names the host\n"
				+ "    * `X02` contains the port\n"
				+ "        * the port will be the normal number with the normal byte order\n"
				+ "    * connects an client socket to the host on the port\n"
				+ "    * `X01` will be set to the `1` on success and `0` on a fail\n"
				+ "    * on success, the SOCKET-ID, can be used as a STREAM-ID\n");
		defConstHelps.put("INT_SOCKET_SERVER_CREATE", 
				  "* `27`: socket server create\n"
				+ "    * `X00` contains the port\n"
				+ "        * the port will be the normal number with the normal byte order\n"
				+ "    * makes a new server socket\n"
				+ "    * `X00` will be set to the SOCKET-ID or `-1` when the operation fails\n");
		defConstHelps.put("INT_SOCKET_SERVER_LISTEN", 
				  "* `28`: socket server listens\n"
				+ "    * `X00` contains the SOCKET-ID\n"
				+ "    * `X01` contains the MAX_QUEUE length\n"
				+ "    * let a server socket listen\n"
				+ "    * `X01` will be set to `1` or `0` when the operation fails\n");
		defConstHelps.put("INT_SOCKET_SERVER_ACCEPT", 
				  "* `29`: socket server accept\n"
				+ "    * `X00` contains the SOCKET-ID\n"
				+ "    * let a server socket accept a client\n"
				+ "    * this operation will block, until a client connects\n"
				+ "    * `X01` will be set a new SOCKET-ID, which can be used as STREAM-ID, or `-1`\n");
		defConstHelps.put("INT_RANDOM", 
				  "* `30`: random\n"
				+ "    * `X00` will be filled with random bits\n");
		defConstHelps.put("INT_MEMORY_COPY", 
				  "* `31`: memory copy\n"
				+ "    * copies a block of memory\n"
				+ "    * this function has undefined behavior if the two blocks overlap\n"
				+ "    * `X00` points to the target memory block\n"
				+ "    * `X01` points to the source memory block\n"
				+ "    * `X02` has the length of bytes to bee copied\n");
		defConstHelps.put("INT_MEMORY_MOVE", 
				  "* `32`: memory move\n"
				+ "    * copies a block of memory\n"
				+ "    * this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)\n"
				+ "    * `X00` points to the target memory block\n"
				+ "    * `X01` points to the source memory block\n"
				+ "    * `X02` has the length of bytes to bee copied\n");
		defConstHelps.put("INT_MEMORY_BSET", 
				  "* `33`: memory byte set\n"
				+ "    * sets a memory block to the given byte-value\n"
				+ "    * `X00` points to the block\n"
				+ "    * `X01` the first byte contains the value to be written to each byte\n"
				+ "    * `X02` contains the length in bytes\n");
		defConstHelps.put("INT_MEMORY_SET", 
				  "* `34`: memory set\n"
				+ "    * sets a memory block to the given int64-value\n"
				+ "    * `X00` points to the block\n"
				+ "    * `X01` contains the value to be written to each element\n"
				+ "    * `X02` contains the count of elements to be set\n");
		defConstHelps.put("INT_STRING_LENGTH", 
				  "* `35`: string length\n"
				+ "    * `X00` points to the STRING\n"
				+ "    * `X00` will be set to the length of the string/ the (byte-)offset of the `'\\0'` character\n");
		defConstHelps.put("INT_STRING_TO_NUMBER", 
				  "* `36`: string to number\n"
				+ "    * `X00` points to the STRING\n"
				+ "    * `X01` points to the base of the number system\n"
				+ "        * (for example `10` for the decimal system or `2` for the binary system)\n"
				+ "    * `X00` will be set to the converted number\n"
				+ "    * `X01` will point to the end of the number-STRING\n"
				+ "        * this might be the `\\0'` terminating character\n"
				+ "    * if the STRING contains illegal characters or the base is not valid, the behavior is undefined\n"
				+ "    * this function will ignore leading space characters\n");
		defConstHelps.put("INT_STRING_TO_FPNUMBER", 
				  "* `37`: string to floating point number\n"
				+ "    * `X00` points to the STRING\n"
				+ "    * `X00` will be set to the converted number\n"
				+ "    * `X01` will point to the end of the number-STRING\n"
				+ "        * this might be the `\\0'` terminating character\n"
				+ "    * if the STRING contains illegal characters or the base is not valid, the behavior is undefined\n"
				+ "    * this function will ignore leading space characters\n");
		defConstHelps.put("INT_NUMBER_TO_STRING", 
				  "* `38`: number to string\n"
				+ "    * `X00` is set to the number to convert\n"
				+ "    * `X01` is points to the buffer to be filled with the number in a STRING format\n"
				+ "    * `X02` contains the base of the number system\n"
				+ "        * the minimum base is `2`\n"
				+ "        * the maximum base is `36`\n"
				+ "        * other values lead to undefined behavior\n"
				+ "    * `X00` will be set to the length of the STRING\n");
		defConstHelps.put("INT_FPNUMBER_TO_STRING", 
				  "* `39`: floating point number to string\n"
				+ "    * `X00` is set to the number to convert\n"
				+ "    * `X02` contains the maximum amount of digits to be used to represent the floating point number\n"
				+ "    * `X01` is points to the buffer to be filled with the number in a STRING format\n");
		defConstHelps.put("INT_STRING_FORMAT", 
				  "* `40`: format string\n"
				+ "    * `X00` is set to the STRING input\n"
				+ "    * `X01` contains the buffer for the STRING output\n"
				+ "        * if `X01` is set to `-1`, `X01` will be allocated to a memory block\n"
				+ "            * the allocated memory block will be exact large enough to contain the formatted STRING\n"
				+ "            * if there could not be allocated enough memory, `X01` will be set to `-1`\n"
				+ "    * `X00` will be set to the length of the output string\n"
				+ "    * the register `X02..XNN` are for the formatting parameters\n"
				+ "        * if there are mor parameters used then there are registers the behavior is undefined.\n"
				+ "            * that leads to a maximum of 249 parameters.\n"
				+ "    * formatting:\n"
				+ "        * everything, which can not be formatted, will be delegated to the target buffer\n"
				+ "        * `%s`: the next argument points to a STRING, which should be inserted here\n"
				+ "        * `%c`: the next argument points to a character, which should be inserted here\n"
				+ "            * note that characters may contain more than one byte\n"
				+ "                * `BIN-0.......` -> one byte (equivalent to an ASCII character)\n"
				+ "                * `BIN-10......` -> invalid, treated as one byte\n"
				+ "                * `BIN-110.....` -> two bytes\n"
				+ "                * `BIN-1110....` -> three bytes\n"
				+ "                * `BIN-11110...` -> four bytes\n"
				+ "                * `BIN-111110..` -> invalid, treated as five byte\n"
				+ "                * `BIN-1111110.` -> invalid, treated as six byte\n"
				+ "                * `BIN-11111110` -> invalid, treated as seven byte\n"
				+ "                * `BIN-11111111` -> invalid, treated as eight byte\n"
				+ "        * `%B`: the next argument points to a byte, which should be inserted here (without being converted to a STRING)\n"
				+ "        * `%d`: the next argument contains a number, which should be converted to a STRING using the decimal number system and than be inserted here\n"
				+ "        * `%f`: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here\n"
				+ "        * `%p`: the next argument contains a pointer, which should be converted to a STRING\n"
				+ "            * if not the pointer will be converted by placing a `\"p-\"` and then the pointer-number converted to a STRING using the hexadecimal number system\n"
				+ "            * if the pointer is `-1` it will be converted to the STRING `\"---\"`\n"
				+ "        * `%h`: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here\n"
				+ "        * `%b`: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here\n"
				+ "        * `%o`: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here\n");
		defConstHelps.put("INT_LOAD_FILE",
				  "* `41`: load file\n"
				+ "    * `X00` is set to the path (inclusive name) of the file\n"
				+ "    * `X00` will point to the memory block, in which the file has been loaded\n"
				+ "    * `X01` will be set to the length of the file (and the memory block)\n"
				+ "    * when an error occured `X00` will be set to `-1`\n");
		defConstHelps.put("INTERRUPT_COUNT", "the number of interrupts activated and implemented by default");
		defConstHelps.put("MAX_VALUE", "the maximal value (2^63-1)");
		defConstHelps.put("MIN_VALUE", "the minimal value (-2^63)");
		defConstHelps.put("STD_IN", "the std-in STREAM-ID");
		defConstHelps.put("STD_OUT", "the std-out STREAM-ID");
		defConstHelps.put("STD_LOG", "the std-log STREAM-ID");
		defConstHelps.put("FP_NAN", "a NaN constant");
		defConstHelps.put("FP_MAX_VALUE", "the maximal floating point number");
		defConstHelps.put("FP_MIN_VALUE", "the minimal floating point number");
		defConstHelps.put("FP_POS_INFINITY", "a floating point containing a positiv infinity value");
		defConstHelps.put("FP_NEG_INFINITY", "a floating point containing a negativ infinity value");
		DEFAULT_CONSTANT_HELPS = Collections.unmodifiableMap(defConstHelps);
	} //@formatter:on

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		int off = hoverRegion.getOffset();
		IDocument document = textViewer.getDocument();
		DocumentValue docval = getDocVal(document);
		TokenInfo inf;
		try {
			inf = getTokenInfo(docval, off);
		} catch (NoSuchElementException nsee) {
			return "";
		}
		if (inf.cpac != null) {
			if (inf.tn.getSymbol().getType() != ConstantPoolGrammarLexer.NAME) {
				return "";
			}
			String text = inf.tn.getText();
			Long val_ = inf.ac.constants.get(text);
			if (val_ == null) {
				return text + " : <unknown constant>";
			}
			long val = val_;
			if (val >= 0) {
				return text + " : " + val + " [HEX-" + Long.toHexString(val) + "]";
			} else {
				return text + " : " + val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
			}
		}
		switch (inf.tn.getSymbol().getType()) {
		case PrimitiveFileGrammarLexer.CONSTANT:
		case PrimitiveFileGrammarLexer.LABEL_DECLARATION:
		case PrimitiveFileGrammarLexer.NAME: {
			String name = inf.tn.getText().replaceFirst("[#@]?([a-zA-Z_]+)", "$1");
			if (inf.ac.constants.containsKey(name)) {
				long val = inf.ac.constants.get(name);
				return constantToString(inf.ac.constants, name, val);
			} else {
				Long val = inf.ac.labels.get(name);
				if (val == null) {
					return name + " : unknown";
				}
				return "@" + name + " : " + (val == null ? "unknown" : "p-" + Long.toHexString(val));
			}
		}
		case PrimitiveFileGrammarLexer.BIN_NUM:
		case PrimitiveFileGrammarLexer.DEC_FP_NUM:
		case PrimitiveFileGrammarLexer.DEC_NUM:
		case PrimitiveFileGrammarLexer.DEC_NUM0:
		case PrimitiveFileGrammarLexer.HEX_NUM:
		case PrimitiveFileGrammarLexer.OCT_NUM:
		case PrimitiveFileGrammarLexer.NEG_DEC_NUM0:
		case PrimitiveFileGrammarLexer.NEG_BIN_NUM:
		case PrimitiveFileGrammarLexer.NEG_HEX_NUM:
		case PrimitiveFileGrammarLexer.NEG_OCT_NUM: {
			NummerNoConstantContext nncc = (NummerNoConstantContext) inf.tn.getParent();
			return constantToString(inf.ac.constants, null, nncc.num);
		}
		case PrimitiveFileGrammarLexer.ERROR:
		case PrimitiveFileGrammarLexer.ERROR_HEX:
		case PrimitiveFileGrammarLexer.ERROR_MESSAGE_START:
		case PrimitiveFileGrammarLexer.ERROR_MESSAGE_END:
			return "error message: " + (String) inf.ac.zusatz;
		case PrimitiveFileGrammarLexer.PLUS:
			if (!(inf.tn.getParent() instanceof ConstBerechnungStrichContext)) {
				if (docval.currentDebugSession == null) {
					return "";
				}
				ParamContext pc = (ParamContext) inf.tn.getParent();
				String value;
				try {
					switch (pc.p.art) {
					case Param.ART_ANUM_BNUM: {
						PVMSnapshot sn = docval.currentDebugSession.getSnapshot();
						byte[] bytes = new byte[24];
						docval.currentDebugSession.getMem(sn.ip, bytes, 0, 16);
						docval.currentDebugSession.getMem(Convert.convertByteArrToLong(bytes, 8) + Convert.convertByteArrToLong(bytes, 16), bytes, 0, 8);
						long val = Convert.convertByteArrToLong(bytes);
						if (val >= 0) {
							value = val + " [HEX-" + Long.toHexString(val) + "]";
						} else {
							value = val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
						}
						break;
					}
					case Param.ART_ASR_BNUM:
					case Param.ART_ANUM_BSR: {
						PVMSnapshot sn = docval.currentDebugSession.getSnapshot();
						byte[] bytes = new byte[16];
						docval.currentDebugSession.getMem(sn.ip, bytes, 0, 16);
						docval.currentDebugSession.getMem(Convert.convertByteArrToLong(bytes, 8) + sn.getRegister(bytes[7]), bytes, 0, 8);
						long val = Convert.convertByteArrToLong(bytes);
						if (val >= 0) {
							value = val + " [HEX-" + Long.toHexString(val) + "]";
						} else {
							value = val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
						}
						break;
					}
					case Param.ART_ASR_BSR: {
						PVMSnapshot sn = docval.currentDebugSession.getSnapshot();
						byte[] bytes = new byte[8];
						docval.currentDebugSession.getMem(sn.ip, bytes, 0, 8);
						docval.currentDebugSession.getMem(sn.getRegister(bytes[7]) + sn.getRegister(bytes[6]), bytes, 0, 8);
						long val = Convert.convertByteArrToLong(bytes);
						if (val >= 0) {
							value = val + " [HEX-" + Long.toHexString(val) + "]";
						} else {
							value = val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
						}
						break;
					}
					case Param.ART_ANUM_BREG:// no PLUS Token
					case Param.ART_ASR_BREG:
					case Param.ART_ANUM:
					case Param.ART_ASR:
					default:
						throw new InternalError("why?! art=" + Param.artToString(pc.p.art));
					}
					getTextInterval((RuleNode) inf.tn.getParent());
					return inf.tn.getParent().getText() + " : " + value;
				} catch (IOException e) {
					return "IOException while comunicating with the debugger: " + e.getMessage();
				}
			}
		case PrimitiveFileGrammarLexer.MINUS:
		case PrimitiveFileGrammarLexer.KOMMA_MINUS:
		case PrimitiveFileGrammarLexer.KOMMA_PLUS: {
			ConstBerechnungStrichContext cbsc = (ConstBerechnungStrichContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.GLEICH_GLEICH:
		case PrimitiveFileGrammarLexer.UNGLEICH: {
			ConstBerechnungGleichheitContext cbsc = (ConstBerechnungGleichheitContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.GROESSER:
		case PrimitiveFileGrammarLexer.GROESSER_GLEICH:
		case PrimitiveFileGrammarLexer.KLEINER_GLEICH:
		case PrimitiveFileGrammarLexer.KLEINER: {
			ConstBerechnungRelativeTestsContext cbsc = (ConstBerechnungRelativeTestsContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.MAL:
		case PrimitiveFileGrammarLexer.GETEILT:
		case PrimitiveFileGrammarLexer.KOMMA_MAL:
		case PrimitiveFileGrammarLexer.KOMMA_GETEILT:
		case PrimitiveFileGrammarLexer.MODULO: {
			ConstBerechnungPunktContext cbsc = (ConstBerechnungPunktContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.FRAGEZEICHEN:
		case PrimitiveFileGrammarLexer.DOPPELPUNKT: {
			ConstBerechnungDirektContext cbsc = (ConstBerechnungDirektContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.LINKS_SCHUB:
		case PrimitiveFileGrammarLexer.ARITMETISCHER_RECHTS_SCHUB:
		case PrimitiveFileGrammarLexer.LOGISCHER_RECHTS_SCHUB: {
			ConstBerechnungSchubContext cbsc = (ConstBerechnungSchubContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.EXIST_CONSTANT:
		case PrimitiveFileGrammarLexer.RND_KL_AUF:
		case PrimitiveFileGrammarLexer.RND_KL_ZU: {
			ConstBerechnungDirektContext cbsc = (ConstBerechnungDirektContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.XNN:
		case PrimitiveFileGrammarLexer.INTCNT:
		case PrimitiveFileGrammarLexer.INTP:
		case PrimitiveFileGrammarLexer.STATUS:
		case PrimitiveFileGrammarLexer.SP:
		case PrimitiveFileGrammarLexer.IP:
			if (docval.currentDebugSession == null) {
				return "";
			}
			try {
				PVMSnapshot snapshot = docval.currentDebugSession.getSnapshot();
				SrContext sr = (SrContext) inf.tn.getParent();
				return Param.toSRString(sr.srnum) + " : " + snapshot.getRegister(sr.srnum);
			} catch (IOException e) {
				return "";
			}
		case PrimitiveFileGrammarLexer.CONSTANT_POOL:
			assert false;
		default:
			return "";
		}
	}

	private String constantToString(Map<String, Long> constants, String name, long val) {
		StringBuilder build = new StringBuilder();
		if (name != null) {
			build.append('#').append(name).append(" : ");
		}
		build.append(val);
		if (val >= 0) {
			build.append(" [HEX-").append(Long.toHexString(val));
		} else {
			build.append(" [NHEX" + Long.toString(val, 16)).append(" = UHEX-").append(Long.toUnsignedString(val, 16));
		}
		build.append(" FP: ").append(Double.longBitsToDouble(val)).append(']');
		if (name != null) {
			if (Objects.equals(constants.get(name), PrimitiveAssembler.START_CONSTANTS.get(name))) {
				build.append('\n').append(DEFAULT_CONSTANT_HELPS.get(name));
			}
		}
		return build.toString();
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}
}