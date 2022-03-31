|:	main file
|	
|	your main file
|:>
|> : load + jump to super main
	~IF (#~FOR_ME == 0)
		~READ_SYM "[THIS]" #MY_EXPORTS_ #ADD~FOR_ME 1 >
	~ELSE
		#MY_EXPORTS_main 0
		#MY_EXPORTS_SUPER_MAIN_NAME 0
	~ENDIF
	~READ_SYM "super-main.psc" #SUPER_MAIN_ #ADD~FOR_ME 1 >
	MOV X04, X00
	MOV X05, X01
	#REL_POS (MY_EXPORTS_SUPER_MAIN_NAME - --POS--)
	LEA X02, REL_POS
	#REL_POS ~DEL
	INT INT_LOAD_FILE
	#ZW_ADD (SUPER_MAIN_super_main - MY_EXPORTS_SUPER_MAIN_JMP_LEN)
	ADD X03, ZW_ADD
	#ZW_ADD ~DEL
	MOV X00, X04
	MOV X01, X05
	~IF #~FOR_ME
		#MAIN_JMP_POS --POS--
	~ENDIF
	MOV IP, X03
	~IF #~FOR_ME
		#EXP~SUPER_MAIN_JMP_LEN (--POS-- - MAIN_JMP_POS)
		#MAIN_JMP_POS ~DEL
		#EXP~SUPER_MAIN_NAME --POS--
	~ENDIF |> the name of the super main mashine file
: CHARS 'UTF-8' "super-main.pmc\0" >

|:	main:
|	X00 is set to the number of program arguments
|	X01 points to the array of STRING pointers, which point to the program arguments
|	return/exit code in X00
|	
|	this dummy main just prints its arguments (in separate lines) and exits with 0
|	you should definitely change this or why do you need/have the complex MAIN_INIT above?
|	or did you just disabled it?
|:>
#MAIN_ARG_MSG_POS --POS--
: CHARS 'UTF-8' "\n\t[%d] -> \0" >
|>#MAIN_ARG_MSG_LEN (--POS-- - MAIN_ARG_MSG_POS)
: > |> do not forget to align!
#MAIN_HELLO_MSG_POS --POS--
:
	CHARS 'UTF-8' (
		"hello I have been called with %s\n"
		"and my %d arguments are:\0"
		|> do not forget the \0 at the end of strings, when they are treated as strings and not as memory blocks with a known length
	)
>
|>#MAIN_HELLO_MSG_LEN (--POS-- - MAIN_HELLO_MSG_POS)
: > |> do not forget to align!
#MAIN_BYE_MSG_POS --POS--
: CHARS 'UTF-8' "\ngoodbye, thanks for calling me.\n" >
|> no need for an \0 because this is not used as a string
#MAIN_BYE_MSG_LEN (--POS-- - MAIN_BYE_MSG_POS)
: > |> do not forget the align!
#EXP~main --POS--
	MOV X03, 0 |> not needed when stack init ensures zero registers
	MOV X05, X00
	MOV X04, X01
	CALL print_hello_msg
	|:	X00: used for interrupts:
	|		string length interrupt: string input, length output
	|		write interrupt: stream-ID
	|		string format: unformatted string input
	|		string format: formatted string length output
	|	X01: used for interrupts:
	|		write int: length
	|		string format: formatted string output
	|	X02: write int: data
	|	X03: argument index (*8)
	|	X04: arguments
	|	X05: max index (*8)
	|:>
	|>MOV X06, 0 |> not needed, because of the constant at the start (MAIN_NEED_ZERO_REG_X06)
	@main_loop
		INC X07
		ADD X06, 8
		CMP X07, X05
		JMPGE loop_end
		CALL print_next_arg
		JMP main_loop
	@loop_end
@print_bye_msg
	MOV X00, STD_OUT
	MOV X01, MAIN_BYE_MSG_LEN
	#REL_POS (MAIN_BYE_MSG_POS - --POS--)
	LEA X02, REL_POS
	#REL_POS ~DEL
	INT INT_STREAMS_WRITE
	MOV X00, 0
	RET

@print_hello_msg
	MOV X02, [X04 + X03]
	MOV X03, X00
	DEC X03
	MOV X01, -1
	#REL_POS (MAIN_HELLO_MSG_POS - --POS--)
	LEA X00, REL_POS
	#REL_POS ~DEL
	INT INT_STRING_FORMAT
	MOV X02, X01
	MOV X01, X00
	MOV X00, STD_OUT
	INT INT_STREAMS_WRITE
	MOV X00, X02
	INT INT_MEMORY_FREE
	RET

@print_next_arg
	#REL_POS (MAIN_ARG_MSG_POS - --POS--)
	LEA X00, REL_POS
	#REL_POS ~DEL
	MOV X01, -1
	MOV X02, X07
	INT INT_STRING_FORMAT
	MOV X02, X01
	MOV X01, X00
	MOV X00, STD_OUT
	INT INT_STREAMS_WRITE
	MOV X02, [X04 + X06]
	MOV X00, X02
	INT INT_STRING_LENGTH
	MOV X01, X00
	MOV X00, STD_OUT
	INT INT_STREAMS_WRITE
	RET

|: |> if you want to call your main in an other file
	#MAIN_IS_IN_THIS_FILE 0
	~READ_SYM "super-main.psc" >
	~IF (#~FOR_ME == 0)
		~READ_SYM "[THIS]" #MY_EXPORTS_ #ADD~FOR_ME 1 >
	~ENDIF
	~IF (MAIN_IS_IN_THIS_FILE & #~FOR_ME)
		#EXP~MAIN_POS 0
	~ENDIF
	~IF MAIN_IS_IN_THIS_FILE
		#REL_POS (--POS-- - MY_EXPORTS_MAIN_POS)
		LEA X02, REL_POS
		#REL_POS ~DEL
	~ELSE
		~READ_SYM "<MAIN_FILE>" #MAIN_FILE_ #ADD~FOR_ME 1 >
		MOV X03, X00
		#REL_POS (MY_EXPORTS_MAIN_FILE_POS - --POS--)
		LEA X00, REL_POS
		#REL_POS ~DEL
		INT INT_LOAD_FILE
		MVAD X02, X00, MAIN_FILE_MAIN_POS
		MOV X00, X03
		~IF #~FOR_ME
			#MAIN_FILE_POS --POS--
		~ENDIF
		: CHARS 'UTF-8' "<MAIN_FILE>" >
	~ENDIF
	#ZW (super_main - 8)
	ADD X02, ZW
	#ZW ~DEL
	MOV IP, X02
:>
