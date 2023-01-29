package com.littletree.mysunsheep.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.littletree.mysunsheep.database.entity.UserInfo;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

@Dao
public interface UserInfoDao {

    @Query("SELECT * FROM user WHERE username = :username")
    UserInfo findUser(String username);

    @Query("SELECT * FROM user WHERE username = :username and password = :password")
    UserInfo findUser(String username, String password);

    @Insert
    void insertUser(UserInfo userInfo);

    @Update
    void update(UserInfo userInfo);

    @Query("SELECT * FROM user order by high_score desc limit :limit")
    List<UserInfo> getHighScoreRank(int limit);

}
