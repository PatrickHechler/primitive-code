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
	#STR_POS 0
	#STR_LEN 0
~ENDIF

MOV X00, STD_OUT
MOV X01, STR_LEN
#REL_POS ( STR_POS - --POS-- )
LEA X02, REL_POS
#REL_POS ~DEL
INT INT_STREAMS_WRITE
XOR X00, X00
INT INT_EXIT

#EXP~STR_POS --POS--
: CHARS 'UTF-8' "hello primitive world\n" >
#EXP~STR_LEN ( --POS-- - STR_POS )
