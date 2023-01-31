package com.guuda.sheep.activity.complete;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.guuda.sheep.R;
import com.guuda.sheep.activity.BaseActivity;
import com.guuda.sheep.databinding.ActivityCompleteBinding;

public class CompleteActivity extends BaseActivity {
    public final static String INTENT_KEY_SCORE = "score";

    ActivityCompleteBinding binding;

    int intentScore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intentScore = getIntent().getIntExtra(INTENT_KEY_SCORE, intentScore);

        binding.scoreTV.setText("得分：" + intentScore);

        binding.head.settingBtn.setVisibility(View.GONE);

        binding.head.userBtn.setVisibility(View.GONE);

        binding.exitBtn.setOnClickListener(v -> {
            finish();
        });


    }
}
