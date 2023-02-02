package com.guuda.sheep.activity.game.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.activity.game.model.Chess;

public class ChessView extends AppCompatImageView {
    public static final int STATE_GAMING = 0;
    public static final int STATE_COVERED = 1;
    public static final int STATE_IN_QUEUE = 2;

    public static final int CHESS_1 = 0;
    public static final int CHESS_2 = 1;
    public static final int CHESS_3 = 2;
    public static final int CHESS_4 = 3;
    public static final int CHESS_5 = 4;
    public static final int CHESS_6 = 5;
    public static final int CHESS_7 = 6;
    public static final int CHESS_8 = 7;
    public static final int CHESS_9 = 8;
    public static final int CHESS_10 = 9;
    public static final int CHESS_11 = 10;
    public static final int CHESS_12 = 11;
    public static final int CHESS_13 = 12;
    public static final int CHESS_14 = 13;
    public static final int CHESS_15 = 14;
    public static final int CHESS_16 = 15;

    private static final long DURATION_DARKEN = 500;
    private static final long DURATION_LIGHTEN = 500;
    public static final long DURATION_CLICK = 100;

    private OnClickListener clickListener;

    private int state = STATE_GAMING;
    private int chess = CHESS_1;


    public ChessView(@NonNull Context context) {
        super(context);

        // 自定义点击事件
        super.setOnClickListener(v -> {
            if (clickListener != null && state == STATE_GAMING) {
                animateClick();
                clickListener.onClick(v);
            }
        });
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        clickListener = listener;
    }

    public void setState(int state) {
        int oldState = this.state;

        // 没变则不用处理
        if (oldState == state) {
            return;
        }

        if (oldState == STATE_COVERED) {
            animateLight();
        } else if (state == STATE_COVERED) {
            animateDark();
        }

        this.state = state;



    }

    public int getState() {
        return state;
    }

    public void setChess(int chess) {
        int imgRes;

        switch (chess) {
            case CHESS_1: imgRes = R.mipmap.ic_chess_1; break;
            case CHESS_2: imgRes = R.mipmap.ic_chess_2; break;
            case CHESS_3: imgRes = R.mipmap.ic_chess_3; break;
            case CHESS_4: imgRes = R.mipmap.ic_chess_4; break;
            case CHESS_5: imgRes = R.mipmap.ic_chess_5; break;
            case CHESS_6: imgRes = R.mipmap.ic_chess_6; break;
            case CHESS_7: imgRes = R.mipmap.ic_chess_7; break;
            case CHESS_8: imgRes = R.mipmap.ic_chess_8; break;
            case CHESS_9: imgRes = R.mipmap.ic_chess_9; break;
            case CHESS_10: imgRes = R.mipmap.ic_chess_10; break;
            case CHESS_11: imgRes = R.mipmap.ic_chess_11; break;
            case CHESS_12: imgRes = R.mipmap.ic_chess_12; break;
            case CHESS_13: imgRes = R.mipmap.ic_chess_13; break;
            case CHESS_14: imgRes = R.mipmap.ic_chess_14; break;
            case CHESS_15: imgRes = R.mipmap.ic_chess_15; break;
            case CHESS_16: imgRes = R.mipmap.ic_chess_16; break;
            default: return;
        }

        this.chess = chess;

        Glide.with(this).load(imgRes).into(this);
    }

    public int getChess() {
        return chess;
    }

    private void animateDark() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(DURATION_DARKEN);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(animator -> {
                float animatedValue = (float) animator.getAnimatedValue();
                setForeground(AppCompatResources.getDrawable(getContext(), R.drawable.fg_black_alpha70));
                getForeground().setAlpha((int) (((double) 255)/100*animatedValue));
        });
//        valueAnimator.setStartDelay(delayTime); // TODO 原有逻辑有延时时间，后面看看是否需要
        valueAnimator.start();
    }

    private void animateLight() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(DURATION_LIGHTEN);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(animator -> {
            float animatedValue = (float) animator.getAnimatedValue();
            Drawable foreground = getForeground();
            if (null != foreground){
                foreground.setAlpha((int) (255 - 255.0 / 100 * animatedValue));
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setForeground(null);
            }
        });
        valueAnimator.start();
    }

    private void animateClick() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale);
        startAnimation(animation);
    }





    Chess chessModel;

    public void setChessModel(Chess chessModel) {
        this.chessModel = chessModel;
    }

    public Chess getChessModel() {
        return chessModel;
    }
}
