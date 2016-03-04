package com.rns.tiffeat.mobile.util;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rns.tiffeat.mobile.DrawerActivity;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealType;

public class CustomerUtils implements AndroidConstants {

	public static void storeCurrentCustomer(Context context, Customer customer) {
		SharedPreferences prefs = context.getSharedPreferences(CUSTOMER_SHARED_CONTEXT, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(CUSTOMER_OBJECT, new Gson().toJson(customer));
		editor.commit();
		Log.d(MYTAG, "Saved the Customer!!");
	}

	public static void exceptionOccurred(String e, String classname) {
		Log.d(MYTAG, "Exception occurred in " + classname + "  : " + e);
	}

	public static Customer getCurrentCustomer(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(CUSTOMER_SHARED_CONTEXT, Context.MODE_PRIVATE);
		String customerJson = prefs.getString(CUSTOMER_OBJECT, null);
		Customer customer = new Customer();
		if (customerJson != null) {
			customer = new Gson().fromJson(customerJson, Customer.class);
			Log.d(MYTAG, "Got the Customer!! " + customer);
		}
		return customer;
	}

	public static void logout(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(CUSTOMER_SHARED_CONTEXT, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(CUSTOMER_OBJECT, null);
		editor.commit();
		Log.d(MYTAG, "Logged Out!");
	}

	@SuppressWarnings("static-access")
	public static void clearFragmentStack(FragmentManager fragmentManager) {

		// for(int i = 0; i < fragmentManager.getBackStackEntryCount()-1; ++i) {
		// fragmentManager.popBackStack();
		fragmentManager.popBackStack(null, fragmentManager.POP_BACK_STACK_INCLUSIVE);
		// }

	}

	public static void removeFragment(FragmentManager fragmentManager, Fragment fragment) {
		FragmentTransaction trans = fragmentManager.beginTransaction();
		trans.remove(fragment);
		trans.commit();
		fragmentManager.popBackStack();
	}

	public static void nextFragment(Fragment fragment, FragmentManager fragmentManager, boolean addToStack) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (addToStack)
			fragmentTransaction.addToBackStack(fragment.getClass().getName()).replace(R.id.container_body, fragment);
		else {
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.container_body, fragment);
		}
		fragmentTransaction.commitAllowingStateLoss();
	}

	public static void changeFont(AssetManager assets, Fragment fragment) {
		FontChangeCrawler fontChanger = new FontChangeCrawler(assets, FONT);
		fontChanger.replaceFonts((ViewGroup) fragment.getView());
	}

	public static Map<String, Object> convertToStringObjectMap(String mapJson) {
		Type typeMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> map = new HashMap<String, Object>();
		map = new Gson().fromJson(mapJson, typeMap);
		return map;
	}

	public static Map<MealType, Date> convertToMealTypeDateMap(String mapJson) {
		Type typeMap = new TypeToken<Map<MealType, Date>>() {
		}.getType();
		Map<MealType, Date> map = new HashMap<MealType, Date>();
		map = new Gson().fromJson(mapJson, typeMap);
		return map;
	}

	@SuppressLint("SimpleDateFormat")
	public static String convertDate(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}

	}

	public static void alertbox(String title, String message, final Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		builder.show();

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 8;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static void startDrawerActivity(Activity activity, CustomerOrder customerOrder, Customer customer, String action) {
		Intent i = new Intent(activity, DrawerActivity.class);
		i.putExtra(ACTION, action);
		if (customerOrder != null) {
			i.putExtra(CUSTOMER_ORDER_OBJECT, new Gson().toJson(customerOrder));
		}
		if (customer != null) {
			i.putExtra(AndroidConstants.CUSTOMER_OBJECT, new Gson().toJson(customerOrder.getCustomer()));
		}
		activity.startActivity(i);
		activity.finish();
	}

	public static String imeino(TelephonyManager tm) {

		String IMEI = tm.getDeviceId().toString();
		if (IMEI != null)
			return IMEI;
		else
			return "No Device ID Found";
	}

}
