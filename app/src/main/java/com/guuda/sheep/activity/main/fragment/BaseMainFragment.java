package com.guuda.sheep.activity.main.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.guuda.sheep.activity.main.MainActivity;
import com.guuda.sheep.R;
import com.guuda.sheep.activity.BaseFragment;
import com.guuda.sheep.activity.main.viewmodel.MainViewModel;
import com.guuda.sheep.activity.main.viewstate.BaseMainViewState;
import com.guuda.sheep.audio.AudioController;

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

    /**
     * 业务相关代码
     */
    protected void refreshMuteButton(ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(AudioController.instance.isMute() ? R.drawable.ic_music_off_white_24 : R.drawable.ic_music_white_24)
                .into(imageView);
    }

}
