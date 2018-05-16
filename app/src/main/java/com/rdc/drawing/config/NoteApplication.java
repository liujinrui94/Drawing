package com.rdc.drawing.config;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.rdc.drawing.board.dao.DaoMaster;
import com.rdc.drawing.board.dao.DaoSession;
import com.rdc.drawing.utils.CrashHandler;
import com.rdc.drawing.view.activity.BaseActivity;
import com.squareup.leakcanary.RefWatcher;

import java.util.Stack;

/**
 * Created by lichaojian on 16-8-28.
 */
public class NoteApplication extends Application {

    public static final int MODE_PATH = 0;
    public static final int MODE_LINE = 1;
    public static final int MODE_RECTANGLE = 2;
    public static final int MODE_CIRCLE = 3;
    public static final int MODE_SHEAR = 4;
    public static final int MODE_ERASER = 5;

    public static final int CANVAS_NORMAL = 0;
    public static final int CANVAS_UNDO = 1;
    public static final int CANVAS_REDO = 2;
    public static final int CANVAS_RESET = 3;

    public static final int COMMAND_ADD = 0;
    public static final int COMMAND_TRANSLATE = 4;
    public static final int COMMAND_SCALE = 5;

    public static final String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/note";
    public static final String TEMPORARY_PATH = ROOT_DIRECTORY + "/temporary";
    public static final int OK = 1;
    public static final int CANCEL = -1;
    public Stack<BaseActivity> allActivity = new Stack<>();

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private RefWatcher refWatcher;
    //静态单例
    public static NoteApplication instances;
    public static NoteApplication getsInstance() {
        return instances;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
//        LeakCanary.install(this);
//        refWatcher = LeakCanary.install(this);
        CrashHandler.getInstance().init(this);
        setDatabase();
    }
    public static RefWatcher getRefWatcher(Context context) {
        NoteApplication application = (NoteApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public static NoteApplication getInstance() {
        return instances;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "drawing-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }


    public void addActivity(BaseActivity activity) {
        if (allActivity == null) {
            allActivity = new Stack<>();
        }
        allActivity.add(activity);
    }

    public void finishActivity(BaseActivity activity) {
        if (activity != null) {
            allActivity.remove(activity);
        }
    }

    public void finishAllActivity() {
        for (int i = 0, size = allActivity.size(); i < size; i++) {
            if (null != allActivity.get(i)) {
                allActivity.get(i).finish();
            }
        }
        allActivity.clear();
    }


    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
