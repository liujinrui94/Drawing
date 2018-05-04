package com.rdc.drawing.view.widget.signature;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 类描述：
 */
class FollowPoints extends FirstPoint implements MyPoint {
    private FirstPoint firstPoint;

    FollowPoints(float fx, float fy, int color, int width, FirstPoint point, int alpha) {
        super(fx, fy, color, width, alpha);
        this.firstPoint = point;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(this.color);
        paint.setStrokeWidth(this.width);
        paint.setAlpha(this.alpha);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);

        canvas.drawLine(this.fx, this.fy, firstPoint.fx, firstPoint.fy, paint);
//        paint.setStrokeWidth((float) (this.width-0.5));
//        canvas.drawLine(this.fx, this.fy, firstPoint.fx-1, firstPoint.fy-1, paint);
//        paint.setStrokeWidth(this.width+1);
//        canvas.drawLine(this.fx, this.fy, firstPoint.fx, firstPoint.fy, paint);
        canvas.drawCircle(this.fx, this.fy, this.width / 2, paint);
//        m_Path.moveTo(this.fx,this.fy);
//        canvas.drawPath(m_Path,paint);
//        m_Path.reset();
    }
}
