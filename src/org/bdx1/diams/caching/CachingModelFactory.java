package org.bdx1.diams.caching;

import org.bdx1.diams.model.DefaultModelFactory;
import org.bdx1.diams.model.SliceManager;

/**
 * This class creates instances of model classes
 * and allows them to use caching functionalities.
 */
public class CachingModelFactory extends DefaultModelFactory {

    @Override
    protected SliceManager makeSliceManager() {
        return new CachingSliceManager();
    }

}
