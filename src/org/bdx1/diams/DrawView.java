package org.bdx1.diams;

import org.bdx1.diams.model.Mask;
import org.bdx1.diams.model.InfiniteMask;
import org.bdx1.diams.util.CantorPair;
import org.bdx1.diams.util.Pair;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
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
private Mask mask = new InfiniteMask();

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
    mPaint.setStrokeWidth(3);
    mCanvas = new Canvas();
    mPath = new Path();
    paths.add(mPath);

}               
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {            

        for (Path p : paths){
            canvas.drawPath(p, mPaint);
        }
        
        /*
        Pair<Integer, Integer> paire = new Pair<Integer, Integer>(0,0);
        for (Integer p : mask.getMask()){
        	paire = CantorPair.decode(p);
        	int x = paire.getFirst();
        	int y = paire.getSecond();
        	canvas.drawPoint(x, y, mPaint);
            //System.out.println("("+ x +"," +y+ ")");
        }*/
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
    	mPath.reset();
        mPath.moveTo(x, y);
        mask.setPixel((int)x, (int)y, true);
        //mCanvas.drawPoint(x, y, mPaint);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
    	mask.setPixel((int)x, (int)y, true);
    	float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
            //mask.setPixel((int)x, (int)y, true);
        }
    }
    private void touch_up() {
    	mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw            
        mPath = new Path();
        paths.add(mPath);
    }



public boolean onTouch(View arg0, MotionEvent event) {
      float x = event.getX();
      float y = event.getY();

      switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
        	  //System.out.println("down");
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