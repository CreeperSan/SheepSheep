package com.littletree.mysunsheep.activity.main.viewmodel;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.littletree.mysunsheep.GameActivity;
import com.littletree.mysunsheep.activity.main.viewstate.BaseMainViewState;
import com.littletree.mysunsheep.activity.main.viewstate.MainLevelSelectViewState;
import com.littletree.mysunsheep.activity.main.viewstate.MainLoginViewState;
import com.littletree.mysunsheep.activity.main.viewstate.MainMenuViewState;
import com.littletree.mysunsheep.activity.main.viewstate.MainRankViewState;
import com.littletree.mysunsheep.activity.main.viewstate.MainRegisterViewState;
import com.littletree.mysunsheep.database.entity.UserInfo;
import com.littletree.mysunsheep.repository.AccountRepository;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.Disposable;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<BaseMainViewState> viewState = new MutableLiveData<>();

    public LiveData<BaseMainViewState> getViewState() {
        return viewState;
    }

    private final MutableLiveData<UserInfo> currentUserInfo = new MutableLiveData<>();

    public LiveData<UserInfo> getCurrentUserInfo() {
        return currentUserInfo;
    }

    /***********************************************************************************************
     * 页面切换操作
     */

    public void toRegister() {
        viewState.postValue(new MainRegisterViewState());
    }

    public void toMenu() {
        viewState.postValue(new MainMenuViewState());
    }

    public void toLogin() {
        logout();
        viewState.postValue(new MainLoginViewState());
    }

    public void toGame(@NonNull Activity activity) {
        activity.startActivity(new Intent(activity, GameActivity.class));
    }

    public void toLevelSelect() {
        boolean unlockLevel2 = false;

        UserInfo tmpUserInfo = currentUserInfo.getValue();
        if (tmpUserInfo != null) {
            unlockLevel2 = tmpUserInfo.reachLevel > 1;
        }

        viewState.postValue(new MainLevelSelectViewState(unlockLevel2));
    }

    Disposable rankLoadDisposable;
    public void toRank() {
        if (rankLoadDisposable != null && !rankLoadDisposable.isDisposed()) {
            rankLoadDisposable.dispose();
            rankLoadDisposable = null;
        }

        viewState.postValue(new MainRankViewState(true, new ArrayList<>()));
        rankLoadDisposable = AccountRepository.getHighScoreRankList().subscribe(rankList -> {
            rankLoadDisposable = null;
            if (!(viewState.getValue() instanceof MainRankViewState)) {
                return;
            }
            viewState.postValue(new MainRankViewState(false, rankList));
        }, throwable -> {
            rankLoadDisposable = null;
            throwable.printStackTrace();
        });
    }

    /***********************************************************************************************
     * 页面操作
     */

    private Disposable loginDisposable;

    public void login(String username, String password) {
        // 防止重复登录
        if (loginDisposable != null && !loginDisposable.isDisposed()) {
            loginDisposable.dispose();
            loginDisposable = null;
        }
        // 登录
        loginDisposable = AccountRepository.login(username, password).subscribe(userInfo -> {
            loginDisposable = null;
            viewState.postValue(new MainMenuViewState());
            currentUserInfo.postValue(userInfo);
        }, throwable -> {
            loginDisposable = null;
            throwable.printStackTrace();
        });
    }

    private Disposable registerDisposable;
    public void register(String username, String password, String passwordConfirm) {
        // 防止重复注册
        if (registerDisposable != null && !registerDisposable.isDisposed()) {
            registerDisposable.dispose();
            registerDisposable = null;
        }
        // 注册
        registerDisposable = AccountRepository.register(username, password, passwordConfirm).subscribe(user -> {
            viewState.postValue(new MainLoginViewState());
        }, throwable -> {
            throwable.printStackTrace();
        });
    }

    public void logout() {
        if (currentUserInfo.getValue() != null) {
            currentUserInfo.postValue(null);
        }
    }

}
