package org.bdx1.diams.model;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;

public class Examen {

    private Map<String,String> patientInfos;
    private Map<String, String> studyInfos;
    private SliceManager sliceManager;

    public Examen(File studyFile, SliceManager manager) {
        sliceManager = manager;
        InformationProvider infosProv = InformationProviderManager.getDicomProvider();
        infosProv.read(studyFile);
        patientInfos = infosProv.getPatientInfos();
        studyInfos = infosProv.getStudyInfos();
        addSlice(studyFile);
    }

    public Map<String, String> getPatientInfos() {
        return Collections.unmodifiableMap(patientInfos);
    }
    
    public Map<String, String> getStudyInfos() {
        return Collections.unmodifiableMap(studyInfos);
    }
    
    public int getNumberOfSlices() {
        return sliceManager.numberOfSlices();
    }
    
    public void addSlice(File sliceFile) {
        sliceManager.addSlice(sliceFile);
    }

    public Slice getSlice(int i) {
        return sliceManager.getSlice(i);
    }
}
