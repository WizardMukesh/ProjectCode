package com.jaipurice.app.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by paras on 12/28/2016.
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    public static JSONObject userData;

    @Override
    public void attachBaseContext(Context base) {
        super.onCreate();
        instance = this;

        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    public static boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)

        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static synchronized MyApplication getInstance() {
        return instance;
    }





    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view!=null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }

    public static void alertDialog(final Activity activity, String msg, String titile){
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(msg);
        adb.setTitle(titile);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();

    }

    public static float roundUp(float value, int roundAfterDecimal) {
        BigDecimal totaalAmt 		= new BigDecimal(value);
        BigDecimal strtotaalAmt 	= totaalAmt.setScale(roundAfterDecimal, RoundingMode.HALF_UP);
        float roundedValue = Float.parseFloat(String.valueOf(strtotaalAmt));
        return roundedValue;
    }

}