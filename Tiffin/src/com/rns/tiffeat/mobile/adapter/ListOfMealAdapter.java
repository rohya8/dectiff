package com.rns.tiffeat.mobile.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.Meal;

public class ListOfMealAdapter extends ArrayAdapter<Meal> implements AndroidConstants {

	private FragmentActivity activity;
	private List<Meal> meals;

	public class ViewHolder {

		TextView name, mealtype, tiffinused;
		ImageView foodimage;
		RatingBar ratingbar;

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

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Meal meal = meals.get(position);
		holder.name.setText(meal.getTitle().toString());
		holder.mealtype.setText(meal.getDescription());
		holder.tiffinused.setText("" + meal.getPrice());

		return convertView;

	}

}
