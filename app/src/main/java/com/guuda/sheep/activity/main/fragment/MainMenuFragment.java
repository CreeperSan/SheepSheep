package com.guuda.sheep.activity.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.activity.main.dialog.MainProfileDialog;
import com.guuda.sheep.activity.game.dialog.GameSettingDialog;
import com.guuda.sheep.activity.main.viewmodel.MainViewModel;
import com.guuda.sheep.activity.main.viewstate.MainMenuViewState;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.database.entity.UserInfo;
import com.guuda.sheep.databinding.FragmentMainMenuBinding;
import com.guuda.sheep.pref.PrefManager;
import com.guuda.sheep.pref.PrefUpdateListener;
import com.guuda.sheep.repository.AccountRepository;
import com.luck.lib.camerax.utils.FileUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainMenuFragment extends BaseMainFragment<MainMenuViewState> {
    private FragmentMainMenuBinding binding;

    private PrefUpdateListener<Boolean> mutePrefListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.startBtn.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                viewModel.toLevelSelect();
            }
        });

        binding.rankBtn.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                viewModel.toRank();
            }
        });

        binding.exitBtn.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                viewModel.toLogin();
            }
        });

        Glide.with(binding.sheepIv).asGif().load(R.mipmap.ic_award_sheep1).into(binding.sheepIv);

        binding.head.userBtn.setVisibility(View.VISIBLE);
        binding.head.userBtn.setOnClickListener(v -> {
            MainViewModel viewModel = getViewModel();
            if (viewModel == null) {
                return;
            }

            Context context = getContext();
            if (context == null) {
                return;
            }

            UserInfo userInfo = viewModel.getCurrentUserInfo().getValue();
            if (userInfo == null) {
                return;
            }

            MainProfileDialog profileDialog = new MainProfileDialog(context, userInfo);
            profileDialog.setListener(new MainProfileDialog.OnEventListener() {
                @Override
                public void selectAvatar() {
                    PictureSelector.create(getContext())
                            .openSystemGallery(SelectMimeType.ofImage())
                            .forSystemResult(new OnResultCallbackListener<LocalMedia>() {
                                @Override
                                public void onCancel() {}

                                @Override
                                public void onResult(ArrayList<LocalMedia> result) {
                                    if (result.size() <= 0) {
                                        return;
                                    }

                                    Context context = getContext();
                                    if (context == null) {
                                        return;
                                    }

                                    LocalMedia localMedia = result.get(0);


                                    profileDialog.setAvatarPath(localMedia.getRealPath());
                                }

                            });
                }

                @Override
                public void onSave(String avatar, String name, long birthdayTimestamp, String location) {
                    Disposable disposable = Observable.just(0).concatMap(v -> {

                        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + userInfo.id + ".jpg");
                        FileUtils.copyPath(context, avatar, file.getAbsolutePath());

                        return AccountRepository.updateAvatar(userInfo.username, file.getAbsolutePath());
                    }).concatMap(v -> {
                        return AccountRepository.updateNickname(userInfo.username, name);
                    }).concatMap(v -> {
                        return AccountRepository.updateBirthday(userInfo.username, birthdayTimestamp);
                    }).concatMap(v -> {
                        return AccountRepository.updateLocation(userInfo.username, location);
                    }).map(val -> {
                        viewModel.refreshUserInfo();
                        return true;
                    }).subscribe(val -> {
                        Log.e("TAG", String.valueOf(val));
                    }, Throwable::printStackTrace);

                }
            });
            profileDialog.show();
        });

        binding.head.soundBtn.setOnClickListener(v -> {
            AudioController.instance.toggleMute();
        });

        binding.head.settingBtn.setOnClickListener(v -> {
            Context context = binding.getRoot().getContext();

            GameSettingDialog settingDialog = new GameSettingDialog(context);
            settingDialog.show();
        });

        binding.head.settingBtn.setVisibility(View.GONE);

        // 音效按钮
        mutePrefListener = value -> refreshMuteButton(binding.head.soundBtn);
        refreshMuteButton(binding.head.soundBtn);
        binding.head.soundBtn.setOnClickListener(v -> {
            boolean newMuteState = AudioController.instance.toggleMute();
            PrefManager.instance.setMute(newMuteState);
            refreshMuteButton(binding.head.soundBtn);
        });
        PrefManager.instance.addMuteChangeListener(mutePrefListener);

        // 设置按钮
        binding.head.settingBtn.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        PrefManager.instance.removeMuteChangeListener(mutePrefListener);
    }

    @Override
    public void refreshViewState(MainMenuViewState viewState) {

    }
}
