package de.hechler.patrick.codesprachen.primitive.assemble;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.Start;

@CheckClass
public class QuickSort {
	
	private static final int BINARY_LENGTH = 1 << 28;
	private static final String INPUT_FILE = "./input/longnumbers_BIG.txt";
	private static final String BINARY_INPUT_FILE = "./input/in.data";
	
	@Start(onlyOnce = true)
	private void init() {
		if ( !Files.exists(Paths.get("./input/in.data"))) {
			long start = System.currentTimeMillis();
			System.out.println("create new random numbers");
			try (OutputStream out = new FileOutputStream("./input/in.data")) {
				Random rnd = new Random();
				byte[] bytes = new byte[1 << 30];
				rnd.nextBytes(bytes);
				out.write(bytes);
			} catch (IOException e) {
				throw new IOError(e);
			}
			System.out.println("created new random numbers, needed: " + (System.currentTimeMillis() - start));
		}
	}
	
	@Check
	private void sorttest() throws IOException {
		long[] data = read(new FileInputStream(INPUT_FILE));
		long[] origData = data.clone();
		sort(data);
		Arrays.sort(origData);
		Checker.assertArrayEquals(data, origData);
	}
	
	@Check
	private void comparetimetest() throws IOException {
		long start = System.currentTimeMillis();
		System.out.println("read_start");
		long[] data = readBinary(new FileInputStream(BINARY_INPUT_FILE));
		System.out.println("read_time: " + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		System.out.println("sort_start");
		sort(data);
		System.out.println("sort_time: " + (System.currentTimeMillis() - start));
	}
	
	private long[] readBinary(FileInputStream in) throws IOException {
		long[] res = new long[BINARY_LENGTH / 8];
		byte[] bytes = new byte[BINARY_LENGTH];
		Checker.assertEquals(res.length * 8, in.read(bytes));
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
	
	private void sort(long[] data) {
		sort(data, 0, data.length - 1);
	}
	
	private void sort(long[] data, final int lowIndex, final int highIndex) {
		int pivot;
		pivot = (highIndex + lowIndex) / 2;
		int currentLow = lowIndex, currentHigh = highIndex;
		big_loop: while (true) {
			for (; currentLow < pivot; currentLow ++ ) {
				if (data[currentLow] > data[pivot]) {
					long swap = data[currentLow];
					data[currentLow] = data[pivot];
					data[pivot] = swap;
					pivot = currentLow;
					break;
				}
			}
			for (; currentHigh > pivot; currentHigh -- ) {
				if (data[currentHigh] < data[pivot]) {
					long swap = data[currentHigh];
					data[currentHigh] = data[pivot];
					data[pivot] = swap;
					pivot = currentHigh;
					continue big_loop;
				}
			}
			if (pivot > lowIndex + 1) {
				sort(data, lowIndex, pivot - 1);
			}
			if (highIndex - pivot > 1) {
				sort(data, pivot + 1, highIndex);
			}
			return;
		}
	}
	
	public static void main(String[] args) {
		Checker.check(QuickSort.class).detailedPrint(System.out, 2);
	}
	
}
