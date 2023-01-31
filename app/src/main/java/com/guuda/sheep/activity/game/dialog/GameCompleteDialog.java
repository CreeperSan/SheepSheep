package com.guuda.sheep.activity.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.guuda.sheep.R;
import com.guuda.sheep.databinding.DialogSucceedBinding;

public class GameCompleteDialog extends Dialog {
    private DialogSucceedBinding binding;

    private DialogInterface.OnDismissListener mDismissListener;

    public GameCompleteDialog(Context context) {
        super(context, R.style.SucceedDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogSucceedBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(v -> {
            dismiss();
        });

        setOnDismissListener(dialog -> {
            mDismissListener.onDismiss(this);
        });
    }

    public void setCloseListener(DialogInterface.OnDismissListener listener) {
        mDismissListener = listener;
    }

}
