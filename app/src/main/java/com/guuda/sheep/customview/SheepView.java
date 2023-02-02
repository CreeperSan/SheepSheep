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
import com.guuda.sheep.activity.game.model.Chess;
import com.guuda.sheep.activity.game.view.ChessView;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.utils.DimensionUtils;
import com.guuda.sheep.utils.RandomIntegerGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private static final int GRASS_COUNT = 28;  //草的数量

    private final static int CHESS_SIZE = 24;  //棋子大小的一半
    private List<ChessView> showChessList;   //棋牌区域image的list
    private List<ChessView> takeinChessList;   //收纳区image的list
    private final static int MAX_DEGREE = 20;  //第二关最多层数
    private final static int MAX_CHESS_NUM = 36; //第二关 单层棋子最多数量
    private int degreeNum = 0;  //获取层数 (为0时，从10-MAX_DEGREE中随机获取层数,有传入用传入)

    private final long downDelayTime = 500;
    private final static long DURATION_CHESS_DARKEN = 500;
    private final static long DURATION_CHESS_LIGHTEN = 500;
    private final static long DURATION_TAKEIN = 40;
    private final static long DURATION_QUEUE_RESORT = 40;
    private final long changeTime = 600;

    private final static int OFFSET_DP_GAME = 156;

    private final static int GRASS_SIZE_DP = 30;

    private final List<Chess> gameChessList = new ArrayList<>();
    private final List<Chess> queueChessList = new ArrayList<>();


    private GameProgressListener gameProgressListener;

    private boolean toolBrightOut = true;
    private boolean toolRecall = true;
    private boolean toolShuffle = true;

    public SheepView(Context context) {
        this(context,null);
    }

    public SheepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        showChessList = new ArrayList<>();
        takeinChessList = new ArrayList<>();

        initView(context);
        initGrassView();
        refreshToolButton();
    }

    private void initView(Context context) {
        sheepLayout = inflate(context,R.layout.layout_sheep, this);
        rlAllLayout = sheepLayout.findViewById(R.id.rl_alllayout);
        grassLayout = sheepLayout.findViewById(R.id.grassLayout);
        chessBackground = sheepLayout.findViewById(R.id.stv_container);
        toolBringToOutsideView = sheepLayout.findViewById(R.id.toolBringToOutside);
        toolRecallView = sheepLayout.findViewById(R.id.toolRecall);
        toolShuffleView = sheepLayout.findViewById(R.id.toolShuffle);


        // 底部暂存区宽高设置
        LayoutParams layoutParams = (RelativeLayout.LayoutParams) chessBackground.getLayoutParams();
        layoutParams.width = DimensionUtils.dp2px(CHESS_SIZE *14+10);
        layoutParams.height = DimensionUtils.dp2px(CHESS_SIZE *2+10);
        chessBackground.setLayoutParams(layoutParams);
    }

    private void initBarrier(){
        for (int i = 0; i < gameChessList.size(); i++) {
            ChessView chessView = new ChessView(mContext);
            Chess chess = gameChessList.get(i);
            locationToMargin(chessView, new Double[]{ chess.x, chess.y }, chess.layer);
            chessView.setChess(chess.type);
            chessView.setChessModel(chess);
            chessView.setOnClickListener(new NoFastClickListener() {
                @Override
                protected void onSingleClick() {
                    AudioController.instance.playEffect(AudioController.SOUND_CLICK);
                    showChessList.remove(chessView);
                    startTakeinAnim(chessView);

                    judgeCanClick(false);
                }
            });

            showChessList.add(chessView);
            rlAllLayout.addView(chessView);
        }

        for (ChessView chessView : showChessList) {
            int mTime = (int) (Math.random() * 700);
            startDownAnim(chessView,mTime);

            if (judgeNeedToDark(chessView)){
                startToDarkAnim(chessView,mTime+downDelayTime);
            }
        }
    }

    private void refreshToolButton() {
        // 道具 - 带到外面
        toolBringToOutsideView.setAlpha(toolBrightOut ? 1f : 0.5f);
        toolBringToOutsideView.setOnClickListener(toolBrightOut ? v -> {
            toolBrightOut = false;
            refreshToolButton();
        } : null);

        // 道具 - 撤回
        toolRecallView.setAlpha(toolRecall ? 1f : 0.5f);
        toolRecallView.setOnClickListener(toolRecall ? v -> {
            toolRecall = false;
            refreshToolButton();
        } : null);

        // 道具 - 打乱顺序
        toolShuffleView.setAlpha(toolShuffle ? 1f : 0.5f);
        toolShuffleView.setOnClickListener(toolShuffle ? v -> {
            toolShuffle = false;
            refreshToolButton();
        } : null);
    }

    public void loadLevel(int barrierNum, int mDegreeNum) {
        this.degreeNum = mDegreeNum;

        if (showChessList.size()>0){
            for (ChessView chessView : showChessList){
                rlAllLayout.removeView(chessView);
            }
        }

        if (takeinChessList.size()>0){
            for (ChessView chessView : takeinChessList){
                rlAllLayout.removeView(chessView);
            }
        }

        showChessList.clear();
        takeinChessList.clear();
        gameChessList.clear();

        if (1 == barrierNum){
            initFirstLocationList();
        }else {
            initSecondLocationList();
        }

        // 重排序
        Collections.reverse(gameChessList);
        gameChessList.sort(Comparator.comparingInt(o -> -o.layer));

        initBarrier();
        judgeCanClick(false);

        // 刷新按钮
        toolBrightOut = true;
        toolRecall = true;
        toolShuffle = true;
        refreshToolButton();
    }

    /**
     * 初始化从顶部下落的动画
     * @param chessView
     * @param delayTime
     */
    private void startDownAnim(ChessView chessView, long delayTime){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(downDelayTime);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                Double[] ilocation = chessView.getChessModel().getLocation();

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) chessView.getLayoutParams();
                layoutParams.leftMargin = (int) (DimensionUtils.getScreenW()/2 -
                        DimensionUtils.dp2px(14* CHESS_SIZE)/2 +
                        DimensionUtils.dp2px(CHESS_SIZE)*ilocation[0]);

                //从屏幕上方一个棋子的位置下落，多加2*20
                double value1 = DimensionUtils.dp2px(OFFSET_DP_GAME) +
                        DimensionUtils.dp2px(CHESS_SIZE) * ilocation[1];

                value1 = value1/100 *animatedValue - DimensionUtils.dp2px(2* CHESS_SIZE);
                layoutParams.topMargin = (int) value1;
                chessView.setLayoutParams(layoutParams);
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
                Double[] tag = chessView.getChessModel().getLocation();
                if (tag[1] == 17&&(tag[0] == 3||tag[0] == 9)){  //滚动放大动画
                    ValueAnimator rollValueAnimator = ValueAnimator.ofFloat(0f, 100f);
                    rollValueAnimator.setDuration(changeTime);
                    rollValueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
                    rollValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float animatedValue = (float) valueAnimator.getAnimatedValue();

                            Log.i("孙", "animatedValue/100: "+animatedValue/100);
                            chessView.setRotation(animatedValue/100*360);
                        }
                    });
                    rollValueAnimator.start();
                }
            }
        });
    }

    private void startToDarkAnim(ChessView chessView,long delayTime){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(DURATION_CHESS_DARKEN);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(animator -> {
                float animatedValue = (float) animator.getAnimatedValue();
                chessView.setForeground(AppCompatResources.getDrawable(mContext, R.drawable.fg_black_alpha70));
                chessView.getForeground().setAlpha((int) (((double) 255)/100*animatedValue));
        });
        valueAnimator.setStartDelay(delayTime);
        valueAnimator.start();
    }

    private void startToLightAnim(ChessView chessView){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(DURATION_CHESS_LIGHTEN);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                if (null!=chessView.getForeground()){
                    chessView.getForeground().setAlpha((int) (255- ((double) 255)/100*animatedValue));
                }
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                chessView.setForeground(null);
            }
        });
        valueAnimator.start();
    }

    private void startTakeinAnim(ChessView chessView){  //放大缩小后，从上方容器到下方容器 移动动画
        chessView.setState(ChessView.STATE_IN_QUEUE);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(DURATION_TAKEIN);
        valueAnimator.setStartDelay(ChessView.DURATION_CLICK);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                int targetLeftMargin = (DimensionUtils.getScreenW()- DimensionUtils.dp2px(14* CHESS_SIZE))/2 + (takeinChessList.size())* DimensionUtils.dp2px(2* CHESS_SIZE);
                int targetTopMargin = DimensionUtils.getScreenH() - DimensionUtils.dp2px(25+2* CHESS_SIZE) - DimensionUtils.dp2px(42+16);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) chessView.getLayoutParams();
                int leftMargin = layoutParams.leftMargin;
                float value1 = (((float) leftMargin)-targetLeftMargin)/100 *animatedValue;
                layoutParams.leftMargin = (int) (leftMargin - value1);

                int topMargin = layoutParams.topMargin;
                float value2 = (((float) topMargin)-targetTopMargin)/100 *animatedValue;
                layoutParams.topMargin = (int) (topMargin - value2);

                chessView.setLayoutParams(layoutParams);
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

                takeinChessList.add(chessView);
                Map<Integer,List<Integer>> mMap= new HashMap<>();  //key资源序号，list 图片在takeinChessList的位置
                for (int i = 0; i < takeinChessList.size(); i++) {
                    if (null == mMap.get(( takeinChessList.get(i).getChessModel().type))){
                        List<Integer> picNumList = new ArrayList<>();
                        picNumList.add(i);
                        mMap.put((takeinChessList.get(i).getChessModel().type),picNumList);
                    }else {
                        mMap.get((takeinChessList.get(i).getChessModel().type)).add(i);
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
                    for (ChessView view : showChessList) {
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

                                            takeinChessList.clear();

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

    private void startLeftAnim(List<ChessView> ivList,List<Integer> mlist){  //被清除的3个序号
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
    private void startLeftSmallAnim(List<ChessView> ivAllList,ChessView chessView,int distanceNum,List<Integer> mlist){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) chessView.getLayoutParams();
        float leftMargin = layoutParams.leftMargin;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 100f);
        valueAnimator.setDuration(DURATION_QUEUE_RESORT);
        valueAnimator.setInterpolator(new LinearInterpolator());  //线性变化

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                float v = (((float) DimensionUtils.dp2px(2* CHESS_SIZE)) * distanceNum) / 100f * animatedValue;
                layoutParams.leftMargin = (int) (leftMargin - v);
                chessView.setLayoutParams(layoutParams);
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

                            List<ChessView> newList = new ArrayList<>();
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

    private void locationToMargin(ChessView chessView,Double[] mLocation,int mDegree){  //坐标转换成marginleft和margintop,层级：最上层为1，依次增加
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                DimensionUtils.dp2px(2* CHESS_SIZE),
                DimensionUtils.dp2px(2* CHESS_SIZE)
        );
        layoutParams.topMargin = -DimensionUtils.dp2px(2* CHESS_SIZE); // 负数是实例化到界面外面，防止动画播放前被看见
        chessView.setLayoutParams(layoutParams);

        chessView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.bg_chessview));
        chessView.setPadding(
                DimensionUtils.dp2px(5),
                DimensionUtils.dp2px(5),
                DimensionUtils.dp2px(5),
                DimensionUtils.dp2px(5)
        );
    }

    private void judgeCanClick(boolean onlyChangeViewClickable){   //判断剩下棋子是否可以点击 ,无法点击background设置蒙版
        for (int i = 0; i < showChessList.size(); i++) {
            boolean canClick = true;
            List<ChessView> mlist = new ArrayList<>(showChessList);
            mlist.remove(i);

            Double[] ilocation = showChessList.get(i).getChessModel().getLocation();
            int idegree = showChessList.get(i).getChessModel().layer;
            for (ChessView chessView : mlist) {
                Double[] lt = (Double[]) chessView.getChessModel().getLocation();
                int dg = chessView.getChessModel().layer;
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

    private boolean judgeNeedToDark(ChessView iv){   //判断是否需要变暗
        boolean needDark = false;

        Double[] ilocation = iv.getChessModel().getLocation();
        int idegree = iv.getChessModel().layer;
        for (ChessView chessView : showChessList) {
            Double[] lt = chessView.getChessModel().getLocation();
            int dg = chessView.getChessModel().layer;
            if (idegree > dg){
                if (Math.abs(lt[0]-ilocation[0])<2&&Math.abs(lt[1]-ilocation[1])<2){
                    needDark = true;
                    break;
                }
            }
        }

        return needDark;
    }

    private void initGrassView(){
        grassLayout.removeAllViews();

        List<Integer[]> grassLocationList = new ArrayList<>();

        for (int i = 0; i < GRASS_COUNT; i++) {
            int loopCount = 0;
            while (loopCount < 100) {
                Integer[] grassLocation = {(int) (Math.round(Math.random() * DimensionUtils.getScreenW())),(int) (Math.round(Math.random() * (DimensionUtils.getScreenH() - DimensionUtils.dp2px(90))))};
                // 计算是否重叠
                boolean isRepeat = false;
                for (Integer[] integers : grassLocationList) {
                    if (
                            Math.abs(grassLocation[0]-integers[0])< DimensionUtils.dp2px(GRASS_SIZE_DP) &&
                            Math.abs(grassLocation[1]-integers[1])< DimensionUtils.dp2px(GRASS_SIZE_DP)
                    ){
                        isRepeat = true;
                        break;
                    }
                }

                if (!isRepeat){
                    grassLocationList.add(grassLocation);
                    break;
                }
                loopCount += 1;
            }
        }

        for (Integer[] grassLocation : grassLocationList) {
            GrassView grassView = new GrassView(mContext);
            LayoutParams layoutParams = new LayoutParams(DimensionUtils.dp2px(GRASS_SIZE_DP), DimensionUtils.dp2px(GRASS_SIZE_DP));
            layoutParams.leftMargin = grassLocation[0];
            layoutParams.topMargin = grassLocation[1];
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
        RandomIntegerGenerator generator = new RandomIntegerGenerator(0, 15, 3);

        gameChessList.add(new Chess(3.0, 4.0, 1, generator.next()));
        gameChessList.add(new Chess(6.0 , 4.0, 1, generator.next()));
        gameChessList.add(new Chess(9.0 , 4.0, 1, generator.next()));
        gameChessList.add(new Chess(3.0 , 8.0, 1, generator.next()));
        gameChessList.add(new Chess(6.0 , 8.0, 1, generator.next()));
        gameChessList.add(new Chess(9.0 , 8.0, 1, generator.next()));
        gameChessList.add(new Chess(3.0 , 12.0, 1, generator.next()));
        gameChessList.add(new Chess(6.0 , 12.0, 1, generator.next()));
        gameChessList.add(new Chess(9.0 , 12.0, 1, generator.next()));

        gameChessList.add(new Chess(3.0 , 3.0,2, generator.next()));
        gameChessList.add(new Chess(6.0 , 3.0,2, generator.next()));
        gameChessList.add(new Chess(9.0 , 3.0,2, generator.next()));
        gameChessList.add(new Chess(3.0 , 7.0,2, generator.next()));
        gameChessList.add(new Chess(6.0 , 7.0,2, generator.next()));
        gameChessList.add(new Chess(9.0 , 7.0,2, generator.next()));
        gameChessList.add(new Chess(3.0 , 11.0, 2, generator.next()));
        gameChessList.add(new Chess(6.0 , 11.0, 2, generator.next()));
        gameChessList.add(new Chess(9.0 , 11.0, 2, generator.next()));

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
        RandomIntegerGenerator generator = new RandomIntegerGenerator(0, 15, 3);
        for (int i = 0; i < degreeContentNum.size(); i++) { //每层

            List<Chess> layerChestList = new ArrayList<>();
            for (int x = 0; x < degreeContentNum.get(i); x++) {  //每层中每个的判断
                int loopCount = 100;
                while (loopCount > 0) {
                    Double[] location = {
                            (double) Math.round(Math.random() * 12),
                            (double) Math.round(Math.random() * 14)
                    };
                    //判断是否有重复
                    boolean isRepeat = false;
                    for (Chess chess : layerChestList) {
                        if (Math.abs(location[0]-chess.x)<2 && Math.abs(location[1]-chess.y)<2){
                            isRepeat = true;
                            break;
                        }
                    }

                    if (!isRepeat){
                        layerChestList.add(new Chess(location[0], location[1], i+1, generator.next()));
                        break;
                    }
                    loopCount--;
                }

            }

            gameChessList.addAll(layerChestList);
        }

        //必须为3的倍数 删除后几个
        int overFlowCount = gameChessList.size() % 3;
        while(overFlowCount-- > 0) {
            gameChessList.remove(gameChessList.size() - 1);
        }

//        //第二层 底部额外的部分
//        int i = degreeNum / 3;
//        //获取底部层数
//        int bottomGegreeNum = i*3;
//        for (int x = 0; x < bottomGegreeNum; x++) {
//            locationList.add(new Double[]{3.0- (5.0)/ CHESS_SIZE *x,17.0});
//            degreeList.add(x+1);
//            locationList.add(new Double[]{9.0+ (5.0)/ CHESS_SIZE *x,17.0});
//            degreeList.add(x+1);
//        }
    }

    public interface GameProgressListener {
        void onLevelPass();
        void onLevelFail();
    }

    public void setGameProgressListener(GameProgressListener gameProgressListener) {
        this.gameProgressListener = gameProgressListener;
    }
}
