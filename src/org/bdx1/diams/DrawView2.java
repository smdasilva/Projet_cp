/*package org.bdx1.diams;

import org.bdx1.diams.model.MatrixMask;
import org.bdx1.diams.util.Pair;
import org.bdx1.diams.util.Point2D;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawView2 extends View implements OnTouchListener {
    private static final String TAG = "DrawView";

    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;

    private Paint       mPaint;   
    private Mask mask;
    private List<Point2D> points = new LinkedList<Point2D>();
    private Drawing drawing = new Drawing(mask);
    private Bezier bezier = new Bezier();
    private Point2D previous;

    public DrawView2(Context context) {
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
        mPaint.setStrokeWidth(15);
        //mCanvas = new Canvas();

        mask = new MatrixMask(512, 512);
    }               

    @Override
    protected void onDraw(Canvas canvas) {            

       

        int[] maskData = mask.getData();
        canvas.drawBitmap(Bitmap.createBitmap(maskData, 512, 512, Bitmap.Config.ARGB_8888),
                0,0,mPaint);

//        Point2D previous = null;
//        for (Point2D p : points) {
//            if (previous != null)
//                canvas.drawLine(previous.x, previous.y, p.x, p.y, mPaint);
//            previous = p;        
//        }


    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 1;

    private void touch_start(float x, float y) {
//        Point2D p = new Point2D(x,y);
//        points.add(p);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
//        Point2D p = new Point2D(x,y);
//        points.add(p);
        
        float x1 = Math.min(x, mX);
        float x2 = Math.max(x, mX);
        float y1 = Math.min(y, mY);
        float y2 = Math.max(y, mY);
        
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        float c = (y-mY)/(x-mX);
        float b = y-(c*x);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            for (float s = x1+1 ; s<x2 ; s+=1)
                for (float z=(b-2) ; z<b+3 ; z++)
                    mask.setPixel((int)s, (int)(c*s+z), true);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
//        float[][] controls = new float[points.size()][2];
//        for (int i=0 ; i<points.size() ; i++) {
//            Point2D p = points.get(i);
//            controls[i][0] = p.x;
//            controls[i][1] = p.y;
//        }
//        float[][] b = bezier.computeBezierPoints(controls, points.size()*5);
//        for(float[] t : b) {
//            mask.setPixel((int)t[0], (int)t[1], true);
//        }
//        points.clear();
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
}*/