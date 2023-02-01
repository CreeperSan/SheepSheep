package com.guuda.sheep.activity.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guuda.sheep.activity.main.dialog.MainProfileDialog;
import com.guuda.sheep.activity.game.dialog.GameSettingDialog;
import com.guuda.sheep.activity.main.viewmodel.MainViewModel;
import com.guuda.sheep.activity.main.viewstate.MainMenuViewState;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.databinding.FragmentMainMenuBinding;
import com.guuda.sheep.pref.PrefManager;
import com.guuda.sheep.pref.PrefUpdateListener;

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

        binding.head.userBtn.setVisibility(View.VISIBLE);
        binding.head.userBtn.setOnClickListener(v -> {
            MainViewModel viewModel = getViewModel();
            if (viewModel == null) {
                return;
            }

            Context context = binding.getRoot().getContext();

            MainProfileDialog profileDialog = new MainProfileDialog(context, viewModel.getCurrentUserInfo().getValue());
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
