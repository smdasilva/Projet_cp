package org.bdx1.diams.model;

import org.bdx1.image.data.LISAImageGray16Bit;

public class LisaImageAdapter implements Image {

    private LISAImageGray16Bit lisaImage;
    
    public LisaImageAdapter(LISAImageGray16Bit adaptedImage) {
        this.lisaImage = adaptedImage;
    }
    
    public int getHeight() {
        return lisaImage.getHeight();
    }

    public int getWidth() {
        return lisaImage.getWidth();
    }

    public int[] getData() {
        return lisaImage.getData();
    }

    public int getMax() {
        return lisaImage.getDataMax();
    }

}
