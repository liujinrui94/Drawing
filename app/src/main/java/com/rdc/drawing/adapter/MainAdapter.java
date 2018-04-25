package com.rdc.drawing.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.drawing.R;
import com.rdc.drawing.bean.HomeBean;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.utils.FileUtils;
import com.rdc.drawing.view.activity.DrawActivity;

import java.io.File;
import java.util.List;

/**
 * Created by lichaojian on 16-8-28.
 */
public class MainAdapter extends BaseAdapter {
    private Activity mContext;
    private List<HomeBean> mPathList;

    public MainAdapter(Activity context, List<HomeBean> pathList) {
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
            holder.yulan_text_view = (TextView) convertView.findViewById(R.id.yulan_text_view);

            holder.select_image = (ImageView) convertView.findViewById(R.id.select_image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);


            holder.yulan_text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DrawActivity.class);
                    intent.putExtra("path", mPathList.get(position).getMap().get("picturePath"));
                    intent.putExtra("filePath", mPathList.get(position).getMap().get("filePath"));
                    mContext.startActivityForResult(intent, 2);

                }
            });

            holder.delect_text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    mBuilder.setTitle("提示");
                    mBuilder.setMessage("是删除此项");
                    mBuilder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(mPathList.get(position).getMap().get("picturePath"));
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
                    mBuilder.show();
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mPathList.get(position).isSelect()) {
            holder.select_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.w_select));
        } else {
            holder.select_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.w_noselect));
        }
        NoteApplication.getInstance(mContext).displayImage("file:///" + mPathList.get(position).getMap().get("picturePath"), holder.mImageView);
        holder.name.setText(
                (mPathList.get(position).getMap().get("picturePath").substring(mPathList.get(position).getMap().get("picturePath").lastIndexOf("/") + 1, mPathList.get(position).getMap().get("picturePath").length())
                ).replace(".png", "")
        );
//        GlideUtils.getInstance().loadLocalImage(new File("file:///" + mPathList.get(position).get("picturePath")),holder.mImageView);

//        Glide.with(mContext).load(new File("file:///" + mPathList.get(position).get("picturePath"))).into(holder.mImageView);

        return convertView;
    }

    static class ViewHolder {
        ImageView mImageView;

        ImageView delect_text_view;

        TextView yulan_text_view;

        ImageView select_image;

        TextView name;

    }
}
