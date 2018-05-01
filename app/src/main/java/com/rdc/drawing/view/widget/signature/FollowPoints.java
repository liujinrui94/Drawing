package com.rdc.drawing.view.widget.signature;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 类描述：
 */
class FollowPoints extends FirstPoint implements MyPoint {
    private FirstPoint firstPoint;

    FollowPoints(float fx, float fy, int color, int width, FirstPoint point,int alpha) {
        super(fx, fy, color, width,alpha);
        this.firstPoint = point;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(this.color);
        paint.setStrokeWidth(this.width);
        paint.setAlpha(this.alpha);
//        canvas.drawLine(this.fx, this.fy, firstPoint.fx, firstPoint.fy, paint);
        canvas.drawCircle(this.fx, this.fy, this.width / 2, paint);
        canvas.drawLine(this.fx, this.fy, firstPoint.fx, firstPoint.fy, paint);

    }
}
