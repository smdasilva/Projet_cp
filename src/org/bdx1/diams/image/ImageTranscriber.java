package org.bdx1.diams.image;

import java.util.Map;

import org.bdx1.diams.model.Image;
import org.bdx1.diams.model.Slice;
import android.graphics.Bitmap;

public class ImageTranscriber {

    public static Bitmap transcribeSlice(Slice slice, int windowCenter, int windowWidth) {
        Image img = slice.getImage();
        int[] imgData = img.getData();
        int[] pixels = new int[imgData.length];
        Map<String,String> infosMap = slice.getInfos();
        float slope = Float.valueOf(infosMap.get("Slope"));
        float intercept = Float.valueOf(infosMap.get("Intercept"));
        
        int min = windowCenter - (windowWidth/2);
        int max = windowCenter + (windowWidth/2);
        min = min < 0 ? 0 : min;
        max = max > img.getMax() ? img.getMax() : max;
        
        for (int i=0 ; i<imgData.length ; i++) {
            //int pixelData = (int) (imgData[i] * slope + intercept);
            int pixelData = imgData[i];
            pixelData = applyHounsfield(pixelData, min, max);
            pixels[i] = pixelData | pixelData<<8 | (pixelData<<16) | pixelData<<24 | (0xFF<<24);
        }
        
        Bitmap bitmap = Bitmap.createBitmap(pixels, img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        return bitmap;
    }
    
    private static int applyHounsfield(int originalValue, int min, int max) {
        originalValue = (originalValue < min) ? min : originalValue;
        originalValue = (originalValue > max) ? max : originalValue;
        int windowLength = max - min;
        return 255*(originalValue-min)/windowLength;
    }
    
}
