|:	makes the stack dynamic grow able
|		- overwrites the Illegal-Memory-Interrupt
|		  which delegates to the old/default interrupt
|		  when there was no stack overflow
|	calls the main
|		- after a jump to the super_main
|		  and the initialization of the stack
|		- exits with the mains return (which is in X00)
|	to jump to the super main:
|		~READ_SYM "<THIS_FILE>"
|		~IF (#~FOR_ME==0)
|			~READ_SYM "[THIS]" #MY_EXPORTS_ #ADD~FOR_ME 1
|		~ENDIF
|		~IF MAIN_IS_IN_THIS_FILE
|			#REL_POS (--POS-- - main)
|			LEA X02, ZW
|			#REL_POS ~DEL
|		~ELSE
|			~READ_SYM "[THIS]" #main_file #ADD~FOR_ME 1
|			#REL_POS (--POS-- - main)
|			LEA X02, ZW
|			#REL_POS ~DEL
|		~ENDIF
|		#ZW (super_main - 8)
|		ADD X02, ZW
|		#ZW ~DEL
|		MOV IP, X02
|:>
~IF (#~MAIN_NEEDS_ZERO_REGS == 0)
	|:	by default this is active, since it
	|	is unknown, what the main really needs.
	|:>
	#MAIN_NEEDS_ZERO_REGS 1
~ENDIF
~IF (#~REMEMBER_OLD_POS == 0)
	|:	by default this is active, since it
	|	can make things easier and it does not hurt.
	|:>
	#REMEMBER_OLD_POS 1
~ENDIF

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
		~ERROR { "X01 is for the program argument values!" }
	~ENDIF
~ENDIF
~IF (#~MAIN_NEEDS_ZERO_REG_X02 != 0)
	~IF MAIN_NEEDS_ZERO_REG_X02
		~ERROR { "X02 is for the main function parameter!" }
	~ENDIF
~ENDIF
~IF (#~REMEMBER_OLD_POS == 0)
	#REMEMBER_OLD_POS 0
~ENDIF
~IF REMEMBER_OLD_POS
	|:	if you do not care about the old pointer you can remove it
	|	by defining the constant REMEMBER_OLD_POS to an zero value.
	|:>
	#EXP~OLD_INT_ILLEGAL_MEM_POS --POS--
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
|:	X00: argument count
|	X01: argument pointer
|	X02: main function
|:>
~IF (#~MAIN_NEEDS_ZERO_REGS == 0)
	#MAIN_NEEDS_ZERO_REGS 0
~ENDIF
~IF (#~MAIN_NEEDS_ZERO_REG_X03 == 0)
	#MAIN_NEEDS_ZERO_REG_X03 0
~ENDIF
#EXP~super_main --POS--
|>@super_main
	CMP SP, -1
	JMPNE make_stack_growable
		MOV X03, X00
		MOV X00, INIT_STACK_SIZE
		INT INT_MEMORY_ALLOC
		CMP X00, -1
		JMPEQ error_stack
		MOV SP, X00
		MOV X00, X03
	@make_stack_growable
		~IF REMEMBER_OLD_POS
			#REL_POS (OLD_INT_ILLEGAL_MEM_POS - --POS--)
			|> save the old interrupt, so if the illegal memory
			|> interrupt is not called because of an stack overflow,
			|> the old can be called.
			|> I know that the old interrupt is most likely -1, but
			|> if the super_main is not directly executed. The 
			|> illegal-memory-interrupt my be overwritten before the super_main
			|> execution.
			|> And if someone wants too add his interrupt, it is easy to do it, when
			|> the someone knows there is no old interrupt.
			MOV [IP + REL_POS], [INTP + TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY]
			#REL_POS ~DEL
		~ENDIF
		#REL_POS (INT_ILLEGAL_MEMORY_POS - --POS--)
		LEA [INTP + TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY], REL_POS
		#REL_POS ~DEL
		#REL_POS (STAK_MEMORY_POS - --POS--)
		MOV [IP + REL_POS], SP
		#REL_POS ~DEL
		~IF (MAIN_NEEDS_ZERO_REG_X03 | MAIN_NEEDS_ZERO_REGS)
			MOV X03, 0
		~ENDIF
		CALO X02, 0
		INT INT_EXIT
#INIT_STACK_SIZE ~DEL
#OLD_INT_ILLEGAL_MEM_POS ~DEL
#INT_ILLEGAL_MEMORY_POS ~DEL
#TABLE_ENTRY_INT_ERRORS_ILLEGAL_MEMORY ~DEL

|:	if you do not want that the message should be printed
|		you can just comment the message (and|or) (one of the|both) constants out
|			if you do that the length will be automatically
|			set to zero, which also leads to the away filtering
|			of the message printing code block
|				the code block will only be assembled when the
|				length constant is greater than zero
|				and only when both constants are defined
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
