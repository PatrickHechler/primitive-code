|:	standalone main file
|	
|	your standalone main
|:>
#MAIN_NEED_ZERO_REG_X06 1
#MAIN_NEED_ZERO_REG_X07 1

#DO_MAIN_INIT 1
#REMEMBER_OLD_POS 0

~IF (#~FOR_ME == 0)
	~READ_SYM "[THIS]" #MY_EXPORTS_ #ADD~FOR_ME 1 >
~ENDIF

~IF (#~DO_MAIN_INIT == 0)
	#DO_MAIN_INIT 0
~ENDIF
~IF DO_MAIN_INIT
	|:	If your main wants all register to be 
	|	zero at the start (expect of course X00 
	|	and X01 you can define MAIN_NEEDS_ZERO_REGS
	|	with a nonzero value.
	|
	|	If your main needs only some registers
	|	to be zero, you can define
	|	MAIN_NEEDS_ZERO_REG_XNN to a nonzero value
	|	where the NN at the start is the number of
	|	the register.
	|	When you do this for the register X00 
	|	or X01 this will cause an error.
	|	When you do this for a register (or
	|	multiple registers) which is not changed
	|	it will be ignored.
	|:>
	~IF (#~MAIN_NEEDS_ZERO_REG_X00 != 0)
		~IF MAIN_NEEDS_ZERO_REG_X00
			~ERROR { "X00 is for the program argument count!" }
		~ENDIF
	~ENDIF
	~IF (#~MAIN_NEEDS_ZERO_REG_X01 != 0)
		~IF MAIN_NEEDS_ZERO_REG_X01
			~ERROR { "X00 is for the program argument values!" }
		~ENDIF
	~ENDIF
	~IF (#~MAIN_NEEDS_ZERO_REGS == 0)
		#MAIN_NEEDS_ZERO_REGS 0
	~ENDIF
	~IF (#~MAIN_NEEDS_ZERO_REG_X02 == 0)
		#MAIN_NEEDS_ZERO_REG_X02 0
	~ENDIF
	JMP super_main
	~IF (#~REMEMBER_OLD_POS == 0)
		#REMEMBER_OLD_POS 0
	~ENDIF
	~IF REMEMBER_OLD_POS
 		|:	if you do not care about the old pointer you can remove it
 		|	by defining the constant REMEMBER_OLD_POS to an zero value.
		|:>
		#OLD_INT_ILLEGAL_MEM_POS --POS--
		: -1 > |> old illegal interrupt
	~ENDIF
	#STAK_MEMORY_POS --POS--
	: -1 > |> stack memory pointer
	#TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY (INT_ERRORS_ILLEGAL_MEMORY * 8)
	#GROW_STACK_MUL 1.25
	#CALL_COMMAND HEX-20
	#PUSH_COMMAND HEX-24
	#INT_ILLEGAL_MEMORY_POS --POS--
		MOV X00, [X0A]
		AND X00, HEX-FF
		CMP X00, CALL_COMMAND
		JMPEQ do_stack_grow
		CMP X00, PUSH_COMMAND
		JMPNE call_old_or_default_interrupt
		@do_stack_grow
			|:	calculate the actual size of the stack
			|	(may even be bigger than the allocated memory block,
			|	so it is useless to save the allocated size)
			|	because of that the memory can't even just be 
			|	reallocated
			|:>
			MOV X01, SP
			#REL_POS (STAK_MEMORY_POS - --POS--)
			SUB X01, [IP + REL_POS]
			#REL_POS ~DEL
			MOV X02, X01
			|> calculate the new size of the stack
			NTFP X01
			MULFP X01, GROW_STACK_MUL
			FPTN X01
			|> allocate memory for the new stack
			MOV X00, X01
			INT INT_MEMORY_ALLOC
			CMP X00, -1
			JMPEQ error_stack
			|:	SP:  old stack pointer
			|	X00: new stack memory
			|	X01: new stack size
			|	X02: actual stack size
			|	X03: new stack size
			|:>
			|> MOV X00, X00
			#REL_POS (STAK_MEMORY_POS - --POS--)
			MOV X01, [IP + REL_POS]
			#REL_POS ~DEL
			|> MOV X02, X02
			INT INT_MEMORY_COPY
			|> set the new stack pointer
			MOV SP, X00
			ADD SP, X02
			|> free the old memory pointer
			MOV X00, X01
			INT INT_MEMORY_FREE
			IRET
		@call_old_or_default_interrupt
			~IF REMEMBER_OLD_POS
				#REL_POS (OLD_INT_ILLEGAL_MEM_POS - --POS--)
				MOV [INTP + TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY], [IP + REL_POS]
				#REL_POS ~DEL
			~ELSE
				|> else call just the default interrupt
				MOV [INTP + TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY], -1
			~ENDIF |> REMEMBER_OLD_POS
			INT INT_ERRORS_ILLEGAL_MEMORY
			#REL_POS (INT_ILLEGAL_MEMORY_POS - --POS--)
			LEA [INTP + TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY], REL_POS
			#REL_POS ~DEL
			IRET
	#CALL_COMMAND ~DEL
	#PUSH_COMMAND ~DEL
	#GROW_STACK_MUL ~DEL
	#INIT_STACK_SIZE 1024
	@super_main
		CMP SP, -1
		JMPNE make_stack_growable
			MOV X02, X00
			MOV X00, INIT_STACK_SIZE
			INT INT_MEMORY_ALLOC
			CMP X00, -1
			JMPEQ error_stack
			MOV SP, X00
			MOV X00, X02
		@make_stack_growable
			~IF REMEMBER_OLD_POS
				#REL_POS (OLD_INT_ILLEGAL_MEM_POS - --POS--)
				|> save the old interrupt, so if the illegal memory interrupt is not called because of an stack overflow, the old can be called
				|> I know that the old interrupt can only not be -1 if this is not the executed program, but something different loads this program
				MOV [IP + REL_POS], [INTP + TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY]
				#REL_POS ~DEL
			~ENDIF
			#REL_POS (INT_ILLEGAL_MEMORY_POS - --POS--)
			LEA [INTP + TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY], REL_POS
			#REL_POS ~DEL
			#REL_POS (STAK_MEMORY_POS - --POS--)
			MOV [IP + REL_POS], SP
			#REL_POS ~DEL
			~IF (MAIN_NEEDS_ZERO_REG_X02 | MAIN_NEEDS_ZERO_REGS)
				MOV X02, 0
			~ENDIF
			CALL main
			INT INT_EXIT
	#INIT_STACK_SIZE ~DEL
	|>#OLD_INT_ILLEGAL_MEM_POS ~DEL
	#INT_ILLEGAL_MEMORY_POS ~DEL
	#TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY ~DEL
	
	|:	if you do not want that the message should be printed you can just comment the message out
	|	if you do that the length will be automatically set to zero, which also leads to the away filtering of the message printing code block
	|		the code block will only be assembled when the length is greater than zero
	|		and only when both constants are defined
	|	if you comment at least one of the two constants out the code block will also be filtered away
	|:> 
	#ERROR_STACK_MSG_POS --POS--
	|> write here your error message when something goes wrong with the stack
	: CHARS 'UTF-8' "error by working with the stack!\n" >
	#ERROR_STACK_MSG_LEN (--POS-- - ERROR_STACK_MSG_POS)
	@error_stack
		~IF (#~ERROR_STACK_MSG_POS & #~ERROR_STACK_MSG_LEN)
			~IF (ERROR_STACK_MSG_LEN > 0)
				MOV X00, STD_LOG
				MOV X01, ERROR_STACK_MSG_LEN
				#REL_POS (ERROR_STACK_MSG_POS - --POS--)
				LEA X02, REL_POS
				#REL_POS ~DEL
				INT INT_STREAMS_WRITE
			~ENDIF
		~ENDIF
		MOV X00, 1
		INT INT_EXIT
	#ERROR_STACK_MSG_POS ~DEL
	#ERROR_STACK_MSG_LEN ~DEL
~ENDIF |> IF DO_MAIN_INIT

|:	please note, that the main init overrides the illegal memory interrupt
|	to allow a dynamic growing stack.
|	if you want to overwrite this interrupt too, you can either just set the
|	position:
	#VERY_OLD_ILLEGAL_INTERRUPT_POS --POS--
		: -1 >	|> if you want/need to call the old interrupt
				|> For example my dynamic stack illegal memory interrupt calls 
				|> the old interrupt, when it was not called, because of an
				|> stack overflow.
	#MY_ILLEGAL_MEMORY_INTERRUPT --POS--
		|> do you interrupt stuff
		IRET
		
	@initmyinterrupt
		#REL_POS1 (MY_ILLEGAL_MEMORY_INTERRUPT - --POS--)
		#REL_POS2 (OLD_INT_ILLEGAL_MEM_POS - --POS--)
		#REL_POS3 (VERY_OLD_ILLEGAL_INTERRUPT_POS - --POS--)
		MOV [IP + REL_POS3], [IP + REL_POS2]
		LEA [IP + REL_POS2], REL_POS1
		#REL_POS3 ~DEL
		#REL_POS2 ~DEL
		#REL_POS1 ~DEL
|	or you modify the interrupt and put your code at the 
|	@call_old_or_default_interrupt label.
|:>

|:	main:
|	X00 is set to the number of program arguments
|	X01 points to the array of STRING pointers, which point to the program arguments
|	return/exit code in X00
|	
|	this dummy main just prints its arguments (in separate lines) and exits with 0
|	you should definitely change this or why do you need/have the complex MAIN_INIT above?
|	or did you just disabled it?
|:>
@main
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
	MOV X01, MY_EXPORTS_MAIN_BYE_MSG_LEN
	#REL_POS (MY_EXPORTS_MAIN_BYE_MSG_POS - --POS--)
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
	#REL_POS (MY_EXPORTS_MAIN_HELLO_MSG_POS - --POS--)
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
	#REL_POS (MY_EXPORTS_MAIN_ARG_MSG_POS - --POS--)
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

~IF #~FOR_ME
	#EXP~MAIN_ARG_MSG_POS --POS--
~ENDIF
: CHARS 'UTF-8' "\n\t[%d] -> \0" >
|>#MAIN_ARG_MSG_LEN (--POS-- - MAIN_ARG_MSG_POS)
:>|> do not forget to align!
~IF #~FOR_ME
	#EXP~MAIN_HELLO_MSG_POS --POS--
~ENDIF
:
	CHARS 'UTF-8' (
		"hello I have been called with %s\n"
		"and my %d arguments are:\0"
		|> do not forget the \0 at the end of system strings
	)
>
|>#MAIN_HELLO_MSG_LEN (--POS-- - MAIN_HELLO_MSG_POS)
:>|> do not forget to align!
~IF #~FOR_ME
	#EXP~MAIN_BYE_MSG_POS --POS--
~ENDIF
: CHARS 'UTF-8' "\ngoodbye, thanks for calling me.\n" >
|> no need for an \0 because this is not used as a string
~IF #~FOR_ME
	#EXP~MAIN_BYE_MSG_LEN (--POS-- - MAIN_BYE_MSG_POS)
~ENDIF
