package com.guuda.sheep.database;

import androidx.room.Room;

import com.guuda.sheep.application.SheepApplication;
import com.guuda.sheep.common.SheepSchedulers;
import com.guuda.sheep.database.dao.UserInfoDao;
import com.guuda.sheep.database.database.SheepDatabase;
import com.guuda.sheep.database.entity.UserInfo;
import com.guuda.sheep.model.NullableObj;
import com.guuda.sheep.utils.MD5Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

public class DatabaseManager {

    public static DatabaseManager instance = new DatabaseManager();

    private SheepDatabase mDatabase;


    private Observable<SheepDatabase> getDatabase() {
        SheepDatabase tmpDB = mDatabase;
        if (tmpDB != null) {
            return Observable.just(tmpDB).observeOn(SheepSchedulers.database);
        }

        return Observable.just(0).observeOn(SheepSchedulers.database).map((val) -> {
           SheepDatabase db = Room.databaseBuilder(SheepApplication.instance, SheepDatabase.class, "Sheep.db").build();
           mDatabase = db;
           return db;
        });
    }

    private Observable<UserInfoDao> getUserInfoDao() {
        return getDatabase().map(SheepDatabase::userInfoDao);
    }


    public Observable<Boolean> init() {
        return getUserInfoDao().delay(1000, TimeUnit.MILLISECONDS).map(user -> {
            return true;
        }).observeOn(SheepSchedulers.ui);
    }


    public Observable<NullableObj<UserInfo>> addAccount(String username, String password) {
        return getUserInfoDao().map(dao -> {
            UserInfo userInfo = new UserInfo();
            userInfo.username = username;
            userInfo.nickname = username;
            userInfo.password = MD5Utils.md5(password);
            dao.insertUser(userInfo);
            return dao;
        }).map(dao -> {
            UserInfo userInfo = dao.findUser(username);
            return new NullableObj<>(userInfo);
        });
    }

    public Observable<NullableObj<UserInfo>> findAccount(String username) {
        return getUserInfoDao().map(dao -> {
            UserInfo userInfo = dao.findUser(username);
            return new NullableObj<>(userInfo);
        });
    }

    public Observable<NullableObj<UserInfo>> findAccount(String username, String password) {
        return getUserInfoDao().map(dao -> {
            UserInfo userInfo = dao.findUser(username, MD5Utils.md5(password));
            return new NullableObj<>(userInfo);
        });
    }

    public Observable<NullableObj<UserInfo>> updateAccount(UserInfo userInfo) {
        return getUserInfoDao().map(dao -> {
            dao.update(userInfo);
            return new NullableObj<>(userInfo);
        });
    }

    public Observable<List<UserInfo>> getHighScoreRank() {
        return getUserInfoDao().map(dao -> {
            return dao.getHighScoreRank(20);
        });
    }

}
