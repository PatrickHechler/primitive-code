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
	
	static final String PRIM_CODE_README = "/users/feri/git/primitive-code/README.md";
	
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
		private static final Pattern CMD_PATTERN = Pattern
				.compile("^`([A-Z_0-9]+)\\s*(<[A-Z_]+>)?\\s*(,\\s*<[A-Z_]+>)?\\s*(,\\s*<[A-Z_]+>)?`$");
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
	
}
