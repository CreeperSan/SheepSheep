package com.littletree.mysunsheep.database.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.littletree.mysunsheep.database.dao.UserInfoDao;
import com.littletree.mysunsheep.database.entity.UserInfo;

@Database(entities = {UserInfo.class}, version = 1, exportSchema = false)
public abstract class SheepDatabase extends RoomDatabase {

    public abstract UserInfoDao userInfoDao();

}
