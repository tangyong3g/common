/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sny.tangyong.common.graphic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.sny.tangyong.common.R;


/**
 * Various utilities shared amongst the Launcher's classes.
 */
public final class Utilities {
    private static int sIconWidth = -1;
    private static int sIconHeight = -1;

    // private final static int DEFAULTICONSIZE = 72; //默认图标数据大小

    private static final Paint sPAINT = new Paint();
    private static final Rect sBOUNDS = new Rect();
    private static final Rect sOLDBOUNDS = new Rect();
    private static Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
    }

    /**
     * 初始化图标大小
     *
     * @param context
     * @author luopeihuan
     */
    private static void initIconSize(Context context) {
        if (sIconWidth == -1) {
            sIconWidth = sIconHeight = getIconSize(context);
        }
    }

    /**
     * <br>功能简述:获取图标大小
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param context
     * @return
     */
    public static int getIconSize(Context context) {
        int iconSize = 0;
//		GoSettingControler controler = GOLauncherApp.getSettingControler();
//		if (controler != null && controler.getDesktopSettingInfo() != null) {
//			DesktopSettingInfo info = controler.getDesktopSettingInfo();
//			iconSize = info.getIconRealSize();
//		} else {
//			final Resources resources = context.getResources();
//			iconSize = (int) resources.getDimension(R.dimen.screen_icon_size);
//			if (GoLauncher.isLargeIcon()) {
//				iconSize = (int) resources
//						.getDimension(R.dimen.screen_icon_large_size);
//			} else if (Machine.isLephone()) {
//				iconSize = Machine.LEPHONE_ICON_SIZE;
//			}
//		}
        final Resources resources = context.getResources();
        iconSize = (int) resources.getDimension(R.dimen.app_icon_size);
        /*if (GoLauncher.isLargeIcon()) {
			iconSize = (int) resources
					.getDimension(R.dimen.screen_icon_large_size);
		} else if (Machine.isLephone()) {
			iconSize = Machine.LEPHONE_ICON_SIZE;
		}*/
        return iconSize;
    }

    /**
     * Returns a Drawable representing the thumbnail of the specified Drawable.
     * The size of the thumbnail is defined by the dimension
     * android.R.dimen.launcher_application_icon_size.
     * <p>
     * This method is not thread-safe and should be invoked on the UI thread
     * only.
     *
     * @param icon    The icon to get a thumbnail of.
     * @param context The application's context.
     * @return A thumbnail for the specified icon or the icon itself if the
     * thumbnail could not be created.
     */
    public static Drawable createIconThumbnail(Drawable icon, Context context) {
        if (icon == null || context == null) {
            return null;
        }

        initIconSize(context);
        int width = sIconWidth;
        int height = sIconHeight;

        final Resources resources = context.getResources();

        float scale = 1.0f;
        if (icon instanceof PaintDrawable) {
            PaintDrawable painter = (PaintDrawable) icon;
            painter.setIntrinsicWidth(width);
            painter.setIntrinsicHeight(height);
        } else if (icon instanceof BitmapDrawable) {
            // Ensure the bitmap has a density.
            BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (null != bitmap && bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                bitmapDrawable.setTargetDensity(resources.getDisplayMetrics());
            }
        }
        int iconWidth = icon.getIntrinsicWidth();
        int iconHeight = icon.getIntrinsicHeight();

        if (width > 0 && height > 0) {
            if (width < iconWidth || height < iconHeight || scale != 1.0f) {
                final float ratio = (float) iconWidth / iconHeight;

                if (iconWidth > iconHeight) {
                    height = (int) (width / ratio);
                } else if (iconHeight > iconWidth) {
                    width = (int) (height * ratio);
                }

                final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE
                        ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
                try {
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    synchronized (sCanvas) {
                        final Canvas canvas = sCanvas;
                        canvas.setBitmap(thumb);
                        // Copy the old bounds to restore them later
                        // If we were to do oldBounds = icon.getBounds(),
                        // the call to setBounds() that follows would
                        // change the same instance and we would lose the
                        // old bounds
                        sOLDBOUNDS.set(icon.getBounds());
                        final int x = (sIconWidth - width) / 2;
                        final int y = (sIconHeight - height) / 2;
                        icon.setBounds(x, y, x + width, y + height);
                        icon.draw(canvas);
                    }
                    icon.setBounds(sOLDBOUNDS);
                    icon = new BitmapDrawable(resources, thumb);
                } catch (OutOfMemoryError e) {
                    return null;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else if (iconWidth < width && iconHeight < height) {
                try {
                    final Bitmap.Config c = Bitmap.Config.ARGB_8888;
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    final float ratio = Math.min((float) width / iconWidth, (float) height
                            / iconHeight);
                    final int scaledWidth = (int) (ratio * iconWidth);
                    final int scaledHeight = (int) (ratio * iconHeight);

                    synchronized (sCanvas) {
                        sBOUNDS.set((sIconWidth - scaledWidth) / 2,
                                (sIconHeight - scaledHeight) / 2, width, height);
                        sOLDBOUNDS.set(0, 0, iconWidth, iconHeight);

                        final Canvas canvas = sCanvas;
                        final Paint paint = sPAINT;

                        canvas.setBitmap(thumb);
                        sOLDBOUNDS.set(icon.getBounds());
                        final int x = (width - scaledWidth) / 2;
                        final int y = (height - scaledHeight) / 2;
                        icon.setBounds(x, y, x + scaledWidth, y + scaledHeight);
                        icon.draw(canvas);

                        paint.setDither(false);
                        paint.setFilterBitmap(true);
                    }
                    icon.setBounds(sOLDBOUNDS);
                    icon = new BitmapDrawable(resources, thumb);
                } catch (OutOfMemoryError e) {
                    return null;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        return icon;
    }

    public static Drawable createIconThumbnail(Drawable icon, int width, int height, Context context) {
        float scale = 1.0f;
        final Resources resources = context.getResources();
        if (icon instanceof PaintDrawable) {
            PaintDrawable painter = (PaintDrawable) icon;
            painter.setIntrinsicWidth(width);
            painter.setIntrinsicHeight(height);
        } else if (icon instanceof BitmapDrawable) {
            // Ensure the bitmap has a density.
            BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (null != bitmap && bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                bitmapDrawable.setTargetDensity(resources.getDisplayMetrics());
            }
        }
        int iconWidth = icon.getIntrinsicWidth();
        int iconHeight = icon.getIntrinsicHeight();

        if (width > 0 && height > 0) {
            if (width < iconWidth || height < iconHeight || scale != 1.0f) {
                final float ratio = (float) iconWidth / iconHeight;

                if (iconWidth > iconHeight) {
                    height = (int) (width / ratio);
                } else if (iconHeight > iconWidth) {
                    width = (int) (height * ratio);
                }

                final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE
                        ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
                try {
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    synchronized (sCanvas) {
                        final Canvas canvas = sCanvas;
                        canvas.setBitmap(thumb);
                        // Copy the old bounds to restore them later
                        // If we were to do oldBounds = icon.getBounds(),
                        // the call to setBounds() that follows would
                        // change the same instance and we would lose the
                        // old bounds
                        sOLDBOUNDS.set(icon.getBounds());
                        final int x = (sIconWidth - width) / 2;
                        final int y = (sIconHeight - height) / 2;
                        icon.setBounds(x, y, x + width, y + height);
                        icon.draw(canvas);
                    }
                    icon.setBounds(sOLDBOUNDS);
                    icon = new BitmapDrawable(resources, thumb);
                } catch (OutOfMemoryError e) {
                    return null;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else if (iconWidth < width && iconHeight < height) {
                try {
                    final Bitmap.Config c = Bitmap.Config.ARGB_8888;
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    synchronized (sCanvas) {
                        final Canvas canvas = sCanvas;
                        canvas.setBitmap(thumb);
                        sOLDBOUNDS.set(icon.getBounds());
                        final int x = (width - iconWidth) / 2;
                        final int y = (height - iconHeight) / 2;
                        icon.setBounds(x, y, x + iconWidth, y + iconHeight);
                        icon.draw(canvas);
                    }
                    icon.setBounds(sOLDBOUNDS);
                    icon = new BitmapDrawable(resources, thumb);
                } catch (OutOfMemoryError e) {
                    return null;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        return icon;
    }

    /**
     * Returns a Bitmap representing the thumbnail of the specified Bitmap. The
     * size of the thumbnail is defined by the dimension
     * android.R.dimen.launcher_application_icon_size.
     * <p>
     * This method is not thread-safe and should be invoked on the UI thread
     * only.
     *
     * @param bitmap  The bitmap to get a thumbnail of.
     * @param context The application's context.
     * @return A thumbnail for the specified bitmap or the bitmap itself if the
     * thumbnail could not be created.
     */
    public static Bitmap createBitmapThumbnail(Bitmap bitmap, Context context) {
        initIconSize(context);
        int width = sIconWidth;
        int height = sIconHeight;

        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        if (width > 0 && height > 0) {
            if (width < bitmapWidth || height < bitmapHeight) {
                final float ratio = (float) bitmapWidth / bitmapHeight;

                if (bitmapWidth > bitmapHeight) {
                    height = (int) (width / ratio);
                } else if (bitmapHeight > bitmapWidth) {
                    width = (int) (height * ratio);
                }

                // Log.d("XViewFrame", "bitmap.getConfig() = " +
                // bitmap.getConfig());
                Bitmap.Config c = (width == sIconWidth && height == sIconHeight) ? bitmap
                        .getConfig() : Bitmap.Config.ARGB_8888;
                if (null == c) {
                    c = Bitmap.Config.ARGB_8888;
                }

                try {
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    synchronized (sCanvas) {
                        final Canvas canvas = sCanvas;
                        final Paint paint = sPAINT;
                        canvas.setBitmap(thumb);
                        paint.setDither(false);
                        paint.setFilterBitmap(true);
                        sBOUNDS.set((sIconWidth - width) / 2, (sIconHeight - height) / 2, width,
                                height);
                        sOLDBOUNDS.set(0, 0, bitmapWidth, bitmapHeight);
                        canvas.drawBitmap(bitmap, sOLDBOUNDS, sBOUNDS, paint);
                    }
                    return thumb;
                } catch (OutOfMemoryError e) {
                    return null;
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } else if (bitmapWidth < width || bitmapHeight < height) {
                try {
                    final Bitmap.Config c = Bitmap.Config.ARGB_8888;
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    final float scale = Math.min((float) width / bitmapWidth, (float) height
                            / bitmapHeight);
                    final int scaledWidth = (int) (scale * bitmapWidth);
                    final int scaledHeight = (int) (scale * bitmapHeight);
                    synchronized (sCanvas) {
                        sBOUNDS.set((sIconWidth - scaledWidth) / 2,
                                (sIconHeight - scaledHeight) / 2, width, height);
                        sOLDBOUNDS.set(0, 0, bitmapWidth, bitmapHeight);

                        final Canvas canvas = sCanvas;
                        final Paint paint = sPAINT;
                        canvas.setBitmap(thumb);
                        paint.setDither(false);
                        paint.setFilterBitmap(true);
                        canvas.drawBitmap(bitmap, sOLDBOUNDS, sBOUNDS, paint);
                    }
                    return thumb;
                } catch (OutOfMemoryError e) {
                    return null;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    /**
     * @param icon
     * @param context
     * @return
     */
    public static Drawable drawReflection(Drawable icon, Context context) {
        initIconSize(context);

        // The gap we want between the reflection and the original image
        final float scale = 1.30f;

        int width = sIconWidth;
        int height = sIconHeight;
        float ratio = sIconHeight / (sIconHeight * scale);
        Bitmap original;
        try {
            original = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            return icon;
        }
        final Canvas cv = new Canvas();
        cv.setBitmap(original);
        icon.setBounds(0, 0, width, height);
        icon.draw(cv);
        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage;
        try {
            reflectionImage = Bitmap.createBitmap(original, 0, height / 2, width, height / 2,
                    matrix, false);
        } catch (OutOfMemoryError e) {
            return new FastBitmapDrawable(original);
        }

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection;
        try {
            bitmapWithReflection = Bitmap.createBitmap(width, (int) (height * scale),
                    Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            return new FastBitmapDrawable(original);
        }

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the gap
        // Paint deafaultPaint = new Paint();
        // canvas.drawRect(0, height, width, height + reflectionGap,
        // deafaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height - 6, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, original.getHeight(), 0,
                bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height - 6, width, bitmapWithReflection.getHeight(), paint);
        // Draw in the original image
        canvas.drawBitmap(original, 0, 0, null);
        original.recycle();
        original = null;
        reflectionImage.recycle();
        reflectionImage = null;
        try {
            return new FastBitmapDrawable(Bitmap.createScaledBitmap(bitmapWithReflection,
                    Math.round(sIconWidth * ratio), sIconHeight, true));
        } catch (OutOfMemoryError e) {
            return icon;
        }
    }

    /**
     * @param icon
     * @param context
     * @param tint
     * @return
     */
    public static Drawable scaledDrawable(Drawable icon, Context context, boolean tint, float scale) {
        initIconSize(context);

        int width = sIconWidth;
        int height = sIconHeight;
        Bitmap original;
        try {
            original = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            return icon;
        }
        Canvas canvas = new Canvas(original);
        canvas.setBitmap(original);
        icon.setBounds(0, 0, width, height);
        icon.draw(canvas);

        if (tint) {
            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(width / 2, 0, width / 2, height, 0xCCFFFFFF,
                    0x33FFFFFF, TileMode.CLAMP);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawRect(0, 0, width, height, paint);
        }
        try {
            Bitmap endImage = Bitmap.createScaledBitmap(original, (int) (width * scale),
                    (int) (height * scale), true);
            original.recycle();
            original = null;
            return new FastBitmapDrawable(endImage);
        } catch (OutOfMemoryError e) {
            return icon;
        }
    }

    /**
     * 获取标准图标大小，图标为正方形，高宽相同.
     *
     * @return
     * @author huyong
     */
    public static int getStandardIconSize(final Context context) {
        initIconSize(context);
        return sIconWidth;
    }

    private static void initStatics(Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        final float density = metrics.density;

//		if (!DrawUtils.sIsLargeDimen) {
//			sIconWidth = (int) resources.getDimension(R.dimen.app_icon_size);
//		} else {
//			sIconWidth = (int) resources.getDimension(R.dimen.app_icon_size_pad);
//		}
        sIconWidth = (int) resources.getDimension(R.dimen.app_icon_size);
        //长宽都必须为2的幂
        //TODO
//		if (!Shared.isPowerOf2(sIconWidth)) {
//			sIconWidth = Shared.nextPowerOf2(sIconWidth);
//		}
        sIconHeight = sIconWidth;
    }

    /**
     * Returns a bitmap suitable for the all apps view. The bitmap will be a
     * power of two sized ARGB_8888 bitmap that can be used as a gl texture.
     */
    public static Bitmap createIconBitmap(Drawable icon, Context context) {
        if (icon == null || context == null) {
            return null;
        }
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                initStatics(context);
            }

            int width = sIconWidth;
            int height = sIconHeight;

            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            } else if (icon instanceof BitmapDrawable) {
                // Ensure the bitmap has a density.
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }

            int sourceWidth = icon.getIntrinsicWidth();
            int sourceHeight = icon.getIntrinsicHeight();

            if (sourceWidth > 0 && sourceHeight > 0) {
                // There are intrinsic sizes.
                if (width < sourceWidth || height < sourceHeight) {
                    // It's too big, scale it down.
                    final float ratio = (float) sourceWidth / sourceHeight;
                    if (sourceWidth > sourceHeight) {
                        height = (int) (width / ratio);
                    } else if (sourceHeight > sourceWidth) {
                        width = (int) (height * ratio);
                    }
                } else if (sourceWidth < width && sourceHeight < height) {
                    // It's small
                    final float ratioW = (float) sourceWidth / width;
                    final float ratioH = (float) sourceHeight / height;
                    if (ratioH > ratioW) {
                        width = (int) (sourceWidth / ratioH);
                    } else {
                        height = (int) (sourceHeight / ratioW);
                    }
                }
            }

            final Canvas canvas = sCanvas;
            Bitmap bitmap = null;
            try {
                bitmap = Bitmap.createBitmap(sIconWidth, sIconHeight, Bitmap.Config.ARGB_8888);
                canvas.setBitmap(bitmap);
            } catch (OutOfMemoryError e) {
                return null;
            }

            sOLDBOUNDS.set(icon.getBounds());
            int left = (sIconWidth - width) / 2;
            int top = (sIconHeight - height) / 2;
            icon.setBounds(left, top, left + width, top + height);
            if (!bitmap.isRecycled()) {
                icon.draw(canvas);
            }
            icon.setBounds(sOLDBOUNDS);

            return bitmap;
        }
    }

    /***
     * 放大图标原始图片到定义的大小，否则图标会在左上角不居中
     * @param drawable
     * @param context
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable, Context context) {
        // drawable转换成bitmap
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        //对创建好的图片做缩放操作。
        int iconSize = (int) context.getResources().getDimension(R.dimen.app_icon_size);
//		if (!DrawUtils.sIsLargeDimen) {
//			iconSize = (int) context.getResources().getDimension(R.dimen.app_icon_size);
//		} else {
//			iconSize = (int) context.getResources().getDimension(R.dimen.app_icon_size_pad);
//		}
        float scaleW = ((float) iconSize) / width;
        float scaleH = ((float) iconSize) / height;

        final Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postScale(scaleW, scaleH);

        Bitmap desImg = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable bd = new BitmapDrawable(desImg);
        return bd;
    }

    public static Bitmap createBitmapFromDrawable(final Drawable drawable) {

        if (drawable == null) {
            return null;
        }

        Bitmap bitmap = null;
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicHeight <= 0 || intrinsicWidth <= 0) {
            return null;
        }

        try {
            Config config = drawable.getOpacity() != PixelFormat.OPAQUE
                    ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config);
        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        canvas = null;
        return bitmap;
    }

    public static BitmapDrawable createBitmapDrawableFromDrawable(final Drawable drawable,
                                                                  Context context) {
        Bitmap bitmap = createBitmapFromDrawable(drawable);
        if (bitmap == null) {
            return null;
        }

        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        return bitmapDrawable;
    }

    /**
     * AppWidgetHostView.updateAppWidgetSize() 的一个代理方法
     * AppWidgetHostView.updateAppWidgetSize (Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight) Added in API level 16
     *
     * @param option
     * @param minW   单位是dp不是px
     * @param minH   单位是dp不是px
     * @param maxW   单位是dp不是px
     * @param maxH   单位是dp不是px
     */
    public static void updateAppWidgetSize(AppWidgetHostView hostView,
                                           Bundle option, int minW, int minH, int maxW, int maxH) {
        // API16才有这个方法
        if (Build.VERSION.SDK_INT >= 16) {
            if (hostView != null) {
                Class cls = hostView.getClass();
                try {
                    Method method = cls.getMethod("updateAppWidgetSize",
                            Bundle.class, int.class, int.class, int.class,
                            int.class);
                    method.invoke(hostView, option, minW, minH, maxW, maxH);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
