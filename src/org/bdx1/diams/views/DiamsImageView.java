package org.bdx1.diams.views;

import org.bdx1.diams.image.ImageTranscriber;
import org.bdx1.diams.model.Slice;
import org.bdx1.diams.util.ImageControls;

import android.content.Context;
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

    private float touchX;
    private float touchY;

    private ImageTranscriber transcriber;

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
        transcriber = new ImageTranscriber(slice);
        updateBitmap();
        //centerImage();
    }

    private void updateBitmap() {
        this.setImageBitmap(transcriber.transcribeSlice(slice, windowCenter, windowWidth));
    }

    private void updateTransformations() {
        this.setImageMatrix(ImageControls.getInstance().getTransformationMatrix());
        this.invalidate();
    }

    public void updateScale(float newScale) {
        ImageControls ctrls = ImageControls.getInstance();
        float scaleRatio = ctrls.scale/newScale;
        ctrls.scale = newScale;
        ctrls.tx *= scaleRatio;
        ctrls.ty *= scaleRatio;
        updateTransformations();
    }

    private void updateTranslation(float tx, float ty) {
        ImageControls ctrls = ImageControls.getInstance();
        ctrls.tx = tx;
        ctrls.ty = ty;
        updateTransformations();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ImageControls ctrls = ImageControls.getInstance();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN :
            touchX = event.getX();
            touchY = event.getY();
            break;
        case MotionEvent.ACTION_MOVE :
            updateTranslation(ctrls.tx+(event.getX()-touchX), ctrls.ty+(event.getY()-touchY));
            touchX = event.getX();
            touchY = event.getY();
            break;
        default :
            
            break;
        }
        return true;
    }


}
