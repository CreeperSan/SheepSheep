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
import com.littletree.mysunsheep.activity.main.viewstate.MainRegisterViewState;
import com.littletree.mysunsheep.databinding.FragmentMainRegisterBinding;

public class MainRegisterFragment extends BaseMainFragment<MainRegisterViewState> {
    private FragmentMainRegisterBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.registerBtn.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {

                String usernameStr = binding.usernameET.getText().toString().trim();
                String passwordStr = binding.passwordET.getText().toString().trim();
                String passwordConfirmStr = binding.confirmPasswordET.getText().toString().trim();

                viewModel.register(usernameStr, passwordStr, passwordConfirmStr);

            }
        });

        binding.backBtn.setOnClickListener(new ViewModelClickListener() {
            @Override
            public void onClick(MainViewModel viewModel) {
                viewModel.toLogin();
            }
        });
    }

    @Override
    public void refreshViewState(MainRegisterViewState viewState) {

    }

}
