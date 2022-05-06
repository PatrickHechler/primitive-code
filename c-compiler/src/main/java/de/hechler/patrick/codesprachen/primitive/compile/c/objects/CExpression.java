package de.hechler.patrick.codesprachen.primitive.compile.c.objects;

import java.util.List;

public class CExpression {

	public static CExpression createGeneric(CompilationUnit cu, CExpression eval, List <CType> types, List <CExpression> exps) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createVaStart(CompilationUnit cu, String text, String varname) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createVaEnd(CompilationUnit cu, String text) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createVaArg(CompilationUnit cu, String text, CType ct) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createOffsetOf(CompilationUnit cu, CType ct, String varname) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createFunc(CompilationUnit cu, CExpression exp, List <CExpression> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createVar(CompilationUnit cu, CVariable cvar) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createString(CompilationUnit cu, List <String> origstrs) {
		// TODO Auto-generated method stub
		return null;
	}

	public static CExpression createConstant(CompilationUnit cu, String numtext) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
