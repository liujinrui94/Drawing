package com.rdc.drawing.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rdc.drawing.R;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.utils.FileUtils;
import com.rdc.drawing.utils.GlideUtils;
import com.rdc.drawing.view.activity.DrawActivity;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by lichaojian on 16-8-28.
 */
public class MainAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map<String, String>> mPathList;

    public MainAdapter(Context context, List<Map<String, String>> pathList) {
        mContext = context;
        mPathList = pathList;
    }

    @Override
    public int getCount() {
        return mPathList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_item, null);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.delect_text_view = (ImageView) convertView.findViewById(R.id.delect_text_view);
            convertView.setTag(holder);

            holder.delect_text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    mBuilder.setTitle("提示");
                    mBuilder.setMessage("是删除此项");
                    mBuilder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(mPathList.get(position).get("picturePath"));
                            FileUtils.delete(file);
                            mPathList.remove(position);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    mBuilder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                }
            });


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NoteApplication.getInstance(mContext).displayImage("file:///" + mPathList.get(position).get("picturePath"), holder.mImageView);

//        GlideUtils.getInstance().loadLocalImage(new File("file:///" + mPathList.get(position).get("picturePath")),holder.mImageView);

//        Glide.with(mContext).load(new File("file:///" + mPathList.get(position).get("picturePath"))).into(holder.mImageView);

        return convertView;
    }

    static class ViewHolder {
        ImageView mImageView;

        ImageView delect_text_view;
    }
}
