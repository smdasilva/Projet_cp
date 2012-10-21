package org.bdx1.diams.parsing;


enum DicomTags {
    PatientName("Patient Name"),
    PatientID("Patient ID"),
    PatientBirthDate("Patient birthday"),
    PatientAge("Patient age"),
    PatientSex("Patient sex"),
    PatientSize("Patient size"),
    PatientWeight("Patient weight"),
    ImagePositionPatient("Image position patient"),
    ImageOrientationPatient("Image orientation patient"),
    StudyID("Study data"),
    StudyDate("Study date"),
    StudyTime("Study time"),
    StudyDescription("Study description"),
    BitsAllocated("Bits allocated"),
    BitsStored("Bits stored"),
    HighBit("High bit"),
    RescaleIntercept("Intercept"),
    RescaleSlope("Slope"),
    WindowCenter("Window center"),
    WindowWidth("Window width"),
    SliceThickness("Slice thickness"),
    WindowCenterWidthExplanation("WindowCenterWidthExplanation");

//    private final AttributeTag tag;
    private final String name;

    DicomTags(String name) {
        this.name = name;
    }

//    AttributeTag getTag() {
//        return tag;
//    }

    String getName() {
        return name;
    }
}