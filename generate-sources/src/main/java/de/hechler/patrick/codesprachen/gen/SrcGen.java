package de.hechler.patrick.codesprachen.gen;

import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface SrcGen {
	
	static final String BASE_DIR = "/home/pat/git/";
	
	static final String PRIM_CODE_README = BASE_DIR + "primitive-code/README.md";
	
	void generate(Writer out) throws IOException;
	
	enum ParamType {
		
		NO_CONST_PARAM, PARAM, CONST_PARAM, LABEL;
		
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
		
		DECIMAL, HEX, UHEX, UHEX_DWORD
	
	}
	
	static record PrimAsmConstant(String name, long value, ValueType valType, String header, List<String> docu) {
		
		private static final Pattern ACTIVATION_PATTERN = Pattern.compile("^###\\s*Predefined\\s*Constants\\s*$");
		private static final Pattern CONST_START_PATTERN = Pattern.compile("^\\*\\s*`([A-Z_]+)`\\s*:\\s*([^\\s].*[^\\s])\\s*$");
		private static final Pattern CONST_VAL_PATTERN = Pattern.compile("^ {4}\\*\\s*value\\s*:\\s*(U?HEX-[0-9A-F]+|-?[0-9]+)\\s*$");
		private static final Pattern CONST_CONT_PATTERN = Pattern.compile("^ {4}\\*\\s*([^\\s].*[^\\s])\\s*$");
		
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
								throw new IllegalStateException(line);
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
								docu.add(line);
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
