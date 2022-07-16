/*
 * pvm_main.c
 *
 *  Created on: Jul 6, 2022
 *      Author: pat
 */

#include <stdio.h>

void setup(int, char**);

int main(int argc, char **argv) {
	setup(argc, argv);
}

void setup(int argc, char** argv)  {
	for (int i = 0; i < argc; i ++) {
		printf("arg[%d]: %s\n", i, argv[i]);
	}
	for (;*argv; argv++) {
		printf("arg: %s\n", *argv);
	}
}
