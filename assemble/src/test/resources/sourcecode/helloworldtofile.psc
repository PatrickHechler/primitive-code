|:
| Hello World code which prints '!!hello world!!\n' to a file
:>
JMP START
:
	#HELLO_WORLD-STR --POS--
	"hello file world"
	#HELLO_WORLD-END --POS--


	#OUT-FILE-STRING --POS--
	|> 67 |> strlen
	|> CHARS 'UTF-16LE' "C:\\Users\\Patrick\\git\\primitive-code\\assemble\\target\\testout\\out.txt"
	16 |> strlen
	CHARS 'UTF-16LE' ".\\output\\out.txt"
	
>
@START
#GET_IP-POS --POS--
GET_IP BX
SUB BX, #GET_IP-POS
#GET_IP-POS ~DEL
ADD BX, #OUT-FILE-STRING
MOV AX, #INT-STREAMS-NEW_OUT
INT #INT-STREAMS

MOV BX, AX
#GET_IP-POS --POS--
GET_IP DX
SUB DX, #GET_IP-POS
#GET_IP-POS ~DEL
ADD DX, #HELLO_WORLD-STR
MOV CX, #HELLO_WORLD-END
SUB CX, #HELLO_WORLD-STR
MOV AX, #INT-STREAMS-WRITE
INT #INT-STREAMS

|> just some stream set/get position (useless/unused) operations

MOV AX, #INT-STREAMS-GET_POS
INT #INT-STREAMS
MOV CX, AX
MOV AX, #INT-STREAMS-SET_POS
INT #INT-STREAMS
MOV AX, #INT-STREAMS-SET_POS_TO_END
INT #INT-STREAMS
MOV AX, #INT-STREAMS-CLOSE_STREAM
INT #INT-STREAMS

MOV BX, 0 |> EXIT-SUCCESS
MOV AX, #INT-ERRORS-EXIT
INT #INT-ERRORS
