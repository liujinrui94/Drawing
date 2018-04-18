package com.rdc.drawing.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.rdc.drawing.R;
import com.rdc.drawing.adapter.MainAdapter;
import com.rdc.drawing.bean.HomeBean;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private List<Map<String, String>> mPicturePathList = new ArrayList<>();

    private List<HomeBean> homeBeanList = new ArrayList<>();


    private List<HomeBean> homeBeanListdelect = new ArrayList<>();

    private MainAdapter mPictureAdapter;

    private TextView back, delect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void initView() {
        GridView gridView = (GridView) findViewById(R.id.gridView);
        back = (TextView) findViewById(R.id.back);
        delect = (TextView) findViewById(R.id.delect);
        gridView.setOnItemClickListener(this);
        findViewById(R.id.add_draw).setOnClickListener(this);
        mPicturePathList = FileUtils.getPicturesPath(NoteApplication.ROOT_DIRECTORY);
        for (int i = 0; i < mPicturePathList.size(); i++) {
            HomeBean homeBean = new HomeBean();
            homeBean.setMap(mPicturePathList.get(i));
            homeBeanList.add(homeBean);
        }
        homeBeanListdelect.addAll(homeBeanList);
        mPictureAdapter = new MainAdapter(this, homeBeanList);
        gridView.setAdapter(mPictureAdapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < homeBeanListdelect.size(); i++) {
                    if (homeBeanListdelect.get(i).isSelect()) {
                        File file = new File(homeBeanListdelect.get(i).getMap().get("picturePath"));
                        FileUtils.delete(file);
                        homeBeanList.remove(homeBeanListdelect.get(i));
                    }
                }
                mPictureAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onClick(View v) {
        int requestCode = 1;
        Intent intent = new Intent(MainActivity.this, DrawActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case NoteApplication.OK:
                Map map = new HashMap();
                map.put("picturePath", data.getStringExtra("path"));
                map.put("filePath", data.getStringExtra("filePath"));
                mPicturePathList.add(map);
                break;
            case NoteApplication.CANCEL:
                break;
            default:
                Log.e(TAG, "onActivityResult" + Integer.toString(resultCode));
                break;
        }
        mPictureAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(MainActivity.this, DrawActivity.class);
//        intent.putExtra("path", mPicturePathList.get(position).get("picturePath"));
//        intent.putExtra("filePath", mPicturePathList.get(position).get("filePath"));
//        startActivityForResult(intent, 2);

        if (homeBeanList.get(position).isSelect()) {
            homeBeanList.get(position).setSelect(false);
        } else {
            homeBeanList.get(position).setSelect(true);
        }
        mPictureAdapter.notifyDataSetChanged();


    }


}
