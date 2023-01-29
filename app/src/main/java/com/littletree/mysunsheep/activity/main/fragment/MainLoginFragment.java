package com.littletree.mysunsheep.activity.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.activity.BaseFragment;
import com.littletree.mysunsheep.activity.main.viewmodel.MainViewModel;
import com.littletree.mysunsheep.activity.main.viewstate.MainLoginViewState;
import com.littletree.mysunsheep.databinding.FragmentMainLoginBinding;

public class MainLoginFragment extends BaseMainFragment<MainLoginViewState> {
    private FragmentMainLoginBinding binding;

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

                viewModel.login(usernameStr, passwordStr);
            }
        });

    }

    @Override
    public void refreshViewState(MainLoginViewState viewState) {

    }
}
