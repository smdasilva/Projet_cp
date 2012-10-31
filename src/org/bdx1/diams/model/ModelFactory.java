package org.bdx1.diams.model;

import java.io.File;

/**
 * This the interface for all factories
 * for model objects.
 */
public interface ModelFactory {

    /**
     * Builds a Slice from the specified File
     * @param source The source File for the Slice
     * @return The constructed Slice
     */
    public Slice makeSlice(File source);

    /**
     * Builds an Examen starting with the specified studyFile
     * @param studyFile The first file of the examen
     * @return The constructed Examen
     */
    public Examen makeExamen(File studyFile);

}