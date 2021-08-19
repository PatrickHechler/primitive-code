# primitive-code
a register based coding language with primitive operations

## CONSTANTS:

* `--POS--` = the position from the begin of the next command
* `#INT-MEMORY` = 0
* `#INT-MEMORY-ALLOC` = 1
* `#INT-MEMORY-REALLOC` = 2
* `#INT-MEMORY-FREE` = 3
* `#INT-ERRORS` = 1
* `#INT-ERRORS-EXIT` = 1
* `#INT-ERRORS-UNKNOWN_COMMAND` = 2

## COMMANDS:

`MOV <NO_CONST_PARAM> , <PARAM>`
* copies the value of the second parameter to the first parameter
    * `p1 <- p2`
    * `IP <- IP + CMD_LEN`

`ADD <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and stores the sum in the first parameter
    * `if ((p1 > 0) & (p2 > 0) & ((p1 + p2) < 0)) | ((p1 < 0) & (p2 < 0) & ((p1 + p2) > 0))`
        * `CARRY <- 1`
    * else
        * `CARRY <- 0`
    * `p1 <- p1 + p2`
    * `IP <- IP + CMD_LEN`

`SUB <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter from the first parameter and stores the result in the first parameter
    * `if ((p1 > 0) & (p2 < 0) & ((p1 + p2) < 0)) | ((p1 < 0) & (p2 > 0) & ((p1 + p2) > 0))`
        * `CARRY <- 1`
    * else
        * `CARRY <- 0`
    * `p1 <- p1 - p2`
    * `IP <- IP + CMD_LEN`

`MUL <NO_CONST_PARAM> , <PARAM>`
* multiplies the first parameter with the second and stores the result in the first parameter
    * `if ((p1 > 0) & (p2 > 0) & ((p1 + p2) < 0)) | ((p1 < 0) & (p2 < 0) & ((p1 + p2) > 0))`
        * `CARRY <- 1`
    * else
        * `CARRY <- 0`
    * `p1 <- p1 * p2`
    * `IP <- IP + CMD_LEN`

`DIV <NO_CONST_PARAM> , <NO_CONST_PARAM>`
* divides the first parameter with the second and stores the result in the first parameter and the reminder in the second parameter
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
    * `if p1 = HEX-8000000000000000
        * `CARRY <- 1`
    * else
        * `CARRY <- 0`
    * `p1 <- p1 * 2`
    * `IP <- IP + CMD_LEN`

`RLSH <NO_CONST_PARAM>`
* shifts bits of the parameter logically right
    * `if p1 = HEX-0000000000000001
        * `CARRY <- 1`
    * else
        * `CARRY <- 0`
    * `p1 <- p1 >> 1`
    * `IP <- IP + CMD_LEN`

`RASH <NO_CONST_PARAM>`
* shifts bits of the parameter arithmetic right
* this effectively divides the parameter with two
    * `if p1 = HEX-0000000000000001
        * `CARRY <- 1`
    * else
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
        * LOWER <- 1
        * GREATHER <- 0
    * `if p1 > p2`
        * LOWER <- 0
        * GREATHER <- 1
    * `if p1 = p2`
        * LOWER <- 0
        * GREATHER <- 0
    * `IP <- IP + CMD_LEN`

`RET`
* sets the instruction pointer to the position which was secured in the stack
    * `IP <- [SP]`
    * `SP <- SP - 1`

`INT <PARAM>`
* calls the interrupt specified by the parameter
    * 0: memory management
        * use `AX` to specify the method of memory management
                1. allocate a memory-block
                    * `BX` saves the size of the block
                    * if the value of `BX` is `-1` after the call the memory-block could not be allocated
                    * if the value of `BX` is not `-1`, `BX` points to the first element of the allocated memory-block
                2. reallocate a memory-block
                    * `BX` points to the memory-block
                    * `CX` saves the new size of the memory-block
                    * if the value of `BX` is `-1` after the call the memory-block could not be reallocated, the old memory-block will remain valid and may be used and should be freed if it is not longer needed
                    * if the value of `BX` is not `-1`, `BX` points to the first element of the allocated memory-block and the old memory-block was automatically freed, so it should not be used
                3. free a memory-block
                    * `BX` points to the old memory-block
                    * after this the memory-block should not be used
    * 1: errors
        * use `AX` to specify the error
            1. exit
                * use `BX` to specify the exit number of the progress
            2. unknown command
                * exits the progress with the exit number -2

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

`SET_IP <PARAM>`
* sets the instruction pointer to the parameter
    * `IP <- p`

`SET_SP <PARAM>`
* sets the stack pointer to the parameter
    * `SP <- p`
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
    * `p <- p + 1`
    * `IP <- IP + CMD_LEN`

* `DEC <NO_CONST_PARAM>`
    * `p <- p - 1`
    * `IP <- IP + CMD_LEN`

## TODO:
* carry
    * `ADDC` add with carry
    * `SUBC` subtract with carry
* user specified interrupts
    * `IRET` return from custom interrupts
    * set interrupt table
    * get interrupt table
    * set interrupt number (int-table 'length')
    * get interrupt number (int-table 'length')
* out + log/err + in stream + file streams
    * functions to get out, log/err, in
    * functions to get file streams
    * functions to write/read from streams or to set/get the pos
