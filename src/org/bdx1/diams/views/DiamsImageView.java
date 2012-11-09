package org.bdx1.diams.views;

import org.bdx1.diams.image.ImageTranscriber;
import org.bdx1.diams.model.Slice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * TODO: document your custom view class.
 */
public class DiamsImageView extends ImageView {

    private Slice slice;
    
    private int windowCenter=0;
    private int windowWidth=1024;
    
    private float scale;
    private float tx;
    private float ty;

    public void setWindowCenter(int windowCenter) {
        this.windowCenter = windowCenter;
        updateBitmap();
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
        updateBitmap();
    }

    public DiamsImageView(Context context) {
        super(context);
        init();
    }

    public DiamsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiamsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setScaleType(ScaleType.MATRIX);
    }
    
    public void setSlice(Slice newSlice) {
        slice = newSlice;
        updateBitmap();
        //centerImage();
    }

    private void updateBitmap() {
        Bitmap bmp = ImageTranscriber.transcribeSlice(slice, windowCenter, windowWidth);
        this.setImageBitmap(bmp);
    }
    
    private void centerImage() {
        //float scaleX = this.getWidth() / (float) this.slice.getImage().getWidth();
        //float scaleY = this.getHeight() / (float) this.slice.getImage().getWidth();
        this.scale = 1;
        this.tx = Math.max(0, 0.5f * this.scale * (this.getWidth() - this.slice.getImage().getWidth()));
        this.ty = Math.max(0, 0.5f * this.scale * (this.getHeight() - this.slice.getImage().getHeight()));
        
        updateTranformations();
    }
    
    private void updateTranformations() {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(tx, ty);
        this.setImageMatrix(matrix);
    }
    
    private void updateScale(float newScale) {
        this.scale = newScale;
        updateTranformations();
    }
    
    private void updateTranslation(float tx, float ty) {
        this.tx = tx;
        this.ty = ty;
        updateTranformations();
    }
}
