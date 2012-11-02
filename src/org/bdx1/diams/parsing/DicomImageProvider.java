package org.bdx1.diams.parsing;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bdx1.diams.model.Image;
import org.bdx1.diams.model.LisaImageAdapter;
import org.bdx1.dicom.DICOMException;
import org.bdx1.dicom.data.DICOMImage;
import org.bdx1.dicom.file.DICOMImageReader;

public class DicomImageProvider implements ImageProvider {

    DICOMImage img;
    
    public boolean read(File sourceFile) {
        DICOMImageReader reader = null;
        try {
            reader = new DICOMImageReader(sourceFile);
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            img = reader.parse();
        } catch (EOFException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (DICOMException e) {
            return false;
        }
        return true;
    }

    public Image getImage() {
        return new LisaImageAdapter(img.getImage());
    }

}
