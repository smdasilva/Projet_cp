package org.bdx1.diams.parsing;

public class InformationProviderManager {

    public static InformationProvider getDicomProvider() {
        return new DicomInfosProvider();
    }
    
}
