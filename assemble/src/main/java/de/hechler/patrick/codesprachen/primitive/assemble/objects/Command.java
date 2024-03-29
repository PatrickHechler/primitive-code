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
package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import java.util.Map;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;

public class Command {
	
	public final Commands cmd;
	public final Param    p1;
	public final Param    p2;
	public final Param    p3;
	
	public Command(Commands cmd, Param p1, Param p2) {
		this(cmd, p1, p2, null);
	}
	
	public Command(Commands cmd, Param p1, Param p2, Param p3) {
		this.cmd = cmd;
		this.p1  = p1;
		this.p2  = p2;
		this.p3  = p3;
	}
	
	public static ConstsContext parseCP(String cp, Map<String, PrimitiveConstant> constants, Map<String, Long> labels, long pos, boolean align, int line,
		int posInLine, int charPos, boolean bailError, ANTLRErrorStrategy errorHandler, ANTLRErrorListener errorListener) {
		ANTLRInputStream          in     = new ANTLRInputStream(cp);
		ConstantPoolGrammarLexer  lexer  = new ConstantPoolGrammarLexer(in);
		CommonTokenStream         tokens = new CommonTokenStream(lexer);
		ConstantPoolGrammarParser parser = new ConstantPoolGrammarParser(tokens);
		if (errorHandler != null) {
			parser.setErrorHandler(errorHandler);
		}
		if (errorListener != null) {
			parser.addErrorListener(errorListener);
		}
		try {
			ConstsContext constantPool = parser.consts(constants, labels, pos, align, bailError);
			return constantPool;
		} catch (AssembleRuntimeException ae) {
			assert false;
			AssembleRuntimeException err = new AssembleRuntimeException(line + ae.line, ae.line == 0 ? ae.posInLine + posInLine : ae.posInLine, ae.length,
				charPos + ae.charPos, ae.getMessage());
			err.setStackTrace(ae.getStackTrace());
			throw err;
		} catch (AssembleError ae) {
			assert bailError;
			AssembleError err = new AssembleError(line, posInLine, ae.length, ae.charPos, ae.getMessage());
			err.setStackTrace(ae.getStackTrace());
			throw err;
		} catch (ParseCancellationException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AssembleError) {
				AssembleError ae  = (AssembleError) cause;
				AssembleError err = new AssembleError(line + ae.line, ae.posInLine + (ae.length == 0 ? posInLine : 0), ae.length, ae.charPos, ae.getMessage());
				err.setStackTrace(ae.getStackTrace());
				throw err;
			} else if (cause instanceof NoViableAltException) {
				NoViableAltException nvae  = (NoViableAltException) cause;
				Token                ot    = nvae.getOffendingToken();
				String[]             names = parser.getTokenNames();
				StringBuilder        msg   = new StringBuilder("illegal token: ").append(ot).append("\nexpected: [");
				IntervalSet          ets   = nvae.getExpectedTokens();
				for (int i = 0; i < ets.size(); i++) {
					if (i > 0) {
						msg.append(", ");
					}
					int t = ets.get(i);
					msg.append('<').append(names[t]).append('>');
				}
				int lineAdd = ot.getLine();
				throw new AssembleError(line + lineAdd, lineAdd == 0 ? posInLine + ot.getCharPositionInLine() : ot.getCharPositionInLine(),
					ot.getStopIndex() - ot.getStartIndex() + 1, ot.getStartIndex(), msg.append(']').toString());
			} else {
				throw new AssembleError(line, posInLine, 1, charPos, e.getClass().getName() + ": " + e.getMessage(), e);
			}
		}
	}
	
	public static AssembleRuntimeException getConvertedCP_ARE(AssembleRuntimeException are, int line, int posInLine, int charPos) {
		if (are == null) {
			return null;
		} else {
			AssembleRuntimeException result = new AssembleRuntimeException(are.line + line, are.line == 0 ? are.posInLine : are.posInLine + posInLine, are.length,
				charPos + are.charPos, are.getMessage(), are.getCause());
			Throwable[]              sup    = are.getSuppressed();
			if (sup != null) {
				for (Throwable s : sup) {
					if (!(s instanceof AssembleRuntimeException)) {
						result.addSuppressed(getConvertedCP_ARE((AssembleRuntimeException) s, line, posInLine, charPos));
					}
				}
			}
			return result;
		}
	}
	
	public long length() {
		switch (cmd.params) {
		case 2:
			int len;
			switch (p1.art) {
			case Param.ART_ANUM_BNUM:
				len = 24;
				break;
			case Param.ART_ANUM:
			case Param.ART_AREG_BNUM:
			case Param.ART_ANUM_BADR:
			case Param.ART_ANUM_BREG:
				len = 16;
				break;
			case Param.ART_AREG:
			case Param.ART_AREG_BADR:
			case Param.ART_AREG_BREG:
				len = 8;
				break;
			default:
				throw new IllegalStateException("unknown art: " + Param.artToString(p1.art) + " " + this);
			}
			if (p2.label != null) {
				len += 8;
			} else {
				switch (p2.art) {
				case Param.ART_ANUM_BNUM:
					len += 16;
				case Param.ART_ANUM:
				case Param.ART_AREG_BNUM:
				case Param.ART_ANUM_BADR:
				case Param.ART_ANUM_BREG:
					len += 8;
					break;
				case Param.ART_AREG:
				case Param.ART_AREG_BADR:
				case Param.ART_AREG_BREG:
					break;
				default:
					throw new IllegalStateException("unknown art: " + Param.artToString(p2.art) + " " + this);
				}
			}
			return len;
		case 1:
			if (p1.label != null) {
				return 8;
			} else {
				switch (p1.art) {
				case Param.ART_ANUM_BNUM:
					return 24;
				case Param.ART_ANUM:
					switch (cmd) {
					case CMD_CALL, CMD_JMP, CMD_JMPAB, CMD_JMPAN, CMD_JMPCC, CMD_JMPCS, CMD_JMPEQ, CMD_JMPERR, CMD_JMPGE, CMD_JMPGT, CMD_JMPLE, CMD_JMPLT,
						CMD_JMPNAN, CMD_JMPNB, CMD_JMPNE, CMD_JMPSB, CMD_JMPZC, CMD_JMPZS:
						return 8;
					// $CASES-OMITTED$
					default:
						return 16;
					}
				case Param.ART_AREG_BNUM:
				case Param.ART_ANUM_BADR:
				case Param.ART_ANUM_BREG:
					return 16;
				case Param.ART_AREG:
				case Param.ART_AREG_BADR:
				case Param.ART_AREG_BREG:
					return 8;
				default:
					throw new IllegalStateException("unknown art: " + Param.artToString(p1.art));
				}
			}
		case 0:
			return 8;
		case 3:
			if (p3.art != Param.ART_ANUM) {
				throw new IllegalStateException("unknown art for p3: " + Param.artToString(p3.art) + " (p3 is only allowed to be a constant number)");
			}
			switch (p1.art) {
			case Param.ART_ANUM_BNUM:
				len = 32;
				break;
			case Param.ART_ANUM:
			case Param.ART_AREG_BNUM:
			case Param.ART_ANUM_BADR:
			case Param.ART_ANUM_BREG:
				len = 24;
				break;
			case Param.ART_AREG:
			case Param.ART_AREG_BADR:
			case Param.ART_AREG_BREG:
				len = 16;
				break;
			default:
				throw new IllegalStateException("unknown art: " + Param.artToString(p1.art));
			}
			switch (p2.art) {
			case Param.ART_ANUM_BNUM:
				return len + 16;
			case Param.ART_ANUM:
			case Param.ART_AREG_BNUM:
			case Param.ART_ANUM_BADR:
			case Param.ART_ANUM_BREG:
				return len + 8;
			case Param.ART_AREG:
			case Param.ART_AREG_BADR:
			case Param.ART_AREG_BREG:
				return len;
			default:
				throw new IllegalStateException("unknown art: " + Param.artToString(p1.art));
			}
		default:
			throw new AssertionError("unknown enum constant of my Command: " + cmd.name());
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(cmd);
		if (p1 != null) {
			b.append(' ');
			b.append(p1);
			if (p2 != null) {
				b.append(", ");
				b.append(p2);
				if (p3 != null) {
					b.append(", ");
					b.append(p3);
				}
			}
		}
		return b.toString();
	}
	
}
