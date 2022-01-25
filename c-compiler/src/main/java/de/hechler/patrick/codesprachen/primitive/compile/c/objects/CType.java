package de.hechler.patrick.codesprachen.primitive.compile.c.objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.NameUse;

public class CType implements NameUse {
	
	private static final int PREDEFINED = 1;
	private static final int TYPEDEF = 2;
	private static final int ENUM = 3;
	private static final int UNION = 4;
	private static final int STRUCT = 5;
	private static final int POINTER = 6;
	private static final int ARRAY = 7;
	
	public final String name;
	private final PreDefCTypes predefined;
	private final CType delegate;
	private final CType[] subs;
	private final Map <String, Long> values;
	private final int value;
	private final int art;
	
	private CType(PreDefCTypes predefined, String name, CType delegate, CType[] subs, Map <String, Long> values, int value, int art) {
		this.predefined = predefined;
		this.name = name;
		this.delegate = delegate;
		this.subs = subs;
		this.values = values;
		this.value = value;
		this.art = art;
	}
	
	
	public static CType createTypedef(String name, CType delegate) {
		return new CType(null, Objects.requireNonNull(name, "no anonymus typedef allowed"), delegate, null, null, 0, TYPEDEF);
	}
	
	public static CType createEnum(String name, Map <String, Long> values) {
		return new CType(null, name, null, null, Collections.unmodifiableMap(new HashMap <>(values)), 0, ENUM);
	}
	
	public static CType createStruct(String name, CType[] subs) {
		return new CType(null, name, null, subs, null, 0, STRUCT);
	}
	
	public static CType createUnion(String name, CType[] subs) {
		return new CType(null, name, null, subs, null, 0, UNION);
	}
	
	public static CType createPointer(String name, CType delegate, int pointers) {
		return new CType(null, name, delegate, null, null, pointers, POINTER);
	}
	
	public static CType createParans(String name, CType delegate) {
		return new CType(null, name, delegate, null, null, 0, POINTER);
	}
	
	public static CType createArray(String name, CType delegate, int len) {
		return new CType(null, name, delegate, null, null, len, ARRAY);
	}
	
	public static CType createArray(String name, CType delegate) {
		return new CType(null, name, delegate, null, null, -1, ARRAY);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + art;
		result = prime * result + ( (delegate == null) ? 0 : delegate.hashCode());
		result = prime * result + ( (name == null) ? 0 : name.hashCode());
		result = prime * result + ( (predefined == null) ? 0 : predefined.hashCode());
		result = prime * result + Arrays.hashCode(subs);
		result = prime * result + ( (values == null) ? 0 : values.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CType other = (CType) obj;
		if (art != other.art) return false;
		switch (art) {
		case PREDEFINED:
			return false;// from every predefined is only one
		case POINTER:
		case ARRAY:
			if (value != other.value) return false;
		case TYPEDEF:
			return delegate.equals(other.delegate);
		case ENUM:
			return values.equals(other.values);
		case UNION:
		case STRUCT:
			return Arrays.deepEquals(subs, other.subs);
		default:
			throw new InternalError("unknown art=" + art);
		}
	}
	
	@Override
	public String toString() {
		return toString(null);
	}
	
	public String toString(String varname) {
		switch (art) {
		case PREDEFINED:
			if (varname == null) return name;
			else return name + ' ' + varname;
		case TYPEDEF:
			if (varname == null) return name + " aka <" + delegate + ">";
			else return name + ' ' + varname;
		case ENUM:
			if (varname == null) return name() + " aka <" + delegate + ">";
			else return "enum " + name();
		case UNION:
			if (varname == null) return "union " + name();
			else return "union " + name() + ' ' + varname;
		case STRUCT:
			if (varname == null) return "struct " + name();
			else return "struct " + name() + ' ' + varname;
		case POINTER: {
			String del = delegate.toString();
			StringBuilder build = new StringBuilder(value + 2 + del.length());
			if (varname != null) build.append(del);
			for (int i = 0; i < value; i ++ ) {
				build.append('*');
			}
			if (varname != null) build.append(varname);
			else build.append(del);
			return toString();
			
		}
		case ARRAY:
			if (varname == null && value == -1) return "(" + delegate + ")[]";
			else if (varname == null) return "(" + delegate + ")[" + value + "]";
			else if (value == -1) return "(" + delegate + ") " + varname + "[]";
			else return "(" + delegate + ") " + varname + "[" + value + "]";
		default:
			throw new InternalError("unknown art=" + art);
		}
	}
	
	private String name() {
		return name == null ? "<anonymus>" : name;
	}
	
	public static enum PreDefCTypes {
		
		pdct_void,
		
		
		pdct_char,
		
		
		pdct_short_int,
		
		pdct_int,
		
		pdct_long_int, pdct_long_long_int,
		
		
		pdct_float_int,
		
		pdct_double_int, pdct_double_long_int,;
		
		public final CType type = new CType(this, name().substring(5).replace('_', ' '), null, null, null, 0, PREDEFINED);
		
		@Override
		public String toString() {
			return type.name;
		}
		
	}
	
	public boolean isTypedef() {
		return art == TYPEDEF;
	}
	
	public boolean isEnum() {
		return art == ENUM;
	}
	
	public boolean isStruct() {
		return art == STRUCT;
	}
	
	public boolean isUnion() {
		return art == UNION;
	}
	
	public boolean isPointer() {
		return art == POINTER;
	}
	
	public boolean isArray() {
		return art == ARRAY;
	}
	
	public Map <String, Long> getEnumConstants() {
		if (art != ENUM) {
			throw new IllegalStateException("this is no enum type! this='" + this + "'");
		}
		return Collections.unmodifiableMap(values);
	}
	
}
