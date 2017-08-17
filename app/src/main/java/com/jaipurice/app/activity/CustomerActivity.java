package com.jaipurice.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.jaipurice.app.adpaters.ProductAdapter;
import com.jaipurice.app.model.CustomerModel;
import com.jaipurice.app.R;
import com.jaipurice.app.adpaters.CustomerrAdapter;
import com.jaipurice.app.model.Product;
import com.jaipurice.app.utils.Constants;
import com.jaipurice.app.utils.SharedPreferenceUtility;
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

public class CustomerActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private ListView customerList;
    private ArrayList<CustomerModel> arrayCusList = new ArrayList<>();
    CustomerrAdapter customerrAdapter;
    private String TAG = this.getClass().getName();
    private SearchView mSearchView;
    private ArrayList<CustomerModel>filteredList;
    public static String customerId="1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        customerList = (ListView) findViewById(R.id.customer_list);
        mSearchView = (SearchView) findViewById(R.id.search_view);

        customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("name",filteredList.get(position).getCustomerName());
                customerId = filteredList.get(position).getCustomerID();
                startActivity(new Intent(CustomerActivity.this, ProductActivity.class));
            }
        });
        customerList();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.customer_activity;
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
                        filteredList = arrayCusList;
                    }
                    else{

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customerrAdapter = new CustomerrAdapter(CustomerActivity.this,filteredList);
                        customerList.setAdapter(customerrAdapter);
                        customerList.setTextFilterEnabled(true);
                        setupSearchView();
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

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint("Search Here");
    }


    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            customerList.clearTextFilter();
        } else {
            customerList.setFilterText(newText);
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}