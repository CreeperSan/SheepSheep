package com.guuda.sheep.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.SettingActivity;
import com.guuda.sheep.activity.game.dialog.GameFailDialog;
import com.guuda.sheep.activity.main.dialog.MainSettingDialog;
import com.guuda.sheep.activity.main.receiver.GameLevelNotifyListener;
import com.guuda.sheep.activity.main.receiver.GameLevelReceiver;
import com.guuda.sheep.pref.PrefManager;
import com.guuda.sheep.pref.PrefUpdateListener;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.customview.NoFastClickListener;
import com.guuda.sheep.customview.SheepView;
import com.guuda.sheep.databinding.ActivityGameBinding;
import com.guuda.sheep.activity.game.dialog.GamePassDialog;
import com.guuda.sheep.activity.game.dialog.GameCompleteDialog;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @ProjectName: MySunSheep
 * @ClassName: GameActivity
 * @Author: littletree
 * @CreateDate: 2022/10/26/026 10:42
 */
public class GameActivity extends AppCompatActivity {
    private final static int DEFAULT_DEGREE_NUM = 1;

    ActivityGameBinding binding;

    //过关dialog
    GamePassDialog dialogPass;
    GameFailDialog dialogFail;

    //通关dialog
    GameCompleteDialog dialogComplete;
    List<Integer> awardSheepRescoureList;

    private PrefUpdateListener<Boolean> mutePrefListener;

    private int barrierNum = 1;  //关卡
    SheepView sheepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AudioController.instance.playBackground(AudioController.BACKGROUND_GAME);

        initDialog();
        initView();
        initTitle();
        initHeadLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AudioController.instance.playBackground(AudioController.BACKGROUND_MENU);

        PrefManager.instance.removeMuteChangeListener(mutePrefListener);
    }

    private void initView(){
        sheepView = new SheepView(GameActivity.this, barrierNum);
        binding.rl.addView(sheepView);

        sheepView.setGameProgressListener(new SheepView.GameProgressListener() {
            @Override
            public void onLevelPass() {
                if (1 == barrierNum){
                    barrierNum = barrierNum + 1;

                    dialogPass.show();

                    Intent intent = new Intent(GameLevelReceiver.ACTION);
                    intent.putExtra("event", GameLevelNotifyListener.LEVEL_1_PASS);
                    sendBroadcast(intent);
                }else {
                    //通关
                    int listnum = MMKV.defaultMMKV().decodeInt("specialSheepListNum");
                    if (listnum == 18){
                        Toast.makeText(GameActivity.this, "你已收集完所有类别", Toast.LENGTH_SHORT).show();
                        listnum = 17;
                    }
                    MMKV.defaultMMKV().encode("specialSheepListNum", listnum+1);

                    //随机gif
                    dialogComplete.show();

                    Intent intent = new Intent(GameLevelReceiver.ACTION);
                    intent.putExtra("event", GameLevelNotifyListener.LEVEL_2_PASS);
                    sendBroadcast(intent);
                }

            }

            @Override
            public void onLevelFail() {
                dialogFail.show();
            }

        });

        LiveEventBus.get("plieNum", Integer.class)
                .observe(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer num) {
                        sheepView.setBarrierNum(2,num);
                    }
                });
    }

    private void refreshTimeText() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        binding.titleTV.setText("- "+month+"月"+day+"日"+" -");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LiveEventBus.get("GameOnresume").post(true);
    }

    private void initTitle(){
        refreshTimeText();

        binding.titleTV.setOnClickListener(new NoFastClickListener() {
            @Override
            protected void onSingleClick() {
                if (barrierNum == 2){
                    startActivity(new Intent(GameActivity.this, SettingActivity.class));
                }
            }
        });
    }

    private void initDialog() {
        dialogPass = new GamePassDialog(this);
        dialogPass.setOnNextLevelListener(v -> {
            refreshTimeText();
            sheepView.setBarrierNum(barrierNum, DEFAULT_DEGREE_NUM);
        });

        dialogFail = new GameFailDialog(this);
        dialogFail.setOnBackListener(v -> {
            dialogPass.dismiss();
            sheepView.setBarrierNum(barrierNum, DEFAULT_DEGREE_NUM);
        });

        dialogComplete = new GameCompleteDialog(this);
        dialogComplete.setCloseListener(dialog -> {
            LiveEventBus.get("succeed").post(true);
            finish();
        });

        awardSheepRescoureList = new ArrayList<>();
        initAwardSheepRescoure();
    }

    private void refreshMuteButton(ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(AudioController.instance.isMute() ? R.drawable.ic_music_off_white_24 : R.drawable.ic_music_white_24)
                .into(imageView);
    }

    private void initHeadLayout() {
        // 音效按钮
        mutePrefListener = value -> refreshMuteButton(binding.head.soundBtn);
        refreshMuteButton(binding.head.soundBtn);
        binding.head.soundBtn.setOnClickListener(v -> {
            boolean newMuteState = AudioController.instance.toggleMute();
            PrefManager.instance.setMute(newMuteState);
            refreshMuteButton(binding.head.soundBtn);
        });
        PrefManager.instance.addMuteChangeListener(mutePrefListener);

        // 设置按钮
        binding.head.settingBtn.setOnClickListener(v -> {
            MainSettingDialog dialog = new MainSettingDialog(this);
            dialog.show();
        });
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
}
