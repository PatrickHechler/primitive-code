/*
 * pvm-int.h
 *
 *  Created on: Nov 9, 2022
 *      Author: pat
 */

#if defined SRC_PVM_INT_H_ | !defined PVM
#error "multpile includes of pvm-int.h or PVM is not defined!"
#endif
#define SRC_PVM_INT_H_

#ifdef PVM_DEBUG
#	define INT_PARAMS num int_num
#else
#	define INT_PARAMS
#endif // PVM_DEBUG

static void int_errors_illegal_interrupt( INT_PARAMS); /* 0 */
static void int_errors_unknown_command( INT_PARAMS); /* 1 */
static void int_errors_illegal_memory( INT_PARAMS); /* 2 */
static void int_errors_arithmetic_error( INT_PARAMS); /* 3 */
static void int_exit( INT_PARAMS); /* 4 */
static void int_memory_alloc( INT_PARAMS); /* 5 */
static void int_memory_realloc( INT_PARAMS); /* 6 */
static void int_memory_free( INT_PARAMS); /* 7 */
static void int_open_stream( INT_PARAMS); /* 8 */
static void int_streams_write( INT_PARAMS); /* 9 */
static void int_streams_read( INT_PARAMS); /* 10 */
static void int_streams_close( INT_PARAMS); /* 11 */
static void int_streams_get_pos( INT_PARAMS); /* 12 */
static void int_streams_seek_set( INT_PARAMS); /* 13 */
static void int_streams_seek_add( INT_PARAMS); /* 14 */
static void int_streams_seek_eof( INT_PARAMS); /* 15 */
static void int_open_element_file( INT_PARAMS); /* 16 */
static void int_open_element_folder( INT_PARAMS); /* 17 */
static void int_open_element_pipe( INT_PARAMS); /* 18 */
static void int_open_element( INT_PARAMS); /* 19 */
static void int_element_open_parent( INT_PARAMS); /* 20 */
static void int_element_get_create( INT_PARAMS); /* 21 */
static void int_element_get_last_mod( INT_PARAMS); /* 22 */
static void int_element_set_create( INT_PARAMS); /* 23 */
static void int_element_set_last_mod( INT_PARAMS); /* 24 */
static void int_element_delete( INT_PARAMS); /* 25 */
static void int_element_move( INT_PARAMS); /* 26 */
static void int_element_get_name( INT_PARAMS); /* 27 */
static void int_element_get_flags( INT_PARAMS); /* 28 */
static void int_element_mod_flags( INT_PARAMS); /* 29 */
static void int_folder_child_count( INT_PARAMS); /* 30 */
static void int_folder_get_child_of_name( INT_PARAMS); /* 31 */
static void int_folder_get_folder_of_name( INT_PARAMS); /* 32 */
static void int_folder_get_file_of_name( INT_PARAMS); /* 33 */
static void int_folder_get_pipe_of_name( INT_PARAMS); /* 34 */
static void int_folder_add_folder( INT_PARAMS); /* 35 */
static void int_folder_add_file( INT_PARAMS); /* 36 */
static void int_folder_add_pipe( INT_PARAMS); /* 37 */
static void int_folder_open_iter( INT_PARAMS); /* 38 */
static void int_file_length( INT_PARAMS); /* 39 */
static void int_file_truncate( INT_PARAMS); /* 40 */
static void int_handle_open_stream( INT_PARAMS); /* 41 */
static void int_pipe_length( INT_PARAMS); /* 42 */
static void int_time_get( INT_PARAMS); /* 43 */
static void int_time_res( INT_PARAMS); /* 44 */
static void int_time_sleep( INT_PARAMS); /* 45 */
static void int_time_wait( INT_PARAMS); /* 46 */
static void int_rnd_open( INT_PARAMS); /* 47 */
static void int_rnd_num( INT_PARAMS); /* 48 */
static void int_mem_cpy( INT_PARAMS); /* 49 */
static void int_mem_mov( INT_PARAMS); /* 50 */
static void int_mem_set( INT_PARAMS); /* 51 */
static void int_str_len( INT_PARAMS); /* 52 */
static void int_str_cmp( INT_PARAMS); /* 53 */
static void int_str_from_num( INT_PARAMS); /* 54 */
static void int_str_from_fpnum( INT_PARAMS); /* 55 */
static void int_str_to_num( INT_PARAMS); /* 56 */
static void int_str_to_fpnum( INT_PARAMS); /* 57 */
static void int_str_to_u16str( INT_PARAMS); /* 58 */
static void int_str_to_u32str( INT_PARAMS); /* 59 */
static void int_str_from_u16str( INT_PARAMS); /* 60 */
static void int_str_from_u32str( INT_PARAMS); /* 61 */
static void int_str_format( INT_PARAMS); /* 62 */
static void int_load_file( INT_PARAMS); /* 63 */
static void int_load_lib( INT_PARAMS); /* 64 */
static void int_unload_lib( INT_PARAMS); /* 65 */

#define INT_ERRORS_ILLEGAL_INTERRUPT                  0
#define INT_ERRORS_UNKNOWN_COMMAND                    1
#define INT_ERRORS_ILLEGAL_MEMORY                     2
#define INT_ERRORS_ARITHMETIC_ERROR                   3
#define INT_EXIT                                      4
// ...
#define INTERRUPT_COUNT                               66

static void (*(ints[]))(INT_PARAMS) = {
	int_errors_illegal_interrupt, /* 0 */
	int_errors_unknown_command, /* 1 */
	int_errors_illegal_memory, /* 2 */
	int_errors_arithmetic_error, /* 3 */
	int_exit, /* 4 */
	int_memory_alloc, /* 5 */
	int_memory_realloc, /* 6 */
	int_memory_free, /* 7 */
	int_open_stream, /* 8 */
	int_streams_write, /* 9 */
	int_streams_read, /* 10 */
	int_streams_close, /* 11 */
	int_streams_get_pos, /* 12 */
	int_streams_seek_set, /* 13 */
	int_streams_seek_add, /* 14 */
	int_streams_seek_eof, /* 15 */
	int_open_element_file, /* 16 */
	int_open_element_folder, /* 17 */
	int_open_element_pipe, /* 18 */
	int_open_element, /* 19 */
	int_element_open_parent, /* 20 */
	int_element_get_create, /* 21 */
	int_element_get_last_mod, /* 22 */
	int_element_set_create, /* 23 */
	int_element_set_last_mod, /* 24 */
	int_element_delete, /* 25 */
	int_element_move, /* 26 */
	int_element_get_name, /* 27 */
	int_element_get_flags, /* 28 */
	int_element_mod_flags, /* 29 */
	int_folder_child_count, /* 30 */
	int_folder_get_child_of_name, /* 31 */
	int_folder_get_folder_of_name, /* 32 */
	int_folder_get_file_of_name, /* 33 */
	int_folder_get_pipe_of_name, /* 34 */
	int_folder_add_folder, /* 35 */
	int_folder_add_file, /* 36 */
	int_folder_add_pipe, /* 37 */
	int_folder_open_iter, /* 38 */
	int_file_length, /* 39 */
	int_file_truncate, /* 40 */
	int_handle_open_stream, /* 41 */
	int_pipe_length, /* 42 */
	int_time_get, /* 43 */
	int_time_res, /* 44 */
	int_time_sleep, /* 45 */
	int_time_wait, /* 46 */
	int_rnd_open, /* 47 */
	int_rnd_num, /* 48 */
	int_mem_cpy, /* 49 */
	int_mem_mov, /* 50 */
	int_mem_set, /* 51 */
	int_str_len, /* 52 */
	int_str_cmp, /* 53 */
	int_str_from_num, /* 54 */
	int_str_from_fpnum, /* 55 */
	int_str_to_num, /* 56 */
	int_str_to_fpnum, /* 57 */
	int_str_to_u16str, /* 58 */
	int_str_to_u32str, /* 59 */
	int_str_from_u16str, /* 60 */
	int_str_from_u32str, /* 61 */
	int_str_format, /* 62 */
	int_load_file, /* 63 */
	int_load_lib, /* 64 */
	int_unload_lib, /* 65 */
};

_Static_assert((sizeof(void (*)(INT_PARAMS)) * INTERRUPT_COUNT) == sizeof(ints), "Error!");
