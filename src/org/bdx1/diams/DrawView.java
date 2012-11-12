package org.bdx1.diams;

import org.bdx1.diams.model.Drawing;
import org.bdx1.diams.model.Mask;
import org.bdx1.diams.model.InfiniteMask;
import org.bdx1.diams.util.CantorPair;
import org.bdx1.diams.util.Pair;
import org.bdx1.diams.util.Point2D;

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
private ArrayList<Point2D> points = new ArrayList<Point2D>();
private Drawing drawing = new Drawing(mask);
float[][] controlPoints;

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

        /*for (Path p : paths){
            canvas.drawPath(p, mPaint);
        }*/
        
    	Bezier b = new Bezier();
        float[][] controlPoints = new float [points.size()][2];
        float[][] resultat;
        
        if(points.size() > 0) {
	    	float dx = points.get(points.size()-1).x - points.get(0).x;
	    	float dy = points.get(points.size()-1).y - points.get(0).y;
	    	int distance = (int) Math.sqrt(Math.pow(dx,2)+ Math.pow(dy,2)); 
	        
	    	for (int i=0; i<points.size(); i++) {
	        	controlPoints[i][0] = points.get(i).x;
	        	controlPoints[i][1] = points.get(i).y;
	        }
	        
	        resultat = b.computeBezierPoints(controlPoints, distance);
	    	
	        for(int i = 0; i< resultat.length; i++) {
				float x = resultat[i][0];
				float y = resultat[i][1];
				Point2D pc = new Point2D(x,y);
				drawing.draw(pc);
			}
	        
	        Pair<Integer, Integer> paire = new Pair<Integer, Integer>(0,0);
	        for (Integer p : mask.getMask()){
	        	paire = CantorPair.decode(p);
	        	int x = paire.getFirst();
	        	int y = paire.getSecond();
	        	canvas.drawPoint(x, y, mPaint);
	            //System.out.println("("+ x +"," +y+ ")");  
	        }
        }
        
        
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 1;

    private void touch_start(float x, float y) {
    	Point2D p = new Point2D(x,y);
    	drawing.draw(p);
    	points.add(p);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
    	Point2D p = new Point2D(x,y);
    	drawing.draw(p);
    	points.add(p);
    	
    	float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //int distance = (int) Math.sqrt(Math.pow(dx,2)+ Math.pow(dy,2)); 
        	//Bezier b = new Bezier();
            //float[][] resultat;

    		/*
    		int nb = points.size();
    		Point2D p1 = points.get(nb-1);
    		Point2D p2 = points.get(nb-2);
    		Point2D p3 = points.get(nb-3);
    		
    		controlPoints[0][0] = p1.x;
    		controlPoints[0][1] = p1.y;
    		controlPoints[1][0] = p2.x;
    		controlPoints[1][1] = p2.y;
    		controlPoints[2][0] = p3.x;
    		controlPoints[2][1] = p3.y;
    		*/
    		
    		//resultat = b.computeBezierPoints(controlPoints, distance);
    		
    		/*for(int i = 0; i<=distance; i++) {
    			x = resultat[i][0];
    			y = resultat[i][1];
    			Point2D pc = new Point2D(x,y);
    			points.add(pc);
    			drawing.draw(pc);
    			System.out.println("("+x+")"+","+y+")");
    		}
         */
        	//mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
            //mask.setPixel((int)x, (int)y, true);
        }
    }
    private void touch_up() {
    	//mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        //mCanvas.drawPath(mPath, mPaint);
        //// kill this so we don't double draw            
        //mPath = new Path();
        //paths.add(mPath);
    	controlPoints = new float [1][2];
    
    	
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