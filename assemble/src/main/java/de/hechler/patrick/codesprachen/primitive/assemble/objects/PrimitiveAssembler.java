package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.START_CONSTANTS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;

public class PrimitiveAssembler {
	
	private final OutputStream out;
	private final PrintStream  exportOut;
	private final boolean      supressWarn;
	private final boolean      defaultAlign;
	private final Path[]       lookups;
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path[] lookups, boolean supressWarnings,
		boolean defaultAlign) {
		this.out = out;
		this.exportOut = exportOut;
		this.supressWarn = supressWarnings;
		this.defaultAlign = defaultAlign;
		this.lookups = lookups;
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
		return preassemble(path, in, new LinkedHashMap <>(START_CONSTANTS));
	}
	
	public ParseContext preassemble(Path path, InputStream in, Map <String, PrimitiveConstant> predefinedConstants)
		throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, InputStream in, Charset cs,
		Map <String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in, cs), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, Reader in, Map <String, PrimitiveConstant> predefinedConstants)
		throws IOException, AssembleError {
		return preassemble(path, new ANTLRInputStream(in), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin) throws IOException, AssembleError {
		return preassemble(path, antlrin, new LinkedHashMap <>(START_CONSTANTS));
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin,
		Map <String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, antlrin, new LinkedHashMap <>(predefinedConstants), true);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin,
		Map <String, PrimitiveConstant> predefinedConstants, boolean bailError) throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, bailError ? new BailErrorStrategy() : null, bailError);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin,
		Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError)
		throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, null);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin,
		Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
		ANTLRErrorListener errorListener) throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener,
			(line, charPos) -> {});
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin,
		Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
		ANTLRErrorListener errorListener, BiConsumer <Integer, Integer> enterConstPool)
		throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool,
			"[THIS]");
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin,
		Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
		ANTLRErrorListener errorListener, BiConsumer <Integer, Integer> enterConstPool, String thisFile)
		throws IOException, AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool,
			thisFile, new LinkedHashMap <>());
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin,
		Map <String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError,
		ANTLRErrorListener errorListener, BiConsumer <Integer, Integer> enterConstPool, String thisFile,
		Map <String, List <Map <String, Long>>> readFiles) throws IOException, AssembleError {
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
			return parser.parse(path, 0L, defaultAlign, predefinedConstants, bailError, errorHandler, errorListener,
				enterConstPool, this, antlrin, thisFile, readFiles);
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
		throw new Error("unknwon error: " + t, t);
	}
	
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
		StringBuilder build = new StringBuilder("error: ").append(t).append("at line ").append(ot.getLine()).append(':')
			.append(ot.getCharPositionInLine()).append(" token.text='").append(ot.getText());
		build.append("' token.id=").append(tokenToString(ot.getType(), PrimitiveFileGrammarLexer.ruleNames))
			.append('\n').append("expected: ");
		for (int i = 0; i < ets.size(); i ++ ) {
			if (i > 0) {
				build.append(", ");
			}
			build.append(' ').append(tokenToString(ets.get(i), PrimitiveFileGrammarLexer.ruleNames));
		}
		throw new AssembleError(ot.getLine(), ot.getCharPositionInLine(), ot.getStopIndex() - ot.getStartIndex() + 1,
			ot.getStartIndex(), build.toString(), t);
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
	
	private void handle(AssembleRuntimeException ae) {
		handle(ae.line, ae.posInLine, ae.charPos, ae.length, ae);
	}
	
	private void handle(AssembleError ae) {
		handle(ae.line, ae.posInLine, ae.charPos, ae.length, ae);
	}
	
	private void handle(int line, int posInLine, int len, int charPos, Throwable t) {
		// StringBuilder build = new StringBuilder();
		// build.append("an error occured at line:
		// ").append(line).append(':').append(posInLine).append("
		// length=").append(len).append('\n');
		// build.append(msg).append('\n');
		// build.append("stack:").append('\n');
		// for (StackTraceElement ste : stack) {
		// build.append(" at ").append(ste).append('\n');
		// }
		throw new AssembleError(line, posInLine, len, charPos, t.getClass().getName() + ": " + t.getMessage(), t);
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
	
	public void assemble(Path path, ANTLRInputStream antlrin, Map <String, PrimitiveConstant> predefinedConstants)
		throws IOException, AssembleError {
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
			out.print(symbol + '=' + Long.toUnsignedString(pc.value, 16).toUpperCase() + '\n');
		});
	}
	
	public static void readSymbols(String prefix, Map <String, PrimitiveConstant> addSymbols, Scanner sc, Path path) {
		StringBuilder comment = new StringBuilder();
		int lineNumber = 1;
		final String regex = "^[#]?([a-zA-Z_0-9]+)\\s*[=]\\s*([0-9a-fA-F]+)$";
		Pattern pattern = Pattern.compile(regex);
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.charAt(0) == '|') {
				comment.append(line);
				continue;
			}
			Matcher matcher = pattern.matcher(line);
			if ( !matcher.matches()) {
				throw new RuntimeException("line does not match regex: line='" + line + "', regex='" + regex + "'");
			}
			String constName = matcher.replaceFirst("$1");
			long val = Long.parseUnsignedLong(matcher.replaceFirst("$2"), 16);
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
	
	public AssembleRuntimeException readSymbols(String readFile, Boolean isSource, String prefix,
		Map <String, PrimitiveConstant> startConsts, Map <String, PrimitiveConstant> addSymbols,
		ANTLRInputStream antlrin, boolean be, Token tok, String thisFile,
		Map <String, List <Map <String, Long>>> readFiles)
		throws IllegalArgumentException, IOException, RuntimeException {
		readFiles = new LinkedHashMap <>(readFiles);
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
			PrimitiveFileTypes type = PrimitiveFileTypes.getTypeFromName(readFile,
				PrimitiveFileTypes.primitiveMashineCode);
			switch (type) {
			case primitiveSourceCode:
				isPrimSourceCode = true;
				break;
			case primitiveSymbolFile:
				isPrimSourceCode = false;
				break;
			default:
				throw new IllegalArgumentException(
					"Source/Symbol not set, but readFile is not *.psc and not *.psf! readFile='" + readFile + "'");
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
								throw new AssembleError(tok.getLine(), tok.getCharPositionInLine(),
									tok.getStopIndex() - tok.getStartIndex() + 1, tok.getStartIndex(),
									"loop detected! started again with the same start consts: in file='" + thisFile
										+ "' read symbols of file='" + rf + "' constants: " + startConsts);
							} else {
								throw new AssembleRuntimeException(tok.getLine(), tok.getCharPositionInLine(),
									tok.getStopIndex() - tok.getStartIndex() + 1, tok.getStartIndex(),
									"loop detected! started again with the same start consts: in file='" + thisFile
										+ "' read symbols of file='" + rf + "' constants: " + startConsts);
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
				ParseContext pc = preassemble(path,
					new ANTLRInputStream(new InputStreamReader(in, StandardCharsets.UTF_8)), startConsts,
					be ? new BailErrorStrategy() : null, be, null, (line, charPos) -> {}, readFile, readFiles);
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
		Map <String, Long> nv = new LinkedHashMap <>();
		startConsts.forEach((n, pc) -> nv.put(n, pc.value));
		return nv;
	}
	
	public void assemble(List <Command> cmds, Map <String, Long> labels) throws IOException {
		long pos = 0;
		boolean alignMode = defaultAlign;
		boolean alignable = false;
		for (Command cmd : cmds) {
			while (true) {
				pos = align(pos, alignable && alignMode);
				if (cmd.getClass() == Command.class) {
					alignable = false;
					assmCommand(labels, cmd);
				} else if (cmd instanceof ConstantPoolCommand) {
					alignable = true;
					ConstantPoolCommand cpc = (ConstantPoolCommand) cmd;
					cpc.write(out);
				} else if (cmd instanceof CompilerCommandCommand) {
					CompilerCommandCommand ccc = (CompilerCommandCommand) cmd;
					switch (ccc.directive) {
					case align:
						alignMode = true;
						break;
					case notAlign:
						alignMode = false;
						break;
					case setPos:
						pos = ccc.value;
						break;
					case assertPos:
						if (pos != ccc.value) {
							throw new AssertionError("not at the assertet position!");
						}
						break;
					default:
						throw new InternalError("unknown directive: " + ccc.directive.name());
					}
				} else {
					cmd = replaceUnknownCommand(cmd);
					continue;
				}
				pos += cmd.length();
				break;
			}
		}
		out.flush();
	}
	
	protected Command replaceUnknownCommand(Command cmd) throws InternalError {
		throw new InternalError("unknown command class: " + cmd.getClass().getName());
	}
	
	private long align(long pos, boolean doAlign) throws IOException {
		if ( !doAlign) return pos;
		int mod = (int) (pos % 8);
		if (mod != 0) {
			int add = 8 - mod;
			byte[] bytes = new byte[add];
			out.write(bytes, 0, bytes.length);
			pos += add;
		}
		return pos;
	}
	
	private void assmCommand(Map <String, Long> labels, Command cmd) throws IOException, InternalError {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) cmd.cmd.num;
		switch (cmd.cmd.params) {
		case 3:
			if (cmd.p3.art != Param.ART_ANUM) {
				throw new IllegalStateException("offset must be a constant! (cmd: CALO)");
			}
			writeTwoParam(cmd, bytes);
			convertLong(bytes, cmd.p3.num);
			out.write(bytes, 0, bytes.length);
			break;
		case 2:
			nullCheck(cmd.p3, cmd);
			switch (cmd.cmd.nokonstParams) {
			case 2:
				noConstCheck(cmd.p2, cmd);
			case 1:
				noConstCheck(cmd.p1, cmd);
			case 0:
				writeTwoParam(cmd, bytes);
				break;
			case -1:
				if (cmd.p2.art != Param.ART_ANUM) {
					throw new IllegalStateException("offset must be a constant! (cmd: CALO)");
				}
				writeOneParam(cmd, bytes);
				convertLong(bytes, cmd.p2.num);
				out.write(bytes, 0, bytes.length);
				break;
			default:
				throw new InternalError("unknown nokonst param count: " + cmd.cmd.name());
			}
			break;
		case 1:
			nullCheck(cmd.p3, cmd);
			nullCheck(cmd.p2, cmd);
			switch (cmd.cmd.nokonstParams) {
			case 1:
				noConstCheck(cmd.p1, cmd);
			case 0:
				writeOneParam(cmd, bytes);
				break;
			case -1:
				if (cmd.p1.label == null && cmd.p1.art != Param.ART_ANUM) {
					throw new IllegalStateException("offset must be a constant! (cmd: CALO)");
				}
				long num;
				if (cmd.p1.label != null) {
					Long numobj = labels.get(cmd.p1.label);
					if (numobj == null) {
						throw new NullPointerException(
							"label not found! label: '" + cmd.p1.label + "' known labels: '" + labels + "'");
					}
					num = numobj;
				} else {
					num = cmd.p1.num;
				}
				convertLong(bytes, num);
				out.write(bytes, 0, bytes.length);
				break;
			default:
				throw new InternalError("unknown nokonst param count: " + cmd.cmd.name());
			}
			break;
		case 0:
			nullCheck(cmd.p3, cmd);
			nullCheck(cmd.p2, cmd);
			nullCheck(cmd.p1, cmd);
			break;
		default:
			throw new InternalError("unknown command param count: " + cmd.cmd.name());
		}
	}
	
	private void nullCheck(Param nullParam, Command cmd) {
		if (nullParam != null) {
			throw new IllegalStateException("this param should be null: '" + nullParam + "' cmd: '" + cmd + '\'');
		}
	}
	
	private void noConstCheck(Param param, Command cmd) {
		if (param.art == Param.ART_ANUM) {
			throw new IllegalStateException("no constants allowed: '" + cmd + '\'');
		}
	}
	
	private void writeOneParam(Command cmd, byte[] bytes) throws IOException {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p1.label == null : "I don't need a label in my params!";
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
				System.err.println(
					"[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
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
					System.err.println(
						"[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
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
					System.err.println(
						"[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
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
				throw new InternalError("unknown art: " + p2art + " cmd: " + cmd);
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
					System.err.println(
						"[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
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
					System.err.println(
						"[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
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
