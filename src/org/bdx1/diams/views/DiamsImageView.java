package org.bdx1.diams.views;

import org.bdx1.diams.image.ImageTranscriber;
import org.bdx1.diams.model.Slice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class DiamsImageView extends View {

    private Slice slice;
    private Paint p;
    
    private int windowCenter=0;
    private int windowWidth=1024;

    public void setWindowCenter(int windowCenter) {
        this.windowCenter = windowCenter;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
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
        ColorMatrix matrix = new ColorMatrix();
        //matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        p = new Paint();
        p.setColorFilter(filter);
    }
    
    public void setSlice(Slice newSlice) {
        slice = newSlice;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(slice != null) {
            Bitmap bitmap = ImageTranscriber.transcribeSlice(slice, windowCenter, windowWidth);
            canvas.drawBitmap(bitmap, 0, 0, p);
        }
    }
}
