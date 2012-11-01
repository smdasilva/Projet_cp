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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.bdx1.diams.thread.DICOMImageCacher;
import org.bdx1.diams.thread.ThreadState;
import org.bdx1.dicom.data.DICOMMetaInformation;
import org.bdx1.dicom.file.DICOMReader;
import org.bdx1.diams.model.Examen;

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
	 * Menu id for DICOM images caching.
	 */
	private static final short MENU_CACHE_IMAGE = 0;

	/**
	 * Menu for the displaying of the about dialog.
	 */
	private static final short MENU_ABOUT = 1;

	/**
	 * Define the progress dialog id for the caching of DICOM image.
	 */
	private static final short PROGRESS_DIALOG_CACHE = 1;

	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------

	/**
	 * Current directory.
	 */
	private File mTopDirectory;

	/**
	 * Array adapter to display the directory and the files.
	 */
	ArrayAdapter<String> mAdapter;

	/**
	 * DICOM file in mTopDirectory count.
	 */
	private int mTotal = 0;

	/**
	 * TextView to display the cached files count.
	 */
	private TextView mCachedFileTextView;

	/**
	 * File chooser main layout.
	 */
	private LinearLayout mMainLayout;

	/**
	 * Progress dialog for file caching.
	 */
	private ProgressDialog cachingDialog;

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

		// Defines the cached files TextView (even if it is not
		// shown) and defines the onClick listener
		mCachedFileTextView = new TextView(this);
		mCachedFileTextView.setPadding(10, 20, 10, 20);
		mCachedFileTextView.setBackgroundColor(0xffffffff);
		mCachedFileTextView.setTextColor(0xff000000);
		mCachedFileTextView.setTextSize(14.0f);
		mCachedFileTextView.setClickable(true);
		mCachedFileTextView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setMessage("Do you want to delete the cached files ?")
						.setTitle("Cached files")
						.setCancelable(true)
						.setPositiveButton("Cancel", null)
						.setNegativeButton("Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										MainActivity.this.deleteCachedFile();
									}
								});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}
		});

		// Checks if the external storage is available
		if (ExternalStorage.checkAvailable()) {
			// If this activity was paused and resumed, then restores its saved
			// state
			if (savedInstanceState != null) {
				String topDirectoryString = savedInstanceState
						.getString(TOP_DIR_ID);
				mTopDirectory = (topDirectoryString == null) ? Environment
						.getExternalStorageDirectory() : new File(
						savedInstanceState.getString("top_directory"));
			} else {
				// Set the top directory
				mTopDirectory = Environment.getExternalStorageDirectory();
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

		super.onListItemClick(l, v, position, id);

		String itemName = mAdapter.getItem(position);

		// If it is a directory, displays its content
		if (itemName.charAt(0) == '/') {
			mTopDirectory = new File(mTopDirectory.getPath() + itemName);
			fill();
			// If itemNam = ".." go to parent directory
		} else if (itemName.equals("..")) {
			mTopDirectory = mTopDirectory.getParentFile();
			fill();
			// If it is a file.
		} else {
			try {
				// Create a DICOMReader to parse meta informations
				DICOMReader dicomReader = new DICOMReader(
						mTopDirectory.getPath() + "/" + itemName);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the top directory absolute path
		outState.putString(TOP_DIR_ID, mTopDirectory.getAbsolutePath());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO : cancelable dialog ?
		switch (id) {
		// Create image cache dialog
		case PROGRESS_DIALOG_CACHE:
			cachingDialog = new ProgressDialog(this);
			cachingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			cachingDialog.setMessage("Caching image...");
			cachingDialog.setCancelable(false);
			return cachingDialog;

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
		if (mTopDirectory.getParent() == null
				|| mTopDirectory.equals(Environment
						.getExternalStorageDirectory())) {
			super.onBackPressed();
		} else {
			mTopDirectory = mTopDirectory.getParentFile();
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

		menu.add(0, MENU_CACHE_IMAGE, 0, "Cache every images");
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
		case MENU_CACHE_IMAGE:
			cacheImages();
			return true;

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
		// If the external storage is not available, we cannot
		// fill the view
		if (!ExternalStorage.checkAvailable())
			return;

		// Remove the cached file text view from the layout
		if (mMainLayout.indexOfChild(mCachedFileTextView) != -1)
			mMainLayout.removeView(mCachedFileTextView);

		// Cached files counter
		int cachedImageCount = 0;

		// Gets the children directories and the files of the top directory
		File[] childrenFiles = mTopDirectory.listFiles();

		// Declares the directories and the files array
		List<String> directoryList = new ArrayList<String>();
		List<String> fileList = new ArrayList<String>();

		// Loops on every child
		for (File child : childrenFiles) {
			// If it is a directory
			if (child.isDirectory()) {
				String directoryName = child.getName();
				if (directoryName.charAt(0) != '.')
					directoryList.add("/" + child.getName());			
			} else { // If it is a file.
				String[] fileName = child.getName().split("\\.");

				if (!child.isHidden()) {
					if (fileName.length > 1) {
						// DICOM files which have no extension or a 'dcm' extension
						if (fileName[fileName.length - 1]
								.equalsIgnoreCase("dcm")) {
							fileList.add(child.getName());
							// Else if it is a LISA image, counts the cached
							// images.
						} else if (fileName[fileName.length - 1].equals("lisa")) {
							cachedImageCount++;
						}
					} else {
						fileList.add(child.getName());
					}
				}
			}
		}

		// Sorts both list
		Collections.sort(directoryList, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(fileList, String.CASE_INSENSITIVE_ORDER);

		// Sets the number of dicom files
		mTotal = fileList.size();

		// Output list will be files before directories
		// then we add the directoryList to the fileList
		fileList.addAll(directoryList);

		if (!mTopDirectory.equals(Environment.getExternalStorageDirectory())) {
			fileList.add(0, "..");
		}

		// If there is cached files, displays it
		if (cachedImageCount > 0) {
			mCachedFileTextView.setText("Cached files: " + cachedImageCount);
			mMainLayout.addView(mCachedFileTextView, 0);
		}

		mAdapter = new ArrayAdapter<String>(this, R.layout.file_chooser_item,
				R.id.fileName, fileList);
		setListAdapter(mAdapter);
	}

	/**
	 * Delete cached files present in the mTopDirectory.
	 */
	private void deleteCachedFile() {

		if (!ExternalStorage.checkWritable()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Cannot delete cached files because "
							+ "the external storage is not writable.")
					.setTitle("[ERROR] Delete cached files.")
					.setCancelable(false).setPositiveButton("Close", null);

			AlertDialog alertDialog = builder.create();
			alertDialog.show();

			return;
		}

		// Get the children directories and the files of the top directory
		File[] childrenFiles = mTopDirectory.listFiles();

		// Loop on the file of the directory
		for (File child : childrenFiles) {
			// If it is not a directory or a hidden file
			if (!child.isDirectory() && child.getName().charAt(0) != '.') {
				// Checks that the extension is "lisa"
				String[] fileName = child.getName().split("\\.");
				// If it is the case, there is a dot in the file name
				if (fileName.length > 1) {
					if (fileName[fileName.length - 1].equals("lisa")) {
						child.delete();
					}
				}
			}
		}
		// Updates the view
		fill();
	}

	/**
	 * Cache of the images in the files array
	 */
	private void cacheImages() {

		try {
			// The handler is inside the function because
			// normally this function is called once.
			final Handler cacheHandler = new Handler() {
				public void handleMessage(Message message) {

					switch (message.what) {

						case ThreadState.STARTED:
							cachingDialog.setMax(message.arg1);
							break;
	
						case ThreadState.PROGRESSION_UPDATE:
							cachingDialog.setProgress(message.arg1);
							break;
	
						case ThreadState.FINISHED:
							try {	
								dismissDialog(PROGRESS_DIALOG_CACHE);	
							} catch (IllegalArgumentException ex) {
								// Do nothing
							}
							fill();
							break;
	
						case ThreadState.CATCHABLE_ERROR_OCCURRED:
							cachingDialog.setProgress(message.arg1);
							Toast.makeText(
									MainActivity.this,
									"[Error]: file (" + (String) message.obj
											+ ") cannot be cached.",
									Toast.LENGTH_SHORT).show();
							break;
	
						case ThreadState.UNCATCHABLE_ERROR_OCCURRED:
							try {	
								dismissDialog(PROGRESS_DIALOG_CACHE);	
							} catch (IllegalArgumentException ex) {
								// Do nothing
							}
							AlertDialog.Builder builder = new AlertDialog.Builder(
									MainActivity.this);
							builder.setMessage(
									"Unknown error: An unknown error occurred during"
											+ " the images caching process.")
									.setTitle("[ERROR] Caching file")
									.setCancelable(false)
									.setPositiveButton("Close", null);
							AlertDialog alertDialog = builder.create();
							alertDialog.show();
							break;
	
						case ThreadState.OUT_OF_MEMORY:
							try {	
								dismissDialog(PROGRESS_DIALOG_CACHE);	
							} catch (IllegalArgumentException ex) {
								// Do nothing
							}
							builder = new AlertDialog.Builder(MainActivity.this);
							builder.setMessage(
									"OutOfMemoryError: During the caching process,"
											+ " an out of memory error occurred.\n\n"
											+ "Your file(s) is (are) too large for your system. You can"
											+ " try again in the file chooser. If the error occures again,"
											+ " then the image(s) cannot be displayed on your device.\n"
											+ "Try to use a desktop file viewer software")
									.setTitle("[ERROR] Caching file")
									.setCancelable(false)
									.setPositiveButton("Close", null);
							alertDialog = builder.create();
							alertDialog.show();
							break;
					};
				}
			};

			// Shows the progress dialog for caching images
			showDialog(PROGRESS_DIALOG_CACHE);

			// Starts the caching org.bdx1.diams.thread
			DICOMImageCacher dicomImageCacher = new DICOMImageCacher(
					cacheHandler, mTopDirectory);
			dicomImageCacher.start();
		} catch (FileNotFoundException e) {
			Toast.makeText(
					MainActivity.this,
					"[Error]: a file may have been suppressed during the caching process." +
					"Please try again later",
					Toast.LENGTH_SHORT).show();
		}
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
