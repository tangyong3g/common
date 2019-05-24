package com.sny.tangyong.common.view.engine;


import com.sny.tangyong.common.view.engine.animation.Transformation;

/**
 * 描述在场景中的抽象物体
 */
public abstract class Entity implements ISurfaceViewListener {


    //在绘制的位置 X
    protected float mDrawX;
    //在绘制的位置 Y
    protected float mDrawY;
    protected int mWidth;
    protected int mHeight;


    public boolean getVisiable() {
        return isVisiable;
    }

    //是否可见,不可见时不再绘制
    private volatile boolean isVisiable = true;

    public boolean getmIsVisiable() {
        return isVisiable;
    }

    public void setmIsVisiable(boolean isVisiable) {
        this.isVisiable = isVisiable;
    }

    @Override
    public void resume() {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void onLowmemory() {

    }
}
