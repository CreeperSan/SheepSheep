package com.littletree.mysunsheep.activity.main.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.littletree.mysunsheep.databinding.DialogSettingBinding;

public class MainSettingDialog extends Dialog {
    private DialogSettingBinding binding;

    public MainSettingDialog(@NonNull Context context) {
        super(context);

        binding = DialogSettingBinding.inflate(LayoutInflater.from(context));

        setContentView(binding.getRoot());

        initView();
    }

    private void initView() {
        binding.btnSoundOn.setOnClickListener(v -> {

        });

        binding.btnSoundOff.setOnClickListener(v -> {

        });

        binding.resumeBtn.setOnClickListener(v -> {

        });

        binding.restartBtn.setOnClickListener(v -> {

        });

        binding.abandonBtn.setOnClickListener(v -> {

        });
    }




}
