package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

	public static final Map<String, Long> START_CONSTANTS;

	private static final byte[] INERPRETER_START = "#!/bin/pvm        --pmc\n".getBytes(StandardCharsets.US_ASCII);

	static {
		Map<String, Long> startConstants = new LinkedHashMap<>();
		startConstants.put("INT_ERRORS_ILLEGAL_INTERRUPT", (Long) 0L);
		startConstants.put("INT_ERRORS_UNKNOWN_COMMAND", (Long) 1L);
		startConstants.put("INT_ERRORS_ILLEGAL_MEMORY", (Long) 2L);
		startConstants.put("INT_ERRORS_ARITHMETIC_ERROR", (Long) 3L);
		startConstants.put("INT_EXIT", (Long) 4L);
		startConstants.put("INT_MEMORY_ALLOC", (Long) 5L);
		startConstants.put("INT_MEMORY_REALLOC", (Long) 6L);
		startConstants.put("INT_MEMORY_FREE", (Long) 7L);
		startConstants.put("INT_STREAMS_NEW_IN", (Long) 8L);
		startConstants.put("INT_STREAMS_NEW_OUT", (Long) 9L);
		startConstants.put("INT_STREAMS_NEW_APPEND", (Long) 10L);
		startConstants.put("INT_STREAMS_NEW_IN_OUT", (Long) 11L);
		startConstants.put("INT_STREAMS_NEW_APPEND_IN_OUT", (Long) 12L);
		startConstants.put("INT_STREAMS_WRITE", (Long) 13L);
		startConstants.put("INT_STREAMS_READ", (Long) 14L);
		startConstants.put("INT_STREAMS_SYNC_STREAM", (Long) 15L);
		startConstants.put("INT_STREAMS_CLOSE_STREAM", (Long) 16L);
		startConstants.put("INT_STREAMS_GET_POS", (Long) 17L);
		startConstants.put("INT_STREAMS_SET_POS", (Long) 18L);
		startConstants.put("INT_STREAMS_SET_POS_TO_END", (Long) 19L);
		startConstants.put("INT_STREAMS_REM", (Long) 20L);
		startConstants.put("INT_STREAMS_MK_DIR", (Long) 21L);
		startConstants.put("INT_STREAMS_REM_DIR", (Long) 22L);
		startConstants.put("INT_TIME_GET", (Long) 23L);
		startConstants.put("INT_TIME_WAIT", (Long) 24L);
		startConstants.put("INT_SOCKET_CLIENT_CREATE", (Long) 25L);
		startConstants.put("INT_SOCKET_CLIENT_CONNECT", (Long) 26L);
		startConstants.put("INT_SOCKET_SERVER_CREATE", (Long) 27L);
		startConstants.put("INT_SOCKET_SERVER_LISTEN", (Long) 28L);
		startConstants.put("INT_SOCKET_SERVER_ACCEPT", (Long) 29L);
		startConstants.put("INT_RANDOM", (Long) 30L);
		startConstants.put("INT_MEMORY_COPY", (Long) 31L);
		startConstants.put("INT_MEMORY_MOVE", (Long) 32L);
		startConstants.put("INT_MEMORY_BSET", (Long) 33L);
		startConstants.put("INT_MEMORY_SET", (Long) 34L);
		startConstants.put("INT_STRING_LENGTH", (Long) 35L);
		startConstants.put("INT_NUMBER_TO_STRING", (Long) 36L);
		startConstants.put("INT_FPNUMBER_TO_STRING", (Long) 37L);
		startConstants.put("INT_STRING_TO_NUMBER", (Long) 38L);
		startConstants.put("INT_STRING_TO_FPNUMBER", (Long) 39L);
		startConstants.put("INT_STRING_FORMAT", (Long) 40L);
		startConstants.put("INT_LOAD_FILE", (Long) 41L);
		startConstants.put("INTERRUPT_COUNT", (Long) 42L);
		startConstants.put("MAX_VALUE", (Long) 0x7FFFFFFFFFFFFFFFL);
		startConstants.put("MIN_VALUE", (Long) (-0x8000000000000000L));
		startConstants.put("STD_IN", (Long) 0L);
		startConstants.put("STD_OUT", (Long) 1L);
		startConstants.put("STD_LOG", (Long) 2L);
		startConstants.put("FP_NAN", (Long) 0x7FFE000000000000L);
		startConstants.put("FP_MAX_VALUE", (Long) 0x7FEFFFFFFFFFFFFFL);
		startConstants.put("FP_MIN_VALUE", (Long) 0x0000000000000001L);
		startConstants.put("FP_POS_INFINITY", (Long) 0x7FF0000000000000L);
		startConstants.put("FP_NEG_INFINITY", (Long) 0xFFF0000000000000L);
		START_CONSTANTS = Collections.unmodifiableMap(startConstants);
	}

	private final OutputStream out;
	private final PrintStream exportOut;
	private final boolean supressWarn;
	private final boolean defaultAlign;
	private final boolean exitOnError;
	private final boolean interpreterStart;
	private final File lookup;

	public PrimitiveAssembler(OutputStream out) {
		this(out, false);
	}

	public PrimitiveAssembler(OutputStream out, boolean supressWarnings) {
		this(out, supressWarnings, true);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, File lookup, boolean supressWarnings) {
		this(out, exportOut, lookup, supressWarnings, true);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, boolean supressWarnings) {
		this(out, exportOut, supressWarnings, true);
	}

	public PrimitiveAssembler(OutputStream out, boolean supressWarnings, boolean defaultAlign) {
		this(out, supressWarnings, defaultAlign, true);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, File lookup, boolean supressWarnings, boolean defaultAlign) {
		this(out, exportOut, lookup, supressWarnings, defaultAlign, true);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, boolean supressWarnings, boolean defaultAlign) {
		this(out, exportOut, supressWarnings, defaultAlign, true);
	}

	public PrimitiveAssembler(OutputStream out, boolean supressWarnings, boolean defaultAlign, boolean exitOnError) {
		this(out, supressWarnings, defaultAlign, exitOnError, true);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, File lookup, boolean supressWarnings, boolean defaultAlign, boolean exitOnError) {
		this(out, exportOut, lookup, supressWarnings, defaultAlign, exitOnError, true);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, boolean supressWarnings, boolean defaultAlign, boolean exitOnError) {
		this(out, exportOut, supressWarnings, defaultAlign, exitOnError, true);
	}

	public PrimitiveAssembler(OutputStream out, boolean supressWarnings, boolean defaultAlign, boolean exitOnError, boolean interpreterStart) {
		this(out, null, supressWarnings, defaultAlign, exitOnError, interpreterStart);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, boolean supressWarnings, boolean defaultAlign, boolean exitOnError, boolean interpreterStart) {
		this(out, exportOut, new File("./"), supressWarnings, defaultAlign, exitOnError, interpreterStart);
	}

	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, File lookup, boolean supressWarnings, boolean defaultAlign, boolean exitOnError, boolean interpreterStart) {
		this.out = out;
		this.exportOut = exportOut;
		this.supressWarn = supressWarnings;
		this.defaultAlign = defaultAlign;
		this.exitOnError = exitOnError;
		this.interpreterStart = interpreterStart;
		this.lookup = lookup == null ? new File("./") : lookup;
	}

	public ParseContext preassemble(InputStream in) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in));
	}

	public ParseContext preassemble(InputStream in, Charset cs) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in, cs));
	}

	public ParseContext preassemble(Reader in) throws IOException, AssembleError {
		return preassemble(in, new HashMap<>(START_CONSTANTS));
	}

	public ParseContext preassemble(InputStream in, Map<String, Long> predefinedConstants) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in), predefinedConstants);
	}

	public ParseContext preassemble(InputStream in, Charset cs, Map<String, Long> predefinedConstants) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in, cs), predefinedConstants);
	}

	public ParseContext preassemble(Reader in, Map<String, Long> predefinedConstants) throws IOException, AssembleError {
		return preassemble(new ANTLRInputStream(in), predefinedConstants);
	}

	public ParseContext preassemble(ANTLRInputStream antlrin) throws IOException, AssembleError {
		return preassemble(antlrin, new HashMap<>(START_CONSTANTS));
	}

	public ParseContext preassemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants) throws IOException, AssembleError {
		return preassemble(antlrin, new HashMap<>(predefinedConstants), true);
	}

	public ParseContext preassemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants, boolean bailError) throws IOException, AssembleError {
		return preassemble(antlrin, predefinedConstants, bailError ? new BailErrorStrategy() : null, bailError);
	}

	public ParseContext preassemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError) throws IOException, AssembleError {
		return preassemble(antlrin, predefinedConstants, errorHandler, bailError, null);
	}

	public ParseContext preassemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError, ANTLRErrorListener errorListener)
			throws IOException, AssembleError {
		return preassemble(antlrin, predefinedConstants, errorHandler, bailError, errorListener, (line, charPos) -> {
		});
	}

	public ParseContext preassemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError, ANTLRErrorListener errorListener,
			BiConsumer<Integer, Integer> enterConstPool) throws IOException, AssembleError {
		return preassemble(antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, "[THIS]");
	}

	public ParseContext preassemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError, ANTLRErrorListener errorListener,
			BiConsumer<Integer, Integer> enterConstPool, String thisFile) throws IOException, AssembleError {
		return preassemble(antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, thisFile, new HashMap<>());
	}

	public ParseContext preassemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants, ANTLRErrorStrategy errorHandler, boolean bailError, ANTLRErrorListener errorListener,
			BiConsumer<Integer, Integer> enterConstPool, String thisFile, Map<String, List<Map<String, Long>>> readFiles) throws IOException, AssembleError {
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
			return parser.parse(0L, defaultAlign, predefinedConstants, bailError, errorHandler, errorListener, enterConstPool, this, antlrin, thisFile, readFiles);
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
		if (exitOnError) {
			System.err.println("unknown error:");
			fullPrint(t, "", "  ");
			System.exit(1);
		} else {
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw new InternalError("unknwon error: " + t, t);
		}
	}

	private void fullPrint(Throwable t, String ident, String identAdd) {
		String nextIdent = ident + identAdd;
		System.err.println(t.getClass().getName());
		System.err.println(ident + "message: " + t.getMessage());
		if (t.getMessage() != t.getLocalizedMessage()) {
			System.err.println(ident + "localized-message: " + t.getMessage());
		}
		System.err.println(ident + "stack-tract:");
		for (StackTraceElement ste : t.getStackTrace()) {
			System.err.println(nextIdent + ste);
		}
		for (Throwable s : t.getSuppressed()) {
			System.err.print(ident + "suppressed: ");
			fullPrint(s, nextIdent, identAdd);
		}
		Throwable cause = t.getCause();
		if (cause != null) {
			System.err.print(ident + "cause: ");
			fullPrint(cause, nextIdent, identAdd);
		}
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
		if (exitOnError) {
			System.err.println("error: " + t);
			System.err.println("at line: " + ot.getLine() + ':' + ot.getCharPositionInLine());
			System.err.println("illegal input: " + ot.getText());
			System.err.println("  token: " + tokenToString(ot.getType(), PrimitiveFileGrammarLexer.ruleNames));
			System.err.println("expected: ");
			for (int i = 0; i < ets.size(); i++) {
				System.err.println("  " + tokenToString(ets.get(i), PrimitiveFileGrammarLexer.ruleNames));
			}
			System.err.flush();
			System.exit(1);
		} else {
			StringBuilder build = new StringBuilder("error: ").append(t).append("at line ").append(ot.getLine()).append(':').append(ot.getCharPositionInLine()).append(" token.text='")
					.append(ot.getText());
			build.append("' token.id=").append(tokenToString(ot.getType(), PrimitiveFileGrammarLexer.ruleNames)).append('\n').append("expected: ");
			for (int i = 0; i < ets.size(); i++) {
				if (i > 0) {
					build.append(", ");
				}
				build.append(' ').append(tokenToString(ets.get(i), PrimitiveFileGrammarLexer.ruleNames));
			}
			throw new AssembleError(ot.getLine(), ot.getCharPositionInLine(), ot.getStopIndex() - ot.getStartIndex() + 1, ot.getStartIndex(), build.toString());
		}
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
		if (exitOnError) {
			System.err.print(build);
			System.err.flush();
			System.exit(1);
		} else {
			throw new AssembleError(line, posInLine, len, charPos, build.toString());
		}
	}

	public void assemble(InputStream in) throws IOException, AssembleError {
		assemble(preassemble(in));
	}

	public void assemble(InputStream in, Charset cs) throws IOException, AssembleError {
		assemble(preassemble(in, cs));
	}

	public void assemble(Reader in) throws IOException, AssembleError {
		assemble(preassemble(in));
	}

	public void assemble(ANTLRInputStream antlrin) throws IOException, AssembleError {
		assemble(preassemble(antlrin));
	}

	public void assemble(ANTLRInputStream antlrin, Map<String, Long> predefinedConstants) throws IOException, AssembleError {
		assemble(preassemble(antlrin, predefinedConstants));
	}

	public void assemble(PrimitiveFileGrammarParser.ParseContext parsed) throws IOException {
		assemble(parsed.commands, parsed.labels);
		export(parsed.exports);
	}

	public void export(Map<String, Long> exports) {
		if (this.exportOut == null) {
			return;
		}
		export(exports, this.exportOut);
	}

	public static void export(Map<String, Long> exports, PrintStream out) {
		exports.forEach((symbol, value) -> {
			out.print(symbol + '=' + Long.toHexString(value).toUpperCase() + '\n');
		});
	}

	public AssembleRuntimeException readSymbols(String readFile, Boolean isSource, String prefix, Map<String, Long> startConsts, Map<String, Long> addSymbols, ANTLRInputStream antlrin, boolean be,
			Token tok, String thisFile, Map<String, List<Map<String, Long>>> readFiles) throws IllegalArgumentException, IOException, RuntimeException {
		readFiles = new HashMap<>(readFiles);
		byte[] bytes = null;
		if (readFile.equals("[THIS]")) {
			assert isSource == null || isSource;
			isSource = true;
			bytes = antlrin.getText(new Interval(0, Integer.MAX_VALUE)).getBytes(StandardCharsets.UTF_8);
			readFile = thisFile;
		}
		boolean iss;
		if (isSource != null) {
			iss = isSource;
		} else {
			PrimitiveFileTypes type = PrimitiveFileTypes.getTypeFromName(readFile, PrimitiveFileTypes.primitiveMashineCode);
			switch (type) {
			case primitiveSourceCode:
				iss = true;
				break;
			case primitiveSymbolFile:
				iss = false;
				break;
			default:
				throw new IllegalArgumentException("Source/Symbol not set, but readFile is not *.psc and not *.psf! readFile='" + readFile + "'");
			}
		}
		try {
			final String rf = readFile;
			readFiles.compute(readFile, (String key, List<Map<String, Long>> oldValue) -> {
				List<Map<String, Long>> newValue;
				if (oldValue == null) {
					newValue = Arrays.asList(new HashMap<>(startConsts));
				} else {
					for (Map<String, Long> startConstants : oldValue) {
						if (startConsts.equals(startConstants)) {
							if (be) {
								throw new AssembleError(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - tok.getStartIndex() + 1, tok.getStartIndex(),
										"loop detected! started again with the same start consts: in file='" + thisFile + "' read symbols of file='" + rf + "' constants: " + startConsts);
							} else {
								throw new AssembleRuntimeException(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - tok.getStartIndex() + 1, tok.getStartIndex(),
										"loop detected! started again with the same start consts: in file='" + thisFile + "' read symbols of file='" + rf + "' constants: " + startConsts);
							}
						}
					}
					newValue = new ArrayList<>(oldValue);
					newValue.add(new HashMap<>(startConsts));
				}
				return newValue;
			});
		} catch (AssembleRuntimeException are) {
			return are;
		}
		File file = new File(readFile);
		if (!file.isAbsolute()) {
			file = new File(this.lookup, readFile);
		}
		try (InputStream input = bytes != null ? new ByteArrayInputStream(bytes) : new FileInputStream(file)) {
			InputStream in = input;
			if (iss) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ParseContext pc = preassemble(new ANTLRInputStream(new InputStreamReader(in, StandardCharsets.UTF_8)), startConsts, be ? new BailErrorStrategy() : null, be, null, (line, charPos) -> {
				}, readFile, readFiles);
				export(pc.exports, new PrintStream(baos, true, "UTF-8"));
				in = new ByteArrayInputStream(baos.toByteArray());
			}
			try (Scanner sc = new Scanner(in, "UTF-8")) {
				readSymbols(prefix, addSymbols, sc);
			}
		}
		return null;
	}

	public static void readSymbols(String prefix, Map<String, Long> addSymbols, Scanner sc) {
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			final String regex = "^#?([a-zA-Z_0-9]+)\\s*[=]\\s*([0-9a-fA-F]+)$";
			if (!line.matches(regex)) {
				throw new RuntimeException("line does not match regex: line='" + line + "', regex='" + regex + "'");
			}
			String constName = line.replaceFirst(regex, "$1");
			long value = Long.parseUnsignedLong(line.replaceFirst(regex, "$2"), 16);
			if (prefix == null) {
				addSymbols.put(constName, value);
			} else {
				addSymbols.put(prefix + constName, value);
			}
		}
	}

	public void assemble(List<Command> cmds, Map<String, Long> labels) throws IOException {
		if (this.interpreterStart) {
			this.out.write(INERPRETER_START);
		}
		long pos = 0;
		boolean align = defaultAlign;
		Command last = null, cmd;
		for (int i = 0; i < cmds.size(); i++) {
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
						final long absoluteDest = Objects.requireNonNull(labels.get(cmd.p1.label), "can't find the used label '" + cmd.p1.label + "', I know the labels '" + labels + "'");
						assert absoluteDest >= 0;
						relativeDest = absoluteDest - pos;
						assert pos + relativeDest == absoluteDest;
					} else if (cmd.p1.art == Param.ART_ANUM) {
						assert cmd.p1.label == null;
						assert cmd.p1.off == 0;
						if (!supressWarn) {
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
			if (!supressWarn) {
				System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
			}
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, off);
			break;
		case Param.ART_ANUM_BREG:
			if (!supressWarn) {
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
				if (!supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				break;
			case Param.ART_ANUM_BREG:
				if (!supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				break;
			case Param.ART_ANUM_BSR:
				Param.checkSR(p1off);
				bytes[index--] = (byte) p1off;
				break;
			case Param.ART_ASR:
				Param.checkSR(p1num);
				bytes[index--] = (byte) p1num;
				break;
			case Param.ART_ASR_BNUM:
				Param.checkSR(p1num);
				bytes[index--] = (byte) p1num;
				break;
			case Param.ART_ASR_BREG:
				Param.checkSR(p1num);
				bytes[index--] = (byte) p1num;
				break;
			case Param.ART_ASR_BSR:
				Param.checkSR(p1num);
				Param.checkSR(p1off);
				bytes[index--] = (byte) p1num;
				bytes[index--] = (byte) p1off;
				break;
			default:
				throw new InternalError("unknown art: " + p1art);
			}
			switch (p2art) {
			case Param.ART_ANUM:
				break;
			case Param.ART_ANUM_BNUM:
				if (!supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				break;
			case Param.ART_ANUM_BREG:
				if (!supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				break;
			case Param.ART_ANUM_BSR:
				Param.checkSR(p2off);
				bytes[index--] = (byte) p2off;
				break;
			case Param.ART_ASR:
				Param.checkSR(p2num);
				bytes[index--] = (byte) p2num;
				break;
			case Param.ART_ASR_BNUM:
				Param.checkSR(p2num);
				bytes[index--] = (byte) p2num;
				break;
			case Param.ART_ASR_BREG:
				Param.checkSR(p2num);
				bytes[index--] = (byte) p2num;
				break;
			case Param.ART_ASR_BSR:
				Param.checkSR(p2num);
				Param.checkSR(p2off);
				bytes[index--] = (byte) p2num;
				bytes[index--] = (byte) p2off;
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
				if (!supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1off);
				break;
			case Param.ART_ANUM_BREG:
				if (!supressWarn) {
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
				if (!supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2off);
				break;
			case Param.ART_ANUM_BREG:
				if (!supressWarn) {
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