package io.luciferldy.bnimation.view;

/**
 * Created by lian_ on 2017/10/17.
 * 每一粒小元素 {@link Element}
 * 包含方向，范围是 0~360 以水平向右的 x 轴为准，沿顺时针计算
 * 包含速度，在每段周期内沿某个方向的前进的距离，通过方向的 sin cos 函数可以求得移动的横纵坐标
 * x 在 x 轴的偏移，可 < 0 ，是累加的结果
 * y 在 y 轴的偏移
 */

public class Element {
    // 360
    public double direction;
    public float speed;
    // 可看做是偏移
    public float x = 0;
    public float y = 0;

    public Element(double direction, float speed) {
        this.direction = direction;
        this.speed = speed;
    }
}
