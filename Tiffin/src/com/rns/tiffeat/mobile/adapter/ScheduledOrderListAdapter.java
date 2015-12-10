package com.rns.tiffeat.mobile.adapter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.content.Context;
import android.opengl.Visibility;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.asynctask.ScheduleCancelOrderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealStatus;
import com.rns.tiffeat.web.bo.domain.OrderStatus;

public class ScheduledOrderListAdapter extends ArrayAdapter<CustomerOrder> implements AndroidConstants {

	private FragmentActivity scheduledOrderFragment;
	private List<CustomerOrder> scheduledOrders;
	private ViewHolder holder;

	public class ViewHolder {
		TextView title, mealType, price, mealStatus, date, orderStatus;
		ImageView mealImage;
		TextView viewMenuButton, switchButton, cancelOrderButton;

	}

	public ScheduledOrderListAdapter(FragmentActivity fragment, int resource, List<CustomerOrder> orders) {
		super(fragment, resource, orders);
		this.scheduledOrders = orders;
		this.scheduledOrderFragment = fragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FontChangeCrawler fontChanger = new FontChangeCrawler(scheduledOrderFragment.getAssets(), FONT);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) scheduledOrderFragment.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.activity_scheduled_orders_adapter, null);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.title = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_meal_title_textView);
			// holder.mealType = (TextView)
			// convertView.findViewById(R.id.quickorder_list_adapter_type_textView);
			holder.date = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_date_textView);
			holder.mealImage = (ImageView) convertView.findViewById(R.id.scheduled_orders_adapter_imageview);
			holder.mealStatus = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_meal_status_textView);
			holder.orderStatus = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_order_status_textView);
			// holder.viewMenuButton = (TextView)
			// convertView.findViewById(R.id.quickorder_list_adapter_viewmenu_button);
			holder.cancelOrderButton = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_switch_button);
			holder.switchButton = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_cancel_button);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (CollectionUtils.isEmpty(scheduledOrders)) {
			return convertView;
		}

		prepareCustomerOrder(scheduledOrders.get(position));

		return convertView;

	}

	private void prepareCustomerOrder(CustomerOrder customerOrder) {
		if (customerOrder == null || customerOrder.getMeal() == null) {
			return;
		}
		holder.title.setText(customerOrder.getMeal().getTitle());
		holder.date.setText("Scheduled From :" + CustomerUtils.convertDate(customerOrder.getDate()));
		setOrderStatus(customerOrder);
		if (!OrderStatus.DELIVERED.equals(customerOrder.getStatus()) || !OrderStatus.CANCELLED.equals(customerOrder.getStatus())) {
			setMealStatus(customerOrder);
		}
		else {
			hideControls();
		}
		prepareOnClickListeners(customerOrder);
	}

	private void setOrderStatus(CustomerOrder customerOrder) {
		if (customerOrder.getStatus() == null) {
			return;
		}
		if (OrderStatus.CANCELLED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setText("Your Order has been cancelled..");
		} else if (OrderStatus.DELIVERED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setText("Your order has been delivered!! Please rate us!!");
		}
	}

	private void prepareOnClickListeners(final CustomerOrder customerOrder) {
		holder.cancelOrderButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ScheduleCancelOrderTask(scheduledOrderFragment, customerOrder).execute("");
			}
		});
	}

	private void setMealStatus(CustomerOrder customerOrder) {
		if (customerOrder.getContent() == null || customerOrder.getMealStatus() == null) {
			holder.mealStatus.setText("Vendor has not decided your menu yet. Hang on..");
			// TODO :holder.viewmenuButton.setVisibility(View.GONE);
			hideControls();
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

	private void hideControls() {
		holder.cancelOrderButton.setVisibility(View.GONE);
		holder.switchButton.setVisibility(View.GONE);
		//TODO :holder.viewMenuButton.setVisibility(View.GONE);
	}

}
