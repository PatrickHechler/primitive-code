# primitive-code
a register based coding language with primitive operations

this is the assembler for the primitive VM

## CONSTANTS:

except for the `--POS--` constant all other constants can be overwritten and removed

* `--POS--` :                          the position from the begin of the next command
* `#INT-MEMORY` :                      0
* `#INT-ERRORS` :                      1
* `#INT-STREAMS` :                     2
* `#INT-TIME` :                        3
* `#INT-MEMORY-ALLOC` :                0
* `#INT-MEMORY-REALLOC` :              1
* `#INT-MEMORY-FREE` :                 2
* `#INT-ERRORS-EXIT` :                 0
* `#INT-ERRORS-UNKNOWN_COMMAND` :      1
* `#INT-STREAMS-GET_OUT` :             0
* `#INT-STREAMS-GET_LOG` :             1
* `#INT-STREAMS-GET_IN` :              2
* `#INT-STREAMS-NEW_IN` :              3
* `#INT-STREAMS-NEW_OUT` :             4
* `#INT-STREAMS-WRITE` :               5
* `#INT-STREAMS-READ` :                6
* `#INT-STREAMS-REM` :                 7
* `#INT-STREAMS-MK_DIR` :              8
* `#INT-STREAMS-REM_DIR` :             9
* `#INT-STREAMS-CLOSE_STREM` :         10
* `#INT-STREAMS-GET_POS` :             11
* `#INT-STREAMS-SET_POS` :             12
* `#INT-STREAMS-SET_POS_TO_END` :      13
* `#MAX-VALUE` :                   HEX-7FFFFFFFFFFFFFFF
* `#MIN-VALUE` :                  NHEX-8000000000000000

## STATUS:
* the status register has some flags which are initialized with random values
    * `LOWER`
    * `GREATHER`
    * `CARRY`
    * `ARITMETHIC_ERR`

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

`INT <PARAM>`
* calls the interrupt specified by the parameter
    * 0: memory management
        * use `AX` to specify the method of memory management
            * 0: allocate a memory-block
                * `BX` saves the size of the block
                * if the value of `BX` is `-1` after the call the memory-block could not be allocated
                * if the value of `BX` is not `-1`, `BX` points to the first element of the allocated memory-block
            * 1: reallocate a memory-block
                * `BX` points to the memory-block
                * `CX` saves the new size of the memory-block
                * if the value of `BX` is `-1` after the call the memory-block could not be reallocated, the old memory-block will remain valid and may be used and should be freed if it is not longer needed
                * if the value of `BX` is not `-1`, `BX` points to the first element of the allocated memory-block and the old memory-block was automatically freed, so it should not be used
            * 2: free a memory-block
                * `BX` points to the old memory-block
                * after this the memory-block should not be used
    * 1: errors
        * use `AX` to specify the error
            * 0: exit
                * use `BX` to specify the exit number of the progress
            * 1: unknown command
                * exits the progress with the exit number -2
    * 2: streams
        * use `AX` to specify
            * 0: get out stream
                * sets the `AX` value to the default out stream of this progress
            * 1: get log stream
                * sets the `AX` value to the default log stream of this progress
            * 2: get in stream
                * sets the `AX` value to the default in stream of this progress
            * 3: open new in stream
                * `BX` contains a pointer to the STRING, which refers to the file which should be read
                * opens a new in stream to the specified file
                * is successfully the STREAM-ID will be saved in the `AX` register, if not `AX` will contain `-1`
            * 4: open new out stream
                * `BX` contains a pointer to the STRING, which refers to the file which should be created
                * opens a new out stream to the specified file
                * if the file exist already it's contend will be overwritten
                * is successfully the STREAM-ID will be saved in the `AX` register, if not `AX` will contain `-1`
            * 5: write
                * `BX` contains the STREAM-ID
                * `CX` contains the number of elements to write
                * `DX` points to the elements to write
                * after execution `AX` will contain the number of written elements
            * 6: read
                * `BX` contains the STREAM-ID
                * `CX` contains the number of elements to read
                * `DX` points to the elements to read
                * after execution `AX` will contain the number of elements, which has been completely read.
                * after execution `BX` will contain the number bits which has been read in the last element the remaining bits of this element will be cleared.
            * 7: remove file
                * `BX` contains a pointer of a STRING with the file
                * if the file was successfully removed `AX` will contain `1`, if not `0`
            * 8: make dictionary
                * `BX` contains a pointer of a STRING with the dictionary
                * if the dictionary was successfully created `AX` will contain `1`, if not `0`
            * 9: remove dictionary
                * `BX` contains a pointer of a STRING with the dictionary
                * if the dictionary was successfully removed `AX` will contain `1`, if not `0`
                * if the dictionary is not empty this call will fail (and set `AX` to `0`)
            * 10: close stream
                * `BX` contains the STREAM-ID
                * if the stream was closed successfully `AX` will contain `1`, if not `0`
            * 11: get stream pos
                * `BX` contains the STREAM-ID
                * `AX` will contain the position of the stream or `-1` if something went wrong.
                * this will set `AX` to the stream position
				* if the stream-ID is the ID of a default stream the behavior is undefined.
            * 12: set stream pos
                * `BX` contains the STREAM-ID
                * `CX` contains the new stream position.
                * this will set the stream position to `CX`
				* if the stream-ID is the ID of a default stream the behavior is undefined.
            * 13: set stream to end
                * `BX` contains the STREAM-ID
                * this will set the stream position to the end
				* if the stream-ID is the ID of a default stream the behavior is undefined.
        * 3: time
            * 0: to get the time in milliseconds
                * `AX` will contain the time in milliseconds
            * 1: to wait the given time in milliseconds
                * `BX` contain the number of milliseconds to wait
                * `BX` will contain the number of remaining milliseconds (or `0` if it finished waiting)

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
* progress arguments
* (Multithreading)
