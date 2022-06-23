package de.hechler.patrick.codesprachen.primitive.runtime.utils;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;

public class PVMDebugConstants {
	
	/**
	 * the first magic send after a connect to validate a debug connection
	 * <p>
	 * a debug connection will be able to use debug commands
	 * <p>
	 * the PVM will response with {@link #DEBUG_CONNECT_MAGIC}
	 * 
	 * @see #WAIT_MAGIC
	 * @see #STEP_MAGIC
	 * @see #STEP_DEEP_MAGIC
	 * @see #RUN_MAGIC
	 * @see #SET_SN_MAGIC
	 * @see #SET_MEM_MAGIC
	 * @see #GET_SN_MAGIC
	 * @see #GET_MEM_MAGIC
	 * @see #GET_POS_BREAKS_MAGIC
	 * @see #GET_DEF_INT_BREAKS_MAGIC
	 * @see #GET_ALL_INT_BREAKS_MAGIC
	 * @see #GET_POS_BREAK_COUNT_MAGIC
	 * @see #GET_DEF_INT_BREAK_COUNT_MAGIC
	 * @see #GET_ALL_INT_BREAK_COUNT_MAGIC
	 * @see #HAS_POS_BREAK_MAGIC
	 * @see #HAS_DEF_INT_BREAK_MAGIC
	 * @see #HAS_ALL_INT_BREAK_MAGIC
	 * @see #ADD_POS_BREAK_MAGIC
	 * @see #ADD_DEF_INT_BREAK_MAGIC
	 * @see #ADD_ALL_INT_BREAK_MAGIC
	 * @see #REM_POS_BREAK_MAGIC
	 * @see #REM_DEF_INT_BREAK_MAGIC
	 * @see #REM_ALL_INT_BREAK_MAGIC
	 */
	public static final long DEBUG_CONNECT_MAGIC           = 0x74C3E97E29EF221BL;
	/**
	 * the first magic send after a connect to validate a std-in connection
	 * <p>
	 * on a std-in connect the debug server will just delegate everything received from the connection to the std-in of the program
	 * <p>
	 * the PVM will response with {@link #STD_IN_CONNECT_MAGIC}
	 */
	public static final long STD_IN_CONNECT_MAGIC          = 0x874614E21370DE22L;
	/**
	 * the first magic send after a connect to validate a std-out connection
	 * <p>
	 * on a std-out connect the debug server will just delegate everything the program writes to it's std-out to the connection
	 * <p>
	 * the PVM will response with {@link #STD_IN_CONNECT_MAGIC}
	 */
	public static final long STD_OUT_CONNECT_MAGIC         = 0x047D7209B1B860D2L;
	/**
	 * the first magic send after a connect to validate a std-err connection
	 * <p>
	 * on a std-err connect the debug server will just delegate everything the program writes to it's std-err to the connection
	 * <p>
	 * the PVM will response with {@link #STD_ERR_CONNECT_MAGIC}
	 */
	public static final long STD_ERR_CONNECT_MAGIC         = 0xB0734984A3DDB2DEL;
	/**
	 * send to disconnect from a debug connection
	 */
	public static final long DISCONNECT_MAGIC              = 0x2CD6F47424686DC4L;
	/**
	 * send when a illegal command was sent.<br>
	 * after this was sent the debug server will close the connection
	 */
	public static final long ILLEGAL_MAGIC                 = 0x297D94DAFD1F0151L;
	/**
	 * send to let the PVM wait<br>
	 * the PVM will respond with {@link #WAIT_MAGIC} after the state has changed
	 */
	public static final long WAIT_MAGIC                    = 0xF85EA6DADBA11403L;
	/**
	 * send to let the PVM run<br>
	 * the PVM will respond with {@link #RUN_MAGIC} after the state has changed
	 */
	public static final long RUN_MAGIC                     = 0x496C197D183A7A51L;
	/**
	 * send to let the PVM execute a single command<br>
	 * the PVM will respond with {@link #STEP_MAGIC} after the state has changed
	 */
	public static final long STEP_MAGIC                    = 0x52F8BFB6DFFA098CL;
	/**
	 * send to let the PVM execute a step over/out or similar<br>
	 * after this magic the next 64-bit number will define how large should be steeped out.<br>
	 * for example {@code 0} for a simple step over and {@code 1} for a simple step out.<br>
	 * the PVM will respond with {@link #STEP_DEEP_MAGIC} after the state has changed
	 */
	public static final long STEP_DEEP_MAGIC               = 0x427895C7EB315117L;
	/**
	 * send to retrieve a snapshot of the PVM (all numbers/registers are 64-bit numbers/registers)<br>
	 * if an error occurred (like invalid memory address) the PVM will respond with {@link #INVALID_VALUE_MAGIC}<br>
	 * the PVM debug server will respond with {@link #GET_SN_MAGIC} followed by the snapshot:<br>
	 * <ol>
	 * <li>IP: the instruction pointer
	 * <li>SP: the stack pointer
	 * <li>STATUS: the status register
	 * <li>INTCNT: the interrupt count register
	 * <li>INTP: the interrupt table pointer register
	 * <li>FS_LOCK: the file-system lock register
	 * <li>X00..XF9: XNN registers are for free use
	 * </ol>
	 * (the PVM has a total of 256 registers)
	 */
	public static final long GET_SN_MAGIC                  = 0xBC3EA1F2D2A6A37EL;
	/**
	 * send a snapshot to the PVM: (all numbers are 64-bit numbers)
	 * <ol>
	 * <li>IP: the instruction pointer
	 * <li>SP: the stack pointer
	 * <li>STATUS: the status register
	 * <li>INTCNT: the interrupt count register
	 * <li>INTP: the interrupt table pointer register
	 * <li>FS_LOCK: the file-system lock register
	 * <li>X00..XF9: XNN registers are for free use
	 * </ol>
	 * (the PVM has a total of 256 registers)<br>
	 * the PVM debug server will respond with after it has replaced its data with the data of the snapshot {@link #SET_SN_MAGIC}
	 * <p>
	 * this should only be done together with {@link #GET_SN_MAGIC} and only when the PVM is known to be waiting.
	 * <p>
	 * if the PVM does not wait the response will be {@link #NOT_WAITING_MAGIC}.
	 */
	public static final long SET_SN_MAGIC                  = 0xBC3EA1F2D2A6A37EL;
	/**
	 * after this MAGIC the debug server will await a 64-bit number as address.<br>
	 * after this the debug server will await a 64-bit number as byte length.
	 * <p>
	 * the PVM debug server will respond with {@link #GET_MEM_MAGIC} followed by the data of the memory block<br>
	 * if the memory block is invalid, the PVM will respond with {@link #INVALID_VALUE_MAGIC} instead
	 */
	public static final long GET_MEM_MAGIC                 = 0xE46B46791FDF128BL;
	/**
	 * after this MAGIC the debug server will await a 64-bit number as address.<br>
	 * after this the debug server will await a 64-bit number as byte length.<br>
	 * after the length the debug server will await the new data of the memory block.
	 * <p>
	 * the PVM will respond with {@value #SET_MEM_MAGIC}.<br>
	 * if the PVM does not wait the response will be {@link #NOT_WAITING_MAGIC}.
	 */
	public static final long SET_MEM_MAGIC                 = 0xA74D2E3F40C3AF27L;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the pos-breakpoint)
	 * <p>
	 * adds a position breakpoint<br>
	 * the PVM will stop executing if the instruction pointer is equal to the given value.
	 * <p>
	 * all values except {@code -1} are valid position breakpoints (even if they are not currently allocated)
	 * <p>
	 * the PVM debug server will response with {@link #ADD_POS_INT_BREAK_MAGIC} if the breakpoint was added successfully<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint was already added before this<br>
	 * and with {@link #INVALID_VALUE} when the breakpoint was {@code -1}
	 */
	public static final long ADD_POS_BREAK_MAGIC           = 0x00B6495DA050CB01L;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the def-int-breakpoint)
	 * <p>
	 * adds a default-interrupt breakpoint<br>
	 * the PVM will stop executing if the default interrupt with its interrupt-number equal to the given value should be executed.
	 * <p>
	 * all values from zero to {@link PrimAsmConstants#INTERRUPT_COUNT} are valid
	 * <p>
	 * the PVM debug server will response with {@link #ADD_DEF_INT_BREAK_MAGIC} if the breakpoint was added successfully<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint was already added before this<br>
	 * and with {@link #INVALID_VALUE_MAGIC} when the breakpoint was out of range (negative or equal/greater than {@link PrimAsmConstants#INTERRUPT_COUNT})
	 */
	public static final long ADD_DEF_INT_BREAK_MAGIC       = 0x304046C21E0C0019L;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the all-int-breakpoint)
	 * <p>
	 * adds a interrupt breakpoint<br>
	 * the PVM will stop executing if the interrupt with its interrupt-number equal to the given value should be executed.
	 * <p>
	 * all positive values are valid
	 * <p>
	 * the PVM debug server will response with {@link #ADD_ALL_INT_BREAK_MAGIC} if the breakpoint was added successfully<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint was already added before this<br>
	 * and with {@link #INVALID_VALUE_MAGIC} when the breakpoint was negative
	 */
	public static final long ADD_ALL_INT_BREAK_MAGIC       = 0xAAD64455056C25FEL;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the pos-breakpoint)
	 * <p>
	 * checks if a position breakpoint exist<br>
	 * <p>
	 * if the breakpoint is registered the PVM debug server will response with {@link #HAS_POS_BREAK_MAGIC}<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint is not registered
	 * 
	 * @see #ADD_POS_BREAK_MAGIC
	 */
	public static final long HAS_POS_BREAK_MAGIC           = 0x00B6495DA050CB01L;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the def-int-breakpoint)
	 * <p>
	 * checks if a default-interrupt breakpoint exist<br>
	 * <p>
	 * if the breakpoint is registered the PVM debug server will response with {@link #HAS_DEF_INT_BREAK_MAGIC}<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint is not registered
	 * 
	 * @see #ADD_DEF_INT_BREAK_MAGIC
	 */
	public static final long HAS_DEF_INT_BREAK_MAGIC       = 0x304046C21E0C0019L;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the all-int-breakpoint)
	 * <p>
	 * checks if a interrupt breakpoint exist<br>
	 * <p>
	 * if the breakpoint is registered the PVM debug server will response with {@link #HAS_ALL_INT_BREAK_MAGIC}<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint is not registered
	 * 
	 * @see #ADD_ALL_INT_BREAK_MAGIC
	 */
	public static final long HAS_ALL_INT_BREAK_MAGIC       = 0xAAD64455056C25FEL;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the pos-breakpoint)
	 * <p>
	 * removes a position breakpoint<br>
	 * the PVM will no longer stop executing if the instruction pointer is equal to the given value.
	 * <p>
	 * the PVM debug server will response with {@link #REM_POS_BREAK_MAGIC} if the breakpoint was removed successfully<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint was not a registered breakpoint
	 */
	public static final long REM_POS_BREAK_MAGIC           = 0x1F7C0D180C960509L;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the def-int-breakpoint)
	 * <p>
	 * removes a default-interrupt breakpoint<br>
	 * the PVM will no longer stop executing if the given default-interrupt with the same intNum should be executed.
	 * <p>
	 * the PVM debug server will response with {@link #REM_DEF_INT_BREAK_MAGIC} if the breakpoint was removed successfully<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint was not a registered breakpoint
	 */
	public static final long REM_DEF_INT_BREAK_MAGIC       = 0x96EFCCE92275AEBCL;
	/**
	 * after this the PVM debug server awaits a 64-bit address (the int-breakpoint)
	 * <p>
	 * removes a interrupt breakpoint<br>
	 * the PVM will no longer stop executing if the given interrupt with the same intNum should be executed.
	 * <p>
	 * the PVM debug server will response with {@link #REM_ALL_INT_BREAK_MAGIC} if the breakpoint was removed successfully<br>
	 * and with {@link #NOTHING_DONE_MAGIC} when the breakpoint was not a registered breakpoint
	 */
	public static final long REM_ALL_INT_BREAK_MAGIC       = 0x12799FF18475B273L;
	/**
	 * <ol>
	 * <li>at first the PVM will send {@link #GET_POS_BREAKS_MAGIC}
	 * <li>then the PVM will send the all-int-breakpoints
	 * <ul>
	 * <li>each all-int-breakpoint will be a 64-bit number
	 * </ul>
	 * <li>the PVM will send the 64-bit number {@code -1} ({@code 0xFFFFFFFFFFFF}) at the end
	 * </ol>
	 */
	public static final long GET_POS_BREAKS_MAGIC          = 0x18E175B9C37A0C1EL;
	/**
	 * <ol>
	 * <li>at first the PVM will send {@link #GET_DEF_INT_BREAKS_MAGIC}
	 * <li>then the PVM will send the all-int-breakpoints
	 * <ul>
	 * <li>each all-int-breakpoint will be a 64-bit number
	 * </ul>
	 * <li>the PVM will send the 64-bit number {@code -1} ({@code 0xFFFFFFFFFFFF}) at the end
	 * </ol>
	 */
	public static final long GET_DEF_INT_BREAKS_MAGIC      = 0x1E1C2ED2010E3C4DL;
	/**
	 * <ol>
	 * <li>at first the PVM will send {@link #GET_ALL_INT_BREAKS_MAGIC}
	 * <li>then the PVM will send the all-int-breakpoints
	 * <ul>
	 * <li>each all-int-breakpoint will be a 64-bit number
	 * </ul>
	 * <li>the PVM will send the 64-bit number {@code -1} ({@code 0xFFFFFFFFFFFF}) at the end
	 * </ol>
	 */
	public static final long GET_ALL_INT_BREAKS_MAGIC      = 0x7E0DE24ADA2D99BEL;
	/**
	 * the PVM debug server will response with {@link #GET_POS_BREAK_COUNT_MAGIC} followed by the pos-breakpoint count as a 64-bit number
	 */
	public static final long GET_POS_BREAK_COUNT_MAGIC     = 0xD60F579ABFD96880L;
	/**
	 * the PVM debug server will response with {@link #GET_DEF_INT_BREAK_COUNT_MAGIC} followed by the def-int-breakpoint count as a 64-bit number
	 */
	public static final long GET_DEF_INT_BREAK_COUNT_MAGIC = 0xB0319BC4B2C0D776L;
	/**
	 * the PVM debug server will response with {@link #GET_ALL_INT_BREAK_COUNT_MAGIC} followed by the all-int-breakpoint count as a 64-bit number
	 */
	public static final long GET_ALL_INT_BREAK_COUNT_MAGIC = 0x64D19AF18B8DF50CL;
	/**
	 * the PVM will send this to tell that nothing was done
	 */
	public static final long NOTHING_DONE_MAGIC            = 0x703A46B8009F14DDL;
	/**
	 * the PVM will send this when a value was invalid and thus the command could not be executed
	 */
	public static final long INVALID_VALUE_MAGIC           = 0xA2552867027D6C34L;
	/**
	 * send from the PVM debug server when a command was sent, but the PVM did not wait and the command only works when the PVM is waiting
	 */
	public static final long NOT_WAITING_MAGIC             = 0xE5B391B664172051L;
	/**
	 * send to receive the state of the PVM<br>
	 * the PVM will response with one of the following MAGICs:
	 * <ul>
	 * <li>{@link #RUNNING_STATE_MAGIC}: the PVM is currently running
	 * <li>{@link #STEPPING_STATE_MAGIC}: the PVM is currently stepping
	 * <li>{@link #WAITING_STATE_MAGIC}: the PVM is currently waiting
	 * </ul>
	 */
	public static final long GET_STATE_MAGIC               = 0x64B9CA2CEF9722F2L;
	/**
	 * this magic indicates that the PVM is currently running
	 */
	public static final long RUNNING_STATE_MAGIC           = 0x67F1568CC4A30FE6L;
	/**
	 * this magic indicates that the PVM is currently stepping
	 */
	public static final long STEPPING_STATE_MAGIC          = 0xFE6E616525372CDAL;
	/**
	 * this magic indicates that the PVM is currently waiting
	 */
	public static final long WAITING_STATE_MAGIC           = 0xD4DB9FD72BE72780L;
	/**
	 * validates a memory range.<br>
	 * <ol>
	 * <li>after this the PVM will await a 64-bit address number
	 * <li>then the PVM will await a 64-bit length number
	 * </ol>
	 * the length must be positive, not negative and not zero!<br>
	 * else the PVM will answer with {@link #ILLEGAL_MAGIC} and close the connection
	 * <p>
	 * if the range is valid, that means the PVM can access every byte inside the given range, the PVM will response with {@link #MEM_CHECK_MAGIC}.<br>
	 * if the PVM can not access at least one byte inside of the given range the PVM will response with {@link #NOTHING_DONE_MAGIC}
	 */
	public static final long MEM_CHECK_MAGIC               = 0xF9563C6B217C468FL;
	/**
	 * frees a block of allocated memory
	 * <p>
	 * after the magic the PVM awaits the start address of the memory block
	 * <p>
	 * if the block was freed successfully, the PVM will response with {@link #FREE_MEMORY_MAGIC}<br>
	 * Otherwise the PVM will response with {@link #NOTHING_DONE_MAGIC} (when the given address is not a start address of a allocated memory block)
	 */
	public static final long FREE_MEMORY_MAGIC             = 0x8EEDD552CE1695D8L;
	/**
	 * allocates a block of memory
	 * <p>
	 * after the magic the PVM awaits the length of the memory block
	 * <p>
	 * if the block was allocated successfully, the PVM will response with {@link #MALLOC_MEMORY_MAGIC} and then the start address of the memory block<br>
	 * if the PVM could not allocate the memory block because it would be too large the PVM will response with {@link #NOTHING_DONE_MAGIC} and than {@code -1}<br>
	 * Otherwise the PVM will response with {@link #INVALID_VALUE_MAGIC} and than {@code -1}<br>
	 * if the length is negative or zero the allocation will always fail with {@link #INVALID_VALUE_MAGIC}
	 */
	public static final long MALLOC_MEMORY_MAGIC           = 0x3C7FAF93F04D69E8L;
	/**
	 * reallocates a block of memory
	 * <p>
	 * <ol>
	 * <li>at first the PVM awaits the old start address of the memory block
	 * <li>then the PVM awaits the new length of the memory block
	 * </ol>
	 * if the block was reallocated successfully, the PVM will response with {@link #REALLOC_MEMORY_MAGIC} and then the new start address of the memory block<br>
	 * if the PVM could not resize the memory block because it would be too large the PVM will response with {@link #NOTHING_DONE_MAGIC} and than {@code -1}<br>
	 * Otherwise the PVM will response with {@link #INVALID_VALUE_MAGIC} and than {@code -1}<br>
	 * if the length is negative or zero the reallocation will always fail with {@link #INVALID_VALUE_MAGIC}
	 */
	public static final long REALLOC_MEMORY_MAGIC          = 0xFAC34E2D7E5DAEF1L;
	
}
