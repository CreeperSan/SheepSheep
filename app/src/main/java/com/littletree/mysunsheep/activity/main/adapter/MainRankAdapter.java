package com.littletree.mysunsheep.activity.main.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.littletree.mysunsheep.activity.main.viewholder.MainRankViewHolder;
import com.littletree.mysunsheep.database.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class MainRankAdapter extends RecyclerView.Adapter<MainRankViewHolder> {

    private List<UserInfo> rankList = new ArrayList<>();

    public void refreshList(List<UserInfo> userInfoList) {
        this.rankList.clear();
        this.rankList.addAll(userInfoList);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainRankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainRankViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MainRankViewHolder holder, int position) {
        if (position < 0 || position >= rankList.size()) {
            return;
        }

        UserInfo userInfo = rankList.get(position);

        holder.setUserInfo(position + 1, userInfo);
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

}
