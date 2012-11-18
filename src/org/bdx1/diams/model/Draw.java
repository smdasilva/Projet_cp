package org.bdx1.diams.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.ImageView;

public class Draw  {
	
	private float mX, mY; // Last Coord
    private float TOUCH_TOLERANCE = 1;
    private Path mPath;
    private Bitmap bitmap;
    private Paint mPaint;
    private Canvas mCanvas;
    
    public Draw() {
    	this.mPath = new Path(); 
    	this.bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ALPHA_8);
    	mCanvas = new Canvas();
    	mCanvas.setBitmap(bitmap); // Ouverture de l'ecriture sur la bitmap
    	
    	/* Proprietes du pinceau */ 
    	mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        //mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);
    }

    public void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    public void touch_move(float x, float y) {
        System.out.println(x+":"+y);
    	float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    public void touch_up() {
        mPath.lineTo(mX, mY);       
    }
    
    public void write_bitmap() {
    	mCanvas.drawPath(mPath, mPaint);
    }
    
    public Bitmap get_bitmap() {
    	return bitmap;
    }

}
