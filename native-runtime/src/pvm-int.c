#ifndef PVM
#error "this file should be included insice of pvm-virtual-mashine.c"
#endif

#define check_string_len0(XNN_OFFSET, mem, name, max_len, error_reg, error_value) \
	num max_len = mem->end - pvm.x[XNN_OFFSET]; \
	char* name = mem->offset + pvm.x[XNN_OFFSET]; \
	num name##_len = strnlen(name, max_len); \
	if (name##_len == max_len) { \
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0); \
		pvm.x[error_reg] = error_value;  \
		return;  \
	}

#define check_string_len(XNN_OFFSET, error_reg, error_value) check_string_len0(XNN_OFFSET, mem, name, max_len, error_reg, error_value)

static void int_errors_illegal_interrupt( INT_PARAMS) /* 0 */{
	exit(128 + pvm.x[0]);
}
static void int_errors_unknown_command( INT_PARAMS) /* 1 */{
	exit(7);
}
static void int_errors_illegal_memory( INT_PARAMS) /* 2 */{
	exit(6);
}
static void int_errors_arithmetic_error( INT_PARAMS) /* 3 */{
	exit(5);
}
static void int_exit( INT_PARAMS) /* 4 */{
	exit(pvm.x[0]);
}
static void int_memory_alloc( INT_PARAMS) /* 5 */{
	if (pvm.x[0] <= 0) {
		if (pvm.x[0] != 0) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		} else {
			pvm.x[0] = -1;
		}
		return;
	}
	struct memory2 mem = alloc_memory(pvm.x[0], 0);
	if (mem.mem) {
		pvm.x[0] = mem.mem->start;
	} else {
		pvm.x[0] = -1;
	}
}
static void int_memory_realloc( INT_PARAMS) /* 6 */{
	if (pvm.x[1] <= 0) {
		if (pvm.x[1] == 0) {
			free_memory(pvm.x[0]);
			pvm.x[0] = -1;
		} else {
			pvm.err = PE_ILLEGAL_ARG;
		}
		return;
	}
	struct memory *mem = realloc_memory(pvm.x[0], pvm.x[1], 0);
	if (mem) {
		pvm.x[0] = mem->start;
	} else {
		pvm.x[0] = -1;
	}
}
static void int_memory_free( INT_PARAMS) /* 7 */{
	free_memory(pvm.x[0]);
}
static void int_open_stream( INT_PARAMS) /* 8 */{
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		pvm.x[1] = 0;
		return;
	}
	check_string_len(0, 1, 0)
	pvm.x[0] = pfs_stream(name, pvm.x[1]);
}
static void int_streams_write( INT_PARAMS) /* 9 */{
	if (pvm.x[1] < 0) {
		pvm.x[1] = 0;
		pvm.err = PFS_ERRNO_ILLEGAL_ARG;
		return;
	}
	struct memory *mem = chk(pvm.x[0], pvm.x[1]).mem;
	if (!mem) {
		return;
	}
	pvm.x[1] = pfs_stream_write(pvm.x[0], mem->offset + pvm.x[2], pvm.x[1]);
}
static void int_streams_read( INT_PARAMS) /* 10 */{
	if (pvm.x[1] < 0) {
		pvm.x[1] = 0;
		pvm.err = PFS_ERRNO_ILLEGAL_ARG;
		return;
	}
	struct memory *mem = chk(pvm.x[0], pvm.x[1]).mem;
	if (!mem) {
		return;
	}
	pvm.x[1] = pfs_stream_read(pvm.x[0], mem->offset + pvm.x[2], pvm.x[1]);
}
static void int_streams_close( INT_PARAMS) /* 11 */{
	pvm.x[0] = pfs_stream_close(pvm.x[0]);
}
static void int_streams_get_pos( INT_PARAMS) /* 12 */{
	pvm.x[1] = pfs_stream_get_pos(pvm.x[0]);
}
static void int_streams_seek_set( INT_PARAMS) /* 13 */{
	pvm.x[1] = pfs_stream_set_pos(pvm.x[0], pvm.x[1]);
}
static void int_streams_seek_add( INT_PARAMS) /* 14 */{
	pvm.x[1] = pfs_stream_add_pos(pvm.x[0], pvm.x[1]);
}
static void int_streams_seek_eof( INT_PARAMS) /* 15 */{
	pvm.x[1] = pfs_stream_seek_eof(pvm.x[0]);
}
static void int_open_element_file( INT_PARAMS) /* 16 */{
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, 1, 0)
	pvm.x[0] = pfs_handle_file(name);
}
static void int_open_element_folder( INT_PARAMS) /* 17 */{
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, 1, 0)
	pvm.x[0] = pfs_handle_folder(name);
}
static void int_open_element_pipe( INT_PARAMS) /* 18 */{
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, 1, 0)
	pvm.x[0] = pfs_handle_pipe(name);
}
static void int_open_element( INT_PARAMS) /* 19 */{
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, 1, 0)
	pvm.x[0] = pfs_handle(name);
}
static void int_element_open_parent( INT_PARAMS) /* 20 */{
	pvm.x[0] = pfs_element_parent(pvm.x[0]);
}
static void int_element_get_create( INT_PARAMS) /* 21 */{
	pvm.x[1] = pfs_element_get_create_time(pvm.x[0]);
}
static void int_element_get_last_mod( INT_PARAMS) /* 22 */{
	pvm.x[1] = pfs_element_get_last_modify_time(pvm.x[0]);
}
static void int_element_set_create( INT_PARAMS) /* 23 */{
	pvm.x[1] = pfs_element_set_create_time(pvm.x[0], pvm.x[1]);
}
static void int_element_set_last_mod( INT_PARAMS) /* 24 */{
	pvm.x[1] = pfs_element_set_last_modify_time(pvm.x[0], pvm.x[1]);
}
static void int_element_delete( INT_PARAMS) /* 25 */{
	pvm.x[0] = pfs_element_delete(pvm.x[0]);
}
static void int_element_move( INT_PARAMS) /* 26 */{
	if (pvm.x[1] != -1) {
		struct memory *mem = chk(pvm.x[1], 1).mem;
		if (!mem) {
			return;
		}
		check_string_len0(0, mem, new_name, max_len, 1, 0)
		if (pvm.x[2] != -1) {
			pvm.x[1] = pfs_element_move(pvm.x[0], pvm.x[2], new_name);
		} else {
			pvm.x[1] = pfs_element_set_name(pvm.x[0], new_name);
		}
	} else if (pvm.x[2] != -1) {
		pvm.x[1] = pfs_element_set_parent(pvm.x[0], pvm.x[2]);
	} else {
		pvm.x[1] = 1;
	}
}
static void int_element_get_name( INT_PARAMS) /* 27 */{
	if (pvm.x[1] == -1) {
		char *buf = NULL;
		num size = 0;
		if (pfs_element_get_name(pvm.x[0], &buf, &size)) {
			struct memory *mem = alloc_memory2(buf, size, 0);
			if (!mem) {
				free(buf);
				return;
			}
			pvm.x[1] = mem->start;
			pvm.x[2] = size;
		} else {
			pvm.x[1] = -1;
		}
	} else {
		struct memory *mem = chk(pvm.x[1], 0).mem;
		if (pvm.x[1] == mem->start) {
			char *buf = mem->offset + mem->start;
			num size = mem->end - mem->start;
			if (pfs_element_get_name(pvm.x[0], &buf, &size)) {
				if (size > mem->end - mem->start) {
					mem->offset = (void*) buf - mem->start;
					mem = realloc_memory(mem->start, size, 0);
				}
				pvm.x[1] = mem->start;
				pvm.x[2] = size;
			} else {
				pvm.x[1] = -1;
			}
		} else {
			char *buf = NULL;
			num size = 0;
			if (!pfs_element_get_name(pvm.x[0], &buf, &size)) {
				pvm.x[1] = -1;
				return;
			}
			num maxsize = mem->end - pvm.x[1];
			if (maxsize > size) {
				num off = pvm.x[1] - mem->start;
				mem = realloc_memory(mem->start, off + size, 0);
				pvm.x[1] = mem->start + off;
			}
			memcpy(mem->offset + pvm.x[1], buf, size);
		}
	}
}
static void int_element_get_flags( INT_PARAMS) /* 28 */{
	pvm.x[1] = pfs_element_get_flags(pvm.x[0]);
}
static void int_element_mod_flags( INT_PARAMS) /* 29 */{
	pvm.x[1] = pfs_element_modify_flags(pvm.x[0], pvm.x[1], pvm.x[2]);
}
static void int_folder_child_count( INT_PARAMS) /* 30 */{
	pvm.x[1] = pfs_folder_child_count(pvm.x[0]);
}
static void int_folder_get_child_of_name( INT_PARAMS) /* 31 */{
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		pvm.x[1] = -1;
		return;
	}
	num max_len = mem->end - pvm.x[1];
	const char *name = mem->offset + pvm.x[1];
	num name_len = strnlen(name, max_len);
	if (name_len == max_len) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		return;
	}
	pvm.x[1] = pfs_folder_child(pvm.x[0], name);
}
static void int_folder_get_folder_of_name( INT_PARAMS) /* 32 */{
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, 1, -1)
	pvm.x[1] = pfs_folder_child_folder(pvm.x[0], name);
}
static void int_folder_get_file_of_name( INT_PARAMS) /* 33 */{
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, 1, -1)
	pvm.x[1] = pfs_folder_child_file(pvm.x[0], name);
}
static void int_folder_get_pipe_of_name( INT_PARAMS) /* 34 */{
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, 1, -1)
	pvm.x[1] = pfs_folder_child_pipe(pvm.x[0], name);
}
static void int_folder_add_folder( INT_PARAMS) /* 35 */{
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, 1, -1)
	pvm.x[1] = pfs_folder_create_folder(pvm.x[0], name);
}
static void int_folder_add_file( INT_PARAMS) /* 36 */{
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, 1, -1)
	pvm.x[1] = pfs_folder_create_file(pvm.x[0], name);
}
static void int_folder_add_pipe( INT_PARAMS) /* 37 */{
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, 1, -1)
	pvm.x[1] = pfs_folder_create_pipe(pvm.x[0], name);
}
static void int_folder_open_iter( INT_PARAMS) /* 38 */{
	pvm.x[1] = pfs_folder_open_iter(pvm.x[0], pvm.x[1] != 0);
}
static void int_file_length( INT_PARAMS) /* 39 */{
	pvm.x[1] = pfs_file_length(pvm.x[0]);
}
static void int_file_truncate( INT_PARAMS) /* 40 */{
	pvm.x[1] = pfs_file_truncate(pvm.x[0], pvm.x[1]);
}
static void int_handle_open_stream( INT_PARAMS) /* 41 */{
	pvm.x[1] = pfs_open_stream(pvm.x[0], pvm.x[1]);
}
static void int_pipe_length( INT_PARAMS) /* 42 */{
	pvm.x[1] = pfs_pipe_length(pvm.x[0]);
}
static void int_time_get( INT_PARAMS) /* 45 */{ // TODO correct values / add to table
	struct timespec spec;
	if (clock_gettime(CLOCK_REALTIME, &spec) == -1) {
		pvm.x[0] = 0;
	} else {
		pvm.x[0] = 1;
		pvm.x[1] = spec.tv_sec;
		pvm.x[2] = spec.tv_nsec;
	}
}
static void int_time_res( INT_PARAMS) /* 45 */{
	struct timespec spec;
	if (clock_getres(CLOCK_REALTIME, &spec) == -1) {
		pvm.x[0] = 0;
	} else {
		pvm.x[0] = 1;
		pvm.x[1] = spec.tv_sec;
		pvm.x[2] = spec.tv_nsec;
	}
}
static void int_time_sleep( INT_PARAMS) /* 46 */{
	struct timespec want, remain;
	want.tv_nsec = pvm.x[0];
	want.tv_sec = pvm.x[1];
	if (nanosleep(&want, &remain) == -1) {
		switch (errno) {
		case EINVAL:
			pvm.err = PFS_ERRNO_ILLEGAL_ARG;
			break;
		default:
			pvm.err = PFS_ERRNO_UNKNOWN_ERROR;
		}
		pvm.x[2] = 0;
	} else {
		pvm.x[2] = 1;
	}
}
static void int_time_sleep( INT_PARAMS) /* 46 */{
	struct timespec want;
	want.tv_nsec = pvm.x[0];
	want.tv_sec = pvm.x[1];
	if (clock_nanosleep(CLOCK_REALTIME, TIMER_ABSTIME, &want, NULL) == -1) {
		switch (errno) {
		case EINVAL:
			pvm.err = PFS_ERRNO_ILLEGAL_ARG;
			break;
		default:
			pvm.err = PFS_ERRNO_UNKNOWN_ERROR;
		}
		pvm.x[2] = 0;
	} else {
		pvm.x[2] = 1;
	}
}
static void int_random_stream( INT_PARAMS) /* 47 */{
	int fd = open("/dev/urandom", O_RDONLY);
	if (fd == -1) {
		pvm.err = PE_UNKNOWN_ERROR;
		pvm.x[0] = -1;
		return;
	}
	int sh = pfs_stream_open_delegate(fd, PFS_SO_PIPE | PFS_SO_READ);
	if (sh == -1) {
		close(fd);
	}
	pvm.x[0] = sh;
}
static void int_random_num( INT_PARAMS) /* 47 */{
	long int r = random();
	if (r < 0) {
		r = -1;
		pvm.err = PE_UNKNOWN_ERROR;
	}
	pvm.x[0] = r;
}
static void int_memory_copy( INT_PARAMS) /* 48 */{
	if (pvm.x[2] < 0) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		return;
	}
	struct memory *src = chk(pvm.x[0], pvm.x[2]).mem;
	if (!src) {
		return;
	}
	struct memory_check dst = chk(pvm.x[1], pvm.x[2]);
	if (dst.mem) {
		return;
	}
	if (dst.changed) {
		src = chk(pvm.x[0], pvm.x[2]).mem;
	}
	memcpy(dst.mem->offset + pvm.x[1], src->offset + pvm.x[0], pvm.x[2]);
}
static void int_memory_move( INT_PARAMS) /* 49 */{
	if (pvm.x[2] < 0) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		return;
	}
	struct memory *src = chk(pvm.x[0], pvm.x[2]).mem;
	if (!src) {
		return;
	}
	struct memory_check dst = chk(pvm.x[1], pvm.x[2]);
	if (dst.mem) {
		return;
	}
	if (dst.changed) {
		src = chk(pvm.x[0], pvm.x[2]).mem;
	}
	memmove(dst.mem->offset + pvm.x[1], src->offset + pvm.x[0], pvm.x[2]);
}
static void int_memory_set( INT_PARAMS) /* 50 */{
	if (pvm.x[2] < 0) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		return;
	}
	struct memory *src = chk(pvm.x[0], pvm.x[2]).mem;
	if (!src) {
		return;
	}
	memset(src->offset + pvm.x[0], pvm.x[1], pvm.x[2]);
}
static void int_string_length( INT_PARAMS) /* 52 */{
	struct memory *str = chk(pvm.x[0], 1).mem;
	if (!str) {
		return;
	}
	num maxlen = str->end - pvm.x[0];
	num len = strnlen(str->offset + pvm.x[0], maxlen);
	if (len == maxlen) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
	} else {
		pvm.x[0] = len;
	}
}
static void int_string_compare( INT_PARAMS) /* 53 */{
	struct memory *str_a = chk(pvm.x[0], 1).mem;
	if (!str_a) {
		return;
	}
	struct memory_check str_b = chk(pvm.x[1], 1);
	if (!str_b.mem) {
		return;
	}
	if (!str_b.changed) {
		str_a = chk(pvm.x[0], 1).mem;
		if (!str_a) {
			return;
		}
	}
	num max_a_len = str_a->end - pvm.x[0];
	num max_b_len = str_b.mem->end - pvm.x[1];
	num max_len = max_a_len < max_b_len ? max_a_len : max_b_len;
	pvm.x[0] = strncmp(str_a->offset + pvm.x[0], str_b.mem->offset + pvm.x[1],
			max_len);
}
static inline num num_to_str(char *dest, num maxlen, num number, int base) {
#define nts_add0(c) \
	if (maxlen) { \
		maxlen--; \
		*(dest++) = c; \
	}
#define nts_add(c) index++; nts_add0(c);
	num n;
	num index = 0;
	if (number < 0) {
		nts_add('-');
		num nn = -(number / base);
		num mod = number % base;
		nts_add('0' - mod);
		n = nn;
	} else if (number) {
		n = number;
	} else {
		nts_add0('0');
		return 1;
	}
	while (n) {
		num nn = n / base;
		num mod = n % base;
		nts_add('0' + mod);
		n = nn;
	}
	return index;
#undef nts_add
#undef nts_add0
}
static void int_number_to_string( INT_PARAMS) /* 54 */{
	num len = pvm.x[3];
	if (len < 0) {
		pvm.x[1] = -1;
		pvm.err = PE_ILLEGAL_ARG;
		return;
	}
	if (pvm.x[2] < 2 || pvm.x[2] > 36) {
		pvm.x[1] = -1;
		pvm.err = PE_ILLEGAL_ARG;
		return;
	}
	num strlen = num_to_str(NULL, 0, pvm.x[0], pvm.x[2]);
	if (len == 0) {
		struct memory2 mem = alloc_memory(strlen + 1, 0);
		if (!mem.mem) {
			pvm.x[1] = -1;
			pvm.err = PE_OUT_OF_MEMORY;
			return;
		}
		num_to_str(mem.adr, strlen + 1, "%ld", pvm.x[0]);
		pvm.x[0] = strlen;
		pvm.x[1] = mem.mem->start;
		pvm.x[3] = strlen + 1;
	} else {
		struct memory *mem = chk(pvm.x[1], len).mem;
		if (!mem) {
			return;
		}
		if (len <= strlen) {
			if (pvm.x[1] != mem->start) {
				interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
				return;
			}
			mem = realloc_memory(pvm.x[1], strlen + 1, 0);
			if (!mem) {
				pvm.x[1] = -1;
				pvm.err = PE_OUT_OF_MEMORY;
				return;
			}
			pvm.x[1] = mem->start;
		}
		num_to_str(mem->offset + pvm.x[1], strlen + 1, "%ld", pvm.x[0]);
		pvm.x[0] = strlen;
		pvm.x[3] = strlen + 1;
	}
}
static void int_fpnumber_to_string( INT_PARAMS) /* 55 */{
	num len = pvm.x[3];
	if (len < 0) {
		pvm.x[1] = -1;
		pvm.err = PE_ILLEGAL_ARG;
		return;
	}
	num strlen = snprintf(NULL, 0, "%.10lg", pvm.fpx[0]);
	if (len == 0) {
		struct memory2 mem = alloc_memory(strlen + 1, 0);
		if (!mem.mem) {
			pvm.x[1] = -1;
			pvm.err = PE_OUT_OF_MEMORY;
			return;
		}
		sprintf(mem.adr, "%.10lg", pvm.fpx[0]);
		pvm.x[0] = strlen;
		pvm.x[1] = mem.mem->start;
		pvm.x[3] = strlen + 1;
	} else {
		struct memory *mem = chk(pvm.x[1], len).mem;
		if (!mem) {
			return;
		}
		if (len <= strlen) {
			if (pvm.x[1] != mem->start) {
				interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
				return;
			}
			mem = realloc_memory(pvm.x[1], strlen + 1, 0);
			if (!mem) {
				pvm.x[1] = -1;
				pvm.err = PE_OUT_OF_MEMORY;
				return;
			}
			pvm.x[1] = mem->start;
		}
		sprintf(mem->offset + pvm.x[1], "%.10lg", pvm.fpx[0]);
		pvm.x[0] = strlen;
		pvm.x[3] = strlen + 1;
	}
}
static void int_string_to_number( INT_PARAMS) /* 56 */{
	abort();
}
static void int_string_to_fpnumber( INT_PARAMS) /* 57 */{
	abort();
}
static void int_string_to_u16string( INT_PARAMS) /* 58 */{
	abort();
}
static void int_string_to_u32string( INT_PARAMS) /* 59 */{
	abort();
}
static void int_u16string_to_string( INT_PARAMS) /* 60 */{
	abort();
}
static void int_u32string_to_string( INT_PARAMS) /* 61 */{
	abort();
}
static void int_string_format( INT_PARAMS) /* 62 */{
	abort();
}
static void int_load_file( INT_PARAMS) /* 63 */{
	abort();
}
static void int_get_file( INT_PARAMS) /* 64 */{
	abort();
}
