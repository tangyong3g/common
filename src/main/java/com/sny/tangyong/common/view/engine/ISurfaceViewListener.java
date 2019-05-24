package com.sny.tangyong.common.view.engine;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * @author tyler.tang
 * @since 2014/04/02
 */
public interface ISurfaceViewListener extends ICleanup{

    /**
     * 初始化
     *
     * @param context
     */
    void create(Context context);


    void resize(int width, int height);

    /**
     * 进入界面
     */
    void resume();

    /**
     * 绘制
     * TODO 这里还不够抽象,因为有可能GL来处理就不需要Canvas
     */
    void drawFrame(Canvas canvas);

    /**
     * 暂停
     */
    void pause();

    /**
     * 释放占用的资源
     */
    void dispose();

}
