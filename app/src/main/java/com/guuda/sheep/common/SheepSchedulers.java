package com.guuda.sheep.common;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SheepSchedulers {

    public static final Scheduler database = Schedulers.single();

    public static final Scheduler io = Schedulers.io();

    public static final Scheduler ui = AndroidSchedulers.mainThread();

}
