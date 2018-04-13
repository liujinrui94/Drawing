package com.rdc.drawing.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.rdc.drawing.R;
import com.rdc.drawing.adapter.MainAdapter;
import com.rdc.drawing.bean.SaveData;
import com.rdc.drawing.board.dao.DaoSession;
import com.rdc.drawing.config.NoteApplication;
import com.rdc.drawing.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private static final String TAG = "MainActivity";
    private List<Map<String, String>> mPicturePathList=new ArrayList<>();
    private MainAdapter mPictureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void initView() {
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        findViewById(R.id.add_draw).setOnClickListener(this);
        mPicturePathList = FileUtils.getPicturesPath(NoteApplication.ROOT_DIRECTORY);
        mPictureAdapter = new MainAdapter(this, mPicturePathList);
        gridView.setAdapter(mPictureAdapter);
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
        Intent intent = new Intent(MainActivity.this, DrawActivity.class);
        intent.putExtra("path", mPicturePathList.get(position).get("picturePath"));
        intent.putExtra("filePath", mPicturePathList.get(position).get("filePath"));
        startActivityForResult(intent, 2);
    }


}
