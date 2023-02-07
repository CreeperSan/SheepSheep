package com.guuda.sheep.activity.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.guuda.sheep.R;
import com.guuda.sheep.activity.main.viewmodel.MainViewModel;
import com.guuda.sheep.activity.main.viewstate.MainLoginViewState;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.databinding.FragmentMainLoginBinding;
import com.guuda.sheep.pref.PrefManager;
import com.guuda.sheep.pref.PrefUpdateListener;

public class MainLoginFragment extends BaseMainFragment<MainLoginViewState> {
    private FragmentMainLoginBinding binding;

    private PrefUpdateListener<Boolean> mutePrefListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 注册
        binding.btnRegister.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                viewModel.toRegister();
            }
        });

        // 登录
        binding.btnLogin.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                String usernameStr = binding.usernameET.getText().toString().trim();
                String passwordStr = binding.passwordET.getText().toString().trim();

                viewModel.login(usernameStr, passwordStr, getContext());
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

        // 设置按钮
        binding.head.settingBtn.setVisibility(View.GONE);

        //
        Glide.with(binding.sheepIv).asGif().load(R.mipmap.ic_award_sheep1).into(binding.sheepIv);

        PrefManager.instance.addMuteChangeListener(mutePrefListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PrefManager.instance.removeMuteChangeListener(mutePrefListener);
    }

    @Override
    public void refreshViewState(MainLoginViewState viewState) {

    }

}
