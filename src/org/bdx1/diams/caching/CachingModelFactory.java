package org.bdx1.diams.caching;

import java.io.File;

import org.bdx1.diams.model.DefaultModelFactory;
import org.bdx1.diams.model.Examen;
import org.bdx1.diams.model.ModelFactory;
import org.bdx1.diams.model.Slice;
import org.bdx1.diams.model.SliceManager;

/**
 * This class creates instances of model classes
 * and allows them to use caching functionalities.
 */
public class CachingModelFactory extends DefaultModelFactory {

    private ModelFactory factory = new DefaultModelFactory();
    
    public Slice makeSlice(File source) {
        return factory.makeSlice(source);
    }

    @Override
    protected SliceManager makeSliceManager() {
        return new CachingSliceManager();
    }

    

}
