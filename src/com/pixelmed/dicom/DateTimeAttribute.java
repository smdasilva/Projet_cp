/* Copyright (c) 2001-2012, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.dicom;

import java.io.*;

/**
 * <p>A concrete class specializing {@link com.pixelmed.dicom.Attribute Attribute} for
 * Date Time (DT) attributes.</p>
 *
 * <p>Though an instance of this class may be created
 * using its constructors, there is also a factory class, {@link com.pixelmed.dicom.AttributeFactory AttributeFactory}.</p>
 *
 * @see com.pixelmed.dicom.Attribute
 * @see com.pixelmed.dicom.AttributeFactory
 * @see com.pixelmed.dicom.AttributeList
 *
 * @author	dclunie
 */
public class DateTimeAttribute extends StringAttribute {

	private static final String identString = "@(#) $Header: /userland/cvs/pixelmed/imgbook/com/pixelmed/dicom/DateTimeAttribute.java,v 1.11 2012/09/28 21:07:31 dclunie Exp $";

	/**
	 * <p>Construct an (empty) attribute.</p>
	 *
	 * @param	t	the tag of the attribute
	 */
	public DateTimeAttribute(AttributeTag t) {
		super(t);
	}

	/**
	 * <p>Read an attribute from an input stream.</p>
	 *
	 * @param	t			the tag of the attribute
	 * @param	vl			the value length of the attribute
	 * @param	i			the input stream
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public DateTimeAttribute(AttributeTag t,long vl,DicomInputStream i) throws IOException, DicomException {
		super(t,vl,i);
	}

	/**
	 * <p>Read an attribute from an input stream.</p>
	 *
	 * @param	t			the tag of the attribute
	 * @param	vl			the value length of the attribute
	 * @param	i			the input stream
	 * @exception	IOException
	 * @exception	DicomException
	 */
	public DateTimeAttribute(AttributeTag t,Long vl,DicomInputStream i) throws IOException, DicomException {
		super(t,vl,i);
	}

	/**
	 * <p>Get the value representation of this attribute (DT).</p>
	 *
	 * @return	'D','T' in ASCII as a two byte array; see {@link com.pixelmed.dicom.ValueRepresentation ValueRepresentation}
	 */
	public byte[] getVR() { return ValueRepresentation.DT; }
	
	/**
	 * <p>Get a DICOM format DT <code>String</code> value from a Java <code>Date</code>.</p>
	 *
	 * <p>Will format the Date for the UTC timezone, converting from whatever timezone is specified in the supplied <code>Date</code> if not UTC.</p>
	 *
	 * @param	date			the Java <code>Date</code> to format
	 * @param	timezone		the Java <code>TimeZone</code> to use
	 * @return					a DICOM formatted DT value
	 */
	public static String getFormattedString(java.util.Date date,java.util.TimeZone timezone) {
		java.text.SimpleDateFormat dateFormatterOutput = new java.text.SimpleDateFormat("yyyyMMddHHmmss.SSSZ");
		dateFormatterOutput.setTimeZone(timezone);
		return dateFormatterOutput.format(date);
	}

	
	/**
	 * <p>Get a DICOM format DT <code>String</code> value from a Java <code>Date</code>.</p>
	 *
	 * <p>Will format the Date for the UTC timezone, converting from whatever timezone is specified in the supplied <code>Date</code> if not UTC.</p>
	 *
	 * @param	date			the Java <code>Date</code> to format
	 * @return					a DICOM formatted DT value
	 */
	public static String getFormattedStringUTC(java.util.Date date) {
		return getFormattedString(date,java.util.TimeZone.getTimeZone("GMT"));
	}

	/**
	 * <p>Get a DICOM format DT <code>String</code> value from a Java <code>Date</code>.</p>
	 *
	 * <p>Will format the Date for the default timezone, converting from whatever timezone is specified in the supplied <code>Date</code> if not the default.</p>
	 *
	 * @param	date			the Java <code>Date</code> to format
	 * @return					a DICOM formatted DT value
	 */
	public static String getFormattedStringDefaultTimeZone(java.util.Date date) {
		return getFormattedString(date,java.util.TimeZone.getDefault());
	}

	/**
	 * <p>Get a DICOM format DT <code>String</code> value from a Java <code>Date</code>.</p>
	 *
	 * <p>Will format the Date for the default timezone, converting from whatever timezone is specified in the supplied <code>Date</code> if not the default.</p>
	 *
	 * @deprecated	use {@link #getFormattedStringDefaultTimeZone(Date) getFormattedStringDefaultTimeZone()} instead
	 *
	 * @param	date			the Java <code>Date</code> to format
	 * @return					a DICOM formatted DT value
	 */
	public static String getFormattedString(java.util.Date date) {
		return getFormattedStringDefaultTimeZone(date);
	}
	
	/**
	 * <p>Get a Java <code>Date</code> from a DICOM format DT <code>String</code> value.</p>
	 *
	 * @param		dateString	the date to parse
	 * @return					a Java <code>Date</code>
	 * @exception				java.text.ParseException
	 */
	public static java.util.Date getDateFromFormattedString(String dateString) throws java.text.ParseException {
//System.err.println("DateTimeAttribute.getDateFromFormattedString(): given "+dateString);
		int l = dateString.length();
		java.util.Date date = null;
		StringBuffer format = new StringBuffer();
		boolean sawTimeZone = false;
		int p = 0;
		int fractionalDigitsStart = 0;
		int fractionalDigitsCount = 0;
		if (l >= 4) {
			format.append("yyyy");
			p = 4;
			if (l >= 6 && Character.isDigit(dateString.charAt(p))) {
				format.append("MM");
				p = 6;
				if (l >= 8 && Character.isDigit(dateString.charAt(p))) {
					format.append("dd");
					p = 8;
					if (l >= 10 && Character.isDigit(dateString.charAt(p))) {
						format.append("HH");
						p = 10;
						if (l >= 12 && Character.isDigit(dateString.charAt(p))) {
							format.append("mm");
							p = 12;
							if (l >= 14 && Character.isDigit(dateString.charAt(p))) {
								format.append("ss");
								p = 14;
								if (l > 14) {
									if (dateString.charAt(p) == '.') {
										// have a fraction
										format.append(".");
										while (++p < l && Character.isDigit(dateString.charAt(p))) {
											++fractionalDigitsCount;
											if (fractionalDigitsStart == 0) {
												fractionalDigitsStart = p;
											}
										}
										if (fractionalDigitsCount > 0) {
											format.append("SSS");	// java.util.Date is number of milliseconds, not a fraction at all ... so we will always send three (no more, no less)
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (p < l) {
			int c = dateString.charAt(p);
			if (c == '+' || c == '-') {
				// have a timezone
				format.append("Z");
				sawTimeZone = true;
			}
		}
		
		// would be better to round rather than truncate to milliseconds (which is all Java supports in Date and Calendar), but this is expedient :(
		if (fractionalDigitsCount > 0) {
			if (fractionalDigitsCount < 3) {
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): fractionalDigits lengthen before = "+dateString);
				StringBuffer trailingZeroPadding = new StringBuffer();
				for (int i=fractionalDigitsCount; i<3; ++i) {
					trailingZeroPadding.append("0");
				}
				dateString = dateString.substring(0,fractionalDigitsStart+fractionalDigitsCount) + trailingZeroPadding.toString() + dateString.substring(fractionalDigitsStart+fractionalDigitsCount);
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): fractionalDigits lengthen after = "+dateString);
			}
			else if (fractionalDigitsCount > 3) {
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): fractionalDigits truncate before = "+dateString);
				dateString = dateString.substring(0,fractionalDigitsStart+3) + dateString.substring(fractionalDigitsStart+fractionalDigitsCount);
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): fractionalDigits truncate after = "+dateString);
			}
			// else was exactly three, which is OK
		}
		
		if (!sawTimeZone) {
			// assume UTC, else behavior of Date will depend on application local time zone, which we do not want
			format.append("Z");
			dateString = dateString + "+0000";
		}
		
		String formatString = format.toString();
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): formatString = "+formatString);
		java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(formatString);
		date = dateFormatter.parse(dateString);
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): given "+dateString+" return "+date.toGMTString());
		return date;
	}
	
	/**
	 * <p>Get a Java <code>Date</code> from a DICOM format DT <code>String</code> value.</p>
	 *
	 * <p>Will use the TimezoneOffsetFromUTC if present in the AttributeList, else will assume UTC (not whatever the local time zone happens to be). </p>
	 *
	 * @param		list		the list containing the attributes
	 * @param		dateTag		the tag of the DA attribute
	 * @param		timeTag		the tag of the TM attribute
	 * @return					a Java <code>Date</code>
	 * @exception				java.text.ParseException
	 */
	public static java.util.Date getDateFromFormattedString(AttributeList list,AttributeTag dateTag,AttributeTag timeTag) throws java.text.ParseException {
		return getDateFromFormattedString(
				Attribute.getSingleStringValueOrEmptyString(list,dateTag)
			  + Attribute.getSingleStringValueOrEmptyString(list,timeTag)
			  + Attribute.getSingleStringValueOrDefault(list,TagFromName.TimezoneOffsetFromUTC,"+0000")
			);
	}
	
	/**
	 * <p>Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by the DT value. </p>
	 *
	 * @param		dateTime	the string to parse
	 * @return					the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this date
	 * @exception				java.text.ParseException
	 */
	public static long getTimeInMilliSecondsSinceEpoch(String dateTime) throws java.text.ParseException {
		java.util.Date date = getDateFromFormattedString(dateTime);
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): given "+dateTime+" Date  is "+date.toGMTString());
		long time = date.getTime();
		//long time = dateFormatterParseWithoutTimeZoneOrFraction.parse(dateTime).getTime();
//System.err.println("DateTimeAttribute.getTimeInMilliSecondsSinceEpoch(): given "+dateTime+" return "+time);
		return time;
	}
	
	/**
	 * <p>Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by the combination of the DA and TM values of the specified pair of attributes. </p>
	 *
	 * <p>Will use the TimezoneOffsetFromUTC if present in the AttributeList, else will assume UTC (not whatever the local time zone happens to be). </p>
	 *
	 * @param		list		the list containing the attributes
	 * @param		dateTag		the tag of the DA attribute
	 * @param		timeTag		the tag of the TM attribute
	 * @return					the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this date
	 * @exception				java.text.ParseException
	 */
	public static long getTimeInMilliSecondsSinceEpoch(AttributeList list,AttributeTag dateTag,AttributeTag timeTag) throws java.text.ParseException {
		return getTimeInMilliSecondsSinceEpoch(
				Attribute.getSingleStringValueOrEmptyString(list,dateTag)
			  + Attribute.getSingleStringValueOrEmptyString(list,timeTag)
			  + Attribute.getSingleStringValueOrDefault(list,TagFromName.TimezoneOffsetFromUTC,"+0000")
			);
	}
}

