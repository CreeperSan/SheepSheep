package com.littletree.mysunsheep.activity.main.fragment;

import android.app.Activity;
import android.view.View;

import androidx.annotation.Nullable;

import com.littletree.mysunsheep.MainActivity;
import com.littletree.mysunsheep.activity.BaseFragment;
import com.littletree.mysunsheep.activity.main.viewmodel.MainViewModel;
import com.littletree.mysunsheep.activity.main.viewstate.BaseMainViewState;

public abstract class BaseMainFragment<T extends BaseMainViewState> extends BaseFragment {

    public abstract void refreshViewState(T viewState);

    @Nullable
    public MainViewModel getViewModel() {
        final Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            return ((MainActivity) activity).getViewModel();
        }
        return null;
    }

    protected abstract class ViewModelClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            MainViewModel mainViewModel = getViewModel();
            if (mainViewModel == null) {
                return;
            }
            onClick(mainViewModel);
        }

        public abstract void onClick(MainViewModel viewModel);
    }

}
