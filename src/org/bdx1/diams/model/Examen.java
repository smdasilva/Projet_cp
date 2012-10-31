package org.bdx1.diams.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;

/**
 * This represents a medical examen.
 * It contains images of the study
 * and informations about the patient and the study.
 */
public class Examen {

    private Map<String,String> patientInfos;
    private Map<String, String> studyInfos;
    private SliceManager sliceManager;

    /**
     * Constructs an Examen
     * @param studyFile The first File of the study
     * @param manager Defines how to handle Slices
     */
    public Examen(File studyFile, SliceManager manager) {
        sliceManager = manager;
        InformationProvider infosProv = InformationProviderManager.getDicomProvider();
        infosProv.read(studyFile);
        patientInfos = infosProv.getPatientInfos();
        studyInfos = infosProv.getStudyInfos();
        addSlice(studyFile);
    }

    /**
     * Returns a Map of informations about the patient of the study
     * @return
     */
    public Map<String, String> getPatientInfos() {
        return Collections.unmodifiableMap(patientInfos);
    }
    
    /**
     * Returns a Map of informations about the study
     * @return
     */
    public Map<String, String> getStudyInfos() {
        return Collections.unmodifiableMap(studyInfos);
    }
    
    /**
     * @return The number of slices in this examen
     */
    public int getNumberOfSlices() {
        return sliceManager.numberOfSlices();
    }
    
    /**
     * Adds a slice to this examen via the SliceManager
     * @param sliceFile The file of the Slice
     */
    public void addSlice(File sliceFile) {
        sliceManager.addSlice(sliceFile);
    }

    /**
     * 
     * @param i The index of the Slice
     * @return The found Slice
     */
    public Slice getSlice(int i) {
        return sliceManager.getSlice(i);
    }
}
