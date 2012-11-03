package org.bdx1.diams;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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

		// Resets fileList
		fileList.clear();
		
		// Gets the children directories and the files of the top directory
		File[] childrenFiles = mTopDirectory.listFiles();

		// Declares the directories and the files array
		List<String> directoryList = new ArrayList<String>();
		// Loops on every child
		for (File child : childrenFiles) {
			// If it is a directory
			if (child.isDirectory()) {
				String directoryName = child.getName();
				if (directoryName.charAt(0) != '.') {
					//TODO: add support for DICOMDIR corresponding to 1 exam of a given patient
					if(containsValidDicomFiles(child)) {
						//Change the name of the dir to reflect patient informations.
						//A click on the dir should directly open the corresponding exam.
					}
					directoryList.add("/" + child.getName());
				}
			} else { // If it is a file.
				String[] fileName = child.getName().split("\\.");

				if (!child.isHidden()) {
					if (fileName.length > 1) {
						// DICOM files which have a 'dcm' extension
						if (fileName[fileName.length - 1]
								.equalsIgnoreCase("dcm") && isValidDicomFile(child)) {
							InformationProvider dicomProv = InformationProviderManager.getDicomProvider();
							dicomProv.read(child);
							Map<String, String> patientInfos = dicomProv.getPatientInfos();
							fileList.add(patientInfos.get("Patient Name") + " "
									+ patientInfos.get("Patient age") + " "
									+ patientInfos.get("Patient sex"));
							fileList.add(child.getName());
						} 
					}
				}
			}
		}

		// Sorts both list
		Collections.sort(directoryList, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(fileList, String.CASE_INSENSITIVE_ORDER);

		// Sets the number of dicom files
		numberOfSlice = fileList.size();

		// Output list will be files before directories
		// then we add the directoryList to the fileList
		fileList.addAll(directoryList);

		if (!mTopDirectory.equals(Environment.getExternalStorageDirectory())) {
			fileList.add(0, "..");
		}
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
	 */
	public boolean isValidDicomFile(File child) {
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
		//TODO: take into account the case when a dir only contains sub-dirs and no files.
		return true;
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
}
