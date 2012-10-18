package org.bdx1.diams.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;

public class Examen {

    private Map<String,String> patientInfos;
    private Map<String, String> studyInfos;

    public Examen(File studyFile) {
        InformationProvider infosProv = InformationProviderManager.getDicomProvider();
        infosProv.read(studyFile);
        patientInfos = infosProv.getPatientInfos();
        studyInfos = infosProv.getStudyInfos();
    }

    public Map<String, String> getPatientInfos() {
        return Collections.unmodifiableMap(patientInfos);
    }
    
    public Map<String, String> getStudyInfos() {
        return Collections.unmodifiableMap(studyInfos);
    }
}
