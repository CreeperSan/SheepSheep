package com.littletree.mysunsheep.activity.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.activity.BaseFragment;
import com.littletree.mysunsheep.activity.main.adapter.MainRankAdapter;
import com.littletree.mysunsheep.activity.main.viewmodel.MainViewModel;
import com.littletree.mysunsheep.activity.main.viewstate.MainRankViewState;
import com.littletree.mysunsheep.databinding.FragmentMainRankBinding;

public class MainRankFragment extends BaseMainFragment<MainRankViewState> {

    private FragmentMainRankBinding binding;

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

        adapter = new MainRankAdapter();
        binding.listView.setAdapter(adapter);
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
