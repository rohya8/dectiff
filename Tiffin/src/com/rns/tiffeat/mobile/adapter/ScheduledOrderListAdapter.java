package com.rns.tiffeat.mobile.adapter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rns.tiffeat.mobile.FirstTimeUse;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.ScheduledUser;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.asynctask.GetMealMenuAsyncTask;
import com.rns.tiffeat.mobile.asynctask.GetNewOrderAreaAsynctask;
import com.rns.tiffeat.mobile.asynctask.ScheduleCancelOrderTask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;
import com.rns.tiffeat.web.bo.domain.MealFormat;
import com.rns.tiffeat.web.bo.domain.MealStatus;
import com.rns.tiffeat.web.bo.domain.MealType;
import com.rns.tiffeat.web.bo.domain.OrderStatus;

public class ScheduledOrderListAdapter extends ArrayAdapter<CustomerOrder> implements AndroidConstants {

	private FragmentActivity scheduledOrderFragment;
	private List<CustomerOrder> scheduledOrders;
	private ViewHolder holder;
	private Customer customer;
	private CustomerOrder customerOrder;
	private ScheduledUser scheduledUserHome;

	public class ViewHolder {
		TextView title, mealType, price, mealStatus, date, orderStatus;
		ImageView mealImage;
		Button viewMenuButton, switchButton, cancelOrderButton, editButton;

	}

	public ScheduledOrderListAdapter(FragmentActivity fragment, int resource, List<CustomerOrder> orders, Customer customer) {
		super(fragment, resource, orders);
		this.scheduledOrders = orders;
		this.scheduledOrderFragment = fragment;
		this.customer = customer;
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
			holder.mealType = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_meal_type_textView);
			holder.date = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_date_textView);
			holder.mealImage = (ImageView) convertView.findViewById(R.id.scheduled_orders_adapter_imageview);
			holder.mealStatus = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_meal_status_textView);
			holder.orderStatus = (TextView) convertView.findViewById(R.id.scheduled_orders_adapter_order_status_textView);
			holder.cancelOrderButton = (Button) convertView.findViewById(R.id.scheduled_orders_adapter_cancel_button);
			holder.switchButton = (Button) convertView.findViewById(R.id.scheduled_orders_adapter_switch_button);
			holder.viewMenuButton = (Button) convertView.findViewById(R.id.scheduled_orders_adapter_viewmenu_button);
			holder.editButton = (Button) convertView.findViewById(R.id.scheduled_orders_adapter_edit_button);

			customerOrder = new CustomerOrder();
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (CollectionUtils.isEmpty(scheduledOrders)) {
			return convertView;
		}

		customerOrder = scheduledOrders.get(position);
		prepareCustomerOrder(customerOrder);

		if (MealType.DINNER.equals(customerOrder.getMealType())) {
			holder.editButton.setText(MealType.LUNCH.toString());
		} else
			holder.editButton.setText(MealType.DINNER.toString());

		holder.cancelOrderButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(scheduledOrderFragment)) {
					Validation.showError(scheduledOrderFragment, ERROR_NO_INTERNET_CONNECTION);
				} else {
					alertbox(customerOrder);
				}

			}

		});

		holder.switchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!Validation.isNetworkAvailable(scheduledOrderFragment)) {
					Validation.showError(scheduledOrderFragment, ERROR_NO_INTERNET_CONNECTION);
				} else {
					switchOrder();
				}
			}

		});

		holder.viewMenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Validation.isNetworkAvailable(scheduledOrderFragment)) {
					Validation.showError(scheduledOrderFragment, ERROR_NO_INTERNET_CONNECTION);
				} else
					viewMenu();
			}
		});

		holder.editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!Validation.isNetworkAvailable(scheduledOrderFragment)) {
					Validation.showError(scheduledOrderFragment, ERROR_NO_INTERNET_CONNECTION);
				} else {
					customerOrder.setMealFormat(MealFormat.SCHEDULED);
					customerOrder.setCustomer(customer);
					customerOrder.setId(0);
					customerOrder.setDate(customerOrder.getDate());
					if (MealType.DINNER.equals(customerOrder.getMealType())) {
						customerOrder.setMealType(MealType.LUNCH);

					} else
						customerOrder.setMealType(MealType.DINNER);
					newOrder(customerOrder);
				}
			}
		});

		return convertView;

	}

	private void newOrder(CustomerOrder customerOrder) {

		new GetNewOrderAreaAsynctask(scheduledOrderFragment, customerOrder).execute();

	}

	private void prepareCustomerOrder(CustomerOrder customerOrder) {
		if (customerOrder == null || customerOrder.getMeal() == null) {
			return;
		}
		holder.title.setText(customerOrder.getMeal().getTitle());
		holder.date.setText("Scheduled From :" + CustomerUtils.convertDate(customerOrder.getDate()));
		holder.mealType.setText(customerOrder.getMealType().toString());

		setOrderStatus(customerOrder);
		if (customerOrder.getStatus() == null || OrderStatus.ORDERED.equals(customerOrder.getStatus())) {
			setMealStatus(customerOrder);
		} else {
			hideControls();
		}

	}

	private void setOrderStatus(CustomerOrder customerOrder) {
		if (customerOrder.getStatus() == null) {
			return;
		}
		if (OrderStatus.CANCELLED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setText("Your Order has been cancelled..");
			hideControls();
		} else if (OrderStatus.DELIVERED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setText("Your order has been delivered!! Please rate us!!");
			hideControls();
		} else if (OrderStatus.PAYABLE.equals(customerOrder.getStatus())) {
			holder.orderStatus.setText("Insufficient funds in the wallet!!!");
		}
		
	}

	private void setMealStatus(CustomerOrder customerOrder) {
		if (customerOrder.getContent() == null || customerOrder.getMealStatus() == null) {
			holder.mealStatus.setText("Vendor has not decided your menu yet. Hang on..");
			hideControls();
			return;
		}
		if (MealStatus.PREPARE.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Vendor is preparing your meal..");
		} else if (MealStatus.COOKING.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Vendor started cooking your meal");
			mealTypeCooking();
		} else if (MealStatus.DISPATCH.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Your tiffin is dispatched and will reach you soon..");
			mealTypeCooking();
		}
	}

	private void mealTypeCooking() {
		holder.cancelOrderButton.setVisibility(View.GONE);
		holder.switchButton.setVisibility(View.GONE);
	}

	private void hideControls() {
		mealTypeCooking();
		holder.viewMenuButton.setVisibility(View.GONE);
	}

	private void cancelOrder() {
		customerOrder.setCustomer(customer);
		ScheduleCancelOrderTask cancelOrderTask = new ScheduleCancelOrderTask(scheduledOrderFragment, customerOrder);
		cancelOrderTask.setScheduledUser(scheduledUserHome);
		cancelOrderTask.execute("");
	}

	private void switchOrder() {
		customerOrder.setCustomer(customer);
		Fragment fragment = null;
		fragment = new FirstTimeUse(customerOrder);
		CustomerUtils.nextFragment(fragment, scheduledOrderFragment.getSupportFragmentManager(), true);
	}

	private void viewMenu() {
		customerOrder.setCustomer(customer);
		new GetMealMenuAsyncTask(scheduledOrderFragment, null, "Scheduled", customerOrder).execute();
	}

	private void alertbox(CustomerOrder customerOrder2) {

		AlertDialog.Builder builder = new AlertDialog.Builder(scheduledOrderFragment, R.style.AppCompatAlertDialogStyle);
		builder.setTitle("Order Status");
		builder.setMessage("Are you sure you want to cancel your order?.");

		builder.setNegativeButton("No", null);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!Validation.isNetworkAvailable(scheduledOrderFragment)) {
					Validation.showError(scheduledOrderFragment, ERROR_NO_INTERNET_CONNECTION);
				} else {
					cancelOrder();
				}
			}
		});

		builder.show();
	}

	public ScheduledUser getScheduledUserHome() {
		return scheduledUserHome;
	}

	public void setScheduledUserHome(ScheduledUser scheduledUserHome) {
		this.scheduledUserHome = scheduledUserHome;
	}

}
