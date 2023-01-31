package com.guuda.sheep.activity.main.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guuda.sheep.R;
import com.guuda.sheep.activity.main.viewmodel.MainViewModel;
import com.guuda.sheep.activity.main.viewstate.MainLevelSelectViewState;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.databinding.FragmentMainLevelSelectBinding;
import com.guuda.sheep.pref.PrefManager;
import com.guuda.sheep.pref.PrefUpdateListener;

public class MainLevelSelectFragment extends BaseMainFragment<MainLevelSelectViewState> {
    private FragmentMainLevelSelectBinding binding;

    private PrefUpdateListener<Boolean> mutePrefListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainLevelSelectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.level1TV.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                viewModel.toGame(activity);
            }
        });

        binding.level2TV.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                viewModel.toGame(activity);
            }
        });

        binding.backBtn.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                viewModel.toMenu();
            }
        });

        binding.lockLevel2.setVisibility(View.VISIBLE);

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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        PrefManager.instance.removeMuteChangeListener(mutePrefListener);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_level_select;
    }

    @Override
    public void refreshViewState(MainLevelSelectViewState viewState) {
        if (binding == null) {
            return;
        }

        binding.lockLevel2.setVisibility(viewState.unlockLevel2 ? View.GONE : View.VISIBLE);

        binding.level2TV.setClickable(viewState.unlockLevel2);
        binding.level2TV.setFocusable(viewState.unlockLevel2);
    }

}
