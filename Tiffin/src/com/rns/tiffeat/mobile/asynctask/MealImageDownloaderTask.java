package com.rns.tiffeat.mobile.asynctask;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.adapter.NewListOfMealAdapter.ViewHolder;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.mobile.util.VendorServerUtils;
import com.rns.tiffeat.web.bo.domain.Meal;

public class MealImageDownloaderTask extends AsyncTask<Meal, Void, Bitmap> {

	private ImageView imageView;
	private ViewHolder holder;
	private LruCache<String, Bitmap> mMemoryCache;

	public MealImageDownloaderTask(ViewHolder holder, ImageView mealImageView, Context context, LruCache<String, Bitmap> mMemoryCache) {
		this.holder = holder;
		this.imageView = mealImageView;
		this.mMemoryCache = mMemoryCache;
	}

	public MealImageDownloaderTask(ViewHolder holder2, ImageView foodimage, Context context) {
		this.holder = holder;
		this.imageView = foodimage;

	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(Meal... arg) {

		Bitmap bitmap = null;
		try {
			bitmap = setimage(arg[0]);
		} catch (IOException e) {
			CustomerUtils.exceptionOccurred(e.getMessage(), getClass().getSimpleName());
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);

		if (result == null)
			imageView.setImageResource(R.drawable.defaultimage);
		else {
			imageView.setImageBitmap(result);
			UserUtils.scaleImage(imageView, result);
		}
		holder.setFoodimage(imageView);
	}

	private Bitmap setimage(Meal meal) throws MalformedURLException, IOException {

		Bitmap bitmap = null;
		String url = VendorServerUtils.createMealImageUrl(meal);

		bitmap = UserUtils.getBitmapFromURL(url);
		/*
		 * if (bitmap != null) { if
		 * (mMemoryCache.get(String.valueOf(meal.getId())) == null) {
		 * mMemoryCache.put(String.valueOf(meal.getId()), bitmap); } }
		 */
		return bitmap;
	}

}
