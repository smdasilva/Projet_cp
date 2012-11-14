package org.bdx1.diams.views;

import org.bdx1.diams.image.ImageTranscriber;
import org.bdx1.diams.model.Image;
import org.bdx1.diams.model.Slice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * TODO: document your custom view class.
 */
public class DiamsImageView extends ImageView {

    private Slice slice;

    private int windowCenter=0;
    private int windowWidth=1024;

    private float scale = 1;
    private float tx;
    private float ty;

    private Bitmap bitmap;

    private float touchX;
    private float touchY;

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
        Image img = slice.getImage();
        bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        updateBitmap();
        //centerImage();
    }

    private void updateBitmap() {
        int[] pixels = ImageTranscriber.transcribeSlice(slice, windowCenter, windowWidth);
        Image img = slice.getImage();
        bitmap.setPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        this.setImageBitmap(bitmap);
    }

    private void centerImage() {
        //float scaleX = this.getWidth() / (float) this.slice.getImage().getWidth();
        //float scaleY = this.getHeight() / (float) this.slice.getImage().getWidth();
        this.scale = 1;
        this.tx = Math.max(0, 0.5f * this.scale * (this.getWidth() - this.slice.getImage().getWidth()));
        this.ty = Math.max(0, 0.5f * this.scale * (this.getHeight() - this.slice.getImage().getHeight()));

        updateTransformations();
    }

    private void updateTransformations() {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(tx, ty);
        this.setImageMatrix(matrix);
        this.invalidate();
    }

    public void updateScale(float newScale) {
        float scaleRatio = scale/newScale;
        this.scale = newScale;
        tx *= scaleRatio;
        ty *= scaleRatio;
        updateTransformations();
    }

    private void updateTranslation(float tx, float ty) {
        this.tx = tx;
        this.ty = ty;
        updateTransformations();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN :
            touchX = event.getX();
            touchY = event.getY();
            break;
        case MotionEvent.ACTION_MOVE :
            updateTranslation(tx+(event.getX()-touchX), ty+(event.getY()-touchY));
            touchX = event.getX();
            touchY = event.getY();
            break;
        default :
            
            break;
        }
        return true;
    }


}
