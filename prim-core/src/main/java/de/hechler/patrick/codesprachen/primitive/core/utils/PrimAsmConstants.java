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
package de.hechler.patrick.codesprachen.primitive.core.utils;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;

@SuppressWarnings("javadoc")
public class PrimAsmConstants {
	
	private PrimAsmConstants() {}
	
	public static final Path START_CONSTANTS_PATH = Paths.get("[START_CONSTANTS]");
	
	public static final Map<String, PrimitiveConstant> START_CONSTANTS;
	
	static {
		Map<String, PrimitiveConstant> startConsts = new LinkedHashMap<>();
		try (InputStream in = PrimAsmConstants.class.getResourceAsStream("/de/hechler/patrick/codesprachen/primitive/core/predefined-constants.psf")) {
			try (Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {
				readSymbols(null, startConsts, sc, START_CONSTANTS_PATH);
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
		START_CONSTANTS = Collections.unmodifiableMap(startConsts);
		
		for (Field field : PrimAsmPreDefines.class.getFields()) {
			try {
				long              val       = field.getLong(null);
				PrimitiveConstant primConst = startConsts.get(field.getName());
				if (primConst == null) {
					throw new AssertionError("validation error: primConst=null field: " + field.getName() + " (" + val + ")");
				}
				if (primConst.value() != val) {
					throw new AssertionError("validation error: field: " + field.getName() + "=" + val + " primConst.val=" + primConst.value() + " (comment):\n"
							+ primConst.comment());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
			}
		}
	}
	
	public static final long MAX_STD_STREAM = PrimAsmPreDefines.STD_LOG;
	
	public static final int IP     = 0;
	public static final int SP     = 1;
	public static final int STATUS = 2;
	public static final int INTCNT = 3;
	public static final int INTP   = 4;
	public static final int ERRNO  = 5;
	public static final int X_ADD  = 6;
	
	public static final int PARAM_BASE  = 0x01;
	public static final int PARAM_A_NUM = 0x02;
	public static final int PARAM_A_REG = 0x04;
	public static final int PARAM_NO_B  = 0x10;
	public static final int PARAM_B_NUM = 0x20;
	public static final int PARAM_B_REG = 0x40;
	public static final int PARAM_B_ADR = 0x80;
	
	public static final int PARAM_ART_ANUM      = PARAM_BASE | PARAM_A_NUM | PARAM_NO_B;
	public static final int PARAM_ART_AREG      = PARAM_BASE | PARAM_A_REG | PARAM_NO_B;
	public static final int PARAM_ART_ANUM_BNUM = PARAM_BASE | PARAM_A_NUM | PARAM_B_NUM;
	public static final int PARAM_ART_AREG_BNUM = PARAM_BASE | PARAM_A_REG | PARAM_B_NUM;
	public static final int PARAM_ART_ANUM_BREG = PARAM_BASE | PARAM_A_NUM | PARAM_B_REG;
	public static final int PARAM_ART_AREG_BREG = PARAM_BASE | PARAM_A_REG | PARAM_B_REG;
	public static final int PARAM_ART_ANUM_BADR = PARAM_BASE | PARAM_A_NUM | PARAM_B_ADR;
	public static final int PARAM_ART_AREG_BADR = PARAM_BASE | PARAM_A_REG | PARAM_B_ADR;
	
	private static final class ExportIOExcep extends RuntimeException {
		
		private static final long serialVersionUID = 248886119080373208L;
		
		private ExportIOExcep(IOException cause) {
			super(cause);
		}
		
	}
	
	public static void export(Map<String, PrimitiveConstant> exports, Appendable out) throws IOException {
		try {
			exports.forEach((symbol, pc) -> {
				try {
					assert symbol.equals(pc.name());
					if (pc.comment() != null) {
						for (String line : pc.comment().split("\r\n?|\n")) {
							if (!line.matches("\\s*\\|.*")) {
								line = "|" + line;
							}
							line = line.trim();
							out.append(line + '\n');
						}
					}
					out.append(symbol + "=UHEX-" + Long.toUnsignedString(pc.value(), 16).toUpperCase() + '\n');
				} catch (IOException e) {
					throw new ExportIOExcep(e);
				}
			});
		} catch (ExportIOExcep e) {
			throw (IOException) e.getCause();
		}
	}
	
	private static final String REGEX = "^\\#?(\\w+)\\s*\\=\\s*(([UN]?(HEX\\-|BIN\\-|OCT\\-|DEC\\-)|\\-)?[0-9a-fA-F]+)$";
	
	public static void readSymbols(String prefix, Map<String, PrimitiveConstant> addSymbols, Scanner sc, Path path) {
		final Pattern pattern    = Pattern.compile(REGEX);
		StringBuilder comment    = new StringBuilder();
		int           lineNumber = 1;
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.charAt(0) == '|') {
				if (line.length() <= 1 || line.charAt(1) != '|') {
					comment.append(line).append('\n');
				}
				continue;
			}
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalStateException("line does not match regex: line='" + line + "', regex='" + REGEX + "'");
			}
			String            constName = matcher.group(1);
			String            strVal    = matcher.group(2);
			long              val       = parseNum(strVal);
			PrimitiveConstant value;
			if (comment.length() == 0) {
				value = new PrimitiveConstant(constName, null, val, path, lineNumber);
			} else {
				value   = new PrimitiveConstant(constName, comment.toString(), val, path, lineNumber);
				comment = new StringBuilder();
			}
			if (prefix == null) {
				addSymbols.put(constName, value);
			} else {
				addSymbols.put(prefix + constName, value);
			}
			lineNumber++;
		}
	}
	
	private static long parseNum(String strVal) {
		long val;
		if (strVal.startsWith("UHEX-")) {
			val = Long.parseUnsignedLong(strVal.substring(5), 16);
		} else if (strVal.startsWith("HEX-")) {
			val = Long.parseLong(strVal.substring(4), 16);
		} else if (strVal.startsWith("NHEX-")) {
			val = Long.parseLong(strVal.substring(4), 16);
		} else if (strVal.startsWith("UDEC-")) {
			val = Long.parseUnsignedLong(strVal.substring(5), 10);
		} else if (strVal.startsWith("DEC-")) {
			val = Long.parseLong(strVal.substring(4), 10);
		} else if (strVal.startsWith("NDEC-")) {
			val = Long.parseLong(strVal.substring(4), 10);
		} else if (strVal.startsWith("UOCT-")) {
			val = Long.parseUnsignedLong(strVal.substring(5), 8);
		} else if (strVal.startsWith("OCT-")) {
			val = Long.parseLong(strVal.substring(4), 8);
		} else if (strVal.startsWith("NOCT-")) {
			val = Long.parseLong(strVal.substring(4), 8);
		} else if (strVal.startsWith("UBIN-")) {
			val = Long.parseUnsignedLong(strVal.substring(5), 2);
		} else if (strVal.startsWith("BIN-")) {
			val = Long.parseLong(strVal.substring(4), 2);
		} else if (strVal.startsWith("NBIN-")) {
			val = Long.parseLong(strVal.substring(4), 2);
		} else {
			val = Long.parseLong(strVal, 10);
		}
		return val;
	}
	
}
