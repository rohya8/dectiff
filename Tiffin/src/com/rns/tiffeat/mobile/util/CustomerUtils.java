package com.rns.tiffeat.mobile.util;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.MealType;

public class CustomerUtils implements AndroidConstants {

	public static void storeCurrentCustomer(Context context, Customer customer) {
		SharedPreferences prefs = context.getSharedPreferences(CUSTOMER_SHARED_CONTEXT, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(CUSTOMER_OBJECT, new Gson().toJson(customer));
		// editor.putBoolean(LOGGED_IN, true);
		editor.commit();
		Log.d(MYTAG, "Saved the Customer!!");
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

	public static void clearFragmentStack(FragmentManager fragmentManager) {

		// for(int i = 0; i < fragmentManager.getBackStackEntryCount()-1; ++i) {
		// fragmentManager.popBackStack();
		fragmentManager.popBackStack(null, fragmentManager.POP_BACK_STACK_INCLUSIVE);
		// }

	}

	public static void nextFragment(Fragment fragment, FragmentManager fragmentManager, boolean addToStack) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (addToStack)
			fragmentTransaction.addToBackStack(fragment.getClass().getName()).replace(R.id.container_body, fragment);
		else {
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.container_body, fragment);
		}
		fragmentTransaction.commit();
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

	public static String convertDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}

}
