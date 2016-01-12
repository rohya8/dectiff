package com.rns.tiffeat.mobile.adapter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.SelectType;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.asynctask.ExistingUserAsyncTask;
import com.rns.tiffeat.mobile.asynctask.GetMenuAndroidAsyncTask;
import com.rns.tiffeat.mobile.asynctask.MealImageDownloaderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealType;

public class ListOfMealAdapter extends ArrayAdapter<Meal> implements AndroidConstants {

	private FragmentActivity activity;
	private List<Meal> meals;
	private CustomerOrder customerOrder;
	private Meal meal;
	private Dialog alertDialog = null;

	public class ViewHolder {

		TextView name, mealtype, tiffinprice, tiffinavailability;
		ImageView foodimage;
		RatingBar ratingbar;
		Button menu, order;

		public ImageView getFoodimage() {
			return foodimage;
		}

		public void setFoodimage(ImageView foodimage) {
			this.foodimage = foodimage;
		}
	}

	public ListOfMealAdapter(FragmentActivity activity, int activityFirstTimeUsedAdapter, List<com.rns.tiffeat.web.bo.domain.Meal> mealList,
			CustomerOrder customerOrder) {

		super(activity, activityFirstTimeUsedAdapter, mealList);
		this.customerOrder = customerOrder;
		this.activity = activity;
		this.meals = mealList;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), FONT);

		meal = meals.get(position);

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.activity_list_of_meals_adapter, null);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.name = (TextView) convertView.findViewById(R.id.list_of_meals_veg_tiffin_textView);
			holder.mealtype = (TextView) convertView.findViewById(R.id.list_of_meals_veg_tiffin_meal_textView);
			holder.foodimage = (ImageView) convertView.findViewById(R.id.list_of_meals_food1_imageView);
			ImageView mealImageView = (ImageView) convertView.findViewById(R.id.list_of_meals_food1_imageView);
			holder.foodimage = mealImageView;
			new MealImageDownloaderTask(holder, mealImageView, getContext()).execute(this.getItem(position));
			holder.tiffinprice = (TextView) convertView.findViewById(R.id.list_of_meals_count_textView1);
			holder.menu = (Button) convertView.findViewById(R.id.list_of_meals_button_menu);
			holder.order = (Button) convertView.findViewById(R.id.list_of_meals_button_order);
			holder.tiffinavailability = (TextView) convertView.findViewById(R.id.list_of_meals_availability_textView);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(meal.getTitle().toString()!=null)
			holder.name.setText(meal.getTitle().toString());
		if(meal.getDescription()!=null)
			holder.mealtype.setText(meal.getDescription());
		if(meal.getPrice()!=null)
			holder.tiffinprice.setText("Rs. " + meal.getPrice());
		if(meal.getMealTime().getDescription()!=null)
			holder.tiffinavailability.setText("Available for : " + meal.getMealTime().getDescription());
		
		holder.order.setTag(position);
		holder.menu.setTag(position);

		holder.order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(activity)) {
					Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
				} else {
					int pos = (Integer) v.getTag();
					orderMeal(pos);
				}
			}

		});

		holder.menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(activity)) {
					Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
				} else {
					int pos = (Integer) v.getTag();
					showMenu(pos);

				}
			}

		});

		return convertView;

	}

	private void orderMeal(int position) {

		if (!Validation.isNetworkAvailable(activity)) {
			Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
		} else {
			customerOrder.setMeal(returnMeal(position));

			if (customerOrder != null && customerOrder.getMealFormat() != null) {
				new ExistingUserAsyncTask(activity, customerOrder).execute();
				return;
			}
			selectTypeOfMeal();

		}
	}

	private void selectTypeOfMeal() {
		Fragment fragment;
		fragment = new SelectType(customerOrder);
		CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), true);
	}

	private Meal returnMeal(int position) {
		if (CollectionUtils.isEmpty(meals) || meals.size() <= position) {
			return null;
		}
		return meals.get(position);
	}

	private void showMenu(final int position) {

		alertDialog = new Dialog(activity);
		alertDialog.setContentView(R.layout.activity_mealtype);
		alertDialog.setTitle("Select Meal Type");
		alertDialog.setCancelable(true);

		final RadioButton lunch = (RadioButton) alertDialog.findViewById(R.id.mealtype_lunch_radioButton);
		final RadioButton dinner = (RadioButton) alertDialog.findViewById(R.id.mealtype_dinner_radioButton);

		lunch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				lunch.setChecked(false);
				alertDialog.dismiss();
				viewMealMenu(position, MealType.LUNCH);
			}

		});

		dinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dinner.setChecked(false);
				alertDialog.dismiss();
				viewMealMenu(position, MealType.DINNER);
			}
		});

		alertDialog.show();
	}

	private void viewMealMenu(final int position, MealType mealType) {
		if (customerOrder == null) {
			customerOrder = new CustomerOrder();
		}
		customerOrder.setMeal(returnMeal(position));
		customerOrder.setMealType(mealType);
		nextActivity(customerOrder);
	}

	private void nextActivity(CustomerOrder custOrder) {
		new GetMenuAndroidAsyncTask(activity, custOrder).execute();
	}
}
