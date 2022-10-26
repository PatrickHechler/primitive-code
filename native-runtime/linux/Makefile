PROJECT_ROOT = $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

PFS_CORE_FOLDER = $(PROJECT_ROOT)../../../PatrFileSys/pfs-core/

PFS_LIB_FOLDER = $(PFS_CORE_FOLDER)exports/static/

PFS_LIB_FILE = $(PFS_LIB_FOLDER)libpfscore.a

TARGET = pvm

DEBUG_TARGET = db-pvm

LDFLAGS =

DEBUG_LDFLAGS = -lpthread

CC = gcc

ifeq ($(BUILD_MODE),debug)
	CFLAGS += -g -Og
else ifeq ($(BUILD_MODE),run)
	CFLAGS += -Ofast
else ifeq ($(BUILD_MODE),linuxtools)
	CFLAGS += -g -pg -fprofile-arcs -ftest-coverage
	LDFLAGS += -pg -fprofile-arcs -ftest-coverage
else
    $(error Build mode $(BUILD_MODE) not supported by this Makefile)
endif

BINARY = $(PROJEXT_ROOT)bin/$(BUILD_MODE)/

COMMON_OBJS = $(BINARY)pvm-virtual-mashine.o

OBJS = $(COMMON_OBJS) $(BINARY)pvm-main.o

DEBUG_OBJS = $(COMMON_OBJS) $(BINARY)pvm-debug.o

TARGET_WITH_PATH = $(BINARY)$(TARGET)

DEBUG_TARGET_WITH_PATH = $(BINARY)$(DEBUG_TARGET)

all:	INIT $(PFS_LIB_FILE) $(TARGET) $(DEBUG_TARGET)

INIT:
	mkdir -p $(BINARY)

$(PFS_LIB_FILE):
	make -C $(PFS_CORE_FOLDER) all

$(TARGET):	$(OBJS)
	$(CC) -o $(TARGET_WITH_PATH) $^ $(LDFLAGS)

$(DEBUG_TARGET):	$(DEBUG_OBJS)
	$(CC) -o $(DEBUG_TARGET_WITH_PATH) $^ $(DEBUG_LDFLAGS)

$(BINARY)%.o:	$(PROJECT_ROOT)%.c
	$(CC) -x c -c $(CFLAGS) -o $@ $<

clean:
	rm -fr $(BINARY)
