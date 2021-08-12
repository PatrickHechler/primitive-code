JAVA_HOME=	C:\MC\BIN\Java11

DLLFLAGS =	-g -shared -Wl,--add-stdcall-alias
INCLUDES =	-I$(JAVA_HOME)\include -I$(JAVA_HOME)\include\win32


CFLAGS	=	-g -IC:\MC\BIN\Java11\include -IC:\MC\BIN\Java11\include\win32

OBJS =		primitive-runtime.o

LIBS =

TARGET =	primitive-runtime.dll

$(TARGET):	$(OBJS)
	gcc -o $(TARGET) $(OBJS) $(DLLFLAGS) $(LIBS)

%.o: %.c
	gcc $(CFLAGS) $(INCLUDES) $< -c

%.dll:	%.c
	gcc $(INCLUDES) -shared -o %.dll %.c

all:	$(TARGET)


clean:
	del /F /Q $(OBJS) $(TARGET)
