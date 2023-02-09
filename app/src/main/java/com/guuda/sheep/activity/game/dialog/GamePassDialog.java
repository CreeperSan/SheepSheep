package com.guuda.sheep.activity.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guuda.sheep.R;
import com.guuda.sheep.databinding.DialogPassBinding;


public class GamePassDialog extends Dialog {
    private DialogPassBinding binding;
    private View.OnClickListener mNextLevelListener;


    public GamePassDialog(Context context) {
        super(context, R.style.SunDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DialogPassBinding.inflate(LayoutInflater.from(getContext()));

        setContentView(binding.getRoot());

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        binding.btnNextLevel.setOnClickListener(v -> {
            dismiss();
        });

        setOnDismissListener(dialog -> {
            if (mNextLevelListener == null) {
                return;
            }
            mNextLevelListener.onClick(binding.getRoot());
        });
    }

    public void setOnNextLevelListener(View.OnClickListener listener) {
        mNextLevelListener = listener;
    }

}
