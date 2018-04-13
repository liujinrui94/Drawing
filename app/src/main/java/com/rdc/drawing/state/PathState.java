package com.rdc.drawing.state;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import com.rdc.drawing.command.Command;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.data.BaseDrawData;
import com.rdc.drawing.data.PathDrawData;
import com.rdc.drawing.utils.CommandUtils;
import com.rdc.drawing.utils.DrawDataUtils;

/**
 * Created by lichaojian on 16-8-28.
 */
public class PathState extends BaseState {
    private static PathState mPathState = null;
    private PathDrawData mPathDrawData;

    private PathState() {
    }

    public static PathState getInstance() {
        if (mPathState == null) {
            mPathState = new PathState();
        }
        return mPathState;
    }

    @Override
    public void onDraw(BaseDrawData baseDrawData, Canvas canvas) {
        if (baseDrawData != null) {
            PathDrawData pathDrawData = (PathDrawData) baseDrawData;
            canvas.drawPath(pathDrawData.getPath(), pathDrawData.getPaint());
        }
    }

    @Override
    public BaseDrawData downDrawData(MotionEvent event, Paint paint) {
        Command command = new Command();
        mPathDrawData = new PathDrawData();
        Paint pathPaint = new Paint(paint);
        Path path = new Path();
        path.reset();
        path.moveTo(event.getX(), event.getY());
        mPathDrawData.setPaint(pathPaint);
        mPathDrawData.setPath(path);
        mPathDrawData.setPoint(event.getX() + "," + event.getY() + "|");
        command.setCommand(NoteApplication.COMMAND_ADD);
        command.getCommandDrawList().add(mPathDrawData);
        CommandUtils.getInstance().getUndoCommandList().add(command);
        DrawDataUtils.getInstance().getSaveDrawDataList().add(mPathDrawData);
        return mPathDrawData;
    }

    @Override
    public void moveDrawData(MotionEvent event, Paint paint, Canvas canvas) {
        mPathDrawData.setPoint(event.getX() + "," + event.getY() + "|");
        mPathDrawData.getPath().lineTo(event.getX(), event.getY());
    }

    @Override
    public void upDrawData(MotionEvent event, Paint paint) {
        mPathDrawData.setPoint(event.getX() + "," + event.getY() + "]");
        mPathDrawData.getPath().lineTo(event.getX(), event.getY());
    }

    @Override
    public void pointerDownDrawData(MotionEvent event) {

    }

    @Override
    public void pointerUpDrawData(MotionEvent event) {

    }

    @Override
    public void destroy() {
        mPathState = null;
    }
}
