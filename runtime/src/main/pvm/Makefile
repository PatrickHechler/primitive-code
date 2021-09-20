CC	= gcc

CFLAGS	= -x c

LIBS	= -lWs2_32

NAME	= pvm

SOURCE	= $(NAME).c

OBJS	= $(NAME).o

TARGET	= $(NAME).exe

#$(TARGET):	$(OBJS)
#	$(CC) --help
#	#-o $(OBJS) $(SOURCE) $(LIBS)
#	#copy $(TARGET) %git%\primitive-code\assemble\

%.o: %.c
	$(CC) $(CFLAGS) $< -c

#%.exe: %.c
$(TARGET):	$(OBJS)
	$(CC) -o $(TARGET) $(OBJS) $(LIBS)
#	$(CC) -Xlinker -L -Xlinker "C:\Program Files (x86)\Windows Kits\10\Lib\10.0.19041.0\um\arm" -Xlinker -l -Xlinker $(LIB) $(CFLAGS) $<
#	$(CC) -Xlinker --print-sysroot -Xlinker -l -Xlinker $(LIB) $(CFLAGS) $<
#	$(CC) $(CFLAGS) $(LIBS) $<

all:	$(TARGET)


clean:
	del /F /Q $(OBJS) $(TARGET)
