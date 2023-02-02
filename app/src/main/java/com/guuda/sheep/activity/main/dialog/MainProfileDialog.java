package com.guuda.sheep.activity.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.widget.DatePicker;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.guuda.sheep.R;
import com.guuda.sheep.activity.BaseDialog;
import com.guuda.sheep.database.entity.UserInfo;
import com.guuda.sheep.databinding.DialogProfileBinding;
import com.luck.lib.camerax.utils.FileUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainProfileDialog extends BaseDialog {
    private DialogProfileBinding binding;

    private final UserInfo userInfo;

    private String[] locationArray;

    public MainProfileDialog(@NonNull Context context, @NonNull UserInfo userInfo) {
        super(context);

        this.userInfo = userInfo;

        birthdayTimestamp = userInfo.birthday;
        locationString = userInfo.location;
        avatarPath = userInfo.avatar;

        locationArray = new String[]{
                "未设置", "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西",
                "山东", "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃",
                "青海", "台湾", "内蒙古", "广西", "西藏", "宁夏", "新疆", "北京", "天津", "上海", "重庆",
                "香港", "澳门", "海外",
        };

        binding = DialogProfileBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        initView();
    }

    private void initView() {
        binding.closeBtn.setOnClickListener(v -> {
            dismiss();
        });

        Glide.with(binding.avatarIV)
                .load(BitmapFactory.decodeFile(avatarPath))
                .placeholder(R.drawable.ic_face_white_24)
                .error(R.drawable.ic_face_white_24)
                .apply(new RequestOptions().circleCrop())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.avatarIV);

        binding.avatarIV.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            listener.selectAvatar();
        });

        binding.nameET.setText(userInfo.getDisplayName());

        binding.birthdayTV.setText(getBirthdayString());
        binding.birthdayTV.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext());
            dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                birthdayTimestamp = calendar.getTime().getTime();

                binding.birthdayTV.setText(getBirthdayString());
            });
            dialog.show();
        });

        binding.locationTV.setText(getLocationString());
        binding.locationTV.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setItems(locationArray, (dialog, which) -> {
                        if (which < 0 || which >= locationArray.length) {
                            return;
                        }

                        locationString = locationArray[which];

                        binding.locationTV.setText(getLocationString());
                    })
                    .show();
        });

        binding.saveBtn.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }

            dismiss();

            listener.onSave(avatarPath, binding.nameET.getText().toString().trim(), birthdayTimestamp, locationString);
        });
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;




        Glide.with(binding.avatarIV)
                .load(BitmapFactory.decodeFile(avatarPath))
                .placeholder(R.drawable.ic_face_white_24)
                .error(R.drawable.ic_face_white_24)
                .apply(new RequestOptions().circleCrop())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.avatarIV);
    }

    private long birthdayTimestamp = 0;
    private String locationString = "";
    private String avatarPath = "";

    private String getBirthdayString() {
        if (birthdayTimestamp < 1000) {
            return "未设置";
        }

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setTime(birthdayTimestamp);
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "年" + month + "月" + day + "日";
    }

    private String getLocationString() {
        if (locationString.trim().length() <= 0) {
            return "未设置";
        } else {
            return locationString;
        }
    }

    public interface OnEventListener {

        void selectAvatar();

        void onSave(String avatar, String name, long birthdayTimestamp, String location);

    }

    private OnEventListener listener;

    public void setListener(OnEventListener listener) {
        this.listener = listener;
    }
}
