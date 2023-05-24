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
 * pvm-version.h
 *
 *  Created on: Nov 2, 2022
 *      Author: pat
 */

#ifndef PVM_VERSION_H_
#define PVM_VERSION_H_

#define PVM_VERSION_MAJOR 0x01
#define PVM_VERSION_MINOR 0x00
#define PVM_VERSION_FIX   0x00

#define PVM_VERSION_STR "1.0.0"

#define PVM_VERSION_HEX ((PVM_VERSION_MAJOR << 16) | (PVM_VERSION_MINOR << 8) | (PVM_VERSION_FIX)

#define PVM_TO_MAJOR(a) ((a) << 16)
#define PVM_TO_MINOR(a) ((a) << 8)
#define PVM_TO_FIX  (a) ((a))

#define PVM_TO_VERS (major, minor, fix) (PVM_TO_MAJOR(major) | PVM_TO_MINOR(minor) | PVM_TO_FIX(fix))

#define PVM_IS_MAJOR(a) (PVM_VERSION_MAJOR== a)
#define PVM_IS_MINOR(a) (PVM_VERSION_MINOR== a)
#define PVM_IS_FIX  (a) (PVM_VERSION_FIX  == a)

#define PVM_IS_VERS (a) (PVM_VERSION_HEX  == a)
#define PVM_MIN_VERS(a) (PVM_VERSION_HEX  >= a)
#define PVM_MAX_VERS(a) (PVM_VERSION_HEX  <= a)

extern void print_version(FILE *file);

#endif /* PVM_VERSION_H_ */
