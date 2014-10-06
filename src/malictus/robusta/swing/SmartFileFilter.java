package malictus.robusta.swing;

import java.io.File;
import javax.swing.filechooser.*;
import malictus.robusta.string.*;

/**
 * A SmartFileFilter is a FileFilter in which the acceptable file types and the file description
 * are set automatically
 */
public class SmartFileFilter extends FileFilter {

	String fileTypes = "";
	String filterDescription = "";

	/**
	 * Constructor for SmartFileFilter
	 *
	* @param fileTypes A comma-separated list of the all the file extensions allowed in the
	 * 		default file filter. Case does not matter.
	 * @param filterDescription A description of the default file filter
	 */
	public SmartFileFilter(String fileTypes, String filterDescription) {
		super();
		this.fileTypes = fileTypes;
		this.filterDescription = filterDescription;
	}

	/**
	 * Overwritten to accept only the files that are specified
	 *
	 * @param f The file that we are testing to see whether or not it is allowed
	 */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = StringUtils.getExtension(f.getName());
        extension = extension.toLowerCase();
        //parse the fileTypes string
        String[] exts = fileTypes.split(",");
        int counter = 0;
        while (counter < exts.length) {
        	String cand = exts[counter];
        	if (cand.trim().toLowerCase().equals(extension)) {
        		return true;
        	}
        	counter = counter + 1;
        }
        return false;
    }

    /**
     * A text description of this file filter
     */
    public String getDescription() {
        return this.filterDescription;
    }

}

