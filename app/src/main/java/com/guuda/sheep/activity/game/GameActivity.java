package com.guuda.sheep.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.guuda.sheep.R;
import com.guuda.sheep.SettingActivity;
import com.guuda.sheep.activity.game.dialog.GameFailDialog;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.customview.AwardView;
import com.guuda.sheep.customview.NoFastClickListener;
import com.guuda.sheep.customview.SheepView;
import com.guuda.sheep.databinding.ActivityGameBinding;
import com.guuda.sheep.activity.game.dialog.GamePassDialog;
import com.guuda.sheep.activity.game.dialog.GameCompleteDialog;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
        initview();
        initTitle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AudioController.instance.playBackground(AudioController.BACKGROUND_MENU);
    }

    private void initview(){
        sheepView = new SheepView(GameActivity.this, barrierNum);
        binding.rl.addView(sheepView);

        sheepView.setGameProgressListener(new SheepView.GameProgressListener() {
            @Override
            public void onLevelPass() {
                if (1 == barrierNum){
                    barrierNum = barrierNum + 1;

                    dialogPass.show();
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
