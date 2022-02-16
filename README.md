# primitive-code

a register based assembler language

this is the assembler-language for the Primitive-Virtual-Machine

## Start

* A primitive-assembler-code file can be assembled to a primitive-machine-code file.
* A primitive-machine-code file can be executed with a primitive-virtual-machine.
    * the `X00` register will be set to the number of arguments
    * the `X01` register will point to the arguments
    * the arguments will point to STRINGs
        * the first argument will be the program itself, all beyond will be the arguments of the program
        * example:
            * `my_program.pmc --example    value --other=val`
            * `X00          <- 4`
            * `[X01]        <- ADDRESS_OF "my_program.pmc\0"`
            * `[X01 + 8]    <- ADDRESS_OF "--example\0"`
            * `[X01 + 16]   <- ADDRESS_OF "value\0"`
            * `[X01 + 24]   <- ADDRESS_OF "--other=val\0"`
    * the `INTCNT` register will be set to `#INTERRUPT_COUNT`
    * the interrupt-table of `INTP` will be initialized and every entry will be set to `-1`
        * so by default the default interrupts will be called, but they can be easily overwritten

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
        * `HEX-0000000000000004` : `CARRY`: if an overflow was detected
        * `HEX-0000000000000008` : `ZERO`: if the last arithmetic or logical operation leaded to zero (`HEX-0000000000000000`)
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
        * (256-5) * 64-bit
        * number registers, for free use
		* `X00` is initialized with the pointer to the program arguments
		* `X01` is initialized with the count of program arguments

## NUMBERS:

* numbers can be assigned to constants, used as a parameter or inside of a parameter as offset
* to write a decimal number it is possible to write just the number.
	* to write a negative decimal number put a `-` before the number
* it is possible to specify the number system, by putting the correct keyword before the number:
	* for binary (base 2): `BIN-`
	* for octal (base 8): `OCT-`
	* for decimal (base 10): `DEC-`
	* for hexadecimal (base 16): `HEX-`
		* to use a unsigned hexadecimal number, put a `U` before the prefix
* to use negative numbers, put a `N` before the prefix of the number system

## CONSTANTS:

* except for the `--POS--` constant all other constants can be overwritten and removed
* to define constants write a '#' as prefix
* the coder can define constants 
* predefined constants:

<pre><code>        --POS-- :                           the actual length of the binary code
        #INT_ERRORS_UNKNOWN_COMMAND :       0
        #INT_ERRORS_ILLEGAL_INTERRUPT :     1
        #INT_ERRORS_ILLEGAL_MEMORY :        2
        #INT_ERRORS_ARITHMETIC_ERROR :      3
        #INT_EXIT :                         4
        #INT_MEMORY_ALLOC :                 5
        #INT_MEMORY_REALLOC :               6
        #INT_MEMORY_FREE :                  7
        #INT_STREAMS_NEW_IN :               8
        #INT_STREAMS_NEW_OUT :              9
        #INT_STREAMS_NEW_APPEND :           10
        #INT_STREAMS_NEW_IN_OUT :           11
        #INT_STREAMS_NEW_APPEND_IN_OUT :    12
        #INT_STREAMS_WRITE :                13
        #INT_STREAMS_READ :                 14
        #INT_STREAMS_CLOSE_STREAM :         15
        #INT_STREAMS_GET_POS :              16
        #INT_STREAMS_SET_POS :              17
        #INT_STREAMS_SET_POS_TO_END :       18
        #INT_STREAMS_REM :                  19
        #INT_STREAMS_MK_DIR :               20
        #INT_STREAMS_REM_DIR :              21
        #INT_TIME_GET :                     22
        #INT_TIME_WAIT :                    23
        #INT_RANDOM :                       24
        #INT_SOCKET_CLIENT-CREATE :         25
        #INT_SOCKET_CLIENT-CONNECT :        26
        #INT_SOCKET_SERVER-CREATE :         27
        #INT_SOCKET_SERVER-LISTEN :         28
        #INT_SOCKET_SERVER-ACCEPT :         29
        #INTERRUPT_COUNT :                  30
        #INT_FUNC_MEMORY_COPY :             30
        #INT_FUNC_MEMORY_MOVE :             31
        #INT_FUNC_MEMORY_BSET :             32
        #INT_FUNC_MEMORY_SET :              33
        #INT_FUNC_STRING_LENGTH :           34
        #INT_FUNC_STRING_TO_FPNUMBER :      35
        #INT_FUNC_STRING_TO_NUMBER :        36
        #INT_FUNC_NUMBER_TO_STRING :        37
        #INT_FUNC_FPNUMBER_TO_STRING :      38
        #INT_FUNC_STRING_FORMAT :           39
        #INT_FUNC_END :                     40
        #MAX_VALUE :                    HEX-7FFFFFFFFFFFFFFF
        #MIN_VALUE :                   NHEX-8000000000000000
        #STD_IN :                           0
        #STD_OUT :                          1
        #STD_LOG :                          2
        #FP-NAN :                      UHEX-7FFE000000000000
        #FP_MAX_VALUE :                UHEX-7FEFFFFFFFFFFFFF
        #FP_MIN_VALUE :                UHEX-0000000000000001
        #FP_POS_INFINITY :             UHEX-7FF0000000000000
        #FP_NEG_INFINITY :             UHEX-FFF0000000000000</code></pre>

## STRINGS:
* a string is an array of multiple characters of the UTF-8 encoding
* a string ends with a '\0' character

## COMMANDS:

`: [...] >`
* a constant pool contains a constant sequence of bytes
	* to write an constant, write the constant and than `WRITE`
	* to write an number, just write the number
	* to write single bytes put a `B-` before the number
		* then only values from `0` to `255` can be written.
		* values out of this range will cause an error

`MOV <NO_CONST_PARAM> , <PARAM>`
* copies the value of the second parameter to the first parameter
    * `p1 <- p2`
    * `IP <- IP + CMD_LEN`

`SWAP <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* swaps the value of the first and the second parameter
    * `ZW <- p1`
    * `p1 <- p2`
    * `p2 <- ZW`
    * `IP <- IP + CMD_LEN`

`ADD <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and stores the sum in the first parameter
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

`ADDC <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and the carry flag and stores the sum in the first parameter
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

`ADDFP <NO_CONST_PARAM> , <PARAM>`
* adds the floating point values of both parameters and stores the floating point sum in the first parameter
    * `if ((p1 > 0.0) & (p2 > 0.0) & ((p1 + p2) < 0.0)) | ((p1 < 0.0) & (p2 < 0.0) & ((p1 + p2) > 0.0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 fp-add p2`
	* `if p1 = 0.0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`SUB <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter from the first parameter and stores the result in the first parameter
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

`SUBC <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter with the carry flag from the first parameter and stores the result in the first parameter
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

`SUBFP <NO_CONST_PARAM> , <PARAM>`
* subtracts the second fp-parameter from the first fp-parameter and stores the fp-result in the first fp-parameter
    * `if ((p1 > 0) & (p2 < 0) & ((p1 fp-sub p2) < 0)) | ((p1 < 0) & (p2 > 0) & ((p1 fp-sub p2) > 0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 fp-sub p2`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`MUL <NO_CONST_PARAM> , <PARAM>`
* multiplies the first parameter with the second and stores the result in the first parameter
    * `if (((p1 > 0) & (p2 > 0) | (p1 < 0) & (p2 < 0)) & ((p1 * p2) < 0)) | (((p1 > 0) & (p2 < 0) | (p1 < 0) & (p2 > 0)) & ((p1 * p2) > 0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 * p2`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`MULFP <NO_CONST_PARAM> , <PARAM>`
* multiplies the first fp parameter with the second fp and stores the fp result in the first parameter
    * `if (((p1 > 0.0) & (p2 > 0.0) | (p1 < 0.0) & (p2 < 0.0)) & ((p1 fp-mul p2) < 0.0)) | (((p1 > 0.0) & (p2 < 0.0) | (p1 < 0.0) & (p2 > 0.0)) & ((p1 fp-mul p2) > 0.0))`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 fp-mul p2`
	* `if p1 = 0.0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`DIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* divides the first parameter with the second and stores the result in the first parameter and the reminder in the second parameter
	* `p1 <- p1 / p2`
	* `p2 <- p1 mod p2`
    * `IP <- IP + CMD_LEN`

`DIVFP <NO_CONST_PARAM> , <PARAM>`
* divides the first fp-parameter with the second fp and stores the fp-result in the first fp-parameter
    * `p1 <- p1 fp-div p2`
	* `if p1 = 0.0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`AND <NO_CONST_PARAM> , <PARAM>`
* uses the logical AND operator with the first and the second parameter and stores the result in the first parameter
    * `p1 <- p1 & p2`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`OR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
    * `p1 <- p1 | p2`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`XOR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
    * `p1 <- p1 ^ p2`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`LSH <NO_CONST_PARAM>`
* shifts bits of the parameter logically left
* this effectively multiplies the parameter with two
    * `if (p1 | NHEX-8000000000000000) = p1`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 * 2`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`RLSH <NO_CONST_PARAM>`
* shifts bits of the parameter logically right
    * `if (p1 | HEX-0000000000000001) = p1`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 >> 1`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`RASH <NO_CONST_PARAM>`
* shifts bits of the parameter arithmetic right
* this effectively divides the parameter with two
    * `if (p1 | HEX-0000000000000001) = p1`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 / 2`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`NOT <NO_CONST_PARAM>`
* uses the logical NOT operator with every bit of the parameter and stores the result in the parameter
* this instruction works like `XOR p1, -1` 
    * `p1 <- ~ p1`
	* `if p1 = 0`
        * `ZERO <- 1`
	* `else`
        * `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`NEG <NO_CONST_PARAM>`
* uses the arithmetic negation operation with the parameter and stores the result in the parameter 
* this instruction works like `MUL p1, -1`
    * `if p1 = #MIN-VALUE`
        * `CARRY <- 1`
		* `ZERO <- 0`
    * `else`
        * `CARRY <- 0`
        * `p1 <- 0 - p1`
		* `if p1 = 0`
			* `ZERO <- 1`
		* `else`
			* `ZERO <- 0`
    * `IP <- IP + CMD_LEN`

`JMP <LABEL>`
* sets the instruction pointer to position of the command after the label
    * `IP <- IP - --POS-- + LABEL`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPEQ <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare operation compared two equal values
    * `if ( ! GREATHER) & ( ! LOWER)`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPNE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare operation compared two different values
    * `if GREATHER | LOWER`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPGT <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was greater
    * `if GREATHER`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPGE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not lower
    * `if ! LOWER`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPLT <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was lower
    * `if LOWER`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPLE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
    * `if ! GREATHER`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPCS <LABEL>`
* sets the instruction pointer to position of the command after the label if the last carry flag is set
    * `if CARRY`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPCC <LABEL>`
* sets the instruction pointer to position of the command after the label if the last carry flag is cleared
    * `if ! CARRY`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPZS <LABEL>`
* sets the instruction pointer to position of the command after the label if the last zero flag is set
    * `if ZERO`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`JMPZC <LABEL>`
* sets the instruction pointer to position of the command after the label if the last zero flag is cleared
    * `if ! ZERO`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
        * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CALL <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
    * `[SP] <- IP`
    * `SP <- SP + 1`
    * `IP <- IP - --POS-- + LABEL`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CMP <PARAM> , <PARAM>`
* compares the two values and stores the result in the status register
    * `if p1 < p2`
        * `LOWER <- 1`
        * `GREATHER <- 0`
    * `if p1 > p2`
        * `LOWER <- 0`
        * `GREATHER <- 1`
    * `if p1 = p2`
        * `LOWER <- 0`
        * `GREATHER <- 0`
    * `IP <- IP + CMD_LEN`

`RET`
* sets the instruction pointer to the position which was secured in the stack
    * `IP <- [SP]`
    * `SP <- SP - 1`

`IRET`
* returns from an interrupt
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
		* this does not use the free interrupt, but works like the default free interrupt (without calling the interrupt (what would cause an infinite recursion)

`INT <PARAM>`
* calls the interrupt specified by the parameter
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
* default interrupts:
    * `0`: unknown command
        * `X00` contains the illegal command
        * calls the exit interrupt with `9`
    * `1`: illegal interrupt
        * `X00` contains the number of the illegal interrupt
        * calls the exit interrupt with `6`
        * if the forbidden interrupt is the exit input, the program exits with `7`
        * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `8`
    * `2`: illegal memory
        * calls the exit interrupt with `5`
    * `3`: arithmetic error
        * calls the exit interrupt with `4`
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
    * `15`: close stream
        * `X00` contains the STREAM-ID
        * if the stream was closed successfully `X00` will contain `1`, if not `0`
    * `16`: get stream pos
        * `X00` contains the STREAM-ID
        * `X01` will contain the position of the stream or `-1` if something went wrong.
    * `17`: set stream pos
        * `X00` contains the STREAM-ID
        * `X01` contains the position to be set.
        * if the stream-ID is the ID of a default stream the behavior is undefined.
        * `X01` will contain the new stream position.
    * `18`: set stream to end
        * `X00` contains the STREAM-ID
        * this will set the stream position to the end
        * `X01` will the new file pos or `-1` if something went wrong
    * `19`: remove file
        * `X00` contains a pointer of a STRING with the file
        * if the file was successfully removed `X00` will contain `1`, if not `0`
    * `20`: make dictionary
        * `X00` contains a pointer of a STRING with the dictionary
        * if the dictionary was successfully created `X00` will contain `1`, if not `0`
    * `21`: remove dictionary
        * `X00` contains a pointer of a STRING with the dictionary
        * if the dictionary was successfully removed `X00` will contain `1`, if not `0`
        * if the dictionary is not empty this call will fail (and set `X00` to `0`)
    * `22`: to get the time in milliseconds
        * `X00` will contain the time in milliseconds or `-1` if not available
    * `23`: to wait the given time in nanoseconds
        * `X00` contain the number of nanoseconds to wait (only values from `0` to `999999999` are allowed)
        * `X01` contain the number of seconds to wait
        * `X00` and `X01` will contain the remaining time (`0` if it finished waiting)
        * `X02` will be `1` if the call was successfully and `0` if something went wrong
			* if `X02` is `1` the remaining time will always be `0`
			* if `X02` is `0` the remaining time will be greater `0`
        * `X00` will not be negative if the progress waited too long
    * `24`: socket client create
        * makes a new client socket
        * `X00` will be set to the SOCKET-ID or `-1` if the operation failed
    * `25`: socket client connect
        * `X00` points to the SOCKET-ID
        * `X01` points to a STRING, which names the host
        * `X02` contains the port
			* the port will be the normal number with the normal byte order
        * connects an client socket to the host on the port
        * `X01` will be set to the `1` on success and `0` on a fail
        * on success, the SOCKET-ID, can be used as a STREAM-ID
    * `26`: socket server create
        * `X00` contains the port
			* the port will be the normal number with the normal byte order
        * makes a new server socket
        * `X00` will be set to the SOCKET-ID or `-1` when the operation fails
    * `27`: socket server listens
        * `X00` contains the SOCKET-ID
        * `X01` contains the MAX_QUEUE length
        * let a server socket listen
        * `X01` will be set to `1` or `0` when the operation fails
    * `28`: socket server accept
        * `X00` contains the SOCKET-ID
        * let a server socket accept a client
        * this operation will block, until a client connects
        * `X01` will be set a new SOCKET-ID, which can be used as STREAM-ID, or `-1`
    * `29`: random
        * `X00` will be filled with random bits
	* `30`: memory copy
		* copies a block of memory
		* this function has undefined behavior if the two blocks overlap
		* `X00` points to the target memory block
		* `X01` points to the source memory block
		* `X02` has the length of bytes to bee copied
	* `31`: memory move
		* copies a block of memory
		* this function makes sure, that the original values of the source block are copied to the target block (even if the two block overlap)
		* `X00` points to the target memory block
		* `X01` points to the source memory block
		* `X02` has the length of bytes to bee copied
	* `32`: memory byte set
		* sets a memory block to the given byte-value
		* `X00` points to the block
		* `X01` the first byte contains the value to be written to each byte
		* `X02` contains the length in bytes
	* `33`: memory set
		* sets a memory block to the given int64-value
		* `X00` points to the block
		* `X01` contains the value to be written to each element
		* `X02` contains the count of elements to be set
	* `34`: string length
		* `X00` points to the STRING
		* `X00` will be set to the length of the string/ the (byte-)offset of the `'\0'` character
	* `35`: string to number
		* `X00` points to the STRING
		* `X01` points to the base of the number system
			* (for example `10` for the decimal system or `2` for the binary system)
		* `X00` will be set to the converted number
		* `X01` will point to the end of the number-STRING
			* this might be the `\0'` terminating character
		* if the STRING contains illegal characters or the base is not valid, the behavior is undefined
		* this function will ignore leading space characters
	* `36`: string to floating point number
		* `X00` points to the STRING
		* `X00` will be set to the converted number
		* `X01` will point to the end of the number-STRING
			* this might be the `\0'` terminating character
		* if the STRING contains illegal characters or the base is not valid, the behavior is undefined
		* this function will ignore leading space characters
	* `37`: number to string
		* `X00` is set to the number to convert
		* `X01` is points to the buffer to be filled with the number in a STRING format
		* `X02` contains the base of the number system
			* the minimum base is `2`
			* the maximum base is `36`
			* other values lead to undefined behavior
	* `38`: floating point number to string
		* `X00` is set to the number to convert
		* `X02` contains the maximum amount of digits to be used to represent the floating point number
		* `X01` is points to the buffer to be filled with the number in a STRING format
	* `39`: format string
		* `X00` is set to the STRING input
		* `X01` contains the buffer for the STRING output
			* if `X01` is set to `-1`, `X01` will be allocated to a memory block
				* the allocated memory block will be exact large enough to contain the formatted STRING
				* if there could not be allocated enough memory, `X01` will be set to `-1`
		* the register `X02..XNN` are for the formatting parameters
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
				* if the pointer is `-1` it will be converted to the STRING `"null"`
				* if not the pointer will be converted by placing a `"p-"` and then the pointer-number converted to a STRING using the hexadecimal number system
			* `%h`: the next argument contains a number, which should be converted to a STRING using the hexadecimal number system and than be inserted here
			* `%b`: the next argument contains a number, which should be converted to a STRING using the binary number system and than be inserted here
			* `%o`: the next argument contains a number, which should be converted to a STRING using the octal number system and than be inserted here

`PUSH <PARAM>`
* pushes the parameter to the stack
    * `SP <- SP + 1`
    * `[SP] <- p`
    * `IP <- IP + CMD_LEN`

`POP <NO_CONST_PARAM>`
* pops the highest value from the stack to the parameter
    * `p <- [SP]`
    * `SP <- SP - 1`
    * `IP <- IP + CMD_LEN`

`INC <NO_CONST_PARAM>`
* increments the param by one
    * `if p = MAX-VALUE`
        * `CARRY <- 1`
        * `ZERO <- 1`
    * `else`
        * `CARRY <- 0`
        * `ZERO <- 0`
    * `p <- p + 1`
    * `IP <- IP + CMD_LEN`

`DEC <NO_CONST_PARAM>`
* decrements the param by one
    * `if p = MIN-VALUE`
        * `CARRY <- 1`
        * `ZERO <- 1`
    * `else`
        * `CARRY <- 0`
        * `ZREO <- 0`
    * `p <- p - 1`
    * `IP <- IP + CMD_LEN`

## not (yet) there/supported
* execute other programs
* Multi-threading/-progressing
