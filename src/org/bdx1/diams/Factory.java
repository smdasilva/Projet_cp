package org.bdx1.diams;

import org.bdx1.diams.caching.CachingModelFactory;
import org.bdx1.diams.model.ModelFactory;

public class Factory {

    public static final ModelFactory MODEL_FACTORY = new CachingModelFactory();
    
}
