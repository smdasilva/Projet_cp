package org.bdx1.diams.parsing;

public class ImageProviderManager {

    public static ImageProvider getDicomProvider() {
        return new DicomImageProvider();
    }
    
}
