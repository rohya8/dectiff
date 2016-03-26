package com.rns.tiffeat.mobile.adapter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.rns.tiffeat.mobile.QuickOrderHomeScreen;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.asynctask.GetMealMenuAsyncTask;
import com.rns.tiffeat.mobile.asynctask.MealRatingAsyncTask;
import com.rns.tiffeat.mobile.asynctask.QuickOrderMealImageDownloaderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealStatus;
import com.rns.tiffeat.web.bo.domain.OrderStatus;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class QuickOrderListAdapter extends ArrayAdapter<CustomerOrder> implements AndroidConstants {
	private FragmentActivity activity;
	private List<CustomerOrder> quickOrders;

	private Customer customer;
	private CustomerOrder customerOrder;
	private ViewHolder holder;

	public List<CustomerOrder> getQuickOrders() {
		return quickOrders;
	}

	public void setQuickOrders(List<CustomerOrder> quickOrders) {
		this.quickOrders = quickOrders;
	}

	public void setQuickHome(QuickOrderHomeScreen quickHome) {
	}

	public ViewHolder getHolder() {
		return holder;
	}

	@Override
	public int getCount() {
		return quickOrders.size();
	}

	public class ViewHolder {
		TextView title, tiffintype, mealStatus, date, orderStatus, quantity, price, rate_us;
		ImageView foodimage;
		TextView viewmenuButton;

		public ImageView getFoodimage() {
			return foodimage;
		}

		public void setFoodimage(ImageView foodimage) {
			this.foodimage = foodimage;
		}

	}

	public QuickOrderListAdapter(FragmentActivity activity, int activityQuickorderListAdapter,
			List<CustomerOrder> quickOrders, Customer customer) {
		super(activity, activityQuickorderListAdapter, quickOrders);
		this.quickOrders = quickOrders;
		this.activity = activity;
		this.customer = customer;
		Collections.reverse(this.quickOrders);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), FONT);
		customerOrder = quickOrders.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.activity_quickorder_list_adapter, parent, false);
			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.title = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_name_textView);
			holder.tiffintype = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_type_textView);
			holder.date = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_date_textView);
			holder.price = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_price_textView);
			holder.quantity = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_quantity_textView);
			holder.foodimage = (ImageView) convertView.findViewById(R.id.quickorder_list_adapter_imageview);
			holder.rate_us = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_rate_us_textview);
			holder.mealStatus = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_meal_status_textView);
			holder.orderStatus = (TextView) convertView
					.findViewById(R.id.quickorder_list_adapter_order_status_textView);
			holder.viewmenuButton = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_viewmenu_button);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		new QuickOrderMealImageDownloaderTask(holder, holder.foodimage, getContext()).execute(customerOrder.getMeal());
		holder.orderStatus.setTag(position);
		holder.viewmenuButton.setTag(position);

		if (customerOrder == null) {
			return convertView;
		}

		if (customerOrder.getMeal().getTitle() != null)
			holder.title.setText(customerOrder.getMeal().getTitle());

		if (customerOrder.getMealType() != null) {
			holder.tiffintype.setText("Meal timing : " + customerOrder.getMealType().getDescription());
		}

		if (OrderStatus.ORDERED.equals(customerOrder.getStatus())) {
			setMealStatus();
		} else {
			holder.viewmenuButton.setVisibility(View.GONE);
		}

		if (customerOrder.getQuantity() != null)
			holder.quantity.setText("Quantity :" + customerOrder.getQuantity().toString());

		if (customerOrder.getPrice() != null)
			holder.price.setText("Rs. " + customerOrder.getPrice().toString());

		setOrderStatus();
		if (customerOrder.getDate() != null)
			holder.date.setText(CustomerUtils.convertDate(customerOrder.getDate()));

		holder.viewmenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				showMenu(pos);
			}
		});

		return convertView;

	}

	private void setOrderStatus() {
		if (customerOrder.getDate() == null || customerOrder.getMeal() == null) {
			return;
		}

		// TODO: Check if works..
		if (!DateUtils.isToday(customerOrder.getDate().getTime())) {
			holder.orderStatus.setVisibility(View.VISIBLE);
			holder.orderStatus.setText("Your have ordered " + customerOrder.getMeal().getTitle() + " for tomorrow.");
			return;
		}

		if (customerOrder.getStatus() == null) {
			return;
		}
		if (OrderStatus.CANCELLED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setVisibility(View.VISIBLE);
			holder.orderStatus.setText("Your Order has been cancelled..");
		} else if (OrderStatus.DELIVERED.equals(customerOrder.getStatus())) 
		{
			holder.orderStatus.setVisibility(View.VISIBLE);
			holder.rate_us.setVisibility(View.VISIBLE);
			holder.orderStatus.setText("Your order has been delivered!!");
			if(customerOrder.getRating()!= null)
			{
				holder.rate_us.setText("You have rated" + " " +customerOrder.getRating());

			}
			else
			{
				holder.rate_us.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final Dialog rankDialog = new Dialog(getContext());
						rankDialog.setContentView(R.layout.activity_meal_rate_us);
						rankDialog.setCancelable(true);
						final RatingBar ratingBar = (RatingBar) rankDialog
								.findViewById(R.id.quickOrder_rate_this_meal_ratingBar);
						ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
							@Override
							public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
							}
						});

						Button updateButton = (Button) rankDialog.findViewById(R.id.quickOrder_rate_this_meal_button);
						updateButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) 
							{
								customerOrder.setRating(new BigDecimal(ratingBar.getRating()));
								new  MealRatingAsyncTask(getContext(),customerOrder).execute();
								//	Toast.makeText(getContext(), "" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
								rankDialog.dismiss();
							}
						});
						// now that the dialog is set up, it's time to show it
						rankDialog.show();
					}
				});
			}}
	}

	private void setMealStatus() {
		if (customerOrder.getContent() == null || customerOrder.getMealStatus() == null) {
			holder.mealStatus.setText("Vendor has not decided your menu yet. Hang on..");
			holder.viewmenuButton.setVisibility(View.GONE);
			return;
		}
		if (MealStatus.PREPARE.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Vendor is preparing your meal..");
		} else if (MealStatus.COOKING.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Vendor started cooking your meal");
		} else if (MealStatus.DISPATCH.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Your tiffin is dispatched and will reach you soon..");
		}
	}

	private void showMenu(int position) {
		CustomerOrder customerOrder = new CustomerOrder();
		customerOrder = quickOrders.get(position);
		customerOrder.setCustomer(customer);
		new GetMealMenuAsyncTask(activity, "QuickOrder", null, customerOrder).execute();
	}

}