package org.bdx1.diams.model;

import java.io.File;

public interface ModelFactory {

    public Slice makeSlice(File source);

    public Examen makeExamen(File studyFile);

}