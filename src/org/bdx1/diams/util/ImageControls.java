package org.bdx1.diams.util;

import android.graphics.Matrix;

public class ImageControls {

    public float tx;
    public float ty;
    public float scale;
    
    private static ImageControls instance;
    
    private ImageControls() {
        this.tx = 0;
        this.ty = 0;
        this.scale = 1;
    }
    
    public static ImageControls getInstance() {
        if (instance == null) {
            instance = new ImageControls();
        }
        return instance;
    }
    
    public Matrix getTransformationMatrix() {
        Matrix m = new Matrix();
        m.postScale(scale, scale);
        m.postTranslate(tx, ty);
        return m;
    }
    
}
