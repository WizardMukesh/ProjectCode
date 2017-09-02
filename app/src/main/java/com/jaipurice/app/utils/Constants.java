package com.jaipurice.app.utils;

/**
 * Created by Nishant on 8/10/2017.
 */

public class Constants {
    public static final String URL_BASE = "http://demoserver.d5infotech.com/jaipur_coldice/API/";
    public static final String URL_LOGIN = URL_BASE + "login.php";
    public static final String URL_CUSTOMER_LIST = URL_BASE + "customers_list.php?rest_api=customers";
    public static final String URL_PRODUCT_LIST = URL_BASE + "products.php?rest_api=products";
    public static final String URL_EMPLOYEE_ITEMS = URL_BASE + "employee_items.php?rest_api=employee_items&emp_id=";
    public static final String URL_TAXES = URL_BASE + "product_settings.php?rest_api=products_settings";
    public static final String URL_ORDER = URL_BASE + "orders.php?rest_api=orders";

    public static final String PREF_IS_LOGGED_IN="is_logged_in";
    public static final String PREF_EMP_ID ="user_id";
    public static final String PREF_USER_NAME="user_name";
    public static final String PREF_USER_FULL_NAME="user_full_name";
    public static final String PREF_TOKEN="token";
    public static final String PREF_CONTACT_NUM="contact";
    public static final String PREF_PHOTO_URL="photo_url";
    public static final String PREF_TAX="taxPercentToApply";
    public static final String PREF_DISCOUNT="discount";
}