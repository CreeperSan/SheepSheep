package com.guuda.sheep.customview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 会动的小草
 */
public class GrassView extends AppCompatImageView {

    public GrassView(Context context) {
        this(context,null);
    }

    public GrassView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrassView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        @DrawableRes int imageRes;
        switch ((int) (Math.round(Math.random() * 3))) {
            case 1:
                imageRes = R.mipmap.ic_grass2;
                break;
            case 2:
                imageRes = R.mipmap.ic_grass3;
                break;
            case 3:
                imageRes = R.mipmap.ic_grass4;
                break;
            default:
                imageRes = R.mipmap.ic_grass1;
        }

        Glide.with(this).load(imageRes).into(this);

        animator = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.2f);
        animator.setDuration(500L);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);

        setPivotY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, getResources().getDisplayMetrics()));
    }

    ObjectAnimator animator;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (animator != null) {
//            setPivotY(getMeasuredHeight());
//            setPivotY(getMeasuredHeight());
            animator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (animator != null) {
            animator.pause();
        }
    }
}
