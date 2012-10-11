package org.bdx1.diams.parsing;

import java.util.Map;

public interface InformationProvider {
    
    public Map<String, String> getPatientInfos();
    public Map<String, String> getSliceInfos();
    
}
