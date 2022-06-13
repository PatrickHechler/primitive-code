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

public class PVMImpl implements PVM {
	
	private static final long NO_LOCK = PatrFileSysConstants.NO_LOCK;
	
	private final MemoryContainer mem;
	private final PVMData         data;
	private final Interrupt[]     defaultInts;
	private final PVMCommand[]    commands;
	private final PatrFileSysImpl fs;
	
	private long    cmd;
	private boolean off1;
	private int     off2;
	private int     off3;
	private int     len;
	private boolean isreg;
	
	public PVMImpl(PatrFileSysImpl fs) {
		this(fs, new Random());
	}
	
	public PVMImpl(PatrFileSysImpl fs, final Random rnd) {
		this.mem = new MemoryContainer();
		this.data = new PVMData(mem);
		this.fs = fs;
		this.defaultInts = new Interrupt[] {
			num -> System.exit((int) (128 + this.data.regs[X_ADD])),
			num -> System.exit(7),
			num -> System.exit(6),
			num -> System.exit(5),
			num -> System.exit((int) this.data.regs[X_ADD]),
			num -> this.data.regs[X_ADD] = this.mem.malloc(this.data.regs[X_ADD]),
			num -> this.data.regs[X_ADD + 1] = this.mem.realloc(this.data.regs[X_ADD], this.data.regs[X_ADD + 1]),
			num -> this.mem.free(this.data.regs[X_ADD]),
			num -> defIntOpenNewStream(),
			num -> defIntWrite(),
			num -> defIntRead(),
			num -> defIntGetFSElement(num),
			num -> defIntGetFSElement(num),
			num -> defIntGetFSElement(num),
			num -> defIntGetFSElement(num),
			num -> defIntDuplicateFSElementHandle(),
			num -> defIntGetParent(),
			num -> defIntFromID(),
			num -> defIntGetSomeDate(num),
			num -> defIntGetSomeDate(num),
			num -> defIntGetSomeDate(num),
			num -> defIntSetSomeDate(num),
			num -> defIntSetSomeDate(num),
			num -> defIntSetSomeDate(num),
			num -> defIntGetLockData(),
			num -> defIntGetLockDate(),
			num -> defIntLockElement(),
			num -> defIntUnlockElement(),
			num -> defIntDeleteElement(),
			num -> defIntMoveElement(),
			num -> defIntGetElementFlags(),
			num -> defIntGetElementFlags(),
			num -> defIntModifyElementFlags(),
			num -> defIntGetFolderChildElementCount(),
			num -> defIntGetChildElement(num),
			num -> defIntGetChildElement(num),
			num -> defIntAddElement(num),
			num -> defIntAddElement(num),
			num -> defIntAddElement(num),
			num -> defIntFileLength(),
			num -> defIntFileHash(),
			num -> defIntFileRWA(num),
			num -> defIntFileRWA(num),
			num -> defIntFileRWA(num),
			num -> defIntFileTruncate(),
			num -> defIntLinkGetTarget(),
			num -> defIntLinkSetTarget(),
			num -> defIntFSLock(),
			num -> defIntFSUnlock(),
			num -> this.data.regs[X_ADD] = System.currentTimeMillis(),
			num -> defIntSleep(),
			num -> this.data.regs[X_ADD] = rnd.nextLong(),
			num -> this.mem.copy(this.data.regs[X_ADD + 1], this.data.regs[X_ADD], this.data.regs[X_ADD + 2]),
			num -> this.mem.move(this.data.regs[X_ADD + 1], this.data.regs[X_ADD], this.data.regs[X_ADD + 2]),
			num -> this.mem.membset(this.data.regs[X_ADD], this.data.regs[X_ADD + 2], (int) (0xFF & this.data.regs[X_ADD + 1])),
			num -> this.mem.memset(this.data.regs[X_ADD], this.data.regs[X_ADD + 2], this.data.regs[X_ADD + 1]),
			num -> this.data.regs[X_ADD] = getU16String(this.data.regs[X_ADD]).length() << 1,
			num -> this.data.regs[X_ADD] = getU16String(this.data.regs[X_ADD]).compareTo(getU16String(this.data.regs[X_ADD + 1])),
			num -> defIntAnyNumberToString(num),
			num -> defIntAnyNumberToString(num),
			num -> defIntStringToAnyNumber(num),
			num -> defIntStringToAnyNumber(num),
			num -> defIntFormattString(),
			num -> defIntStringConvert(num),
			num -> defIntStringConvert(num),
			num -> defIntLoadFile(),
		};
		if (this.defaultInts.length != INTERRUPT_COUNT) {
			throw new AssertionError("expected int-count=" + INTERRUPT_COUNT + " int-count=" + this.defaultInts.length);
		}
		PVMCommand uc = () -> interrupt(INT_ERRORS_UNKNOWN_COMMAND);
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
					throw new AssertionError("command at 0x" + Integer.toHexString(val) + " was not of the correct type! expected: " + f.getName() + " but got type: " + c.getClass().getSimpleName());
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
			cmd = mem.get(data.regs[IP]);
			this.commands[(int) (cmd & 0xFF)].execute();
		} catch (PrimitiveErrror e) {
			interrupt(e.intNum);
		}
	}
	
	private long getNC(long p) throws PrimitiveErrror {
		if (isreg) {
			assert p == (int) p;
			return data.regs[(int) p];
		} else {
			return mem.get(p);
		}
	}
	
	private void setNC(long p, long val) throws PrimitiveErrror {
		if (isreg) {
			assert p == (int) p;
			data.regs[(int) p] = val;
		} else {
			mem.set(p, val);
		}
	}
	
	private long getNC(boolean isreg, long p) throws PrimitiveErrror {
		if (isreg) {
			assert p == (int) p;
			return data.regs[(int) p];
		} else {
			return mem.get(p);
		}
	}
	
	private void setNC(boolean isreg, long p, long val) throws PrimitiveErrror {
		if (isreg) {
			assert p == (int) p;
			data.regs[(int) p] = val;
		} else {
			mem.set(p, val);
		}
	}
	
	private long getConstParam() throws PrimitiveErrror {
		switch ((int) (0xFF & (cmd >> (off1 ? 48 : 40)))) {
		case PARAM_ART_ANUM:
			len += 8;
			return mem.get(data.regs[IP] + off2 ++ );
		case PARAM_ART_ANUM_BNUM:
			len += 16;
			long a = mem.get(data.regs[IP] + (off2 += 8));
			long b = mem.get(data.regs[IP] + (off2 += 8));
			return mem.get(a + b);
		case PARAM_ART_ANUM_BREG:
			len += 8;
			a = mem.get(data.regs[IP] + (off2 += 8));
			return mem.get(a);
		case PARAM_ART_ANUM_BSR:
			len += 8;
			a = mem.get(data.regs[IP] + (off2 += 8));
			b = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			return mem.get(a + b);
		case PARAM_ART_ASR:
			a = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			return a;
		case PARAM_ART_ASR_BNUM:
			len += 8;
			a = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			b = mem.get(data.regs[IP] + (off2 += 8));
			return mem.get(a + b);
		case PARAM_ART_ASR_BREG:
			a = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			return mem.get(a);
		case PARAM_ART_ASR_BSR:
			a = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			b = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			return mem.get(a + b);
		default:
			throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND);
		}
	}
	
	private long getNoConstParam() throws PrimitiveErrror {
		isreg = false;
		switch ((int) (0xFF & (cmd >> (off1 ? 48 : 40)))) {
		case PARAM_ART_ANUM:
			len += 8;
			return mem.get(data.regs[IP] + off2 ++ );
		case PARAM_ART_ANUM_BNUM:
			len += 16;
			long a = mem.get(data.regs[IP] + (off2 += 8));
			long b = mem.get(data.regs[IP] + (off2 += 8));
			return (a + b);
		case PARAM_ART_ANUM_BREG:
			len += 8;
			a = mem.get(data.regs[IP] + (off2 += 8));
			return (a);
		case PARAM_ART_ANUM_BSR:
			len += 8;
			a = mem.get(data.regs[IP] + (off2 += 8));
			b = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			return (a + b);
		case PARAM_ART_ASR:
			a = (cmd >> (off3 += 8) & 0xFF);
			isreg = true;
			return a;
		case PARAM_ART_ASR_BNUM:
			len += 8;
			a = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			b = mem.get(data.regs[IP] + (off2 += 8));
			return (a + b);
		case PARAM_ART_ASR_BREG:
			a = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			return (a);
		case PARAM_ART_ASR_BSR:
			a = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			b = data.regs[(int) (cmd >> (off3 += 8) & 0xFF)];
			return (a + b);
		default:
			throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND);
		}
	}
	
	private void interrupt(long intNum) {
		while (true) {
			try {
				if (intNum < 0L || intNum >= data.regs[INTCNT]) {
					if (data.regs[INTCNT] > 0L) {
						long old = data.regs[X_ADD];
						data.regs[X_ADD] = intNum;
						interrupt(INT_ERRORS_ILLEGAL_INTERRUPT);
						data.regs[X_ADD] = old;
					} else {
						System.exit(128);
					}
				} else if (data.regs[INTP] == -1L) {
					defInt(intNum);
				} else {
					long address = mem.get(data.regs[INTP] + (intNum << 3));
					if (address == -1L) {
						defInt(intNum);
					} else {
						long save = mem.malloc(128);
						mem.set(save, data.regs[IP]);
						mem.set(save + 8L, data.regs[SP]);
						mem.set(save + 16L, data.regs[STATUS]);
						mem.set(save + 24L, data.regs[INTCNT]);
						mem.set(save + 32L, data.regs[INTP]);
						mem.set(save + 40L, data.regs[FS_LOCK]);
						mem.set(save + 48L, data.regs[X_ADD]);
						mem.set(save + 56L, data.regs[X_ADD + 1]);
						mem.set(save + 64L, data.regs[X_ADD + 2]);
						mem.set(save + 72L, data.regs[X_ADD + 3]);
						mem.set(save + 80L, data.regs[X_ADD + 4]);
						mem.set(save + 88L, data.regs[X_ADD + 5]);
						mem.set(save + 86L, data.regs[X_ADD + 6]);
						mem.set(save + 88L, data.regs[X_ADD + 7]);
						mem.set(save + 112L, data.regs[X_ADD + 8]);
						mem.set(save + 120L, data.regs[X_ADD + 9]);
						data.regs[X_ADD + 9] = save;
						data.regs[IP] = address;
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
		byte[] bytes = new byte[16];
		int len;
		for (len = 0;; len ++ ) {
			if (len >= bytes.length) {
				bytes = Arrays.copyOf(bytes, len + len >> 1);
			}
			bytes[len] = (byte) mem.getByte(address + len);
			if (bytes[len] == 0 && len > 0 && bytes[len - 1] == 0) {
				break;
			}
		}
		return new String(bytes, 0, len, StandardCharsets.UTF_16BE);
	}
	
	private String getU8String(long address) throws PrimitiveErrror {
		byte[] bytes = new byte[16];
		int len;
		for (len = 0;; len ++ ) {
			if (len >= bytes.length) {
				bytes = Arrays.copyOf(bytes, len + len >> 1);
			}
			bytes[len] = (byte) mem.getByte(address + len);
			if (bytes[len] == 0) {
				break;
			}
		}
		return new String(bytes, 0, len, StandardCharsets.UTF_8);
	}
	
	private void defIntLoadFile() throws PrimitiveErrror {
		String[] names = getU16String(data.regs[X_ADD]).split("\\/");
		try {
			PatrFolder parent = fs.getRoot();
			for (int i = names[0].isEmpty() ? 1 : 0; i < names.length - 1; i ++ ) {
				parent = parent.getElement(names[i], NO_LOCK).getFolder();
			}
			PatrFile file = parent.getElement(names[names.length - 1], NO_LOCK).getFile();
			long len = file.length(NO_LOCK);
			long addr = mem.malloc(len);
			if (addr == -1L) {
				fail(X_ADD, STATUS_OUT_OF_MEMORY);
				return;
			}
			byte[] bytes = new byte[(int) Math.min(1 << 30, len)];
			for (long wrote = 0L; wrote < len;) {
				int cpy = (int) Math.min(bytes.length, len - wrote);
				file.getContent(bytes, wrote, 0, cpy, NO_LOCK);
				// FIXME optimize
				for (int si = 0; si < cpy; si ++ ) {
					mem.setByte(addr + wrote + si, bytes[si]);
				}
			}
			data.regs[X_ADD] = addr;
			data.regs[X_ADD + 1] = len;
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
		String input = toU8 ? getU16String(data.regs[X_ADD]) : getU8String(data.regs[X_ADD]);
		byte[] result = input.getBytes(toU8 ? StandardCharsets.UTF_8 : StandardCharsets.UTF_16BE);
		long len = result.length + (toU8 ? 1L : 2L);
		if (data.regs[X_ADD + 2] < len) {
			if (data.regs[X_ADD + 2] == 0L) {
				data.regs[X_ADD + 1] = mem.malloc(len);
			} else if (data.regs[X_ADD + 2] > 0L) {
				data.regs[X_ADD + 1] = mem.realloc(data.regs[X_ADD + 1], len);
			} else {
				fail(X_ADD + 3, STATUS_ILLEGAL_ARG);
				return;
			}
			data.regs[X_ADD + 2] = len;
		}
		for (int i = 0; i < result.length; i ++ ) {
			mem.setByte(data.regs[X_ADD + 1] + i, result[i]);
		}
		mem.setByte(data.regs[X_ADD + 1] + result.length, 0);
		if ( !toU8) {
			mem.setByte(data.regs[X_ADD + 1] + result.length + 1, 0);
		}
		data.regs[X_ADD + 3] = data.regs[X_ADD + 1] + result.length;
	}
	
	private void defIntFormattString() throws PrimitiveErrror {
		String input = getU16String(data.regs[X_ADD]);
		char[] cs = input.toCharArray();
		StringBuilder result = new StringBuilder();
		int argN = X_ADD + 3;
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
			if (argN >= data.regs.length) {
				fail(X_ADD, STATUS_ILLEGAL_ARG);
				return;
			}
			long arg = data.regs[argN ++ ];
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
		byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_16BE);
		if (data.regs[X_ADD + 2] < bytes.length + 2L) {
			if (data.regs[X_ADD + 2] == 0L) {
				data.regs[X_ADD + 1] = mem.malloc(bytes.length + 2L);
			} else if (data.regs[X_ADD + 2] > 0L) {
				data.regs[X_ADD + 1] = mem.realloc(data.regs[X_ADD + 1], bytes.length + 2L);
			} else {
				fail(X_ADD, STATUS_ILLEGAL_ARG);
				return;
			}
			data.regs[X_ADD + 2] = bytes.length + 2L;
		}
		// FIXME optimize
		for (int i = 0; i < bytes.length; i ++ ) {
			mem.setByte(data.regs[X_ADD + 1] + i, bytes[i]);
		}
		mem.setByte(data.regs[X_ADD + 1] + bytes.length, 0);
		mem.setByte(data.regs[X_ADD + 1] + bytes.length + 1, 0);
	}
	
	private void defIntStringToAnyNumber(int intNum) throws PrimitiveErrror {
		String str = getU16String(data.regs[X_ADD]).trim();
		try {
			switch (intNum) {
			case (int) INT_STRING_TO_NUMBER: {
				int numsys = (int) data.regs[X_ADD + 1];
				if ( ((long) numsys) != data.regs[X_ADD + 1] || numsys < 2 || numsys > 36) {
					fail( -X_ADD - 1, STATUS_ILLEGAL_ARG);
					return;
				}
				data.regs[X_ADD] = Long.parseLong(str, numsys);
				break;
			}
			case (int) INT_STRING_TO_FPNUMBER: {
				data.regs[X_ADD] = Double.doubleToRawLongBits(Double.parseDouble(str));
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
		int lenOff;
		switch (intNum) {
		case (int) INT_NUMBER_TO_STRING:
			if (data.regs[X_ADD + 2] > 36L || data.regs[X_ADD + 2] < 2L) {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
				return;
			}
			bytes = Long.toString(data.regs[X_ADD], (int) data.regs[X_ADD + 2]).toUpperCase().getBytes(StandardCharsets.UTF_16BE);
			lenOff = X_ADD + 3;
			break;
		case (int) INT_FPNUMBER_TO_STRING:
			bytes = Double.toString(Double.longBitsToDouble(data.regs[X_ADD])).getBytes(StandardCharsets.UTF_16BE);
			lenOff = X_ADD + 2;
			break;
		default:
			throw new InternalError();
		}
		long addr = data.regs[X_ADD + 1];
		if (bytes.length + 2L > data.regs[lenOff]) {
			if (data.regs[lenOff] == 0L) {
				addr = mem.malloc(bytes.length + 2L);
			} else if (data.regs[lenOff] > 0L) {
				addr = mem.realloc(addr, bytes.length + 2L);
			} else {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
				return;
			}
			data.regs[lenOff] = bytes.length + 2L;
		}
		// FIXME optimize
		for (int i = 0; i < bytes.length; i ++ ) {
			mem.setByte(addr + i, 0xFF & bytes[i]);
		}
		mem.setByte(addr + bytes.length, 0);
		mem.setByte(addr + bytes.length + 1, 0);
	}
	
	private void defIntSleep() {
		long start = System.nanoTime();
		try {
			Thread.sleep( (data.regs[X_ADD + 2] * 1000) + (data.regs[X_ADD + 1] / 1000000), (int) data.regs[X_ADD + 1]);
			data.regs[X_ADD + 1] = data.regs[X_ADD + 2] = 0L;
		} catch (InterruptedException e) {
			long remain = System.nanoTime() - start;
			data.regs[X_ADD + 1] = remain % 1000000000L;
			data.regs[X_ADD + 2] = remain / 1000000000L;
		}
	}
	
	private void defIntFSUnlock() throws PrimitiveErrror {
		try {
			fs.setLock(data.regs[FS_LOCK]);
			fs.removeLock();
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
			data.regs[FS_LOCK] = fs.lock(data.regs[X_ADD]);
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntLinkSetTarget() throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK),
			ntaddress = data.regs[X_ADD + 1],
			ntid = mem.get(ntaddress + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getLink().getTarget(lock);
			mem.set(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) e).id);
			mem.set(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			long newLen = data.regs[X_ADD + 1];
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			final long length = data.regs[X_ADD + 1],
				addr = data.regs[X_ADD + 2];
			byte[] bytes = new byte[(int) Math.min(length, 1 << 30)];
			long copied;
			for (copied = 0L; copied < length;) {
				int cpy = (int) Math.min(bytes.length, length - copied);
				switch (intNum) {
				case (int) INT_FS_FILE_READ:
					e.getContent(bytes, data.regs[X_ADD + 3], 0, cpy, lock);
					// FIXME optimize
					for (int i = 0; i < cpy; i ++ ) {
						mem.set(addr + i, 0xFF & bytes[i]);
					}
					break;
				case (int) INT_FS_FILE_WRITE:
				case (int) INT_FS_FILE_APPEND:
					// FIXME optimize
					for (int i = 0; i < cpy; i ++ ) {
						bytes[i] = (byte) mem.get(addr + i);
					}
					if (intNum == (int) INT_FS_FILE_APPEND) {
						e.appendContent(bytes, 0, cpy, lock);
					} else {
						e.setContent(bytes, data.regs[X_ADD + 3], 0, cpy, lock);
					}
					break;
				default:
					throw new InternalError();
				}
			}
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			final long addr = data.regs[X_ADD + 1];
			byte[] bytes = e.getHashCode(lock);
			for (int i = 0; i < bytes.length; i ++ ) {
				mem.setByte(addr + i, 0xFF & bytes[i]);
			}
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFile e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFile();
			data.regs[X_ADD + 1] = e.length(lock);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			String name = getU16String(data.regs[X_ADD + 1]);
			PatrFileSysElement child;
			switch (intNum) {
			case (int) INT_FS_FOLDER_ADD_FILE:
				child = e.addFile(name, lock);
				break;
			case (int) INT_FS_FOLDER_ADD_FOLDER:
				child = e.addFolder(name, lock);
				break;
			case (int) INT_FS_FOLDER_ADD_LINK: {
				final long taddress = data.regs[X_ADD + 2],
					tid = mem.get(taddress + FS_ELEMENT_OFFSET_ID);
				PatrFolder te = fs.fromID(new PatrID(fs, tid, fs.getStartTime())).getFolder();
				child = e.addLink(name, te, lock);
				break;
			}
			default:
				throw new InternalError();
			}
			mem.set(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) child).id);
			mem.set(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			PatrFileSysElement child;
			switch (intNum) {
			case (int) INT_FS_FOLDER_GET_CHILD_OF_INDEX: {
				int index = (int) data.regs[X_ADD + 1];
				if (index != data.regs[X_ADD + 1]) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				child = e.getElement(index, lock);
				break;
			}
			case (int) INT_FS_FOLDER_GET_CHILD_OF_NAME: {
				String name = getU16String(data.regs[X_ADD + 1]);
				child = e.getElement(name, lock);
				break;
			}
			default:
				throw new InternalError();
			}
			mem.set(address + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) child).id);
			mem.set(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFolder e = fs.fromID(new PatrID(fs, id, fs.getStartTime())).getFolder();
			data.regs[X_ADD + 1] = e.elementCount(lock);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			((PatrFileSysElementImpl) e).flag((int) data.regs[X_ADD + 1], (int) data.regs[X_ADD + 2]);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetElementFlags() throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			data.regs[X_ADD + 1] = ((PatrFileSysElementImpl) e).getFlags();
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntMoveElement() throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK),
			npaddress = data.regs[X_ADD];
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			if (npaddress != -1L) {
				final long npid = mem.get(npaddress + FS_ELEMENT_OFFSET_ID),
					nplock = mem.get(npaddress + FS_ELEMENT_OFFSET_LOCK);
				PatrFolder np = fs.fromID(new PatrID(fs, npid, fs.getStartTime())).getFolder();
				e.setParent(np, lock, data.regs[X_ADD + 3], nplock);
			}
			if (data.regs[X_ADD + 2] != -1L) {
				String newName = getU16String(data.regs[X_ADD + 2]);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			e.delete(lock, data.regs[X_ADD + 1]);
			mem.free(address);
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
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			e.removeLock(lock);
			mem.set(address + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			data.regs[X_ADD + 1] = 1L;
		} catch (ElementLockedException e) {
			fail( -X_ADD - 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail( -X_ADD - 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail( -X_ADD - 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntLockElement() throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			mem.set(address + FS_ELEMENT_OFFSET_LOCK, e.lock(data.regs[X_ADD + 1]));
		} catch (ElementLockedException e) {
			fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD + 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetLockDate() throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			data.regs[X_ADD + 1] = e.getLockTime();
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetLockData() throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			data.regs[X_ADD] = e.getLockData();
		} catch (ElementLockedException e) {
			fail(X_ADD, STATUS_ELEMENT_LOCKED);
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntSetSomeDate(int intNum) throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID),
			lock = mem.get(address + FS_ELEMENT_OFFSET_LOCK);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			switch (intNum) {
			case (int) INT_FS_ELEMENT_SET_CREATE:
				e.setCreateTime(data.regs[X_ADD + 1], lock);
				break;
			case (int) INT_FS_ELEMENT_SET_LAST_MOD:
				e.setLastModTime(data.regs[X_ADD + 1], lock);
				break;
			case (int) INT_FS_ELEMENT_SET_LAST_META_MOD:
				e.setLastMetaModTime(data.regs[X_ADD + 1], lock);
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
	
	private void defIntGetSomeDate(int itnNum) throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			switch (itnNum) {
			case (int) INT_FS_ELEMENT_GET_CREATE:
				data.regs[X_ADD + 1] = e.getCreateTime();
				break;
			case (int) INT_FS_ELEMENT_GET_LAST_MOD:
				data.regs[X_ADD + 1] = e.getLastModTime();
				break;
			case (int) INT_FS_ELEMENT_GET_LAST_META_MOD:
				data.regs[X_ADD + 1] = e.getLastMetaModTime();
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
		final long id = data.regs[X_ADD];
		try {
			fs.setLock(data.regs[FS_LOCK]);
			fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			long addr = mem.malloc(16L);
			mem.set(addr + FS_ELEMENT_OFFSET_ID, id);
			mem.set(addr + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			data.regs[X_ADD] = addr;
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail(X_ADD, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
		}
	}
	
	private void defIntGetParent() throws PrimitiveErrror {
		final long address = data.regs[X_ADD],
			id = mem.get(address + FS_ELEMENT_OFFSET_ID);
		try {
			fs.setLock(data.regs[FS_LOCK]);
			PatrFileSysElement e = fs.fromID(new PatrID(fs, id, fs.getStartTime()));
			PatrFolder parent = e.getParent();
			data.regs[X_ADD] = ((PatrFileSysElementImpl) parent).id;
			data.regs[X_ADD] = 1L;
		} catch (IllegalStateException | IllegalArgumentException e) {
			fail( -X_ADD - 1, STATUS_ILLEGAL_ARG);
		} catch (IOException e) {
			fail( -X_ADD - 1, STATUS_IO_ERR);
		}
	}
	
	private void defIntDuplicateFSElementHandle() throws PrimitiveErrror {
		long address = data.regs[X_ADD];
		long dup = mem.malloc(16L);
		data.regs[X_ADD] = dup;
		if (dup != -1L) {
			mem.copy(address, dup, 16L);
		}
	}
	
	private void defIntGetFSElement(int intNum) throws PrimitiveErrror {
		String path = getU16String(data.regs[X_ADD]);
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
			long addr = mem.malloc(16L);
			mem.set(addr + FS_ELEMENT_OFFSET_ID, ((PatrFileSysElementImpl) file).id);
			mem.set(addr + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
			data.regs[X_ADD] = addr;
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
		final long id = data.regs[X_ADD],
			len = data.regs[X_ADD + 1],
			addr = data.regs[X_ADD + 2];
		if (id <= MAX_STD_STREAM) {
			InputStream in;
			if (id == STD_IN) {
				in = System.in;
			} else {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
				return;
			}
			// FIXME optimize
			for (long reat = 0L; reat < len; reat ++ ) {
				try {
					mem.set(addr + reat, in.read());
				} catch (IOException e) {
					if (reat == 0L) fail(X_ADD + 1, STATUS_IO_ERR);
					else data.regs[X_ADD + 1] = reat;
					return;
				}
			}
		} else {
			try {
				long pos = mem.get(id + FS_STREAM_OFFSET_POS),
					fileAddr = mem.get(id + FS_STREAM_OFFSET_FILE),
					mode = mem.get(id + 16L),
					fid = mem.get(fileAddr + FS_ELEMENT_OFFSET_ID),
					lock = mem.get(fileAddr + FS_ELEMENT_OFFSET_LOCK);
				if ( (mode & OPEN_READ) != 0) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				fs.setLock(data.regs[FS_LOCK]);
				PatrFile file;
				try {
					file = fs.fromID(new PatrID(fs, fid, fs.getStartTime())).getFile();
				} catch (IllegalStateException e) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				byte[] bytes = new byte[(int) Math.min(1024L, len)];
				file.withLock(() -> {
					long npos = pos;
					for (long reat = 0L; reat < len;) {
						// FIXME optimize
						for (int i = 0; i < bytes.length; i ++ ) {
							bytes[i] = (byte) mem.getByte(addr + reat);
						}
						try {
							try {
								long length = file.length(lock);
								if (npos >= length) {
									assert npos == length;
									mem.set(id + FS_STREAM_OFFSET_POS, npos);
									data.regs[X_ADD + 1] = reat;
									return;
								}
								int cpy = (int) Math.min(len - reat, Math.min(length - npos, bytes.length));
								file.getContent(bytes, npos, 0, cpy, lock);
								npos += cpy;
								reat += cpy;
								mem.set(id + FS_STREAM_OFFSET_POS, npos);
							} catch (ElementLockedException e) {
								if (e instanceof ElementLockedException) fail(X_ADD + 1, STATUS_READ_ONLY);
								else fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
								return;
							}
						} catch (IOException e) {
							fail(X_ADD + 1, STATUS_IO_ERR);
							return;
						}
					}
					data.regs[X_ADD + 1] = len;
				});
				return;
			} catch (IOException e) {
				fail(X_ADD + 1, STATUS_IO_ERR);
				return;
			}
		}
	}
	
	private void defIntWrite() throws PrimitiveErrror {
		final long id = data.regs[X_ADD],
			len = data.regs[X_ADD + 1],
			addr = data.regs[X_ADD + 2];
		if (id <= MAX_STD_STREAM) {
			OutputStream out;
			if (id == STD_OUT) {
				out = System.out;
			} else if (id == STD_LOG) {
				out = System.err;
			} else {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
				return;
			}
			if (len < 0L) {
				fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
			}
			// FIXME optimize
			for (long wrote = 0L; wrote < len; wrote ++ ) {
				try {
					out.write(mem.getByte(addr + wrote));
				} catch (IOException e) {
					fail(X_ADD + 1, STATUS_IO_ERR);
					return;
				}
			}
		} else {
			try {
				final long pos = mem.get(id + FS_STREAM_OFFSET_POS),
					fileAddr = mem.get(id + FS_STREAM_OFFSET_FILE),
					mode = mem.get(id + 16L),
					fid = mem.get(fileAddr + FS_ELEMENT_OFFSET_ID),
					lock = mem.get(fileAddr + FS_ELEMENT_OFFSET_LOCK);
				if ( (mode & OPEN_WRITE) != 0) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				fs.setLock(data.regs[FS_LOCK]);
				PatrFile file;
				try {
					file = fs.fromID(new PatrID(fs, fid, fs.getStartTime())).getFile();
				} catch (IllegalStateException e) {
					fail(X_ADD + 1, STATUS_ILLEGAL_ARG);
					return;
				}
				boolean append = (mode & OPEN_APPEND) != 0;
				byte[] bytes = new byte[(int) Math.min(1024L, len)];
				file.withLock(() -> {
					long npos = pos;
					for (long wrote = 0L; wrote < len;) {
						// FIXME optimize
						for (int i = 0; i < bytes.length; i ++ ) {
							bytes[i] = (byte) mem.getByte(addr + wrote);
						}
						try {
							try {
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
								mem.set(id + FS_STREAM_OFFSET_POS, npos);
							} catch (OutOfSpaceException e) {
								fail(X_ADD + 1, STATUS_OUT_OF_SPACE);
								return;
							} catch (ElementLockedException e) {
								if (e instanceof ElementLockedException) fail(X_ADD + 1, STATUS_READ_ONLY);
								else fail(X_ADD + 1, STATUS_ELEMENT_LOCKED);
								return;
							}
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
		String path = getU16String(data.regs[X_ADD]);
		String[] names = path.split("\\/");
		try {
			try {
				PatrFolder parent = fs.getRoot();
				for (int i = 0; i < names.length - 1; i ++ ) {
					parent = parent.getElement(names[i], NO_LOCK).getFolder();
				}
				long mode = data.regs[X_ADD + 1];
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
				try {
					file.ensureAccess(NO_LOCK, checkBits, rof);
				} catch (ElementLockedException e) {
					if (e instanceof ElementReadOnlyException) {
						fail(X_ADD, STATUS_READ_ONLY);
						return;
					} else {
						fail(X_ADD, STATUS_ELEMENT_LOCKED);
						return;
					}
				}
				long address = mem.malloc(40L);
				if (address == -1L) {
					fail(X_ADD, STATUS_OUT_OF_MEMORY);
					return;
				}
				mem.set(address + FS_STREAM_OFFSET_FILE, address + 24L);
				mem.set(address + FS_STREAM_OFFSET_POS, 0L);
				mem.set(address + 16L, mode);
				mem.set(address + 24L + FS_ELEMENT_OFFSET_ID, ((PatrFileImpl) file).id);
				mem.set(address + 24L + FS_ELEMENT_OFFSET_LOCK, NO_LOCK);
				data.regs[X_ADD] = address;
			} catch (OutOfSpaceException e) {
				fail(X_ADD, STATUS_OUT_OF_SPACE);
				return;
			}
		} catch (IOException e) {
			fail(X_ADD, STATUS_IO_ERR);
			return;
		}
	}
	
	private void fail(int markingRegister, long newStatusFlag) {
		if (markingRegister > 0) data.regs[markingRegister] = -1L;
		else data.regs[ -markingRegister] = 0L;
		data.regs[STATUS] |= newStatusFlag;
	}
	
	private abstract class Cmd_1CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam();
			exec(p1);
			data.regs[IP] += len;
		}
		
		protected abstract void exec(long p1) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_2CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam(),
				p2 = getConstParam();
			exec(p1, p2);
			data.regs[IP] += len;
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1NCP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getNoConstParam();
			exec(p1);
			data.regs[IP] += len;
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
			data.regs[IP] += len;
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1NCP_1CP_AL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getNoConstParam();
			long p2 = getConstParam();
			exec(p1, p2);
			data.regs[IP] += len;
		}
		
		protected abstract void exec(long p1, long p2) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1LP_IL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = mem.get(data.regs[IP] + 1L);
			exec(p1);
		}
		
		protected abstract void exec(long p1) throws PrimitiveErrror;
		
	}
	
	private abstract class Cmd_1CP_1LP_NL implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long p1 = getConstParam(),
				p2 = mem.get(data.regs[IP] + 1L);
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
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
					// } else if (res == 0L) { //not possible
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
				} else if (res == 0L) { // only with (MIN + MIN)
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
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
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
					// } else if (res == 0L) {
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (p1v < 0L && p2 > 0L) {
				if (res > 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
					// } else if (res == 0L) { // not possible
					// data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
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
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
				} else if (res == 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
				} else if (res == 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
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
				data.regs[STATUS] |= STATUS_ZERO;
			} else {
				data.regs[STATUS] &= ~STATUS_ZERO;
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
				data.regs[STATUS] |= STATUS_ZERO;
			} else {
				data.regs[STATUS] &= ~STATUS_ZERO;
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
				data.regs[STATUS] |= STATUS_ZERO;
			} else {
				data.regs[STATUS] &= ~STATUS_ZERO;
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
				data.regs[STATUS] |= STATUS_ZERO;
			} else {
				data.regs[STATUS] &= ~STATUS_ZERO;
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
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else if (res == Long.MIN_VALUE) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_CARRY | STATUS_ZERO);
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
					data.regs[STATUS] |= STATUS_CARRY | STATUS_ZERO;
				} else {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~STATUS_ZERO;
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~ (STATUS_CARRY | STATUS_ZERO);
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
					data.regs[STATUS] |= STATUS_CARRY | STATUS_ZERO;
				} else {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~STATUS_ZERO;
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~ (STATUS_CARRY | STATUS_ZERO);
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
					data.regs[STATUS] |= STATUS_CARRY | STATUS_ZERO;
				} else {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~STATUS_ZERO;
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~ (STATUS_CARRY | STATUS_ZERO);
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
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else if (res == Long.MAX_VALUE) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_CARRY | STATUS_ZERO);
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
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else if (res == Long.MIN_VALUE) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_CARRY | STATUS_ZERO);
			}
		}
		
	}
	
	// uc, new MOV(), new ADD(), new SUB(), new MUL(), new DIV(), new AND(), new OR(), new XOR(), new NOT(), new NEG(), new LSH(), new RLSH(), new RASH(), new DEC(), new INC(),
	
	private class JMP extends Cmd_1LP_IL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			data.regs[IP] += p1;
		}
		
	}
	
	private abstract class PosCondJmp extends Cmd_1LP_IL {
		
		private final long cond;
		
		private PosCondJmp(long cond) {
			this.cond = cond;
		}
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			if ( (data.regs[STATUS] & cond) != 0) {
				data.regs[IP] += p1;
			} else {
				data.regs[IP] += len;
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
			if ( (data.regs[STATUS] & cond) == 0) {
				data.regs[IP] += p1;
			} else {
				data.regs[IP] += len;
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
	
	// new JMP(), new JMPEQ(), new JMPNE(), new JMPGT(), new JMPGE(), new JMPLT(), new JMPLE(), new JMPCS(), new JMPCC(), new JMPZS(), new JMPZC(), new JMPNAN(), new JMPAN(), uc, uc, uc,
	
	private class CALL extends Cmd_1LP_IL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			mem.set(data.regs[SP], data.regs[IP]);
			data.regs[SP] += 8;
			data.regs[IP] += p1;
		}
		
	}
	
	private class CMP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			if (p1 > p2) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_GREATHER) & ~ (STATUS_EQUAL | STATUS_LOWER);
			} else if (p1 < p2) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_LOWER) & ~ (STATUS_EQUAL | STATUS_GREATHER);
			} else {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_EQUAL) & ~ (STATUS_GREATHER | STATUS_LOWER);
			}
		}
		
	}
	
	private class RET implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			data.regs[SP] -= 8;
			data.regs[IP] = mem.get(data.regs[SP]);
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
			mem.set(data.regs[SP], p1);
			data.regs[SP] += 8;
		}
		
	}
	
	private class POP extends Cmd_1NCP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			data.regs[SP] -= 8;
			setNC(data.regs[SP], p1);
		}
		
	}
	
	private class IRET implements PVMCommand {
		
		@Override
		public void execute() throws PrimitiveErrror {
			long zw = data.regs[X_ADD + 0x09];
			data.regs[IP] = mem.get(zw);
			data.regs[SP] = mem.get(zw + 8);
			data.regs[STATUS] = mem.get(zw + 16);
			data.regs[INTCNT] = mem.get(zw + 24);
			data.regs[INTP] = mem.get(zw + 32);
			data.regs[FS_LOCK] = mem.get(zw + 40);
			data.regs[X_ADD] = mem.get(zw + 48);
			data.regs[X_ADD + 1] = mem.get(zw + 56);
			data.regs[X_ADD + 2] = mem.get(zw + 64);
			data.regs[X_ADD + 3] = mem.get(zw + 72);
			data.regs[X_ADD + 4] = mem.get(zw + 80);
			data.regs[X_ADD + 5] = mem.get(zw + 88);
			data.regs[X_ADD + 6] = mem.get(zw + 96);
			data.regs[X_ADD + 7] = mem.get(zw + 104);
			data.regs[X_ADD + 8] = mem.get(zw + 112);
			data.regs[X_ADD + 9] = mem.get(zw + 120);
		}
		
	}
	
	private class SWAP extends Cmd_2NCP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(isregp1, p1),
				p2v = getNC(p2);
			setNC(isregp1, p1, p2v);
			setNC(p2, p1v);
		}
		
	}
	
	private class LEA extends Cmd_2NCP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			setNC(p1, p2 + data.regs[IP]);
		}
		
	}
	
	private class MVAD extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p3 = mem.get(data.regs[IP] + len);
			len += 8;
			setNC(p1, p2 + p3);
		}
		
	}
	
	private class CALO extends Cmd_1CP_1LP_NL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			mem.set(data.regs[SP], data.regs[IP]);
			data.regs[SP] += 8;
			data.regs[IP] = p1 + p2;
		}
		
	}
	
	private class BCP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long res = p1 & p2;
			if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_NONE_BITS) & ~ (STATUS_SOME_BITS | STATUS_ALL_BITS);
			} else if (res == p2) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ALL_BITS | STATUS_SOME_BITS) & ~ (STATUS_NONE_BITS);
			} else {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_SOME_BITS) & ~ (STATUS_NONE_BITS | STATUS_ALL_BITS);
			}
		}
		
	}
	
	private class CMPFP extends Cmd_2CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1 = Double.longBitsToDouble(p1),
				fp2 = Double.longBitsToDouble(p2);
			if (fp1 > fp2) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_GREATHER) & ~ (STATUS_EQUAL | STATUS_LOWER | STATUS_NAN);
			} else if (fp1 < fp2) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_LOWER) & ~ (STATUS_GREATHER | STATUS_EQUAL | STATUS_NAN);
			} else if (fp1 != fp2) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_NAN) & ~ (STATUS_GREATHER | STATUS_EQUAL | STATUS_LOWER);
			} else {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_EQUAL) & ~ (STATUS_GREATHER | STATUS_LOWER | STATUS_NAN);
			}
		}
		
	}
	
	private class CHKFP extends Cmd_1CP_AL {
		
		@Override
		protected void exec(long p1) throws PrimitiveErrror {
			double fp1 = Double.longBitsToDouble(p1);
			if (fp1 == Double.POSITIVE_INFINITY) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_GREATHER) & ~ (STATUS_ZERO | STATUS_LOWER | STATUS_NAN);
			} else if (fp1 == Double.NEGATIVE_INFINITY) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_LOWER) & ~ (STATUS_GREATHER | STATUS_ZERO | STATUS_NAN);
			} else if (fp1 != fp1) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_NAN) & ~ (STATUS_GREATHER | STATUS_ZERO | STATUS_LOWER);
			} else {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_EQUAL) & ~ (STATUS_ZERO | STATUS_LOWER | STATUS_NAN);
			}
		}
		
	}
	
	// new CALL(), new CMP(), new RET(), new INT(), new PUSH(), new POP(), new IRET(), new SWAP(), new LEA(), new MVAD(), new CALO(), new BCP(), new CMPFP(), new CHKFP(), uc, uc,
	
	private class ADDC extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v + p2;
			if ( (data.regs[STATUS] & STATUS_CARRY) != 0) {
				res ++ ;
			}
			setNC(p1, res);
			if (p1v > 0L && p2 > 0L) {
				if (res < 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
				} else if (res == 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (p1v < 0L && p2 < 0L) {
				if (res > 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
				} else if (res == 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
			}
		}
		
	}
	
	private class SUBC extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			long p1v = getNC(p1);
			long res = p1v - p2;
			if ( (data.regs[STATUS] & STATUS_CARRY) != 0) {
				res -- ;
			}
			setNC(p1, res);
			if (p1v > 0L && p2 < 0L) {
				if (res < 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
				} else if (res == 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (p1v < 0L && p2 > 0L) {
				if (res > 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_CARRY) & ~ (STATUS_ZERO);
				} else if (res == 0L) {
					data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO | STATUS_CARRY);
				} else {
					data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
				}
			} else if (res == 0L) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_CARRY);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_ZERO | STATUS_CARRY);
			}
		}
		
	}
	
	private class ADDFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)),
				fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v + fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_NAN);
			} else if (fpres != fpres) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_NAN) & ~ (STATUS_ZERO);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_NAN | STATUS_ZERO);
			}
		}
		
	}
	
	private class SUBFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)),
				fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v - fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_NAN);
			} else if (fpres != fpres) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_NAN) & ~ (STATUS_ZERO);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_NAN | STATUS_ZERO);
			}
		}
		
	}
	
	private class MULFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)),
				fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v * fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_NAN);
			} else if (fpres != fpres) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_NAN) & ~ (STATUS_ZERO);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_NAN | STATUS_ZERO);
			}
		}
		
	}
	
	private class DIVFP extends Cmd_1NCP_1CP_AL {
		
		@Override
		protected void exec(long p1, long p2) throws PrimitiveErrror {
			double fp1v = Double.longBitsToDouble(getNC(p1)),
				fp2 = Double.longBitsToDouble(p2);
			double fpres = fp1v / fp2;
			setNC(p1, Double.doubleToRawLongBits(fpres));
			if (fpres == 0.0D) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_ZERO) & ~ (STATUS_NAN);
			} else if (fpres != fpres) {
				data.regs[STATUS] = (data.regs[STATUS] | STATUS_NAN) & ~ (STATUS_ZERO);
			} else {
				data.regs[STATUS] = (data.regs[STATUS]) & ~ (STATUS_NAN | STATUS_ZERO);
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
			long p1v = getNC(isregp1, p1),
				p2v = getNC(p2);
			long np1 = Long.divideUnsigned(p1v, p2v),
				np2 = Long.remainderUnsigned(p1v, p2v);
			setNC(isregp1, p1, np1);
			setNC(p2, np2);
		}
		
	}
	
	// new ADDC(), new SUBC(), new ADDFP(), new SUBFP(), new MULFP(), new DIVFP(), new NTFP(), new FPTN(), new UDIV(), uc, uc, uc, uc, uc, uc, uc,
	
}
