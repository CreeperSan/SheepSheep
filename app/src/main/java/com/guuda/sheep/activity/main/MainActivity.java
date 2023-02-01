package com.guuda.sheep.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.activity.main.receiver.GameLevelNotifyListener;
import com.guuda.sheep.activity.main.receiver.GameLevelReceiver;
import com.guuda.sheep.database.entity.UserInfo;
import com.guuda.sheep.databinding.ActivityMainBinding;
import com.guuda.sheep.repository.AccountRepository;
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
import com.guuda.sheep.utils.DimensionUtils;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    private List<Integer[]> mGrassPositionList;  //草坐标列表
    private final static int GRASS_NUM = 28;  //草的数量

    private List<Integer> awardSheepResourcesList; //通关羊gif
    private int listSucceedNum;  //通关羊数量

    private BaseFragment currentFragment;

    private MainViewModel mViewModel;

    private GameLevelReceiver gameLevelReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        MMKV.initialize(this);
        mGrassPositionList = new ArrayList<>();
        awardSheepResourcesList = new ArrayList<>();
        initAwardSheepResources();
        initGrassView();
        initViewModel();
        initReceiver();

        //通关在首页增加一只
        LiveEventBus.get("succeed", Boolean.class)
                .observe(this, bo -> {
                        listSucceedNum = listSucceedNum +1;
                        ImageView iv = new ImageView(MainActivity.this);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DimensionUtils.dp2px(60), DimensionUtils.dp2px(60));
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParams.leftMargin = (int) (DimensionUtils.getScreenW()/2 - DimensionUtils.dp2px( 50) - Math.random()* DimensionUtils.dp2px(160) + DimensionUtils.dp2px(80));
                        layoutParams.bottomMargin = (int) (DimensionUtils.getScreenH()/5 - Math.random()* DimensionUtils.dp2px(60) + DimensionUtils.dp2px(30));
                        iv.setLayoutParams(layoutParams);
                        binding.rl.addView(iv);

                        Glide.with(MainActivity.this)
                                .load(awardSheepResourcesList.get(listSucceedNum -1))
                                .into(iv);
                });

        //游戏页面onresume
        LiveEventBus.get("GameOnresume", Boolean.class)
                .observe(this, boo -> {
                        AudioController.instance.resumeBackground();
                });

        currentFragment = new MainLoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, currentFragment).commit();


        AudioController.instance.playBackground(AudioController.BACKGROUND_MENU);
    }

    private void initGrassView(){
        binding.grassLayout.removeAllViews();

        for (int i = 0; i < GRASS_NUM; i++) {
            addGrassLocation(mGrassPositionList, 0);
        }
        for (Integer[] integers : mGrassPositionList) {
            GrassView grassView = new GrassView(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DimensionUtils.dp2px(30), DimensionUtils.dp2px(30));
            layoutParams.leftMargin = integers[0];
            layoutParams.topMargin = integers[1];
            grassView.setLayoutParams(layoutParams);
            binding.grassLayout.addView(grassView);
        }

    }


    public MainViewModel getViewModel() {
        if (mViewModel == null) {
            mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
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

    private void initReceiver() {
        gameLevelReceiver = new GameLevelReceiver(event -> {
            MainViewModel viewModel = getViewModel();
            if (viewModel == null) {
                return;
            }

            UserInfo userInfo = viewModel.getCurrentUserInfo().getValue();
            if (userInfo == null) {
                return;
            }

            switch (event) {
                case GameLevelNotifyListener.LEVEL_1_PASS : {
                    Disposable disposable = AccountRepository.updateReachLevel(userInfo.username, 1).subscribe(val -> {
                        Log.e("TAG", "第一关通关");
                    }, Throwable::printStackTrace);
                    break;
                }
                case GameLevelNotifyListener.LEVEL_2_PASS : {
                    Disposable disposable = AccountRepository.updateReachLevel(userInfo.username, 2).subscribe(val -> {
                        Log.e("TAG", "第二关通关");
                    }, Throwable::printStackTrace);

                    userInfo.highScore += 1;
                    Disposable disposableHighScore = AccountRepository.updateHighScore(userInfo.username, userInfo.highScore).subscribe(val -> {
                        Log.e("TAG", "第二关通关积分+1");
                    }, Throwable::printStackTrace);
                    break;
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GameLevelReceiver.ACTION);
        registerReceiver(gameLevelReceiver, intentFilter);
    }

    private void startAnim(){

        //以获取的战利品羊gif
        listSucceedNum = MMKV.defaultMMKV().decodeInt("specialSheepListNum");
        if (listSucceedNum > 0){
            for (Integer resources : awardSheepResourcesList) {
                ImageView iv = new ImageView(MainActivity.this);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DimensionUtils.dp2px(60), DimensionUtils.dp2px(60));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.leftMargin = (int) (DimensionUtils.getScreenW()/2 - DimensionUtils.dp2px(50) - Math.random()* DimensionUtils.dp2px(160) + DimensionUtils.dp2px(80));
                layoutParams.bottomMargin = (int) (DimensionUtils.getScreenH()/5 - Math.random()* DimensionUtils.dp2px(60) + DimensionUtils.dp2px(30));
                iv.setLayoutParams(layoutParams);
                binding.rl.addView(iv);

                Glide.with(this)
                        .load(resources)
                        .into(iv);
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
            if (repeatNum>100){
            }else {
                addGrassLocation(list,repeatNum);
            }
        }
    }

    private void initAwardSheepResources(){
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep2);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep3);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep4);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep5);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep6);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep7);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep8);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep9);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep10);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep11);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep12);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep13);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep14);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep15);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep16);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep17);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep18);
        awardSheepResourcesList.add(R.mipmap.ic_award_sheep19);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startAnim();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        AudioController.instance.pauseBackground();

        unregisterReceiver(gameLevelReceiver);
    }
}