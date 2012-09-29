package util.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;

public class DicomParser implements Parser {

    private String extension;
    
    public DicomParser() {
	extension = "dcm";
    }
    
    @Override
    public String getExtension() {
	return extension;
    }

    @Override
    public void loadFile(String filename, Examen exam) {
	// reading dicom object from file
	DicomObject dicom;
	DicomInputStream stream = null;
	try {
	    stream = new DicomInputStream(new File(filename));
	    dicom = stream.readDicomObject();
	} catch (IOException io) {
	    return;
	} finally {
	    try {
	        stream.close();
	    }
	    catch (IOException ignore) {
	    }
	}
	
	boolean firstFlag = false;
	
	// reading informations
	Information generalInfos = exam.getInformations();
	if (generalInfos.getSize() == 0) {
	    firstFlag = true;
	}
    }

    /** Not supported */
    @Override
    public boolean saveFile(String filename, Examen exam, List<Boolean> options) {
	return false;
    }

}
