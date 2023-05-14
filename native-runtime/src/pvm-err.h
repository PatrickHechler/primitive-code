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
/*
 * pvm-err.h
 *
 *  Created on: Nov 9, 2022
 *      Author: pat
 */

#ifndef SRC_PVM_ERR_H_
#define SRC_PVM_ERR_H_

enum perr {
	// GENERATED-CODE-START
	// this code-block is automatic generated, do not modify
	PE_NONE                          = 0, /* indicates no error */
	PE_UNKNOWN_ERROR                 = 1, /* indicates an unknown error */
	PE_NO_MORE_ELEMENTS              = 2, /* indicates that there are no more elements */
	PE_ELEMENT_WRONG_TYPE            = 3, /* indicates that the element has not the wanted/allowed type */
	PE_ELEMENT_NOT_EXIST             = 4, /* indicates that the element does not exist */
	PE_ELEMENT_ALREADY_EXIST         = 5, /* indicates that the element already exists */
	PE_OUT_OF_SPACE                  = 6, /* indicates that there is not enough space on the device */
	PE_IO_ERR                        = 7, /* indicates an IO error */
	PE_ILLEGAL_ARG                   = 8, /* indicates an illegal argument */
	PE_ILLEGAL_MAGIC                 = 9, /* indicates that some magic value is invalid */
	PE_OUT_OF_MEMORY                 = 10, /* indicates that the system is out of memory */
	PE_ROOT_FOLDER                   = 11, /* indicates that the root folder does not support this operation */
	PE_PARENT_IS_CHILD               = 12, /* indicates that the parent can't be made to it's own child */
	PE_ELEMENT_USED                  = 13, /* indicates the element is still used somewhere else */
	PE_OUT_OF_RANGE                  = 14, /* indicates that some value was outside of the allowed range */
	PE_FOLDER_NOT_EMPTY              = 15, /* indicates that the operation was canceled, because only empty folders can be deleted */
	
	// here is the end of the automatic generated code-block
	// GENERATED-CODE-END
};

#endif /* SRC_PVM_ERR_H_ */
