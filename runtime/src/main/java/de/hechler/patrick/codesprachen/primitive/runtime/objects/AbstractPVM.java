package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_ELEMENT_OFFSET_ID;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_ELEMENT_OFFSET_LOCK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_LOCK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_STREAM_OFFSET_FILE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_STREAM_OFFSET_POS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTCNT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTERRUPT_COUNT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_ERRORS_ILLEGAL_INTERRUPT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_ERRORS_UNKNOWN_COMMAND;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FPNUMBER_TO_STRING;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_ELEMENT_GET_CREATE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_ELEMENT_GET_LAST_META_MOD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_ELEMENT_GET_LAST_MOD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_ELEMENT_SET_CREATE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_ELEMENT_SET_LAST_META_MOD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_ELEMENT_SET_LAST_MOD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FILE_APPEND;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FILE_READ;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FILE_WRITE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FOLDER_ADD_FILE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FOLDER_ADD_FOLDER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FOLDER_ADD_LINK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FOLDER_GET_CHILD_OF_INDEX;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_FOLDER_GET_CHILD_OF_NAME;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_GET_ELEMENT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_GET_FILE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_GET_FOLDER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_FS_GET_LINK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_NUMBER_TO_STRING;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_STRING_TO_FPNUMBER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_STRING_TO_NUMBER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_STR_TO_U8STR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_U8STR_TO_STR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.IP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.MAX_STD_STREAM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.OPEN_APPEND;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.OPEN_CREATE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.OPEN_NEW_FILE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.OPEN_READ;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.OPEN_TRUNCATE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.OPEN_WRITE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ANUM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ANUM_BNUM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ANUM_BREG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ANUM_BSR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ASR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ASR_BNUM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ASR_BREG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ASR_BSR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.SP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_ALL_BITS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_CARRY;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_ELEMENT_ALREADY_EXIST;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_ELEMENT_LOCKED;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_ELEMENT_NOT_EXIST;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_ELEMENT_WRONG_TYPE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_EQUAL;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_GREATHER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_ILLEGAL_ARG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_IO_ERR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_LOWER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_NAN;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_NONE_BITS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_OUT_OF_MEMORY;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_OUT_OF_SPACE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_READ_ONLY;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_SOME_BITS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_ZERO;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STD_IN;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STD_LOG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STD_OUT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.X_ADD;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.PVM;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.functional.Interrupt;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.functional.PVMCommand;
import de.hechler.patrick.pfs.exception.ElementLockedException;
import de.hechler.patrick.pfs.exception.ElementReadOnlyException;
import de.hechler.patrick.pfs.exception.OutOfSpaceException;
import de.hechler.patrick.pfs.interfaces.PatrFile;
import de.hechler.patrick.pfs.interfaces.PatrFileSysElement;
import de.hechler.patrick.pfs.interfaces.PatrFolder;
import de.hechler.patrick.pfs.interfaces.PatrLink;
import de.hechler.patrick.pfs.objects.fs.PatrFileImpl;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysElementImpl;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysImpl;
import de.hechler.patrick.pfs.objects.fs.PatrID;
import de.hechler.patrick.pfs.utils.PatrFileSysConstants;

public abstract class AbstractPVM implements PVM {
	
	private static final Charset STRING_U16 = StandardCharsets.UTF_16LE;
	
	private static final int MAX_BUFFER = 2048;
	
	private static final long NO_LOCK = PatrFileSysConstants.NO_LOCK;
	
	protected final Random          rnd;
	protected final PatrFileSysImpl fs;
	protected final Interrupt[]     defaultInts;
	protected final PVMCommand[]    commands;
	protected final OutputStream    stdlog;
	protected final OutputStream    stdout;
	protected final InputStream     stdin;
	
	
	protected long  ip;
	private long    cmd;
	private boolean off1;
	private int     off2;
	private int     off3;
	private int     len;
	private boolean isreg;
	
	protected abstract void putReg(int reg, long val);
	
	protected abstract long getReg(int reg);
	
	protected abstract void putLong(long addr, long val) throws PrimitiveErrror;
	
	protected abstract long getLong(long addr) throws PrimitiveErrror;
	
	protected abstract void putChar(long addr, char val) throws PrimitiveErrror;
	
	protected abstract char getChar(long addr) throws PrimitiveErrror;
	
	protected abstract void putByte(long addr, byte val) throws PrimitiveErrror;
	
	protected abstract byte getByte(long addr) throws PrimitiveErrror;
	
	protected abstract long malloc(long len) throws OutOfMemoryError;
	
	protected abstract long realloc(long addr, long len) throws PrimitiveErrror, OutOfMemoryError;
	
	protected abstract void free(long addr) throws PrimitiveErrror;
	
	protected abstract void memcpy(long srcaddr, long dstaddr, long len) throws PrimitiveErrror;
	
	protected abstract void memmov(long srcaddr, long dstaddr, long len) throws PrimitiveErrror;
	
	protected abstract void memset(long addr, long len, byte val) throws PrimitiveErrror;
	
	protected abstract void memset(long addr, long len, long val) throws PrimitiveErrror;
	
	protected abstract void set(char[] src, long dstaddr, int cc) throws PrimitiveErrror;
	
	protected abstract void set(byte[] src, long dstaddr, int bc) throws PrimitiveErrror;
	
	protected abstract void cpy(char[] src, char[] dst, int cc) throws PrimitiveErrror;
	
	protected abstract void cpy(String[] src, String[] dst, int oc) throws PrimitiveErrror;
	
	protected abstract void cpy(byte[] src, byte[] dst, int bc) throws PrimitiveErrror;
	
	protected abstract void get(long srcaddr, char[] dst, int cc) throws PrimitiveErrror;
	
	protected abstract void get(long srcaddr, byte[] dst, int bc) throws PrimitiveErrror;
	
	protected abstract void get(long srcaddr, byte[] dst, int bo, int bc) throws PrimitiveErrror;
	
	/**
	 * copies the registers from IP..X09 to <code>dstaddr</code>
	 */
	protected abstract void getregs(long dstaddr) throws PrimitiveErrror;
	
	/**
	 * copies the memory block <code>srcaddr</code> to the registers IP..X09
	 */
	protected abstract void setregs(long srcaddr) throws PrimitiveErrror;
	
	/**
	 * copies the registers to the byte array <code>dst</code><br>
	 * the array has to be of length 256
	 */
	protected abstract void getregs(byte[] dst);
	
	/**
	 * copies the byte array {@code src} to the registers<br>
	 * the array has to be of length 256
	 */
	protected abstract void setregs(byte[] src);
	
	/**
	 * checks if the given memory range is valid<br>
	 * if {@code addr} is not inside of a valid memory range a {@link PrimitiveErrror} is thrown.<br>
	 * if the memory range with {@code addr} does not also contains {@code addr + len - 1} a {@link PrimitiveErrror} is throws.
	 * <p>
	 * in other words if the PVM can not access all bytes in the given memory range (from {@code addr} to {@code addr + len - 1}) a {@link PrimitiveErrror} is thrown
	 * 
	 * @param addr
	 *             the memory address
	 * @param len
	 *             the length of the given memory range in bytes (always greater than zero)
	 * @throws PrimitiveErrror
	 *                         if the range is invalid (always with {@link PrimAsmConstants#INT_ERRORS_ILLEGAL_MEMORY})
	 */
	protected abstract void checkmem(long addr, long len) throws PrimitiveErrror;
	
	public AbstractPVM(PatrFileSysImpl fs, OutputStream stdout, OutputStream stdlog, InputStream stdin) {
		this(fs, new Random(), stdout, stdlog, stdin);
	}
	
	public AbstractPVM(PatrFileSysImpl fs, Random rnd, OutputStream stdout, OutputStream stdlog, InputStream stdin) {
		this.rnd = rnd;
		this.fs = fs;
		this.stdout = stdout;
		this.stdlog = stdlog;
		this.stdin = stdin;
		this.defaultInts = new Interrupt[] { //
			num -> System.exit((int) (128L + getReg(X_ADD))), //
			num -> System.exit(7), //
			num -> System.exit(6), //
			num -> System.exit(5), //
			num -> System.exit((int) getReg(X_ADD)), //
			num -> {
				try {
					long l = getReg(X_ADD);
					long a = malloc(l);
					putReg(X_ADD, a);
				} catch (OutOfMemoryError e) {
					putReg(X_ADD, -1L);
				}
			}, //
			num -> {
				try {
					long a = getReg(X_ADD);
					long l = getReg(X_ADD + 1);
					putReg(X_ADD + 1, realloc(a, l));
				} catch (OutOfMemoryError e) {
					putReg(X_ADD + 1, -1L);
				}
			}, //
			num -> {
				long a = getReg(X_ADD);;
				free(a);
			}, //
			num -> defIntOpenNewStream(), //
			num -> defIntWrite(), //
			num -> defIntRead(), //
			num -> defIntGetFSElement(num), //
			num -> defIntGetFSElement(num),
			num -> defIntGetFSElement(num), //
			num -> defIntGetFSElement(num), //
			num -> defIntDuplicateFSElementHandle(), //
			num -> defIntGetParent(), //
			num -> defIntFromID(),
			num -> defIntGetSomeDate(num), //
			num -> defIntGetSomeDate(num), //
			num -> defIntGetSomeDate(num), //
			num -> defIntSetSomeDate(num), //
			num -> defIntSetSomeDate(num),
			num -> defIntSetSomeDate(num), //
			num -> defIntGetLockData(), //
			num -> defIntGetLockDate(), //
			num -> defIntLockElement(), //
			num -> defIntUnlockElement(),
			num -> defIntDeleteElement(), //
			num -> defIntMoveElement(), //
			num -> defIntGetElementFlags(), //
			num -> defIntGetElementFlags(), //
			num -> defIntModifyElementFlags(),
			num -> defIntGetFolderChildElementCount(), //
			num -> defIntGetChildElement(num), //
			num -> defIntGetChildElement(num), //
			num -> defIntAddElement(num),
			num -> defIntAddElement(num), //
			num -> defIntAddElement(num), //
			num -> defIntFileLength(), //
			num -> defIntFileHash(), //
			num -> defIntFileRWA(num),
			num -> defIntFileRWA(num), //
			num -> defIntFileRWA(num), //
			num -> defIntFileTruncate(), //
			num -> defIntLinkGetTarget(), //
			num -> defIntLinkSetTarget(),
			num -> defIntFSLock(), //
			num -> defIntFSUnlock(), //
			num -> putReg(X_ADD, System.currentTimeMillis()), //
			num -> defIntSleep(),
			num -> putReg(X_ADD, rnd.nextLong()), //
			num -> memcpy(getReg(X_ADD + 1), getReg(X_ADD), getReg(X_ADD + 2)), //
			num -> memmov(getReg(X_ADD + 1), getReg(X_ADD), getReg(X_ADD + 2)), //
			num -> memset(getReg(X_ADD), getReg(X_ADD + 2), (byte) getReg(X_ADD + 1)), //
			num -> memset(getReg(X_ADD), getReg(X_ADD + 2), getReg(X_ADD + 1)),
			num -> {
				// this.regs[X_ADD] = getU16String(this.regs[X_ADD]).length() << 1,
				long sa, a;
				sa = a = getReg(X_ADD);
				for (char c;; a += 2L) {
					c = getChar(a);
					if (c == '\0') break;
				}
				putReg(X_ADD, a - sa);
			}, //
			num -> {
				// num -> this.regs[X_ADD] = getU16String(this.regs[X_ADD]).compareTo(getU16String(this.regs[X_ADD +
				// 1])),
				long a = getReg(X_ADD), a2 = getReg(X_ADD + 1);
				for (char c, c2;; a += 2L, a2 += 2) {
					c = getChar(a);
					c2 = getChar(a2);
					if (c == '\0') {
						putReg(X_ADD, - (0xFFFFL & c2)); // if c2 is also '\0' 0 is correct, else its negative
					} else if (c == c2) {
						continue;
					} else {
						putReg(X_ADD, (0xFFFFL & c) - (0xFFFFL & c2));
					}
				}
			}, //
			num -> defIntAnyNumberToString(num), //
			num -> defIntAnyNumberToString(num), //
			num -> defIntStringToAnyNumber(num), //
			num -> defIntStringToAnyNumber(num),
			num -> defIntFormattString(), //
			num -> defIntStringConvert(num), //
			num -> defIntStringConvert(num), //
			num -> defIntLoadFile(), //
		};
		if (this.defaultInts.length != INTERRUPT_COUNT) {
			throw new AssertionError("expected int-count=" + INTERRUPT_COUNT + " int-count=" + this.defaultInts.length);
		}
		PVMCommand uc = () -> { throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND); };
		this.commands = new PVMCommand[] {
			// @formatter:off
			uc,         new MOV(),   new ADD(),   new SUB(),   new MUL(),   new DIV(),   new AND(),   new OR(),    new XOR(),   new NOT(),   new NEG(),   new LSH(),    new RLSH(),  new RASH(), new DEC(), new INC(),
			new JMP(),  new JMPEQ(), new JMPNE(), new JMPGT(), new JMPGE(), new JMPLT(), new JMPLE(), new JMPCS(), new JMPCC(), new JMPZS(), new JMPZC(), new JMPNAN(), new JMPAN(), uc,          uc,       uc,
			new CALL(), new CMP(),   new RET(),   new INT(),   new PUSH(),  new POP(),   new IRET(),  new SWAP(),  new LEA(),   new MVAD(),  new CALO(),  new BCP(),    new CMPFP(), new CHKFP(), uc,       uc,
			new ADDC(), new SUBC(),  new ADDFP(), new SUBFP(), new MULFP(), new DIVFP(), new NTFP(),  new FPTN(),  new UDIV(),  uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			uc,         uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,          uc,           uc,          uc,          uc,       uc,
			// @formatter:on
		};
		if (this.commands.length != 256) {
			throw new AssertionError("expected command-length=256 command-length=" + this.commands.length);
		}
		Map <String, Field> fields = new HashMap <>();
		for (Field f : PrimAsmCommands.class.getFields()) {
			fields.put(f.getName(), f);
		}
		for (int i = 0; i < this.commands.length; i ++ ) {
			PVMCommand c = this.commands[i];
			if (c == uc) continue;
			Field f = fields.remove(c.getClass().getSimpleName());
			try {
				int val = f.getInt(null);
				if (val != i) {
					throw new AssertionError("command at 0x" + Integer.toHexString(val) + " was not of the correct type! expected: " + f.getName() + " but got type: "
						+ c.getClass().getSimpleName());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new AssertionError(e);
			}
		}
		if ( !fields.isEmpty()) {
			throw new AssertionError("I do not suppoert all commands! missing: " + fields.values());
		}
	}
	
	public AbstractPVM init(int start, String[] args) throws OutOfMemoryError, AssertionError {
		try {
			putReg(IP, -1L);
			putReg(SP, -1L);
			putReg(STATUS, 0L);
			putReg(INTCNT, INTERRUPT_COUNT);
			long argc = args.length - start;
			long adr = malloc(argc << 3);
			for (int i = start; i < args.length; i ++ ) {
				char[] cs = args[i].toCharArray();
				long cl = ((long) cs.length) << 1L;
				long len = cl + 2L;
				long a = malloc(len);
				set(cs, a, cs.length);
				putChar(a + cl, '\0');
				putLong(adr, a);
				adr += 8L;
			}
			long addr = malloc(INTERRUPT_COUNT << 3);
			putReg(INTP, addr);
			memset(addr, INTERRUPT_COUNT << 3, (byte) -1);
			putLong(FS_LOCK, NO_LOCK);
			return this;
		} catch (PrimitiveErrror e) {
			AssertionError ae = new AssertionError(e.getLocalizedMessage(), e);
			ae.setStackTrace(e.getStackTrace());
			throw ae;
		}
	}
	
	@Override
	public void run() {
		while (true) {
			execute();
		}
	}
	
	protected void execute() {
		try {
			len = 8;
			off1 = true;
			off2 = 0;
			off3 = -8;
			ip = getReg(IP);
			cmd = getLong(ip);
			int cmdNum = (int) ( (cmd >>> 56L) & 0xFF);
			exeCmd(cmdNum);
		} catch (PrimitiveErrror e) {
			interrupt(e.intNum);
		}
	}
	
	protected void exeCmd(int cmdNum) throws PrimitiveErrror {
		this.commands[cmdNum].execute();
	}
	
	/*
	 * the getNC()/setNC() methods will not make any memory check!
	 * 
	 * so all memory checks has to be done before!
	 */
	private long getNC(long p) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			return getReg((int) p);
		} else {
			return getLong(p);
		}
	}
	
	private void setNC(long p, long val) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			putReg((int) p, val);
		} else {
			putLong(p, val);
		}
	}
	
	private long getNC(boolean isreg, long p) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			return getReg((int) p);
		} else {
			return getLong(p);
		}
	}
	
	private void setNC(boolean isreg, long p, long val) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			putReg((int) p, val);
		} else {
			putLong(p, val);
		}
	}
	
	private long getConstParam() throws PrimitiveErrror {
		switch ((int) (0xFF & (cmd >> (off1 ? 48 : 40)))) {
		case PARAM_ART_ANUM:
			len += 8;
			return getLong(ip + off2 ++ );
		case PARAM_ART_ANUM_BNUM:
			len += 16;
			long a = getLong(ip + (off2 += 8));
			long b = getLong(ip + (off2 += 8));
			long addr = a + b;
			return getLong(addr);
		case PARAM_ART_ANUM_BREG:
			len += 8;
			a = getLong(ip + (off2 += 8));
			return getLong(a);
		case PARAM_ART_ANUM_BSR:
			len += 8;
			a = getLong(ip + (off2 += 8));
			b = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			addr = a + b;
			return getLong(addr);
		case PARAM_ART_ASR:
			a = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			return a;
		case PARAM_ART_ASR_BNUM:
			len += 8;
			a = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			b = getLong(ip + (off2 += 8));
			addr = a + b;
			return getLong(addr);
		case PARAM_ART_ASR_BREG:
			a = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			return getLong(a);
		case PARAM_ART_ASR_BSR:
			a = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			b = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			addr = a + b;
			return getLong(addr);
		default:
			throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND);
		}
	}
	
	private long getNoConstParam() throws PrimitiveErrror {
		isreg = false;
		switch ((int) (0xFF & (cmd >> (off1 ? 48 : 40)))) {
		case PARAM_ART_ANUM:
			len += 8;
			return getLong(ip + off2 ++ );
		case PARAM_ART_ANUM_BNUM:
			len += 16;
			long a = getLong(ip + (off2 += 8));
			long b = getLong(ip + (off2 += 8));
			long addr = a + b;
			return addr;
		case PARAM_ART_ANUM_BREG:
			len += 8;
			a = getLong(ip + (off2 += 8));
			return (a);
		case PARAM_ART_ANUM_BSR:
			len += 8;
			a = getLong(ip + (off2 += 8));
			b = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			addr = a + b;
			return addr;
		case PARAM_ART_ASR:
			a = (cmd >> (off3 += 8) & 0xFF);
			isreg = true;
			return a;
		case PARAM_ART_ASR_BNUM:
			len += 8;
			a = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			b = getLong(ip + (off2 += 8));
			addr = a + b;
			return addr;
		case PARAM_ART_ASR_BREG:
			a = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			return (a);
		case PARAM_ART_ASR_BSR:
			a = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			b = getReg((int) (cmd >> (off3 += 8) & 0xFF));
			addr = a + b;
			return addr;
		default:
			throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND);
		}
	}
	
	private void interrupt(long intNum) {
		while (true) {
			try {
				anyInt(intNum);
			} catch (PrimitiveErrror e) {
				intNum = e.intNum;
				continue;
			}
			break;
		}
	}
	
	protected void anyInt(long intNum) throws PrimitiveErrror {
		long intcnt = getReg(INTCNT);
		if (intNum < 0L || intNum >= intcnt) {
			if (intcnt > 0L) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_INTERRUPT);
			} else {
				System.exit(128);
			}
		} else {
			long intp = getReg(INTP);
			if (intp == -1L) {
				defInt(intNum);
			} else {
				long addr = intp + (intNum << 3);
				long address = getLong(addr);
				if (address == -1L) {
					defInt(intNum);
				} else {
					try {
						long save = malloc(128L);
						getregs(save);
						putReg(X_ADD + 9, save);
						putReg(IP, address);
					} catch (OutOfMemoryError e) {
						throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
					}
				}
			}
		}
	}
	
	protected void defInt(long intNum) throws PrimitiveErrror {
		if (intNum >= INTERRUPT_COUNT) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_INTERRUPT);
		} else {
			this.defaultInts[(int) intNum].execute((int) intNum);
		}
	}
	
	private String getU16String(long address) throws PrimitiveErrror {
		char[] cs = new char[16];
		int len;
		int off;
		for (len = 0, off = 0;; len ++ , off ++ , address += 2L) {
			if (len > cs.length) {
				char[] o = cs;
				cs = new char[cs.length + cs.length >>> 1];
				cpy(o, cs, off);
			}
			char c = getChar(address);
			if (c == '\0') break;
			cs[off] = c;
		}
		return new String(cs, 0, len);
	}
	
	private String getU8String(long address) throws PrimitiveErrror {
		byte[] bytes = new byte[16];
		int len;
		int off = 0;
		for (len = 0;; len ++ , off ++ ) {
			if (len >= bytes.length) {
				bytes = Arrays.copyOf(bytes, len + len >> 1);
			}
			byte b = getByte(address + len);
			if (bytes[len] == 0) {
				break;
			}
			bytes[off] = b;
		}
		return new String(bytes, 0, len, StandardCharsets.UTF_8);
	}
	
	
	private String[] getPathNames(long address) throws PrimitiveErrror {
		String[] s = new String[4];
		int si = 0;
		char[] cs = new char[16];
		int ci;
		for (ci = 0;; ci ++ , address += 2L) {
			char c = getChar(address);
			if (c == '\0') break;
			if (c == '/') {
				if (si >= s.length) {
					String[] o = s;
					s = new String[s.length + s.length >>> 1];
					cpy(o, s, si);
				}
				s[si] = new String(cs, 0, ci);
				ci = 0;
				si ++ ;
				continue;
			}
			if (ci >= cs.length) {
				char[] o = cs;
				cs = new char[cs.length + cs.length >>> 1];
				cpy(o, cs, ci);
			}
			cs[ci] = c;
		}
		if (si + 1 != s.length) {
			String[] o = s;
			s = new String[si + 1];
			cpy(o, s, si);
		}
		s[si] = new String(cs, 0, ci);
		return s;
	}
	
	private void defIntLoadFile() throws PrimitiveErrror {
		String[] names = getPathNames(getReg(X_ADD));
		try {
			PatrFolder parent = fs.getRoot();
			for (int i = names[0].isEmpty() ? 1 : 0; i < names.length - 1; i ++ ) {
				parent = parent.getElement(names[i], NO_LOCK).getFolder();
			}
			PatrFile file = parent.getElement(names[names.length - 1], NO_LOCK).getFile();
			long len = file.length(NO_LOCK);
			long addr = malloc(len);
			byte[] bytes = new byte[(int) Math.min(MAX_BUFFER, len)];
			for (long wrote = 0L; wrote < len;) {
				int cpy = (int) Math.min(bytes.length, len - wrote);
				file.getContent(bytes, wrote, 0, cpy, NO_LOCK);
				set(bytes, addr + wrote, cpy);
			}
			putReg(X_ADD, addr);
			putReg(X_ADD + 1, len);
		} catch (OutOfMemoryError e) {
			fail(X_ADD, STATUS_OUT_OF_MEMORY);
		} catch (IllegalStateException e) {
			fail(X_ADD, STATUS_ELEMENT_WRONG_TYPE);
		} catch (NoSuchFileException e) {
			fail(X_ADD, STATUS_ELEMENT_NOT_EXIST);
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntStringConvert(int intNum) throws PrimitiveErrror {
		boolean toU8;
		switch (intNum) {
		case (int) INT_STR_TO_U8STR:
			toU8 = true;
			break;
		case (int) INT_U8STR_TO_STR:
			toU8 = false;
			break;
		default:
			throw new InternalError();
		}
		String input = toU8 ? getU16String(getReg(X_ADD)) : getU8String(getReg(X_ADD));
		byte[] result = input.getBytes(toU8 ? StandardCharsets.UTF_8 : STRING_U16);
		long len = result.length + (toU8 ? 1L : 2L);
		long x02 = getReg(X_ADD + 2);
		long x01 = getReg(X_ADD + 1);
		if (x02 < len) {
			long addr;
			try {
				if (x02 == 0L) {
					addr = malloc(len);
					putReg(X_ADD + 1, addr);
					x01 = addr;
				} else if (x02 > 0L) {
					addr = realloc(x01, len);
					putReg(X_ADD + 1, addr);
					x01 = addr;
				} else {
					fail(X_ADD + 3, STATUS_ILLEGAL_ARG);
					return;
				}
			} catch (OutOfMemoryError e) {
				fail(X_ADD + 3, STATUS_OUT_OF_MEMORY);
				return;
			}
			putReg(X_ADD + 2, len);
		}
		for (int i = 0; i < result.length; i ++ ) {
			putByte(x01 + i, result[i]);
		}
		if (toU8) {
			putByte(x01 + result.length, (byte) 0);
		} else {
			putChar(x01 + result.length, '\0');
		}
		putReg(X_ADD + 3, x01 + result.length);
	}
	
	private void defIntFormattString() throws PrimitiveErrror {
		String input = getU16String(getReg(X_ADD));
		char[] cs = input.toCharArray();
		StringBuilder result = new StringBuilder();
		int argReg = X_ADD + 3;
		for (int i = 0; i < input.length();) {
			int index = input.indexOf('%', i);
			result.append(cs, i, index - i);
			if (index + 1 >= input.length()) {
				fail(X_ADD, STATUS_ILLEGAL_ARG);
				return;
			}
			char c = input.charAt(index + 1);
			if (c == '%') {
				result.append('%');
				continue;
			}
			if (argReg >= 256L << 3) {
				fail(X_ADD, STATUS_ILLEGAL_ARG);
				return;
			}
			long arg = getReg(argReg);
			argReg ++ ;
			switch (c) {
			case 's':
				result.append(getU16String(arg));
				break;
			case 'c':
				result.append((char) arg);
				break;
			case 'B':
				result.append((char) (byte) arg);
				break;
			case 'd':
				result.append(Long.toString(arg, 10));
				break;
			case 'f':
				result.append(Double.toString(Double.longBitsToDouble(arg)));
				break;
			case 'p':
				if (arg == -1L) {
					result.append("p-inval");
				} else {
					result.append("p-").append(Long.toHexString(arg));
				}
				break;
			case 'h':
				result.append(Long.toString(arg, 16));
				break;
			case 'b':
				result.append(Long.toString(arg, 2));
				break;
			case 'o':
				result.append(Long.toString(arg, 8));
				break;
			default:
				fail(X_ADD, STATUS_ILLEGAL_ARG);
				return;
			}
			i = index + 2;
		}
		char[] CS = result.toString().toCharArray();
		long addr;
		long x02 = getReg(X_ADD + 2);
		long length = ((long) CS.length) << 1 + 2L;
		if (x02 < length) {
			try {
				if (x02 == 0L) {
					addr = malloc(length);
					putReg(X_ADD + 1, addr);
				} else if (x02 > 0L) {
					long oadr = getReg(X_ADD + 1);
					addr = realloc(oadr, length);
					putReg(X_ADD + 2, addr);
				} else {
					fail(X_ADD, STATUS_ILLEGAL_ARG);
					return;
				}
			} catch (OutOfMemoryError e) {
				fail(X_ADD, STATUS_OUT_OF_MEMORY);
				return;
			}
			putReg(X_ADD + 2, CS.length + 2L);
		} else {
			addr = getReg(X_ADD + 1);
		}
		set(cs, addr, (int) ( (length - 2L) >> 1));
		putChar(addr + length - 2L, '\0');
	}
	
	private void defIntStringToAnyNumber(int intNum) throws PrimitiveErrror {
		String str = getU16String(getReg(X_ADD)).trim();
		try {
			switch (intNum) {
			case (int) INT_STRING_TO_NUMBER: {
				long x01 = getReg(X_ADD + 1);
				int numsys = (int) x01;
				if ( ((long) numsys) != x01 || numsys < 2 || numsys > 36) {
					fail( -X_ADD - 1, STATUS_ILLEGAL_ARG);
					return;
				}
				putReg(X_ADD, Long.parseLong(str, numsys));
				break;
			}
			case (int) INT_STRING_TO_FPNUMBER: {
				putReg(X_ADD, Double.doubleToRawLongBits(Double.parseDouble(str)));
				break;
			}
			default:
				throw new InternalError();
			}
		} catch (NumberFormatException nfe) {
			fail( -X_ADD - 1, STATUS_ILLEGAL_ARG);
		}
	}
	
	private void defIntAnyNumberToString(int intNum) throws PrimitiveErrror {
		byte[] bytes;
		int lenReg;
		switch (intNum) {
		case (int) INT_NUMBER_TO_STRING: {
			long x02 = getReg(X_ADD + 2);
			if (x02 > 36L || x02 < 2L) {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
				return;
			}
			bytes = Long.toString(getReg(X_ADD), (int) x02).toUpperCase().getBytes(STRING_U16);
			lenReg = X_ADD + 3;
			break;
		}
		case (int) INT_FPNUMBER_TO_STRING:
			bytes = Double.toString(Double.longBitsToDouble(getReg(X_ADD))).getBytes(STRING_U16);
			lenReg = X_ADD + 2;
			break;
		default:
			throw new InternalError();
		}
		long addr = getReg(X_ADD + 1);
		long lenOffVal = getLong(lenReg);
		if (bytes.length + 2L > lenOffVal) {
			try {
				if (lenOffVal == 0L) {
					addr = malloc(bytes.length + 2L);
				} else if (lenOffVal > 0L) {
					addr = realloc(addr, bytes.length + 2L);
				} else {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
			} catch (OutOfMemoryError e) {
				fail(X_ADD + 1, STATUS_OUT_OF_MEMORY);
				return;
			}
			putLong(lenReg, bytes.length + 2L);
		}
		set(bytes, addr, bytes.length);
		putChar(addr + bytes.length, '\0');
	}
	
	private void defIntSleep() {
		long start = System.nanoTime();
		try {
			long secs = getReg(X_ADD + 2);
			long nanos = getReg(X_ADD + 1),
				millis2 = nanos / 1000000L;
			int nanosi = (int) (nanos % 1000000L);
			Thread.sleep( (secs * 1000L) + millis2, nanosi);
			putReg(X_ADD + 1, 0L);
			putReg(X_ADD + 2, 0L);
		} catch (InterruptedException e) {
			long remain = System.nanoTime() - start;
			long r1 = remain / 1000000000L;
			long r2 = remain % 1000000000L;
			putReg(X_ADD + 1, r1);
			putReg(X_ADD + 1, r2);
		}
	}
	
	private void defIntFSUnlock() throws PrimitiveErrror {
		try {
			fs.setLock(getLong(FS_LOCK));
			fs.removeLock();
			putReg(FS_LOCK, NO_LOCK);
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntFSLock() throws PrimitiveErrror {
		try {
			putReg(FS_LOCK, fs.lock(getReg(X_ADD)));
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntLinkSetTarget() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK),
			ntaddress = getReg(X_ADD + 1);
		long ntid = getLong(ntaddress + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrLink e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getLink();
			PatrFileSysElement nt = fs.fromID(new PatrID(fs, ntid, fs.getStartTime()));
			e.setTarget(nt, lock);
		} catch (OutOfSpaceException e) {
			fail(X_ADD + 1, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD + 1, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntLinkGetTarget() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getLink().getTarget(lock);
			putLong(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) e).id);
			putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
		} catch (OutOfSpaceException e) {
			fail(X_ADD + 1, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD + 1, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileTruncate() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			long newLen = getReg(X_ADD + 1);
			e.truncate(newLen, lock);
		} catch (OutOfSpaceException e) {
			fail(X_ADD + 1, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD + 1, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileRWA(int intNum) throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			final long length = getReg(X_ADD + 1);
			long addr = getReg(X_ADD + 2);
			
			byte[] bytes = new byte[(int) Math.min(length, MAX_BUFFER)];
			long copied;
			long x03 = getReg(X_ADD + 3);
			for (copied = 0L; copied < length;) {
				int cpy = (int) Math.min(bytes.length, length - copied);
				switch (intNum) {
				case (int) INT_FS_FILE_READ:
					e.getContent(bytes, x03, 0, cpy, lock);
					set(bytes, addr, cpy);
					break;
				case (int) INT_FS_FILE_WRITE:
				case (int) INT_FS_FILE_APPEND:
					get(addr, bytes, cpy);
					if (intNum == (int) INT_FS_FILE_APPEND) {
						e.appendContent(bytes, 0, cpy, lock);
					} else {
						e.setContent(bytes, x03, 0, cpy, lock);
					}
					break;
				default:
					throw new InternalError();
				}
				addr += cpy;
			}
			assert copied == length;
		} catch (OutOfSpaceException e) {
			fail(X_ADD + 1, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD + 1, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileHash() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			final long addr = getReg(X_ADD + 1);
			byte[] bytes = e.getHashCode(lock);
			assert bytes.length == 32;
			
			set(bytes, addr, bytes.length);
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileLength() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			putReg(X_ADD + 1, e.length(lock));
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntAddElement(int intNum) throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getReg(FS_LOCK));
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			String name = getU16String(getReg(X_ADD + 1));
			PatrFileSysElement child;
			switch (intNum) {
			case (int) INT_FS_FOLDER_ADD_FILE:
				child = e.addFile(name, lock);
				break;
			case (int) INT_FS_FOLDER_ADD_FOLDER:
				child = e.addFolder(name, lock);
				break;
			case (int) INT_FS_FOLDER_ADD_LINK: {
				final long taddress = getReg(X_ADD + 2);
				final long tid = getLong(taddress + FS_ELEMENT_OFFSET_ID);
				PatrFolder te = fs.fromID(new PatrID(fs, tid, fs.getStartTime())).getFolder();
				child = e.addLink(name, te, lock);
				break;
			}
			default:
				throw new InternalError();
			}
			putLong(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) child).id);
			putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
		} catch (FileAlreadyExistsException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_ALREADY_EXIST);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD + 1, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetChildElement(int intNum) throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			PatrFileSysElement child;
			long x01 = getReg(X_ADD + 1);
			switch (intNum) {
			case (int) INT_FS_FOLDER_GET_CHILD_OF_INDEX: {
				int index = (int) x01;
				if (index != x01) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				child = e.getElement(index, lock);
				break;
			}
			case (int) INT_FS_FOLDER_GET_CHILD_OF_NAME: {
				String name = getU16String(x01);
				child = e.getElement(name, lock);
				break;
			}
			default:
				throw new InternalError();
			}
			putLong(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) child).id);
			putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetFolderChildElementCount() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID), lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			putReg(X_ADD + 1, e.elementCount(lock));
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntModifyElementFlags() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			((PatrFileSysElementImpl) e).flag(lock, (int) getReg(X_ADD + 1), (int) getReg(X_ADD + 2));
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetElementFlags() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			putReg(X_ADD, 0x00000000FFFFFFFFL & ((PatrFileSysElementImpl) e).getFlags());
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntMoveElement() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK),
			npaddress = getReg(X_ADD + 1);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			if (npaddress != -1L) {
				
				final long npid = getLong(npaddress + FS_ELEMENT_OFFSET_ID),
					nplock = getLong(npaddress + FS_ELEMENT_OFFSET_LOCK);
				PatrFolder np = fs.fromID(new PatrID(fs, npid, fs.getStartTime())).getFolder();
				e.setParent(np, lock, getReg(X_ADD + 3), nplock);
			}
			long x02 = getReg(X_ADD + 2);
			if (x02 != -1L) {
				String newName = getU16String(x02);
				e.setName(newName, lock);
			}
		} catch (OutOfSpaceException e) {
			fail(X_ADD, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X_ADD, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntDeleteElement() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			e.delete(lock, getReg(X_ADD + 1));
			free(address);
		} catch (OutOfSpaceException e) {
			fail(X_ADD, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntUnlockElement() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			e.removeLock(lock);
			putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			putReg(X_ADD + 1, 1L);
		} catch (ElementLockedException e) {
			fail( -X_ADD - 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail( -X_ADD - 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail( -X_ADD - 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntLockElement() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		// lock set
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			putLong(address + FS_ELEMENT_OFFSET_LOCK, e.lock(getReg(X_ADD + 1)));
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetLockDate() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			putReg(X_ADD + 1, e.getLockTime());
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetLockData() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			putReg(X_ADD, e.getLockData());
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntSetSomeDate(int intNum) throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(getReg(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			switch (intNum) {
			case (int) INT_FS_ELEMENT_SET_CREATE:
				e.setCreateTime(getReg(X_ADD + 1), lock);
				break;
			case (int) INT_FS_ELEMENT_SET_LAST_MOD:
				e.setLastModTime(getReg(X_ADD + 1), lock);
				break;
			case (int) INT_FS_ELEMENT_SET_LAST_META_MOD:
				e.setLastMetaModTime(getReg(X_ADD + 1), lock);
				break;
			default:
				throw new InternalError();
			}
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetSomeDate(int intNum) throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(getReg(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			switch (intNum) {
			case (int) INT_FS_ELEMENT_GET_CREATE:
				putReg(X_ADD + 1, e.getCreateTime());
				break;
			case (int) INT_FS_ELEMENT_GET_LAST_MOD:
				putReg(X_ADD + 1, e.getLastModTime());
				break;
			case (int) INT_FS_ELEMENT_GET_LAST_META_MOD:
				putReg(X_ADD + 1, e.getLastMetaModTime());
				break;
			default:
				throw new InternalError();
			}
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntFromID() throws PrimitiveErrror {
		final long id = getReg(X_ADD);
		try {
			fs.setLock(getReg(FS_LOCK));
			fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			long addr = malloc(16L);
			putLong(addr + FS_ELEMENT_OFFSET_ID, id);
			putLong(addr + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			putReg(X_ADD, addr);
		} catch (OutOfMemoryError e) {
			fail(X_ADD, STATUS_OUT_OF_MEMORY);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetParent() throws PrimitiveErrror {
		final long address = getReg(X_ADD);
		final long id = getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(getLong(FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			PatrFolder parent = e.getParent();
			putReg(X_ADD, ((PatrFileSysElementImpl) parent).id);
			putReg(X_ADD + 1, 1L);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail( -X_ADD - 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail( -X_ADD - 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntDuplicateFSElementHandle() throws PrimitiveErrror {
		try {
			long address = getReg(X_ADD),
				dupAddr = malloc(16L);
			memcpy(address, dupAddr, 16L);
			putReg(X_ADD, dupAddr);
		} catch (OutOfMemoryError e) {
			fail( -X_ADD, STATUS_OUT_OF_MEMORY);
		}
	}
	
	private void defIntGetFSElement(int intNum) throws PrimitiveErrror {
		String path = getU16String(getReg(X_ADD));
		String[] names = path.split("\\/");
		try {
			PatrFolder parent = fs.getRoot();
			for (int i = 0; i < names.length - 1; i ++ ) {
				parent = parent.getElement(names[i], NO_LOCK).getFolder();
			}
			PatrFileSysElement file = parent.getElement(names[names.length - 1], NO_LOCK);
			switch (intNum) {
			case (int) INT_FS_GET_FILE:
				if ( !file.isFile()) {
					fail(X_ADD, STATUS_ELEMENT_WRONG_TYPE);
					return;
				}
				break;
			case (int) INT_FS_GET_FOLDER:
				if ( !file.isFolder()) {
					fail(X_ADD, STATUS_ELEMENT_WRONG_TYPE);
					return;
				}
				break;
			case (int) INT_FS_GET_LINK:
				if ( !file.isLink()) {
					fail(X_ADD, STATUS_ELEMENT_WRONG_TYPE);
					return;
				}
			case (int) INT_FS_GET_ELEMENT:
				break;
			default:
				throw new AssertionError();
			}
			long addr = malloc(16L);
			putLong(addr + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) file).id);
			putLong(addr + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			putReg(X_ADD, addr);
		} catch (OutOfMemoryError e) {
			fail(X_ADD, STATUS_OUT_OF_MEMORY);
		} catch (IllegalStateException e) {
			fail(X_ADD, STATUS_ELEMENT_WRONG_TYPE);
		} catch (NoSuchFileException e) {
			fail(X_ADD, STATUS_ELEMENT_NOT_EXIST);
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntRead() throws PrimitiveErrror {
		final long id = getReg(X_ADD),
			len = getReg(X_ADD + 1),
			addr = getReg(X_ADD + 2);
		
		if (id <= MAX_STD_STREAM) {
			InputStream in;
			if (id == STD_IN) {
				in = stdin;
			} else {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
				return;
			}
			byte[] bytes = new byte[(int) Math.min(len, MAX_BUFFER)];
			for (long reat = 0L; reat < len;) {
				try {
					int r = in.read(bytes);
					set(bytes, addr + reat, r);
					reat += r;
				} catch (IOException e) {
					if (reat == 0L) fail(X_ADD + 1, STATUS_IO_ERR);
					else putReg(X_ADD + 1, reat);
					return;
				}
			}
		} else {
			try {
				long pos = getLong(id + FS_STREAM_OFFSET_POS), fileAddr = getLong(id + FS_STREAM_OFFSET_FILE), mode = getLong(id + 16L),
					fid = getLong(fileAddr + FS_ELEMENT_OFFSET_ID), lock = getLong(fileAddr + FS_ELEMENT_OFFSET_LOCK);
				if ( (mode & OPEN_READ) != 0) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				fs.setLock(getLong(FS_LOCK));
				PatrFile file;
				try {
					file = fs.fromID(new PatrID(fs, fid, fs.getStartTime())).getFile();
				} catch (IllegalStateException e) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				byte[] bytes = new byte[(int) Math.min(MAX_BUFFER, len)];
				file.withLock(() -> {
					try {
						long npos = pos;
						long length = file.length(lock);
						for (long reat = 0L; reat < len;) {
							if (npos >= length) {
								assert npos == length;
								putLong(id + FS_STREAM_OFFSET_POS, npos);
								putReg(X_ADD + 1, reat);
								return;
							}
							int cpy = (int) Math.min(len - reat, Math.min(length - npos, bytes.length));
							file.getContent(bytes, npos, 0, cpy, lock);
							set(bytes, addr + reat, cpy);
							npos += cpy;
							reat += cpy;
							putLong(id + FS_STREAM_OFFSET_POS, npos);
						}
					} catch (ElementLockedException e) {
						fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
						return;
					} catch (IOException e) {
						fail(X_ADD + 1, STATUS_IO_ERR);
						return;
					}
					putReg(X_ADD + 1, len);
				});
				return;
			} catch (IOException e) {
				fail(X_ADD + 1, STATUS_IO_ERR);
				return;
			}
		}
	}
	
	private void defIntWrite() throws PrimitiveErrror {
		final long id = getReg(X_ADD),
			len = getReg(X_ADD + 1),
			addr = getReg(X_ADD + 2);
		
		if (id <= MAX_STD_STREAM) {
			OutputStream out;
			if (id == STD_OUT) {
				out = stdout;
			} else if (id == STD_LOG) {
				out = stdlog;
			} else {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
				return;
			}
			if (len < 0L) {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
			}
			byte[] bytes = new byte[(int) Math.min(MAX_BUFFER, len)];
			for (long wrote = 0L; wrote < len;) {
				int cpy = (int) Math.min(len - wrote, bytes.length);
				get(addr + wrote, bytes, cpy);
				try {
					out.write(bytes, 0, cpy);
				} catch (IOException e) {
					fail(X_ADD + 1, STATUS_IO_ERR);
					return;
				}
			}
		} else {
			try {
				final long pos = getLong(id + FS_STREAM_OFFSET_POS), fileAddr = getLong(id + FS_STREAM_OFFSET_FILE), mode = getLong(id + 16L),
					fid = getLong(fileAddr + FS_ELEMENT_OFFSET_ID), lock = getLong(fileAddr + FS_ELEMENT_OFFSET_LOCK);
				if ( (mode & OPEN_WRITE) != 0) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				fs.setLock(getLong(FS_LOCK));
				PatrFile file;
				try {
					file = fs.fromID(new PatrID(fs, fid, fs.getStartTime())).getFile();
				} catch (IllegalStateException e) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				boolean append = (mode & OPEN_APPEND) != 0;
				byte[] bytes = new byte[(int) Math.min(MAX_BUFFER, len)];
				file.withLock(() -> {
					long npos = pos;
					for (long wrote = 0L; wrote < len;) {
						try {
							int maxcpy = (int) Math.min(wrote - len, bytes.length);
							get(addr + wrote, bytes, maxcpy);
							long length = file.length(lock);
							int cpy = 0;
							if ( !append && npos < length) {
								cpy = (int) Math.min(length - npos, maxcpy);
								file.setContent(bytes, npos, 0, cpy, lock);
								npos += cpy;
								wrote += cpy;
							}
							int cop = (int) Math.min(len - wrote, bytes.length - cpy);
							if (cop > 0) {
								file.appendContent(bytes, cpy, cop, lock);
								cpy = 0;
								npos += cop;
								wrote += cop;
							}
							putLong(id + FS_STREAM_OFFSET_POS, npos);
						} catch (OutOfSpaceException e) {
							fail(X_ADD + 1, STATUS_OUT_OF_SPACE);
							return;
						} catch (ElementReadOnlyException e) {
							fail(X_ADD + 1, STATUS_READ_ONLY);
							return;
						} catch (ElementLockedException e) {
							fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
							return;
						} catch (IOException e) {
							fail(X_ADD + 1, STATUS_IO_ERR);
							return;
						}
					}
				});
				return;
			} catch (IOException e) {
				fail(X_ADD + 1, STATUS_IO_ERR);
				return;
			}
		}
	}
	
	private void defIntOpenNewStream() throws PrimitiveErrror {
		String path = getU16String(getReg(X_ADD));
		String[] names = path.split("\\/");
		try {
			PatrFolder parent = fs.getRoot();
			for (int i = 0; i < names.length - 1; i ++ ) {
				parent = parent.getElement(names[i], NO_LOCK).getFolder();
			}
			long mode = getReg(X_ADD + 1);
			if ( (mode & OPEN_APPEND) != 0) {
				mode |= OPEN_WRITE;
			}
			if ( (mode & (OPEN_READ | OPEN_WRITE)) != 0) {
				fail(X_ADD, STATUS_ILLEGAL_ARG);
				return;
			}
			if ( (mode & (OPEN_CREATE | OPEN_NEW_FILE | OPEN_TRUNCATE)) != 0) {
				if ( (mode & OPEN_WRITE) != 0) {
					fail(X_ADD, STATUS_ILLEGAL_ARG);
					return;
				}
			}
			if ( (mode & OPEN_NEW_FILE) != 0 && (mode & (OPEN_CREATE | OPEN_TRUNCATE)) != 0) {
				fail(X_ADD, STATUS_ILLEGAL_ARG);
				return;
			}
			PatrFile file;
			try {
				file = parent.getElement(names[names.length - 1], NO_LOCK).getFile();
				if ( (mode & OPEN_NEW_FILE) != 0) {
					fail(X_ADD, STATUS_ELEMENT_ALREADY_EXIST);
					return;
				}
			} catch (IllegalStateException e) {
				fail(X_ADD, STATUS_ELEMENT_WRONG_TYPE);
				return;
			} catch (NoSuchFileException e) {
				if ( (mode & (OPEN_NEW_FILE | OPEN_CREATE)) == 0) {
					fail(X_ADD, STATUS_ELEMENT_NOT_EXIST);
					return;
				}
				file = parent.addFile(names[names.length - 1], NO_LOCK);
			}
			if ( (mode & OPEN_TRUNCATE) != 0) {
				file.removeContent(NO_LOCK);
			}
			long checkBits = 0;
			boolean rof = false;
			if ( (mode & OPEN_WRITE) != 0) {
				checkBits |= PatrFileSysConstants.LOCK_NO_WRITE_ALLOWED_LOCK;
			}
			if ( (mode & OPEN_READ) != 0) {
				checkBits |= PatrFileSysConstants.LOCK_NO_READ_ALLOWED_LOCK;
				rof = true;
			}
			file.ensureAccess(NO_LOCK, checkBits, rof);
			long address;
			address = malloc(40L);
			putLong(address + FS_STREAM_OFFSET_FILE, address + 24L);
			putLong(address + FS_STREAM_OFFSET_POS, 0L);
			putLong(address + 16L, mode);
			putLong(address + 24L + FS_ELEMENT_OFFSET_ID, ((PatrFileImpl) file).id);
			putLong(address + 24L + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			putReg(X_ADD, address);
		} catch (ElementReadOnlyException e) {
			fail(X_ADD, STATUS_READ_ONLY);
			return;
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
			return;
		} catch (OutOfMemoryError e) {
			fail(X_ADD, STATUS_OUT_OF_MEMORY);
			return;
		} catch (OutOfSpaceException e) {
			fail(X_ADD, STATUS_OUT_OF_SPACE);
			return;
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
			return;
		}
	}
	
	private void fail(long markingRegisterOff, long newStatusFlag) {
		if (markingRegisterOff > 0) putReg((int) markingRegisterOff, -1L);
		else putReg((int) -markingRegisterOff, 0L);
		putReg(STATUS, getReg(STATUS) | newStatusFlag);
	}
	
	private abstract class Cmd_1CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam();
			exec(p1);
			putReg(IP, getReg(IP) + len);
		}
		
		protected abstract void exec(long p1) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_2CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam(), p2 = getConstParam();
			exec(p1, p2);
			putReg(IP, getReg(IP) + len);
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1NCP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getNoConstParam();
			exec(p1);
			putReg(IP, getReg(IP) + len);
		}
		
		protected abstract void exec(long p1) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_2NCP_AL implements PVMCommand {
		
		protected boolean isregp1;
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getNoConstParam();
			isregp1 = isreg;
			long p2 = getNoConstParam();
			exec(p1, p2);
			putReg(IP, getReg(IP) + len);
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1NCP_1CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getNoConstParam();
			long p2 = getConstParam();
			exec(p1, p2);
			putReg(IP, getReg(IP) + len);
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1LP_IL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getLong(getReg(IP) + 8L);
			exec(p1);
		}
		
		protected abstract void exec(long p1) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1CP_1LP_NL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam(), p2 = getLong(getReg(IP) + 8L);
			exec(p1, p2);
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private class MOV extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			setNC(p1, p2);
		}
		
	}
	
	private class ADD extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v + p2;
			setNC(p1, res);
			if (p1v > 0L && p2 > 0L) {
				if (res < 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) { //not possible
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) { // only with (MIN + MIN)
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class SUB extends Cmd_1NCP_1CP_AL {
		
		@SuppressWarnings("unused")
		static final long l = Long.MIN_VALUE - Long.MAX_VALUE;
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v - p2;
			setNC(p1, res);
			if (p1v > 0L && p2 < 0L) {
				if (res < 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) {
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 > 0L) {
				if (res > 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) { // not possible
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class MUL extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v * p2;
			setNC(p1, res);
			if (p1v > 0L && p2 > 0L) {
				if (res < 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class DIV extends Cmd_2NCP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(isregp1, p1);
			long p2v = getNC(p2);
			setNC(p1, p1v / p2v);
			setNC(isregp1, p2, p1v % p2v);
		}
		
	}
	
	private class AND extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v & p2;
			setNC(p1, res);
			if (res == 0L) {
				putReg(STATUS, getReg(STATUS) | STATUS_ZERO);
			} else {
				putReg(STATUS, getReg(STATUS) & ~STATUS_ZERO);
			}
		}
		
	}
	
	private class OR extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v | p2;
			setNC(p1, res);
			if (res == 0L) {
				putReg(STATUS, getReg(STATUS) | STATUS_ZERO);
			} else {
				putReg(STATUS, getReg(STATUS) & ~STATUS_ZERO);
			}
		}
		
	}
	
	private class XOR extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v ^ p2;
			setNC(p1, res);
			if (res == 0L) {
				putReg(STATUS, getReg(STATUS) | STATUS_ZERO);
			} else {
				putReg(STATUS, getReg(STATUS) & ~STATUS_ZERO);
			}
		}
		
	}
	
	private class NOT extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = ~p1v;
			setNC(p1, res);
			if (res == 0L) {
				putReg(STATUS, getReg(STATUS) | STATUS_ZERO);
			} else {
				putReg(STATUS, getReg(STATUS) & ~STATUS_ZERO);
			}
		}
		
	}
	
	private class NEG extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = -p1v;
			setNC(p1, res);
			if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else if (res == Long.MIN_VALUE) {
				putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class LSH extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v << p2;
			setNC(p1, res);
			if (res >>> p2 != p1) {
				if (res == 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_CARRY | STATUS_ZERO));
			}
		}
		
	}
	
	private class RLSH extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v >>> p2;
			setNC(p1, res);
			if (res << p2 != p1) {
				if (res == 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_CARRY | STATUS_ZERO));
			}
		}
		
	}
	
	
	private class RASH extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v >> p2;
			setNC(p1, res);
			if (res << p2 != p1) {
				if (res == 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class DEC extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v - 1;
			setNC(p1, res);
			if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else if (res == Long.MAX_VALUE) {
				putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class INC extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v + 1;
			setNC(p1, res);
			if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else if (res == Long.MIN_VALUE) {
				putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	// uc, new MOV(), new ADD(), new SUB(), new MUL(), new DIV(), new AND(), new OR(), new XOR(), new
	// NOT(), new NEG(), new LSH(), new RLSH(), new RASH(), new DEC(), new INC(),
	
	private class JMP extends Cmd_1LP_IL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			putReg(IP, getReg(IP) + p1);
		}
		
	}
	
	private abstract class PosCondJmp extends Cmd_1LP_IL {
		
		private final long cond;
		
		private PosCondJmp(long cond) {
			this.cond = cond;
		}
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			if ( (getReg(STATUS) & cond) != 0) {
				putReg(IP, getReg(IP) + p1);
			} else {
				putReg(IP, getReg(IP) + len);
			}
		}
		
	}
	
	private abstract class NegCondJmp extends Cmd_1LP_IL {
		
		private final long cond;
		
		private NegCondJmp(long cond) {
			this.cond = cond;
		}
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			if ( (getReg(STATUS) & cond) == 0) {
				putReg(IP, getReg(IP) + p1);
			} else {
				putReg(IP, getReg(IP) + len);
			}
		}
		
	}
	
	private class JMPEQ extends PosCondJmp {
		
		public JMPEQ() {
			super(STATUS_EQUAL);
		}
		
	}
	
	private class JMPNE extends NegCondJmp {
		
		public JMPNE() {
			super(STATUS_EQUAL);
		}
		
	}
	
	private class JMPGT extends PosCondJmp {
		
		public JMPGT() {
			super(STATUS_GREATHER);
		}
		
	}
	
	private class JMPGE extends PosCondJmp {
		
		public JMPGE() {
			super(STATUS_EQUAL | STATUS_GREATHER);
		}
		
	}
	
	private class JMPLT extends PosCondJmp {
		
		public JMPLT() {
			super(STATUS_LOWER);
		}
		
	}
	
	private class JMPLE extends PosCondJmp {
		
		public JMPLE() {
			super(STATUS_EQUAL | STATUS_LOWER);
		}
		
	}
	
	private class JMPCS extends PosCondJmp {
		
		public JMPCS() {
			super(STATUS_CARRY);
		}
		
	}
	
	private class JMPCC extends NegCondJmp {
		
		public JMPCC() {
			super(STATUS_CARRY);
		}
		
	}
	
	private class JMPZS extends PosCondJmp {
		
		public JMPZS() {
			super(STATUS_ZERO);
		}
		
	}
	
	private class JMPZC extends NegCondJmp {
		
		public JMPZC() {
			super(STATUS_ZERO);
		}
		
	}
	
	private class JMPNAN extends PosCondJmp {
		
		public JMPNAN() {
			super(STATUS_NAN);
		}
		
	}
	
	private class JMPAN extends NegCondJmp {
		
		public JMPAN() {
			super(STATUS_NAN);
		}
		
	}
	
	// new JMP(), new JMPEQ(), new JMPNE(), new JMPGT(), new JMPGE(), new JMPLT(), new JMPLE(), new
	// JMPCS(), new JMPCC(), new JMPZS(), new JMPZC(), new JMPNAN(), new JMPAN(), uc, uc, uc,
	
	private class CALL extends Cmd_1LP_IL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long sp = getReg(SP);
			long ip = getReg(IP);
			putLong(sp, ip);
			putReg(SP, sp + 8L);
			putReg(IP, ip + p1);
		}
		
	}
	
	private class CMP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			if (p1 > p2) {
				putReg(STATUS, (getReg(STATUS) | STATUS_GREATHER) & ~ (STATUS_EQUAL | STATUS_LOWER));
			} else if (p1 < p2) {
				putReg(STATUS, (getReg(STATUS) | STATUS_LOWER) & ~ (STATUS_EQUAL | STATUS_GREATHER));
			} else {
				putReg(STATUS, (getReg(STATUS) | STATUS_EQUAL) & ~ (STATUS_GREATHER | STATUS_LOWER));
			}
		}
		
	}
	
	private class RET implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long sp = getReg(SP) - 8L;
			putReg(SP, sp);
			putReg(IP, getLong(sp));
		}
		
	}
	
	private class INT extends Cmd_1CP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			interrupt(p1);
		}
		
	}
	
	private class PUSH extends Cmd_1CP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long sp = getReg(SP);
			putLong(sp, p1);
			putReg(SP, sp + 8L);
		}
		
	}
	
	private class POP extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long sp = getReg(SP) - 8L;
			putReg(SP, sp);
			setNC(p1, getLong(sp));
		}
		
	}
	
	private class IRET implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long zw = getReg(X_ADD + 9);
			setregs(zw);
		}
		
	}
	
	private class SWAP extends Cmd_2NCP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(isregp1, p1), p2v = getNC(p2);
			setNC(isregp1, p1, p2v);
			setNC(p2, p1v);
		}
		
	}
	
	private class LEA extends Cmd_2NCP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			setNC(p1, p2 + getReg(IP));
		}
		
	}
	
	private class MVAD extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p3 = getLong(getReg(IP) + len);
			len += 8;
			setNC(p1, p2 + p3);
		}
		
	}
	
	private class CALO extends Cmd_1CP_1LP_NL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long sp = getReg(SP);
			long ip = getReg(IP);
			putLong(sp, ip);
			putReg(SP, sp + 8L);
			putReg(IP, p1 + p2);
		}
		
	}
	
	private class BCP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long res = p1 & p2;
			if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_NONE_BITS) & ~ (STATUS_SOME_BITS | STATUS_ALL_BITS));
			} else if (res == p2) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ALL_BITS | STATUS_SOME_BITS) & ~ (STATUS_NONE_BITS));
			} else {
				putReg(STATUS, (getReg(STATUS) | STATUS_ALL_BITS) & ~ (STATUS_NONE_BITS | STATUS_ALL_BITS));
			}
		}
		
	}
	
	private class CMPFP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1 = Double.longBitsToDouble(p1), fp2 = Double.longBitsToDouble(p2);
			if (fp1 > fp2) {
				putReg(STATUS, (getReg(STATUS) | STATUS_GREATHER) & ~ (STATUS_EQUAL | STATUS_LOWER | STATUS_NAN));
			} else if (fp1 < fp2) {
				putReg(STATUS, (getReg(STATUS) | STATUS_LOWER) & ~ (STATUS_EQUAL | STATUS_GREATHER | STATUS_NAN));
			} else if (fp1 != fp2) {
				putReg(STATUS, (getReg(STATUS) | STATUS_NAN) & ~ (STATUS_EQUAL | STATUS_LOWER | STATUS_GREATHER));
			} else {
				putReg(STATUS, (getReg(STATUS) | STATUS_EQUAL) & ~ (STATUS_GREATHER | STATUS_LOWER | STATUS_NAN));
			}
		}
		
	}
	
	private class CHKFP extends Cmd_1CP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			double fp1 = Double.longBitsToDouble(p1);
			if (fp1 == Double.POSITIVE_INFINITY) {
				putReg(STATUS, (getReg(STATUS) | STATUS_GREATHER) & ~ (STATUS_ZERO | STATUS_LOWER | STATUS_NAN));
			} else if (fp1 == Double.NEGATIVE_INFINITY) {
				putReg(STATUS, (getReg(STATUS) | STATUS_LOWER) & ~ (STATUS_ZERO | STATUS_GREATHER | STATUS_NAN));
			} else if (fp1 != fp1) {
				putReg(STATUS, (getReg(STATUS) | STATUS_NAN) & ~ (STATUS_ZERO | STATUS_LOWER | STATUS_GREATHER));
			} else {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_GREATHER | STATUS_LOWER | STATUS_NAN));
			}
		}
		
	}
	
	// new CALL(), new CMP(), new RET(), new INT(), new PUSH(), new POP(), new IRET(), new SWAP(), new
	// LEA(), new MVAD(), new CALO(), new BCP(), new CMPFP(), new CHKFP(), uc, uc,
	
	private class ADDC extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v + p2;
			if ( (getReg(STATUS) & STATUS_CARRY) != 0) {
				res ++ ;
			}
			setNC(p1, res);
			if (p1v > 0L && p2 > 0L) {
				if (res < 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) { //still not possible
					// putReg( + STATUS, (getReg( STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class SUBC extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v - p2;
			if ( (getReg(STATUS) & STATUS_CARRY) != 0) {
				res -- ;
			}
			setNC(p1, res);
			if (p1v > 0L && p2 < 0L) {
				if (res < 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) { // still not possible
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 > 0L) {
				if (res > 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					putReg(STATUS, (getReg(STATUS) | STATUS_ZERO | STATUS_CARRY));
				} else {
					putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class ADDFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)), fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v + fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				putReg(STATUS, (getReg(STATUS) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_NAN | STATUS_ZERO));
			}
		}
		
	}
	
	private class SUBFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)), fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v - fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				putReg(STATUS, (getReg(STATUS) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_NAN | STATUS_ZERO));
			}
		}
		
	}
	
	private class MULFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)), fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v * fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				putReg(STATUS, (getReg(STATUS) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_NAN | STATUS_ZERO));
			}
		}
		
	}
	
	private class DIVFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)), fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v / fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				putReg(STATUS, (getReg(STATUS) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				putReg(STATUS, (getReg(STATUS) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				putReg(STATUS, (getReg(STATUS)) & ~ (STATUS_NAN | STATUS_ZERO));
			}
		}
		
	}
	
	private class NTFP extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			double fpres = (double) getNC(p1);
			setNC(p1, Double.doubleToRawLongBits(fpres));
		}
		
	}
	
	private class FPTN extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long res = (long) Double.longBitsToDouble(getNC(p1));
			setNC(p1, res);
		}
		
	}
	
	private class UDIV extends Cmd_2NCP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(isregp1, p1), p2v = getNC(p2);
			long np1 = Long.divideUnsigned(p1v, p2v), np2 = Long.remainderUnsigned(p1v, p2v);
			setNC(isregp1, p1, np1);
			setNC(p2, np2);
		}
		
	}
	
	// new ADDC(), new SUBC(), new ADDFP(), new SUBFP(), new MULFP(), new DIVFP(), new NTFP(), new
	// FPTN(), new UDIV(), uc, uc, uc, uc, uc, uc, uc,
	
}
