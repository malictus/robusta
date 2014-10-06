package malictus.robusta.string;

import java.util.*;

/**
 * A collection of general string-based utilities.
 */
public class StringUtils {

	private StringUtils() {}

	/**
	 * Convert a long into a hex string. This method goes beyond the toHexString() method by appending the usual zeroes
	 * (up to 8) and the '0x' header to the beginning of the string.
	 *
	 * @param decimal the long
	 * @return the formatted hex string
	 */
	public static String convertToHex(long decimal) {
		String x = Long.toHexString(decimal);
		while (x.length() < 8) {
			x = "0" + x;
		}
		x = "0x" + x;
		return x;
	}

	/**
	 * Convert an int into a hex string. This method goes beyond the toHexString() method by appending the usual zeroes
	 * (up to 4) and the '0x' header to the beginning of the string.
	 *
	 * @param decimal the integer
	 * @return the formatted hex string
	 */
	public static String convertToHex(int decimal) {
		String x = Integer.toHexString(decimal);
		while (x.length() < 4) {
			x = "0" + x;
		}
		x = "0x" + x;
		return x;
	}

	/**
	 * Given a string, return the string's file extension, defined as everything in the
	 * string after the last period (.) If no periods are present in string, an empty
	 * string is returned.
	 *
	 * @param string The input string
	 * @return The string's file extension
	 */
	public static String getExtension(String string) {
		String extension = "";
		int i = string.lastIndexOf('.');
        if ((i > 0) &&  (i < string.length() - 1)) {
            extension = string.substring(i+1);
        }
        return extension;
	}

	/**
	 * Remove all null characters from the end of a string.
	 */
	static public String removeNulls(String inputString) {
		char nullChar = 0;
		String nullString = "" + nullChar;
		while ((inputString.length() > 0) && (inputString.substring(inputString.length() - 1, inputString.length()).equals(nullString))) {
			inputString = inputString.substring(0, inputString.length() - 1);
		}
		return inputString;
	}

	/**
     * Converts a millisecond offset into a
     * friendlier hh:mm:ss format & returns the string. If time is less than one
     * hour, returns mm:ss only.
     *
     * @param offset the offset to be converted
     * @return the String representation of the time value
     */
	static public String convertMillisToHoursMinutesSeconds(long offset) {
		return convertSecondsToHoursMinutesSeconds((int)(offset / 1000));
	}

	/**
     * Converts a second offset into a
     * friendlier hh:mm:ss format & returns the string. If time is less than one
     * hour, returns mm:ss only.
     *
     * @param offset the offset to be converted
     * @return the String representation of the time value
     */
    static public String convertSecondsToHoursMinutesSeconds(int offset) {
        int seconds = offset;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        String strSeconds = new String();
        String strMinutes = new String();
        seconds = seconds - (minutes * 60);
        minutes = minutes - (hours * 60);
        if (seconds < 10) {
            strSeconds = "0" + seconds;
        } else {
            strSeconds = "" + seconds;
        }
        if ((minutes < 10) && (hours > 0)) {
            strMinutes = "0" + minutes;
        } else {
            strMinutes = "" + minutes;
        }
        if (hours > 0) {
            return(hours + ":" + strMinutes + ":" + strSeconds);
        } else {
            return(strMinutes + ":" + strSeconds);
        }
    }

    /**
     * Given a string of the form hh:mm:ss or mm:ss, returns the total seconds
     *
     * @throws NumberFormatException if String can't be converted
     * @return the total number of seconds that the string represents
     */
    static public int convertHoursMinutesSecondsToSeconds(String offset) throws NumberFormatException {
        boolean isOK = true;
        StringTokenizer tokenizer = new StringTokenizer(offset, ":");
        String token = null;
        int first = -1;
        int second = -1;
        int third = -1;
        try { token = tokenizer.nextToken(); } catch (NoSuchElementException e) { isOK = false; }
        try { first = Integer.parseInt(token); } catch (NumberFormatException e) { isOK = false; }
        try { token = tokenizer.nextToken(); } catch (NoSuchElementException e) { isOK = false; }
        try { second = Integer.parseInt(token); } catch (NumberFormatException e) { isOK = false; }
        if (tokenizer.hasMoreTokens()) {
        	try { token = tokenizer.nextToken(); } catch (NoSuchElementException e) { isOK = false; }
        	try { third = Integer.parseInt(token); } catch (NumberFormatException e) { isOK = false; }
        }
        if (!isOK) {
            throw new NumberFormatException();
        }
        if (third == -1) {
        	if ((first < 0) || (first > 59)) {
        		throw new NumberFormatException();
        	}
        	if ((second < 0) || (second > 59)) {
        		throw new NumberFormatException();
        	}
            return((first * 60) + (second * 1));
        } else {
        	if ((third < 0) || (third > 59)) {
        		throw new NumberFormatException();
        	}
        	if ((second < 0) || (second > 59)) {
        		throw new NumberFormatException();
        	}
            return((first * 3600) + (second * 60) + (third * 1));
        }
    }

}
