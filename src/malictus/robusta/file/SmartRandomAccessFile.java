package malictus.robusta.file;

import java.io.*;

/**
 * A SmartRandomAccessFile is a RandomAccessFile with additional functionality.
 * This includes working with both signed and unsigned numbers, both little
 * and big endian, as well as null-terminated strings.
 */
public class SmartRandomAccessFile extends RandomAccessFile {

	public SmartRandomAccessFile(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}

	public SmartRandomAccessFile(String name, String mode) throws FileNotFoundException {
		super(name, mode);
	}

	/**
	 * Read a 4-character ASCII chunk ID such as those used in the RIFF/WAV file format
	 */
	public String readChunkID() throws IOException {
		byte[] buf = new byte[4];
		buf[0] = this.readByte();
		buf[1] = this.readByte();
		buf[2] = this.readByte();
		buf[3] = this.readByte();
		return new String(buf);
	}

	/**
	 * Read an 8-byte signed number - little endian
	 */
	public long read64BitSignedLE() throws IOException {
		long inputNumber = this.readLong();
		inputNumber = Long.reverseBytes(inputNumber);
		return inputNumber;
	}

	/**
	 * Write an 8-byte signed number - little endian
	 */
	public void write64BitSignedLE(long inputNumber) throws IOException {
		inputNumber = Long.reverseBytes(inputNumber);
		this.writeLong(inputNumber);
	}

	/**
	 * Read an 8-byte signed number - big endian
	 */
	public long read64BitSignedBE() throws IOException {
		long inputNumber = this.readLong();
		return inputNumber;
	}

	/**
	 * Write an 8-byte signed number - big endian
	 */
	public void write64BitSignedBE(long inputNumber) throws IOException {
		this.writeLong(inputNumber);
	}

	/**
	 * Read a 4-byte unsigned number - little endian
	 */
	public long read32BitUnsignedLE() throws IOException {
		int inputNumber = this.readInt();
		inputNumber = Integer.reverseBytes(inputNumber);
		long output = inputNumber & 0xffffffffL;
		return output;
	}

	/**
	 * Write a 4-byte unsigned number - little endian
	 */
	public void write32BitUnsignedLE(long inputNumber) throws IOException, NumberFormatException {
		if ((inputNumber < 0) || (inputNumber > 4294967295L)) {
			throw new NumberFormatException("Number is not in range");
		}
		int output = (int)(inputNumber & 0xffffffffL);
		output = Integer.reverseBytes(output);
		this.writeInt(output);
	}

	/**
	 * Read a 4-byte unsigned number - big endian
	 */
	public long read32BitUnsignedBE() throws IOException {
		int inputNumber = this.readInt();
		long output = inputNumber & 0xffffffffL;
		return output;
	}

	/**
	 * Write a 4-byte unsigned number - big endian
	 */
	public void write32BitUnsignedBE(long inputNumber) throws IOException {
		if ((inputNumber < 0) || (inputNumber > 4294967295L)) {
			throw new NumberFormatException("Number is not in range");
		}
		int output = (int)(inputNumber & 0xffffffffL);
		this.writeInt(output);
	}

	/**
	 * Read a 4-byte signed number - little endian
	 */
	public int read32BitSignedLE() throws IOException {
		int inputNumber = this.readInt();
		inputNumber = Integer.reverseBytes(inputNumber);
		return inputNumber;
	}

	/**
	 * Write a 4-byte signed number - little endian
	 */
	public void write32BitSignedLE(int inputNumber) throws IOException {
		int output = Integer.reverseBytes(inputNumber);
		this.writeInt(output);
	}

	/**
	 * Read a 4-byte signed number - big endian
	 */
	public int read32BitSignedBE() throws IOException {
		return this.readInt();
	}

	/**
	 * Write a 4-byte signed number - big endian
	 */
	public void write32BitSignedBE(int inputNumber) throws IOException {
		this.writeInt(inputNumber);
	}

	/**
	 * Read a 2-byte unsigned number - little endian
	 */
	public int read16BitUnsignedLE() throws IOException {
		short inputNumber = this.readShort();
		inputNumber = Short.reverseBytes(inputNumber);
		int output = inputNumber & 0xffff;
		return output;
	}

	/**
	 * Write a 2-byte unsigned number - little endian
	 */
	public void write16BitUnsignedLE(int inputNumber) throws IOException {
		if ((inputNumber < 0) || (inputNumber > 65535)) {
			throw new NumberFormatException("Number is not in range");
		}
		short output = (short)(inputNumber & 0xffff);
		output = Short.reverseBytes(output);
		this.writeShort(output);
	}

	/**
	 * Read a 2-byte unsigned number - big endian
	 */
	public int read16BitUnsignedBE() throws IOException {
		short inputNumber = this.readShort();
		int output = inputNumber & 0xffff;
		return output;
	}

	/**
	 * Write a 2-byte unsigned number - big endian
	 */
	public void write16BitUnsignedBE(int inputNumber) throws IOException {
		if ((inputNumber < 0) || (inputNumber > 65535)) {
			throw new NumberFormatException("Number is not in range");
		}
		short output = (short)(inputNumber & 0xffff);
		this.writeShort(output);
	}

	/**
	 * Read a 2-byte signed number - little endian
	 */
	public short read16BitSignedLE() throws IOException {
		short inputNumber = this.readShort();
		inputNumber = Short.reverseBytes(inputNumber);
		return inputNumber;
	}

	/**
	 * Write a 2-byte signed number - little endian
	 */
	public void write16BitSignedLE(short inputNumber) throws IOException {
		inputNumber = Short.reverseBytes(inputNumber);
		this.writeShort(inputNumber);
	}

	/**
	 * Read a 2-byte signed number - big endian
	 */
	public short read16BitSignedBE() throws IOException {
		return this.readShort();
	}

	/**
	 * Write a 2-byte signed number - big endian
	 */
	public void write16BitSignedBE(short inputNumber) throws IOException {
		this.writeShort(inputNumber);
	}	

	/**
	 * Reads and returns a null-terminated, 8-bit ANSI string from a file. Does not append the null character
	 * itself to the returned string.
	 *
	 * @return the null terminated string
	 * @throws Exception if the file can't be read
	 */
	public String readNullTerminatedString() throws IOException {
		return readNullTerminatedString(-1);
	}

	/**
	 * Reads and returns a null-terminated, 8-bit ANSI string from a file. Does not append the null character
	 * itself to the returned string. This version contains a set stopping point for which to stop attempting to look for
	 * a null character. If the stop point is reached, the method will return with the string as read so far.
	 *
	 * @param stoppoint the byte position in the file before which the null character should have been found.
	 * If this value is set to -1, the method will ignore and keep reading indefinitely.
	 * @return the null terminated string
	 * @throws Exception if the file can't be read, or an error occurs.
	 */
	public String readNullTerminatedString(long stoppoint) throws IOException, EOFException {
		String x = "";
		byte[] w = new byte[1];
		byte next = this.readByte();
		while ( (next != 0) && (this.getFilePointer() < this.length()) ) {
			w[0] = next;
			x = x + new String(w);
			if (stoppoint != -1) {
				if (this.getFilePointer() >= stoppoint) {
					return x;
				}
			}
			next = this.readByte();
		}
		return x;
	}

}
