package com.rns.tiffeat.mobile.asynctask;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.adapter.PreviousOrderListAdapter.ViewHolder;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.UserUtils;
import com.rns.tiffeat.mobile.util.VendorServerUtils;
import com.rns.tiffeat.web.bo.domain.Meal;

public class PreviousOrderMealImageDownloaderTask extends AsyncTask<Meal, Void, Bitmap> {

	private ImageView imageView;
	private ViewHolder holder;

	public PreviousOrderMealImageDownloaderTask(ViewHolder holder, ImageView mealImageView, Context context) {
		this.holder = holder;
		this.imageView = mealImageView;
	}

	public PreviousOrderMealImageDownloaderTask(ViewHolder holder) {
		this.holder = holder;
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

		Log.d(AndroidConstants.MYTAG, "Downloaded the Image ..");
	}

	private Bitmap setimage(Meal meal) throws MalformedURLException, IOException {

		Bitmap bitmap = null;
		bitmap = UserUtils.getBitmapFromURL(VendorServerUtils.createMealImageUrl(meal));

		return bitmap;
	}

}
