PROJECT_ROOT = $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

PFS_CORE_EXP_FOLDER = $(PROJECT_ROOT)../../PatrFileSys/pfs-core/exports/

PFS_LIB_INCLUDE = $(PFS_CORE_EXP_FOLDER)include/

PFS_LIB_FOLDER = $(PFS_CORE_EXP_FOLDER)static/

BUILD_TARGET_DEBUG = DEBUG_PVM

BUILD_TARGET_RUN = RUN_PVM

TARGET = pvm

DEBUG_TARGET = db-pvm

LDFLAGS = -L$(PFS_LIB_FOLDER) -lpfscore

DEBUG_LDFLAGS = 
#-lpthread

CC = gcc

CFLAGS += -I$(PFS_LIB_INCLUDE)

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

BIN_ROOT = $(PROJEXT_ROOT)bin/

BINARY_PARENT = $(BIN_ROOT)$(BUILD_MODE)/

BINARY = $(BINARY_PARENT)$(BUILD_TARGET)/

SOURCE = $(PROJEXT_ROOT)src/

OBJS = $(BINARY)pvm-virtual-mashine.o $(BINARY)pvm-main.o

TARGET_WITH_PATH = $(BIN_ROOT)$(TARGET)

DEBUG_TARGET_WITH_PATH = $(BIN_ROOT)$(DEBUG_TARGET)

ifeq ($(BUILD_TARGET), $(BUILD_TARGET_DEBUG))
	CFLAGS += -DPVM_DEBUG
else ifeq ($(BUILD_TARGET), $(BUILD_TARGET_RUN))
else ifeq ($(BUILD_TARGET), )
else
	$(error Build target $(BUILD_MODE) not supported by this Makefile)
endif

all:	$(TARGET) $(DEBUG_TARGET)
	echo FINISHED all

run:	$(TARGET)

debug:	$(DEBUG_TARGET)

$(TARGET):	INIT $(PFS_LIB_FILE)
	mkdir -p $(BINARY_PARENT)$(BUILD_TARGET_RUN)
	BUILD_TARGET=$(BUILD_TARGET_RUN) make -C . $(TARGET_WITH_PATH)

$(DEBUG_TARGET):	INIT $(PFS_LIB_FILE)
	mkdir -p $(BINARY_PARENT)$(BUILD_TARGET_DEBUG)
	BUILD_TARGET=$(BUILD_TARGET_DEBUG) make -C . $(DEBUG_TARGET_WITH_PATH)

INIT:
	echo BUILD_MODE=$(BUILD_MODE)
	mkdir -p $(BINARY_PARENT)

$(PFS_LIB_FILE):
	make -C $(PFS_CORE_FOLDER) all

$(TARGET_WITH_PATH):	$(OBJS)
	$(CC) -o $(TARGET_WITH_PATH) $^ $(LDFLAGS)
	echo FINISHED $(TARGET)

$(DEBUG_TARGET_WITH_PATH):	$(OBJS)
	$(CC) -o $(DEBUG_TARGET_WITH_PATH) $^ $(LDFLAGS)
	echo FINISHED $(DEBUG_TARGET)

$(BINARY)%.o:	$(SOURCE)%.c
	$(CC) -x c -c $(CFLAGS) -o $@ $<

clean:
	rm -fr $(BIN_ROOT)
