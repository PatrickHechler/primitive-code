# primitive-code
a register based coding language with primitive operations

this is the assembler for the primitive VM

## Start

* A primitive-asm-code file can be assembled to a primitive-machine-code (.pmc) file.
* A pmc-file can be executed with a primitive-virtual-machine.
    * the `AX` register will contain the number of arguments
    * the `BX` register will point to the argument pointers 
    * the argument pointers will point to STRINGs

## CONSTANTS:

except for the `--POS--` constant all other constants can be overwritten and removed

* `--POS--` :                           the position from the begin of the next command
* `#INT_ERRORS_UNKNOWN_COMMAND` :        0
* `#INT_ERRORS_ILLEGAL_INTERRUPT` :      1
* `#INT_ERRORS_ILLEGAL_MEMORY` :         2
* `#INT_ERRORS_ARITHMETIC_ERROR` :       3
* `#INT_EXIT` :                          4
* `#INT_MEMORY_ALLOC` :                  5
* `#INT_MEMORY_REALLOC` :                6
* `#INT_MEMORY_FREE` :                   7
* `#INT_STREAMS_GET_STD_OUT` :           8
* `#INT_STREAMS_GET_STD_LOG` :           9
* `#INT_STREAMS_GET_STD_IN` :           10
* `#INT_STREAMS_NEW_IN` :               11
* `#INT_STREAMS_NEW_OUT` :              12
* `#INT_STREAMS_WRITE` :                13
* `#INT_STREAMS_READ` :                 14
* `#INT_STREAMS_CLOSE_STREAM` :         15
* `#INT_STREAMS_GET_POS` :              16
* `#INT_STREAMS_SET_POS` :              17
* `#INT_STREAMS_SET_POS_TO_END` :       18
* `#INT_FS_REM` :                       19
* `#INT_FS_MK_DIR` :                    20
* `#INT_FS_REM_DIR` :                   21
* `#INT_TIME_GET` :                     22
* `#INT_TIME_WAIT` :                    23
* `#INT_RANDOM` :                       24
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
* a string is a structure which contains multiple characters of the UTF-16 encoding
* the first element of the string is the number of characters it contains 
* all following elements contains four characters
    * the last element may contain less characters (it contains the number of the first element modulo four characters)

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

`JMPLO <LABEL>`
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
* an interrupt can be overwritten:
    * `MEM-ALLOC{size=32, DX=Pointer} <- DX`
	    * this does not call the interrupt, which is used to allocate memory, but is compatible to the interrupt, which is used to free allocated memory
	    * allocate memory and save the `DX` register in the new allocated memory-block
		* then let the `DX` point to the new allocated memory-block
	* `[DX]      <- DX`
	* `[DX + 8]  <- CX`
	* `[DX + 16] <- BX`
	* `[DX + 24] <- AX`
	* `CX        <- IP`
	* `IP        <- [INT_TABLE + (PARAM * 8)]`
* calls the interrupt specified by the parameter
	* `0`: unknown command
		* `AX` contains the illegal command
		* calls the exit interrupt with `-2`
	* `1`: illegal interrupt
		* `AX` contains the number of the illegal interrupt
		* calls the exit interrupt with `-2`
		* if this interrupt is tried to bee called, but it is forbidden to call this interrupt, the program exits with `-3`
		* if the forbidden interrupt is the exit input, the program exits with `-3`
	* `2`: illegal memory
		* calls the exit interrupt with `-2`
	* `3`: arithmetic error
		* calls the exit interrupt with `-2`
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
		* after execution `AX` will contain the number of written elements
	* `14`: read
		* `AX` contains the STREAM-ID
		* `BX` contains the number of elements to read
		* `CX` points to the elements to read
		* after execution `AX` will contain the number of elements, which has been completely read.
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
* program arguments
* (Multi-threading/-progressing)
