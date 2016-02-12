package com.rns.tiffeat.mobile.adapter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

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
import android.widget.TextView;

import com.rns.tiffeat.mobile.LoginFragment;
import com.rns.tiffeat.mobile.QuickOrderFragment;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.ScheduledOrderFragment;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.asynctask.GetMenuAndroidAsyncTask;
import com.rns.tiffeat.mobile.asynctask.MealImageDownloaderTask;
import com.rns.tiffeat.mobile.asynctask.ScheduleChangeOrderTask;
import com.rns.tiffeat.mobile.asynctask.ScheduledOrderAsyncTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealType;

public class NewListOfMealAdapter extends ArrayAdapter<Meal> implements AndroidConstants {

	private List<Meal> meals;
	private CustomerOrder customerOrder;
	private Meal meal;
	private FragmentActivity activity;

	public class ViewHolder {

		TextView tiffintitle, tiffinprice, vendorname, description;
		ImageView foodimage;
		Button order;

		public ImageView getFoodimage() {
			return foodimage;
		}

		public void setFoodimage(ImageView foodimage) {
			this.foodimage = foodimage;
		}
	}

	public NewListOfMealAdapter(FragmentActivity activity, int activityFirstTimeUsedAdapter, List<com.rns.tiffeat.web.bo.domain.Meal> mealList,
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
			convertView = vi.inflate(R.layout.new_activity_list_of_meals_adapter, null);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.tiffintitle = (TextView) convertView.findViewById(R.id.new_listofmeals_adapter_title_textView);
			holder.vendorname = (TextView) convertView.findViewById(R.id.new_listofmeals__adapter_vendorname_textView);
			holder.foodimage = (ImageView) convertView.findViewById(R.id.new_listofmeals_adapter_food_imageView);
			ImageView mealImageView = (ImageView) convertView.findViewById(R.id.new_listofmeals_adapter_food_imageView);
			holder.foodimage = mealImageView;
			new MealImageDownloaderTask(holder, mealImageView, getContext()).execute(this.getItem(position));
			holder.tiffinprice = (TextView) convertView.findViewById(R.id.new_listofmeals_adapter_tiffinprice_textView);
			holder.description = (TextView) convertView.findViewById(R.id.new_listofmeals_adapter_desc_textView);
			holder.order = (Button) convertView.findViewById(R.id.new_listofmeals_adapter_order_button);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (meal.getTitle().toString() != null)
			holder.tiffintitle.setText(meal.getTitle().toString());
		if (meal.getPrice() != null)
			holder.tiffinprice.setText("Rs. " + meal.getPrice());
		if (meal.getVendor() != null)
			holder.vendorname.setText(meal.getVendor().getName());

		holder.order.setTag(position);

		if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
			if (meal.getDescription() != null) {
				holder.description.setText(meal.getDescription());
			}  
			
		} else if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat())) {
			if (meal.getAvailableFrom() != null) {
				holder.description.setText("Starts from : " + CustomerUtils.convertDate(meal.getAvailableFrom()));
			}
		}
		

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

		return convertView;

	}

	private void orderMeal(int position) {

		if (!Validation.isNetworkAvailable(activity)) {
			Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
		} else {
			customerOrder.setMeal(returnMeal(position));
			if (MealFormat.QUICK.equals(customerOrder.getMealFormat())) {
				nextActivity();
			} else if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat())) {
				if (meals.get(position).getAvailableFrom() != null) {
					customerOrder.setDate(meals.get(position).getAvailableFrom());
					nextActivity();
				} else {
					CustomerUtils.alertbox(TIFFEAT, "Sorry meal is not available for " + customerOrder.getMealType().toString(), activity);
					return;
				}
			}
		}
	}

	private void nextActivity() {
		Fragment fragment = null;

		if (customerOrder.getCustomer() == null) {
			fragment = new LoginFragment(customerOrder);
			CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), true);
			return;
		}
		if (MealFormat.SCHEDULED.equals(customerOrder.getMealFormat())) {
			if (customerOrder.getId() != 0) {
				new ScheduleChangeOrderTask(activity, customerOrder).execute();
			} else if (customerOrder.getId() == 0 && customerOrder.getAddress() != null) {
				new ScheduledOrderAsyncTask(activity, customerOrder).execute();
			} else {
				fragment = new ScheduledOrderFragment(customerOrder);
			}
		} else {
			fragment = new QuickOrderFragment(customerOrder);
		}
		if (fragment != null) {
			CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), true);
		}
	}

	private Meal returnMeal(int position) {
		if (CollectionUtils.isEmpty(meals) || meals.size() <= position) {
			return null;
		}
		return meals.get(position);
	}

}
