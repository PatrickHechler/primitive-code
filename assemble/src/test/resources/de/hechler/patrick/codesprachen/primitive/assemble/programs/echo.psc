|>This file is part of the Primitive Code Project
|>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
|>Copyright (C) 2023  Patrick Hechler
|>
|>This program is free software: you can redistribute it and/or modify
|>it under the terms of the GNU General Public License as published by
|>the Free Software Foundation, either version 3 of the License, or
|>(at your option) any later version.
|>
|>This program is distributed in the hope that it will be useful,
|>but WITHOUT ANY WARRANTY; without even the implied warranty of
|>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
|>GNU General Public License for more details.
|>
|>You should have received a copy of the GNU General Public License
|>along with this program.  If not, see <https://www.gnu.org/licenses/>.
~IF ( #~FOR_ME == 0 )
	~READ_SYM "[THIS]" #ADD~FOR_ME 1 >
~ELSE
	#SPACE_STR_POS 0
	#NEWLINE_STR_POS 0
	#STR_LEN 0
~ENDIF

MOV X03, X01
@ECHO_LOOP
	ADD X03, 8
	CMP [X03], -1
	JMPEQ FINISH
	
	CMP X04, 0
	JMPEQ SKIP_SPACE
		MOV X00, STD_OUT
		MOV X01, STR_LEN
		#REL_POS ( SPACE_STR_POS - --POS-- )
		LEA X02, REL_POS
		#REL_POS ~DEL
		INT INT_STREAMS_WRITE
	@SKIP_SPACE
	MOV X04, 1
	
	MOV X02, [X03]
	MOV X00, X02
	INT INT_STR_LEN
	MOV X01, X00
	MOV X00, STD_OUT
	INT INT_STREAMS_WRITE
	
	JMP ECHO_LOOP
@FINISH
MOV X00, STD_OUT
MOV X01, STR_LEN
#REL_POS ( NEWLINE_STR_POS - --POS-- )
LEA X02, REL_POS
#REL_POS ~DEL
INT INT_STREAMS_WRITE
XOR X00, X00
INT INT_EXIT

$not-align

#EXP~SPACE_STR_POS --POS--
: CHARS 'UTF-8' " " >
#SPACE_STR_LEN ( --POS-- - SPACE_STR_POS )

#EXP~NEWLINE_STR_POS --POS--
: CHARS 'UTF-8' "\n" >
#NEWLINE_STR_LEN ( --POS-- - NEWLINE_STR_POS )

#EXP~STR_LEN 1

~IF ( ( #~FOR_ME == 0 ) * ( ( SPACE_STR_LEN != STR_LEN ) + ( NEWLINE_STR_LEN != STR_LEN ) ) )
	~ERROR { "SPACE_STR_LEN (" SPACE_STR_LEN ") or NEWLINE_STR_LEN (" NEWLINE_STR_LEN ") is not " STR_LEN }
~ENDIF
