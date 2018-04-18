package com.rdc.drawing.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rdc.drawing.config.NoteApplication;

/**
 * Created by lichaojian on 16-8-28.
 */
public abstract class BaseActivity extends AppCompatActivity{

    public abstract void initView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.getInstance().addActivity(this);
    }
}
