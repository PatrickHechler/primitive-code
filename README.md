# primitive-code

a register based assembler language

the assembler language for the Primitive-Virtual-Machine

## Start

* A primitive-assembler-code file can be assembled to a primitive-machine-code file.
* A primitive-machine-code file can be executed with a primitive-virtual-machine.
    * the `X00` register will be set to the number of arguments
    * the `X01` register will point to the arguments
    * the arguments will point to STRINGs
        * the first argument will be the program itself, all beyond will be the arguments of the program
        * the entry after the last argument will be `-1`
        * example: (assuming there is a patr-file-sys at `./patr-file-sys.pfs` which contains a executable at `/bin/my_program`)
            * `pvm --pfs=./patr-file-sys.pfs --pmf=/bin/my_program --example value --other=val`
            * `X00          <-- 4`
            * `[X01]        <-- ADDRESS_OF "/bin/program\0"`
                * `[[X01]]       <-- '/'`
                * `[[X01] + 1]   <-- 'b'`
                * `[[X01] + 2]   <-- 'i'`
                * `[[X01] + 3]   <-- 'n'`
                * `[[X01] + 4]   <-- '/'`
                * `[[X01] + 5]   <-- 'p'`
                * `[[X01] + 6]   <-- 'r'`
                * `[[X01] + 7]  <-- 'o'`
                * `[[X01] + 8]  <-- 'g'`
                * `[[X01] + 9]  <-- 'r'`
                * `[[X01] + 10]  <-- 'a'`
                * `[[X01] + 11]  <-- 'm'`
                * `[[X01] + 12]  <-- '\0'`
                * note that `[[X01] + 13]` is already invalid
            * `[X01 + 8]    <-- ADDRESS_OF "--example\0"`
            * `[X01 + 16]   <-- ADDRESS_OF "value\0"`
            * `[X01 + 24]   <-- ADDRESS_OF "--other=val\0"`
            * `[X01 + 32]   <-- -1`
        * the memory blocks for the program arguments and the memory block for the argument array is not resizable and also not freeable
    * the `INTCNT` register will be set to `#INTERRUPT_COUNT`
    * the interrupt-table of `INTP` will be initialized with every entry set to `-1`
        * the default interrupt-table will be an `#INTERRUPT_COUNT * 8` sized memory block
        * so by default the default interrupts will be called, but they can be easily overwritten
    * the `SP` will be set to the start of an memory block
        1. the stack will try to automatically resize itself, when it is too small
        2. this is when the memory blocks last address is a little below an address which is tried be used
            * a address `A` is considered a little below an other address `B` when `B - A` is less then or equal to `8`
        3. when this happens, the `SP` register will be changed to point to the same data in the new memory block
        * note that the stack pointer will NOT be modified, when the memory block is resized manually.
        * when the stack is not able to grow, it will cause the `INT_ERROR_ILLEGAL_MEMORY` to be executed instead
        * this means the `PUSH` command can be used without caring about a stack overflow
            * (the `POP` command can still cause a stack underflow)
        * note that this also means that the `SP` register is the only reliable source of the stack memory block
            * any other address in the stack memory block can get outdated when a `PUSH` (or something simmilar) command is executed
            * example of how it not works:
                1. `PUSH X00 |> push the X00 value to the stack`
                2. `MVAD [SP], SP, 8 |> push the address of the X00 value to the stack`
                3. `ADD SP, 8`
                4. `|> works until here`
                5. `CALL sub`
                6. `|> note that the call also stores the current address in the stack and thus the X00 address stored previously may get corrupt/invalid`
                7. `|> also note that the sub may also use the stack before using the stored address its last time`
        * also note that this means that the `SP` register can not be used to store other information, because it will get corruopted whwn the stack grows
        * this is an example of letting the stack grow, until there is no longer enough memory to let the stack grow, which will cause an INT_ERROR_ILLEGAL_MEMORY
            1. `LOOP:`
            2. `  PUSH X00`
            3. `  JMP LOOP`
        * this example, can be used to ensure that the stack can still grow `X01` entries
            1. `XOR X00, X00 |> set X00 to 0`
            2. `MOV X01, MIN_STACK_GROWABLE |> define MIN_STACK_GROWABLE to a positive number before`
            3. `LOOP:`
            4. `  MOV [SP + X00], [SP + X00] |> only read or write access is needed (here is both used)`
            5. `  ADD X00, 8`
            6. `  DEC X01`
            7. `  JMPZC LOOP`

## Register

the primitive virtual machine has the following 64-bit registers:

* `IP`
    * the instruction pointer points to the command to be executed
    * initialized with the begin of the loaded machine code file
* `SP`
    * the stack pointer points to the command to be executed
    * initialized with the begin of an automatic growing memory block
* `INTP`
    * points to the interrupt-table
    * initialized with the interrupt table
        * this table has by default a memory size of  `#INTERRUPT_COUNT * 8` bytes
        * all entries of the table are initialized with `-1`
* `INTCNT`
    * saves the number of allowed interrupts (`0..(INTCNT-1)` are allowed)
        * all other will call the `INT_ERROR_ILLEGAL_INTERRUPT` interrupt
        * when the value stored in this register is negative or zero no interrupts will be allowed
    * initialized with the interrupt count which can be used as default interrupts (`#INTERRUPT_COUNT`)
* `STATUS`
    * saves some results of operations
    * `STATUS_LOWER`: if on the last `CMP A, B` `A` was lower than `B`
    * `STATUS_GREATER`: if on the last `CMP A, B` `A` was greater than `B`
    * `STATUS_EQUAL`: if on the last `CMP A, B` `A` was greater than `B`
    * `STATUS_OVERFLOW`: if an overflow was detected
    * `STATUS_ZERO`: if the last arithmetic or logical operation leaded to zero (`0`)
    * `STATUS_NAN`: if the last floating point operation leaded to a NaN value
    * `STATUS_ALL_BITS`: if on the last `BCP A, B` was `A & B = B`
    * `STATUS_SOME_BITS`: if on the last `BCP A, B` was `A & B != 0`
    * `STATUS_NONE_BITS`: if on the last `BCP A, B` was `A & B = 0`
    * initialized with `0`
* `ERRNO`
    * number registers, used to indicate what went wrong
    * initialized with `0`
* `X[00..F9]`
    * `250 registers`
    * number registers, for free use
    * `X00` is initialized with a pointer to the program arguments
        * the `X00` register will point to an array pointers
        * these pointers will point to an (by default `UTF-8` encoded) string
        * these strings will be terminated by a zero byte
    * `X01` is initialized with the count of program arguments
    * the other `XNN` registers are initilized with 0

every register can also be addressed:
    * each register has a constant memory address
    * the registers are at the memory addresses `4096..6144` (`HEX-1000..HEX-1800`)
    * the `IP` register has the address `4096` : `HEX-1000`
    * the `SP` register has the address `4104` : `HEX-1008`
    * the `STATUS` register has the address `4112` : `HEX-1010`
    * the `INTCNT` register has the address `4120` : `HEX-1018`
    * the `INTP` register has the address `4128` : `HEX-1020`
    * the `ERRNO` register has the address `4136` : `HEX-1028`
    * the `X00..XF9` registers has the address space `4144..6144` (`HEX-1030..HEX-1800`)
        * each `XNN` register address can be calculated by multiplying the Hex `NN` value with `8` and than adding `4144` (`HEX-1030`)
        * examples:
            * `X00` : `[4144]` : `[HEX-1030]`
            * `X01` : `[4152]` : `[HEX-1038]`
            * `X0F` : `[4264]` : `[HEX-10A8]`
            * `X10` : `[4272]` : `[HEX-10B0]`
            * `X11` : `[4280]` : `[HEX-10B8]`
            * `X78` : `[5104]` : `[HEX-13F0]`
            * `X79` : `[5112]` : `[HEX-13F8]`
            * `X7F` : `[5160]` : `[HEX-1428]`
            * `XF8` : `[6128]` : `[HEX-17F0]`
            * `XF9` : `[6136]` : `[HEX-17F8]`

## NUMBERS

* numbers can be assigned to constants, used as a parameter or inside of a parameter as offset
* to use a decimal number it is possible to write just the number.
    * to use a negative decimal number put a `-` before the plain number
* it is possible to specify the number system, by putting the correct keyword before the number:
    * for binary (base 2): `BIN-`
    * for octal (base 8): `OCT-`
    * for decimal (base 10): `DEC-`
    * for hexadecimal (base 16): `HEX-`
        * to use a unsigned hexadecimal number, put a `U` before the prefix
    * to use negative numbers, put a `N` before the prefix of the number system

## CONSTANTS

* except for the `--POS--` constant all other constants can be overwritten and removed
* to define constants write a `'#'` as prefix
* to load the constant of an other file
    * `~READ_SYM "<FILE>" [...] >`
        * `[...]`: nothing, one or multiple of the following:
            * `#<CONSTANT_PREFIX>`
                * to set before all read constants the given prefix
            * `--MY_CONSTS--`
                * to not use the default constant, but the constants which are now set
                * only possible, when the file is a primitive source code file
            * `#ADD~<NAME> <VALUE>`
                * to add the a constant with the given name and value to the start-constants of the file
                * only possible, when the file is a primitive source code file
            * `--SOURCE--`
                * to set the type of the file to a primitive source code file
            * `--SYMBOL--`
                * to set the type of the file to a primitive symbol file
            * `--SIMPLE-SYMBOL--`
                * to set the type of the file to a simple symbol file
        * if the file type has not been set, the file must end with one of these:
            * `*.psf`: is assumed to be a primitive symbol file
            * `*.psc`: is assumed to be a primitive source code file
            * `*.ssf`: is assumed to be a simple symbol file
            * `[THIS]` is assumed to be a primitive source code file
            * any other name will cause an error
        * if `<FILE>` is `[THIS]` the file, which is now parsed is used.
            * `--SYMBOL--` is not allowd to be mixed with the spcial `[THIS]` path
        * if the file is a simple symbol file:
            * functions:
                * the `FUNC_` prefix will be added before the function name
                * the value of the function constant will be the offset of the function entry point
                    * the value will be relative from the file start
                * args/results:
                    * args will have the `FUNC_<func-name>_ARG_` prefix before the argument name
                    * results will have the `FUNC_<func-name>_RES_` prefix before the result name
                    * the values will be the offset in the function structure
                * to see how to call functions look at the simple-code ducumentation
                    * https://github.com/PatrickHechler/simple-code/blob/main/README.md#function-call
            * variables:
                * the `VAR_` prefix will be added before the variable name
                * the value will be the offset of the variable
                    * the value will be relative from the file start
            * structures:
                * the structure size will be saved in the constant `STRUCT_<struct-name>_SIZE`
                * all members of the structure will get a constant:
                    * the name will be `STRUCT_<struct-name>_OFFSET_<member-name>`
                    * the value will be the offset of the member inside of the structure
            * constants:
                * constants get the prefix `CONST_`
                * the value will be the value of the constant
* to set define an export constant
    * `#EXP~<NAME> <VALUE>`
    * an export constant can be used like a normal constant
    * when an export constant is deleted or overwritten like an normal constant, this will not affect the export
    * to delete an export constant, write `#EXP~<NAME> ~DEL`
        * then it will be deleted as normal and as export constant
    * to change a normal constant to an export constant, just redefine it: `#EXP~<NAME> <NAME>`
* the `--POS--` constant holds the current length of the binary code in bytes (note that this is not aligned) as value
    * note the binary is aligned, directly before a command, so the --POS-- has the unaligned value.

### Predefined Constants
* `INT_ERROR_ILLEGAL_INTERRUPT` : illegal interrupt
    * value: `0`
    * params:
        * `X00` `intnum`: (`num`) the number of the illegal interrupt
    * exits with `(128 + illegal_interrup_number)` (without calling the exit interrupt)
    * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `128`
    * the pvm may print an error message before terminating
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ERROR_UNKNOWN_COMMAND` : unknown command
    * value: `1`
    * exits with `7` (without calling the exit interrupt)
    * the pvm may print an error message before terminating
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ERROR_ILLEGAL_MEMORY` : illegal memory
    * value: `2`
    * exits with `6` (without calling the exit interrupt)
    * the pvm may print an error message before terminating
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ERROR_ARITHMETIC_ERROR` : arithmetic error
    * value: `3`
    * exits with `5` (without calling the exit interrupt)
    * the pvm may print an error message before terminating
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_EXIT` : exit
    * value: `4`
    * params:
        * `X00` `exitnum`: (`ubyte`) the exit number this progress will have
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_MEMORY_ALLOC` : allocate a memory-block
    * value: `5`
    * params:
        * `X00` `len`: (`unum`) the size of the block to be allocated
    * result values:
        * `X00` `mem`: (`ubyte#`) the allocated memory block or `-1` on error
    * on error `ERRNO` will be set to a non-zero value
    * note that `mem` will be 8 byte aligned
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_MEMORY_REALLOC` : reallocate a memory-block
    * value: `6`
    * params:
        * `X00` `oldMem`: (`ubyte#`) points to the old memory-block
        * `X01` `newLen`: (`unum`) the new size of the memory-block
    * result values:
        * `X01` `newMem`: (`ubyte#`) points to the new memory-block or `-1` on error
    * on error `ERRNO` will be set to a non-zero value
    * note that `newMem` will be 8 byte aligned
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_MEMORY_FREE` : free a memory-block
    * value: `7`
    * params:
        * `X00` `mem`: (`ubyte#`) points to the old memory-block
    * after this the memory-block should not be used
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_OPEN` : open new stream
    * value: `8`
    * params:
        * `X00` `fileName`: (`char#`) the STRING, which refers to the file which should be read
        * `X01` `flags`: (`unum`) the open flags (see the `STREAM_*` constants)
    * result values:
        * `X00` `id`: (`num`) the ID of the opened STREAM or `-1` on error
    * opens a new stream to the specified file
    * to close the stream use the stream close interrupt (`INT_STREAM_CLOSE`)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_WRITE` : write
    * value: `9`
    * params:
        * `X00` `id`: (`num`) the STREAM-ID
        * `X01` `len`: (`unum`) the number of bytes to write
        * `X02` `data`: (`ubyte#`) points to the params to write
    * result values:
        * `X01` `wrote`: (`num`) will be set to the number of written bytes
    * if less bytes than len where written, an error occured (for example the disk could be full)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_READ` : read
    * value: `10`
    * params:
        * `X00` `id`: (`num`) the STREAM-ID
        * `X01` `len`: (`unum`) the number of bytes to read
        * `X02` `data`: (`ubyte#`) points to the params to read
    * result values:
        * `X01` `read`: (`unum`) the number of bytes which have been read
    * when less than len bytes where read either an error occured or end of file/pipe has reached
        * end of file/pipe is not considered an error (`ERRNO` will be unmodified)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_CLOSE` : stream close
    * value: `11`
    * params:
        * `X00` `id`: (`num`) the STREAM-ID
    * result values:
        * `X00` `success`: (`num`) will be set to `1` on success and `0` on error
    * note that even on error the STREAM-ID will be released by the system, so do NOT retry
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_FILE_GET_POS` : stream file get position
    * value: `12`
    * params:
        * `X00` `id`: (`num`) the (FILE-)STREAM-ID
    * result values:
        * `X01` `pos`: (`num`) be set to the stream position or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_FILE_SET_POS` : stream file set position
    * value: `13`
    * params:
        * `X00` `id`: (`num`) the (FILE-)STREAM-ID
        * `X01` `pos`: (`unum`) the new position of the stream
    * result values:
        * `X01` `success`: (`num`) `1` on success and `0` on error
    * note that it is possible to set the stream position behind the end of the file.
        * when this is done, the next write (not append) operation will fill the hole with zeros
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_FILE_ADD_POS` : stream file add position
    * value: `14`
    * params:
        * `X00` `id`: (`num`) the (FILE-)STREAM-ID
        * `X01` `add`: (`num`) the value to be added to the stream position
    * result values:
        * `X01` `pos`: (`num`) the new position or `-1` on error
    * if add is lower than the negative value of the old position, this operation will fail (the new position will not be negative)
    * note that it is possible to set the stream position behind the end of the file.
        * when this is done, the next write (not append) operation will fill the hole with zeros
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_FILE_SEEK_EOF` : stream file seek eof
    * value: `15`
    * params:
        * `X00` `id`: (`num`) the STREAM-ID
    * result values:
        * `X01` `pos`: (`num`) the new position of the stream or `-1` on error
    * sets the position of the stream to the end of the file (the file length)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_FILE` : open element handle file
    * value: `16`
    * params:
        * `X00` `file`: (`char#`) points to the STRING which contains the path of the file to be opened
    * result values:
        * `X00` `id`: (`num`) the newly opened (FILE-)ELEMENT-ID or `-1` on error
    * this operation will fail if the element is no file
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_FOLDER` : open element handle folder
    * value: `17`
    * params:
        * `X00` `folder`: (`char#`) points to the STRING which contains the path of the folder to be opened
    * result values:
        * `X00` `id`: (`num`) the newly opened (FOLDER-)ELEMENT-ID or `-1` on error
    * this operation will fail if the element is no folder
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_PIPE` : open element handle pipe
    * value: `18`
    * params:
        * `X00` `pipe`: (`char#`) points to the `STRING` which contains the path of the pipe to be opened
    * result values:
        * `X00` `id`: (`num`) the newly opened (PIPE-)ELEMENT-ID or `-1` on error
    * this operation will fail if the element is no pipe
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STREAM_ELEMENT` : open element handle (any)
    * value: `19`
    * params:
        * `X00` `element`: (`char#`) the STRING which contains the path of the element to be opened
    * result values:
        * `X00` `id`: (`num`) the newly opened ELEMENT-ID or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_OPEN_PARENT` : element open parent handle
    * value: `20`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
    * result values:
        * `X00` `parent_id`: (`num`) the newly opened (FOLDER-)ELEMENT-ID or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_GET_CREATE` : get create date
    * value: `21`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
    * result values:
        * `X01` `date`: (`num`) the create date or `-1` on error
    * note that `-1` may be the create date of the element, so check `ERRNO` instead
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_GET_LAST_MOD` : get last mod date
    * value: `22`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
    * result values:
        * `X01` `date`: (`num`) will be set to the last modified date or `-1` on error
    * note that `-1` may be the last modified date of the element, so check `ERRNO` instead
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_SET_CREATE` : set create date
    * value: `23`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
        * `X01` `date`: (`num`) the new create date of the element
    * result values:
        * `X01` `success`: (`num`) `1` on success or `0` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_SET_LAST_MOD` : set last modified date
    * value: `24`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
        * `X01` `date`: (`num`) the new last modified date of the element
    * result values:
        * `X01`  `success`: (`num`) `1` on success or `0` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_DELETE` : element delete
    * value: `25`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
        * `X01` `force`: (`num`) always try to delete the element
    * result values:
        * `X01` `success`: (`num`) `1` on success or `0` on error
    * this operation always closes the given ELEMENT-ID, even when it fails
    * if `force` has a non-zero value, delete the element, even if it is still loaded somewhere else
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_MOVE` : element move
    * value: `26`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
        * `X01` `newName`: (`char#`) points to a STRING which will be the new name or it is set to `-1` if the name should not be changed
        * `X02` `newParentId`: (`num`) contains the ELEMENT-ID of the new parent of `-1` if the new parent should not be changed
    * result values:
        * `X01` `success`: (`num`) `1` on success or `0` on error
    * when both `newName` and `newParentId` are set to `-1` this operation will do nothing
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_GET_NAME` : element get name
    * value: `27`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
        * `X01` `buffer`: (`char#`) points the the a memory block, which should be used to store the name as a STRING
    * result values:
        * `X01` `name`: (`char#`) points to the name as STRING representation or `-1` on error
    * when `buffer` is set to `-1` a new memory block will be allocated
        * when the memory block is not large enough, it will be resized
        * note that when `X01` does not point to the start of the memory block the resize will fail
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_GET_FLAGS` : element get flags
    * value: `28`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
    * result values:
        * `X01` `flags`: (`num`) will be set to the flags or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_ELEMENT_MODIFY_FLAGS` : element modify flags
    * value: `29`
    * params:
        * `X00` `id`: (`num`) the ELEMENT-ID
        * `X01` `addFlags`: (`udword`) the flags to be added
        * `X02` `remFlags`: (`udword`) the flags to be removed
    * result values:
        * `X01` `success`: (`num`) `1` on success or `0` on error
    * only the low 32 bit will be used for the flags and the high 32 bit will be ignored
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_CHILD_COUNT` : element folder child count
    * value: `30`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
    * result values:
        * `X01` `childCount`: (`num`) the number of child params the folder has or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_CHILD_OF_NAME` : folder get child of name
    * value: `31`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to a STRING with the name of the child
    * result values:
        * `X01` `childId`: (`num`) the newly opened ELEMENT-ID for the child or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME` : folder get child folder of name
    * value: `32`
    * params:
        * `X00` `id`: (`num`) the ELEMENT/FOLDER-ID
        * `X01` `name`: (`char#`) points to a STRING with the name of the child
    * result values:
        * `X01` `childId`: (`num`) the newly opened (FOLDER-)ELEMENT-ID for the child or `-1` on error
    * this operation will fail if the child is no folder
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_CHILD_FILE_OF_NAME` : folder get child file of name
    * value: `33`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to a STRING with the name of the child
    * result values:
        * `X01` `childId`: (`num`) the newly opened (FILE-)ELEMENT-ID for the child or `-1` on error
    * this operation will fail if the child is no file
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME` : folder get child pipe of name
    * value: `34`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to a STRING with the name of the child
    * result values:
        * `X01` `childId`: (`num`) the newly opened (PIPE-)ELEMENT-ID for the child or `-1` on error
    * this operation will fail if the child is no pipe
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_DESCENDAND_OF_PATH` : folder get descendant of path
    * value: `35`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `path`: (`char#`) points to a STRING with the relative path of the descendant
    * result values:
        * `X01` `descId`: (`num`) the newly opened ELEMENT-ID for the descendant or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_DESCENDAND_FOLDER_OF_PATH` : folder get descendant folder of path
    * value: `36`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to a STRING with the relative path of the descendant
    * result values:
        * `X01` `descId`: (`num`) the newly opened (PIPE-)ELEMENT-ID for the descendant or `-1` on error
    * this operation will fail if the descendant is no folder
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_DESCENDAND_FILE_OF_PATH` : folder get descendant file of path
    * value: `37`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to a STRING with the relative path of the descendant
    * result values:
        * `X01` `descId`: (`num`) the newly opened (PIPE-)ELEMENT-ID for the descendant or `-1` on error
    * this operation will fail if the descendant is no file
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_DESCENDAND_PIPE_OF_PATH` : folder get descendant pipe of path
    * value: `38`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to a STRING with the relative path of the descendant
    * result values:
        * `X01` `descId`: (`num`) the newly opened (PIPE-)ELEMENT-ID for the descendant or `-1` on error
    * this operation will fail if the descendant is no pipe
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_CREATE_CHILD_FOLDER` : folder add child folder
    * value: `39`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to a STRING with the name of the child
    * result values:
        * `X01` `childId`: (`num`) the newly created/opened (FOLDER-)ELEMENT-ID for the child or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_CREATE_CHILD_FILE` : folder add child file
    * value: `40`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to the STRING name of the new child element
    * result values:
        * `X01` `childId`: (`num`) will be set to a newly created/opened (FILE-)ELEMENT-ID for the child or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_CREATE_CHILD_PIPE` : folder add child pipe
    * value: `41`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `name`: (`char#`) points to the STRING name of the new child element
    * result values:
        * `X01` `childId`: (`num`) will be set to a newly created/opened (PIPE-)ELEMENT-ID for the child or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FOLDER_OPEN_ITER`: open child iterator of folder
    * value: `42`
    * params:
        * `X00` `id`: (`num`) the (FOLDER-)ELEMENT-ID
        * `X01` `showHidden`: (`num`) is set to `0` if hidden files should be skipped and any other value if not
    * result values:
        * `X01` `iterId`: (`num`) will be set to the FOLDER-ITER-ID or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FILE_LENGTH` : get the length of a file
    * value: `43`
    * params:
        * `X00` `id`: (`num`) the (FILE-)ELEMENT-ID
    * result values:
        * `X01` `len`: (`num`) the file length in bytes or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_FILE_TRUNCATE` : set the length of a file
    * value: `44`
    * params:
        * `X00` `id`: (`num`) the (FILE-)ELEMENT-ID
        * `X01` `len`: (`num`) the new length of the file
    * result values:
        * `X01` `success`: (`num`) will be set `1` on success or `0` on error
    * this will append zeros to the file when the new length is larger than the old length or remove all content after the new length
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_HANDLE_OPEN_STREAM` : opens a stream from a file or pipe handle
    * value: `45`
    * params:
        * `X00` `id`: (`num`) the (FILE-/PIPE-)ELEMENT-ID
        * `X01` `flags`: (`udword`) the open flags (see the `STREAM_*` constants)
    * result values:
        * `X01` `streamId`: (`num`) the STREAM-ID or `-1` on error
    * note that this interrupt works for both files and pipes, but will fail for folders
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_PIPE_LENGTH` : get the length of a pipe
    * value: `46`
    * params:
        * `X00` `id`: (`num`) the (PIPE-)ELEMENT-ID
    * result values:
        * `X01` `len`: (`num`) the pipe length in bytes or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_TIME_GET` : get the current system time
    * value: `47`
    * result values:
        * `X00` `secs`: (`num`) the curent system time in seconds since the epoch
        * `X01` `nanos`: (`num`) the current additional nanoseconds system time
        * `X02` `success`: (`num`) `1` on success and `0` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_TIME_RES` : get the system time resolution
    * value: `48`
        * `X00` `secs`: (`num`) the resolution in seconds
        * `X01` `nanos`: (`num`) the additional nanoseconds resulution
        * `X02` `success`: (`num`) `1` on success and `0` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_TIME_SLEEP` : to sleep the given time in nanoseconds
    * value: `49`
    * params:
        * `X00` `secs`: (`num`) the number of seconds to wait (only values GREATER or equal to `0` are allowed)
        * `X01` `nanos`: (`num`) the number of nanoseconds to wait (only values from `0` to `999999999` are allowed)
    * result values:
        * `X00` `remainSecs`: (`num`) the remaining number of seconds to wait
        * `X01` `remainNanos`: (`num`) the remaining number of nanoseconds to wait
        * `X02` `success`: (`num`) `1` on success and `0` on error
    * note that `X00` will not be negative if the progress waited too long
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_TIME_WAIT` : to wait the given time in nanoseconds
    * value: `50`
    * params:
        * `X00` `secs`: (`num`) the number of seconds since the epoch (only values GREATER or equal to `0` are allowed)
        * `X01` `nanos`: (`num`) the additional number of nanoseconds (only values from `0` to `999999999` are allowed)
    * result values:
        * `X02` `success`: (`num`) `1` on success and `0` on error
    * this interrupt will wait until the current system time is equal or after the given absolute time.
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_RND_OPEN` : open a read stream which delivers random values
    * value: `51`
    * result values:
        * `X00` `id`: (`num`) the (PIPE-)STREAM-ID or `-1` on error
    * the stream will only support read operations
        * not write/append or seek/setpos operations
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_RND_NUM` : sets `X00` to a random number
    * value: `52`
    * result values:
        * `X00` `rnd`: (`num`) 64 random bits
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_MEM_CMP` : memory compare
    * value: `53`
    * params:
        * `X00` `memA`: (`ubyte#`) points to the first memory block
        * `X01` `memB`: (`ubyte#`) points to the second memory block
        * `X02` `len`: (`num`) has the length in bytes of both memory blocks
    * result values:
        * `STATUS` `cmpRes`: (`LOWER`, `GREATER`, `EQUAL`) if `memA` is lower/greater/equal than/to `memB`
    * compares two blocks of memory
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_MEM_CPY` : memory copy
    * value: `54`
    * params:
        * `X00` `dstMem`: (`ubyte#`) points to the target memory block
        * `X01` `srcMem`: (`ubyte#`) points to the source memory block
        * `X02` `len`: (`num`) has the length of bytes to be copied
    * copies a block of memory
    * this function has undefined behavior if the two blocks overlap
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_MEM_MOV` : memory move
    * value: `55`
    * params:
        * `X00` `srcMem`: (`ubyte#`) points to the target memory block
        * `X01` `dstMem`: (`ubyte#`) points to the source memory block
        * `X02` `len`: (`num`) has the length of bytes to be copied
    * copies a block of memory
    * this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_MEM_BSET` : memory byte set
    * value: `56`
    * params:
        * `X00` `mem`: (`ubyte#`) points to the block
        * `X01` `val`: (`ubyte`) the first byte contains the value to be written to each byte
        * `X02` `len`: (`num`) contains the length in bytes
    * sets a memory block to the given byte-value
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_LEN` : string length
    * value: `57`
    * params:
        * `X00` `str`: (`char#`) points to the STRING
    * result values:
        * `X00` `len`: (`num`) the length of the string the/(byte-)offset of the first byte from the `'\0'` character
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_INDEX` : string index of
    * value: `58`
    * params:
        * `X00` `str`: (`char#`) points to the STRING
        * `X01` `chr`: (`char`) the character to search
    * result values:
        * `X00` `index`: (`num`) the index of the character/the (byte-)offset of the first byte equal to the given character
    * if the given character could not be found, `index` will be `-1`
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_CMP` : string compare
    * value: `59`
    * params:
        * `X00` `strA`: (`char#`) points to the first STRING
        * `X01` `strB`: (`char#`) points to the second STRING
    * result values:
        * `STATUS` `cmpRes`: (`LOWER`, `GREATER`, `EQUAL`) if `memA` is lower/greater/equal than/to `memB`
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_FROM_NUM` : number to string
    * value: `60`
    * params:
        * `X00` `val`: (`num`) is set to the number to convert
        * `X01` `buf`: (`char#`) is points to the buffer to be filled with the number in a STRING representation
        * `X02` `base`: (`num`) contains the base of the number system (must be between `2` and `36` (both inclusive))
        * `X03` `bufLen`: (`unum`) is set to the length of the buffer or `0` if the buffer should be allocated
    * result values:
        * `X00` `strLen`: (`num`) will be set to the size of the STRING (without the `\0` terminating character)
        * `X01` `str`: (`char#`) will be set to the new buffer
        * `X03` `newBufLen`: (`num`) will be set to the new size of the buffer or `-1` on error
    * the new length will be the old length or if the old length is smaller than the size of the STRING (with `\0`) than the size of the STRING (with `\0`)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_FROM_FPNUM` : floating point number to string
    * value: `61`
    * params:
        * `X00` `val`: (`fpnum`) is set to the floating point number to convert
        * `X01` `buf`: (`char#`) points to the buffer to be filled with the number in a STRING format
        * `X02` `bufLen`: (`unum`) is set to the current length of the buffer or `0` when the buffer should be allocated
    * result values:
        * `X00` `strLen`: (`num`) will be set to the size of the STRING (without the `\0` terminator)
        * `X01` `str`: (`char#`) will be set to the new buffer or `-1` on error
        * `X02` `newBufLen`: (`num`) will be set to the new size of the buffer
    * the new length will be the old length or if the old length is smaller than the size of the STRING (with `\0`) than the size of the STRING (with `\0`)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_TO_NUM` : string to number
    * value: `62`
    * params:
        * `X00` `str`: (`char#`) points to the STRING
        * `X01` `base`: (`num`) the base of the number system (between `2` and `36` (both inclusive))
    * result values:
        * `X00` `val`: (`num`) will be set to the converted number
        * `X01` `success`: (`num`) `1` on success and `0` on error
    * if the STRING represents a value out of the 64-bit number range `X00` will be min or max value and `ERRNO` will be set to `ERR_OUT_OF_RANGE`
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_TO_FPNUM` : string to floating point number
    * value: `63`
    * params:
        * `X00` `str`: (`char#`) points to the STRING
    * result values:
        * `X00` `val`: (`fpnum`) the converted number
        * `X01` `success`: (`num`) `1` on success and `0` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_TO_U16STR` : STRING to U16-STRING
    * value: `64`
    * params:
        * `X00` `u8str`: (`char#`) points to the STRING (`UTF-8`)
        * `X01` `u16str`: (`uword#`) points to the buffer to be filled with the to `UTF-16` converted string
        * `X02` `bufLen`: (`num`) the length of the buffer (in bytes)
    * result values:
        * `X00` `u8strUnconcStart`: (`char#`) points to the start of the unconverted sequenze (or behind the `\0` terminator)
        * `X01` `u16strUnconvStart`: (`uword#`) points to the start of the unmodified space of the target buffer
        * `X02` `remBufLen`: (`num`) will be set to unmodified space at the end of the buffer
        * `X03` `remU8Len`: (`num`) will be set to the number of converted characters or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_TO_U32STR` : STRING to U32-STRING
    * value: `65`
    * params:
        * `X00` `u8str`: (`char#`) points to the STRING (`UTF-8`)
        * `X01` `u32str`: (`udword#`) points to the buffer to be filled with the to `UTF-32` converted string
        * `X02` `bufLen`: (`num`) the length of the buffer (in bytes)
    * result values:
        * `X00` `u8strUnconcStart`: (`char#`) points to the start of the unconverted sequenze (or behind the `\0` terminator)
        * `X01` `u32strUnconvStart`: (`udword#`) points to the start of the unmodified space of the target buffer
        * `X02` `remBufLen`: (`num`) will be set to unmodified space at the end of the buffer
        * `X03` `remU8Len`: (`num`) will be set to the number of converted characters or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_FROM_U16STR` : U16-STRING to STRING
    * value: `66`
    * params:
        * `X00` `u16str`: (`uword#`) points to the `UTF-16` STRING
        * `X01` `u8str`: (`char#`) points to the buffer to be filled with the converted STRING (`UTF-8`)
        * `X02` `bufLen`: (`num`) the length of the buffer (in bytes)
    * result values:
        * `X00` `u16strUnconcStart`: (`uword#`) points to the start of the unconverted sequenze (or behind the `\0` terminator)
        * `X01` `u8strUnconvStart`: (`char#`) points to the start of the unmodified space of the target buffer
        * `X02` `remBufLen`: (`num`) will be set to unmodified space at the end of the buffer
        * `X03` `remU8Len`: (`num`) will be set to the number of converted characters or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_FROM_U32STR` : U32-STRING to STRING
    * value: `67`
    * params:
        * `X00` `u32str`: (`uword#`) points to the `UTF-32` STRING
        * `X01` `u8str`: (`char#`) points to the buffer to be filled with the converted STRING (`UTF-8`)
        * `X02` `bufLen`: (`num`) the length of the buffer (in bytes)
    * result values:
        * `X00` `u32strUnconcStart`: (`uword#`) points to the start of the unconverted sequenze (or behind the `\0` terminator)
        * `X01` `u8strUnconvStart`: (`char#`) points to the start of the unmodified space of the target buffer
        * `X02` `remBufLen`: (`num`) will be set to unmodified space at the end of the buffer
        * `X03` `remU8Len`: (`num`) will be set to the number of converted characters or `-1` on error
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_STR_FORMAT` : format string
    * value: `68`
    * params:
        * `X00` `frmtStr`: (`char#`) is set to the STRING input
        * `X01` `outStr`: (`char#`) contains the buffer for the STRING output
        * `X02` `bufLen`: (`num`) the length of the buffer
        * `X03` `args`: (`num#`) points to the formatting arguments (note that every argument will consume 64 bits)
    * result values:
        * `X00` `outLen`: (`num`) will be set to the length of the output string (the offset of the `\0` character) or `-1` on error
    * if `outLne` is larger or equal to `bufLen`, only the first `bufLen` bytes will be written to the buffer
    * formatting:
        * `%%`: to escape an `%` character (only one `%` will be in the formatted STRING)
        * `%s`: the next argument points to a STRING, which should be inserted here
        * `%c`: the next argument starts with a byte, which should be inserted here
            * note that UTF-8 characters are not always represented by one byte, but there will always be only one byte used
        * `%n`: consumes two arguments
            1. the next argument contains a number in the range of `2..36`.
                * if the first argument is less than `2` or larger than `36` the interrupt will fail
            2. which should be converted to a STRING using the number system with the basoe of the first argument and than be inserted here
        * `%d`: the next argument contains a number, which should be converted to a STRING using the decimal number system (`10`) and than be inserted here
        * `%f`: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here
        * `%p`: the next argument contains a pointer, which should be converted to a STRING
            * if the pointer is not `-1` the pointer will be converted by placing a `"p-"` and then the unsigned pointer-number converted to a STRING using the hexadecimal number system (`16`)
            * if the pointer is `-1` it will be converted to the STRING `"p-inval"`
        * `%h`: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system (`16`) and than be inserted here
        * `%b`: the next argument contains a number, which should be converted to a STRING using the binary number system (`2`) and than be inserted here
        * `%o`: the next argument contains a number, which should be converted to a STRING using the octal number system (`8`) and than be inserted here
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_LOAD_FILE` : load a file
    * value: `69`
    * params:
        * `X00` `file`: (`char#`) is set to the path (inclusive name) of the file
    * result values:
        * `X00` `data`: (`ubyte#`) points to the memory block, in which the file has been loaded or `-1` on error
        * `X01` `len`: (`num`) the length of the file (and the memory block)
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_LOAD_LIB` : load a library file
    * value: `70`
    * params:
        * `X00` `file`: (`char#`) is set to the path (inclusive name) of the file
        * `X01` `initOff`: (`num`) is the offset (from the file start) of the files init function (or `-1` if there is none)
        * `X02` `initIsPntr`: (`num`) non-`0` if the `initOff` only holds the offset of the offset of the init function and `0` if `initOff` holds the offset of the init function
    * result values:
        * `X00` `data`: (`ubyte#`) points to the memory block, in which the libary has been loaded
        * `X01` `len`: (`num`) the length of the libary (and the memory block)
        * `X02` `loaded`: (`num`) `1` if the libary has been loaded as result of this interrupt and `0` if the file was already loaded
    * similar like the load file interrupt loads a file for the program.
        * the difference is that this interrupt remembers which files has been loaded
        * the other difference is that the file may only be unloaded with the unload lib interrupt (not with the free interrupt)
            * the returned memory block also can not be resized
        * if the interrupt is executed multiple times with the same file, it will return every time the same memory block.
        * this interrupt does not recognize files loaded with the `64` (`INT_LOAD_FILE`) interrupt.
    * file initialisation:
        1. if the file was already loaded `initOff` and `initIsPntr` are ignored (see `loaded`)
        2. if `initOff` is `-1` then `initIsPntr` is ignored
        3. if `initIsPntr` is set to `0`
            * a `CALL` is done with `X00 + initOff` as target
            * `initOff` points to the initialisation function (relative from the file start)
        4. if `initIsPntr` is set to a value other than `0`
            * if `[X00 + initOff]` is `-1` no call is done
            * otherwise a `CALL` is done with `X00 + [X00 + initOff]` as target
            * `initOff` points to the address where the offset of the initialisation function is stored
                * both are relative from the file start
        5. note that the initialisation function may set the result values
            * when this is unacceptable `initOff` should be set to `-1`
            * note that only the `IP` is saved on the stack when calling the initialisation function
    * when an error occurred `X01` will be set to `-1`, `X00` will be unmodified and `ERRNO` will be set to a non-zero value
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_CREATE_LIB` : turn a memory block to a libary
    * value: `71`
    * params:
        * `X00` `file`: (`char#`) is set to the name of the libary
        * `X01` `mem`: (`ubyte#`) the normal memory block which should be converted to a lobary memory block
    * result values:
        * `X00` `data`: (`ubyte#`) points to the memory block, in which the libary has been loaded
        * `X01` `len`: (`num`) the length of the file (and the memory block)
        * `X02` `loaded`: (`num`) `1` if the libary has been loaded/converted as result of this interrupt and `0` if the libary was already loaded
    * if `loaded` is `0` `mem` will not be converted to a libary memory block
    * by using this interrupt the program can convert a normal memory block to a libary memory block
    * when an error occurred `X01` will be set to `-1`, `X00` will be unmodified and `ERRNO` will be set to a non-zero value
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INT_UNLOAD_LIB` : unload a library file
    * value: `72`
    * params:
        * `X00` `data`: (`ubyte#`) points to the start of the memory block loaded with `INT_LOAD_LIB` or converted with `INT_CREATE_LIB`
    * unloads a library previously loaded with the load lib interrupt
    * this interrupt will ensure that the given memory block will be freed and not again be returned from the load lib interrupt
    * this value can be used by the `INT` command to indicate that this interrupt should be called
* `INTERRUPT_COUNT` : the number of interrupts
    * value: `73`
    * the number of interrupts supported by default
    * the `INTCNT` register is initialed with this value
* `FP_NAN` : not a number
    * value: `UHEX-7FFE000000000000`
    * this floating point constant holds a NaN value
* `FP_MAX_VALUE` : floating point maximum finite
    * value: `UHEX-7FEFFFFFFFFFFFFF`
    * the maximum not infinite floating point value
* `FP_MIN_VALUE` : floating point minimum finite
    * value: `UHEX-0000000000000001`
    * the minimum not infinite floating point value
* `FP_POS_INFINITY` : floating point positive infinity
    * value: `UHEX-7FF0000000000000`
    * the floating point constant for positive infinity
* `FP_NEG_INFINITY` : floating point negative infinity
    * value: `UHEX-FFF0000000000000`
    * the floating point constant for negative infinity
* `REGISTER_MEMORY_START` : register memory block address
    * value: `HEX-0000000000001000`
    * the start address of the register memory block
* `REGISTER_MEMORY_ADDR_IP` : address of `IP`
    * value: `HEX-0000000000001008`
    * the start address of the `IP` register
    * this constant has the same value as the `REGISTER_MEMORY_START` constant
* `REGISTER_MEMORY_ADDR_SP` : address of `SP`
    * value: `HEX-0000000000001008`
    * the start address of the `SP` register
* `REGISTER_MEMORY_ADDR_INTP` : address of `INTP`
    * value: `HEX-0000000000001010`
    * the start address of the `INTP` register
* `REGISTER_MEMORY_ADDR_INTCNT` : address of `INTCNT`
    * value: `HEX-0000000000001018`
* `REGISTER_MEMORY_ADDR_STATUS` : address of `STATUS`
    * value: `HEX-0000000000001020`
    * the start address of the `STATUS` register
* `REGISTER_MEMORY_ADDR_ERRNO` : address of `ERRNO`
    * value: `HEX-0000000000001028`
    * the start address of the `ERRNO` register
* `REGISTER_MEMORY_START_XNN` : address of `X00`
    * value: `HEX-0000000000001030`
    * the offset of the `XNN` registers
    * the address of a `XNN` register can be calculated by multiplying the register number and adding this constant
* `REGISTER_MEMORY_LAST_ADDRESS` : address of the last `XNN` register
    * value: `HEX-00000000000017F8`
    * this constant holds the last valid address of the registers
* `REGISTER_MEMORY_END_ADDRESS_SPACE` : the address after the last address
    * value: `HEX-0000000000001800`
    * this constant holds the lowest address, which is above the register memory block
* `MAX_VALUE` : the maximum number value
    * value: `HEX-7FFFFFFFFFFFFFFF`
    * this constant holds the maximum number value
* `MIN_VALUE` : the minimum number value
    * value: `NHEX-8000000000000000`
    * this constant holds the minimum number value
* `STD_IN` : the _ID_ of the _STDIN_ stream
    * value: `0`
    * this constant holds the _Stream-ID_ of the _STDIN_ stream
    * the stream is initially open for reading
    * write and seek operations on the _STDIN_ stream will fail
* `STD_OUT` : the _ID_ of the STDOUT stream
    * value: `1`
    * this constant holds the _Stream-ID_ of the _STDOUT_ stream
    * the stream is initially open for writing
    * read and seek operations on the _STDOUT_ stream will fail
* `STD_LOG` : the _ID_ of the _STDLOG_ stream
    * value: `2`
    * this constant holds the _Stream-ID_ of the _STDLOG_ stream
    * the stream is initially open for writing
    * read and seek operations on the _STDLOG_ stream will fail
* `ERR_NONE` : indicates no error
    * value: `0`
    * this constant has to hold the zero value
    * every non zero value in the `ERRNO` register indicates some error
    * after handling the error the `ERRNO` register should be set to this value
* `ERR_UNKNOWN_ERROR` : indicates an unknown error
    * value: `1`
    * this error value is used when there occurred some unknown error
    * this error value is the least helpful value for error handling
* `ERR_NO_MORE_ELEMENTS` : indicates that there are no more params
    * value: `2`
    * this error value is used when an iterator was used too often
* `ERR_ELEMENT_WRONG_TYPE` : indicates that the element has not the wanted/allowed type
    * value: `3`
    * this error value indicates that some operation was used, which is not supported by the given element
    * for example when an file is asked how many children it has
* `ERR_ELEMENT_NOT_EXIST` : indicates that the element does not exist
    * value: `4`
    * this error value indicates that some element does not exist
* `ERR_ELEMENT_ALREADY_EXIST` : indicates that the element already exists
    * value: `5`
    * this error value indicates that an element should be created but it exists already
* `ERR_OUT_OF_SPACE` : indicates that there is not enough space on the device
    * value: `6`
    * this error value indicates that the file system could not allocate the needed blocks
* `ERR_IO_ERR` : indicates an IO error
    * value: `7`
    * this error value indicates an Input/Output error
* `ERR_ILLEGAL_ARG` : indicates an illegal argument
    * value: `8`
    * this error value indicates that some argument has an illegal value
* `ERR_ILLEGAL_STATE` : indicates that some state is invalid
    * value: `9`
    * this error value indicates that a state is invalid, for example an invalid/unknown magic value
* `ERR_OUT_OF_MEMORY` : indicates that the system is out of memory
    * value: `10`
    * this error value indicates that the system could not allocate the needed memory
* `ERR_ROOT_FOLDER` : indicates that the root folder does not support this operation
    * value: `11`
    * this error value indicates that the root folder restrictions does not allow the tried operation
* `ERR_PARENT_IS_CHILD` : indicates that the parent can't be made to it's own child
    * value: `12`
    * this error value indicates that it was tried to move a folder to one of it's (possibly indirect) children
* `ERR_ELEMENT_USED` : indicates the element is still used somewhere else
    * value: `13`
    * this error value indicates that an element has open multiple handles (more than the used handle)
* `ERR_OUT_OF_RANGE` : indicates that some value was outside of the allowed range
    * value: `14`
    * this error value indicates that some value was outside of the allowed range
* `ERR_FOLDER_NOT_EMPTY` : indicates that the operation failed, because only empty folders can be deleted
    * value: `15`
    * this error value indicates that the operation failed, because only empty folders can be deleted
    * this error will occur, when a non empty folder is tried to be deleted
* `ERR_ELEMENT_DELETED` : indicates that the operation failed, because the element was deleted
    * value: `16`
    * this error value indicates that the operation failed, because the element was deleted
    * this error will occur, when a non empty folder is tried to be deleted
* `UNMODIFIABLE_FLAGS` : element flags that can not be modified
    * value: `UHEX-000000FF`
    * these flags can not be modified after an element was created
    * these flags hold essential information for the file system (for example if an element is a folder)
* `FLAG_FOLDER` : folder flag
    * value: `UHEX-00000001`
    * this flag is used for all folders
* `FLAG_FILE` : file flag
    * value: `UHEX-00000002`
    * this flag is used for all files
* `FLAG_PIPE` : pipe flag
    * value: `UHEX-00000004`
    * this flag is used for all pipes
* `FLAG_EXECUTABLE` : flag for executables
    * value: `UHEX-00000100`
    * this flag is used to indicate, that a file can be executed
* `FLAG_HIDDEN` : flag for hidden params
    * value: `UHEX-01000000`
    * this flag is used to indicate, that an element should be hidden
* `OPEN_ONLY_CREATE` : create the element for the stream
    * value: `UHEX-00000001`
    * used when a stream is opened, when the element should be created during the open operation
    * when used the open operation will fail, if the element already exists
    * when used the `OPEN_FILE` or `OPEN_PIPE` flag has to be set
* `OPEN_ALSO_CREATE` : possibly create the element for the stream
    * value: `UHEX-00000002`
    * used when a stream is opened, when the element should be created during the open operation if it doesn't exists already
    * when used the `OPEN_FILE` or `OPEN_PIPE` flag has to be set
* `OPEN_FILE` : create a file stream
    * value: `UHEX-00000004`
    * used when the stream should be used for a file, will fail if the existing element is a pipe
    * when used the `OPEN_PIPE` flag is not allowed
* `OPEN_PIPE` : create a pipe stream
    * value: `UHEX-00000008`
    * used when the stream should be used for a pipe, will fail if the existing element is a file
    * when used the `OPEN_FILE` flag is not allowed
* `OPEN_READ` : create a readable stream
    * value: `UHEX-00000100`
    * used to open a stream, which support the use of the read operations
* `OPEN_WRITE` : create a writable stream
    * value: `UHEX-00000200`
    * used to open a stream, which support the use of the write operations
* `OPEN_APPEND` : create a writable stream in append mode
    * value: `UHEX-00000400`
    * used to open a stream, which support the use of the write operations
    * the given stream will seek the file/pipe end before every write operation
    * for pipes the `OPEN_WRITE` flag is equally to this flag
* `OPEN_FILE_TRUNC` : truncate the file
    * value: `UHEX-00010000`
    * truncate the files content during the open operation
    * this flag can be used only with file streams
* `OPEN_FILE_EOF` : start at end of file
    * value: `UHEX-00020000`
    * when used the stream will not start at the start of the file, but its end
    * this flag can be used only with file streams
* `STATUS_LOWER`: indicates that the last compare of A and B resulted in `A` is lower than `B`
    * value: `UHEX-0000000000000001`
    * this value is used for the `STATUS` register
* `STATUS_GREATER`: indicates that the last compare of A and B resulted in `A` is greater than `B`
    * value: `UHEX-0000000000000002`
    * this value is used for the `STATUS` register
* `STATUS_EQUAL`: indicates that the last compare of A and B resulted in `A` is equal to `B`
    * value: `UHEX-0000000000000004`
    * this value is used for the `STATUS` register
* `STATUS_OVERFLOW`: indicates that an overflow was detected
    * value: `UHEX-0000000000000008`
    * this value is used for the `STATUS` register
* `STATUS_ZERO`: indicates that the result of the operation was zero
    * value: `UHEX-0000000000000010`
    * this value is used for the `STATUS` register
* `STATUS_NAN`: indicates that the result of the operation was a NAN value
    * value: `UHEX-0000000000000020`
    * this value is used for the `STATUS` register
* `STATUS_ALL_BITS`: indicates that the last bit compare all bits were set
    * value: `UHEX-0000000000000040`
    * this value is used for the `STATUS` register
* `STATUS_SOME_BITS`: indicates that the last bit compare some bits were set
    * value: `UHEX-0000000000000080`
    * this value is used for the `STATUS` register
* `STATUS_NONE_BITS`: indicates that the last bit compare no bits were set
    * value: `UHEX-0000000000000100`
    * this value is used for the `STATUS` register

## STRINGS
* a string is an array of multiple characters of the `UTF-8` encoding
* a string ends with a `'\0'` character

## PRE-COMMANDS

the pre-commands ar executed at assemble time, not runtime

`$align` or `$ALIGN`
* to align code when possible and needed

`$not-align`, `$not_align`, `$NOT-ALIGN` or `$NOT_ALIGN`
* to not align, even if possible

`~IF [CONST_EXPRESSION]`
* to start a conditional block
    * the code in the block will be ignored when the `[CONST_EXPRESSION]` is `zero`

`~ELSE-IF [CONST_EXPRESSION]`
* to end and start a conditional block
    * this can only be used when the code is currently in a `~IF` or `~ELSE-IF` block
    * the code in the block will be ignored if the `[CONST_EXPRESSION]` is `zero` or if any previously block in the currently `~IF [COND] ... (~ELSE-IF [COND] ...) *` row was not ignored

`~ELSE`
* to end and start a conditional block
    * this can only be used when the code is currently in a `~IF` or `~ELSE-IF` block
    * the code in the block will be ignored if any previously block in the currently `~IF [COND] ... (~ELSE-IF [COND] ...) *` row was not ignored

`~ENDIF`
* to end a conditional block
    * this can only be used when the code is currently in a `~IF`, `~ELSE-IF` or `~ELSE` block

`~ERROR ([CONST_EXPRESSION] | '{' ( '"' ([^"\\] | '\\' .)* '"' | ([hH] ':')? [CONST_BERECHNUNG] )* '}' )?`
* to cause an error

`~READ_SYM "<FILE>" [...] >`
* to load the constant of an other file
        * `[...]`: nothing, one or multiple of the following:
            * `#<CONSTANT_PREFIX>`
                * to set before all read constants the given prefix
            * `--MY_CONSTS--`
                * to not use the default constant, but the constants which are now set
                * only possible, when the file is a primitive source code file
            * `#ADD~<NAME> <VALUE>`
                * to add the a constant with the given name and value to the start-constants of the file
                * only possible, when the file is a primitive source code file
            * `--SOURCE--`
                * to set the type of the file to a primitive source code file
            * `--SYMBOL--`
                * to set the type of the file to a primitive symbol file
        * if the file type has not been set, the file must end with one of these:
            * `*.psf`: is assumed to be a primitive symbol file
            * `*.psc`: is assumed to be a primitive source code file
            * `[THIS]` is assumed to be a primitive source code file
            * any other name will cause an error
        * if `<FILE>` is `[THIS]` the file, which is now parsed is used.

`#EXP~<NAME> <VALUE>`
* to set define an export constant
    * an export constant can be used like a normal constant
    * when an export constant is deleted or overwritten like an normal constant, this will not affect the export
    * to delete an export constant, write `#EXP~<NAME> ~DEL`
        * then it will be deleted as normal and as export constant
    * to change a normal constant to an export constant, just redefine it: `#EXP~<NAME> <NAME>`

`: [...] >`
* a constant pool contains a constant sequence of bytes
    * to write an constant, write the constant and than `WRITE`
    * to write an number, just write the number
    * to write single bytes put a `B-` before the number
        * then only values from `0` to `255` (both inclusive) can be written.
        * values outside of this range will cause an error
    * only before and after a constant pool the code may be aligned

## COMMANDS

### 00.. : data

#### 000. : move data

`EXTERN`
* executes an extern function identified by the current address of the instruction pointer (the `IP` register) and then returns
* this command can be used to create libaries which operate outside of the virtual mashine
* note that for this command to work, the PVMs extern functions must be initilized
* note that this command is not supported by the assembler
* definition:
    * `func a = extern_func_from_addr(IP)`
    * `if ther is a extern function at the IP address`
        * `tmp <-- IP`
        * `execute the extern code`
        * `if tmp = IP`
            * `RET`
    * `else`
        * `act as if the comand was not known (see INT_ERROR_UNKNOWN_COMMAND)`
* binary:
    * `00 00 00 00 00 00 00 00`

`MVB <NO_CONST_PARAM> , <BYTE_PARAM>`
* copies the byte value of the second parameter to the first byte parameter
* definition:
    * `p1 <-8-bit- p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `00 01 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.NUM_NUM|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.NUM_NUM|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.NUM_NUM|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MVW <NO_CONST_PARAM> , <PARAM>`
* copies the word value of the second parameter to the first word parameter
* definition:
    * `p1 <-16-bit- p2 `
    * `IP <- IP + CMD_LEN`
* binary:
    * `00 02 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MVDW <NO_CONST_PARAM> , <PARAM>`
* copies the double-word value of the second parameter to the first double-word parameter
* definition:
    * `p1 <-32-bit- p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `00 03 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MOV <NO_CONST_PARAM> , <PARAM>`
* copies the value of the second parameter to the first parameter
* definition:
    * `p1 <- p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `00 04 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`LEA <NO_CONST_PARAM> , <PARAM>`
* sets the first parameter of the value of the second parameter plus the instruction pointer
* definition:
    * `p1 <- p2 + IP`
    * `IP <- IP + CMD_LEN`
* binary:
    * `00 05 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MVAD <NO_CONST_PARAM> , <PARAM> , <CONST_PARAM>`
* copies the value of the second parameter plus the third parameter to the first parameter
* definition:
    * `p1 <- p2 + p3`
    * `IP <- IP + CMD_LEN`
* binary:
    * `00 06 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`
    * `<P3.NUM_NUM>`

`SWAP <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* swaps the value of the first and the second parameter
* definition:
    * `ZW <- p1`
    * `p1 <- p2`
    * `p2 <- ZW`
    * `IP <- IP + CMD_LEN`
* binary:
    * `00 07 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

### 01.. : math

#### 010. : logic

`OR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
* definition:
    * `if (p1 | p2) = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `p1 <- p1 | p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 00 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`AND <NO_CONST_PARAM> , <PARAM>`
* uses the logical AND operator with the first and the second parameter and stores the result in the first parameter
* definition:
    * `if (p1 & p2) = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `p1 <- p1 & p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 01 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`XOR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
* definition:
    * `if (p1 ^ p2) = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `p1 <- p1 ^ p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 02 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`NOT <NO_CONST_PARAM>`
* uses the logical NOT operator with every bit of the parameter and stores the result in the parameter
* this instruction works like `XOR p1, -1`
* definition:
    * `if p1 = -1`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `p1 <- ~ p1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 03 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`LSH <NO_CONST_PARAM>, <PARAM>`
* shifts bits of the parameter logically left
* definition:
    * `if ((p1 << p2) >> p2) = p1`
        * `OVERFLOW <- 0`
    * `else`
        * `OVERFLOW <- 1`
    * `p1 <- p1 << p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 04 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`RASH <NO_CONST_PARAM>, <PARAM>`
* shifts bits of the parameter arithmetic right
* definition:
    * `if ((p1 >> p2) << p2) = p1`
        * `OVERFLOW <- 1`
    * `else`
        * `OVERFLOW <- 0`
    * `p1 <- p1 >> 2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 05 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`RLSH <NO_CONST_PARAM>, <PARAM>`
* shifts bits of the parameter logically right
* definition:
    * `if ((p1 >>> p2) << p2) = p1`
        * `OVERFLOW <- 1`
    * `else`
        * `OVERFLOW <- 0`
    * `p1 <- p1 >>> 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 06 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

#### 011. : simple arithmetic

`ADD <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and stores the sum in the first parameter
* definition:
    * `p1 <- p1 + p2`
    * `if ((p1 < 0) & (p2 > 0) & (p1 - p2 > 0))`
        * `ZERO <-  0`
        * `OVERFLOW <- 1`
    * `else if ((p1 > 0) & (p2 < 0) & (p1 - p2 < 0))`
        * `ZERO <-  0`
        * `OVERFLOW <- 1`
    * `else if p1 != 0`
        * `OVERFLOW <- 0`
        * `ZERO <- 0`
    * `else`
        * `OVERFLOW <- 0`
        * `ZERO <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 10 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SUB <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter from the first parameter and stores the result in the first parameter
* definition:
    * `p1 <- p1 - p2`
    * `if ((p1 < 0) & (p2 < 0) & (p1 + p2 > 0))`
        * `ZERO <-  0`
        * `OVERFLOW <- 1`
    * `else if ((p1 > 0) & (p2 > 0) & (p1 + p2 < 0))`
        * `ZERO <-  0`
        * `OVERFLOW <- 1`
    * `else if p1 != 0`
        * `OVERFLOW <- 0`
        * `ZERO <- 0`
    * `else`
        * `OVERFLOW <- 0`
        * `ZERO <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 11 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MUL <NO_CONST_PARAM> , <PARAM>`
* multiplies the first parameter with the second and stores the result in the first parameter
* definition:
    * `p1 <- p1 * p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 12 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`DIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* divides the first parameter with the second and stores the result in the first parameter and the reminder in the second parameter
* definition:
    * `p1 <- p1 / p2`
    * `p2 <- p1 mod p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 13 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`NEG <NO_CONST_PARAM>`
* uses the arithmetic negation operation with the parameter and stores the result in the parameter
* this instruction works like `MUL p1, -1`
* definition:
    * `if p1 = UHEX-8000000000000000`
        * `OVERFLOW <- 1`
    * `else`
        * `OVERFLOW <- 0`
    * `p1 <- 0 - p1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 14 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`ADDC <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and the OVERFLOW flag and stores the sum in the first parameter
* definition:
    * `ZW <- p1 + (p2 + OVERFLOW)`
    * `if ((p1 > 0) & ((p2 + OVERFLOW) > 0) & ((p1 + p2 + OVERFLOW) < 0)) | ((p1 < 0) & ((p2 + OVERFLOW) < 0) & ((p1 + (p2 + OVERFLOW)) > 0))`
        * `OVERFLOW <- 1`
    * `else`
        * `OVERFLOW <- 0`
    * `p1 <- ZW`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 15 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SUBC <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter with the OVERFLOW flag from the first parameter and stores the result in the first parameter
* definition:
    * `ZW <- p1 - (p2 + OVERFLOW)`
    * `if (p1 > 0) & ((p2 + OVERFLOW) < 0) & ((p1 - (p2 + OVERFLOW)) < 0)) | ((p1 < 0) & (p2 > 0) & ((p1 - (p2 + OVERFLOW)) > 0))`
        * `OVERFLOW <- 1`
    * `else`
        * `OVERFLOW <- 0`
    * `p1 <- ZW`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 16 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`INC <NO_CONST_PARAM>`
* increments the param by one
* definition:
    * `if p1 = MAX_VALUE`
        * `OVERFLOW <- 1`
        * `ZERO <- 0`
    * `else if p1 = -1`
        * `OVERFLOW <- 0`
        * `ZERO <- 1`
    * `else`
        * `OVERFLOW <- 0`
        * `ZERO <- 0`
    * `p1 <- p1 + 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 17 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`DEC <NO_CONST_PARAM>`
* decrements the param by one
* definition:
    * `if p1 = MIN_VALUE`
        * `OVERFLOW <- 1`
        * `ZERO <- 0`
    * `else if p1 = 1`
        * `OVERFLOW <- 0`
        * `ZERO <- 1`
    * `else`
        * `OVERFLOW <- 0`
        * `ZREO <- 0`
    * `p1 <- p1 - 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 18 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

#### 012. : floating-point arithmetic

`ADDFP <NO_CONST_PARAM> , <PARAM>`
* adds the floating point values of both parameters and stores the floating point sum in the first parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 or p2 is signaling-NAN
    * `p1 <- p1 fp-add p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 20 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SUBFP <NO_CONST_PARAM> , <PARAM>`
* subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the first fp-parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 or p2 is signaling-NAN
    * `p1 <- p1 fp-sub p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 21 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MULFP <NO_CONST_PARAM> , <PARAM>`
* multiplies the first fp parameter with the second fp and stores the fp result in the first parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 or p2 is signaling-NAN
    * `p1 <- p1 fp-mul p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 22 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`DIVFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result in the first fp-parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 or p2 is signaling-NAN
    * `p1 <- p1 fp-div p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 23 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`NEGFP <NO_CONST_PARAM>`
* multiplies the fp parameter with -1.0
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is signaling-NAN
    * `p1 <- p1 fp-mul -1.0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 24 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`MODFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result of the remainder in the first fp-parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 or p2 is signaling-NAN
    * `p1 <- p1 fp-mod p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 25 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

#### 013. : floating-point quiet arithmetic

`ADDQFP <NO_CONST_PARAM> , <PARAM>`
* adds the floating point values of both parameters and stores the floating point sum in the first parameter
* definition:
    * `p1 <- p1 fp-quiet-add p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 30 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SUBQFP <NO_CONST_PARAM> , <PARAM>`
* subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the first fp-parameter
* definition:
    * `p1 <- p1 fp-quiet-sub p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 31 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MULQFP <NO_CONST_PARAM> , <PARAM>`
* multiplies the first fp parameter with the second fp and stores the fp result in the first parameter
* definition:
    * `p1 <- p1 fp-quiet-mul p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 32 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`DIVQFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result in the first fp-parameter
* definition:
    * `p1 <- p1 fp-quiet-div p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 33 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`NEGQFP <NO_CONST_PARAM>`
* multiplies the fp parameter with -1.0
* definition:
    * `p1 <- p1 fp-quiet-mul -1.0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 34 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`MODQFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result of the remainder in the first fp-parameter
* definition:
    * `p1 <- p1 fp-quiet-mod p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 35 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

#### 014. : floating-point quiet arithmetic

`ADDSFP <NO_CONST_PARAM> , <PARAM>`
* adds the floating point values of both parameters and stores the floating point sum in the first parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is NAN
    * `p1 <- p1 fp-signal-add p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 40 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SUBSFP <NO_CONST_PARAM> , <PARAM>`
* subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the first fp-parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is NAN
    * `p1 <- p1 fp-signal-sub p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 41 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MULSFP <NO_CONST_PARAM> , <PARAM>`
* multiplies the first fp parameter with the second fp and stores the fp result in the first parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is NAN
    * `p1 <- p1 fp-signal-mul p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 42 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`DIVSFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result in the first fp-parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is NAN
    * `p1 <- p1 fp-signal-div p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 43 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`NEGSFP <NO_CONST_PARAM>`
* multiplies the fp parameter with -1.0
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is NAN
    * `p1 <- p1 fp-signal-mul -1.0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 44 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`MODSFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result of the remainder in the first fp-parameter
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is NAN
    * `p1 <- p1 fp-signal-mod p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 45 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

#### 015. : unsigned arithmetic

`UADD <NO_CONST_PARAM> , <PARAM>`
* like ADD, but uses the parameters as unsigned parameters
* definition:
    * `p1 <- p1 uadd p2`
    * `if ((p2 & (p1 usub p2)) & UHEX-8000000000000000) != 0`
        * `OVERFLOW <- 1`
        * `if p1 != 0`
            * `ZERO <- 0`
        * `else`
            * `ZERO <- 1`
    * `else if p1 != 0`
        * `OVERFLOW <- 0`
        * `ZERO <- 0`
    * `else`
        * `OVERFLOW <- 0`
        * `ZERO <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 50 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`USUB <NO_CONST_PARAM> , <PARAM>`
* like SUB, but uses the parameters as unsigned parameters
* definition:
    * `p1 <- p1 usub p2`
    * `if (p1 uadd p2) < p2`
        * `OVERFLOW <- 1`
        * `if p1 != 0`
            * `ZERO <- 0`
        * `else`
            * `ZERO <- 1`
    * `else if p1 != 0`
        * `OVERFLOW <- 0`
        * `ZERO <- 0`
    * `else`
        * `OVERFLOW <- 0`
        * `ZERO <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 51 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`UMUL <NO_CONST_PARAM> , <PARAM>`
* like MUL, but uses the parameters as unsigned parameters
* definition:
    * `p1 <- p1 umul p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 52 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`UDIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* like DIV, but uses the parameters as unsigned parameters
* definition:
    * `p1 <- oldp1 udiv oldp2`
    * `p2 <- oldp1 umod oldp2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 53 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

#### 016. : big arithmetic

`BADD <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* like ADD, but uses the parameters as 128 bit value parameters
    * if registers are used the next register is also used
    * the last register will cause the illegal memory interrupt
* definition:
    * `p1 <- p1 big-add p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 60 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`BSUB <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* like SUB, but uses the parameters as 128 bit value parameters
    * if registers are used the next register is also used
    * the last register will cause the illegal memory interrupt
* definition:
    * `p1 <- p1 big-sub p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 61 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`BMUL <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* like MUL, but uses the parameters as 128 bit value parameters
    * if registers are used the next register is also used
    * the last register will cause the illegal memory interrupt
* definition:
    * `p1 <- p1 big-mul p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 62 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`BDIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* like DIV, but uses the parameters as 128 bit value parameters
    * if registers are used the next register is also used
    * the last register will cause the illegal memory interrupt
* definition:
    * `p1 <- oldp1 big-div oldp2`
    * `p2 <- oldp1 big-mod oldp2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 63 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`BNEG <NO_CONST_PARAM>`
* like NEG, but uses the parameters as 128 bit value parameters
    * if registers are used the next register is also used
    * the last register will cause the illegal memory interrupt
* definition:
    * `p1 <- big-neg p1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 64 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

#### 017. : convert number types

`FPTN <NO_CONST_PARAM>`
* converts the value of the floating point param to a number
* note that there may be some information lost when converting a floating point number to a number
* definition:
    * note that the aritmetic error interrupt is executed instead if p1 is no normal value
    * `p1 <- as_num(p1)`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 70 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`NTFP <NO_CONST_PARAM>`
* converts the value of the number param to a floating point
* note that there may be some information lost when converting a number to a floating point number
* definition:
    * `p1 <- as_fp(p1)`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 71 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

### 02.. : program control

#### 020. : compare/check

`CMP <PARAM> , <PARAM>`
* compares the two values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else`
        * `GREATER <- 0`
        * `LOWER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 00 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`BCP <PARAM> , <PARAM>`
* compares the two values on logical/bit level
* note that `ALL_BITS` will not be set when `p1` is zero
* definition:
    * `if (p1 & p2) = 0`
        * `ALL_BITS <- 0`
        * `SOME_BITS <- 0`
        * `NONE_BITS <- 1`
    * `else if (p1 & p2) = p1`
        * `ALL_BITS <- 1`
        * `SOME_BITS <- 1`
        * `NONE_BITS <- 0`
    * `else`
        * `ALL_BITS <- 0`
        * `SOME_BITS <- 1`
        * `NONE_BITS <- 0`
* binary:
    * `02 01 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`CMPFP <PARAM> , <PARAM>`
* compares the two floating point values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is signal-NaN | p2 is signal-NaN`
        * execute the aritmetic error interrupt
    * `else if p1 is NaN | p2 is NaN`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NaN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 02 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`CMPSFP <PARAM> , <PARAM>`
* compares the two floating point values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else if p1 is NaN | p2 is NaN`
        * execute the aritmetic error interrupt
    * `else`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 03 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`CMPQFP <PARAM> , <PARAM>`
* compares the two floating point values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is NaN | p2 is NaN`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NaN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 04 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`CHKFP <PARAM>`
* checks if the floating point param is a positive, negative infinity, NaN or normal value
* definition:
    * `if p1 is positive-infinity`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `NAN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is negative-infinity`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `NAN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is signal-NaN`
        * execute the aritmetic error interrupt
    * `else if p1 is NaN`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NAN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NAN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 05 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`CHKQFP <PARAM>`
* checks if the floating point param is a positive, negative infinity, NaN or normal value
* definition:
    * `if p1 is positive-infinity`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `NAN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is negative-infinity`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `NAN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is NaN`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NAN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NAN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 06 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`CHKSFP <PARAM>`
* checks if the floating point param is a positive, negative infinity, NaN or normal value
* definition:
    * `if p1 is positive-infinity`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 is negative-infinity`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else if p1 is NaN`
        * execute the aritmetic error interrupt
    * `else`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 07 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`CMPU <PARAM> , <PARAM>`
* compares the two unsigned values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else`
        * `GREATER <- 0`
        * `LOWER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 08 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`CMPB <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* compares the two 128 bit values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else`
        * `GREATER <- 0`
        * `LOWER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 09 <B-P1.TYPE> <B-P2.TYPE> <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SGN <PARAM>`
* compares the value with `0` and stores the result in the status register
* this command is like `CMP PARAM , 0`
* definition:
    * `if p1 > 0`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 < 0`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else`
        * `GREATER <- 0`
        * `LOWER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 0A <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`SGNFP <PARAM>`
* compares the floating-point value with `0.0` and stores the result in the status register
* this command is like `CMPFP PARAM , 0.0`
* definition:
    * `if p1 > 0.0`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 < 0.0`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is signal-NaN`
        * execute the aritmetic error interrupt
    * `else if p1 is NaN`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NaN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `GREATER <- 0`
        * `LOWER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 0B <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`SGNSFP <PARAM>`
* compares the floating-point value with `0.0` and stores the result in the status register
* this command is like `CMPFP PARAM , 0.0`
* definition:
    * `if p1 > 0.0`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 < 0.0`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else if p1 is NaN`
        * execute the aritmetic error interrupt
    * `else`
        * `GREATER <- 0`
        * `LOWER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 0C <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`SGNQFP <PARAM>`
* compares the floating-point value with `0.0` and stores the result in the status register
* this command is like `CMPFP PARAM , 0.0`
* definition:
    * `if p1 > 0.0`
        * `GREATER <- 1`
        * `LOWER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 < 0.0`
        * `GREATER <- 0`
        * `LOWER <- 1`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is NaN`
        * `LOWER <- 0`
        * `GREATER <- 0`
        * `NaN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `GREATER <- 0`
        * `LOWER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 0D <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

#### 021. : conditional jump

`JMPERR <LABEL>`
* sets the instruction pointer to position of the command after the label if the `ERRNO` register stores a value other than `0`
* definition:
    * `if ERRNO != 0`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 10 <RELATIVE_LABEL (48-bit)>`

`JMPEQ <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare operation compared two equal values
* definition:
    * `if EQUAL`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 11 <RELATIVE_LABEL (48-bit)>`

`JMPNE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare operation compared two different values
* definition:
    * `if EQUAL`
        * `IP <- IP + CMD_LEN`
    * `else`
        * `IP <- IP + RELATIVE_LABEL`
* binary:
    * `02 12 <RELATIVE_LABEL (48-bit)>`

`JMPGT <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was greater
* definition:
    * `if GREATER`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 13 <RELATIVE_LABEL (48-bit)>`

`JMPGE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not lower
* definition:
    * `if GREATER | EQUAL`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 14 <RELATIVE_LABEL (48-bit)>`

`JMPLT <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was lower
* definition:
    * `if LOWER`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 15 <RELATIVE_LABEL (48-bit)>`

`JMPLE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
* definition:
    * `if LOWER | EQUAL`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 16 <RELATIVE_LABEL (48-bit)>`

`JMPCS <LABEL>`
* sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is set
* definition:
    * `if OVERFLOW`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 17 <RELATIVE_LABEL (48-bit)>`

`JMPCC <LABEL>`
* sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is cleared
* definition:
    * `if ! OVERFLOW`
        * `IP <- IP + CMD_LEN`
    * `else`
        * `IP <- IP + RELATIVE_LABEL`
* binary:
    * `02 18 <RELATIVE_LABEL (48-bit)>`

`JMPZS <LABEL>`
* sets the instruction pointer to position of the command after the label if the last zero flag is set
* definition:
    * `if ZERO`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 19 <RELATIVE_LABEL (48-bit)>`

`JMPZC <LABEL>`
* sets the instruction pointer to position of the command after the label if the last zero flag is cleared
* definition:
    * `if ! ZERO`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 1A <RELATIVE_LABEL (48-bit)>`

`JMPNAN <LABEL>`
* sets the instruction pointer to position of the command after the label if the last NaN flag is set
* definition:
    * `if NAN`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 1B <RELATIVE_LABEL (48-bit)>`

`JMPAN <LABEL>`
* sets the instruction pointer to position of the command after the label if the last NaN flag is cleared
* definition:
    * `if ! NAN`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 1C <RELATIVE_LABEL (48-bit)>`


`JMPAB <LABEL>`
* sets the instruction pointer to position of the command after the label if the `ALL_BITS` flag is set
* definition:
    * `if ALL_BITS`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 1D <RELATIVE_LABEL (48-bit)>`

`JMPSB <LABEL>`
* sets the instruction pointer to position of the command after the label if the `SOME_BITS` flag is set
* definition:
    * `if SOME_BITS`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 1E <RELATIVE_LABEL (48-bit)>`

`JMPNB <LABEL>`
* sets the instruction pointer to position of the command after the label if the `NONE_BITS` flag is set
* definition:
    * `if NONE_BITS`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
* binary:
    * `02 1F <RELATIVE_LABEL (48-bit)>`

#### 022. : unconditional jump

`JMP <LABEL>`
* sets the instruction pointer to position of the command after the label
* definition:
    * `IP <- IP + RELATIVE_LABEL`
* binary:
    * `02 20 <RELATIVE_LABEL (48-bit)>`

`JMPO <PARAM>, <CONST_PARAM>`
* sets the instruction pointer to position of the summ of the parameters
* definition:
    * `IP <- p1 + p2`
* binary:
    * `02 21 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `<P2.NUM_NUM>`

`JMPNO <PARAM>`
* sets the instruction pointer to position of the parameter
* definition:
    * `IP <- p1`
* binary:
    * `02 22 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

#### 023. : interrupt

`INT <PARAM>`
* calls the interrupt specified by the parameter
* an interrupt can be overwritten:
    * the interrupt-table is saved in the `INTP` register
    * to overwrite the interrupt `N`, write to `(INTP + (N * 8))` the absolute position of the address
        * `|> example to overwrite a interrupt`
        * `LEA [INTP + OVERWRITE_INT_NUM_MULTIPLIED_WITH_8], RELATIVE_POS_FROM_GET_TO_INTERRUPT`
    * on failure the default interrupts use the `ERRNO` register to store information about the error which caused the interrupt to fail
* negative interrupts will always cause the illegal interrupt to be called instead
* when `INTCNT` is greater then the number of default interrupts and the called interrupt is not overwritten, the illegal interrupt will be called instead
* for the list of default interrupts see the [predefined constant](#predefined-constants) documentation
    * note that all predefined interrupts set the `ERRNO` register to a nonzero value on error
* definition:
    * `IP         <- IP + CMD_LEN`
    * note that default interrupts get called with a different routine
    * `ZW         <- MEM-ALLOC{size=128}`
        * if the memory allocation fails, the program will terminate with 127
        * the allocated memory block will not be resizable, but can be freed normally with the free interrupt or with the `IRET` command
    * `[ZW]       <- IP`
    * `[ZW + 8]   <- SP`
    * `[ZW + 16]  <- STATUS`
    * `[ZW + 24]  <- INTCNT`
    * `[ZW + 32]  <- INTP`
    * `[ZW + 40]  <- ERRNO`
    * `[ZW + 48]  <- X00`
    * `[ZW + 56]  <- X01`
    * `[ZW + 64]  <- X02`
    * `[ZW + 72]  <- X03`
    * `[ZW + 80]  <- X04`
    * `[ZW + 88]  <- X05`
    * `[ZW + 96]  <- X06`
    * `[ZW + 104] <- X07`
    * `[ZW + 112] <- X08`
    * `[ZW + 120] <- X09`
    * `X09        <- ZW`
    * `IP         <- [INTP + (p1 * 8)]`
        * if the address `INTP + (p1 * 8)` is invalid the pvm will execute the illegal memory interrupt
            * the pvm will terminate with 127 instead if the address `INTP + (INT_ERROR_ILLEGAL_MEMORY * 8)` is also invalid
        * note that if the address `[INTP + (p1 * 8)]` the illegal memory interrupt will be executed.
            * note that if is the illegal memory interrupt entry is invalid (and not `-1`) a loop will occur
                * note that in this loop the program would allocate memory, until there is no longer enough memory
* binary:
    * `02 30 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`IRET`
* returns from an interrupt
* if the address stored in `X09` was not retrieved from an `INT` execution, the PVM will call the illegal memory interrupt
* definition:
    * `ZW      <- X09`
    * `IP      <- [X09]`
    * `SP      <- [X09 + 8]`
    * `STATUS  <- [X09 + 16]`
    * `INTCNT  <- [X09 + 24]`
    * `INTP    <- [X09 + 32]`
    * `ERRNO   <- [X09 + 40]`
    * `X00     <- [X09 + 48]`
    * `X01     <- [X09 + 56]`
    * `X02     <- [X09 + 64]`
    * `X03     <- [X09 + 72]`
    * `X04     <- [X09 + 80]`
    * `X05     <- [X09 + 88]`
    * `X06     <- [X09 + 98]`
    * `X07     <- [X09 + 104]`
    * `X08     <- [X09 + 112]`
    * `X09     <- [X09 + 120]`
    * `FREE ZW`
        * this does not use the free interrupt, but works like the default free interrupt (without calling the interrupt (what could cause an infinite recursion))
* binary:
    * `02 31 00 00 00 00 00 00

### 03.. : stack
#### 030. : call

`CALL <LABEL>`
* sets the instruction pointer to position of the label
* and pushes the current instruction pointer to the stack
* definition:
    * `[SP] <- IP`
    * `SP <- SP + 8`
    * `IP <- IP + RELATIVE_LABEL`
* binary:
    * `03 00 <RELATIVE_LABEL (48-bit)>`

`CALO <PARAM>, <CONST_PARAM>`
* sets the instruction pointer to position of the summ of the parameters
* and pushes the current instruction pointer to the stack
* definition:
    * `[SP] <- IP`
    * `SP <- SP + 8`
    * `IP <- p1 + p2`
        * note that this call is not relative from the current position
* binary:
    * `03 01 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `<P2.NUM_NUM>`

`CALNO <PARAM>`
* sets the instruction pointer to position of the parameter
* and pushes the current instruction pointer to the stack
* definition:
    * `[SP] <- IP`
    * `SP <- SP + 8`
    * `IP <- p1`
        * note that this call is not relative from the current position
* binary:
    * `03 02 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

#### 031. : return

`RET`
* sets the instruction pointer to the position which was secured in the stack
* definition:
    * `IP <- [SP + -8]`
    * `SP <- SP - 8`
* binary:
    * `03 10 00 00 00 00 00 00`

#### 032. : push/pop

`PUSH <PARAM>`
* pushes the parameter to the stack
* definition:
    * `[SP] <- p1`
    * `SP <- SP + 8`
    * `IP <- IP + CMD_LEN`
* binary:
    * `03 20 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`POP <NO_CONST_PARAM>`
* pops the highest value from the stack to the parameter
* definition:
    * `p1 <- [SP - 8]`
    * `SP <- SP - 8`
    * `IP <- IP + CMD_LEN`
* binary:
    * `03 21 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`PUSHBLK <PARAM> , <PARAM>`
* pushes the memory block, which is refered by p1 and p2 large to the stack
* definition:
    * `note that p1 is not allowed to be negative`
    * `MEMORY_COPY{TARGET=SP,SOURCE=p1,BYTE_COUNT=p2}`
    * `SP <- SP + p1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `03 22 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`POPBLK <PARAM> , <PARAM>`
* pops the memory block, which will be saved to p1 and is p2 large from the stack
* definition:
    * `note that p2 is not allowed to be negative`
    * `MEMORY_COPY{TARGET=p1,SOURCE=SP-p2,BYTE_COUNT=p2}`
    * `SP <- SP - p1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `03 23 <B-P1.TYPE> 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

## not (yet) there/supported
* CAS: compare and swap (when equal)
* support for enviroment-variables
* Multi-threading
    * maby thread-groups/processes
    * maby allow overwrite of default interrupts for child threads/processes
    * syncronizing/locks
    * execute other programs
* sockets
