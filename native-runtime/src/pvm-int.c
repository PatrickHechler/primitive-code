#ifndef PVM
#error "this file should be included insice of pvm-virtual-mashine.c"
#endif

#include <pfs.h>
#include <pfs-stream.h>
#include <pfs-iter.h>
#include <pfs-element.h>
#include <pfs-folder.h>
#include <pfs-file.h>
#include <pfs-pipe.h>

static void int_errors_illegal_interrupt INT_PARAMS /* 0 */ {
	exit(128 + pvm.x[0]);
}
static void int_errors_unknown_command INT_PARAMS /* 1 */ {
	exit(7);
}
static void int_errors_illegal_memory INT_PARAMS /* 2 */ {
	exit(6);
}
static void int_errors_arithmetic_error INT_PARAMS /* 3 */ {
	exit(5);
}
static void int_exit INT_PARAMS /* 4 */ {
	exit(pvm.x[0]);
}
static void int_memory_alloc INT_PARAMS /* 5 */ {
	if (pvm.x[0] <= 0) {
		if (pvm.x[0] != 0) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		}
		pvm.x[0] = -1;
	} else {
		struct memory2 mem = alloc_memory(pvm.x[0]);
		if (mem.mem) {
			pvm.x[0] = mem.mem->start;
		} else {
			pvm.x[0] = -1;
		}
	}
}
static void int_memory_realloc INT_PARAMS /* 6 */ {
	if (pvm.x[1] <= 0) {
		if (pvm.x[1] == 0) {
			free_memory(pvm.x[0]);
		} else {
			pvm.errno = PE_ILLEGAL_ARG;
		}
		pvm.x[0] = -1;
	} else {
		struct memory *mem = realloc_memory(pvm.x[0], pvm.x[1]);
		if (mem) {
			pvm.x[0] = mem->start;
		} else {
			pvm.x[0] = -1;
		}
	}
}
static void int_memory_free INT_PARAMS /* 7 */ {
	free_memory(pvm.x[0]);
}
static void int_open_stream INT_PARAMS /* 8 */ {
	struct memory* mem = chk(pvm.x[0]);
	if (!mem) {
		return;
	}
	pvm.x[0] = pfs_stream(mem->offset + pvm.x[0], pvm.x[1]);
}
static void int_streams_write INT_PARAMS /* 9 */ {
	struct memory* mem = chk(pvm.x[0]);
	if (!mem) {
		return;
	}
	pvm.x[1] = pfs_stream_write(pvm.x[0], mem->offset + pvm.x[2], pvm.x[1]);
}
static void int_streams_read INT_PARAMS /* 10 */ {
	struct memory* mem = chk(pvm.x[0]);
	if (!mem) {
		return;
	}
	pvm.x[1] = pfs_stream_read(pvm.x[0], mem->offset + pvm.x[2], pvm.x[1]);
}
static void int_streams_close INT_PARAMS /* 11 */ {
	pvm.x[0] = pfs_stream_close(pvm.x[0]);
}
static void int_streams_get_pos INT_PARAMS /* 12 */ {
	abort();
}
static void int_streams_seek_set INT_PARAMS /* 13 */ {
	abort();
}
static void int_streams_seek_add INT_PARAMS /* 14 */ {
	abort();
}
static void int_streams_seek_eof INT_PARAMS /* 15 */ {
	abort();
}
static void int_open_element_file INT_PARAMS /* 16 */ {
	abort();
}
static void int_open_element_folder INT_PARAMS /* 17 */ {
	abort();
}
static void int_open_element_pipe INT_PARAMS /* 18 */ {
	abort();
}
static void int_open_element INT_PARAMS /* 19 */ {
	abort();
}
static void int_element_open_parent INT_PARAMS /* 20 */ {
	abort();
}
static void int_element_get_create INT_PARAMS /* 21 */ {
	abort();
}
static void int_element_get_last_mod INT_PARAMS /* 22 */ {
	abort();
}
static void int_element_set_create INT_PARAMS /* 23 */ {
	abort();
}
static void int_element_set_last_mod INT_PARAMS /* 24 */ {
	abort();
}
static void int_element_delete INT_PARAMS /* 25 */ {
	abort();
}
static void int_element_move INT_PARAMS /* 26 */ {
	abort();
}
static void int_element_get_flags INT_PARAMS /* 27 */ {
	abort();
}
static void int_element_mod_flags INT_PARAMS /* 28 */ {
	abort();
}
static void int_folder_child_count INT_PARAMS /* 29 */ {
	abort();
}
static void int_folder_get_child_of_name INT_PARAMS /* 30 */ {
	abort();
}
static void int_folder_add_folder INT_PARAMS /* 31 */ {
	abort();
}
static void int_folder_add_file INT_PARAMS /* 32 */ {
	abort();
}
static void int_folder_add_link INT_PARAMS /* 33 */ {
	abort();
}
static void int_folder_open_iter INT_PARAMS /* 34 */ {
	abort();
}
static void int_folder_create_folder INT_PARAMS /* 35 */ {
	abort();
}
static void int_folder_create_file INT_PARAMS /* 36 */ {
	abort();
}
static void int_folder_create_pipe INT_PARAMS /* 37 */ {
	abort();
}
static void int_file_length INT_PARAMS /* 38 */ {
	abort();
}
static void int_file_truncate INT_PARAMS /* 39 */ {
	abort();
}
static void int_file_open_stream INT_PARAMS /* 40 */ {
	abort();
}
static void int_pipe_length INT_PARAMS /* 41 */ {
	abort();
}
static void int_pipe_truncate INT_PARAMS /* 42 */ {
	abort();
}
static void int_pipe_open_stream INT_PARAMS /* 43 */ {
	abort();
}
static void int_time_get INT_PARAMS /* 44 */ {
	abort();
}
static void int_time_wait INT_PARAMS /* 45 */ {
	abort();
}
static void int_random INT_PARAMS /* 46 */ {
	abort();
}
static void int_memory_copy INT_PARAMS /* 47 */ {
	abort();
}
static void int_memory_move INT_PARAMS /* 48 */ {
	abort();
}
static void int_memory_bset INT_PARAMS /* 49 */ {
	abort();
}
static void int_memory_set INT_PARAMS /* 50 */ {
	abort();
}
static void int_string_length INT_PARAMS /* 51 */ {
	abort();
}
static void int_string_compare INT_PARAMS /* 52 */ {
	abort();
}
static void int_number_to_string INT_PARAMS /* 53 */ {
	abort();
}
static void int_fpnumber_to_string INT_PARAMS /* 54 */ {
	abort();
}
static void int_string_to_number INT_PARAMS /* 55 */ {
	abort();
}
static void int_string_to_fpnumber INT_PARAMS /* 56 */ {
	abort();
}
static void int_string_to_u16string INT_PARAMS /* 57 */ {
	abort();
}
static void int_string_to_u32string INT_PARAMS /* 58 */ {
	abort();
}
static void int_u16string_to_string INT_PARAMS /* 59 */ {
	abort();
}
static void int_u32string_to_string INT_PARAMS /* 60 */ {
	abort();
}
static void int_string_format INT_PARAMS /* 61 */ {
	abort();
}
static void int_load_file INT_PARAMS /* 62 */ {
	abort();
}
