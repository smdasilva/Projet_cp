/*
 *
 * Copyright (C) 2011 Pierre Malarme
 *
 * Authors: Pierre Malarme <pmalarme at ulb.ac.be>
 *
 * Institution: Laboratory of Image Synthesis and Analysis (LISA)
 *              Faculty of Applied Science
 *              Universite Libre de Bruxelles (U.L.B.)
 *
 * Website: http://lisa.ulb.ac.be
 *
 * This file <DICOMFile.java> is part of Droid Dicom Viewer.
 *
 * Droid Dicom Viewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Droid Dicom Viewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Droid Dicom Viewer. If not, see <http://www.gnu.org/licenses/>.
 *
 * Released date: 17-02-2011
 *
 * Version: 1.0
 *
 */

package org.bdx1.dicom.data;

/**
 * DICOM file containing a meta information object (DICOMMetaInformation)
 * and a DICOM body object (DICOMBody).
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMFile {
	
	// ---------------------------------------------------------------
	// - <final> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * DICOM meta information.
	 */
	protected final DICOMMetaInformation mMetaInformation;
	
	/**
	 * DICOM body.
	 */
	protected final DICOMBody mBody;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMFile(DICOMMetaInformation metaInformation, DICOMBody body) {
		
		mMetaInformation = metaInformation;
		mBody = body;
		
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * @return DICOM meta information.
	 */
	public DICOMMetaInformation getMetaInformation() {
		return mMetaInformation;
	}
	
	/**
	 * @return DICOM body.
	 */
	public DICOMBody getBody() {
		return mBody;
	}
	
	/**
	 * @return True if the file has DICOM meta information.
	 * False otherwise.
	 */
	public boolean hasMetaInformation() {
		return mMetaInformation != null;
	}
	
	/**
	 * @return True if the file has DICOM body.
	 * False otherwise.
	 */
	public boolean hasBody() {
		return mBody != null;
	}

}
