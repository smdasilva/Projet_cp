package org.bdx1.diams;


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
    List<Point> points = new ArrayList<Point>();
    //Paint paint = new Paint();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        //paint.setColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas) {
    	 Path path = new Path();

    	    if(points.size() > 1){
    	        for(int i = points.size() - 2; i < points.size(); i++){
    	            if(i >= 0){
    	                Point point = points.get(i);

    	                if(i == 0){
    	                    Point next = points.get(i + 1);
    	                    point.dx = ((next.x - point.x) / 3);
    	                    point.dy = ((next.y - point.y) / 3);
    	                }
    	                else if(i == points.size() - 1){
    	                    Point prev = points.get(i - 1);
    	                    point.dx = ((point.x - prev.x) / 3);
    	                    point.dy = ((point.y - prev.y) / 3);
    	                }
    	                else{
    	                    Point next = points.get(i + 1);
    	                    Point prev = points.get(i - 1);
    	                    point.dx = ((next.x - prev.x) / 3);
    	                    point.dy = ((next.y - prev.y) / 3);
    	                }
    	            }
    	        }
    	    }

    	    boolean first = true;
    	    for(int i = 0; i < points.size(); i++){
    	        Point point = points.get(i);
    	        if(first){
    	            first = false;
    	            path.moveTo(point.x, point.y);
    	        }
    	        else{
    	            Point prev = points.get(i - 1);
    	            path.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);
    	        }
    	    }
    	    canvas.drawPath(path, paint);
    }

    public boolean onTouch(View view, MotionEvent event) {
    	if(event.getAction() != MotionEvent.ACTION_UP){
            Point point = new Point();
            point.x = event.getX();
            point.y = event.getY();
            points.add(point);
            invalidate();
            //Log.d(TAG, "point: " + point);
            return true;
        }
        return super.onTouchEvent(event);
}

class Point {
    float x, y;
    float dx, dy;

    @Override
    public String toString() {
        return x + ", " + y;
    }
}

}