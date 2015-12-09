package com.rns.tiffeat.mobile.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
	String todaydate, orderdate;

	public void setQuickHome(QuickOrderHomeScreen quickHome) {
	}

	public ViewHolder getHolder() {
		return holder;
	}

	public class ViewHolder 
	{
		TextView title, tiffintype,repeatorder, price, mealStatus, date, orderStatus;
		ImageView foodimage;
		TextView viewmenuButton;
		boolean viewMenuClicked;
		boolean repeatorderclicked;

		public void setViewMenuClicked(boolean viewMenuClicked) {
			this.viewMenuClicked = viewMenuClicked;
		}

		public boolean isViewMenuClicked() {
			return viewMenuClicked;
		}

		public void setRepeatOrderClicked(boolean repeatorderclicked) {
			this.repeatorderclicked = repeatorderclicked;
		}

		public boolean isRepeatOrderClicked() {
			return repeatorderclicked;
		}

	}

	public QuickOrderListAdapter(FragmentActivity activity, int activityQuickorderListAdapter, List<CustomerOrder> quickOrders, Customer customer) {
		super(activity, activityQuickorderListAdapter, quickOrders);
		this.quickOrders = quickOrders;
		this.activity = activity;
		this.customer = customer;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), FONT);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.activity_quickorder_list_adapter, null);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.title = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_name_textView);
			holder.tiffintype = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_type_textView);
			holder.date = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_date_textView);
			holder.foodimage = (ImageView) convertView.findViewById(R.id.quickorder_list_adapter_imageview);
			holder.mealStatus = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_meal_status_textView);
			holder.orderStatus = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_order_status_textView);
			holder.viewmenuButton = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_viewmenu_button);
			holder.repeatorder = (TextView) convertView.findViewById(R.id.quickorder_list_adapter_repeatorder_button);
			
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		customerOrder = quickOrders.get(position);

		if (customerOrder == null) {
			return convertView;
		}

		holder.title.setText(customerOrder.getMeal().getTitle());
		if (customerOrder.getMealType() != null) {
			holder.tiffintype.setText(customerOrder.getMealType().getDescription());
		}

		if (holder.foodimage != null) {

			// new
			// ImageDownloaderTask(getActivity(),holder.foodimage).execute(objmeal.getImage());
		}

		if (OrderStatus.ORDERED.equals(customerOrder.getStatus())) {
			setMealStatus();
		} else {
			holder.viewmenuButton.setVisibility(View.GONE);
		}

		setOrderStatus();
		DateFormat dt = new SimpleDateFormat();
		holder.date.setText(dt.format(customerOrder.getDate()));

		holder.viewmenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowMenu(customerOrder);
			}
		});

		holder.repeatorder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
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
			holder.orderStatus.setText("Your Order has been cancelled..");
		} else if (OrderStatus.DELIVERED.equals(customerOrder.getStatus())) {
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

	private void ShowMenu(CustomerOrder customerOrder2) {
		customerOrder2.setCustomer(customer);
		new GetMealMenuAsyncTask(activity, "QuickOrder", null, customerOrder2).execute();
	}
	
	private void repeatOrder(CustomerOrder customerOrder) {
		customerOrder.setCustomer(customer);
		Fragment fragment = null;
		fragment = new SelectType(customerOrder);

		CustomerUtils.nextFragment(fragment, activity.getSupportFragmentManager(), false);

	}

}