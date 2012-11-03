package org.bdx1.diams;

import java.io.File;
import java.util.List;

public interface FileHandler {

	/**
	 * Computes the list content for the file browser.
	 */
	public abstract List<String> fill();

	/**
	 * Determines if a file is a valid Dicom file. Used to fill the files list correctly.
	 * CAUTION : may become deprecated soon.
	 */
	public abstract boolean isValidDicomFile(File child);

	public abstract int getNumberOfSlice();

	public abstract void setmTopDirectory(File file);
	
	public abstract File getmTopDirectory();

}