package org.bdx1.diams.model;

import java.io.File;

/**
 * This class is responsible for managing Slices.
 * It knows slice order, number and can furnish Slice from an index.
 */
public interface SliceManager {

    /**
     * Adds the Slice specified by source at the end
     * of the collection of slices.
     * @param source The source file from which to load the slice
     */
    public void addSlice(File source);
    
    /**
     * Returns the Slice identified by index i.
     * @param i The index of the Slice
     * @return The specified Slice
     */
    public Slice getSlice(int i);
    
    /**
     * Returns the number of Slices in this manager
     * @return Number of Slices this manager contains
     */
    public int numberOfSlices();
}
