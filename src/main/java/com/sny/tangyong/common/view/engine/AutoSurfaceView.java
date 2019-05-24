package com.sny.tangyong.common.view.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sny.tangyong.common.BuildConfig;
import com.sny.tangyong.common.graphic.Rect;

import java.util.ArrayList;


/**
 * 主要作用
 * 1:完成绘制线程的定义,绘制生命周期定义
 * 2:FPS日志记录,具体业务往下沉
 */
public abstract class AutoSurfaceView extends SurfaceView implements Runnable, ISurfaceViewListener, SurfaceHolder.Callback {

    //Debug标记
    private static final boolean DEBUG = true;
    //TAG
    static final String TAG = "AutoSurfaceView";
    //线程名称
    static final String THREAD_NAME = "AutoSurfaceViewThread";
    //SurfaceView Width Height
    protected int mWidth;
    protected int mHeight;
    //中心点
    protected Point mPointCenter;
    //绘制线程
    Thread renderThread = null;
    SurfaceHolder holder;
    //绘制标记
    volatile boolean running = false;
    Paint mPaint;
    // 场景中的实体
    ArrayList<Entity> mEntities = new ArrayList<>();

    //处理帧率
    private long lastFrameTime = System.nanoTime();
    private float deltaTime = 0;
    private float deltaTimeTotal;
    private long mDel;

    //用来清除屏幕
    PorterDuffXfermode cleanMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    PorterDuffXfermode srcMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    private static Handler mHalder;
    //测试入口
    Rect mRect;
    private boolean mDisplayTestEnter = false;
    Context mContext;


    /**
     * 构造方法
     *
     * @param context
     */
    public AutoSurfaceView(Context context) {
        super(context);
        mContext = context;
        holder = getHolder();
        holder.addCallback(this);
        mPaint = new Paint();
        mPaint.setTextSize(24);
        mPaint.setColor(Color.RED);
    }


    public void setDisplayTestEnter(boolean displayEnter) {

        mDisplayTestEnter = displayEnter;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onMeasure()");
        }
    }

    /**
     * 重新绘制
     * <p>
     * Activity resume启动线程
     */
    public void resume() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "resume()");
        }
        running = true;
        renderThread = new Thread(this, THREAD_NAME);
        renderThread.start();
    }

    /**
     * 暂停绘制
     */
    public void pause() {

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "pause");
        }

        if (!renderThread.isInterrupted())
            renderThread.interrupt();

        running = false;
        renderThread = null;
    }

    /**
     * 具体绘制往下传,到下面实现
     */
    protected abstract void drawFrame(Canvas canvas, Paint paint, long frameStartTime, float deltaTime);

    int fps = 0;
    long lastTime = SystemClock.uptimeMillis(); // ms
    int frameCount = 0;


    /**
     * FPS
     *
     * @param canvas
     */
    private void fps(Canvas canvas) {
        ++frameCount;
        long curTime = SystemClock.uptimeMillis();
        // 取固定时间间隔为1秒
        if (curTime - lastTime > 1000) {
            fps = frameCount;
            frameCount = 0;
            lastTime = curTime;
            Log.i("FPS", fps + "");
        }
    }


    @SuppressLint("HandlerLeak")
    public void run() {

        Looper.prepare();

        mHalder = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!running) return;
                if (!holder.getSurface().isValid()) {
                    requestRender();
                    return;
                }
                Canvas canvas = holder.lockCanvas();
                drawFrame(canvas);
                holder.unlockCanvasAndPost(canvas);
                requestRender();
            }
        };
        requestRender();

        Looper.loop();
    }

    private static final int DRAW_FRAME_ACTION = 999;

    /**
     * 请求绘制
     */
    public static void requestRender() {
        Message msg = new Message();
        msg.what = DRAW_FRAME_ACTION;
        mHalder.sendMessage(msg);
    }


    /**
     * 绘制每一帧前清除屏幕
     *
     * @param canvas
     */
    private void clean(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        Paint p = new Paint();
        p.setXfermode(cleanMode);
        canvas.drawPaint(p);
        p.setXfermode(srcMode);
    }

    public static Handler getHanlder() {
        return mHalder;
    }

    /**
     * 绘制每一帧率
     *
     * @param canvas
     */
    public void drawFrame(Canvas canvas) {
        if (!running) return;
        if (canvas == null) return;
        clean(canvas);
        long time = System.nanoTime();
        deltaTime = (time - lastFrameTime) / 1000000000.0f;
        lastFrameTime = time;
        deltaTimeTotal += deltaTime;
        // 当前帧开始绘制的时间
        long frameStartTime = System.currentTimeMillis();
        fps(canvas);
        drawFrame(canvas, mPaint, frameStartTime, deltaTime);

        if (BuildConfig.DEBUG && DEBUG && mDisplayTestEnter) {
            mRect.drawFrame(canvas);
        }
        // 绘制当前帧所用时长
        frameStartTime = System.currentTimeMillis() - frameStartTime;
        mDel = frameStartTime;
    }


    @Override
    public void dispose() {

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "dispose");
        }

        // 释放场景中的物体
        if (mEntities != null) {
            for (Entity temp : mEntities) {
                temp.cleanup();
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "surfaceChanged \tW: " + width + "\tH: " + height);
        }
        mWidth = width;
        mHeight = height;
        mPointCenter = new Point(mWidth / 2, mHeight / 2);


        if (BuildConfig.DEBUG && DEBUG) {
            Log.i(TAG, "AutoSurfaceView()");
            mRect = new Rect(mWidth, mHeight);
            mRect.create(mContext);
        }


        resize(width, height);
    }

    @Override
    public void onLowmemory() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onLowmemory");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "surfaceCreated");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "surfaceDestroyed");
        }
    }

    @Override
    public void resize(int width, int height) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "resize:\t" + width);
        }


    }

}
