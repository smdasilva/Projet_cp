package org.bdx1.diams.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.bdx1.diams.parsing.InformationProvider;
import org.bdx1.diams.parsing.InformationProviderManager;

public class BaseSlice implements Slice {

    private Map<String, String> sliceInfos;
    
    BaseSlice(File sourceFile) {
        InformationProvider dicomProv = InformationProviderManager.getDicomProvider();
        dicomProv.read(sourceFile);
        sliceInfos = dicomProv.getSliceInfos();
    }
    
    public Map<String, String> getInfos() {
        return Collections.unmodifiableMap(sliceInfos);
    }
}
