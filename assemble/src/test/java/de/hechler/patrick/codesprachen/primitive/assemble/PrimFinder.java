package de.hechler.patrick.codesprachen.primitive.assemble;

import static de.hechler.patrick.zeugs.check.Assert.assertEquals;
import static de.hechler.patrick.zeugs.check.Assert.assertNotArrayEquals;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.objects.Checker;

@CheckClass
public class PrimFinder {
	
	
	private static final int BINARY_LENGTH = 1 << 7;
	private static final String INPUT_FILE = "./input/longnumbers_BIG.txt";
	private static final String BINARY_INPUT_FILE = "./input/in2.data";
	private static final String BINARY_OUTPUT_FILE = "./output/out.data";
	private static final boolean OVERWRITE = false;
	
	@Check
	private void primfind_test() throws IOException {
		long[] data = read(new FileInputStream(INPUT_FILE));
		long[] otherData = data.clone();
		for (int i = 0; i < data.length; i ++ ) {
			data[i] = checkPrim(data[i]);
		}
		assertNotArrayEquals(data, otherData);
	}
	
	@Check
	private void comparetimetest() throws IOException {//needs ca. 30 secs
//		long start = System.currentTimeMillis();
//		System.out.println("read_start");
		long[] data = readBinary(new FileInputStream(BINARY_INPUT_FILE));
//		print(data);
//		System.out.println("read_time: " + (System.currentTimeMillis() - start) + "ms");
//		start = System.currentTimeMillis();
//		System.out.println("prim-check_start");
		for (int i = 0; i < data.length; i ++ ) {
			data[i] = checkPrim(data[i]);
		}
//		System.out.println("prim-check_time: " + (System.currentTimeMillis() - start) + "ms");
//		start = System.currentTimeMillis();
//		System.out.println("write-solution_start");
		writeBinary(new FileOutputStream(BINARY_OUTPUT_FILE), data);
//		System.out.println("write-solution_time: " + (System.currentTimeMillis() - start) + "ms");
//		print(data);
	}
	
	private long checkPrim(long potPrim) {
		final long wurzel = wurzel(potPrim);
		if (wurzel * wurzel == potPrim) {
			return wurzel;
		}
		if ( (potPrim & 1L) == 0L) {
			if (potPrim == 2L) {
				return -1L;
			} else {
				return 2L;
			}
		}
		for (long checker = 3L; checker <= wurzel; checker += 2L) {
			if (potPrim % checker == 0) {
				return checker;
			}
		}
		return -1L;
	}
	
	private long wurzel(long potPrim) {
		for (long checker = 0L;; checker ++ ) {
			long checkerPow = checker * checker;
			if (checkerPow == potPrim) {
				return checker;
			} else if (checkerPow > potPrim) {
				return checker - 1L;
			}
		}
	}
	
	private void writeBinary(FileOutputStream out, long[] data) throws IOException {
		byte[] bytes = new byte[BINARY_LENGTH];
		assertEquals(BINARY_LENGTH / 8, data.length);
		for (int i1 = 0, i2 = 0; i1 < data.length; i1 ++ , i2 += 8) {
			bytes[i2] = (byte) data[i1];
			bytes[i2 + 1] = (byte) (data[i1] >> 8);
			bytes[i2 + 2] = (byte) (data[i1] >> 16);
			bytes[i2 + 3] = (byte) (data[i1] >> 24);
			bytes[i2 + 4] = (byte) (data[i1] >> 32);
			bytes[i2 + 5] = (byte) (data[i1] >> 40);
			bytes[i2 + 6] = (byte) (data[i1] >> 48);
			bytes[i2 + 7] = (byte) (data[i1] >> 56);
		}
		out.write(bytes);
	}
	
	private long[] readBinary(FileInputStream in) throws IOException {
		long[] res = new long[BINARY_LENGTH / 8];
		byte[] bytes = new byte[BINARY_LENGTH];
		assertEquals(res.length * 8, in.read(bytes));
		assertEquals( -1, in.read());
		for (int i1 = 0, i2 = 0; i1 < res.length; i1 ++ , i2 += 8) {
			res[i1] = 0xFFL & bytes[i2];
			res[i1] |= (0xFFL & bytes[i2 + 1]) << 8;
			res[i1] |= (0xFFL & bytes[i2 + 2]) << 16;
			res[i1] |= (0xFFL & bytes[i2 + 3]) << 24;
			res[i1] |= (0xFFL & bytes[i2 + 4]) << 32;
			res[i1] |= (0xFFL & bytes[i2 + 5]) << 40;
			res[i1] |= (0xFFL & bytes[i2 + 6]) << 48;
			res[i1] |= (0xFFL & bytes[i2 + 7]) << 56;
		}
		return res;
	}
	
	private long[] read(InputStream in) throws IOException {
		try (Scanner scanner = new Scanner(in)) {
			List <Long> result = new ArrayList <Long>();
			while (scanner.hasNext()) {
				result.add(scanner.nextLong());
			}
			long[] arr = new long[result.size()];
			for (int i = 0; i < result.size(); i ++ ) {
				arr[i] = result.get(i);
			}
			return arr;
		}
	}
	
	@SuppressWarnings("unused")
	private void print(long[] data) {
		for (int i = 0; i < data.length; i ++ ) {
			System.out.println("data[" + i + "]: " + Long.toHexString(data[i]) + "  =  " + data[i]);
		}
	}
	
	public static void main(String[] args) {
		if ( !Files.exists(Paths.get(BINARY_INPUT_FILE)) || OVERWRITE) {
			long start = System.currentTimeMillis();
			System.out.println("create new random numbers");
			try (OutputStream out = new FileOutputStream(BINARY_INPUT_FILE)) {
				Random rnd = new Random();
				byte[] bytes = new byte[BINARY_LENGTH];
				rnd.nextBytes(bytes);
				for (int i = 7; i < BINARY_LENGTH; i += 8) {
					bytes[i] &= 0x7F;
				}
				out.write(bytes);
			} catch (IOException e) {
				throw new IOError(e);
			}
			System.out.println("created new random numbers, needed: " + (System.currentTimeMillis() - start));
		}
		Checker.check(PrimFinder.class).detailedPrint(System.out, 2);
	}
	
}
