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
#	define INT_PARAMS (num int_num)
#else
#	define INT_PARAMS ()
#endif // PVM_DEBUG

static void int_errors_illegal_interrupt INT_PARAMS; /* 0  */
static void int_errors_unknown_command INT_PARAMS; /* 1  */
static void int_errors_illegal_memory INT_PARAMS; /* 2  */
static void int_errors_arithmetic_error INT_PARAMS; /* 3  */
static void int_exit INT_PARAMS; /* 4  */
static void int_memory_alloc INT_PARAMS; /* 5  */
static void int_memory_realloc INT_PARAMS; /* 6  */
static void int_memory_free INT_PARAMS; /* 7  */
static void int_open_stream INT_PARAMS; /* 8  */
static void int_streams_write INT_PARAMS; /* 9  */
static void int_streams_read INT_PARAMS; /* 10 */
static void int_streams_close INT_PARAMS; /* 11 */
static void int_streams_get_pos INT_PARAMS; /* 12 */
static void int_streams_seek_set INT_PARAMS; /* 13 */
static void int_streams_seek_add INT_PARAMS; /* 14 */
static void int_streams_seek_eof INT_PARAMS; /* 15 */
static void int_open_element_file INT_PARAMS; /* 16 */
static void int_open_element_folder INT_PARAMS; /* 17 */
static void int_open_element_pipe INT_PARAMS; /* 18 */
static void int_open_element INT_PARAMS; /* 19 */
static void int_element_open_parent INT_PARAMS; /* 20 */
static void int_element_get_create INT_PARAMS; /* 21 */
static void int_element_get_last_mod INT_PARAMS; /* 22 */
static void int_element_set_create INT_PARAMS; /* 23 */
static void int_element_set_last_mod INT_PARAMS; /* 24 */
static void int_element_delete INT_PARAMS; /* 25 */
static void int_element_move INT_PARAMS; /* 26 */
static void int_element_get_name INT_PARAMS; /* 27 */
static void int_element_get_flags INT_PARAMS; /* 28 */
static void int_element_mod_flags INT_PARAMS; /* 29 */
static void int_folder_child_count INT_PARAMS; /* 30 */
static void int_folder_get_child_of_name INT_PARAMS; /* 31 */
static void int_folder_get_folder_of_name INT_PARAMS; /* 32 */
static void int_folder_get_file_of_name INT_PARAMS; /* 33 */
static void int_folder_get_pipe_of_name INT_PARAMS; /* 34 */
static void int_folder_add_folder INT_PARAMS; /* 35 */
static void int_folder_add_file INT_PARAMS; /* 36 */
static void int_folder_add_pipe INT_PARAMS; /* 37 */
static void int_folder_open_iter INT_PARAMS; /* 38 */
static void int_folder_create_folder INT_PARAMS; /* 39 */ // TODO remove
static void int_folder_create_file INT_PARAMS; /* 40 */ // TODO remove
static void int_folder_create_pipe INT_PARAMS; /* 41 */ // TODO remove
static void int_file_length INT_PARAMS; /* 42 */
static void int_file_truncate INT_PARAMS; /* 43 */
static void int_file_open_stream INT_PARAMS; /* 44 */
static void int_pipe_length INT_PARAMS; /* 45 */
static void int_pipe_truncate INT_PARAMS; /* 46 */
static void int_pipe_open_stream INT_PARAMS; /* 47 */
static void int_time_get INT_PARAMS; /* 48 */
static void int_time_wait INT_PARAMS; /* 49 */
static void int_random INT_PARAMS; /* 50 */
static void int_memory_copy INT_PARAMS; /* 51 */
static void int_memory_move INT_PARAMS; /* 52 */
static void int_memory_bset INT_PARAMS; /* 53 */
static void int_memory_set INT_PARAMS; /* 54 */
static void int_string_length INT_PARAMS; /* 55 */
static void int_string_compare INT_PARAMS; /* 56 */
static void int_number_to_string INT_PARAMS; /* 57 */
static void int_fpnumber_to_string INT_PARAMS; /* 58 */
static void int_string_to_number INT_PARAMS; /* 59 */
static void int_string_to_fpnumber INT_PARAMS; /* 60 */
static void int_string_to_u16string INT_PARAMS; /* 61 */
static void int_string_to_u32string INT_PARAMS; /* 62 */
static void int_u16string_to_string INT_PARAMS; /* 63 */
static void int_u32string_to_string INT_PARAMS; /* 64 */
static void int_string_format INT_PARAMS; /* 65 */
static void int_load_file INT_PARAMS; /* 66 */
static void int_get_file INT_PARAMS; /* 67 */

#	define INT_ERRORS_ILLEGAL_INTERRUPT                  0
#	define INT_ERRORS_UNKNOWN_COMMAND                    1
#	define INT_ERRORS_ILLEGAL_MEMORY                     2
#	define INT_ERRORS_ARITHMETIC_ERROR                   3
#	define INT_EXIT                                      4
#	define INT_MEMORY_ALLOC                              5
#	define INT_MEMORY_REALLOC                            6
#	define INT_MEMORY_FREE                               7
#	define INT_OPEN_STREAM                               8
#	define INT_STREAMS_WRITE                             9
#	define INT_STREAMS_READ                              10
#	define INT_STREAMS_CLOSE                             11
#	define INT_STREAMS_GET_POS                           12
#	define INT_STREAMS_SEEK_SET                          13
#	define INT_STREAMS_SEEK_ADD                          14
#	define INT_STREAMS_SEEK_EOF                          15
#	define INT_OPEN_ELEMENT_FILE                         16
#	define INT_OPEN_ELEMENT_FOLDER                       17
#	define INT_OPEN_ELEMENT_PIPE                         18
#	define INT_OPEN_ELEMENT                              19
#	define INT_ELEMENT_OPEN_PARENT                       20
#	define INT_ELEMENT_GET_CREATE                        21
#	define INT_ELEMENT_GET_LAST_MOD                      22
#	define INT_ELEMENT_SET_CREATE                        23
#	define INT_ELEMENT_SET_LAST_MOD                      24
#	define INT_ELEMENT_DELETE                            25
#	define INT_ELEMENT_MOVE                              26
#	define INT_ELEMENT_GET_NAME                          27
#	define INT_ELEMENT_GET_FLAGS                         28
#	define INT_ELEMENT_MOD_FLAGS                         29
#	define INT_FOLDER_CHILD_COUNT                        30
#	define INT_FOLDER_GET_CHILD_OF_NAME                  31
#	define INT_FOLDER_GET_FOLDER_OF_NAME                 32
#	define INT_FOLDER_GET_FILE_OF_NAME                   33
#	define INT_FOLDER_GET_PIPE_OF_NAME                   34
#	define INT_FOLDER_ADD_FOLDER                         35
#	define INT_FOLDER_ADD_FILE                           36
#	define INT_FOLDER_ADD_LINK                           37
#	define INT_FOLDER_OPEN_ITER                          38
#	define INT_FOLDER_CREATE_FOLDER                      39 // TODO remove
#	define INT_FOLDER_CREATE_FILE                        40 // TODO remove
#	define INT_FOLDER_CREATE_PIPE                        41 // TODO remove
#	define INT_FILE_LENGTH                               42
#	define INT_FILE_TRUNCATE                             43
#	define INT_FILE_OPEN_STREAM                          44
#	define INT_PIPE_LENGTH                               45
#	define INT_PIPE_TRUNCATE                             46
#	define INT_PIPE_OPEN_STREAM                          47
#	define INT_TIME_GET                                  48
#	define INT_TIME_WAIT                                 49
#	define INT_RANDOM                                    50
#	define INT_MEMORY_COPY                               51
#	define INT_MEMORY_MOVE                               52
#	define INT_MEMORY_BSET                               53
#	define INT_MEMORY_SET                                54
#	define INT_STRING_LENGTH                             55
#	define INT_STRING_COMPARE                            56
#	define INT_NUMBER_TO_STRING                          57
#	define INT_FPNUMBER_TO_STRING                        58
#	define INT_STRING_TO_NUMBER                          59
#	define INT_STRING_TO_FPNUMBER                        60
#	define INT_STRING_TO_U16STRING                       61
#	define INT_STRING_TO_U32STRING                       62
#	define INT_U16STRING_TO_STRING                       63
#	define INT_U32STRING_TO_STRING                       64
#	define INT_STRING_FORMAT                             65
#	define INT_LOAD_FILE                                 66
#	define INTERRUPT_COUNT                               67

static void (*(ints[]))INT_PARAMS = {
	int_errors_illegal_interrupt /* 0  */,
	int_errors_unknown_command /* 1  */,
	int_errors_illegal_memory /* 2  */,
	int_errors_arithmetic_error /* 3  */,
	int_exit /* 4  */,
	int_memory_alloc /* 5  */,
	int_memory_realloc /* 6  */,
	int_memory_free /* 7  */,
	int_open_stream /* 8  */,
	int_streams_write /* 9  */,
	int_streams_read /* 10 */,
	int_streams_close /* 11 */,
	int_streams_get_pos /* 12 */,
	int_streams_seek_set /* 13 */,
	int_streams_seek_add /* 14 */,
	int_streams_seek_eof /* 15 */,
	int_open_element_file /* 16 */,
	int_open_element_folder /* 17 */,
	int_open_element_pipe /* 18 */,
	int_open_element /* 19 */,
	int_element_open_parent /* 20 */,
	int_element_get_create /* 21 */,
	int_element_get_last_mod /* 22 */,
	int_element_set_create /* 23 */,
	int_element_set_last_mod /* 24 */,
	int_element_delete /* 25 */,
	int_element_move /* 26 */,
	int_element_get_name /* 27 */,
	int_element_get_flags /* 28 */,
	int_element_mod_flags /* 29 */,
	int_folder_child_count /* 30 */,
	int_folder_get_child_of_name /* 31 */,
	int_folder_get_folder_of_name /* 32 */,
	int_folder_get_file_of_name /* 33 */,
	int_folder_get_pipe_of_name /* 34 */,
	int_folder_add_folder /* 35 */,
	int_folder_add_file /* 36 */,
	int_folder_add_pipe /* 37 */,
	int_folder_open_iter /* 38 */,
	int_folder_create_folder /* 39 */, // TODO remove
	int_folder_create_file /* 40 */,// TODO remove
	int_folder_create_pipe /* 41 */,// TODO remove
	int_file_length /* 42 */,
	int_file_truncate /* 43 */,
	int_file_open_stream /* 44 */,
	int_pipe_length /* 45 */,
	int_pipe_truncate /* 46 */,
	int_pipe_open_stream /* 47 */,
	int_time_get /* 48 */,
	int_time_wait /* 49 */,
	int_random /* 50 */,
	int_memory_copy /* 51 */,
	int_memory_move /* 52 */,
	int_memory_bset /* 53 */,
	int_memory_set /* 54 */,
	int_string_length /* 55 */,
	int_string_compare /* 56 */,
	int_number_to_string /* 57 */,
	int_fpnumber_to_string /* 58 */,
	int_string_to_number /* 59 */,
	int_string_to_fpnumber /* 60 */,
	int_string_to_u16string /* 61 */,
	int_string_to_u32string /* 62 */,
	int_u16string_to_string /* 63 */,
	int_u32string_to_string /* 64 */,
	int_string_format /* 65 */,
	int_load_file /* 66 */,
};

_Static_assert((sizeof(void (*)INT_PARAMS) * INTERRUPT_COUNT) == sizeof(ints), "Error!");
