package com.guuda.sheep.activity.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guuda.sheep.R;
import com.guuda.sheep.activity.main.adapter.MainRankAdapter;
import com.guuda.sheep.activity.main.viewmodel.MainViewModel;
import com.guuda.sheep.activity.main.viewstate.MainRankViewState;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.databinding.FragmentMainRankBinding;
import com.guuda.sheep.pref.PrefManager;
import com.guuda.sheep.pref.PrefUpdateListener;

public class MainRankFragment extends BaseMainFragment<MainRankViewState> {

    private FragmentMainRankBinding binding;

    private PrefUpdateListener<Boolean> mutePrefListener;

    private MainRankAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainRankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.exitBtn.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                viewModel.toMenu();
            }
        });

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

        adapter = new MainRankAdapter();
        binding.listView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        PrefManager.instance.removeMuteChangeListener(mutePrefListener);
    }

    @Override
    public void refreshViewState(MainRankViewState viewState) {
        if (binding == null) {
            return;
        }

        if (viewState.isLoading) {
            binding.listView.setVisibility(View.GONE);
            binding.loadingHintText.setVisibility(View.VISIBLE);
        } else {
            adapter.refreshList(viewState.rankList);
            binding.listView.setVisibility(View.VISIBLE);
            binding.loadingHintText.setVisibility(View.GONE);
        }
    }
}
