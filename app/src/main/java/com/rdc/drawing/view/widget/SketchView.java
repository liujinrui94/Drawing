package com.rdc.drawing.view.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rdc.drawing.bean.SaveData;
import com.rdc.drawing.board.dao.DaoSession;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class SketchView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {

    public static final int STROKE = 0;
    public static final int ERASER = 1;
    public static final int DEFAULT_STROKE_SIZE = 7;
    public static final int DEFAULT_ERASER_SIZE = 50;
    private float strokeSize = DEFAULT_STROKE_SIZE;
    private int strokeColor = Color.BLACK;
    private float eraserSize = DEFAULT_ERASER_SIZE;
    private DaoSession daoSession = NoteApplication.getsInstance().getDaoSession();
    private Path m_Path;
    private Paint m_Paint;
    private float mX, mY;
    private int width, height;

    private ArrayList<Pair<Path, Paint>> paths = new ArrayList<>();
    private ArrayList<Pair<Path, Paint>> undonePaths = new ArrayList<>();
    private Bitmap bitmap;
    private int mode = STROKE;

    private int MyAlpha = 55;
    private OnDrawChangedListener onDrawChangedListener;
    private RadioGroup radioGroup;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private Boolean hasMove = false;

    public SketchView(Context context, AttributeSet attr) {
        super(context, attr);

//        setFocusable(true);
//        setFocusableInTouchMode(true);
        setBackgroundColor(Color.WHITE);
        this.setOnTouchListener(this);
        // 初始化paint
        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);
        m_Paint.setDither(true);
        m_Paint.setColor(strokeColor);
        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeJoin(Paint.Join.ROUND);
        m_Paint.setStrokeCap(Paint.Cap.ROUND);
        m_Paint.setStrokeWidth(strokeSize);
//        m_Paint.setAlpha(MyAlpha);
        m_Path = new Path();

        invalidate();
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

    public void setMyAlpha(int myAlpha) {
        MyAlpha = myAlpha;
    }


    public void saveNew(Context context) {
        if (paths.size() < 1) {
            Toast.makeText(context, "暂未修改", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = ImageUtil.getUriFromBitmap(getBitmap(), getTimeNew());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);  // 这是刷新单个文件
        intent.setData(uri);
        context.sendBroadcast(intent);
        SaveData saveData = new SaveData();
        saveData.setImageName(getFileName(uri.toString()));
        saveData.setPicturePath(ImageUtil.getRealFilePath(context, uri));
        daoSession.getSaveDataDao().insert(saveData);
        clearView();
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
    }

    public String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }

    }

    public static String getTimeNew() {
        String time;
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) +
                "-" + calendar.get(Calendar.HOUR_OF_DAY) + "-" + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.SECOND);
        return time;
    }

    public void updata(SaveData saveData, Context context) {
        if (paths.size() < 1) {
            Toast.makeText(context, "暂未修改", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = ImageUtil.getUriFromBitmap(getBitmap(), saveData.getImageName());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);  // 这是刷新单个文件
        intent.setData(uri);
        context.sendBroadcast(intent);
    }


    public void setStrokeSize(float strokeSize) {
        this.strokeSize = strokeSize;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        if (mode == STROKE || mode == ERASER)
            this.mode = mode;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }


    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        if (radioGroup.getCheckedRadioButtonId()!=0) {
            radioGroup.clearCheck();
        }
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
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
                touch_up(x,y);
                invalidate();
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        for (Pair<Path, Paint> p : paths) {
            canvas.drawPath(p.first, p.second);
        }
    }

    public Bitmap getDrawBitmap() {
        return bitmap;
    }

    private void touch_start(float x, float y) {
        // 清除undone list
        undonePaths.clear();

        if (mode == ERASER) {
            m_Paint.setColor(Color.WHITE);
            m_Paint.setStrokeWidth(eraserSize);
        } else {
            m_Paint.setColor(strokeColor);
            m_Paint.setStrokeWidth(strokeSize);
        }
        // 复制m_Paint
        Paint newPaint = new Paint(m_Paint);
        // 避免啥都没有的时候调用橡皮擦在那里乱擦
        if (!(paths.size() == 0 &&mode == ERASER && bitmap == null)) {
            paths.add(new Pair<>(m_Path, newPaint));
        }

        m_Path.reset();
        m_Path.moveTo(x, y);
        m_Path.quadTo(x, y, x, y);
        m_Path.addCircle(x, y, m_Paint.getStrokeWidth() / 64, Path.Direction.CCW);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        m_Path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        mX = x;
        mY = y;
    }

    private void touch_up(float x, float y) {
//        m_Path.moveTo(x, y);
//        m_Path.quadTo(x, y, mX, mY);
        // 复制m_Paint
        Paint newPaint = new Paint(m_Paint);
        // 避免啥都没有的时候调用橡皮擦在那里乱擦
        if (!(paths.size() == 0 && mode == ERASER && bitmap == null)) {
            paths.add(new Pair<>(m_Path, newPaint));
        }
        // 避免重复画两次
        m_Path = new Path();
    }

    // 返回画图结果用来保存
//    public Bitmap getBitmap() {
//        if (paths.size() == 0)
//            return null;
//        if (bitmap == null) {
//            bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
//            // 底色设置成白色
//            bitmap.eraseColor(Color.WHITE);
//        }
//        Canvas canvas = new Canvas(bitmap);
//        for (Pair<Path, Paint> p : paths) {
//            canvas.drawPath(p.first, p.second);
//        }
//        return bitmap;
//    }

    public Bitmap getBitmap() {
        if (paths.size() == 0) {
            return null;
        } else {
            Bitmap bitmap = Bitmap.createBitmap(this.getWidth(),
                    this.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            this.layout(0, 0, this.getWidth(), this.getHeight());
            this.draw(canvas);
            return bitmap;
        }
    }

    public void setBitmap(Bitmap bitmap) {
        Bitmap cbitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        this.bitmap = cbitmap;
//        cbitmap=null;
//        cbitmap.recycle();
    }

    // 撤销一笔
    public void BackView() {
        if (paths.size() >= 2) {
            undonePaths.add(paths.remove(paths.size() - 1));
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }
    }


    public int getUndoneCount() {
        return undonePaths.size();
    }

    public ArrayList<Pair<Path, Paint>> getPaths() {
        return paths;
    }

    public void setSize(int size, int eraserOrStroke) {
        switch (eraserOrStroke) {
            case STROKE:
                strokeSize = size;
                break;
            case ERASER:
                eraserSize = size;
                break;
        }
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }

    public void setStrokeColor(int color) {
        strokeColor = color;
    }


    // 删除所有画图，包括选择的图片
    public void clearView() {
        paths.clear();
        undonePaths.clear();
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
        invalidate();
    }

    public void setOnDrawChangedListener(OnDrawChangedListener listener) {
        this.onDrawChangedListener = listener;
    }

    public void recycle() {
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    public interface OnDrawChangedListener {
        void onDrawChanged();
    }


}