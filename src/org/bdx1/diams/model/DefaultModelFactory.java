package org.bdx1.diams.model;

import java.io.File;

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
}
