package org.bdx1.diams.parsing;

import java.io.File;
import java.util.Map;

public class DicomInfosProvider implements InformationProvider {

    private File target;

    DicomInfosProvider(File target) {
        this.target = target;
    }
    
    public Map<String, String> getPatientInfos() {
        return null;
    }

    public Map<String, String> getSliceInfos() {
        return null;
    }

}
