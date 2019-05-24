package com.sny.tangyong.common.graphic;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sny.tangyong.common.view.engine.Entity;


/**
 * 测试入口
 */
public class Rect extends Entity {

    public RectF mHgRect;
    public RectF mInRect;
    public RectF mLoopRect;
    public RectF mOutRect;
    public RectF mStop;

    int mContainerWidth;
    int mContainerHeight;


    int mLeft = 0;
    int mTop = 0;
    int mWidth = 100;
    int mHeight = 60;
    int mInterval = 20;
    Paint mPaint = new Paint();
    int mLeftOffset = 0;
    int mTopOffset = 0;


    public Rect(int width, int height) {
        mContainerHeight = height;
        mContainerWidth = width;

        mWidth = width / 5;
        mHeight = height / 15;
    }


    @Override
    public void create(Context context) {
        init(context);
    }

    public void init(Context context) {

        mHgRect = new RectF(mLeft, mTop, mWidth, mHeight + mTop);

        mInRect = new RectF(mLeft + mWidth + mInterval, 0 + mTop, 2 * mWidth + mInterval, mHeight + mTop);
        mLoopRect = new RectF(mLeft + mWidth * 2 + mInterval * 2, 0 + mTop, mWidth * 3 + 2 * mInterval, mHeight + mTop);
        mOutRect = new RectF(mLeft + mWidth * 3 + mInterval * 3, 0 + mTop, mWidth * 4 + 3 * mInterval, mHeight + mTop);
        mStop = new RectF(mLeft + mWidth * 4 + mInterval * 4, 0 + mTop, mWidth * 5 + 4 * mInterval, mHeight + mTop);
        setmIsVisiable(true);
    }


    @Override
    public void drawFrame(Canvas canvas) {

        if (!getmIsVisiable()) {
            return;
        }

        mLeftOffset = mWidth / 3;
        mTopOffset = mHeight / 3;

        mPaint.reset();
        mPaint.setColor(Color.GREEN);

        canvas.drawRect(mHgRect, mPaint);
        mPaint.reset();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(20);
        canvas.drawText("enter", 0, 5, mHgRect.left + mLeftOffset, mHgRect.bottom - mTopOffset, mPaint);


        mPaint.reset();
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(mInRect, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(20);
        canvas.drawText("in", 0, 2, mInRect.left + mLeftOffset, mInRect.bottom - mTopOffset, mPaint);

        mPaint.reset();
        mPaint.setColor(Color.RED);
        canvas.drawRect(mLoopRect, mPaint);
        mPaint.setTextSize(20);
        mPaint.setColor(Color.BLACK);
        canvas.drawText("loop", 0, 4, mLoopRect.left + mLeftOffset, mLoopRect.bottom - mTopOffset, mPaint);

        /* */
        mPaint.reset();
        mPaint.setColor(Color.BLUE);
        canvas.drawRect(mOutRect, mPaint);
        mPaint.setTextSize(20);
        mPaint.setColor(Color.BLACK);
        canvas.drawText("out", 0, 3, mOutRect.left + mLeftOffset, mOutRect.bottom - mTopOffset, mPaint);


        mPaint.reset();
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(mStop, mPaint);
        mPaint.setTextSize(20);
        mPaint.setColor(Color.BLACK);
        canvas.drawText("stop", 0, 3, mStop.left + mLeftOffset, mStop.bottom - mTopOffset, mPaint);

    }

    @Override
    public void dispose() {

    }
}
