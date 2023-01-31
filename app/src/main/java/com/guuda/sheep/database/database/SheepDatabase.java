package com.guuda.sheep.database.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.guuda.sheep.database.dao.UserInfoDao;
import com.guuda.sheep.database.entity.UserInfo;

@Database(entities = {UserInfo.class}, version = 1, exportSchema = false)
public abstract class SheepDatabase extends RoomDatabase {

    public abstract UserInfoDao userInfoDao();

}
