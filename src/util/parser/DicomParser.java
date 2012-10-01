package util.parser;

import java.io.File;
import java.util.List;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;

public class DicomParser implements Parser {

    private enum DicomTags {
        PatientName("Patient Name", TagFromName.PatientName),
        PatientID("Patient ID", TagFromName.PatientID),
        PatientBirthDate("Patient birthday", TagFromName.PatientBirthDate),
        PatientAge("Patient age", TagFromName.PatientAge),
        PatientSex("Patient sex", TagFromName.PatientSex),
        PatientSize("Patient size", TagFromName.PatientSize),
        PatientWeight("Patient weight", TagFromName.PatientWeight),
        ImagePositionPatient("Image position patient", TagFromName.ImagePositionPatient),
        ImageOrientationPatient("Image orientation patient", TagFromName.ImageOrientationPatient),
        StudyID("Study data", TagFromName.StudyID),
        StudyDate("Study date", TagFromName.StudyDate),
        StudyTime("Study time", TagFromName.StudyTime),
        StudyDescription("Study description", TagFromName.StudyDescription),
        BitsAllocated("Bits allocated", TagFromName.BitsAllocated),
        BitsStored("Bits stored", TagFromName.BitsStored),
        HighBit("High bit", TagFromName.HighBit),
        RescaleIntercept("Intercept", TagFromName.RescaleIntercept),
        RescaleSlope("Slope", TagFromName.RescaleSlope),
        WindowCenter("Window center", TagFromName.WindowCenter),
        WindowWidth("Window width", TagFromName.WindowWidth),
        SliceThickness("Slice thickness", TagFromName.SliceThickness),
        WindowCenterWidthExplanation("WindowCenterWidthExplanation", TagFromName.WindowCenterWidthExplanation);

        private final AttributeTag tag;
        private final String name;

        DicomTags(String name, AttributeTag tag) {
            this.name = name;
            this.tag = tag;
        }

        AttributeTag getTag() {
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
        File dicomFile = new File(filename);
        Information generalInfos = exam.getInformations();
        boolean firstFlag = false;

        // reading dicom Attributes from file
        AttributeList list = new AttributeList();
        list.read(dicomFile);
        if (generalInfos.size() == 0) {
            firstFlag = true;
            for (DicomTags tag : DicomTags.values()) {
                Attribute attr = list.get(tag.getTag());
                if (attr != null) {
                    StringBuilder buffer = new StringBuilder();
                    for (String s : attr.getStringValues()) {
                        buffer.append(s);
                    }
                    generalInfos.addInformations(tag.getName(), buffer.toString());
                } else {
                    generalInfos.addInformation(tag.getName(), "null");
                }
            }
        }
    }

    /** Not supported */
    @Override
    public boolean saveFile(String filename, Examen exam, List<Boolean> options) {
        return false;
    }

}
