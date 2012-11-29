package org.bdx1.diams;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdx1.commons.ExternalStorage;
import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;
import org.bdx1.dicom.file.DICOMReader;

import android.os.Environment;

public class DicomDirFileHandler implements FileHandler {
	
	private File mTopDirectory;
	private int numberOfSlice = 0;
	private List<String> fileList = new ArrayList<String>();
	private Map<Integer, File> DicomDirAndConcretFiles = new HashMap<Integer, File>();
	private Integer currentFileposition = 0;

	public DicomDirFileHandler(File topDir) {
		mTopDirectory = topDir;
	}

	/**
	 * Computes the list content for the file browser.
	 */
	public List<String> fill() {
		// If the external storage is not available, we cannot
		// compute the list
		//TODO: raise an exception instead of returning an empty list?
		if (!ExternalStorage.checkAvailable())
			return fileList;

		// Resets fileList and DicomDirAndConcretFiles
		fileList.clear();
		DicomDirAndConcretFiles.clear();
		currentFileposition = 0;
		
		// Gets the children directories and the files of the top directory
		File[] childrenFiles = mTopDirectory.listFiles();

		// Declares the directories and the files array
		List<String> directoryList = new ArrayList<String>();
		
		if (!mTopDirectory.equals(Environment.getExternalStorageDirectory())) {
			fileList.add(0, "..");
			currentFileposition += 1;
		}
		
		// Loops on every child
		for (File child : childrenFiles) {
			// If it is a directory
			if (child.isDirectory()) {
				String directoryName = child.getName();
				if (directoryName.charAt(0) != '.') {
					if(containsValidDicomFiles(child)) {
						// Creates a link between objects in the displayed list and real files.
						File patientDefaultFile = getPatientFileFromDir(child);
						DicomDirAndConcretFiles.put(currentFileposition, patientDefaultFile);
						// Changes the name of the dir to reflect patient informations.
						InformationProvider dicomProv = InformationProviderManager.getDicomProvider();
						dicomProv.read(patientDefaultFile);
						Map<String, String> patientInfos = dicomProv.getPatientInfos();
						//TODO: distinction between different exams of a same patient (date, id etc.)
						directoryList.add("/" + patientInfos.get("Patient Name") + " "
								+ patientInfos.get("Patient age") + " "
								+ patientInfos.get("Patient sex"));
					} else {
						directoryList.add("/" + child.getName());
					}
					currentFileposition += 1;
				}
			}
		}

		// Sorts both list
		//Collections.sort(directoryList, String.CASE_INSENSITIVE_ORDER);
		//Collections.sort(fileList, String.CASE_INSENSITIVE_ORDER);

		// Sets the number of dicom files... Not used for now
		numberOfSlice = fileList.size();

		// Output list will be files before directories
		// then we add the directoryList to the fileList
		fileList.addAll(directoryList);

		return fileList;
	}
	
	/**
	 * Determines if a directory is a valid Dicom directory. Use it to fill the list correctly
	 */
	private boolean containsValidDicomFiles(File dir) {
		return (containsDicomFiles(dir) && isValidDicomDir(dir));
	}

	/**
	 * Determines if a directory contains at least one Dicom file.
	 */
	private boolean containsDicomFiles(File dir) {
		for (File child : dir.listFiles()) {
			if (!child.isDirectory()) {
				String[] fileName = child.getName().split("\\.");
				if (fileName[fileName.length - 1].equalsIgnoreCase("dcm")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines if a file is a valid Dicom file. Used to fill the files list correctly.
	 * CAUTION: may become deprecated soon.
	 */
	private boolean isValidDicomFile(File child) {
		try {
			// Create a DICOMReader to parse meta informations
			DICOMReader dicomReader = new DICOMReader(child);
			dicomReader.parseMetaInformation();
			dicomReader.close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	
	/**
	 * Determines if a directory contains valid Dicom files.
	 */
	private boolean isValidDicomDir(File dir) {
		for (File child : dir.listFiles()) {
			try {
				// Ignores sub-directories
				if (!child.isDirectory()) {
					// Create a DICOMReader to parse meta informations
					DICOMReader dicomReader = new DICOMReader(child);
					dicomReader.parseMetaInformation();
					dicomReader.close();
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the first valid Dicom file that will be used as the default
	 * slice when the DicomDir is opened as an exam.
	 */
	private File getPatientFileFromDir(File dir) {
		for (File child : dir.listFiles()) {
			try {
				// Ignores sub-directories
				if (!child.isDirectory()) {
					// Create a DICOMReader to parse meta informations
					DICOMReader dicomReader = new DICOMReader(child);
					dicomReader.parseMetaInformation();
					dicomReader.close();
					// Returns the first valid Dicom file
					return child;
				}
			} catch (Exception ex) {
				// Do nothing
			}
		}
		return null;
	}

	public int getNumberOfSlice() {
		return numberOfSlice;
	}
	
	public void setmTopDirectory(File topDir) {
		this.mTopDirectory = topDir;
	}
	
	public File getmTopDirectory() {
		return this.mTopDirectory;
	}

	public File getClickedDicomDir(Integer position) {
		if(DicomDirAndConcretFiles.containsKey(position)) {
			return DicomDirAndConcretFiles.get(position);
		} else {
			return null;
		}
	}
}
