package com.rns.tiffeat.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.rns.tiffeat.mobile.asynctask.LogoutAsynctask;
import com.rns.tiffeat.mobile.util.AndroidConstants;
import com.rns.tiffeat.mobile.util.CustomerUtils;
import com.rns.tiffeat.mobile.util.FontChangeCrawler;
import com.rns.tiffeat.web.bo.domain.Customer;
import com.rns.tiffeat.web.bo.domain.CustomerOrder;

public class DrawerActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, AndroidConstants {

	private Toolbar mToolbar;
	private FragmentDrawer drawerFragment;
	private Customer customer;
	private String action;

	@Override
	public void onBackPressed() {

		FragmentManager fragmentManager = getSupportFragmentManager();
		int backcount = fragmentManager.getBackStackEntryCount();
		if (backcount > 1) {
			super.onBackPressed();
		} else
			finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);

		mToolbar = (Toolbar) findViewById(R.id.tool_bar);
		CustomerOrder customerOrder = new CustomerOrder();
		if (getIntent().getExtras() != null) {
			String customerJson = (String) getIntent().getExtras().get(AndroidConstants.CUSTOMER_OBJECT);
			customer = new Gson().fromJson(customerJson, Customer.class);
			action = (String) getIntent().getExtras().get("action");
			customerOrder = new Gson().fromJson(getIntent().getExtras().getString(CUSTOMER_ORDER_OBJECT), CustomerOrder.class);
		}
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
		drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
		drawerFragment.setDrawerListener(this);

		if (action != null) {
			if (action.equals(ACTION_QUICK_HOME)) {
				Fragment fragment = null;
				fragment = quickOrdersHome(customerOrder);
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
				return;
			} else if (action.equals(ACTION_SCHEDULED_HOME)) {
				Fragment fragment = null;
				fragment = scheduledOrdersHome(customerOrder);
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
				return;
			} else if (action.equals(ACTION_QUICK_ORDER)) {
				Fragment fragment = null;
				fragment = new QuickOrderFragment(customerOrder);
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
				return;
			} else if (action.equals(ACTION_SCHEDULED_ORDER)) {
				Fragment fragment = null;
				fragment = new ScheduledOrderFragment(customerOrder);
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
				return;
			} else if (action.equals(ACTION_REGISTRATION)) {
				Fragment fragment = null;
				fragment = new UserRegistration(customerOrder);
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
				return;
			}
		}

		if (customer != null) {
			if (customer.getQuickOrders() != null && customer.getQuickOrders().size() > 0) {
				Fragment fragment = null;
				fragment = new QuickOrderHomeScreen(customer);
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
				return;
			} else if (customer.getScheduledOrder() != null && customer.getScheduledOrder().size() > 0) {
				Fragment fragment = null;
				fragment = new ScheduledOrderHomeScreen(customer);
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
				return;
			}

		}

		displayView(0);
	}

	public void onDrawerItemSelected(View view, int position) {
		displayView(position);

	}

	private void displayView(int position) {

		hideSoftKeyboard();

		customer = CustomerUtils.getCurrentCustomer(DrawerActivity.this);
		if (customer.getName() == null) {
			newUserDrawer(position);
			return;
		}
		if (customer.getName() != null) {
			loggedInUserDrawer(position);
			return;
		}

	}

	private void loggedInUserDrawer(int position) {
		Fragment fragment = null;
		CustomerOrder customerOrder = new CustomerOrder();
		String title = getString(R.string.app_name);

		switch (position) {

		case 0:
			fragment = homeScreen(customerOrder);
			break;
		case 1:
			fragment = scheduledOrdersHome(customerOrder);
			break;

		case 2:
			fragment = quickOrdersHome(customerOrder);
			break;

		case 3:
			if (org.apache.commons.collections.CollectionUtils.isEmpty(customer.getPreviousOrders())) {
				CustomerUtils.alertbox(TIFFEAT, "No orders yet...", DrawerActivity.this);
				fragment = homeScreen(customerOrder);
			} else {
				CustomerUtils.clearFragmentStack(getSupportFragmentManager());
				fragment = new PreviousOrderHomeScreen(customer);
			}
			break;

		case 4:
			fragment = new WalletFragment();
			break;
			
		case 5:
			fragment = new TermsFragment();
			break;

		case 6:
			CustomerOrder custOrder = new CustomerOrder();
			custOrder.setCustomer(customer);
			fragment = new AboutUsFragment(custOrder);
			break;

		case 7:
			fragment = new ContactusFragment();
			break;

		case 8:

			if (customer != null) {
				CustomerUtils.logout(this);
				new LogoutAsynctask(this).execute();
			} else {
				CustomerUtils.alertbox(TIFFEAT, " You are not logged in  ", DrawerActivity.this);
				fragment = new NewFirstTimeScreen();
			}
			break;

		default:
			break;
		}

		if (fragment != null) {
			title = "TiffEat";
			getSupportActionBar().setTitle(title);
			if (isFragmentToBeAddedToBackStack(fragment)) {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
			} else {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), true);
			}
		}

	}

	private Fragment quickOrdersHome(CustomerOrder customerOrder) {
		Fragment fragment;
		CustomerUtils.clearFragmentStack(getSupportFragmentManager());
		fragment = new QuickOrderHomeScreen(customer);
		return fragment;
	}

	private Fragment scheduledOrdersHome(CustomerOrder customerOrder) {
		Fragment fragment;
		CustomerUtils.clearFragmentStack(getSupportFragmentManager());
		fragment = new ScheduledOrderHomeScreen(customer);
		return fragment;
	}

	private Fragment homeScreen(CustomerOrder customerOrder) {
		Fragment fragment;
		customerOrder.setCustomer(customer);

		fragment = new NewFirstTimeScreen(customerOrder);
		return fragment;
	}

	private void newUserDrawer(int position) {

		Fragment fragment = null;
		String title = getString(R.string.app_name);

		switch (position) {

		case 0:
			if (customer == null || TextUtils.isEmpty(customer.getEmail())) {
				fragment = new NewFirstTimeScreen();
			}
			break;

		case 4:

			// Intent intent = new Intent(this, LoginActivity.class);
			// startActivity(intent);

			CustomerOrder customerOrder = new CustomerOrder();
			Intent intent = new Intent(DrawerActivity.this, LoginActivity.class);
			intent.putExtra(CUSTOMER_ORDER_OBJECT, new Gson().toJson(customerOrder));
			startActivity(intent);
			finish();

			break;

		case 1:
			fragment = new TermsFragment();
			title = getString(R.string.nav_item_terms);

			break;

		case 2:
			fragment = new AboutUsFragment();
			title = getString(R.string.nav_item_terms);
			break;

		case 3:

			fragment = new ContactusFragment();
			title = getString(R.string.nav_item_contactus);
			break;

		default:
			break;
		}

		if (fragment != null) {
			title = "TiffEat";
			getSupportActionBar().setTitle(title);
			if (isFragmentToBeAddedToBackStack(fragment)) {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), false);
			} else {
				CustomerUtils.nextFragment(fragment, getSupportFragmentManager(), true);
			}
		}

	}

	private boolean isFragmentToBeAddedToBackStack(Fragment fragment) {
		return fragment instanceof TermsFragment || fragment instanceof ContactusFragment || fragment instanceof AboutUsFragment
				|| fragment instanceof QuickOrderHomeScreen || fragment instanceof ScheduledOrderHomeScreen;
	}

	public void setContentView(View view) {
		super.setContentView(view);
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), FONT);
		fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
	}

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

}