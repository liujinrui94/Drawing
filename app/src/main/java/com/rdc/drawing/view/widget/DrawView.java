package com.rdc.drawing.view.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rdc.drawing.bean.SaveData;
import com.rdc.drawing.board.dao.DaoSession;
import com.rdc.drawing.board.dao.SaveDataDao;
import com.rdc.drawing.command.Command;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.data.BaseDrawData;
import com.rdc.drawing.state.BaseState;
import com.rdc.drawing.state.PathState;
import com.rdc.drawing.utils.BaseProgressDialog;
import com.rdc.drawing.utils.CommandUtils;
import com.rdc.drawing.utils.DrawDataUtils;
import com.rdc.drawing.utils.FileUtils;
import com.rdc.drawing.utils.SVGUtils;
import com.rdc.drawing.utils.XMLUtils;
import com.rdc.drawing.view.widget.impl.DrawViewInterface;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lichaojian on 16-8-28.
 */
public class DrawView extends View implements DrawViewInterface {

    private static final String TAG = "DrawView";
    private Paint mPaint;//画笔
    private Canvas mCanvas;//画布
    private Bitmap mBitmap;
    private int mCanvasCode = NoteApplication.CANVAS_NORMAL;
    private BaseDrawData mBaseDrawData = null;
    private BaseState mCurrentState = PathState.getInstance();//当前绘制的状态

    private RadioGroup radioGroup;

    private BaseProgressDialog baseProgressDialog;

    private RecyclerView recyclerView;

    private LinearLayout linearLayout;

    private Context context;

    private List<BaseState> baseStateList = null;

    private boolean canDraw = true;


    public DrawView(Context context) {
        super(context);
        initParameter(context);
        this.context = context;
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParameter(context);
        this.context = context;
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParameter(context);
        this.context = context;
    }

    public void setRadioGroup(RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public void setCanvasCode(int mCanvasCode) {
        this.mCanvasCode = mCanvasCode;
    }

    private void initParameter(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
        initPaint();
        initCanvas();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setMyAlpha(int alpha, int colorString, int stroke) {
        mPaint = new Paint();
        mPaint.setColor(colorString);
        mPaint.setStrokeWidth(stroke);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAlpha(alpha);
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public boolean isCanDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    private void initCanvas() {
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.WHITE);
    }

    public void changePaintColor(int color) {
        mPaint.setColor(color);
    }

    public void changePaintSize(float width) {
        mPaint.setStrokeWidth(width);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        switch (mCanvasCode) {
            case NoteApplication.CANVAS_NORMAL:
                mCurrentState.onDraw(mBaseDrawData, canvas);
                break;
            case NoteApplication.CANVAS_UNDO:
                undo();
                break;
            case NoteApplication.CANVAS_REDO:
                redo();
                break;
            case NoteApplication.CANVAS_RESET:
                reset();
                break;
            default:
                Log.e(TAG, "onDraw" + Integer.toString(mCanvasCode));
                break;
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (radioGroup != null) {
            radioGroup.clearCheck();

        }
        if (recyclerView.getVisibility() == VISIBLE) {
            recyclerView.setVisibility(GONE);
            linearLayout.setVisibility(VISIBLE);
        }

        mCanvasCode = NoteApplication.CANVAS_NORMAL;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                if (mBaseDrawData == null && !mCurrentState.isShear()) {
                    reset();
                    drawFromData();
                    return super.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mCurrentState.pointerDownDrawData(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mCurrentState.pointerUpDrawData(event);
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }


    private void actionDown(MotionEvent event) {
        mBaseDrawData = mCurrentState.downDrawData(event, mPaint);
    }

    private void actionMove(MotionEvent event) {
        mCurrentState.moveDrawData(event, mPaint, mCanvas);
    }

    private void actionUp(MotionEvent event) {
        mCurrentState.upDrawData(event, mPaint);
        mCurrentState.onDraw(mBaseDrawData, mCanvas);
    }

    @Override
    public void redo() {
        redoFromCommand(CommandUtils.getInstance().redo());
    }

    @Override
    public void undo() {
        reset();
        undoFromCommand(CommandUtils.getInstance().undo());
    }

    @Override
    public void reset() {
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(mPaint);
        mPaint.setXfermode(null);
    }

    @Override
    public String save(String path, String rootPath) {
        // TODO: 2016/8/8 从数据源里面获取相应的参数，保存为XML,PNG,SVG
        String pngFileName = null;
        pngFileName = FileUtils.saveAsPng(mBitmap, path, rootPath);
        XMLUtils xmlUtils = new XMLUtils();
        String[] xmlData = DrawDataUtils.getInstance().getXMLData();
        xmlUtils.encodeXML(rootPath + "/" + pngFileName + ".xml", xmlData[0], xmlData[1], xmlData[2]);
        SVGUtils svgUtils = new SVGUtils();
        svgUtils.encodeSVG(DrawDataUtils.getInstance().getSaveDrawDataList(), rootPath + "/" + pngFileName + ".svg");
        return pngFileName;
    }

    public void setCurrentState(BaseState currentState) {
        mCurrentState = currentState;
    }

    private void undoFromCommand(Command command) {

        if (command != null) {
            switch (command.getCommand()) {
                case NoteApplication.COMMAND_ADD:
                    Iterator<BaseDrawData> iterator = DrawDataUtils.getInstance().getSaveDrawDataList().iterator();
                    while (iterator.hasNext()) {
                        BaseDrawData baseDrawData = iterator.next();
                        if (baseDrawData.equals(command.getCommandDrawList().get(0))) {
                            iterator.remove();
                            break;
                        }
                    }
                    drawFromData();
                    break;
                case NoteApplication.COMMAND_TRANSLATE:
                    Iterator<BaseDrawData> commandIterator = command.getCommandDrawList().iterator();
                    DrawDataUtils.getInstance().offSetDrawData(commandIterator, -command.getOffSetX(), -command.getOffSetY());
                    drawFromData();
                    break;
                case NoteApplication.COMMAND_SCALE:
                    break;
                default:
                    Log.e(TAG, "undoFromCommand" + Integer.toString(command.getCommand()));
                    break;
            }
        }
    }


    private void redoFromCommand(Command command) {
        if (command != null) {
            switch (command.getCommand()) {
                case NoteApplication.COMMAND_ADD:
                    drawFromRedoData(command.getCommandDrawList());
                    break;
                case NoteApplication.COMMAND_TRANSLATE:
                    Iterator<BaseDrawData> commandIterator = command.getCommandDrawList().iterator();
                    DrawDataUtils.getInstance().offSetDrawData(commandIterator, command.getOffSetX(), command.getOffSetY());
                    reset();
                    drawFromData();
                    break;
                case NoteApplication.COMMAND_SCALE:
                    break;
                default:
                    Log.e(TAG, "redoFromCommand" + Integer.toString(command.getCommand()));
                    break;
            }
        }
    }

    /**
     * 从redo里面恢复数据
     */
    private void drawFromRedoData(List<BaseDrawData> redoList) {
        int size = redoList.size();
        for (int i = 0; i < size; ++i) {
            BaseDrawData baseDrawData = redoList.get(i);
            baseDrawData.onDraw(mCanvas);
            DrawDataUtils.getInstance().getSaveDrawDataList().add(baseDrawData);
        }
    }

    public void drawFromData() {
        int size = DrawDataUtils.getInstance().getSaveDrawDataList().size();
        for (int i = 0; i < size; ++i) {
            BaseDrawData baseDrawData = DrawDataUtils.getInstance().getSaveDrawDataList().get(i);
            baseDrawData.onDraw(mCanvas);
        }
    }

    public void addCommand() {
        Iterator<BaseDrawData> iterator = DrawDataUtils.getInstance().getSaveDrawDataList().iterator();
        Command command;
        while (iterator.hasNext()) {
            BaseDrawData baseDrawData = iterator.next();
            command = new Command();
            command.setCommand(NoteApplication.COMMAND_ADD);
            command.getCommandDrawList().add(baseDrawData);
            CommandUtils.getInstance().getUndoCommandList().add(command);
        }
    }
}
