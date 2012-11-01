/* Copyright (c) 2001-2012, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.dicom;

import java.util.*;
import java.text.NumberFormat;
import java.io.*;
//import javax.imageio.*;
//import javax.imageio.stream.ImageInputStream;
//import javax.imageio.spi.*;
//import javax.imageio.event.IIOReadProgressListener;
//import java.awt.image.*; 

import java.util.zip.*;

/**
 * <p>The {@link com.pixelmed.dicom.AttributeList AttributeList} class maintains a list of individual DICOM attributes.</p>
 *
 * <p>Instances of the class may be used for entire composite storage SOP instances, or fragments of such instances
 * such as meta information headers, or simply as lists of attributes to be passed to other
 * methods (e.g. lists of attributes to add or remove from another list).</p>
 *
 * <p>The class is actually implemented by extending {@link java.util.TreeMap java.util.TreeMap}
 * as a map of {@link com.pixelmed.dicom.AttributeTag AttributeTag} keys to
 * {@link com.pixelmed.dicom.Attribute Attribute} values. Consequently, all the methods
 * of the underlying collection are available, including adding key-value pairs and
 * extracting values by key. Iteration through the list of key-value pairs in
 * the map is also supported, and the iterator returns values in the ascending numerical
 * order of the {@link com.pixelmed.dicom.AttributeTag AttributeTag} keys, since
 * that is how {@link com.pixelmed.dicom.AttributeTag AttributeTag} implements
 * {@link java.lang.Comparable Comparable}.</p>
 *
 * <p>Note that large attribute values such as Pixel Data may be left on disk rather
 * than actually read in when the list is created, and loaded on demand; extreme
 * caution should be taken if the underlying file from which an AttributeList has
 * been read is moved or renamed; a specific method, {@link #setFileUsedByOnDiskAttributes(File file) setFileUsedByOnDiskAttributes()},
 * is provided to address this concern.</p>
 *
 * <p>The class provides methods for reading entire objects as a list of attributes,
 * from files or streams. For example, the following fragment will read an entire
 * object from the specified file and dump the contents of the attribute list:</p>
 *
 * <pre>
 * 	AttributeList list = new AttributeList();
 * 	list.read(arg[0]);
 * 	System.err.print(list);
 * </pre>
 *
 * <p>Similarly, methods are provided for writing entire objects. For example, the
 * previous fragment could be extended to write the list to a file unchanged as follows:</p>
 *
 * <pre>
 *	list.write(arg[1],TransferSyntax.ExplicitVRLittleEndian,true,true);
 * </pre>
 *
 *<p>Note that in general, one would want to perform significantly more cleaning
 * up before writing an object that has just been read, and a number of such
 * methods are provided either in this class or on related classes
 * as illustrated in this example:</p>
 *
 * <pre>
 * 	AttributeList list = new AttributeList();
 * 	list.read(arg[0]);
 *	//list.removePrivateAttributes();
 *	list.removeGroupLengthAttributes();
 *	list.removeMetaInformationHeaderAttributes();
 *	list.remove(TagFromName.DataSetTrailingPadding);
 *	list.correctDecompressedImagePixelModule();
 *	list.insertLossyImageCompressionHistoryIfDecompressed();
 *	FileMetaInformation.addFileMetaInformation(list,TransferSyntax.ExplicitVRLittleEndian,"OURAETITLE");
 *	list.write(arg[1],TransferSyntax.ExplicitVRLittleEndian,true,true);
 * </pre>
 *
 * <p>Note that this example is essentially the functionality of the {@link #main(String[]) main()} method
 * of this class, which may be used as a copying utility when invoked with input and output file arguments.</p>
 *
 * <p>Correction of the PhotometricInterpretation and related attributes by an explicit call to the
 * {@link #correctDecompressedImagePixelModule() correctDecompressedImagePixelModule()}
 * method is necessary if the color space of a compressed input
 * transfer syntax was changed during decompression (e.g., from YBR_FULL_422 for JPEG lossy to RGB),
 * since the PixelData is always decompressed during reading by the {@link #read(String) read()} method and its ilk;
 * the call does nothing (is harmless) if the input was not compressed or not multi-component.</p>
 *
 * <p>Individual attributes can be added or deleted as desired, either using a newly created
 * list or one which has been read in from an existing object. For example, to zero out the
 * patient's name one might do something like the following:</p>
 *
 * <pre>
 *	list.replaceWithZeroLengthIfPresent(TagFromName.PatientName);
 * </pre>
 *
 * <p> or to replace it with a particular value one might do the following:</p>
 * <pre>
 *	Attribute a = new PersonNameAttribute(TagFromName.PatientName);
 *	a.addValue(value);
 *	list.put(TagFromName.PatientName,a);		// one could list.remove(TagFromName.PatientName) first, but this is implicit in the put
 * </pre>
 *
 * <p>A more compact shorthand method for adding new (or replacing existing) attributes (if they are in the dictionary so that the VR can be determined) is also supplied:</p>
 *
 * <pre>
 *	list.putNewAttribute(TagFromName.PatientName);
 * </pre>
 *
 * <p>and if a specific character set other than the default is in use:</p>
 *
 * <pre>
 *	list.putNewAttribute(TagFromName.PatientName,specificCharacterSet);
 * </pre>
 *
 * <p>and since this method returns the generated attribute, values can easily be added as:</p>
 *
 * <pre>
 *	list.putNewAttribute(TagFromName.PatientName,specificCharacterSet).addValue("Blo^Joe");
 * </pre>
 *
 *
 * <p>Note also that the {@link com.pixelmed.dicom.Attribute Attribute} class provides some useful
 * static methods for extracting and manipulating individual attributes within a list. For example:</p>
 *
 * <pre>
 *	String patientName=Attribute.getSingleStringValueOrNull(list,TagFromName.PatientName);
 * </pre>
 *
 * <p>Ideally one should take care when adding or manipulating lists of attributes to handle
 * the specific character set correctly and consistently when there is a possibility that it
 * may be other than the default. The previous example of replacing the patient's name
 * could be more properly rewritten as:</p>
 *
 * <pre>
 *	SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(Attribute.getStringValues(list,TagFromName.SpecificCharacterSet));
 *	Attribute a = new PersonNameAttribute(TagFromName.PatientName,specificCharacterSet);
 *	a.addValue(value);
 *	list.put(TagFromName.PatientName,a);
 * </pre>
 *
 * <p>Note that in this example if the SpecificCharacterSet attribute were not found or was present but empty
 * the various intervening methods would return null and the
 * {@link com.pixelmed.dicom.SpecificCharacterSet#SpecificCharacterSet(String[]) SpecificCharacterSet()}
 * constructor would use the default (ascii) character set.</p>
 *
 * <p>When an attribute list is read in, the SpecificCharacterSet attribute is automatically detected
 * and set and applied to all string attributes as they are read in and converted to the internal
 * string form which is used by Java (Unicode).</p>
 *
 * <p>The same applies when they are written, with some limitations on which character sets are supported. However,
 * if new attributes have been added to the list with a different SpecificCharacterSet, it is necessary to call
 * {@link #insertSuitableSpecificCharacterSetForAllStringValues() insertSuitableSpecificCharacterSetForAllStringValues()} before writing, which will check all string values
 * of all attributes affected by SpecificCharacterSet, choose a suitable new SpecificCharacterSet, and
 * insert or replace the existing SpecificCharacterSet attribute. By the time that the list is written out,
 * all of the Attributes must have the same SpecificCharacterSet.</p>
 *
 * @see com.pixelmed.dicom.Attribute
 * @see com.pixelmed.dicom.AttributeTag
 * @see com.pixelmed.dicom.FileMetaInformation
 * @see com.pixelmed.dicom.SpecificCharacterSet
 * @see com.pixelmed.dicom.TagFromName
 * @see com.pixelmed.dicom.TransferSyntax
 *
 * @author	dclunie
 */
public class AttributeList extends TreeMap<AttributeTag,Attribute> {

	/***/
	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/dicom/AttributeList.java,v 1.117 2012/09/08 14:41:59 dclunie Exp $";

	private static final long badAttributeLimit = 10;	// pretty arbitrary, but avoids very long wait on large non-DICOM files, and still permits the occasional bad ones
	
	private static final long maximumLongVRValueLengthThatCanBeAllocated = Integer.MAX_VALUE;	// Java arrays are index by int and hence are limited in size :(
	
	private static final long maximumShortVRValueLength = 65535l;
	
	private static final long maximumSaneFixedValueLengthWhenRecoveringFromIncorrectImplicitVRElementEncodinginExplicitVR = 100000l; // 100 kB seems large enough, but is an arbitrary choice
	
	/**
	 * <p>Check that the value length is reasonable under the circumstances.</p>
	 *
	 * <p>Used to avoid trying to allocate insanely large attribute values when parsing corrupt or non-DICOM datasets.</p>
	 *
	 * <p>Protected so that this can be overridden in sub-classes if necessary for a particular application.</p>
	 *
	 * @param	vl
	 * @param	vr
	 * @param	tag
	 * @param	encounteredIncorrectImplicitVRElementEncodinginExplicitVR	which increases the likelihood of an insane value
	 * @exception	DicomException	if the value lenght is not reasonable
	 */
	protected void checkSanityOfValueLength(long vl,byte vr[],AttributeTag tag,boolean encounteredIncorrectImplicitVRElementEncodinginExplicitVR) throws DicomException {
//System.err.println("AttributeList.checkSanityOfValueLength(): vr = "+ValueRepresentation.getAsString(vr)+" vl = "+vl);
		if (vl < 0) {
			throw new DicomException("Illegal fixed VL ("+vl+" dec, 0x"+Long.toHexString(vl)+") - is negative - probably incorrect dataset - giving up");
		}
		if (vl > maximumLongVRValueLengthThatCanBeAllocated && ValueRepresentation.isUnknownVR(vr)) {
			throw new DicomException("Illegal fixed VL ("+vl+" dec, 0x"+Long.toHexString(vl)+") - is larger than can be allocated for unknown VR - probably incorrect dataset - giving up");
		}
		// logic here is that a short VL VR should never have a VL greater than can be sent in explicit VR (2^16-1 == 65535),
		// with the except of RT DVH (DS) that sometimes must be sent as implicit VR (Mathews, Bosch 2006 Phys. Med. Biol. 51 L11 doi:10.1088/0031-9155/51/5/L01)
		if (vl > maximumShortVRValueLength && ValueRepresentation.isShortValueLengthVR(vr) && !tag.equals(TagFromName.DVHData)) {
			throw new DicomException("Unlikely fixed VL ("+vl+" dec, 0x"+Long.toHexString(vl)+") for non-bulk data tag - probably incorrect dataset - giving up");
		}
		if (encounteredIncorrectImplicitVRElementEncodinginExplicitVR && vl > maximumSaneFixedValueLengthWhenRecoveringFromIncorrectImplicitVRElementEncodinginExplicitVR) {
			throw new DicomException("Unlikely fixed VL ("+vl+" dec, 0x"+Long.toHexString(vl)+") when recovering from incorrect Implicit VR element encoding in Explicit VR Transfer Syntax - giving up");
		}
		// otherwise can be as large as it needs to be
	}
	
	private static boolean haveScannedForCodecs;

	/***/
	protected static DicomDictionary dictionary;
	
	/***/
	protected static void createDictionaryifNecessary() {
		if (dictionary == null) {
//System.err.println("AttributeList.createDictionaryifNecessary(): creating static dictionary");
			dictionary = new DicomDictionary();
		}
	}
	
	private void dumpListOfAllAvailableReaders(PrintStream out) {
//		String[] formats=ImageIO.getReaderFormatNames();
//		for (int i=0; formats != null && i<formats.length; ++i) {
//			out.println(formats[i]+":");
//			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formats[i]);
//			while (readers.hasNext()) {
//				ImageReader reader = readers.next();
//				ImageReaderSpi spi = reader.getOriginatingProvider();
//				out.println("\t"+spi.getDescription(Locale.US)+" "+spi.getVendorName()+" "+spi.getVersion());
//			}
//		}
	}

	/**
	 *
	 */
	public boolean equals(Object o) {
//System.err.println("AttributeList.equals():");
		if (o instanceof AttributeList) {
			AttributeList olist = (AttributeList)o;
			if (size() == olist.size()) {
				Iterator<Attribute> i = values().iterator();
				while (i.hasNext()) {
					Attribute a = i.next();
					Attribute oa = olist.get(a.getTag());
					// ideally would have Attribute.equals() available to us, but don't at this time :(
					String as = a.getDelimitedStringValuesOrDefault("").trim();		// otherwise trailing spaces may cause mismatch, e.g., padded "DCM " versus unpadded "DCM"
					String oas = oa.getDelimitedStringValuesOrDefault("").trim();
//System.err.println("AttributeList.equals(): comparing trimmed string "+as);
//System.err.println("AttributeList.equals():      with trimmed string "+oas);
					if (!as.equals(oas)) {
						return false;
					}
				}
				return true;
			}
			else {
//System.err.println("AttributeList.equals(): different sizes");
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 *
	 */
	public int hashCode() {
		int hash = 0;
		for (Attribute a : values()) {
			// ideally would have Attribute.hashCode() available to us, but don't at this time :(
			hash += a.toString().hashCode();
		}
//System.err.println("AttributeList.hashCode(): hashCode = "+hash);
		return hash;
	}
	
//	private class OurIIOReadProgressListener implements IIOReadProgressListener {
//		public void imageComplete(ImageReader source) {
////System.out.println("OurIIOReadProgressListener:imageComplete()");
//		}
//		public void imageProgress(ImageReader source,float percentageDone) {
////System.out.println("OurIIOReadProgressListener:imageProgress(): percentageDone="+percentageDone);
//		}
//		public void imageStarted(ImageReader source,int imageIndex) {
////System.out.println("OurIIOReadProgressListener:imageStarted(): imageIndex="+imageIndex);
//		}
//		public void readAborted(ImageReader source) {
////System.out.println("OurIIOReadProgressListener:readAborted()");
//		}
//		public void sequenceComplete(ImageReader source) {
////System.out.println("OurIIOReadProgressListener:sequenceComplete()");
//		}
//		public void sequenceStarted(ImageReader source,int minIndex) {
////System.out.println("OurIIOReadProgressListener:sequenceStarted(): minIndex="+minIndex);
//		}
//		public void thumbnailComplete(ImageReader source) {
////System.out.println("OurIIOReadProgressListener:thumbnailComplete()");
//		}
//		public void thumbnailProgress(ImageReader source,float percentageDone) {
////System.out.println("OurIIOReadProgressListener:thumbnailProgress(): percentageDone="+percentageDone);
//		}
//		public void thumbnailStarted(ImageReader source,int imageIndex,int thumbnailIndex) {
////System.out.println("OurIIOReadProgressListener:thumbnailStarted(): imageIndex="+imageIndex+" thumbnailIndex="+thumbnailIndex);
//		}
//	}

	/**
	 * @param	i
	 * @exception	IOException
	 */
	private AttributeTag readAttributeTag(DicomInputStream i) throws IOException {
		int group   = i.readUnsigned16();
		int element = i.readUnsigned16();
		return new AttributeTag(group,element);
	}

	/**
	 * @param	a
	 * @param	i
	 * @param	byteOffset
	 * @param	lengthToRead
	 * @param	specificCharacterSet
	 * @return								the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	private long readNewSequenceAttribute(Attribute a,DicomInputStream i,long byteOffset,long lengthToRead,SpecificCharacterSet specificCharacterSet) throws IOException, DicomException {
//System.err.println("AttributeList.readNewSequenceAttribute(): start");
		boolean undefinedLength = lengthToRead == 0xffffffffl;
		long endByteOffset=(undefinedLength) ? 0xffffffffl : byteOffset+lengthToRead-1;

//System.err.println("readNewSequenceAttribute: start byteOffset="+byteOffset+" lengthToRead="+lengthToRead+" endByteOffset="+endByteOffset);
		try {
			// CBZip2InputStream.available() always returns zero, and since we terminate
			// on exceptions anyway, just forget about it
			while (/*i.available() > 0 && */(undefinedLength || byteOffset < endByteOffset)) {
//System.err.println("readNewSequenceAttribute: loop byteOffset="+byteOffset);
				long itemStartOffset=byteOffset;
				AttributeTag tag = readAttributeTag(i);
				byteOffset+=4;
//System.err.println("readNewSequenceAttribute: tag="+tag);
				long vl = i.readUnsigned32();		// always implicit VR form for items and delimiters
				byteOffset+=4;
//System.err.println(byteOffset+" "+tag+" VL=<0x"+Long.toHexString(vl)+">");
				if (tag.equals(TagFromName.SequenceDelimitationItem)) {
//System.err.println("readNewSequenceAttribute: SequenceDelimitationItem");
					break;
				}
				else if (tag.equals(TagFromName.Item)) {
//System.err.println("readNewSequenceAttribute: Item byteOffset="+byteOffset);
					AttributeList list = new AttributeList();
					byteOffset=list.read(i,byteOffset,vl,false,specificCharacterSet);
//System.err.println("readNewSequenceAttribute: back from reading Item byteOffset="+byteOffset);
					((SequenceAttribute)a).addItem(list,itemStartOffset);
				}
				else {
					throw new DicomException("Bad tag "+tag+"(not Item or Sequence Delimiter) in Sequence at byte offset "+byteOffset);
				}
			}
		}
		catch (EOFException e) {
//System.err.println("Closing on "+e);
			if (!undefinedLength) throw new EOFException();
		}
		catch (IOException e) {
//System.err.println("Closing on "+e);
			if (!undefinedLength) throw new IOException();		// InflaterInputStream seems to throw IOException rather than EOFException
		}
//System.err.println("readNewSequenceAttribute: return byteOffset="+byteOffset);
//System.err.println("AttributeList.readNewSequenceAttribute(): end");
		return byteOffset;
	}


	/**
	 * @param	i
	 * @param	byteOffset
	 * @param	lengthToRead
	 * @param	stopAfterMetaInformationHeader
	 * @param	specificCharacterSet
	 * @return								the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	private long read(DicomInputStream i,long byteOffset,long lengthToRead,boolean stopAfterMetaInformationHeader,SpecificCharacterSet specificCharacterSet) throws IOException, DicomException {
		return read(i,byteOffset,lengthToRead,stopAfterMetaInformationHeader,specificCharacterSet,null);
	}

	/**
	 * @param	i
	 * @param	byteOffset
	 * @param	lengthToRead
	 * @param	stopAfterMetaInformationHeader
	 * @param	specificCharacterSet
	 * @param	stopAtTag					the tag (in the top level data set) at which to stop
	 * @return								the byte offset at which the read stopped (which will be just past the stopAtTag, if stopped)
	 * @exception	IOException
	 * @exception	DicomException
	 */
	private long read(DicomInputStream i,long byteOffset,long lengthToRead,boolean stopAfterMetaInformationHeader,
			SpecificCharacterSet specificCharacterSet,AttributeTag stopAtTag) throws IOException, DicomException {
//System.err.println("AttributeList.read(): start");
//System.err.println("read: Stop tag is "+stopAtTag);
		if (i.areReadingDataSet()) {
			// Test to see whether or not a codec needs to be pushed on the stream ... after the first time, the TransferSyntax will always be ExplicitVRLittleEndian 
//System.err.println("Testing for deflate and bzip2 TS");
			if (i.getTransferSyntaxToReadDataSet().isDeflated()) {
				// insert deflate into input stream and make a new DicomInputStream
//System.err.println("Creating new DicomInputStream from deflate");
				i = new DicomInputStream(new InflaterInputStream(i,new Inflater(true)),TransferSyntax.ExplicitVRLittleEndian,false);
				byteOffset=0;
			}
			else if (i.getTransferSyntaxToReadDataSet().isBzip2ed()) {
				// insert bzip2 into input stream and make a new DicomInputStream
//System.err.println("Creating new DicomInputStream from bzip2");
				try {
					Class classToUse = Thread.currentThread().getContextClassLoader().loadClass("org.apache.excalibur.bzip2.CBZip2InputStream");
					Class [] argTypes  = {InputStream.class};
					Object[] argValues = {i};
					InputStream bzipInputStream = (InputStream)(classToUse.getConstructor(argTypes).newInstance(argValues));
					i = new DicomInputStream(bzipInputStream,TransferSyntax.ExplicitVRLittleEndian,false);
					byteOffset=0;
				}
				catch (java.lang.reflect.InvocationTargetException e) {
					throw new DicomException("Not a correctly encoded bzip2 bitstream - "+e);
				}
				catch (Exception e) {	// may be ClassNotFoundException,NoSuchMethodException,InstantiationException
					throw new DicomException("Could not instantiate bzip2 codec - "+e);
				}
			}
		}
		
		createDictionaryifNecessary();
		
		boolean undefinedLength = lengthToRead == 0xffffffffl;
		long endByteOffset=(undefinedLength) ? 0xffffffffl : byteOffset+lengthToRead-1;

//System.err.println("read: start byteOffset="+byteOffset+" endByteOffset="+endByteOffset+" lengthToRead="+lengthToRead);
		byte vrBuffer[] = new byte[2];
		boolean explicit = i.getTransferSyntaxInUse().isExplicitVR();
		
		// keep track of pixel data size in case need VL for encapsulated data ...
		int rows = 0;
		int columns = 0;
		int frames = 1;
		int samplesPerPixel = 1;
		int bytesPerSample = 0;

		int badAttributeCount = 0;
		AttributeTag tag = null;
		try {
			// CBZip2InputStream.available() always returns zero, and since we terminate
			// on exceptions anyway, just forget about it
			while (/*i.available() > 0 && */(undefinedLength || byteOffset < endByteOffset)) {
//System.err.println("read: i.available()="+i.available());
//System.err.println("read: loop byteOffset="+byteOffset+" endByteOffset="+endByteOffset);
				tag = readAttributeTag(i);
				byteOffset+=4;
//System.err.println("read: tag="+tag);

				if (stopAtTag != null && tag.equals(stopAtTag)) {
//System.err.println("read: stopped at "+tag);
					return byteOffset;	// stop now, since we have reached the tag at which we were told to stop
				}
				
				if (tag.equals(TagFromName.ItemDelimitationItem)) {
//System.err.println("read: ItemDelimitationItem");
					// Read and discard value length
					i.readUnsigned32();
					byteOffset+=4;
					return byteOffset;	// stop now, since we must have been called to read an item's dataset
				}
				
				if (tag.equals(TagFromName.Item)) {
					// this is bad ... there shouldn't be Items here since they should
					// only be found during readNewSequenceAttribute()
					// however, try to work around Philips bug ...
					long vl = i.readUnsigned32();		// always implicit VR form for items and delimiters
					byteOffset+=4;
System.err.println("Ignoring bad Item at "+byteOffset+" "+tag+" VL=<0x"+Long.toHexString(vl)+">");
					// let's just ignore it for now
					continue;
				}
				
				boolean checkForIncorrectImplicitVRElementEncodinginExplicitVR = false;		// DicomWorks bug
				boolean encounteredIncorrectImplicitVRElementEncodinginExplicitVR = false;
				if (explicit && i.markSupported()) {
					checkForIncorrectImplicitVRElementEncodinginExplicitVR = true;
					i.mark(4/*forward read limit*/);
				}

				byte vr[];
				if (explicit) {
					vr=vrBuffer;
					i.readInsistently(vr,0,2);
					if (checkForIncorrectImplicitVRElementEncodinginExplicitVR && (vr[0] < 'A' || vr[1] < 'A')) {	// i.e., not a valid explicit VR
System.err.println("AttributeList.read(): "+tag+" encountered incorrect Implicit VR Element encoding in Explicit VR Transfer Syntax ... compensating");					
						encounteredIncorrectImplicitVRElementEncodinginExplicitVR = true;	// will force reading of 32 bit implicit style VL
						i.reset();
						vr[0]='U';
						vr[1]='N';
					}
					else {
						byteOffset+=2;
					}
				}
				else {
					vr = dictionary.getValueRepresentationFromTag(tag);
					if (vr == null)  {
						vr=vrBuffer;
						vr[0]='U';
						vr[1]='N';
					}
				}
	
				long vl;
				if (explicit && !encounteredIncorrectImplicitVRElementEncodinginExplicitVR) {
					if (ValueRepresentation.isShortValueLengthVR(vr)) {
						vl=i.readUnsigned16();
						byteOffset+=2;
					}
					else {
						i.readUnsigned16();	// reserved bytes
						vl=i.readUnsigned32();
						byteOffset+=6;
					}
				}
				else {
					vl=i.readUnsigned32();
					byteOffset+=4;
				}

				if (explicit) {
					// do not do this until AFTER the value length has been read, since explicit UN uses the long form of length
					if (ValueRepresentation.isUnknownVR(vr)) {
						byte vrd[] = dictionary.getValueRepresentationFromTag(tag);
						if (vrd != null && vrd.length >= 2) {
//System.err.println("AttributeList.read(): For tag "+tag+" consider overriding explicit VR "+ValueRepresentation.getAsString(vr)+" with "+ValueRepresentation.getAsString(vrd));
							if (!ValueRepresentation.isSequenceVR(vrd)) {
//System.err.println("AttributeList.read(): For tag "+tag+" overriding explicit VR "+ValueRepresentation.getAsString(vr)+" with "+ValueRepresentation.getAsString(vrd));
								vr[0] = vrd[0];
								vr[1] = vrd[1];
							}
						}
					}
				}
				
				if (tag.isPrivateCreator()) {	// silently override VR, whether it be explictly UN or just wrong, or the default UN up to this point for implicit
					vr[0] = 'L';
					vr[1] = 'O';
				}
//System.err.println(byteOffset+" "+tag+" VR=<"+ValueRepresentation.getAsString(vr)+"> VL=<0x"+Long.toHexString(vl)+">");

				Attribute a = null;

				if (ValueRepresentation.isSequenceVR(vr) || (ValueRepresentation.isUnknownVR(vr) && vl == 0xffffffffl)) {
					a=new SequenceAttribute(tag);
					byteOffset=readNewSequenceAttribute(a,i,byteOffset,vl,specificCharacterSet);
				}
				else if (vl != 0xffffffffl) {
					checkSanityOfValueLength(vl,vr,tag,encounteredIncorrectImplicitVRElementEncodinginExplicitVR);
					try {
						a = AttributeFactory.newAttribute(tag,vr,vl,i,specificCharacterSet,explicit,bytesPerSample,byteOffset);	// creates and reads the attribute
					}
					catch (Exception e) {
						e.printStackTrace(System.err);
						a = null;
						if (++badAttributeCount > badAttributeLimit) {
							throw new DicomException("Too many bad attributes - probably not a DICOM dataset at all - giving up");
						}
					}
					byteOffset+=vl;
				}
				else if (vl == 0xffffffffl && tag.equals(TagFromName.PixelData)/* && i.getTransferSyntaxInUse().isEncapsulated()*/) {	// assume encapsulated in case TS is not recognized
					int wordsPerFrame = rows*columns*samplesPerPixel;
//System.err.println("Undefined length encapsulated Pixel Data: words per frame "+wordsPerFrame);
					String tsuid = i.getTransferSyntaxInUse().getUID();
//System.err.println("Undefined length encapsulated Pixel Data: TransferSyntax UID "+tsuid);
					boolean doneReadingEncapsulatedData = false;
					EncapsulatedInputStream ei = new EncapsulatedInputStream(i);
					//try {
					{
						if (tsuid.equals(TransferSyntax.PixelMedEncapsulatedRawLittleEndian)) {
							if (bytesPerSample == 1) {
								byte[] values = new byte[wordsPerFrame*frames];
								for (int f=0; f<frames; ++f) {
									ei.read(values,f*wordsPerFrame,wordsPerFrame);
									//ei.nextFrame();
								}
								a = new OtherByteAttribute(tag);
								a.setValues(values);
								doneReadingEncapsulatedData=true;
							}
							else if (bytesPerSample == 2) {
								short[] values = new short[wordsPerFrame*frames];
								for (int f=0; f<frames; ++f) {
									ei.readUnsigned16(values,f*wordsPerFrame,wordsPerFrame);
									//ei.nextFrame();
								}
								a = new OtherWordAttribute(tag);
								a.setValues(values);
								doneReadingEncapsulatedData=true;
							}
							else {
								throw new DicomException("Encapsulated data of more than 2 bytes per sample not supported (got "+bytesPerSample+")");
							}
						}
						else {
							if (!haveScannedForCodecs) {
//System.err.println("AttributeList.read(): Scanning for ImageIO plugin codecs");
//								ImageIO.scanForPlugins();
								haveScannedForCodecs=true;
							}
							pixelDataWasDecompressed = true;
							pixelDataWasLossy=false;
							lossyMethod=null;
							compressionRatio=0;
							String readerWanted = null;
//System.err.println("AttributeList.read(): TransferSyntax = "+tsuid);
							if (tsuid.equals(TransferSyntax.JPEGBaseline) || tsuid.equals(TransferSyntax.JPEGExtended)) {
								readerWanted="JPEG";
								pixelDataWasLossy=true;
								lossyMethod="ISO_10918_1";
//System.err.println("Undefined length encapsulated Pixel Data in JPEG Lossy");
							}
							else if (tsuid.equals(TransferSyntax.JPEG2000)) {
								readerWanted="JPEG2000";
								pixelDataWasLossy=true;
								lossyMethod="ISO_15444_1";
//System.err.println("Undefined length encapsulated Pixel Data in JPEG 2000");
							}
							else if (tsuid.equals(TransferSyntax.JPEG2000Lossless)) {
								readerWanted="JPEG2000";
//System.err.println("Undefined length encapsulated Pixel Data in JPEG 2000");
							}
							else if (tsuid.equals(TransferSyntax.JPEGLossless) || tsuid.equals(TransferSyntax.JPEGLosslessSV1)) {
								readerWanted="jpeg-lossless";
//System.err.println("Undefined length encapsulated Pixel Data in JPEG Lossless");
							}
							else if (tsuid.equals(TransferSyntax.JPEGLS)) {
								readerWanted="jpeg-ls";
//System.err.println("Undefined length encapsulated Pixel Data in JPEG-LS");
							}
							else if (tsuid.equals(TransferSyntax.JPEGNLS)) {
								readerWanted="jpeg-ls";
								pixelDataWasLossy=true;
								lossyMethod="ISO_14495_1";
//System.err.println("Undefined length encapsulated Pixel Data in JPEG-LS");
							}
							else {
								readerWanted="JPEG";
System.err.println("Unrecognized Transfer Syntax "+tsuid+" for encapsulated PixelData - guessing "+readerWanted);
							}
							if (readerWanted != null) {
//								ImageReader reader = null;
//								ImageReaderSpi spi = null;
//								try {
//									reader =  (ImageReader)(ImageIO.getImageReadersByFormatName(readerWanted).next());
//									spi = reader.getOriginatingProvider();
//									String readerDescription = spi.getDescription(Locale.US);
//									String readerVendorName = spi.getVendorName();
//									String readerVersion = spi.getVersion();
//									if (tsuid.equals(TransferSyntax.JPEGExtended) && readerDescription.equals("Standard JPEG Image Reader") && readerVendorName.equals("Sun Microsystems, Inc.")) {
//										throw new DicomException("Reader "+readerDescription+" "+readerVendorName+" "+readerVersion+" does not support extended lossy JPEG Transfer Syntax "+tsuid);
//									}
//System.err.println("Using reader from "+readerDescription+" "+readerVendorName+" "+readerVersion);
////									OurIIOReadProgressListener progressListener = new OurIIOReadProgressListener();
////									reader.addIIOReadProgressListener(progressListener);
////System.err.println("Back from reader.addIIOReadProgressListener()");
//								}
//								catch (Exception e) {
//									dumpListOfAllAvailableReaders(System.err);
//									throw new DicomException("No reader for "+readerWanted+" available for Transfer Syntax "+tsuid+"\nCaused by: "+e);
//								}
//								if (reader != null) {
//									byte[]  bytePixelData = null;	// lazy instantiation of one or the other
//									short[] shortPixelData = null;
////System.err.println("Using columns = "+columns);
////System.err.println("Using rows = "+rows);
////System.err.println("Using frames = "+frames);
////System.err.println("Using samplesPerPixel = "+samplesPerPixel);
////System.err.println("Using bytesPerSample = "+bytesPerSample);
//									int pixelsPerFrame = columns*rows*samplesPerPixel;
//									int pixelsPerMultiFrameImage = pixelsPerFrame*frames;
//									for (int f=0; f<frames; ++f) {
////System.err.println("Starting frame "+f);
//										BufferedImage image = null;
//										ImageInputStream iiois = ImageIO.createImageInputStream(ei);
//										reader.setInput(iiois,true/*seekForwardOnly*/,true/*ignoreMetadata*/);
//										image = reader.read(0);
////System.err.println("Back from frame "+f+" reader.read(), BufferedImage="+image);
//										if (image == null) {
//											throw new DicomException("Reader "+spi.getDescription(Locale.US)+" "+spi.getVendorName()+" "+spi.getVersion()
//												+" returned null image for Transfer Syntax "+tsuid);
//										}
//										else {
//											Raster raster = image.getData();
//											int numDataElements = raster.getNumDataElements();
////System.err.println("getNumDataElements="+numDataElements);
//											if (numDataElements == samplesPerPixel) {
//												int transferType = raster.getTransferType();
////System.err.println("getTransferType="+transferType);
//												if (transferType == DataBuffer.TYPE_BYTE) {
////System.err.println("Getting "+(samplesPerPixel > 1 ? "interleaved " : "")+samplesPerPixel+" channel byte data");
//													byte[] vPixelData = (byte[])(raster.getDataElements(0,0,columns,rows,null));
////System.err.println("Decompressed byte array length "+vPixelData.length+" expected "+pixelsPerFrame);
//													if (bytePixelData == null) {
//														if (frames == 1) {
//															bytePixelData = vPixelData;
//														}
//														else {
//															bytePixelData = new byte[pixelsPerMultiFrameImage];
//														}
//													}
//													if (vPixelData != null) {
//														System.arraycopy(vPixelData,0,bytePixelData,pixelsPerFrame*f,pixelsPerFrame);
//													}
//												}
//												else if (transferType == DataBuffer.TYPE_SHORT
//												      || transferType == DataBuffer.TYPE_USHORT) {
////System.err.println("Getting "+(samplesPerPixel > 1 ? "interleaved " : "")+samplesPerPixel+" channel byte data");
//													short[] vPixelData = (short[])(raster.getDataElements(0,0,columns,rows,null));
////System.err.println("Decompressed short array length "+vPixelData.length+" expected "+pixelsPerFrame);
//													if (shortPixelData == null) {
//														if (frames == 1) {
//															shortPixelData = vPixelData;
//														}
//														else {
//															shortPixelData = new short[pixelsPerMultiFrameImage];
//														}
//													}
//													if (vPixelData != null) {
//														System.arraycopy(vPixelData,0,shortPixelData,pixelsPerFrame*f,pixelsPerFrame);
//													}
//												}
//											}
//										}
//										ei.nextFrame();
//									}
//									ei.readSequenceDelimiter();		// since we terminated loop on number of frames, rather than keeping going until ran out, we need to absorb the delimiter
//									if (bytePixelData != null) {
//										a = new OtherByteAttribute(tag);
//										a.setValues(bytePixelData);
//										pixelDataWasDecompressed = true;
//									}
//									else if (shortPixelData != null) {
//										a = new OtherWordAttribute(tag);
//										a.setValues(shortPixelData);
//										pixelDataWasDecompressed = true;
//									}
//									doneReadingEncapsulatedData=true;
//									try {
////System.err.println("AttributeList.read(): Calling dispose() on reader");
//										reader.dispose();
//									}
//									catch (Exception e) {
//										e.printStackTrace(System.err);
//									}
//								}
							}
							else {
//System.err.println("Unrecognized Transfer Syntax "+tsuid+" for encapsulated PixelData");
								throw new DicomException("Unrecognized Transfer Syntax "+tsuid+" for encapsulated PixelData");
							}
						}
					}
					//catch (Exception e) {
					//	e.printStackTrace(System.err);
					//}
					if (!doneReadingEncapsulatedData) {
//System.err.println("Skipping encapsulated pixel data");
						while (ei.skip(1024) > 0);	// it is appropriate to use skip() rather than use skipInsistently() here 
					}
					{
						long encapsulatedBytesRead = ei.getBytesRead();
						byteOffset+= encapsulatedBytesRead;		// otherwise won't be able to detect end of fixed length sequences and items that contain encapsulated pixel data (e.g., IconImageSequence)
						if (pixelDataWasDecompressed) {
							// compute CR with precision of three decimal places
							compressionRatio = (long)columns*rows*samplesPerPixel*bytesPerSample*frames*1000/encapsulatedBytesRead;
							compressionRatio = compressionRatio / 1000;
//System.err.println("AttributeList.read(): compressionRatio = "+compressionRatio);
						}
					}
//System.err.println("Done with encapsulated pixel data");
				}

				if (a != null) {
//System.err.println(a.toString());
					if (get(tag) != null) {
System.err.println("Illegal duplicate tag in dataset - "+tag+" - replacing previous occurence");
					}
					put(tag,a);

					if (tag.equals(TagFromName.FileMetaInformationGroupLength)) {
						if (i.areReadingMetaHeader()) {
//System.err.println("Found meta-header");
//System.err.println("Length attribute class="+a.getClass());
							long metaLength=a.getSingleIntegerValueOrDefault(0);
							byteOffset=read(i,byteOffset,metaLength,false,null,stopAtTag);		// detects and sets transfer syntax for reading dataset
							i.setReadingDataSet();
							if (stopAfterMetaInformationHeader) {
//System.err.println("Stopping after meta-header");
								break;
							}
							else {
//System.err.println("Calling read");
								byteOffset=read(i,byteOffset,0xffffffffl,false,null,stopAtTag);	// read to end (will detect and set own SpecificCharacterSet)
//System.err.println("Back from read after metaheader: now undefinedLength="+undefinedLength+" byteOffset="+byteOffset+" endByteOffset="+endByteOffset);
								break;	// ... no plausible reason to continue past this point
							}
						}
						else {
							// ignore it, e.g. nested within a sequence item (GE bug).
//System.err.println("Ignoring unexpected FileMetaInformationGroupLength outside meta information header");
						}
					}
					else if (tag.equals(TagFromName.TransferSyntaxUID)) {
						if (i.areReadingMetaHeader()) {
							i.setTransferSyntaxToReadDataSet(new TransferSyntax(a.getSingleStringValueOrDefault(TransferSyntax.ExplicitVRLittleEndian)));
						}
						else {
							// ignore it, e.g. nested within a sequence item (GE bug).
//System.err.println("Ignoring unexpected TransferSyntaxUID outside meta information header");
						}
					}
					else if (tag.equals(TagFromName.SpecificCharacterSet)) {
						specificCharacterSet = new SpecificCharacterSet(a.getStringValues(),a.getByteValues());
					}
					else if (tag.equals(TagFromName.Columns)) {
						columns = a.getSingleIntegerValueOrDefault(0);
//System.err.println("Setting columns = "+columns);
					}
					else if (tag.equals(TagFromName.Rows)) {
						rows = a.getSingleIntegerValueOrDefault(0);
//System.err.println("Setting rows = "+rows);
					}
					else if (tag.equals(TagFromName.NumberOfFrames)) {
						frames = a.getSingleIntegerValueOrDefault(1);
//System.err.println("Setting frames = "+frames);
					}
					else if (tag.equals(TagFromName.SamplesPerPixel)) {
						samplesPerPixel = a.getSingleIntegerValueOrDefault(1);
//System.err.println("Setting samplesPerPixel = "+samplesPerPixel);
					}
					else if (tag.equals(TagFromName.BitsAllocated)) {
						bytesPerSample = (a.getSingleIntegerValueOrDefault(16)-1)/8+1;
//System.err.println("Setting bytesPerSample = "+bytesPerSample);
					}
				}
			}
		}
		catch (EOFException e) {
//System.err.println("Closing on "+e);
			if (!undefinedLength) throw new EOFException();
		}
		catch (IOException e) {
//System.err.println("Closing on "+e);
			if (!undefinedLength) throw new IOException();		// InflaterInputStream seems to throw IOException rather than EOFException
		}
//System.err.println("AttributeList.read(): end");
		return byteOffset;
	}

	/**
	 * <p>Read the meta information header (if present) and then stop.</p>
	 *
	 * <p>Leaves the stream opened and positioned at the start of the data set.</p>
	 *
	 * @param	i		the stream to read from
	 * @return			the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long readOnlyMetaInformationHeader(DicomInputStream i) throws IOException, DicomException {
		return read(i,i.getByteOffsetOfStartOfData(),0xffffffffl,true,null);
//System.err.println("readOnlyMetaInformationHeader(): afterwards i.areReadingDataSet()="+i.areReadingDataSet());
		// important that i.areReadingDataSet() be true at this point ... triggers check for codec if read (or copied) further
	}

	/**
	 * <p>Read all the DICOM attributes in the stream until the specified tag is encountered.</p>
	 *
	 * <p>Does not read beyond the group element pair of the specified stop tag.</p>
	 *
	 * <p>Leaves the stream open.</p>
	 *
	 * @param	i				the stream to read from
	 * @param	stopAtTag		the tag (in the top level data set) at which to stop
	 * @return					the byte offset at which the read stopped (which will be just past the stopAtTag, if stopped)
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(DicomInputStream i,AttributeTag stopAtTag) throws IOException, DicomException {
//System.err.println("read(DicomInputStream i,AttributeTag stopAtTag="+stopAtTag+"):");
		return read(i,i.getByteOffsetOfStartOfData(),0xffffffffl,false,null,stopAtTag);
	}

	/**
	 * <p>Read all the DICOM attributes in the stream until there are no more.</p>
	 *
	 * <p>Leaves the stream open.</p>
	 *
	 * @param	i		the stream to read from
	 * @return			the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(DicomInputStream i) throws IOException, DicomException {
		return read(i,i.getByteOffsetOfStartOfData(),0xffffffffl,false,null);
	}

	/**
	 * <p>Read an entire DICOM object in the specified file.</p>
	 *
	 * <p>Reads the attributes of both the meta information header (if present) and data set.</p>
	 *
	 * @param	name				the input file name
	 * @param	transferSyntaxUID	the transfer syntax to use for the data set (leave null for autodetection)
	 * @param	hasMeta				look for a meta information header
	 * @param	useBufferedStream	buffer the input for better performance
	 * @return						the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(String name,String transferSyntaxUID,boolean hasMeta,boolean useBufferedStream) throws IOException, DicomException {
		return read(name,transferSyntaxUID,hasMeta,useBufferedStream,null);
	}
	
	/**
	 * <p>Read an entire DICOM object in the specified file.</p>
	 *
	 * <p>Reads the attributes of both the meta information header (if present) and data set.</p>
	 *
	 * @param	name				the input file name
	 * @param	transferSyntaxUID	the transfer syntax to use for the data set (leave null for autodetection)
	 * @param	hasMeta				look for a meta information header
	 * @param	useBufferedStream	buffer the input for better performance
	 * @param	stopAtTag			the tag (in the top level data set) at which to stop
	 * @return						the byte offset at which the read stopped (which will be just past the stopAtTag, if stopped)
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(String name,String transferSyntaxUID,boolean hasMeta,boolean useBufferedStream,AttributeTag stopAtTag) throws IOException, DicomException {
		long byteOffset = 0;
		InputStream i = null;
		DicomInputStream di = null;
		try {
			File file = new File(name);
			i = new FileInputStream(file);
			if (useBufferedStream) {
				i = new BufferedInputStream(i);
			}
			di = new DicomInputStream(i,transferSyntaxUID,hasMeta);
			if (di.getFile() == null) {
				di.setFile(file);	// need this to allow large PixelData OX to be left on disk (000596)
			}
			byteOffset = read(di,stopAtTag);
		}
		catch (IOException e) {
			throw e;			// we do this so that the finally will execute, whether an exception or not
		}
		catch (DicomException e) {
			throw e;			// we do this so that the finally will execute, whether an exception or not
		}
		finally {
			if (di != null) {
				try {
					di.close();
				}
				catch (Exception e) {
				}
			}
			if (i != null) {
				try {
					i.close();
				}
				catch (Exception e) {
				}
			}
		}
		return byteOffset;
	}

	/**
	 * <p>Read the meta information header (if present) for the specified file and then close it.</p>
	 *
	 * @param	name	the input file name
	 * @return			the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long readOnlyMetaInformationHeader(String name) throws IOException, DicomException {
		long byteOffset = 0;
		InputStream i = null;
		DicomInputStream di = null;
		try {
			i = new FileInputStream(name);
			i=new BufferedInputStream(i);
			di = new DicomInputStream(i,null,true/*hasMeta*/);
			byteOffset = readOnlyMetaInformationHeader(di);
		}
		catch (IOException e) {
			throw e;			// we do this so that the finally will execute, whether an exception or not
		}
		catch (DicomException e) {
			throw e;			// we do this so that the finally will execute, whether an exception or not
		}
		finally {
			if (di != null) {
				try {
					di.close();
				}
				catch (Exception e) {
				}
			}
			if (i != null) {
				try {
					i.close();
				}
				catch (Exception e) {
				}
			}
		}
		return byteOffset;
	}

	/**
	 * <p>Read an entire DICOM object in the specified file.</p>
	 *
	 * <p>Reads the attributes of both the meta information header (if present) and data set.</p>
	 *
	 * <p>Always tries to automatically detect the meta information header or transfer syntax
	 * if no meta information header and buffers the input for better performance.</p>
	 *
	 * @param	name	the input file name
	 * @return			the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(String name) throws IOException, DicomException {
		return read(name,null,true,true);
	}
	
	/**
	 * <p>Read an entire DICOM object in the specified file.</p>
	 *
	 * <p>Reads the attributes of both the meta information header (if present) and data set.</p>
	 *
	 * <p>Always tries to automatically detect the meta information header or transfer syntax
	 * if no meta information header and buffers the input for better performance.</p>
	 *
	 * @param	name			the input file name
	 * @param	stopAtTag		the tag (in the top level data set) at which to stop
	 * @return					the byte offset at which the read stopped (which will be just past the stopAtTag, if stopped)
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(String name,AttributeTag stopAtTag) throws IOException, DicomException {
		return read(name,null,true,true,stopAtTag);
	}

	/**
	 * <p>Read an entire DICOM object in the specified file.</p>
	 *
	 * <p>Reads the attributes of both the meta information header (if present) and data set.</p>
	 *
	 * <p>Always tries to automatically detect the meta information header or transfer syntax
	 * if no meta information header and buffers the input for better performance.</p>
	 *
	 * @param	file	the input file
	 * @return			the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(File file) throws IOException, DicomException {
		return read(file.getCanonicalPath());
	}
	
	/**
	 * <p>Read an entire DICOM object in the specified file.</p>
	 *
	 * <p>Reads the attributes of both the meta information header (if present) and data set.</p>
	 *
	 * <p>Always tries to automatically detect the meta information header or transfer syntax
	 * if no meta information header and buffers the input for better performance.</p>
	 *
	 * @param	file			the input file
	 * @param	stopAtTag		the tag (in the top level data set) at which to stop
	 * @return					the byte offset at which the read stopped (which will be just past the stopAtTag, if stopped)
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long read(File file,AttributeTag stopAtTag) throws IOException, DicomException {
		return read(file.getCanonicalPath(),stopAtTag);
	}
	
	/**
	 * <p>Read the meta information header (if present) for the specified file and then close it.</p>
	 *
	 * @param	file	the input file
	 * @return			the byte offset at which the read stopped
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public long readOnlyMetaInformationHeader(File file) throws IOException, DicomException {
		return readOnlyMetaInformationHeader(file.getCanonicalPath());
	}
	
	/**
	 * <p>Write the entire attribute list (which may be part of a larger enclosing dataset) to the specified stream.</p>
	 *
	 * <p>Does not close the output stream, assumes any meta header vs. dataset ransition has occurred and
	 * further assumes that any additional codecs (like deflate) have already been pushed onto the stream.</p>
	 *
	 * <p>Intended for use only for write datasets that are within sequence items (hence is not public).</p>
	 *
	 * @param	dout		the stream to write to
	 * @exception	IOException
	 * @exception	DicomException
	 */
	protected void writeFragment(DicomOutputStream dout) throws IOException, DicomException {
		for (Attribute a : values()) {
//System.err.println("Writing "+a);
			a.write(dout);
		}
	}
	
	/**
	 * <p>Write the entire attribute list to the specified stream.</p>
	 *
	 * <p>Leaves the stream open.</p>
	 *
	 * @param	dout		the stream to write to
	 * @param	useMeta		true if the meta information header attributes are to be written, false if they are to be ommitted
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(DicomOutputStream dout,boolean useMeta) throws IOException, DicomException {
		DeflaterOutputStream deflaterOutputStream = null;
		OutputStream bzip2OutputStream = null;
		for (Attribute a : values()) {
			boolean isDataSetAttribute = a.getTag().getGroup() > 0x0002;
			if (isDataSetAttribute) {
				// Test to see whether or not a codec needs to be pushed on the stream ... after the first time, the TransferSyntax will always be ExplicitVRLittleEndian 
//System.err.println("Testing for deflate and bzip2 TS");
				if (dout.getTransferSyntaxToWriteDataSet().isDeflated()) {
					// insert deflate into output stream and make a new DicomOutputStream
//System.err.println("Creating new DicomOutputStream from deflate");
					deflaterOutputStream = new DeflaterOutputStream(dout,new Deflater(Deflater.BEST_COMPRESSION,true/*nowrap*/));
					dout = new DicomOutputStream(deflaterOutputStream,null,TransferSyntax.ExplicitVRLittleEndian);
				}
				else if (dout.getTransferSyntaxToWriteDataSet().isBzip2ed()) {
					// insert bzip2 into output stream and make a new DicomOutputStream
//System.err.println("DicomStreamCopier.copy(): Output stream - creating new DicomOutputStream from bzip2");
					try {
						Class classToUse = Thread.currentThread().getContextClassLoader().loadClass("org.apache.excalibur.bzip2.CBZip2OutputStream");
						Class [] argTypes  = {OutputStream.class};
						Object[] argValues = {dout};
						bzip2OutputStream = (OutputStream)(classToUse.getConstructor(argTypes).newInstance(argValues));
						dout = new DicomOutputStream(bzip2OutputStream,null/*no meta-header*/,TransferSyntax.ExplicitVRLittleEndian);
					}
					catch (Exception e) {	// may be ClassNotFoundException,NoSuchMethodException,InstantiationException
						throw new DicomException("Could not instantiate bzip2 codec - "+e);
					}
				}
				dout.setWritingDataSet();
			}
			if (isDataSetAttribute || useMeta) {
//System.err.println("Writing "+a);
				a.write(dout);
			}
		}
		// do not use dout.close(), since causes network activities to fail
		// a dout.flush() alone is not sufficient to flush any remaining output from any pushed codecs
		if (deflaterOutputStream != null) {
			deflaterOutputStream.finish();		// NOT close(), since we may not want to close the underlying output stream (e.g., on a network association); method is specific to java.util.zip.DeflaterOutputStream
		}
		if (bzip2OutputStream != null) {
			bzip2OutputStream.close();			// flush() alone is not sufficient :(
			//assert closeWhenDone=true;
		}
	}
	
	/**
	 * <p>Write the entire attribute list to the specified stream.</p>
	 *
	 * <p>Leaves the stream open.</p>
	 *
	 * @param	dout		the stream to write to
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(DicomOutputStream dout) throws IOException, DicomException {
		write(dout,true/*useMeta*/);
	}

	/**
	 * <p>Write the entire attribute list to the specified stream.</p>
	 *
	 * <p>Closes the stream after writing.</p>
	 *
	 * @param	o					the stream to write to
	 * @param	transferSyntaxUID	the transfer syntax to use to write the data set
	 * @param	useMeta				write the meta information header attributes (if true they must be present in the list with appropriate values already)
	 * @param	useBufferedStream	buffer the output for better performance (set this true only if the supplied stream is not already buffered)
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(OutputStream o,String transferSyntaxUID,boolean useMeta,boolean useBufferedStream) throws IOException, DicomException {
		write(o,transferSyntaxUID,useMeta,useBufferedStream,true/*closeAfterWrite*/);
	}

	/**
	 * <p>Write the entire attribute list to the specified stream.</p>
	 *
	 * @param	o					the stream to write to
	 * @param	transferSyntaxUID	the transfer syntax to use to write the data set
	 * @param	useMeta				write the meta information header attributes (if true they must be present in the list with appropriate values already)
	 * @param	useBufferedStream	buffer the output for better performance (set this true only if the supplied stream is not already buffered)
	 * @param	closeAfterWrite		requests that the supplied stream be closed after writing
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(OutputStream o,String transferSyntaxUID,boolean useMeta,boolean useBufferedStream,boolean closeAfterWrite) throws IOException, DicomException {
		if (useBufferedStream) o=new BufferedOutputStream(o);
		try {
			DicomOutputStream dout = new DicomOutputStream(o,useMeta ? TransferSyntax.ExplicitVRLittleEndian : null,transferSyntaxUID);
			write(dout,useMeta);
			dout.close();
		}
		finally {
			if (closeAfterWrite) {
				o.close();
			}
		}
	}

	/**
	 * <p>Write the entire attribute list to the named file.</p>
	 *
	 * @param	name			the file name to write to
	 * @param	transferSyntaxUID	the transfer syntax to use to write the data set
	 * @param	useMeta			write the meta information header attributes (if true they must be present in the list with appropriate values already)
	 * @param	useBufferedStream	buffer the output for better performance
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(String name,String transferSyntaxUID,boolean useMeta,boolean useBufferedStream) throws IOException, DicomException {
		OutputStream o = new FileOutputStream(name);
		write(o,transferSyntaxUID,useMeta,useBufferedStream);
	}

	/**
	 * <p>Write the entire attribute list to the named file in explicit VR little endian transfer syntax with a meta information header.</p>
	 *
	 * @param	name			the file name to write to
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(String name) throws IOException, DicomException {
		write(name,TransferSyntax.ExplicitVRLittleEndian,true/*useMeta*/,true/*useBufferedStream*/);
	}

	/**
	 * <p>Write the entire attribute list to the specified file.</p>
	 *
	 * @param	file			the file to write to
	 * @param	transferSyntaxUID	the transfer syntax to use to write the data set
	 * @param	useMeta			write the meta information header attributes (if true they must be present in the list with appropriate values already)
	 * @param	useBufferedStream	buffer the output for better performance
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(File file,String transferSyntaxUID,boolean useMeta,boolean useBufferedStream) throws IOException, DicomException {
		OutputStream o = new FileOutputStream(file);
		write(o,transferSyntaxUID,useMeta,useBufferedStream);
	}

	/**
	 * <p>Write the entire attribute list to the specified file in explicit VR little endian transfer syntax with a meta information header.</p>
	 *
	 * @param	file			the file to write to
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public void write(File file) throws IOException, DicomException {
		write(file,TransferSyntax.ExplicitVRLittleEndian,true/*useMeta*/,true/*useBufferedStream*/);
	}

	/**
	 * <p>Associates the specified value (attribute) with the specified key (tag).</p>
	 *
	 * <p>If the map previously contained a mapping for this key, the old value is replaced.</p>
	 *
	 * <p>No untyped Object put(Object key, Object value) method is required to over ride the super
	 * class method since the parent TreeMap class of AttributeList is now typed, and the untyped
	 * method would have the same erasure.</p>
	 *
	 * @see java.util.TreeMap#put(Object,Object)
	 *
	 * @param	t			key (tag) with which the specified value (attribute) is to be associated
	 * @param	a			value (attribute) to be associated with the specified key (tag)
	 * @return				previous value (attribute) associated with specified key (tag), or null if there was no mapping for key (tag) 
	 * @exception	NullPointerException	thrown if a or t is null
	 * @exception	ClassCastException	thrown if a or t is not the correct class
	 */
	public Attribute put(AttributeTag t, Attribute a) throws NullPointerException,ClassCastException {
		if (a == null || t == null) {
			throw new NullPointerException();
		}
		else {
			return super.put(t,a);	// do not need cast to Attribute since super class is now typed
		}
	}


	/**
	 * <p>Associates the specified value (attribute) with the key that is the existing tag of the attribute.</p>
	 *
	 * <p>If the map previously contained a mapping for this key, the old value is replaced.</p>
	 *
	 * @see #put(AttributeTag,Attribute)
	 *
	 * @param	a			value (attribute) to be associated with the specified key (tag)
	 * @return				previous value (attribute) associated with specified key (tag), or null if there was no mapping for key (tag) 
	 * @exception	NullPointerException	thrown if a or t is null
	 */
	public Attribute put(Attribute a) {
		if (a == null) {
			throw new NullPointerException();
		}
		else {
			return put(a.getTag(),a);
		}
	}
	/**
	 * <p>Returns the value (attribute) to which this map maps the specified key (tag).</p>
	 *
	 * <p>Returns null if the map contains no mapping for this key. A return value of null
	 * does indicate that the map contains no mapping for the key, unlike {@link java.util.TreeMap#get(Object) java.util.get(Object)}
	 * since the put operation checks for and disallows null insertions. This contract will hold
	 * true unless one goes to great effort to insert a key that maps to a null value
	 * by using one of the other insertion methods of the super class, in which case
	 * other operations (like writing) may fail later with a NullPointerException.</p>
	 *
	 * @param	t	key (tag) whose associated value (attribute) is to be returned
	 */
	public Attribute get(AttributeTag t) {
		return super.get(t);	// do not need cast to Attribute since super class is now typed
	}

	/**
	 * <p>Determine whether or not this list is an image.</p>
	 *
	 * <p>An image is defined to be something with a PixelData attribute at the top level.</p>
	 *
	 * @return	true if an image 
	 */
	public boolean isImage() {
		return get(TagFromName.PixelData) != null;
	}

	/**
	 * <p>Determine whether or not this list is an enhanced instance.</p>
	 *
	 * <p>An enhanced instance is defined to be something with a Shared or Per-Frame Functional Groups Sequence attribute at the top level.</p>
	 *
	 * @return	true if an enhanced instance 
	 */
	public boolean isEnhanced() {
		return get(TagFromName.SharedFunctionalGroupsSequence) != null || get(TagFromName.PerFrameFunctionalGroupsSequence) != null;
	}

	/**
	 * <p>Determine whether or not this list is an SR Document.</p>
	 *
	 * <p>An SR Document is defined to be something with a ContentSequence attribute at the top level.</p>
	 *
	 * @see com.pixelmed.dicom.SOPClass#isStructuredReport(String)
	 *
	 * @return	true if an SR Document 
	 */
	public boolean isSRDocument() {
		return get(TagFromName.ContentSequence) != null;
	}

	/**
	 * <p>Get the dictionary in use for this list.</p>
	 *
	 * <p>Creates one if necessary.</p>
	 *
	 * @return	the dictionary 
	 */
	public static DicomDictionary getDictionary() {
		createDictionaryifNecessary();
		return dictionary;
	}
	
	/**
	 * <p>Removes the mapping for this key (tag), if present.</p>
	 *
	 * @param	tag	key (tag) for which mapping should be removed
	 * @return		previous value (attribute) associated with specified key (tag), or null if there was no mapping for key (tag) 
	 */
	public Attribute remove(AttributeTag tag) {
		return (Attribute)(super.remove(tag));
	}

	// useful list handling routines beyond those inherited from TreeMap
	
	/**
	 * <p>Replaces an attribute with a zero length attribute, if present in the list.</p>
	 *
	 * <p>Does nothing if the attribute was not already present.</p>
	 *
	 * @param	tag		key (tag) for which the attribute should be replaced
	 * @exception	DicomException	thrown if there is any difficulty creating the new zero length attribute
	 */
	public void replaceWithZeroLengthIfPresent(AttributeTag tag) throws DicomException {
		Object o=get(tag);
		if (o != null) {
			//remove(tag);
			Attribute a = AttributeFactory.newAttribute(tag,getDictionary().getValueRepresentationFromTag(tag));
			put(tag,a);
		}
	}
	
	/**
	 * <p>Replaces an attribute with a dummy value, if present in the list.</p>
	 *
	 * <p>Does nothing if the attribute was not already present.</p>
	 *
	 * @param	tag			key (tag) for which the attribute should be replaced
	 * @param	dummyValue	the dummy value to use
	 * @exception	DicomException	thrown if there is any difficulty creating the new zero length attribute
	 */
	public void replaceWithDummyValueIfPresent(AttributeTag tag,String dummyValue) throws DicomException {
		Object o=get(tag);
		if (o != null) {
			//remove(tag);
			Attribute a = AttributeFactory.newAttribute(tag,getDictionary().getValueRepresentationFromTag(tag));
			a.addValue(dummyValue);
			put(tag,a);
		}
	}

	// list management methods ...
	
	/**
	 * <p>Remove any private attributes present in the list.</p>
	 *
	 * <p>Private attributes are all those with an odd group number.</p>
	 *
	 * <p>Also recurses into standard sequences and removes any private attributes therein.</p>
	 */
	public void removePrivateAttributes() {
		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			Attribute a = i.next();
			if (a.getTag().isPrivate()) {
				i.remove();
			}
			else if (a instanceof SequenceAttribute) {
				Iterator<SequenceItem> i2 = ((SequenceAttribute)a).iterator();
				while (i2.hasNext()) {
					SequenceItem item = i2.next();
					if (item != null) {
						AttributeList list = item.getAttributeList();
						if (list != null) {
							list.removePrivateAttributes();
						}
					}
				}
			}
		}
	}
	
	/**
	 * <p>Remove unsafe private attributes present in the list.</p>
	 *
	 * <p>Unsafe private attributes are all those with an odd group number that are not known to be safe,
	 * in the sense that they do not contain individually identifiable information.</p>
	 *
	 * <p>Will not remove private creators of potentially safe private tags, even if there are no such safe tags found.</p>
	 *
	 * <p>Also recurses into standard sequences and removes any unsafe private attributes therein.</p>
	 *
	 * @see com.pixelmed.dicom.ClinicalTrialsAttributes
	 */
	public void removeUnsafePrivateAttributes() {
		Set privateCreatorTagsForSafePrivateAttributes = new HashSet();	// of AttributeTag
		// 1st pass ... remove the unsafe private tags themselves
		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			Attribute a = i.next();
			AttributeTag t = a.getTag();
//System.err.println("AttributeList.removeUnsafePrivateAttributes(): "+a);
			boolean removed = false;
			if (t.isPrivate()) {
				if (ClinicalTrialsAttributes.isSafePrivateAttribute(t,this)) {	// creators are safe
//System.err.println("AttributeList.removeUnsafePrivateAttributes(): is safe "+t);
					// leave the private attribute in place, and keep track of its private creator, so that we don't remove it later
					AttributeTag creatorTag = getPrivateCreatorTag(t);
//System.err.println("AttributeList.removeUnsafePrivateAttributes(): adding creatorTag to set to retain "+creatorTag);
					privateCreatorTagsForSafePrivateAttributes.add(creatorTag);
				}
				else {
//System.err.println("AttributeList.removeUnsafePrivateAttributes(): is unsafe "+t);
					i.remove();
					removed = true;
				}
			}
			// recurse into sequence if standard, or if private tag that was not removed, since safe private sequences may contain unsafe content !
			if (!removed && a instanceof SequenceAttribute) {
				Iterator<SequenceItem> i2 = ((SequenceAttribute)a).iterator();
				while (i2.hasNext()) {
					SequenceItem item = i2.next();
					if (item != null) {
						AttributeList list = item.getAttributeList();
						if (list != null) {
							list.removeUnsafePrivateAttributes();
						}
					}
				}
			}
		}
		// 2nd pass ... remove any private creator that is not used anymore (i.e., had no safe tags)
		i = values().iterator();
		while (i.hasNext()) {
			Attribute a = i.next();
			AttributeTag t = a.getTag();
			if (t.isPrivateCreator() && !privateCreatorTagsForSafePrivateAttributes.contains(t)) {
//System.err.println("AttributeList.removeUnsafePrivateAttributes(): removing unused creator "+t);
				i.remove();
			}
		}
	}
	
	/**
	 * <p>Get the private creator of a private tag.</p>
	 *
	 * @param	tag	the private tag
	 * @return		the private creator tag
	 */
	public AttributeTag getPrivateCreatorTag(AttributeTag tag) {
		int group = tag.getGroup();
		int element = tag.getElement();
		int block = (element & 0xff00) >> 8;
		return new AttributeTag(group,block);
	}
	
	/**
	 * <p>Get the private creator of a private tag.</p>
	 *
	 * @param	tag	the private tag
	 * @return		the private creator, or an empty String if not found
	 */
	public String getPrivateCreatorString(AttributeTag tag) {
//System.err.println("AttributeList.getPrivateCreatorString(): for tag "+tag);
		AttributeTag creatorTag = getPrivateCreatorTag(tag);
//System.err.println("AttributeList.getPrivateCreatorString(): creatorTag is "+creatorTag);
		String creator = Attribute.getSingleStringValueOrEmptyString(this,creatorTag);
//System.err.println("AttributeList.getPrivateCreatorString(): creator is "+creator);
		return creator;
	}
	
	/**
	 * <p>Remove any meta information header attributes present in the list.</p>
	 *
	 * <p>Meta information header attributes are all those in group 0x0002.</p>
	 *
	 * <p>Note that this should always be done when modifying the SOP Class or
	 * Instance UID of an attribute list what has been read before writing,
	 * since it is vital that the corresponding meta information header attributes
	 * match those in the data set.</p>
	 *
	 * @see com.pixelmed.dicom.FileMetaInformation
	 */
	public void removeMetaInformationHeaderAttributes() {
		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			Attribute a = i.next();
			if (a.getTag().getGroup() == 0x0002) {
				i.remove();
			}
		}
	}
	
	/**
	 * <p>Remove any group length attributes present in the list, except the meta information header length, as well as LengthToEnd.</p>
	 *
	 * <p>Group length attributes are all those with an element of 0x0000.</p>
	 *
	 * <p>LengthToEnd (0x0008,0x0001) is always removed if present as well.</p>
	 *
	 * <p>These have never been required in DICOM and are a holdover from the old
	 * ACR-NEMA days, and are a source of constant problems, so should always
	 * be removed.</p>
	 *
	 * <p>The meta information header length is left alone, since it is mandatory.</p>
	 *
	 * <p>Also recurses into sequences and removes any contained group lengths.</p>
	 *
	 * @see com.pixelmed.dicom.FileMetaInformation
	 */
	public void removeGroupLengthAttributes() {
		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			Attribute a = i.next();
			if (a instanceof SequenceAttribute) {
				Iterator<SequenceItem> i2 = ((SequenceAttribute)a).iterator();
				while (i2.hasNext()) {
					SequenceItem item = i2.next();
					if (item != null) {
						AttributeList list = item.getAttributeList();
						if (list != null) {
							list.removeGroupLengthAttributes();
						}
					}
				}
			}
			else {
				AttributeTag t = a.getTag();
				if (t.getElement() == 0x0000 && t.getGroup() != 0x0002) i.remove();	// leave metaheader alone
			}
		}
		remove(TagFromName.LengthToEnd);
	}

	/**
	 * <p>Remove any overlay attributes present in the list.</p>
	 *
	 * <p>Overlay attributes are those for which {@link com.pixelmed.dicom.AttributeTag#isOverlayGroup() com.pixelmed.dicom.AttributeTag.isOverlayGroup()} returns true.</p>
	 *
	 * <p>Note that any overlay data in the high bits of the PixelData are NOT removed by this method
	 * but can be removed by reading the <code>PixelData</code> into a {@link com.pixelmed.display.SourceImage com.pixelmed.display.SourceImage} and creating a new <code>PixelData</code> attribute from it.</p>
	 */
	public void removeOverlayAttributes() {
		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			Attribute a = i.next();
			if (a.getTag().isOverlayGroup()) {
				i.remove();
			}
		}
	}

	/**
	 * <p>Remove any curve attributes present in the list.</p>
	 *
	 * <p>Curve attributes are those for which {@link com.pixelmed.dicom.AttributeTag#isCurveGroup() com.pixelmed.dicom.AttributeTag.isCurveGroup()} returns true.</p>
	 */
	public void removeCurveAttributes() {
		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			Attribute a = i.next();
			if (a.getTag().isCurveGroup()) {
				i.remove();
			}
		}
	}
	
	// Miscellaneous methods ...
	
	/**
	 * <p>Dump the contents of the attribute list as a human-readable string.</p>
	 *
	 * <p>Each attribute is written to a separate line, in the form defined
	 * for {@link com.pixelmed.dicom.Attribute#toString() com.pixelmed.dicom.Attribute.toString()}.</p>
	 *
	 * @return			the string
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		createDictionaryifNecessary();

		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			str.append(((Attribute)i.next()).toString(dictionary));
			str.append("\n");
		}
		return str.toString();
	}

	/**
	 * <p>Dump the contents of the attribute list as a human-readable string.</p>
	 *
	 * <p>Each attribute is written to a separate line, in the form defined
	 * for {@link com.pixelmed.dicom.Attribute#toString(DicomDictionary dictionary) com.pixelmed.dicom.Attribute.toString(DicomDictionary dictionary)}.</p>
	 *
	 * @param	dictionary	the dictionary to use to look up the name
	 * @return			the string
	 */
	public String toString(DicomDictionary dictionary) {
		StringBuffer str = new StringBuffer();

		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			str.append(((Attribute)i.next()).toString(dictionary));
			str.append("\n");
		}
		return str.toString();
	}


	/**
	 * <p>Get all the string values for the attribute list, separated by the specified delimiter.</p>
	 *
	 * <p>If there is no string value for an individual value or an exception trying to fetch it, the supplied default is returned for each Attribute.</p>
	 *
	 * <p>A canonicalized (unpadded) form is returned for each Attribute value, not the original string.</p>
	 *
	 * @param	dflt		what to return if there are no (valid) string values
	 * @param	format		the format to use for each numerical or decimal value (null if none)
	 * @param	delimiter	the delimiter to use between each value
	 * @return				the values as a delimited {@link java.lang.String String}
	 */
	public String getDelimitedStringValuesOrDefault(String dflt,NumberFormat format,String delimiter) {
		StringBuffer str = new StringBuffer();
		String separator = "";
		Iterator<Attribute> i = values().iterator();
		while (i.hasNext()) {
			str.append(separator);
			str.append(((Attribute)i.next()).getDelimitedStringValuesOrDefault(dflt,format));
			separator = delimiter;
		}
		return str.toString();
	}

	/**
	 * <p>Change the file containing the data used by any attribute whose values are left on disk, for example if the file has been renamed.</p>
	 *
	 * @param	file	the new file containing the data
	 */
	public void setFileUsedByOnDiskAttributes(File file) {
//System.err.println("AttributeList.setFileUsedByOnDiskAttributes(): file = "+file);
		for (Attribute a : values()) {
//System.err.println("AttributeList.setFileUsedByOnDiskAttributes(): checking "+a.getClass()+" - "+a.toString(dictionary));
			if (a instanceof OtherByteAttributeOnDisk) {
//System.err.println("AttributeList.setFileUsedByOnDiskAttributes(): setting OtherByteAttributeOnDisk to file = "+file);
				((OtherByteAttributeOnDisk)a).setFile(file);
			}
			else if (a instanceof OtherWordAttributeOnDisk) {
//System.err.println("AttributeList.setFileUsedByOnDiskAttributes(): setting OtherWordAttributeOnDisk to file = "+file);
				((OtherWordAttributeOnDisk)a).setFile(file);
			}
		}
	}

	/**
	 * <p>Create a new attribute with the specified tag and insert it in the map associating the generated attribute with the specified tag as the key.</p>
	 *
	 * <p>If the map previously contained a mapping for the tag (key), the old value is replaced.</p>
	 *
	 * @param	t						key ({@link com.pixelmed.dicom.AttributeTag AttributeTag} tag) with which the generated attribute is to be associated
	 * @param	specificCharacterSet	the {@link com.pixelmed.dicom.SpecificCharacterSet SpecificCharacterSet} to be used text values
	 * @return							the newly created attribute 
	 * @exception	DicomException		if cannot create attribute, such as if cannot find tag in dictionary
	 */
	public Attribute putNewAttribute(AttributeTag t,SpecificCharacterSet specificCharacterSet) throws DicomException {
		Attribute a = null;
		byte[] vr = getDictionary().getValueRepresentationFromTag(t);
		if (vr == null) {
			throw new DicomException("No such data element as "+t+" in dictionary");
		}
		else {
			a = AttributeFactory.newAttribute(t,vr,specificCharacterSet);
			if (a == null) {
				throw new DicomException("Could not create attribute for tag "+t);
			}
			else {
				super.put(t,a);
			}
		}
		return a;
	}

	/**
	 * <p>Create a new attribute with the specified tag and insert it in the map associating the generated attribute with the specified tag as the key.</p>
	 *
	 * <p>If the map previously contained a mapping for the tag (key), the old value is replaced.</p>
	 *
	 * @param	t						key ({@link com.pixelmed.dicom.AttributeTag AttributeTag} tag) with which the generated attribute is to be associated
	 * @return							the newly created attribute 
	 * @exception	DicomException		if cannot create attribute, such as if cannot find tag in dictionary
	 */
	public Attribute putNewAttribute(AttributeTag t) throws DicomException {
		return putNewAttribute(t,null);
	}

	/**
	 * <p>Set the SpecificCharacterSet suitable for all of the string attributes of a dataset in an AttributeList.</p>
	 *
	 * <p>Any existing SpecificCharacterSet for Attributes (whether read or created de novo) is replaced.</p>
	 *
	 * <p>Recurses into SequenceAttributes.</p>
	 */
	public void setSpecificCharacterSet(SpecificCharacterSet specificCharacterSet) {
		if (specificCharacterSet != null) {
			Iterator<Attribute> it = values().iterator();
			while (it.hasNext()) {
				Attribute a = it.next();
				if (a != null) {
					if (a instanceof SequenceAttribute) {
						Iterator<SequenceItem> is = ((SequenceAttribute)a).iterator();
						while (is.hasNext()) {
							SequenceItem item = is.next();
							if (item != null) {
								AttributeList list = item.getAttributeList();
								if (list != null) {
									list.setSpecificCharacterSet(specificCharacterSet);
								}
							}
						}
					}
					else if (a instanceof StringAttributeAffectedBySpecificCharacterSet) {
						((StringAttributeAffectedBySpecificCharacterSet)a).setSpecificCharacterSet(specificCharacterSet);
					}
				}
			}
		}
	}

	/**
	 * <p>Get a new SpecificCharacterSet suitable for encoding all of the values of the string attributes of a dataset.</p>
	 *
	 * <p>Any existing SpecificCharacterSet within the AttributeList or any SpecificCharacterSet established for specific Attributes (e.g., when read or created de novo) is ignored.</p>
	 *
	 * <p>Only a single value is ever used (i.e., ISO 2022 escapes are not created).</p>
	 *
	 * @return	a new SpecificCharacterSet sufficient to encode all values
	 */
	public SpecificCharacterSet getSuitableSpecificCharacterSetForAllStringValues() {
		return new SpecificCharacterSet(this);
	}

	/**
	 * <p>Insert a new SpecificCharacterSet suitable for encoding all of the values of the string attributes of a dataset into the AttributeList.</p>
	 *
	 * <p>Any existing SpecificCharacterSet within the AttributeList or any SpecificCharacterSet established for specific Attributes (e.g., when read or created de novo) is ignored.</p>
	 *
	 * <p>Only a single value is ever used (i.e., ISO 2022 escapes are not created).</p>
	 *
	 * <p>If the encoding is ASCII, which is the default, the SpecificCharacterSet is removed (since it is Type 1C).</p>
	 *
	 * @exception	DicomException		if cannot create attribute
	 */
	public void insertSuitableSpecificCharacterSetForAllStringValues() throws DicomException {
		SpecificCharacterSet specificCharacterSet = getSuitableSpecificCharacterSetForAllStringValues();
//System.err.println("AttributeList.insertSuitableSpecificCharacterSetForAllStringValues(): specificCharacterSet = "+specificCharacterSet);
		if (specificCharacterSet != null) {
			String specificCharacterSetValue = specificCharacterSet.getValueToUseInSpecificCharacterSetAttribute();
//System.err.println("AttributeList.insertSuitableSpecificCharacterSetForAllStringValues(): specificCharacterSetValue = "+specificCharacterSetValue);
			if (specificCharacterSetValue != null && specificCharacterSetValue.length() > 0) {
				putNewAttribute(TagFromName.SpecificCharacterSet).addValue(specificCharacterSetValue);
			}
			else {
				remove(TagFromName.SpecificCharacterSet);
			}
			setSpecificCharacterSet(specificCharacterSet);
		}
	}
	
	protected boolean pixelDataWasDecompressed = false;	// set if decompressed during reading of Pixel Data attribute in this AttributeList instance
	protected boolean pixelDataWasLossy = false;		// set if decompressed from lossy transfer syntax during reading of Pixel Data attribute in this AttributeList instance
	protected String lossyMethod = null;
	protected double compressionRatio = 0;					// zero is an invalid ratio, so is a flag that it has not been set
	
	/**
	 * <p>Update the lossy image compression history attributes if a lossy compressed input transfer syntax was decompress during reading.</p>
	 *
	 * <p> E.g., from YBR_FULL_422 for JPEG lossy to RGB.</p>
	 *
	 * <p>Does nothing (is harmless) if the input was not compressed or not multi-component.</p>
	 *
	 * <p>Recurses into sequences in case there is icon pixel data that was also decompressed.</p>
	 *
	 * @exception	DicomException		if cannot create replacement attribute
	 */
	public void insertLossyImageCompressionHistoryIfDecompressed() throws DicomException {
		if (pixelDataWasDecompressed && pixelDataWasLossy) {
			{ Attribute a = new CodeStringAttribute(TagFromName.LossyImageCompression); a.addValue("01"); put(a); }
			if (lossyMethod != null && lossyMethod.length() > 0) {
				Attribute aLossyImageCompressionMethod = get(TagFromName.LossyImageCompressionMethod);
				if (aLossyImageCompressionMethod == null) {
					aLossyImageCompressionMethod = new CodeStringAttribute(TagFromName.LossyImageCompressionMethod);
					put(aLossyImageCompressionMethod);
				}
				int valueNumberBeingEdited = 0;
				if (aLossyImageCompressionMethod.getVM() == 0) {
					aLossyImageCompressionMethod.addValue(lossyMethod);
					valueNumberBeingEdited = 0;
				}
				else {
					String[] values = aLossyImageCompressionMethod.getStringValues();
					if (values.length > 0 && values[values.length-1] != null && !values[values.length-1].equals(lossyMethod)) {
						valueNumberBeingEdited = values.length;
						aLossyImageCompressionMethod.addValue(lossyMethod);
					}
				}
//System.err.println("AttributeList.correctDecompressedImagePixelModule(): aLossyImageCompressionMethod= "+aLossyImageCompressionMethod);
				
				{
					Attribute aLossyImageCompressionRatio = get(TagFromName.LossyImageCompressionRatio);
					if (aLossyImageCompressionRatio == null) {
						aLossyImageCompressionRatio = new DecimalStringAttribute(TagFromName.LossyImageCompressionRatio);
						put(aLossyImageCompressionRatio);
					}
					if (aLossyImageCompressionRatio.getVM() <= valueNumberBeingEdited) {
						while (--valueNumberBeingEdited > 0) {
							aLossyImageCompressionRatio.addValue("");
						}
						if (compressionRatio > 0) {
							aLossyImageCompressionRatio.addValue(compressionRatio);
						}
						else {
							// don't have a CR to insertm, but need to keep values in sequence
							aLossyImageCompressionRatio.addValue("");
						}
					}
//System.err.println("AttributeList.correctDecompressedImagePixelModule(): aLossyImageCompressionRatio= "+aLossyImageCompressionRatio);
				}
			}
		}
	}
	
	/**
	 * <p>Correct the PhotometricInterpretation and Planar Configuration iff the color space if a compressed input transfer syntax was decompressed during reading.</p>
	 *
	 * <p> E.g., from YBR_FULL_422 for JPEG lossy to RGB.</p>
	 *
	 * <p>Does nothing (is harmless) if the input was not compressed or not multi-component.</p>
	 *
	 * <p>Recurses into sequences in case there is icon pixel data that was also decompressed.</p>
	 *
	 * @exception	DicomException		if cannot create replacement attribute
	 */
	public void correctDecompressedImagePixelModule() throws DicomException {
		if (pixelDataWasDecompressed && Attribute.getSingleIntegerValueOrDefault(this,TagFromName.SamplesPerPixel,0) > 1) {
			String vPhotometricInterpretation = Attribute.getSingleStringValueOrEmptyString(this,TagFromName.PhotometricInterpretation);
			if (vPhotometricInterpretation.equals("YBR_FULL_422")
			 || vPhotometricInterpretation.equals("YBR_FULL")
			 || vPhotometricInterpretation.equals("YBR_PARTIAL_422")
			 || vPhotometricInterpretation.equals("YBR_PARTIAL_420")
			 || vPhotometricInterpretation.equals("YBR_RCT")
			 || vPhotometricInterpretation.equals("YBR_ICT")) {
//System.err.println("AttributeList.correctDecompressedImagePixelModule(): changing PhotometricInterpretation from "+vPhotometricInterpretation+" to RGB");
				Attribute a = new CodeStringAttribute(TagFromName.PhotometricInterpretation); a.addValue("RGB"); put(a);
			}
			{ Attribute a = new UnsignedShortAttribute(TagFromName.PlanarConfiguration); a.addValue(0); put(a); }	// output of JIIO codecs is always interleaved (?) regardless of old header
		}
		Iterator<Attribute> it = values().iterator();
		while (it.hasNext()) {
			Attribute a = it.next();
			if (a != null) {
				if (a instanceof SequenceAttribute) {
					Iterator<SequenceItem> is = ((SequenceAttribute)a).iterator();
					while (is.hasNext()) {
						SequenceItem item = is.next();
						if (item != null) {
							AttributeList list = item.getAttributeList();
							if (list != null) {
								list.correctDecompressedImagePixelModule();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * <p>Construct a text title to describe this list.</p>
	 *
	 * @return			a single line constructed from patient, study, series and instance attribute values
	 */
	public String buildInstanceTitleFromAttributeList() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.PatientName));
		buffer.append("[");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.PatientID));
		buffer.append("]");
		buffer.append(":");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.StudyID));
		buffer.append("[");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.StudyDate));
		buffer.append(":");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.StudyDescription));
		buffer.append("]");
		buffer.append(":");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.SeriesNumber));
		buffer.append("[");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.Modality));
		buffer.append(":");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.SeriesDescription));
		buffer.append("]");
		buffer.append(":");
		buffer.append(Attribute.getSingleStringValueOrEmptyString(this,TagFromName.InstanceNumber));
		buffer.append(":");
		return buffer.toString();
	}

	/**
	 * <p>Construct a text title to describe the specified list.</p>
	 *
	 * @param	list
	 * @return			a single line constructed from patient, study, series and instance attribute values (or empty string if list is null)
	 */
	public static String buildInstanceTitleFromAttributeList(AttributeList list) {
		return list == null ? "" : list.buildInstanceTitleFromAttributeList();
	}

	/**
	 * <p>Find all referenced SOP Instance UIDs that are nested within Sequences in the specified list and add them to the supplied set.</p>
	 *
	 * @param	list
	 * @param	setOfReferencedSOPInstanceUIDs	may be empty or null
	 * @return									the supplied set or a new set if null, with all referenced SOP Instance UIDs found added
	 */
	public static Set<String> findAllNestedReferencedSOPInstanceUIDs(AttributeList list,Set<String> setOfReferencedSOPInstanceUIDs) {
		if (setOfReferencedSOPInstanceUIDs == null) {
			setOfReferencedSOPInstanceUIDs = new HashSet<String>();
		}
		Iterator it = list.values().iterator();
		while (it.hasNext()) {
			Attribute a = (Attribute)it.next();
			if (a != null) {
				if (a instanceof SequenceAttribute) {
					Iterator is = ((SequenceAttribute)a).iterator();
					while (is.hasNext()) {
						SequenceItem item = (SequenceItem)is.next();
						if (item != null) {
							AttributeList itemList = item.getAttributeList();
							if (itemList != null) {
								Iterator itn = itemList.values().iterator();
								while (itn.hasNext()) {
									Attribute an = (Attribute)itn.next();
									if (an != null && an.getTag().equals(TagFromName.ReferencedSOPInstanceUID)) {
										String referencedSOPInstanceUID = an.getSingleStringValueOrEmptyString();
										if (referencedSOPInstanceUID.length() > 0) {
//System.err.println("findAllNestedReferencedSOPInstanceUIDs(): adding "+referencedSOPInstanceUID);
											setOfReferencedSOPInstanceUIDs.add(referencedSOPInstanceUID);
										}
									}
								}
								findAllNestedReferencedSOPInstanceUIDs(itemList,setOfReferencedSOPInstanceUIDs);
							}
						}
					}
				}
				// do NOT look for ReferencedSOPInstanceUID here; only want to find when nested (not in top level dataset, which is invalid DICOM, due to need to workaround buggy RIDER Pilot data, for example)
			}
		}
		return setOfReferencedSOPInstanceUIDs;
	}
	
	/**
	 * <p>Find all referenced SOP Instance UIDs that are nested within Sequences in the specified list.</p>
	 *
	 * @param	list
	 * @return			a set of all referenced SOP Instance UIDs
	 */
	public static Set<String> findAllNestedReferencedSOPInstanceUIDs(AttributeList list) {
		return findAllNestedReferencedSOPInstanceUIDs(list,new HashSet<String>());
	}

	/**
	 * <p>Find all referenced SOP Instance UIDs that are nested within Sequences in this list and add them to the supplied set.</p>
	 *
	 * @param	setOfReferencedSOPInstanceUIDs	may be empty or null
	 * @return									the supplied set or a new set if null, with all referenced SOP Instance UIDs found added
	 */
	public Set<String> findAllNestedReferencedSOPInstanceUIDs(Set<String> setOfReferencedSOPInstanceUIDs) {
		return findAllNestedReferencedSOPInstanceUIDs(this,setOfReferencedSOPInstanceUIDs);
	}
	
	/**
	 * <p>Find all referenced SOP Instance UIDs that are nested within Sequences in this list.</p>
	 *
	 * @return			a set of all referenced SOP Instance UIDs
	 */
	public Set<String> findAllNestedReferencedSOPInstanceUIDs() {
		return findAllNestedReferencedSOPInstanceUIDs(new HashSet<String>());
	}
	
	/**
	 * @param	arg
	 */
	static void test(String arg[]) {

		try {
			AttributeTag tag = new AttributeTag(0x0020,0x000d);
			try {
				Class classToUse = CodeStringAttribute.class;
				Class [] argTypes  = {AttributeTag.class};
				Object[] argValues = {tag};
				Attribute a = (Attribute)(classToUse.getConstructor(argTypes).newInstance(argValues));
System.err.println("made an "+a);
			}
			catch (Exception e) {
				throw new DicomException("Could not instantiate an attribute for "+tag+": "+e);
			}
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		System.err.println("do it buffered, looking for metaheader, no uid specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],null,true,true);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
			
			if (arg.length > 1) {
				//System.err.println("also writing it after removing lengths and meta information header to "+arg[1]);
				//System.err.println("also writing it after removing lengths to "+arg[1]);
				System.err.println("also writing it unchanged to "+arg[1]);
				//list.removeGroupLengthAttributes();			
				//list.removeMetaInformationHeaderAttributes();
				list.write(arg[1],TransferSyntax.ExplicitVRLittleEndian,true,true);
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		System.err.println("do it unbuffered, looking for metaheader, no uid specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],null,true,false);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		System.err.println("do it buffered, looking for metaheader, EVRLE specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],"1.2.840.10008.1.2.1",true,true);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		System.err.println("do it unbuffered, looking for metaheader, EVRLE specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],"1.2.840.10008.1.2.1",true,false);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		System.err.println("do it buffered, no metaheader, no uid specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],null,false,true);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		System.err.println("do it unbuffered, no metaheader, no uid specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],null,false,false);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		System.err.println("do it buffered, no metaheader, IVRLE specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],"1.2.840.10008.1.2",false,true);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		System.err.println("do it unbuffered, no metaheader, IVRLE specified");
		try {
			AttributeList list = new AttributeList();
			System.err.println("test reading list");
			list.read(arg[0],"1.2.840.10008.1.2",false,false);
			System.err.println("test iteration through list");
			System.err.print(list.toString());
			System.err.println("test fetching specific tags");
			System.err.println(list.get(new AttributeTag(0x0020,0x000d)));
			System.err.println(list.get(new AttributeTag(0x0010,0x0010)));
			System.err.println(list.get(new AttributeTag(0x0070,0x0010)));		// won't be there
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * <p>Read the DICOM input file as a list of attributes and write it to the specified output file or dump it.</p>
	 *
	 * <p>When copying, removes any group lengths and creates a new meta information header.</p>
	 *
	 * @param	arg	array of one string (the filename to read and dump),
	 *				two strings (the filename to read and the filename to write),
	 *				three strings (the filename to read, the transfer syntax to write, the filename to write), or
	 *				four strings (the transfer syntax to read (must be zero length if metaheader present), the filename to read, the transfer syntax to write, the filename to write)
	 *				five strings (the transfer syntax to read (must be zero length if metaheader present), the filename to read, the transfer syntax to write, the filename to write, and whether or not to write a metaheader)
	 */
	public static void main(String arg[]) {
		if (arg.length == 3 && arg[2].equals("TEST")) {
			test(arg);
		}
		else if (arg.length > 0) {
			String inputTransferSyntax = null;
			String inputFileName = null;
			String outputTransferSyntax = TransferSyntax.ExplicitVRLittleEndian;
			String outputFileName = null;
			boolean outputMeta = true;
			if (arg.length == 1) {
				inputFileName = arg[0];
			}
			else if (arg.length == 2) {
				inputFileName = arg[0];
				outputFileName = arg[1];
			}
			else if (arg.length == 3) {
				inputFileName = arg[0];
				outputTransferSyntax = TransferSyntaxFromName.getUID(arg[1]);
				outputFileName = arg[2];
			}
			else if (arg.length == 4) {
				inputTransferSyntax = TransferSyntaxFromName.getUID(arg[0]);
				inputFileName = arg[1];
				outputTransferSyntax = TransferSyntaxFromName.getUID(arg[2]);
				outputFileName = arg[3];
			}
			else if (arg.length == 5) {
				inputTransferSyntax = TransferSyntaxFromName.getUID(arg[0]);
				inputFileName = arg[1];
				outputTransferSyntax = TransferSyntaxFromName.getUID(arg[2]);
				outputFileName = arg[3];
				outputMeta = ! arg[4].toUpperCase(java.util.Locale.US).contains("NO");
			}
			try {
				AttributeList list = new AttributeList();
//long startReadTime = System.currentTimeMillis();
				list.read(inputFileName,inputTransferSyntax,inputTransferSyntax == null || inputTransferSyntax.length() == 0/*tryMeta*/,true);
//System.err.println("AttributeList.main(): read - done in "+(System.currentTimeMillis()-startReadTime)+" ms");
				if (outputFileName == null) {
					System.err.print("Dumping ...");
					System.err.print(list.toString());
				}
				else {
					list.removeGroupLengthAttributes();			
					list.removeMetaInformationHeaderAttributes();
					list.remove(TagFromName.DataSetTrailingPadding);
					list.correctDecompressedImagePixelModule();
					// would be nice to check here for an additional arg that was a replacement character set
					FileMetaInformation.addFileMetaInformation(list,outputTransferSyntax,"OURAETITLE");
//long startWriteTime = System.currentTimeMillis();
					list.write(outputFileName,outputTransferSyntax,outputMeta,true);
//System.err.println("AttributeList.main(): write - done in "+(System.currentTimeMillis()-startWriteTime)+" ms");
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}
}



