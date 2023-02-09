package com.guuda.sheep.activity.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.guuda.sheep.R;
import com.guuda.sheep.databinding.DialogFailBinding;


public class GameFailDialog extends Dialog {
    private DialogFailBinding binding;
    private GameFailActionListener mListener;


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

        binding.btnRestart.setOnClickListener(v -> {
            dismiss();
            mListener.onRestart();
        });

        binding.btnExit.setOnClickListener(v -> {
            dismiss();
            mListener.onGiveUp();
        });

    }

    public void setOnDialogEventListener(GameFailActionListener listener) {
        mListener = listener;
    }

    public interface GameFailActionListener {

        void onRestart();

        void onGiveUp();

    }

}
