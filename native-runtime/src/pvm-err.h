/*
 * pvm-err.h
 *
 *  Created on: Nov 9, 2022
 *      Author: pat
 */

#ifndef SRC_PVM_ERR_H_
#define SRC_PVM_ERR_H_

enum perr {
    PE_NONE                  = 0,  /* if pfs_errno is not set/no error occurred */
    PE_UNKNOWN_ERROR         = 1,  /* if an operation failed because of an unknown/unspecified error */
    PE_NO_MORE_ELEMNETS      = 2,  /* if the iterator has no next element */
    PE_ELEMENT_WRONG_TYPE    = 3,  /* if an IO operation failed because the element is not of the correct type (file expected, but folder or reverse) */
    PE_ELEMENT_NOT_EXIST     = 4,  /* if an IO operation failed because the element does not exist */
    PE_ELEMENT_ALREADY_EXIST = 5,  /* if an IO operation failed because the element already existed */
    PE_OUT_OF_SPACE          = 6,  /* if an IO operation failed because there was not enough space in the file system */
    PE_IO_ERR                = 7,  /* if an unspecified IO error occurred */
    PE_ILLEGAL_ARG           = 8,  /* if there was at least one invalid argument */
    PE_ILLEGAL_MAGIC         = 9,  /* if there was an invalid magic value */
    PE_OUT_OF_MEMORY         = 10, /* if an IO operation failed because there was not enough space in the file system */
    PE_ROOT_FOLDER           = 11, /* if an IO operation failed because the root folder has some restrictions */
    PE_PARENT_IS_CHILD       = 12, /* if an folder can not be moved because the new child (maybe a deep/indirect child) is a child of the folder */
};

#ifndef NO_CHECK_WITH_PFS
#include <pfs-err.h>
_Static_assert(PE_NONE                  == PFS_ERRNO_NONE                  , "Error!");
_Static_assert(PE_UNKNOWN_ERROR         == PFS_ERRNO_UNKNOWN_ERROR         , "Error!");
_Static_assert(PE_NO_MORE_ELEMNETS      == PFS_ERRNO_NO_MORE_ELEMNETS      , "Error!");
_Static_assert(PE_ELEMENT_WRONG_TYPE    == PFS_ERRNO_ELEMENT_WRONG_TYPE    , "Error!");
_Static_assert(PE_ELEMENT_NOT_EXIST     == PFS_ERRNO_ELEMENT_NOT_EXIST     , "Error!");
_Static_assert(PE_ELEMENT_ALREADY_EXIST == PFS_ERRNO_ELEMENT_ALREADY_EXIST , "Error!");
_Static_assert(PE_OUT_OF_SPACE          == PFS_ERRNO_OUT_OF_SPACE          , "Error!");
_Static_assert(PE_IO_ERR                == PFS_ERRNO_IO_ERR                , "Error!");
_Static_assert(PE_ILLEGAL_ARG           == PFS_ERRNO_ILLEGAL_ARG           , "Error!");
_Static_assert(PE_ILLEGAL_MAGIC         == PFS_ERRNO_ILLEGAL_MAGIC         , "Error!");
_Static_assert(PE_OUT_OF_MEMORY         == PFS_ERRNO_OUT_OF_MEMORY         , "Error!");
_Static_assert(PE_ROOT_FOLDER           == PFS_ERRNO_ROOT_FOLDER           , "Error!");
_Static_assert(PE_PARENT_IS_CHILD       == PFS_ERRNO_PARENT_IS_CHILD       , "Error!");
#endif

#endif /* SRC_PVM_ERR_H_ */
