package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeThread.*;

import java.io.IOError;
import java.io.IOException;
import java.util.function.Consumer;

import org.eclipse.debug.core.DebugEvent;

import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;

public enum PrimitiveCodeCommandTypes {
	Suspend(-1, -1, DebugEvent.SUSPEND, DebugEvent.CLIENT_REQUEST, STATE_WAITING, com -> {
		try {
			com.pause();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}), Terminate(-1, -1, -1, -1, STATE_TERMINATING, com -> {
	}),

	Step_Into(DebugEvent.RESUME, DebugEvent.STEP_INTO, DebugEvent.SUSPEND, DebugEvent.STEP_END, STATE_STEPPING, com -> {
		try {
			com.pause();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}),
	Step_Over(DebugEvent.RESUME, DebugEvent.STEP_OVER, DebugEvent.SUSPEND, DebugEvent.STEP_END, STATE_STEPPING, com -> {
		try {
			com.pause();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}), Step_Return(DebugEvent.RESUME, DebugEvent.STEP_RETURN, DebugEvent.SUSPEND, DebugEvent.STEP_END, STATE_STEPPING,
			com -> {
				try {
					com.pause();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}),

	Resume(DebugEvent.RESUME, DebugEvent.UNSPECIFIED, DebugEvent.SUSPEND, DebugEvent.STEP_END, STATE_RUNNING, com -> {
		try {
			com.run();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}),

	Disconnect(-1, -1, DebugEvent.CHANGE, DebugEvent.STATE, STATE_DISCONNECTED, com -> {
		try {
			com.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}),

	;

	public final int stateNum;
	public final Consumer<PVMDebugingComunicator> init;
	public final int dns;
	public final int ddns;
	public final int dne;
	public final int ddne;

	private PrimitiveCodeCommandTypes(int dns, int ddns, int dne, int ddne, int stateNum,
			Consumer<PVMDebugingComunicator> init) {
		this.stateNum = stateNum;
		this.init = init;
		this.dns = dns;
		this.ddns = ddns;
		this.dne = dne;
		this.ddne = ddne;
	}

	@Override
	public String toString() {
		return name().replace('_', ' ');
	}

}
