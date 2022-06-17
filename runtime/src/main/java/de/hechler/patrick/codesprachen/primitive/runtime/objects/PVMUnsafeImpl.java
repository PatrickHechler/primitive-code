package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.*;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_ELEMENT_OFFSET_LOCK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_LOCK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_STREAM_OFFSET_FILE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.FS_STREAM_OFFSET_POS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTCNT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTERRUPT_COUNT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_ERRORS_ILLEGAL_INTERRUPT;
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
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class PVMUnsafeImpl implements PVM {
	
	private static final Charset STRING_U16 = StandardCharsets.UTF_16LE;
	
	private static final Unsafe U;
	
	static {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			U = (Unsafe) theUnsafe.get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new InternalError(e);
		}
		if (Unsafe.ARRAY_BYTE_INDEX_SCALE != 1 || Unsafe.ARRAY_CHAR_INDEX_SCALE != 2) {
			throw new AssertionError();
		}
	}
	
	private static final int MAX_BUFFER = 2048;
	
	private static final long NO_LOCK = PatrFileSysConstants.NO_LOCK;
	
	private static final long IP_OFF      = 8L * (long) IP;
	private static final long SP_OFF      = 8L * (long) SP;
	private static final long STATUS_OFF  = 8L * (long) STATUS;
	private static final long INTCNT_OFF  = 8L * (long) INTCNT;
	private static final long INTP_OFF    = 8L * (long) INTP;
	private static final long FS_LOCK_OFF = 8L * (long) FS_LOCK;
	private static final long X00_OFF     = 8L * (long) X_ADD;
	private static final long X01_OFF     = 8L * ( ((long) X_ADD) + 1L);
	private static final long X02_OFF     = 8L * ( ((long) X_ADD) + 2L);
	private static final long X03_OFF     = 8L * ( ((long) X_ADD) + 3L);
	private static final long X09_OFF     = 8L * ( ((long) X_ADD) + 9L);
	
	private final MemoryChecker   mem;
	private final long            regs = U.allocateMemory(256L * 8L);
	private final Interrupt[]     defaultInts;
	private final PVMCommand[]    commands;
	private final PatrFileSysImpl fs;
	
	private long    cmd;
	private boolean off1;
	private int     off2;
	private int     off3;
	private long    avl;
	private int     len;
	private boolean isreg;
	
	public PVMUnsafeImpl(PatrFileSysImpl fs) {
		this(fs, new Random());
	}
	
	public PVMUnsafeImpl(PatrFileSysImpl fs, final Random rnd) {
		this.mem = new MemoryChecker(U);
		U.putLong(regs + IP_OFF, -1L);
		U.putLong(regs + SP_OFF, -1L);
		U.putLong(regs, STATUS_OFF, 0L);
		U.putLong(regs + INTCNT_OFF, INTERRUPT_COUNT);
		long addr = U.allocateMemory(INTERRUPT_COUNT << 3);
		mem.malloc(addr, INTERRUPT_COUNT << 3);
		U.putLong(regs + INTP_OFF, addr);
		U.setMemory(null, addr, INTERRUPT_COUNT << 3, (byte) -1);
		U.putLong(regs + FS_LOCK_OFF, NO_LOCK);
		this.fs = fs;
		this.defaultInts = new Interrupt[] {num -> System.exit((int) (128L + U.getLong(regs + X00_OFF))), //
			num -> System.exit(7), //
			num -> System.exit(6), //
			num -> System.exit(5),
			num -> System.exit((int) U.getLong(regs + X00_OFF)), //
			num -> {
				long l = U.getLong(regs, X00_OFF);
				long a = U.allocateMemory(l);
				mem.malloc(a, l);
				U.putLong(regs + X00_OFF, a);
			}, //
			num -> {
				long oa = U.getLong(regs, X00_OFF);
				long l = U.getLong(regs, X01_OFF);
				mem.chackAllocated(oa, l);
				long a = U.reallocateMemory(oa, l);
				mem.realloc(oa, a, l);
			}, //
			num -> {
				long a = U.getLong(regs, X00_OFF);;
				mem.chackAllocated(a);
				U.freeMemory(a);
				mem.free(a);
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
			num -> U.putLong(regs, X00_OFF, System.currentTimeMillis()), //
			num -> defIntSleep(),
			num -> U.putLong(regs + X00_OFF, rnd.nextLong()), //
			num -> U.copyMemory(null, U.getLong(regs, X01_OFF), null, U.getLong(regs, X00_OFF), U.getLong(regs, X02_OFF)),
			num -> U.copyMemory(null, U.getLong(regs, X01_OFF), null, U.getLong(regs, X00_OFF), U.getLong(regs, X02_OFF)),
			num -> U.setMemory(null, U.getLong(regs + X00_OFF), U.getLong(regs + X02_OFF), (byte) U.getLong(regs + X01_OFF)), //
			num -> {
				long a = U.getLong(regs + X00_OFF), l = U.getLong(regs + X02_OFF), v = U.getLong(regs + X01_OFF);
				long bv = v & 0xFF;
				long bl = l << 3;
				if (bl != l) {
					throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
				}
				mem.check(a, bl);
				if (bv != ( (v >>> 8) & 0xFF) || bv != ( (v >>> 16) & 0xFF) || bv != ( (v >>> 24) & 0xFF) || bv != ( (v >>> 32) & 0xFF) || bv != ( (v >>> 40) & 0xFF)
					|| bv != ( (v >>> 48) & 0xFF) || bv != ( (v >>> 56) & 0xFF)) {
					for (; l > 0L; l -- ) {
						U.putLong(a ++ , v);
					}
				} else {
					U.setMemory(null, a, bl, (byte) bv);
				}
			}, //
			num -> {
				// this.regs[X_ADD] = getU16String(this.regs[X_ADD]).length() << 1,
				long sa, a;
				sa = a = U.getLong(regs + X00_OFF);
				long avl = mem.avl(sa);
				for (char c;; a += 2L, avl -= 2) {
					if (avl < 2L) {
						throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
					}
					c = U.getChar(a);
					if (c == '\0') break;
				}
				U.putLong(regs + X00_OFF, a - sa);
			}, //
			num -> {
				// num -> this.regs[X_ADD] = getU16String(this.regs[X_ADD]).compareTo(getU16String(this.regs[X_ADD +
				// 1])),
				long a = U.getLong(regs + X00_OFF), a2 = U.getLong(regs + X01_OFF);
				long avl = Math.min(mem.avl(a), mem.avl(a2));
				for (char c, c2;; a += 2L, a2 += 2, avl -= 2) {
					if (avl < 2L) {
						throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
					}
					c = U.getChar(a);
					c2 = U.getChar(a2);
					if (c == '\0') {
						U.putLong(regs + X00_OFF, - (0xFFFFL & c2)); // if c2 is also '\0' 0 is correct, else its negative
					} else if (c == c2) {
						continue;
					} else {
						U.putLong(regs + X00_OFF, (0xFFFFL & c) - (0xFFFFL & c2));
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
			num -> defIntLoadFile(), };
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
			long ip = U.getLong(regs + IP_OFF);
			avl = mem.avl(ip);
			if (avl < len) {
				interrupt(INT_ERRORS_ILLEGAL_MEMORY);
				return;
			}
			cmd = U.getLong(ip);
			this.commands[(int) (cmd & 0xFF)].execute();
		} catch (PrimitiveErrror e) {
			interrupt(e.intNum);
		}
	}
	
	/*
	 * the getNC()/setNC() methods will not make any memory check!
	 * 
	 * so all memory checks has to be done before!
	 */
	private long getNC(long p) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			return U.getLong(regs + p);
		} else {
			return U.getLong(p);
		}
	}
	
	private void setNC(long p, long val) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			U.putLong(regs + p, val);
		} else {
			U.putLong(p, val);
		}
	}
	
	private long getNC(boolean isreg, long p) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			return U.getLong(regs + p);
		} else {
			return U.getLong(p);
		}
	}
	
	private void setNC(boolean isreg, long p, long val) throws PrimitiveErrror {
		if (isreg) {
			assert (p & 0xFFL) == p;
			U.putLong(regs + p, val);
		} else {
			U.putLong(p, val);
		}
	}
	
	private long getConstParam() throws PrimitiveErrror {
		long ip = U.getLong(regs + IP_OFF);
		switch ((int) (0xFF & (cmd >> (off1 ? 48 : 40)))) {
		case PARAM_ART_ANUM:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			return U.getLong(ip + off2 ++ );
		case PARAM_ART_ANUM_BNUM:
			len += 16;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			long a = U.getLong(ip + (off2 += 8));
			long b = U.getLong(ip + (off2 += 8));
			long addr = a + b;
			mem.check(addr, 8L);
			return U.getLong(addr);
		case PARAM_ART_ANUM_BREG:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			a = U.getLong(ip + (off2 += 8));
			mem.check(a, 8L);
			return U.getLong(a);
		case PARAM_ART_ANUM_BSR:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			a = U.getLong(ip + (off2 += 8));
			b = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			addr = a + b;
			mem.check(addr, 8L);
			return U.getLong(addr);
		case PARAM_ART_ASR:
			a = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			return a;
		case PARAM_ART_ASR_BNUM:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			a = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			b = U.getLong(ip + (off2 += 8));
			addr = a + b;
			mem.check(addr, 8L);
			return U.getLong(addr);
		case PARAM_ART_ASR_BREG:
			a = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			return U.getLong(a);
		case PARAM_ART_ASR_BSR:
			a = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			b = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			addr = a + b;
			mem.check(addr, 8L);
			return U.getLong(addr);
		default:
			throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND);
		}
	}
	
	private long getNoConstParam() throws PrimitiveErrror {
		isreg = false;
		long ip = U.getLong(regs + IP_OFF);
		switch ((int) (0xFF & (cmd >> (off1 ? 48 : 40)))) {
		case PARAM_ART_ANUM:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			return U.getLong(ip + off2 ++ );
		case PARAM_ART_ANUM_BNUM:
			len += 16;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			long a = U.getLong(ip + (off2 += 8));
			long b = U.getLong(ip + (off2 += 8));
			long addr = a + b;
			mem.check(addr, 8L);
			return addr;
		case PARAM_ART_ANUM_BREG:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			a = U.getLong(ip + (off2 += 8));
			mem.check(a, 8L);
			return (a);
		case PARAM_ART_ANUM_BSR:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			a = U.getLong(ip + (off2 += 8));
			b = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			addr = a + b;
			mem.check(addr, 8L);
			return addr;
		case PARAM_ART_ASR:
			a = (cmd >> (off3 += 8) & 0xFF);
			isreg = true;
			return a;
		case PARAM_ART_ASR_BNUM:
			len += 8;
			if (avl < len) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			a = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			b = U.getLong(ip + (off2 += 8));
			addr = a + b;
			mem.check(addr, 8L);
			return addr;
		case PARAM_ART_ASR_BREG:
			a = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			return (a);
		case PARAM_ART_ASR_BSR:
			a = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			b = U.getLong(regs + ( (cmd >> (off3 += 8) & 0xFF) << 3));
			addr = a + b;
			mem.check(addr, 8L);
			return addr;
		default:
			throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND);
		}
	}
	
	private void interrupt(long intNum) {
		while (true) {
			try {
				long intcnt = U.getLong(regs + INTCNT_OFF);
				if (intNum < 0L || intNum >= intcnt) {
					if (intcnt > 0L) {
						interrupt(INT_ERRORS_ILLEGAL_INTERRUPT);
					} else {
						System.exit(128);
					}
				} else {
					long intp = U.getLong(regs + INTP_OFF);
					if (intp == -1L) {
						defInt(intNum);
					} else {
						long addr = intp + (intNum << 3);
						mem.check(addr, 8L);
						long address = U.getLong(addr);
						if (address == -1L) {
							defInt(intNum);
						} else {
							long save = U.allocateMemory(128L);
							mem.malloc(save, 128L);
							U.copyMemory(null, regs, null, save, 128L);
							U.putLong(regs + X09_OFF, save);
							U.putLong(regs + IP_OFF, address);
						}
					}
				}
			} catch (PrimitiveErrror e) {
				intNum = e.intNum;
				continue;
			}
			break;
		}
	}
	
	private void defInt(long intNum) throws PrimitiveErrror {
		if (intNum >= INTERRUPT_COUNT) {
			interrupt(INT_ERRORS_ILLEGAL_INTERRUPT);
		} else {
			this.defaultInts[(int) intNum].execute((int) intNum);
		}
	}
	
	private String getU16String(long address) throws PrimitiveErrror {
		char[] cs = new char[16];
		int len;
		long off;
		long avl = mem.avl(address);
		for (len = 0, off = Unsafe.ARRAY_CHAR_BASE_OFFSET;; len ++ , off += 2, address += 2L, avl -= 2L) {
			if (avl < 2L) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			if (len > cs.length) {
				char[] o = cs;
				cs = new char[cs.length + cs.length >>> 1];
				U.copyMemory(o, Unsafe.ARRAY_CHAR_BASE_OFFSET, cs, Unsafe.ARRAY_CHAR_BASE_OFFSET, off - Unsafe.ARRAY_CHAR_BASE_OFFSET);
			}
			char c = U.getChar(address);
			if (c == '\0') break;
			U.putChar(cs, off, c);
		}
		return new String(cs, 0, len);
	}
	
	private String getU8String(long address) throws PrimitiveErrror {
		byte[] bytes = new byte[16];
		int len;
		long off = Unsafe.ARRAY_BYTE_BASE_OFFSET;
		long avl = mem.avl(address);
		for (len = 0;; len ++ , off ++ , avl -= 2L) {
			if (avl < 2L) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			if (len >= bytes.length) {
				bytes = Arrays.copyOf(bytes, len + len >> 1);
			}
			byte b = U.getByte(address + len);
			if (bytes[len] == 0) {
				break;
			}
			U.putByte(bytes, off, b);
		}
		return new String(bytes, 0, len, StandardCharsets.UTF_8);
	}
	
	
	private String[] getPathNames(long address) throws PrimitiveErrror {
		String[] s = new String[4];
		int sl = 0;
		long so = ((long) Unsafe.ARRAY_OBJECT_BASE_OFFSET);
		char[] cs = new char[16];
		int len;
		long off;
		long avl = mem.avl(address);
		for (len = 0, off = Unsafe.ARRAY_CHAR_BASE_OFFSET;; len ++ , off += 2, address += 2L, avl -= 2L) {
			if (avl < 2L) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			if (len > cs.length) {
				char[] o = cs;
				cs = new char[cs.length + cs.length >>> 1];
				U.copyMemory(o, Unsafe.ARRAY_CHAR_BASE_OFFSET, cs, Unsafe.ARRAY_CHAR_BASE_OFFSET, off - Unsafe.ARRAY_CHAR_BASE_OFFSET);
			}
			char c = U.getChar(address);
			if (c == '\0') break;
			if (c == '/') {
				if (sl >= s.length) {
					String[] o = s;
					s = new String[s.length + s.length >>> 1];
					U.copyMemory(o, Unsafe.ARRAY_OBJECT_BASE_OFFSET, s, Unsafe.ARRAY_OBJECT_BASE_OFFSET, so - Unsafe.ARRAY_OBJECT_BASE_OFFSET);
				}
				U.putObject(s, so, new String(cs, 0, len));
				off = Unsafe.ARRAY_CHAR_BASE_OFFSET;
				len = 0;
				so += Unsafe.ARRAY_OBJECT_INDEX_SCALE;
				continue;
			}
			U.putChar(cs, off, c);
		}
		if (sl >= s.length) {
			String[] o = s;
			s = new String[s.length + 1];
			U.copyMemory(o, Unsafe.ARRAY_OBJECT_BASE_OFFSET, s, Unsafe.ARRAY_OBJECT_BASE_OFFSET, so - Unsafe.ARRAY_OBJECT_BASE_OFFSET);
			U.putObject(s, so, new String(cs, 0, len));
			return s;
		}
		U.putObject(s, so, new String(cs, 0, len));
		if (sl + 1 != s.length) {
			String[] res = new String[sl + 1];
			U.copyMemory(res, Unsafe.ARRAY_OBJECT_BASE_OFFSET, s, Unsafe.ARRAY_OBJECT_BASE_OFFSET, so - Unsafe.ARRAY_OBJECT_BASE_OFFSET + Unsafe.ARRAY_OBJECT_INDEX_SCALE);
			return res;
		}
		return s;
	}
	
	private void defIntLoadFile() throws PrimitiveErrror {
		String[] names = getPathNames(U.getLong(regs + X00_OFF));
		try {
			PatrFolder parent = fs.getRoot();
			for (int i = names[0].isEmpty() ? 1 : 0; i < names.length - 1; i ++ ) {
				parent = parent.getElement(names[i], NO_LOCK).getFolder();
			}
			PatrFile file = parent.getElement(names[names.length - 1], NO_LOCK).getFile();
			long len = file.length(NO_LOCK);
			long addr = U.allocateMemory(len);
			mem.malloc(addr, len);
			byte[] bytes = new byte[(int) Math.min(MAX_BUFFER, len)];
			for (long wrote = 0L; wrote < len;) {
				int cpy = (int) Math.min(bytes.length, len - wrote);
				file.getContent(bytes, wrote, 0, cpy, NO_LOCK);
				U.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, addr + wrote, cpy);
			}
			U.putLong(regs + X00_OFF, addr);
			U.putLong(regs + X01_OFF, len);
		} catch (OutOfMemoryError e) {
			fail(X00_OFF, STATUS_OUT_OF_MEMORY);
		} catch (IllegalStateException e) {
			fail(X00_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (NoSuchFileException e) {
			fail(X00_OFF, STATUS_ELEMENT_NOT_EXIST);
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
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
		String input = toU8 ? getU16String(U.getLong(regs + X00_OFF)) : getU8String(U.getLong(regs + X00_OFF));
		byte[] result = input.getBytes(toU8 ? StandardCharsets.UTF_8 : STRING_U16);
		long len = result.length + (toU8 ? 1L : 2L);
		long x02 = U.getLong(regs + X02_OFF);
		long x01 = U.getLong(regs + X01_OFF);
		if (x02 < len) {
			long addr;
			if (x02 == 0L) {
				addr = U.allocateMemory(len);
				mem.malloc(addr, len);
				U.putLong(regs + X01_OFF, addr);
				x01 = addr;
			} else if (x02 > 0L) {
				addr = U.reallocateMemory(x01, len);
				mem.malloc(addr, len);
				U.putLong(regs + X01_OFF, addr);
				x01 = addr;
			} else {
				fail(X03_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			U.putLong(regs + X02_OFF, len);
		}
		for (int i = 0; i < result.length; i ++ ) {
			U.putByte(x01 + i, result[i]);
		}
		if (toU8) {
			U.putByte(x01 + result.length, (byte) 0);
		} else {
			U.putChar(x01 + result.length, '\0');
		}
		U.putLong(regs + X03_OFF, x01 + result.length);
	}
	
	private void defIntFormattString() throws PrimitiveErrror {
		String input = getU16String(U.getLong(regs + X00_OFF));
		char[] cs = input.toCharArray();
		StringBuilder result = new StringBuilder();
		long argOff = X03_OFF;
		for (int i = 0; i < input.length();) {
			int index = input.indexOf('%', i);
			result.append(cs, i, index - i);
			if (index + 1 >= input.length()) {
				fail(X00_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			char c = input.charAt(index + 1);
			if (c == '%') {
				result.append('%');
				continue;
			}
			if (argOff >= 256L << 3) {
				fail(X00_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			long arg = U.getLong(regs + argOff);
			argOff += 8L;
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
				fail(X00_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			i = index + 2;
		}
		char[] CS = result.toString().toCharArray();
		long addr;
		long x02 = U.getLong(regs + X02_OFF);
		long length = ((long) CS.length) << 1 + 2L;
		if (x02 < length) {
			if (x02 == 0L) {
				addr = U.allocateMemory(length);
				mem.malloc(addr, CS.length + 2L);
				U.putLong(regs + X01_OFF, addr);
			} else if (x02 > 0L) {
				long oadr = U.getLong(regs + X01_OFF);
				addr = U.reallocateMemory(oadr, length);
				mem.realloc(oadr, addr, length);
				U.putLong(regs + X02_OFF, addr);
			} else {
				fail(X00_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			U.putLong(regs + X02_OFF, CS.length + 2L);
		} else {
			addr = U.getLong(regs + X01_OFF);
		}
		U.copyMemory(cs, Unsafe.ARRAY_CHAR_BASE_OFFSET, null, addr, length - 2L);
		U.putChar(addr + length - 2L, '\0');
	}
	
	private void defIntStringToAnyNumber(int intNum) throws PrimitiveErrror {
		String str = getU16String(U.getLong(regs + X00_OFF)).trim();
		try {
			switch (intNum) {
			case (int) INT_STRING_TO_NUMBER: {
				long x01 = U.getLong(regs + X01_OFF);
				int numsys = (int) x01;
				if ( ((long) numsys) != x01 || numsys < 2 || numsys > 36) {
					fail( -X01_OFF, STATUS_ILLEGAL_ARG);
					return;
				}
				U.putLong(regs + X00_OFF, Long.parseLong(str, numsys));
				break;
			}
			case (int) INT_STRING_TO_FPNUMBER: {
				U.putLong(regs + X00_OFF, Double.doubleToRawLongBits(Double.parseDouble(str)));
				break;
			}
			default:
				throw new InternalError();
			}
		} catch (NumberFormatException nfe) {
			fail( -X01_OFF, STATUS_ILLEGAL_ARG);
		}
	}
	
	private void defIntAnyNumberToString(int intNum) throws PrimitiveErrror {
		byte[] bytes;
		long lenOff;
		switch (intNum) {
		case (int) INT_NUMBER_TO_STRING: {
			long x02 = U.getLong(regs + X02_OFF);
			if (x02 > 36L || x02 < 2L) {
				fail(X01_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			bytes = Long.toString(U.getLong(regs + X00_OFF), (int) x02).toUpperCase().getBytes(STRING_U16);
			lenOff = regs + X03_OFF;
			break;
		}
		case (int) INT_FPNUMBER_TO_STRING:
			bytes = Double.toString(Double.longBitsToDouble(U.getLong(regs + X00_OFF))).getBytes(STRING_U16);
			lenOff = regs + X02_OFF;
			break;
		default:
			throw new InternalError();
		}
		long addr = U.getLong(regs + X01_OFF);
		long lenOffVal = U.getLong(lenOff);
		if (bytes.length + 2L > lenOffVal) {
			if (lenOffVal == 0L) {
				addr = U.allocateMemory(bytes.length + 2L);
				mem.malloc(addr, bytes.length + 2L);
			} else if (lenOffVal > 0L) {
				long oaddr = addr;
				addr = U.reallocateMemory(addr, bytes.length + 2L);
				mem.realloc(oaddr, addr, bytes.length + 2L);
			} else {
				fail(X01_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			U.putLong(lenOff, bytes.length + 2L);
		}
		U.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, addr, bytes.length);
		U.putChar(addr + bytes.length, '\0');
	}
	
	private void defIntSleep() {
		long start = System.nanoTime();
		try {
			long secs = U.getLong(regs + X02_OFF);
			long nanos = U.getLong(regs + X01_OFF),
				millis2 = nanos / 1000000L;
			int nanosi = (int) (nanos % 1000000L);
			Thread.sleep( (secs * 1000L) + millis2, nanosi);
			U.putLong(regs + X01_OFF, 0L);
			U.putLong(regs + X02_OFF, 0L);
		} catch (InterruptedException e) {
			long remain = System.nanoTime() - start;
			long r1 = remain / 1000000000L;
			long r2 = remain % 1000000000L;
			U.putLong(regs + X01_OFF, r1);
			U.putLong(regs + X01_OFF, r2);
		}
	}
	
	private void defIntFSUnlock() throws PrimitiveErrror {
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			fs.removeLock();
			U.putLong(regs + FS_LOCK_OFF, NO_LOCK);
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntFSLock() throws PrimitiveErrror {
		try {
			U.putLong(regs + FS_LOCK_OFF, fs.lock(U.getLong(regs + X00_OFF)));
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntLinkSetTarget() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK),
			ntaddress = U.getLong(regs + X01_OFF);
		// mem.check(ntaddress, 16L); // lock is ignored here
		mem.check(ntaddress, 8L);
		long ntid = U.getLong(ntaddress + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrLink e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getLink();
			PatrFileSysElement nt = fs.fromID(new PatrID(fs, ntid, fs.getStartTime()));
			e.setTarget(nt, lock);
		} catch (OutOfSpaceException e) {
			fail(X01_OFF, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X01_OFF, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntLinkGetTarget() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getLink().getTarget(lock);
			U.putLong(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) e).id);
			U.putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
		} catch (OutOfSpaceException e) {
			fail(X01_OFF, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X01_OFF, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileTruncate() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			long newLen = U.getLong(regs + X01_OFF);
			e.truncate(newLen, lock);
		} catch (OutOfSpaceException e) {
			fail(X01_OFF, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X01_OFF, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileRWA(int intNum) throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			final long length = U.getLong(regs + X01_OFF);
			long addr = U.getLong(regs + X02_OFF);
			mem.check(addr, length);
			byte[] bytes = new byte[(int) Math.min(length, MAX_BUFFER)];
			long copied;
			long x03 = U.getLong(regs + X03_OFF);
			for (copied = 0L; copied < length;) {
				int cpy = (int) Math.min(bytes.length, length - copied);
				switch (intNum) {
				case (int) INT_FS_FILE_READ:
					e.getContent(bytes, x03, 0, cpy, lock);
					U.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, addr, cpy);
					break;
				case (int) INT_FS_FILE_WRITE:
				case (int) INT_FS_FILE_APPEND:
					U.copyMemory(null, addr, bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, cpy);
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
			fail(X01_OFF, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X01_OFF, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileHash() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			final long addr = U.getLong(regs + X01_OFF);
			byte[] bytes = e.getHashCode(lock);
			assert bytes.length == 32;
			mem.check(address, bytes.length);
			U.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, addr, bytes.length);
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntFileLength() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			U.putLong(regs + X01_OFF, e.length(lock));
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntAddElement(int intNum) throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK));
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			String name = getU16String(U.getLong(regs + X01_OFF));
			PatrFileSysElement child;
			switch (intNum) {
			case (int) INT_FS_FOLDER_ADD_FILE:
				child = e.addFile(name, lock);
				break;
			case (int) INT_FS_FOLDER_ADD_FOLDER:
				child = e.addFolder(name, lock);
				break;
			case (int) INT_FS_FOLDER_ADD_LINK: {
				final long taddress = U.getLong(regs + X02_OFF);
				mem.check(taddress, 8L); // lock ignored
				final long tid = U.getLong(taddress + FS_ELEMENT_OFFSET_ID);
				PatrFolder te = fs.fromID(new PatrID(fs, tid, fs.getStartTime())).getFolder();
				child = e.addLink(name, te, lock);
				break;
			}
			default:
				throw new InternalError();
			}
			U.putLong(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) child).id);
			U.putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
		} catch (FileAlreadyExistsException e) {
			fail(X01_OFF, STATUS_ELEMENT_ALREADY_EXIST);
		} catch (ElementReadOnlyException e) {
			fail(X01_OFF, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetChildElement(int intNum) throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			PatrFileSysElement child;
			long x01 = U.getLong(regs + X01_OFF);
			switch (intNum) {
			case (int) INT_FS_FOLDER_GET_CHILD_OF_INDEX: {
				int index = (int) x01;
				if (index != x01) {
					fail(X01_OFF, STATUS_ILLEGAL_ARG);
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
			U.putLong(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) child).id);
			U.putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetFolderChildElementCount() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID), lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			U.putLong(regs + X01_OFF, e.elementCount(lock));
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X01_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntModifyElementFlags() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			((PatrFileSysElementImpl) e).flag(lock, (int) U.getLong(regs + X01_OFF), (int) U.getLong(regs + X02_OFF));
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetElementFlags() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 8L); // lock ignored
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			U.putLong(regs + X00_OFF, 0x00000000FFFFFFFFL & ((PatrFileSysElementImpl) e).getFlags());
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntMoveElement() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK),
			npaddress = U.getLong(regs + X01_OFF);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			if (npaddress != -1L) {
				mem.check(npaddress, 16L);
				final long npid = U.getLong(npaddress + FS_ELEMENT_OFFSET_ID),
					nplock = U.getLong(npaddress + FS_ELEMENT_OFFSET_LOCK);
				PatrFolder np = fs.fromID(new PatrID(fs, npid, fs.getStartTime())).getFolder();
				e.setParent(np, lock, U.getLong(regs + X03_OFF), nplock);
			}
			long x02 = U.getLong(regs + X02_OFF);
			if (x02 != -1L) {
				String newName = getU16String(x02);
				e.setName(newName, lock);
			}
		} catch (OutOfSpaceException e) {
			fail(X00_OFF, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X00_OFF, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException e) {
			fail(X00_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntDeleteElement() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			e.delete(lock, U.getLong(regs + X01_OFF));
			mem.free(address);
			U.freeMemory(address);
		} catch (OutOfSpaceException e) {
			fail(X00_OFF, STATUS_OUT_OF_SPACE);
		} catch (ElementReadOnlyException e) {
			fail(X00_OFF, STATUS_READ_ONLY);
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntUnlockElement() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			e.removeLock(lock);
			U.putLong(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			U.putLong(regs + X01_OFF, 1L);
		} catch (ElementLockedException e) {
			fail( -X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail( -X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail( -X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntLockElement() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L); // lock set
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			U.putLong(address + FS_ELEMENT_OFFSET_LOCK, e.lock(U.getLong(regs + X01_OFF)));
		} catch (ElementLockedException e) {
			fail(X01_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetLockDate() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 8L); // lock not used
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			U.putLong(regs + X01_OFF, e.getLockTime());
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetLockData() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 8L); // lock not used
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			U.putLong(regs + X00_OFF, e.getLockData());
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntSetSomeDate(int intNum) throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 16L);
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID),
			lock = U.getLong(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			switch (intNum) {
			case (int) INT_FS_ELEMENT_SET_CREATE:
				e.setCreateTime(U.getLong(regs + X01_OFF), lock);
				break;
			case (int) INT_FS_ELEMENT_SET_LAST_MOD:
				e.setLastModTime(U.getLong(regs + X01_OFF), lock);
				break;
			case (int) INT_FS_ELEMENT_SET_LAST_META_MOD:
				e.setLastMetaModTime(U.getLong(regs + X01_OFF), lock);
				break;
			default:
				throw new InternalError();
			}
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetSomeDate(int intNum) throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 8L); // lock not used
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			switch (intNum) {
			case (int) INT_FS_ELEMENT_GET_CREATE:
				U.putLong(regs + X01_OFF, e.getCreateTime());
				break;
			case (int) INT_FS_ELEMENT_GET_LAST_MOD:
				U.getLong(regs + X01_OFF, e.getLastModTime());
				break;
			case (int) INT_FS_ELEMENT_GET_LAST_META_MOD:
				U.getLong(regs + X01_OFF, e.getLastMetaModTime());
				break;
			default:
				throw new InternalError();
			}
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntFromID() throws PrimitiveErrror {
		final long id = U.getLong(regs + X00_OFF);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK));
			fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			long addr = U.allocateMemory(16L);
			mem.malloc(addr, 16L);
			U.putLong(addr + FS_ELEMENT_OFFSET_ID, id);
			U.putLong(addr + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			U.putLong(regs + X00_OFF, addr);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X00_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetParent() throws PrimitiveErrror {
		final long address = U.getLong(regs + X00_OFF);
		mem.check(address, 8L); // lock not used
		final long id = U.getLong(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(U.getLong(regs + FS_LOCK_OFF));
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			PatrFolder parent = e.getParent();
			U.putLong(regs + X00_OFF, ((PatrFileSysElementImpl) parent).id);
			U.putLong(regs + X01_OFF, 1L);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail( -X01_OFF, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail( -X01_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntDuplicateFSElementHandle() throws PrimitiveErrror {
		try {
			long address = U.getLong(regs + X00_OFF),
				dupAddr;
			mem.check(address, 16L);
			dupAddr = U.allocateMemory(16L);
			mem.malloc(dupAddr, 16L);
			U.copyMemory(null, address, null, dupAddr, 16L);
			U.putLong(regs + X00_OFF, dupAddr);
		} catch (OutOfMemoryError e) {
			fail( -X00_OFF, STATUS_OUT_OF_MEMORY);
		}
	}
	
	private void defIntGetFSElement(int intNum) throws PrimitiveErrror {
		String path = getU16String(U.getLong(regs + X00_OFF));
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
					fail(X00_OFF, STATUS_ELEMENT_WRONG_TYPE);
					return;
				}
				break;
			case (int) INT_FS_GET_FOLDER:
				if ( !file.isFolder()) {
					fail(X00_OFF, STATUS_ELEMENT_WRONG_TYPE);
					return;
				}
				break;
			case (int) INT_FS_GET_LINK:
				if ( !file.isLink()) {
					fail(X00_OFF, STATUS_ELEMENT_WRONG_TYPE);
					return;
				}
			case (int) INT_FS_GET_ELEMENT:
				break;
			default:
				throw new AssertionError();
			}
			long addr = U.allocateMemory(16L);
			mem.malloc(addr, 16L);
			U.putLong(addr + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) file).id);
			U.putLong(addr + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			U.putLong(regs + X00_OFF, addr);
		} catch (OutOfMemoryError e) {
			fail(X00_OFF, STATUS_OUT_OF_MEMORY);
		} catch (IllegalStateException e) {
			fail(X00_OFF, STATUS_ELEMENT_WRONG_TYPE);
		} catch (NoSuchFileException e) {
			fail(X00_OFF, STATUS_ELEMENT_NOT_EXIST);
		} catch (ElementLockedException e) {
			fail(X00_OFF, STATUS_ELEMENT_LOCKED);
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
		}
	}
	
	private void defIntRead() throws PrimitiveErrror {
		final long id = U.getLong(regs + X00_OFF),
			len = U.getLong(regs + X01_OFF),
			addr = U.getLong(regs + X02_OFF);
		mem.check(addr, len);
		if (id <= MAX_STD_STREAM) {
			InputStream in;
			if (id == STD_IN) {
				in = System.in;
			} else {
				fail(X01_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			byte[] bytes = new byte[(int) Math.min(len, MAX_BUFFER)];
			for (long reat = 0L; reat < len;) {
				try {
					int r = in.read(bytes);
					U.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, addr + reat, r);
					reat += r;
				} catch (IOException e) {
					if (reat == 0L) fail(X01_OFF, STATUS_IO_ERR);
					else U.putLong(regs + X01_OFF, reat);
					return;
				}
			}
		} else {
			try {
				long pos = U.getLong(id + FS_STREAM_OFFSET_POS), fileAddr = U.getLong(id + FS_STREAM_OFFSET_FILE), mode = U.getLong(id + 16L),
					fid = U.getLong(fileAddr + FS_ELEMENT_OFFSET_ID), lock = U.getLong(fileAddr + FS_ELEMENT_OFFSET_LOCK);
				if ( (mode & OPEN_READ) != 0) {
					fail(X01_OFF, STATUS_ILLEGAL_ARG);
					return;
				}
				fs.setLock(U.getLong(regs + FS_LOCK_OFF));
				PatrFile file;
				try {
					file = fs.fromID(new PatrID(fs, fid, fs.getStartTime())).getFile();
				} catch (IllegalStateException e) {
					fail(X01_OFF, STATUS_ILLEGAL_ARG);
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
								U.putLong(id + FS_STREAM_OFFSET_POS, npos);
								U.putLong(regs + X01_OFF, reat);
								return;
							}
							int cpy = (int) Math.min(len - reat, Math.min(length - npos, bytes.length));
							file.getContent(bytes, npos, 0, cpy, lock);
							U.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, addr + reat, cpy);
							npos += cpy;
							reat += cpy;
							U.putLong(id + FS_STREAM_OFFSET_POS, npos);
						}
					} catch (ElementLockedException e) {
						fail(X01_OFF, STATUS_ELEMENT_LOCKED);
						return;
					} catch (IOException e) {
						fail(X01_OFF, STATUS_IO_ERR);
						return;
					}
					U.getLong(regs + X01_OFF, len);
				});
				return;
			} catch (IOException e) {
				fail(X01_OFF, STATUS_IO_ERR);
				return;
			}
		}
	}
	
	private void defIntWrite() throws PrimitiveErrror {
		final long id = U.getLong(regs + X00_OFF),
			len = U.getLong(regs + X01_OFF),
			addr = U.getLong(regs + X02_OFF);
		mem.check(addr, len);
		if (id <= MAX_STD_STREAM) {
			OutputStream out;
			if (id == STD_OUT) {
				out = System.out;
			} else if (id == STD_LOG) {
				out = System.err;
			} else {
				fail(X01_OFF, STATUS_ILLEGAL_ARG);
				return;
			}
			if (len < 0L) {
				fail(X01_OFF, STATUS_ILLEGAL_ARG);
			}
			byte[] bytes = new byte[(int) Math.min(MAX_BUFFER, len)];
			for (long wrote = 0L; wrote < len;) {
				U.copyMemory(null, addr + wrote, bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, bytes.length);
				try {
					out.write(bytes);
				} catch (IOException e) {
					fail(X01_OFF, STATUS_IO_ERR);
					return;
				}
			}
		} else {
			try {
				final long pos = U.getLong(id + FS_STREAM_OFFSET_POS), fileAddr = U.getLong(id + FS_STREAM_OFFSET_FILE), mode = U.getLong(id + 16L),
					fid = U.getLong(fileAddr + FS_ELEMENT_OFFSET_ID), lock = U.getLong(fileAddr + FS_ELEMENT_OFFSET_LOCK);
				if ( (mode & OPEN_WRITE) != 0) {
					fail(X01_OFF, STATUS_ILLEGAL_ARG);
					return;
				}
				fs.setLock(U.getLong(regs + FS_LOCK_OFF));
				PatrFile file;
				try {
					file = fs.fromID(new PatrID(fs, fid, fs.getStartTime())).getFile();
				} catch (IllegalStateException e) {
					fail(X01_OFF, STATUS_ILLEGAL_ARG);
					return;
				}
				boolean append = (mode & OPEN_APPEND) != 0;
				byte[] bytes = new byte[(int) Math.min(MAX_BUFFER, len)];
				file.withLock(() -> {
					long npos = pos;
					for (long wrote = 0L; wrote < len;) {
						try {
							U.copyMemory(null, addr + wrote, bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, bytes.length);
							long length = file.length(lock);
							int cpy = 0;
							if ( !append && npos < length) {
								cpy = (int) Math.min(len - wrote, Math.min(length - npos, bytes.length));
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
							U.putLong(id + FS_STREAM_OFFSET_POS, npos);
						} catch (OutOfSpaceException e) {
							fail(X01_OFF, STATUS_OUT_OF_SPACE);
							return;
						} catch (ElementReadOnlyException e) {
							fail(X01_OFF, STATUS_READ_ONLY);
							return;
						} catch (ElementLockedException e) {
							fail(X01_OFF, STATUS_ELEMENT_LOCKED);
							return;
						} catch (IOException e) {
							fail(X01_OFF, STATUS_IO_ERR);
							return;
						}
					}
				});
				return;
			} catch (IOException e) {
				fail(X01_OFF, STATUS_IO_ERR);
				return;
			}
		}
	}
	
	private void defIntOpenNewStream() throws PrimitiveErrror {
		String path = getU16String(U.getLong(regs + X00_OFF));
		String[] names = path.split("\\/");
		try {
			try {
				PatrFolder parent = fs.getRoot();
				for (int i = 0; i < names.length - 1; i ++ ) {
					parent = parent.getElement(names[i], NO_LOCK).getFolder();
				}
				long mode = U.getLong(regs + X01_OFF);
				if ( (mode & OPEN_APPEND) != 0) {
					mode |= OPEN_WRITE;
				}
				if ( (mode & (OPEN_READ | OPEN_WRITE)) != 0) {
					fail(X00_OFF, STATUS_ILLEGAL_ARG);
					return;
				}
				if ( (mode & (OPEN_CREATE | OPEN_NEW_FILE | OPEN_TRUNCATE)) != 0) {
					if ( (mode & OPEN_WRITE) != 0) {
						fail(X00_OFF, STATUS_ILLEGAL_ARG);
						return;
					}
				}
				if ( (mode & OPEN_NEW_FILE) != 0 && (mode & (OPEN_CREATE | OPEN_TRUNCATE)) != 0) {
					fail(X00_OFF, STATUS_ILLEGAL_ARG);
					return;
				}
				PatrFile file;
				try {
					file = parent.getElement(names[names.length - 1], NO_LOCK).getFile();
					if ( (mode & OPEN_NEW_FILE) != 0) {
						fail(X00_OFF, STATUS_ELEMENT_ALREADY_EXIST);
						return;
					}
				} catch (IllegalStateException e) {
					fail(X00_OFF, STATUS_ELEMENT_WRONG_TYPE);
					return;
				} catch (NoSuchFileException e) {
					if ( (mode & (OPEN_NEW_FILE | OPEN_CREATE)) == 0) {
						fail(X00_OFF, STATUS_ELEMENT_NOT_EXIST);
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
				try {
					file.ensureAccess(NO_LOCK, checkBits, rof);
				} catch (ElementLockedException e) {
					if (e instanceof ElementReadOnlyException) {
						fail(X00_OFF, STATUS_READ_ONLY);
						return;
					} else {
						fail(X00_OFF, STATUS_ELEMENT_LOCKED);
						return;
					}
				}
				long address = U.allocateMemory(40L);
				mem.malloc(address, 40L);
				if (address == -1L) {
					fail(X00_OFF, STATUS_OUT_OF_MEMORY);
					return;
				}
				U.putLong(address + FS_STREAM_OFFSET_FILE, address + 24L);
				U.putLong(address + FS_STREAM_OFFSET_POS, 0L);
				U.putLong(address + 16L, mode);
				U.putLong(address + 24L + FS_ELEMENT_OFFSET_ID, ((PatrFileImpl) file).id);
				U.putLong(address + 24L + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
				U.putLong(regs + X00_OFF, address);
			} catch (OutOfSpaceException e) {
				fail(X00_OFF, STATUS_OUT_OF_SPACE);
				return;
			}
		} catch (IOException e) {
			fail(X00_OFF, STATUS_IO_ERR);
			return;
		}
	}
	
	private void fail(long markingRegisterOff, long newStatusFlag) {
		if (markingRegisterOff > 0) U.putLong(regs + markingRegisterOff, -1L);
		else U.putLong(regs - markingRegisterOff, 0L);
		U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) | newStatusFlag);
	}
	
	private abstract class Cmd_1CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam();
			exec(p1);
			U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + len);
		}
		
		protected abstract void exec(long p1) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_2CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam(), p2 = getConstParam();
			exec(p1, p2);
			U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + len);
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1NCP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getNoConstParam();
			exec(p1);
			U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + len);
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
			U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + len);
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1NCP_1CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getNoConstParam();
			long p2 = getConstParam();
			exec(p1, p2);
			U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + len);
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1LP_IL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = U.getLong(U.getLong(regs + IP_OFF) + 8L);
			exec(p1);
		}
		
		protected abstract void exec(long p1) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1CP_1LP_NL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam(), p2 = U.getLong(U.getLong(regs + IP_OFF) + 8L);
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
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) { //not possible
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) { // only with (MIN + MIN)
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
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
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) {
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 > 0L) {
				if (res > 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) { // not possible
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
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
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
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
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) | STATUS_ZERO);
			} else {
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) & ~STATUS_ZERO);
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
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) | STATUS_ZERO);
			} else {
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) & ~STATUS_ZERO);
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
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) | STATUS_ZERO);
			} else {
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) & ~STATUS_ZERO);
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
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) | STATUS_ZERO);
			} else {
				U.putLong(regs + STATUS_OFF, U.getLong(regs + STATUS_OFF) & ~STATUS_ZERO);
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
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else if (res == Long.MIN_VALUE) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
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
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_CARRY | STATUS_ZERO));
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
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_CARRY | STATUS_ZERO));
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
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
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
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else if (res == Long.MAX_VALUE) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
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
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else if (res == Long.MIN_VALUE) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	// uc, new MOV(), new ADD(), new SUB(), new MUL(), new DIV(), new AND(), new OR(), new XOR(), new
	// NOT(), new NEG(), new LSH(), new RLSH(), new RASH(), new DEC(), new INC(),
	
	private class JMP extends Cmd_1LP_IL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + p1);
		}
		
	}
	
	private abstract class PosCondJmp extends Cmd_1LP_IL {
		
		private final long cond;
		
		private PosCondJmp(long cond) {
			this.cond = cond;
		}
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			if ( (U.getLong(regs + STATUS_OFF) & cond) != 0) {
				U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + p1);
			} else {
				U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + len);
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
			if ( (U.getLong(regs + STATUS_OFF) & cond) == 0) {
				U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + p1);
			} else {
				U.putLong(regs + IP_OFF, U.getLong(regs + IP_OFF) + len);
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
			long sp = U.getLong(regs + SP_OFF);
			long ip = U.getLong(regs + IP_OFF);
			U.putLong(sp, ip);
			U.putLong(regs + SP_OFF, sp + 8L);
			U.putLong(regs + IP_OFF, ip + p1);
		}
		
	}
	
	private class CMP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			if (p1 > p2) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_GREATHER) & ~ (STATUS_EQUAL | STATUS_LOWER));
			} else if (p1 < p2) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_LOWER) & ~ (STATUS_EQUAL | STATUS_GREATHER));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_EQUAL) & ~ (STATUS_GREATHER | STATUS_LOWER));
			}
		}
		
	}
	
	private class RET implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long sp = U.getLong(regs + SP_OFF) - 8L;
			U.putLong(regs + SP_OFF, sp);
			U.putLong(regs + IP_OFF, U.getLong(sp));
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
			long sp = U.getLong(regs + SP_OFF);
			U.putLong(sp, p1);
			U.putLong(regs + SP_OFF, sp + 8L);
		}
		
	}
	
	private class POP extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			long sp = U.getLong(regs + SP_OFF) - 8L;
			U.putLong(regs + SP_OFF, sp);
			setNC(p1, U.getLong(sp));
		}
		
	}
	
	private class IRET implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long zw = U.getLong(regs + X09_OFF);
			U.copyMemory(null, zw, null, regs, 128L);
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
			setNC(p1, p2 + U.getLong(regs + IP_OFF));
		}
		
	}
	
	private class MVAD extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p3 = U.getLong(U.getLong(regs + IP_OFF) + len);
			len += 8;
			setNC(p1, p2 + p3);
		}
		
	}
	
	private class CALO extends Cmd_1CP_1LP_NL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long sp = U.getLong(regs + SP_OFF);
			long ip = U.getLong(regs + IP_OFF);
			U.putLong(sp, ip);
			U.putLong(regs + SP_OFF, sp + 8L);
			U.putLong(regs + IP_OFF, p1 + p2);
		}
		
	}
	
	private class BCP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long res = p1 & p2;
			if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_NONE_BITS) & ~ (STATUS_SOME_BITS | STATUS_ALL_BITS));
			} else if (res == p2) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ALL_BITS | STATUS_SOME_BITS) & ~ (STATUS_NONE_BITS));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ALL_BITS) & ~ (STATUS_NONE_BITS | STATUS_ALL_BITS));
			}
		}
		
	}
	
	private class CMPFP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1 = Double.longBitsToDouble(p1), fp2 = Double.longBitsToDouble(p2);
			if (fp1 > fp2) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_GREATHER) & ~ (STATUS_EQUAL | STATUS_LOWER | STATUS_NAN));
			} else if (fp1 < fp2) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_LOWER) & ~ (STATUS_EQUAL | STATUS_GREATHER | STATUS_NAN));
			} else if (fp1 != fp2) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_NAN) & ~ (STATUS_EQUAL | STATUS_LOWER | STATUS_GREATHER));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_EQUAL) & ~ (STATUS_GREATHER | STATUS_LOWER | STATUS_NAN));
			}
		}
		
	}
	
	private class CHKFP extends Cmd_1CP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			double fp1 = Double.longBitsToDouble(p1);
			if (fp1 == Double.POSITIVE_INFINITY) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_GREATHER) & ~ (STATUS_ZERO | STATUS_LOWER | STATUS_NAN));
			} else if (fp1 == Double.NEGATIVE_INFINITY) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_LOWER) & ~ (STATUS_ZERO | STATUS_GREATHER | STATUS_NAN));
			} else if (fp1 != fp1) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_NAN) & ~ (STATUS_ZERO | STATUS_LOWER | STATUS_GREATHER));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_GREATHER | STATUS_LOWER | STATUS_NAN));
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
			if ( (U.getLong(regs + STATUS_OFF) & STATUS_CARRY) != 0) {
				res ++ ;
			}
			setNC(p1, res);
			if (p1v > 0L && p2 > 0L) {
				if (res < 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
					// } else if (res == 0L) { //still not possible
					// U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
			}
		}
		
	}
	
	private class SUBC extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v - p2;
			if ( (U.getLong(regs + STATUS_OFF) & STATUS_CARRY) != 0) {
				res -- ;
			}
			setNC(p1, res);
			if (p1v > 0L && p2 < 0L) {
				if (res < 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) { // still not possible
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (p1v < 0L && p2 > 0L) {
				if (res > 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_CARRY) & ~ (STATUS_ZERO));
				} else if (res == 0L) {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO | STATUS_CARRY));
				} else {
					U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
				}
			} else if (res == 0L) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_CARRY));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_ZERO | STATUS_CARRY));
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
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_NAN | STATUS_ZERO));
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
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_NAN | STATUS_ZERO));
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
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_NAN | STATUS_ZERO));
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
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_ZERO) & ~ (STATUS_NAN));
			} else if (fpres != fpres) {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF) | STATUS_NAN) & ~ (STATUS_ZERO));
			} else {
				U.putLong(regs + STATUS_OFF, (U.getLong(regs + STATUS_OFF)) & ~ (STATUS_NAN | STATUS_ZERO));
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
