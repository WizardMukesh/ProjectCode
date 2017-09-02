package com.jaipurice.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jaipurice.app.R;
import com.jaipurice.app.utils.SharedPreferenceUtility;

import java.io.IOException;

/**
 * Created by SoNu on 8/12/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected abstract int getLayoutResourceId();

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
/*
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
*/


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                Log.e("Hello","LOGIUT");
                alertLogout(BaseActivity.this, "Are sure to logout from the app","Logout");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public  void alertLogout(final Activity activity, String msg, String titile){
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(msg);
        adb.setTitle(titile);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPreferenceUtility.getInstance().clearSharedPreferences();
                startActivity(new Intent(activity, LoginActivity.class));
                finish();
            }
        });
        adb.setNegativeButton("Cancel",null);
        adb.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("destroyed","destr");
/*
        if(ConnectionActivity.mBluetoothSocket!=null) {
            try {
                if (ConnectionActivity.mBluetoothSocket.getInputStream() != null) {
                        ConnectionActivity.mBluetoothSocket.getInputStream().close();
                }

                if (ConnectionActivity.mBluetoothSocket.getOutputStream() != null) {
                        ConnectionActivity.mBluetoothSocket.getOutputStream().close();
                }

                if (ConnectionActivity.mBluetoothSocket != null) {
                        ConnectionActivity.mBluetoothSocket.close();
                }
            }
            catch (IOException e){Log.e("LODA","bhosa");}
        }
*/
    }


}