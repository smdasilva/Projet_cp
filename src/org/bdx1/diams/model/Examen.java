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
    private List<File> sliceFiles = new LinkedList<File>();
    private SliceCacher cache = new SliceCacher(5);

    public Examen(File studyFile) {
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
        return sliceFiles.size();
    }
    
    public void addSlice(File sliceFile) {
        sliceFiles.add(sliceFile);
    }

    public Slice getSlice(int i) {
        return cache.getSlice(sliceFiles.get(i));
    }
}