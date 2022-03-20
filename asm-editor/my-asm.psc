|:	hello world code
	prints just hello world
:>
	JMP main
	#helloWorld_POS --POS--
    : "hello world" >
	#helloWorldLength ( --POS-- - helloWorld_POS)
@main
	#relativePOS ( helloWorld_POS - --POS-- )
	MOV X00, STD_OUT
	LEA X01, relativePOS
	MOV X02, helloWorldLength
	INT INT_STREAMS_WRITE
	MOV X00, 0
	INT INT_EXIT
	MOV [-1], 2

|:
org.eclipse.core.internal.filebuffers.SynchronizableDocument@d35d84a=primitivecodeeclipseplugin.objects.DocumentValue@626edbe0
:>