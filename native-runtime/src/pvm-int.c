//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
#ifndef PVM
#error "this file should be included inside of pvm-virtual-mashine.c"
#endif

#include <pfs/pfs-err.h>

#define check_string_len0(XNN_OFFSET, mem, name, max_len, error_hook) \
	num max_len = mem->end - pvm.x[XNN_OFFSET]; \
	char* name = mem->offset + pvm.x[XNN_OFFSET]; \
	num name##_len = strnlen(name, max_len); \
	if (name##_len == max_len) { \
		error_hook \
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0); \
		return;  \
	}

#define check_string_len(XNN_OFFSET, error_hook) check_string_len0(XNN_OFFSET, mem, name, max_len, error_hook)

#define write_error(msg) write(STDERR_FILENO, msg, strlen(msg))

static void int_error_illegal_interrupt( INT_PARAMS) {
	write_error("illegal interrupt\n");
	pvm_exit(128 + pvm.x[0]);
}
static void int_error_unknown_command( INT_PARAMS) {
	write_error("illegal command\n");
	pvm_exit(7);
}
static void int_error_illegal_memory( INT_PARAMS) {
	write_error("illegal memory\n");
	pvm_exit(6);
}
static void int_error_arithmetic_error( INT_PARAMS) {
	write_error("arithmetic error\n");
	pvm_exit(5);
}
static void int_exit( INT_PARAMS) {
	pvm_exit(pvm.x[0]);
}
static void int_memory_alloc( INT_PARAMS) {
	if (pvm.x[0] <= 0) {
		if (pvm.x[0] != 0) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		} else {
			pvm.x[0] = -1;
		}
		return;
	}
	struct memory2 mem = alloc_memory(pvm.x[0], 0);
	if (mem.mem) {
		pvm.x[0] = mem.mem->start;
	} else {
		pvm.err = PE_OUT_OF_MEMORY;
		pvm.x[0] = -1;
	}
}
static void int_memory_realloc( INT_PARAMS) {
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
		pvm.err = PE_OUT_OF_MEMORY;
		pvm.x[0] = -1;
	}
}
static void int_memory_free( INT_PARAMS) {
	free_memory(pvm.x[0]);
}
static void int_stream_open( INT_PARAMS) {
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, )
	pvm.x[0] = pfs_stream(name, pvm.x[1]);
}
static void int_stream_write( INT_PARAMS) {
	if (pvm.x[1] < 0) {
		pvm.x[1] = 0;
		pvm.err = PE_ILLEGAL_ARG;
		return;
	}
	struct memory *mem = chk(pvm.x[2], pvm.x[1]).mem;
	if (!mem) {
		return;
	}
	pvm.x[1] = pfs_stream_write(pvm.x[0], mem->offset + pvm.x[2], pvm.x[1]);
}
static void int_stream_read( INT_PARAMS) {
	if (pvm.x[1] < 0) {
		pvm.x[1] = 0;
		pvm.err = PE_ILLEGAL_ARG;
		return;
	}
	struct memory *mem = chk(pvm.x[2], pvm.x[1]).mem;
	if (!mem) {
		return;
	}
	pvm.x[1] = pfs_stream_read(pvm.x[0], mem->offset + pvm.x[2], pvm.x[1]);
}
static void int_stream_close( INT_PARAMS) {
	pvm.x[0] = pfs_stream_close(pvm.x[0]);
}
static void int_stream_file_get_pos( INT_PARAMS) {
	pvm.x[1] = pfs_stream_get_pos(pvm.x[0]);
}
static void int_stream_file_set_pos( INT_PARAMS) {
	pvm.x[1] = pfs_stream_set_pos(pvm.x[0], pvm.x[1]);
}
static void int_stream_file_add_pos( INT_PARAMS) {
	pvm.x[1] = pfs_stream_add_pos(pvm.x[0], pvm.x[1]);
}
static void int_stream_file_seek_eof( INT_PARAMS) {
	pvm.x[1] = pfs_stream_seek_eof(pvm.x[0]);
}
static void int_open_file( INT_PARAMS) {
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, )
	pvm.x[0] = pfs_handle_file(name);
}
static void int_open_folder( INT_PARAMS) {
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, )
	pvm.x[0] = pfs_handle_folder(name);
}
static void int_open_pipe( INT_PARAMS) {
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, )
	pvm.x[0] = pfs_handle_pipe(name);
}
static void int_open_element( INT_PARAMS) {
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(0, )
	pvm.x[0] = pfs_handle(name);
}
static void int_element_close( INT_PARAMS) {
	pvm.x[0] = pfs_element_close(pvm.x[0]);
}
static void int_element_open_parent( INT_PARAMS) {
	pvm.x[0] = pfs_element_parent(pvm.x[0]);
}
static void int_element_get_create( INT_PARAMS) {
	pvm.x[1] = pfs_element_get_create_time(pvm.x[0]);
}
static void int_element_get_last_mod( INT_PARAMS) {
	pvm.x[1] = pfs_element_get_last_modify_time(pvm.x[0]);
}
static void int_element_set_create( INT_PARAMS) {
	pvm.x[1] = pfs_element_set_create_time(pvm.x[0], pvm.x[1]);
}
static void int_element_set_last_mod( INT_PARAMS) {
	pvm.x[1] = pfs_element_set_last_modify_time(pvm.x[0], pvm.x[1]);
}
static void int_element_delete( INT_PARAMS) {
	pvm.x[0] = pfs_element_delete(pvm.x[0], pvm.x[1]);
}
static void int_element_move( INT_PARAMS) {
	if (pvm.x[1] != -1) {
		struct memory *mem = chk(pvm.x[1], 1).mem;
		if (!mem) {
			return;
		}
		check_string_len0(0, mem, new_name, max_len, )
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
static inline void int_element_get_name__path_impl(int (*get_name)(int eh, char **name_buf, i64 *buf_len)) {
	if (pvm.x[1] == -1) {
		char *buf = NULL;
		num size = 0;
		if (get_name(pvm.x[0], &buf, &size)) {
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
			if (get_name(pvm.x[0], &buf, &size)) {
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
			if (!get_name(pvm.x[0], &buf, &size)) {
				pvm.x[1] = -1;
				return;
			}
			num maxsize = mem->end - pvm.x[1];
			if (maxsize > size) {
				free(buf);
				interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
				return;
			}
			memcpy(mem->offset + pvm.x[1], buf, size);
			free(buf);
		}
	}
}
static void int_element_get_name( INT_PARAMS) {
	int_element_get_name__path_impl(pfs_element_get_name);
}
static void int_element_get_path(INT_PARAMS){
	int_element_get_name__path_impl(pfs_element_path0);
}
static void int_element_get_fs_path(INT_PARAMS){
	int_element_get_name__path_impl(pfs_element_fs_path0);
}
static void int_element_get_mount(INT_PARAMS){
	pvm.x[1] = pfs_mount_get_mount_point(pvm.x[0]);
}
static void int_element_get_flags( INT_PARAMS) {
	pvm.x[1] = pfs_element_get_flags(pvm.x[0]);
}
static void int_element_modify_flags( INT_PARAMS) {
	pvm.x[1] = pfs_element_modify_flags(pvm.x[0], 0xFFFF & pvm.x[1],
			0xFFFF & pvm.x[2]);
}
static void int_folder_child_count( INT_PARAMS) {
	pvm.x[1] = pfs_folder_child_count(pvm.x[0]);
}
static inline void int_folder_open_child__of_name_impl(int (*get_child)(int eh, const char *name)) {
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, )
	pvm.x[1] = get_child(pvm.x[0], name);
}
static void int_folder_open_child_of_name( INT_PARAMS) {
	int_folder_open_child__of_name_impl(pfs_folder_child);
}
static void int_folder_open_child_folder_of_name( INT_PARAMS) {
	int_folder_open_child__of_name_impl(pfs_folder_child_folder);
}
static void int_folder_open_child_mount_of_name( INT_PARAMS) {
	int_folder_open_child__of_name_impl(pfs_folder_child_mount);
}
static void int_folder_open_child_file_of_name( INT_PARAMS) {
	int_folder_open_child__of_name_impl(pfs_folder_child_file);
}
static void int_folder_open_child_pipe_of_name( INT_PARAMS) {
	int_folder_open_child__of_name_impl(pfs_folder_child_pipe);
}
static void int_folder_open_desc_of_path(INT_PARAMS){
	int_folder_open_child__of_name_impl(pfs_folder_descendant);
}
static void int_folder_open_desc_folder_of_path(INT_PARAMS){
	int_folder_open_child__of_name_impl(pfs_folder_descendant_folder);
}
static void int_folder_open_desc_mount_of_path(INT_PARAMS){
	int_folder_open_child__of_name_impl(pfs_folder_descendant_mount);
}
static void int_folder_open_desc_file_of_path(INT_PARAMS){
	int_folder_open_child__of_name_impl(pfs_folder_descendant_file);
}
static void int_folder_open_desc_pipe_of_path(INT_PARAMS){
	int_folder_open_child__of_name_impl(pfs_folder_descendant_pipe);
}

static inline void int_folder_create_child_impl(int (*create_child)(int eh, const char *name), _Bool is_mount, int (*create_mount_child)(int eh, const char *name, i64 block_count, i32 block_size)) {
	struct memory *mem = chk(pvm.x[1], 1).mem;
	if (!mem) {
		return;
	}
	check_string_len(1, )
	if (is_mount) {
		pvm.x[1] = create_child(pvm.x[0], name);
	} else {
		pvm.x[1] = create_mount_child(pvm.x[0], name, pvm.x[2], pvm.x[3]);
	}
}
static void int_folder_create_child_folder( INT_PARAMS) {
	int_folder_create_child_impl(pfs_folder_create_folder, 0, NULL);
}
static void int_folder_create_child_file( INT_PARAMS) {
	int_folder_create_child_impl(pfs_folder_create_file, 0, NULL);
}
static void int_folder_create_child_pipe( INT_PARAMS) {
	int_folder_create_child_impl(pfs_folder_create_pipe, 0, NULL);
}
static void int_folder_create_child_mount_tmp( INT_PARAMS) {
	int_folder_create_child_impl(NULL, 1, pfs_folder_create_mount_temp);
}
static void int_folder_create_child_mount_intern( INT_PARAMS) {
	int_folder_create_child_impl(NULL, 1, pfs_folder_create_mount_intern);
}
static void int_folder_open_iter( INT_PARAMS) {
	pvm.x[1] = pfs_folder_open_iter(pvm.x[0], pvm.x[1] != 0);
}
static void int_folder_iter_next( INT_PARAMS) {
	pvm.x[0] = pfs_iter_next(pvm.x[0]);
}
static void int_folder_iter_close( INT_PARAMS) {
	pfs_iter_close(pvm.x[0]);
}
static void int_file_length( INT_PARAMS) {
	pvm.x[1] = pfs_file_length(pvm.x[0]);
}
static void int_file_truncate( INT_PARAMS) {
	pvm.x[1] = pfs_file_truncate(pvm.x[0], pvm.x[1]);
}
static void int_handle_open_stream( INT_PARAMS) {
	pvm.x[1] = pfs_open_stream(pvm.x[0], pvm.x[1]);
}
static void int_pipe_length( INT_PARAMS) {
	pvm.x[1] = pfs_pipe_length(pvm.x[0]);
}
_Static_assert(offsetof(struct timespec, tv_sec)
== 0, "Error!");
_Static_assert(offsetof(struct timespec, tv_nsec) == 8, "Error!");

static void int_time_get( INT_PARAMS) {
	struct timespec *pntr = (struct timespec*) &pvm.x[0];
	if (clock_gettime(CLOCK_REALTIME, pntr) == -1) {
		pvm.x[0] = 0;
	} else {
		pvm.x[2] = 1;
	}
}
static void int_time_res( INT_PARAMS) {
	struct timespec *pntr = (struct timespec*) &pvm.x[0];
	if (clock_getres(CLOCK_REALTIME, pntr) == -1) {
		pvm.x[0] = 0;
	} else {
		pvm.x[2] = 1;
	}
}
static void int_time_sleep( INT_PARAMS) {
	struct timespec *pntr = (struct timespec*) &pvm.x[0];
	if (nanosleep(pntr, pntr) == -1) {
		switch (errno) {
		case EINVAL:
			pvm.err = PE_ILLEGAL_ARG;
			break;
		default:
			pvm.err = PE_UNKNOWN_ERROR;
		}
		pvm.x[2] = 0;
	} else {
		pvm.x[2] = 1;
	}
}
static void int_time_wait( INT_PARAMS) {
	struct timespec *pntr = (struct timespec*) &pvm.x[0];
	if (clock_nanosleep(CLOCK_REALTIME, TIMER_ABSTIME, pntr, NULL) == -1) {
		switch (errno) {
		case EINVAL:
			pvm.err = PE_ILLEGAL_ARG;
			break;
		default:
			pvm.err = PE_UNKNOWN_ERROR;
		}
		pvm.x[2] = 0;
	} else {
		pvm.x[2] = 1;
	}
}
static i64 rnd_read(struct delegate_stream *str, void *buf, const i64 len) {
	random_data(buf, len);
	return len;
}
static struct delegate_stream rnd_str = { .read = rnd_read };
static void int_rnd_open( INT_PARAMS) {
	pvm.x[0] = pfs_stream_open_delegate(&rnd_str);
}
static void int_rnd_num( INT_PARAMS) {
	pvm.x[0] = random_num();
}
static void int_mem_cmp( INT_PARAMS) {
	if (pvm.x[2] < 0) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	struct memory *mem_a = chk(pvm.x[0], pvm.x[2]).mem;
	if (!mem_a) {
		return;
	}
	struct memory_check mem_b = chk(pvm.x[1], pvm.x[2]);
	if (!mem_b.mem) {
		return;
	}
	if (!mem_b.changed) {
		mem_a = chk(pvm.x[0], pvm.x[2]).mem;
		if (!mem_a) {
			return;
		}
	}
	num max_a_len = mem_a->end - pvm.x[0];
	num max_b_len = mem_b.mem->end - pvm.x[1];
	if (max_a_len < pvm.x[2] || max_b_len < pvm.x[2]) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	pvm.x[2] = memcmp(mem_a->offset + pvm.x[0], mem_b.mem->offset + pvm.x[1],
			pvm.x[2]);
}
static void int_mem_cpy( INT_PARAMS) {
	if (pvm.x[2] < 0) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
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
	int cmp = memcmp(dst.mem->offset + pvm.x[1], src->offset + pvm.x[0],
			pvm.x[2]);
	if (cmp > 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (cmp < 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER)) | S_EQUAL;
	}
}
static void int_mem_mov( INT_PARAMS) {
	if (pvm.x[2] < 0) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
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
static void int_mem_bset( INT_PARAMS) {
	if (pvm.x[2] < 0) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	struct memory *src = chk(pvm.x[0], pvm.x[2]).mem;
	if (!src) {
		return;
	}
	memset(src->offset + pvm.x[0], 0xFF & pvm.x[1], pvm.x[2]);
}
static void int_str_len( INT_PARAMS) {
	struct memory *str = chk(pvm.x[0], 1).mem;
	if (!str) {
		return;
	}
	num maxlen = str->end - pvm.x[0];
	num len = strnlen(str->offset + pvm.x[0], maxlen);
	if (len == maxlen) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
	} else {
		pvm.x[0] = len;
	}
}
static void int_str_index( INT_PARAMS) {
	struct memory *str = chk(pvm.x[0], 1).mem;
	if (!str) {
		return;
	}
	num maxlen = str->end - pvm.x[0];
	num len = strnlen(str->offset + pvm.x[0], maxlen);
	if (len == maxlen) {
		void *found = memchr(str->offset + pvm.x[0], pvm.x[1], maxlen);
		if (found) { // lucky code
			pvm.x[0] = found - (str->offset + pvm.x[0]);
		} else { // unlucky code
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		}
	} else {
		void *found = strchr(str->offset + pvm.x[0], pvm.x[1]);
		if (found) { // lucky code
			pvm.x[0] = found - (str->offset + pvm.x[0]);
		} else {
			pvm.x[0] = -1;
		}
	}
}
static void int_str_cmp( INT_PARAMS) {
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
	if (strnlen(str_a->offset + pvm.x[0], max_a_len) == max_a_len
			|| strnlen(str_b.mem->offset + pvm.x[1], max_b_len) == max_b_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	int cmp = strcmp(str_a->offset + pvm.x[0], str_b.mem->offset + pvm.x[1]);
	if (cmp > 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (cmp < 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER)) | S_EQUAL;
	}
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
		num mod = -(number % base);
		if (mod < 10) {
			nts_add('0' + mod);
		} else {
			nts_add('A' + mod);
		}
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
		if (mod < 10) {
			nts_add('0' + mod);
		} else {
			nts_add('A' + mod);
		}
		n = nn;
	}
	return index;
#undef nts_add
#undef nts_add0
}
static void int_str_from_num( INT_PARAMS) {
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
		num_to_str(mem.adr, strlen + 1, pvm.x[0], pvm.x[2]);
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
				interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
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
		num_to_str(mem->offset + pvm.x[1], strlen + 1, pvm.x[0], pvm.x[2]);
		pvm.x[0] = strlen;
		pvm.x[3] = strlen + 1;
	}
}
static void int_str_from_fpnum( INT_PARAMS) {
	num len = pvm.x[3];
	if (len < 0) {
		pvm.x[1] = -1;
		pvm.err = PE_ILLEGAL_ARG;
		return;
	}
	num strlen = snprintf(NULL, 0, "%.10g", pvm.fpx[0]);
	if (len == 0) {
		struct memory2 mem = alloc_memory(strlen + 1, 0);
		if (!mem.mem) {
			pvm.x[1] = -1;
			pvm.err = PE_OUT_OF_MEMORY;
			return;
		}
		sprintf(mem.adr, "%.10g", pvm.fpx[0]);
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
				interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
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
		sprintf(mem->offset + pvm.x[1], "%.10g", pvm.fpx[0]);
		pvm.x[0] = strlen;
		pvm.x[3] = strlen + 1;
	}
}
static void int_str_to_num( INT_PARAMS) {
	if (pvm.x[2] < 2 || pvm.x[2] > 23) {
		pvm.err = PE_ILLEGAL_ARG;
		pvm.x[1] = 0;
		return;
	}
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	num max_len = mem->end - pvm.x[0];
	char *str = mem->offset + pvm.x[0];
	if (isspace(*str)) {
		pvm.err = PE_ILLEGAL_ARG;
		pvm.x[1] = 0;
		return;
	}
	num str_len = strnlen(str, max_len);
	if (str_len == max_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	char *end;
	errno = 0;
	num val = strtol(str, &end, pvm.x[2]);
	if (errno) {
		pvm.x[0] = 0;
		if (errno == ERANGE) {
			pvm.err = PE_OUT_OF_RANGE;
		} else if (errno == EINVAL) {
			pvm.err = PE_ILLEGAL_ARG;
		} else {
			pvm.err = PE_UNKNOWN_ERROR;
		}
	} else if (*end) {
		pvm.err = PE_ILLEGAL_ARG;
		pvm.x[0] = 0;
	} else {
		pvm.x[0] = 1;
	}
	pvm.x[0] = val;
}
static void int_str_to_fpnum( INT_PARAMS) {
	struct memory *mem = chk(pvm.x[0], 1).mem;
	if (!mem) {
		return;
	}
	num max_len = mem->end - pvm.x[0];
	const char *str = mem->offset + pvm.x[0];
	if (isspace(*str)) {
		pvm.err = PE_ILLEGAL_ARG;
		pvm.x[1] = 0;
		return;
	}
	num str_len = strnlen(str, max_len);
	if (str_len == max_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	char *end;
	errno = 0;
	fpnum val = strtod(str, &end);
	if (errno) {
		pvm.x[0] = 0;
		if (errno == ERANGE) {
			pvm.err = PE_OUT_OF_RANGE;
		} else if (errno == EINVAL) {
			pvm.err = PE_ILLEGAL_ARG;
		} else {
			pvm.err = PE_UNKNOWN_ERROR;
		}
	} else if (*end) {
		pvm.err = PE_ILLEGAL_ARG;
		pvm.x[0] = 0;
	} else {
		pvm.x[0] = 1;
	}
	pvm.fpx[0] = val;
}
static inline num simple_len(const char *string, num max_len, int size) {
	num cur_len = 0;
	while (1) {
		for (int i = size; i; i--) {
			if (i > max_len - cur_len) {
				return max_len;
			}
			if (string[cur_len]) {
				cur_len += size;
				break;
			}
		}
		return cur_len;
	}
}
static inline void conv_unicode_strings(const char *to_identy,
		const char *from_identy, int from_size) {
	if (pvm.x[2] < 0) {
		pvm.err = PE_ILLEGAL_ARG;
		pvm.x[1] = -1;
		return;
	}
	struct memory *in_mem = chk(pvm.x[0], 1).mem;
	if (!in_mem) {
		return;
	}
	num max_in_len = in_mem->end - pvm.x[0];
	char *in_str = in_mem->offset + pvm.x[0];
	num in_len = simple_len(in_str, max_in_len, from_size);
	if (in_len == max_in_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	struct memory_check out_mem = chk(pvm.x[1], pvm.x[2]);
	if (!out_mem.mem) {
		return;
	}
	if (out_mem.changed) {
		in_mem = chk(pvm.x[0], 1).mem;
		if (!in_mem) {
			return;
		}
		max_in_len = in_mem->end - pvm.x[0];
		in_str = in_mem->offset + pvm.x[0];
		in_len = simple_len(in_str, max_in_len, from_size);
		if (in_len == max_in_len) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			return;
		}
	}
	char *out_str = out_mem.mem->offset + pvm.x[1];
	in_len++; // include \0
	iconv_t cd = iconv_open(to_identy, from_identy);
	if (cd == (iconv_t) -1) {
		pvm.err = PE_UNKNOWN_ERROR;
		pvm.x[1] = -1;
		return;
	}
	pvm.x[3] = iconv(cd, &in_str, &in_len, &out_str, &pvm.x[2]);
	iconv_close(cd);
}
#define identy_str_std "UTF-8"
#define identy_str_u16 "UTF-16"
#define identy_str_u32 "UTF-32"
static void int_str_to_u16str( INT_PARAMS) {
	conv_unicode_strings(identy_str_u16, identy_str_std, 1);
}
static void int_str_to_u32str( INT_PARAMS) {
	conv_unicode_strings(identy_str_u32, identy_str_std, 1);
}
static void int_str_from_u16str( INT_PARAMS) {
	conv_unicode_strings(identy_str_std, identy_str_u16, 2);
}
static void int_str_from_u32str( INT_PARAMS) {
	conv_unicode_strings(identy_str_std, identy_str_u32, 4);
}
#undef identy_str_std
#undef identy_str_u16
#undef identy_str_u32
static void int_str_format( INT_PARAMS) {
	struct memory *input_mem = chk(pvm.x[0], 1).mem;
	if (!input_mem) {
		return;
	}
	struct memory_check arg_mem = chk(pvm.x[3], 0);
	if (!arg_mem.mem) {
		return;
	}
	if (arg_mem.changed) {
		input_mem = chk(pvm.x[0], 1).mem;
		if (!input_mem) {
			return;
		}
	}
	num remain_out = pvm.x[2];
	char *out;
	if (remain_out) {
		if (remain_out < 0) {
			pvm.err = PE_ILLEGAL_ARG;
			pvm.x[0] = -1;
			return;
		}
		struct memory_check out_mem = chk(pvm.x[1], remain_out);
		if (!out_mem.mem) {
			return;
		}
		if (out_mem.changed) {
			input_mem = chk(pvm.x[0], 1).mem;
			if (!input_mem) {
				return;
			}
			arg_mem = chk(pvm.x[3], 0);
			if (!arg_mem.mem) {
				return;
			}
		}
		out = out_mem.mem->offset + pvm.x[1];
	}
	const char *in = input_mem->offset + pvm.x[0];
	num max_in_len = input_mem->end - pvm.x[0];
	num in_len = strnlen(in, max_in_len);
	if (in_len == max_in_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	num remain_args = (arg_mem.mem->end - pvm.x[3]) >> 3;
	void *arg_pntr = arg_mem.mem->offset + pvm.x[3];
	num len = 0;
#define add0(c) \
	if (remain_out) { \
		remain_out--; \
		*(out++) = c; \
	}
#define add(c) len ++; add0(c);
	for (; *in; in++) {
		if (*in != '%') {
			add(*in);
			continue;
		}
		in++;
		if (*in == '%') {
			add('%');
			continue;
		}
		if (!remain_args) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			return;
		}
		switch (*in) {
		case 's': {
			num pntr_num = *(num*) arg_pntr;
			struct memory_check str_mem = chk(pntr_num, 1);
			if (!str_mem.mem) {
				return;
			}
			if (str_mem.changed) {
				input_mem = chk(pvm.x[0], 1).mem;
				if (!input_mem) {
					return;
				}
				arg_mem = chk(pvm.x[3], 0);
				if (!arg_mem.mem) {
					return;
				}
				if (remain_out) {
					struct memory *out_mem = chk(pvm.x[1], remain_out).mem;
					out = out_mem->offset + pvm.x[1] + len;
				}
			}
			num max_str_len = str_mem.mem->end - pntr_num;
			char *str_pntr = str_mem.mem->offset + pntr_num;
			num str_len = strnlen(str_pntr, max_str_len);
			if (str_len == max_str_len) {
				interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
				return;
			}
			len += str_len;
			if (remain_out) {
				num cpy_len = remain_out > str_len ? str_len : remain_out;
				memmove(out, str_pntr, cpy_len);
				remain_out -= cpy_len;
				out += cpy_len;
			}
			break;
		}
		case 'c': {
			add(*(char* )arg_pntr);
			break;
		}
		case 'n': {
			num base = *(num*) arg_pntr;
			remain_args--;
			arg_pntr += sizeof(num);
			if (!remain_args) {
				interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
				return;
			}
			if (base < 2 || base > 36) {
				pvm.x[0] = -1;
				pvm.err = PE_ILLEGAL_ARG;
				return;
			}
			num num_str_len = num_to_str(out, remain_out, *(num*) arg_pntr,
					base);
			len += num_str_len;
			out += num_str_len;
			if (num_str_len > remain_out) {
				remain_out = 0;
			} else {
				remain_out -= num_str_len;
			}
			break;
		}
		case 'd': {
			num num_str_len = num_to_str(out, remain_out, *(num*) arg_pntr, 10);
			len += num_str_len;
			out += num_str_len;
			if (num_str_len > remain_out) {
				remain_out = 0;
			} else {
				remain_out -= num_str_len;
			}
			break;
		}
		case 'f': {
			num num_str_len = snprintf(out, remain_out, "%.10g",
					*(fpnum*) arg_pntr);
			len += num_str_len;
			out += num_str_len;
			if (num_str_len > remain_out) {
				remain_out = 0;
			} else {
				remain_out -= num_str_len;
			}
			break;
		}
		case 'p': {
			num pntr = *(num*) arg_pntr;
			add('p');
			add('-');
			if (pntr == -1) {
				add('i');
				add('n');
				add('v');
				add('a');
				add('l');
			} else {
				num num_str_len = num_to_str(out, remain_out, *(num*) arg_pntr,
						16);
				len += num_str_len;
				out += num_str_len;
				if (num_str_len > remain_out) {
					remain_out = 0;
				} else {
					remain_out -= num_str_len;
				}
			}
			break;
		}
		case 'h': {
			num num_str_len = num_to_str(out, remain_out, *(num*) arg_pntr, 16);
			len += num_str_len;
			out += num_str_len;
			if (num_str_len > remain_out) {
				remain_out = 0;
			} else {
				remain_out -= num_str_len;
			}
			break;
		}
		case 'b': {
			num num_str_len = num_to_str(out, remain_out, *(num*) arg_pntr, 2);
			len += num_str_len;
			out += num_str_len;
			if (num_str_len > remain_out) {
				remain_out = 0;
			} else {
				remain_out -= num_str_len;
			}
			break;
		}
		case 'o': {
			num num_str_len = num_to_str(out, remain_out, *(num*) arg_pntr, 8);
			len += num_str_len;
			out += num_str_len;
			if (num_str_len > remain_out) {
				remain_out = 0;
			} else {
				remain_out -= num_str_len;
			}
			break;
		}
		default:
			pvm.x[0] = -1;
			pvm.err = PE_ILLEGAL_ARG;
			return;
		}
		remain_args--;
		arg_pntr += 8;
	}
#undef add
#undef add0
}
static void int_load_file( INT_PARAMS) {
	struct memory *name_mem = chk(pvm.x[0], 1).mem;
	if (!name_mem) {
		return;
	}
	num max_name_len = name_mem->end - pvm.x[0];
	const char *name = name_mem->offset + pvm.x[0];
	num name_len = strnlen(name, max_name_len);
	if (name_len == max_name_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	int sh = pfs_stream(name, PFS_SO_FILE | PFS_SO_READ);
	if (sh == -1) {
		pvm.x[0] = -1;
		return;
	}
	i64 eof = pfs_stream_seek_eof(sh);
	if (eof == -1) {
		pvm.x[0] = -1;
		return;
	}
	if (!pfs_stream_set_pos(sh, 0)) {
		pvm.x[0] = -1;
		return;
	}
	struct memory2 file_mem = alloc_memory(eof, 0);
	if (!file_mem.mem) {
		pvm.x[0] = -1;
		return;
	}
	i64 reat = pfs_stream_read(sh, file_mem.adr, eof);
	if (reat != eof) {
		free_memory(file_mem.mem->start);
		pvm.x[0] = -1;
		return;
	}
	pvm.x[0] = file_mem.mem->start;
	pvm.x[1] = eof;
}

static int loaded_libs_equal(const void *a, const void *b) {
	const struct loaded_libs_entry *ea = a, *eb = b;
	return strcmp(ea->name, eb->name) == 0;
} // hashes a structure which starts with a pointer to a string
static uint64_t string_hash(const void *a) {
	const unsigned char *cs = *(unsigned char**) a;
	uint64_t res = 0;
	while (1) {
		if (*cs == '\0')
			break;
		res ^= *(cs++);
		if (*cs == '\0')
			break;
		res ^= (*(cs++)) << 8;
		if (*cs == '\0')
			break;
		res ^= (*(cs++)) << 16;
		if (*cs == '\0')
			break;
		res ^= (*(cs++)) << 24;
		if (*cs == '\0')
			break;
		res ^= (uint64_t) (*(cs++)) << 32;
		if (*cs == '\0')
			break;
		res ^= (uint64_t) (*(cs++)) << 40;
		if (*cs == '\0')
			break;
		res ^= (uint64_t) (*(cs++)) << 48;
		if (*cs == '\0')
			break;
		res ^= (uint64_t) (*(cs++)) << 56;
	}
	return res;
}
static int unload_lib(void *arg0, void *element) {
	struct memory *mem = (struct memory*) arg0;
	struct loaded_libs_entry *entry = element;
	if (entry->pntr != mem->start) {
		return 1;
	}
	if (hashset_remove(&loaded_libs, string_hash(entry), entry) != entry) {
		abort();
	}
	free(entry->name);
	free(entry);
	free_mem_impl(mem);
	return 0;
}
static void int_load_lib( INT_PARAMS) {
	struct memory *name_mem = chk(pvm.x[0], 1).mem;
	if (!name_mem) {
		return;
	}
	num max_name_len = name_mem->end - pvm.x[0];
	char *name = name_mem->offset + pvm.x[0];
	num name_len = strnlen(name, max_name_len);
	if (name_len == max_name_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	void *old = hashset_get(&loaded_libs, string_hash(&name), &name);
	if (old) {
		struct loaded_libs_entry *entry = old;
		struct memory *old_mem = chk(entry->pntr, 0).mem;
		pvm.x[0] = old_mem->start;
		pvm.x[1] = old_mem->end - old_mem->start;
		pvm.x[2] = 0;
		return;
	}
	int fh = pfs_handle_file(name);
	if (fh == -1) {
		pvm.x[0] = -1;
		return;
	}
	char *name2 = pfs_element_path(fh);
	if (name2 == NULL) {
		pfs_element_close(fh);
		pvm.x[0] = -1;
		return;
	}
	if (strcmp(name, name2)) {
		old = hashset_get(&loaded_libs, string_hash(&name2), &name2);
		if (old) {
			free(name2);
			struct loaded_libs_entry *entry = old;
			struct memory *old_mem = chk(entry->pntr, 0).mem;
			pvm.x[0] = old_mem->start;
			pvm.x[1] = old_mem->end - old_mem->start;
			pvm.x[2] = 0;
			return;
		}
	}
	name = name2;
	int sh = pfs_open_stream(fh, PFS_SO_FILE | PFS_SO_READ | PFS_SO_FILE_EOF);
	if (sh == -1) {
		pvm.x[0] = -1;
		return;
	}
	pfs_element_close(fh);
	i64 eof = pfs_stream_get_pos(sh);
	if (eof == -1) {
		pvm.x[1] = -1;
		return;
	}
	if (!pfs_stream_set_pos(sh, 0)) {
		pvm.x[1] = -1;
		return;
	}
	struct memory2 lib_mem = alloc_memory(eof, MEM_LIB_ALL);
	if (!lib_mem.mem) {
		pvm.x[1] = -1;
		return;
	}
	i64 reat = pfs_stream_read(sh, lib_mem.adr, eof);
	if (reat != eof) {
		free_memory(lib_mem.mem->start);
		pvm.x[1] = -1;
		if (!pvm.err) {
			pvm.err = PE_IO_ERR;
		}
		return;
	}
	struct loaded_libs_entry *new_entry = malloc(
			sizeof(struct loaded_libs_entry));
	if (!new_entry) {
		free(name);
		pvm.x[1] = -1;
		errno = 0;
		pvm.err = PE_OUT_OF_MEMORY;
		return;
	}
	new_entry->name = name;
	new_entry->pntr = lib_mem.mem->start;
	old = hashset_put(&loaded_libs, string_hash(&new_entry), &new_entry);
	if (old) {
		abort();
	}
	if (pvm.x[1] != -1) {
		if (pvm.x[1] < 0) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			return;
		}
		num new_ip;
		if (pvm.x[2]) {
			num offset = *(num*) (lib_mem.adr + pvm.x[1]);
			if (offset == -1) {
				goto end;
			}
			if ((offset < 0)
					|| ((lib_mem.mem->end - offset - 8) < lib_mem.mem->start)) {
				interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
				return;
			}
			new_ip = lib_mem.mem->start + offset;
		} else {
			new_ip = lib_mem.mem->start + pvm.x[1];
		}
		struct memory *stack_mem = chk(pvm.sp, 8).mem;
		if (!stack_mem) { // unload the file again, because it could not be initialized
			hashset_for_each(&loaded_libs, unload_lib,
					(void*) lib_mem.mem->start);
			return;
		}
		*(num*) (stack_mem->offset + pvm.sp) = pvm.ip;
		pvm.sp += 8;
		pvm.ip = new_ip;
		if ((lib_mem.mem->end - pvm.x[1] - 8) < lib_mem.mem->start) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			return;
		}
	}
	end: ;
	pvm.x[0] = lib_mem.mem->start;
	pvm.x[1] = eof;
	pvm.x[2] = 1;
}
static void int_create_lib( INT_PARAMS) {
	struct memory *name_mem = chk(pvm.x[0], 1).mem;
	if (!name_mem) {
		return;
	}
	num max_name_len = name_mem->end - pvm.x[0];
	char *name = name_mem->offset + pvm.x[0];
	num name_len = strnlen(name, max_name_len);
	if (name_len == max_name_len) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	void *old = hashset_get(&loaded_libs, string_hash(&name), &name);
	if (old) {
		struct loaded_libs_entry *entry = old;
		struct memory *old_mem = chk(entry->pntr, 0).mem;
		pvm.x[0] = old_mem->start;
		pvm.x[1] = old_mem->end - old_mem->start;
		pvm.x[2] = 0;
		return;
	}
	num err = pvm.err;
	int fh = pfs_handle(name);
	if (fh != -1) {
		char *name2 = pfs_element_path(fh);
		pfs_element_close(fh);
		if (!name2) {
			pvm.x[1] = -1;
			return;
		}
		if (strcmp(name, name2)) {
			old = hashset_get(&loaded_libs, string_hash(&name2), &name2);
			if (old) {
				free(name2);
				struct loaded_libs_entry *entry = old;
				struct memory *old_mem = chk(entry->pntr, 0).mem;
				pvm.x[0] = old_mem->start;
				pvm.x[1] = old_mem->end - old_mem->start;
				pvm.x[2] = 0;
				return;
			}
		}
		name = name2;
	} else {
		pfs_element_close(fh);
		char *name2 = malloc(name_len + 1);
		if (!name2) {
			errno = 0;
			pvm.err = PE_OUT_OF_MEMORY;
			pvm.x[1] = -1;
			return;
		}
		memcpy(name2, name, name_len + 1);
		name = name2;
	}
	pvm.err = err;
	struct memory *new_lib_mem = chk(pvm.x[1], 1).mem;
	if (!new_lib_mem) {
		pvm.x[1] = -1;
		return;
	}
	if (new_lib_mem->flags & MEM_LIB_CREATE_FORBIDDEN_MASK) {
		pvm.err = PFS_ERRNO_ILLEGAL_ARG;
		pvm.x[1] = -1;
		return;
	}
	struct loaded_libs_entry *new_entry = malloc(
			sizeof(struct loaded_libs_entry));
	if (!new_entry) {
		free(name);
		errno = 0;
		pvm.err = PE_OUT_OF_MEMORY;
		pvm.x[1] = -1;
		return;
	}
	new_entry->name = name;
	new_entry->pntr = new_lib_mem->start;
	old = hashset_put(&loaded_libs, string_hash(&new_entry), &new_entry);
	if (old) {
		abort();
	}
	end: ;
	new_lib_mem->flags |= MEM_LIB_ALL;
	pvm.x[0] = new_lib_mem->start;
	pvm.x[1] = new_lib_mem->end - new_lib_mem->start;
	pvm.x[2] = 1;
}
static void int_unload_lib( INT_PARAMS) {
	struct memory *mem = chk(pvm.x[0], 0).mem;
	if ((mem->flags & MEM_LIB) == 0) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	hashset_for_each(&loaded_libs, unload_lib, (void*) mem->start);
}
