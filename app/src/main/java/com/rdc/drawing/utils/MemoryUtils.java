package com.rdc.drawing.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;

/**
 * Created by Edianzu on 2018/5/10.
 */

public class MemoryUtils {


    public static String getAvailMemory(Context mContext) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(mContext, mi.availMem);// 将获取的内存大小规格化
    }


    public static String getTotalMemory(Context mContext) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(mContext, mi.totalMem);// 将获取的内存大小规格化
    }

}
