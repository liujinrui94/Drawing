package com.rdc.drawing.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.rdc.drawing.R;

import java.io.File;

import static android.R.attr.animation;

/**
 * @author: FengWenyao
 * @email: fengwenyao@qdcftx.com
 * @time: 2017/7/6 10:58
 * @description: 图片加载
 */


public class GlideUtils {
    private static GlideUtils instance = new GlideUtils();

    public static GlideUtils getInstance() {
        return instance;
    }

    public void loadNetImage(String url, ImageView imageView) {
        loadNetImage(url, imageView, R.mipmap.ic_launcher);
    }

    private void loadNetImage(String url, ImageView imageView, int defaultImg) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(defaultImg)
                .error(defaultImg)
                .animate(animation)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }
    public void loadNetImageNoDefaultImg(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .animate(animation)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }


    /**
     * 加载圆形网络图片(默认图片)
     *
     * @param url       地址
     * @param imageView 加载控件
     */
    public void loadNetCircleDefaultImage(String url, ImageView imageView) {
        loadNetCircleImage(url, imageView, R.mipmap.ic_launcher);
    }

    /**
     * 加载圆形网络图片（可定义图片）
     *
     * @param url        地址
     * @param imageView  加载控件
     * @param defaultImg 错误加载图片
     */
    private void loadNetCircleImage(String url, ImageView imageView, int defaultImg) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(defaultImg)
                .error(defaultImg)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .crossFade()
                .into(imageView);
    }

    /**
     * 加载圆角网络图片(默认图片)
     *
     * @param url       地址
     * @param imageView 加载控件
     */
    public void loadNetRoundDefaultImage(String url, ImageView imageView) {
        loadNetRoundImage(url, imageView, R.mipmap.ic_launcher, 15);
    }

    /**
     * 加载圆角网络图片(可定义角度和背景图片)
     *
     * @param url        地址
     * @param imageView  加载控件
     * @param defaultImg 默认图片
     * @param radius     角度
     */
    private void loadNetRoundImage(String url, ImageView imageView, int defaultImg, int radius) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .transform(new CenterCrop(context), new GlideRoundTransform(context, radius))
                .placeholder(defaultImg)
                .error(defaultImg)
                .crossFade()
                .into(imageView);
    }


    //加载本地图片
    public void loadLocalImage(int resId, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(resId)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    //加载本地图片
    public void loadLocalImage(File file, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(file)
                .diskCacheStrategy( DiskCacheStrategy.NONE )//禁用磁盘缓存
                .skipMemoryCache(true)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    //加载本地圆形图片
    public void loadLocalCircleImage(int resId, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(resId)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .transform(new GlideCircleTransform(imageView.getContext()))
                .centerCrop()
                .into(imageView);
    }

    //加载本地圆角图片
    public void loadLocalRoundImage(int resId, ImageView imageView, int radius) {
        Glide.with(imageView.getContext())
                .load(resId)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .transform(new CenterCrop(imageView.getContext()), new GlideRoundTransform(imageView.getContext(), radius))
                .centerCrop()
                .into(imageView);
    }

    //加载本地圆形file图片
    public void loadLocalCircleImage(File file, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(file)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy( DiskCacheStrategy.NONE )//禁用磁盘缓存
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .transform(new GlideCircleTransform(imageView.getContext()))
                .centerCrop()
                .into(imageView);
    }

}