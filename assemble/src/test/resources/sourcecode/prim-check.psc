	CMP SP, -1
	JMPNE skip_stack_init
	MOV X00, 1024
	INT INT_MEMORY_ALLOC
	CMP X00, -1
	JMPEQ error_1
	MOV SP, X00
@skip_stack_init
	CALL main
	INT INT_EXIT
#file_POS --POS--
	: CHARS 'UTF-8' "./input/in2.data\0" >
	: > |> for align
#file_out_POS --POS--
	: CHARS 'UTF-8' "./output/out2-pc.data\0" >
#file_length 8
|:#file_numbers (file_length / 8)
~IF (file_length % 8 != 0)
	~ERROR {"file_length is not dividable with 8! file_length=" file_length }
~ENDIF:>
@main
	MOV X00, file_length
	INT INT_MEMORY_ALLOC
	CMP X00, -1
	JMPEQ error_2
	MOV X02, X00
	#main_GET_IP_POS (--POS-- - file_POS)
	MOV X00, IP
	SUB X00, main_GET_IP_POS
	#main_GET_IP_POS ~DEL
	INT INT_STREAMS_NEW_IN
	CMP X00, -1
	JMPEQ error_3
	MOV X01, file_length
	INT INT_STREAMS_READ
	CMP X01, file_length
	JMPNE error_4
	MOV X05, X02
|:	MOV X06, 0 :>
	|:	X00: for is_prim: potential_prim
	|	X01: for is_prim: result
	|	X02: for is_prim: free use/?
	|	X03: for is_prim: free use/?
	|	X04: for is_prim: free use/?
	:>
|:@main_find_prims_loop :>
	MOV X00, [X05]
	|:	X00: potential prim num
	|
	|	X00 <- ?
	|	X01 <- ?
	|	if prim:
	|		X02 <- NDEC-1
	|	if no prim:
	|		X02 <- mod 0 number |> does not have to be a prim
	|	X03 <- ?
	|	X04 <- ?
	:>
	CALL is_prim
	MOV [X05], X02
|:	ADD X05, 8 
	INC X06
	CMP X06, file_numbers
	JMPLT main_find_prims_loop
	SUB X05, file_length :>
	#main_GET_IP_POS (--POS-- - file_out_POS)
	MOV X00, IP
	SUB X00, main_GET_IP_POS
	#main_GET_IP_POS ~DEL
	INT INT_STREAMS_NEW_OUT
	CMP X00, -1
	JMPEQ error_5
	MOV X02, X05
	MOV X01, file_length
	INT INT_STREAMS_WRITE
	CMP X01, file_length
	JMPNE error_6
	MOV X00, 26
	INT INT_MEMORY_ALLOC
	CMP X00, -1
	JMPEQ return
	|> the result has already been saved in the out file, so it is no error when it can not be printed
	MOV X01, X00
	MOV X00, [X05]
	MOV X02, 16
	INT INT_FUNC_NUMBER_TO_STRING
	MOV [X01 + X00], HEX-0A
	MOV X02, X01
	MOV X01, X00
	INC X01
	MOV X00, STD_OUT
	INT INT_STREAMS_WRITE
	MOV X00, 0 |> EXIT_SUCCESS
	|> the result has already been saved in the out file, so it is no error when it can not be printed
	RET |> main gets called, main caller exits

#error_msg_POS --POS--
	:
		CHARS 'UTF-8' (
			"An error occurred\n"
			"I will exit the program now (with exit code 1) (@error_%d)\n"
			"SP=%p\n"
			"STATUS=%h\n"
			"X00=%d\n"
			"X01=%d\n"
			"X02=%d\n"
			"X04=%d\n"
			"X05=%d\n"
			"XFA=%h\n"
			"\0"
		)
	>
	: >
@error_1
	MOV X10, 1
	JMP error
@error_2
	MOV X10, 2
	JMP error
@error_3
	MOV X10, 3
	JMP error
@error_4
	MOV X10, 4
	JMP error
@error_5
	MOV X10, 5
	JMP error
@error_6
	MOV X10, 6
@error
	MOV X0B, XFA
	MOV X0A, X05
	MOV X09, X04
	MOV X08, X03
	MOV X07, X02
	MOV X06, X01
	MOV X05, X00
	MOV X04, STATUS
	MOV X03, SP
	MOV X02, X10
	MOV X01, -1
	#error_msg_POS_dif (--POS-- - error_msg_POS)
	MOV X00, IP
	SUB X00, error_msg_POS_dif
	#error_msg_POS_dif ~DEL
	INT INT_FUNC_STRING_FORMAT
	MOV X02, X01
	MOV X01, X00
	MOV X00, STD_LOG
	INT INT_STREAMS_WRITE
	INT INT_STREAMS_SYNC_STREAM |> just make sure the error gets printed, before the program exits
	MOV X00, 1
	INT INT_EXIT

|:
|	X00 <- X00
|	X01 <- wurzel(X00)
|	X00 has a natural number wurzel:
|		X02 <- X01 * X01
|	X00 has no natural number wurzel:
|		X02 <- (X01 + 1) * (X01 + 1)
:>
@wurzel
	MOV X01, 0
@wurzel_loop
	MOV X02, X01
	MUL X02, X02
	CMP X00, X02
	JMPEQ return
	JMPLT wurzel_ret_sub_1
	INC X01
	JMP wurzel_loop
@wurzel_ret_sub_1
	DEC X01
@return
	RET

|:
|	X00: potential prim num
|
|	X00 <- X00
|	X01 <- ?
|	if prim:
|		X02 <- NDEC-1
|	if no prim:
|		X02 <- mod 0 number |> does not have to be a prim
|	X03 <- ?
|	X04 <- ?
:>
@is_prim
	CALL wurzel
	CMP X00, X02
	JMPEQ is_prim_X01Hit
	MOV X02, X00
	AND X02, 1
	JMPZS is_prim_check_2
	MOV X02, 3 |> init check
	|:	X00:			MOD
	|	X01:			wurzel
	|	X02:			check
	|	X03:			DIV
	|	X04:			potential_prim
	:>
@is_prim_loop
	CMP X02, X01
	JMPGE is_prim_ret_N1
	MOV X03, X04
	MOV X00, X02
	DIV X03, X00
	CMP X00, 0
	JMPEQ return |> X02 is already set to the mod zero num
	ADD X02, 2
	JMP is_prim_loop
@is_prim_X01Hit
	MOV X02, X01
	RET
@is_prim_check_2
	CMP X00, 2
	JMPEQ is_prim_ret_N1
	MOV X02, 2
	RET
@is_prim_ret_N1
	MOV X02, -1
	RET
