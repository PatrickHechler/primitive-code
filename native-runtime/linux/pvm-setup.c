/*
 * pvm-setup.c
 *
 *  Created on: Oct 26, 2022
 *      Author: pat
 */

#if !defined PVM_RUN && !defined PVM_DEBUG
#error "pvm-setup.c should never be compiled directly!"
#endif

#include <string.h>
#include <stdlib.h>
#include <stddef.h>
#include <stdio.h>

static void setup(int argc, char **argv);

static void setup_help(void);
static void setup_version(void);

static void setup(int argc, char **argv) {
	for (; *argv; argv++, argc --) {
		if (!strcmp("--help", *argv)) {
			setup_help();
			exit(1);
		} else if (!strcmp("--version", *argv)) {
			setup_version();
			exit(1);
#ifdef PVM_DEBUG
		} else if (!strcmp("--wait", *argv)) {

		} else if (!memcmp("--port=", *argv, 7)) {

#endif // PVM_DEBUG
		}
	}
}

static void setup_help(void) {
	printf(
#ifdef PVM_DEBUG
			"Usage: db-pvm [Options] --pmc=[EXECUTE_FILE] [ARGUMENTS]\n"
#else
			"Usage: pvm [Options] --pmc=[EXECUTE_FILE] [ARGUMENTS]\n"
#endif
			"Options-Rules:\n"
			"  --pfs=[PFS_FILE]\n"
			"    this Option is NOT optional!\n"
			"  --pfs=[EXECUTE_FILE]\n"
#ifdef PVM_DEBUG
			"    this Option is only optional when --wait is set!\n"
			"  --wait\n"
			"    load the program, but do not start the execution\n"
			"    when this option is set --port=[PORT] or --pipe=[PIPE]\n"
			"    has to be specified.\n"
			"  --port=[PORT]\n"
			"    if this option is set --pipe=[PIPE] is not allowed\n"
			"  --pipe=[PIPE]\n"
			"    if this option is set --port=[PORT] is not allowed\n"
#else
			"    this Option is NOT optional!\n"
			"    if this option is not set the PVM can't start\n"
#endif
			"Options:\n"
			"  --help\n"
			"    print this message and exit\n"
			"  --version\n"
			"    print the version and exit\n"
#ifdef PVM_DEBUG
			"  --port=[PORT]\n"
			"    set the debug port\n"
			"  --pipe=[PIPE]\n"
			"    set the debug pipe\n"
			"  --wait\n"
			"    to not start the execution of the program\n"
			"    wait until an attached debugger commands to execute\n"
			"    (with a normal continue or step command).\n"
#endif
			"  --pfs=[PFS_FILE]\n"
			"    set the file which contains the Patr-File-System\n"
			"  --pmc=[EXECUTE_FILE]\n"
			"    set the file to executed.\n"
			"    all arguments after this option will be passed to the\n"
			"    program as program arguments.\n"
			"    the first argument will be [EXECUTE_FILE], the following\n"
			"    program arguments will be the following arguments.\n"
#ifdef PVM_DEBUG
			"    it is recommended to specify this option even when\n"
			"    --port=[PORT] is specified.\n"
#endif
			""
	);
}

static void setup_version(void) {

}
