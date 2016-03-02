package com.rns.tiffeat.mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.rns.tiffeat.mobile.adapter.NewListOfMealAdapter.ViewHolder;
import com.rns.tiffeat.mobile.asynctask.MealImageDownloaderTask;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.mobile.util.VendorServerUtils;
import com.rns.tiffeat.web.bo.domain.Meal;

public class ImageCache {
	private static LruCache<String, Bitmap> mMemoryCache = null;
	private static int cacheSize = 1024 * 1024 * 10;
	private static ImageView imageView;
	private static ViewHolder holder;
	private static Context context;
	private static Meal meal;

	public void setImageView(ImageView imageView2) {
		imageView = imageView2;
	}

	public static void loadToView(String url) {

		if (url == null || url.length() == 0)
			return;

		if (mMemoryCache == null) {

			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return (bitmap.getRowBytes() * bitmap.getHeight());
				}
			};
		}

		Bitmap bitmap = getBitmapFromMemCache(url);

		if (bitmap == null) {
			new MealImageDownloaderTask(holder, imageView, context,mMemoryCache).execute(meal);
		} else {
			UserUtils.scaleImage(imageView, bitmap);
			imageView.setImageBitmap(bitmap);
			holder.setFoodimage(imageView);
		}
	}

	public static Bitmap getBitmapFromMemCache(String url) {
		return (Bitmap) mMemoryCache.get(url);
	}

	public static void imageData(ViewHolder holder2, ImageView foodimage, Context context2, Meal meal2) {
		holder = holder2;
		imageView = foodimage;
		context = context2;
		meal = meal2;

		String url = VendorServerUtils.createMealImageUrl(meal);
		loadToView(url);
	}
}
