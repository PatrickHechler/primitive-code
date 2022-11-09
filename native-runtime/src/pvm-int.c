#ifndef PVM
#error "this file should be included insice of pvm-virtual-mashine.c"
#endif

static void int_errors_illegal_interrupt INT_PARAMS {
	exit(128 + pvm.xnn[0]);
}
static void int_errors_unknown_command INT_PARAMS {
	exit(7);
}
static void int_errors_illegal_memory INT_PARAMS {
	exit(6);
}
static void int_errors_arithmetic_error INT_PARAMS {
	exit(5);
}
static void int_exit INT_PARAMS {
	exit(pvm.xnn[0]);
}
static void int_memory_alloc INT_PARAMS {
	if (pvm.xnn[0] <= 0) {
		if (pvm.xnn[0] != 0) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		}
		pvm.xnn[0] = -1;
	} else {
		struct memory2 mem = alloc_memory(pvm.xnn[0]);
		if (mem.mem) {
			pvm.xnn[0] = mem.mem->start;
		} else {
			pvm.xnn[0] = -1;
		}
	}
}
static void int_memory_realloc INT_PARAMS {
	if (pvm.xnn[1] <= 0) {
		if (pvm.xnn[1] == 0) {
			free_memory(pvm.xnn[0]);
		} else {
			pvm.errno = PE_ILLEGAL_ARG;
		}
		pvm.xnn[0] = -1;
	} else {
		struct memory *mem = realloc_memory(pvm.xnn[0], pvm.xnn[1]);
		if (mem) {
			pvm.xnn[0] = mem->start;
		} else {
			pvm.xnn[0] = -1;
		}
	}
}
static void int_memory_free INT_PARAMS {
	free_memory(pvm.xnn[0]);
}
static void int_open_stream INT_PARAMS {
	abort();
}
static void int_streams_write INT_PARAMS {
	abort();
}
static void int_streams_read INT_PARAMS {
	abort();
}
static void int_streams_close INT_PARAMS {
	abort();
}
static void int_streams_get_pos INT_PARAMS {
	abort();
}
static void int_streams_seek_set INT_PARAMS {
	abort();
}
static void int_streams_seek_add INT_PARAMS {
	abort();
}
static void int_streams_seek_eof INT_PARAMS {
	abort();
}
static void int_open_element_file INT_PARAMS {
	abort();
}
static void int_open_element_folder INT_PARAMS {
	abort();
}
static void int_open_element_pipe INT_PARAMS {
	abort();
}
static void int_open_element INT_PARAMS {
	abort();
}
static void int_element_open_parent INT_PARAMS {
	abort();
}
static void int_element_get_create INT_PARAMS {
	abort();
}
static void int_element_get_last_mod INT_PARAMS {
	abort();
}
static void int_element_set_create INT_PARAMS {
	abort();
}
static void int_element_set_last_mod INT_PARAMS {
	abort();
}
static void int_element_delete INT_PARAMS {
	abort();
}
static void int_element_move INT_PARAMS {
	abort();
}
static void int_element_get_flags INT_PARAMS {
	abort();
}
static void int_element_mod_flags INT_PARAMS {
	abort();
}
static void int_folder_child_count INT_PARAMS {
	abort();
}
static void int_folder_get_child_of_name INT_PARAMS {
	abort();
}
static void int_folder_add_folder INT_PARAMS {
	abort();
}
static void int_folder_add_file INT_PARAMS {
	abort();
}
static void int_folder_add_link INT_PARAMS {
	abort();
}
static void int_folder_open_iter INT_PARAMS {
	abort();
}
static void int_folder_create_folder INT_PARAMS {
	abort();
}
static void int_folder_create_file INT_PARAMS {
	abort();
}
static void int_folder_create_pipe INT_PARAMS {
	abort();
}
static void int_file_length INT_PARAMS {
	abort();
}
static void int_file_truncate INT_PARAMS {
	abort();
}
static void int_file_open_stream INT_PARAMS {
	abort();
}
static void int_pipe_length INT_PARAMS {
	abort();
}
static void int_pipe_truncate INT_PARAMS {
	abort();
}
static void int_pipe_open_stream INT_PARAMS {
	abort();
}
static void int_time_get INT_PARAMS {
	abort();
}
static void int_time_wait INT_PARAMS {
	abort();
}
static void int_random INT_PARAMS {
	abort();
}
static void int_memory_copy INT_PARAMS {
	abort();
}
static void int_memory_move INT_PARAMS {
	abort();
}
static void int_memory_bset INT_PARAMS {
	abort();
}
static void int_memory_set INT_PARAMS {
	abort();
}
static void int_string_length INT_PARAMS {
	abort();
}
static void int_string_compare INT_PARAMS {
	abort();
}
static void int_number_to_string INT_PARAMS {
	abort();
}
static void int_fpnumber_to_string INT_PARAMS {
	abort();
}
static void int_string_to_number INT_PARAMS {
	abort();
}
static void int_string_to_fpnumber INT_PARAMS {
	abort();
}
static void int_string_to_u16string INT_PARAMS {
	abort();
}
static void int_string_to_u32string INT_PARAMS {
	abort();
}
static void int_u16string_to_string INT_PARAMS {
	abort();
}
static void int_u32string_to_string INT_PARAMS {
	abort();
}
static void int_string_format INT_PARAMS {
	abort();
}
static void int_load_file INT_PARAMS {
	abort();
}
