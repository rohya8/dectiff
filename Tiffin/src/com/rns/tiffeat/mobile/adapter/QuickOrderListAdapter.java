package com.rns.tiffeat.mobile.adapter;

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
import com.rns.tiffeat.mobile.asynctask.GetMealMenuAsyncTask;
import com.rns.tiffeat.mobile.asynctask.QuickOrderMealImageDownloaderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealStatus;
import com.rns.tiffeat.web.bo.domain.OrderStatus;

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
		TextView title, tiffintype, repeatorder, mealStatus, date, orderStatus;
		ImageView foodimage;
		TextView viewmenuButton, vendorname;

		public ImageView getFoodimage() {
			return foodimage;
		}

		public void setFoodimage(ImageView foodimage) {
			this.foodimage = foodimage;
		}

	}

	public QuickOrderListAdapter(FragmentActivity activity, int activityQuickorderListAdapter, List<CustomerOrder> quickOrders, Customer customer) {
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
			convertView = vi.inflate(R.layout.activity_quickorder_list_adapter, null);
			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.title = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_name_textView);
			holder.tiffintype = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_type_textView);
			holder.date = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_date_textView);
			ImageView mealImageView = (ImageView) convertView.findViewById(R.id.quickorder_list_adapter_imageview);
			holder.foodimage = mealImageView;
			new QuickOrderMealImageDownloaderTask(holder, mealImageView, getContext()).execute(customerOrder.getMeal());
			holder.mealStatus = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_meal_status_textView);
			holder.orderStatus = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_order_status_textView);
			holder.viewmenuButton = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_viewmenu_button);
			holder.repeatorder = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_repeatorder_button);
			holder.vendorname = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_vendorname_textView);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.orderStatus.setTag(position);
		holder.viewmenuButton.setTag(position);
		holder.repeatorder.setTag(position);

		if (customerOrder == null) {
			return convertView;
		}

		if (customerOrder.getMeal().getTitle() != null)
			holder.title.setText(customerOrder.getMeal().getTitle());

		if (customerOrder.getMealType() != null) {
			holder.tiffintype.setText("Meal timing : "+customerOrder.getMealType().getDescription());
		}

		if (OrderStatus.ORDERED.equals(customerOrder.getStatus())) {
			setMealStatus();
		} else {
			holder.viewmenuButton.setVisibility(View.GONE);
		}

		if (customerOrder.getMeal().getVendor() != null)
			holder.vendorname.setText("Vendor name : "+ customerOrder.getMeal().getVendor().getName());

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

		holder.repeatorder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				customerOrder = quickOrders.get(pos);
				customerOrder.setCustomer(customer);
				repeatOrder(customerOrder);
			}

		});

		return convertView;

	}

	private void setOrderStatus() {
		if (customerOrder.getStatus() == null) {
			return;
		}
		if (OrderStatus.CANCELLED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setVisibility(View.VISIBLE);
			holder.orderStatus.setText("Your Order has been cancelled..");
		} else if (OrderStatus.DELIVERED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setVisibility(View.VISIBLE);
			holder.orderStatus.setText("Your order has been delivered!! Please rate us!!");
		}
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

	private void repeatOrder(CustomerOrder customerOrder) {
		customerOrder.setId(0);
		customerOrder.setCustomer(customer);
		Fragment fragment = null;
		fragment = new SelectType(customerOrder);
		CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), false);
	}

}