package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMMemoryAction.MemAction;

public class PrimitiveVM {
	
	private static final int DEFAULT_PORT = 5894;
	private static final int DEFAULT_TIMEOUT = 1000;
	
	private static final int SIGNAL_KILL = 1;
	private static final int SIGNAL_HALT = 2;
	private static final int SIGNAL_STEP = 3;
	private static final int SIGNAL_RUN = 4;
	private static final int SIGNAL_STATE = 5;
	private static final int SIGNAL_WRITE_SNAPSHOT = 6;
	private static final int SIGNAL_READ_SNAPSHOT = 7;
	// @SuppressWarnings("unused")
	// private static final int ACTION_MIN_SIGNAL = 8;
	// @SuppressWarnings("unused")
	// private static final int ACTION_MAX_SIGNAL = 12;
	// instead using MemAction.signal
	private static final int SIGNAL_ILLEGAL = 255;
	
	private final Process process;
	private final OutputStream output;
	private final InputStream input;
	private final Socket socket;
	
	public PrimitiveVM(Process process, OutputStream output, InputStream input, Socket socket) {
		this.process = process;
		this.output = output;
		this.input = input;
		this.socket = socket;
	}
	
	public static PrimitiveVM create(String file, String... args) throws IOException {
		return create(DEFAULT_PORT, DEFAULT_TIMEOUT, file, args);
	}
	
	public static PrimitiveVM create(int port, int timeout, String file, String... args) throws IOException {
		Process exec = Runtime.getRuntime().exec(new String[] {"pvm", "--listen", "" + port, "--wait", "-pmc", file });
		Socket s = new Socket("localhost", port);
		s.setSoTimeout(timeout);
		InputStream in = s.getInputStream();
		OutputStream out = s.getOutputStream();
		return new PrimitiveVM(exec, out, in, s);
	}
	
	
	
	public void setTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}
	
	public Process getProcess() {
		return process;
	}
	
	public void writeSnapshot(PVMSnapshot snapshot) throws IOException {
		byte[] bytes = new byte[1 + 8 * 8];
		bytes[0] = SIGNAL_WRITE_SNAPSHOT;
		convertLong(snapshot.ax, bytes, 1);
		convertLong(snapshot.bx, bytes, 9);
		convertLong(snapshot.cx, bytes, 17);
		convertLong(snapshot.dx, bytes, 25);
		convertLong(snapshot.ip, bytes, 33);
		convertLong(snapshot.sp, bytes, 41);
		convertLong(snapshot.intp, bytes, 49);
		convertLong(snapshot.status, bytes, 57);
		output.write(bytes);
		output.flush();
		int val = input.read();
		if (val != SIGNAL_WRITE_SNAPSHOT) {
			throw new AssertionError("pvm did not answert properly! asserted=" + SIGNAL_HALT + " or " + SIGNAL_RUN + " but got=" + val);
		}
	}
	
	public PVMSnapshot makeSnapshot() throws IOException {
		output.write(SIGNAL_READ_SNAPSHOT);
		output.flush();
		byte[] bytes = new byte[8 * 8];
		input.read(bytes);
		//@formatter:off
		long 	ax = convertLong(bytes, 0),
				bx = convertLong(bytes, 8),
				cx = convertLong(bytes, 16),
				dx = convertLong(bytes, 24),
				ip = convertLong(bytes, 32),
				sp = convertLong(bytes, 40),
				intp = convertLong(bytes, 48),
				status = convertLong(bytes, 56);
		//@formatter:on
		return new PVMSnapshot(ax, bx, cx, dx, ip, sp, intp, status);
	}
	
	public void makeAction(PVMMemoryAction action) throws IOException {
		switch (action.action) {
		case allocate:
		case reallocate:
		case free: {
			byte[] bytes = new byte[8];
			output.write(action.action.signal);
			if (action.action != MemAction.allocate) {
				convertLong(action.getPNTR(), bytes, 0);
				output.write(bytes);
			}
			if (action.action != MemAction.free) {
				convertLong(action.getValues0().length, bytes, 0);
				output.write(bytes);
			}
			output.flush();
			input.read(bytes);
			long val = convertLong(bytes, 0);
			action.setPNTR(val);
			break;
		}
		case write: {
			long[] longs = action.getValues0();
			byte[] bytes = new byte[1 + (8 * (longs.length + 1))];
			bytes[0] = (byte) action.action.signal;
			convertLong((long) longs.length, bytes, 1);
			for (int i = 0; i < longs.length; i ++ ) {
				convertLong(longs[i], bytes, i * 8 + 1);
			}
			output.write(bytes);
			output.flush();
			int val = input.read();
			if (val != action.action.signal) {
				throw new AssertionError("pvm did not answert properly! asserted=" + SIGNAL_HALT + " or " + SIGNAL_RUN + " but got=" + val);
			}
			break;
		}
		case read: {
			byte[] bytes = new byte[19];
			long[] longs = action.getValues0();
			bytes[0] = (byte) action.action.signal;
			convertLong(action.getPNTR(), bytes, 1);
			convertLong((long) longs.length, bytes, 9);
			output.write(bytes);
			output.flush();
			bytes = new byte[longs.length * 8];
			input.read(bytes);
			for (int i = 0; i < longs.length; i ++ ) {
				longs[i] = convertLong(bytes, 8 * i);
			}
			break;
		}
		default:
			throw new InternalError("unknown action: " + action.action.name() + " of action obj: '" + action + "'");
		}
	}
	
	public PVMState getState() throws IOException {
		if ( !process.isAlive()) {
			return PVMState.finish;
		}
		output.write(SIGNAL_STATE);
		output.flush();
		int val = input.read();
		switch (val) {
		case SIGNAL_HALT:
			return PVMState.suspended;
		case SIGNAL_RUN:
			return PVMState.running;
		default:
			throw new AssertionError("pvm did not answert properly! asserted=" + SIGNAL_HALT + " or " + SIGNAL_RUN + " but got=" + val);
		}
	}
	
	public void kill() throws IOException {
		send(SIGNAL_KILL);
		socket.close();
	}
	
	public void halt() throws IOException {
		send(SIGNAL_HALT);
	}
	
	public void step() throws IOException {
		send(SIGNAL_STEP);
	}
	
	public void run() throws IOException {
		send(SIGNAL_RUN);
	}
	
	private void send(int signal) throws IOException {
		output.write(signal);
		output.flush();
		int in = input.read();
		if (in != signal) {
			if (in == SIGNAL_ILLEGAL) {
				throw new IllegalStateException("this is not allowed, maby the pvm is currently running and can't do this? mySignal=" + signal);
			}
			throw new AssertionError("pvm did not answert properly! asserted=" + signal + " but got=" + in);
		}
	}
	
	private void convertLong(long l, byte[] bytes, int i) {
		bytes[i + 0] = (byte) l;
		bytes[i + 1] = (byte) (l >> 8);
		bytes[i + 2] = (byte) (l >> 16);
		bytes[i + 3] = (byte) (l >> 24);
		bytes[i + 4] = (byte) (l >> 32);
		bytes[i + 5] = (byte) (l >> 40);
		bytes[i + 6] = (byte) (l >> 48);
		bytes[i + 7] = (byte) (l >> 56);
	}
	
	private long convertLong(byte[] bytes, int i) {
		long ret;
		ret = ( (0xFFL & ((long) bytes[i + 0])));
		ret |= ( (0xFFL & ((long) bytes[i + 1])) << 8);
		ret |= ( (0xFFL & ((long) bytes[i + 2])) << 16);
		ret |= ( (0xFFL & ((long) bytes[i + 3])) << 24);
		ret |= ( (0xFFL & ((long) bytes[i + 4])) << 32);
		ret |= ( (0xFFL & ((long) bytes[i + 5])) << 40);
		ret |= ( (0xFFL & ((long) bytes[i + 6])) << 48);
		ret |= ( (0xFFL & ((long) bytes[i + 7])) << 56);
		return ret;
	}
	
	@Override
	protected void finalize() throws Throwable {
		socket.close();
	}
	
	public static enum PVMState {
		running, suspended, finish
	}
	
}
