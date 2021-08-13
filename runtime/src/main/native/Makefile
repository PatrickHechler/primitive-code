JAVA_HOME=	C:\Program Files\Java\jdk1.8.0_251

DLLFLAGS =	-g -shared -Wl,--add-stdcall-alias
INCLUDES =	-I"$(JAVA_HOME)\include" -I"$(JAVA_HOME)\include\win32"


#CFLAGS	=	-g -IC:\MC\BIN\Java11\include -IC:\MC\BIN\Java11\include\win32
CFLAGS	=	-g -I"C:\Program Files\Java\jdk1.8.0_251\include" -I"C:\Program Files\Java\jdk1.8.0_251\include\win32"

OBJS =		primitive-runtime.o

LIBS =

TARGET =	primitive-runtime.dll

$(TARGET):	$(OBJS)
	gcc -o $(TARGET) $(OBJS) $(DLLFLAGS) $(LIBS)
	copy $(TARGET) C:\Users\feri\git\primitive-code\assemble\

%.o: %.c
	gcc $(CFLAGS) $(INCLUDES) $< -c

%.dll:	%.c
	gcc $(INCLUDES) -shared -o %.dll %.c

all:	$(TARGET)


clean:
	del /F /Q $(OBJS) $(TARGET)
