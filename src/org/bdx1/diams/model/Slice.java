package org.bdx1.diams.model;

import java.util.Map;

/**
 * Interface for a Slice.
 * Defines operations on a Slice object.
 * Represents a part of an Examen.
 */
public interface Slice {

    /**
     * Function to retrieve informations about the Slice.
     * @return A Map containing all slice informations.
     */
    public Map<String, String> getInfos();

}