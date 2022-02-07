# primitive-code
a register based coding language with primitive operations

this is the assembler for the Primitive-Virtual-Machine

## Start

* A primitive-assembler-code file can be assembled to a primitive-machine-code file.
* A primitive-machine-code file can be executed with a primitive-virtual-machine.
    * the `AX` register will contain the number of arguments
    * the `BX` register will point to the arguments
    * the arguments will point to STRINGs
        * the first argument will be the program itself, all beyond will be the arguments of the program
        * example:
            * `my_program.pmc --example    value --other=val`
            * `AX          <- 4`
            * `[[BX]]      <- "my_program.pmc\0"`
            * `[[BX + 8]]  <- "--example\0"`
            * `[[BX + 16]] <- "value\0"`
            * `[[BX + 24]] <- "--other=val\0"`
    * the `INTCNT` register will be set to `#INTERRUPT_COUNT`
    * the interrupt-table of `INTP` will be initialized and every entry will be set to `-1`
        * so by default the default interrupts will be called, but they can be easily overwritten

## Primitive virtual machine

* the primitive virtual machine has the following registers:
    * `[A-D]X`
        * 4 * 64-bit
        * number registers, for free use
    * `IP`
        * `64-bit`
        * the instruction pointer points to the command to be executed
    * `STATUS`
        * `64-bit`
        * saves some results of operations
        * `HEX-0000000000000001` : `LOWER`: if on the las `CMP A, B` A was lower than B
        * `HEX-0000000000000002` : `GREATHER`: if on the las `CMP A, B` A was greater than B
        * `HEX-0000000000000004` : `CARRY`: if an overflow was detected
    * `INTCNT`
        * `64-bit`
        * saves the number of allowed interrupts (`0..(INTCNT-1)` are allowed
            * all other will call the `INT-ERRORS_ILLEGAL_INTERRUPT` interrupt
    * `INTP`
        * `64-bit`
        * points to the interrupt-table

## CONSTANTS:

except for the `--POS--` constant all other constants can be overwritten and removed

* `--POS--` :                           the position from the begin of the next command
* `#INT-ERRORS-UNKNOWN_COMMAND`         0
* `#INT-ERRORS_ILLEGAL_INTERRUPT`       1
* `#INT-ERRORS_ILLEGAL_MEMORY`          2
* `#INT-ERRORS_ARITHMETIC_ERROR`        3
* `#INT-EXIT`                           4
* `#INT-MEMORY-ALLOC`                   5
* `#INT-MEMORY-REALLOC`                 6
* `#INT-MEMORY-FREE`                    7
* `#INT-STREAMS-NEW_IN`                 8
* `#INT-STREAMS-NEW_OUT`                9
* `#INT-STREAMS-NEW_APPEND`             10
* `#INT_STREAMS-NEW_IN_OUT`             11
* `#INT-STREAMS-NEW_APPEND_IN_OUT       12
* `#INT-STREAMS-WRITE`                  13
* `#INT-STREAMS-READ`                   14
* `#INT-STREAMS-CLOSE_STREAM`           15
* `#INT-STREAMS-GET_POS`                16
* `#INT-STREAMS-SET_POS`                17
* `#INT-STREAMS-SET_POS_TO_END`         18
* `#INT-STREAMS-REM`                    19
* `#INT-STREAMS-MK_DIR`                 20
* `#INT-STREAMS-REM_DIR`                21
* `#INT-TIME-GET`                       22
* `#INT-TIME-WAIT`                      23
* `#INT-RANDOM`                         24
* `#INT-SOCKET-CLIENT-CREATE`           25
* `#INT-SOCKET-CLIENT-CONNECT`          26
* `#INT-SOCKET-SERVER-CREATE`           27
* `#INT-SOCKET-SERVER-LISTEN`           28
* `#INT-SOCKET-SERVER-ACCEPT`           29
* `#INTERRUPT_COUNT`                    30
* `#MAX-VALUE` :                    HEX-7FFFFFFFFFFFFFFF
* `#MIN-VALUE` :                   NHEX-8000000000000000
* `#STD-IN` :                            0
* `#STD-OUT` :                           1
* `#STD-LOG` :                           2

## STATUS:
* the status register has some flags which are initialized with random values
    * `LOWER`
    * `GREATHER`
    * `CARRY`

## STRINGS:
* if any command, function or whatever of primitive-code refers to STRING(s) this definition is used
* a string is an array of multiple characters of the UTF-8 encoding
* a string ends with a '\0' character

## COMMANDS:

`MOV <NO_CONST_PARAM> , <PARAM>`
* copies the value of the second parameter to the first parameter
    * `p1 <- p2`
    * `IP <- IP + CMD_LEN`

`ADD <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and stores the sum in the first parameter
    * `if ((p1 > 0) & (p2 > 0) & ((p1 + p2) < 0)) | ((p1 < 0) & (p2 < 0) & ((p1 + p2) > 0))`
        * `CARRY <- 1`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `CARRY <- 0`
        * `ARITMETHIC_ERR <- 0`
    * `p1 <- p1 + p2`
    * `IP <- IP + CMD_LEN`

`ADDC <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and the carry flag and stores the sum in the first parameter
    * `if ((p1 > 0) & ((p2 + CARRY) > 0) & ((p1 + p2 + CARRY) < 0)) | ((p1 < 0) & ((p2 + CARRY) < 0) & ((p1 + (p2 + CARRY)) > 0))`
        * `CARRY <- 1`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `CARRY <- 0`
        * `ARITMETHIC_ERR <- 0`
    * `p1 <- p1 + (p2 + CARRY)`
    * `IP <- IP + CMD_LEN`

`SUB <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter from the first parameter and stores the result in the first parameter
    * `if ((p1 > 0) & (p2 < 0) & ((p1 - p2) < 0)) | ((p1 < 0) & (p2 > 0) & ((p1 - p2) > 0))`
        * `CARRY <- 1`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `CARRY <- 0`
        * `ARITMETHIC_ERR <- 0`
    * `p1 <- p1 - p2`
    * `IP <- IP + CMD_LEN`

`SUBC <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter with the carry flag from the first parameter and stores the result in the first parameter
    * `if (p1 > 0) & ((p2 + CARRY) < 0) & ((p1 - (p2 + CARRY)) < 0)) | ((p1 < 0) & (p2 > 0) & ((p1 - (p2 + CARRY)) > 0))`
        * `CARRY <- 1`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `CARRY <- 0`
        * `ARITMETHIC_ERR <- 0`
    * `p1 <- p1 - (p2 + CARRY)`
    * `IP <- IP + CMD_LEN`

`MUL <NO_CONST_PARAM> , <PARAM>`
* multiplies the first parameter with the second and stores the result in the first parameter
    * `if ((p1 > 0) & (p2 > 0) & ((p1 + p2) < 0)) | ((p1 < 0) & (p2 < 0) & ((p1 + p2) > 0))`
        * `CARRY <- 1`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `CARRY <- 0`
        * `ARITMETHIC_ERR <- 0`
    * `p1 <- p1 * p2`
    * `IP <- IP + CMD_LEN`

`DIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* divides the first parameter with the second and stores the result in the first parameter and the reminder in the second parameter
    * `if p2 = 0`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `ARITMETHIC_ERR <- 0`
        * `p1 <- p1 / p2`
        * `p2 <- p1 % p2`
    * `IP <- IP + CMD_LEN`

`AND <NO_CONST_PARAM> , <PARAM>`
* uses the logical AND operator with the first and the second parameter and stores the result in the first parameter
    * `p1 <- p1 & p2`
    * `IP <- IP + CMD_LEN`

`OR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
    * `p1 <- p1 | p2`
    * `IP <- IP + CMD_LEN`

`XOR <NO_CONST_PARAM> , <PARAM>`
* uses the logical OR operator with the first and the second parameter and stores the result in the first parameter
    * `p1 <- p1 ^ p2`
    * `IP <- IP + CMD_LEN`

`LSH <NO_CONST_PARAM>`
* shifts bits of the parameter logically left
* this effectively multiplies the parameter with two
    * `if p1 = HEX-8000000000000000`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 * 2`
    * `IP <- IP + CMD_LEN`

`RLSH <NO_CONST_PARAM>`
* shifts bits of the parameter logically right
    * `if p1 = HEX-0000000000000001`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 >> 1`
    * `IP <- IP + CMD_LEN`

`RASH <NO_CONST_PARAM>`
* shifts bits of the parameter arithmetic right
* this effectively divides the parameter with two
    * `if p1 = HEX-0000000000000001`
        * `CARRY <- 1`
    * `else`
        * `CARRY <- 0`
    * `p1 <- p1 / 2`
    * `IP <- IP + CMD_LEN`

`NOT <NO_CONST_PARAM>`
* uses the logical NOT operator with every bit of the parameter and stores the result in the parameter
* this instruction works like `XOR p1, -1` 
    * `p1 <- ~ p1`
    * `IP <- IP + CMD_LEN`

`NEG <NO_CONST_PARAM>`
* uses the arithmetic negation operation with the parameter and stores the result in the parameter 
* this instruction works like `MUL p1, -1`
    * `if p1 = #MIN-VALUE`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `ARITMETHIC_ERR <- 0`
        * `p1 <- 0 - p1`
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

`JMPCS <LABEL>`
* sets the instruction pointer to position of the command after the label if the last carry flag is cleared
    * `if ! CARRY`
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
    * `IP <- CX
    * `AX <- [DX]`
    * `BX <- [DX + 8]`
    * `CX <- [DX + 16]`
    * `DX <- [DX + 24]`
    * `FREE DX`
        * this does not call the interrupt, which is used to free allocated memory, but is compatible to the interrupt, which is used for allocating memory

`INT <PARAM>`
* calls the interrupt specified by the parameter
    * `[MEM-ALLOC{size=32, TARGET=DX} + 24] <- DX`
        * allocate memory and save the `DX` register in the new allocated memory-block
        * this does not call the interrupt, which is used to allocate memory, but is compatible to the interrupt, which is used to free allocated memory
        * then let the `DX` point to the new allocated memory-block
            * but save the old value of the `DX` register in the new memory-block (+ 24)
    * `[DX + 16] <- CX`
    * `[DX + 8]  <- BX`
    * `[DX]      <- AX`
    * `CX        <- IP + CMD_LEN`
        * if the interrupt is automatically called, `CX` is set to the `IP` (`CX <- IP`)
            * so the program can retry its operation
            * (for example because of a division with zero or an illegal memory access)
    * `IP <- [INTS + (PARAM * 8)]`
* an interrupt can be overwritten:
    * with `GET_INTS` the interrupt-table can be received
    * to overwrite the interrupt `N`, write to `N * 8` the absolute position of address
    * example:
        * `PUSH AX` |> only needed when the value of `AX` should not be overwritten
        * `PUSH BX` |> only needed when the value of `BX` should not be overwritten
        * `GET_IP AX` |> this and the next command is not needed if the absolute position is already known
        * `ADD AX, #RELATIVE-POS-FROM-GET-TO-INTERRUPT`
        * `GET_INTS BX` |> unneeded if the interrupt table is already known
        * `MOV [BX + #OVERWRITE-INT_NUM-MULTIPLIED-WITH_8], AX`
        * `POP BX` |> only needed when the value of `BX` should not be overwritten
        * `POP AX` |> only needed when the value of `AX` should not be overwritten
* default interrupts:
    * `0`: unknown command
        * `AX` contains the illegal command
        * calls the exit interrupt with `9`
    * `1`: illegal interrupt
        * `AX` contains the number of the illegal interrupt
        * calls the exit interrupt with `7`
        * if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `8`
        * if the forbidden interrupt is the exit input, the program exits with `8`
    * `2`: illegal memory
        * calls the exit interrupt with `6`
    * `3`: arithmetic error
        * calls the exit interrupt with `5`
    * `4`: exit
        * use `AX` to specify the exit number of the progress
    * `5`: allocate a memory-block
        * `AX` saves the size of the block
        * if the value of `AX` is `-1` after the call the memory-block could not be allocated
        * if the value of `AX` is not `-1`, `AX` points to the first element of the allocated memory-block
    * `6`: reallocate a memory-block
        * `AX` points to the memory-block
        * `BX` saves the new size of the memory-block
        * if the value of `BX` is `-1` after the call the memory-block could not be reallocated, the old memory-block will remain valid and may be used and should be freed if it is not longer needed
        * if the value of `BX` is not `-1`, `BX` points to the first element of the allocated memory-block and the old memory-block was automatically freed, so it should not be used
    * `7`: free a memory-block
        * `AX` points to the old memory-block
        * after this the memory-block should not be used
    * `8`: open new in stream
        * `AX` contains a pointer to the STRING, which refers to the file which should be read
        * opens a new in stream to the specified file
        * is successfully the STREAM-ID will be saved in the `AX` register, if not `AX` will contain `-1`
        * output operations are not supported on the new stream
    * `9`: open new out stream
        * `AX` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `AX` register, if not `AX` will contain `-1`
        * input operations are not supported on the new stream
    * `10`: open new out, append stream
        * `AX` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `AX` register, if not `AX` will contain `-1`
    * `11`: open new in/out stream
        * `AX` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `AX` register, if not `AX` will contain `-1`
    * `12`: open new in/out, append stream
        * `AX` contains a pointer to the STRING, which refers to the file which should be created
        * opens a new out stream to the specified file
        * if the file exist already it's contend will be overwritten
        * is successfully the STREAM-ID will be saved in the `AX` register, if not `AX` will contain `-1`
    * `13`: write
        * `AX` contains the STREAM-ID
        * `BX` contains the number of elements to write
        * `CX` points to the elements to write
        * after execution `BX` will contain the number of written elements
    * `14`: read
        * `AX` contains the STREAM-ID
        * `BX` contains the number of elements to read
        * `CX` points to the elements to read
        * after execution `BX` will contain the number of elements, which has been read.
    * `15`: close stream
        * `AX` contains the STREAM-ID
        * if the stream was closed successfully `AX` will contain `1`, if not `0`
    * `16`: get stream pos
        * `AX` contains the STREAM-ID
        * `BX` will contain the position of the stream or `-1` if something went wrong.
        * if the stream-ID is the ID of a default or input stream the behavior is undefined.
    * `17`: set stream pos
        * `AX` contains the STREAM-ID
        * `BX` contains the position to be set.
        * if the stream-ID is the ID of a default stream the behavior is undefined.
        * `BX` will contain the new stream position.
    * `18`: set stream to end
        * `AX` contains the STREAM-ID
        * this will set the stream position to the end
        * `BX` will the new file pos or `-1` if something went wrong
        * if the stream-ID is the ID of a default or input stream the behavior is undefined.
    * `19`: remove file
        * `AX` contains a pointer of a STRING with the file
        * if the file was successfully removed `AX` will contain `1`, if not `0`
    * `20`: make dictionary
        * `AX` contains a pointer of a STRING with the dictionary
        * if the dictionary was successfully created `AX` will contain `1`, if not `0`
    * `21`: remove dictionary
        * `AX` contains a pointer of a STRING with the dictionary
        * if the dictionary was successfully removed `AX` will contain `1`, if not `0`
        * if the dictionary is not empty this call will fail (and set `AX` to `0`)
    * `22`: to get the time in nanoseconds
        * `AX` will contain the time in nanoseconds or `-1` if not available
    * `23`: to wait the given time in nanoseconds
        * `AX` contain the number of nanoseconds to wait
        * `AX` will contain the number of remaining nanoseconds (or `0` if it finished waiting) or `-1` on an error
        * `AX` will not be negative if the progress waited too long
    * `24`: random
        * `AX` will be filled with random bits
    * `25`: socket client create
        * makes a new client socket
        * `AX` will be set to the SOCKET-ID or `-1` if the operation failed
    * `26`: socket client connect
        * `AX` points to the SOCKET-ID
        * `BX` points to a STRING, which names the host
        * `CX` contains the port
        * connects an client socket to the host on the port
        * `BX` will be set to the `1` on success and `0` on a fail
        * on success, the SOCKET-ID, can be used as a STREAM-ID
    * `27`: socket server create
        * `AX` contains the port
        * makes a new server socket
        * `AX` will be set to the SOCKET-ID or `-1` when the operation fails
    * `28`: socket server listens
        * `AX` contains the SOCKET-ID
        * `BX` contains the MAX_QUEUE length
        * let a server socket listen
        * `BX` will be set to `1` or `0` when the operation fails
    * `29`: socket server accept
        * `AX` contains the SOCKET-ID
        * let a server socket accept a client
        * this operation will block, until a client connects
        * `BX` will be set a new SOCKET-ID, which can be used as STREAM-ID, or `-1`

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

`SET_INTS <PARAM>`
* sets the interrupt pointer to the parameter
    * `INTS <- p`

`SET_IP <PARAM>`
* sets the instruction pointer to the parameter
    * `IP <- p`

`SET_SP <PARAM>`
* sets the stack pointer to the parameter
    * `SP <- p`
    * `IP <- IP + CMD_LEN`

`GET_INTS <NO_CONST_PARAM>`
* copies the interrupt pointer to the parameter
    * `p <- INTS`
    * `IP <- IP + CMD_LEN`

`GET_IP <NO_CONST_PARAM>`
* copies the instruction pointer to the parameter
    * `p <- IP`
    * `IP <- IP + CMD_LEN`
* note, that the instruction pointer will modify as result of this command
    * the written value will be the instruction pointer directly before this command
    * so you can use `SET_IP` to land at this command (`GET_IP`)

`GET_SP <NO_CONST_PARAM>`
* copies the stack pointer to the parameter
    * `p <- SP`
    * `IP <- IP + CMD_LEN`

* `INC <NO_CONST_PARAM>`
    * `if p = MAX-VALUE`
        * `CARRY <- 1`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `CARRY <- 0`
        * `ARITMETHIC_ERR <- 0`
    * `p <- p + 1`
    * `IP <- IP + CMD_LEN`

* `DEC <NO_CONST_PARAM>`
    * `if p = MIN-VALUE`
        * `CARRY <- 1`
        * `ARITMETHIC_ERR <- 1`
    * `else`
        * `CARRY <- 0`
        * `ARITMETHIC_ERR <- 0`
    * `p <- p - 1`
    * `IP <- IP + CMD_LEN`

## TODO
* execute other programs
* Multi-threading/-progressing
