package com.example.jia.controlview.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.jia.controlview.R;
import com.example.jia.controlview.view.catView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private  catView cat;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }






    private void initView() {
        cat= (catView) findViewById(R.id.cat);

        img= (ImageView) findViewById(R.id.img);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
