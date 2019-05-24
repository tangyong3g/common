package com.sny.tangyong.common.view.engine.animation;


public class ValueAnimation extends Animation {

    private float mFromValue;
    private float mToValue;
    private float mCurrentValue;

    /**
     * Constructor to use when building an AlphaAnimation from code
     *
     * @param fromValue Starting alpha value for the animation, where 1.0 means
     *                  fully opaque and 0.0 means fully transparent.
     * @param toValue   Ending alpha value for the animation.
     */
    public ValueAnimation(float fromValue, float toValue) {
        mFromValue = fromValue;
        mToValue = toValue;
    }

    /**
     * Changes the alpha property of the supplied {@link Transformation}
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float alpha = mFromValue;
        mCurrentValue = alpha + ((mToValue - alpha) * interpolatedTime);
        t.setAlpha(mCurrentValue);
    }

    public float getCurrentValue() {
        return mCurrentValue;
    }

    @Override
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override
    public boolean willChangeBounds() {
        return false;
    }


}
