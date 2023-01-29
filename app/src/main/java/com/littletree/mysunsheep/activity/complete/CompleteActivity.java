package com.littletree.mysunsheep.activity.complete;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.activity.BaseActivity;
import com.littletree.mysunsheep.databinding.ActivityCompleteBinding;

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

        binding.exitBtn.setOnClickListener(v -> {
            finish();
        });


    }
}
