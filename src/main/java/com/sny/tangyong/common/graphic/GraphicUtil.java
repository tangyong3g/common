package com.sny.tangyong.common.graphic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/19.
 */
public class GraphicUtil {

    /**
     * calculate sample size
     *
     * @param originalOptions 不需要把相素的数据编码出来
     * @param reqWidth    需要的宽
     * @param reqHeight 高
     * @return 比例
     */
    public static int calculateInSampleSize(BitmapFactory.Options originalOptions, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        int width = originalOptions.outWidth;
        int height = originalOptions.outHeight;

        Log.d("tyler.tang", "original width:" + width);

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 按照请求创建一张符合尺寸的图像
     *
     * @param width 宽
     * @param height 高
     * @param res 图片资源ID
     * @param optionsOriginal
     * @param resources
     * @return
     */
    public static Bitmap decodeBitmapWithWH(int width, int height, int res, BitmapFactory.Options optionsOriginal, Resources resources) {

        BitmapFactory.Options optionsNew = new BitmapFactory.Options();
        optionsNew.inJustDecodeBounds = false;
        int simpleSize = calculateInSampleSize(optionsOriginal, width, height);
        optionsNew.inSampleSize = simpleSize;

        Log.d("tyler.tang", "width:" + width + "simpleSize:\t" + simpleSize);

        return BitmapFactory.decodeResource(resources, res, optionsNew);
    }
}
