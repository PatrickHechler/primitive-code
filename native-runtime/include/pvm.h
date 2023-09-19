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
#	undef HALF_PORTABLE_BUILD
#endif

#ifdef HALF_PORTABLE_BUILD
#	define PFS_HALF_PORTABLE_BUILD
#	define PVM_HALF_PORTABLE_BUILD
#endif

#include <pfs/patr-file-sys.h>

#if !defined __unix__
#	define PVM_PORTABLE_BUILD
#elif !defined __linux__
#	define PVM_HALF_PORTABLE_BUILD
#endif

#ifdef PVM_PORTABLE_BUILD
#	undef PVM_HALF_PORTABLE_BUILD
#endif

#if defined PVM_PORTABLE_BUILD && !defined PFS_PORTABLE_BUILD
#warning "the PVM uses a portable build with a non portable PFS"
#endif
#if !defined PVM_PORTABLE_BUILD && defined PFS_PORTABLE_BUILD
#warning "the PVM uses a non portable build with a portable PFS"
#endif
#if !defined PVM_HALF_PORTABLE_BUILD && defined PFS_HALF_PORTABLE_BUILD
#warning "the PVM uses a non half portable build with a half portable PFS"
#endif
#if defined PVM_HALF_PORTABLE_BUILD && !defined PFS_HALF_PORTABLE_BUILD
#warning "the PVM uses a half portable build with a non half portable PFS"
#endif

#if defined PVM_PORTABLE_BUILD || defined PFS_HALF_PORTABLE_BUILD
#warning "the PVM (half) portable build is not really portable yet (especially when PVM_DEBUG is defined)"
#endif

#endif /* INCLUDE_PVM_H_ */
