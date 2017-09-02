package com.jaipurice.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jaipurice.app.R;
import com.jaipurice.app.application.MyApplication;
import com.jaipurice.app.utils.Constants;
import com.jaipurice.app.utils.SharedPreferenceUtility;
import com.jaipurice.app.webservice.WebServiceHandler;
import com.jaipurice.app.webservice.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);

        //loadSplashTimer();

    }

    private void loadSplashTimer() {
        new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if(SharedPreferenceUtility.getInstance().get(Constants.PREF_IS_LOGGED_IN,false)) {
                    Intent intent = new Intent(SplashActivity.this, ConnectionActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();

    }

    public void alertDialog(final Activity activity, String msg, String titile){
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(msg);
        adb.setTitle(titile);
        adb.setPositiveButton("Go to Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
            }
                });
        adb.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.isConnectingToInternet())
            getTaxes();
        else
            alertDialog(SplashActivity.this,"No Internet Connection Found","Network Error");
    }

    private void getTaxes() {
        WebServiceHandler webServiceHandler = new WebServiceHandler(SplashActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("TAXES",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.optInt("status_code")==1){
                                float tax = Float.parseFloat(jsonObject.optJSONArray("details").optJSONObject(0).optString("cgst"));
                                float discount = Float.parseFloat(jsonObject.optJSONArray("details").optJSONObject(0).optString("discount"));
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_TAX, tax);
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_DISCOUNT, discount);

                                if(SharedPreferenceUtility.getInstance().get(Constants.PREF_IS_LOGGED_IN,false)) {
                                    Intent intent = new Intent(SplashActivity.this, ConnectionActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else
                                MyApplication.alertDialog(SplashActivity.this,"Could not process request","Error");
                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.get(Constants.URL_TAXES);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}