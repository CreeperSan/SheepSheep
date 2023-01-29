package com.littletree.mysunsheep.activity.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.activity.BaseFragment;
import com.littletree.mysunsheep.activity.main.viewstate.MainLevelSelectViewState;
import com.littletree.mysunsheep.databinding.FragmentMainLevelSelectBinding;

public class MainLevelSelectFragment extends BaseMainFragment<MainLevelSelectViewState> {
    private FragmentMainLevelSelectBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainLevelSelectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_level_select;
    }

    @Override
    public void refreshViewState(MainLevelSelectViewState viewState) {

    }
}
