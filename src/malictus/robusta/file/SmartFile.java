package malictus.robusta.file;

import java.io.*;
import java.net.URI;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import malictus.robusta.swing.*;

/**
 * A SmartFile adds additional functionality to a basic java.io.File object. This includes conversions to/from bytes/kilobyte/megabyte/gigabyte,
 * generating checksums, and enhanced methods of copying and appending files and file fragments.
 */
public class SmartFile extends File {

	private static final int BUFFER_SIZE = 65536;

	public SmartFile(String pathname) {
		super(pathname);
	}

	public SmartFile(String parent, String child) {
		super(parent, child);
	}

	public SmartFile(File parent, String child) {
		super(parent, child);
	}

	public SmartFile(URI uri) {
		super(uri);
	}

	public SmartFile(File file) {
		super(file.getPath());
	}

	/**
	 * Convert bytes to kilobytes.
	 *
	 * @param bytes byte amount
	 * @return byte amount converted to kilobytes
	 */
	public static double bytesToKilobytes(long bytes) {
		return ((double)bytes/1024d);
	}

	/**
	 * Convert kilobytes to bytes.
	 *
	 * @param kilobytes kilobyte amount
	 * @return amount converted to bytes
	 */
	public static long kilobytesToBytes(double kilobytes) {
		return (new Double(kilobytes * 1024d)).longValue();
	}

	/**
	 * Convert bytes to megabytes.
	 *
	 * @param bytes byte amount
	 * @return byte amount converted to megabytes
	 */
	public static double bytesToMegabytes(long bytes) {
		return (((double)bytes/1024d)/1024d);
	}

	/**
	 * Convert megabytes to bytes.
	 *
	 * @param megabytes megabyte amount
	 * @return amount converted to bytes
	 */
	public static long megabytesToBytes(double megabytes) {
		return (new Double(megabytes * 1024d * 1024d)).longValue();
	}

	/**
	 * Convert bytes to gigabytes.
	 *
	 * @param bytes byte amount
	 * @return byte amount converted to gigabytes
	 */
	public static double bytesToGigabytes(long bytes) {
		return (((double)bytes/1024d)/1024d/1024d);
	}

	/**
	 * Convert gigabytes to bytes.
	 *
	 * @param gigabytes gigabyte amount
	 * @return amount converted to bytes
	 */
	public static long gigabytesToBytes(double gigabytes) {
		return (new Double(gigabytes * 1024d * 1024d * 1024d)).longValue();
	}

	/**
	 * Return a human readable string that expresses the approximate number of bytes that is input. Based on the
	 * number of bytes, the string will return the value in bytes, KB, MB, or GB. For example, 1.23 GB, 2.7 MB, etc..
	 *
	 * @param bytes number of bytes
	 * @return the string that represents the number of bytes
	 */
	public static String getByteSizeString(long bytes) {
		DecimalFormat df = new DecimalFormat("0.00");
		if (SmartFile.bytesToGigabytes(bytes) > 1) {
			return df.format(SmartFile.bytesToGigabytes(bytes)) + " GB";
		}
		if (SmartFile.bytesToMegabytes(bytes) > 1) {
			return df.format(SmartFile.bytesToMegabytes(bytes)) + " MB";
		}
		if (SmartFile.bytesToKilobytes(bytes) > 1) {
			return df.format(SmartFile.bytesToKilobytes(bytes)) + " KB";
		}
		return bytes + " bytes";
	}

	/**
	 * Return a human-readable indication of the file size of this file.  (1.23 GB, 2.7 MB, etc.)
	 *
	 * @return the file size string
	 */
	public String getFileSizeString() {
		return SmartFile.getByteSizeString(this.length());
	}

	/**
	 * Copy a file to a new location.
	 *
	 * @param source the source file
	 * @param dest the destination file
	 * @param overwrite whether to overwrite if file exists already. If set to false, and file exists, and IOException will be thrown.
	 * @throws IOException if the file cannot be written for some reason
	 */
	public static void copyFile(File source, File dest, boolean overwrite) throws IOException {
		SmartFile.copyFile(source, dest, overwrite, null);
	}

	/**
	 * Copy a file to a new location.
	 *
	 * @param source the source file
	 * @param dest the destination file
	 * @param overwrite whether to overwrite if file exists already. If set to false, and file exists, and IOException will be thrown.
	 * @param spd a SmartProgressWindow to show progress on the task; this may be null.
	 * 			If progress is canceled, this method will close file refs but will NOT delete the incomplete file itself.
	 * @throws IOException if the file cannot be written for some reason;
	 */
	public static void copyFile(File source, File dest, boolean overwrite, SmartProgressWindow spd) throws IOException {
		//make sure dest isn't same as orig
		if (dest.getPath().equals(source.getPath())) {
			throw new IOException("Destination is the same as the original.");
		}
		if (spd != null) {
			spd.setProgressCounterMax(100);
			spd.setProgressCounter(0);
		}
		if (dest.exists()) {
			if (overwrite) {
				dest.delete();
			} else {
				throw new IOException("File already exists");
			}
		}
		dest.createNewFile();
		FileInputStream fin = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(dest);
		try {
			//copy contents
			byte[] buffer = new byte[BUFFER_SIZE];
	        int len;
	        int counter = 0;
	        long last = (long)(source.length() / (float)BUFFER_SIZE);
	        if (last == 0) {
				last = 1;
			}
	        while ((len = fin.read(buffer)) > 0) {
	        	if (spd != null) {
		    		if (spd.wasCanceled()) {
		    			fin.close();
		    	        fos.close();
		    			spd.taskFinished();
		    			return;
		    		}
		    		counter = counter + 1;
		    		float x = ((float)(counter) / (float)last) * 100f;
		    		spd.setProgressCounter((int)x);
		    	}
	            fos.write(buffer, 0, len);
	        }
	        fin.close();
	        fos.close();
		} catch (IOException err) {
			fin.close();
			fos.close();
			throw err;
		}
	}
	
	/**
	 * Writes data from a source file to the destination file. Any existing data in the source file will be overwritten.
	 *
	 * @param source the source file
	 * @param dest the destination file. If this file doesn't exist, it will be created first.
	 * @param sourceStart start position in the source file for the data to be copied
	 * @param sourceEnd end position in the source file for the data to be copied
	 * @param destStart start position in the destination file to begin writing data
	 * @throws IOException if the read/write operations fail
	 */
	public static void writeToFile(File source, File dest, long sourceStart, long sourceEnd, long destStart) throws IOException {
		SmartFile.writeToFile(source, dest, sourceStart, sourceEnd, destStart, null);
	}
	
	/**
	 * Writes data from a source file to the destination file. Any existing data in the source file will be overwritten.
	 *
	 * @param source the source file
	 * @param dest the destination file. If this file doesn't exist, it will be created first.
	 * @param sourceStart start position in the source file for the data to be copied
	 * @param sourceEnd end position in the source file for the data to be copied
	 * @param destStart start position in the destination file to begin writing data
	 * @param spd a SmartProgressWindow to track progress of the procedure; may be null.
	 * @throws IOException if the read/write operations fail
	 */
	public static void writeToFile(File source, File dest, long sourceStart, long sourceEnd, long destStart, SmartProgressWindow spd) throws IOException {
		//make sure dest isn't same as orig
		if (dest.getPath().equals(source.getPath())) {
			throw new IOException("Destination is the same as the original.");
		}
		if (!source.exists()) {
			throw new IOException("File does not exist");
		}
		if (sourceStart >= sourceEnd) {
			throw new IOException("Incorrect start and end times");
		}
		if (sourceEnd > source.length()) {
			throw new IOException("End time exceeds file length");
		}
		if (destStart > dest.length()) {
			throw new IOException("Incorrect destination start time");
		}
		if (!dest.exists()) {
			dest.createNewFile();
    	}
		SmartRandomAccessFile fin = new SmartRandomAccessFile(source, "r");
		SmartRandomAccessFile fos = new SmartRandomAccessFile(dest, "rw");
		if (spd != null) {
			spd.setProgressCounterMax(100);
			spd.setProgressCounter(0);
		}
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
	        int counter = 0;
	        long last = (long)((sourceEnd - sourceStart) / (float)BUFFER_SIZE);
	        if (last == 0) {
				last = 1;
			}
	        fin.seek(sourceStart);
	        fos.seek(destStart);
	        while (fin.getFilePointer() < sourceEnd) {
	        	if (spd != null) {
		    		if (spd.wasCanceled()) {
		    			fin.close();
		    		    fos.close();
		    			spd.taskFinished();
		    			return;
		    		}
		    		counter = counter + 1;
		    		float x = ((float)(counter) / (float)last) * 100f;
		    		spd.setProgressCounter((int)x);
		    	}
	        	int len = fin.read(buffer);
	        	if ((fin.getFilePointer()) <= sourceEnd) {
	        		fos.write(buffer, 0, len);
	        	} else {
	        		fos.write(buffer, 0, ((int)((sourceEnd - sourceStart) % BUFFER_SIZE)));
	        	}
	        }
	        fin.close();
	        fos.close();
		} catch (IOException err) {
			fin.close();
			fos.close();
			throw err;
		} catch (Exception generalerr) {
			fin.close();
			fos.close();
			throw new IOException("Exception thrown while copying file:\n" + generalerr.getMessage());
		}
	}

	/**
	 * Appends data from a source file to the end of the destination file.
	 *
	 * @param source the source file
	 * @param dest the destination file. If this file doesn't exist, it will be created first.
	 * @param start start position for the data to be copied
	 * @param end end position for the data to be copied
	 * @throws IOException if the append fails
	 */
	public static void appendToFile(File source, File dest, long start, long end) throws IOException {
		SmartFile.appendToFile(source, dest, start, end, null);
	}

	/**
	 * Appends data from a source file to the end of the destination file.
	 *
	 * @param source the source file
	 * @param dest destination file. If this file doesn't exist, it will be created first.
	 * @param start start position for the data to be copied
	 * @param end end position for the data to be copied
	 * @param spd a SmartProgressWindow to track progress of the copy procedure; may be null. If process is canceled, appended file will not be deleted.
	 * @throws IOException if the append fails
	 */
	public static void appendToFile(File source, File dest, long start, long end, SmartProgressWindow spd) throws IOException {
		SmartFile.writeToFile(source, dest, start, end, dest.length(), spd);
	}

	/**
	 * Delete the specified portion of this file. Any bytes after the deleted portion will be moved appropriately.
	 *
	 * @param start start position for the data to be deleted
	 * @param end end position for the data to be deleted
	 * @throws IOException if the delete fails
	 */
	public void deleteFromFile(long start, long end) throws IOException {
		deleteFromFile(start, end, null);
	}

	/**
	 * Delete the specified portion of this file. Any bytes after the deleted portion will be moved appropriately.
	 *
	 * @param start start position for the data to be deleted
	 * @param end end position for the data to be deleted
	 * @param spd a SmartProgressWindow to track progress of the delete procedure; may be null.
	 * 		Process should not be canceled since file corruption would result.
	 * @throws IOException if the delete fails
	 */
	public void deleteFromFile(long start, long end, SmartProgressWindow spd) throws IOException {
		if (spd != null) {
			spd.setProgressCounterMax(100);
			spd.setProgressCounter(0);
		}
		SmartRandomAccessFile raf = new SmartRandomAccessFile(this, "rw");
		try {
			//first check to see if portion being deleted is at very end of file
			if (end >= this.length()) {
				//easy!
				raf.setLength(start);
				raf.close();
				return;
			}
			raf.seek(end);
			long curpos = raf.getFilePointer();
			byte[] buf = new byte[BUFFER_SIZE];
			int counter = 0;
			int last = (int)((raf.length() - curpos) / (float)BUFFER_SIZE);
			if (last == 0) {
				last = 1;
			}
			while ((curpos + BUFFER_SIZE) < raf.length()) {
				if (spd != null) {
		    		counter = counter + 1;
		    		float x = ((float)(counter) / (float)last) * 100f;
		    		spd.setProgressCounter((int)x);
		    	}
				int x = raf.read(buf);
				if (x != buf.length) {
					throw new IOException("Read error while moving data");
				}
				raf.seek(curpos - (end - start));
				raf.write(buf);
				raf.seek(curpos + BUFFER_SIZE);
				curpos = raf.getFilePointer();
			}
			//finish up any remaining bytes
			if (raf.length() != curpos) {
				buf = new byte[(int)(raf.length() - curpos)];
				int x = raf.read(buf);
				if (x != buf.length) {
					throw new Exception("Read error 2 while moving data");
				}
				raf.seek(curpos - (end - start));
				raf.write(buf);
			}
			//remove anything after this
			long newEnd = raf.getFilePointer();
			raf.setLength(newEnd);
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		} catch (Exception err) {
			raf.close();
			err.printStackTrace();
			throw new IOException("General error");
		}
	}

	/**
	 * Insert data from one file into another file. This data can be inserted into any place in the destination file, any bytes after the insert will be
	 * moved appropriately.
	 *
	 * @param source the source file to copy from
	 * @param sourceStart the start position in the source file to copy from
	 * @param sourceEnd the end position in the source file to copy from
	 * @param dest the destination file
	 * @param destStart the position in the destination file to begin writing inserted data
	 * @throws IOException if the insert fails
	 */
	public static void insertIntoFile(File source, long sourceStart, long sourceEnd, File dest, long destStart) throws IOException {
		SmartFile.insertIntoFile(source, sourceStart, sourceEnd, dest, destStart, null);
	}

	/**
	 * Insert data from one file into another file. This data can be inserted into any place in the destination file;
	 * all bytes after the insert will be moved appropriately.
	 *
	 * @param source the source file to copy from
	 * @param sourceStart the start position in the source file to copy from
	 * @param sourceEnd the end position in the source file to copy from
	 * @param dest the destination file
	 * @param destStart the position in the destination file to begin writing inserted data
	 * @param spd a SmartProgressWindow to track progress of the insert procedure; may be null. Process should not be canceled since file corruption would result.
	 * @throws IOException if the insert fails
	 */
	public static void insertIntoFile(File source, long sourceStart, long sourceEnd, File dest, long destStart, SmartProgressWindow spd) throws IOException {
		if (spd != null) {
			spd.setProgressCounterMax(100);
			spd.setProgressCounter(0);
		}
		if ((sourceEnd < sourceStart) || (sourceStart < 0)) {
			throw new IOException("Incorrect source start and end points");
		}
		if (destStart < 0) {
			throw new IOException("Incorrect destination start point");
		}
		SmartRandomAccessFile rafDest = new SmartRandomAccessFile(dest, "rw");
		SmartRandomAccessFile rafSource = new SmartRandomAccessFile(source, "r");
		byte[] buf = new byte[BUFFER_SIZE];
		try {
			//first, move all existing data in dest file over by the appropriate amount
			long oldend = rafDest.length();
			long moveAmt = sourceEnd - sourceStart;
			rafDest.setLength(rafDest.length() + moveAmt);
			//start at the end so we don't overwrite data before it's read!
			rafDest.seek(oldend);
			long curpos;
			int counter = 0;
			int last = (int)((rafDest.getFilePointer() - destStart) / BUFFER_SIZE);
			if (last == 0) {
				last = 1;
			}
			while ((rafDest.getFilePointer() - BUFFER_SIZE) >= destStart) {
				if (spd != null) {
		    		counter = counter + 1;
		    		float x = ((float)(counter) / (float)last) * 100f;
		    		spd.setProgressCounter((int)x);
		    	}
				curpos = rafDest.getFilePointer();
				rafDest.seek(curpos - BUFFER_SIZE);
				int x = rafDest.read(buf);
				if (x != buf.length) {
					throw new IOException("Read error while inserting file");
				}
				rafDest.seek(curpos - BUFFER_SIZE + moveAmt);
				rafDest.write(buf);
				rafDest.seek(curpos - BUFFER_SIZE);
			}
			//get any leftover bytes
			if (rafDest.getFilePointer() != destStart) {
				buf = new byte[(int)(rafDest.getFilePointer() - destStart)];
				rafDest.seek(destStart);
				int x = rafDest.read(buf);
				if (x != (rafDest.getFilePointer() - destStart)) {
					throw new IOException("Read error while inserting file");
				}
				rafDest.seek(destStart + moveAmt);
				rafDest.write(buf);
			}
			//now write the new inserted bytes
			if (spd != null) {
				spd.setProgressCounter(0);
			}
			counter = 0;
			last = (int)((sourceEnd - sourceStart) / BUFFER_SIZE);
			if (last == 0) {
				last = 1;
			}
			buf = new byte[BUFFER_SIZE];
			rafSource.seek(sourceStart);
			rafDest.seek(destStart);
			while ((rafSource.getFilePointer() + BUFFER_SIZE) <= sourceEnd) {
				if (spd != null) {
		    		counter = counter + 1;
		    		float x = ((float)(counter) / (float)last) * 100f;
		    		spd.setProgressCounter((int)x);
		    	}
				int x = rafSource.read(buf);
				if (x != buf.length) {
					throw new IOException("Read error while reading source file");
				}
				rafDest.write(buf);
			}
			//get the last few bytes
			if (rafSource.getFilePointer() < sourceEnd) {
				buf = new byte[(int)(sourceEnd - rafSource.getFilePointer())];
				int x = rafSource.read(buf);
				if (x != buf.length) {
					throw new IOException("Read error while reading source file");
				}
				rafDest.write(buf);
			}
			//DONE!
			rafDest.close();
			rafSource.close();
		} catch (IOException err) {
			rafDest.close();
			rafSource.close();
			throw err;
		} catch (Exception err) {
			err.printStackTrace();
			rafDest.close();
			rafSource.close();
			throw new IOException("General error");
		}
	}

	/**
	 * Insert data from a byte array into this file. This data can be inserted into any place in the destination file; all bytes after the insert will be
	 * moved appropriately.
	 *
	 * @param bytearray the array to write data from
	 * @param start the place in the file to begin writing the data
	 * @throws IOException if read/write error occurs
	 */
	public void insertIntoFile(byte[] bytearray, long start) throws IOException {
		insertIntoFile(bytearray, start, null);
	}
	
	/**
	* Insert data from a byte array into this file. This data can be inserted into any place in the destination file; all bytes after the insert will be
	 * moved appropriately.
	 *
	 * @param bytearray the array to write data from
	 * @param offset start position to begin reading in the byte array
	 * @param len number of bytes to read in byte array
	 * @param start the place in the file to begin writing the data
	 * @throws IOException if read/write error occurs
	 */
	public void insertIntoFile(byte[] bytearray, int offset, int len, int start)  throws IOException {
		insertIntoFile(bytearray, offset, start, len, null);
	}
	
	/**
	 * Insert data from a byte array into this file. This data can be inserted into any place in the destination file; all bytes after the insert will be
	 * moved appropriately.
	 *
	 * @param bytearray the array to write data from
	 * @param start the place in the file to begin writing the data
	 * @param spd a SmartProgressWindow to track progress of the insert procedure; may be null. Process should not be canceled since file corruption would result.
	 * @throws IOException if read/write error occurs
	 */
	public void insertIntoFile(byte[] bytearray, long start, SmartProgressWindow spd) throws IOException {
		insertIntoFile(bytearray, 0, bytearray.length, start, spd);
	}

	/**
	 * Insert data from a byte array into this file. This data can be inserted into any place in the destination file; all bytes after the insert will be
	 * moved appropriately.
	 *
	 * @param bytearray the array to write data from
	 * @param offset start position to begin reading in the byte array
	 * @param len number of bytes to read in byte array
	 * @param start the place in the file to begin writing the data
	 * @param spd a SmartProgressWindow to track progress of the insert procedure; may be null. Process should not be canceled since file corruption would result.
	 * @throws IOException if read/write error occurs
	 */
	public void insertIntoFile(byte[] bytearray, int offset, int len, long start, SmartProgressWindow spd) throws IOException {
		if (spd != null) {
			spd.setProgressCounterMax(100);
			spd.setProgressCounter(0);
		}
		if (bytearray == null) {
			throw new IOException("Byte array is null");
		}
		if (bytearray.length == 0) {
			return;
		}
		if ((start < 0) || (start > this.length())) {
			throw new IOException("Incorrect start value");
		}
		SmartRandomAccessFile raf = new SmartRandomAccessFile(this, "rw");
		byte[] buf = new byte[BUFFER_SIZE];
		try {
			//first, move all existing data over by the appropriate amount
			long oldend = raf.length();
			raf.setLength(raf.length() + len);
			//start at the end so we don't overwrite data before it's read!
			raf.seek(oldend);
			long curpos;
			int counter = 0;
			int last = (int)((raf.getFilePointer() - start) / BUFFER_SIZE);
			if (last == 0) {
				last = 1;
			}
			while ((raf.getFilePointer() - BUFFER_SIZE) >= start) {
				if (spd != null) {
		    		counter = counter + 1;
		    		float x = ((float)(counter) / (float)last) * 100f;
		    		spd.setProgressCounter((int)x);
		    	}
				curpos = raf.getFilePointer();
				raf.seek(curpos - BUFFER_SIZE);
				int x = raf.read(buf);
				if (x != buf.length) {
					throw new IOException("Read error while inserting file");
				}
				raf.seek(curpos - BUFFER_SIZE + len);
				raf.write(buf);
				raf.seek(curpos - BUFFER_SIZE);
			}
			//get any leftover bytes
			if (raf.getFilePointer() != start) {
				buf = new byte[(int)(raf.getFilePointer() - start)];
				raf.seek(start);
				int x = raf.read(buf);
				if (x != (raf.getFilePointer() - start)) {
					throw new IOException("Read error while inserting file");
				}
				raf.seek(start + len);
				raf.write(buf);
			}
			//now write new inserted bytes
			raf.seek(start);
			raf.write(bytearray, offset, len);
			//DONE!
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		} catch (Exception err) {
			err.printStackTrace();
			raf.close();
			throw new IOException("General error");
		}
	}
	
	/**
	 * Replace part of one file with part of another. The new data does not have to be the same size as the portion that
	 * is being replaced.
	 * 
	 * @param source the source file that data will be copied from
	 * @param sourceStart the start byte position for data to be copied
	 * @param sourceEnd the end byte position for data to be copied
	 * @param dest the destination file that will be written to
	 * @param destStart the start byte position that will be replaced
	 * @param destEnd the end byte position that will be replaced
	 * @throws IOException if an error occur in reading or writing the files
	 */
	public static void replace(File source, long sourceStart, long sourceEnd, File dest, long destStart, long destEnd) throws IOException {
		SmartFile.replace(source, sourceStart, sourceEnd, dest, destStart, destEnd, null);
	}
	
	/**
	 * Replace part of one file with part of another. The new data does not have to be the same size as the portion that
	 * is being replaced.
	 * 
	 * @param source the source file that data will be copied from
	 * @param sourceStart the start byte position for data to be copied
	 * @param sourceEnd the end byte position for data to be copied
	 * @param dest the destination file that will be written to
	 * @param destStart the start byte position that will be replaced
	 * @param destEnd the end byte position that will be replaced
	 * @param spd a SmartProgressWindow that can track progress of this task; may be null
	 * @throws IOException if an error occur in reading or writing the files
	 */
	public static void replace(File source, long sourceStart, long sourceEnd, File dest, long destStart, long destEnd, SmartProgressWindow spd) throws IOException {
		if (spd != null) {
			spd.setProgressCounterMax(100);
			spd.setProgressCounter(0);
		}
		SmartFile smartDest = new SmartFile(dest);
		if ((sourceStart < 0) || (sourceStart > source.length())) {
			throw new IOException("Incorrect source start value");
		}
		if ((destStart < 0) || (destStart > dest.length())) {
			throw new IOException("Incorrect dest start value");
		}
		if ((sourceEnd < 0) || (sourceEnd < sourceStart)) {
			throw new IOException("Incorrect source end value");
		}
		if ((destEnd < 0) || (destEnd < destStart)) {
			throw new IOException("Incorrect dest end value");
		}
		//calcuate how much the file size actually needs to change
		long removeAmt = destEnd - destStart;
		long addAmt = sourceEnd - sourceStart;
		if (removeAmt == addAmt) {
			//the easiest case; just directly write over the old data and that's it!
			SmartFile.writeToFile(source, dest, sourceStart, sourceEnd, destStart, spd);
		} else if (removeAmt > addAmt) {
			//file will shrink somewhat
			smartDest.deleteFromFile(destStart, destStart + (removeAmt - addAmt), spd);
			SmartFile.writeToFile(source, dest, sourceStart, sourceEnd, destStart, spd);
		} else {
			//file size will increase somewhat
			//first overwrite
			SmartFile.writeToFile(source, dest, sourceStart, sourceStart + (int)removeAmt, destStart, spd);
			//then insert
			SmartFile.insertIntoFile(source, sourceStart + removeAmt, sourceEnd, dest, destStart + removeAmt, spd);
		}
	}
	
	/**
	 * Replace part of the current file with the specified byte array. New array does not have to be the same size as the portion that
	 * is being replaced.
	 * 
	 * @param bytearray array of bytes to insert into file
	 * @param start beginning byte position of data that will be replaced
	 * @param end end byte position of data that will be replaced
	 * @throws IOException if an error occurs in writing the file
	 */
	public void replace(byte[] bytearray, long start, long end) throws IOException {
		replace(bytearray, start, end, null);
	}
	
	/**
	 * Replace part of the current file with the specified byte array. New array does not have to be the same size as the portion that
	 * is being replaced.
	 * 
	 * @param bytearray array of bytes to insert into file
	 * @param start beginning byte position of data that will be replaced
	 * @param end end byte position of data that will be replaced
	 * @param spd a SmartProgressWindow that can track progress of this task; may be null
	 * @throws IOException if an error occurs in writing the file
	 */
	public void replace(byte[] bytearray, long start, long end, SmartProgressWindow spd) throws IOException {
		if (spd != null) {
			spd.setProgressCounterMax(100);
			spd.setProgressCounter(0);
		}
		if (bytearray == null) {
			throw new IOException("Byte array is null");
		}
		if ((start < 0) || (start > this.length())) {
			throw new IOException("Incorrect start value");
		}
		if ((end < 0) || (end < start)) {
			throw new IOException("Incorrect end value");
		}
		//calcuate how much the file size actually needs to change
		long removeAmt = end - start;
		int addAmt = bytearray.length;
		SmartRandomAccessFile raf = new SmartRandomAccessFile(this, "rw");
		try {
			if (removeAmt == addAmt) {
				//the easiest case; just directly write over the old data and that's it!
				raf.seek(start);
				raf.write(bytearray);
			} else if (removeAmt > addAmt) {
				//file will shrink somewhat
				this.deleteFromFile(start, start + (removeAmt - addAmt), spd);
				raf.seek(start);
				raf.write(bytearray);
			} else {
				//file size will increase somewhat
				//first overwrite
				raf.seek(start);
				raf.write(bytearray, 0, (int)removeAmt);
				//then insert
				this.insertIntoFile(bytearray, (int)removeAmt, (int)(bytearray.length - removeAmt), raf.getFilePointer(), spd);
			}
			raf.close();
		} catch (IOException err) {
			raf.close();
			throw err;
		}
	}
		
    /**
     * Will write a string out to this file. This does not append, but rather creates a new file (or erases a previous one),
     * and writes the string to the new file.
     *
     * @param theString  The string to write out
     * @param overwrite whether to write the string if the file already exists. If set to false, and file exists, an exception will be thrown.
     * @throws IOException if file can't be written
     */
    public void writeStringToFile(String theString, boolean overwrite) throws IOException {
    	if (this.exists()) {
    		if (overwrite) {
    			this.delete();
    		} else {
    			throw new IOException("File already exists");
    		}
    	}
    	this.createNewFile();
    	FileWriter fw = new FileWriter(this);
    	try {
    		fw.write(theString);
    		fw.flush();
    		fw.close();
    	} catch (IOException e) {
    		fw.flush();
    		fw.close();
    		throw e;
    	}
    }

    /**
     * Will append a string to the end of this file. If this file does not already exist,
     * it will be created automatically.
     *
     * @param theString  The string to append
     * @throws IOException if file can't be written
     */
    public void appendStringToFile(String theString) throws IOException {
    	if (!this.exists()) {
    		this.createNewFile();
    	}
    	FileWriter fw = new FileWriter(this, true);
    	try {
    		fw.append(theString);
    		fw.flush();
    		fw.close();
    	} catch (IOException e) {
    		fw.flush();
    		fw.close();
    		throw e;
    	}
    }

    /**
	 * Returns an MD5 checksum value for the file.
	 *
	 * @return a string representing the checksum value
	 * @throws IOException if file not found or can't be read
	 */
	public String getMD5Checksum() throws IOException {
		return getMD5Checksum(0, this.length(), null);
	}

	/**
	 * Returns an MD5 checksum value for the file.
	 *
	 * @param spw a SmartProgressWindow for displaying progress information
	 * @return a string representing the checksum value
	 * @throws IOException if file not found or can't be read
	 */
	public String getMD5Checksum(SmartProgressWindow spw) throws IOException {
		return getMD5Checksum(0, this.length(), spw);
	}

	/**
	 * Returns an MD5 checksum value for a portion of the file.
	 *
	 * @param start the start position of the portion to checksum
	 * @param end the end position of the portion to checksum
	 * @return a string representing the checksum value
	 * @throws IOException if file not found or can't be read
	 */
	public String getMD5Checksum(long start, long end) throws IOException {
		return getMD5Checksum(start, end, null);
	}

	/**
	 * Returns an MD5 checksum value for a portion of the file.
	 *
	 * @param start the start position of the portion to checksum
	 * @param end the end position of the portion to checksum
	 * @param spw a SmartProgressWindow for displaying progress information
	 * @return a string representing the checksum value
	 * @throws IOException if file not found or can't be read
	 */
	public String getMD5Checksum(long start, long end, SmartProgressWindow spw) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(this, "r");
		long m = end - start;
		byte[] buffer = new byte[BUFFER_SIZE];
		if (spw != null) {
			spw.setProgressCounterMax(100);
			spw.setProgressCounter(0);
		}
	    MessageDigest complete = null;
	    try {
	    	complete = MessageDigest.getInstance("MD5");
	    } catch (Exception err) {
	    	err.printStackTrace();
	    	throw new IOException("Error generating MD5");
	    }
	    try {
	    	raf.seek(start);
	    	int numRead;
		    do {
		    	if (raf.getFilePointer() >= (end - BUFFER_SIZE)) {
		    		buffer = new byte[(int)(end - raf.getFilePointer())];
		    	}
		    	numRead = raf.read(buffer);
		    	if (numRead > 0) {
		    		complete.update(buffer, 0, numRead);
		        }
		    	if (spw != null) {
		    		if (spw.wasCanceled()) {
		    			spw.taskFinished();
		    			return "";
		    		}
		    		long l = raf.getFilePointer() - start;
		    		float x = ((float)l / (float)m) * 100f;
		    		spw.setProgressCounter((int)x);
		    	}
		    } while (numRead > 0);
		    raf.close();
		    byte[] inn = complete.digest();
		    byte ch = 0x00;
		    int i = 0;
		    String pseudo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		    StringBuffer out = new StringBuffer(inn.length * 2);
		    while (i < inn.length) {
		        ch = (byte) (inn[i] & 0xF0);
		        ch = (byte) (ch >>> 4);
		        ch = (byte) (ch & 0x0F);
		        out.append(pseudo[ (int) ch]);
		        ch = (byte) (inn[i] & 0x0F);
		        out.append(pseudo[ (int) ch]);
		        i++;
		    }
		    return new String(out);
	    } catch (IOException err) {
	    	raf.close();
	    	throw err;
	    }
	}
}
