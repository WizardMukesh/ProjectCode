package com.jaipurice.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.jaipurice.app.R;
import com.jaipurice.app.adpaters.ProductAdapter;
import com.jaipurice.app.application.MyApplication;
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
 * Created by Sonu on 8/12/2017.
 */

public class ProductActivity extends BaseActivity {
    private GridView gridViewProducts;
    private ArrayList<Product> productArrayList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private String TAG = this.getClass().getName();
    private Button btnGo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        gridViewProducts = (GridView) findViewById(R.id.products_grid);
        btnGo = (Button) findViewById(R.id.btn_go);


        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Product> finalList = new ArrayList<>();
                for (int i = 0; i < productArrayList.size(); i++) {
                    if(productArrayList.get(i).getUserSelectedQty() > 0)
                    {
                        finalList.add(productArrayList.get(i));
                    }
                }
                if(finalList.size()>0) {
                    Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                    intent.putExtra("list", finalList);
                    startActivity(intent);
                }
                else
                    MyApplication.alertDialog(ProductActivity.this,"You have not added any item in cart","Cart");
            }
        });

        getEmployeeItems();

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.products_activity;
    }

    //---------------------------------show customer list------------------------------------------------------
    public void getEmployeeItems(){
        WebServiceHandler webServiceHandler = new WebServiceHandler(ProductActivity.this);
        webServiceHandler.serviceListener  =new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e(TAG,response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(new JSONObject(response).optInt("status_code")==1)
                            {
                                JSONObject jsonObj = new JSONObject(response);
                                JSONArray jsonArray = jsonObj.getJSONArray("details");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String itemId = jsonObject.getString("id");
                                    String itemName = jsonObject.getString("item_name");
                                    String price = jsonObject.getString("item_price");
                                    String itemImage = jsonObject.getString("item_image");

                                    Product product = new Product();
                                    product.setItemId(itemId);
                                    product.setItemName(itemName);
                                    product.setItemPrice(price);
                                    product.setItemImageURL(itemImage);
                                    product.setEmpTotalItemsQty(Integer.parseInt(jsonObject.optString("quantity")));
                                    product.setUserSelectedQty(0);

                                    productArrayList.add(product);
                                }
                            }
                            else{
                                btnGo.setVisibility(View.GONE);
                                MyApplication.alertDialog(ProductActivity.this, "You have no items", "Employee Items");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        productAdapter = new ProductAdapter(ProductActivity.this, R.layout.product_single_row, productArrayList);
                        gridViewProducts.setAdapter(productAdapter);
                    }
                });
            }
        };
        try {
            webServiceHandler.get(Constants.URL_EMPLOYEE_ITEMS+ SharedPreferenceUtility.getInstance().get(Constants.PREF_EMP_ID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}