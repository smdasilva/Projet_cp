package org.bdx1.diams.caching;

import java.io.File;

import org.bdx1.diams.model.DefaultModelFactory;
import org.bdx1.diams.model.Examen;
import org.bdx1.diams.model.ModelFactory;
import org.bdx1.diams.model.Slice;

/**
 * This class creates instances of model classes
 * and allows them to use caching functionalities.
 */
public class CachingModelFactory implements ModelFactory {

    private ModelFactory factory = new DefaultModelFactory();
    
    public Slice makeSlice(File source) {
        return factory.makeSlice(source);
    }

    public Examen makeExamen(File studyFile) {
        return new Examen(studyFile, new CachingSliceManager());
    }

}
