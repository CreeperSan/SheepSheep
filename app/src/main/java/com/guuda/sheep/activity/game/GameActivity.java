package com.guuda.sheep.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.SettingActivity;
import com.guuda.sheep.activity.game.dialog.GameFailDialog;
import com.guuda.sheep.activity.game.dialog.GameSettingDialog;
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

import java.util.Calendar;


public class GameActivity extends AppCompatActivity {
    public static final String INTENT_LEVEL = "level";

    private final static int DEFAULT_DEGREE_NUM = 1;

    ActivityGameBinding binding;

    // 弹窗
    GamePassDialog dialogPass;
    GameFailDialog dialogFail;
    GameCompleteDialog dialogComplete;
    GameSettingDialog dialogSetting;

    private PrefUpdateListener<Boolean> mutePrefListener;

    private int level = 1;  //关卡

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AudioController.instance.playBackground(AudioController.BACKGROUND_GAME);

        initIntent();
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

    private void initIntent() {
        level = getIntent().getIntExtra(INTENT_LEVEL, level);
    }

    private void initView(){
        // 初始化时载入关卡
        binding.sheepView.loadLevel(level, DEFAULT_DEGREE_NUM);

        binding.sheepView.setGameProgressListener(new SheepView.GameProgressListener() {
            @Override
            public void onLevelPass() {
                if (1 == level){
                    level = level + 1;

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

                AudioController.instance.playEffect(AudioController.SOUND_COMPLETE);
            }

            @Override
            public void onLevelFail() {
                dialogFail.show();

                AudioController.instance.playEffect(AudioController.SOUND_FAIL);
            }

        });

        LiveEventBus.get("plieNum", Integer.class)
                .observe(this, num -> {
                    binding.sheepView.loadLevel(2,num);
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
                if (level == 2){
                    startActivity(new Intent(GameActivity.this, SettingActivity.class));
                }
            }
        });
    }

    private void initDialog() {
        dialogPass = new GamePassDialog(this);
        dialogPass.setOnNextLevelListener(v -> {
            refreshTimeText();
            binding.sheepView.loadLevel(level, DEFAULT_DEGREE_NUM);
        });

        dialogFail = new GameFailDialog(this);
        dialogFail.setOnBackListener(v -> {
            dialogPass.dismiss();
            binding.sheepView.loadLevel(level, DEFAULT_DEGREE_NUM);
        });

        dialogComplete = new GameCompleteDialog(this);
        dialogComplete.setCloseListener(dialog -> {
            LiveEventBus.get("succeed").post(true);
            finish();
        });

        dialogSetting = new GameSettingDialog(this);
        dialogSetting.setListener(new GameSettingDialog.EventListener() {
            @Override
            public void onSoundOn() {
                AudioController.instance.unmute();
                PrefManager.instance.setMute(false);
            }

            @Override
            public void onSoundOff() {
                AudioController.instance.mute();
                PrefManager.instance.setMute(true);
            }

            @Override
            public void onResume() {

            }

            @Override
            public void onRestart() {
                binding.sheepView.loadLevel(level, DEFAULT_DEGREE_NUM);
            }

            @Override
            public void onGiveUp() {
                finish();
            }
        });

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
            dialogSetting.show();
        });
    }

    @Override
    public void onBackPressed() {
        if (dialogSetting.isShowing()) {
            dialogSetting.dismiss();
        } else {
            dialogSetting.show();
        }
    }
}
