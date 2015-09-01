package com.example.tranimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

/**
 * 自动滚动超出屏幕大小的图片
 * 
 * @author 小姜
 * @time 2015-9-1 下午5:25:49
 */
public class AutoScrollImageView extends ImageView {

    /** Delay between a pair of frames at a 60 FPS frame rate. */
    private static final long FRAME_DELAY = 1000 / 60;
    //矩阵，用于移动图片
    private Matrix matrix;
    //每次滚动的距离
    private int distance = -1;
    //矩阵的坐标方位
    float[] values = new float[9];
    //图片的宽度
    private int drawableWidth;
    //imageView的宽度
    private float viewWidth;
    //为了图片填充所使用的放大比例
    private float scale;

    public AutoScrollImageView(Context context) {
        this(context, null, 0);
    }

    public AutoScrollImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(ImageView.ScaleType.MATRIX);
        init();
    }

    /**
     * 初始化
     * 
     * @author 小姜
     * @time 2015-9-1 下午5:25:40
     */
    @SuppressLint("NewApi")
    private void init() {
        matrix = new Matrix();
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //通过代码计算实现ScaleType的CENTER_CROP模式
                Drawable d = getDrawable();
                viewWidth = getWidth();
                float viewHeight = getHeight();
                drawableWidth = d.getIntrinsicWidth();
                int drawableHeight = d.getIntrinsicHeight();
                float widthScale = viewWidth / drawableWidth;
                float heightScale = viewHeight / drawableHeight;
                scale = Math.max(widthScale, heightScale);
                matrix.postScale(scale, scale);
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }
    

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //通过矩阵移动图片
        matrix.postTranslate(distance, 0);
        setImageMatrix(matrix);
        //获取当前矩阵的坐标数值
        matrix.getValues(values);
        //判断是否到达边界，到达边界就通过正负数来控制移动方向
        if (values[2] <= -(drawableWidth * scale - viewWidth)+1) {//+1是为了防止到边缘是有白边
            distance = 1;
        } else if (values[2] >= 0) {
            distance = -1;
        }
        postInvalidateDelayed(FRAME_DELAY);
    }

}
