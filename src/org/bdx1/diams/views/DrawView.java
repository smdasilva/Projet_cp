package org.bdx1.diams.views;



import org.bdx1.diams.model.InfiniteMask;
import org.bdx1.diams.model.Mask;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class DrawView extends ImageView implements OnTouchListener {

    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mPaint;   
    Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ALPHA_8);
    Mask mask = new InfiniteMask(512,512);


    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public void save() {
    	for(int i = 0; i<bitmap.getWidth(); i++){
            for(int j = 0; j<bitmap.getHeight(); j++){
            	if(bitmap.getPixel(i, j) != 0) {    
            		mask.setPixel(i, j, true);
            	}
            }
    	}
    }
    	
    
    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        mCanvas = new Canvas();
        mCanvas.setBitmap(bitmap);
        mPath = new Path();
    }               
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas calque) {
        mCanvas.drawPath(mPath, mPaint);
        calque.drawBitmap(bitmap, 0, 0, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 1;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mCanvas.drawPoint(x, y, mPaint);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);    
    }

    


    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touch_start(x, y);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            touch_move(x, y);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            touch_up();
            invalidate();
            break;
        }
        return true;
    }

}