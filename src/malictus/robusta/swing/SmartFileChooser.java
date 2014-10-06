package malictus.robusta.swing;

import javax.swing.*;

/**
 * A JFileChooser that will automatically create file filters to go with it.
 */
public class SmartFileChooser extends JFileChooser {

	/**
	 * Init the SmartFileChooser with a single default file filter
	 *
	 * @param fileTypes A comma-separated list of the all the file extensions allowed in the
	 * default file filter. Case does not matter.
	 * @param filterDescription A description of the default file filter
	 * @param showAcceptAll Whether to show an 'all files' filter as well
	 */
	public SmartFileChooser(String fileTypes, String filterDescription, boolean showAcceptAll) {
		super();
		SmartFileFilter sff = new SmartFileFilter(fileTypes, filterDescription);
		this.setAcceptAllFileFilterUsed(showAcceptAll);
        this.addChoosableFileFilter(sff);
        this.setMultiSelectionEnabled(false);
	}
	
	/**
	 * Init the SmartFileChooser with an array of file filters
	 *
	 * @param fileTypes An array of comma-separated lists of the all the file extensions allowed in the
	 * file filters. Case does not matter.
	 * @param filterDescription An array of the descriptions of the file filters. Size of this array must match the size of the fileType array.
	 * @param showAcceptAll Whether to show an 'all file' filter as well
	 */
	public SmartFileChooser(String[] fileTypes, String[] filterDescription, boolean showAcceptAll) throws Exception {
		super();
		if (fileTypes.length != filterDescription.length) {
			throw new Exception("File type and filter descriptions arrays must be the same length");
		}
		int counter = 0;
		while (counter < fileTypes.length) {
			SmartFileFilter sff = new SmartFileFilter(fileTypes[counter], filterDescription[counter]);
			this.addChoosableFileFilter(sff);
			counter = counter + 1;
		}
		this.setAcceptAllFileFilterUsed(showAcceptAll);
        this.setMultiSelectionEnabled(false);
	}

	/**
	 * Init a SmartFileChooser with an 'accept all' file filter only
	 */
	public SmartFileChooser() {
		this.setAcceptAllFileFilterUsed(true);
        this.setMultiSelectionEnabled(false);
	}

}
