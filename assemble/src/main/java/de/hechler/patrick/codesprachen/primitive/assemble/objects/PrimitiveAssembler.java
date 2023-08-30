// This file is part of the Primitive Code Project
// DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
// Copyright (C) 2023 Patrick Hechler
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import static de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveCodeAssembleMain.LOG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.Convert.convertLongToByteArr;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.START_CONSTANTS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
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

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.assemble.enums.FileTypes;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable;

@SuppressWarnings("javadoc")
public class PrimitiveAssembler implements Closeable {
	
	private static final String UNWANTED_LABEL_IN_PARAMS = "I don't need a label in my params!";
	
	private static final String UNKNOWN_ART = "unknown art: ";
	
	private static final String CONSTANT_MEMORY_POINTER_WARN = "[WARN]: It is not recommended to access memory with a constant adress.";
	
	private static final String ADD_CONSTANTS_RUNTIME_WARN = "[WARN]: It is not recommended to add two constant numbers at runtime to access memory.";
	
	private static final String PARAM_MUST_BE_A_CONSTANT_CMD = "param must be a constant! (cmd: ";
	
	private static final String UNKNOWN_NOCONST_PARAM_COUNT = "unknown noconst param count: ";
	
	private static final String UNKNOWN_CONST_PARAM_COUNT = "unknown const param count: ";
	
	private final OutputStream out;
	private final PrintStream  exportOut;
	private final boolean      supressWarn;
	private final boolean      defaultAlign;
	private final Path[]       lookups;
	
	public PrimitiveAssembler(OutputStream out, PrintStream exportOut, Path[] lookups, boolean supressWarnings, boolean defaultAlign) {
		this.out          = out;
		this.exportOut    = exportOut;
		this.supressWarn  = supressWarnings;
		this.defaultAlign = defaultAlign;
		this.lookups      = lookups;
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
		return preassemble(path, in, new LinkedHashMap<>(START_CONSTANTS));
	}
	
	public ParseContext preassemble(Path path, InputStream in, Map<String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, InputStream in, Charset cs, Map<String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, new InputStreamReader(in, cs), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, Reader in, Map<String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		return preassemble(path, new ANTLRInputStream(in), predefinedConstants);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin) throws AssembleError {
		return preassemble(path, antlrin, new LinkedHashMap<>(START_CONSTANTS));
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants) throws AssembleError {
		return preassemble(path, antlrin, new LinkedHashMap<>(predefinedConstants), true);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, long pos) throws AssembleError {
		return preassemble(path, antlrin, new LinkedHashMap<>(predefinedConstants), true, pos);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, boolean bailError)
			throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, bailError ? new BailErrorStrategy() : null, bailError);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, boolean bailError, long pos)
			throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, bailError ? new BailErrorStrategy() : null, bailError, pos);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, null);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, long pos) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, null, pos);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, (line, charPos) -> {//
		});
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener, long pos) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, (line, charPos) -> {//
		}, pos);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> enterConstPool) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, "[THIS]");
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> enterConstPool, long pos) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, "[THIS]", pos);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> enterConstPool, String thisFile) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, thisFile, new LinkedHashMap<>());
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> enterConstPool, String thisFile, long pos) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, thisFile, new LinkedHashMap<>(), pos);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> enterConstPool, String thisFile,
			Map<String, List<Map<String, Long>>> readFiles) throws AssembleError {
		return preassemble(path, antlrin, predefinedConstants, errorHandler, bailError, errorListener, enterConstPool, thisFile, readFiles, 0L);
	}
	
	public ParseContext preassemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants, ANTLRErrorStrategy errorHandler,
			boolean bailError, ANTLRErrorListener errorListener, BiConsumer<Integer, Integer> enterConstPool, String thisFile,
			Map<String, List<Map<String, Long>>> readFiles, long pos) throws AssembleError {
		PrimitiveFileGrammarLexer  lexer  = new PrimitiveFileGrammarLexer(antlrin);
		CommonTokenStream          tokens = new CommonTokenStream(lexer);
		PrimitiveFileGrammarParser parser = new PrimitiveFileGrammarParser(tokens);
		if (errorHandler != null) { parser.setErrorHandler(errorHandler); }
		if (errorListener != null) { parser.addErrorListener(errorListener); }
		try {
			return parser.parse(path, pos, this.defaultAlign, predefinedConstants, bailError, errorHandler, errorListener, enterConstPool, this, antlrin, thisFile,
					readFiles);
		} catch (ParseCancellationException e) {
			Throwable cause = e.getCause();
			if (cause == null) { throw e; }
			if (cause instanceof AssembleError ae) {
				assert false;// this should never happen
				handle(ae);
			} else if (cause instanceof AssembleRuntimeException are) {
				assert false;// this should never happen, since this should not
								// be thrown
				handle(are);
			} else if (cause instanceof NoViableAltException nvae) {
				handle(nvae);
			} else if (cause instanceof InputMismatchException ime) {
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
		throw new AssertionError("handle returned");
	}
	
	/*
	 * the handle methods will never return normally they either throw an error or call System.exit(1)
	 */
	private static void handleUnknwon(Throwable t) {
		if (t instanceof Error e) { throw e; }
		throw new AssertionError("unknwon error: " + t, t);
	}
	
	private static void handle(InputMismatchException ime) {
		IntervalSet ets = ime.getExpectedTokens();
		Token       ot  = ime.getOffendingToken();
		handleIllegalInput(ime, ot, ets);
	}
	
	private static void handle(NoViableAltException nvae) {
		IntervalSet ets = nvae.getExpectedTokens();
		Token       ot  = nvae.getOffendingToken();
		handleIllegalInput(nvae, ot, ets);
	}
	
	private static void handleIllegalInput(Throwable t, Token ot, IntervalSet ets) throws AssembleError {
		StringBuilder build = new StringBuilder("error: ").append(t).append("at line ").append(ot.getLine()).append(':').append(ot.getCharPositionInLine())
				.append(" token.text='").append(ot.getText());
		build.append("' token.id=").append(tokenToString(ot.getType(), PrimitiveFileGrammarLexer.ruleNames)).append('\n').append("expected: ");
		for (int i = 0; i < ets.size(); i++) {
			if (i > 0) { build.append(", "); }
			build.append(' ').append(tokenToString(ets.get(i), PrimitiveFileGrammarLexer.ruleNames));
		}
		throw new AssembleError(ot.getLine(), ot.getCharPositionInLine(), ot.getStopIndex() - ot.getStartIndex() + 1, ot.getStartIndex(), build.toString(), t);
	}
	
	private static String tokenToString(int tok, String[] names) {
		String token;
		if (tok > 0) {
			token = "<" + names[tok] + '>';
		} else if (tok == Recognizer.EOF) {
			token = "<EOF>";
		} else {
			token = "<UNKNOWN=" + tok + ">";
		}
		return token;
	}
	
	private static void handle(AssembleRuntimeException ae) {
		handle(ae.line, ae.posInLine, ae.charPos, ae.length, ae);
	}
	
	private static void handle(AssembleError ae) {
		handle(ae.line, ae.posInLine, ae.charPos, ae.length, ae);
	}
	
	private static void handle(int line, int posInLine, int len, int charPos, Throwable t) {
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
	
	public void assemble(Path path, ANTLRInputStream antlrin, Map<String, PrimitiveConstant> predefinedConstants) throws IOException, AssembleError {
		assemble(preassemble(path, antlrin, predefinedConstants));
	}
	
	public void assemble(PrimitiveFileGrammarParser.ParseContext parsed) throws IOException {
		assemble(parsed.commands, parsed.labels);
		export(parsed.exports);
	}
	
	public void export(Map<String, PrimitiveConstant> exports) {
		if (this.exportOut == null) { return; }
		PrimAsmConstants.export(exports, this.exportOut);
	}
	
	public AssembleRuntimeException readSymbols(String readFile, Boolean isSource, boolean isSimpleSymbol, String prefix, Map<String, PrimitiveConstant> startConsts,
			Map<String, PrimitiveConstant> addSymbols, ANTLRInputStream antlrin, boolean be, Token tok, String thisFile,
			Map<String, List<Map<String, Long>>> readFiles) throws IllegalArgumentException, IOException {
		readFiles = new LinkedHashMap<>(readFiles);
		byte[] bytes      = null;
		int    startIndex = tok.getStartIndex();
		if (readFile.equals("[THIS]")) {
			if (isSource != null && !isSource.booleanValue()) {
				if (be) {
					throw new AssembleError(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - startIndex, startIndex,
							"is source set to false, but path is set to [THIS]");
				}
				return new AssembleRuntimeException(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - startIndex, startIndex,
						"is source set to false, but path is set to [THIS]");
			}
			bytes    = antlrin.getText(new Interval(0, antlrin.size() - 1)).getBytes(StandardCharsets.UTF_8);
			isSource = Boolean.TRUE;
			readFile = thisFile;
		}
		boolean isPrimSourceCode;
		if (isSource != null) {
			isPrimSourceCode = isSource.booleanValue();
		} else {
			FileTypes type = FileTypes.getTypeFromName(readFile, FileTypes.PRIMITIVE_MASHINE_CODE); // here used as magic for illegal
			switch (type) {
			case PRIMITIVE_SOURCE_CODE -> isPrimSourceCode = true;
			case PRIMITIVE_SYMBOL_FILE -> isPrimSourceCode = false;
			case SIMPLE_SYMBOL_FILE -> {
				isPrimSourceCode = false;
				isSimpleSymbol   = true;
			}
			// $CASES-OMITTED$
			default -> throw new IllegalArgumentException("Source/Symbol not set, but readFile is not *.psc and not *.psf! readFile='" + readFile + "'");
			}
		}
		if (isPrimSourceCode) {
			try {
				final String rf = readFile;
				readFiles.compute(readFile, (String key, List<Map<String, Long>> oldValue) -> detectLoop(startConsts, be, tok, thisFile, startIndex, rf, oldValue));
			} catch (AssembleRuntimeException are) {
				return are;
			}
		}
		Path path = Paths.get(readFile);
		if (!path.isAbsolute()) {
			for (int i = 0; i < this.lookups.length; i++) {
				Path p = Paths.get(this.lookups[i].toString(), readFile);
				if (Files.exists(p)) {
					path = p;
					break;
				}
			}
		}
		if (isSimpleSymbol) {
			try (Reader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				Map<String, SimpleExportable> imps = SimpleExportable.readExports(null, in);
				SimpleExportable.toPrimConsts(addSymbols, prefix, imps, path);
			}
			return null;
		}
		try (InputStream input = bytes != null ? new ByteArrayInputStream(bytes) : Files.newInputStream(path)) {
			InputStream in = input;
			if (isPrimSourceCode) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ParseContext          pc   = preassemble(path, new ANTLRInputStream(new InputStreamReader(in, StandardCharsets.UTF_8)), startConsts,
						be ? new BailErrorStrategy() : null, be, null, (line, charPos) -> {                                                         //
													},
						readFile, readFiles);
				PrimAsmConstants.export(pc.exports, new PrintStream(baos, true, "UTF-8"));
				in = new ByteArrayInputStream(baos.toByteArray());
			}
			try (Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {
				PrimAsmConstants.readSymbols(prefix, addSymbols, sc, path);
			}
		}
		return null;
	}
	
	private static List<Map<String, Long>> detectLoop(Map<String, PrimitiveConstant> startConsts, boolean be, Token tok, String thisFile, int startIndex,
			final String rf, List<Map<String, Long>> oldValue) throws AssembleError {
		List<Map<String, Long>> newValue;
		if (oldValue == null) {
			newValue = Arrays.asList(convertPrimConstMapToLongMap(startConsts));
		} else {
			for (Map<String, Long> startConstants : oldValue) {
				if (startConsts.equals(startConstants)) {
					if (be) {
						throw new AssembleError(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - startIndex + 1, startIndex,
								"loop detected! started again with the same start consts: in file='" + thisFile + "' read symbols of file='" + rf + "' constants: "
										+ startConsts);
					}
					throw new AssembleRuntimeException(tok.getLine(), tok.getCharPositionInLine(), tok.getStopIndex() - startIndex + 1, startIndex,
							"loop detected! started again with the same start consts: in file='" + thisFile + "' read symbols of file='" + rf + "' constants: "
									+ startConsts);
				}
			}
			newValue = new ArrayList<>(oldValue);
			newValue.add(convertPrimConstMapToLongMap(startConsts));
		}
		return newValue;
	}
	
	private static Map<String, Long> convertPrimConstMapToLongMap(Map<String, PrimitiveConstant> startConsts) {
		Map<String, Long> nv = new LinkedHashMap<>();
		startConsts.forEach((n, pc) -> nv.put(n, Long.valueOf(pc.value())));
		return nv;
	}
	
	public void assemble(List<Command> cmds, Map<String, Long> labels) throws IOException {
		long    pos       = 0;
		boolean alignMode = this.defaultAlign;
		boolean alignable = false;
		long    index     = -1;
		for (Command cmd : cmds) {
			index++;
			boolean cond = true;
			while (cond) {
				pos = align(pos, alignable && alignMode);
				if (cmd.getClass() == Command.class) {
					alignable = false;
					asmCommand(pos, labels, cmd);
				} else if (cmd instanceof ConstantPoolCommand cpc) {
					alignable = true;
					cpc.write(this.out);
				} else if (cmd instanceof CompilerCommandCommand ccc) {
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
							throw new AssertionError("not at the assertet position! (expected:" + ccc.value + " pos:" + pos + ") index=" + index
									+ (index >= (1 << 5) ? "" : " commands: " + cmds.subList(0, (int) index + 1)));
						}
						break;
					default:
						throw new AssertionError("unknown directive: " + ccc.directive.name());
					}
				} else {
					cmd = replaceUnknownCommand(cmd);
					continue;
				}
				pos  += cmd.length();
				cond  = false;
			}
		}
		this.out.flush();
	}
	
	protected Command replaceUnknownCommand(Command cmd) throws AssertionError {
		throw new AssertionError("unknown command class: " + cmd.getClass().getName());
	}
	
	private long align(long pos, boolean doAlign) throws IOException {
		if (!doAlign) return pos;
		int mod = (int) (pos % 8);
		if (mod != 0) {
			int    add   = 8 - mod;
			byte[] bytes = new byte[add];
			this.out.write(bytes, 0, bytes.length);
			pos += add;
		}
		return pos;
	}
	
	private void asmCommand(long pos, Map<String, Long> labels, Command cmd) throws IOException, AssertionError {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) cmd.cmd.num;
		bytes[1] = (byte) (cmd.cmd.num >>> 8);
		switch (cmd.cmd.params) {
		case 3 -> asm3Params(cmd, bytes);
		case 2 -> asm2Params(cmd, bytes);
		case 1 -> asm1Params(pos, labels, cmd, bytes);
		case 0 -> asm0Params(cmd, bytes);
		default -> throw new AssertionError("unknown command param count: " + cmd.cmd.name());
		}
	}
	
	private void asm0Params(Command cmd, byte[] bytes) throws IOException {
		nullCheck(cmd.p3, cmd);
		nullCheck(cmd.p2, cmd);
		nullCheck(cmd.p1, cmd);
		writeNullParam(cmd, bytes);
	}
	
	private void asm1Params(long pos, Map<String, Long> labels, Command cmd, byte[] bytes) throws IOException, AssertionError {
		nullCheck(cmd.p3, cmd);
		nullCheck(cmd.p2, cmd);
		switch (cmd.cmd.noconstParams) {
		case 1 -> {
			noConstCheck(cmd.p1, cmd);
			writeOneParam(cmd, bytes);
		}
		case 0 -> writeOneParam(cmd, bytes);
		case -1 -> {
			long num;
			if (cmd.p1.label != null) {
				Long numobj = labels.get(cmd.p1.label);
				if (numobj == null) {
					throw new NullPointerException("label not found! label: '" + cmd.p1.label + "' known labels: '" + labels + "'");
				}
				num = numobj.longValue() - pos;
			} else if (cmd.p1.art == Param.ART_ANUM) {
				num = cmd.p1.num;
			} else {
				throw new IllegalStateException(PARAM_MUST_BE_A_CONSTANT_CMD + cmd.cmd.name() + ')');
			}
			fillJumpAddr(bytes, num);
			this.out.write(bytes, 0, bytes.length);
		}
		default -> throw new AssertionError(UNKNOWN_NOCONST_PARAM_COUNT + cmd.cmd.name());
		}
	}
	
	private void asm2Params(Command cmd, byte[] bytes) throws AssertionError, IOException {
		nullCheck(cmd.p3, cmd);
		switch (cmd.cmd.constParams) {
		case 1 -> {
			if (cmd.cmd.noconstParams != 0) { throw new AssertionError(UNKNOWN_NOCONST_PARAM_COUNT + cmd.cmd.name()); }
			writeOneParam(cmd, bytes);
		}
		case 0 -> {
			switch (cmd.cmd.noconstParams) {
			case 2 -> {
				noConstCheck(cmd.p2, cmd);
				noConstCheck(cmd.p1, cmd);
				writeTwoParam(cmd, bytes);
			}
			case 1 -> {
				noConstCheck(cmd.p1, cmd);
				writeTwoParam(cmd, bytes);
			}
			case 0 -> writeTwoParam(cmd, bytes);
			case -1 -> {
				if (cmd.p2.art != Param.ART_ANUM) { throw new IllegalStateException(PARAM_MUST_BE_A_CONSTANT_CMD + cmd.cmd.name() + ')'); }
				writeOneParam(cmd, bytes);
				fillJumpAddr(bytes, cmd.p2.num);
				this.out.write(bytes, 0, bytes.length);
			}
			default -> throw new AssertionError(UNKNOWN_NOCONST_PARAM_COUNT + cmd.cmd.name());
			}
		}
		default -> throw new AssertionError(UNKNOWN_CONST_PARAM_COUNT + cmd.cmd.name());
		}
	}
	
	private void asm3Params(Command cmd, byte[] bytes) throws AssertionError, IOException {
		if (cmd.cmd.noconstParams != 1) { throw new AssertionError(UNKNOWN_NOCONST_PARAM_COUNT + cmd.cmd.name()); }
		if (cmd.p3.art != Param.ART_ANUM) { throw new IllegalStateException(PARAM_MUST_BE_A_CONSTANT_CMD + cmd.cmd.name() + ')'); }
		noConstCheck(cmd.p2, cmd);
		writeTwoParam(cmd, bytes);
		convertLongToByteArr(bytes, cmd.p3.num);
		this.out.write(bytes, 0, bytes.length);
	}
	
	private static void nullCheck(Param nullParam, Command cmd) {
		if (nullParam != null) { throw new IllegalStateException("this param should be null: '" + nullParam + "' cmd: '" + cmd + '\''); }
	}
	
	private static void noConstCheck(Param param, Command cmd) {
		if (param.art == Param.ART_ANUM) { throw new IllegalStateException("no constants allowed: '" + cmd + '\''); }
	}
	
	private static void fillJumpAddr(byte[] bytes, long num) {
		if ((num & 0x0000800000000000L) == 0) {
			if ((num & 0xFFFF000000000000L) != 0L) {
				throw new IllegalArgumentException("number is out of the 48 bit range: " + num + " 0x" + Long.toHexString(num));
			}
		} else if ((num & 0xFFFF000000000000L) != 0xFFFF000000000000L) {
			throw new IllegalArgumentException("number is out of the 48 bit range: " + num + " 0x" + Long.toHexString(num));
		}
		bytes[2] = (byte) num;
		bytes[3] = (byte) (num >> 8);
		bytes[4] = (byte) (num >> 16);
		bytes[5] = (byte) (num >> 24);
		bytes[6] = (byte) (num >> 32);
		bytes[7] = (byte) (num >> 40);
	}
	
	private void writeNullParam(@SuppressWarnings("unused") Command cmd, byte[] bytes) throws IOException {
		assert bytes.length == 8;
		this.out.write(bytes, 0, 8);
	}
	
	private void writeOneParam(Command cmd, byte[] bytes) throws IOException {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p1.label == null : UNWANTED_LABEL_IN_PARAMS;
		assert bytes.length == 8;
		bytes[2] = (byte) cmd.p1.art;
		long num = cmd.p1.num;
		long off = cmd.p1.off;
		int  art = cmd.p1.art;
		addRegs(bytes, num, off, art, 7);
		addNums(bytes, num, off, art);
		this.out.write(bytes, 0, 8);
	}
	
	private void writeTwoParam(Command cmd, byte[] bytes) throws IOException {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p2 != null : "I need a second Param!";
		assert cmd.p1.label == null : UNWANTED_LABEL_IN_PARAMS;
		assert cmd.p2.label == null : UNWANTED_LABEL_IN_PARAMS;
		final long p1num = cmd.p1.num;
		final long p1off = cmd.p1.off;
		final long p2num = cmd.p2.num;
		final long p2off = cmd.p2.off;
		final int  p1art = cmd.p1.art;
		final int  p2art = cmd.p2.art;
		int        index = 7;
		bytes[2] = (byte) p1art;
		bytes[3] = (byte) p2art;
		index    = addRegs(bytes, p1num, p1off, p1art, index);
		addRegs(bytes, p2num, p2off, p2art, index);
		addNums(bytes, p1num, p1off, p1art);
		addNums(bytes, p2num, p2off, p2art);
		this.out.write(bytes, 0, 8);
	}
	
	private void addNums(byte[] bytes, final long pnum, final long poff, final int part) throws IOException, AssertionError {
		switch (part) {
		case Param.ART_ANUM -> {
			this.out.write(bytes, 0, 8);
			convertLongToByteArr(bytes, pnum);
		}
		case Param.ART_ANUM_BNUM -> {
			if (!this.supressWarn) { LOG.warning(ADD_CONSTANTS_RUNTIME_WARN); }
			this.out.write(bytes, 0, 8);
			convertLongToByteArr(bytes, pnum);
			this.out.write(bytes, 0, 8);
			convertLongToByteArr(bytes, poff);
		}
		case Param.ART_ANUM_BADR -> {
			if (!this.supressWarn) { LOG.warning(CONSTANT_MEMORY_POINTER_WARN); }
			this.out.write(bytes, 0, 8);
			convertLongToByteArr(bytes, pnum);
		}
		case Param.ART_ANUM_BREG -> {
			this.out.write(bytes, 0, 8);
			convertLongToByteArr(bytes, pnum);
		}
		case Param.ART_AREG -> {/**/}
		case Param.ART_AREG_BNUM -> {
			this.out.write(bytes, 0, 8);
			convertLongToByteArr(bytes, poff);
		}
		case Param.ART_AREG_BADR -> {/**/}
		case Param.ART_AREG_BREG -> {/**/}
		default -> throw new AssertionError(UNKNOWN_ART + part);
		}
	}
	
	private int addRegs(byte[] bytes, final long pnum, final long poff, final int part, int index) throws AssertionError {
		switch (part) {
		case Param.ART_ANUM -> { /**/ }
		case Param.ART_ANUM_BNUM -> { if (!this.supressWarn) { LOG.warning(ADD_CONSTANTS_RUNTIME_WARN); } }
		case Param.ART_ANUM_BADR -> { if (!this.supressWarn) { LOG.warning(CONSTANT_MEMORY_POINTER_WARN); } }
		case Param.ART_ANUM_BREG -> {
			Param.checkSR(poff);
			bytes[index--] = (byte) poff;
		}
		case Param.ART_AREG -> {
			Param.checkSR(pnum);
			bytes[index--] = (byte) pnum;
		}
		case Param.ART_AREG_BNUM -> {
			Param.checkSR(pnum);
			bytes[index--] = (byte) pnum;
		}
		case Param.ART_AREG_BADR -> {
			Param.checkSR(pnum);
			bytes[index--] = (byte) pnum;
		}
		case Param.ART_AREG_BREG -> {
			Param.checkSR(pnum);
			Param.checkSR(poff);
			bytes[index--] = (byte) pnum;
			bytes[index--] = (byte) poff;
		}
		default -> throw new AssertionError(UNKNOWN_ART + part);
		}
		return index;
	}
	
	@Override
	public void close() throws IOException {
		try {
			this.out.close();
		} finally {
			if (this.exportOut != null) {
				this.exportOut.close();
			}
		}
	}
	
}
