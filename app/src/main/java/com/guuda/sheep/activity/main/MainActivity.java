package com.guuda.sheep.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.guuda.sheep.activity.BaseFragment;
import com.guuda.sheep.activity.main.fragment.MainLevelSelectFragment;
import com.guuda.sheep.activity.main.fragment.MainLoginFragment;
import com.guuda.sheep.activity.main.fragment.MainMenuFragment;
import com.guuda.sheep.activity.main.fragment.MainRankFragment;
import com.guuda.sheep.activity.main.fragment.MainRegisterFragment;
import com.guuda.sheep.activity.main.viewmodel.MainViewModel;
import com.guuda.sheep.activity.main.viewstate.BaseMainViewState;
import com.guuda.sheep.activity.main.viewstate.MainLevelSelectViewState;
import com.guuda.sheep.activity.main.viewstate.MainLoginViewState;
import com.guuda.sheep.activity.main.viewstate.MainMenuViewState;
import com.guuda.sheep.activity.main.viewstate.MainRankViewState;
import com.guuda.sheep.activity.main.viewstate.MainRegisterViewState;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.customview.GrassView;
import com.guuda.sheep.utils.PUtil;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewDataBinding binding;

    private List<GrassView> GrassViewList;  //草list
    private List<Integer[]> mGrasslocationList;  //草坐标列表
    private int grassNum = 28;  //草的数量

    private List<Integer> awardSheepRescoureList; //通关羊gif
    private int listSucceednum;  //通关羊数量

    private BaseFragment currentFragment;

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MMKV.initialize(this);
        GrassViewList = new ArrayList<>();
        mGrasslocationList = new ArrayList<>();
        awardSheepRescoureList = new ArrayList<>();
        initAwardSheepRescoure();
        initGrassView();
        initViewModel();

        //通关在首页增加一只
        LiveEventBus.get("succeed", Boolean.class)
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean bo) {
                        listSucceednum = listSucceednum+1;
                        ImageView iv = new ImageView(MainActivity.this);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(PUtil.dip2px(MainActivity.this, 60), PUtil.dip2px(MainActivity.this, 60));
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParams.leftMargin = (int) (PUtil.getScreenW(MainActivity.this)/2 - PUtil.dip2px(MainActivity.this, 50) - Math.random()*PUtil.dip2px(MainActivity.this, 160) + PUtil.dip2px(MainActivity.this, 80));
                        layoutParams.bottomMargin = (int) (PUtil.getScreenH(MainActivity.this)/5 - Math.random()*PUtil.dip2px(MainActivity.this, 60) + PUtil.dip2px(MainActivity.this, 30));
                        iv.setLayoutParams(layoutParams);
                        ((RelativeLayout)findViewById(R.id.rl)).addView(iv);

                        Glide.with(MainActivity.this)
                                .load(awardSheepRescoureList.get(listSucceednum-1))
                                .into(iv);
                    }
                });

        //游戏页面onresume
        LiveEventBus.get("GameOnresume", Boolean.class)
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean boo) {
                        AudioController.instance.resumeBackground();
                    }
                });

        currentFragment = new MainLoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, currentFragment).commit();


        AudioController.instance.playBackground(AudioController.BACKGROUND_MENU);
    }

    private void initGrassView(){
        for (int i = 0; i < grassNum; i++) {
            addGrassLocation(mGrasslocationList, 0);
        }
        for (Integer[] integers : mGrasslocationList) {
            GrassView grassView = new GrassView(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(PUtil.dip2px(this, 30), PUtil.dip2px(this, 30));
            layoutParams.leftMargin = integers[0];
            layoutParams.topMargin = integers[1];
            grassView.setLayoutParams(layoutParams);
            GrassViewList.add(grassView);
            ((RelativeLayout)findViewById(R.id.rl)).addView(grassView);
        }

        //抖动
        for (GrassView grassView : GrassViewList) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_grass);
            animation.setAnimationListener(new ReStartAnimationListener());
            grassView.startAnimation(animation);
        }
    }


    public MainViewModel getViewModel() {
        if (mViewModel == null) {
            mViewModel = new ViewModelProvider(this).get(MainViewModel.class);;
        }
        return mViewModel;
    }

    private void initViewModel() {
        getViewModel().getViewState().observe(this, tmpViewState -> {
            BaseMainViewState viewState = tmpViewState == null ? new MainLoginViewState() : tmpViewState;

            if (viewState instanceof MainLevelSelectViewState) {
                MainLevelSelectViewState levelSelectViewState = (MainLevelSelectViewState) viewState;
                if (!(currentFragment instanceof MainLevelSelectFragment)) {
                    MainLevelSelectFragment replaceFragment = new MainLevelSelectFragment();
                    replaceFragment.refreshViewState(levelSelectViewState);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, replaceFragment).commitNow();
                    currentFragment = replaceFragment;
                } else {
                    ((MainLevelSelectFragment) currentFragment).refreshViewState(levelSelectViewState);
                }
            } else if (viewState instanceof MainLoginViewState) {
                MainLoginViewState loginViewState = (MainLoginViewState) viewState;
                if (!(currentFragment instanceof MainLoginFragment)) {
                    MainLoginFragment replaceFragment = new MainLoginFragment();
                    replaceFragment.refreshViewState(loginViewState);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, replaceFragment).commitNow();
                    currentFragment = replaceFragment;
                } else {
                    ((MainLoginFragment) currentFragment).refreshViewState(loginViewState);
                }
            } else if (viewState instanceof MainMenuViewState) {
                MainMenuViewState menuViewState = (MainMenuViewState) viewState;
                if (!(currentFragment instanceof MainMenuFragment)) {
                    MainMenuFragment replaceFragment = new MainMenuFragment();
                    replaceFragment.refreshViewState(menuViewState);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, replaceFragment).commitNow();
                    currentFragment = replaceFragment;
                } else {
                    ((MainMenuFragment) currentFragment).refreshViewState(menuViewState);
                }
            } else if (viewState instanceof MainRankViewState) {
                MainRankViewState rankViewState = (MainRankViewState) viewState;
                if (!(currentFragment instanceof MainRankFragment)) {
                    MainRankFragment replaceFragment = new MainRankFragment();
                    replaceFragment.refreshViewState(rankViewState);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, replaceFragment).commitNow();
                    currentFragment = replaceFragment;
                } else {
                    ((MainRankFragment) currentFragment).refreshViewState(rankViewState);
                }
            } else if (viewState instanceof MainRegisterViewState) {
                MainRegisterViewState registerViewState = (MainRegisterViewState) viewState;
                if (!(currentFragment instanceof MainRegisterFragment)) {
                    MainRegisterFragment replaceFragment = new MainRegisterFragment();
                    replaceFragment.refreshViewState(registerViewState);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, replaceFragment).commitNow();
                    currentFragment = replaceFragment;
                } else {
                    ((MainRegisterFragment) currentFragment).refreshViewState(registerViewState);
                }
            }
        });
    }

    private void startAnim(){

        //以获取的战利品羊gif
        listSucceednum = MMKV.defaultMMKV().decodeInt("specialSheepListNum");
        if (listSucceednum>0){
            List<Integer> mList = new ArrayList<>();
            for (int i = 0; i < listSucceednum; i++) {
                mList.add(awardSheepRescoureList.get(i));
            }
            for (Integer recoures : mList) {
                ImageView iv = new ImageView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(PUtil.dip2px(this, 60), PUtil.dip2px(this, 60));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.leftMargin = (int) (PUtil.getScreenW(this)/2 - PUtil.dip2px(this, 50) - Math.random()*PUtil.dip2px(this, 160) + PUtil.dip2px(this, 80));
                layoutParams.bottomMargin = (int) (PUtil.getScreenH(this)/5 - Math.random()*PUtil.dip2px(this, 60) + PUtil.dip2px(this, 30));
                iv.setLayoutParams(layoutParams);
                ((RelativeLayout)findViewById(R.id.rl)).addView(iv);

                Glide.with(this)
                        .load(recoures)
                        .into(iv);
            }
        }
    }

    private void addGrassLocation(List<Integer[]> list,int repeatNum){  //判断是否添加成功，如果大于100次循环就强制结束
        Integer[] mGrasslocation = {(int) (Math.round(Math.random() * PUtil.getScreenW(this))),(int) (Math.round(Math.random() * (PUtil.getScreenH(this) - PUtil.dip2px(this,90))))};
        //判断是否有重复
        boolean isRepeat = false;
        for (Integer[] integers : list) {
            if (Math.abs(mGrasslocation[0]-integers[0])<PUtil.dip2px(this, 30)
                    &&Math.abs(mGrasslocation[1]-integers[1])<PUtil.dip2px(this, 30)){
                isRepeat = true;
                break;
            }
        }

        if (!isRepeat){
            list.add(mGrasslocation);
        }else {
            repeatNum = repeatNum + 1;
            if (repeatNum>100){
            }else {
                addGrassLocation(list,repeatNum);
            }
        }
    }

    private class ReStartAnimationListener implements Animation.AnimationListener {
        public void onAnimationEnd(Animation animation) {
            animation.reset();
            animation.setAnimationListener(new ReStartAnimationListener());
            animation.start();
        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }

    }

    private void initAwardSheepRescoure(){
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep2);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep3);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep4);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep5);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep6);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep7);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep8);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep9);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep10);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep11);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep12);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep13);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep14);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep15);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep16);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep17);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep18);
        awardSheepRescoureList.add(R.mipmap.ic_award_sheep19);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startAnim();
    }

    @Override
    protected void onDestroy() {
        for (GrassView grassView : GrassViewList) {
            grassView.getAnimation().cancel();
        }

        super.onDestroy();

        AudioController.instance.pauseBackground();
    }
}