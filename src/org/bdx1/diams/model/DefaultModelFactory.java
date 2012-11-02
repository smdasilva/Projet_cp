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
        return new Examen(studyFile, new ListSliceManager());
    }

    public static Image makeImage(File dicom) {
        ImageProvider prov = ImageProviderManager.getDicomProvider();
        if (!prov.read(dicom))
            return null;
        return prov.getImage();
    }
}
