package de.hechler.patrick.codesprachen.primitive.core.utils;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;

public class PrimAsmConstants {
	
	public static final Path START_CONSTANTS_PATH = Paths.get("[START_CONSTANTS]");
	
	public static final Map <String, PrimitiveConstant> START_CONSTANTS;
	
	static {
		Map <String, PrimitiveConstant> startConsts = new HashMap <>();
		int lineNum = 1;
		try (InputStream in = PrimAsmConstants.class.getResourceAsStream("/de/hechler/patrick/codesprachen/primitive/core/default-constants.psf")) {
			try (Scanner sc = new Scanner(in, "UTF-8")) {
				StringBuilder commentbuild = new StringBuilder();
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.charAt(0) == '|') {
						commentbuild.append(line).append('\n');
					} else {
						int index = line.indexOf('=');
						String name = line.substring(0, index);
						String num = line.substring(index + 1);
						long val;
						if (num.matches("[0-9A-F]+")) {
							val = Long.parseLong(num);
						} else {
							throw new InternalError();
						}
						PrimitiveConstant primConst = new PrimitiveConstant(name, commentbuild.toString(), val, START_CONSTANTS_PATH, lineNum);
						PrimitiveConstant old = startConsts.put(name, primConst);
						if (old != null) {
							throw new AssertionError(name);
						}
						commentbuild = new StringBuilder();
					}
					lineNum ++ ;
				}
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
		START_CONSTANTS = Collections.unmodifiableMap(startConsts);
	}
	
	public static final long MAX_STD_STREAM = PrimAsmPreDefines.STD_LOG;
	
	static {
		for (Field field : PrimAsmPreDefines.class.getFields()) {
			try {
				long val = field.getLong(null);
				PrimitiveConstant primConst = START_CONSTANTS.get(field.getName());
				if (primConst == null) {
					throw new AssertionError("validation error: primConst=null field: " + field.getName() + " (" + val + ")");
				}
				if (primConst.value != val) {
					throw new AssertionError("validation error: field: " + field.getName() + "=" + val + " primConst.val=" + primConst.value + " (comment):\n" + primConst.comment);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
			}
		}
	}
	
	public static final int IP      = 0;
	public static final int SP      = 1;
	public static final int STATUS  = 2;
	public static final int INTCNT  = 3;
	public static final int INTP    = 4;
	public static final int FS_LOCK = 5;
	public static final int X_ADD   = 6;
	
	
	
	public static final int PARAM_BASE  = 0x01;
	public static final int PARAM_A_NUM = 0x00;
	public static final int PARAM_A_SR  = 0x02;
	public static final int PARAM_NO_B  = 0x00;
	public static final int PARAM_B_REG = 0x04;
	public static final int PARAM_B_NUM = 0x08;
	public static final int PARAM_B_SR  = 0x0C;
	
	public static final int PARAM_ART_ANUM      = PARAM_BASE | PARAM_A_NUM | PARAM_NO_B;
	public static final int PARAM_ART_ASR       = PARAM_BASE | PARAM_A_SR | PARAM_NO_B;
	public static final int PARAM_ART_ANUM_BREG = PARAM_BASE | PARAM_A_NUM | PARAM_B_REG;
	public static final int PARAM_ART_ASR_BREG  = PARAM_BASE | PARAM_A_SR | PARAM_B_REG;
	public static final int PARAM_ART_ANUM_BNUM = PARAM_BASE | PARAM_A_NUM | PARAM_B_NUM;
	public static final int PARAM_ART_ASR_BNUM  = PARAM_BASE | PARAM_A_SR | PARAM_B_NUM;
	public static final int PARAM_ART_ANUM_BSR  = PARAM_BASE | PARAM_A_NUM | PARAM_B_SR;
	public static final int PARAM_ART_ASR_BSR   = PARAM_BASE | PARAM_A_SR | PARAM_B_SR;
	
}
