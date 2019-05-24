package com.sny.tangyong.common.graphic;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.sny.tangyong.common.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * 
 * <br>类描述:创建bitmap的工具类
 * <br>功能详细描述:
 * 
 * @author  chaoziliang
 * @date  [2012-9-8]
 */
public class BitmapUtility {
	private static final String TAG = "BitmapUtility";

	public static final Bitmap createBitmap(View view, float scale, Config config) {
		Bitmap viewBmp = null;
		Bitmap pRet = null;
		if (null == view) {
			Log.i(TAG, "create bitmap function param view is null");
			return pRet;
		}

		//		view.buildDrawingCache();

		int scaleWidth = (int) (view.getWidth() * scale);
		int scaleHeight = (int) (view.getHeight() * scale);
		if (scaleWidth <= 0 || scaleHeight <= 0) {
			Log.i(TAG, "create bitmap function param view is not layout");
			return pRet;
		}

		boolean bViewDrawingCacheEnable = view.isDrawingCacheEnabled();
		if (!bViewDrawingCacheEnable) {
			view.setDrawingCacheEnabled(true);
		}
		try {
			viewBmp = view.getDrawingCache(true);
			//如果拿到的缓存为空
			if (viewBmp == null) {
				// pRet = Bitmap.createBitmap(scaleWidth, scaleHeight, view.isOpaque()? Config.RGB_565: Config.ARGB_8888);
				pRet = Bitmap.createBitmap(scaleWidth, scaleHeight, config);
				Canvas canvas = new Canvas(pRet);
				canvas.scale(scale, scale);
				view.draw(canvas);
				canvas = null;
			} else {
				pRet = Bitmap.createScaledBitmap(viewBmp, scaleWidth, scaleHeight, true);
			}
			if (viewBmp != null && !viewBmp.isRecycled()) {
				viewBmp.recycle();
				viewBmp = null;
			}
		} catch (OutOfMemoryError e) {
			if (viewBmp != null && !viewBmp.isRecycled()) {
				viewBmp.recycle();
				viewBmp = null;
			}
			if (pRet != null && !pRet.isRecycled()) {
				pRet.recycle();
				pRet = null;
			}
			Log.i(TAG, "create bitmap out of memory");
		} catch (Exception e) {
			if (viewBmp != null && !viewBmp.isRecycled()) {
				viewBmp.recycle();
				viewBmp = null;
			}
			if (pRet != null && !pRet.isRecycled()) {
				pRet.recycle();
				pRet = null;
			}
			Log.i(TAG, "create bitmap exception");
		}
		if (!bViewDrawingCacheEnable) {
			view.setDrawingCacheEnabled(false);
		}

		return pRet;
	}

	public static final Bitmap createBitmap(Bitmap bmp, int desWidth, int desHeight) {
		Bitmap pRet = null;
		if (null == bmp) {
			Log.i(TAG, "create bitmap function param bmp is null");
			return pRet;
		}

		try {
			pRet = Bitmap.createBitmap(desWidth, desHeight, Config.ARGB_8888);
			Canvas canvas = new Canvas(pRet);
			int left = (desWidth - bmp.getWidth()) / 2;
			int top = (desHeight - bmp.getHeight()) / 2;
			canvas.drawBitmap(bmp, left, top, null);
			canvas = null;
		} catch (OutOfMemoryError e) {
			pRet = null;
			Log.i(TAG, "create bitmap out of memory");
		} catch (Exception e) {
			pRet = null;
			Log.i(TAG, "create bitmap exception");
		}

		return pRet;
	}

	public static final Bitmap createScaledBitmap(Bitmap bmp, int scaleWidth, int scaleHeight) {
		Bitmap pRet = null;
		if (null == bmp) {
			Log.i(TAG, "create scale bitmap function param bmp is null");
			return pRet;
		}

		if (scaleWidth == bmp.getWidth() && scaleHeight == bmp.getHeight()) {
			return bmp;
		}

		try {
			pRet = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
		} catch (OutOfMemoryError e) {
			pRet = null;
			Log.i(TAG, "create scale bitmap out of memory");
		} catch (Exception e) {
			pRet = null;
			Log.i(TAG, "create scale bitmap exception");
		}

		return pRet;
	}

	public static final boolean saveBitmap(Bitmap bmp, String bmpName) {
		if (null == bmp) {
			Log.i(TAG, "save bitmap to file bmp is null");
			return false;
		}

		FileOutputStream stream = null;
		try {
			File file = new File(bmpName);
			if (file.exists()) {
				boolean bDel = file.delete();
				if (!bDel) {
					Log.i(TAG, "delete src file fail");
					return false;
				}
			} else {
				File parent = file.getParentFile();
				if (null == parent) {
					Log.i(TAG, "get bmpName parent file fail");
					return false;
				}
				if (!parent.exists()) {
					boolean bDir = parent.mkdirs();
					if (!bDir) {
						Log.i(TAG, "make dir fail");
						return false;
					}
				}
			}
			boolean bCreate = file.createNewFile();
			if (!bCreate) {
				Log.i(TAG, "create file fail");
				return false;
			}

			stream = new FileOutputStream(file);
			boolean bOk = bmp.compress(CompressFormat.PNG, 100, stream);
			if (!bOk) {
				Log.i(TAG, "bitmap compress file fail");
				return false;
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
			return false;
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (Exception e2) {
					Log.i(TAG, "close stream " + e2.toString());
				}
			}
		}

		return true;
	}

	public static final Uri saveBitmapAsJpeg(Bitmap bmp, String bmpName) {
		if (null == bmp) {
			Log.i("dly", "save bitmap to file bmp is null");
			return null;
		}

		FileOutputStream stream = null;
		File file = new File("/data/data/com.zeroteam.zerolauncher/files" + bmpName.substring(bmpName.lastIndexOf("/")));
		try {
			if (file.exists()) {
				boolean bDel = file.delete();
				if (!bDel) {
					Log.i("dly", "delete src file fail");
					return null;
				}
			} else {
				File parent = file.getParentFile();
				if (null == parent) {
					Log.i("dly", "get bmpName parent file fail");
					return null;
				}
				if (!parent.exists()) {
					boolean bDir = parent.mkdirs();
					if (!bDir) {
						Log.i("dly", "make dir fail");
						return null;
					}
				}
			}
			boolean bCreate = file.createNewFile();
			if (!bCreate) {
				Log.i("dly", "create file fail");
				return null;
			}

			stream = new FileOutputStream(file);
			boolean bOk = bmp.compress(CompressFormat.JPEG, 100, stream);
			if (!bOk) {
				Log.i("dly", "bitmap compress file fail");
				return null;
			}
		} catch (Exception e) {
			Log.i("dly", "　saveBitmapAsJpeg++ｅ　" + e.toString());
			return null;
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (Exception e2) {
					Log.i("dly", "close stream " + e2.toString());
				}
			}
		}

		return Uri.fromFile(file);
	}

	public static Bitmap createGrayBitmap(Bitmap src, Context context) {
		if (src == null) {
			return null;
		}
		BitmapDrawable drawable = new BitmapDrawable(src);
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0f);
		drawable.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		return Utilities.createIconBitmap(drawable, context);
	}

	public static Bitmap loadBitmap(Context context, Uri uri) {
		Bitmap pRet = null;
		if (null == context) {
			Log.i(TAG, "load bitmap context is null");
			return pRet;
		}
		if (null == uri) {
			Log.i(TAG, "load bitmap uri is null");
			return pRet;
		}

		InputStream is = null;
		int sampleSize = 1;
		Options opt = new Options();

		boolean bool = true;
		while (bool) {
			try {
				is = context.getContentResolver().openInputStream(uri);
				opt.inSampleSize = sampleSize;
				pRet = null;
				pRet = BitmapFactory.decodeStream(is, null, opt);
				bool = false;
			} catch (OutOfMemoryError e) {
				sampleSize *= 2;
				if (sampleSize > (1 << 10)) {
					bool = false;
				}
			} catch (Throwable e) {
				bool = false;
				Log.i(TAG, e.getMessage());
			} finally {
				try {
					is.close();
				} catch (Exception e2) {
					Log.i(TAG, e2.getMessage());
					Log.i(TAG, "load bitmap close uri stream exception");
				}
			}
		}

		return pRet;
	}

	public static BitmapDrawable zoomDrawable(Context context, Drawable drawable, int w, int h) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = null;
			// drawable 转换成 bitmap
			if (drawable instanceof BitmapDrawable) {
				//如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
				oldbmp = ((BitmapDrawable) drawable).getBitmap();
			} else {
				oldbmp = Utilities.createBitmapFromDrawable(drawable);
			}

			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
			float scaleWidth = (float) w / width; // 计算缩放比例
			float scaleHeight = (float) h / height;
			matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
			matrix = null;
			return new BitmapDrawable(context.getResources(), newbmp); // 把 bitmap 转换成 drawable 并返回
		}
		return null;
	}

	public static BitmapDrawable zoomDrawable(Drawable drawable, int w, int h, Resources res) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = null;
			// drawable 转换成 bitmap
			if (drawable instanceof BitmapDrawable) {
				//如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
				oldbmp = ((BitmapDrawable) drawable).getBitmap();
			} else {
				oldbmp = Utilities.createBitmapFromDrawable(drawable);
			}
			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象

			float scaleWidth = (float) w / width; // 计算缩放比例
			float scaleHeight = (float) h / height;
			matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
			matrix = null;
			return new BitmapDrawable(res, newbmp); // 把 bitmap 转换成 drawable 并返回
		}
		return null;
	}

	public static BitmapDrawable zoomDrawable(Drawable drawable, float wScale, float hScale,
			Resources res) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = null;
			// drawable 转换成 bitmap
			if (drawable instanceof BitmapDrawable) {
				//如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
				oldbmp = ((BitmapDrawable) drawable).getBitmap();
			} else {
				oldbmp = Utilities.createBitmapFromDrawable(drawable);
			}

			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
			matrix.postScale(wScale, hScale); // 设置缩放比例
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
			matrix = null;
			return new BitmapDrawable(res, newbmp); // 把 bitmap 转换成 drawable 并返回
		}
		return null;
	}

	public static BitmapDrawable clipDrawable(BitmapDrawable drawable, int w, int h, Resources res) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			if (width < w) {
				w = width;
			}
			if (height < h) {
				h = height;
			}
			int x = (width - w) >> 1;
			int y = (height - h) >> 1;
			Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
			Bitmap newbmp = Bitmap.createBitmap(drawable.getBitmap(), x, y, w, h, matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
			matrix = null;
			return new BitmapDrawable(res, newbmp); // 把 bitmap 转换成 drawable 并返回
		}
		return null;
	}

	/**
     * 返回根据性状进行勾勒的图片
     *
     * @param context
     * @param drawable 原图
     * @param mask 勾勒的性状
     * @return 勾勒完成的图
     */
    public static BitmapDrawable getMaskIcon(Context context, Drawable drawable, Drawable maskDrawable,
    		Drawable coverDrawable) {
        try {
            Bitmap oldbmp = null;
            if (drawable != null) {
                // drawable 转换成 bitmap
                if (drawable instanceof BitmapDrawable) {
                    // 如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
                    oldbmp = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    oldbmp = createBitmapFromDrawable(drawable);
                }

            }
            Bitmap maskbmp = null;
            if (maskDrawable != null) {
                // drawable 转换成 bitmap
                if (maskDrawable instanceof BitmapDrawable) {
                    // 如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
                    maskbmp = ((BitmapDrawable) maskDrawable).getBitmap();
                } else {
                    maskbmp = createBitmapFromDrawable(maskDrawable);
                }
            }

            Bitmap coverBmp = null;
            if (coverDrawable != null) {
            	if (coverDrawable instanceof BitmapDrawable) {
            		coverBmp = ((BitmapDrawable) coverDrawable).getBitmap();
            	} else {
            		coverBmp = createBitmapFromDrawable(coverDrawable);
				}
            }


            return getMaskIcon(context, oldbmp, maskbmp, coverBmp);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    /**
     * 返回根据性状进行勾勒的图片
     * @param context
     * @param bitmap
     * @param maskBitmap
     * @return
     */
    public static BitmapDrawable getMaskIcon(Context context, Bitmap bitmap, Bitmap maskBitmap
    		, Bitmap coverBitmap) {
        try {
            if (maskBitmap == null) {
                return null;
            }
            int maskW = maskBitmap.getWidth();
            int maskH = maskBitmap.getHeight();
            Bitmap temp = Bitmap.createBitmap(maskW, maskH, Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(temp);

            if (bitmap != null) {

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                if (width == 0 || height == 0) {
                    return null;
                }
                Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
                float scale =  1.0f;
                int offsetCutX = 0;
                int offsetCutY = 0;
                if (width > height) {
                    offsetCutX = (width - height) / 2;
                    scale = (float) maskH / height;
                } else {
                    offsetCutY = (height - width) / 2;
                    scale = (float) maskW / width;
                }
                matrix.postScale(scale, scale); // 设置缩放比例
                Bitmap newbmp = Bitmap.createBitmap(bitmap, offsetCutX, offsetCutY,
                        width - 2 * offsetCutX, height - 2 * offsetCutY, matrix, true); // 建立新的
                                                                                            // bitmap
                                                                                            // ，其内容是对原
                                                                                            // bitmap的缩放后的图
                final int drawTop = Math.max(0, maskH - newbmp.getHeight());
                final int drawLeft = Math.max(0, (maskW - newbmp.getWidth()) / 2);
                canvasTemp.drawBitmap(newbmp, drawLeft, drawTop, null);
                matrix = null;
            }

            Paint paint = new Paint();
            PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
            paint.setXfermode(xfermode);
            if (!maskBitmap.isRecycled()) {
                canvasTemp.drawBitmap(maskBitmap, 0, 0, paint);
            }

            if (coverBitmap != null) {
            	paint.reset();
            	canvasTemp.drawBitmap(coverBitmap, 0, 0, paint);
            }
            return new BitmapDrawable(context.getResources(), temp);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public static Drawable composeDrawableText(Context context, Drawable src, String text, int textSize) {
    	if (src == null) {
    		return null;
    	}
    	if (text == null) {
    		return src;
    	}
    	try {
			Bitmap srcBitmap = null;
			if (src instanceof BitmapDrawable) {
				srcBitmap = ((BitmapDrawable) src).getBitmap();
			} else {
				srcBitmap = createBitmapFromDrawable(src);
			}
			if (srcBitmap == null) {
				return null;
			}
			int width = srcBitmap.getWidth();
			int height = srcBitmap.getHeight();
			Bitmap temp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(temp);
			canvas.drawBitmap(srcBitmap, 0, 0, null);

			Paint paint = new Paint();
			paint.setTextSize(textSize);
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setColor(Color.WHITE);
			paint.setAntiAlias(true); // 抗锯齿
			paint.setTextAlign(Paint.Align.CENTER);
			int size = text.length();
			int length = (int) paint.measureText(text);
			int center = length / size / 2;
			int offX = width / 2;
			int offY = height / 2 + center + 1;
			canvas.drawText(text, offX, offY, paint);

			return new BitmapDrawable(context.getResources(), temp);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return null;
    }

    public static Bitmap createBitmapFromDrawable(final Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        Bitmap bitmap = null;
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();

        try {
            Config config = drawable.getOpacity() != PixelFormat.OPAQUE
                    ? Config.ARGB_8888
                    : Config.RGB_565;
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

	public static int calculateInSampleSize(Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * <br>功能简述: 按指定大小从文件解析bitmap
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param filename
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
            int reqWidth, int reqHeight) {
		if (reqWidth == 0 || reqHeight == 0) {
			return null;
		}
        // First decode with inJustDecodeBounds=true to check dimensions
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

	/**
	 * <br>功能简述: 按指定大小从Resources解析bitmap
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param res
	 * @param id
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromRes(Resources res, int id,
            int reqWidth, int reqHeight, Options options) {
		if (reqWidth == 0 || reqHeight == 0 || res == null || id <= 0) {
			return null;
		}
        // First decode with inJustDecodeBounds=true to check dimensions
		if (options == null) {
			options = new Options();
		}
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, id, options);
    }

	public static Bitmap decodeSampledBitmapFromRes(Resources res, int id,
            int reqWidth, int reqHeight) {
		return decodeSampledBitmapFromRes(res, id, reqWidth, reqHeight, null);
	}

	private static Canvas sCanvas = null;
	private static Paint sPaint = null;
	private static Matrix sMatrix = null;
	private static PorterDuffXfermode sXfermode = null; //实现遮罩层 (mask)
	private static PorterDuffXfermode sXfermodeSRC = null; // NDK扩展背景生成马赛卡前，绘制内容到bitmap的模式
	private static Bitmap sTempIcon = null; //实现遮罩层 (mask)

	public static BitmapDrawable composeAppIconDrawable(Context context , Bitmap base, BitmapDrawable cover, BitmapDrawable drawable, BitmapDrawable mask,
			float scale, boolean autoFillBase, boolean roundCenter) {
		Bitmap bit = composeAppIconDrawable(context , base, cover, drawable.getBitmap(), mask, scale, autoFillBase, roundCenter);
		return bit == null ? drawable : new BitmapDrawable(bit);
	}
	/**
	 * 合成图片
	 *
	 * @author huyong
	 * @param base
	 *            ：合成图片底图
	 * @param cover
	 *            ：合成图片罩子
	 * @param drawable
	 *            ： 待合成的源图
	 * @param drawable
	 *            ： 合成图片蒙版
	 * @param scale
	 *            ：缩放比率
	 * @return
	 */
	public static Bitmap composeAppIconDrawable(Context context , Bitmap base, BitmapDrawable cover, Bitmap drawable, BitmapDrawable mask,
			float scale, boolean autoFillBase, boolean roundCenter) {
		if (sCanvas == null || sPaint == null || sMatrix == null || sXfermode == null || sTempIcon == null || sXfermodeSRC == null) {
			sCanvas = new Canvas();
			sCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG));
			sPaint = new Paint();
			sMatrix = new Matrix();
			sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
			sXfermodeSRC = new PorterDuffXfermode(PorterDuff.Mode.SRC);
			int iconSize = context.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
			sTempIcon =  Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
		}
		// 用于判断mask是否只影响图标还是图标与底座都合成,目前只为true
		Bitmap tempBitmap = null;

		if (scale < 0) {
			// scale<0 是特殊情况，为不需要裁剪和底座、遮罩，直接返回缩放后的图即可
			return Utilities.createBitmapThumbnail(drawable, context);
		}
		// 有底图或罩子
		if (autoFillBase) {
			base = sTempIcon;
		}
		if (base == null) {
			if (cover != null && cover.getBitmap() != null) {
				final Config config = cover.getOpacity() != PixelFormat.OPAQUE
						? Config.ARGB_8888
						: Config.RGB_565;
				base = Bitmap.createBitmap(cover.getBitmap().getWidth(), cover.getBitmap()
						.getHeight(), config);
			}

			if (base == null) {
				return drawable;
			}
		}
		int width = base.getWidth();
		int height = base.getHeight();
		float scaleWidth = scale * width; // 缩放后的宽大小
		float scaleHeight = scale * height; // 缩放后的高大小
		final Bitmap midBitmap = drawable;
		float scaleFactorW = 0f; // 缩放后较原图的宽的比例
		float scaleFactorH = 0f; // 缩放后较原图的高的比例
		IconMixtureParamerMaker.IconParamer paramer = new IconMixtureParamerMaker.IconParamer();
		if (scale == 1.0f) {
			paramer = IconMixtureParamerMaker.getIconOffset(paramer, drawable, roundCenter);
			if (!paramer.regular) {
				int paddingTotal = DrawUtils.dip2px(10);
				if (scaleWidth > paddingTotal && scaleHeight > paddingTotal) {
					scaleWidth -= paddingTotal;
					scaleHeight -= paddingTotal;
				}
			}
		}

		if (midBitmap != null) {
			final int realWidth = midBitmap.getWidth();
			final int realHeight = midBitmap.getHeight();
			scaleFactorW = scaleWidth / realWidth;
			scaleFactorH = scaleHeight / realHeight;
		}

		synchronized (sCanvas) {
			final Canvas canvas = sCanvas;
			final Paint paint = sPaint;
			final Matrix matrix = sMatrix;

			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			matrix.reset();
			tempBitmap = Bitmap.createBitmap(base);
			if (paramer.regular) {
				// 规则图形，把底座填充为透明，使用一套缩放比例
				tempBitmap.eraseColor(Color.TRANSPARENT);
				float toScaleSize = (float) drawable.getHeight() / (drawable.getHeight() - paramer.pixel_offset * 2);
				matrix.setScale(scaleFactorW * toScaleSize, scaleFactorH * toScaleSize);
				scaleWidth *= toScaleSize;
				scaleHeight *= toScaleSize;
				matrix.postTranslate((width - scaleWidth) / 2f, (height - scaleHeight) / 2f);
			} else {
				if (autoFillBase) {
					tempBitmap.eraseColor(Color.TRANSPARENT);
				}
				// 不规则图形，绘制底座，使用另外一套缩放比例
				matrix.setScale(scaleFactorW, scaleFactorH);
				matrix.postTranslate((width - scaleWidth) / 2f, (height - scaleHeight) / 2f);
			}
			canvas.setBitmap(tempBitmap);
			canvas.drawBitmap(drawable, matrix, paint);

			if (!paramer.regular && autoFillBase) {
//				Xfermode xfermode = paint.getXfermode();
//				paint.setXfermode(sXfermodeSRC);
//				canvas.setBitmap(sTempIcon);
//				canvas.drawBitmap(tempBitmap, 0, 0, paint);
//				paint.setXfermode(xfermode);
//				int color = ImageUtils.mosaicImage(sTempIcon, 4, null);
//				ImageUtils.mosaicImageDrawBase(sTempIcon, tempBitmap, color, -1, true);
//				canvas.setBitmap(tempBitmap);
			}

			if (cover != null) {
				canvas.drawBitmap(cover.getBitmap(), 0, 0, null);
			}
			// 加上mask蒙版
			if (mask != null) {
				Xfermode xf = paint.getXfermode();
				paint.setXfermode(sXfermode);
				canvas.drawBitmap(mask.getBitmap(), 0, 0, paint);
				paint.setXfermode(xf);
			}
		}
		return tempBitmap;
	}

	/**
	 * 合成图片
	 *
	 * @author liwenxue
	 * @param pictures
	 *            ： 待合成的图片
	 * @param width
	 *            ： 合成图片宽
	 * @param height
	 *            ： 合成图片高
	 * @return 合成过后的图片
	 */
	public static Bitmap composeBitmaps(Bitmap[] pictures, int width, int height) {
		if (pictures != null && pictures.length > 0) {
			// 创建一张新的图片
			Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			if (newBitmap != null) {
				Canvas cv = new Canvas(newBitmap);
				for (int i = 0; i < pictures.length; i ++) {
			        // draw 缩放图片
					Matrix matrix = new Matrix();
					int picWidth = pictures[i].getWidth();
					int picHeight = pictures[i].getHeight();
					matrix.postScale((float) width / picWidth, (float) height / picHeight);
			        cv.drawBitmap(pictures[i], matrix, null);
				}
				// 保存
				cv.save();
		        cv.restore();

		        return newBitmap;
			}
		}
		return null;
	}

	/**
	 * 通过图片id获取bitmap
	 * @param context
	 * @param id 图片资源id
	 * @return
	 */
	public static Bitmap createBitmapWithResId(Context context, int id) {
		Bitmap bmp = null;
		Options opt = new Options();
		opt.inPreferredConfig = Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(id);
		try {
			bmp = BitmapFactory.decodeStream(is, null, opt);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bmp;

	}

	public static final String FBSHARE_FILEPATH_JPG = Environment.getExternalStorageDirectory().getPath() + "/share_image/";
	public static final String FBSHARE_FILENAME_JPG = "fb_share_";
    /**
     * 存储图片
     * @param bitmap 图片数据
     * @param picName 存储名称
     */
    public static Uri savePicToSD(Bitmap bitmap, int picNum) {
        // 照片全路径
        String fileName = "";
        // 文件夹路径
        String pathUrl = FBSHARE_FILEPATH_JPG;
        FileOutputStream fos = null;
        File file = new File(pathUrl);
        file.mkdirs(); // 创建文件夹
        fileName = pathUrl + FBSHARE_FILENAME_JPG + picNum;
        try {
            fos = new FileOutputStream(fileName + ".jpg");
            boolean bOK = bitmap.compress(CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {  
            e.printStackTrace();
        } finally {  
            try {  
                fos.flush();  
                fos.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return Uri.parse("file:///sdcard/share_image/" + FBSHARE_FILENAME_JPG + picNum + ".jpg");
    }
    
    private static final int CONNECT_TIME_OUT = 10000;
	private static final int READ_TIME_OUT = 30000;
	/** 
	 * 下载在线商店主题图片到sd卡
	 * @param context
	 * @param imgUrl　在线图片url
	 * @param name　保存名称
	 */
	public static Uri downloadThemeImgFromUrl2SD(Context context, String imgUrl, int picNum) {
		Bitmap result = null;
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		HttpURLConnection urlCon = null;
		try {
			urlCon = (HttpURLConnection) new URL(imgUrl).openConnection();
			urlCon.setConnectTimeout(CONNECT_TIME_OUT);
			urlCon.setReadTimeout(READ_TIME_OUT);

			inputStream = (InputStream) urlCon.getInputStream();
			result = BitmapFactory.decodeStream(inputStream);
//			outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
			outputStream = new FileOutputStream(FBSHARE_FILEPATH_JPG + FBSHARE_FILENAME_JPG + picNum + ".jpg"); 
			result.compress(CompressFormat.JPEG, 100, outputStream);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		} catch (SocketTimeoutException e) {
			Log.e("dly", "socket timeout");
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("dly", "other e " + e);
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (urlCon != null) {
				urlCon.disconnect();
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return Uri.parse("file:///sdcard/share_image/" + FBSHARE_FILENAME_JPG + picNum + ".jpg");
	}

	public static Bitmap getBitmapFromAsset(Context context, String strName) {
		AssetManager assetManager = context.getAssets();
		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(strName);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
