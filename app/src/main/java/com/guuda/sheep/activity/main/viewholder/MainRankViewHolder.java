package com.guuda.sheep.activity.main.viewholder;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.guuda.sheep.R;
import com.guuda.sheep.database.entity.UserInfo;
import com.guuda.sheep.databinding.ItemRankingBinding;

public class MainRankViewHolder extends RecyclerView.ViewHolder {
    ItemRankingBinding binding;

    public MainRankViewHolder(ViewGroup container) {
        this(LayoutInflater.from(container.getContext()).inflate(R.layout.item_ranking, container, false));
    }

    private MainRankViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemRankingBinding.bind(itemView);
    }

    public void setUserInfo(int rank, UserInfo userInfo) {
        binding.rankTV.setText(String.valueOf(rank));

        Glide.with(binding.avatarIV)
                .load(BitmapFactory.decodeFile(userInfo.avatar))
                .placeholder(R.drawable.ic_face_white_24)
                .error(R.drawable.ic_face_white_24)
                .apply(new RequestOptions().circleCrop())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.avatarIV);

        binding.usernameTV.setText(userInfo.getDisplayName());

        binding.scoreTV.setText(String.valueOf(userInfo.highScore));
    }

}
