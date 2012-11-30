package org.bdx1.diams.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.bdx1.diams.Factory;
import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;

/**
 * Default implementation for the Slice interface.
 * Implements equals for comparisons.
 */
public class BaseSlice implements Slice {

    private Map<String, String> sliceInfos;
    private Image img;
    private Mask mask;
    
    BaseSlice(File sourceFile) {
        InformationProvider dicomProv = InformationProviderManager.getDicomProvider();
        dicomProv.read(sourceFile);
        sliceInfos = dicomProv.getSliceInfos();
        img = Factory.MODEL_FACTORY.makeImage(sourceFile);
        mask = Factory.MODEL_FACTORY.makeMask(img);
    }
    
    public Map<String, String> getInfos() {
        return Collections.unmodifiableMap(sliceInfos);
    }
    
    public boolean equals(Object other) {
        if (other instanceof Slice)
            return equals((Slice)other);
        return false;
    }
    
    public boolean equals(Slice other) {
        return this.getInfos().equals(other.getInfos());
    }

    public Image getImage() {
        return img;
    }

    public Mask getMask() {
        return null;
    }
}
