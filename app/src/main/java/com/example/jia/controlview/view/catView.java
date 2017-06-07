package com.example.jia.controlview.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;


import com.example.jia.controlview.R;
import com.example.jia.controlview.activity.ScreenShotActivity;

import java.math.BigDecimal;

/**
 * Created by jia on 2017/6/6.
 */

public class catView extends View  {
    final public static int DRAG = 1;
    final public static int ZOOM = 2;

    public int mode = 0;

    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
    private Matrix saveMatrix = new Matrix();

    private float x_down = 0;
    private float y_down = 0;
    private Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(), R.drawable.cat);
    private PointF mid = new PointF();
    private float initDis = 1f;
    private int screenWidth, screenHeight;
    private float[] x = new float[4];
    private float[] y = new float[4];
    private boolean flag = false;
    private float downY[] = new float[3];//手指按下时Y轴坐标
    private float upY[] = new float[3];//手指松开时Y轴坐标
    private boolean is3Pointer = false;//是否是三指滑动
    private float matchY[] = new float[3];//手指滑动间距


    private boolean viewChange=false;
    public catView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }



    public  catView(Context context){
        super(context);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        matrix = new Matrix();

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            drawBitmap(canvas);    // 根据 matrix 来重绘新的view

    }


    private void drawBitmap(Canvas canvas) {
        canvas.drawBitmap(bitmap, matrix, null);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int action = event.getAction();
        if(event.getPointerCount()==3){
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_POINTER_DOWN:
                    for(int i=0;i<pointerCount;i++){
                        int pointerId = event.getPointerId(i);
                        downY[pointerId]=event.getY();//这是收集3指按下的Y坐标
                    }
                    is3Pointer = true;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if(is3Pointer){ //如果是三个手指，就获取每个手指的滑动间距，否则就清空数据
                        for (int i = 0; i < pointerCount; i++) {
                            int pointerId = event.getPointerId(i);
                            upY[pointerId] = event.getY(i);//这是收集3指松开的Y坐标
                            matchY[pointerId] = Math.abs(upY[pointerId] - downY[pointerId]);
                        }
                    }else {
                        clearArrayData(downY);
                        clearArrayData(upY);
                        clearArrayData(matchY);
                    }
                    break;
            }
            boolean[] flag = new boolean[] {
                    false, false, false,
            };//这三个是用来依次判断3个点的滑动长度是否超过一定值
            for(int i = 0; i < pointerCount; i++){
                int pointerId = event.getPointerId(i);
                    if(matchY[pointerId]>100){//若长度超过100
                        flag[i] = true;
                        matchY[pointerId] = 0;
                    }
            }
            if(flag[0] && flag[1] && flag[2]){//判断3个均为超过100,开始响应三指下滑截屏事件

                bitmap= shot((Activity) getContext());
                x_down+=1;
                Toast.makeText(getContext(),"触摸点数:"+event.getPointerCount(),Toast.LENGTH_SHORT).show();
            }

        }


        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                saveMatrix.set(matrix);
                x_down = event.getX();
                y_down = event.getY();
                // 初始为drag模式
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                saveMatrix.set(matrix);
                // 初始的两个触摸点间的距离
                initDis = spacing(event);
                // 设置为缩放模式
                mode = ZOOM;
                // 多点触摸的时候 计算出中间点的坐标
                midPoint(mid, event);
                break;

            case MotionEvent.ACTION_MOVE:

                // drag模式
                if (mode == DRAG) {
                    // 设置当前的 matrix
                    matrix1.set(saveMatrix);
                    // 平移 当前坐标减去初始坐标 移动的距离
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    // 判断达到移动标准
                    flag = checkMatrix(matrix1);
                    if (flag) {
                        // 设置matrix
                        matrix.set(matrix1);

                        // 调用ondraw重绘
                        invalidate();
                    }
                } else if (mode == ZOOM) {
                    matrix1.set(saveMatrix);
                    float newDis = spacing(event);
                    // 计算出缩放比例
                    float scale = newDis / initDis;

                    // 以mid为中心进行缩放
                    matrix1.postScale(scale, scale, mid.x, mid.y);
                    flag = checkMatrix(matrix1);
                    if (flag) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
        }

        return true;
    }
    private void clearArrayData(float[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }

    //取两点的距离
    private float spacing(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float)Math.sqrt(x * x + y * y);
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }

    //取两点的中点
    private void midPoint(PointF point, MotionEvent event) {
        try {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        } catch (IllegalArgumentException ex) {
        }
    }

    private boolean checkMatrix(Matrix m) {

        GetFour(m);

        // 出界判断
        //view的右边缘x坐标小于屏幕宽度的1/3的时候，
        // view左边缘大于屏幕款短的2/3的时候
        //view的下边缘在屏幕1/3上的时候
        //view的上边缘在屏幕2/3下的时候
        if ((x[0] < screenWidth / 3 && x[1] < screenWidth / 3
                && x[2] < screenWidth / 3 && x[3] < screenWidth / 3)
                || (x[0] > screenWidth * 2 / 3 && x[1] > screenWidth * 2 / 3
                && x[2] > screenWidth * 2 / 3 && x[3] > screenWidth * 2 / 3)
                || (y[0] < screenHeight / 3 && y[1] < screenHeight / 3
                && y[2] < screenHeight / 3 && y[3] < screenHeight / 3)
                || (y[0] > screenHeight * 2 / 3 && y[1] > screenHeight * 2 / 3
                && y[2] > screenHeight * 2 / 3 && y[3] > screenHeight * 2 / 3)) {
            return true;
        }
        // 图片现宽度
        double width = Math.sqrt((x[0] - x[1]) * (x[0] - x[1]) + (y[0] - y[1])
                * (y[0] - y[1]));
        // 缩放比率判断 宽度打雨3倍屏宽，或者小于1/3屏宽
        if (width < screenWidth / 3 || width > screenWidth * 3) {
            return true;
        }
        return false;


    }

    private void GetFour(Matrix matrix) {
        float[] f = new float[9];
        matrix.getValues(f);
        // 图片4个顶点的坐标
        //矩阵  9     MSCALE_X 缩放的， MSKEW_X 倾斜的    。MTRANS_X 平移的
        x[0] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X] * 0
                + f[Matrix.MTRANS_X];
        y[0] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y] * 0
                + f[Matrix.MTRANS_Y];
        x[1] = f[Matrix.MSCALE_X] * bitmap.getWidth() + f[Matrix.MSKEW_X] * 0
                + f[Matrix.MTRANS_X];
        y[1] = f[Matrix.MSKEW_Y] * bitmap.getWidth() + f[Matrix.MSCALE_Y] * 0
                + f[Matrix.MTRANS_Y];
        x[2] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X]
                * bitmap.getHeight() + f[Matrix.MTRANS_X];
        y[2] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y]
                * bitmap.getHeight() + f[Matrix.MTRANS_Y];
        x[3] = f[Matrix.MSCALE_X] * bitmap.getWidth() + f[Matrix.MSKEW_X]
                * bitmap.getHeight() + f[Matrix.MTRANS_X];
        y[3] = f[Matrix.MSKEW_Y] * bitmap.getWidth() + f[Matrix.MSCALE_Y]
                * bitmap.getHeight() + f[Matrix.MTRANS_Y];
    }

    private Bitmap shot(Activity activity) {
        //View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度 /
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
       // Log.i("TAG", "" + statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b2 = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b2;
    }
}
