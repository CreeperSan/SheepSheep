package com.littletree.mysunsheep.activity.main.viewstate;

import androidx.annotation.NonNull;

import com.littletree.mysunsheep.database.entity.UserInfo;

import java.util.List;

public class MainRankViewState extends BaseMainViewState {
    public final boolean isLoading;
    public final List<UserInfo> rankList;

    public MainRankViewState(boolean isLoading, @NonNull List<UserInfo> rankList) {
        this.isLoading = isLoading;
        this.rankList = rankList;
    }

}