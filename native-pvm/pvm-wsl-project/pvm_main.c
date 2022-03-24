/*
 * pvm_main.c
 *
 *  Created on: 12.01.2022
 *      Author: Patrick
 */

 /* https://devblogs.microsoft.com/cppblog/targeting-windows-subsystem-for-linux-from-visual-studio/
  * for visual studie:
  * after restart:
  * 	on wsl:
  * 		sudo service ssh start
  */

#include "pvm_defs.h"
#include "pvm_signal_handler.h"
#include "pvm_virtual_mashine.h"
#include "pvm_debug.h"

#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <setjmp.h>

#define INTERPRETER_START "#!/bin/pvm"

extern struct pvm pvm;

extern enum pvm_runConditon pvm_rc;

int halt = 0;

static int setup(const int, char**);
static void help(FILE*);
NO_RETURN_PREFIX static void crash(const int, const int, char**, const char*) NO_RETURN_POSTFIX;

int main(int argc, char **argv) {
#ifdef DEBUG_LOG
	printf("hello\n");
	fflush(stdout);
#endif // DEBUG_LOG
	int port = setup(argc, argv);
#ifdef DEBUG_LOG
	printf("[pvm]: finished setup\n");
	fflush(stdout);
#endif // DEBUG_LOG
	if (port != -1) {
		pvm_debug_start(port);
#ifdef DEBUG_LOG
		printf("[pvm]: debug started (port=%d)\n", port);
		fflush(stdout);
	} else {
		printf("[pvm]: no debug started enabled\n");
		fflush(stdout);
#endif // DEBUG_LOG
	}
	init_pvm_signal_handler();
	while (1) {
		pvm_vm_execute();
		pvm_debug_notify();
	}
}

static int setup(const int argc, char **argv) {
	int port = -1;
	pvm_rc = PVM_RC_ALWAYS_RUN;
	memset(&pvm, 0, sizeof(struct pvm));
	pvm.sp = (num*) -1;
	pvm.intcnt = ALL_INTS_INTCNT;
	pvm.intp = memset(malloc(ALL_INTS_INTCNT * sizeof(void*)), 0xFF, ALL_INTS_INTCNT * sizeof(void*));
	for (int i = 1; i < argc; i++) {
		const char *av = argv[i];
#ifdef DEBUG_LOG
		printf("[main]: make now arg: '%s'\n", av);
		fflush(stdout);
#endif // DEBUG_LOG
		if (av[0] != '-') {
			crash(i, argc, argv, "unknown argument\n");
		}
		switch (av[1]) {
		case '-': {
			switch (av[2]) {
			case 'h':
				if (strcmp("elp", av + 3) != 0) {
					crash(i, argc, argv, "unknown argument\n");
				}
				help(stdout);
				break;
			case 'p': {
				const char *cs;
				switch (av[3]) {
				case 'o': {
					if ('r' != av[4] || 't' != av[5]) {
						crash(i, argc, argv, "unknown argument\n");
					}
					switch (av[6]) {
					case '\0':
						cs = argv[++i];
						break;
					case '=':
						cs = av + 7;
						break;
					default:
						crash(i, argc, argv, "unknown argument\n");
					}
					if (port != -1) {
						crash(i, argc, argv, "double set of port\n");
					}
					port = atoi(cs);
					break;
				}
				case 'm': {
					if (av[4] != 'c') {
						crash(i, argc, argv, "unknown argument\n");
					}
					switch (av[5]) {
					case '=':
						cs = av + 6;
						argv[i] += 6;
						break;
					case '\0':
						if (argc <= ++i) {
							crash(i, argc, argv, "not enugh args\n");
						}
						cs = argv[i];
						break;
					default:
						crash(i, argc, argv, "unknown argument\n");
					}
					if (pvm.ip != NULL) {
						crash(i, argc, argv, "double set of pmc\n");
					}
#ifdef DEBUG_LOG
					printf("[main]: pmc='%s'\n", cs);
					fflush(stdout);
#endif //DEBUG_LOG
					int f = open64(cs, O_LARGEFILE | O_RDONLY);
					if (f == -1) {
						crash(i, argc, argv, "could not open the mashine-code-file\n");
					}
					num len = lseek64(f, 0, SEEK_END);
					if (len == -1L) {
						crash(i, argc, argv, "could not seek the end of the mashine-code-file\n");
					}
					if (lseek64(f, 0, SEEK_SET) != 0L) {
						crash(i, argc, argv, "could not set the pos of the mashine-code-file to zero\n");
					}
					pvm.ip = malloc(len);
					if (pvm.ip == NULL) {
						fprintf(stderr, "len=%ld\n", len);
						crash(i, argc, argv, "could not allocate enugh memory for the machine-code-file\n");
					}
					long readlen = 0;
					do {
						long addlen = read(f, pvm.ip, len);
						if (addlen == 0L) {
							fprintf(stderr, "read=%ld\n"
									"len=%ld\n", readlen, len);
							crash(i, argc, argv, "could not read the complete machine-code-file\n"
									"reached end-of-file too early\n");
						}
						readlen += addlen;
					} while (readlen < len);
					pvm.x[0] = argc - i;
					pvm.x[1] = (num) (argv + i);
					i = argc;
					close(f);
#ifdef DEBUG_LOG
					printf("[main]: programm-argc=%ld\n", pvm.x[0]);
					fflush(stdout);
#endif //DEBUG_LOG
					break;
				}
				default:
					crash(i, argc, argv, "unknown argument\n");
				}
				break;
			}
			case 'w': {
				if (strcmp("ait", av + 3) != 0) {
					crash(i, argc, argv, "unknown argument\n");
				}
				if (pvm_rc != PVM_RC_ALWAYS_RUN) {
					crash(i, argc, argv, "double set of the wait flag\n");
				}
				pvm_rc = PVM_RC_STOP;
				break;
			}
			default:
				crash(i, argc, argv, "unknown argument\n");
			}
			break;
		}
		case 's': {
			const char *cmp = "tack-size=" - 2;
			for (int i = 2;; i++) {
				if (cmp[i] != av[i]) {
					if (cmp[i] != '\0') {
						crash(i, argc, argv, "unknown argument\n");
					}
					num ss = atol(av + i);
					pvm.sp = malloc(ss);
					break;
				}
			}
			break;
		}
		default:
			crash(i, argc, argv, "unknown argument\n");
		}
	}
	if (port == -1 && pvm_rc != PVM_RC_ALWAYS_RUN) {
		crash(-1, argc, argv, "wait flag set, but there is no debug port set!\n");
	}
	if (pvm.ip == NULL && pvm_rc == PVM_RC_ALWAYS_RUN) {
		crash(-1, argc, argv, "no execution file set and wait flag not set: use --pmc=<FILE> or --wait\n");
	}
	if (pvm.ip != NULL) {
		num len = strlen(INTERPRETER_START);
		strncmp(INTERPRETER_START, (char*) pvm.ip, len);
		pvm.ip = (num*) (strchr(((char*) pvm.ip) + len, '\n') + 1);
//		pvm.ip = (num*) (((num) pvm.ip) + len);
	}
	return port;
}

void help(FILE *out) {
	fputs("--help\n", out);
	fputs("        to print this message\n", out);
	fputs("--pmc <PRIMITIVE_CODE_FILE>\n", out);
	fputs("  or\n", out);
	fputs("--pmc=<PRIMITIVE_CODE_FILE>\n", out);
	fputs("        to set the execution file\n", out);
	fputs("--port <PORT>\n", out);
	fputs("  or\n", out);
	fputs("--port=<PORT>\n", out);
	fputs("        to set the debug port\n", out);
	fputs("--wait\n", out);
	fputs("        to wait with the execution\n", out);
	fputs("        only usable when <port> is set\n", out);
	fputs("-stack-size=<STACK_SIZE>\n", out);
	fputs("        to set the init stack size\n", out);
}

void crash(const int index, const int argc, char **argv, const char *msg) {
	if (msg != NULL) {
		fputs(msg, stderr);
	}
	fprintf(stderr, "error happened at index=%d\n", index);
	for (int i = 0; i < argc; i++) {
		if (i == index) {
			fputs("error happened here -> ", stderr);
		}
		fprintf(stderr, "[%d]='%s'\n", i, argv[i]);
	}
	fflush(NULL);
	exit(3);
}
