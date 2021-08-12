# primitive-code
a register based coding language with primitive operations

### COMMANDS:

`MOV <NO_CONST_PARAM> , <PARAM>`
* copies the value of the second parameter to the first parameter
    * `p1 <- p2
    * `IP <- IP + CMD_LEN`

`ADD <NO_CONST_PARAM> , <PARAM>`
* adds the values of both parameters and stores the sum in the first parameter
    * `p1 <- p1 + p2`
    * `IP <- IP + CMD_LEN`

`SUB <NO_CONST_PARAM> , <PARAM>`
* subtracts the second parameter from the first parameter and stores the result in the first parameter
    * `p1 <- p1 - p2`
    * `IP <- IP + CMD_LEN`

`MUL <NO_CONST_PARAM> , <PARAM>`
* multiplies the first parameter with the second and stores the result in the first parameter
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

`NOT <NO_CONST_PARAM>`
* uses the logical NOT operator with the parameter and stores the result in the parameter
    * `p1 <- ! p1`
    * `IP <- IP + CMD_LEN`

`NEG <NO_CONST_PARAM>`
* uses the arithmetic negation operation with the parameter and stores the result in the parameter 
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

`CALL <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
    * `[SP] <- IP`
    * `SP <- SP + 1`
    * `IP <- IP - --POS-- + LABEL`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CALLEQ <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
	* `if ( ! GREATHER) & ( ! LOWER)`
        * `[SP] <- IP`
        * `SP <- SP + 1`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
	    * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CALLNE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
	* `if GREATHER | LOWER`
        * `[SP] <- IP`
        * `SP <- SP + 1`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
	    * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CALLGT <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
	* `if GREATHER`
        * `[SP] <- IP`
        * `SP <- SP + 1`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
	    * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CALLGE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
	* `if ! LOWER`
        * `[SP] <- IP`
        * `SP <- SP + 1`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
	    * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CALLLO <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
	* `if ! LOWER`
        * `[SP] <- IP`
        * `SP <- SP + 1`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
	    * `IP <- IP + CMD_LEN`
    * note that all jumps and calls are relative, so it does not matter if the code was loaded to the memory address 0 or not

`CALLLE <LABEL>`
* sets the instruction pointer to position of the command after the label if the last compare result was not greater
	* `if ! GREATHER`
        * `[SP] <- IP`
        * `SP <- SP + 1`
        * `IP <- IP - --POS-- + LABEL`
    * `else`
	    * `IP <- IP + CMD_LEN`
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
    * 0: use to allocate a block of memory
	    * use `AX` to specify the size of the block
		* `AX` will point to the allocated block of memory or it will contain the value `-1` if the block could not be allocated
	* 1:

`PUSH`

`POP`

`SET_IP`

### TODO:
SHIFT operations
