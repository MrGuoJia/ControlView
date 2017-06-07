/*
package com.example.jia.controlview.activity;
这个没用到= =
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.jia.controlview.R;

public class ScreenShotActivity extends AppCompatActivity {
    private ImageView shotScreenShow;
    private Button btn_delete;
    private Button btn_return;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);
        initView();
    }

    private void initView() {
        shotScreenShow= (ImageView) findViewById(R.id.showShotScreen);
        btn_delete= (Button) findViewById(R.id.btn_delete);
        btn_return= (Button) findViewById(R.id.btn_return);
        Intent intent=getIntent();
        if(intent!=null){
            bitmap=intent.getParcelableExtra("bitmap");
            shotScreenShow.setImageBitmap(bitmap);
        }
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              bitmap= getTransparentBitmap(bitmap, 0);//透明度设置为0
                shotScreenShow.setImageBitmap(bitmap);

            }
        });
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScreenShotActivity.this,MainActivity.class));
                ScreenShotActivity.this.finish();
            }



        });

    }
    public  Bitmap getTransparentBitmap(Bitmap sourceImg, int number){
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }

}
*/
