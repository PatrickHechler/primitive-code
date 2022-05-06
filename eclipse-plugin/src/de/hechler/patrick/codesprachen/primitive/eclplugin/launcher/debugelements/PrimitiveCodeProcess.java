package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBinaryStreamMonitor;
import org.eclipse.debug.core.model.IBinaryStreamsProxy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

public class PrimitiveCodeProcess implements IProcess, IStreamsProxy, IBinaryStreamsProxy, Runnable {

	public final PrimitiveCodeDebugTarget debug;
	public final Process process;
	private Map<String, String> attributes;
	public final PrimitiveCodeStreamMonitor out;
	public final PrimitiveCodeStreamMonitor err;
	private volatile CompletableFuture<Void> onExit;

	public PrimitiveCodeProcess(PrimitiveCodeDebugTarget debug, Process process, String cmdLine, long launchTime, String dir, String path) {
		this.debug = debug;
		this.process = process;
		this.out = new PrimitiveCodeStreamMonitor(process.getInputStream());
		this.err = new PrimitiveCodeStreamMonitor(process.getErrorStream());
		this.onExit = this.process.onExit().thenAccept(p -> {
			assert p == this.process;
			this.err.setTerminated();
			this.out.setTerminated();
			this.attributes.put(DebugPlugin.ATTR_TERMINATE_TIMESTAMP, Long.toString(System.currentTimeMillis()));
			p.descendants().forEach(ph -> {
				ph.destroy();
				for (int i = 0; i < 10 && ph.isAlive(); i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (ph.isAlive()) {
					ph.destroyForcibly();
				}
			});
			PrimitiveCodeDebugElement.fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
		});
		this.attributes = new HashMap<>();
		this.attributes.put(ATTR_PROCESS_TYPE, "PVM Debug");
		this.attributes.put(ATTR_CMDLINE, cmdLine);
		this.attributes.put(DebugPlugin.ATTR_CAPTURE_OUTPUT, "true");
		this.attributes.put(DebugPlugin.ATTR_TERMINATE_DESCENDANTS, "true");
		this.attributes.put(DebugPlugin.ATTR_MERGE_OUTPUT, "false");
		this.attributes.put(DebugPlugin.ATTR_CONSOLE_ENCODING, "UTF-8");
		this.attributes.put(DebugPlugin.ATTR_WORKING_DIRECTORY, path);
		this.attributes.put(DebugPlugin.ATTR_PATH, path);
		this.attributes.put(DebugPlugin.ATTR_LAUNCH_TIMESTAMP, Long.toString(launchTime));
	}

	@Override
	public boolean canTerminate() {
		return true;
	}

	@Override
	public boolean isTerminated() {
		return !this.process.isAlive();
	}

	@Override
	public void terminate() {
		PrimitiveCodeDebugElement.executeCommand(this, this.debug, getName(), this::blockingTerminate, PrimitiveCodeCommandTypes.Terminate);
	}

	public void blockingTerminate() {
		if (!this.process.isAlive()) {
			return;
		}
		this.process.destroy();
		try {
			this.process.waitFor(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
		if (!this.process.isAlive()) {
			return;
		}
		this.process.destroyForcibly();
		try {
			this.process.waitFor();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public String getLabel() {
		return "PVM Process: " + process.toString();
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		return this;
	}

	@Override
	public void setAttribute(String key, String value) {
		this.attributes.put(key, value);
	}

	@Override
	public String getAttribute(String key) {
		String attr = this.attributes.get(key);
		if (attr == null) {
			if (ATTR_PROCESS_LABEL.equals(key)) {
				return getLabel();
			}
		}
		return attr;
	}

	@Override
	public int getExitValue() throws DebugException {
		try {
			return this.process.exitValue();
		} catch (IllegalThreadStateException e) {
			throw new DebugException(new Status(IStatus.ERROR, getClass(), "pvm process is still running: " + e.getMessage(), e));
		}
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return PrimitiveCodeDebugElement.findAdapter(adapter, this, this.process);
	}

	@Override
	public IStreamMonitor getErrorStreamMonitor() {
		return this.err;
	}

	@Override
	public IStreamMonitor getOutputStreamMonitor() {
		return this.out;
	}

	@Override
	public void write(String input) throws IOException {
		this.process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void closeInputStream() throws IOException {
		this.process.getOutputStream().close();
	}

	@Override
	public IBinaryStreamMonitor getBinaryErrorStreamMonitor() {
		return this.err;
	}

	@Override
	public IBinaryStreamMonitor getBinaryOutputStreamMonitor() {
		return this.out;
	}

	@Override
	public void write(byte[] data, int offset, int length) throws IOException {
		this.process.getOutputStream().write(data, offset, length);
	}

	@Override
	public void run() {
		new Thread(this.err, "PVM: stderr stream").start();
		new Thread(this.out, "PVM: stdout stream").start();
	}

	public void atExit(Consumer<Process> listener) {
		this.onExit = this.onExit.thenAccept(v -> {
			listener.accept(this.process);
		});
	}

	@Override
	public String toString() {
		return getLabel();
	}

	public String getName() {
		return getLabel();
	}

	@Override
	public ILaunch getLaunch() {
		return this.debug.launch;
	}

}
