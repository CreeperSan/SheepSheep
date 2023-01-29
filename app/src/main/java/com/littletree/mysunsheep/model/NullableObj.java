package com.littletree.mysunsheep.model;

import androidx.annotation.Nullable;

public class NullableObj<T> {

    @Nullable
    public final T obj;

    public NullableObj(@Nullable T obj) {
        this.obj = obj;
    }

}
