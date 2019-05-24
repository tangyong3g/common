package com.sny.tangyong.common.graphic.g3d;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;


/**
 * 完成FPS的绘制,实现程序基本的生命周期接口
 */
public abstract class AbstractApplication extends InputAdapter implements ApplicationListener {

    private static final String TAG = "AbstractApplication";
    private FPSLogger mLogger = new FPSLogger();
    private SpriteBatch mBatch;
    private BitmapFont mFont;

    private Logger mLoger = new com.badlogic.gdx.utils.Logger(TAG, Logger.INFO);

    @Override
    public void create() {
        mLoger.info("create");
        mBatch = new SpriteBatch();
        mFont = new BitmapFont();
    }

    @Override
    public void resize(int width, int height) {
        mLoger.info("resize");
    }

    @Override
    public void render() {
        mLogger.log();
        mBatch.begin();
        mFont.draw(mBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        mBatch.end();
    }

    @Override
    public void pause() {
        mLoger.info("pause");
    }

    @Override
    public void resume() {
        mLoger.info("resume");
    }

    @Override
    public void dispose() {
        mLoger.info("dispose");
    }

}
