JAVA_HOME=	C:\MC\BIN\Java11

DLLFLAGS =	-g -shared -Wl,--add-stdcall-alias
INCLUDES =	-I$(JAVA_HOME)\include -I$(JAVA_HOME)\include\win32

CC	= gcc

CFLAGS	=	-g -IC:\MC\BIN\Java11\include -IC:\MC\BIN\Java11\include\win32

OBJS =		primitive-runtime.o

LIBS =

TARGET =	primitive-runtime.dll

$(TARGET):	$(OBJS)
	$(CC) -o $(TARGET) $(OBJS) $(DLLFLAGS) $(LIBS)
	copy $(TARGET) %git%\primitive-code\assemble\

%.o: %.c
	$(CC) $(CFLAGS) $(INCLUDES) $< -c

%.dll:	%.c
	$(CC) $(INCLUDES) -shared -o %.dll %.c

all:	$(TARGET)


clean:
	del /F /Q $(OBJS) $(TARGET)
