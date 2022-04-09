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
        * example:
            * `my_program.pmc --example    value     --other=val`
            * `X00          <- 4`
            * `[X01]        <- ADDRESS_OF "my_program.pmc\0"`
            * `[X01 + 8]    <- ADDRESS_OF "--example\0"`
            * `[X01 + 16]   <- ADDRESS_OF "value\0"`
            * `[X01 + 24]   <- ADDRESS_OF "--other=val\0"`
    * the `INTCNT` register will be set to `#INTERRUPT_COUNT`
    * the interrupt-table of `INTP` will be initialized and every entry will be set to `-1`
        * so by default the default interrupts will be called, but they can be easily overwritten
    * the `SP` will be either be set to `-1` or `SP` will point to the start of an memory block (with an undefined size)

## Primitive virtual machine

* the primitive virtual machine has the following registers:
    * `IP`
        * `64-bit`
        * the instruction pointer points to the command to be executed
        * initialized with the begin of the loaded machine code file
    * `SP`
        * `64-bit`
        * the stack pointer points to the command to be executed
        * initialized with `-1` or the begin of a memory block
    * `STATUS`
        * `64-bit`
        * saves some results of operations
        * `HEX-0000000000000001` : `LOWER`: if on the last `CMP A, B` A was lower than B
        * `HEX-0000000000000002` : `GREATHER`: if on the last `CMP A, B` A was greater than B
        * `HEX-0000000000000004` : `EQUAL`: if on the last `CMP A, B` A was greater than B
        * `HEX-0000000000000008` : `CARRY`: if an overflow was detected
        * `HEX-0000000000000010` : `ZERO`: if the last arithmetic or logical operation leaded to zero (`HEX-0000000000000000`)
        * `HEX-0000000000000020` : `NAN`: if the last floating point operation leaded to a NaN value
        * initialized with `0`
    * `INTCNT`
        * `64-bit`
        * saves the number of allowed interrupts (`0..(INTCNT-1)` are allowed)
            * all other will call the `INT-ERRORS_ILLEGAL_INTERRUPT` interrupt
        * initialized with the interrupt count which can be used as default interrupts
    * `INTP`
        * `64-bit`
        * points to the interrupt-table
        * initialized with the interrupt table
            * this table has a memory size of  `INTCNT * 8` bytes
            * all entries of the table are initialized with `-1`
    * `X[00..FA]`
        * `251 * 64-bit`
        * number registers, for free use
        * `X00` is initialized with the pointer to the program arguments
        * `X01` is initialized with the count of program arguments

## NUMBERS:

* numbers can be assigned to constants, used as a parameter or inside of a parameter as offset
* to use a decimal number it is possible to write just the number.
    * to use a negative decimal number put a `-` before the number
* it is possible to specify the number system, by putting the correct keyword before the number:
    * for binary (base 2): `BIN-`
    * for octal (base 8): `OCT-`
    * for decimal (base 10): `DEC-`
    * for hexadecimal (base 16): `HEX-`
        * to use a unsigned hexadecimal number, put a `U` before the prefix
* to use negative numbers, put a `N` before the prefix of the number system

## CONSTANTS:

* except for the `--POS--` constant all other constants can be overwritten and removed
* to define constants write a `'#'` as prefix
* to load the constant of an other file
    * `~READ_SYM "<FILE>" [...] >`
        * [...]: no, one or multiple of the following:
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
            * `.psf`: is assumed to be a primitive symbol file
            * `.psc`: is assumed to be a primitive source code file
			* any other end will cause an error
		* if "<FILE>" is "[THIS]" the file, which is now parsed is used.
* to set define an export constant
    * `#EXP~<NAME> <VALUE>`
    * an export constant can be used like a normal constant
    * when an export constant is deleted or overwritten like an normal constant, this will not affect the export
    * to delete an export constant, write `#EXP~<NAME> ~DEL`
        * then it will be deleted as normal and as export constant
    * to change a normal constant to an export constant, just redefine it: `#EXP~<NAME> <NAME>`
* predefined constants:
<pre><code>        --POS-- :                           the actual length of the binary code in bytes
        INT_ERRORS_ILLEGAL_INTERRUPT :      0
        INT_ERRORS_UNKNOWN_COMMAND :        1
        INT_ERRORS_ILLEGAL_MEMORY :         2
        INT_ERRORS_ARITHMETIC_ERROR :       3
        INT_EXIT :                          4
        INT_MEMORY_ALLOC :                  5
        INT_MEMORY_REALLOC :                6
        INT_MEMORY_FREE :                   7
        INT_STREAMS_NEW_IN :                8
        INT_STREAMS_NEW_OUT :               9
        INT_STREAMS_NEW_APPEND :            10
        INT_STREAMS_NEW_IN_OUT :            11
        INT_STREAMS_NEW_APPEND_IN_OUT :     12
        INT_STREAMS_WRITE :                 13
        INT_STREAMS_READ :                  14
        INT_STREAMS_SYNC_STREAM :           15
        INT_STREAMS_CLOSE_STREAM :          16
        INT_STREAMS_GET_POS :               17
        INT_STREAMS_SET_POS :               18
        INT_STREAMS_SET_POS_TO_END :        19
        INT_STREAMS_REM :                   20
        INT_STREAMS_MK_DIR :                21
        INT_STREAMS_REM_DIR :               22
        INT_TIME_GET :                      23
        INT_TIME_WAIT :                     24
        INT_SOCKET_CLIENT_CREATE :          25
        INT_SOCKET_CLIENT_CONNECT :         26
        INT_SOCKET_SERVER_CREATE :          27
        INT_SOCKET_SERVER_LISTEN :          28
        INT_SOCKET_SERVER_ACCEPT :          29
        INT_RANDOM :                        30
        INT_MEMORY_COPY :                   31
        INT_MEMORY_MOVE :                   32
        INT_MEMORY_BSET :                   33
        INT_MEMORY_SET :                    34
        INT_STRING_LENGTH :                 35
        INT_NUMBER_TO_STRING :              36
        INT_FPNUMBER_TO_STRING :            37
        INT_STRING_TO_NUMBER :              38
        INT_STRING_TO_FPNUMBER :            39
        INT_STRING_FORMAT :                 40
        INT_LOAD_FILE :                     41
        INTERRUPT_COUNT :                   42
        MAX_VALUE :                     HEX-7FFFFFFFFFFFFFFF
        MIN_VALUE :                    NHEX-8000000000000000
        STD_IN :                            0
        STD_OUT :                           1
        STD_LOG :                           2
        FP_NAN :                       UHEX-7FFE000000000000
        FP_MAX_VALUE :                 UHEX-7FEFFFFFFFFFFFFF
        FP_MIN_VALUE :                 UHEX-0000000000000001
        FP_POS_INFINITY :              UHEX-7FF0000000000000
        FP_NEG_INFINITY :              UHEX-FFF0000000000000</code></pre>
        
## STRINGS:
* a string is an array of multiple characters of the `UTF-8` encoding
* a string ends with a `'\0'` character

## COMMANDS:

`: [...] >`
* a constant pool contains a constant sequence of bytes
    * to write an constant, write the constant and than `WRITE`
    * to write an number, just write the number
    * to write single bytes put a `B-` before the number
        * then only values from `0` to `255` (both inclusive) can be written.
        * values outside of this range will cause an error

`MOV <NO_CONST_PARAM> , <PARAM>`
* copies the value of the second parameter to the first parameter
* definition:
    * `p1 <- p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `01 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `29 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`
    * `<P3.NUM_NUM>`

`LEA <NO_CONST_PARAM> , <PARAM>`
* sets the first parameter of the value of the second parameter plus the instruction pointer
* definition:
    * `p1 <- p2 + IP`
    * `IP <- IP + CMD_LEN`
* binary:
    * `28 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SWAP <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* swaps the value of the first and the second parameter
* definition:
    * `ZW <- p1`
    * `p1 <- p2`
    * `p2 <- ZW`
    * `IP <- IP + CMD_LEN`
* binary:
    * `27 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`ADD <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and stores the sum in the first parameter
* definition:
    * `if ((p1 > 0) & (p2 > 0) & ((p1 + p2) < 0)) | ((p1 < 0) & (p2 < 0) & ((p1 + p2) > 0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 + p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `02 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`ADDC <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and the carry flag and stores the sum in the first parameter
* definition:
    * `if ((p1 > 0) & ((p2 + CARRY) > 0) & ((p1 + p2 + CARRY) < 0)) | ((p1 < 0) & ((p2 + CARRY) < 0) & ((p1 + (p2 + CARRY)) > 0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 + (p2 + CARRY)`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `30 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

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

`SUB <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter from the first parameter and stores the result in the first parameter
* definition:
    * `if ((p1 > 0) & (p2 < 0) & ((p1 - p2) < 0)) | ((p1 < 0) & (p2 > 0) & ((p1 - p2) > 0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 - p2`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `03 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`SUBC <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter with the carry flag from the first parameter and stores the result in the first parameter
* definition:
    * `if (p1 > 0) & ((p2 + CARRY) < 0) & ((p1 - (p2 + CARRY)) < 0)) | ((p1 < 0) & (p2 > 0) & ((p1 - (p2 + CARRY)) > 0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 - (p2 + CARRY)`
    * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `31 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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
    * `04 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`UMUL <NO_CONST_PARAM> , <PARAM>`
* like MUL, but uses the parameters as unsigned parameters
* definition:
    * `p1 <- p1 u-mul p2`
        * `if p1 = 0`
        * `ZERO <- 1`
    * `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`
* binary:
    * `39 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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

`DIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* divides the first parameter with the second and stores the result in the first parameter and the reminder in the second parameter
* definition:
    * `p1 <- p1 / p2`
    * `p2 <- p1 mod p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `05 <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `[P2.NUM_NUM]`
    * `[P2.OFF_NUM]`

`UDIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* like DIV, but uses the parameters as unsigned parameters
* definition:
    * `p1 <- p1 udiv p2`
    * `p2 <- p1 mod p2`
    * `IP <- IP + CMD_LEN`
* binary:
    * `3A <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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

`LSH <NO_CONST_PARAM>, <PARAM>`
* shifts bits of the parameter logically left
* definition:
    * `if ((p1 << p2) >> p2) = p1`
        * `CARRY <- 0`
    * `else`
        * `CARRY <- 1`
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
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
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
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
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

`NEG <NO_CONST_PARAM>`
* uses the arithmetic negation operation with the parameter and stores the result in the parameter 
* this instruction works like `MUL p1, -1`
* definition:
    * `if p1 = 0`
        * `CARRY <- 0`
        * `ZERO <- 1`
    * `if p1 = #MIN-VALUE`
        * `CARRY <- 1`
        * `ZERO <- 0`
    * `else`
        * `CARRY <- 0`
        * `ZERO <- 0`
    * `p1 <- 0 - p1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `0A <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
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
* sets the instruction pointer to position of the command after the label if the last carry flag is set
* definition:
    * `if CARRY`
        * `IP <- IP + RELATIVE_LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not
* binary:
    * `17 00 00 00 00 00 00 00`
    * `<RELATIVE_LABEL>`

`JMPCC <LABEL>`
* sets the instruction pointer to position of the command after the label if the last carry flag is cleared
* definition:
    * `if CARRY`
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

`CALO <PARAM>, <LABEL/CONST_PARAM>`
* sets the instruction pointer to position of the label
* and pushes the current instruction pointer to the stack
    * `[SP] <- IP`
    * `SP <- SP + 8`
    * `IP <- p1 + (p2)`
        * the call will not be made relative from this position, so the label remains relative to the start of the file it is declared in
* binary:
    * `2A <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`
    * `<P2.NUM_NUM>`

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
    * `2B <B-P1.TYPE> <B-P2.TYPE> 00 <B-P2.OFF_REG|00> <B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|B-P2.NUM_REG|B-P2.OFF_REG|00>`
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

`IRET`
* returns from an interrupt
* definition:
    * `ZW     <- X0A`
    * `IP     <- [X0A]`
    * `SP     <- [X0A + 8]`
    * `STATUS <- [X0A + 16]`
    * `INTCNT <- [X0A + 24]`
    * `INTP   <- [X0A + 32]`
    * `X00    <- [X0A + 40]`
    * `X01    <- [X0A + 48]`
    * `X02    <- [X0A + 56]`
    * `X03    <- [X0A + 64]`
    * `X04    <- [X0A + 72]`
    * `X05    <- [X0A + 80]`
    * `X06    <- [X0A + 88]`
    * `X07    <- [X0A + 98]`
    * `X08    <- [X0A + 104]`
    * `X09    <- [X0A + 112]`
    * `X0A    <- [X0A + 120]`
    * `FREE ZW`
        * this does not use the free interrupt, but works like the default free interrupt (without calling the interrupt (what could cause an infinite recursion))
* binary:
    * `23 00 00 00 00 00 00 00

`INT <PARAM>`
* calls the interrupt specified by the parameter
* definition:
    * `ZW <- MEM-ALLOC{size=128}`
    * `[ZW]       <- IP`
    * `[ZW + 8]   <- SP`
    * `[ZW + 16]  <- STATUS`
    * `[ZW + 24]  <- INTCNT`
    * `[ZW + 32]  <- INTP`
    * `[ZW + 40]  <- X00`
    * `[ZW + 48]  <- X01`
    * `[ZW + 56]  <- X02`
    * `[ZW + 64]  <- X03`
    * `[ZW + 72]  <- X04`
    * `[ZW + 80]  <- X05`
    * `[ZW + 88]  <- X06`
    * `[ZW + 96]  <- X07`
    * `[ZW + 104] <- X08`
    * `[ZW + 112] <- X09`
    * `[ZW + 120] <- X0A`
    * `X0A        <- ZW`
    * `IP         <- [INTP + (p1 * 8)]`
* an interrupt can be overwritten:
    * the interrupt-table is saved in the `INTP` register
    * to overwrite the interrupt `N`, write to `(INTP + (N * 8))` the absolute position of the address
    * example:
        * `PUSH X00` |> only needed when the value of `X00` should not be overwritten
        * `MOV X00, IP` |> this and the next command is not needed if the absolute position is already known
        * `ADD/SUB X00, #RELATIVE-POS-FROM-GET-TO-INTERRUPT`
        * `MOV [INTP + #OVERWRITE_INT_NUM_MULTIPLIED_WITH_8], X00`
        * `POP X00` |> only needed when the value of `X00` should not be overwritten
* negative interrupts will always cause the illegal interrup to be called instead
* when `INTCNT` is greather then the number of default interrupts and the called interrupt is not overwritten, the illegal interrupt will be called instead
* default interrupts:
    * `0`: illegal interrupt
        * `X00` contains the number of the illegal interrupt
        * calls the exit interrupt with `(64 + illegal_interrup_number)`
        * if the forbidden interrupt is the exit input, the program exits with `(64 + 4) = 68`, but does not calls the exit interrupt to do so
        * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `63`
    * `1`: unknown command
        * `X00` contains the illegal command
        * calls the exit interrupt with `62`
    * `2`: illegal memory
        * calls the exit interrupt with `61`
    * `3`: arithmetic error
        * calls the exit interrupt with `60`
    * `4`: exit
        * use `X00` to specify the exit number of the progress
    * `5`: allocate a memory-block
        * `X00` saves the size of the block
        * if the value of `X00` is `-1` after the call the memory-block could not be allocated
        * if the value of `X00` is not `-1`, `X00` points to the first element of the allocated memory-block
    * `6`: reallocate a memory-block
        * `X00` points to the memory-block
        * `X01` saves the new size of the memory-block
        * if the value of `X01` is `-1` after the call the memory-block could not be reallocated, the old memory-block will remain valid and may be used and should be freed if it is not longer needed
        * if the value of `X01` is not `-1`, `X01` points to the first element of the allocated memory-block and the old memory-block was automatically freed, so it should not be used
    * `7`: free a memory-block
        * `X00` points to the old memory-block
        * after this the memory-block should not be used
    * `8`: open new in stream
        * `X00` contains a pointer to the STRING, which refers to the file which should be read
        * opens a new in stream to the specified file
        * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`
        * output operations are not supported on the new stream
    * `9`: open new out stream
        * `X00` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`
        * input operations are not supported on the new stream
    * `10`: open new out, append stream
        * `X00` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`
    * `11`: open new in/out stream
        * `X00` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`
    * `12`: open new in/out, append stream
        * `X00` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `X00` register, if not `X00` will contain `-1`
    * `13`: write
        * `X00` contains the STREAM-ID
        * `X01` contains the number of elements to write
        * `X02` points to the elements to write
        * after execution `X01` will contain the number of written elements or `-1` if an error occurred
    * `14`: read
        * `X00` contains the STREAM-ID
        * `X01` contains the number of elements to read
        * `X02` points to the elements to read
        * after execution `X01` will contain the number of elements, which has been read or `-1` if an error occurred.
        * if `X01` is `0` the end of the stream has reached
        * reading less bytes than expected does not mead that the stream has reached it's end
    * `15`: sync stream
        * `X00` contains the STREAM-ID
        * if `X00` is set to `-1`, it will be tried to syncronize everything
        * if the synchronization was successfully `X00` will be set to `1`, if not `0`
    * `16`: close stream
        * `X00` contains the STREAM-ID
        * if the stream was closed successfully `X00` will contain `1`, if not `0`
    * `17`: get stream pos
        * `X00` contains the STREAM-ID
        * `X01` will contain the position of the stream or `-1` if something went wrong.
    * `18`: set stream pos
        * `X00` contains the STREAM-ID
        * `X01` contains the position to be set.
        * if the stream-ID is the ID of a default stream the behavior is undefined.
        * `X01` will contain the new stream position.
    * `19`: set stream to end
        * `X00` contains the STREAM-ID
        * this will set the stream position to the end
        * `X01` will the new file pos or `-1` if something went wrong
    * `20`: remove file
        * `X00` contains a pointer of a STRING with the file
        * if the file was successfully removed `X00` will contain `1`, if not `0`
    * `21`: make dictionary
        * `X00` contains a pointer of a STRING with the dictionary
        * if the dictionary was successfully created `X00` will contain `1`, if not `0`
    * `22`: remove dictionary
        * `X00` contains a pointer of a STRING with the dictionary
        * if the dictionary was successfully removed `X00` will contain `1`, if not `0`
        * if the dictionary is not empty this call will fail (and set `X00` to `0`)
    * `23`: to get the time in milliseconds
        * `X00` will contain the time in milliseconds or `-1` if not available
    * `24`: to wait the given time in nanoseconds
        * `X00` contain the number of nanoseconds to wait (only values from `0` to `999999999` are allowed)
        * `X01` contain the number of seconds to wait
        * `X00` and `X01` will contain the remaining time (`0` if it finished waiting)
        * `X02` will be `1` if the call was successfully and `0` if something went wrong
            * if `X02` is `1` the remaining time will always be `0`
            * if `X02` is `0` the remaining time will be greater `0`
        * `X00` will not be negative if the progress waited too long
    * `25`: socket client create
        * makes a new client socket
        * `X00` will be set to the SOCKET-ID or `-1` if the operation failed
    * `26`: socket client connect
        * `X00` points to the SOCKET-ID
        * `X01` points to a STRING, which names the host
        * `X02` contains the port
            * the port will be the normal number with the normal byte order
        * connects an client socket to the host on the port
        * `X01` will be set to the `1` on success and `0` on a fail
        * on success, the SOCKET-ID, can be used as a STREAM-ID
    * `27`: socket server create
        * `X00` contains the port
            * the port will be the normal number with the normal byte order
        * makes a new server socket
        * `X00` will be set to the SOCKET-ID or `-1` when the operation fails
    * `28`: socket server listens
        * `X00` contains the SOCKET-ID
        * `X01` contains the MAX_QUEUE length
        * let a server socket listen
        * `X01` will be set to `1` or `0` when the operation fails
    * `29`: socket server accept
        * `X00` contains the SOCKET-ID
        * let a server socket accept a client
        * this operation will block, until a client connects
        * `X01` will be set a new SOCKET-ID, which can be used as STREAM-ID, or `-1`
    * `30`: random
        * `X00` will be filled with random bits
    * `31`: memory copy
        * copies a block of memory
        * this function has undefined behavior if the two blocks overlap
        * `X00` points to the target memory block
        * `X01` points to the source memory block
        * `X02` has the length of bytes to bee copied
    * `32`: memory move
        * copies a block of memory
        * this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)
        * `X00` points to the target memory block
        * `X01` points to the source memory block
        * `X02` has the length of bytes to bee copied
    * `33`: memory byte set
        * sets a memory block to the given byte-value
        * `X00` points to the block
        * `X01` the first byte contains the value to be written to each byte
        * `X02` contains the length in bytes
    * `34`: memory set
        * sets a memory block to the given int64-value
        * `X00` points to the block
        * `X01` contains the value to be written to each element
        * `X02` contains the count of elements to be set
    * `35`: string length
        * `X00` points to the STRING
        * `X00` will be set to the length of the string/ the (byte-)offset of the `'\0'` character
    * `36`: number to string
        * `X00` is set to the number to convert
        * `X01` is points to the buffer to be filled with the number in a STRING format
        * `X02` contains the base of the number system
            * the minimum base is `2`
            * the maximum base is `36`
            * other values lead to undefined behavior
        * `X00` will be set to the length of the STRING
    * `37`: floating point number to string
        * `X00` is set to the number to convert
        * `X02` contains the maximum amount of digits to be used to represent the floating point number
        * `X01` is points to the buffer to be filled with the number in a STRING format
    * `38`: string to number
        * `X00` points to the STRING
        * `X01` points to the base of the number system
            * (for example `10` for the decimal system or `2` for the binary system)
        * `X00` will be set to the converted number
        * `X01` will point to the end of the number-STRING
            * this might be the `\0'` terminating character
        * if the STRING contains illegal characters or the base is not valid, the behavior is undefined
        * this function will ignore leading space characters
    * `39`: string to floating point number
        * `X00` points to the STRING
        * `X00` will be set to the converted number
        * `X01` will point to the end of the number-STRING
            * this might be the `\0'` terminating character
        * if the STRING contains illegal characters or the base is not valid, the behavior is undefined
        * this function will ignore leading space characters
    * `40`: format string
        * `X00` is set to the STRING input
        * `X01` contains the buffer for the STRING output
            * if `X01` is set to `-1`, `X01` will be allocated to a memory block
                * the allocated memory block will be exact large enough to contain the formatted STRING
                * if there could not be allocated enough memory, `X01` will be set to `-1`
        * `X00` will be set to the length of the output string
        * the register `X02..XNN` are for the formatting parameters
            * if there are mor parameters used then there are registers the behavior is undefined.
                * that leads to a maximum of 249 parameters.
        * formatting:
            * everything, which can not be formatted, will be delegated to the target buffer
            * `%s`: the next argument points to a STRING, which should be inserted here
            * `%c`: the next argument points to a character, which should be inserted here
                * note that characters may contain more than one byte
                    * `BIN-0.......` -> one byte (equivalent to an ASCII character)
                    * `BIN-10......` -> invalid, treated as one byte
                    * `BIN-110.....` -> two bytes
                    * `BIN-1110....` -> three bytes
                    * `BIN-11110...` -> four bytes
                    * `BIN-111110..` -> invalid, treated as five byte
                    * `BIN-1111110.` -> invalid, treated as six byte
                    * `BIN-11111110` -> invalid, treated as seven byte
                    * `BIN-11111111` -> invalid, treated as eight byte
            * `%B`: the next argument points to a byte, which should be inserted here (without being converted to a STRING)
            * `%d`: the next argument contains a number, which should be converted to a STRING using the decimal number system and than be inserted here
            * `%f`: the next argument contains a floating point number, which should be converted to a STRING and than be inserted here
            * `%p`: the next argument contains a pointer, which should be converted to a STRING
                * if not the pointer will be converted by placing a `"p-"` and then the pointer-number converted to a STRING using the hexadecimal number system
                * if the pointer is `-1` it will be converted to the STRING `"---"`
            * `%h`: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here
            * `%b`: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here
            * `%o`: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here
    * `41`: load file
        * `X00` is set to the path (inclusive name) of the file
        * `X00` will point to the memory block, in which the file has been loaded
        * `X01` will be set to the length of the file (and the memory block)
        * when an error occured `X00` will be set to `-1`
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

`INC <NO_CONST_PARAM>`
* increments the param by one
* definition:
    * `if p1 = MAX_VALUE`
        * `CARRY <- 1`
        * `ZERO <- 0`
    * `else if p1 = -1`
        * `CARRY <- 0`
        * `ZERO <- 1`
    * `else`
        * `CARRY <- 0`
        * `ZERO <- 0`
    * `p1 <- p1 + 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `0F <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

`DEC <NO_CONST_PARAM>`
* decrements the param by one
* definition:
    * `if p1 = MIN_VALUE`
        * `CARRY <- 1`
        * `ZERO <- 0`
    * `else if p1 = 1`
        * `CARRY <- 0`
        * `ZERO <- 1`
    * `else`
        * `CARRY <- 0`
        * `ZREO <- 0`
    * `p1 <- p1 - 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `0E <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
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

`CHKFP <PARAM>`
* checks weater the floating point param is a positive and negative infinity and for NaN
* definition:
    * `if p1 is positive-infinity`
        * `GREATHER <- 1`
        * `LOWER <- 0`
        * `NAN <- 0`
        * `ZERO <- 0`
    * `else if p1 is negative-infinity`
        * `GREATHER <- 0`
        * `LOWER <- 1`
        * `NAN <- 0`
        * `ZERO <- 0`
    * `else if p1 is NaN`
        * `LOWER <- 0`
        * `GREATHER <- 0`
        * `NAN <- 1`
        * `ZERO <- 0`
    * `else`
        * `LOWER <- 0`
        * `GREATHER <- 0`
        * `NAN <- 0`
        * `ZERO <- 1`
    * `IP <- IP + CMD_LEN`
* binary:
    * `38 <B-P1.TYPE> 00 00 00 00 <B-P1.OFF_REG|00> <B-P1.NUM_REG|B-P1.OFF_REG|00>`
    * `[P1.NUM_NUM]`
    * `[P1.OFF_NUM]`

## not (yet) there/supported
* execute other programs
* Multi-threading/-progressing
