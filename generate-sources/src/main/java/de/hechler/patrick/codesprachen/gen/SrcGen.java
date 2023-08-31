// This file is part of the Patr File System and Code Projects
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
package de.hechler.patrick.codesprachen.gen;

import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;

@SuppressWarnings("javadoc")
public interface SrcGen {
	
	static final String BASE_DIR           = initilizeBaseDir();
	static final String PRIMITIVE_CODE_DIR = BASE_DIR + "primitive-code/";
	static final String PATR_FILE_SYS_DIR  = BASE_DIR + "PatrFileSys/";
	static final String SIMPLE_CODE_DIR    = BASE_DIR + "simple-code/";
	static final String J2P_DIR            = BASE_DIR + "java-2-prim/";
	static final String SIMPLE_COMPILE_DIR = SIMPLE_CODE_DIR + "simple-compile/";
	static final String PRIM_CODE_README   = PRIMITIVE_CODE_DIR + "README.md";
	static final String J2P_DOC_FILE       = J2P_DIR + "doc/file.md";
	
	static String initilizeBaseDir() {
		String baseDir = System.getProperty("user.home") + "/git/";
		System.out.println("base dir: " + baseDir);
		return baseDir;
	}
	
	void generate(Writer out) throws IOException;
	
	static String mdToJavadoc(String md) {
		String javadoc = md.replace("&", "&amp;");
		javadoc = javadoc.replace(">", "&gt;").replace("<", "&lt;");
		javadoc = javadoc.replaceAll("`([^`]*)`", "<code>$1</code>");
		javadoc = javadoc.replaceAll("(\\s)___([^_]*)___(\\s)", "$1<b><i>$2</i></b>$3");
		javadoc = javadoc.replaceAll("(\\s)__([^_]*)__(\\s)", "$1<b>$2</b>$3");
		javadoc = javadoc.replaceAll("(\\s)_([^_]*)_(\\s)", "$1<i>$2</i>$3");
		javadoc = javadoc.replace("[predefined constant](#predefined-constants)", "{@link PrimAsmPreDefines predefined constant}");
		return javadoc;
	}
	
	static String writeJavadocLines(Writer out, String firstBreak, List<String> lines) throws IOException {
		return writeJavadocLines(out, "\t * ", firstBreak, lines);
	}
	
	static String writeJavadocLines(Writer out, String lineStart, String firstBreak, List<String> lines) throws IOException {
		String br = firstBreak;
		for (Iterator<String> iter = lines.iterator(); iter.hasNext();) {
			String line = iter.next();
			if (line.charAt(0) == '*') {
				out.write(br + "\n" + lineStart + SrcGen.mdToJavadoc(line.substring(1).trim()));
				br = "<br>";
			} else {
				List<Boolean> stack = new ArrayList<>();
				out.write("\n" + lineStart + listStart(line.charAt(4), stack, line) + '\n');
				javadocNonDirectListLine(out, lineStart, iter, line, stack);
				br = "<br>";
				if (!stack.isEmpty()) throw new IllegalStateException("stack is not empty");
			}
		}
		return br;
	}
	
	static void javadocNonDirectListLine(Writer out, String lineStart, Iterator<String> iter, String line, List<Boolean> stack) throws IOException {
		int     deep            = 1;
		String  start           = "    *";
		boolean missingEntryEnd = false;
		while (true) {
			if (!line.startsWith(start)) {
				String  leadingWhite = line.substring(0, line.length() - line.stripLeading().length());
				boolean cond         = true;
				if (line.charAt(leadingWhite.length()) != start.charAt(start.length() - 1)) {
					start = start.substring(0, start.length() - 1) + line.substring(leadingWhite.length(), leadingWhite.length() + 1);
					cond  = !line.startsWith(start);
				}
				if (cond) {
					if (leadingWhite.length() < start.length() - 1) {
						if (missingEntryEnd) {
							missingEntryEnd = false;
							out.write("</li>\n");
						}
						do {
							out.write(lineStart + listEnd(stack) + "</li>\n");
							deep--;
							start = start.substring(4);
						} while (leadingWhite.length() < start.length() - 1);
					} else if (leadingWhite.length() > start.length() - 1) {
						if (!missingEntryEnd) { throw new IllegalStateException(); }
						out.write("\n" + lineStart + listStart(line.charAt(leadingWhite.length()), stack, line) + '\n');
						missingEntryEnd = false;
						deep++;
						start = "    " + start;
					} else {
						throw new IllegalStateException("'" + line + "' (start: '" + start + "')");
					}
					if (leadingWhite.length() != start.length() - 1 || !leadingWhite.matches("^ *$")) {
						throw new IllegalStateException("'" + line + "' start='" + start + "'");
					}
				}
			}
			if (missingEntryEnd) {
				out.write("</li>\n");
			}
			out.write(lineStart + "<li>" + mdToJavadoc(mdListEntryText(line).trim()));
			missingEntryEnd = true;
			if (!iter.hasNext()) {
				line = null;
				break;
			}
			line = iter.next();
			if (line.charAt(0) == '*') {
				break;
			}
		}
		javadocNonDirectListEnd(out, lineStart, line, stack, deep, missingEntryEnd);
	}
	
	static void javadocNonDirectListEnd(Writer out, String lineStart, String line, List<Boolean> stack, int deep, boolean missingEntryEnd) throws IOException {
		if (missingEntryEnd) {
			out.write("</li>\n");
		}
		while (--deep > 0) {
			out.write(lineStart + listEnd(stack) + "</li>\n");
		}
		out.write(lineStart + listEnd(stack) + '\n');
		if (line != null) {
			out.write(lineStart + mdToJavadoc(line.substring(1).trim()));
		}
	}
	
	static final Pattern MD_LIST_LINE = Pattern.compile("^( {4})+([+*]|[0-9]+.)\\s*(.*)$");
	
	static String mdListEntryText(String line) {
		Matcher matcher = MD_LIST_LINE.matcher(line);
		if (!matcher.matches()) { throw new IllegalStateException("'" + line + "'"); }
		return matcher.group(matcher.groupCount());
	}
	
	static String listEnd(List<Boolean> stack) {
		if (stack.remove(stack.size() - 1).booleanValue()) {
			return "</ol>";
		}
		return "</ul>";
	}
	
	static String listStart(char c, List<Boolean> stack, String fullLine) {
		if (c == '*') {
			stack.add(Boolean.FALSE);
			return "<ul>";
		} else if (c >= '0' && c <= '9') {
			stack.add(Boolean.TRUE);
			return "<ol>";
		} else {
			throw new IllegalStateException("'" + c + "' fullLine: '" + fullLine + "'");
		}
	}
	
	enum ParamType {
		
		NO_CONST_PARAM, PARAM, CONST_PARAM, LABEL, NONE;
		
		public static ParamType val(String type) {
			if (type == null || type.isEmpty()) { return null; }
			if (type.charAt(0) == ',') {
				type = type.substring(1).trim();
			}
			return switch (type) {
			case "NO_CONST_PARAM", "<NO_CONST_PARAM>" -> NO_CONST_PARAM;
			case "PARAM", "<PARAM>" -> PARAM;
			case "CONST_PARAM", "<CONST_PARAM>" -> CONST_PARAM;
			case "LABEL", "<LABEL>" -> LABEL;
			case "NONE", "<NONE>" -> NONE;
			default -> throw new IllegalStateException("unknown parameter type: '" + type + "'");
			};
		}
		
	}
	
	static class J2P {
		
		public static final String JNI_ENV_START = "JNI_Env_";
		public static final String JNI_ENV_END   = "_OFF";
		
		private static final Pattern HEADER_PATTERN  = Pattern.compile("^##\\s*([A-Za-z0-9_\\- ]+)$");
		private static final Pattern CONST_START     = Pattern
				.compile("^\\+\\s+`\\s*([A-Za-z0-9_]+)\\s*`\\s*:\\s*`\\s*([0-9]+)\\s*`\\s*:\\s*`\\s*U?HEX-([0-9A-Fa-f]+)\\s*`\\s*$");
		private static final Pattern OFF_CONST_START = Pattern.compile("^\\+\\s+`\\s*offset=([0-9]+)=U?HEX-([0-9A-Fa-f]+)\\s*`\\s*:\\s*_([A-Za-z0-9_]+)_\\s*$");
		private static final Pattern CONST_DOC       = Pattern.compile("^ +\\+\\s+(.*)$");
		
		public static final Map<String, PrimitiveConstant> CONSTANTS = readConsts();
		
		private static Map<String, PrimitiveConstant> readConsts() {
			Map<String, PrimitiveConstant> result = new LinkedHashMap<>();
			try (Stream<String> lines = Files.lines(Path.of(J2P_DOC_FILE), StandardCharsets.UTF_8)) {
				lines.forEachOrdered(new Consumer<>() {
					
					int                state;
					int                lineNumber;
					String             name;
					long               value;
					final List<String> docLines = new ArrayList<>();
					
					@Override
					public void accept(String line) {
						if (this.lineNumber < 0) { return; }
						if (++this.lineNumber < 0) throw new IllegalStateException("more than int-max lines");
						switch (this.state) {
						case 0 -> {
							Matcher header = HEADER_PATTERN.matcher(line);
							if (header.matches() && header.group(1).equals("Error Constants")) {
								this.state++;
							}
						}
						case 1 -> readConst(result, line);
						case 2 -> {
							Matcher header = HEADER_PATTERN.matcher(line);
							if (header.matches() && header.group(1).equals("_JNI-Env_")) {
								this.state++;
							}
						}
						case 3 -> {
							if (line.equals("Operations:")) {
								this.state++;
							} else if (HEADER_PATTERN.matcher(line).matches()) {
								throw new IllegalStateException("got a header before the Operations list started '" + line + "'");
							}
						}
						case 4 -> readConst(result, line);
						case 5 -> this.lineNumber = -1;
						default -> throw new AssertionError(this.state);
						}
					}
					
					private Pattern constStart() { return this.state != 4 ? CONST_START : OFF_CONST_START; }
					
					private int valGroup() { return this.state != 4 ? 2 : 1; }
					
					private int hexGroup() { return this.state != 4 ? 3 : 2; }
					
					private String constName(Matcher matcher) {
						if (this.state != 4) {
							String n = matcher.group(1);
							if (n.startsWith(JNI_ENV_START)) {
								throw new IllegalStateException("the name starts with the special JNI_ENV_START value: " + JNI_ENV_START + " name: " + n);
							}
							return n;
						}
						return JNI_ENV_START + matcher.group(3) + JNI_ENV_END;
					}
					
					private void readConst(Map<String, PrimitiveConstant> result, String line) {
						if (this.name == null) {
							newConst(line);
						} else if (line.isEmpty()) {
							finishConstant(result);
							this.docLines.clear();
							this.name = null;
							this.state++;
						} else if (line.startsWith("    ")) {
							Matcher matcher = CONST_DOC.matcher(line);
							if (!matcher.matches()) {
								throw new IllegalStateException("the line does not match CONST_DOC regex, but starts with 4 spaces: '" + line + "'");
							} // modify the line in comment()
							this.docLines.add(line);
						} else {
							finishConstant(result);
							this.docLines.clear();
							newConst(line);
						}
					}
					
					private void newConst(String line) {
						Matcher matcher = constStart().matcher(line);
						if (!matcher.matches()) {
							throw new IllegalStateException("first line does not match CONST_START regex: '" + line + "'");
						}
						this.name  = constName(matcher);
						this.value = Long.parseLong(matcher.group(valGroup()));
						if (this.value != Long.parseUnsignedLong(matcher.group(hexGroup()), 16)) {
							throw new IllegalStateException("the two values are different: '" + line + "'");
						}
						if (this.state == 4 && (this.value & 7) != 0) {
							throw new IllegalStateException("illegal value (offset values must be dividable by 8): '" + line + "'");
						}
					}
					
					private void finishConstant(Map<String, PrimitiveConstant> result) {
						PrimitiveConstant pc = new PrimitiveConstant(this.name, comment(), this.value, GenSourceMain.J2P_CONSTANTS_PATH, this.lineNumber);
						result.put(this.name, pc);
					}
					
					private String comment() {
						StringBuffer res = this.docLines.stream().collect(StringBuffer::new, (b, s) -> b.append('|').append(s, 5, s.length()).append('\n'),
								StringBuffer::append);
						res.replace(res.length() - 1, res.length(), "");
						return res.toString();
					}
					
				});
			} catch (IOException e) {
				throw new IOError(e);
			}
			return Collections.unmodifiableMap(result);
		}
		
	}
	
	static record PrimAsmReadmeCommand(String name, ParamType p1, ParamType p2, ParamType p3, int num, List<String> general, List<String> definition) {
		
		private static final Pattern ACTIVATION_PATTERN = Pattern.compile("^\\s*##\\s*COMMANDS\\s*$");
		private static final Pattern CMD_PATTERN = Pattern.compile("^`([A-Z_0-9]+)\\s*(<[A-Z_]+>)?\\s*(,\\s*<[A-Z_]+>)?\\s*(,\\s*<[A-Z_]+>)?`$");
		private static final Pattern DEFINITION_PATTERN = Pattern.compile("^\\s*[+*]\\s*definition\\s*:\\s*$");
		private static final Pattern BINARY_PATTERN = Pattern.compile("^\\s*[+*]\\s*binary\\s*:\\s*$");
		private static final Pattern NUM_PATTERN = Pattern.compile("^\\s*[+*]\\s*`\\s*([0-9A-F][0-9A-F])\\s*([0-9A-F][0-9A-F]).*$");
		private static final Pattern IGNORE_PATTERN = Pattern.compile("^\\s*[+*]\\s*`\\s*[<\\[].*$");
		private static final Pattern HEADERS_PATTERN = Pattern.compile("^\\s*###\\s*([0-9A-F][0-9A-F])\\.\\.\\s*:(.*)$");
		private static final Pattern SUB_HEADERS_PATTERN = Pattern.compile("^\\s*####\\s*([0-9A-F][0-9A-F][0-9A-F])\\.\\s*:(.*)$");
		private static final Pattern END_PATTERN = Pattern.compile("^\\s*##\\s*not\\s*\\(\\s*yet\\s*\\)\\s*there\\s*/\\s*supported\\s*$");
		
		private static final String[] HEADERS = new String[256];
		private static final String[] SUB_HEADERS = new String[256 * 16];
		
		
		
		public static String header(int num) {
			return HEADERS[num];
		}
		
		public static String subHeader(int num) {
			return SUB_HEADERS[num];
		}
		
		public static final List<PrimAsmReadmeCommand> ALL_CMDS = primAsmCmds();
		
		static List<PrimAsmReadmeCommand> primAsmCmds() {
			List<PrimAsmReadmeCommand> result = new ArrayList<>();
			try (Stream<String> lines = Files.lines(Path.of(PRIM_CODE_README), StandardCharsets.UTF_8)) {
				lines.forEachOrdered(new Consumer<>() {
					
					private boolean activated = false;
					private boolean finish    = false;
					
					private String       cmdName    = null;
					private ParamType    p1         = null;
					private ParamType    p2         = null;
					private ParamType    p3         = null;
					private int          num        = -1;
					private List<String> general    = null;
					private List<String> definition = null;
					
					@Override
					public void accept(String line) {
						if (this.finish) {/**/} else if (!this.activated) {
							Matcher matcher = ACTIVATION_PATTERN.matcher(line);
							if (matcher.matches()) {
								this.activated = true;
							}
						} else if (line.isBlank()) {
							if (this.cmdName != null) { throw new IllegalStateException(this.cmdName); }
						} else if (this.cmdName == null) {
							Matcher matcher = CMD_PATTERN.matcher(line);
							if (!matcher.matches()) {
								matcher = SUB_HEADERS_PATTERN.matcher(line);
								if (matcher.matches()) {
									int val = Integer.parseInt(matcher.group(1), 16);
									SUB_HEADERS[val] = matcher.group(2).trim();
								} else {
									matcher = HEADERS_PATTERN.matcher(line);
									if (matcher.matches()) {
										int val = Integer.parseInt(matcher.group(1), 16);
										HEADERS[val] = matcher.group(2).trim();
									} else {
										matcher = IGNORE_PATTERN.matcher(line);
										if (!matcher.matches()) {
											matcher = END_PATTERN.matcher(line);
											if (!matcher.matches()) {
												throw new IllegalStateException(line);
											}
											this.finish = true;
										}
									}
								}
							} else {
								this.cmdName = matcher.group(1);
								String s1 = matcher.group(2);
								String s2 = matcher.group(3);
								String s3 = matcher.group(4);
								this.p1      = ParamType.val(s1);
								this.p2      = ParamType.val(s2);
								this.p3      = ParamType.val(s3);
								this.general = new ArrayList<>();
							}
						} else if (this.definition == null) {
							Matcher matcher = DEFINITION_PATTERN.matcher(line);
							if (matcher.matches()) {
								this.definition = new ArrayList<>();
							} else {
								this.general.add(line);
							}
						} else if (this.num == -1) {
							Matcher matcher = BINARY_PATTERN.matcher(line);
							if (matcher.matches()) {
								this.num = -2;
							} else {
								this.definition.add(line);
							}
						} else if (this.num == -2) {
							Matcher matcher = NUM_PATTERN.matcher(line);
							if (!matcher.matches()) { throw new IllegalStateException(line); }
							int lb = Integer.parseInt(matcher.group(2), 16);
							int hb = Integer.parseInt(matcher.group(1), 16);
							this.num = (hb << 8) | lb;
							PrimAsmReadmeCommand parc = new PrimAsmReadmeCommand(this.cmdName, this.p1, this.p2, this.p3, this.num,
									Collections.unmodifiableList(this.general), Collections.unmodifiableList(this.definition));
							result.add(parc);
							reset();
						}
					}
					
					
					private void reset() {
						if ((this.num & 0xFFFF) != this.num) { throw new IllegalStateException(); }
						this.cmdName    = null;
						this.p1         = null;
						this.p2         = null;
						this.p3         = null;
						this.num        = -1;
						this.general    = null;
						this.definition = null;
					}
					
				});
			} catch (IOException e) {
				throw new IOError(e);
			}
			return Collections.unmodifiableList(result);
		}
		
	}
	
	enum ValueType {
		
		DECIMAL, HEX, UHEX, NHEX, UHEX_DWORD
	
	}
	
	static record PrimAsmConstant(String name, long value, ValueType valType, String header, List<String> docu) {
		
		private static final Pattern ACTIVATION_PATTERN = Pattern.compile("^###\\s*Predefined\\s*Constants\\s*$");
		private static final Pattern CONST_START_PATTERN = Pattern.compile("^\\*\\s*`([A-Z_0-9]+)`\\s*:\\s*([^\\s].*[^\\s])\\s*$");
		private static final Pattern CONST_VAL_PATTERN = Pattern.compile("^ {4}\\*\\s*value\\s*:\\s*`([NU]?HEX-[0-9A-F]+|-?[0-9]+)`\\s*$");
		private static final Pattern CONST_CONT_PATTERN = Pattern.compile("^( {4})+[*0-9].*$");
		
		public static final List<PrimAsmConstant> ALL_CONSTANTS = primAsmConstants();
		
		private static List<PrimAsmConstant> primAsmConstants() {
			List<PrimAsmConstant> result = new ArrayList<>();
			try (Stream<String> lines = Files.lines(Path.of(PRIM_CODE_README), StandardCharsets.UTF_8)) {
				lines.forEachOrdered(new Consumer<String>() {
					
					private boolean activated = false;
					private boolean finish    = false;
					
					private String       name    = null;
					private String       header  = null;
					private long         value;
					private ValueType    valType = null;
					private List<String> docu    = null;
					
					@Override
					public void accept(String line) {
						if (this.finish) {/**/} else if (!this.activated) {
							Matcher matcher = ACTIVATION_PATTERN.matcher(line);
							if (matcher.matches()) {
								this.activated = true;
							}
						} else if (this.name == null) {
							Matcher matcher = CONST_START_PATTERN.matcher(line);
							if (!matcher.matches()) {
								throw new IllegalStateException("'" + line + "'");
							}
							this.name   = matcher.group(1);
							this.header = matcher.group(2);
						} else if (this.valType == null) {
							Matcher matcher = CONST_VAL_PATTERN.matcher(line);
							if (!matcher.matches()) { throw new IllegalStateException(line); }
							String val = matcher.group(1);
							if (val.startsWith("UHEX-")) {
								val = val.substring(5);
								switch (val.length()) {
								case 16 -> this.valType = ValueType.UHEX;
								case 8 -> this.valType = ValueType.UHEX_DWORD;
								default -> throw new IllegalStateException(line);
								}
								this.value = Long.parseUnsignedLong(val, 16);
							} else if (val.startsWith("HEX-")) {
								this.value   = Long.parseLong(val.substring(4), 16);
								this.valType = ValueType.HEX;
							} else if (val.startsWith("NHEX-")) {
								this.value   = Long.parseLong(val.substring(4), 16);
								this.valType = ValueType.NHEX;
							} else {
								this.value   = Long.parseLong(val);
								this.valType = ValueType.DECIMAL;
							}
							this.docu = new ArrayList<>();
						} else {
							Matcher matcher = CONST_CONT_PATTERN.matcher(line);
							if (!matcher.matches()) {
								result.add(new PrimAsmConstant(this.name, this.value, this.valType, this.header, Collections.unmodifiableList(this.docu)));
								this.name    = null;
								this.header  = null;
								this.value   = -1L;
								this.valType = null;
								this.docu    = null;
								if (line.isBlank()) {
									this.finish = true;
								} else {
									accept(line);
								}
							} else {
								this.docu.add(line.substring(4));
							}
						}
					}
					
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Collections.unmodifiableList(result);
		}
		
	}
	
}
