package com.guuda.sheep.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    @LayoutRes
    protected int getLayoutID() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutID = getLayoutID();
        if (layoutID != 0) {
            return inflater.inflate(layoutID, container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
