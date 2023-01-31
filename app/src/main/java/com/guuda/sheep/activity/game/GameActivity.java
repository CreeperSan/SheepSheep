package com.guuda.sheep.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.SettingActivity;
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
    ActivityGameBinding binding;

    //过关dialog
    GamePassDialog dialog_pass;
    GamePassDialog.Builder dialog_passbuilder;
    TextView dialog_nolivetitle;
    TextView dialog_nolive_btn_one;
    ImageView iv;

    //通关dialog
    GameCompleteDialog dialog_succeed;
    GameCompleteDialog.Builder dialog_succeedbuilder;
    AwardView view_award;
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

        initdialog();
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
                    dialog_nolivetitle.setText("过关");
                    dialog_nolive_btn_one.setText("进入下一关");
                    barrierNum = barrierNum + 1;

                    Glide.with(GameActivity.this)
                            .load(R.mipmap.ic_succeed)
                            .into(iv);

                    dialog_nolive_btn_one.setOnClickListener(new NoFastClickListener() {
                        @Override
                        protected void onSingleClick() {
                            dialog_pass.dismiss();
                            refreshTimeText();
                            sheepView.setBarrierNum(barrierNum,0);
                        }
                    });

                    dialog_pass.show();
                }else {
                    //通关
                    view_award.startAnim();

                    int listnum = MMKV.defaultMMKV().decodeInt("specialSheepListNum");
                    if (listnum == 18){
                        Toast.makeText(GameActivity.this, "你已收集完所有类别", Toast.LENGTH_SHORT).show();
                        listnum = 17;
                    }
                    MMKV.defaultMMKV().encode("specialSheepListNum", listnum+1);

                    view_award.setRescoure(awardSheepRescoureList.get(listnum));
                    //随机gif
                    dialog_succeed.show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(0X123);
                        }
                    },4000);

                }

            }

            @Override
            public void onLevelFail() {
                dialog_nolivetitle.setText("你失败了 再接再厉");
                dialog_nolive_btn_one.setText("重新开始");
                Glide.with(GameActivity.this)
                        .load(R.mipmap.ic_fail)
                        .into(iv);

                dialog_nolive_btn_one.setOnClickListener(new NoFastClickListener() {
                    @Override
                    protected void onSingleClick() {
                        dialog_pass.dismiss();
                        sheepView.setBarrierNum(barrierNum,0);
                    }
                });
                dialog_pass.show();
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

    private void initdialog() {
        dialog_passbuilder = new GamePassDialog.Builder(GameActivity.this);
        dialog_pass = dialog_passbuilder.create();
        dialog_nolivetitle = dialog_passbuilder.getTitleTextView();
        dialog_nolive_btn_one = dialog_passbuilder.getBtn_one();
        iv = dialog_passbuilder.getIv_gif();
        dialog_pass.setCanceledOnTouchOutside(false);

        dialog_succeedbuilder = new GameCompleteDialog.Builder(GameActivity.this);
        dialog_succeed = dialog_succeedbuilder.create();
        view_award = dialog_succeedbuilder.getView_award();
        dialog_succeed.setCanceledOnTouchOutside(false);

        awardSheepRescoureList = new ArrayList<>();
        initAwardSheepRescoure();
    }

    /**
     * 更新的Handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0X123: {
                    LiveEventBus.get("succeed").post(true);
                    view_award.cancelAnim();
                    finish();
                    break;
                }
            }
        }
    };

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
