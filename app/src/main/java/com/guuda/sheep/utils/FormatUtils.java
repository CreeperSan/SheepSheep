package com.guuda.sheep.utils;

public class FormatUtils {

    private FormatUtils() { }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() <= 0;
    }

}