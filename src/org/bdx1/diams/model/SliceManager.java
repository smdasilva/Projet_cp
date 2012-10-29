package org.bdx1.diams.model;

import java.io.File;

public interface SliceManager {

    public void addSlice(File source);
    
    public Slice getSlice(int i);
    
    public int numberOfSlices();
}
