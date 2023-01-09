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
            * `[X01]        <-- ADDRESS_OF "/bin/my_program\0"`
                * `[[X01]]       <-- '/'`
                * `[[X01] + 1]   <-- 'b'`
                * `[[X01] + 2]   <-- 'i'`
                * `[[X01] + 3]   <-- 'n'`
                * `[[X01] + 4]   <-- '/'`
                * `[[X01] + 5]   <-- 'm'`
                * `[[X01] + 6]   <-- 'y'`
                * `[[X01] + 7]   <-- '_'`
                * `[[X01] + 8]   <-- 'p'`
                * `[[X01] + 9]   <-- 'r'`
                * `[[X01] + 10]  <-- 'o'`
                * `[[X01] + 11]  <-- 'g'`
                * `[[X01] + 12]  <-- 'r'`
                * `[[X01] + 13]  <-- 'a'`
                * `[[X01] + 14]  <-- 'm'`
                * `[[X01] + 15] <-- '\0'`
            * `[X01 + 8]    <-- ADDRESS_OF "--example\0"`
            * `[X01 + 16]   <-- ADDRESS_OF "value\0"`
            * `[X01 + 24]   <-- ADDRESS_OF "--other=val\0"`
            * `[X01 + 32]   <-- -1`
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
        * when the stack is not able to grow, it will cause the `INT_ERRORS_ILLEGAL_MEMORY` to be executed instead
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
        * this is an example of letting the stack grow, until there is no longer enugh memory to let the stack grow, which will cause an INT_ERRORS_ILLEGAL_MEMORY
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

* the primitive virtual machine has the following 64-bit registers:
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
            * all other will call the `INT-ERRORS_ILLEGAL_INTERRUPT` interrupt
            * when the value stored in this register is negative or zero no interrupts will be allowed
        * initialized with the interrupt count which can be used as default interrupts (`#INTERRUPT_COUNT`)
    * `STATUS`
        * saves some results of operations
        * `UHEX-0000000000000001` : `LOWER`: if on the last `CMP A, B` `A` was lower than `B`
        * `UHEX-0000000000000002` : `GREATHER`: if on the last `CMP A, B` `A` was greater than `B`
        * `UHEX-0000000000000004` : `EQUAL`: if on the last `CMP A, B` `A` was greater than `B`
        * `UHEX-0000000000000008` : `OVERFLOW`: if an overflow was detected
        * `UHEX-0000000000000010` : `ZERO`: if the last arithmetic or logical operation leaded to zero (`0`)
        * `UHEX-0000000000000020` : `NAN`: if the last floating point operation leaded to a NaN value
        * `UHEX-0000000000000040` : `ALL_BITS`: if on the last `BCP A, B` was `A & B = B`
        * `UHEX-0000000000000080` : `SOME_BITS`: if on the last `BCP A, B` was `A & B != 0`
        * `UHEX-0000000000000100` : `NONE_BITS`: if on the last `BCP A, B` was `A & B = 0`
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
    * `ERRNO`
        * number registers, used to indicate what went wrong
        * the `ERRNO` register is initilized with 0
        * the `ERRNO` register has always the same value as the last `XNN` register
            * currently the last `XNN` register is `XFA`
            * `ERRNO` is just an other name for the last `XNN` register
* every register can also be addressed:
    * each register has a constant memory address
    * the registers are at the memory addresses `4096..6144` (`HEX-1000..HEX-1800`)
    * the `IP` register has the address `4096` : `HEX-1000`
    * the `SP` register has the address `4104` : `HEX-1008`
    * the `STATUS` register has the address `4112` : `HEX-1010`
    * the `INTCNT` register has the address `4120` : `HEX-1018`
    * the `INTP` register has the address `4128` : `HEX-1020`
    * the `FS_LOCK` register has the address `4136` : `HEX-1028`
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
            * `XF9` : `[6136]` : `[HEX-17F8] : ERRNO`
    * the `ERRNO` registers has the address space `6136` (`HEX-17F8`)

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
        * if the file type has not been set, the file must end with one of these:
            * `*.psf`: is assumed to be a primitive symbol file
            * `*.psc`: is assumed to be a primitive source code file
            * `[THIS]` is assumed to be a primitive source code file
            * any other name will cause an error
        * if `<FILE>` is `[THIS]` the file, which is now parsed is used.
* to set define an export constant
    * `#EXP~<NAME> <VALUE>`
    * an export constant can be used like a normal constant
    * when an export constant is deleted or overwritten like an normal constant, this will not affect the export
    * to delete an export constant, write `#EXP~<NAME> ~DEL`
        * then it will be deleted as normal and as export constant
    * to change a normal constant to an export constant, just redefine it: `#EXP~<NAME> <NAME>`
* predefined constants:
<pre><code>
    --POS--                               the current length of the binary code in bytes
    INT_ERRORS_ILLEGAL_INTERRUPT          0
    INT_ERRORS_UNKNOWN_COMMAND            1
    INT_ERRORS_ILLEGAL_MEMORY             2
    INT_ERRORS_ARITHMETIC_ERROR           3
    INT_EXIT                              4
    INT_MEMORY_ALLOC                      5
    INT_MEMORY_REALLOC                    6
    INT_MEMORY_FREE                       7
    INT_OPEN_STREAM                       8
    INT_STREAMS_WRITE                     9
    INT_STREAMS_READ                      10
    INT_STREAMS_CLOSE                     11
    INT_STREAMS_FILE_GET_POS              12
    INT_STREAMS_FILE_SET_POS              13
    INT_STREAMS_FILE_ADD_POS              14
    INT_STREAMS_FILE_SEEK_EOF             15
    INT_OPEN_FILE                         16
    INT_OPEN_FOLDER                       17
    INT_OPEN_PIPE                         18
    INT_OPEN_ELEMENT                      19
    INT_ELEMENT_OPEN_PARENT               20
    INT_ELEMENT_GET_CREATE                21
    INT_ELEMENT_GET_LAST_MOD              22
    INT_ELEMENT_SET_CREATE                23
    INT_ELEMENT_SET_LAST_MOD              24
    INT_ELEMENT_DELETE                    25
    INT_ELEMENT_MOVE                      26
    INT_ELEMENT_GET_NAME                  27
    INT_ELEMENT_GET_FLAGS                 28
    INT_ELEMENT_MODIFY_FLAGS              29
    INT_FOLDER_CHILD_COUNT                30
    INT_FOLDER_OPEN_CHILD_OF_NAME         31
    INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME  32
    INT_FOLDER_OPEN_CHILD_FILE_OF_NAME    33
    INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME    34
    INT_FOLDER_CREATE_CHILD_FOLDER        35
    INT_FOLDER_CREATE_CHILD_FILE          36
    INT_FOLDER_CREATE_CHILD_PIPE          37
    INT_FS_FILE_LENGTH                    38
    INT_FS_FOLDER_CHILD_COUNT             39
    INT_FS_FOLDER_GET_CHILD_OF_INDEX      40
    INT_FS_FOLDER_GET_CHILD_OF_NAME       41
    INT_FS_FOLDER_ADD_FOLDER              42
    INT_FS_FOLDER_ADD_FILE                43
    INT_FS_FOLDER_ADD_LINK                44
    INT_FS_FILE_LENGTH                    45
    INT_FS_FILE_HASH                      46
    INT_FS_FILE_READ                      47
    INT_FS_FILE_WRITE                     48
    INT_FS_FILE_APPEND                    49
    INT_FS_FILE_REM_CONTENT               50
    INT_FS_FILE_TRUNCATE                  51
    INT_FS_LINK_GET_TARGET                52
    INT_FS_LINK_SET_TARGET                53
    INT_FS_FILE_CREATE                    54
    INT_FS_FOLDER_CREATE                  55
    INT_FS_LINK_CREATE                    56
    INT_FS_LOCK                           57
    INT_FS_UNLOCK                         58
    INT_FS_BLOCK                          59
    INT_FS_UNBLOCK                        60
    INT_TIME_GET                          61
    INT_TIME_WAIT                         62
    INT_RANDOM                            63
    INT_MEMORY_COPY                       64
    INT_MEMORY_MOVE                       65
    INT_MEMORY_BSET                       66
    INT_MEMORY_SET                        67
    INT_STRING_LENGTH                     68
    INT_STRING_COMPARE                    69
    INT_NUMBER_TO_STRING                  70
    INT_FPNUMBER_TO_STRING                71
    INT_STRING_TO_NUMBER                  72
    INT_STRING_TO_FPNUMBER                73
    INT_STRING_FORMAT                     74
    INT_LOAD_FILE                         75
    INTERRUPT_COUNT                       76
    MAX_VALUE                         HEX-7FFFFFFFFFFFFFFF
    MIN_VALUE                        NHEX-8000000000000000
    STD_IN                                0
    STD_OUT                               1
    STD_LOG                               2
    FS_STREAM_OFFSET_FILE                 0
    FS_STREAM_OFFSET_POS                  8
    FS_ELEMENT_OFFSET_ID                  0
    FS_ELEMENT_OFFSET_LOCK                8
    LOCK_NO_READ_ALLOWED             UHEX-0000000100000000
    LOCK_NO_WRITE_ALLOWED_LOCK       UHEX-0000000200000000
    LOCK_NO_DELETE_ALLOWED_LOCK      UHEX-0000000400000000
    LOCK_NO_META_CHANGE_ALLOWED_LOCK UHEX-0000000800000000
    LOCK_SHARED_LOCK                 UHEX-4000000000000000
    LOCK_LOCKED_LOCK                 UHEX-8000000000000000
    LOCK_NO_LOCK                     UHEX-0000000000000000
    FLAG_FOLDER                       HEX-00000001
    FLAG_FILE                         HEX-00000002
    FLAG_LINK                         HEX-00000004
    FLAG_READ_ONLY                    HEX-00000008
    FLAG_EXECUTABLE                   HEX-00000010
    FLAG_HIDDEN                       HEX-00000020
    FLAG_FOLDER_SORTED                HEX-00000040
    FLAG_FILE_ENCRYPTED               HEX-00000080
    FP_NAN                           UHEX-7FFE000000000000
    FP_MAX_VALUE                     UHEX-7FEFFFFFFFFFFFFF
    FP_MIN_VALUE                     UHEX-0000000000000001
    FP_POS_INFINITY                  UHEX-7FF0000000000000
    FP_NEG_INFINITY                  UHEX-FFF0000000000000
    STATUS_LOWER                     UHEX-0000000000000001
    STATUS_GREATHER                  UHEX-0000000000000002
    STATUS_EQUAL                     UHEX-0000000000000004
    STATUS_OVERFLOW                  UHEX-0000000000000008
    STATUS_ZERO                      UHEX-0000000000000010
    STATUS_NAN                       UHEX-0000000000000020
    STATUS_ALL_BITS                  UHEX-0000000000000040
    STATUS_SOME_BITS                 UHEX-0000000000000080
    STATUS_NONE_BITS                 UHEX-0000000000000100
    STATUS_ELEMENT_WRONG_TYPE        UHEX-0040000000000000
    STATUS_ELEMENT_NOT_EXIST         UHEX-0080000000000000
    STATUS_ELEMENT_ALREADY_EXIST     UHEX-0100000000000000
    STATUS_OUT_OF_SPACE              UHEX-0200000000000000
    STATUS_READ_ONLY                 UHEX-0400000000000000
    STATUS_ELEMENT_LOCKED            UHEX-0800000000000000
    STATUS_IO_ERR                    UHEX-1000000000000000
    STATUS_ILLEGAL_ARG               UHEX-2000000000000000
    STATUS_OUT_OF_MEMORY             UHEX-4000000000000000
    STATUS_ERROR                     UHEX-8000000000000000
    REGISTER_MEMORY_START             HEX-0000000000001000
    REGISTER_MEMORY_START_XNN         HEX-0000000000001028
    REGISTER_MEMORY_LAST_ADDRESS      HEX-00000000000017F8
    REGISTER_MEMORY_END_ADDRESS_SPACE HEX-0000000000001800
</code></pre>

## STRINGS
* a string is an array of multiple characters of the `UTF-8` encoding
* a string ends with a `'\0'` character
* a `U16-STRING` is a `STRING`, but with `UTF-16LE` encoding
* a `U32-STRING` is a `STRING`, but with `UTF-32LE` encoding

## COMMANDS

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

`MVB <NO_CONST_PARAM> , <PARAM>`
* copies the byte value of the second parameter to the first byte parameter
* definition:
    * `p1 <-8-bit- p2`
    * `IP <- IP + CMD_LEN`
* binary:
    *  `01 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `02 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MVDW <NO_CONST_PARAM> , <PARAM>`
* copies the double-word value of the second parameter to the first double-word parameter
* definition:
    * `p1 <-32-bit- p2 `
    * `IP <- IP + CMD_LEN`
* binary:
    * `03 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `04 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `05 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `06 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `07 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

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
    * `10 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `11 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `12 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `13 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `0A <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
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
    * `30 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `31 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`AND <NO_CONST_PARAM> , <PARAM>`
* uses the logical AND operator with the first and the second parameter and stores the result in the first parameter
* definition:
    * `p1 <- p1 & p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `06 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`OR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
* definition:
    * `p1 <- p1 | p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `07 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`XOR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
* definition:
    * `p1 <- p1 ^ p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `08 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`NOT <NO_CONST_PARAM>`
* uses the logical NOT operator with every bit of the parameter and stores the result in the parameter
* this instruction works like `XOR p1, -1` 
* definition:
    * `p1 <- ~ p1`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `09 <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`LSH <NO_CONST_PARAM>, <PARAM>`
* shifts bits of the parameter logically left
* definition:
    * `if ((p1 << p2) >>> p2) = p1`
        * `OVERFLOW <- 0`
    * `else`
        * `OVERFLOW <- 1`
    * `p1 <- p1 << p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `0B <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`RLSH <NO_CONST_PARAM>, <PARAM>`
* shifts bits of the parameter logically right
* definition:
    * `if ((p1 >> p2) << p2) = p1`
        * `OVERFLOW <- 1`
    * `else`
        * `OVERFLOW <- 0`
    * `p1 <- p1 >> 1`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `0C <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`RASH <NO_CONST_PARAM>, <PARAM>`
* shifts bits of the parameter arithmetic right
* definition:
    * `if ((p1 >>> p2) <<< p2) = p1`
        * `OVERFLOW <- 1`
    * `else`
        * `OVERFLOW <- 0`
    * `p1 <- p1 >>> 2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `0D <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

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
    * `0E <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

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
    * `0F <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`JMP <LABEL>`
* sets the instruction pointer to position of the command after the label
* definition:
    * `IP <- IP + RELATIVE_LABEL`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `10 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPEQ <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare operation compared two equal values
* definition:
    * `if EQUAL`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `11 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPNE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare operation compared two different values
* definition:
    * `if EQUAL`
        * `IP <- IP + CMD_LEN`
    * `else`
        * `IP <- IP + RELATIVE_LABEL`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `12 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPGT <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was greater
* definition:
    * `if GREATHER`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `13 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPGE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not lower
* definition:
    * `if GREATHER | EQUAL`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `14 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPLT <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was lower
* definition:
    * `if LOWER`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `15 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPLE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
* definition:
    * `if LOWER | EQUAL`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `16 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPCS <LABEL>`
* sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is set
* definition:
    * `if OVERFLOW`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `17 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPCC <LABEL>`
* sets the instruction pointer to position of the command after the label if the last OVERFLOW flag is cleared
* definition:
    * `if OVERFLOW`
        * `IP <- IP + CMD_LEN`
    * `else`
        * `IP <- IP + RELATIVE_LABEL`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `18 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPZS <LABEL>`
* sets the instruction pointer to position of the command after the label if the last zero flag is set
* definition:
    * `if ZERO`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `19 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPZC <LABEL>`
* sets the instruction pointer to position of the command after the label if the last zero flag is cleared
* definition:
    * `if ! ZERO`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `1A 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPNAN <LABEL>`
* sets the instruction pointer to position of the command after the label if the last NaN flag is set
* definition:
    * `if NAN`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `1B 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPAN <LABEL>`
* sets the instruction pointer to position of the command after the label if the last NaN flag is cleared
* definition:
    * `if ! NAN`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `1C 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`


`JMPAB <LABEL>`
* sets the instruction pointer to position of the command after the label if the last AllBits flag is set
* definition:
    * `if ALL_BITS`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `1D 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPSB <LABEL>`
* sets the instruction pointer to position of the command after the label if the last SomeBits flag is set
* definition:
    * `if SOME_BITS`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `1D 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPNB <LABEL>`
* sets the instruction pointer to position of the command after the label if the last NoneBits flag is set
* definition:
    * `if NONE_BITS`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `1D 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`CALL <LABEL>`
* sets the instruction pointer to position of the label
* and pushes the current instruction pointer to the stack
* definition:
    * `[SP] <- IP`
    * `SP <- SP + 8`
    * `IP <- IP + RELATIVE_LABEL`
* binary:
    * `20 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`CMP <PARAM> , <PARAM>`
* compares the two values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATHER <- 1`
        * `LOWER <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATHER <- 0`
        * `LOWER <- 1`
        * `EQUAL <- 0`
    * `else`
        * `GREATHER <- 0`
        * `LOWER <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `21 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`RET`
* sets the instruction pointer to the position which was secured in the stack
* definition:
    * `SP <- SP - 8`
    * `IP <- [SP]`
* binary:
    * `22 00 00 00 00 00 00 00`

`INT <PARAM>`
* calls the interrupt specified by the parameter
* default interrupts may get called with a diffrent routine
* definition:
    * `ZW <- MEM-ALLOC{size=128}`
    * `[ZW]       <- IP`
    * `[ZW + 8]   <- SP`
    * `[ZW + 16]  <- STATUS`
    * `[ZW + 24]  <- INTCNT`
    * `[ZW + 32]  <- INTP`
    * `[ZW + 40]  <- FS_LOCK`
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
* an interrupt can be overwritten:
    * the interrupt-table is saved in the `INTP` register
    * to overwrite the interrupt `N`, write to `(INTP + (N * 8))` the absolute position of the address
    * on failure the default interrupts use the `ERRNO` register to store information about the error which caused the interrupt to fail
    * example:
        * `PUSH X00` |> only needed when the value of `X00` should not be overwritten
        * `MOV X00, IP` |> this and the next command is not needed if the absolute position is already known
        * `ADD/SUB X00, #RELATIVE-POS-FROM-GET-TO-INTERRUPT`
        * `MOV [INTP + #OVERWRITE_INT_NUM_MULTIPLIED_WITH_8], X00`
        * `POP X00` |> only needed when the value of `X00` should not be overwritten
* negative interrupts will always cause the illegal interrup to be called instead
* when `INTCNT` is greather then the number of default interrupts and the called interrupt is not overwritten, the illegal interrupt will be called instead
* default interrupts:
    * `0 : INT_ERRORS_ILLEGAL_INTERRUPT`: illegal interrupt
        * `X00` contains the number of the illegal interrupt
        * exits with `(128 + illegal_interrup_number)` (without calling the exit interrupt)
        * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `128`
    * `1 : INT_ERRORS_UNKNOWN_COMMAND`: unknown command
        * exits with `7` (without calling the exit interrupt)
    * `2 : INT_ERRORS_ILLEGAL_MEMORY`: illegal memory
        * exits with `6` (without calling the exit interrupt)
    * `3 : INT_ERRORS_ARITHMETIC_ERROR`: arithmetic error
        * exits with `5` (without calling the exit interrupt)
    * `4 : INT_EXIT`: exit
        * use `X00` to specify the exit number of the progress
    * `5 : INT_MEMORY_ALLOC`: allocate a memory-block
        * `X00` saves the size of the block
        * if the value of `X00` is `-1` after the call the memory-block could not be allocated
        * if the value of `X00` is not `-1`, `X00` points to the first element of the allocated memory-block
    * `6 : INT_MEMORY_REALLOC`: reallocate a memory-block
        * `X00` points to the memory-block
        * `X01` is set to the new size of the memory-block
        * `X01` will be `-1` if the memory-block could not be reallocated, the old memory-block will remain valid and should be freed if it is not longer needed
        * `X01` will point to the new memory block, the old memory-block was automatically freed, so it should not be used, the new block should be freed if it is not longer needed
    * `7 : INT_MEMORY_FREE`: free a memory-block
        * `X00` points to the old memory-block
        * after this the memory-block should not be used
    * `8 : INT_OPEN_STREAM`: open new stream
        * `X00` contains a pointer to the STRING, which refers to the file which should be read
        * `X01` specfies the open mode: (bitwise flags)
            * `OPEN_ONLY_CREATE`
                * fail if the file/pipe exist already
                * when this flags is set either `OPEN_FILE` or `OPEN_PIPE` has to be set
            * `OPEN_ALSO_CREATE`
                * create the file/pipe if it does not exist, but do not fail if the file/pipe exist already (overwritten by PFS_SO_ONLY_CREATE)
            * `OPEN_FILE`
                * fail if the element is a pipe and if a create flag is set create a file if the element does not exist already
                * this flag is not compatible with `OPEN_PIPE`
            * `OPEN_PIPE`
                * fail if the element is a file and if a create flag is set create a pipe
                * this flag is not compatible with `OPEN_FILE`
            * `OPEN_READ`
                * open the stream for read access
            * `OPEN_WRITE`
                * open the stream for write access
            * `OPEN_APPEND`
                * open the stream for append access (before every write operation the position is set to the end of the file)
                * implicitly also sets `OPEN_WRITE` (for pipes there is no diffrence in `OPEN_WRITE` and `OPEN_APPEND`)
            * `OPEN_FILE_TRUNCATE`
                * truncate the files content
                * implicitly sets `OPEN_FILE`
                * nop when also `OPEN_ONLY_CREATE` is set
            * `OPEN_FILE_EOF`
                * set the position initially to the end of the file not the start
                * ignored when opening a pipe
            * other flags will be ignored
            * the operation will fail if it is not spezified if the file should be opened for read, write and/or append
        * opens a new stream to the specified file
        * if successfully the STREAM-ID will be saved in the `X00` register
        * if failed `X00` will contain `-1`
        * to close the stream use the stream close interrupt (`INT_STREAM_CLOSE`)
    * `9 : INT_STREAMS_WRITE`: write
        * `X00` contains the STREAM-ID
        * `X01` contains the number of elements to write
        * `X02` points to the elements to write
        * `X01` will be set to the number of written bytes.
    * `10 : INT_STREAMS_READ`: read
        * `X00` contains the STREAM-ID
        * `X01` contains the number of elements to read
        * `X02` points to the elements to read
        * after execution `X01` will contain the number of elements, which has been read.
            * when the value is less than len either an error occured or end of file/pipe has reached (which is not considered an error)
    * `11 : INT_STREAMS_CLOSE`: stream close
        * `X00` contains the STREAM-ID
        * `X00` will be set to 1 on success and 0 on error
    * `12 : INT_STREAMS_FILE_GET_POS`: stream file get position
        * `X00` contains the STREAM/FILE_STREAM-ID
        * `X01` will be set to the stream position or -1 on error
    * `13 : INT_STREAMS_FILE_SET_POS`: stream file set position
        * `X00` contains the STREAM/FILE_STREAM-ID
        * `X01` contains the new position of the stream
        * `X01` will be set to 1 or 0 on error
        * note that it is possible to set the stream position behind the end of the file.
            * when this is done, the next write (not append) operation will fill the hole with zeros
    * `14 : INT_STREAMS_FILE_ADD_POS`: stream file add position
        * `X00` contains the STREAM/FILE_STREAM-ID
        * `X01` contains the value, which should be added to the position of the stream
            * `X01` is allowed to be negative, but the sum of the old position and `X01` is not allowed to be negative
        * `X01` will be set to the new position or -1 on error
        * note that it is possible to set the stream position behind the end of the file.
            * when this is done, the next write (not append) operation will fill the hole with zeros
    * `15 : INT_STREAMS_FILE_SEEK_EOF`: stream file seek eof
        * `X00` contains the STREAM-ID
        * `X01` will be set to the new position of the stream or -1 on error
        * sets the position of the stream to the end of the file (the file length)
    * `16 : INT_OPEN_FILE`: open element handle file
        * `X00` points to the `STRING` which contains the path of the file to be opened
        * `X00` will be set to the newly opened STREAM/FILE-ID or -1 on error
        * this operation will fail if the element is no file
    * `17 : INT_OPEN_FOLDER`: open element handle folder
        * `X00` points to the `STRING` which contains the path of the folder to be opened
        * `X00` will be set to the newly opened STREAM/FOLDER-ID or -1 on error
        * this operation will fail if the element is no folder
    * `18 : INT_OPEN_PIPE`: open element handle pipe
        * `X00` points to the `STRING` which contains the path of the pipe to be opened
        * `X00` will be set to the newly opened STREAM/PIPE-ID or -1 on error
        * this operation will fail if the element is no pipe
    * `19 : INT_OPEN_ELEMENT`: open element handle (any)
        * `X00` points to the `STRING` which contains the path of the element to be opened
        * `X00` will be set to the newly opened STREAM-ID or -1 on error
    * `20 : INT_ELEMENT_OPEN_PARENT`: element open parent handle
        * `X00` contains the ELEMENT-ID
        * `X00` will be set to the newly opened ELEMENT/FOLDER-ID or -1 on error
    * `21 : INT_ELEMENT_GET_CREATE`: get create date
        * `X00` contains the ELEMENT-ID
        * `X01` will be set to the create date or `-1` on error
            * note that `-1` may be the create date of the element, so check `ERRNO` instead
    * `22 : INT_ELEMENT_GET_LAST_MOD`: get last mod date
        * `X00` contains the ELEMENT-ID
        * `X01` will be set to the last modified date or `-1` on error
            * note that `-1` may be the last modified date of the element, so check `ERRNO` instead
    * `23 : INT_ELEMENT_SET_CREATE`: set create date
        * `X00` contains the ELEMENT-ID
        * `X00` contains the new create date of the element
        * `X01` will be set to `1` or `0` on error
    * `24 : INT_ELEMENT_SET_LAST_MOD`: set last modified date
        * `X00` contains the ELEMENT-ID
        * `X00` contains the last modified date of the element
        * `X01` will be set to `1` or `0` on error
    * `25 : INT_ELEMENT_DELETE`: element delete
        * `X00` contains the ELEMENT-ID
        * note that this operation automatically closes the given ELEMENT-ID, the close interrupt should not be invoked after this interrupt returned
        * `X01` will be set to `1` or `0` on error
    * `26 : INT_ELEMENT_MOVE`: element move
        * `X00` contains the ELEMENT-ID
        * `X01` points to a STRING which will be the new name or it is set to `-1` if the name should not be changed
        * `X02` contains the ELEMENT-ID of the new parent of `-1` if the new parent should not be changed
        * when both `X01` and `X02` are set to `-1` this operation will do nothing
        * `X01` will be set to `1` or `0` on error
    * `27 : INT_ELEMENT_GET_NAME`: element get name
        * `X00` contains the ELEMENT-ID
        * `X01` points the the a memory block, which should be used to store the name as a STRING
            * when `X01` is set to `-1` a new memory block will be allocated
        * on success `X01` will point to the name as STRING representation
            * when the memory block is not large enugh, it will be resized
            * note that when `X01` does not point to the start of the memory block the start of the memory block can still be moved during the reallocation
        * on error `X01` will be set to `-1`
    * `28 : INT_ELEMENT_GET_FLAGS`: element get flags
        * `X00` contains the ELEMENT-ID
        * `X01` will be set to the flags or `-1` on error
    * `29 : INT_ELEMENT_MODIFY_FLAGS`: element modify flags
        * `X00` contains the ELEMENT-ID
        * `X01` contains the flags to be added
        * `X02` contains the flags to be removed
        * note that only the low 32 bit will be used and the high 32 bit will be ignored
        * `X01` will be set to `1` or `0` on error
    * `30 : INT_FOLDER_CHILD_COUNT`: element folder child count
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X01` will be set to the number of child elements the folder has or `-1` on error
    * `31 : INT_FOLDER_OPEN_CHILD_OF_NAME`: folder get child of name
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X00` points to a STRING with the name of the child
        * `X01` will be set to a newly opened ELEMENT-ID for the child or `-1` on error
    * `32 : INT_FOLDER_OPEN_CHILD_FOLDER_OF_NAME`: folder get child folder of name
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X00` points to a STRING with the name of the child
        * this operation will fail if the child is no folder
        * `X01` will be set to a newly opened ELEMENT/FOLDER-ID for the child or `-1` on error
    * `33 : INT_FOLDER_OPEN_CHILD_FILE_OF_NAME`: folder get child file of name
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X00` points to a STRING with the name of the child
        * this operation will fail if the child is no file
        * `X01` will be set to a newly opened ELEMENT/FILE-ID for the child or `-1` on error
    * `34 : INT_FOLDER_OPEN_CHILD_PIPE_OF_NAME`: folder get child pipe of name
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X00` points to a STRING with the name of the child
        * this operation will fail if the child is no pipe
        * `X01` will be set to a newly opened ELEMENT/PIPE-ID for the child or `-1` on error
    * `35 : INT_FOLDER_CREATE_CHILD_FOLDER`: folder add child folder
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X00` points to a STRING with the name of the child
        * `X01` will be set to a newly opened/created ELEMENT/FOLDER-ID for the child or `-1` on error
    * `36 : INT_FOLDER_CREATE_CHILD_FILE`: folder add child file
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X01` points to the STRING name of the new child element
        * `X01` will be set to a newly opened/created ELEMENT/FILE-ID for the child or `-1` on error
    * `37 : INT_FOLDER_CREATE_CHILD_PIPE`: folder add child pipe
        * `X00` contains the ELEMENT/FOLDER-ID
        * `X01` points to the STRING name of the new child element
        * `X01` will be set to a newly opened/created ELEMENT/PIPE-ID for the child or `-1` on error
    * `38`: file length
        * `X00` points to the fs-element file
        * `X01` will be set to the length of the file in bytes
        * `X01` will be set to `-1` on error
    * `39 : `: file hash
        * `X00` points to the fs-element file
        * `X01` points to a at least 32-byte large memory block (256-bits : 32-bytes)
            * the memory block from `X01` will be filled with the SHA-256 hash code of the file
        * `X01` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no file
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the element is locked with a diffrent lock
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` contains an invalid ID
                    * if the given ID of the fs-element is invalid (because it was deleted)
    * `40`: file read
        * `X00` points to the fs-element file
        * `X01` contains the number of bytes to read
        * `X01` points to a memory block to which the file data should be filled
        * `X03` contains the offset from the file
        * `X02` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no file
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the element is locked with a diffrent lock
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` contains an invalid ID or the offset / read count is invalid
                    * if the given ID of the fs-element is invalid (because it was deleted)
                    * or if the read count or file offset is negative
                    * or if the read count + file offset is larger than the file length
    * `41`: file write
        * `X00` points to the fs-element file
        * `X01` contains the number of bytes to write
        * `X02` points to the memory block with the data to write
        * `X03` contains the offset from the file
        * `X02` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no file
                * `UHEX-0400000000000000` : `STATUS_ELEMENT_READ_ONLY`: operation was denied because read-only
                    * if the element is marked as read-only
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the element is locked with a diffrent lock
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` contains an invalid ID or the offset / read count is invalid
                    * if the given ID of the fs-element is invalid (because it was deleted)
                    * or if the write count or file offset is negative
                    * or if the write count + file offset is larger than the file length
    * `42`: file append
        * `X00` points to the fs-element file
        * `X01` contains the number of bytes to append
        * `X02` points to the memory block with the data to write
        * `X01` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no file
                * `UHEX-0200000000000000` : `STATUS_OUT_OF_SPACE`: operation failed bcause the there could not be allocated enugh space for the larger file
                    * the file system could either not allocate enugh blocks for the new larger file
                    * or the file system could not allocate enugh space for the larger file system entry of the file
                * `UHEX-0400000000000000` : `STATUS_ELEMENT_READ_ONLY`: operation was denied because read-only
                    * if the element is marked as read-only
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the element is locked with a diffrent lock
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` contains an invalid ID or the offset / read count is invalid
                    * if the given ID of the fs-element is invalid (because it was deleted)
                    * or if the write count or file offset is negative
                    * or if the write count + file offset is larger than the file length
    * `43`: file truncate
        * `X00` points to the fs-element file
        * `X01` contains the new length of the file
        * removes all data from the file which is behind the new length
        * `X01` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no file
                * `UHEX-0200000000000000` : `STATUS_OUT_OF_SPACE`: operation failed bcause the there could not be allocated enugh space
                    * the file system was not able to resize the file system entry to a smaller size
                        * the block intern table sometimes grow when a area is released
                        * if the block intern table can not grow this error occurres
                * `UHEX-0400000000000000` : `STATUS_ELEMENT_READ_ONLY`: operation was denied because read-only
                    * if the element is marked as read-only
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the element is locked with a diffrent lock
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` contains an invalid ID or the offset / read count is invalid
                    * if the given ID of the fs-element is invalid (because it was deleted)
                    * or if the new length is larger than the current file length
                    * or if the new length is negative
    * `44`: link get target
        * `X00` points to the fs-element link
        * `[X00]` : `[X00 + FS_ELEMENT_OFFSET_ID]` will be set to the target ID
        * `X01` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no link
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the element is locked with a diffrent lock
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` contains an invalid ID or the offset / read count is invalid
                    * if the given ID of the fs-element is invalid (because it was deleted)
    * `45`: link set target
        * `X00` points to the fs-element link
        * `X01` points to the new target element
        * sets the target element of the link
            * also flags the link with file or folder and rremoves the other flag (`HEX-00000001` : `FLAG_FOLDER` or `HEX-00000002` : `FLAG_FILE`)
        * `X00` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no link
                * `UHEX-0400000000000000` : `STATUS_ELEMENT_READ_ONLY`: operation was denied because read-only
                    * if the element is marked as read-only
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the element is locked with a diffrent lock
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` contains an invalid ID or the offset / read count is invalid
                    * if the given ID of the fs-element is invalid (because it was deleted)
    * `46`: lock file-system
        * `X00` contains the new lock data
        * the lock is like a lock for elements, but it works for all elements
        * if the file system is already exclusively locked the operation will fail
        * if the file system is locked with a shared lock and the lock data of the given lock is the same to the lock data of the current lock:
            * a shared lock is flaged with `UHEX-4000000000000000` : `LOCK_SHARED_LOCK`
            * the new lock will not contain the shared lock counter
            * the lock should be released like a exclusive lock, when it is no longer needed
            * a shared lock does not give you any permissions, it just blocks operations for all (also for those with the lock)
        * if the given lock is not flagged with `UHEX-8000000000000000` : `LOCK_LOCKED_LOCK`, it will be automatically be flagged with `UHEX-8000000000000000`: `LOCK_LOCKED_LOCK`
        * `X00` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the file syste, is already locked
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X00` does not only contain lock data bits
                    * if the given lock does not only specify the lock data
        * the lock of the file system will be remembered in the `FS_LOCK` register
    * `47`: unlock file-system
        * if the file system is not locked with the given lock the operation will fail
            * if the `FS_LOCK` is `UHEX-0000000000000000` : `LOCK_NO_LOCK`, the operation will always try to remove the lock of the element
        * if the file system is locked with a shared lock:
            * if this is the last lock, the shared lock will be removed
            * else the shared lock counter will be decremented
        * `X00` will be set to `-1` on error
            * the `ERRNO` register will be set:
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the file system is locked with a diffrent lock or not locked at all
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified io error occurred
                    * if some IO error occurred
    * `48`: to get the time in milliseconds
        * `X00` will contain the time in milliseconds or `-1` if not available
    * `49`: to wait the given time in nanoseconds
        * `X00` contain the number of nanoseconds to wait (only values from `0` to `999999999` are allowed)
        * `X01` contain the number of seconds to wait
        * `X00` and `X01` will contain the remaining time (both `0` if it finished waiting)
        * `X02` will be `1` if the call was successfully and `0` if something went wrong
            * if `X02` is `1` the remaining time will always be `0`
            * if `X02` is `0` the remaining time will be greater `0`
        * `X00` will not be negative if the progress waited too long
    * `50`: random
        * `X00` will be filled with random bits
    * `51`: memory copy
        * copies a block of memory
        * this function has undefined behavior if the two blocks overlap
        * `X00` points to the target memory block
        * `X01` points to the source memory block
        * `X02` has the length of bytes to bee copied
    * `52`: memory move
        * copies a block of memory
        * this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)
        * `X00` points to the target memory block
        * `X01` points to the source memory block
        * `X02` has the length of bytes to bee copied
    * `53`: memory byte set
        * sets a memory block to the given byte-value
        * `X00` points to the block
        * `X01` the first byte contains the value to be written to each byte
        * `X02` contains the length in bytes
    * `54`: memory set
        * sets a memory block to the given int64-value
        * `X00` points to the block
        * `X01` contains the value to be written to each element
        * `X02` contains the count of elements to be set
    * `55`: string length
        * `X00` points to the STRING
        * `X00` will be set to the length of the string/ the (byte-)offset of the first byte from the `'\0'` character
    * `56`: string compare
        * `X00` points to the first STRING
        * `X01` points to the second STRING
        * `X00` will be set to zero if both are equal STRINGs, a value greather zero if the first is greather and below zero if the second is greather
            * a STRING is greather if the first missmatching char has numeric greather value
    * `57`: number to string
        * `X00` is set to the number to convert
        * `X01` is points to the buffer to be filled with the number in a STRING format
        * `X02` contains the base of the number system
            * the minimum base is `2`
            * the maximum base is `36`
            * other values lead to undefined behavior
        * `X03` is set to the length of the buffer
            * `0` when the buffer should be allocated by this interrupt
        * `X00` will be set to the size of the STRING
        * `X01` will be set to the new buffer
        * `X03` will be set to the new size of the buffer
            * the new length will be the old length or if the old length is smaller than the size of the STRING (with `\0`) than the size of the STRING (with `\0`)
        * on error `X01` will be set to `-1`
            * the `ERRNO` register will be set:
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X02` is an invalid number system or an invalid buffer size
                    * if the given number system is smaller than `2` or larger than `36`
                    * or if the buffer size is negative
                * `UHEX-4000000000000000` : `STATUS_OUT_OF_MEMORY`: operation failed because the system could not allocate enough memory
                    * the system tries to allocate some memory but was not able to allocate the needed memory
    * `58`: floating point number to string
        * `X00` is set to the number to convert
        * `X01` points to the buffer to be filled with the number in a STRING format
        * `X02` is set to the current size of the buffer
            * `0` when the buffer should be allocated by this interrupt
        * `X00` will be set to the size of the STRING
        * `X01` will be set to the new buffer
        * `X02` will be set to the new size of the buffer
            * the new length will be the old length or if the old length is smaller than the size of the STRING (with `\0`) than the size of the STRING (with `\0`)
        * on error `X01` will be set to `-1`
            * the `ERRNO` register will be set:
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: `X02` is an invalid number system
                    * if the buffer size is negative
                * `UHEX-4000000000000000` : `STATUS_OUT_OF_MEMORY`: operation failed because the system could not allocate enough memory
                    * the system tries to allocate some memory but was not able to allocate the needed memory
    * `59`: string to number
        * `X00` points to the STRING
        * `X01` points to the base of the number system
            * (for example `10` for the decimal system or `2` for the binary system)
        * `X00` will be set to the converted number
        * this function will ignore leading and following white-space characters
        * on success `X01` will be set to `1`
        * on error `X01` will be set to `0`
            * the STRING contains illegal characters
            * or the base is not valid
    * `60`: string to floating point number
        * `X00` points to the STRING
        * `X00` will be set to the converted number
        * if the STRING contains illegal characters or the base is not valid, the behavior is undefined
        * this function will ignore leading and following white-space characters
        * on success `X01` will be set to `1`
        * on error `X01` will be set to `0`
            * the STRING contains illegal characters
            * or the base is not valid
    * `61`: format string
        * `X00` is set to the STRING input
        * `X01` contains the buffer for the STRING output
        * `X02` is the current size of the buffer in bytes
        * the register `X03..XNN` are for the formatting arguments
            * that leads to a maximum of 248 arguments
        * `X00` will be set to the length of the output string (the offset of the `\0` character)
        * `X01` will be set to the output buffer
        * `X02` will be set to the new buffer size in bytes
        * if an error occured `X00` will be set to `-1`
            * the `ERRNO` register will be set:
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: operation failed because because there were invalid arguments
                    * if the last charactet of the input string is a `%` character
                    * or if there are invalid formatting characters
                        * a `%` is not followed by a `%`, `s`, `c`, `B`, `d`, `f`, `p`, `h`, `b` or `o` character
                    * or if there are too many arguments needed
                * `UHEX-4000000000000000` : `STATUS_OUT_OF_MEMORY`: operation failed because the system could not allocate enough memory
                    * if the buffer could not be resized
            * `X02` will be set to the current size of the buffer
        * formatting:
            * `%%`: to escape an `%` character (only one `%` will be in the formatted STRING)
            * `%s`: the next argument points to a STRING, which should be inserted here
            * `%c`: the next argument starts with a word, which should be inserted here
                * note that UTF-16 characters contain not always two bytes, but there will always be only two bytes used
            * `%d`: the next argument contains a number, which should be converted to a STRING using the decimal number system and than be inserted here
            * `%f`: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here
            * `%p`: the next argument contains a pointer, which should be converted to a STRING
                * if not the pointer will be converted by placing a `"p-"` and then the unsigned pointer-number converted to a STRING using the hexadecimal number system
                * if the pointer is `-1` it will be converted to the STRING `"p-inval"`
            * `%h`: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here
            * `%b`: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here
            * `%o`: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here
    * `62`: STRING to U8-STRING
        * `X00` contains the STRING
        * `X01` points to a buffer for the U8-SRING
        * `X02` is set to the size of the size of the buffer
        * `X01` will point to the U8-STRING
        * `X02` will be set to the U8-STRING buffer size
        * `X03` will contain the offset of the `\0` character from the U8-STRING
        * on error `X03` will be set to `-1`
            * the `ERRNO` register will be set:
                * `UHEX-2000000000000000` : `STATUS_ILLEGAL_ARG`: operation failed because of invalid arguments
                    * if the old buffer length is negative
                * `UHEX-4000000000000000` : `STATUS_OUT_OF_MEMORY`: operation failed because the system could not allocate enough memory
                    * if the buffer could not be resized
    * `63`: U8-STRING to STRING
        * `X00` contains the U8-STRING
        * `X01` points to a buffer for the SRING
        * `X02` is set to the size of the size of the buffer
        * `X01` will point to the STRING
        * `X02` will be set to the STRING buffer size
        * `X03` will point to the `\0` character of the STRING
    * `64`: load file
        * `X00` is set to the path (inclusive name) of the file
        * `X00` will point to the memory block, in which the file has been loaded
        * `X01` will be set to the length of the file (and the memory block)
        * when an error occurred `X00` will be set to `-1`
            * the `ERRNO` register will be set:
                * `UHEX-0040000000000000`: `STATUS_ELEMENT_WRONG_TYPE`: the given element is of the wrong type
                    * if the given element is no file
                * `UHEX-0080000000000000` : `STATUS_ELEMENT_NOT_EXIST`: operation failed because the element does not exist
                    * if the given file does not exists
                * `UHEX-0800000000000000` : `STATUS_ELEMENT_LOCKED`: operation was denied because of lock
                    * if the file system is locked with a different lock or not locked at all
                * `UHEX-1000000000000000` : `STATUS_IO_ERR`: an unspecified IO error occurred
                    * if some IO error occurred
                * `UHEX-4000000000000000` : `STATUS_OUT_OF_MEMORY`: operation failed because the system could not allocate enough memory
                    * if the buffer could not be resized
    * `65`: get file 
        * similar like `64` (`INT_LOAD_FILE`) this interrupt loads a file for the program.
            * the only difference is that this interrupt remembers which files has been loaded
            * if the interrupt is executed multiple times with the same file, it will return every time the same memory block.
            * file changes after the file has already been loaded with this interrupt are ignored.
                * only if the file moved or deleted the interrupt recognize the change.
                * if the file gets moved and the new file path is used the interrupt recognizes the old file
                * thus the same memory block is still returned if the file gets moved and the new path is used
                    * thus changes in the content of the file are never recognized
            * this interrupt does not recognize files loaded with the `64` (`INT_LOAD_FILE`) interrupt.
        * `X00` is set to the path (inclusive name) of the file
        * `X00` will point to the memory block, in which the file has been loaded
        * `X01` will be set to the length of the file (and the memory block)
        * `X02` will be set to `1` if the file has been loaded as result of this interrupt and `0` if the file was previously loaded
        * when an error occurred `X00` will be set to `-1`
* binary:
    * `23 <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`PUSH <PARAM>`
* pushes the parameter to the stack
* definition:
    * `[SP] <- p1`
    * `SP <- SP + 8`
    * `IP <- IP + CMD_LEN`
* binary:
    * `24 <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`POP <NO_CONST_PARAM>`
* pops the highest value from the stack to the parameter
* definition:
    * `SP <- SP - 8`
    * `p1 <- [SP]`
    * `IP <- IP + CMD_LEN`
* binary:
    * `25 <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`IRET`
* returns from an interrupt
* definition:
    * `ZW      <- X09`
    * `IP      <- [X09]`
    * `SP      <- [X09 + 8]`
    * `STATUS  <- [X09 + 16]`
    * `INTCNT  <- [X09 + 24]`
    * `INTP    <- [X09 + 32]`
    * `FS_LOCK <- [X09 + 40]`
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
    * `23 00 00 00 00 00 00 00

`CALO <PARAM>, <LABEL/CONST_PARAM>`
* sets the instruction pointer to position of the label
* and pushes the current instruction pointer to the stack
    * `[SP] <- IP`
    * `SP <- SP + 8`
    * `IP <- p1 + p2`
        * the call will not be made relative from this position, so the label remains relative to the start of the file it is declared in
* binary:
    * `2A <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `<P2.NUM_NUM>`

`BCP <PARAM> , <PARAM>`
* compares the two values on bit level
* definition
    * `if (p1 & p2) = 0`
        * `ALL_BITS <- 0`
        * `SOME_BITS <- 0`
        * `NONE_BITS <- 1`
    * `else if (p1 & p2) = p2`
        * `ALL_BITS <- 1`
        * `SOME_BITS <- 1`
        * `NONE_BITS <- 0`
    * `else`
        * `ALL_BITS <- 0`
        * `SOME_BITS <- 1`
        * `NONE_BITS <- 0`
* binary:
    * `2B <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`CMPFP <PARAM> , <PARAM>`
* compares the two floating point values and stores the result in the status register
* definition:
    * `if p1 > p2`
        * `GREATHER <- 1`
        * `LOWER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 < p2`
        * `GREATHER <- 0`
        * `LOWER <- 1`
        * `NaN <- 0`
        * `EQUAL <- 0`
    * `else if p1 = NaN | p2 = NaN`
        * `LOWER <- 0`
        * `GREATHER <- 0`
        * `NaN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `LOWER <- 0`
        * `GREATHER <- 0`
        * `NaN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `2C <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`CHKFP <PARAM>`
* checks if the floating point param is a positive, negative infinity, NaN or normal value
* definition:
    * `if p1 is positive-infinity`
        * `GREATHER <- 1`
        * `LOWER <- 0`
        * `NAN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is negative-infinity`
        * `GREATHER <- 0`
        * `LOWER <- 1`
        * `NAN <- 0`
        * `EQUAL <- 0`
    * `else if p1 is NaN`
        * `LOWER <- 0`
        * `GREATHER <- 0`
        * `NAN <- 1`
        * `EQUAL <- 0`
    * `else`
        * `LOWER <- 0`
        * `GREATHER <- 0`
        * `NAN <- 0`
        * `EQUAL <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `2D <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`ADDFP <NO_CONST_PARAM> , <PARAM>`
* adds the floating point values of both parameters and stores the floating point sum in the first parameter
* definition:
    * `p1 <- p1 fp-add p2`
    * `if p1 = 0.0`
        * `ZERO <- 1`
        * `NAN <- 0`
    * `else if p1 = NaN`
        * `ZERO <- 0`
        * `NAN <- 1`
    * `else`
        * `ZERO <- 0`
        * `NAN <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `32 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SUBFP <NO_CONST_PARAM> , <PARAM>`
* subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the first fp-parameter
* definition:
    * `p1 <- p1 fp-sub p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `if p1 = NaN`
        * `NAN <- 1`
    * `else`
        * `NAN <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `33 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`MULFP <NO_CONST_PARAM> , <PARAM>`
* multiplies the first fp parameter with the second fp and stores the fp result in the first parameter
* definition:
    * `p1 <- p1 fp-mul p2`
    * `if p1 = 0.0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `if p1 = NaN`
        * `NAN <- 1`
    * `else`
        * `NAN <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `34 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`DIVFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result in the first fp-parameter
* definition:
    * `p1 <- p1 fp-div p2`
    * `if p1 = 0.0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `if p1 = NaN`
        * `NAN <- 1`
    * `else`
        * `NAN <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `35 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`NTFP <NO_CONST_PARAM>`
* converts the value of the number param to a floating point
* the value after the 
* definition:
    * `p1 <- as_fp(p1)`
    * `IP <- IP + CMD_LEN`
* binary:
    * `36 <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`FPTN <NO_CONST_PARAM>`
* converts the value of the floating point param to a number
* the value after the 
* definition:
    * `p1 <- as_num(p1)`
    * `IP <- IP + CMD_LEN`
* binary:
    * `37 <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`UDIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* like DIV, but uses the parameters as unsigned parameters
* definition:
    * `p1 <- p1 udiv p2`
    * `p2 <- p1 umod p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `38 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

## not (yet) there/supported
* Multi-threading
    * encapsuling of threads
    * maby allow overwrite of default interrupts for child threads
    * maby thread-groups
* (Multi-progressing)
    * only multi-threading currently planned
* syncronizing/locks
* sockets
* execute other programs
    * already possible when done manually
        * jump to the main function of the target program
            * memory is not freed by that way
            * loaded files with the get interrupt are not unloaded by that way
    * maby make an own interrupt
* support for enviroment-variables