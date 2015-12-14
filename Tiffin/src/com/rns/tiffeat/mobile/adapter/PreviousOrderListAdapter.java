package com.rns.tiffeat.mobile.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.QuickOrderHomeScreen;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.SelectType;
import com.rns.tiffeat.mobile.asynctask.PreviousOrderMealImageDownloaderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class PreviousOrderListAdapter extends ArrayAdapter<CustomerOrder> implements AndroidConstants {
	private FragmentActivity activity;
	private List<CustomerOrder> previousOrders;
	private Customer customer;
	private CustomerOrder customerOrder;
	private ViewHolder holder;
	private QuickOrderHomeScreen quickHome;

	public void setQuickHome(QuickOrderHomeScreen quickHome) {
		this.quickHome = quickHome;
	}

	public ViewHolder getHolder() {
		return holder;
	}

	public class ViewHolder {
		TextView title, tiffintype, price, date;
		ImageView foodimage;
		TextView repeatorderButton;
		boolean viewMenuClicked;

		public void setViewMenuClicked(boolean viewMenuClicked) {
			this.viewMenuClicked = viewMenuClicked;
		}

		public boolean isViewMenuClicked() {
			return viewMenuClicked;
		}

		public ImageView getFoodimage() {
			return foodimage;
		}

		public void setFoodimage(ImageView foodimage) {
			this.foodimage = foodimage;
		}

	}

	public PreviousOrderListAdapter(FragmentActivity activity, int activityQuickorderListAdapter, List<CustomerOrder> previousOrders, Customer customer) {
		super(activity, activityQuickorderListAdapter, previousOrders);
		this.previousOrders = new ArrayList<CustomerOrder>();
		this.activity = activity;
		this.customer = customer;
		this.previousOrders.addAll(previousOrders);
		Collections.reverse(this.previousOrders);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), FONT);

		customerOrder = previousOrders.get(position);

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.activity_previousorder_list_adapter, null);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.title = (TextView) convertView.findViewById(R.id.previousorder_list_adapter_name_textView);
			holder.tiffintype = (TextView) convertView.findViewById(R.id.previousorder_list_adapter_type_textView);
			holder.date = (TextView) convertView.findViewById(R.id.previousorder_list_adapter_date_textView);
			ImageView mealImageView = (ImageView) convertView.findViewById(R.id.previousorder_list_adapter_imageview);
			holder.foodimage = mealImageView;
			new PreviousOrderMealImageDownloaderTask(holder, mealImageView, getContext()).execute(customerOrder.getMeal());
			holder.repeatorderButton = (TextView) convertView.findViewById(R.id.previousorder_list_adapter_repeatorder_button);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.repeatorderButton.setTag(position);

		holder.title.setText(customerOrder.getMeal().getTitle());
		holder.tiffintype.setText(customerOrder.getMealType().toString());

		holder.date.setText(CustomerUtils.convertDate(customerOrder.getDate()));

		if (holder.foodimage != null) {
		}

		holder.repeatorderButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				customerOrder = previousOrders.get(pos);

				customerOrder.setCustomer(customer);

				repeatActivity(customerOrder);

			}
		});

		return convertView;

	}

	private void repeatActivity(CustomerOrder customerOrder2) {

		Fragment fragment = null;
		fragment = new SelectType(customerOrder2);

		CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), false);
	}
}