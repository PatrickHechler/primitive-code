//This file is part of the Patr File System and Code Projects
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
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("javadoc")
public interface SrcGen {
	
	static final String BASE_DIR           = initilizeBaseDir();
	static final String PRIMITIVE_CODE_DIR = BASE_DIR + "primitive-code/";
	static final String PATR_FILE_SYS_DIR  = BASE_DIR + "PatrFileSys/";
	static final String SIMPLE_CODE_DIR    = BASE_DIR + "simple-code/";
	static final String PRIM_CODE_README   = PRIMITIVE_CODE_DIR + "README.md";
	
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
				out.write("\n" + lineStart + listStart(line.charAt(4), stack) + '\n');
				int     deep            = 1;
				String  start           = "    *";
				boolean missingEntryEnd = false;
				while (true) {
					if (line.startsWith(start)) {/**/
					} else {
						String leadingWhite = line.substring(0, line.length() - line.stripLeading().length());
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
							out.write("\n" + lineStart + listStart(line.charAt(leadingWhite.length()), stack) + '\n');
							missingEntryEnd = false;
							deep++;
							start = "    " + start;
						} else {
							throw new IllegalStateException("'" + line + "'");
						}
						if (leadingWhite.length() != start.length() - 1 || !leadingWhite.matches("^ *$")) {
							throw new IllegalStateException("'" + line + "' start='" + start + "'");
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
				br = "<br>";
				if (!stack.isEmpty()) { throw new IllegalStateException("stack is not empty"); }
			}
		}
		return br;
	}
	
	static final Pattern MD_LIST_LINE = Pattern.compile("^( {4})+(\\*|[0-9]+.)\\s*(.*)$");
	
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
	
	static String listStart(char c, List<Boolean> stack) {
		if (c == '*') {
			stack.add(Boolean.FALSE);
			return "<ul>";
		} else if (c >= '0' && c <= '9') {
			stack.add(Boolean.TRUE);
			return "<ol>";
		} else {
			throw new IllegalStateException("'" + c + "'");
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
	
	static record PrimAsmReadmeCommand(String name, ParamType p1, ParamType p2, ParamType p3, int num, List<String> general,
			List<String> definition) {
		
		private static final Pattern ACTIVATION_PATTERN = Pattern.compile("^\\s*##\\s*COMMANDS\\s*$");
		private static final Pattern CMD_PATTERN = Pattern.compile("^`([A-Z_0-9]+)\\s*(<[A-Z_]+>)?\\s*(,\\s*<[A-Z_]+>)?\\s*(,\\s*<[A-Z_]+>)?`$");
		private static final Pattern DEFINITION_PATTERN = Pattern.compile("^\\s*\\*\\s*definition\\s*:\\s*$");
		private static final Pattern BINARY_PATTERN = Pattern.compile("^\\s*\\*\\s*binary\\s*:\\s*$");
		private static final Pattern NUM_PATTERN = Pattern.compile("^\\s*\\*\\s*`\\s*([0-9A-F][0-9A-F])\\s*([0-9A-F][0-9A-F]).*$");
		private static final Pattern IGNORE_PATTERN = Pattern.compile("^\\s*\\*\\s*`\\s*[<\\[].*$");
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
				lines.forEachOrdered(new Consumer<String>() {
					
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
						if (finish) {/**/} else if (!activated) {
							Matcher matcher = ACTIVATION_PATTERN.matcher(line);
							if (matcher.matches()) {
								activated = true;
							}
						} else if (line.isBlank()) {
							if (cmdName != null) { throw new IllegalStateException(cmdName); }
						} else if (cmdName == null) {
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
											} else {
												finish = true;
											}
										}
									}
								}
							} else {
								cmdName = matcher.group(1);
								String s1 = matcher.group(2);
								String s2 = matcher.group(3);
								String s3 = matcher.group(4);
								p1      = ParamType.val(s1);
								p2      = ParamType.val(s2);
								p3      = ParamType.val(s3);
								general = new ArrayList<>();
							}
						} else if (definition == null) {
							Matcher matcher = DEFINITION_PATTERN.matcher(line);
							if (matcher.matches()) {
								definition = new ArrayList<>();
							} else {
								general.add(line);
							}
						} else if (num == -1) {
							Matcher matcher = BINARY_PATTERN.matcher(line);
							if (matcher.matches()) {
								num = -2;
							} else {
								definition.add(line);
							}
						} else if (num == -2) {
							Matcher matcher = NUM_PATTERN.matcher(line);
							if (!matcher.matches()) { throw new IllegalStateException(line); }
							int lb = Integer.parseInt(matcher.group(2), 16);
							int hb = Integer.parseInt(matcher.group(1), 16);
							num = (hb << 8) | lb;
							PrimAsmReadmeCommand parc = new PrimAsmReadmeCommand(cmdName, p1, p2, p3, num, Collections.unmodifiableList(general),
									Collections.unmodifiableList(definition));
							result.add(parc);
							reset();
						}
					}
					
					
					private void reset() {
						if ((num & 0xFFFF) != num) { throw new IllegalStateException(); }
						cmdName    = null;
						p1         = null;
						p2         = null;
						p3         = null;
						num        = -1;
						general    = null;
						definition = null;
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
						if (finish) {/**/} else if (!activated) {
							Matcher matcher = ACTIVATION_PATTERN.matcher(line);
							if (matcher.matches()) {
								activated = true;
							}
						} else if (name == null) {
							Matcher matcher = CONST_START_PATTERN.matcher(line);
							if (!matcher.matches()) {
								throw new IllegalStateException("'" + line + "'");
							} else {
								name   = matcher.group(1);
								header = matcher.group(2);
							}
						} else if (valType == null) {
							Matcher matcher = CONST_VAL_PATTERN.matcher(line);
							if (!matcher.matches()) { throw new IllegalStateException(line); }
							String val = matcher.group(1);
							if (val.startsWith("UHEX-")) {
								val = val.substring(5);
								switch (val.length()) {
								case 16 -> valType = ValueType.UHEX;
								case 8 -> valType = ValueType.UHEX_DWORD;
								default -> throw new IllegalStateException(line);
								}
								value = Long.parseUnsignedLong(val, 16);
							} else if (val.startsWith("HEX-")) {
								value   = Long.parseLong(val.substring(4), 16);
								valType = ValueType.HEX;
							} else if (val.startsWith("NHEX-")) {
								value   = Long.parseLong(val.substring(4), 16);
								valType = ValueType.NHEX;
							} else {
								value   = Long.parseLong(val);
								valType = ValueType.DECIMAL;
							}
							docu = new ArrayList<>();
						} else {
							Matcher matcher = CONST_CONT_PATTERN.matcher(line);
							if (!matcher.matches()) {
								result.add(new PrimAsmConstant(name, value, valType, header, Collections.unmodifiableList(docu)));
								name    = null;
								header  = null;
								value   = -1L;
								valType = null;
								docu    = null;
								if (line.isBlank()) {
									finish = true;
								} else {
									accept(line);
								}
							} else {
								docu.add(line.substring(4));
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
