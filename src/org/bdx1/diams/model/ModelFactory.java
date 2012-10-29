package org.bdx1.diams.model;

import java.io.File;

public class ModelFactory {

    public static Slice makeSlice(File source) {
        return new BaseSlice(source);
    }
    
}
