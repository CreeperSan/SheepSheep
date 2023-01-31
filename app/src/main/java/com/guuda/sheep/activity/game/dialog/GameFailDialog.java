package com.guuda.sheep.activity.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.guuda.sheep.R;
import com.guuda.sheep.databinding.DialogFailBinding;


public class GameFailDialog extends Dialog {
    private DialogFailBinding binding;
    private View.OnClickListener mNextLevelListener;


    public GameFailDialog(Context context) {
        super(context, R.style.SunDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogFailBinding.inflate(LayoutInflater.from(getContext()));

        setContentView(binding.getRoot());

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        binding.btnOne.setOnClickListener(v -> {
            dismiss();
        });

        setOnDismissListener(dialog -> {
            if (mNextLevelListener == null) {
                return;
            }
            mNextLevelListener.onClick(binding.getRoot());
        });
    }

    public void setOnBackListener(View.OnClickListener listener) {
        mNextLevelListener = listener;
    }

}
