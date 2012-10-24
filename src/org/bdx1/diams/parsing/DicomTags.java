package org.bdx1.diams.parsing;

import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;


enum DicomTags {
    PatientName("Patient Name",TagFromName.PatientName),
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