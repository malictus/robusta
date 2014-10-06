package malictus.robusta.demo;

import java.io.*;
import malictus.robusta.file.*;

/**
 * Demonstrate and test several of the SmartFile and SmartRandomAccessFile features.
 */
public class SmartFileDemo {

	/**
	 * Run to demonstrate SmartFile and SmartRandomAccessFile features
	 *
	 * @param args not currently used
	 */
	public static void main(String[] args) {
		SmartFile x;
		SmartFile y;
		try {
			//Create a temp file that we'll read and write to.
			System.out.println("Creating temp file");
			x = new SmartFile(File.createTempFile("robusta", ".tmp"));

			//write several strings to the file
			System.out.println("Writing to file");
			x.writeStringToFile("Sample string to file. Another sample sentence. And another one.", true);
			x.appendStringToFile(" Here's one more. ");

			//show size of file
			System.out.println("Size of file is now " + x.getFileSizeString() + " which is " + SmartFile.bytesToKilobytes(x.length()) + " kilobytes");

			//copy file and check checksums of copied file
			System.out.println("Attempting to copy file...");
			y = new SmartFile(File.createTempFile("robusta", ".tmp"));
			SmartFile.copyFile(x, y, true);
			String xChecksum = x.getMD5Checksum();
			String yChecksum = y.getMD5Checksum();
			if (xChecksum.equals(yChecksum)) {
				System.out.println("Checksums match! Success");
			} else {
				throw new IOException("Checksums don't match");
			}

			//append the second sentence onto the end of the copied file
			System.out.println("Testing appendToFile method");
			SmartFile.appendToFile(x, y, 23, 48);
			//add a null character to the end, then read the entire null-terminated string
			SmartRandomAccessFile yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(yRaf.length());
			yRaf.writeByte(0);
			yRaf.seek(0);
			String test = yRaf.readNullTerminatedString();
			yRaf.close();
			System.out.println(test);
			if (!test.equals("Sample string to file. Another sample sentence. And another one. Here's one more. Another sample sentence. ")) {
				throw new IOException("Append to file test failed");
			}

			//test delete from file function
			System.out.println("Running delete from file tests");
			y.deleteFromFile(7, 14);
			yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(0);
			test = yRaf.readNullTerminatedString();
			yRaf.close();
			System.out.println(test);
			if (!test.equals("Sample to file. Another sample sentence. And another one. Here's one more. Another sample sentence. ")) {
				throw new IOException("Delete from file test failed");
			}

			//test insert to file function (from byte array)
			System.out.println("Insert to file tests");
			String i = "XXXXX";
			long oldSize = y.length();
			y.insertIntoFile(i.getBytes(), 2);
			long newSize = y.length();
			System.out.println("Old length = " + oldSize + "; New Size = " + newSize);
			if (!(newSize - 5 == oldSize)) {
				throw new IOException("Insert to file test failed; incorrect number of bytes");
			}
			yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(0);
			test = yRaf.readNullTerminatedString();
			yRaf.close();
			System.out.println(test);
			if (!test.equals("SaXXXXXmple to file. Another sample sentence. And another one. Here's one more. Another sample sentence. ")) {
				throw new IOException("Insert to file test failed");
			}

			//test insert to file function (from another file)
			y.deleteFromFile(2, 7);
			SmartFile.insertIntoFile(x, 0, 5, y, 2);
			yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(0);
			test = yRaf.readNullTerminatedString();
			yRaf.close();
			System.out.println(test);
			if (!test.equals("SaSamplmple to file. Another sample sentence. And another one. Here's one more. Another sample sentence. ")) {
				throw new IOException("Insert to file test from another file failed");
			}
			
			//test replace file function
			String string = "ABCDE";
			byte[] array = string.getBytes();
			//test all three kinds of replace operations (same, bigger, and smaller than original)
			y.replace(array, 0, 5);
			y.replace(array, 10, 11);
			y.replace(array, 20, 30);
			yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(0);
			test = yRaf.readNullTerminatedString();
			yRaf.close();
			System.out.println(test);
			if (!test.equals("ABCDEplmplABCDE to fABCDEer sample sentence. And another one. Here's one more. Another sample sentence. ")) {
				throw new IOException("Replace test from byte array file failed");
			}
			//now replace one file with part of another
			SmartFile.replace(x, 0, 13, y, 0, 15);
			yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(0);
			test = yRaf.readNullTerminatedString();
			yRaf.close();
			System.out.println(test);
			if (!test.equals("Sample string to fABCDEer sample sentence. And another one. Here's one more. Another sample sentence. ")) {
				throw new IOException("Replace test from byte array file failed");
			}
			SmartFile.replace(x, 18, 28, y, 18, 23);
			yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(0);
			test = yRaf.readNullTerminatedString();
			yRaf.close();
			System.out.println(test);
			if (!test.equals("Sample string to file. Another sample sentence. And another one. Here's one more. Another sample sentence. ")) {
				throw new IOException("Replace test from byte array file failed");
			}
			
			//now test writing binary data to a file, and reading it back
			System.out.print("Running binary data read/write tests... ");
			yRaf = new SmartRandomAccessFile(y, "rw");
			yRaf.seek(yRaf.length());
			long pos = yRaf.getFilePointer();
			yRaf.write16BitSignedBE((short)-2);
			yRaf.write16BitSignedLE((short)-2);
			//note that here we are writing an UNSIGNED short, so the value can exceed 32767
			yRaf.write16BitUnsignedBE(40000);
			yRaf.write16BitUnsignedLE(40000);
			yRaf.write32BitSignedBE(-4);
			yRaf.write32BitSignedLE(-4);
			//again, this value can be higher than if it were signed
			yRaf.write32BitUnsignedBE(2147483649L);
			yRaf.write32BitUnsignedLE(2147483649L);
			yRaf.write64BitSignedBE(13);
			yRaf.write64BitSignedLE(13);
			yRaf.seek(pos);
			//now read all the data that has just been written to confirm that it works
			short sho1 = yRaf.read16BitSignedBE();
			short sho2 = yRaf.read16BitSignedLE();
			if ( (sho1 != -2) || (sho2 != -2) ) {
				throw new IOException("Error reading/writing shorts.");
			}
			int int1 = yRaf.read16BitUnsignedBE();
			int int2 = yRaf.read16BitUnsignedLE();
			if ( (int1 != 40000) || (int2 != 40000) ) {
				throw new IOException("Error reading/writing shorts.");
			}
			int1 = yRaf.read32BitSignedBE();
			int2 = yRaf.read32BitSignedLE();
			if ( (int1 != -4) || (int2 != -4) ) {
				throw new IOException("Error reading/writing ints.");
			}
			long long1 = yRaf.read32BitUnsignedBE();
			long long2 = yRaf.read32BitUnsignedLE();
			if ( (long1 != 2147483649L) || (long2 != 2147483649L) ) {
				throw new IOException("Error reading/writing ints.");
			}
			long1 = yRaf.read64BitSignedBE();
			long2 = yRaf.read64BitSignedLE();
			if ( (long1 != 13) || (long2 != 13) ) {
				throw new IOException("Error reading/writing longs.");
			}
			System.out.println("Passed!");
			yRaf.close();
			

			//delete temp files and confirm that deletion happens (confirms that all file refs are successfully removed)
			System.out.println("Attempting to delete files");
			x.delete();
			y.delete();
			if (x.exists()) {
				throw new IOException("X temp file cannot be deleted.");
			}
			if (y.exists()) {
				throw new IOException("Y temp file cannot be deleted.");
			}
			System.out.println("All test passed; success! Finished!");
		} catch (IOException err) {
			System.out.println("ERROR");
			err.printStackTrace();
		}
	}

}

