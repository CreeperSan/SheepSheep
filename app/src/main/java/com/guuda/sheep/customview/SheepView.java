package com.guuda.sheep.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.coorchice.library.SuperTextView;
import com.guuda.sheep.R;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.utils.DimensionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 羊view
 *  在中间建立一个16*ChessSize(纵坐标) - 14*ChessSize(横坐标) 的坐标系
 *  每个模块宽高为(ChessSize*2)*(ChessSize*2)
 *  以左上角为坐标点，被覆盖即无法点击和覆盖阴影
 */
public class SheepView extends RelativeLayout {
    private Context mContext;
    private View sheepLayout;
    private RelativeLayout rlAllLayout;
    private RelativeLayout grassLayout;
    private SuperTextView chessBackground;
    private ImageView toolBringToOutsideView;
    private ImageView toolRecallView;
    private ImageView toolShuffleView;

    private int grassNum = 28;  //草的数量

    private final static int CHESS_SIZE = 24;  //棋子大小的一半
    private List<ImageView> showChessList;   //棋牌区域image的list
    private List<ImageView> takeinChessList;   //收纳区image的list
    private List<Integer> rescoureList;   //图片资源list
    private int MAX_DEGREE = 20;  //第二关最多层数
    private final int MAX_CHESS_NUM = 36; //第二关 单层棋子最多数量
    private int degreeNum = 0;  //获取层数 (为0时，从10-MAX_DEGREE中随机获取层数,有传入用传入)

    private final long downDelayTime = 500;
    private final long ToDarkTime = 500;
    private final long ToLightTime = 500;
    private final long takeinTime = 40;
    private final long leftTime = 40;
    private final long changeTime = 600;

    private List<Double[]> locationList;  //棋子坐标列表
    private List<Integer> iconList;   //图标列表
    private List<Integer> degreeList;   //等级列表

    private final List<Integer[]> mGrassLocationList;  //草坐标列表

    private int barrierNum = 1;  //关卡
    private GameProgressListener gameProgressListener;

    public SheepView(Context context) {
        this(context,null);
    }

    public SheepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mGrassLocationList = new ArrayList<>();
        showChessList = new ArrayList<>();
        takeinChessList = new ArrayList<>();
        rescoureList = new ArrayList<>();
        locationList = new ArrayList<>();
        iconList = new ArrayList<>();
        degreeList = new ArrayList<>();

        initView(context);
        initResourcesList();
        initGrassView();

    }

    private void initView(Context context) {
        sheepLayout = inflate(context,R.layout.layout_sheep, this);
        rlAllLayout = sheepLayout.findViewById(R.id.rl_alllayout);
        grassLayout = sheepLayout.findViewById(R.id.grassLayout);
        chessBackground = sheepLayout.findViewById(R.id.stv_container);
        toolBringToOutsideView = sheepLayout.findViewById(R.id.toolBringToOutside);
        toolRecallView = sheepLayout.findViewById(R.id.toolRecall);
        toolShuffleView = sheepLayout.findViewById(R.id.toolShuffle);


        LayoutParams layoutParams = (RelativeLayout.LayoutParams) chessBackground.getLayoutParams();
        layoutParams.width = DimensionUtils.dp2px(CHESS_SIZE *14+10);
        layoutParams.height = DimensionUtils.dp2px(CHESS_SIZE *2+10);
        chessBackground.setLayoutParams(layoutParams);
    }

    private void initBarrier(){
        for (int i = 0; i < iconList.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            locationToMargin(imageView,locationList.get(i),degreeList.get(i));
            imageView.setImageResource(rescoureList.get(iconList.get(i)));
            imageView.setTag(R.id.sheepview_imageview_picrescoure,iconList.get(i));
            imageView.setOnClickListener(new NoFastClickListener() {
                @Override
                protected void onSingleClick() {
                    AudioController.instance.playEffect(AudioController.SOUND_CLICK);
                    showChessList.remove(imageView);
                    scaleChangeAnim(imageView);

                    judgeCanClick(false);
                }
            });

            showChessList.add(imageView);
            rlAllLayout.addView(imageView);
        }

        for (ImageView imageView : showChessList) {
            int mTime = (int) (Math.random() * 700);
            startDownAnim(imageView,mTime);

            if (judgeNeedToDark(imageView)){
                startToDarkAnim(imageView,mTime+downDelayTime);
            }
        }
    }

    public void setBarrierNum(int barrierNum,int mDegreeNum) {
        this.barrierNum = barrierNum;
        this.degreeNum = mDegreeNum;

        if (showChessList.size()>0){
            for (ImageView iv : showChessList){
                rlAllLayout.removeView(iv);
            }
        }

        if (takeinChessList.size()>0){
            for (ImageView iv : takeinChessList){
                rlAllLayout.removeView(iv);
            }
        }

        showChessList.clear();
        takeinChessList.clear();
        locationList.clear();
        iconList.clear();
        degreeList.clear();

        if (1 == barrierNum){
            initFirstLocationList();
            initFirstBarrier();
        }else {
            initSecondLocationList();
            initSecondBarrier();
        }

        //倒序
        Collections.reverse(locationList);
        Collections.reverse(iconList);
        Collections.reverse(degreeList);
        initBarrier();
        judgeCanClick(false);
    }

    private void startDownAnim(ImageView imageView, long delayTime){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(downDelayTime);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                Double[] ilocation = (Double[]) imageView.getTag(R.id.sheepview_imageview_location);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.leftMargin = (int) (DimensionUtils.getScreenW()/2 - DimensionUtils.dp2px(14* CHESS_SIZE)/2 +
                                        DimensionUtils.dp2px(CHESS_SIZE)*ilocation[0]);

                //从屏幕上方一个棋子的位置下落，多加2*20
                double value1 = DimensionUtils.getScreenH() / 2 - DimensionUtils.dp2px(16* CHESS_SIZE)/2 +
                        DimensionUtils.dp2px(CHESS_SIZE)*(ilocation[1]+2);
                value1 = value1/100 *animatedValue - DimensionUtils.dp2px(2* CHESS_SIZE);
                layoutParams.topMargin = (int) value1;
                imageView.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.setStartDelay(delayTime);
        valueAnimator.start();

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Double[] tag = ((Double[]) imageView.getTag(R.id.sheepview_imageview_location));
                if (tag[1] == 17&&(tag[0] == 3||tag[0] == 9)){  //滚动放大动画
                    ValueAnimator rollValueAnimator = ValueAnimator.ofFloat(0f, 100f);
                    rollValueAnimator.setDuration(changeTime);
                    rollValueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
                    rollValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float animatedValue = (float) valueAnimator.getAnimatedValue();

                            Log.i("孙", "animatedValue/100: "+animatedValue/100);
                            imageView.setRotation(animatedValue/100*360);
                        }
                    });
                    rollValueAnimator.start();
                }
            }
        });
    }

    private void startToDarkAnim(ImageView imageView,long delayTime){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(ToDarkTime);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                imageView.setForeground(AppCompatResources.getDrawable(mContext, R.drawable.fg_black_alpha70));
                imageView.getForeground().setAlpha((int) (((double) 255)/100*animatedValue));
            }
        });
        valueAnimator.setStartDelay(delayTime);
        valueAnimator.start();
    }

    private void startToLightAnim(ImageView imageView){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(ToLightTime);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                if (null!=imageView.getForeground()){
                    imageView.getForeground().setAlpha((int) (255- ((double) 255)/100*animatedValue));
                }
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setForeground(null);
            }
        });
        valueAnimator.start();
    }

    private void scaleChangeAnim(ImageView imageView){ //点击后 棋子放大缩小 缩放动画
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_scale);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startTakeinAnim(imageView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation);
    }

    private void startTakeinAnim(ImageView imageView){  //放大缩小后，从上方容器到下方容器 移动动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(takeinTime);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                int targetLeftMargein = (DimensionUtils.getScreenW()- DimensionUtils.dp2px(14* CHESS_SIZE))/2 + (takeinChessList.size())* DimensionUtils.dp2px(2* CHESS_SIZE);
                int targettopMargein = DimensionUtils.getScreenH()- DimensionUtils.dp2px(25+2* CHESS_SIZE);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                int leftMargin = layoutParams.leftMargin;
                float value1 = (((float) leftMargin)-targetLeftMargein)/100 *animatedValue;
                layoutParams.leftMargin = (int) (leftMargin - value1);
                int topMargin = layoutParams.topMargin;
                float value2 = (((float) topMargin)-targettopMargein)/100 *animatedValue;
                layoutParams.topMargin = (int) (topMargin - value2);

                imageView.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                takeinChessList.add(imageView);
                Map<Integer,List<Integer>> mMap= new HashMap<>();  //key资源序号，list 图片在takeinChessList的位置
                for (int i = 0; i < takeinChessList.size(); i++) {
                    if (null == mMap.get(((int) takeinChessList.get(i).getTag(R.id.sheepview_imageview_picrescoure)))){
                        List<Integer> picNumList = new ArrayList<>();
                        picNumList.add(i);
                        mMap.put(((int) takeinChessList.get(i).getTag(R.id.sheepview_imageview_picrescoure)),picNumList);
                    }else {
                        mMap.get(((int) takeinChessList.get(i).getTag(R.id.sheepview_imageview_picrescoure))).add(i);
                    }
                }

                boolean canClear = false;  //能消除
                for (Integer key : mMap.keySet()) {
                    if (mMap.get(key).size() == 3){
                        canClear = true;

                        startClearAnim(mMap.get(key));
                        break;
                    }
                }

                if (!canClear){
                    if (takeinChessList.size()>6){  //数量为7，即失败
                        if (null!= gameProgressListener){
                            gameProgressListener.onLevelFail();
                        }
                    }
                }else {
                    //冻结所有上层棋子的点击事件
                    for (ImageView view : showChessList) {
                        view.setEnabled(false);
                    }
                }
            }
        });
        valueAnimator.start();
    }

    int finishNum = 0;
    private void startClearAnim(List<Integer> mlist){ //缩小，变星星
        for (Integer integer : mlist) {
            Animation animation_small = AnimationUtils.loadAnimation(mContext, R.anim.anim_tosmall);
            animation_small.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    takeinChessList.get(integer).setBackground(null);
                    takeinChessList.get(integer).setImageResource(R.mipmap.ic_star);

                    Animation animation_big = AnimationUtils.loadAnimation(mContext, R.anim.anim_tobig); //放大，然后gone
                    animation_big.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            finishNum = finishNum + 1;
                            if (finishNum == 3){   //3个动画都结束
                                Log.i("孙", "过程1: ");
                                if (takeinChessList.size() == 3){  //当前只有3个，无需左滑动画
                                    rlAllLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (Integer x : mlist){
                                                rlAllLayout.removeView(takeinChessList.get(x));
                                            }
                                            List<ImageView> newlist = new ArrayList<>();
                                            takeinChessList = newlist;

                                            //判断上面是否还有，没有即成功
                                            if (showChessList.size() == 0){
                                                if (null!= gameProgressListener){
                                                    gameProgressListener.onLevelPass();
                                                }
                                            }else {
                                                judgeCanClick(true);
                                            }
                                        }
                                    });
                                }else {
                                    Log.i("孙", "过程2: ");
                                    startLeftAnim(takeinChessList,mlist);
                                }

                                finishNum = 0;
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    takeinChessList.get(integer).startAnimation(animation_big);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            takeinChessList.get(integer).startAnimation(animation_small);
        }

    }

    private void startLeftAnim(List<ImageView> ivList,List<Integer> mlist){  //被清除的3个序号
        for (int i = 0; i < ivList.size(); i++) {
            Log.i("孙", "过程6: ");
            if (i!=mlist.get(0)&&i!=mlist.get(1)&&i!=mlist.get(2)){
                Log.i("孙", "过程7: ");
                int leftDistanceNum = 0;  //左移几个棋子的距离
                if (i>mlist.get(0)){
                    leftDistanceNum = leftDistanceNum + 1;
                }
                if (i>mlist.get(1)){
                    leftDistanceNum = leftDistanceNum + 1;
                }
                if (i>mlist.get(2)){
                    leftDistanceNum = leftDistanceNum + 1;
                }

                startLeftSmallAnim(ivList,ivList.get(i),leftDistanceNum,mlist);
            }
        }
    }

    int leftFinishNum = 0;
    private void startLeftSmallAnim(List<ImageView> ivAllList,ImageView iv,int distanceNum,List<Integer> mlist){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        float leftMargin = layoutParams.leftMargin;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(leftTime);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                float v = (((float) DimensionUtils.dp2px(2* CHESS_SIZE)) * distanceNum) / 100f * animatedValue;
                layoutParams.leftMargin = (int) (leftMargin - v);
                iv.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.start();

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.i("孙", "过程5: ");
                leftFinishNum = leftFinishNum + 1;
                if (leftFinishNum == (ivAllList.size()-3)){  //所有左滑动画结束
                    Log.i("孙", "过程3: ");
                    rlAllLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            for (Integer x : mlist) {
                                rlAllLayout.removeView(takeinChessList.get(x));
                            }

                            List<ImageView> newList = new ArrayList<>();
                            for (int i = 0; i < takeinChessList.size(); i++) {
                                boolean isSame = false;
                                for (Integer x : mlist) {
                                    if (i == x){
                                        isSame = true;
                                    }
                                }

                                if (!isSame){
                                    newList.add(takeinChessList.get(i));
                                }
                            }

                            Log.i("孙", "过程4: ");
                            takeinChessList = newList;

                            judgeCanClick(true);
                        }
                    });
                    leftFinishNum = 0;
                }
            }
        });
    }

    private void locationToMargin(ImageView iv,Double[] mLocation,int mDegree){  //坐标转换成marginleft和margintop,层级：最上层为1，依次增加
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DimensionUtils.dp2px(2* CHESS_SIZE), DimensionUtils.dp2px(2* CHESS_SIZE));
        layoutParams.topMargin = -DimensionUtils.dp2px(2* CHESS_SIZE);
        iv.setLayoutParams(layoutParams);

        iv.setTag(R.id.sheepview_imageview_degree,mDegree);
        iv.setTag(R.id.sheepview_imageview_location,mLocation);
        iv.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.bg_chessview));
        iv.setPadding(DimensionUtils.dp2px(5), DimensionUtils.dp2px(5), DimensionUtils.dp2px(5), DimensionUtils.dp2px(5));
    }

    private void judgeCanClick(boolean onlyChangeViewClickable){   //判断剩下棋子是否可以点击 ,无法点击background设置蒙版
        for (int i = 0; i < showChessList.size(); i++) {
            boolean canClick = true;
            List<ImageView> mlist = new ArrayList<>(showChessList);
            mlist.remove(i);

            Double[] ilocation = (Double[]) showChessList.get(i).getTag(R.id.sheepview_imageview_location);
            int idegree = ((int) showChessList.get(i).getTag(R.id.sheepview_imageview_degree));
            for (ImageView imageView : mlist) {
                Double[] lt = (Double[]) imageView.getTag(R.id.sheepview_imageview_location);
                int dg = ((int) imageView.getTag(R.id.sheepview_imageview_degree));
                if (idegree > dg){
                    if (Math.abs(lt[0]-ilocation[0])<2&&Math.abs(lt[1]-ilocation[1])<2){
                        canClick = false;
                        break;
                    }
                }
            }

            if (canClick){
                showChessList.get(i).setEnabled(true);
                if (!onlyChangeViewClickable){
                    if (null!=showChessList.get(i).getForeground()&&showChessList.get(i).getForeground().getAlpha() < 255){
                        startToLightAnim(showChessList.get(i));
                    }
                }
            }else {
                showChessList.get(i).setEnabled(false);
            }
        }
    }

    private boolean judgeNeedToDark(ImageView iv){   //判断是否需要变暗
        boolean needDark = false;

        Double[] ilocation = (Double[]) iv.getTag(R.id.sheepview_imageview_location);
        int idegree = ((int) iv.getTag(R.id.sheepview_imageview_degree));
        for (ImageView imageView : showChessList) {
            Double[] lt = (Double[]) imageView.getTag(R.id.sheepview_imageview_location);
            int dg = ((int) imageView.getTag(R.id.sheepview_imageview_degree));
            if (idegree > dg){
                if (Math.abs(lt[0]-ilocation[0])<2&&Math.abs(lt[1]-ilocation[1])<2){
                    needDark = true;
                    break;
                }
            }
        }

        return needDark;
    }

    private void initResourcesList(){
        rescoureList.add(R.mipmap.ic_chess_1);
        rescoureList.add(R.mipmap.ic_chess_2);
        rescoureList.add(R.mipmap.ic_chess_3);
        rescoureList.add(R.mipmap.ic_chess_4);
        rescoureList.add(R.mipmap.ic_chess_5);
        rescoureList.add(R.mipmap.ic_chess_6);
        rescoureList.add(R.mipmap.ic_chess_7);
        rescoureList.add(R.mipmap.ic_chess_8);
        rescoureList.add(R.mipmap.ic_chess_9);
        rescoureList.add(R.mipmap.ic_chess_10);
        rescoureList.add(R.mipmap.ic_chess_11);
        rescoureList.add(R.mipmap.ic_chess_12);
        rescoureList.add(R.mipmap.ic_chess_13);
        rescoureList.add(R.mipmap.ic_chess_14);
        rescoureList.add(R.mipmap.ic_chess_15);
        rescoureList.add(R.mipmap.ic_chess_16);
    }

    private void initGrassView(){
        grassLayout.removeAllViews();

        for (int i = 0; i < grassNum; i++) {
            addGrassLocation(mGrassLocationList, 0);
        }
        for (Integer[] integers : mGrassLocationList) {
            GrassView grassView = new GrassView(mContext);
            LayoutParams layoutParams = new LayoutParams(DimensionUtils.dp2px(30), DimensionUtils.dp2px(30));
            layoutParams.leftMargin = integers[0];
            layoutParams.topMargin = integers[1];
            grassView.setLayoutParams(layoutParams);
            grassLayout.addView(grassView);
        }

    }

    /**
     * 第一关的坐标为2层
     * 底部：
     * （3,3） （6,3） （9,3）
     * （3,7） （6,7） （9,7）
     * （3,11） （6,11） （9,11）
     * 顶部：
     * （3,4） （6,4） （9,4）
     * （3,8） （6,8） （9,8）
     * （3,12） （6,12） （9,12）
     */
    private void initFirstLocationList(){
        Double[] location1 = {3.0,4.0};
        Double[] location2 = {6.0,4.0};
        Double[] location3 = {9.0,4.0};
        Double[] location4 = {3.0,8.0};
        Double[] location5 = {6.0,8.0};
        Double[] location6 = {9.0,8.0};
        Double[] location7 = {3.0,12.0};
        Double[] location8 = {6.0,12.0};
        Double[] location9 = {9.0,12.0};

        Double[] location10 = {3.0,3.0};
        Double[] location11 = {6.0,3.0};
        Double[] location12 = {9.0,3.0};
        Double[] location13 = {3.0,7.0};
        Double[] location14 = {6.0,7.0};
        Double[] location15 = {9.0,7.0};
        Double[] location16 = {3.0,11.0};
        Double[] location17 = {6.0,11.0};
        Double[] location18 = {9.0,11.0};

        locationList.add(location1);
        locationList.add(location2);
        locationList.add(location3);
        locationList.add(location4);
        locationList.add(location5);
        locationList.add(location6);
        locationList.add(location7);
        locationList.add(location8);
        locationList.add(location9);

        locationList.add(location10);
        locationList.add(location11);
        locationList.add(location12);
        locationList.add(location13);
        locationList.add(location14);
        locationList.add(location15);
        locationList.add(location16);
        locationList.add(location17);
        locationList.add(location18);

        for (int i = 0; i < 18; i++) {
            if (i<9){
                degreeList.add(1);
            }else {
                degreeList.add(2);
            }
        }
    }

    //第一层图片设置
    private void initFirstBarrier(){
        //取3个不同的随机数 选出3个icon
        int i1 = (int) (Math.round(Math.random() * 15));
        int i2 = (int) (Math.round(Math.random() * 15));
        while (i2 == i1){
            i2 = (int) (Math.round(Math.random() * 15));
        }
        int i3 = (int) (Math.round(Math.random() * 15));
        while (i3 == i1||i3 == i2){
            i3 = (int) (Math.round(Math.random() * 15));
        }

        for (int i = 0; i < 6; i++) {
            iconList.add(i1);
            iconList.add(i2);
            iconList.add(i3);
        }
        Collections.shuffle(iconList); //打乱
    }

    /**
     * 第二关的坐标
     * 可以从（0,0）至（12,14）中随机生成
     * 棋盘大小为7*8 理想情况一层摆满最多56个 实际可能只能25个
     * 同一层不能重叠
     * 层级越大，数量越多
     */
    private void initSecondLocationList(){
        //获取随机层数
        if (degreeNum == 0){
            degreeNum = (int) (Math.random() * (MAX_DEGREE - 10)) + 10;
        }else if (degreeNum<5){
            degreeNum = 5;
        }

        //生成小于46的层数的随机数
        List<Integer> degreeContentNum = new ArrayList<>();
        for (int i = 0; i < degreeNum; i++) {
            int num = (int) (Math.random() * (MAX_CHESS_NUM -6)) + 6;
            degreeContentNum.add(num);
        }

        //从小到大排列
        degreeContentNum.sort((o1, o2) -> {
            int diff = (Integer) o1 - (Integer) o2;
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return 0;
            }
        });

        //生成坐标
        for (int i = 0; i < degreeContentNum.size(); i++) { //每层
            List<Double[]> everyDegreeLocationList = new ArrayList<>();
            for (int x = 0; x < degreeContentNum.get(i); x++) {  //每层中每个的判断
                addSmallLocation(everyDegreeLocationList,i+1, 0);
            }

//            Log.i("孙", "每层数量: "+degreeContentNum.get(i));
            locationList.addAll(everyDegreeLocationList);
        }

        //必须为3的倍数 删除前几个
        if (locationList.size()%3 !=0){
            for (int i = 0; i < locationList.size() % 3; i++) {
                locationList.remove(i);
                degreeList.remove(i);
            }
        }

        //第二层 底部额外的部分
        int i = degreeNum / 3;
        //获取底部层数
        int bottomGegreeNum = i*3;
        for (int x = 0; x < bottomGegreeNum; x++) {
            locationList.add(new Double[]{3.0- ((double) 5.0)/ CHESS_SIZE *x,17.0});
            degreeList.add(x+1);
            locationList.add(new Double[]{9.0+ ((double) 5.0)/ CHESS_SIZE *x,17.0});
            degreeList.add(x+1);
        }
    }

    //第二层图片设置
    private void initSecondBarrier(){
        for (int i = 0; i < locationList.size() / 3; i++) {
            int i1 = (int) (Math.round(Math.random() * 15));
            for (int x = 0; x < 3; x++) {
                iconList.add(i1);
            }
        }
        Collections.shuffle(iconList); //打乱
    }

    private void addSmallLocation(List<Double[]> list,int mDegree,int repeatNum){  //判断是否添加成功，如果大于100次循环就强制结束
        Double[] location = {
                (double) Math.round(Math.random() * 12),
                (double) Math.round(Math.random() * 14)
        };
        //判断是否有重复
        boolean isRepeat = false;
        for (Double[] integers : list) {
            if (Math.abs(location[0]-integers[0])<2 && Math.abs(location[1]-integers[1])<2){
                isRepeat = true;
                break;
            }
        }

        if (!isRepeat){
            list.add(location);
            degreeList.add(mDegree);
        }else {
            repeatNum = repeatNum + 1;
            if (repeatNum <= 100){
                addSmallLocation(list,mDegree,repeatNum);
            }
        }
    }

    private void addGrassLocation(List<Integer[]> list,int repeatNum){  //判断是否添加成功，如果大于100次循环就强制结束
        Integer[] grassLocation = {(int) (Math.round(Math.random() * DimensionUtils.getScreenW())),(int) (Math.round(Math.random() * (DimensionUtils.getScreenH() - DimensionUtils.dp2px(90))))};
        //判断是否有重复
        boolean isRepeat = false;
        for (Integer[] integers : list) {
            if (Math.abs(grassLocation[0]-integers[0])< DimensionUtils.dp2px(30)
                    &&Math.abs(grassLocation[1]-integers[1])< DimensionUtils.dp2px(30)){
                isRepeat = true;
                break;
            }
        }

        if (!isRepeat){
            list.add(grassLocation);
        }else {
            repeatNum = repeatNum + 1;
            if (repeatNum <= 100){
                addGrassLocation(list, repeatNum);
            }
        }
    }

    public interface GameProgressListener {
        void onLevelPass();
        void onLevelFail();
    }

    public void setGameProgressListener(GameProgressListener gameProgressListener) {
        this.gameProgressListener = gameProgressListener;
    }
}
