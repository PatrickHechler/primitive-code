#if defined SRC_PVM_CMD_CMDS_GEN_H_ | !defined SRC_PVM_CMD_H_
#	error "Multiple includes of pvm-cmd-cmds-gen.h or SRC_PVM_CMD_H_ is not defined!"
#endif
#define SRC_PVM_CMD_CMDS_GEN_H_
/*
 * WARNING: this file is generated, DO NOT MODIFY
 */
#define ILL_1 c_ill,
#define ILL_10 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1 ILL_1
#define ILL_100 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10 ILL_10
#define ILL_1000 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100
#define ILL_10000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000 ILL_1000
static void(*cmds[])() = {
    /* 0... */
        /* 00.. : data */
            /* 000. : move data */
                /* 0000 */ ILL_1
                /* 0001 */ c_mvb,
                /* 0002 */ c_mvw,
                /* 0003 */ c_mvdw,
                /* 0004 */ c_mov,
                /* 0005 */ c_lea,
                /* 0006 */ c_mvad,
                /* 0007 */ c_swap,
                /* 0008 */ ILL_1
                /* 0009 */ ILL_1
                /* 000A */ ILL_1
                /* 000B */ ILL_1
                /* 000C */ ILL_1
                /* 000D */ ILL_1
                /* 000E */ ILL_1
                /* 000F */ ILL_1
            /* 001. */ ILL_10
            /* 002. */ ILL_10
            /* 003. */ ILL_10
            /* 004. */ ILL_10
            /* 005. */ ILL_10
            /* 006. */ ILL_10
            /* 007. */ ILL_10
            /* 008. */ ILL_10
            /* 009. */ ILL_10
            /* 00A. */ ILL_10
            /* 00B. */ ILL_10
            /* 00C. */ ILL_10
            /* 00D. */ ILL_10
            /* 00E. */ ILL_10
            /* 00F. */ ILL_10
        /* 01.. : math */
            /* 010. : logic */
                /* 0100 */ c_or,
                /* 0101 */ c_and,
                /* 0102 */ c_xor,
                /* 0103 */ c_not,
                /* 0104 */ c_lsh,
                /* 0105 */ c_rash,
                /* 0106 */ c_rlsh,
                /* 0107 */ ILL_1
                /* 0108 */ ILL_1
                /* 0109 */ ILL_1
                /* 010A */ ILL_1
                /* 010B */ ILL_1
                /* 010C */ ILL_1
                /* 010D */ ILL_1
                /* 010E */ ILL_1
                /* 010F */ ILL_1
            /* 011. : simple arithmetic */
                /* 0110 */ c_add,
                /* 0111 */ c_sub,
                /* 0112 */ c_mul,
                /* 0113 */ c_div,
                /* 0114 */ c_neg,
                /* 0115 */ c_addc,
                /* 0116 */ c_subc,
                /* 0117 */ c_inc,
                /* 0118 */ c_dec,
                /* 0119 */ ILL_1
                /* 011A */ ILL_1
                /* 011B */ ILL_1
                /* 011C */ ILL_1
                /* 011D */ ILL_1
                /* 011E */ ILL_1
                /* 011F */ ILL_1
            /* 012. : floating-point arithmetic */
                /* 0120 */ c_addfp,
                /* 0121 */ c_subfp,
                /* 0122 */ c_mulfp,
                /* 0123 */ c_divfp,
                /* 0124 */ c_negfp,
                /* 0125 */ ILL_1
                /* 0126 */ ILL_1
                /* 0127 */ ILL_1
                /* 0128 */ ILL_1
                /* 0129 */ ILL_1
                /* 012A */ ILL_1
                /* 012B */ ILL_1
                /* 012C */ ILL_1
                /* 012D */ ILL_1
                /* 012E */ ILL_1
                /* 012F */ ILL_1
            /* 013. : unsigned arithmetic */
                /* 0130 */ c_uadd,
                /* 0131 */ c_usub,
                /* 0132 */ c_umul,
                /* 0133 */ c_udiv,
                /* 0134 */ ILL_1
                /* 0135 */ ILL_1
                /* 0136 */ ILL_1
                /* 0137 */ ILL_1
                /* 0138 */ ILL_1
                /* 0139 */ ILL_1
                /* 013A */ ILL_1
                /* 013B */ ILL_1
                /* 013C */ ILL_1
                /* 013D */ ILL_1
                /* 013E */ ILL_1
                /* 013F */ ILL_1
            /* 014. : big arithmetic */
                /* 0140 */ c_badd,
                /* 0141 */ c_bsub,
                /* 0142 */ c_bmul,
                /* 0143 */ c_bdiv,
                /* 0144 */ c_bneg,
                /* 0145 */ ILL_1
                /* 0146 */ ILL_1
                /* 0147 */ ILL_1
                /* 0148 */ ILL_1
                /* 0149 */ ILL_1
                /* 014A */ ILL_1
                /* 014B */ ILL_1
                /* 014C */ ILL_1
                /* 014D */ ILL_1
                /* 014E */ ILL_1
                /* 014F */ ILL_1
            /* 015. : convert */
                /* 0150 */ c_fptn,
                /* 0151 */ c_ntfp,
                /* 0152 */ ILL_1
                /* 0153 */ ILL_1
                /* 0154 */ ILL_1
                /* 0155 */ ILL_1
                /* 0156 */ ILL_1
                /* 0157 */ ILL_1
                /* 0158 */ ILL_1
                /* 0159 */ ILL_1
                /* 015A */ ILL_1
                /* 015B */ ILL_1
                /* 015C */ ILL_1
                /* 015D */ ILL_1
                /* 015E */ ILL_1
                /* 015F */ ILL_1
            /* 016. */ ILL_10
            /* 017. */ ILL_10
            /* 018. */ ILL_10
            /* 019. */ ILL_10
            /* 01A. */ ILL_10
            /* 01B. */ ILL_10
            /* 01C. */ ILL_10
            /* 01D. */ ILL_10
            /* 01E. */ ILL_10
            /* 01F. */ ILL_10
        /* 02.. : program control */
            /* 020. : compare/check */
                /* 0200 */ c_cmp,
                /* 0201 */ c_cmpl,
                /* 0202 */ c_cmpfp,
                /* 0203 */ c_chkfp,
                /* 0204 */ c_cmpu,
                /* 0205 */ c_cmpb,
                /* 0206 */ ILL_1
                /* 0207 */ ILL_1
                /* 0208 */ ILL_1
                /* 0209 */ ILL_1
                /* 020A */ ILL_1
                /* 020B */ ILL_1
                /* 020C */ ILL_1
                /* 020D */ ILL_1
                /* 020E */ ILL_1
                /* 020F */ ILL_1
            /* 021. : conditional jump */
                /* 0210 */ c_jmperr,
                /* 0211 */ c_jmpeq,
                /* 0212 */ c_jmpne,
                /* 0213 */ c_jmpgt,
                /* 0214 */ c_jmpge,
                /* 0215 */ c_jmplt,
                /* 0216 */ c_jmple,
                /* 0217 */ c_jmpcs,
                /* 0218 */ c_jmpcc,
                /* 0219 */ c_jmpzs,
                /* 021A */ c_jmpzc,
                /* 021B */ c_jmpnan,
                /* 021C */ c_jmpan,
                /* 021D */ c_jmpab,
                /* 021E */ c_jmpsb,
                /* 021F */ c_jmpnb,
            /* 022. : jump */
                /* 0220 */ c_jmp,
                /* 0221 */ ILL_1
                /* 0222 */ ILL_1
                /* 0223 */ ILL_1
                /* 0224 */ ILL_1
                /* 0225 */ ILL_1
                /* 0226 */ ILL_1
                /* 0227 */ ILL_1
                /* 0228 */ ILL_1
                /* 0229 */ ILL_1
                /* 022A */ ILL_1
                /* 022B */ ILL_1
                /* 022C */ ILL_1
                /* 022D */ ILL_1
                /* 022E */ ILL_1
                /* 022F */ ILL_1
            /* 023. : interrupt */
                /* 0230 */ c_int,
                /* 0231 */ c_iret,
                /* 0232 */ ILL_1
                /* 0233 */ ILL_1
                /* 0234 */ ILL_1
                /* 0235 */ ILL_1
                /* 0236 */ ILL_1
                /* 0237 */ ILL_1
                /* 0238 */ ILL_1
                /* 0239 */ ILL_1
                /* 023A */ ILL_1
                /* 023B */ ILL_1
                /* 023C */ ILL_1
                /* 023D */ ILL_1
                /* 023E */ ILL_1
                /* 023F */ ILL_1
            /* 024. */ ILL_10
            /* 025. */ ILL_10
            /* 026. */ ILL_10
            /* 027. */ ILL_10
            /* 028. */ ILL_10
            /* 029. */ ILL_10
            /* 02A. */ ILL_10
            /* 02B. */ ILL_10
            /* 02C. */ ILL_10
            /* 02D. */ ILL_10
            /* 02E. */ ILL_10
            /* 02F. */ ILL_10
        /* 03.. : stack */
            /* 030. : call and return */
                /* 0300 */ c_call,
                /* 0301 */ c_calo,
                /* 0302 */ c_ret,
                /* 0303 */ ILL_1
                /* 0304 */ ILL_1
                /* 0305 */ ILL_1
                /* 0306 */ ILL_1
                /* 0307 */ ILL_1
                /* 0308 */ ILL_1
                /* 0309 */ ILL_1
                /* 030A */ ILL_1
                /* 030B */ ILL_1
                /* 030C */ ILL_1
                /* 030D */ ILL_1
                /* 030E */ ILL_1
                /* 030F */ ILL_1
            /* 031. : push and pop */
                /* 0310 */ c_push,
                /* 0311 */ c_pop,
                /* 0312 */ c_pushblk,
                /* 0313 */ c_popblk,
                /* 0314 */ ILL_1
                /* 0315 */ ILL_1
                /* 0316 */ ILL_1
                /* 0317 */ ILL_1
                /* 0318 */ ILL_1
                /* 0319 */ ILL_1
                /* 031A */ ILL_1
                /* 031B */ ILL_1
                /* 031C */ ILL_1
                /* 031D */ ILL_1
                /* 031E */ ILL_1
                /* 031F */ ILL_1
            /* 032. */ ILL_10
            /* 033. */ ILL_10
            /* 034. */ ILL_10
            /* 035. */ ILL_10
            /* 036. */ ILL_10
            /* 037. */ ILL_10
            /* 038. */ ILL_10
            /* 039. */ ILL_10
            /* 03A. */ ILL_10
            /* 03B. */ ILL_10
            /* 03C. */ ILL_10
            /* 03D. */ ILL_10
            /* 03E. */ ILL_10
            /* 03F. */ ILL_10
        /* 04.. */ ILL_100
        /* 05.. */ ILL_100
        /* 06.. */ ILL_100
        /* 07.. */ ILL_100
        /* 08.. */ ILL_100
        /* 09.. */ ILL_100
        /* 0A.. */ ILL_100
        /* 0B.. */ ILL_100
        /* 0C.. */ ILL_100
        /* 0D.. */ ILL_100
        /* 0E.. */ ILL_100
        /* 0F.. */ ILL_100
    /* 1... */ ILL_1000
    /* 2... */ ILL_1000
    /* 3... */ ILL_1000
    /* 4... */ ILL_1000
    /* 5... */ ILL_1000
    /* 6... */ ILL_1000
    /* 7... */ ILL_1000
    /* 8... */ ILL_1000
    /* 9... */ ILL_1000
    /* A... */ ILL_1000
    /* B... */ ILL_1000
    /* C... */ ILL_1000
    /* D... */ ILL_1000
    /* E... */ ILL_1000
    /* F... */ ILL_1000
};
#undef ILL_1
#undef ILL_10
#undef ILL_100
#undef ILL_1000
#undef ILL_10000
