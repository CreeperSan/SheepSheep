package com.guuda.sheep.utils;

import java.util.Random;

public class RandomIntegerGenerator {
    private final int min;
    private final int max;
    private final int eachRepeat;


    private int repeatCount;
    private int cacheValue;

    private final Random random = new Random();

    public RandomIntegerGenerator(int min, int max) {
        this(min, max, 1);
    }

    /***
     *
     * @param min 最小值
     * @param max 最大值（包含）
     * @param eachRepeat 重复次数
     */
    public RandomIntegerGenerator(int min, int max, int eachRepeat) {
        this.min = min;
        this.max = max;
        this.eachRepeat = eachRepeat;
        this.repeatCount = 0;
    }

    public int next() {
        if (--repeatCount <= 0){
            repeatCount = eachRepeat;
            cacheValue = random.nextInt(max - min + 1) + min;
        }

        return cacheValue;

    }

}
