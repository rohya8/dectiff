package com.rns.tiffeat.mobile.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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
import com.rns.tiffeat.mobile.ScheduledOrderFragment;
import com.rns.tiffeat.mobile.SelectType;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.asynctask.GetMenuAndroidAsyncTask;
import com.rns.tiffeat.mobile.asynctask.ScheduleChangeOrderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;

public class ListOfMealAdapter extends ArrayAdapter<Meal> implements AndroidConstants {

	private FragmentActivity activity;
	private List<Meal> meals;
	private CustomerOrder customerOrder;
	private Map<MealType, Date> availableMealType;
	private Meal meal;
	private int mealposition;

	public class ViewHolder {

		TextView name, mealtype, tiffinused;
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

	public ListOfMealAdapter(FragmentActivity activity, int activityFirstTimeUsedAdapter, List<com.rns.tiffeat.web.bo.domain.Meal> meallist,
			CustomerOrder customerOrder) {

		super(activity, activityFirstTimeUsedAdapter, meallist);
		customerOrder = new CustomerOrder();
		this.customerOrder = customerOrder;
		this.meals = new ArrayList<Meal>();
		this.activity = activity;
		this.meals.addAll(meallist);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), FONT);

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
			holder.tiffinused = (TextView) convertView.findViewById(R.id.list_of_meals_count_textView1);
			holder.menu = (Button) convertView.findViewById(R.id.list_of_meals_button_menu);
			holder.order = (Button) convertView.findViewById(R.id.list_of_meals_button_order);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		meal = meals.get(position);
		holder.name.setText(meal.getTitle().toString());
		holder.mealtype.setText(meal.getDescription());
		holder.tiffinused.setText("" + meal.getPrice());
		mealposition = position;

		holder.order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(activity)) {
					Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
				} else {

					orderMeal(mealposition);
				}
			}

		});

		holder.menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!Validation.isNetworkAvailable(activity)) {
					Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
				} else {

					showMenu();

				}
			}

		});

		return convertView;

	}

	private void orderMeal(int position) {
		if (!Validation.isNetworkAvailable(activity)) {
			Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
		} else {

			Fragment fragment = null;
			if (customerOrder != null && customerOrder.getMealFormat() != null) {
				if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat())) {

					if (customerOrder.getId() == 0) {
						availableMealType.put(customerOrder.getMealType(), customerOrder.getDate());
						fragment = new ScheduledOrderFragment(customerOrder, availableMealType);
						CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), true);
					} else
						new ScheduleChangeOrderTask(activity, customerOrder, meal).execute();
				}
			} else {
				fragment = new SelectType(meal, customerOrder);
				CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), true);
			}

		}
	}

	private void showMenu() {
		Dialog alertDialog = null;
		alertDialog = new Dialog(activity);
		alertDialog.setContentView(R.layout.activity_mealtype);
		alertDialog.setTitle("Select Meal Type");
		alertDialog.setCancelable(true);

		RadioButton lunch = (RadioButton) alertDialog.findViewById(R.id.mealtype_lunch_radioButton);
		RadioButton dinner = (RadioButton) alertDialog.findViewById(R.id.mealtype_dinner_radioButton);

		lunch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(customerOrder==null)
				{
					CustomerOrder custOrder = new CustomerOrder();
					custOrder.setMeal(meal);

					custOrder.setMealType(MealType.LUNCH);

					nextActivity(custOrder);

				}
				else
					nextActivity(customerOrder);

			}


		});

		dinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(customerOrder==null)
				{
					CustomerOrder custOrder = new CustomerOrder();
					custOrder.setMeal(meal);

					custOrder.setMealType(MealType.DINNER);

					nextActivity(custOrder);

				}
				else
					nextActivity(customerOrder);


			}
		});
	}

	private void nextActivity(CustomerOrder custOrder) {
		new GetMenuAndroidAsyncTask(activity, custOrder).execute();
	}
}
