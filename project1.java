/*Edited Version
 * Note: The Program requests entry of both input file and key file names
 */
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ucd")
public class project1 {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// /////////////////////////////////////////////Reading the input File
		boolean valid1 = false;
		String data = "";
		String key = "";
		while (!valid1) {
			System.out.println("Enter the name of the INPUT file: ");
			Scanner get = new Scanner(System.in);
			String file = get.next();
			try {
				valid1 = true;
				FileReader reader = new FileReader(file);
				Scanner rescan = new Scanner(reader);
				while (rescan.hasNext()) {
					data = data + rescan.nextLine();
				}
			} catch (IOException e) {
				System.out
						.println("Re-enter the name of the file, Invalid File Name");
				valid1 = false;
			}
		}
		// /////////////////////////////////Reading key file
		boolean valid = false;
		while (!valid) {
			System.out.println("Enter the name of the KEY file: ");
			Scanner get = new Scanner(System.in);
			String file = get.next();
			try {
				valid = true;
				FileReader reader = new FileReader(file);
				Scanner rescan = new Scanner(reader);
				while (rescan.hasNext()) {
					key = key + rescan.nextLine();
				}
			} catch (IOException e) {
				System.out
						.println("Re-enter the name of the file, Invalid File Name");
				valid = false;
			}
		}
		PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
		System.setOut(out);
		System.out.println("Initial text");
		System.out.println(data);

		// /////////////////////////////////////////////////Pre-processing
		String pattern = "(\\s*)(\\W*)";
		String replace = "";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(data);
		data = m.replaceAll(replace);
		System.out.println("output after Preprocessing...");
		System.out.println(data);

		// ////////////////////////////////////////////////////Substitution
		char[] dataArray = data.toCharArray();
		char[] new_dataArray = new char[data.length()];
		char[] keyArray = key.toCharArray();
		int counter = 0;
		System.out.println("Output after Substitution...");
		for (char eachLetter : dataArray) {
			int message = (((int) eachLetter) - 65);
			int the_key = (int) (keyArray[counter % (keyArray.length)]) - 65;
			new_dataArray[counter] = (char) (((message + the_key) % 26) + 65);
			System.out.print(new_dataArray[counter]);
			counter++;
		}
		System.out.println();

		// /////////////////////////////////forming arrays and padding
		ArrayList<char[][]> theArrays = new ArrayList<char[][]>();
		int count = 0;
		char[] monoArray = new char[16];
		for (char eachLetter : new_dataArray) {
			monoArray[count % 16] = eachLetter;
			count++;
			if (count % 16 == 0) {
				theArrays.add(monoToBidi(monoArray, 4, 4));
			}
		}

		// padding the incomplete array
		if (count % 16 != 15) {
			for (int letter = count % 16; letter < 16; letter++) {
				monoArray[letter] = 'A';
			}
			theArrays.add(monoToBidi(monoArray, 4, 4));
		}
		System.out.println("Output after Padding...");
		printal(theArrays);

		// shifting padded arrays
		int counteri = 0;
		for (char[][] eachArray : theArrays) {
			theArrays.set(counteri, shiftArr(eachArray));
			counteri++;
		}
		System.out.println("Output after shifting arrays...");
		printal(theArrays);

		// /parity checking and adding.......
		System.out.println("Output after parity checking and adding...");
		ArrayList<String> outpu = getBinary(theArrays);
		print_hex(outpu);
		System.out.println("Output after complex multiplication");
		ArrayList<String> output = multi(tobin(outpu));
		print_hex(tohex(output));
	}

	/**
	 * Mono to bidi.
	 * 
	 * @param array
	 *            the array
	 * @param rows
	 *            the rows
	 * @param cols
	 *            the cols
	 * @return the char[][]
	 */
	public static char[][] monoToBidi(char[] array, int rows, int cols) {
		if (array.length != (rows * cols))
			throw new IllegalArgumentException("Invalid array length");

		char[][] bidi = new char[rows][cols];
		for (int i = 0; i < rows; i++)
			System.arraycopy(array, (i * cols), bidi[i], 0, cols);

		return bidi;
	}

	// printing 4x4 matrix
	/**
	 * Printm.
	 * 
	 * @param matrix
	 *            the matrix
	 */
	public static void printm(char matrix[][]) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.print("\n");
		}
	}

	// printing array-list
	/**
	 * Printal.
	 * 
	 * @param theArr
	 *            the the arr
	 */
	public static void printal(ArrayList<char[][]> theArr) {
		for (int i = 0; i < theArr.size(); i++) {
			printm(theArr.get(i));
			System.out.println();
		}
	}

	// shifting arrays
	/**
	 * Shift arr.
	 * 
	 * @param theArr
	 *            the the arr
	 * @return the char[][]
	 */
	public static char[][] shiftArr(char[][] theArr) {
		char[] shifted = { theArr[0][0], theArr[0][1], theArr[0][2],
				theArr[0][3], theArr[1][1], theArr[1][2], theArr[1][3],
				theArr[1][0], theArr[2][2], theArr[2][3], theArr[2][0],
				theArr[2][1], theArr[3][3], theArr[3][0], theArr[3][1],
				theArr[3][2] };
		return monoToBidi(shifted, 4, 4);
	}

	// printing out hexidecimals in correct format
	/**
	 * Print_hex.
	 * 
	 * @param theArr
	 *            the the arr
	 */
	public static void print_hex(ArrayList<String> theArr) {
		int count = 0;
		for (String each_hex : theArr) {
			System.out.print(each_hex + " ");
			count++;
			if (count % 4 == 0) {
				System.out.println();
			}
			if (count % 16 == 0) {
				System.out.println();
			}
		}

	}

	// //convert binary to hexadecimal
	/**
	 * Tohex.
	 * 
	 * @param theArr
	 *            the the arr
	 * @return the array list
	 */
	public static ArrayList<String> tohex(ArrayList<String> theArr) {
		ArrayList<String> newArr = new ArrayList<String>();
		for (String eachstr : theArr) {
			newArr.add(Integer.toHexString(Integer.parseInt(eachstr, 2)));
		}
		return newArr;
	}

	/**
	 * Tobin.
	 * 
	 * @param theArr
	 *            the the arr
	 * @return the array list
	 */
	public static ArrayList<String> tobin(ArrayList<String> theArr) {
		ArrayList<String> newArr = new ArrayList<String>();
		for (String eachstr : theArr) {
			newArr.add(Integer.toBinaryString(Integer.parseInt(eachstr, 16)));
		}
		return newArr;
	}

	// /parity checking//adding format
	/**
	 * Gets the binary.
	 * 
	 * @param theArr
	 *            the the arr
	 * @return the binary
	 */
	public static ArrayList<String> getBinary(ArrayList<char[][]> theArr) {
		ArrayList<String> binas = new ArrayList<String>();
		for (char[][] eachArray : theArr) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					int int_value = (int) eachArray[i][j];

					String the_binas = Integer.toBinaryString(int_value);
					if (the_binas.length() != 8) {
						the_binas = "0" + the_binas;
					}
					// /parity checking and adding
					int count = 0;
					char[] all = the_binas.toCharArray();
					for (int ii = 0; ii <= 7; ii++) {
						if ((int) ('1') == ((int) all[ii])) {
							count++;
						}
					}
					// /parity adding
					count = count % 2;
					if (count == 0) {
						all[0] = all[0];
					} else {
						if ((int) all[0] == (int) '1')
							all[0] = '0';
						else
							all[0] = '1';
					}
					StringBuilder sb = new StringBuilder(all.length);
					for (Character c : all)
						sb.append(c.charValue());
					binas.add(Integer.toHexString(Integer.parseInt(
							sb.toString(), 2)));
				}
			}
		}
		return binas;

	}

	// ///complex multiplication of array columns
	/**
	 * Multi.
	 * 
	 * @param str
	 *            the str
	 * @return the array list
	 */
	public static ArrayList<String> multi(ArrayList<String> str) {
		ArrayList<String> final_ans = str;
		for (int i = 0; i < (str.size() / 16); i++) {
			for (int ii = 0; ii < 4; ii++) {
				String a, ai, b, bi, c, ci, d, di = "";
				a = str.get((16 * i) + ii);
				b = str.get(((16 * i) + 4) + ii);
				c = str.get(((16 * i) + 8) + ii);
				d = str.get(((16 * i) + 12) + ii);

				ai = xor((xor(rgf_mul(a, 2), rgf_mul(b, 3))), (xor(c, d)));
				bi = xor((xor((xor(a, rgf_mul(b, 2))), rgf_mul(c, 3))), d);
				ci = xor((xor((xor(a, b)), rgf_mul(c, 2))), rgf_mul(d, 3));
				di = xor((xor((xor(rgf_mul(a, 3), b)), c)), rgf_mul(d, 2));

				final_ans.set(((16 * i) + ii), ai);
				final_ans.set((((16 * i) + 4) + ii), bi);
				final_ans.set((((16 * i) + 8) + ii), ci);
				final_ans.set((((16 * i) + 12) + ii), di);

			}
		}
		return final_ans;
	}

	// /shifting
	/**
	 * Rgf_mul.
	 * 
	 * @param str
	 *            the str
	 * @param n
	 *            the n
	 * @return the string
	 */
	public static String rgf_mul(String str, int n) {
		int i = Integer.parseInt(str, 2);
		String bi = Integer.toBinaryString(i);
		String shiftedi = Integer.toBinaryString(i << 1);
		shiftedi = '0' + shiftedi.substring(1);
		if (n == 2 && shiftedi.contains("1"))
			shiftedi = xor(shiftedi, "000011011");
		else if (n == 3) {
			shiftedi = xor(shiftedi, bi);
			if (shiftedi.contains("1"))
				shiftedi = xor(shiftedi, "000011011");
		}
		return shiftedi;
	}

	// /xor operation
	/**
	 * Xor.
	 * 
	 * @param str1
	 *            the str1
	 * @param str2
	 *            the str2
	 * @return the string
	 */
	public static String xor(String str1, String str2) {
		int stri = Integer.parseInt(str1, 2);
		int strii = Integer.parseInt(str2, 2);
		int val = stri ^ strii;

		return (Integer.toBinaryString(val));
	}

}
