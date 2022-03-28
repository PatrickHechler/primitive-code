package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public abstract class PrimitiveCodeVariable extends PrimitiveCodeNTDE implements IVariable {

	protected long oldVal;
	protected boolean changed;

	public PrimitiveCodeVariable(PrimitiveCodeDebugTarget debug) {
		super(debug);
	}

	@Override
	public String getModelIdentifier() {
		return debug.getModelIdentifier();
	}

	@Override
	public PrimitiveCodeDebugTarget getDebugTarget() {
		return debug;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return PrimitiveCodeDebugTarget.findAdapter(adapter, this);
	}

	@Override
	public void setValue(String expression) throws DebugException {
		setValue(extractValue(expression));
	}

	@Override
	public void setValue(IValue value) throws DebugException {
		if (value instanceof PrimitiveCodeVariable) {
			setValue(((PrimitiveCodeVariable) value).getLongValue());
		} else {
			setValue(value.getValueString());
		}

	}

	public abstract void setValue(long value);

	@Override
	public boolean supportsValueModification() {
		return true;
	}

	@Override
	public boolean verifyValue(String expression) {
		try {
			extractValue(expression);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean verifyValue(IValue value) {
		if (value instanceof PrimitiveCodeVariable) {
			((PrimitiveCodeVariable) value).getLongValue();
			return true;
		} else {
			try {
				return verifyValue(value.getValueString());
			} catch (DebugException e) {
				return false;
			}
		}
	}

	@Override
	public IValue getValue() {
		return new PrimCodeValue();
	}

	public void update() {
		long val = getLongValue();
		changed = val != oldVal;
		oldVal = val;
	}

	public abstract long getLongValue();

	@Override
	public abstract String getName();

	@Override
	public String getReferenceTypeName() {
		return getType().toString();
	}

	public abstract PrimCodeVauleType getType();

	@Override
	public boolean hasValueChanged() {
		return changed;
	}

	public void setChanged() {
		this.changed = true;
	}

	public static long extractValue(String expression) {
		return extractValue0(expression.toUpperCase().replaceFirst("^(INST|\\s|POINTER|\\:)*", ""));
	}

	public static long extractValue0(String expression) {
		long val;
		if (expression.startsWith("UHEX-")) {
			val = Long.parseUnsignedLong(expression.substring(5), 16);
		} else if (expression.startsWith("HEX-")) {
			val = Long.parseLong(expression.substring(4), 16);
		} else if (expression.startsWith("NHEX-")) {
			val = Long.parseLong(expression.substring(4), 16);
		} else if (expression.startsWith("DEC-")) {
			val = Long.parseLong(expression.substring(4), 10);
		} else if (expression.startsWith("NDEC-")) {
			val = Long.parseLong(expression.substring(4), 10);
		} else if (expression.startsWith("OCT-")) {
			val = Long.parseLong(expression.substring(4), 8);
		} else if (expression.startsWith("NOCT-")) {
			val = Long.parseLong(expression.substring(4), 8);
		} else if (expression.startsWith("BIN-")) {
			val = Long.parseLong(expression.substring(4), 2);
		} else if (expression.startsWith("NBIN-")) {
			val = Long.parseLong(expression.substring(4), 2);
		} else if (expression.matches("[0-9]*\\.[0-9]*")) {
			val = Double.doubleToRawLongBits(Double.parseDouble(expression));
		} else {
			val = Long.parseLong(expression, 10);
		}
		return val;
	}

	public static enum PrimCodeVauleType {

		pointer, int64,

	}

	public class PrimCodeValue implements IValue {

		@Override
		public String getModelIdentifier() {
			return debug.getModelIdentifier();
		}

		@Override
		public IDebugTarget getDebugTarget() {
			return debug;
		}

		@Override
		public ILaunch getLaunch() {
			return debug.getLaunch();
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return PrimitiveCodeDebugTarget.findAdapter(adapter, this);
		}

		@Override
		public String getReferenceTypeName() {
			return PrimitiveCodeVariable.this.getReferenceTypeName();
		}

		@Override
		public String getValueString() {
			switch (getType()) {
				case pointer :
					return "pointer: UHEX-" + Long.toUnsignedString(getLongValue(), 16).toUpperCase();
				case int64 :
					return "HEX-" + Long.toString(getLongValue(), 16).toUpperCase();
				default :
					throw new InternalError("unknown type: " + getType());
			}
		}

		@Override
		public boolean isAllocated() {
			return true;
		}

		@Override
		public IVariable[] getVariables() {
			return new IVariable[0];
		}

		@Override
		public boolean hasVariables() {
			return false;
		}

	}

}
