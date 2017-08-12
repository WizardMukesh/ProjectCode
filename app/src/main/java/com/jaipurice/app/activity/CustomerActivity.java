package com.jaipurice.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.jaipurice.app.Model.CustomerModel;
import com.jaipurice.app.R;
import com.jaipurice.app.adpaters.CustomerrAdapter;
import com.jaipurice.app.utils.Constants;
import com.jaipurice.app.webservice.WebServiceHandler;
import com.jaipurice.app.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nishant on 8/12/2017.
 */

public class CustomerActivity extends AppCompatActivity {
    private ListView customerList;
    private ArrayList<CustomerModel> arrayCusList = new ArrayList<>();
    CustomerrAdapter customerrAdapter;
    private String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_row);

        customerList = (ListView) findViewById(R.id.customer_list);

        customerList();

    }

//---------------------------------show customer list------------------------------------------------------
    public void customerList(){
        WebServiceHandler webServiceHandler = new WebServiceHandler(CustomerActivity.this);
        webServiceHandler.serviceListener  =new WebServiceListener() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);
                JSONObject jsonObj = null;
                try {
                    if(new JSONObject(response).getString("status_code").equals("1"))
                    {
                        jsonObj = new JSONObject(response);
                        JSONArray jsonArray = jsonObj.getJSONArray("details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String cusID = jsonObject.getString("id");
                            String cusName = jsonObject.getString("name");
                            String cusContactNO = jsonObject.getString("contact_number");
                            String cusImage = jsonObject.getString("photo");

                            CustomerModel customerModel = new CustomerModel();
                            customerModel.setCustomerID(cusID);
                            customerModel.setCustomerName(cusName);
                            customerModel.setCustomerPhone(cusContactNO);
                            customerModel.setCustomerPhoto(cusImage);

                            arrayCusList.add(customerModel);
                        }
                    }
                    else{

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customerrAdapter = new CustomerrAdapter(CustomerActivity.this,arrayCusList);
                        customerList.setAdapter(customerrAdapter);
                    }
                });
            }
        };
        try {
            webServiceHandler.get(Constants.URL_CUSTOMER_LIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

