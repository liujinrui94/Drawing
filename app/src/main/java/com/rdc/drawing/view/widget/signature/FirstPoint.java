package com.rdc.drawing.view.widget.signature;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * @author: FengWenyao
 * @email: fengwenyao@qdcftx.com
 * @time: 2017/8/14 15:37
 * @description: 签名画笔
 */

public class FirstPoint extends Point {
    protected float fx, fy;//画笔的中心点
    protected int color;//画笔的颜色
    protected int width;//画笔的宽度
    protected int alpha;//

    public FirstPoint(float x, float y, int color, int width,int alpha) {
        this.fx = x;
        this.fy = y;
        this.color = color;
        this.width = width;
        this.alpha=alpha;
    }
    public FirstPoint(float x, float y, int color, int width) {
        this.fx = x;
        this.fy = y;
        this.color = color;
        this.width = width;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(this.color);
        paint.setAlpha(alpha);
        canvas.drawCircle(this.fx, this.fy, this.width / 2, paint);
    }

    public float getFx() {
        return fx;
    }

    public void setFx(float fx) {
        this.fx = fx;
    }

    public float getFy() {
        return fy;
    }

    public void setFy(float fy) {
        this.fy = fy;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "FirstPoint{" +
                "x=" + x +
                ", y=" + y +
                ", color=" + color +
                ", width=" + width +
                '}';
    }
}
