|: 
| Hello World code which adds the values of AX and BX and returns the result in CX
| between that it prints '!!hello world!!\n' on the default out stream 
:>
JMP START
:
	#HELLO_WORLD-STR --POS--
	"!!Hello world!!\n"
	#HELLO_WORLD-END --POS--
>
@START
ADD AX, BX
MOV DX, AX
MOV BX, 2
MOV AX, #INT-MEMORY-ALLOC
INT #INT-MEMORY
DEC BX
SET_SP BX
PUSH DX
MOV AX, #INT-STREAMS-GET_OUT
INT #INT-STREAMS
MOV BX, AX
#GET_IP-POS --POS--
GET_IP DX
SUB DX, #GET_IP-POS
ADD DX, #HELLO_WORLD-STR
MOV CX, #HELLO_WORLD-END
SUB CX, #HELLO_WORLD-STR
MOV AX, #INT-STREAMS-WRITE
INT #INT-STREAMS
POP CX
MOV BX, 0 |> EXIT-SUCCESS
MOV AX, #INT-ERRORS-EXIT
INT #INT-ERRORS
