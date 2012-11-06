package org.bdx1.diams.image;

import java.util.Map;

import org.bdx1.diams.model.Image;
import org.bdx1.diams.model.Slice;
import android.graphics.Bitmap;

public class ImageTranscriber {

    public static Bitmap transcribeSlice(Slice slice) {
        Image img = slice.getImage();
        int[] imgData = img.getData();
        int[] pixels = new int[imgData.length];
        Map<String,String> infosMap = slice.getInfos();
        float slope = Float.valueOf(infosMap.get("Slope"));
        float intercept = Float.valueOf(infosMap.get("Intercept"));
        
        for (int i=0 ; i<imgData.length ; i++) {
            //int pixelData = (int) (imgData[i] * slope + intercept);
            int pixelData = imgData[i];
            pixels[i] = (0xFF<<24) | (pixelData<<16) | (pixelData<<8) | pixelData;
        }
        
        Bitmap bitmap = Bitmap.createBitmap(pixels, img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        return bitmap;
    }
    
}
