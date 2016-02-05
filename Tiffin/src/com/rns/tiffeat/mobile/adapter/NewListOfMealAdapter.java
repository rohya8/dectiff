package com.rns.tiffeat.mobile.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;

public class NewListOfMealAdapter extends BaseAdapter implements AndroidConstants
{

	private ArrayList<String> object;

	private FragmentActivity activity;
	
	//private List<Meal> meals;
	
	// private CustomerOrder customerOrder;
	// private Meal meal;
	//private Dialog alertDialog = null;

	public class ViewHolder {

		TextView name, tiffinprice;
		ImageView foodimage;
		Button order;

		/*public ImageView getFoodimage() {
			return foodimage;
		}

		public void setFoodimage(ImageView foodimage) {
			this.foodimage = foodimage;
		}
*/	}

	public NewListOfMealAdapter(FragmentActivity activity2, int newActivityListOfMealsAdapter, ArrayList<String> object2) {
	this.object=object2;
	activity=activity2;
	}

	/*
	 * public NewListOfMealAdapter(FragmentActivity activity, int
	 * activityFirstTimeUsedAdapter, List<com.rns.tiffeat.web.bo.domain.Meal>
	 * mealList, CustomerOrder customerOrder) {
	 * 
	 * super(activity, activityFirstTimeUsedAdapter, mealList);
	 * this.customerOrder = customerOrder; this.activity = activity; this.meals
	 * = mealList;
	 * 
	 * }
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), FONT);

		// meal = meals.get(position);

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.new_activity_list_of_meals_adapter, null);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.name = (TextView) convertView.findViewById(R.id.list_of_meals_veg_tiffin_textView);
			// holder.mealtype = (TextView)
			// convertView.findViewById(R.id.list_of_meals_veg_tiffin_meal_textView);
			holder.foodimage = (ImageView) convertView.findViewById(R.id.list_of_meals_food1_imageView);
			ImageView mealImageView = (ImageView) convertView.findViewById(R.id.list_of_meals_food1_imageView);
			holder.foodimage = mealImageView;
			// new MealImageDownloaderTask(holder, mealImageView,
			// getContext()).execute(this.getItem(position));
			holder.tiffinprice = (TextView) convertView.findViewById(R.id.list_of_meals_count_textView1);
			// holder.menu = (Button)
			// convertView.findViewById(R.id.list_of_meals_button_menu);
			holder.order = (Button) convertView.findViewById(R.id.list_of_meals_button_order);
			// holder.tiffinavailability = (TextView)
			// convertView.findViewById(R.id.list_of_meals_availability_textView);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// if(meal.getTitle().toString()!=null)
		// holder.name.setText(meal.getTitle().toString());
		// if(meal.getDescription()!=null)
		// holder.mealtype.setText(meal.getDescription());
		// if(meal.getPrice()!=null)
		// holder.tiffinprice.setText("Rs. " + meal.getPrice());
		// if(meal.getMealTime().getDescription()!=null)
		// holder.tiffinavailability.setText("Available for : " +
		// meal.getMealTime().getDescription());
		//
		// holder.order.setTag(position);
		// holder.menu.setTag(position);
		//
		// holder.order.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (!Validation.isNetworkAvailable(activity)) {
		// Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
		// } else {
		// int pos = (Integer) v.getTag();
		// orderMeal(pos);
		// }
		// }
		//
		// });
		//
		// holder.menu.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (!Validation.isNetworkAvailable(activity)) {
		// Validation.showError(activity, ERROR_NO_INTERNET_CONNECTION);
		// } else {
		// int pos = (Integer) v.getTag();
		// showMenu(pos);
		//
		// }
		// }
		//
		// });

		return convertView;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return object.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*private void orderMeal(int position) {

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
*/

/*//	}
//
//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	}
//
//	public View getView(int position, View convertView, ViewGroup parent) 
//
//	{
//
//		ViewHolder holder;
//		View view = convertView;
//
//		if(view == null)
//		{
//			holder = new ViewHolder();
//			view = inflater.inflate(R.layout.activity_student_department_notification_list_adapter, null);
//			holder.name_tv = (TextView)view.findViewById(R.id.studentdepartmentnotificationlist_adapter_textView);
//			//holder.iv=(ImageView)view.findViewById(R.id.departmentnotify_customimageView);
//			holder.notify_no_tv=(TextView)view.findViewById(R.id.studentdepartmentnotificationlist_adapter_notificationno_textView);
//			view.setTag(holder);
//		}
//		else
//			holder = (ViewHolder)view.getTag();
//
//		holder.name_tv.setText(name.get(position));
//		//holder.iv.setImageResource(image.get(position));
//		holder.notify_no_tv.setText(notify_no.get(position));
//		// TODO Auto-generated method stub
//		return view;
//
//	}	
//
*/}
