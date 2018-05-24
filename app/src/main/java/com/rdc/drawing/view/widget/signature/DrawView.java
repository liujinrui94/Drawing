package com.rdc.drawing.view.widget.signature;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rdc.drawing.bean.SaveData;
import com.rdc.drawing.board.dao.DaoSession;
import com.rdc.drawing.board.dao.SaveDataDao;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.utils.ImageUtil;
import com.rdc.drawing.utils.PreUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DrawView extends View implements View.OnTouchListener {

    private List<FirstPoint> points;
    private Paint paint;
    private Context mContext;

    private Bitmap bitmap;

    private int PaintSize, alpha = 255;
    private int PaintColor = Color.parseColor("#000000");

    private RadioGroup radioGroup;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private DaoSession daoSession = NoteApplication.getsInstance().getDaoSession();

    public DrawView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public void DrawBitmap(Bitmap mBitmap) {
        this.bitmap = mBitmap;
        invalidate();
    }

    public Boolean getHasDraw() {
        if (points.size() < 1) {
            return true;
        } else {
            return false;
        }
    }

    private void init() {
        points = new ArrayList<>();
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
    }

    public void BackView() {
        if (points.size() > 0) {
            points.remove(points.size() - 1);
            invalidate();
        }
    }

    public void clearView() {
        points.clear();
        invalidate();
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }


    public void setMyAlpha(int alpha) {
        this.alpha = alpha;
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

    /**
     * 返回图片
     *
     * @return
     * @throws IOException
     */
    public Bitmap saveSignature() {
        if (points.size() == 0) {
            return null;
        } else {
            Bitmap bitmap = Bitmap.createBitmap(this.getWidth(),
                    this.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            this.draw(canvas);
            return bitmap;
        }
    }

    public static String getTimeNew() {
        String time;
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) +
                "-" + calendar.get(Calendar.HOUR_OF_DAY) + "-" + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.SECOND);
        return time;
    }

    public void saveNew(Context context) {
        if (points.size() < 1) {
            Toast.makeText(context, "暂未修改", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = ImageUtil.getUriFromBitmap(saveSignature(), getTimeNew());
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

    public void updata(SaveData saveData, Context context) {
        if (points.size() < 1) {
            Toast.makeText(context, "暂未修改", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = ImageUtil.getUriFromBitmap(saveSignature(), saveData.getImageName());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);  // 这是刷新单个文件
        intent.setData(uri);
        context.sendBroadcast(intent);
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

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        for (FirstPoint point : points) {
            point.draw(canvas, paint);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (radioGroup != null) {
            radioGroup.clearCheck();
        }
        if (recyclerView.getVisibility() == VISIBLE) {
            recyclerView.setVisibility(GONE);
            linearLayout.setVisibility(VISIBLE);
        }

        int action = event.getAction();
        FirstPoint point;
        if (action == MotionEvent.ACTION_MOVE) {
            float firstX = event.getX();
            float firstY = event.getY();
            point = new FollowPoints(firstX, firstY, PaintColor,
                    PaintSize, points.get(points.size() - 1), alpha);

        } else if (action == MotionEvent.ACTION_DOWN) {
            float lastX = event.getX();
            float lastY = event.getY();
            point = new FirstPoint(lastX, lastY, PaintColor, PaintSize, alpha);
        } else {
            return false;
        }
        points.add(point);

        invalidate();
        return true;
    }

    public void changePaintColor(int i) {

        PaintColor = i;

    }


    public void changePaintSize(int i) {
        PaintSize = i;

    }
}
