package com.jaipurice.app.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jaipurice.app.R;
import com.jaipurice.app.application.MyApplication;
import com.jaipurice.app.utils.Constants;
import com.jaipurice.app.utils.PermissionResultCallback;
import com.jaipurice.app.utils.PermissionUtils;
import com.jaipurice.app.utils.SharedPreferenceUtility;
import com.jaipurice.app.webservice.WebServiceHandler;
import com.jaipurice.app.webservice.WebServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AAAAAAAAAA on 8/10/2017.
 */

public class LoginActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,PermissionResultCallback {

    private EditText userName,userPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    String username,userpassword;
    private String TAG = this.getClass().getName();
    private ArrayList<String> permissions=new ArrayList<>();
    private PermissionUtils permissionUtils;
    private boolean arePermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        permissionUtils=new PermissionUtils(LoginActivity.this);

        userName = (EditText)findViewById(R.id.editUserName);
        userPassword = (EditText)findViewById(R.id.editPassword);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        permissionUtils.check_permission(permissions,"App Should Have These Permissions",1);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arePermissionsGranted) {
                    username = userName.getText().toString();
                    userpassword = userPassword.getText().toString();
                    String url = Constants.URL_LOGIN + "?username=" + username + "&password=" + userpassword + "";
                    loginUser(url);

                }
                else
                    permissionUtils.check_permission(permissions,"App Should Have These Permissions",1);
            }
        });
    }

    private void loginUser(String url) {
        WebServiceHandler webServiceHandler = new WebServiceHandler(LoginActivity.this);
        webServiceHandler.serviceListener  =new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e(TAG,response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.optInt("status_code")==1) {
                                JSONObject jsonObject1 = jsonObject.optJSONArray("details").getJSONObject(0);

                                SharedPreferenceUtility.getInstance().save(Constants.PREF_IS_LOGGED_IN,true);
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_EMP_ID,jsonObject1.optString("id"));
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_USER_NAME,jsonObject1.optString("username"));
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_USER_FULL_NAME,jsonObject1.optString("name"));
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_TOKEN,jsonObject1.optString("token"));
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_CONTACT_NUM,jsonObject1.optString("contact_number"));
                                SharedPreferenceUtility.getInstance().save(Constants.PREF_PHOTO_URL,jsonObject1.optString("photo"));

                                Intent intent = new Intent(LoginActivity.this, ConnectionActivity.class);
                                startActivity(intent);
                            }
                            else
                                MyApplication.alertDialog(LoginActivity.this,jsonObject.optString("status_description"),"Login Error");

                        }catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // redirects to utils

        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    @Override
    public void PermissionGranted(int request_code) {
        Log.e(TAG,"Granted");
        arePermissionsGranted = true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.e(TAG,"Partially Granted");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.e(TAG,"Denied");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.e(TAG,"NeverAsk");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}