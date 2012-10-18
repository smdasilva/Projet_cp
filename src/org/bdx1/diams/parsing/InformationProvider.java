package org.bdx1.diams.parsing;

import java.io.File;
import java.util.Map;

public interface InformationProvider {
    
    public boolean read(File target);
    public Map<String, String> getPatientInfos();
    public Map<String, String> getSliceInfos();
    public Map<String, String> getStudyInfos();
    
}
