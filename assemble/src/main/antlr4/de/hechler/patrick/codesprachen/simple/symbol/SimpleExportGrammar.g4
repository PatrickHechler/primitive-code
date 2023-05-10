 grammar SimpleExportGrammar;

 @parser::header {
import java.util.function.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import de.hechler.patrick.codesprachen.simple.symbol.objects.*;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable.*;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.*;
import de.hechler.patrick.codesprachen.simple.symbol.interfaces.*;
import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.*;
}

simpleExports [Object relative] returns [SimpleExportable[] imported] :
	{
		Map<String, SimpleStructType> structs = new HashMap<>();
		List<SimpleExportable> imps = new ArrayList<>();
	}
	(
		export [relative, structs]
		{imps.add($export.imp);}
		( LINE | EOF )
	)*
	{$imported = SimpleExportable.correctImports(structs, imps);}
	EOF
;

export [Object relative, Map<String, SimpleStructType> structs] returns [SimpleExportable imp] :
	constExport
	{$imp = $constExport.imp;}
	|
	structExport
	{
		Object obj = structs.put($structExport.imp.name, $structExport.imp);
		assert obj == null : "old: '" + obj + "' new: '" + $structExport.imp + '\'';
		$imp = $structExport.imp;
	}
	|
	varExport [relative]
	{$imp = $varExport.imp;}
	|
	functionExport [relative]
	{$imp = $functionExport.imp;}
;

structExport returns [SimpleStructType imp]:
	STRUCT NAME_OR_NUMBER STRUCT varList STRUCT
	{$imp = new SimpleStructType($NAME_OR_NUMBER.getText(), true, $varList.list);}
;

varList returns [List<SimpleOffsetVariable> list]:
	{$list = new ArrayList<>();}
	(
		variable [null, null]
		{$list.add($variable.v);}
		(
			VAR_SEP
			variable [null, null]
			{$list.add($variable.v);}
		)*
	)?
;

constExport returns [SimpleExportable imp] :
	CONST name = NAME_OR_NUMBER CONST number = NAME_OR_NUMBER 
	{
		long num = Long.parseUnsignedLong($number.getText(), 16);
		$imp = new SimpleConstant($name.getText(), num, true);
	}
;

varExport [Object relative] returns [SimpleExportable imp] :
	VAR NAME_OR_NUMBER VAR variable [relative, $NAME_OR_NUMBER.getText()]
	{$imp = $variable.v;}
;

functionExport [Object relative] returns [SimpleExportable imp] :
	FUNC addr = NAME_OR_NUMBER
	FUNC name = NAME_OR_NUMBER
	functionType [relative]
	{$imp = new SimpleFunctionSymbol(Long.parseUnsignedLong($addr.getText(), 16), relative, $name.getText(), (SimpleFuncType) $functionType.t);}
;

variable [Object relative, String number] returns [SimpleOffsetVariable v] :
	NAME_OR_NUMBER NAME_TYPE_SEP
	type [relative]
	{
		if (number == null) {
			$v = new SimpleOffsetVariable($type.t, $NAME_OR_NUMBER.getText(), false);
		} else {
			$v = new SimpleOffsetVariable(Long.parseUnsignedLong(number, 16), relative, $type.t, $NAME_OR_NUMBER.getText(), true);
		}
	}
;

type [Object relative] returns [SimpleType t] :
	(
		primType
		{$t = $primType.t;}
		|
		structType
		{$t = $structType.t;}
		|
		functionType[relative]
		{$t = $functionType.t;}
	)
	(
		POINTER
		{$t = new SimpleTypePointer($t);}
		|
		ARRAY NAME_OR_NUMBER ARRAY
		{$t = new SimpleTypeArray($t, Integer.parseUnsignedInt($NAME_OR_NUMBER.getText(), 16));}
		|
		UNKNOWN_SIZE_ARRAY
		{$t = new SimpleTypeArray($t, -1);}
	)*
;

functionType [Object relative] returns [SimpleType t] :
	FUNC args = varList
	FUNC2 res = varList
	FUNC
	{$t = new SimpleFuncType($args.list, $res.list);}
;

structType returns [SimpleType t] :
	NAME_OR_NUMBER
	{$t = new SimpleFutureStructType($NAME_OR_NUMBER.getText());}
;

primType returns [SimpleType t] :
	PRIM_FPNUM
	{$t = SimpleType.NUM;}
	|
	PRIM_NUM
	{$t = SimpleType.NUM;}
	|
	PRIM_UNUM
	{$t = SimpleType.UNUM;}
	|
	PRIM_DWORD
	{$t = SimpleType.DWORD;}
	|
	PRIM_UDWORD
	{$t = SimpleType.UDWORD;}
	|
	PRIM_WORD
	{$t = SimpleType.WORD;}
	|
	PRIM_UWORD
	{$t = SimpleType.WORD;}
	|
	PRIM_BYTE
	{$t = SimpleType.BYTE;}
	|
	PRIM_UBYTE
	{$t = SimpleType.UBYTE;}
;

UNKNOWN_SIZE_ARRAY : ']' ;
ARRAY :              '[' ;
POINTER :            '#' ;

PRIM_FPNUM :        '.fp' ;
PRIM_NUM :          '.n' ;
PRIM_UNUM :         '.un' ;
PRIM_DWORD :        '.dw' ;
PRIM_UDWORD :       '.udw' ;
PRIM_WORD :         '.w' ;
PRIM_UWORD :        '.uw' ;
PRIM_BYTE :         '.b' ;
PRIM_UBYTE :        '.ub' ;

FUNC2 :             '~F' ;
FUNC :              '~f' ;
VAR :               '~v' ;
STRUCT :            '~s' ;
CONST :             '~c' ;

NAME_TYPE_SEP :     ':' ;
VAR_SEP :           ',' ;

NAME_OR_NUMBER : [a-zA-Z_0-9]+ ;

LINE : [\r\n]+ ;
