package org.bdx1.diams.parsing;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdx1.dicom.DICOMException;
import org.bdx1.dicom.data.DICOMBody;
import org.bdx1.dicom.file.DICOMImageReader;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;

public class DicomInfosProvider implements InformationProvider {

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

    private static final String NOT_AVAILABLE = "Not Available";
    private DICOMBody body;
    
    DicomInfosProvider() {
    }

    public Map<String, String> getPatientInfos() {
//        Map<String, String> infos = new HashMap<String, String>();
//        infos.put(DicomTags.PatientName.getName(), body.getPatientName());
//        infos.put(DicomTags.PatientAge.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.PatientBirthDate.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.PatientID.getName(), body.getPatientId());
//        infos.put(DicomTags.PatientSex.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.PatientSize.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.PatientWeight.getName(), NOT_AVAILABLE);
//        return infos;
        return getMapOfTags(patientTags);
    }

    public Map<String, String> getSliceInfos() {
//        Map<String, String> infos = new HashMap<String, String>();
//        infos.put(DicomTags.BitsAllocated.getName(), Integer.toString(body.getBitsAllocated()));
//        infos.put(DicomTags.BitsStored.getName(), Integer.toString(body.getBitsStored()));
//        infos.put(DicomTags.HighBit.getName(), Integer.toString(body.getHightBit()));
//        infos.put(DicomTags.RescaleIntercept.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.RescaleSlope.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.SliceThickness.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.WindowCenter.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.WindowWidth.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.WindowCenterWidthExplanation.getName(), NOT_AVAILABLE);
//        return infos;
        return getMapOfTags(sliceTags);
    }

    public boolean read(File target) {
//        DICOMImageReader reader = null;
//        try {
//            reader = new DICOMImageReader(target);
//        } catch (FileNotFoundException e) {
//            return false;
//        }
//        try {
//            body = reader.parse().getBody();
//        } catch (EOFException e) {
//            return false;
//        } catch (IOException e) {
//            return false;
//        } catch (DICOMException e) {
//            return false;
//        }
//        return true;
        try {
            attributes.read(target);
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
//        Map<String, String> infos = new HashMap<String, String>();
//        infos.put(DicomTags.StudyID.getName(), Integer.toString(body.getStudyId()));
//        infos.put(DicomTags.StudyDate.getName(), body.getStudyDate());
//        infos.put(DicomTags.StudyDescription.getName(), body.getStudyDescription());
//        infos.put(DicomTags.StudyTime.getName(), body.getStudyTime());
//        infos.put(DicomTags.ImageOrientationPatient.getName(), NOT_AVAILABLE);
//        infos.put(DicomTags.ImagePositionPatient.getName(), NOT_AVAILABLE);
//        return infos;
        return getMapOfTags(studyTags);
    }
}
