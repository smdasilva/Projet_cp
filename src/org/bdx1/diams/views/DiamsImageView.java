package org.bdx1.diams.views;

import org.bdx1.diams.R;
import org.bdx1.diams.model.Image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class DiamsImageView extends View {

    private Image img;
    private Paint p;

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
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        p = new Paint();
        p.setColorFilter(filter);
    }
    
    public void setImage(Image newImage) {
        img = newImage;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (img != null) {
            
            canvas.drawBitmap(img.getData(), 0, img.getWidth(), 0, 0,
                img.getWidth(), img.getHeight(),
                false, p);
        }
    }
}
