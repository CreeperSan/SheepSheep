package com.littletree.mysunsheep.repository;


import com.littletree.mysunsheep.common.SheepSchedulers;
import com.littletree.mysunsheep.database.DatabaseManager;
import com.littletree.mysunsheep.database.entity.UserInfo;
import com.littletree.mysunsheep.exception.AccountException;
import com.littletree.mysunsheep.model.NullableObj;
import com.littletree.mysunsheep.utils.MD5Utils;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class AccountRepository {

    private AccountRepository() { }

    /**
     * 获取上一次登录的账号业务流程
     * @return
     */
    public static Observable<NullableObj<UserInfo>> getUserInfo() {
        return Observable.just(0).observeOn(SheepSchedulers.database).map(integer -> {
            return new NullableObj<UserInfo>(null);
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 登录账号业务流程
     * @param username
     * @param password
     * @return
     */
    public static Observable<UserInfo> login(String username, String password) {
        return Observable.just(0).observeOn(SheepSchedulers.database).concatMap(val -> {
            // 检查登录信息
            return DatabaseManager.instance.findAccount(username, password);
        }).map(accountNullable -> {
            UserInfo userInfo = accountNullable.obj;
            if (userInfo == null) {
                throw new AccountException("密码错误或者用户不存在");
            }
            return userInfo;
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 注册账号 业务流程
     * @param username
     * @param password
     * @param passwordConfirm
     * @return
     */
    public static Observable<UserInfo> register(String username, String password, String passwordConfirm) {
        return Observable.just(0).observeOn(SheepSchedulers.database).map(val -> {
            // 检查密码是否一致
            if (!password.equals(passwordConfirm)) {
                throw new AccountException("两次输入的密码不一致");
            }
            return val;
        }).concatMap(val -> {
            // 查找账号信息
            return DatabaseManager.instance.findAccount(username);
        }).concatMap(accountNullable -> {
            // 检查是否已经注册
            if (accountNullable.obj != null) {
                // 如果已经注册，则抛出异常
                throw new AccountException("账号已经注册，请直接登录");
            }
            // 如果没有注册，则进行注册
            return DatabaseManager.instance.addAccount(username, password);
        }).concatMap(accountNullable -> {
            // 检查数据是否已经写入到数据库
            return DatabaseManager.instance.findAccount(username, password);
        }).map(findUserInfo -> {
            UserInfo userInfo = findUserInfo.obj;
            if (userInfo == null) {
                throw new AccountException("账号注册失败");
            }
            return userInfo;
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 更新账号的最高分
     * @param username
     * @param highScore
     * @return
     */
    public static Observable<NullableObj<UserInfo>> updateHighScore(String username, int highScore) {
        return Observable.just(highScore).map(score -> {
            if (score < 0) {
                throw new AccountException("账号最高分不能小于0");
            }
            return score;
        }).concatMap(score -> {
            return DatabaseManager.instance.findAccount(username);
        }).map(accountNullable -> {
            UserInfo userInfo = accountNullable.obj;
            if (userInfo == null) {
                throw new AccountException("账号不存在");
            }
            return userInfo;
        }).concatMap(userInfo -> {
            userInfo.highScore = highScore;
            return DatabaseManager.instance.updateAccount(userInfo);
        }).concatMap(val -> {
            return DatabaseManager.instance.findAccount(username);
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 更新账号的位置
     * @param username
     * @param location
     * @return
     */
    public static Observable<NullableObj<UserInfo>> updateLocation(String username, String location) {
        return Observable.just(location).concatMap(score -> {
            return DatabaseManager.instance.findAccount(username);
        }).map(accountNullable -> {
            UserInfo userInfo = accountNullable.obj;
            if (userInfo == null) {
                throw new AccountException("账号不存在");
            }
            return userInfo;
        }).concatMap(userInfo -> {
            userInfo.location = location;
            return DatabaseManager.instance.updateAccount(userInfo);
        }).concatMap(val -> {
            return DatabaseManager.instance.findAccount(username);
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 更新账号的昵称
     * @param username
     * @param nickname
     * @return
     */
    public static Observable<NullableObj<UserInfo>> updateNickname(String username, String nickname) {
        return Observable.just(nickname).concatMap(score -> {
            return DatabaseManager.instance.findAccount(username);
        }).map(accountNullable -> {
            UserInfo userInfo = accountNullable.obj;
            if (userInfo == null) {
                throw new AccountException("账号不存在");
            }
            return userInfo;
        }).concatMap(userInfo -> {
            userInfo.nickname = nickname;
            return DatabaseManager.instance.updateAccount(userInfo);
        }).concatMap(val -> {
            return DatabaseManager.instance.findAccount(username);
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 更新账号的生日
     * @param username
     * @param birthdayTimestamp
     * @return
     */
    public static Observable<NullableObj<UserInfo>> updateBirthday(String username, long birthdayTimestamp) {
        return Observable.just(birthdayTimestamp).concatMap(score -> {
            return DatabaseManager.instance.findAccount(username);
        }).map(accountNullable -> {
            UserInfo userInfo = accountNullable.obj;
            if (userInfo == null) {
                throw new AccountException("账号不存在");
            }
            return userInfo;
        }).concatMap(userInfo -> {
            userInfo.birthday = birthdayTimestamp;
            return DatabaseManager.instance.updateAccount(userInfo);
        }).concatMap(val -> {
            return DatabaseManager.instance.findAccount(username);
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 更新账号的生日
     * @param username
     * @param path
     * @return
     */
    public static Observable<NullableObj<UserInfo>> updateAvatar(String username, String path) {
        return Observable.just(path).concatMap(score -> {
            return DatabaseManager.instance.findAccount(username);
        }).map(accountNullable -> {
            UserInfo userInfo = accountNullable.obj;
            if (userInfo == null) {
                throw new AccountException("账号不存在");
            }
            return userInfo;
        }).concatMap(userInfo -> {
            userInfo.avatar = path;
            return DatabaseManager.instance.updateAccount(userInfo);
        }).concatMap(val -> {
            return DatabaseManager.instance.findAccount(username);
        }).observeOn(SheepSchedulers.ui);
    }

    /**
     * 更新账号达到的最高关卡数
     * @param username
     * @param reachLevel
     * @return
     */
    public static Observable<NullableObj<UserInfo>> updateReachLevel(String username, int reachLevel) {
        return Observable.just(reachLevel).concatMap(score -> {
            return DatabaseManager.instance.findAccount(username);
        }).map(accountNullable -> {
            UserInfo userInfo = accountNullable.obj;
            if (userInfo == null) {
                throw new AccountException("账号不存在");
            }
            return userInfo;
        }).concatMap(userInfo -> {
            userInfo.reachLevel = Math.max(reachLevel, userInfo.reachLevel);
            return DatabaseManager.instance.updateAccount(userInfo);
        }).concatMap(val -> {
            return DatabaseManager.instance.findAccount(username);
        }).observeOn(SheepSchedulers.ui);
    }

    public static Observable<List<UserInfo>> getHighScoreRankList() {
        return DatabaseManager.instance.getHighScoreRank().observeOn(SheepSchedulers.ui);
    }

}
