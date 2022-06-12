package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.*;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.PVM;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.functional.Interrupt;
import de.hechler.patrick.pfs.exception.ElementLockedException;
import de.hechler.patrick.pfs.exception.ElementReadOnlyException;
import de.hechler.patrick.pfs.exception.OutOfSpaceException;
import de.hechler.patrick.pfs.interfaces.PatrFile;
import de.hechler.patrick.pfs.interfaces.PatrFileSysElement;
import de.hechler.patrick.pfs.interfaces.PatrFolder;
import de.hechler.patrick.pfs.objects.fs.PatrFileImpl;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysElementImpl;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysImpl;
import de.hechler.patrick.pfs.objects.fs.PatrID;
import de.hechler.patrick.pfs.utils.PatrFileSysConstants;

public class PVMImpl implements PVM {
	
	private static final long     NO_LOCK     = PatrFileSysConstants.NO_LOCK;
	private final MemoryContainer mem         = new MemoryContainer();
	private final PVMData         data        = new PVMData(mem);
	private final Interrupt[]     defaultInts = new Interrupt[] {
		num -> System.exit((int) (128 + data.regs[X_ADD])),
		num -> System.exit(7),
		num -> System.exit(6),
		num -> System.exit(5),
		num -> System.exit((int) data.regs[X_ADD]),
		num -> data.regs[X_ADD] = mem.malloc(data.regs[X_ADD]),
		num -> data.regs[X_ADD + 1] = mem.realloc(data.regs[X_ADD], data.regs[X_ADD + 1]),
		num -> mem.free(data.regs[X_ADD]),
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
	};
	private final PatrFileSysImpl fs;
	
	private long    cmd;
	private boolean off1;
	private int     off2;
	private int     off3;
	private int     len;
	private boolean isreg;
	
	
	public PVMImpl(PatrFileSysImpl fs) {
		if (this.defaultInts.length != INTERRUPT_COUNT) {
			throw new AssertionError("expected int-count=" + INTERRUPT_COUNT + " int-count=" + this.defaultInts.length);
		}
		this.fs = fs;
	}
	
	@Override
	public void run() {
		while (true) {
			execute();
		}
	}
	
	protected void execute() {
		len = 8;
		off1 = true;
		off2 = 0;
		off3 = -8;
		try {
			cmd = mem.get(data.regs[IP]);
			switch ((int) ( (cmd >> 56) & 0xFF)) {
			case ADD:
				add();
				break;
			case ADDC:
				addc();
				break;
			case ADDFP:
				addfp();
				break;
			case AND:
				and();
				break;
			case BCP:
				bcp();
				break;
			case CALL:
				call();
				break;
			case CALO:
				calo();
				break;
			case CHKFP:
				chkfp();
				break;
			case CMP:
				cmp();
				break;
			case CMPFP:
				cmpfp();
				break;
			case DEC:
				dec();
				break;
			case DIV:
				div();
				break;
			case DIVFP:
				divfp();
				break;
			case FPTN:
				fptn();
				break;
			case INC:
				inc();
				break;
			case INT:
				INT();
				break;
			case IRET:
				break;
			case JMP:
				break;
			case JMPAN:
				break;
			case JMPCC:
				break;
			case JMPCS:
				break;
			case JMPEQ:
				break;
			case JMPGE:
				break;
			case JMPGT:
				break;
			case JMPLE:
				break;
			case JMPLT:
				break;
			case JMPNAN:
				break;
			case JMPNE:
				break;
			case JMPZC:
				break;
			case JMPZS:
				break;
			case LEA:
				break;
			case LSH:
				break;
			case MOV:
				break;
			case MUL:
				break;
			case MULFP:
				break;
			case MVAD:
				break;
			case NEG:
				break;
			case NOT:
				break;
			case NTFP:
				break;
			case OR:
				break;
			case POP:
				break;
			case PUSH:
				break;
			case RASH:
				break;
			case RET:
				break;
			case RLSH:
				break;
			case SUB:
				break;
			case SUBC:
				break;
			case SUBFP:
				break;
			case SWAP:
				break;
			case UDIV:
				break;
			case UMUL:
				break;
			case XOR:
				break;
			default:
				throw new PrimitiveErrror(INT_ERRORS_UNKNOWN_COMMAND);
			}
			data.regs[IP] += len;
		} catch (PrimitiveErrror e) {
			interrupt(e.intNum);
		}
	}
	
	private void INT() throws PrimitiveErrror {
		long p1 = getConstParam();
		interrupt(p1);
	}
	
	private void inc() throws PrimitiveErrror {
		long p1 = getConstParam();
		long p1v = getNoConstVal(p1);
		long res = p1v + 1L;
		if (res == Long.MIN_VALUE) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_ZERO)) | STATUS_CARRY;
		} else if (res == 0L) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_CARRY)) | STATUS_ZERO;
		} else {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_ZERO | STATUS_CARRY));
		}
		setNoConstVal(p1, res);
	}
	
	private void fptn() throws PrimitiveErrror {
		long p1 = getNoConstParam();
		double p1v = Double.longBitsToDouble(getNoConstVal(p1));
		long res;
		try {
			res = (long) p1v;
		} catch (ArithmeticException e) {
			throw new PrimitiveErrror(INT_ERRORS_ARITHMETIC_ERROR);
		}
		setNoConstVal(p1, res);
	}
	
	private void divfp() throws PrimitiveErrror {
		long p1 = getNoConstParam();
		boolean isregp1 = isreg;
		long p2 = getConstParam();
		double p1v = Double.longBitsToDouble(getNoConstVal(isregp1, p1));
		double p2v = Double.longBitsToDouble(getNoConstVal(p2));
		double res1 = (p1v) / (p2v);
		double res2 = (p1v) % (p2v);
		setNoConstVal(isregp1, p1, Double.doubleToRawLongBits(res1));
		setNoConstVal(p2, Double.doubleToRawLongBits(res2));
	}
	
	private void div() throws PrimitiveErrror {
		long p1 = getNoConstParam();
		boolean isregp1 = isreg;
		long p2 = getConstParam();
		long p1v = getNoConstVal(isregp1, p1);
		long p2v = getNoConstVal(p2);
		long res1;
		long res2;
		try {
			res1 = (p1v) / (p2v);
			res2 = (p1v) % (p2v);
		} catch (ArithmeticException e) {
			throw new PrimitiveErrror(INT_ERRORS_ARITHMETIC_ERROR);
		}
		setNoConstVal(isregp1, p1, res1);
		setNoConstVal(p2, res2);
	}
	
	private void dec() throws PrimitiveErrror {
		long p1 = getConstParam();
		long p1v = getNoConstVal(p1);
		long res = p1v - 1L;
		if (res == Long.MAX_VALUE) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_ZERO)) | STATUS_CARRY;
		} else if (res == 0L) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_CARRY)) | STATUS_ZERO;
		} else {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_ZERO | STATUS_CARRY));
		}
		setNoConstVal(p1, res);
	}
	
	private void cmpfp() throws PrimitiveErrror {
		double p1 = Double.longBitsToDouble(getConstParam());
		double p2 = Double.longBitsToDouble(getConstParam());
		if (p1 > p2) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_EQUAL | STATUS_NAN)) | STATUS_GREATHER;
		} else if (p1 < p2) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_GREATHER | STATUS_EQUAL | STATUS_NAN)) | STATUS_LOWER;
		} else if (Double.isNaN(p1) || Double.isNaN(p2)) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_GREATHER | STATUS_EQUAL)) | STATUS_NAN;
		} else {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_GREATHER | STATUS_NAN)) | STATUS_EQUAL;
		}
	}
	
	private void cmp() throws PrimitiveErrror {
		long p1 = getConstParam();
		long p2 = getConstParam();
		if (p1 > p2) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_EQUAL)) | STATUS_GREATHER;
		} else if (p1 < p2) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_GREATHER | STATUS_EQUAL)) | STATUS_LOWER;
		} else {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_GREATHER)) | STATUS_EQUAL;
		}
	}
	
	private void chkfp() throws PrimitiveErrror {
		double p1 = Double.longBitsToDouble(getConstParam());
		if (p1 == Double.POSITIVE_INFINITY) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_NAN | STATUS_ZERO)) | STATUS_GREATHER;
		} else if (p1 == Double.NEGATIVE_INFINITY) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_GREATHER | STATUS_NAN | STATUS_ZERO)) | STATUS_LOWER;
		} else if (Double.isNaN(p1)) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_GREATHER | STATUS_ZERO)) | STATUS_NAN;
		} else {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_LOWER | STATUS_NAN | STATUS_GREATHER)) | STATUS_ZERO;
		}
	}
	
	private void calo() throws PrimitiveErrror {
		long p1 = getConstParam();
		long p2 = mem.get(data.regs[IP] + len);
		mem.set(data.regs[SP], data.regs[IP]);
		data.regs[SP] += 8;
		data.regs[IP] = p1 + p2;
	}
	
	private void call() throws PrimitiveErrror {
		mem.set(data.regs[SP], data.regs[IP]);
		data.regs[SP] += 8;
		data.regs[IP] += mem.get(data.regs[IP] + 8);
	}
	
	private void bcp() throws PrimitiveErrror {
		long p1 = getConstParam();
		long p2 = getConstParam();
		long and = (p1) & (p2);
		if (and == 0L) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_ALL_BITS | STATUS_SOME_BITS)) | STATUS_NONE_BITS;
		} else if (and == p2) {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_NONE_BITS)) | STATUS_ALL_BITS | STATUS_SOME_BITS;
		} else {
			data.regs[STATUS] = (data.regs[STATUS] & ~ (STATUS_NONE_BITS | STATUS_ALL_BITS)) | STATUS_SOME_BITS;
		}
	}
	
	private void and() throws PrimitiveErrror {
		long p1 = getNoConstParam();
		long p2 = getConstParam();
		long p1v = getNoConstVal(p1);
		long res = (p1v) & (p2);
		setNoConstVal(p1, res);
	}
	
	private void addfp() throws PrimitiveErrror {
		long p1 = getNoConstParam();
		long p2 = getConstParam();
		long p1v = getNoConstVal(p1);
		long res = Double.doubleToRawLongBits(Double.longBitsToDouble(p1v) + Double.longBitsToDouble(p2));
		if (p1v > 0L && p2 > 0L) {
			if (res < 0L) {
				data.regs[STATUS] |= STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~STATUS_CARRY;
			}
		} else if (p1v < 0L && p2 < 0L) {
			if (res > 0L) {
				data.regs[STATUS] |= STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~STATUS_CARRY;
			}
		} else {
			data.regs[STATUS] &= ~STATUS_CARRY;
		}
		setNoConstVal(p1, res);
	}
	
	private void addc() throws PrimitiveErrror {
		long p1 = getNoConstParam();
		long p2 = getConstParam();
		long p1v = getNoConstVal(p1);
		long res;
		if ( (data.regs[STATUS] & STATUS_CARRY) != 0) {
			res = p1v + p2 + 1;
		} else {
			res = p1v + p2;
		}
		if (p1v > 0L && p2 > 0L) {
			if (res < 0L) {
				data.regs[STATUS] |= STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~STATUS_CARRY;
			}
		} else if (p1v < 0L && p2 < 0L) {
			if (res > 0L) {
				data.regs[STATUS] |= STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~STATUS_CARRY;
			}
		} else {
			data.regs[STATUS] &= ~STATUS_CARRY;
		}
		setNoConstVal(p1, res);
	}
	
	private void add() throws PrimitiveErrror {
		long p1 = getNoConstParam();
		long p2 = getConstParam();
		long p1v = getNoConstVal(p1);
		long res = p1v + p2;
		if (p1v > 0L && p2 > 0L) {
			if (res < 0L) {
				data.regs[STATUS] |= STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~STATUS_CARRY;
			}
		} else if (p1v < 0L && p2 < 0L) {
			if (res > 0L) {
				data.regs[STATUS] |= STATUS_CARRY;
			} else {
				data.regs[STATUS] &= ~STATUS_CARRY;
			}
		} else {
			data.regs[STATUS] &= ~STATUS_CARRY;
		}
		setNoConstVal(p1, res);
	}
	
	private long getNoConstVal(long p) throws PrimitiveErrror {
		if (isreg) {
			assert p == (int) p;
			return data.regs[(int) p];
		} else {
			return mem.get(p);
		}
	}
	
	private void setNoConstVal(long p, long val) throws PrimitiveErrror {
		if (isreg) {
			assert p == (int) p;
			data.regs[(int) p] = val;
		} else {
			mem.set(p, val);
		}
	}
	
	private long getNoConstVal(boolean isreg, long p) throws PrimitiveErrror {
		if (isreg) {
			assert p == (int) p;
			return data.regs[(int) p];
		} else {
			return mem.get(p);
		}
	}
	
	private void setNoConstVal(boolean isreg, long p, long val) throws PrimitiveErrror {
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
				interrupt(e.intNum);
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
		return new String(bytes, 0, len, StandardCharsets.UTF_16);
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
	
}
