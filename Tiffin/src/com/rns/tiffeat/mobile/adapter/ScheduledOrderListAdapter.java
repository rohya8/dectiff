package com.rns.tiffeat.mobile.adapter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.app.Dialog;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.rns.tiffeat.mobile.NewChangeOrderFragment;
import com.rns.tiffeat.mobile.NewScheduleLunchOrDinnerFragment;
import com.rns.tiffeat.mobile.R;
import com.rns.tiffeat.mobile.ScheduledOrderHomeScreen;
import com.rns.tiffeat.mobile.Validation;
import com.rns.tiffeat.mobile.asynctask.GetMealMenuAsyncTask;
import com.rns.tiffeat.mobile.asynctask.MealRatingAsyncTask;
import com.rns.tiffeat.mobile.asynctask.ScheduleCancelOrderTask;
import com.rns.tiffeat.mobile.asynctask.ScheduleOrderMealImageDownloaderTask;
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
	private ScheduledOrderHomeScreen scheduledUserHome;

	public class ViewHolder {
		TextView title, mealType, price, mealStatus, date, orderStatus,rateus;
		ImageView foodimage;
		Button viewMenuButton, switchButton, cancelOrderButton, addOtherMealTypeButton;

		public ImageView getFoodimage() {
			return foodimage;
		}

		public void setFoodimage(ImageView foodimage) {
			this.foodimage = foodimage;
		}

	}

	public List<CustomerOrder> getScheduledOrders() {
		return scheduledOrders;
	}

	public void setScheduledOrders(List<CustomerOrder> scheduledOrders) {
		this.scheduledOrders = scheduledOrders;
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

		customerOrder = scheduledOrders.get(position);

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) scheduledOrderFragment.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.activity_scheduled_orders_adapter, null);

			holder = new ViewHolder();
			fontChanger.replaceFonts((ViewGroup) convertView);
			holder.title = (TextView) convertView.findViewById(R.id.scheduledorder_adapter_mealtitle_textView);
			holder.mealType = (TextView) convertView.findViewById(R.id.scheduledorder_adapter_mealtype_textView);
			holder.date = (TextView) convertView.findViewById(R.id.scheduledorder_adapter_date_textView);
			holder.foodimage = (ImageView) convertView.findViewById(R.id.scheduledorder_adapter_imageview);
			holder.rateus = (TextView)convertView.findViewById(R.id.scheduledorder_adapter_rateus_textView);
			holder.mealStatus = (TextView) convertView.findViewById(R.id.scheduledorder_adapter_mealstatus_textView);
			holder.orderStatus = (TextView) convertView.findViewById(R.id.scheduledorder_adapter_orderstatus_textView);
			holder.cancelOrderButton = (Button) convertView.findViewById(R.id.scheduledorder_adapter_cancel_button);
			holder.switchButton = (Button) convertView.findViewById(R.id.scheduledorder_adapter_switch_button);
			holder.viewMenuButton = (Button) convertView.findViewById(R.id.scheduledorder_adapter_viewmenu_button);
			holder.addOtherMealTypeButton = (Button) convertView.findViewById(R.id.scheduledorder_adapter_edit_button);
			holder.price = (TextView) convertView.findViewById(R.id.scheduledorder_adapter_price_textView);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		new ScheduleOrderMealImageDownloaderTask(holder, holder.foodimage, getContext()).execute(customerOrder.getMeal());
		if (CollectionUtils.isEmpty(scheduledOrders)) {
			return convertView;
		}

		holder.cancelOrderButton.setTag(position);
		holder.switchButton.setTag(position);
		holder.viewMenuButton.setTag(position);
		holder.addOtherMealTypeButton.setTag(position);
		holder.mealStatus.setVisibility(View.VISIBLE);

		prepareCustomerOrder(customerOrder);

		prepareAddOtherMealTypeButton();

		holder.cancelOrderButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int pos = (Integer) v.getTag();
				customerOrder = scheduledOrders.get(pos);
				cancelConfirmationAlert(customerOrder);

			}

		});

		holder.switchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				customerOrder = scheduledOrders.get(pos);
				switchOrder(customerOrder);
			}

		});

		holder.viewMenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				customerOrder = scheduledOrders.get(pos);
				viewMenu(customerOrder);
			}
		});

		holder.addOtherMealTypeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				prepareNewOrder(pos);
			}

		});

		return convertView;

	}

	private void prepareAddOtherMealTypeButton() {
		if (CollectionUtils.isEmpty(customer.getScheduledOrder())) {
			holder.addOtherMealTypeButton.setText("Order Tiffin");
			return;
		}
		if (customer.getScheduledOrder().size() > 1) {
			holder.addOtherMealTypeButton.setVisibility(View.GONE);
			return;
		}

		if (MealType.DINNER.equals(customerOrder.getMealType())) {
			holder.addOtherMealTypeButton.setText("Add " + MealType.LUNCH.getDescription());
		} else
			holder.addOtherMealTypeButton.setText("Add " + MealType.DINNER.getDescription());
	}

	private void newOrder(CustomerOrder customerOrder) {

		Fragment fragment = null;
		fragment = new NewScheduleLunchOrDinnerFragment(customerOrder);
		CustomerUtils.nextFragment(fragment, scheduledOrderFragment.getSupportFragmentManager(), true);

	}

	private void prepareCustomerOrder(CustomerOrder customerOrder) {
		if (customerOrder == null || customerOrder.getMeal() == null) {
			return;
		}
		holder.title.setText(customerOrder.getMeal().getTitle());
		if (customerOrder.getMeal().getPrice() != null) {
			holder.price.setText("Rs. " + customerOrder.getMeal().getPrice().toString());
		}

		if (customerOrder.getDate() != null)
			holder.date.setText("Scheduled From :" + CustomerUtils.convertDate(customerOrder.getDate()));
		else
			holder.date.setText("");

		if (customerOrder.getMealType() != null) {
			holder.mealType.setText(customerOrder.getMealType().toString());
		}

		setOrderStatus(customerOrder);
		if (customerOrder.getStatus() == null || OrderStatus.ORDERED.equals(customerOrder.getStatus())) {
			setMealStatus(customerOrder);
		} else {
			hideControls(customerOrder);
		}

	}

	private void setOrderStatus(CustomerOrder customerOrder) {
		if (customerOrder.getStatus() == null) {
			holder.orderStatus.setVisibility(View.GONE);
			return;
		}
		if (OrderStatus.CANCELLED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setVisibility(View.VISIBLE);
			holder.orderStatus.setText("Your Order has been cancelled..");
			hideControls(customerOrder);
		} else if (OrderStatus.DELIVERED.equals(customerOrder.getStatus())) {
			holder.orderStatus.setVisibility(View.VISIBLE);
			holder.orderStatus.setText("Your order has been delivered!! Please rate us!!");
			hideControls(customerOrder);
		} else if (OrderStatus.PAYABLE.equals(customerOrder.getStatus())) {
			holder.orderStatus.setText("Insufficient funds in the wallet!!!");
			holder.mealStatus.setVisibility(View.GONE);
		}

	}

	private void setMealStatus(CustomerOrder customerOrder) {
		if (customerOrder.getContent() == null || customerOrder.getMealStatus() == null) {
			holder.mealStatus.setText("Vendor has not decided your menu yet. Hang on..");
			hideControls(customerOrder);
			return;
		}
		if (MealStatus.PREPARE.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Vendor is preparing your meal..");
		} else if (MealStatus.COOKING.equals(customerOrder.getMealStatus())) {
			holder.mealStatus.setText("Vendor started cooking your meal");
			mealTypeCooking(customerOrder);
		} else if (MealStatus.DISPATCH.equals(customerOrder.getMealStatus())) 
		{
			holder.mealStatus.setText("Your tiffin is dispatched and will reach you soon..");
			mealTypeCooking(customerOrder);
		}
	}

	private void mealTypeCooking(CustomerOrder customerOrder2) 
	{

		holder.cancelOrderButton.setVisibility(View.GONE);
		holder.switchButton.setVisibility(View.GONE);
		holder.rateus.setVisibility(View.VISIBLE);
		if(customerOrder.getRating()!= null)
		{
			holder.rateus.setText("You have rated" + " " +customerOrder2.getRating());

		}
		else
		{
			holder.rateus.setOnClickListener(new OnClickListener() {
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
		}

	}

	private void hideControls(CustomerOrder customerOrder2) {
		mealTypeCooking(customerOrder2);
		holder.mealStatus.setVisibility(View.GONE);
		holder.viewMenuButton.setVisibility(View.GONE);
	}

	private void cancelOrder() {
		customerOrder.setCustomer(customer);
		customerOrder.setId(customerOrder.getCustomerOrderId());
		ScheduleCancelOrderTask cancelOrderTask = new ScheduleCancelOrderTask(scheduledOrderFragment, customerOrder);
		cancelOrderTask.setScheduledUser(scheduledUserHome);
		cancelOrderTask.execute("");
	}

	private void switchOrder(CustomerOrder customerOrder) {

		customerOrder.setId(customerOrder.getCustomerOrderId());
		customerOrder.setCustomer(customer);
		Fragment fragment = null;
		fragment = new NewChangeOrderFragment(customerOrder);
		CustomerUtils.nextFragment(fragment, scheduledOrderFragment.getSupportFragmentManager(), true);
	}

	private void viewMenu(CustomerOrder customerOrder1) {
		customerOrder1.setCustomer(customer);
		new GetMealMenuAsyncTask(scheduledOrderFragment, null, "Scheduled", customerOrder1).execute();
	}

	private void cancelConfirmationAlert(CustomerOrder customerOrder2) {

		AlertDialog.Builder builder = new AlertDialog.Builder(scheduledOrderFragment, R.style.AppCompatAlertDialogStyle);
		builder.setTitle("Order Status");
		builder.setMessage("Are you sure you want to cancel your order?");

		builder.setNegativeButton("No", null);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancelOrder();
			}
		});

		builder.show();
	}

	public ScheduledOrderHomeScreen getScheduledUserHome() {
		return scheduledUserHome;
	}

	public void setScheduledUserHome(ScheduledOrderHomeScreen scheduledUserHome) {
		this.scheduledUserHome = scheduledUserHome;
	}

	private void prepareNewOrder(int pos) {

		CustomerOrder order = new CustomerOrder();
		order.setAddress(customerOrder.getAddress());
		order.setMealFormat(MealFormat.SCHEDULED);
		order.setLocation(customerOrder.getLocation());
		order.setDate(new Date());
		order.setCustomer(customer);
		if (MealType.DINNER.equals(customerOrder.getMealType())) {
			order.setMealType(MealType.LUNCH);
		} else {
			order.setMealType(MealType.DINNER);
		}
		newOrder(order);
	}

}
