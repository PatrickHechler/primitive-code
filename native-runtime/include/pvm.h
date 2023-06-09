/*
 * pvm.h
 *
 *  Created on: Jun 9, 2023
 *      Author: pat
 */

#ifndef INCLUDE_PVM_H_
#define INCLUDE_PVM_H_

#ifdef PORTABLE_BUILD
#	define PFS_PORTABLE_BUILD
#	define PVM_PORTABLE_BUILD
#endif

#ifdef HALF_PORTABLE_BUILD
#	define PFS_HALF_PORTABLE_BUILD
#	define PVM_HALF_PORTABLE_BUILD
#endif

#include <patr-file-sys.h>

#if !defined __unix__
#	define PVM_PORTABLE_BUILD
#elif !defined __linux__
#	define PVM_HALF_PORTABLE_BUILD
#endif

#endif /* INCLUDE_PVM_H_ */
