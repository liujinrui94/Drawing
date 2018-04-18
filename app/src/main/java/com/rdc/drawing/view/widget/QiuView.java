package com.rdc.drawing.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

public class QiuView extends View {

    Paint paint = new Paint();


    PointF point = new PointF();

    private int radius = 10;

    public QiuView(Context context, int color) {
        super(context);
        paint.setColor(color);
//        paint.set
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    public QiuView(Context context) {
        super(context);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 + 200, canvas.getHeight() / 2, paint);

    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        paint.setStrokeWidth(radius);
        invalidate();
    }

    //   触摸事件
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            point.set(event.getX(), event.getY());
//            invalidate();
//        }
//        return true;
//    }
}