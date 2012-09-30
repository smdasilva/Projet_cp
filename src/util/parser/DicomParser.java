package util.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;

public class DicomParser implements Parser {

    private enum DicomTags {
        PatientName("Patient Name", Tag.PatientName),
        PatientID("Patient ID", Tag.PatientID),
        PatientBirthDate("Patient birthday", Tag.PatientBirthDate),
        PatientAge("Patient age", Tag.PatientAge),
        PatientSex("Patient sex", Tag.PatientSex),
        PatientSize("Patient size", Tag.PatientSize),
        PatientWeight("Patient weight", Tag.PatientWeight),
        ImagePositionPatient("Image position patient", Tag.ImagePositionPatient),
        ImageOrientationPatient("Image orientation patient", Tag.ImageOrientationPatient),
        StudyID("Study data", Tag.StudyID),
        StudyDate("Study date", Tag.StudyDate),
        StudyTime("Study time", Tag.StudyTime),
        StudyDescription("Study description", Tag.StudyDescription),
        BitsAllocated("Bits allocated", Tag.BitsAllocated),
        BitsStored("Bits stored", Tag.BitsStored),
        HighBit("High bit", Tag.HighBit),
        RescaleIntercept("Intercept", Tag.RescaleIntercept),
        RescaleSlope("Slope", Tag.RescaleSlope),
        WindowCenter("Window center", Tag.WindowCenter),
        WindowWidth("Window width", Tag.WindowWidth),
        SliceThickness("Slice thickness", Tag.SliceThickness),
        WindowCenterWidthExplanation("WindowCenterWidthExplanation", Tag.WindowCenterWidthExplanation);
        
        private final int tag;
        private final String name;
        
        DicomTags(String name, int tag) {
            this.name = name;
            this.tag = tag;
        }
        
        int getTag() {
            return tag;
        }
        
        String getName() {
            return name;
        }
    }
    
    private final String extension;

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
        
        for (DicomTags tag : DicomTags.values()) {
            generalInfos.addInformation(tag.getName(), dicom.getString(tag.getTag()));
        }
    }

    /** Not supported */
    @Override
    public boolean saveFile(String filename, Examen exam, List<Boolean> options) {
        return false;
    }

}
