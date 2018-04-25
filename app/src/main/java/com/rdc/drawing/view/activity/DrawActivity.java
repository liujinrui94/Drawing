package com.rdc.drawing.view.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.rdc.drawing.R;
import com.rdc.drawing.adapter.BaseRecyclerAdapter;
import com.rdc.drawing.adapter.PictureAdapter;
import com.rdc.drawing.adapter.SmartViewHolder;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.state.BaseState;
import com.rdc.drawing.state.CircleState;
import com.rdc.drawing.state.EraserState;
import com.rdc.drawing.state.LineState;
import com.rdc.drawing.state.PathState;
import com.rdc.drawing.state.RectangleState;
import com.rdc.drawing.state.ShearState;
import com.rdc.drawing.utils.BaseProgressDialog;
import com.rdc.drawing.utils.CommandUtils;
import com.rdc.drawing.utils.DrawDataUtils;
import com.rdc.drawing.utils.FileUtils;
import com.rdc.drawing.utils.ScreenBrightness;
import com.rdc.drawing.view.widget.DrawView;
import com.rdc.drawing.view.widget.ModeSelectWindow;
import com.rdc.drawing.view.widget.QiuView;
import com.rdc.drawing.view.widget.VerticalSeekBar;
import com.rdc.drawing.view.widget.impl.ModeSelectCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrawActivity extends BaseActivity implements View.OnClickListener, ColorPicker.OnColorChangedListener, ModeSelectCallBack, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {

    private static final String TAG = "DrawActivity";
    //初始化对话框
    private AppCompatDialog mAppCompatDialog;
    private VerticalSeekBar mVerticalSeekBar;
//    private VerticalSeekBar  drawing_c_hb_seekBar;
    private DrawView mDrawView;

    private TextView mTVSelectMode;
    private TextView mTVPageSize;
    private String mPicturePath = null;
    private int mPageSize = 1;
    private ModeSelectWindow mModeSelectWindow;
    //抽屉
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PictureAdapter mPageAdapter;
    private List<String> list = null;
    private AlertDialog.Builder mBuilder;
    private VerticalSeekBar right_seekBar;

    private TextView drawing_progress;

    private int tmd = 0;

    private RecyclerView drawing_button_RecyclerView;

    private LinearLayout drawing_button_linearlayout;

    private BaseRecyclerAdapter baseRecyclerAdapter;

    private List<String> color_list = new ArrayList<>();

    private Toast toast = null;

    private QiuView qiuView;
    private View drawing_button_id;

    private Button btn_back;


    public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";

    private TextView draw_color_one;

    private ImageView c_xp;
    private boolean isxp = false;

//    private TextView c_hb;

//    private TextView c_light;

    private TextView drawing_color, drawing_c_hb_tv;

    private RadioGroup drawing_button_rg;

    private LinearLayout right_seekBar_ll,  drawing_c_tm_progress_ll;

//    private LinearLayout drawing_c_hb_ll;

    private LinearLayout ll_save, ll_reset, ll_redo;
    private BaseProgressDialog baseProgressDialog;
    private int stroke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw);
        initView();
        initDrawData();
        mDrawView.setRadioGroup(drawing_button_rg);
        mDrawView.setLinearLayout(drawing_button_linearlayout);
        mDrawView.setRecyclerView(drawing_button_RecyclerView);
    }

    private void initDrawData() {
        Intent intent = getIntent();
        mPicturePath = intent.getStringExtra("path");
        String filePath = intent.getStringExtra("filePath");
        if (mPicturePath != null) {
            Log.e("lichaojian--path", mPicturePath);
            String xmlPath = mPicturePath.substring(0, mPicturePath.length() - 3) + "xml";
            DrawDataUtils.getInstance().structureReReadXMLData("file://" + xmlPath);
            mDrawView.drawFromData();
            mDrawView.addCommand();
            if (!filePath.equals("null") && filePath != null) {
                File file = new File(filePath);
                File[] allFiles = file.listFiles();
                for (int i = 0; i < allFiles.length; ++i) {
                    if (allFiles[i].getPath().contains("png")) {
                        list.add(allFiles[i].getPath());
                    }
                }
            }
        }

        mPageSize = list.size();
        if (mPageSize == 0) {
            mPageSize = 1;
        }
        mTVPageSize.setText(Integer.toString(mPageSize));
    }

    @Override
    public void initView() {
        color_list.add("#000000");
        color_list.add("#2020FE");
        color_list.add("#FF0909");
        color_list.add("#49972E");
        color_list.add("#FFFFFF");
        color_list.add("#787878");

        color_list.add("#A50801");
        color_list.add("#D84F19");
        color_list.add("#EE5509");
        color_list.add("#F09B1A");
        color_list.add("#F6C119");
        color_list.add("#F4E651");
//
        color_list.add("#D0DB1B");
        color_list.add("#AFD244");
        color_list.add("#09621D");
        color_list.add("#71ACD6");
        color_list.add("#001E86");
        color_list.add("#A576EA");
//
        color_list.add("#B669EB");
        color_list.add("#B24298");
        color_list.add("#E65AA8");
        color_list.add("#F8467E");
        color_list.add("#FFC5D1");
        color_list.add("#FFCCFF");
        drawing_color = (TextView) findViewById(R.id.drawing_color);


        ImageView drawing_go_back= (ImageView) findViewById(R.id.drawing_go_back);
        drawing_color.setWidth(drawing_go_back.getWidth());
        drawing_color.setHeight(drawing_go_back.getHeight());

        baseProgressDialog = new BaseProgressDialog(DrawActivity.this);
        qiuView = new QiuView(DrawActivity.this);
        drawing_button_id = findViewById(R.id.drawing_button_id);
        drawing_button_rg = (RadioGroup) findViewById(R.id.drawing_button_rg);
//        drawing_c_hb_tv = (TextView) findViewById(R.id.drawing_c_hb_tv);
        drawing_button_RecyclerView = (RecyclerView) findViewById(R.id.drawing_button_RecyclerView);
        drawing_button_linearlayout = (LinearLayout) findViewById(R.id.drawing_button_linearlayout);
        drawing_button_RecyclerView.setNestedScrollingEnabled(false);
        drawing_button_RecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 12));
        drawing_button_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        drawing_button_RecyclerView.setHasFixedSize(true);

        right_seekBar_ll = (LinearLayout) findViewById(R.id.right_seekBar_ll);
//        drawing_c_hb_ll = (LinearLayout) findViewById(R.id.drawing_c_hb_ll);
        drawing_c_tm_progress_ll = (LinearLayout) findViewById(R.id.drawing_c_tm_progress_ll);
        baseRecyclerAdapter = new BaseRecyclerAdapter<String>(color_list, R.layout.color_text_item) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, final String model, int position) {
                holder.itemView.findViewById(R.id.color_text_item_textView).setBackgroundColor(Color.parseColor(model));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawing_color.setBackgroundColor(Color.parseColor(model));
                        mDrawView.setCurrentState(PathState.getInstance());
                        mDrawView.changePaintColor(Color.parseColor(model));
                        c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp));
                        isxp = false;
//                        mDrawView.setMyAlpha(drawing_c_hb_seekBar.getProgress(), Color.parseColor(model), mVerticalSeekBar.getProgress());
                        drawing_button_RecyclerView.setVisibility(View.GONE);
                        drawing_button_linearlayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        drawing_button_RecyclerView.setAdapter(baseRecyclerAdapter);
        drawing_progress = (TextView) findViewById(R.id.drawing_progress);

        findViewById(R.id.draw_color_one).setOnClickListener(this);
        findViewById(R.id.draw_color_two).setOnClickListener(this);
        findViewById(R.id.draw_color_three).setOnClickListener(this);
        findViewById(R.id.drawing_go_back).setOnClickListener(this);
        findViewById(R.id.drawing_go_clear).setOnClickListener(this);
        findViewById(R.id.drawing_color).setOnClickListener(this);
//        c_hb = (TextView) findViewById(R.id.drawing_c_hb);
//        c_hb.setOnClickListener(this);
//        c_light = (TextView) findViewById(R.id.drawing_c_light);
//        c_light.setOnClickListener(this);
//        findViewById(R.id.drawing_c_tm).setOnClickListener(this);
        c_xp = (ImageView) findViewById(R.id.drawing_c_xp);
        c_xp.setOnClickListener(this);
        //初始化颜色板
        initColorPickerDialog();
        //初始化自定义的ToolBar
        initToolbar();
        mDrawView = (DrawView) findViewById(R.id.draw_view);
        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.seekBar);
//        drawing_c_hb_seekBar = (VerticalSeekBar) findViewById(R.id.drawing_c_hb_seekBar);
        right_seekBar = (VerticalSeekBar) findViewById(R.id.right_seekBar);


        right_seekBar.setProgress(ScreenBrightness.getScreenBrightness(getBaseContext()));

//        mVerticalSeekBar.setOnSeekBarChangeListener(this);
//        drawing_c_hb_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean bo) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

//        drawing_c_hb_seekBar.setMaxProgress(255);
        mVerticalSeekBar.setMaxProgress(255);
        right_seekBar.setMaxProgress(255);
//        drawing_c_hb_seekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
//            @Override
//            public void onStart(VerticalSeekBar slideView, int progress) {
//
//            }
//
//            @Override
//            public void onProgress(VerticalSeekBar slideView, int progress) {
//                mDrawView.getmPaint().setColor(mDrawView.getmPaint().getColor());
//                mDrawView.setMyAlpha(drawing_c_hb_seekBar.getProgress(), mDrawView.getmPaint().getColor(), mVerticalSeekBar.getProgress());
//                tmd = progress;
//                double a = progress;
//                double b = 255;
//                double d = a / b;
//                drawing_c_hb_tv.setText(String.format("%.2f", d));
//            }
//
//            @Override
//            public void onStop(VerticalSeekBar slideView, int progress) {
//
//            }
//        });
        mVerticalSeekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {

            }

            @Override
            public void onProgress(VerticalSeekBar slideView, int progress) {
                mDrawView.changePaintSize(progress);
                double a = progress;
                double b = 255;
                double d = a / b;
                TextView textView = (TextView) findViewById(R.id.drawing_c_tm_progress);
                textView.setText(String.format("%.2f", d));
                ToastUtil(progress);
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {

            }
        });
//        right_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
        right_seekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {

            }

            @Override
            public void onProgress(VerticalSeekBar slideView, int progress) {
                ScreenBrightness.setWindowBrightness(progress, DrawActivity.this);
                double a = progress;
                double b = 255;
                double d = a / b;
                drawing_progress.setText(String.format("%.2f", d));
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {

            }
        });






        drawing_button_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                drawing_c_tm_progress_ll.setVisibility(View.INVISIBLE);
//                drawing_c_hb_ll.setVisibility(View.INVISIBLE);
                right_seekBar_ll.setVisibility(View.INVISIBLE);
                switch (radioGroup.getCheckedRadioButtonId()) {
                   case R.id.rb_two:
                        drawing_c_tm_progress_ll.setVisibility(View.VISIBLE);
                        break;
                  /*  case R.id.rb_three:
                        drawing_c_hb_ll.setVisibility(View.VISIBLE);
                        break;*/
                    case R.id.rb_four:
                        right_seekBar_ll.setVisibility(View.VISIBLE);
                        break;

                }

            }
        });





        mDrawView.changePaintSize(mVerticalSeekBar.getProgress());
        //初始化TAB
        initNavigationTab();
        //初始化模式选择的PopupWindow
        initModeSelectWindow();
        initDrawerLayout();
        initDialog();
    }

    private void initDialog() {
        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("提示");
        mBuilder.setMessage("是否保存当前画布");
        mBuilder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });
        mBuilder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DrawActivity.this.finish();
            }
        });
    }

    private void initModeSelectWindow() {
        mModeSelectWindow = new ModeSelectWindow(this, R.layout.mode_popuwindow);
        mModeSelectWindow.setModeSelectCallBack(this);
    }

    /**
     * 初始化自定义toolbar
     */
    private void initToolbar() {
        findViewById(R.id.ll_back).setOnClickListener(this);
        findViewById(R.id.ll_num).setOnClickListener(this);
        findViewById(R.id.ll_add).setOnClickListener(this);
        findViewById(R.id.ll_undo).setOnClickListener(this);
        findViewById(R.id.ll_redo).setOnClickListener(this);
        findViewById(R.id.ll_reset).setOnClickListener(this);
        findViewById(R.id.ll_save).setOnClickListener(this);
        ll_save = (LinearLayout) findViewById(R.id.ll_save);
        ll_reset = (LinearLayout) findViewById(R.id.ll_reset);
        ll_redo = (LinearLayout) findViewById(R.id.ll_redo);
        mTVPageSize = (TextView) findViewById(R.id.tv_page_size);
    }

    /**
     * 初始化导航Tab
     */
    private void initNavigationTab() {
        findViewById(R.id.rl_color_select_dialog).setOnClickListener(this);
        findViewById(R.id.rl_pencil_menu_select).setOnClickListener(this);
        findViewById(R.id.rl_mode_select).setOnClickListener(this);
        mTVSelectMode = (TextView) findViewById(R.id.tv_select_mode);
        findViewById(R.id.rl_shear).setOnClickListener(this);
        findViewById(R.id.rl_hard_eraser).setOnClickListener(this);

        draw_color_one = (TextView) findViewById(R.id.draw_color_one);
    }

    /**
     * 初始化一个颜色选择板块
     */
    private void initColorPickerDialog() {
        mAppCompatDialog = new AppCompatDialog(this);
        mAppCompatDialog.setContentView(R.layout.dialog_color_picker);
        ColorPicker colorPicker = (ColorPicker) mAppCompatDialog.findViewById(R.id.picker);
        SVBar svBar = (SVBar) mAppCompatDialog.findViewById(R.id.sv_bar);
        OpacityBar opacityBar = (OpacityBar) mAppCompatDialog.findViewById(R.id.opacity_bar);
        SaturationBar saturationBar = (SaturationBar) mAppCompatDialog.findViewById(R.id.saturation_bar);//饱和度
        ValueBar valueBar = (ValueBar) mAppCompatDialog.findViewById(R.id.value_bar);

        colorPicker.addSVBar(svBar);
        colorPicker.addOpacityBar(opacityBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);

        colorPicker.getColor();
        colorPicker.setOldCenterColor(colorPicker.getColor());
        colorPicker.setOnColorChangedListener(this);
        colorPicker.setShowOldCenterColor(false);
    }

//    private void chongzhi() {
//        c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp));
//        isxp = false;
//        right_seekBar.setVisibility(View.GONE);
//        drawing_c_hb_seekBar.setVisibility(View.GONE);
//        mVerticalSeekBar.setVisibility(View.GONE);
//        mDrawView.setCanDraw(true);
//
//        Drawable rightDrawable = getResources().getDrawable(R.mipmap.c_light);
//        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
//        c_light.setCompoundDrawables(null, rightDrawable, null, null);
//
//        Drawable rightDrawable1 = getResources().getDrawable(R.mipmap.c_hb_not);
//        rightDrawable1.setBounds(0, 0, rightDrawable1.getMinimumWidth(), rightDrawable1.getMinimumHeight());
//        c_hb.setCompoundDrawables(null, rightDrawable1, null, null);
//
//    }

    private void initDrawerLayout() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        final ListView mListView = (ListView) findViewById(R.id.listView);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                null, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                mPageAdapter.notifyDataSetChanged();
            }

        };

        list = new ArrayList<>();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mPageAdapter = new PictureAdapter(this, list);
//        mListView.setAdapter(mPageAdapter);
//        mListView.setOnItemClickListener(this);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        right_seekBar_ll.setVisibility(View.INVISIBLE);
//        drawing_c_hb_ll.setVisibility(View.INVISIBLE);
        drawing_c_tm_progress_ll.setVisibility(View.INVISIBLE);
        drawing_button_rg.clearCheck();
        ColorDrawable colorDrawable = (ColorDrawable) draw_color_one.getBackground();//获取背景颜色
        colorDrawable.getColor();
        switch (v.getId()) {
            case R.id.draw_color_one://第一个颜色
//                mDrawView.setCurrentState(PathState.getInstance());
//                drawing_color.setBackgroundColor(colorDrawable.getColor());
//                mDrawView.setAlpha(tmd);
//                c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp));
//                isxp = false;
//                mDrawView.changePaintColor( mDrawView.getmPaint().getColor());
//                mDrawView.changePaintSize(mVerticalSeekBar.getProgress());

                drawing_color.setBackgroundColor(Color.parseColor("#000000"));
                mDrawView.setCurrentState(PathState.getInstance());
                c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp));
//                mDrawView.setMyAlpha(drawing_c_hb_seekBar.getProgress(), mDrawView.getmPaint().getColor(), mVerticalSeekBar.getProgress());
                mDrawView.changePaintColor(Color.parseColor("#000000"));
                mDrawView.changePaintSize(mVerticalSeekBar.getProgress());
                isxp = false;

                break;

            case R.id.draw_color_two://第二个颜色

//                mDrawView.setCurrentState(PathState.getInstance());
//                ColorDrawable colorDrawable = (ColorDrawable) draw_color_one.getBackground();//获取背景颜色
//                colorDrawable.getColor();
//                mDrawView.changePaintColor(colorDrawable.getColor());
                drawing_color.setBackgroundColor(Color.parseColor("#0000FE"));
                mDrawView.setCurrentState(PathState.getInstance());
                c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp));
//                mDrawView.setMyAlpha(drawing_c_hb_seekBar.getProgress(), mDrawView.getmPaint().getColor(), mVerticalSeekBar.getProgress());
                mDrawView.changePaintColor( Color.parseColor("#0000FE"));
                mDrawView.changePaintSize(mVerticalSeekBar.getProgress());
                isxp = false;
                break;

            case R.id.draw_color_three://第三个颜色
                drawing_color.setBackgroundColor(Color.parseColor("#FE0000"));
                mDrawView.setCurrentState(PathState.getInstance());
                mDrawView.changePaintColor(Color.parseColor("#FE0000"));
                c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp));
//                mDrawView.setMyAlpha(drawing_c_hb_seekBar.getProgress(), mDrawView.getmPaint().getColor(), mVerticalSeekBar.getProgress());
                mDrawView.changePaintColor( mDrawView.getmPaint().getColor());
                mDrawView.changePaintSize(mVerticalSeekBar.getProgress());
                isxp = false;
                break;


            case R.id.drawing_color://选择颜色
//                mAppCompatDialog.show();
                if (drawing_button_linearlayout.getVisibility() == View.VISIBLE) {
                    drawing_button_linearlayout.setVisibility(View.GONE);
                    drawing_button_RecyclerView.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.drawing_go_back://撤回一步

                mDrawView.setCanvasCode(NoteApplication.CANVAS_UNDO);
                mDrawView.invalidate();

                break;

            case R.id.drawing_go_clear://清除滑板

                mDrawView.setCanvasCode(NoteApplication.CANVAS_RESET);
                mDrawView.invalidate();
                dataReset();

                break;
            case R.id.drawing_c_xp://橡皮
                if (isxp) {
                    mDrawView.setCurrentState(PathState.getInstance());
                    colorDrawable = (ColorDrawable) drawing_color.getBackground();
                    colorDrawable.getColor();
                    mDrawView.changePaintColor(colorDrawable.getColor());
                    c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp));
                    isxp = false;
                } else {
                    mDrawView.setCurrentState(EraserState.getInstance());
                    c_xp.setImageDrawable(getResources().getDrawable(R.mipmap.c_xp_not));
                    isxp = true;
                }


                break;
//            case R.id.drawing_c_tm://粗细
//                mDrawView.setCanDraw(false);
//                if (mVerticalSeekBar.getVisibility() == View.VISIBLE) {
//                    mVerticalSeekBar.setVisibility(View.GONE);
//                } else {
//                    mVerticalSeekBar.setVisibility(View.VISIBLE);
//                }
//                break;
//            case R.id.drawing_c_hb://透明
//                mDrawView.setCanDraw(false);
//                if (drawing_c_hb_seekBar.getVisibility() == View.GONE) {
//                    drawing_c_hb_seekBar.setVisibility(View.VISIBLE);
//
//                    Drawable rightDrawable = getResources().getDrawable(R.mipmap.c_hb);
//                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
////                    c_hb.setCompoundDrawables(null, rightDrawable, null, null);
//                } else {
//                    drawing_c_hb_seekBar.setVisibility(View.GONE);
//                    Drawable rightDrawable = getResources().getDrawable(R.mipmap.c_hb_not);
//                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
////                    c_hb.setCompoundDrawables(null, rightDrawable, null, null);
//                }
//                break;

            case R.id.ll_back:
                if (ll_redo.getVisibility() == View.VISIBLE) {
                    ll_redo.setVisibility(View.INVISIBLE);
                    ll_reset.setVisibility(View.INVISIBLE);
                    ll_save.setVisibility(View.INVISIBLE);
                    drawing_button_id.setVisibility(View.GONE);
                    findViewById(R.id.btn_back).setBackground(getResources().getDrawable(R.mipmap.shrink));
                } else {
                    drawing_button_id.setVisibility(View.VISIBLE);
                    ll_redo.setVisibility(View.VISIBLE);
                    ll_reset.setVisibility(View.VISIBLE);
                    ll_save.setVisibility(View.VISIBLE);
                    drawing_button_id.setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_back).setBackground(getResources().getDrawable(R.mipmap.full_screen));
                }

                break;
            case R.id.ll_num:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.ll_add:
                mTVPageSize.setText(Integer.toString(++mPageSize));
                String fileName = mDrawView.save(mPicturePath, NoteApplication.TEMPORARY_PATH);
                list.add(NoteApplication.TEMPORARY_PATH + "/" + fileName + ".png");
                mDrawView.setCanvasCode(NoteApplication.CANVAS_RESET);
                mDrawView.invalidate();
                dataReset();
                break;
            case R.id.ll_undo:
                mDrawView.setCanvasCode(NoteApplication.CANVAS_UNDO);
                mDrawView.invalidate();
                break;
            case R.id.ll_redo:
//                mDrawView.setCanvasCode(NoteApplication.CANVAS_REDO);
//                mDrawView.invalidate();
//                NoteApplication.getInstance().AppExit();

                try {
                    Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"}); //关机
                    proc.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_reset:
                // TODO: 2016/8/8 在此处弹出dialog让用户确认清空画布
//                mDrawView.setCanvasCode(NoteApplication.CANVAS_RESET);
//                mDrawView.invalidate();
//                dataReset();
//                mBuilder.show();
//                save();
                startActivity(new Intent(DrawActivity.this, MainActivity.class));
                DrawActivity.this.finish();
                break;
            case R.id.ll_save:
//                if (mPageSize > 1) {
//                    copy();
//                } else {
//                    save();
//                }
                save();
//                startActivity(new Intent(DrawActivity.this,MainActivity.class));

                break;
            case R.id.rl_pencil_menu_select:
                break;
            case R.id.rl_color_select_dialog:
                mAppCompatDialog.show();
                break;
            case R.id.rl_mode_select:
                mModeSelectWindow.showPopupWindow(v);
                break;
            case R.id.rl_shear:
                //mDrawView.setCurrentState(ShearState.getInstance(mDrawView));
                break;
            case R.id.rl_hard_eraser:
                mDrawView.setCurrentState(EraserState.getInstance());
                break;
//            case R.id.ll_qunping:
//                if (drawing_button_id.getVisibility() == View.VISIBLE) {
//                    drawing_button_id.setVisibility(View.GONE);
//                    findViewById(R.id.toolbar).setVisibility(View.GONE);
//                    ll_qunping.setVisibility(View.VISIBLE);
//                } else {
//
//                    findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
//                    findViewById(R.id.btn_back).setBackground(getResources().getDrawable(R.mipmap.full_screen));
//                    ll_qunping.setVisibility(View.GONE);
//                }
//                break;

            default:
                Log.e(TAG, Integer.toString(v.getId()));
                break;
        }
    }

    @Override
    public void onColorChanged(int color) {
        mDrawView.changePaintColor(color);
    }

    @Override
    public void onSelectMode(int id) {
        BaseState baseState;
        switch (id) {
            case R.id.rl_normal_mode_select:
                mTVSelectMode.setText(R.string.mode_path);
                baseState = PathState.getInstance();
                break;
            case R.id.rl_line_mode_select:
                mTVSelectMode.setText(R.string.mode_line);
                baseState = LineState.getInstance();
                break;
            case R.id.rl_rectangle_mode_select:
                mTVSelectMode.setText(R.string.mode_rectangle);
                baseState = RectangleState.getInstance();
                break;
            case R.id.rl_circle_mode_select:
                mTVSelectMode.setText(R.string.mode_circle);
                baseState = CircleState.getInstance();
                break;
            default:
                baseState = PathState.getInstance();
                break;
        }

        mDrawView.setCurrentState(baseState);
    }

    @Override
    public void finish() {
        super.finish();
        dataReset();
        stateReset();
        File temporaryFile = new File(NoteApplication.TEMPORARY_PATH);
        if (temporaryFile.exists()) {
            FileUtils.delete(temporaryFile);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void dataReset() {
        DrawDataUtils.getInstance().getSaveDrawDataList().clear();
        DrawDataUtils.getInstance().getShearDrawDataList().clear();
        CommandUtils.getInstance().getRedoCommandList().clear();
        CommandUtils.getInstance().getUndoCommandList().clear();
    }

    private void stateReset() {
        PathState.getInstance().destroy();
        LineState.getInstance().destroy();
        RectangleState.getInstance().destroy();
        CircleState.getInstance().destroy();
        EraserState.getInstance().destroy();
        ShearState.getInstance(mDrawView).destroy();
    }


    private void save() {

        if (baseProgressDialog != null)
            baseProgressDialog.cancel();
        baseProgressDialog.setDialogDetail("");
        baseProgressDialog.show();
        String fileName = mDrawView.save(mPicturePath, NoteApplication.ROOT_DIRECTORY);
        if (DrawDataUtils.getInstance().getSaveDrawDataList().size() > 0 && fileName != null) {
            showToast("保存成功");
            Intent intent = new Intent();
            intent.putExtra("path", NoteApplication.ROOT_DIRECTORY + "/" + fileName + ".png");
            intent.putExtra("filePath", "null");
            if (mPicturePath == null) {
                setResult(NoteApplication.OK, intent);
            } else {
                setResult(NoteApplication.CANCEL, intent);
            }
            mDrawView.setCanvasCode(NoteApplication.CANVAS_RESET);
            mDrawView.invalidate();
            dataReset();
        } else {
            showToast("保存失败,请检查当前画布是否为空。");

        }
        baseProgressDialog.dismiss();
        mPicturePath = null;
    }

    private void copy() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date currentDate = new Date(System.currentTimeMillis());
        String newFilePath = simpleDateFormat.format(currentDate);
        File bookFile = new File(NoteApplication.ROOT_DIRECTORY + "/" + newFilePath);
        if (!bookFile.exists()) {
            bookFile.mkdir();
        }
        FileUtils.fileCopy(NoteApplication.TEMPORARY_PATH, NoteApplication.ROOT_DIRECTORY + "/" + newFilePath);
        Intent intent = new Intent();
        String path = list.get(0).split("/")[list.get(0).split("/").length - 1];
        intent.putExtra("path", NoteApplication.ROOT_DIRECTORY + "/" + newFilePath + "/" + path);
        intent.putExtra("filePath", NoteApplication.ROOT_DIRECTORY + "/" + newFilePath);
        setResult(NoteApplication.OK, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTVPageSize.setText(Integer.toString(position + 1));
        String pngPath = list.get(position);
        String xmlPath = pngPath.substring(0, pngPath.length() - 3) + "xml";
        mDrawView.reset();
        dataReset();
        DrawDataUtils.getInstance().structureReReadXMLData("file://" + xmlPath);
        mDrawView.drawFromData();
        mDrawView.addCommand();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (DrawDataUtils.getInstance().getSaveDrawDataList().size() > 0) {
                mBuilder.show();
            } else {
                DrawActivity.this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void ToastUtil(int radius) {

        qiuView.setRadius(radius);
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(DrawActivity.this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(qiuView);
        toast.show();
    }
}
