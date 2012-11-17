package org.bdx1.diams.image;

import org.bdx1.diams.model.Image;
import org.bdx1.diams.model.Slice;

import android.graphics.Bitmap;

public class ImageTranscriber {

    private Slice slice;
    private Bitmap bitmap;
    private int[] pixels;

    public ImageTranscriber(Slice slice) {
        this.slice = slice;
        Image img = slice.getImage();
        bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        pixels = new int[img.getWidth()*img.getHeight()];
    }
    
    public Bitmap transcribeSlice(Slice slice, int windowCenter, int windowWidth) {
        Image img = slice.getImage();
        int[] imgData = img.getData();
        
        int min = windowCenter - (windowWidth/2);
        int max = windowCenter + (windowWidth/2);
        min = min < 0 ? 0 : min;
        max = max > img.getMax() ? img.getMax() : max;
        
        for (int i=0 ; i<imgData.length ; i++) {
            int pixelData = imgData[i];
            pixelData = applyHounsfield(pixelData, min, max);
            pixels[i] = pixelData | pixelData<<8 | (pixelData<<16) | pixelData<<24 | (0xFF<<24);
        }
        
        bitmap.setPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        
        return bitmap;
    }
    
    private static int applyHounsfield(int originalValue, int min, int max) {
        originalValue = (originalValue < min) ? min : originalValue;
        originalValue = (originalValue > max) ? max : originalValue;
        int windowLength = max - min;
        return 255*(originalValue-min)/windowLength;
    }
    
}
