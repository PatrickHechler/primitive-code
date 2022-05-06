|> hello world code, which prints 'hello world\n'
#hw_pos (13 * 8) |> manually calculated
#hw_len  12 |> manually calculated
	MOV X00, STD_OUT
	MOV X01, hw_len
#hw_rel_pos  (--POS-- - hw_pos)
	MOV X02, IP
	SUB X02, hw_rel_pos
	INT INT_STREAMS_WRITE
	MOV X00, 0
	INT INT_EXIT
:CHARS 'ASCII' "hello world\n"> |> UTF-8 supports ASCII

|: |> alternative where the data is at the start of the program
|	JMP main
|	#hw_pos --POS--
|:CHARS 'UTF-8' "hello world\n">
|	#hw_len  (--POS-- - hw_pos)
|@main
|	MOV X00, STD_OUT
|	MOV X01, hw_len
|	#hw_rel_pos  (--POS-- - #hw_pos)
|	MOV X02, IP
|	SUB X02, hw_rel_pos
|	INT INT_STREAMS_WRITE
|	MOV X00, 0
|	INT INT_EXIT
|:>
