package org.bdx1.diams;

import java.io.File;
import java.util.List;

/**
 * Interface used in the file browser to handle the files and directories.
 * @author Clement Badiola
 */
public interface FileHandler {

	/**
	 * Computes the list content for the file browser.
	 * @return a list containing the names of the files and directories to display.
	 */
	public abstract List<String> fill();

	/**
	 * Gets the top directory.
	 * @return a file representing the top directory.
	 */
	public abstract File getmTopDirectory();

	/**
	 * Gets the number of slices found in the current working directory.
	 * @return an integer representing the number of slices.
	 */
	public abstract int getNumberOfSlice();

	/**
	 * Sets the working top directory to a new value.
	 * @param file the new top directory.
	 */
	public abstract void setmTopDirectory(File file);

}