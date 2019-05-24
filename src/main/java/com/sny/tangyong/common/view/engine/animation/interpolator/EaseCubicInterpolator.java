package com.sny.tangyong.common.view.engine.animation.interpolator;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * http://cubic-bezier.com/#.36,0,.55,.99
 * <p>
 * https://blog.csdn.net/goumiaoqiong/article/details/52136013?locationNum=10&fps=1
 */
public class EaseCubicInterpolator implements Interpolator {

    private int mLastI;
    private static final float STEP_SIZE = 2.4414062E-4F;
    private final PointF point1;
    private final PointF point2;

    public EaseCubicInterpolator() {
        this(0.2F, 0.0F, 0.2F, 1.0F);
    }

    public EaseCubicInterpolator(float x1, float y1, float x2, float y2) {
        this.mLastI = 0;
        this.point1 = new PointF();
        this.point2 = new PointF();
        this.point1.x = x1;
        this.point1.y = y1;
        this.point2.x = x2;
        this.point2.y = y2;
    }

    public float getInterpolation(float input) {
        float t = input;
        if (input == 0.0F) {
            this.mLastI = 0;
        }

        for (int i = this.mLastI; i < 4096; ++i) {
            t = (float) i * 2.4414062E-4F;
            double tempX = cubicEquation((double) t, (double) this.point1.x, (double) this.point2.x);
            if (tempX >= (double) input) {
                this.mLastI = i;
                break;
            }
        }

        double value = cubicEquation((double) t, (double) this.point1.y, (double) this.point2.y);
        if (input == 1.0F) {
            this.mLastI = 0;
        }

        return (float) value;
    }

    public static double cubicEquation(double t, double p1, double p2) {
        double u = 1.0D - t;
        double tt = t * t;
        double uu = u * u;
        double ttt = tt * t;
        return 3.0D * uu * t * p1 + 3.0D * u * tt * p2 + ttt;
    }


}
