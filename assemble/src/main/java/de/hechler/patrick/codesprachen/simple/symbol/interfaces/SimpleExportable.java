//This file is part of the Primitive Code Project
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
package de.hechler.patrick.codesprachen.simple.symbol.interfaces;

import java.io.IOError;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;
import de.hechler.patrick.codesprachen.simple.symbol.SimpleExportGrammarLexer;
import de.hechler.patrick.codesprachen.simple.symbol.SimpleExportGrammarParser;
import de.hechler.patrick.codesprachen.simple.symbol.SimpleExportGrammarParser.SimpleExportsContext;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleConstant;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleFunctionSymbol;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable.SimpleOffsetVariable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleFuncType;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleStructType;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleType;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleTypeArray;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleTypePointer;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleTypePrimitive;

/**
 * a simple whatever which can be exported
 * 
 * @author pat
 */
public interface SimpleExportable extends SimpleNameable {
	
	static final char UNKNOWN_SIZE_ARRAY = ']';
	static final char ARRAY              = '[';
	static final char POINTER            = '#';
	
	static final String PRIM_FPNUM  = ".fp";
	static final String PRIM_NUM    = ".n";
	static final String PRIM_UNUM   = ".un";
	static final String PRIM_DWORD  = ".dw";
	static final String PRIM_UDWORD = ".udw";
	static final String PRIM_WORD   = ".w";
	static final String PRIM_UWORD  = ".uw";
	static final String PRIM_BYTE   = ".b";
	static final String PRIM_UBYTE  = ".ub";
	
	static final String FUNC   = "~f";
	static final String FUNC2  = "~F";
	static final String VAR    = "~v";
	static final String STRUCT = "~s";
	static final String CONST  = "~c";
	
	static final char NAME_TYPE_SEP = ':';
	static final char VAR_SEP       = ',';
	
	/**
	 * returns <code>true</code> if this object is marked as export and <code>false</code> if not
	 * 
	 * @return <code>true</code> if this object is marked as export and <code>false</code> if not
	 */
	boolean isExport();
	
	/**
	 * convert this {@link SimpleExportable} to an export {@link String}
	 * 
	 * @return this {@link SimpleExportable} converted to an export {@link String}
	 * 
	 * @throws IllegalStateException if this {@link SimpleExportable} is not marked as {@link #isExport() exportable}
	 */
	String toExportString() throws IllegalStateException;
	
	SimpleExportable changeRelative(Object relative);
	
	public static SimpleExportable[] correctImports(Map<String, SimpleStructType> structs, List<SimpleExportable> imps) {
		int                impcnt = imps.size();
		SimpleExportable[] result = new SimpleExportable[impcnt];
		for (int i = 0; i < impcnt; i++) {
			SimpleExportable se = imps.get(i);
			switch (se) {
			case @SuppressWarnings("preview") SimpleConstant sc -> {/* nothing to do */}
			case @SuppressWarnings("preview") SimpleFunctionSymbol sf -> {
				ImportHelp.correctArray(structs, sf.type.arguments);
				ImportHelp.correctArray(structs, sf.type.results);
			}
			case @SuppressWarnings("preview") SimpleOffsetVariable sv -> {
				SimpleType corrected = ImportHelp.correctType(structs, sv.type);
				if (corrected != sv.type) {
					se = new SimpleOffsetVariable(sv.offset(), sv.relative(), corrected, sv.name, true);
				}
			}
			case @SuppressWarnings("preview") SimpleFutureStructType fst -> {
				se = structs.get(fst.name);
			}
			case @SuppressWarnings("preview") SimpleStructType st -> {/* nothing to do */}
			default -> throw new AssertionError("unknown exportable class: " + se.getClass().getName());
			}
			result[i] = se;
		}
		return result;
	}
	
	static class ImportHelp {
		
		
		private ImportHelp() {}
		
		public static final String VAR_PREFIX        = "VAR_";
		public static final String STRUCT_PREFIX     = "STRUCT_";
		public static final String FUNC_PREFIX       = "FUNC_";
		public static final String CONST_PREFIX      = "CONST_";
		public static final String DEPENDENCY_PREFIX = "DEP_";
		
		private static SimpleType correctType(Map<String, SimpleStructType> structs, SimpleType type) {
			if (type instanceof SimpleFuncType sft) {
				correctArray(structs, sft.arguments);
				correctArray(structs, sft.results);
			} else if (type instanceof SimpleTypePointer p) {
				SimpleType corrected = correctType(structs, p.target);
				if (corrected != p.target) {
					if (p instanceof SimpleTypeArray arr) {
						return new SimpleTypeArray(corrected, arr.elementCount);
					} else {
						return new SimpleTypePointer(corrected);
					}
				}
			} else if (type instanceof SimpleFutureStructType sfst) {
				SimpleStructType res = structs.get(sfst.name);
				if (res == null) {
					throw new NoSuchElementException(
						"the needed structure was not exported! (name='" + ((SimpleFutureStructType) type).name + "') (exported structs: " + structs + ")");
				}
				return res;
			} else if (type instanceof SimpleStructType) {
				throw new InternalError("simple struct type is not allowed here (should possibly be a SimpleFutureStructType)");
			} else if (!(type instanceof SimpleTypePrimitive)) { throw new InternalError("unknown type class: " + type.getClass().getName()); }
			return type;
		}
		
		private static void correctArray(Map<String, SimpleStructType> structs, SimpleOffsetVariable[] results) {
			for (int i = 0; i < results.length; i++) {
				SimpleOffsetVariable sv        = results[i];
				SimpleType           corrected = correctType(structs, sv.type);
				if (corrected != sv.type) {
					results[i] = new SimpleOffsetVariable(sv.offset(), sv.relative(), corrected, sv.name, true);
				}
			}
		}
		
		public static void convertConst(Map<String, PrimitiveConstant> result, String prefix, SimpleConstant sc, Path path) {
			String start = CONST_PREFIX;
			if (prefix != null) {
				start = prefix + start;
			}
			PrimitiveConstant pc = new PrimitiveConstant(start + sc.name(), sc.toString(), sc.value(), path, -1);
			checkedPut(result, pc);
		}
		
		public static void convertFunc(Map<String, PrimitiveConstant> result, String prefix, SimpleFunctionSymbol sf, Path path, boolean addFunc) {
			String start = FUNC_PREFIX + sf.name;
			if (prefix != null) {
				start = prefix + start;
			}
			if (addFunc) {
				PrimitiveConstant pc = new PrimitiveConstant(start, sf.toString(), sf.address(), path, -1);
				checkedPut(result, pc);
			}
			String argStart = start + "_ARG_";
			for (SimpleOffsetVariable sv : sf.type.arguments) {
				PrimitiveConstant pc = new PrimitiveConstant(argStart + sv.name, sv.toString(), sv.offset(), path, -1);
				checkedPut(result, pc);
			}
			String resStart = start + "_RES_";
			for (SimpleOffsetVariable sv : sf.type.results) {
				PrimitiveConstant pc = new PrimitiveConstant(resStart + sv.name, sv.toString(), sv.offset(), path, -1);
				checkedPut(result, pc);
			}
		}
		
		public static void convertStrut(Map<String, PrimitiveConstant> result, String prefix, SimpleStructType ss, Path path) {
			String start = STRUCT_PREFIX + ss.name;
			if (prefix != null) {
				start = prefix + start;
			}
			PrimitiveConstant pc = new PrimitiveConstant(start + "_SIZE", ss.toString(), ss.byteCount(), path, -1);
			checkedPut(result, pc);
			start += "_OFFSET_";
			for (SimpleOffsetVariable sv : ss.members) {
				pc = new PrimitiveConstant(start + sv.name, sv.toString(), sv.offset(), null, -1);
				checkedPut(result, pc);
			}
		}
		
		public static void convertVar(Map<String, PrimitiveConstant> result, String prefix, SimpleOffsetVariable sv, Path path) {
			String start = VAR_PREFIX;
			if (prefix != null) {
				start = prefix + start;
			}
			PrimitiveConstant pc = new PrimitiveConstant(start + sv.name, sv.type.toString(), sv.offset(), path, -1);
			checkedPut(result, pc);
		}
		
		static void checkedPut(Map<String, PrimitiveConstant> result, PrimitiveConstant pc) {
			if (result.put(pc.name(), pc) != null) {
				throw new IllegalStateException("multiple exports whould get the same name when converting to primitive constants");
			}
		}
		
	}
	
	static class SimpleFutureStructType implements SimpleType {
		
		public final String name;
		
		public SimpleFutureStructType(String name) {
			this.name = name;
		}
		
		//@formatter:off
		@Override public boolean isStruct() { return true; }
		@Override public boolean isPrimitive() { return false; }
		@Override public boolean isPointerOrArray() { return false; }
		@Override public boolean isPointer() { return false; }
		@Override public boolean isArray() { return false; }
		@Override public boolean isFunc() { return false; }
		@Override public long    byteCount() { throw new UnsupportedOperationException(); }
		@Override public void    appendToExportStr(@SuppressWarnings("unused") StringBuilder build) { throw new UnsupportedOperationException(); }
		//@formatter:on
		
		@Override
		public String toString() {
			return "struct " + this.name;
		}
		
	}
	
	static void exportVars(StringBuilder build, SimpleVariable... arr) {
		boolean first = true;
		for (SimpleVariable sv : arr) {
			if (!first) {
				build.append(VAR_SEP);
			}
			first = false;
			build.append(sv.name).append(NAME_TYPE_SEP);
			sv.type.appendToExportStr(build);
		}
	}
	
	static Map<String, SimpleExportable> readExports(Object relative, Reader r) {
		try {
			ANTLRInputStream in = new ANTLRInputStream();
			in.load(r, 1024, 1024);
			Lexer                     lexer  = new SimpleExportGrammarLexer(in);
			CommonTokenStream         toks   = new CommonTokenStream(lexer);
			SimpleExportGrammarParser parser = new SimpleExportGrammarParser(toks);
			parser.setErrorHandler(new BailErrorStrategy());
			SimpleExportsContext          sec    = parser.simpleExports(relative);
			SimpleExportable[]            se     = sec.imported;
			Map<String, SimpleExportable> result = new HashMap<>(se.length);
			for (int i = 0; i < se.length; i++) {
				result.put(se[i].name(), se[i]);
			}
			return result;
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	static void toPrimConsts(Map<String, PrimitiveConstant> addSymbols, String prefix, Map<String, SimpleExportable> imps, Path path) {
		for (SimpleExportable imp : imps.values()) {
			if (imp instanceof SimpleConstant sc) {
				ImportHelp.convertConst(addSymbols, prefix, sc, path);
			} else if (imp instanceof SimpleFunctionSymbol sf) {
				ImportHelp.convertFunc(addSymbols, prefix, sf, path, true);
			} else if (imp instanceof SimpleStructType ss) {
				ImportHelp.convertStrut(addSymbols, prefix, ss, path);
			} else if (imp instanceof SimpleOffsetVariable sv) {
				ImportHelp.convertVar(addSymbols, prefix, sv, path);
			} else {
				throw new AssertionError(imp.getClass() + " : " + imp);
			}
		}
	}
	
}
