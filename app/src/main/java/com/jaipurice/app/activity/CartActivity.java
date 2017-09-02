package com.jaipurice.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jaipurice.app.R;
import com.jaipurice.app.adpaters.CartAdapter;
import com.jaipurice.app.application.MyApplication;
import com.jaipurice.app.model.Product;
import com.jaipurice.app.utils.Constants;
import com.jaipurice.app.utils.SharedPreferenceUtility;
import com.jaipurice.app.utils.UnicodeFormatter;
import com.jaipurice.app.webservice.WebServiceHandler;
import com.jaipurice.app.webservice.WebServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nishant on 8/12/2017.
 */

public class CartActivity extends BaseActivity{
    private ArrayList<Product> productArrayList;
    private String TAG = this.getClass().getName();
    private ListView listCartItems;
    private TextView textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount;
    private CartAdapter cartAdapter;
    private Button btnCheckOut;
    public static boolean isFirstPrintSuccess;
    private String tax;
    private int printCount = 0;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listCartItems = (ListView) findViewById(R.id.listCartItems);
        productArrayList = (ArrayList<Product>) getIntent().getSerializableExtra("list");
        cartAdapter = new CartAdapter(CartActivity.this,R.layout.cart_item_row, productArrayList);
        listCartItems.setAdapter(cartAdapter);
        progressDialog = new ProgressDialog(CartActivity.this);
        progressDialog.setMessage("Printing Bill");
        progressDialog.setCancelable(false);

        findViews();

        tax = SharedPreferenceUtility.getInstance().get(Constants.PREF_TAX)+"";
        textCartTotalDiscount.setText("-"+SharedPreferenceUtility.getInstance().get(Constants.PREF_DISCOUNT)+"%");
        textTax1.setText("+"+tax+"%");
        textTax2.setText("+"+tax+"%");

        cartAdapter.refreshBottomViews(textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog(CartActivity.this,"Are you sure to checkout these items","Checkout");
            }
        });
    }

    private void findViews() {
        textCartTotalAmount = (TextView) findViewById(R.id.textCartTotalAmount);
        textTax1 = (TextView) findViewById(R.id.textTax1);
        textTax2 = (TextView) findViewById(R.id.textTax2);
        textCartTotalDiscount = (TextView) findViewById(R.id.textCartTotalDiscount);
        textCartNetAmount = (TextView) findViewById(R.id.textCartNetAmount);
        btnCheckOut = (Button) findViewById(R.id.buttonCheckout);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_cart;
    }

    private void alertDialog(final Activity activity, String msg, String titile){
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(msg);
        adb.setTitle(titile);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkOut();
                dialog.dismiss();
            }
        });
        adb.setNegativeButton("Cancel",null);
        adb.show();

    }

    private void checkOut() {
        String json = buildJson();
        WebServiceHandler webServiceHandler = new WebServiceHandler(CartActivity.this);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Order Res",response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("status_code") ==1 ){
                                String billingId = jsonObject.optJSONObject("details").optInt("billing_id")+"";
                                progressDialog.show();
                                printDynamicBill(billingId, "Customer");
                            }
                            else
                                MyApplication.alertDialog(CartActivity.this,"Could not place order","Order");
                        }
                        catch (JSONException e){e.printStackTrace();}
                    }
                });
            }
        };
        try {
            webServiceHandler.get(Constants.URL_ORDER + "&values="+json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //printDynamicBill();
    }

    private String buildJson() {
        String json="";
        try{
            JSONObject mainObject = new JSONObject();
            JSONObject detailsObject = new JSONObject();
            JSONArray orders = new JSONArray();
            JSONObject priceObject = new JSONObject();

            detailsObject.put("employee_id", SharedPreferenceUtility.getInstance().get(Constants.PREF_EMP_ID));
            detailsObject.put("customer_id", CustomerActivity.customerId);
            mainObject.put("details",detailsObject);

            for (int i = 0; i < productArrayList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",productArrayList.get(i).getItemId());
                jsonObject.put("item_id",productArrayList.get(i).getItem());
                jsonObject.put("quantity",productArrayList.get(i).getUserSelectedQty()+"");
                float subTotal = productArrayList.get(i).getUserSelectedQty() * Float.parseFloat(productArrayList.get(i).getItemPrice());
                int roundedSubTotal = Math.round(subTotal);
                jsonObject.put("sub_total", roundedSubTotal+"");
                orders.put(i,jsonObject);
            }

            mainObject.put("orders",orders);


            priceObject.put("service_tax",cartAdapter.serviceTax+"");
            priceObject.put("discount",cartAdapter.discount+"");
            priceObject.put("total_price",textCartNetAmount.getText().toString());
            //mainObject.put("price",priceObject);

            json = mainObject.toString();
            Log.e("json",json);
        }catch (JSONException e){e.printStackTrace();}
        return json;
    }


    private void printDynamicBill(final String billingId, final String type){
        printCount = printCount + 1;
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = ConnectionActivity.mBluetoothSocket
                            .getOutputStream();
                    if(!isFirstPrintSuccess) {
                        String temp = "PRINT";
                        os.write(temp.getBytes());
                        isFirstPrintSuccess = true;
                    }

                    float totalPrice=0f;
                    String BILL = type+" Bill\n";
                    BILL = BILL + "Total no of products: "+  CartAdapter.totalNumProducts  +"\n"
                            + "Total quantity: "+   CartAdapter.totalNumQty  +" Kg\n\n" +
                            "anoop inc\n" +
                            "C-392,Brijlalpura,India,302019\n"+
                            "Phone:9414061877\n"+
                            "Email:graganoop32@gmail.com\n";

                    BILL = BILL
                            + "------------------------------\n";

                    BILL = BILL + String.format("%1$-12s %2$12s", "Bill To", "Pay To");
                    BILL = BILL + "\n";
                    BILL = BILL
                            + "------------------------------";

                    BILL = BILL + "\n" + String.format("%1$-12s %2$12s", CustomerActivity.customerName, " "+CustomerActivity.customerName);
                    BILL = BILL + "\n" + String.format("%1$-12s %2$12s", "GST:"+CustomerActivity.gstNumber, " GST:"+CustomerActivity.gstNumber);

                    BILL = BILL + "\n";
                    BILL = BILL + "\n" + "Invoice No.: "+ billingId;
                    BILL = BILL + "\n" + "Invoice Date: "+ getCurrentDate();

                    BILL = BILL
                            + "\n------------------------------\n";


                    BILL = BILL + String.format("%1$-11s %2$4s %3$9s", "Item",  "Qty(Kg)", "Rate");
                    BILL = BILL + "\n";
                    BILL = BILL
                            + "------------------------------";

                    for (int i = 0; i < productArrayList.size(); i++) {
                        Product product = productArrayList.get(i);
                        float subTotal = product.getUserSelectedQty() * Float.parseFloat(product.getItemPrice());
                        totalPrice = totalPrice+subTotal;
                        BILL = BILL + "\n " + String.format("%1$-11s %2$3s %3$10s", "ICE ("+product.getHsnCode()+")", product.getUserSelectedQty()+"", subTotal+"");
                    }

                    float floatAmt = Float.parseFloat(textCartNetAmount.getText().toString());

                    BILL = BILL
                            + "\n------------------------------";
                    BILL = BILL + "\n ";


                    BILL = BILL + "       Sub Total:" + "   " + totalPrice + "\n";
                    BILL = BILL + "         Discount:" + "   " + CartAdapter.discount + "\n";
                    BILL = BILL + "        Total Tax:" + "   " + CartAdapter.serviceTax + "\n";
                    BILL = BILL + "      Grand Total:" + "   " + Math.round(floatAmt) + "\n";

                    BILL = BILL
                            + "------------------------------\n";
                    BILL = BILL + "Sales Tax Summary\n";

                    BILL = BILL
                            + "------------------------------\n";
                    BILL = BILL + String.format("%1$-7s %2$6s %3$4s %4$5s", " Name",  " Amount", " Rate", "Tax");
                    BILL = BILL + "\n";
                    BILL = BILL
                            + "------------------------------";

                    BILL = BILL + "\n " + String.format("%1$-7s %2$6s %3$5s %4$5s", "CGST", CartAdapter.taxableAmount, tax+"%", CartAdapter.singleTax);
                    BILL = BILL + "\n " + String.format("%1$-7s %2$6s %3$5s %4$5s", "SGST", CartAdapter.taxableAmount, tax+"%", CartAdapter.singleTax);

                    BILL = BILL
                            + "\n--------------------------------\n";
                    BILL = BILL + "\n\n ";

                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));

                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        Log.e("DSFF","dfsfds");
        t.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(printCount == 1)
                    printDynamicBill(billingId, "Employee");
                else {
                    progressDialog.dismiss();
                    startActivity(new Intent(CartActivity.this,CustomerActivity.class));
                    finish();
                }
            }
        }, printCount==1 ? 6000 : 4000);
    }


    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public String getCurrentDate() {
        String formattedDate="";
        Calendar c = Calendar.getInstance();
        System.out.println("Current date => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());
        Log.e("DATA",formattedDate);
        return formattedDate;
    }
}