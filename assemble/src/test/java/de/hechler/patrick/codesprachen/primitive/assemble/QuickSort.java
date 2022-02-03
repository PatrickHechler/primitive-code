package de.hechler.patrick.codesprachen.primitive.assemble;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;

@CheckClass
public class QuickSort {

	private static final String INPUT_FILE = "./input/longnumbers_BIG.txt";

	@Check
	private void sorttest() throws IOException {
		long[] data = read(new FileInputStream(INPUT_FILE));
		long[] origData = data.clone();
		sort(data);
		Arrays.sort(origData);
		Checker.assertArrayEquals(data, origData);
		print(data);
	}

	private long[] read(InputStream in) throws IOException {
		List<Long> result = new ArrayList<Long>();
		Scanner scanner = new Scanner(in);
		while (scanner.hasNext()) {
			result.add(scanner.nextLong());
		}
		long[] arr = new long[result.size()];
		for (int i = 0; i < result.size(); i++) {
			arr[i] = result.get(i);
		}
		return arr;
	}

	private void print(long[] data) {
		for (int i = 0; i < data.length; i++) {
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
			for (; currentLow < pivot; currentLow++) {
				if (data[currentLow] > data[pivot]) {
					long swap = data[currentLow];
					data[currentLow] = data[pivot];
					data[pivot] = swap;
					pivot = currentLow;
					break;
				}
			}
			for (; currentHigh > pivot; currentHigh--) {
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
