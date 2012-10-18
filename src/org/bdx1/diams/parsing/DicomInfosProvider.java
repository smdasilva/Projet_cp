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

public class DicomInfosProvider implements InformationProvider {

    private static List<DicomTags> patientTags =
            Arrays.asList(DicomTags.PatientName, DicomTags.PatientAge, DicomTags.PatientID,
                    DicomTags.PatientBirthDate, DicomTags.PatientSex, DicomTags.PatientSize,
                    DicomTags.PatientWeight);

    private AttributeList attributes = new AttributeList();

    DicomInfosProvider() {
    }

    public Map<String, String> getPatientInfos() {
        Map<String, String> map = new HashMap<String, String>();

        for (DicomTags tag : patientTags) {
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

    public Map<String, String> getSliceInfos() {
        return null;
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

}
