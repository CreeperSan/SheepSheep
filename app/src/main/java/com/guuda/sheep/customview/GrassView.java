package com.guuda.sheep.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.guuda.sheep.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 会动的小草
 */
public class GrassView extends View {
    private final Paint mPaint = new Paint();

    public GrassView(Context context) {
        this(context,null);
    }

    public GrassView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrassView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#639332"));
        mPaint.setAntiAlias(true);//抗锯齿效果
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);

        int num = (int) (Math.round(Math.random() * 3));
        Bitmap mBitmap = null;
        switch (num){
            case 0:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_grass1);
                break;
            case 1:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_grass2);
                break;
            case 2:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_grass3);
                break;
            case 3:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_grass5);
                break;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap, 60, 60, true);
        canvas.drawBitmap(scaledBitmap, 0f, 0f, mPaint);

    }

}
