
package com.rns.tiffeat.mobile.util;

public interface AndroidConstants {
	String GOOGLE_PROJECT_ID = " ";
	String RESPONSE_INVALID = "INVALID";
	String RESPONSE_OK = "OK";
	String VENDOR_OBJECT = "vendor";
	String TAG = "TiffEAT";
	// String ROOT_URL = "http://d5fb94c7.ngrok.io/tiffeat-web/";
	String ROOT_URL = "http://192.168.0.3:8080/tiffeat-web/";
	//String ROOT_URL = "http://www.tiffeat.com/";
	String CUSTOMER_OBJECT = "customer";
	String CUSTOMER_ORDER_OBJECT = "customerOrderObject";
	String MEAL_OBJECT = "meal";

	String GET_VENDORS_FOR_AREA = ROOT_URL + "getVendorsForAreaAndroid?pinCode={pinCode}";
	String GET_MEALS_FOR_VENDORS = ROOT_URL + "getVendorMealsAndroid?vendor={vendor}";
	String CUSTOMER_REGISTRATION = ROOT_URL + "registerCustomerAndroid?customer={customer}";
	String CUSTOMER_QUICK_ORDER_URL = ROOT_URL + "quickOrderAndroid?customerOrder={customerOrderObject}";
	String CUSTOMER_SCHEDULED_ORDER_URL = ROOT_URL + "scheduledOrderAndroid?customerOrder={customerOrderObject}";
	String GET_AREAS = ROOT_URL + "getAreasAndroid.htm";
	String CUSTOMER_GET_MEAL_URL = ROOT_URL + "getAvailableMealTypeDatesAndroid?customerOrder={customerOrderObject}";
	String CUSTOMER_GET_MENU_URL = ROOT_URL + "getMenuAndroid?customerOrder={customerOrderObject}";
	String GET_CURRENT_CUSTOMER_URL = ROOT_URL + "getCurrentCustomerAndroid?customer={customer}";
	String CUSTOMER_LOGIN_URL = ROOT_URL + "loginCustomerAndroid?customer={customer}";
	String CUSTOMER_GOOGLE_LOGIN_URL = ROOT_URL + "loginWithGoogleCustomerAndroid?customer={customer}";
	String PAYMENT_URL = ROOT_URL + "paymentAndroid?customerOrder={customerOrderObject}";
	String VALIDATE_CUSTOMER_QUICKORDER_URL = ROOT_URL + "validateQuickOrderAndroid?customerOrder={customerOrderObject}";
	String VALIDATE_CUSTOMER_SCHEDULEORDER_URL = ROOT_URL + "validateScheduledOrderAndroid?customerOrder={customerOrderObject}";
	String ADD_TO_WALLET_URL = ROOT_URL + "addToWalletAndroid?customer={customer}";
	String CANCEL_ORDER_URL = ROOT_URL + "cancelOrderAndroid?customerOrder={customerOrderObject}";
	String CHANGE_ORDER_URL = ROOT_URL + "changeOrderAndroid?customerOrder={customerOrderObject}";
	String DOWNLOAD_MEAL_IMAGE_ANDROID = "downloadMealImageAndroid?meal=";

	String CUSTOMER_SHARED_CONTEXT = "customerShared";
	String PIN_CODE = "pinCode";
	String REG_ID = "regId";
	String USER_PREFERENCES = "userPreferences";
	String LOGGED_IN = "loggedIn";
	String MYTAG = "tiffeat-android";
	String TIFFEAT = "TiffEat";
	String FONT = "Roboto-Regular.ttf";

	String ERROR_NO_INTERNET_CONNECTION = "No Internet connection";
	String ERROR_FETCHING_DATA = "Error Fetching Data..... Please check internet connection and Try Again ";
	String NO_VENDORS_CURRENTLY_AVAILABLE_IN_THIS_AREA = "No Vendors Currently available in this area ";

	String MODEL_RESULT = "result";
	String DATE_FORMAT = "EEE, MMM d";
}
