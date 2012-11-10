package org.bdx1.diams.model;

import java.io.File;

import org.bdx1.diams.parsing.ImageProvider;
import org.bdx1.diams.parsing.ImageProviderManager;

/**
 * Default implementation for ModelFactory.
 * Uses only classes from the model package
 */
public class DefaultModelFactory implements ModelFactory {

    public Slice makeSlice(File source) {
        return new BaseSlice(source);
    }
    
    public Examen makeExamen(File studyFile) {
        if (studyFile.isDirectory()) {
            Examen exam = null;
            for (File fils : studyFile.listFiles()) {
                if (fils.isFile() && fils.getName().endsWith(".dcm")) {
                    if (exam == null)
                        exam = new Examen(fils, makeSliceManager());
                    exam.addSlice(fils);
                }
            }
            return exam;
        } else { 
            return new Examen(studyFile, makeSliceManager());
        }
    }

    public static Image makeImage(File dicom) {
        ImageProvider prov = ImageProviderManager.getDicomProvider();
        if (!prov.read(dicom))
            return null;
        return prov.getImage();
    }
    
    protected SliceManager makeSliceManager() {
        return new ListSliceManager();
    }
}
