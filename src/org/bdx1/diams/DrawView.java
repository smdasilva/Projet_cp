package org.bdx1.diams;

import java.util.ArrayList;

import org.bdx1.diams.model.InfiniteMask;
import org.bdx1.diams.model.Mask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawView extends View implements OnTouchListener {
private static final String TAG = "DrawView";

private static final float MINP = 0.25f;
private static final float MAXP = 0.75f;

private Canvas  mCanvas;
private Path    mPath;
private Paint       mPaint;   
private ArrayList<Path> paths = new ArrayList<Path>();
Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
//Canvas calque = new Canvas(bitmap);
int[] pix =new int [512 * 512]; // matrice
boolean maintenant = false;
Mask m = new InfiniteMask(512,512);


public DrawView(Context context) {
    super(context);
    setFocusable(true);
    setFocusableInTouchMode(true);

    this.setOnTouchListener(this);

    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setDither(true);
    mPaint.setColor(Color.BLACK);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeJoin(Paint.Join.ROUND);
    mPaint.setStrokeCap(Paint.Cap.ROUND);
    mPaint.setStrokeWidth(1);
    mCanvas = new Canvas();
    mCanvas.setBitmap(bitmap);
    mPath = new Path();
    paths.add(mPath);
 
    
}               
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas calque) {
    	
        for (Path p : paths){
        	mCanvas.drawPath(p, mPaint);
        }
        
       // calque.drawBitmap(bitmap, 0, 0, mPaint);
        
        
        if(maintenant) {
        
        for(int i = 0; i<bitmap.getWidth(); i++){
    		for(int j = 0; j<bitmap.getHeight(); j++){
    			if(m.getPixel(i,j)) {
    				calque.drawPoint(i, j, mPaint);
    			}
    		}
        } 
        }
        
        
        
        /*if(maintenant == true) {
	    	for(int i = 0; i<bitmap.getWidth(); i++){
	    		for(int j = 0; j<bitmap.getHeight(); j++){
	    		if(bitmap.getPixel(i, j) != 0) {
	    		System.out.println(i+','+j+':'+ bitmap.getPixel(i,j));
	    		System.out.println("ajout de" + i +":"+j );
	    		m.setPixel(i, j, true);
	    		} 
	    	}
	    }
	    	
	    	for(int i = 0; i<bitmap.getWidth(); i++){
	    		for(int j = 0; j<bitmap.getHeight(); j++){
	    			
	    			if(m.getPixel(i, j)) {
	    				System.out.println("coco");
	    				calque.drawPoint(i, j, mPaint);
	    			}
	    		}
        }
	    
        } else {*/
	        //for (Path p : paths){
	        	//calque.drawPath(p, mPaint);
	       // }
        	
        
        
        maintenant = false;

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 1;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        System.out.println(x+":"+y);
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
        // commit the path to our offscreen
        //calque.drawPath(mPath, mPaint);
        // kill this so we don't double draw            
        mPath = new Path();
        paths.add(mPath);
        
        int width, height;
        //Bitmap bmpOriginal = BitmapFactory.decodeResource(getResources(), bitmap.getGenerationId());
        /*Bitmap bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.mabitmap);
        height = 512;
        width = 512;
        
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);*/
        
        //Bitmap bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.mabitmap);
        //height = bmpOriginal.getHeight();
        //width = bmpOriginal.getWidth();
       //bitmap.getPixels(pix, 0, 512, 0, 0, 512, 512);
        
        for(int i = 0; i<bitmap.getWidth(); i++){
    		for(int j = 0; j<bitmap.getHeight(); j++){
    		if(bitmap.getPixel(i, j) != 0) {
    			m.setPixel(i, j, true);
    		}
    		}
        }
        
        maintenant = true;
        
        
        
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