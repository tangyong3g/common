package com.sny.tangyong.common.graphic;

import android.graphics.Bitmap;
import android.graphics.Color;


/**
 * 图标合成参数的计算器
 *
 */
public class IconMixtureParamerMaker {
	static final float START_OFFSET = 0.2f; // 有效像素距离位图四边的最大距离，相对于整张位图的比例
	static final float EXTRA_PADDING = 0.00f; // 裁剪时增加的额外裁剪像素，相对于增长位图的比例
	static final float PIXEL_COUNT = 0.5f; // 每条边被判断为规则的有效像素比例
	// TODO 这里要处理
	static final int OUT_STAND_PIXEL_OFFSET = 1; // 边界是否有突出像素的检查偏移值
	static final int OUT_STAND_PIXEL_COUNT = 1; // 边界是否有突出像素的检查偏移值
	
	// 对边和邻边的最大差、相对于整个图标的宽度的比值
	static final float DELTA_SIDE = 0.04f; // 对边
	static final float DELTA_DSIDE = 0.04f; // 邻边
	
	static final int[] END_LEFT = new int[2];
	static final int[] END_TOP = new int[2];
	static final int[] END_BOTTOM = new int[2];
	static final int[] END_RIGHT = new int[2];
	
	// 计算图标内容范围区域的入口
	public static final IconParamer getIconOffset(IconParamer paramer, Bitmap bitmap, boolean roundCenter) {
		int centerX = bitmap.getWidth() / 2;
		int centerY = bitmap.getHeight() / 2;
		// 检测内容与图片上边界的距离
		int end = (int) (bitmap.getHeight() * START_OFFSET);
		int index = -1;
		for (int i = 0; i < end; i++) {
			int pixel = bitmap.getPixel(centerX, i);
			if (isValidPixelWithouAlpha(pixel)) {
				index = i;
				break;
			}
		}
		boolean topFound = checkRow(bitmap, index, true);
		if (!topFound) {
			return paramer;
		}
		int offsetTop = index;
		
		// 检测内容与图片下边界的距离
		end = bitmap.getHeight() - (int) (bitmap.getHeight() * START_OFFSET);
		index = -1;
		for (int i = bitmap.getHeight() - 1; i >= end; i--) {
			int pixel = bitmap.getPixel(centerX, i);
			if (isValidPixelWithouAlpha(pixel)) {
				index = i;
				break;
			}
		}
		boolean bottomFound = checkRow(bitmap, index, false);
		if (!bottomFound) {
			return paramer;
		}
		int offsetBottom = bitmap.getHeight() - index;
		
		// 检测内容与图片左边界的距离
		end = (int) (bitmap.getWidth() * START_OFFSET);
		index = -1;
		for (int i = 0; i <= end; i++) {
			int pixel = bitmap.getPixel(i, centerY);
			if (isValidPixelWithouAlpha(pixel)) {
				index = i;
				break;
			}
		}
		boolean leftFound = checkColumn(bitmap, index, true);
		if (!leftFound) {
			return paramer;
		}
		int offsetLeft = index;
		
		// 检测内容与图片右边界的距离
		end = bitmap.getWidth() - (int) (bitmap.getWidth() * START_OFFSET);
		index = -1;
		for (int i = bitmap.getWidth() - 1; i >= end; i--) {
			int pixel = bitmap.getPixel(i, centerY);
			if (isValidPixelWithouAlpha(pixel)) {
				index = i;
				break;
			}
		}
		boolean rightFound = checkColumn(bitmap, index, false);
		if (!rightFound) {
			return paramer;
		}
		int offsetRight = bitmap.getWidth() - index;

		
		int sideMaxDelta = (int) (bitmap.getWidth() * DELTA_SIDE);
		int dSideMaxDelta = (int) (bitmap.getWidth() * DELTA_DSIDE);
		// 检查4条边的差值是否过大，避免一些类长方形，或者偏向某个角的图标也被判断为规则图形
		if (Math.abs(offsetTop - offsetBottom) > sideMaxDelta 
			 || Math.abs(offsetLeft - offsetRight) > sideMaxDelta
			 || Math.abs(offsetTop - offsetLeft) > dSideMaxDelta
			 || Math.abs(offsetBottom - offsetLeft) > dSideMaxDelta
			 || Math.abs(offsetTop - offsetRight) > dSideMaxDelta
			 || Math.abs(offsetBottom - offsetRight) > dSideMaxDelta) {
			return paramer;
		}
		
		paramer.regular = true;

		if (!roundCenter) {
			paramer.pixel_offset = Math.max(Math.max(offsetTop, offsetBottom), Math.max(offsetLeft, offsetRight));
			return paramer;
		}
		
		// 圆角大小的判断，裁剪范围为圆角的中心点
		int rTop = Math.max(END_LEFT[0], END_RIGHT[0]) - offsetTop;
		int rBottom = Math.max(bitmap.getHeight() - END_LEFT[1], bitmap.getHeight() - END_RIGHT[1]) - offsetBottom;
		int rLeft =  Math.max(END_TOP[0], END_BOTTOM[0]) - offsetLeft;
		int rRight = Math.max(bitmap.getWidth() - END_TOP[1], bitmap.getWidth() - END_BOTTOM[1]) - offsetRight;
		
		// 圆弧的中点
		offsetTop += getCenterOffset(rTop);
		offsetBottom += getCenterOffset(rBottom);
		offsetLeft += getCenterOffset(rLeft);
		offsetRight += getCenterOffset(rRight);
		
		// 得出最终的裁剪的范围
		paramer.pixel_offset =  Math.min(Math.min(offsetTop, offsetBottom), Math.min(offsetLeft, offsetRight));
		return paramer;
	}
	
	private static int getCenterOffset(int r) {
		if (r <= 0) {
			return 0;
		}
		return (int) (r - Math.sqrt(r * r / 2));
	}
	
	/**
	 * 检测点往上下扩散，求边长
	 * @param bitmap
	 * @param column
	 * @param left
	 * @return
	 */
	private static boolean checkColumn(Bitmap bitmap, int column, boolean left) {
		if (column < 0) {
			return false;
		}
		int[] tempIndex = left ? END_LEFT : END_RIGHT;
		int center = bitmap.getHeight() / 2;
		// 寻找最上边点
		for (int i = center; i >= 0; i--) {
			int pixel = bitmap.getPixel(column, i);
			if (isValidPixel(pixel)) {
				tempIndex[0] = i;
			} else {
				break;
			}
		}

		// 寻找最下边的点
		for (int i = center; i < bitmap.getHeight(); i++) {
			int pixel = bitmap.getPixel(column, i);
			if (isValidPixel(pixel)) {
				tempIndex[1] = i;
			} else {
				break;
			}
		}
		// 最上边的点和最下边点必须相距 PIXEL_COUNT * h 以上，否则视为非规则图形
		boolean validRow = tempIndex[1] - tempIndex[0] >= PIXEL_COUNT * bitmap.getHeight();
		if (validRow) {
			validRow = checkOutstandCol(bitmap, left ? column - OUT_STAND_PIXEL_OFFSET : column + OUT_STAND_PIXEL_OFFSET);
		}
		return validRow;
	}
	
	/**
	 * 检测点往左右扩散，求边长
	 * @param bitmap
	 * @param row
	 * @param top
	 * @return
	 */
	private static boolean checkRow(Bitmap bitmap, int row, boolean top) {
		if (row < 0) {
			return false;
		}
		int center = bitmap.getWidth() / 2;
		int[] tempIndex = top ? END_TOP : END_BOTTOM;
		// 寻找最左边点
		for (int i = center; i >= 0; i--) {
			int pixel = bitmap.getPixel(i, row);
			if (isValidPixel(pixel)) {
				tempIndex[0] = i;
			} else {
				break;
			}
		}
		
		// 寻找最右边的点
		for (int i = center; i < bitmap.getWidth(); i++) {
			int pixel = bitmap.getPixel(i, row);
			if (isValidPixel(pixel)) {
				tempIndex[1] = i;
			} else {
				break;
			}
		}
		// 最左边的点和最右边点必须相距 PIXEL_COUNT * w以上，否则视为非规则图形
		boolean validRow = tempIndex[1] - tempIndex[0] >= PIXEL_COUNT * bitmap.getWidth();
		if (validRow) {
			validRow = checkOutstandRow(bitmap, top ? row - OUT_STAND_PIXEL_OFFSET : row + OUT_STAND_PIXEL_OFFSET);
		}
		return validRow;
	}
	
	private static final boolean isValidPixel(int pixel) {
		return pixel != Color.TRANSPARENT;
	}
	
	private static final boolean isValidPixelWithouAlpha(int pixel) {
		return (pixel >>> 24) == 255 ? true : false;
	}
	
	private static final boolean checkOutstandRow(Bitmap bitmap, int row) {
		if (row < 0 || row >= bitmap.getHeight()) {
			return true;
		}
		
		int validPixel = 0;
		for (int i = 0; i < bitmap.getWidth(); i++) {
			int pixel = bitmap.getPixel(i, row);
			if (isValidPixelWithouAlpha(pixel)) {
				validPixel++;
				if (validPixel >= OUT_STAND_PIXEL_COUNT) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 检测点附近是否有突出边界的内容
	 * @param bitmap
	 * @param col
	 * @return
	 */
	private static final boolean checkOutstandCol(Bitmap bitmap, int col) {
		if (col < 0 || col >= bitmap.getWidth()) {
			return true;
		}
		
		int validPixel = 0;
		for (int i = 0; i < bitmap.getHeight(); i++) {
			int pixel = bitmap.getPixel(col, i);
			if (isValidPixelWithouAlpha(pixel)) {
				validPixel++;
				if (validPixel >= OUT_STAND_PIXEL_COUNT) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 结果参数
	 * @author panguowei
	 *
	 */
	public static class IconParamer {
		public boolean regular = false;
//		public boolean need_mask = true;
		public int pixel_offset = 0;
	}
}
