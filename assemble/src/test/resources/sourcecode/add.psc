|: 
| Hello World code which adds the values of X00 and X01 and returns the result in X02
:>
ADD X00, X01
MOV X02, X00
MOV X00, 0 |> EXIT-SUCCESS
INT INT_EXIT