package com.guuda.sheep.activity.main.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.guuda.sheep.activity.BaseDialog;
import com.guuda.sheep.databinding.DialogSettingBinding;

public class MainSettingDialog extends BaseDialog {
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
