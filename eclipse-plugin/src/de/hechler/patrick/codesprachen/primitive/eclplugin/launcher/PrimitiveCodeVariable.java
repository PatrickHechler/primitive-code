package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

@Deprecated
public abstract class PrimitiveCodeVariable implements IVariable {

	protected final PrimitiveCodeDebugTarget debugTarget;
	protected long oldVal;

	public PrimitiveCodeVariable(PrimitiveCodeDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
		this.oldVal = getLongValue();
	}

	@Override
	public String getModelIdentifier() {
		return debugTarget.getModelIdentifier();
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return debugTarget;
	}

	@Override
	public ILaunch getLaunch() {
		return debugTarget.getLaunch();
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

	public void changeNotify() {
		oldVal = getLongValue();
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
		return getLongValue() != oldVal;
	}

	public static long extractValue(String expression) {
		return PrimitiveCodeDebugTarget
				.extractValue(expression.toUpperCase().replaceFirst("^(INST|\\s|POINTER|\\:)*", ""));
	}

	public static enum PrimCodeVauleType {

		pointer, int64,

	}

	public class PrimCodeValue implements IValue {

		@Override
		public String getModelIdentifier() {
			return debugTarget.getModelIdentifier();
		}

		@Override
		public IDebugTarget getDebugTarget() {
			return debugTarget;
		}

		@Override
		public ILaunch getLaunch() {
			return debugTarget.getLaunch();
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
			case pointer:
				return "pointer: UHEX-" + Long.toUnsignedString(getLongValue(), 16);
			case int64:
				return "HEX-" + Long.toString(getLongValue(), 16);
			default:
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
