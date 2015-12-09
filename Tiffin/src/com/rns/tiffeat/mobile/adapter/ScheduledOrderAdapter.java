package com.rns.tiffeat.mobile.adapter;

import java.util.List;

import com.rns.tiffeat.mobile.QuickOrderHomeScreen;
import com.rns.tiffeat.mobile.ScheduledUser;
import com.rns.tiffeat.mobile.adapter.QuickOrderListAdapter.ViewHolder;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ScheduledOrderAdapter extends ArrayAdapter< CustomerOrder> implements AndroidConstants 
{
	

	
	
	private FragmentActivity activity;
	private List<CustomerOrder> scheduledOrder;
	private Customer customer;
	private CustomerOrder customerOrder;
	private ViewHolder holder;
	String todaydate, orderdate;
	
	public void setScheduledOrder(ScheduledUser scheduledUser) 
	{
		
	}
	
	public ViewHolder getHolder() 
	{
		return holder;
	}
	
	public class ViewHolder 
	{
		TextView title, tiffintype,repeatorder, price, mealStatus, date, orderStatus;
		ImageView foodimage;
		TextView viewmenuButton;
		Button switchButton,cancelButton;
		boolean viewMenuClicked,switchClicked,cancelClicked;
		boolean repeatorderclicked;

		public void setViewMenuClicked(boolean viewMenuClicked) {
			this.viewMenuClicked = viewMenuClicked;
		}

		public boolean isViewMenuClicked() {
			return viewMenuClicked;
		}
		
		public void setSwitchButtonClicked(boolean viewMenuClicked) {
			this.viewMenuClicked = viewMenuClicked;
		}

		public boolean isSwitchClicked() {
			return viewMenuClicked;
		}
		
		public void setCancelClicked(boolean viewMenuClicked) {
			this.viewMenuClicked = viewMenuClicked;
		}

		public boolean isCancelClicked() {
			return viewMenuClicked;
		}

		
	}
	
	public ScheduledOrderAdapter(FragmentActivity activity, int activityScheduledOrderAdapter, List<CustomerOrder> scheduledOrder, Customer customer) 
	{
		super(activity, activityScheduledOrderAdapter, scheduledOrder);
		this.scheduledOrder  = scheduledOrder;
		this.activity = activity;
		this.customer = customer;
	}

	
	
}
