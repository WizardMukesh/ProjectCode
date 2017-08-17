package com.jaipurice.app.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jaipurice.app.R;
import com.jaipurice.app.utils.Constants;
import com.jaipurice.app.utils.SharedPreferenceUtility;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);

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
    }

