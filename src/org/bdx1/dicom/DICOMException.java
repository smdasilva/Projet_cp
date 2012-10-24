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
 * This file <DICOMException.java> is part of Droid Dicom Viewer.
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

package org.bdx1.dicom;

/**
 * DICOM Exception.
 * 
 * DICOM exception occurs when the parsing of the DICOM file
 * is not possible or if the DICOM file is not supported by
 * the software.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMException extends Exception {
	
	/**
	 * Generated by Eclipse
	 */
	private static final long serialVersionUID = -2837146355840633988L;

	public DICOMException() {
		super();
	}
	
	public DICOMException(String msg) {
		super(msg);
	}
	
	public DICOMException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
	public DICOMException(Throwable throwable) {
		super(throwable);
	}

}