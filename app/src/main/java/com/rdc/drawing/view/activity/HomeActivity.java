package com.rdc.drawing.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.rdc.drawing.R;
import com.rdc.drawing.adapter.BaseRecyclerAdapter;
import com.rdc.drawing.adapter.SmartViewHolder;
import com.rdc.drawing.bean.SaveData;
import com.rdc.drawing.board.dao.DaoSession;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.utils.FileUtils;
import com.rdc.drawing.utils.GlideUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Edianzu on 2018/4/12.
 */

public class HomeActivity extends BaseActivity {

    DaoSession daoSession = NoteApplication.getsInstance().getDaoSession();

    private RecyclerView recyclerView;
    private BaseRecyclerAdapter baseRecyclerAdapter;
    private List<SaveData> saveDataList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        Long l1 = new Long(201806181800l);
        if (Long.parseLong(sdf.format(Calendar.getInstance().getTime())) > l1) {
        } else {
            initView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveDataList = daoSession.loadAll(SaveData.class);
        baseRecyclerAdapter.refresh(saveDataList);
    }

    public void initView() {
        findViewById(R.id.add_draw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DrawActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DrawActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.delect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int l = saveDataList.size();
                for (int j = 0; j < l; j++) {
                    for (int i = 0; i < saveDataList.size(); i++) {
                        if (saveDataList.get(i).isSelect()) {
                            daoSession.getSaveDataDao().delete(saveDataList.get(i));
                            File file = new File(saveDataList.get(i).getPicturePath());
                            FileUtils.delete(file);
                            saveDataList.remove(saveDataList.get(i));
                            continue;
                        }
                    }
                }

                baseRecyclerAdapter.refresh(saveDataList);
            }
        });


        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 4));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        baseRecyclerAdapter = new BaseRecyclerAdapter<SaveData>(saveDataList, R.layout.main_item) {
            @Override
            protected void onBindViewHolder(final SmartViewHolder holder, final SaveData model, final int position) {
                holder.text(R.id.name, model.getImageName());
                GlideUtils.getInstance().loadLocalImage(new File(model.getPicturePath()), (ImageView) holder.itemView.findViewById(R.id.imageView));
                holder.itemView.findViewById(R.id.yulan_text_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), DrawActivity.class);
                        intent.putExtra("model", model);
                        startActivity(intent);
                        finish();
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.isSelect()) {
                            model.setSelect(false);
                            holder.image(R.id.select_image, R.mipmap.w_noselect);
                        } else {
                            model.setSelect(true);
                            holder.image(R.id.select_image, R.mipmap.w_select);
                        }
                    }
                });
                if (model.isSelect()) {
                    holder.image(R.id.select_image, R.mipmap.w_select);
                } else {
                    holder.image(R.id.select_image, R.mipmap.w_noselect);
                }
                holder.itemView.findViewById(R.id.delect_text_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                        mBuilder.setTitle("提示");
                        mBuilder.setMessage("是删除此项");
                        mBuilder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                daoSession.getSaveDataDao().delete(model);
                                File file = new File(model.getPicturePath());
                                FileUtils.delete(file);
                                saveDataList.remove(position);
                                baseRecyclerAdapter.refresh(saveDataList);
//                                notifyDataSetChanged();
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
            }


        };
        recyclerView.setAdapter(baseRecyclerAdapter);
    }

}
