package com.littletree.mysunsheep.activity.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.littletree.mysunsheep.GameActivity;
import com.littletree.mysunsheep.MainActivity;
import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.activity.BaseFragment;
import com.littletree.mysunsheep.activity.main.dialog.MainProfileDialog;
import com.littletree.mysunsheep.activity.main.dialog.MainSettingDialog;
import com.littletree.mysunsheep.activity.main.viewmodel.MainViewModel;
import com.littletree.mysunsheep.activity.main.viewstate.MainMenuViewState;
import com.littletree.mysunsheep.customview.NoFastClickListener;
import com.littletree.mysunsheep.databinding.FragmentMainMenuBinding;

public class MainMenuFragment extends BaseMainFragment<MainMenuViewState> {
    private FragmentMainMenuBinding binding;

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
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                viewModel.toGame(activity);
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

        });

        binding.head.settingBtn.setOnClickListener(v -> {
            Context context = binding.getRoot().getContext();

            MainSettingDialog settingDialog = new MainSettingDialog(context);
            settingDialog.show();
        });

    }

    @Override
    public void refreshViewState(MainMenuViewState viewState) {

    }
}
