package com.guuda.sheep.activity.game.model;

public class Chess {
    public double x;
    public double y;
    public int layer;
    public int type;

    public Chess() { }

    public Chess(double x, double y, int layer, int type) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.type = type;
    }

    public Double[] getLocation() {
        return new Double[]{ x, y };
    }

}
