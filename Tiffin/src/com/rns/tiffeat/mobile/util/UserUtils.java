package com.rns.tiffeat.mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rns.tiffeat.mobile.R;

public class UserUtils implements AndroidConstants {
	public static Bitmap getBitmapFromURL(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream openHttpConnection(String urlString) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}

	public static void scaleImage(ImageView view, Context context) {
		Drawable drawing = view.getDrawable();
		if (drawing == null) {
			return;
		}
		Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int bounding = dpToPx(250, context);

		float xScale = ((float) bounding) / width;
		float yScale = ((float) bounding) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		width = scaledBitmap.getWidth(); // re-use
		height = scaledBitmap.getHeight(); // re-use
		BitmapDrawable result = new BitmapDrawable(scaledBitmap);

		view.setImageDrawable(result);

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);

	}

	private static int dpToPx(int dp, Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	public static void scaleImage(ImageView mImageView, Bitmap bitmap) {
		int intendedWidth = mImageView.getWidth();

		if (bitmap == null)
			return;

		int originalWidth = bitmap.getWidth();
		int originalHeight = bitmap.getHeight();

		float scale = (float) intendedWidth / originalWidth;
		int newHeight = (int) Math.round(originalHeight * scale);

		mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		mImageView.getLayoutParams().width = intendedWidth;
		mImageView.getLayoutParams().height = newHeight;
	}

	public static ProgressDialog showLoadingDialog(Context myactivity, CharSequence title, CharSequence text) {
		ProgressDialog progressDialog = new ProgressDialog(myactivity);
		progressDialog.setIndeterminate(true);
		progressDialog.setIndeterminateDrawable(myactivity.getResources().getDrawable(R.anim.progress_dialog_anim));
		progressDialog.setTitle(title);
		progressDialog.setMessage(text);
		progressDialog.setCancelable(false);
		progressDialog.show();
		return progressDialog;
	}

}
