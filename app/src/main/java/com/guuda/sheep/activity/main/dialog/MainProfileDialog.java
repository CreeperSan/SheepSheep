package com.guuda.sheep.activity.main.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.activity.BaseDialog;
import com.guuda.sheep.database.entity.UserInfo;
import com.guuda.sheep.databinding.DialogProfileBinding;

public class MainProfileDialog extends BaseDialog {
    private DialogProfileBinding binding;

    private UserInfo userInfo;


    public MainProfileDialog(@NonNull Context context, UserInfo userInfo) {
        super(context);

        this.userInfo = userInfo;

        binding = DialogProfileBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        initView();
    }

    private void initView() {
        binding.closeBtn.setOnClickListener(v -> {
            dismiss();
        });

        Glide.with(binding.avatarIV)
                .load(userInfo.avatar)
                .placeholder(R.drawable.ic_face_white_24)
                .error(R.drawable.ic_face_white_24)
                .into(binding.avatarIV);

        binding.nameET.setText(userInfo.getDisplayName());

        binding.birthdayTV.setText(String.valueOf(userInfo.birthday));

        binding.locationTV.setText(userInfo.location);

        binding.saveBtn.setOnClickListener(v -> {

        });
    }



}
