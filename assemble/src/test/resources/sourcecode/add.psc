|: 
| Hello World code which adds the values of AX and BX and returns the result in CX
:>
ADD AX, BX
MOV CX, AX
MOV AX, 0 |> EXIT-SUCCESS
INT #INT-EXIT