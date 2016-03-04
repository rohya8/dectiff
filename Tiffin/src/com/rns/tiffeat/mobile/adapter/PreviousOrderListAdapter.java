package com.rns.tiffeat.mobile.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.PreviousOrderHomeScreen;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.asynctask.PreviousOrderMealImageDownloaderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class PreviousOrderListAdapter extends ArrayAdapter<CustomerOrder> implements AndroidConstants {
	private FragmentActivity activity;
	private List<CustomerOrder> previousOrders;
	private CustomerOrder customerOrder;
	private ViewHolder holder;

	public List<CustomerOrder> getPreviousOrders() {
		return previousOrders;
	}

	public void setPreviousOrders(List<CustomerOrder> previousOrders) {
		this.previousOrders = previousOrders;
	}

	public void setQuickHome(PreviousOrderHomeScreen previusHome) {
	}

	public ViewHolder getHolder() {
		return holder;
	}

	public class ViewHolder {
		TextView title, tiffintype, price, date;
		ImageView foodimage;
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

	public PreviousOrderListAdapter(FragmentActivity activity, int activityQuickorderListAdapter, List<CustomerOrder> previousOrders) {
		super(activity, activityQuickorderListAdapter, previousOrders);
		this.activity = activity;
		this.previousOrders = previousOrders;
		Collections.reverse(this.previousOrders);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), FONT);
		customerOrder = previousOrders.get(position);

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.activity_previousorder_list_adapter, parent, false);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.title = (TextView) convertView.findViewById(R.id.previousorder_list_adapter_name_textView);
			holder.tiffintype = (TextView) convertView.findViewById(R.id.previousorder_list_adapter_type_textView);
			holder.date = (TextView) convertView.findViewById(R.id.previousorder_list_adapter_date_textView);
			holder.foodimage = (ImageView) convertView.findViewById(R.id.previousorder_list_adapter_imageview);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		new PreviousOrderMealImageDownloaderTask(holder, holder.foodimage, getContext()).execute(customerOrder.getMeal());

		if (customerOrder.getMeal().getTitle() != null)
			holder.title.setText(customerOrder.getMeal().getTitle());

		if (customerOrder.getMealType().toString() != null)
			holder.tiffintype.setText(customerOrder.getMealType().toString());

		if (customerOrder.getDate() != null)
			holder.date.setText(CustomerUtils.convertDate(customerOrder.getDate()));

		return convertView;
	}

}