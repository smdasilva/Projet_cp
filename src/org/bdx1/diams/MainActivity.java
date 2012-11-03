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
 * This file <MainActivity.java> is part of Droid Dicom Viewer.
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

package org.bdx1.diams;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.bdx1.commons.ExternalStorage;
import org.bdx1.dicom.data.DICOMMetaInformation;
import org.bdx1.dicom.file.DICOMReader;
import org.bdx1.diams.model.Examen;
import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;

/**
 * File chooser.
 * 
 * @author Clement Badiola
 * @version 1.0
 * 
 */
public class MainActivity extends ListActivity {

	// ---------------------------------------------------------------
	// - <static> VARIABLES
	// ---------------------------------------------------------------

	/**
	 * Id for the onSaveInstanceState.
	 */
	private static final String TOP_DIR_ID = "top_directory";

	/**
	 * Menu for the displaying of the about dialog.
	 */
	private static final short MENU_ABOUT = 1;

	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------

	/**
	 * File handler.
	 */
	private FileHandler fileHandler;

	/**
	 * Array adapter to display the directory and the files.
	 */
	ArrayAdapter<String> mAdapter;

	/**
	 * File chooser main layout.
	 */
	private LinearLayout mMainLayout;

	// ---------------------------------------------------------------
	// # <override> FUNCTIONS
	// ---------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Sets the content view to a file browser
		setContentView(R.layout.file_chooser_list);

		// Defines the main layout
		mMainLayout = (LinearLayout) findViewById(R.id.file_chooser_mainLayout);

		// Checks if the external storage is available
		if (ExternalStorage.checkAvailable()) {
			// If this activity was paused and resumed, then restores its saved
			// state
			if (savedInstanceState != null) {
				String topDirectoryString = savedInstanceState
						.getString(TOP_DIR_ID);
				fileHandler = ((topDirectoryString == null) ? new DicomDirFileHandler(Environment
						.getExternalStorageDirectory()) : new DicomDirFileHandler(new File(
						savedInstanceState.getString("top_directory"))));
			} else {
				// Set the file handler
				fileHandler = new DicomDirFileHandler(Environment.getExternalStorageDirectory());
				// Displays the disclaimer
				displayDisclaimer();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// If there is no external storage available, quit the application
		if (!ExternalStorage.checkAvailable()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"There is no external storage available.\n"
							+ "1) There is no external storage : add one.\n"
							+ "2) Your external storage is used by a computer:"
							+ " disconnect it from the computer.")
					.setTitle("[ERROR] No External Storage")
					.setCancelable(false)
					.setPositiveButton("Exit",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									MainActivity.this.finish();
								}
							});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			// Else displays data
		} else {
			fill();
		}
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//TODO: add a toast ("please wait") to tell the user that the program is working.
		super.onListItemClick(l, v, position, id);

		String itemName = mAdapter.getItem(position);
		File clickedFile = fileHandler.getClickedDicomDir(position);
		if (clickedFile != null) {
			//TODO: pass the exam to the drawing view.
			System.out.println("MAKING A NEW EXAM");
			Factory.MODEL_FACTORY.makeExamen(clickedFile);
		} else { //TODO: clean up this part.
			// If it is a directory, displays its content
			if (itemName.charAt(0) == '/') {
				String clickedPath = fileHandler.getmTopDirectory().getPath() + itemName;
				fileHandler.setmTopDirectory(new File(clickedPath));
				fill();
				// If itemNam = ".." go to parent directory
			} else if (itemName.equals("..")) {
				File parentFile = fileHandler.getmTopDirectory().getParentFile();
				fileHandler.setmTopDirectory(parentFile);
				fill();
				// If it is a file.
			} else {
				try {
					// Create a DICOMReader to parse meta informations
					String filePath = fileHandler.getmTopDirectory().getPath() + "/" + itemName;
					DICOMReader dicomReader = new DICOMReader(filePath);
					DICOMMetaInformation metaInformation = dicomReader
							.parseMetaInformation();
					dicomReader.close();
	
					if (metaInformation.getSOPClassUID().equals(
							"1.2.840.10008.1.3.10")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setMessage(
								"Media Storage Directory (DICOMDIR) are not supported yet.")
								.setTitle("[ERROR] Opening file " + itemName)
								.setCancelable(false)
								.setPositiveButton("Close",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog, int id) {
												// Do nothing
											}
										});
						AlertDialog alertDialog = builder.create();
						alertDialog.show();
					} else {
						/*
						 * // Open the DICOM Viewer Intent intent = new Intent(this,
						 * DICOMViewer.class); intent.putExtra("DICOMFileName",
						 * mTopDirectory.getPath() + "/" + itemName);
						 * intent.putExtra("FileCount", mTotal);
						 * startActivity(intent);
						 */
					}
	
				} catch (Exception ex) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(
							"Error while opening the file " + itemName + ". \n"
									+ ex.getMessage())
							.setTitle("[ERROR] Opening file " + itemName)
							.setCancelable(false)
							.setPositiveButton("Close",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											// Do nothing
										}
									});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the top directory absolute path
		String topDirPath = fileHandler.getmTopDirectory().getAbsolutePath();
		outState.putString(TOP_DIR_ID, topDirPath);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		default:
			return null;
		}
	}

	// ---------------------------------------------------------------
	// + <override> FUNCTIONS
	// ---------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// If the directory is the external storage directory or there is no
		// parent,
		// super.onBackPressed(). Else goes to the parent directory.
		if (fileHandler.getmTopDirectory().getParent() == null
				|| fileHandler.getmTopDirectory().equals(Environment
						.getExternalStorageDirectory())) {
			super.onBackPressed();
		} else {
			File parentFile = fileHandler.getmTopDirectory().getParentFile();
			fileHandler.setmTopDirectory(parentFile);
			fill();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		//menu.add(0, MENU_CACHE_IMAGE, 0, "Cache every images");
		menu.add(0, MENU_ABOUT, 1, "About");

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {

		case MENU_ABOUT:/*
						 * Dialog dialog = new Dialog(this);
						 * dialog.setContentView(R.layout.dialog_about);
						 * dialog.setTitle("Droid Dicom Viewer: About");
						 * dialog.show();
						 */
			return true;

		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	// ---------------------------------------------------------------
	// - FUNCTIONS
	// ---------------------------------------------------------------

	/**
	 * Updates the view's content.
	 */
	private void fill() {
		//The file handler computes and returns the list
		List<String> fileList = fileHandler.fill();
		mAdapter = new ArrayAdapter<String>(this, R.layout.file_chooser_item,
				R.id.fileName, fileList );
		setListAdapter(mAdapter);
	}

	/**
	 * Display the disclaimer.
	 */
	private void displayDisclaimer() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"DIAMS, based on Droid Dicom Viewer is a Free and Open Source"
						+ " Software (FOSS) licensed under the terms of the GNU General Public"
						+ " License as published by the Free Software Foundation, either version 3 "
						+ "of the License, or (at your option) any later version.\n\n"
						+ "THIS VERSION OF DIAMS IS NOT CERTIFIED AS A MEDICAL DEVICE "
						+ "(CE-1 or FDA) FOR PRIMARY DIAGNOSIS OR CLINICAL PRACTICE. "
						+ "THIS SOFTWARE CAN ONLY BE USED AS A REVIEWING OR SCIENTIFIC SOFTWARE AND "
						+ "CANNOT BE USED AS A MEDICAL DEVICE FOR PRIMARY DIAGNOSTIC OR ANY OTHER "
						+ "CLINICAL PRACTICE.\n\n"
						+ "DIAMS is distributed in an academic context in the hope that it will be useful, "
						+ "but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY "
						+ "or FITNESS FOR A PARTICULAR PURPOSE. See the Droid Dicom License for more "
						+ "details: http://code.google.com/p/droid-dicom-viewer/wiki/License.\n\n"
						+ "DICOM COMPATIBILITY\n"
						+ "Droid Dicom Viewer implements a part of the DICOM standard. "
						+ "It reads DICOM images that are coded on 8 bits and 16 bits. "
						+ "It supports only grayscale uncompressed DICOM files. "
						+ "It parses implicit and explicit (little endian and big endian) "
						+ "value representations (VR).\n\n"
						+ "2012 - Clement Badiola, Samuel Da Silva, Alexandre Perrot, Reda Lyazidi")
				.setTitle("Disclaimer")
				.setCancelable(false)
				.setPositiveButton("Decline",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								MainActivity.this.finish();
							}
						}).setNegativeButton("Accept", null);

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}
