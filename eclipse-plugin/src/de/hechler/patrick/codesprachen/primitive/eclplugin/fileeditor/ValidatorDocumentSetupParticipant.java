package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.filebuffers.IDocumentSetupParticipantExtension;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.CpanythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.TokenInfo;

public class ValidatorDocumentSetupParticipant implements IDocumentSetupParticipant, IDocumentSetupParticipantExtension {

	private static final Map<IFile, DocumentValue> files = new HashMap<>();
	private static final Map<IDocument, DocumentValue> docs = new HashMap<>();

	public static final String MY_ID = "org.eclipse.ui.genericeditor.GenericEditor";

	public static File getProjectFile(IProject project) {
		IPath path = project.getLocation();
		return path.toFile();
	}

	public static ParseContext getContext(IFile file) {
		try {
			return getDocVal(file).context;
		} catch (IllegalArgumentException e) {
			try {
				String value = new String(file.getContents().readAllBytes(), file.getCharset());
				return preassemble(value, new PrimitiveAssembler(OutputStream.nullOutputStream()), null, (a, b) -> {
				});
			} catch (IOException | CoreException e1) {
				throw new InternalError(e1);
			}
		}
	}

	public static DocumentValue getDocVal(IFile file) throws IllegalArgumentException {
		DocumentValue docval = files.get(file);
		if (docval == null) {
			throw new IllegalArgumentException("docval is null, file=" + file);
		}
		return docval;
	}

	public static DocumentValue getDocVal(IDocument doc) throws IllegalArgumentException {
		DocumentValue docval = docs.get(doc);
		if (docval == null) {
			throw new IllegalArgumentException("docval is null, document=" + doc);
		}
		return docval;
	}

	public static TokenInfo getTokenInfo(DocumentValue docval, int off) throws NoSuchElementException {
		for (int i = 0, size = docval.context.children.size(); i < size; i++) {
			ParseTree pt = docval.context.children.get(i);
			Interval ti = getTextInterval(pt);
			if (ti.b >= off) {
				if (pt instanceof AnythingContext) {
					AnythingContext ac = (AnythingContext) pt;
					return getTokenInfo(ac, ac, off);
				} else if (pt instanceof TerminalNode) {
					AnythingContext ac = null;
					for (i--; i >= 0; i--) {
						if (docval.context.children.get(i) instanceof AnythingContext) {
							ac = (AnythingContext) docval.context.children.get(i);
							break;
						}
					}
					return new TokenInfo((TerminalNode) pt, ac);
				} else {
					throw new InternalError("ptclass: " + pt.getClass());
				}
			}
		}
		throw new NoSuchElementException("offset " + off + " not found!");
	}

	private static TokenInfo getTokenInfo(AnythingContext ac, RuleContext rc, int off) throws NoSuchElementException {
		final int len = rc.getChildCount();
		for (int i = 0; i < len; i++) {
			if (getTextInterval(rc.getChild(i)).b >= off) {
				return getTokenInfo(ac, rc.getChild(i), off);
			}
		}
		throw new NoSuchElementException("offset " + off + " not found!");
	}

	private static TokenInfo getTokenInfo(AnythingContext ac, ParseTree pt, int off) {
		if (pt instanceof RuleNode) {
			return getTokenInfo(ac, (RuleContext) pt, off);
		} else {
			TerminalNode tn = (TerminalNode) pt;
			if (tn.getSymbol().getType() != PrimitiveFileGrammarLexer.CONSTANT_POOL) {
				return new TokenInfo(tn, ac);
			}
			if (ac.zusatz == null) {// disabled
				return new TokenInfo(tn, ac);
			}
			return getTokenInfo(ac, off - getTextInterval(tn).a);
		}
	}

	private static TokenInfo getTokenInfo(AnythingContext ac_, int off) {
		List<ParseTree> children = ((ConstsContext) ac_.zusatz).children;
		for (ParseTree pt : children) {
			if (getTextInterval(pt).b >= off) {
				if (pt instanceof CpanythingContext) {
					return getTokenInfo(ac_, (CpanythingContext) pt, pt, off);
				} else {
					return new TokenInfo((TerminalNode) pt, ac_, TokenInfo.CONST_POOL_START_END_TOKEN);
				}
			}
		}
		throw new NoSuchElementException("offset " + off + " not found!");
	}

	private static TokenInfo getTokenInfo(AnythingContext ac_, CpanythingContext ac, RuleContext rc, int off) throws NoSuchElementException {
		final int len = rc.getChildCount();
		for (int i = 0; i < len; i++) {
			if (getTextInterval(rc.getChild(i)).b >= off) {
				return getTokenInfo(ac_, ac, rc.getChild(i), off);
			}
		}
		throw new NoSuchElementException("offset " + off + " not found!");
	}

	private static TokenInfo getTokenInfo(AnythingContext ac_, CpanythingContext ac, ParseTree pt, int off) {
		if (pt instanceof RuleNode) {
			return getTokenInfo(ac_, ac, (RuleContext) pt, off);
		} else {
			return new TokenInfo((TerminalNode) pt, ac_, ac);
		}
	}

	public static Interval getTextInterval(ParseTree pt) {
		if (pt instanceof RuleNode) {
			return getTextInterval((RuleNode) pt);
		} else {
			return getTextInterval((TerminalNode) pt);
		}
	}

	public static Interval getTextInterval(TerminalNode tn) {
		return getTextInterval(tn.getSymbol());
	}

	public static Interval getTextInterval(Token tok) {
		return Interval.of(tok.getStartIndex(), tok.getStopIndex());
	}

	public static Interval getTextInterval(RuleNode rn) {
		int start = getTextStart(rn);
		int stop = getTextStop(rn);
		return Interval.of(start, stop);
	}

	private static int getTextStart(RuleNode rn) {
		Token tok;
		try {
			Method met = rn.getClass().getMethod("getStart");
			tok = (Token) met.invoke(rn);
			if (tok == null) {
				throw new NullPointerException("start token is null");
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) {
			ParseTree child = rn.getChild(0);
			if (child instanceof RuleNode) {
				return getTextStart((RuleNode) child);
			} else if (child != null) {
				tok = ((TerminalNode) child).getSymbol();
			} else {
				throw new InternalError(e);
			}
		}
		return tok.getStartIndex();
	}

	private static int getTextStop(RuleNode rn) {
		Token tok;
		try {
			Method met = rn.getClass().getMethod("getStop");
			tok = (Token) met.invoke(rn);
			if (tok == null) {
				throw new NullPointerException("stop token is null");
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) {
			ParseTree child = rn.getChild(rn.getChildCount() - 1);
			if (child instanceof RuleNode) {
				return getTextStop((RuleNode) child);
			} else if (child != null) {
				tok = ((TerminalNode) child).getSymbol();
			} else {
				throw new InternalError(e);
			}
		}
		return tok.getStopIndex();
	}

	private final class DocumentValidator implements IDocumentListener {

		private final List<IMarker> markers;
		private final IFile file;
		private final ANTLRErrorListener errorListener;
		private final BiConsumer<Integer, Integer> enterCpPool;
		private int cpLine;
		private int cpCharPos;

		private DocumentValidator(IFile file) {
			this.markers = new ArrayList<>();
			this.file = file;
			this.errorListener = new ANTLRErrorListener() {

				@Override
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
					Token tok = (Token) offendingSymbol;
					Parser p = (Parser) recognizer;
					boolean inConstPool;
					switch (p.getGrammarFileName()) {
					case "PrimitiveFileGrammar.g4": {
						inConstPool = false;
						ParserRuleContext rc = p.getContext();
						while (true) {
							if (rc instanceof ParseContext) {
								ParseContext ac = (ParseContext) rc;
								if (!ac.enabled) {
									return;
								}
								break;
							}
							rc = rc.getParent();
						}
						break;
					}
					case "ConstantPoolGrammar.g4":
						inConstPool = true;
						break;
					default:
						throw new InternalError("unknown grammar File name: '" + p.getGrammarFileName() + "'", e);
					}
					IntervalSet expected = p.getExpectedTokens();
					if (expected.size() > 5) {
						StringBuilder build = new StringBuilder();
						build.append("unexpected token: ").append(tok).append('\n');
						build.append("expected:").append('\n');
						for (int i = 0, size = expected.size(); i < size; i++) {
							int symbol = expected.toIntegerList().get(i);
							String[] names = p.getTokenNames();
							String symToStr;
							if (symbol != -1) {
								symToStr = names[symbol];
							} else {
								symToStr = "<EOF>";
							}
							build.append("  ").append(symToStr).append(" : ").append(symbol).append('\n');
						}
						msg = build.toString();
					}
					if (inConstPool) {
						addError(DocumentValidator.this.markers, msg, line + DocumentValidator.this.cpLine, tok.getStartIndex() + DocumentValidator.this.cpCharPos, tok.getStartIndex());
					} else {
						addError(DocumentValidator.this.markers, msg, line, tok.getStartIndex(), tok.getStartIndex());
					}
				}

				// @formatter:off
				@Override
				public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
				}
				@Override
				public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
				}
				@Override
				public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
				}
				// @formatter:on

			};
			this.enterCpPool = (line, charPos) -> {
				this.cpLine = line;
				this.cpCharPos = charPos;
			};
		}

		private DocumentValue createDocVal(IFile file) {
			File lookup = null;
			if (file != null) {
				lookup = getProjectFile(file.getProject());
			}
			return new DocumentValue(Collections.unmodifiableList(this.markers), lookup);
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			doUpdateDocument(event.getDocument());
		}

		public void doUpdateDocument(IDocument doc) {
			DocumentValue val = docs.get(doc);
			for (IMarker marker : this.markers) {
				try {
					marker.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			this.markers.clear();
			preassemble(doc, val);
			if (val.context.are != null) {
				addAssembleRuntimeError(val.context.are);
			}
			for (ParseTree pt : val.context.children) {
				if (pt instanceof AnythingContext) {
					AnythingContext ac = (AnythingContext) pt;
					if (ac.CONSTANT_POOL != null) {
						ConstsContext cc = (ConstsContext) ac.zusatz;
						if (cc != null) {
							searchTodos(cc);
						}
					}
					searchTodos(ac);
				}
			}
		}

		private void searchTodos(RuleContext rc) {
			for (int i = 0, size = rc.getChildCount(); i < size; i++) {
				ParseTree child = rc.getChild(i);
				if (child instanceof PrimitiveFileGrammarParser.CommentContext || child instanceof ConstantPoolGrammarParser.CommentContext) {
					int line = getStartLine((RuleContext) child);
					String text = child.getText();
					for (int index = text.indexOf("TODO"); index >= 0; index = text.indexOf("TODO")) {
						boolean ignore = false;
						if (index > 0) {
							if (text.substring(index - 1, index).matches("[A-Za-z0-9_\\-\\\\]")) {
								ignore = true;
							} else if (text.substring(index + "TODO".length(), index + "TODO".length() + 1).matches("[A-Za-z0-9_\\-\\\\]")) {
								ignore = true;
							}
						}
						final String regex = "^([^\r\n]*)(\r\n?|\n)((.|\r|\n)*)$";
						String msg = text.substring(index).replaceFirst(regex, "$1");
						String skip = text.substring(0, index - 1);
						while (skip.matches(regex)) {
							skip = skip.replaceFirst(regex, "$3");
							line++;
						}
						if (!ignore) {
							addBoockmark(this.markers, msg, line);
						}
						text = text.substring(index + msg.length());
					}
				} else if (child instanceof RuleContext) {
					searchTodos((RuleContext) child);
				}
			}
		}

		private int getStartLine(RuleContext rc) {
			try {
				Class<?> cls = rc.getClass();
				Field field = cls.getField("start");
				Token tok = (Token) field.get(rc);
				return tok.getLine();
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new InternalError(e);
			}
		}

		private void addBoockmark(List<IMarker> markers, String msg, int line) {
			if (file == null) {
				return;
			}
			try {
				IMarker marker = file.createMarker(IMarker.TASK);
				marker.setAttribute(IMarker.MESSAGE, msg);
				marker.setAttribute(IMarker.LINE_NUMBER, line);
				markers.add(marker);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		private void addAssembleRuntimeError(AssembleRuntimeException are) throws InternalError {
			addError(this.markers, are);
			Throwable[] others = are.getSuppressed();
			for (Throwable t : others) {
				if (t instanceof AssembleRuntimeException) {
					addAssembleRuntimeError((AssembleRuntimeException) t);
				} else {
					assert false : "suppressed is not an AssembleRuntimeException: class: " + t.getClass().getName() + " msg: '" + t.getMessage() + "'";
				}
			}
		}

		private void addError(List<IMarker> markers, AssembleRuntimeException are) {
			addProblem(markers, are.getMessage(), are.line, are.charPos, are.length, IMarker.SEVERITY_ERROR);
		}

		private void addError(List<IMarker> markers, String msg, int line, int charPos, int length) {
			addProblem(markers, msg, line, charPos, length, IMarker.SEVERITY_ERROR);
		}

		private void addProblem(List<IMarker> markers, String msg, int line, int charPos, int length, int severity) {
			if (file == null) {
				return;
			}
			try {
				IMarker marker = file.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.SEVERITY, severity);
				marker.setAttribute(IMarker.MESSAGE, msg);
				marker.setAttribute(IMarker.LINE_NUMBER, line);
				marker.setAttribute(IMarker.CHAR_START, charPos);
				marker.setAttribute(IMarker.CHAR_END, charPos + length - 1);
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				markers.add(marker);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
		}

		private void preassemble(IDocument document, DocumentValue docval) {
			docval.context = ValidatorDocumentSetupParticipant.preassemble(document.get(), docval.asm, this.errorListener, this.enterCpPool);
		}

	}

	private static ParseContext preassemble(String document, PrimitiveAssembler asm, ANTLRErrorListener el, BiConsumer<Integer, Integer> ecp) {
		try {
			return asm.preassemble(new ANTLRInputStream(document), new HashMap<>(PrimitiveAssembler.START_CONSTANTS), null, false, el, ecp);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public void setup(IDocument document) {
		System.err.println("[PSC-WARN]: document loaded without file: " + document);
		DocumentValidator listener = new DocumentValidator(null);
		document.addDocumentListener(listener);
		initDocVal(null, document, listener);
		listener.doUpdateDocument(document);
	}

	@Override
	public void setup(IDocument document, IPath location, LocationKind locationKind) {
		DocumentValidator listener;
		IFile file = null;
		if (locationKind == LocationKind.IFILE) {
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
			listener = new DocumentValidator(file);
			document.addDocumentListener(listener);
		} else {
			System.err.println("[PSC-WARN]: document loaded without file: " + document);
			listener = new DocumentValidator(null);
			document.addDocumentListener(listener);
		}
		initDocVal(file, document, listener);
		listener.doUpdateDocument(document);
	}

	private void initDocVal(IFile file, IDocument document, DocumentValidator dv) {
		DocumentValue docval = dv.createDocVal(file);
		DocumentValue old = docs.put(document, docval);
		assert null == old;
		if (file != null) {
			old = files.put(file, docval);
			assert null == old;
			try {
				file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
				file.deleteMarkers(IMarker.TASK, false, IResource.DEPTH_ZERO);
			} catch (CoreException e) {
			}
		}
	}

}
