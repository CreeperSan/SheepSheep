package com.guuda.sheep.activity.game.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.guuda.sheep.activity.BaseDialog;
import com.guuda.sheep.databinding.DialogSettingBinding;

public class GameSettingDialog extends BaseDialog {
    private DialogSettingBinding binding;

    private EventListener listener;

    public GameSettingDialog(@NonNull Context context) {
        super(context);

        binding = DialogSettingBinding.inflate(LayoutInflater.from(context));

        setContentView(binding.getRoot());

        initView();

        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        binding.btnSoundOn.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            listener.onSoundOn();
        });

        binding.btnSoundOff.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            listener.onSoundOff();
        });

        binding.resumeBtn.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            dismiss();
            listener.onResume();
        });

        binding.restartBtn.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            dismiss();
            listener.onRestart();
        });

        binding.abandonBtn.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            dismiss();
            listener.onGiveUp();
        });
    }


    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public interface EventListener {

        void onSoundOn();

        void onSoundOff();

        void onResume();

        void onRestart();

        void onGiveUp();

    }

}
