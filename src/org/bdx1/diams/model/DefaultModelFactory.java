package org.bdx1.diams.model;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bdx1.dicom.DICOMException;
import org.bdx1.dicom.data.DICOMImage;
import org.bdx1.dicom.file.DICOMImageReader;

/**
 * Default implementation for ModelFactory.
 * Uses only classes from the model package
 */
public class DefaultModelFactory implements ModelFactory {

    public Slice makeSlice(File source) {
        return new BaseSlice(source);
    }
    
    public Examen makeExamen(File studyFile) {
        return new Examen(studyFile, new ListSliceManager());
    }

    public static Image makeImage(File dicom) {
        DICOMImageReader reader;
        try {
            reader = new DICOMImageReader(dicom);
        } catch (FileNotFoundException e) {
            return null;
        }
        DICOMImage img;
        try {
            img = reader.parse();
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (DICOMException e) {
            return null;
        }
        return new LisaImageAdapter(img.getImage());
    }
}
