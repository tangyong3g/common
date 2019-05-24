package com.sny.tangyong.common.view.engine.animation;


/**
 *  前后两端慢中间比较快,有点模拟真实加速到加速的过程
 */
public class SmootherInterpolation implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        return input * input * input * (input * (input * 6 - 15) + 10);
    }
}
