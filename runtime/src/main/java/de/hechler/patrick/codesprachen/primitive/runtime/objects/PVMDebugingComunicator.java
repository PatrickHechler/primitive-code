package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertByteArrToLong;
import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertLongListToLongArr;
import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertLongToByteArr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PVMDebugingComunicator {

	private final Process pvm;
	private final InputStream in;
	private final OutputStream out;

	public PVMDebugingComunicator(Process pvm, Socket sok) throws IOException {
		this(pvm, sok.getInputStream(), sok.getOutputStream());
	}

	public PVMDebugingComunicator(Process pvm, InputStream in, OutputStream out) {
		this.pvm = pvm;
		this.in = in;
		this.out = out;
	}

	/**
	 * exit the pvm with exit-code 0<br>
	 */
	private static final int PVM_DEBUG_EXIT = 1;
	/**
	 * pause the pvm<br>
	 */
	private static final int PVM_DEBUG_PAUSE = 2;
	/**
	 * run the pvm<br>
	 * with the debug setting<br>
	 */
	private static final int PVM_DEBUG_RUN = 3;
	/**
	 * let the pvm make a snapshot and send it to this debugger<br>
	 * the pvm responses with:<br>
	 * the snapshot<br>
	 * snapshot includes:<br>
	 * 64-bit: the AX register<br>
	 * 64-bit: the BX register<br>
	 * 64-bit: the CX register<br>
	 * 64-bit: the DX register<br>
	 * 64-bit: the stack pointer<br>
	 * 64-bit: the instruction pointer<br>
	 * 64-bit: the status register<br>
	 * 64-bit: the interrupt count register<br>
	 * 64-bit: the interrupt pointer<br>
	 */
	private static final int PVM_DEBUG_GET_SNAPSHOT = 4;
	/**
	 * send a snapshot to the pvm and let the pvm overwrite the current state with
	 * the snapshot<br>
	 * snapshot includes: see {@link #PVM_DEBUG_GET_SNAPSHOT}
	 * 
	 * @see #PVM_DEBUG_GET_SNAPSHOT
	 */
	private static final int PVM_DEBUG_SET_SNAPSHOT = 5;
	/**
	 * read some parts of the memory<br>
	 * the attached debugger sends:<br>
	 * 64-bit: the address<br>
	 * 64-bit: the length<br>
	 * the pvm responses with:<br>
	 * the array
	 */
	private static final int PVM_DEBUG_GET_MEMORY = 6;
	/**
	 * read some parts of the memory<br>
	 * the attached debugger sends:<br>
	 * 64-bit: the address<br>
	 * 64-bit: the length<br>
	 * the array
	 */
	private static final int PVM_DEBUG_SET_MEMORY = 7;
	/**
	 * send all current breakpoints to the attached debugger<br>
	 * the pvm responses with:<br>
	 * the breakpoint-list<br>
	 * the breakpoint-list:<br>
	 * the list of 64-bit breakpoints<br>
	 * a -1/0xFFFFFFFFFFFFFFFF address at the end<br>
	 */
	private static final int PVM_DEBUG_GET_BREAKPOINTS = 8;
	/**
	 * receive new breakpoints to be added to the current breakpoints<br>
	 * doubled breakpoints will be ignored<br>
	 * the breakpoint-list:<br>
	 * see pvm_debug_get_breakpoints<br>
	 */
	private static final int PVM_DEBUG_ADD_BREAKPOINTS = 9;
	/**
	 * receive a breakpoint-list to be added to the current breakpoints<br>
	 * doubled breakpoints will be ignored<br>
	 * the breakpoint-list:<br>
	 * see pvm_debug_get_breakpoints<br>
	 */
	private static final int PVM_DEBUG_REMOVE_BREAKPOINTS = 10;
	/**
	 * the pvm first responses with:<br>
	 * 0x01: if breakpoints are enabled<br>
	 * 0x00: if breakpoints are disabled<br>
	 * then the pvm sends the pvm_debug_executed_command<br>
	 */
	private static final int PVM_DEBUG_GET_IGNORE_BREAKPOINTS = 11;
	/**
	 * if all breakpoints should be ignored or not.<br>
	 * ignoring the breakpoints does not delete the breakpoints, and adding
	 * breakpoints will not change<br>
	 * this property.<br>
	 * receive:<br>
	 * 0x01: if breakpoints should not be ignored<br>
	 * 0x00: if breakpoints should be ignored<br>
	 */
	private static final int PVM_DEBUG_SET_IGNORE_BREAKPOINTS = 12;
	/**
	 * let the pvm the next command execute
	 */
	private static final int PVM_DEBUG_EXECUTE_NEXT = 13;
	/**
	 * allocate memory: 64-bit: size the pvm responses with: 64-bit: the PNTR
	 */
	private static final int PVM_DEBUG_ALLOCMEM = 14;
	/**
	 * reallocate memory: 64-bit: PNTR 64-bit: newsize the pvm responses with:
	 * 64-bit: the new PNTR
	 */
	private static final int PVM_DEBUG_REALLOCMEM = 15;
	/**
	 * free memory: 64-bit: PNTR
	 */
	private static final int PVM_DEBUG_FREEMEM = 16;
	/**
	 * let the pvm run until an error occurs, exit gets called or of course if a
	 * breakpoint triggers (only if the breakpoints are enabled)
	 */
	private static final int PVM_DEBUG_EXECUTE_UNTIL_ERROR_OR_END_CALL = 17;
	/**
	 * let the pvm run until it would exit or an breakpoint triggers (an overwritten
	 * exit will still be executed, since it does not exit the pvm)
	 */
	private static final int PVM_DEBUG_EXECUTE_UNTIL_EXIT = 18;
	/**
	 * if not other specified the pvm will response with this message
	 */
	private static final int EXECUTED_COMMAND = 0x7F;
	/**
	 * returned from the socket when the pvm terminated and thus closed the
	 * socket<br>
	 * this constant holds simply the end-of-file/stream constant {@code -1}
	 */
	private static final int PVM_TERMINATED = -1;

	public void exit() throws IOException, RuntimeException {
		out.write(PVM_DEBUG_EXIT);
		if (pvm == null) {
			checkedRead(PVM_TERMINATED);// socket automatically closed on exit
		} else {
			try {
				int exitVal = pvm.waitFor();
				if (exitVal != 0) {
					throw new RuntimeException("pvm did not terminated with exit-code 0");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void pause() throws IOException {
		out.write(PVM_DEBUG_PAUSE);
		checkedRead(EXECUTED_COMMAND, PVM_TERMINATED);
	}

	public void run() throws IOException {
		out.write(PVM_DEBUG_RUN);
		checkedRead(EXECUTED_COMMAND, PVM_TERMINATED);
	}

	public PVMSnapshot getSnapshot() throws IOException {
		out.write(PVM_DEBUG_GET_SNAPSHOT);
		byte[] bytes = new byte[PVMSnapshot.PVM_SNAPSHOT_LENGH];
		read(bytes);
		return PVMSnapshot.create(bytes);
	}

	public void setSnapshot(PVMSnapshot sn) throws IOException {
		out.write(PVM_DEBUG_SET_SNAPSHOT);
		byte[] bytes = sn.toBytes();
		out.write(bytes);
		checkedRead(EXECUTED_COMMAND);
	}

	public void getMem(long PNTR, byte[] bytes, int off, int len) throws IOException {
		if (len <= 0) {
			throw new IllegalArgumentException("len <= 0: len=" + len);
		}
		out.write(PVM_DEBUG_GET_MEMORY);
		if (len >= 16) {
			convertLongToByteArr(bytes, off, PNTR);
			convertLongToByteArr(bytes, off + 8, (long) len);
			out.write(bytes, off, 16);
		} else {
			byte[] zw = new byte[16];
			convertLongToByteArr(zw, 0, PNTR);
			convertLongToByteArr(zw, 8, (long) len);
			out.write(zw);
		}
		if (checkedRead(1, 0) == 1) {
			read(bytes, off, len);
		} else {
			throw new RuntimeException(
					"the pvm had an error by reading the memory PNTR=0x" + Long.toHexString(PNTR) + " len=" + len);
		}
	}

	public void setMem(long PNTR, byte[] bytes, int off, int len) throws IOException {
		out.write(PVM_DEBUG_SET_MEMORY);
		byte[] zw = new byte[16];
		convertLongToByteArr(zw, 0, PNTR);
		convertLongToByteArr(zw, 8, (long) len);
		out.write(zw);
		out.write(bytes, off, len);
		if (checkedRead(1, 0) == 0) {
			throw new IOException("the pvm had an error by setting the memory (PNTR=" + PNTR + " len=" + len + ")");
		}
	}

	public long[] getBreakpoints() throws IOException {
		byte[] bytes = new byte[8];
		out.write(PVM_DEBUG_GET_BREAKPOINTS);
		List<Long> ls = new ArrayList<>();
		while (true) {
			read(bytes);
			long l = convertByteArrToLong(bytes, 0);
			if (l == -1L) {
				break;
			}
			ls.add(Long.valueOf(l));
		}
		return convertLongListToLongArr(ls);
	}

	public void addBreakpoints(long[] add) throws IOException {
		out.write(PVM_DEBUG_ADD_BREAKPOINTS);
		byte[] bytes = new byte[8];
		for (int i = 0; i < add.length; i++) {
			convertLongToByteArr(bytes, 0, add[i]);
			out.write(bytes);
		}
		convertLongToByteArr(bytes, 0, -1L);
		out.write(bytes);
		checkedRead(EXECUTED_COMMAND);
	}

	public void removeBreakpoints(long[] rem) throws IOException {
		out.write(PVM_DEBUG_REMOVE_BREAKPOINTS);
		byte[] bytes = new byte[8];
		for (int i = 0; i < rem.length; i++) {
			convertLongToByteArr(bytes, 0, rem[i]);
			out.write(bytes);
		}
		convertLongToByteArr(bytes, 0, -1L);
		out.write(bytes);
		checkedRead(EXECUTED_COMMAND);
	}

	public boolean isBreakpointsEnabled() throws IOException {
		out.write(PVM_DEBUG_GET_IGNORE_BREAKPOINTS);
		return checkedRead(1, 0) == 1;
	}

	public void setBreakpointsEnabled(boolean enabled) throws IOException {
		out.write(PVM_DEBUG_SET_IGNORE_BREAKPOINTS);
		out.write(enabled ? 1 : 0);
		checkedRead(EXECUTED_COMMAND);
	}

	public void executeNext() throws IOException {
		out.write(PVM_DEBUG_EXECUTE_NEXT);
		checkedRead(EXECUTED_COMMAND);
	}

	public long memalloc(long size) throws IOException {
		out.write(PVM_DEBUG_ALLOCMEM);
		byte[] bytes = new byte[8];
		read(bytes);
		return convertByteArrToLong(bytes, 0);
	}

	public long realloc(long PNTR, long newsize) throws IOException {
		out.write(PVM_DEBUG_REALLOCMEM);
		byte[] bytes = new byte[16];
		convertLongToByteArr(bytes, 0, PNTR);
		convertLongToByteArr(bytes, 8, newsize);
		out.write(bytes);
		read(bytes, 0, 8);
		return convertByteArrToLong(bytes, 0);
	}

	public void free(long PNTR) throws IOException {
		out.write(PVM_DEBUG_FREEMEM);
		byte[] bytes = new byte[8];
		convertLongToByteArr(bytes, 0, PNTR);
		out.write(bytes);
		checkedRead(EXECUTED_COMMAND);
	}

	public void executeUntilErrorOrExitCall() throws IOException {
		out.write(PVM_DEBUG_EXECUTE_UNTIL_ERROR_OR_END_CALL);
		checkedRead(EXECUTED_COMMAND);
	}

	public void executeUntilExit() throws IOException {
		out.write(PVM_DEBUG_EXECUTE_UNTIL_EXIT);
		checkedRead(EXECUTED_COMMAND);
	}

	public void executeUntilExit(long[] programm, boolean stopAlsoOnErrors) throws IOException {
		PVMSnapshot sn = getSnapshot();
		sn.ip = memalloc(programm.length * 8);
		setSnapshot(sn);
		byte[] bytes = new byte[programm.length * 8];
		for (int i = 0; i < programm.length; i++) {
			bytes[(8 * i)] = (byte) programm[i];
			bytes[1 + (8 * i)] = (byte) (programm[i] << 8);
			bytes[2 + (8 * i)] = (byte) (programm[i] << 16);
			bytes[3 + (8 * i)] = (byte) (programm[i] << 24);
			bytes[4 + (8 * i)] = (byte) (programm[i] << 32);
			bytes[5 + (8 * i)] = (byte) (programm[i] << 40);
			bytes[6 + (8 * i)] = (byte) (programm[i] << 48);
			bytes[7 + (8 * i)] = (byte) (programm[i] << 56);
		}
		setMem(sn.ip, bytes, 0, bytes.length);
		if (stopAlsoOnErrors) {
			executeUntilErrorOrExitCall();
		} else {
			executeUntilExit();
		}
	}

	public void detach() {
		try {
			out.close();
			in.close();
		} catch (IOException e) {
		}
	}

	private int checkedRead(int... expect) throws IOException {
		int r = in.read();
		for (int i = 0; i < expect.length; i++) {
			if (r == expect[i]) {
				return r;
			}
		}
		byte[] bytes = new byte[in.available()];
		IOException suppress = null;
		try {
			read(bytes);
		} catch (IOException err) {
			suppress = err;

		}
		RuntimeException re = new RuntimeException("did not read the expected things: read=" + r + " expected="
				+ Arrays.toString(expect) + " behind that was: " + Arrays.toString(bytes));
		if (suppress != null) {
			re.addSuppressed(suppress);
		}
		throw re;
	}

	private void read(byte[] bytes) throws IOException {
		read(bytes, 0, bytes.length);
	}

	private void read(byte[] bytes, int off, int len) throws IOException {
		final int origOff = off;
		for (int need = len, r; need > 0; need -= r, off += r) {
			r = in.read(bytes, off, need);
			if (r == -1) {
				throw new IOException("end of stream reached too early! len=" + len + " stillNeed=" + need + " read: "
						+ Arrays.toString(Arrays.copyOfRange(bytes, origOff, len - need)));
			}
		}
	}

}
