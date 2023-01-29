package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import de.hechler.patrick.codesprachen.gen.SrcGen;

public class GenCorePrimAsmCmds implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		for (PrimAsmReadmeCommand cmd : SrcGen.PrimAsmReadmeCommand.ALL_CMDS) {
			out.write("\t/**\n");
			out.write("\t * <b>" + cmd.name() + "</b> <code>(" + word(cmd.num(), ' ') + ")</code><br>\n");
			writeParams(out, cmd);
			out.write("\t * ");
			writeLines(out, "<p>", cmd.general());
			out.write("\t * <p>\n");
			out.write("\t * <b>definition:</b>");
			writeLines(out, "<br>", cmd.definition());
			out.write("\t */\n");
			out.write("\tpublic static final int " + cmd.name());
			out.write("        ".substring(cmd.name().length()) + "= 0x" + word(cmd.num()) + ";\n");
		}
	}
	
	private static void writeParams(Writer out, PrimAsmReadmeCommand cmd) throws IOException {
		if (cmd.p1() != null) {
			out.write("\t * Parameter: <code>&lt;" + cmd.p1() + "&gt;");
			if (cmd.p2() != null) {
				out.write(" , &lt;" + cmd.p2() + "&gt;");
				if (cmd.p3() != null) {
					out.write(" , &lt;" + cmd.p3() + "&gt;");
				}
			}
			out.write("</code>\n");
		} else {
			out.write("\t * <i>Parameter: none</i>\n");
		}
	}
	
	private static void writeLines(Writer out, String firstBreak, List<String> lines) throws IOException {
		String br = firstBreak;
		for (Iterator<String> iter = lines.iterator(); iter.hasNext();) {
			String line = iter.next();
			if (line.charAt(0) == '*') {
				out.write(br + "\n\t " + mdToJavadoc(line));
				br = "<br>";
			} else {
				out.write("\n\t * <ul>\n");
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
								out.write("\t * </ul></li>\n");
								deep--;
								start = start.substring(4);
							} while (leadingWhite.length() < start.length() - 1);
						} else if (leadingWhite.length() > start.length() - 1) {
							if (!missingEntryEnd) { throw new IllegalStateException(); }
							out.write("\n\t * <ul>\n");
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
					out.write("\t * <li>" + mdToJavadoc(line.substring(line.indexOf('*') + 1).trim()));
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
					out.write("\t * </ul></li>\n");
				}
				if (line != null) {
					out.write("\t * <li>" + mdToJavadoc(line.substring(1).trim()));
					out.write("</li>\n");
				}
				out.write("\t * </ul>");
				br = "";
			}
		}
		out.write(br + "\n");
		
	}
	
	private static String mdToJavadoc(String md) {
		String javadoc = md.replace("&", "&amp;");
		javadoc = javadoc.replace(">", "&gt;").replace("<", "&lt;");
		javadoc = javadoc.replaceAll("`([^`]*)`", "<code>$1</code>");
		javadoc = javadoc.replaceAll("(\\s)___([^_]*)___(\\s)", "$1<b><i>$2</i></b>$3");
		javadoc = javadoc.replaceAll("(\\s)__([^_]*)__(\\s)", "$1<b>$2</b>$3");
		javadoc = javadoc.replaceAll("(\\s)_([^_]*)_(\\s)", "$1<i>$2</i>$3");
		javadoc = javadoc.replace("[predefined constant](#predefined-constants)", "{@link PrimAsmPreDefines predefined constant}");
		return javadoc;
	}
	
	private static String word(int word, char between) {
		return byteHex(word >>> 8) + between + byteHex(word);
	}
	
	private static String word(int word) {
		return byteHex(word >>> 8) + byteHex(word);
	}
	
	private static String byteHex(int val) {
		String str = Integer.toHexString(0xFF & val);
		if (str.length() == 1) { return "0" + str; }
		return str;
	}
	
}
