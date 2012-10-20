package org.bdx1.diams.parsing;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;

class DicomInfosProvider implements InformationProvider {

    private static final List<DicomTags> patientTags =
            Arrays.asList(DicomTags.PatientName, DicomTags.PatientAge, DicomTags.PatientID,
                    DicomTags.PatientBirthDate, DicomTags.PatientSex, DicomTags.PatientSize,
                    DicomTags.PatientWeight);
    
    private static final List<DicomTags> studyTags =
            Arrays.asList(DicomTags.StudyDate, DicomTags.StudyDescription,
                    DicomTags.StudyID, DicomTags.StudyTime,
                    DicomTags.ImageOrientationPatient, DicomTags.ImagePositionPatient);
    
    private static final List<DicomTags> sliceTags = 
            Arrays.asList(DicomTags.BitsAllocated, DicomTags.BitsStored, DicomTags.HighBit,
                    DicomTags.RescaleIntercept, DicomTags.RescaleSlope, DicomTags.SliceThickness,
                    DicomTags.WindowCenter, DicomTags.WindowWidth,
                    DicomTags.WindowCenterWidthExplanation);

    private AttributeList attributes = new AttributeList();

    DicomInfosProvider() {
    }

    public Map<String, String> getPatientInfos() {
        return getMapOfTags(patientTags);
    }

    public Map<String, String> getSliceInfos() {
        return getMapOfTags(sliceTags);
    }

    public boolean read(File target) {
        try {
            this.attributes.read(target);
        } catch (IOException e) {
            return false;
        } catch (DicomException e) {
            return false;
        }
        return true;
    }

    private Map<String,String> getMapOfTags(List<DicomTags> list) {
        Map<String, String> map = new HashMap<String, String>();

        for (DicomTags tag : list) {
            Attribute value = attributes.get(tag.getTag());
            if (value != null) {
                String stringValue = value.getSingleStringValueOrNull();
                map.put(tag.getName(), stringValue);
            } else {
                map.put(tag.getName(), "null");
            }
        }

        return map;
    }

    public Map<String, String> getStudyInfos() {
        return getMapOfTags(studyTags);
    }
}
